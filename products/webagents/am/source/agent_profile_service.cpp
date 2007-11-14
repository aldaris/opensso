/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 *
 */

/*
* This service does the following functions:
* 1. Initial naming request to determine rest service url
* 2. Agent authentication.
* 3. Fetches agent profile properties using REST attribute service.
* 4. Agent logout
*/

#include <prlock.h>
#include <prnetdb.h>
#include <prmem.h>
#include <prtime.h>
#include <prprf.h>
#include "agent_profile_service.h"
#include "xml_tree.h"
#include "http.h"
#include "sso_token.h"
#include "auth_svc.h"
#include "am_properties.h"
#include "url.h"
#include "naming_info.h"
#include "naming_service.h"
#include "am_web.h"


USING_PRIVATE_NAMESPACE
#define READ_INIT_BUF_LEN 1024
#define EXCEPTION "exception"


#define DEFAULT_AGENT_AUTH_MODULE "Application"
#define DEFAULT_AGENT_ORG_NAME "/"
#define ATTRIB_URI "xml/attributes"

const std::string authURLAttribute("iplanet-am-naming-auth-url");
const std::string restURLAttribute("sun-naming-idsvcs-rest-url");
const std::string protocolPart("%protocol");
const std::string hostPart("%host");
const std::string portPart("%port");
const std::string uriPart("%uri");
const std::size_t MAX_PORT_LENGTH = 5;


AgentProfileService::AgentProfileService(const Properties& config)
    : BaseService("Agent Profile Service",
                config,
                config.get(AM_COMMON_CERT_DB_PASSWORD_PROPERTY, ""),
                config.get(AM_AUTH_CERT_ALIAS_PROPERTY, ""),
                config.getBool(AM_COMMON_TRUST_SERVER_CERTS_PROPERTY, false)),
    mNamingServiceURL(config.get(AM_COMMON_NAMING_URL_PROPERTY, "")),
    mNamingServiceInfo(mNamingServiceURL),
    mNamingService(config,
                   config.get(AM_COMMON_CERT_DB_PASSWORD_PROPERTY,""),
                   config.get(AM_AUTH_CERT_ALIAS_PROPERTY,""),
                   config.getBool(AM_COMMON_TRUST_SERVER_CERTS_PROPERTY, false))
{

} // constructor


AgentProfileService::~AgentProfileService()
{
}

/**
 * Fetches agent profile attributes using REST attribute service
 * Agent has to be authenticated before doing this.
 * If successful, properties object gets loaded with profile attributes
 */
am_status_t AgentProfileService::getAgentAttributes(
    const std::string appSSOToken, 
    am_properties_t properties)
{

    am_status_t status = AM_FAILURE;
    Http::Response response;
    const char *parameter = NULL;
    const char *repositoryLocation = NULL;
    am_status_t getStatus; 
    std::string certName;
    std::string::size_type pos;

    std::string encodedAgentToken = Http::encode(appSSOToken);

    std::string urlparam = "?subjectid=";
    urlparam.append(encodedAgentToken);

    setRestSvcInfo(mRestURL);

    status =  doHttpGet(mRestSvcInfo, urlparam, Http::CookieList(),
                        response, READ_INIT_BUF_LEN,
                        certName);
    if(status == AM_SUCCESS) {
        std::string xmlResponse(response.getBodyPtr());
        pos = xmlResponse.find(EXCEPTION,0);
        if(pos != std::string::npos){
            status = AM_REST_ATTRS_SERVICE_FAILURE;
        } else {
            try {
                status = parseAgentResponse(xmlResponse, properties);
            } catch(...) {
                Log::log(logModule, Log::LOG_ERROR, 
                    "parseAgentResponse(): Attribute xml parsing error");
                status = AM_REST_ATTRS_SERVICE_FAILURE;
            }
        }
    } else {
        status = AM_REST_ATTRS_SERVICE_FAILURE;
    }


   return status;

}

/**
 * Agent logout 
 */
am_status_t
AgentProfileService::agentLogout(const Properties &config)
{
    am_status_t status = AM_SUCCESS;
    AuthService authSvc(config);
    try {
        if (mAuthCtx.getSSOToken().size() >= 0) {
            authSvc.logout(mAuthCtx);
        }
    }
    catch (InternalException& iex) {
        status = iex.getStatusCode();
        Log::log(logModule, Log::LOG_WARNING, 
            "Service::do_agent_auth_logout(): "
            "Internal Exception in agent logout, "
            "status %s.",  am_status_to_name(status));
    }
    catch (std::exception& ex) {
        Log::log(logModule, Log::LOG_WARNING, 
            "Service::do_agent_auth_logout(): "
            "Exception in agent logout, msg [%s].", ex.what());
        status = AM_AUTH_FAILURE;
    }
    catch (...) {
        Log::log(logModule, Log::LOG_WARNING, 
            "Service::do_agent_auth_logout(): "
            "Unknown error in agent logout.");
        status = AM_AUTH_FAILURE;
    }
    return status;
}


/**
 * Parses agent profile REST attributes service response and sets the
 * properties in properties object.
 *
 * Throws XMLTree::ParseException 
 */
am_status_t  AgentProfileService::parseAgentResponse(const std::string xmlResponse,
					       am_properties_t properties ) 
{
    am_status_t status = AM_SUCCESS;

    XMLTree namingTree(false, xmlResponse.c_str(), xmlResponse.size());
    XMLElement element = namingTree.getRootElement();
    XMLElement element1; 

    // get the root element userdetails
    if (element.isNamed("userdetails")) {

        // get the first attribute element
	element.getSubElement("attribute", element);
	if (element.isNamed("attribute")) {

            // iterate through all the attribute elements
            while (element.isValid()) {
                if(element.isNamed("attribute")) {
                    std::string propName;
                    std::string propValue;
                    // get property name and assign it to propName
                    if (element.getAttributeValue("name", propName)) {

                        // get property's value(s) and assign it to propValue 
                        element1 = element.getFirstSubElement();
                        if (element1.isNamed("value")) {
                            int i = 0;

                            // if multiple values exist, 
                            //append them with a separator ' '
                             while (element1.isValid()) {
                                 if(element1.isNamed("value")) {
			                         std::string tmpValue;
                                     if (element1.getValue(tmpValue)) {
                                         if(i > 0)
                                             propValue.append(" ");
                                          propValue.append(tmpValue);
                                      } else {
                                          throw XMLTree::ParseException(
                                              "Attribute value missing ");
			              }
		                  }
                                  element1.nextSibling();
                                  i++;
                              } // end of value element while loop 
                                    
                          } // end of if value check 


                          // assign  property name and value in properties object.
                          if(!propValue.empty()) {
                              am_properties_set(properties, 
                                  propName.c_str(), 
                                  propValue.c_str());
                          } else {
                              // The following is required for validation of 
                              // agent repository location value.
                              if(strcasecmp(propName.c_str(), 
                                AM_WEB_AGENT_REPOSITORY_LOCATION_PROPERTY) == 0) 
                              {
                                  am_properties_set_null(properties, 
                                      propName.c_str(), "\0");
                              }
                          }
                          Log::log(logModule, Log::LOG_DEBUG, 
                              "agentAttributes() %s : %s", 
                              propName.c_str(), propValue.c_str());

                    } else {
			    throw XMLTree::ParseException("Attribute name missing ");
                    }
		}
		element.nextSibling();
            }

            status = AM_SUCCESS;
        } else {
            throw XMLTree::ParseException("attribute element not present in "
					  "Attributes Response xml");
        } 
    } else {
        throw XMLTree::ParseException("userdetails root element not present in "
					  "Attributes Response xml");
    }

    Log::log(logModule, Log::LOG_DEBUG, "parseAgentResponse() "
	     "returning with status %s.", am_status_to_string(status));
    return status;
}


/**
 * Sets mAuthSvcInfo member variable
 * Throws: InternalException upon error
 */
void
AgentProfileService::setAuthSvcInfo(std::string authURL)
{

    try {
        URL verifyURL(authURL);
    } catch (InternalException &iex) {
        throw InternalException("AuthService::setAuthSvcInfo()",
                                "Malformed URL.",
                                AM_AUTH_FAILURE);
    }

    mAuthSvcInfo.setFromString(authURL);
    Log::log(logModule, Log::LOG_MAX_DEBUG,
             "Number of servers in service:%d, '%s'.",
             mAuthSvcInfo.getNumberOfServers(), authURL.c_str());

    return;
}

/**
 * Sets mRestSvcInfo member variable
 * Throws: InternalException upon error
 */
void
AgentProfileService::setRestSvcInfo(std::string restURL)
{

    try {
        URL verifyURL(restURL);
    } catch (InternalException &iex) {
    Log::log(logModule, Log::LOG_ERROR,
             "Error in rest url :'%s' ", restURL.c_str());
        throw InternalException("AuthService::setAuthSvcInfo()",
                                "Malformed URL.",
                                AM_AUTH_FAILURE);
    }

    mRestSvcInfo.setFromString(restURL);
    Log::log(logModule, Log::LOG_MAX_DEBUG,
             "Number of servers in service:%d, '%s'.",
             mRestSvcInfo.getNumberOfServers(), restURL.c_str());

    return;
}


/**
 * Does Agent authentication 
 */
am_status_t AgentProfileService::agentLogin(const Properties &config, 
                                            const std::string userName, 
                                            const std::string passwd, 
                                            std::string& ssoToken)
{
    am_status_t status = AM_SUCCESS;

    Log::log(logModule, Log::LOG_DEBUG, 
         "AgentProfileService()::agentLogin() :%s", userName.c_str());
    AuthService authSvc(config);

    const char *thisfunc = "AgentProfileService::agentLogin()";
    std::string orgName;
    std::string moduleName;

    // Get all the parameters.
    // Right now we just handle username/password callback for LDAP module.
    // in the future we may want to handle different callbacks depending
    // on the module.
    orgName = config.get(AM_POLICY_ORG_NAME_PROPERTY,
                            DEFAULT_AGENT_ORG_NAME);
    moduleName = config.get(AM_POLICY_MODULE_NAME_PROPERTY,
                               DEFAULT_AGENT_AUTH_MODULE);

    // Create Auth context, do login.
    try {
        std::string certName, namingURLs;   // dummy strings for linux compile.
        AuthContext authC(orgName, certName, namingURLs);
        Log::log(logModule, Log::LOG_DEBUG, 
            "AgentProfileService()::agentLogin() :%s", mAuthURL.c_str());
        authC.authSvcInfo.setFromString(mAuthURL);

        authSvc.create_auth_context_cac(authC);
        Log::log(logModule, Log::LOG_DEBUG, 
            "AgentProfileService()::agentLogin() :%s", mAuthURL.c_str());
        authSvc.login(authC, AM_AUTH_INDEX_MODULE_INSTANCE, moduleName);

        std::vector<am_auth_callback_t>& callbacks = authC.getRequirements();
        int nCallbacks = callbacks.size();
        if (nCallbacks > 0) {
            bool userNameIsSet = false;
            bool passwdIsSet = false;
            for (int i = nCallbacks-1; i >= 0; --i) {
                am_auth_callback_t& cb = callbacks[i];
                switch(cb.callback_type) {
                    case NameCallback:
                        Log::log(logModule, Log::LOG_DEBUG,
                         "%s: Setting name callback to '%s'.",
                         thisfunc, userName.c_str());
                        cb.callback_info.name_callback.response = 
                            userName.c_str();
                        userNameIsSet = true;
                        break;
                    case PasswordCallback:
                        Log::log(logModule, Log::LOG_DEBUG,
                         "%s: Setting password callback.", thisfunc);
                        cb.callback_info.password_callback.response = 
                            passwd.c_str();
                        passwdIsSet = true;
                        break;
                    default:
                        // ignore unexpected callback.
                        Log::log(logModule, Log::LOG_WARNING,
                         "%s: Unexpected callback type %d ignored.",
                         thisfunc, cb.callback_type);
                        continue;
                        break;
                }
            }

            authSvc.submitRequirements(authC);

            am_auth_status_t auth_sts = authC.getStatus();
            switch(auth_sts) {
                case AM_AUTH_STATUS_SUCCESS:
                break;
                case AM_AUTH_STATUS_FAILED:
                case AM_AUTH_STATUS_NOT_STARTED:
                case AM_AUTH_STATUS_IN_PROGRESS:
                case AM_AUTH_STATUS_COMPLETED:
                default:
                    status =  AM_AUTH_FAILURE;
                    Log::log(logModule, Log::LOG_ERROR,
                     "Agent failed to login to FAM, auth status %d.",
                     auth_sts);
                    break;
            }
        }
        else {
            Log::log(logModule, Log::LOG_ERROR,
                 "%s: Agent cannot login to FAM: Expected auth callbacks "
                 "for agent login not found.", thisfunc);
            status =  AM_AUTH_FAILURE;
        }

        // set the SSO Token.
        ssoToken = authC.getSSOToken();

        // save the auth context for logging out when service is destroyed
        mAuthCtx = authC;

        Log::log(logModule, Log::LOG_DEBUG, 
             "%s: Successfully logged in as %s.",
             thisfunc, userName.c_str());
    } catch(...) {
        Log::log(logModule, Log::LOG_DEBUG, 
             "%s: login failed for agent profile:  %s.",
             thisfunc, userName.c_str());
        status =  AM_AUTH_FAILURE;
    }

    return status;

}

/**
 * Checks for REST service url element present in naming response.
 */
am_status_t AgentProfileService::isRESTServiceAvailable()
{
    am_status_t status = AM_SUCCESS;

    Http::CookieList cookieList;
    NamingInfo namingInfo;
    const char *thisFunc = "isRESTServiceAvailable()";

    try {
        status = mNamingService.doNamingRequest(mNamingServiceInfo,
                                               cookieList,
                                               namingInfo);
        if (status == AM_SUCCESS) {
            std::string restURL = 
                namingInfo.extraProperties.get(restURLAttribute, "", true);
            if(!restURL.empty()) {
               Log::log(logModule, Log::LOG_DEBUG,
                     ": Using rest service info from naming response %s:", 
                     restURL.c_str());
               std::string authURL = 
                   namingInfo.extraProperties.get(authURLAttribute, "", true);
               parseURL(restURL, true, mRestURL);
               parseURL(authURL, false, mAuthURL);
            
            } else {
                status = AM_REST_SERVICE_NOT_AVAILABLE;
                Log::log(logModule, Log::LOG_DEBUG,
                     "%s: No REST service available. "
                     "So local agent configuration file gets used.", 
                      thisFunc);
            }

        } else {
                Log::log(logModule, Log::LOG_ERROR,
                     "%s: An error occured while doing naming request. %s", 
                      thisFunc, am_status_to_string(status));
        }    
    } catch(...) {
        Log::log(logModule, Log::LOG_ERROR,
             "%s: An error occured while doing naming request. %s", 
              thisFunc, am_status_to_string(status));
    }

    return status;
}


/**
 * Parses naming response's service url value.
 *
 * The AM Naming Service returns Naming Service Table
 * URLs without replacing %protocol://%host:%port
 * when the request does not have an SSO token.
 * So all those % values have to replaced using
 * naming.url property value
*/
void AgentProfileService::parseURL(std::string serviceURL, 
                                   bool isRestURL, 
                                   std::string& parsedServiceURL)
{
    am_status_t status = AM_SUCCESS;
    char portBuf[MAX_PORT_LENGTH + 1];
    ServiceInfo::const_iterator iter;

    for (iter = mNamingServiceInfo.begin(); 
         iter != mNamingServiceInfo.end() ; ++iter) {

        std::string protocol = (*iter).getProtocol();
        if(!protocol.empty()) {
        std::string tmpURL = serviceURL;
        std::string hostname = (*iter).getHost();
        unsigned short portnumber = (*iter).getPort();
        snprintf(portBuf, sizeof(portBuf), "%u", portnumber);
        std::string uri = (*iter).getURI();
        char *c_uri = NULL;
        char *uriTok = NULL;

        if (status == AM_SUCCESS) {
            std::size_t pos = 0;

            pos = tmpURL.find (protocolPart, pos);
            while (pos != std::string::npos) {
                tmpURL.replace (pos, protocolPart.size(), protocol);
                pos = tmpURL.find (protocolPart, pos + 1);
            }
            pos = 0;
            pos = tmpURL.find (hostPart, pos);
            while (pos != std::string::npos) {
                tmpURL.replace (pos, hostPart.size(), hostname);
                pos = tmpURL.find (hostPart, pos + 1);
            }
            pos = 0;
            pos = tmpURL.find (portPart, pos);
            while (pos != std::string::npos) {
                tmpURL.replace (pos, portPart.size(), portBuf);
                pos = tmpURL.find (portPart, pos + 1);
            }
            // Need to use strtok as the uri contains eg:opensso/namingservice
            // we want only /opensso
            if (!uri.empty()) {
                c_uri = (char *) malloc(strlen(uri.c_str())+1);
                strcpy(c_uri,uri.c_str());
                uriTok = strtok(c_uri, "/");
                if (uriTok != NULL) {
                    std::string tmpUri(uriTok);
                    std::string newUri("/");
                    newUri.append(tmpUri);
                    
                    pos = 0;
                    pos = tmpURL.find (uriPart, pos);
                    while (pos != std::string::npos) {
                        tmpURL.replace (pos, uriPart.size(), newUri);
                        pos = tmpURL.find (uriPart, pos + 1);
                    }
                }
                free(c_uri);
            }
        }
        // for rest service url, xml/attributes needs to be appended
        if(isRestURL) {
            tmpURL.append(ATTRIB_URI);
        }
        // if multiple urls are present in naming url property, 
        // then those many urls need to be present in 
        // auth service and rest service urls also. 
        tmpURL.append(" ");
        parsedServiceURL.append(tmpURL);
        }
    }
    Log::log(logModule, Log::LOG_MAX_DEBUG,
             "Number of servers in service: '%s'.", 
             parsedServiceURL.c_str());
    return;
} 


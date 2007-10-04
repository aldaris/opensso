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
 * $Id: AuthXMLResponse.java,v 1.4 2007-10-04 22:05:32 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.authentication.server;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenID;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.service.AuthUtils;
import com.sun.identity.authentication.share.AuthXMLTags;
import com.sun.identity.authentication.share.AuthXMLUtils;

/**
 * AuthXMLResponse constructs the response XML string to return
 * to the client.
 */
public class AuthXMLResponse {
    String authErrorCode;
    String authErrorMessage;
    String authErrorTemplate;
    String authURL;
    static com.sun.identity.shared.debug.Debug debug;
    String tokenId = null; 
    AuthContextLocal authContext = null;
    AuthContext.Status loginStatus=AuthContext.Status.IN_PROGRESS;
    String errorCode = null;
    String errorMessage = null;
    String errorTemplate = null;
    String loginURL = null;
    Callback[] reqdCallbacks ;
    int requestType ;
    String successURL = null;
    String failureURL = null;
    boolean isException = false;
    SSOToken ssoToken = null;
    SSOTokenID ssoTokenID = null;
    AuthUtils authUtils = null;
    String authIdentifier;
    String prevAuthIdentifier;
    Set moduleNames = new HashSet() ;
    AuthContextLocal oldAuthContext = null;

    /**
     * Creates <code>AuthXMLResponse</code> object
     * @param requestType id of request type
     */
    public AuthXMLResponse(int requestType)  {
        debug = com.sun.identity.shared.debug.Debug.getInstance("amXMLHandler");
        this.requestType = requestType;
        authUtils = new AuthUtils();
    }

    /**
     * Sets the error code.
     *
     * @param errorCode Error Code.
     */
    public void setErrorCode(String errorCode) {
      try {
        this.errorCode = errorCode;
        authErrorMessage = authUtils.getErrorMessage(errorCode);
        authErrorTemplate = authUtils.getErrorTemplate(errorCode);
        if (debug.messageEnabled()) {
                debug.message("Auth error message " + authErrorMessage);
                debug.message("Auth error template" + authErrorTemplate);
        }
        loginStatus=AuthContext.Status.FAILED; 
        } catch (Exception e) {
          debug.message("error : " , e);
        }
    }

    /**
     * Sets Error Message.
     *
     * @param msg Error Message.
     */
    public void setErrorMessage(String msg) {
        authErrorMessage = msg;
    }

    /**
     * Sets Template.
     *
     * @param template Template.
     */
    public void setErrorTemplate(String template) {
        authErrorTemplate = template;
    }
    
    /**
     * Sets the failure URL.
     *
     * @param failureURL Failure URL.
     */
    public void setFailureURL(String failureURL) {
        this.failureURL = failureURL;
    }

    /**
     * Sets the login status.
     *
     * @param loginStatus Login Status.
     */
    public void setLoginStatus(AuthContext.Status loginStatus) {
        this.loginStatus = loginStatus;
    }
        
    /**
     * Sets the callbacks sent by module.
     *
     * @param reqdCallbacks Array of Request Callbacks.
     */
    public void setReqdCallbacks(Callback reqdCallbacks[]) {
        this.reqdCallbacks = reqdCallbacks;
    }

    /**
     * Returns error XML string
     *
     * @return error XML string
     */
    public String createXMLErrorString() {
        StringBuffer errorXMLString = new StringBuffer();
        errorXMLString.append(AuthXMLTags.EXCEPTION_BEGIN)
                      .append(AuthXMLTags.SPACE);

        if (authErrorMessage != null) {
            errorXMLString.append(AuthXMLTags.MESSAGE)
                          .append(AuthXMLTags.EQUAL)
                          .append(AuthXMLTags.QUOTE)
                          .append(authErrorMessage)
                          .append(AuthXMLTags.QUOTE)
                          .append(AuthXMLTags.SPACE);
        }
        if (tokenId != null) {
            errorXMLString.append(AuthXMLTags.TOKEN_ID)
                          .append(AuthXMLTags.EQUAL)
                          .append(AuthXMLTags.QUOTE)
                          .append(tokenId)
                          .append(AuthXMLTags.QUOTE);
        }
        if (errorCode != null) {
            errorXMLString.append(AuthXMLTags.SPACE)
                          .append(AuthXMLTags.ERROR_CODE)
                          .append(AuthXMLTags.EQUAL)
                          .append(AuthXMLTags.QUOTE)
                          .append(errorCode)
                          .append(AuthXMLTags.QUOTE);
        }
        if (authErrorTemplate != null) {        
            errorXMLString.append(AuthXMLTags.SPACE)
                          .append(AuthXMLTags.TEMPLATE_NAME)
                          .append(AuthXMLTags.EQUAL)
                          .append(AuthXMLTags.QUOTE)
                          .append(authErrorTemplate)
                          .append(AuthXMLTags.QUOTE);
        }        
                                        
        errorXMLString.append(AuthXMLTags.ELEMENT_CLOSE)
                      .append(AuthXMLTags.EXCEPTION_END);

        String errorString = errorXMLString.toString();
        if (debug.messageEnabled()) {
            debug.message("Error Response String : " + errorString);
        }
        return errorString;
   }

    /* create the login status xml string */
    String createLoginStatusString() {
        debug.message("in createLoginStatusSTring");    
        StringBuffer statusString  = new StringBuffer();
        String status = loginStatus.toString();        
            statusString.append(AuthXMLTags.LOGIN_STATUS_BEGIN);
        if (status != null) {
            statusString.append(AuthXMLTags.SPACE)
                        .append(AuthXMLTags.STATUS)
                        .append(AuthXMLTags.EQUAL)
                        .append(AuthXMLTags.QUOTE)
                        .append(status)
                        .append(AuthXMLTags.QUOTE);
        }

        if (loginStatus == AuthContext.Status.SUCCESS)  {
            setSSOToken();
            if (ssoToken != null) {
                ssoTokenID = ssoToken.getTokenID();
                if (debug.messageEnabled()) {
                    debug.message("ssoTokenID is : " + ssoTokenID.toString());
                }
            }
            if (ssoTokenID != null) {
                statusString.append(AuthXMLTags.SPACE)
                            .append(AuthXMLTags.SSOTOKEN)
                            .append(AuthXMLTags.EQUAL)
                            .append(AuthXMLTags.QUOTE)
                            .append(ssoTokenID.toString())
                            .append(AuthXMLTags.QUOTE);
            }
        
            successURL = authUtils.getLoginSuccessURL(authContext);

            if (successURL != null) {
                statusString.append(AuthXMLTags.SPACE)
                            .append(AuthXMLTags.SUCCESS_URL)
                            .append(AuthXMLTags.EQUAL)
                            .append(AuthXMLTags.QUOTE)
                            .append(successURL)
                            .append(AuthXMLTags.QUOTE);
            }

            Subject subject = authContext.getSubject();

            if (debug.messageEnabled()) {
                 debug.message("Subject is : " + subject);
            } 

            String serializedSubject = AuthXMLUtils.getSerializedSubject(
                subject);
            if (debug.messageEnabled()) {
                 debug.message(
                    "serialized Subject is : " + serializedSubject);
            } 
            statusString.append(AuthXMLTags.ELEMENT_CLOSE)
                        .append(AuthXMLTags.SUBJECT_BEGIN)
                        .append(serializedSubject)
                        .append(AuthXMLTags.SUBJECT_END);
        } else if (loginStatus == AuthContext.Status.FAILED) {
            failureURL = authUtils.getLoginFailedURL(authContext);
            if (failureURL != null) {
                statusString.append(AuthXMLTags.SPACE)
                            .append(AuthXMLTags.FAILURE_URL)
                            .append(AuthXMLTags.EQUAL)
                            .append(AuthXMLTags.QUOTE)
                            .append(failureURL)
                            .append(AuthXMLTags.QUOTE);
            }
            statusString.append(AuthXMLTags.ELEMENT_CLOSE);
        } else {
            statusString.append(AuthXMLTags.ELEMENT_CLOSE);
        }

        statusString.append(AuthXMLTags.LOGIN_STATUS_END);
 
        String statusXMLString = statusString.toString();
        if (debug.messageEnabled()) {
            debug.message("LoginStatus XML STring : " + statusXMLString);
        }
        return statusXMLString;
    }

    /**
     * Returns XML for the response based on the requested client information.
     *
     * @return XML for the response based on the requested client information.
     */
    public String toXMLString() {
        try{
            StringBuffer xmlString = new StringBuffer();

            String[] authIdentifierArray = new String[1];
            authIdentifierArray[0] = authIdentifier;        

            if (debug.messageEnabled()) {
                debug.message("New authIdentifier : " + authIdentifier);
                debug.message(
                    "Returning authIdentifier : " + authIdentifierArray[0]);
            }

            xmlString.append(MessageFormat.format(
                AuthXMLTags.XML_RESPONSE_PREFIX,(Object[])authIdentifierArray));

            if (debug.messageEnabled()) {
                debug.message("Request type is : " + requestType);
            }

            switch (requestType) {
            case AuthXMLRequest.NewAuthContext:
                if (loginStatus==AuthContext.Status.FAILED || isException) {
                    xmlString.append(createXMLErrorString());
                    authUtils.destroySession(authContext);
                } else if (loginStatus == AuthContext.Status.SUCCESS) {
                    xmlString.append(createLoginStatusString());
                    debug.message("destroying old session");
                    if (oldAuthContext != null) {
                        authUtils.destroySession(oldAuthContext);
                    }
                } else {
                    xmlString.append(createLoginStatusString());
                }
                break;
            case AuthXMLRequest.Login:
            case AuthXMLRequest.LoginIndex:
            case AuthXMLRequest.LoginSubject:
            case AuthXMLRequest.SubmitRequirements:
                if (reqdCallbacks != null) {
                    String xmlCallback
                        = AuthXMLUtils.getXMLForCallbacks(reqdCallbacks);
                    xmlString.append(AuthXMLTags.GET_REQS_BEGIN);
                    xmlString.append(xmlCallback);
                    xmlString.append(AuthXMLTags.GET_REQS_END);
                } else { 
                    if ((loginStatus == AuthContext.Status.FAILED) || 
                        isException
                    ) {
                        xmlString.append(createLoginStatusString());
                        xmlString.append(createXMLErrorString());
                            authUtils.destroySession(authContext);
                    } else if (loginStatus == AuthContext.Status.SUCCESS) {
                        xmlString.append(createLoginStatusString());
                        if (oldAuthContext != null) {
                            if (debug.messageEnabled()) {
                                debug.message("AuthXMLResponse.toXMLString : "
                                    +"destroying old session");
                            }
                            authUtils.destroySession(oldAuthContext);
                        }
                    }
                }
                break;
            case AuthXMLRequest.QueryInformation:
                 if (isException) {
                     xmlString.append(createXMLErrorString());        
                 } else {
                     xmlString.append(getXMLForQueryInfo());
                 }
                 break;
            case AuthXMLRequest.Logout:
            case AuthXMLRequest.Abort:
                 if (isException) {
                     xmlString.append(createXMLErrorString());        
                 } else {
                     xmlString.append(createLoginStatusString());
                 }
                 break;
            }

            xmlString.append(AuthXMLTags.XML_RESPONSE_SUFFIX);
            return xmlString.toString();
        } catch (Exception e) {
            debug.message("Exceiton in toXMLString : " , e);
            return null;
        }
    }

    /**
     * Sets the authentication context.
     *
     * @param authContext Authentication Context.
     */
    public void setAuthContext (AuthContextLocal authContext) {
        this.authContext = authContext;
    }

    /**
     * Sets if exception was thrown.
     *
     * @param isException <code>true</code> if exception was thrown.
     */
    public void setIsException(boolean isException) {
        this.isException = isException;
    }

    /**
     * Sets the authentication identifier.
     *
     * @param authIdentifier Authentication identifier.
     */
    public void setAuthIdentifier(String authIdentifier) {
        this.authIdentifier = authIdentifier;
    }

    /**
     * Sets the module names.
     *
     * @param moduleNames Module Names.
     */
    public void setModuleNames(Set moduleNames) {
        this.moduleNames = moduleNames;
    }

    /**
     * Returns XML for query information.
     *
     * @return XML for query information.
     */
    public String getXMLForQueryInfo() {
        StringBuffer xmlString = new StringBuffer();
        xmlString.append(AuthXMLTags.QUERY_RESULT_BEGIN)
                 .append(AuthXMLTags.SPACE)
                 .append(AuthXMLTags.REQUESTED_INFO)
                 .append(AuthXMLTags.EQUAL)
                 .append(AuthXMLTags.QUOTE)
                 .append(AuthXMLTags.MODULE_INSTANCE)
                 .append(AuthXMLTags.QUOTE) 
                 .append(AuthXMLTags.ELEMENT_END);

        if (!moduleNames.isEmpty()) {
            Iterator mIterator = moduleNames.iterator();
            while (mIterator.hasNext()) {
                String moduleName = (String) mIterator.next();
                xmlString.append(AuthXMLTags.VALUE_BEGIN)
                         .append(moduleName)
                         .append(AuthXMLTags.VALUE_END);
            }
        }

        xmlString.append(AuthXMLTags.QUERY_RESULT_END);
                
        String queryResult = xmlString.toString();

        if (debug.messageEnabled()) {
            debug.message("Query Result : " + queryResult);
        }

        return queryResult;
    }

    /**
     * Sets <code>SSOToken</code>
     */
    public void setSSOToken() {
        if (authContext != null) {
            ssoToken = authContext.getSSOToken();
        }
        if (debug.messageEnabled()) {
            debug.message("ssoToken is: " + ssoToken);
        }
    }

    /**
     * Sets previous authentication context.
     *
     * @param aOldAuthContext previous authentication context object.
     */
    public void setPrevAuthContext(AuthContextLocal aOldAuthContext) {
        this.oldAuthContext = aOldAuthContext;
    }

}

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

#ifndef AGENT_PROFILE_SERVICE_H
#define AGENT_PROFILE_SERVICE_H

#include "base_service.h"
#include "internal_macros.h"
#include "naming_info.h"
#include "naming_service.h"
#include "sso_token.h"
#include "service_info.h"

#include "thread_pool.h"
#include "auth_svc.h"



BEGIN_PRIVATE_NAMESPACE

/*
* This service does the following functions:
* 1. Initial naming request to determine rest service url
* 2. Agent authentication.
* 3. Fetches agent profile properties using REST attribute service.
* 4. Agent logout
*/


class AgentProfileService: public BaseService {
public:
    explicit AgentProfileService(const Properties& props);
    virtual ~AgentProfileService();

    am_status_t getAgentAttributes( const std::string ssoToken,  
                           am_properties_t properties);
    am_status_t agentLogin(const Properties &config, 
                           const std::string userName, 
                           const std::string passwd, 
                           std::string& appSSOToken); 

    am_status_t agentLogout(const Properties &config); 
    am_status_t isRESTServiceAvailable();

private:

    ServiceInfo mRestSvcInfo; 
    ServiceInfo mAuthSvcInfo; 
    std::string mRestURL;
    std::string mAuthURL;
    std::string mNamingServiceURL;
    ServiceInfo mNamingServiceInfo;
    NamingService mNamingService;
    AuthContext mAuthCtx;

    void setRestSvcInfo(std::string restURL);
    void setAuthSvcInfo(std::string restURL);

    am_status_t parseAgentResponse( const std::string xmlResponse,  
                           am_properties_t properties);
    void AgentProfileService::parseURL(std::string serviceURL, 
                                       bool isRestURL,
                                       std::string &parsedServiceURL);
};

END_PRIVATE_NAMESPACE

#endif	/* not AGENT_PROFILE_SERVICE_H */

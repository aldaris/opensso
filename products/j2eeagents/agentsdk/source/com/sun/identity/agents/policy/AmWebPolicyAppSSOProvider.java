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
 * $Id: AmWebPolicyAppSSOProvider.java,v 1.2 2008-05-14 21:14:25 sean_brydon Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.policy;

import com.iplanet.sso.SSOToken;
import com.sun.identity.agents.arch.AgentBase;
import com.sun.identity.agents.arch.AgentConfiguration;
import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Manager;
import com.sun.identity.agents.common.CommonFactory;
import com.sun.identity.agents.common.IApplicationSSOTokenProvider;

/**
 * The class provides an application Single Sign-On token
 */
public class AmWebPolicyAppSSOProvider extends AgentBase implements
        IAmWebPolicyAppSSOProvider {
    
    public AmWebPolicyAppSSOProvider(Manager manager) {
        super(manager);
    }
    
    public void initialize() throws AgentException {
        if (isLogMessageEnabled()) {
            logMessage("AmWebPolicyAppSSOProvider: initialized");
        }
    }

    /* (non-Javadoc)
     * @see com.sun.identity.security.AppSSOTokenProvider#getAppSSOToken()
     */
    public SSOToken getAppSSOToken() {
        SSOToken result = null;
        try {
            result = AgentConfiguration.getAppSSOToken();  
        } catch (AgentException aex) {
            logError("AmWebPolicyAppSSOProvider.getAppSSOToken: Unable"
                    + " to create AppSSOToken", aex);  
        }
        return result;
    }
}

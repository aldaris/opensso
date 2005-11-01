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
 * $Id: DemoAdminTokenFactory.java,v 1.1 2005-11-01 00:28:34 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.server;

import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.authentication.internal.server.AuthSPrincipal;
import com.sun.identity.security.AppSSOTokenProvider;
import com.sun.identity.security.AppSSOTokenProviderFactory;

public class DemoAdminTokenFactory extends AppSSOTokenProviderFactory {
    
    private static Debug debug = Debug.getInstance("amDemo");

    protected AppSSOTokenProvider newProvider(String userName, String password) 
    {
        return new DemoAppSSOTokenProvider(userName, password);
    }
    
    private class DemoAppSSOTokenProvider implements AppSSOTokenProvider {
        
        private String userName;
        private String password;
        
        private DemoAppSSOTokenProvider(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }
        
        public SSOToken getAppSSOToken() {
            SSOToken result = null;
            try {
                SSOTokenManager mgr = SSOTokenManager.getInstance();
            
                //Acquire Admin SSO Token
                result = mgr.createSSOToken(
                    new AuthSPrincipal(userName), password);
                
                if (debug.messageEnabled()) {
                    debug.message("DemoAppSSOTokenProvider: token is: " 
                            + result);
                }
            } catch (Exception ex) {
                debug.error("DemoAppSSOTokenProvider: exception", ex);
            }
            
            return result;
        }
    }

}

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
 * $Id: SSOTokenProvider.java,v 1.1 2006-03-31 05:07:16 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.log.spi;

import java.security.AccessController;

import com.iplanet.sso.SSOToken;
import com.sun.identity.security.AdminTokenAction;

/**
 * This class implements <code>ITokenProvider</code> interface and
 * representing SSO token provider.
 */
public class SSOTokenProvider implements ITokenProvider{
    /**
     * Construct the <code>SSOTokenProvider</code>
     */
    public SSOTokenProvider(){
    }
    
    /**
     * Return <code>SSOToken</code> that is casted to <code>Object</code>
     * Given paramers are used to get SSOToken.
     * @param name user name
     * @param Password password for user
     * @return <code>SSOToken</code> that is casted to <code>Object</code>
     */
    public Object createToken(String name, String Password){
        SSOToken ssoToken = null;
	ssoToken = (SSOToken) AccessController.doPrivileged(
		AdminTokenAction.getInstance());
        return ssoToken;
    }
}




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
 * $Id: SAML2IDPProxy.java,v 1.1 2007-08-07 23:39:05 weisun2 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.saml2.plugins;

import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.protocol.AuthnRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface <code>SAML2IDPProxy</code> is used to find a preferred
 * Identity Authenticating provider to proxy the authentication request.
 */ 
public interface SAML2IDPProxy {

    /**
     * Returns the preferred IDP.
     * @param authnRequest original authnrequest
     * @param hostProviderID ProxyIDP providerID.
     * @param realm Realm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return providerID of the authenticating provider to be proxied.
     *  <code>null</code> to disable the proxying and continue for the local 
     *  authenticating provider. 
     * @exception SAML2Exception if error occurs. 
     */
    public String getPreferredIDP (
          AuthnRequest authnRequest, 
          String hostProviderID,
          String realm, 
          HttpServletRequest request,
          HttpServletResponse response 
    ) throws SAML2Exception;
}

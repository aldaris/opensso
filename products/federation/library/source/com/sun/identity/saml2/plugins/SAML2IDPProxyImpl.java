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
 * $Id: SAML2IDPProxyImpl.java,v 1.2 2007-10-04 04:31:35 hengming Exp $
 */

package com.sun.identity.saml2.plugins;

import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.profile.SPSSOFederate;
import com.sun.identity.saml2.protocol.AuthnRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class <code>SAML2IDPProxyImpl</code> is used to find a preferred Identity
 * Authenticating provider to proxy the authentication request.
 */ 
public class SAML2IDPProxyImpl implements SAML2IDPFinder {
    /**
     * Default Constructor.
     */
    public SAML2IDPProxyImpl(){}

    /**
     * Returns a list of preferred IDP providerIDs.
     * @param authnRequest original authnrequest
     * @param hostProviderID ProxyIDP providerID.
     * @param realm Realm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return a list of providerID's of the authenticating providers to be
     *     proxied or <code>null</code> to disable the proxying and continue
     *     for the localauthenticating provider.
     * @exception SAML2Exception if error occurs.
     */
    public List getPreferredIDP (
          AuthnRequest authnRequest,
          String hostProviderID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response
    ) throws SAML2Exception
    {       
        SAML2Utils.debug.message("SAML2IDPProxyImpl.getPreferredIDP:Init");
        try {
            SAML2MetaManager sm = new SAML2MetaManager();
            // Retreive MetaData
            if (sm == null) {
                throw new SAML2Exception(
                    SAML2Utils.bundle.getString("errorMetaManager"));
            }
            SPSSOConfigElement spEntityCfg =
                sm.getSPSSOConfig(realm, authnRequest.getIssuer().getValue());
            Map spConfigAttrsMap = null;
            if (spEntityCfg != null) {
                spConfigAttrsMap = SAML2MetaUtils.getAttributes(spEntityCfg);
            }         
            String useIntroductionForProxying = 
                SPSSOFederate.getParameter(spConfigAttrsMap,
                    SAML2Constants.USE_INTRODUCTION_FOR_IDP_PROXY);

            if (useIntroductionForProxying == null ||
                !useIntroductionForProxying.equals("true")) 
            {
                List proxyIDPs = (List) spConfigAttrsMap.get(
                    SAML2Constants.IDP_PROXY_LIST);
                if (proxyIDPs == null || proxyIDPs.isEmpty()) {
                    SAML2Utils.debug.error("SAML2IDPProxyImpl.getPrefferedIDP:" +
                        "Preferred IDPs are null.");
                    return null;
                }
                List providerIDs = new ArrayList();
                providerIDs.add(proxyIDPs.iterator().next());
                return providerIDs;
            } else {
                /* TODO: test introduction cookie case*/
                /*
                StringBuffer redirectURL = new StringBuffer(100);
                String baseURL = FSServiceUtils.getBaseURL(request);
                redirectURL.append(baseURL).append(SAML2Constants.IDP_FINDER_URL)
                        .append("?").append("RequestID=")
                        .append(authnRequest.getRequestID())
                        .append("&").append("ProviderID=")
                        .append(hostEntityID);
                SAML2Utils.forwardRequest(
                    request, response, redirectURL.toString());
                throw new FSRedirectException(SAML2Utils.bundle.getString(
                    "Redirection_Happened"));
                */  
                return null;    
            }
        } catch (SAML2MetaException ex) {
            SAML2Utils.debug.error("SAML2IDPProxyImpl.getPreferredIDP: " +
                "meta Exception in retrieving the preferred IDP", ex);
            return null;
        } catch (Exception e) {
            SAML2Utils.debug.error("SAML2IDPProxyImpl.getPreferredIDP: " +
                "Exception in retrieving the preferred IDP", e);
            return null;
        }
    }
}

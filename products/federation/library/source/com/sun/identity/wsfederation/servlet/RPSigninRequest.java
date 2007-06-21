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
 * $Id: RPSigninRequest.java,v 1.1 2007-06-21 23:01:33 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.servlet;

import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.shared.DateUtils;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.encode.URLEncDec;
import com.sun.identity.wsfederation.common.WSFederationConstants;
import com.sun.identity.wsfederation.meta.WSFederationMetaException;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.identity.wsfederation.common.WSFederationUtils;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.meta.WSFederationMetaUtils;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pat
 */
public class RPSigninRequest extends WSFederationAction {
    private static Debug debug = WSFederationUtils.debug;
    
    String whr;
    String wtrealm;
    String wreply;
    String wctx;
    String wct;
            
    /**
     * Creates a new instance of RPSigninRequest
     * @param request 
     * @param response 
     * @param whr 
     * @param wtrealm 
     * @param wreply 
     * @param wctx 
     */
    public RPSigninRequest(HttpServletRequest request,
        HttpServletResponse response, String whr, String wtrealm, 
        String wct, String wctx, String wreply) {
        super(request,response);
        this.whr = whr;
        this.wtrealm = wtrealm;
        this.wct = wct;
        this.wctx = wctx;
        this.wreply = wreply;
    }
    
    public void process() throws IOException
    {
        String classMethod = "RPSigninRequest.process: ";
        
        try {
            if (debug.messageEnabled()) {
                debug.message(classMethod+"entered method");
            }
            
            if (wctx == null || wctx.length() == 0)
            {
                // Exchange reply URL for opaque identifier
                wctx = (wreply != null && (wreply.length() > 0)) ? 
                    WSFederationUtils.putReplyURL(wreply) : null;
            }
            
            String spEntityId = 
                WSFederationMetaManager.getEntityByMetaAlias(wtrealm);
            String spRealm = SAML2MetaUtils.getRealmByMetaAlias(wtrealm);
            Map<String,List<String>> spConfig = 
                WSFederationMetaUtils.getAttributes(
                WSFederationMetaManager.getSPSSOConfig(spRealm,spEntityId));

            String accountRealmSelection = 
                    spConfig.get(
                    com.sun.identity.wsfederation.common.WSFederationConstants.ACCOUNT_REALM_SELECTION).get(0);
            if ( accountRealmSelection == null )
            {
                accountRealmSelection = 
                    WSFederationConstants.ACCOUNT_REALM_SELECTION_DEFAULT;
            }
            String accountRealmCookieName = 
                spConfig.get(WSFederationConstants.ACCOUNT_REALM_COOKIE_NAME).get(0);
            if ( accountRealmCookieName == null )
            {
                accountRealmCookieName = 
                    WSFederationConstants.ACCOUNT_REALM_COOKIE_NAME_DEFAULT;
            }
            String homeRealmDiscoveryService = 
                spConfig.get(
                WSFederationConstants.HOME_REALM_DISCOVERY_SERVICE).get(0);
            
            if (debug.messageEnabled()) {
                debug.message(classMethod+"account realm selection method is " + 
                    accountRealmSelection);
            }

            String idpEntityId = null;
            if (whr != null && whr.length() > 0)
            {
                // whr parameter overrides other mechanisms...
                idpEntityId = whr;

                if (accountRealmSelection.equals(WSFederationConstants.COOKIE)) 
                {
                    // ...and overwrites cookie
                    Cookie cookie = new Cookie(accountRealmCookieName,whr);
                    // Set cookie to persist for a year
                    cookie.setMaxAge(60*60*24*365);
                    response.addCookie(cookie);
                }
            }
            else
            {
                if (accountRealmSelection.equals(
                    WSFederationConstants.USERAGENT)) {
                    String uaHeader = 
                        request.getHeader(WSFederationConstants.USERAGENT);
                    if (debug.messageEnabled()) {
                        debug.message(classMethod+"user-agent is :" + uaHeader);
                    }
                    idpEntityId = 
                        WSFederationUtils.accountRealmFromUserAgent(uaHeader, 
                        accountRealmCookieName);
                } else if (accountRealmSelection.equals(
                    WSFederationConstants.COOKIE)) {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (int i = 0; i < cookies.length; i++) {
                            if (cookies[i].getName().equals(
                                accountRealmCookieName)) {
                                idpEntityId = cookies[i].getValue();
                                break;
                            }
                        }
                    }
                } else {
                    debug.error(classMethod+"unexpected value for " + 
                        WSFederationConstants.ACCOUNT_REALM_SELECTION + " : " + 
                        accountRealmSelection);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }

            if (idpEntityId == null) {
                StringBuffer url = new StringBuffer(homeRealmDiscoveryService);
                url.append("?wtrealm=" + wtrealm);
                url.append("&wreply=" + request.getRequestURL());
                if (wctx != null) {
                    url.append("&wctx=" + wctx);
                }
                if (debug.messageEnabled()) {
                    debug.message(classMethod + 
                        "no account realm - redirecting to :" + url);
                }
                response.sendRedirect(url.toString());
                return;
            }
            if (debug.messageEnabled()) {
                debug.message(classMethod+"account realm:" + idpEntityId);
            }
            
            FederationElement idp = 
                WSFederationMetaManager.getEntityDescriptor(null,idpEntityId);
            FederationElement sp = 
                WSFederationMetaManager.getEntityDescriptor(null,spEntityId);
            
            String endpoint = 
                WSFederationMetaManager.getTokenIssuerEndpoint(idp);
            if (debug.messageEnabled()) {
                debug.message(classMethod+"endpoint:" + endpoint);
            }
            String resourceRealm = 
                WSFederationMetaManager.getTokenIssuerName(sp);
            if (debug.messageEnabled()) {
                debug.message(classMethod+"resource realm:" + resourceRealm);
            }
            String replyURL = 
                WSFederationMetaManager.getTokenIssuerEndpoint(sp);
            if (debug.messageEnabled()) {
                debug.message(classMethod+"replyURL:" + replyURL);
            }
            StringBuffer url = new StringBuffer(endpoint);
            url.append("?wa=");
            url.append(URLEncDec.encode(WSFederationConstants.WSIGNIN10));
            url.append("&wctx=");
            url.append(URLEncDec.encode(wctx));
            url.append("&wreply=");
            url.append(URLEncDec.encode(replyURL));
            url.append("&wct=");
            url.append(URLEncDec.encode(DateUtils.toUTCDateFormat(new Date())));
            url.append("&wtrealm=");
            url.append(URLEncDec.encode(resourceRealm));
            if (debug.messageEnabled()) {
                debug.message(classMethod+"Redirecting to:" + url);
            }
            response.sendRedirect(url.toString());
        } catch (WSFederationMetaException ex) {
            debug.error(classMethod+"WSFederationMetaException ", ex);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    }
}

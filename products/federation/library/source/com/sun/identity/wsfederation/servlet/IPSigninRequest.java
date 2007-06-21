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
 * $Id: IPSigninRequest.java,v 1.1 2007-06-21 23:01:32 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.servlet;

import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.shared.DateUtils;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.encode.URLEncDec;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.identity.wsfederation.common.WSFederationUtils;

/**
 *
 * @author pat
 */
public class IPSigninRequest extends WSFederationAction {
    private static Debug debug = WSFederationUtils.debug;
    
    String wtrealm;
    String whr;
    String wct;
    String wctx;
    String wreply;
            
    /**
     * Creates a new instance of RPSigninRequest
     */
    public IPSigninRequest(HttpServletRequest request,
        HttpServletResponse response, String whr, String wtrealm, String wct,
        String wctx, String wreply) {
        super(request,response);
        this.whr = whr;
        this.wtrealm = wtrealm;
        this.wct = wct;
        this.wctx = wctx;
        this.wreply = wreply;
    }
    
    public void process() throws IOException
    {
        String classMethod = "IPSigninRequest.process: ";
        Object session = null;

        // get the user sso session from the request
        try {
            session = SessionManager.getProvider().getSession(request);
        } catch (SessionException se) {
            if (debug.messageEnabled()) {
                debug.message(
                    classMethod + "Unable to retrieve user session.");
            }
            session = null;
        }

        if (session == null) {
            // the user has not logged in yet, redirect to auth
            try {
                redirectAuthentication();
            } catch (IOException ioe) {
                debug.error(classMethod +
                    "Unable to redirect to authentication.", ioe);
                response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                   SAML2Utils.bundle.getString("UnableToRedirectToAuth"));
            } catch (SAML2Exception se) {
                debug.error(classMethod +
                    "Unable to redirect to authentication.", se);
                response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                   SAML2Utils.bundle.getString("UnableToRedirectToAuth"));
            } 
            return;
        }

        // TODO
        boolean sessionUpgrade = false;
        
        if (!sessionUpgrade) {
             sendResponse();
        }
    }

    /**
     * Redirect to authenticate service
     */
    private void redirectAuthentication() 
        throws SAML2Exception, IOException {
        /* JUST GET IT TO COMPILE!
        String classMethod = "IDPSSOFederate.redirectAuthentication: ";
        // get the authentication service url 
        StringBuffer newURL = new StringBuffer(
                                IDPSSOUtil.getAuthenticationServiceURL(
                                realm, idpEntityID, request));
        // find out the authentication method, e.g. module=LDAP, from
        // authn context mapping 
        IDPAuthnContextMapper idpAuthnContextMapper = 
            IDPSSOUtil.getIDPAuthnContextMapper(realm, idpEntityID);
        
        IDPAuthnContextInfo info = 
            idpAuthnContextMapper.getIDPAuthnContextInfo(
                authnReq, idpEntityID, realm);
        Set authnTypeAndValues = info.getAuthnTypeAndValues();
        if ((authnTypeAndValues != null) 
            && (!authnTypeAndValues.isEmpty())) { 
            Iterator iter = authnTypeAndValues.iterator();
            StringBuffer authSB = new StringBuffer((String)iter.next());
            while (iter.hasNext()) {
                authSB.append("&"); 
                authSB.append((String)iter.next());
            }
            if (newURL.indexOf("?") == -1) {
                newURL.append("?");
            } else {
                newURL.append("&");
            }
            newURL.append(authSB.toString());
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(classMethod +
                    "authString=" + authSB.toString());
            }
        }
        if (newURL.indexOf("?") == -1) {
            newURL.append("?goto=");
        } else {
            newURL.append("&goto=");
        }
        newURL.append(URLEncDec.encode(request.getRequestURL().
                       append("?ReqID=").append(reqID).toString()));
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(classMethod +
                "New URL for authentication: " + newURL.toString());
        }
        // TODO: here we should check if the new URL is one
        //       the same web container, if yes, forward,
        //       if not, redirect
        response.sendRedirect(newURL.toString());
        */
    }
    /**
     * Sends <code>Response</code> containing an <code>Assertion</code>
     * back to the requesting service provider
     *
     * @param request the <code>HttpServletRequest</code> object
     * @param response the <code>HttpServletResponse</code> object
     * @param authnReq the <code>AuthnRequest</code> object
     * @param idpMetaAlias the meta alias of the identity provider
     * @param relayState the relay state 
     * 
     */
    private void sendResponse() 
        throws IOException {
        /*

        String classMethod = "IDPSSOFederate.sendResponeToACS: " ;
        String spEntityID = authnReq.getIssuer().getValue();
    
        String nameIDFormat = null;
        NameIDPolicy policy = authnReq.getNameIDPolicy();
        if (policy != null) {
            nameIDFormat = policy.getFormat();
        }
        try {
            IDPSSOUtil.doSSOFederate(request, response, authnReq, 
                  spEntityID, idpMetaAlias, nameIDFormat, relayState); 
        } catch (SAML2Exception se) {
            SAML2Utils.debug.error(classMethod +
                "Unable to do sso or federation.", se);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                SAML2Utils.bundle.getString("UnableToDOSSOOrFederation"));
       
        }
         */
    }
}

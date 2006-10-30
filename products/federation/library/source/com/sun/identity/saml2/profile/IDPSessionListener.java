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
 * $Id: IDPSessionListener.java,v 1.1 2006-10-30 23:16:35 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.profile;

import com.sun.identity.plugin.session.SessionListener;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionProvider;
import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Utils;

/**
 * The class <code>IDPSessionListener</code> implements
 * SessionListener interface and is used for maintaining the 
 * IDP session cache.
 */

public class IDPSessionListener 
             implements SessionListener {
    /**
     *  Constructor of <code>IDPSessionListener</code>.
     */

    public IDPSessionListener() {
    }

    /**
     *  Callback for SessionListener.
     *  It is used for cleaning up the IDP session cache.
     *  
     *  @param session The session object
     */
    public void sessionInvalidated(Object session)
    {
        String classMethod = "IDPSessionListener.sessionInvalidated: "; 
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                classMethod + "Entering ...");
        }
        if (session == null) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(
                    classMethod + "Session is null.");
            }
            return;
        }
        try {
           SessionProvider sessionProvider = SessionManager.getProvider(); 
           String[] values = sessionProvider.getProperty(
               session, SAML2Constants.IDP_SESSION_INDEX);
           if (values == null || values.length == 0) {
               if (SAML2Utils.debug.messageEnabled()) {
                   SAML2Utils.debug.message(
                       classMethod +
                       "No sessionIndex stored in session.");
               }
               return;
           }
           String sessionIndex = values[0];
           if (sessionIndex == null || sessionIndex.length() == 0) {
               if (SAML2Utils.debug.messageEnabled()) {
                   SAML2Utils.debug.message(
                       classMethod +
                       "No sessionIndex stored in session.");
               }
               return;
           }
           IDPCache.idpSessionsByIndices.remove(sessionIndex);
           if (SAML2Utils.debug.messageEnabled()) {
               SAML2Utils.debug.message(
                   classMethod +
                   "cleaned up the IDP session cache for a session" +
                   " expiring or being destroyed: sessionIndex=" +
                   sessionIndex);
           }
        } catch (SessionException e) {
            if (SAML2Utils.debug.warningEnabled()) {
                SAML2Utils.debug.warning(
                    classMethod + "invalid or expired session.", e);
            }
        }
    }
}

/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: SPSessionListener.java,v 1.3 2008-06-25 05:47:55 qcheng Exp $
 *
 */


package com.sun.identity.saml2.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.identity.plugin.session.SessionListener;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionProvider;
import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.saml2.common.SAML2Utils;

/**
 * The class <code>SPSessionListener</code> implements
 * SessionListener interface and is used for maintaining the 
 * SP session cache.
 */

public class SPSessionListener implements SessionListener {

    private String infoKeyString = null;
    private String sessionID = null;

    /**
     *  Constructor of <code>SPSessionListener</code>.
     */
    public SPSessionListener(String infoKeyString, String sessionID) {
        this.infoKeyString = infoKeyString;
        this.sessionID = sessionID;
    }

    /**
     *  Callback for SessionListener.
     *  It is used for cleaning up the SP session cache.
     *  
     *  @param session The session object
     */
    public void sessionInvalidated(Object session)
    {
        String classMethod = "SPSessionListener.sessionInvalidated: "; 
        if (session == null || infoKeyString == null ||
            sessionID == null) {
            return;
        }
        SessionProvider sessionProvider = null;
        try {
            sessionProvider = SessionManager.getProvider();
        } catch (SessionException se) {
            return;
        }
        if (!sessionID.equals(sessionProvider.getSessionID(session)))
        {
            return;
        }
        List fedSessionList = (List)
            SPCache.fedSessionListsByNameIDInfoKey.get(infoKeyString);
        if (fedSessionList == null) {
            return;
        }
   
        synchronized (fedSessionList) {
            Iterator iter = fedSessionList.iterator();
            while (iter.hasNext()) {
                SPFedSession temp = (SPFedSession) iter.next();
                if (temp.spTokenID.equals(sessionID)) {
                    iter.remove();
                } 
            }
            if (fedSessionList.isEmpty()) {
                SPCache.fedSessionListsByNameIDInfoKey.remove(
                    infoKeyString);
            }
        }    
    }
}



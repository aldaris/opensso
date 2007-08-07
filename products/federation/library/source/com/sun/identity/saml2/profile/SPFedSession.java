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
 * $Id: SPFedSession.java,v 1.2 2007-08-07 23:39:07 weisun2 Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.profile;

import com.sun.identity.saml2.common.NameIDInfo;
import java.util.List; 
import java.util.ArrayList; 
import java.util.Iterator;
import com.sun.identity.saml2.common.SAML2Utils;

/**
 * This class provides the memory store for
 * SAML request and response information on Service Provider side.
 *
 */

public class SPFedSession {
    List sessionPartners;
    /**
     * <code>SessionIndex</code> from IDP.
     */
    public String idpSessionIndex = null;

    /**
     * SP side session ID.
     */
    public String spTokenID = null;

    /**
     * <code>NameIDInfo</code> for the session.
     */
    public NameIDInfo info = null;
    
    /**
     * Constructs new <code>SPFedSession</code> object
     *
     * @param idpSessionIndex sessionIndex of Identity Provider
     * @param sessionID session id of Service Provider
     * @param info NameIDInfo object.
     */
    public SPFedSession(String idpSessionIndex,
                        String sessionID,
                        NameIDInfo info)
    {
        this.idpSessionIndex = idpSessionIndex;
        this.spTokenID = sessionID;
        this.info = info;
        this. sessionPartners = new ArrayList();
    }
}

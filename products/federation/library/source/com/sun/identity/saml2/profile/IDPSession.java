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
 * $Id: IDPSession.java,v 1.1 2006-10-30 23:16:35 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.profile;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a session in the identity provider side.
 * It keeps track of information that is needed for single sign on
 * as well as single log out.
 */

public class IDPSession {

    private Object session = null;
    private List nameIDandSPpairs = null;
    private String pendingLogoutRequestID = null; 
    private String originatingLogoutRequestID = null;
    private String originatingLogoutSPEntityID = null;
    private boolean doLogoutAll = false;   
    /**
     * Constructor for a <code>IDPSession</code>.
     *
     * @param session the session object corresponding 
     *   to the <code>IDPSession</code>
     */ 
    public IDPSession(Object session) {
        this.session = session;
        nameIDandSPpairs = new ArrayList();
    }
    
    /**
     * Returns the session object.
     *
     * @return the session object.
     */ 
    public Object getSession() {
        return session;
    }
    
    /**
     * Returns the list of <code>NameID</code> and 
     *    <code>SPEntityID</code> pair of the session.
     *
     * @return the list of <code>NameID</code> and 
     *    <code>SPEntityID</code> pair of the session      
     */ 
    public List getNameIDandSPpairs() {
        return nameIDandSPpairs;
    }
    
    /**
     * Sets the pending log out request id of the session.
     *
     * @param id the request id
     */ 
    public void setPendingLogoutRequestID(String id) {
        pendingLogoutRequestID = id;
    }
    
    /**
     * Returns the pending log out request id of the session.
     *
     * @return id the pending log out request id
     */ 
    public String getPendingLogoutRequestID() {
        return pendingLogoutRequestID;
    }
    
    /**
     * Sets the original log out request id of the session.
     *
     * @param id the request id
     */ 
    public void setOriginatingLogoutRequestID(String id) {
        originatingLogoutRequestID = id;
    }
    
    /**
     * Returns the original log out request id of the session.
     *
     * @return the original log out request id
     */ 
    public String getOriginatingLogoutRequestID() {
        return originatingLogoutRequestID;
    }
    
    /**
     * Sets the original log out <code>SPEntityID</code> of the session.
     *
     * @param id the <code>SPEntityID</code>
     */ 
    public void setOriginatingLogoutSPEntityID(String id) {
        originatingLogoutSPEntityID = id;
    }
    
    /**
     * Returns the original log out <code>SPEntityID</code> of the session.
     *
     * @return the original log out <code>SPEntityID</code> 
     */ 
    public String getOriginatingLogoutSPEntityID() {
        return originatingLogoutSPEntityID;
    }

    /**
     * Sets the logoutAll property.
     *
     * @param enable true or false
     */ 
    public void setLogoutAll(boolean enable) {
        doLogoutAll = enable;
    }

    /**
     * Returns the logoutAll property.
     *
     * @return the logoutAll property.
     */ 
    public boolean getLogoutAll() {
        return doLogoutAll;
    }
}

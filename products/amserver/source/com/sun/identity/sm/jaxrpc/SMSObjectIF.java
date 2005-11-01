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
 * $Id: SMSObjectIF.java,v 1.1 2005-11-01 00:31:36 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm.jaxrpc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import com.iplanet.sso.SSOException;
import com.sun.identity.sm.SMSException;

/**
 * JAX-RPC interface for SMSObject and Services
 */
public interface SMSObjectIF extends Remote {

    public void checkForLocal() throws RemoteException;

    public Map read(String t, String name) throws SMSException, SSOException,
            RemoteException;

    public void create(String token, String objName, Map attributes)
            throws SMSException, SSOException, RemoteException;

    public void modify(String token, String objName, String mods)
            throws SMSException, SSOException, RemoteException;

    public void delete(String token, String objName) throws SMSException,
            SSOException, RemoteException;

    public Set subEntries(String token, String dn, String filter,
            int numOfEntries, boolean sortResults, boolean ascendingOrder)
            throws SMSException, SSOException, RemoteException;

    public Set schemaSubEntries(String token, String dn, String filter,
            String sidFilter, int numOfEntries, boolean sortResults, boolean ao)
            throws SMSException, SSOException, RemoteException;

    public Set search(String token, String startDN, String filter)
            throws SMSException, SSOException, RemoteException;

    public Set searchSubOrgNames(String token, String dn, String filter,
            int numOfEntries, boolean sortResults, boolean ascendingOrder,
            boolean recursive) throws SMSException, SSOException,
            RemoteException;

    public Set searchOrganizationNames(String token, String dn,
            int numOfEntries, boolean sortResults, boolean ascendingOrder,
            String serviceName, String attrName, Set values)
            throws SMSException, SSOException, RemoteException;

    public boolean entryExists(String token, String objName)
            throws SSOException, RemoteException;

    public String getRootSuffix() throws RemoteException;

    // Objects changed within <i>time</i> minutes
    public Set objectsChanged(int time) throws RemoteException;

    public String registerNotificationURL(String url) throws RemoteException;

    public void deRegisterNotificationURL(String notificationID)
            throws RemoteException;

    // Interface to receive object changed notifications
    public void notifyObjectChanged(String name, int type)
            throws RemoteException;
}

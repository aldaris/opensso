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
 * $Id: DirectoryManagerImpl.java,v 1.1 2005-11-01 00:29:32 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk.remote;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.iplanet.am.sdk.AMDirectoryManager;
import com.iplanet.am.sdk.AMDirectoryWrapper;
import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMObjectListener;
import com.iplanet.am.sdk.AMSearchResults;
import com.iplanet.am.sdk.IdRepoListener;
import com.iplanet.am.sdk.ldap.Compliance;
import com.iplanet.am.sdk.ldap.DCTree;
import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.comm.server.PLLServer;
import com.iplanet.services.comm.server.SendNotificationException;
import com.iplanet.services.comm.share.Notification;
import com.iplanet.services.comm.share.NotificationSet;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.iplanet.ums.SearchControl;
import com.iplanet.ums.SortKey;
import com.sun.identity.common.CaseInsensitiveHashMap;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdOperation;
import com.sun.identity.idm.IdRepo;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.SMSUtils;
import com.sun.identity.sm.SchemaType;

public class DirectoryManagerImpl implements DirectoryManagerIF,
        AMObjectListener {

    // protected static DirectoryManager dMgr = DirectoryManager.getInstance();
    protected static AMDirectoryManager dMgr = AMDirectoryWrapper.getInstance();

    protected static DCTree dcTree = new DCTree();

    protected static Compliance compl = new Compliance();

    protected static Debug debug = Debug.getInstance("amProfile_Server");

    protected static SSOTokenManager tm;

    protected static boolean initialized;

    // Cache of modifications for last 30 minutes & notification URLs
    static int cacheSize = 30;

    static LinkedList cacheIndices = new LinkedList();

    static LinkedList idrepoCacheIndices = new LinkedList();

    static HashMap cache = new HashMap(cacheSize);

    static HashMap idrepoCache = new HashMap(cacheSize);

    static HashMap notificationURLs = new HashMap();

    static String serverURL;

    // public static AMObjectListener idrepoListener = null;

    public DirectoryManagerImpl() {
        if (initialized) {
            return;
        }

        // Construct serverURL
        serverURL = SystemProperties.get("com.iplanet.am.server.protocol")
                + "://" + SystemProperties.get("com.iplanet.am.server.host")
                + ":" + SystemProperties.get("com.iplanet.am.server.port");

        // Get TokenManager and register this class for events
        try {
            tm = SSOTokenManager.getInstance();
            dMgr.addListener((SSOToken) AccessController
                    .doPrivileged(AdminTokenAction.getInstance()), this);
            IdRepoListener.addRemoteListener(new IdRepoEventListener());
            // idrepoListener = new IdEventListener();
            initialized = true;
            if (debug.messageEnabled()) {
                debug.message("DirectoryManagerImpl::init success: "
                        + serverURL);
            }
        } catch (Exception e) {
            // Debug the message
            debug.error("DirectoryManagerImpl::init ERROR", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#createAMTemplate(
     *      java.lang.String, java.lang.String, int, java.lang.String,
     *      java.util.Map, int)
     */
    public String createAMTemplate(String token, String entryDN,
            int objectType, String serviceName, Map attributes, int priority)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.createAMTemplate(ssoToken, entryDN, objectType,
                    serviceName, attributes, priority);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#createEntry(
     *      java.lang.String, java.lang.String, int, java.lang.String,
     *      java.util.Map)
     */
    public void createEntry(String token, String entryName, int objectType,
            String parentDN, Map attributes) throws AMRemoteException,
            SSOException, RemoteException {

        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dMgr.createEntry(ssoToken, entryName, objectType, parentDN,
                    attributes);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#doesEntryExists(
     *      java.lang.String, java.lang.String)
     */
    public boolean doesEntryExists(String token, String entryDN)
            throws AMRemoteException, SSOException, RemoteException {
        SSOToken ssoToken = tm.createSSOToken(token);
        return dMgr.doesEntryExists(ssoToken, entryDN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#getAMTemplateDN(
     *      java.lang.String, java.lang.String, int, java.lang.String, int)
     */
    public String getAMTemplateDN(String token, String entryDN, int objectType,
            String serviceName, int type) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getAMTemplateDN(ssoToken, entryDN, objectType,
                    serviceName, type);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#getAttributes(
     *      java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public Map getAttributes3(String token, String entryDN,
            boolean ignoreCompliance, boolean byteValues, int profileType)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getAttributes(ssoToken, entryDN, ignoreCompliance,
                    byteValues, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#getAttributes(
     *      java.lang.String, java.lang.String, int)
     */
    public Map getAttributes1(String token, String entryDN, int profileType)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getAttributes(ssoToken, entryDN, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#getAttributes(
     *      java.lang.String, java.lang.String, java.util.Set, int)
     */
    public Map getAttributes2(String token, String entryDN, Set attrNames,
            int profileType) throws AMRemoteException, SSOException,
            RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr
                    .getAttributes(ssoToken, entryDN, attrNames, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#getAttributesByteValues
     *      (java.lang.String, java.lang.String, int)
     */
    public Map getAttributesByteValues1(String token, String entryDN,
            int profileType) throws AMRemoteException, SSOException,
            RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getAttributesByteValues(ssoToken, entryDN, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getAttributesByteValues(java.lang.String, java.lang.String,
     *      java.util.Set, int)
     */
    public Map getAttributesByteValues2(String token, String entryDN,
            Set attrNames, int profileType) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getAttributesByteValues(ssoToken, entryDN, attrNames,
                    profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getAttributesForSchema(java.lang.String)
     */
    public Set getAttributesForSchema(String objectclass)
            throws RemoteException {
        return dMgr.getAttributesForSchema(objectclass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getCreationTemplateName(int)
     */
    public String getCreationTemplateName(int objectType)
            throws RemoteException {
        com.iplanet.am.sdk.ldap.DirectoryManager dm = 
            com.iplanet.am.sdk.ldap.DirectoryManager.getInstance();
        return dm.getCreationTemplateName(objectType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getDCTreeAttributes(java.lang.String, java.lang.String,
     *      java.util.Set, boolean, int)
     */
    public Map getDCTreeAttributes(String token, String entryDN, Set attrNames,
            boolean byteValues, int objectType) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getDCTreeAttributes(ssoToken, entryDN, attrNames,
                    byteValues, objectType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getDeletedObjectFilter(int)
     */
    public String getDeletedObjectFilter(int objecttype)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            return compl.getDeletedObjectFilter(objecttype);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getExternalAttributes(java.lang.String, java.lang.String,
     *      java.util.Set, int)
     */
    public Map getExternalAttributes(String token, String entryDN,
            Set attrNames, int profileType) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getExternalAttributes(ssoToken, entryDN, attrNames,
                    profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getGroupFilterAndScope(java.lang.String, java.lang.String, int)
     */
    public LinkedList getGroupFilterAndScope(String token, String entryDN,
            int profileType) throws AMRemoteException, SSOException,
            RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            String[] array = dMgr.getGroupFilterAndScope(ssoToken, entryDN,
                    profileType);
            LinkedList list = new LinkedList();
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
            return list;
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getMembers(java.lang.String, java.lang.String, int)
     */
    public Set getMembers(String token, String entryDN, int objectType)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getMembers(ssoToken, entryDN, objectType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF# getNamingAttr(int,
     *      java.lang.String)
     */
    public String getNamingAttr(int objectType, String orgDN)
            throws RemoteException {

        com.iplanet.am.sdk.ldap.DirectoryManager dm = 
            com.iplanet.am.sdk.ldap.DirectoryManager.getInstance();
        return dm.getNamingAttr(objectType, orgDN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getObjectClassFromDS(int)
     */
    public String getObjectClassFromDS(int objectType) throws RemoteException {
        com.iplanet.am.sdk.ldap.DirectoryManager dm = 
            com.iplanet.am.sdk.ldap.DirectoryManager.getInstance();
        return dm.getObjectClassFromDS(objectType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getObjectType(java.lang.String, java.lang.String)
     */
    public int getObjectType(String token, String dn) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getObjectType(ssoToken, dn);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getOrganizationDN(java.lang.String, java.lang.String)
     */
    public String getOrganizationDN(String token, String entryDN)
            throws AMRemoteException, RemoteException, SSOException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getOrganizationDN(ssoToken, entryDN);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      verifyAndGetOrganizationDN(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public String verifyAndGetOrgDN(String token, String entryDN, 
            String childDN) throws AMRemoteException, RemoteException, 
            SSOException 
    {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.verifyAndGetOrgDN(ssoToken, entryDN, childDN);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getOrgDNFromDomain(java.lang.String, java.lang.String)
     */
    public String getOrgDNFromDomain(String token, String domain)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dcTree.getOrganizationDN(ssoToken, domain);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getOrgSearchFilter(java.lang.String)
     */
    public String getOrgSearchFilter(String entryDN) throws RemoteException {
        return dMgr.getOrgSearchFilter(entryDN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getRegisteredServiceNames(java.lang.String, java.lang.String)
     */
    public Set getRegisteredServiceNames(String token, String entryDN)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            // SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getRegisteredServiceNames(null, entryDN);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getSearchFilterFromTemplate(int, java.lang.String, java.lang.String)
     */
    public String getSearchFilterFromTemplate(int objectType, String orgDN,
            String searchTemplateName) throws RemoteException {
        com.iplanet.am.sdk.ldap.DirectoryManager dm =
            com.iplanet.am.sdk.ldap.DirectoryManager.getInstance();
        return dm.getSearchFilterFromTemplate(objectType, orgDN,
                searchTemplateName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getTopLevelContainers(java.lang.String)
     */
    public Set getTopLevelContainers(String token) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getTopLevelContainers(ssoToken);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      isAncestorOrgDeleted(java.lang.String, java.lang.String, int)
     */
    public boolean isAncestorOrgDeleted(String token, String dn, 
            int profileType) throws AMRemoteException, SSOException, 
                RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return compl.isAncestorOrgDeleted(ssoToken, dn, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      modifyMemberShip(java.lang.String, java.util.Set, java.lang.String,
     *      int, int)
     */
    public void modifyMemberShip(String token, Set members, String target,
            int type, int operation) throws AMRemoteException, SSOException,
            RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dMgr.modifyMemberShip(ssoToken, members, target, type, operation);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      registerService(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void registerService(String token, String orgDN, String serviceName)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dMgr.registerService(ssoToken, orgDN, serviceName);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      removeAdminRole(java.lang.String, java.lang.String, boolean)
     */
    public void removeAdminRole(String token, String dn, boolean recursive)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dMgr.removeAdminRole(ssoToken, dn, recursive);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      removeEntry(java.lang.String, java.lang.String, int, boolean,
     *      boolean)
     */
    public void removeEntry(String token, String entryDN, int objectType,
            boolean recursive, boolean softDelete) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dMgr.removeEntry(ssoToken, entryDN, objectType, recursive,
                    softDelete);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      renameEntry(java.lang.String, int, java.lang.String,
     *      java.lang.String, boolean)
     */
    public String renameEntry(String token, int objectType, String entryDN,
            String newName, boolean deleteOldName) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.renameEntry(ssoToken, objectType, entryDN, newName,
                    deleteOldName);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      search(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public Set search1(String token, String entryDN, String searchFilter,
            int searchScope) throws AMRemoteException, SSOException,
            RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.search(ssoToken, entryDN, searchFilter, searchScope);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     */
    public Map search2(String token, String entryDN, String searchFilter,
            List sortKeys, int startIndex, int beforeCount, int afterCount,
            String jumpTo, int timeOut, int maxResults, int scope,
            boolean allAttributes, String[] attrNames)
            throws AMRemoteException, SSOException, RemoteException {
        // Construct the SortKeys
        SortKey[] keys = null;
        int keysLength = 0;
        if (sortKeys != null && (keysLength = sortKeys.size()) != 0) {
            keys = new SortKey[keysLength];
            for (int i = 0; i < keysLength; i++) {
                String data = (String) sortKeys.get(i);
                keys[i] = new SortKey();
                if (data.startsWith("true:")) {
                    keys[i].reverse = true;
                } else {
                    keys[i].reverse = false;
                }
                keys[i].attributeName = data.substring(5);
            }
        }
        // Construct SearchControl
        SearchControl sc = new SearchControl();
        if (keys != null) {
            sc.setSortKeys(keys);
        }
        if (jumpTo == null) {
            sc.setVLVRange(startIndex, beforeCount, afterCount);
        } else {
            sc.setVLVRange(jumpTo, beforeCount, afterCount);
        }
        sc.setTimeOut(timeOut);
        sc.setMaxResults(maxResults);
        sc.setSearchScope(scope);
        sc.setAllReturnAttributes(allAttributes);

        // Perform the search
        try {
            AMSearchResults results = dMgr.search(tm.createSSOToken(token),
                    entryDN, searchFilter, sc, attrNames);
            // Convert results to Map
            Map answer = results.getResultAttributes();
            if (answer == null) {
                answer = new HashMap();
            }
            answer.put(com.iplanet.am.sdk.remote.DirectoryManager.AMSR_COUNT,
                    Integer.toString(results.getTotalResultCount()));
            answer.put(com.iplanet.am.sdk.remote.DirectoryManager.AMSR_RESULTS,
                    results.getSearchResults());
            answer.put(com.iplanet.am.sdk.remote.DirectoryManager.AMSR_CODE,
                    Integer.toString(results.getErrorCode()));
            return (answer);
        } catch (AMException amex) {
            debug.error("DMI::search(with SearchControl):  " + amex);
            throw convertException(amex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      setAttributes(java.lang.String, java.lang.String, int,
     *      java.util.Map, java.util.Map, boolean)
     */
    public void setAttributes(String token, String entryDN, int objectType,
            Map stringAttributes, Map byteAttributes, boolean isAdd)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dMgr.setAttributes(ssoToken, entryDN, objectType, stringAttributes,
                    byteAttributes, isAdd);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      setGroupFilter(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setGroupFilter(String token, String entryDN, String filter)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dMgr.setGroupFilter(ssoToken, entryDN, filter);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      unRegisterService(java.lang.String, java.lang.String, int,
     *      java.lang.String, int)
     */
    public void unRegisterService(String token, String entryDN, int objectType,
            String serviceName, int type) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            // TODO FIX LATER
            dMgr.unRegisterService(ssoToken, entryDN, objectType, serviceName,
                    null, type);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      updateUserAttribute(java.lang.String, java.util.Set,
     *      java.lang.String, boolean)
     */
    public void updateUserAttribute(String token, Set members,
            String staticGroupDN, boolean toAdd) throws AMRemoteException,
            SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dMgr.updateUserAttribute(ssoToken, members, staticGroupDN, toAdd);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      verifyAndDeleteObject(java.lang.String, java.lang.String)
     */
    public void verifyAndDeleteObject(String token, String dn)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            compl.verifyAndDeleteObject(ssoToken, dn);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }

    }

    private AMRemoteException convertException(AMException amex) {
        String ldapErrCodeString = null;
        if ((ldapErrCodeString = amex.getLDAPErrorCode()) == null) {
            return new AMRemoteException(amex.getMessage(),
                    amex.getErrorCode(), 0, (String[]) amex.getMessageArgs());
        } else {
            return new AMRemoteException(amex.getMessage(),
                    amex.getErrorCode(), Integer.parseInt(ldapErrCodeString),
                    (String[]) amex.getMessageArgs());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getAttributes(java.lang.String, java.lang.String, java.util.Set,
     *      boolean, boolean, int)
     */
    public Map getAttributes4(String token, String entryDN, Set attrNames,
            boolean ignoreCompliance, boolean byteValues, int profileType)
            throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dMgr.getAttributes(ssoToken, entryDN, attrNames,
                    ignoreCompliance, byteValues, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
    }

    // Notification methods
    public Set objectsChanged(int time) throws RemoteException {
        Set answer = new HashSet();
        // Get the cache index for times upto time+2
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // Add 1 minute to offset, the initial lookup
        calendar.add(Calendar.MINUTE, 1);
        for (int i = 0; i < time + 3; i++) {
            calendar.add(Calendar.MINUTE, -1);
            String cacheIndex = calendarToString(calendar);
            Set modDNs = (Set) cache.get(cacheIndex);
            if (modDNs != null)
                answer.addAll(modDNs);
        }
        if (debug.messageEnabled()) {
            debug.message("DirectoryManagerImpl:objectsChanged in time: "
                    + time + " minutes:\n" + answer);
        }
        return (answer);
    }

    public String registerNotificationURL(String url) throws RemoteException {
        String id = SMSUtils.getUniqueID();
        try {
            // Check URL is not the local server
            if (!url.startsWith(serverURL)) {
                notificationURLs.put(id, new URL(url));
                if (debug.messageEnabled()) {
                    debug.message("DirectoryManagerImpl:register for "
                            + "notification URL: " + url);
                }
            } else {
                // Cannot add this server for notifications
                if (debug.warningEnabled()) {
                    debug.warning("DirectoryManagerImpl:registerURL "
                            + "cannot add local server: " + url);
                }
                throw (new RemoteException("invalid-notification-URL"));
            }
        } catch (MalformedURLException e) {
            if (debug.warningEnabled()) {
                debug.warning("DirectoryManagerImpl:registerNotificationURL "
                        + " invalid URL: " + url, e);
            }
        }
        return (id);
    }

    public void deRegisterNotificationURL(String notificationID)
            throws RemoteException {
        notificationURLs.remove(notificationID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#assignService_idrepo(
     *      java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      com.sun.identity.sm.SchemaType, java.util.Map, java.lang.String,
     *      java.lang.String)
     */
    public void assignService_idrepo(String token, String type, String name,
            String serviceName, String stype, Map attrMap, String amOrgName,
            String amsdkDN) throws RemoteException, IdRepoException,
            SSOException {
        SSOToken ssoToken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        SchemaType schemaType = new SchemaType(stype);
        dMgr.assignService(ssoToken, idtype, name, serviceName, schemaType,
                attrMap, amOrgName, amsdkDN);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#create_idrepo(
     *      java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map, java.lang.String)
     */
    public String create_idrepo(String token, String type, String name,
            Map attrMap, String amOrgName) throws RemoteException,
            IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return IdUtils.getUniversalId(dMgr.create(stoken, idtype, name,
                attrMap, amOrgName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#delete_idrepo(
     *      java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void delete_idrepo(String token, String type, String name,
            String orgName, String amsdkDN) throws RemoteException,
            IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        dMgr.delete(stoken, idtype, name, orgName, amsdkDN);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getAssignedServices_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map, java.lang.String,
     *      java.lang.String)
     */
    public Set getAssignedServices_idrepo(String token, String type,
            String name, Map mapOfServiceNamesAndOCs, String amOrgName,
            String amsdkDN) throws RemoteException, IdRepoException,
            SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return dMgr.getAssignedServices(stoken, idtype, name,
                mapOfServiceNamesAndOCs, amOrgName, amsdkDN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getAttributes1_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Set, java.lang.String,
     *      java.lang.String)
     */
    public Map getAttributes1_idrepo(String token, String type, String name,
            Set attrNames, String amOrgName, String amsdkDN)
            throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        Map res = dMgr.getAttributes(stoken, idtype, name, attrNames,
                amOrgName, amsdkDN, true);
        if (res != null && res instanceof CaseInsensitiveHashMap) {
            Map res2 = new HashMap();
            Iterator it = res.keySet().iterator();
            while (it.hasNext()) {
                Object attr = it.next();
                res2.put(attr, res.get(attr));
            }
            res = res2;
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#getAttributes2_idrepo(
     *      java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Map getAttributes2_idrepo(String token, String type, String name,
            String amOrgName, String amsdkDN) throws RemoteException,
            IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        Map res = dMgr.getAttributes(stoken, idtype, name, amOrgName, amsdkDN);
        DirectoryManager.debug.error("Obtained map from server: "
                + res.getClass().getName());
        if (res != null && res instanceof CaseInsensitiveHashMap) {
            Map res2 = new HashMap();
            Iterator it = res.keySet().iterator();
            while (it.hasNext()) {
                Object attr = it.next();
                res2.put(attr, res.get(attr));
            }
            res = res2;
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#getMembers_idrepo(
     *      java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Set getMembers_idrepo(String token, String type, String name,
            String amOrgName, String membersType, String amsdkDN)
            throws RemoteException, IdRepoException, SSOException {
        Set results = new HashSet();
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        IdType mtype = IdUtils.getType(membersType);
        Set idSet = dMgr.getMembers(stoken, idtype, name, amOrgName, mtype,
                amsdkDN);
        if (idSet != null) {
            Iterator it = idSet.iterator();
            while (it.hasNext()) {
                AMIdentity id = (AMIdentity) it.next();
                results.add(IdUtils.getUniversalId(id));
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#getMemberships_idrepo(
     *      java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Set getMemberships_idrepo(String token, String type, String name,
            String membershipType, String amOrgName, String amsdkDN)
            throws RemoteException, IdRepoException, SSOException {
        Set results = new HashSet();
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        IdType mtype = IdUtils.getType(membershipType);
        Set idSet = dMgr.getMemberships(stoken, idtype, name, mtype, amOrgName,
                amsdkDN);
        if (idSet != null) {
            Iterator it = idSet.iterator();
            while (it.hasNext()) {
                AMIdentity id = (AMIdentity) it.next();
                results.add(IdUtils.getUniversalId(id));
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getServiceAttributes_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.util.Set,
     *      java.lang.String, java.lang.String)
     */
    public Map getServiceAttributes_idrepo(String token, String type,
            String name, String serviceName, Set attrNames, String amOrgName,
            String amsdkDN) throws RemoteException, IdRepoException,
            SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return dMgr.getServiceAttributes(stoken, idtype, name, serviceName,
                attrNames, amOrgName, amsdkDN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getSupportedOperations_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public Set getSupportedOperations_idrepo(String token, String type,
            String amOrgName) throws RemoteException, IdRepoException,
            SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        Set opSet = dMgr.getSupportedOperations(stoken, idtype, amOrgName);
        Set resSet = new HashSet();
        if (opSet != null) {
            Iterator it = opSet.iterator();
            while (it.hasNext()) {
                IdOperation thisop = (IdOperation) it.next();
                String opStr = thisop.getName();
                resSet.add(opStr);
            }
        }
        return resSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getSupportedTypes_idrepo(java.lang.String,
     *      java.lang.String)
     */
    public Set getSupportedTypes_idrepo(String token, String amOrgName)
            throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        Set typeSet = dMgr.getSupportedTypes(stoken, amOrgName);
        Set resTypes = new HashSet();
        if (typeSet != null) {
            Iterator it = typeSet.iterator();
            while (it.hasNext()) {
                IdType thistype = (IdType) it.next();
                String typeStr = thistype.getName();
                resTypes.add(typeStr);
            }
        }
        return resTypes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      isExists_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean isExists_idrepo(String token, String type, String name,
            String amOrgName) throws RemoteException, SSOException,
            IdRepoException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return dMgr.isExists(stoken, idtype, name, amOrgName);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      isActive_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public boolean isActive_idrepo(String token, String type, String name,
            String amOrgName, String amsdkDN) throws RemoteException,
            IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return dMgr.isActive(stoken, idtype, name, amOrgName, amsdkDN);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      modifyMemberShip_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Set, java.lang.String,
     *      int, java.lang.String)
     */
    public void modifyMemberShip_idrepo(String token, String type, String name,
            Set members, String membersType, int operation, String amOrgName)
            throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        IdType mtype = IdUtils.getType(membersType);
        dMgr.modifyMemberShip(stoken, idtype, name, members, mtype, operation,
                amOrgName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      modifyService_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      com.sun.identity.sm.SchemaType, java.util.Map, java.lang.String,
     *      java.lang.String)
     */
    public void modifyService_idrepo(String token, String type, String name,
            String serviceName, String stype, Map attrMap, String amOrgName,
            String amsdkDN) throws RemoteException, IdRepoException,
            SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        SchemaType schematype = new SchemaType(stype);
        dMgr.modifyService(stoken, idtype, name, serviceName, schematype,
                attrMap, amOrgName, amsdkDN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      removeAttributes_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Set, java.lang.String,
     *      java.lang.String)
     */
    public void removeAttributes_idrepo(String token, String type, String name,
            Set attrNames, String amOrgName, String amsdkDN)
            throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        dMgr.removeAttributes(stoken, idtype, name, attrNames, amOrgName,
                amsdkDN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      search1_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map, boolean, int,
     *      int, java.util.Set, java.lang.String)
     */
    public Map search1_idrepo(String token, String type, String pattern,
            Map avPairs, boolean recursive, int maxResults, int maxTime,
            Set returnAttrs, String amOrgName) throws RemoteException,
            IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        IdSearchResults idres = dMgr.search(stoken, idtype, pattern, avPairs,
                recursive, maxResults, maxTime, returnAttrs, amOrgName);
        return IdSearchResultsToMap(idres);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      search2_idrepo(java.lang.String,
     *      java.lang.String, java.lang.String, int, int, java.util.Set,
     *      boolean, int, java.util.Map, boolean, java.lang.String)
     */
    public Map search2_idrepo(String token, String type, String pattern,
            int maxTime, int maxResults, Set returnAttrs,
            boolean returnAllAttrs, int filterOp, Map avPairs,
            boolean recursive, String amOrgName) throws RemoteException,
            IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        IdSearchControl ctrl = new IdSearchControl();
        ctrl.setAllReturnAttributes(returnAllAttrs);
        ctrl.setMaxResults(maxResults);
        ctrl.setRecursive(recursive);
        ctrl.setReturnAttributes(returnAttrs);
        ctrl.setTimeOut(maxTime);
        IdSearchOpModifier modifier = (filterOp == IdRepo.OR_MOD) ? 
                IdSearchOpModifier.OR
                : IdSearchOpModifier.AND;
        ctrl.setSearchModifiers(modifier, avPairs);
        IdSearchResults idres = dMgr.search(stoken, idtype, pattern, ctrl,
                amOrgName);
        return IdSearchResultsToMap(idres);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#setAttributes_idrepo(
     *      java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map, boolean,
     *      java.lang.String, java.lang.String)
     */
    public void setAttributes_idrepo(String token, String type, String name,
            Map attributes, boolean isAdd, String amOrgName, String amsdkDN)
            throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        dMgr.setAttributes(stoken, idtype, name, attributes, isAdd, amOrgName,
                amsdkDN, true);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#unassignService_idrepo(
     *      java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.util.Map,
     *      java.lang.String, java.lang.String)
     */
    public void unassignService_idrepo(String token, String type, String name,
            String serviceName, Map attrMap, String amOrgName, String amsdkDN)
            throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        dMgr.unassignService(stoken, idtype, name, serviceName, attrMap,
                amOrgName, amsdkDN);

    }

    // Implementation for AMObjectListener
    public void objectChanged(String name, int type, Map configMap) {
        processEntryChanged(EventListener.OBJECT_CHANGED, name, type, null,
                true);
    }

    public void objectsChanged(String name, int type, Set attrNames,
            Map configMap) {
        processEntryChanged(EventListener.OBJECTS_CHANGED, name, type,
                attrNames, true);
    }

    public void permissionsChanged(String name, Map configMap) {
        processEntryChanged(EventListener.PERMISSIONS_CHANGED, name, 0, null,
                true);
    }

    public void allObjectsChanged() {
        processEntryChanged(EventListener.ALL_OBJECTS_CHANGED, "", 0, null,
                true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      deRegisterNotificationURL_idrepo(java.lang.String)
     */
    public void deRegisterNotificationURL_idrepo(String notificationID)
            throws RemoteException {
        notificationURLs.remove(notificationID);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      objectsChanged_idrepo(int)
     */
    public Set objectsChanged_idrepo(int time) throws RemoteException {
        Set answer = new HashSet();
        // Get the cache index for times upto time+2
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // Add 1 minute to offset, the initial lookup
        calendar.add(Calendar.MINUTE, 1);
        for (int i = 0; i < time + 3; i++) {
            calendar.add(Calendar.MINUTE, -1);
            String cacheIndex = calendarToString(calendar);
            Set modDNs = (Set) idrepoCache.get(cacheIndex);
            if (modDNs != null)
                answer.addAll(modDNs);
        }
        if (debug.messageEnabled()) {
            debug.message("DirectoryManagerImpl:objectsChanged in time: "
                    + time + " minutes:\n" + answer);
        }
        return (answer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      registerNotificationURL_idrepo(java.lang.String)
     */
    public String registerNotificationURL_idrepo(String url)
            throws RemoteException {
        // TODO Auto-generated method stub
        String id = SMSUtils.getUniqueID();
        try {
            // Check URL is not the local server
            if (!url.startsWith(serverURL)) {
                notificationURLs.put(id, new URL(url));
                if (debug.messageEnabled()) {
                    debug.message("DirectoryManagerImpl:register for "
                            + "notification URL: " + url);
                }
            } else {
                // Cannot add this server for notifications
                if (debug.warningEnabled()) {
                    debug.warning("DirectoryManagerImpl:registerURL "
                            + "cannot add local server: " + url);
                }
                throw (new RemoteException("invalid-notification-URL"));
            }
        } catch (MalformedURLException e) {
            if (debug.warningEnabled()) {
                debug.warning("DirectoryManagerImpl:registerNotificationURL "
                        + " invalid URL: " + url, e);
            }
        }
        return (id);
    }

    // Implementation to process entry changed events
    protected static synchronized void processEntryChanged(String method,
            String name, int type, Set attrNames, boolean amsdk) {
        HashMap thisCache = amsdk ? cache : idrepoCache;
        LinkedList cIndices = amsdk ? cacheIndices : idrepoCacheIndices;
        // Obtain the cache index
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String cacheIndex = calendarToString(calendar);
        Set modDNs = (Set) thisCache.get(cacheIndex);
        if (modDNs == null) {
            modDNs = new HashSet();
            thisCache.put(cacheIndex, modDNs);
            // Maintain cacheIndex
            cacheIndices.addFirst(cacheIndex);
            if (cIndices.size() > cacheSize) {
                cIndices.removeLast();
            }
        }

        // Construct the XML document for the event change
        StringBuffer sb = new StringBuffer(100);
        sb.append("<EventNotification><AttributeValuePair>").append(
                "<Attribute name=\"method\" /><Value>").append(method).append(
                "</Value></AttributeValuePair>").append(
                "<AttributeValuePair><Attribute name=\"entityName\" />")
                .append("<Value>").append(name).append(
                        "</Value></AttributeValuePair>");
        if (method.equalsIgnoreCase("objectChanged")
                || method.equalsIgnoreCase("objectsChanged")) {
            sb.append("<AttributeValuePair><Attribute name=\"eventType\" />")
                    .append("<Value>").append(type).append(
                            "</Value></AttributeValuePair>");
            if (method.equalsIgnoreCase("objectsChanged")) {
                sb.append("<AttributeValuePair><Attribute ").append(
                        "name=\"attrNames\"/>");
                for (Iterator items = attrNames.iterator(); items.hasNext();) {
                    String attr = (String) items.next();
                    sb.append("<Value>").append(attr).append("</Value>");
                }
                sb.append("</AttributeValuePair>");
            }
        }
        sb.append("</EventNotification>");
        // Add to cache
        modDNs.add(sb.toString());
        if (debug.messageEnabled()) {
            debug.message("DirectoryManagerImpl::processing entry change: "
                    + sb.toString());
        }

        // If notification URLs are present, send notifications
        NotificationSet ns = null;
        for (Iterator entries = notificationURLs.entrySet().iterator(); entries
                .hasNext();) {
            Map.Entry entry = (Map.Entry) entries.next();
            String id = (String) entry.getKey();
            URL url = (URL) entry.getValue();

            // Construct NotificationSet
            if (ns == null) {
                Notification notification = new Notification(sb.toString());
                ns = amsdk ? new NotificationSet(
                        com.iplanet.am.sdk.remote.DirectoryManager.SDK_SERVICE)
                        : new NotificationSet(
                                    com.iplanet.am.sdk.remote.DirectoryManager.
                                    IDREPO_SERVICE);
                ns.addNotification(notification);
            }
            try {
                PLLServer.send(url, ns);
                if (debug.messageEnabled()) {
                    debug.message("DirectorManagerImpl:sentNotification "
                            + "URL: " + url + " Data: " + ns);
                }
            } catch (SendNotificationException ne) {
                if (debug.warningEnabled()) {
                    debug.warning("DirectoryManagerImpl: failed sending "
                            + "notification to: " + url + "\nRemoving "
                            + "URL from notification list.", ne);
                }
                // Remove the URL from Notification List
                notificationURLs.remove(id);
            }
        }
    }

    private static String calendarToString(Calendar calendar) {
        // Get year, month, date, hour and minute
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        // Clear the calendar, set the params and get the string
        calendar.clear();
        calendar.set(year, month, date, hour, minute);
        return (serverURL + calendar.toString());
    }

    private Map IdSearchResultsToMap(IdSearchResults res) {
        // TODO ..check if the Map gets properly populated and sent.
        Map answer = new HashMap();
        Map answer1 = res.getResultAttributes();
        if (answer == null) {
            answer = new HashMap();
        }
        Map attrMaps = new HashMap();
        Set ids = res.getSearchResults();
        Set idStrings = new HashSet();
        if (ids != null) {
            Iterator it = ids.iterator();
            while (it.hasNext()) {
                AMIdentity id = (AMIdentity) it.next();
                String idStr = IdUtils.getUniversalId(id);
                idStrings.add(idStr);
                Map attrMap = (Map) answer1.get(id);
                if (attrMap != null) {
                    attrMaps.put(idStr, attrMap);
                }
            }
        }
        answer.put(com.iplanet.am.sdk.remote.DirectoryManager.AMSR_RESULTS,
                idStrings);
        answer.put(com.iplanet.am.sdk.remote.DirectoryManager.AMSR_CODE,
                new Integer(res.getErrorCode()));
        answer.put(com.iplanet.am.sdk.remote.DirectoryManager.AMSR_ATTRS,
                attrMaps);
        return (answer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMObjectListener#getConfigMap()
     */
    public Map getConfigMap() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMObjectListener#setConfigMap(java.util.Map)
     */
    public void setConfigMap(Map cmap) {
        // TODO Auto-generated method stub

    }
}

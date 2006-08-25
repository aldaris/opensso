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
 * $Id: DirectoryManagerImpl.java,v 1.5 2006-08-25 21:19:28 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk.remote;

import com.iplanet.am.sdk.AMDirectoryAccessFactory;
import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMObjectListener;
import com.iplanet.am.sdk.AMSearchResults;
import com.iplanet.am.sdk.common.IComplianceServices;
import com.iplanet.am.sdk.common.IDCTreeServices;
import com.iplanet.am.sdk.common.IDirectoryServices;
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
import com.sun.identity.idm.IdServicesFactory;
import com.sun.identity.idm.IdOperation;
import com.sun.identity.idm.IdRepo;
import com.sun.identity.idm.IdRepoListener;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdServices;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.sm.SMSUtils;
import com.sun.identity.sm.SchemaType;
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

public class DirectoryManagerImpl implements DirectoryManagerIF,
    AMObjectListener {
    
    protected static Debug debug = Debug.getInstance("amProfile_Server");
    
    protected static SSOTokenManager tm;
    
    protected static boolean initialized;
    
    // Handle to all the new DirectoryServices implementations.
    protected static IDirectoryServices dsServices;
    
    protected static IDCTreeServices dcTreeServices;
    
    protected static IComplianceServices complianceServices;
    
    // IdRepo Services Handlers
    protected static IdServices idServices;
    
    // Cache of modifications for last 30 minutes & notification URLs
    static int cacheSize = 30;
    
    static LinkedList cacheIndices = new LinkedList();
    
    static LinkedList idrepoCacheIndices = new LinkedList();
    
    static HashMap cache = new HashMap(cacheSize);
    
    static HashMap idrepoCache = new HashMap(cacheSize);
    
    static HashMap notificationURLs = new HashMap();
    
    static String serverURL;
    
    static {
        
        dsServices = AMDirectoryAccessFactory.getDirectoryServices();
        dcTreeServices = AMDirectoryAccessFactory.getDCTreeServices();
        complianceServices = AMDirectoryAccessFactory.getComplianceServices();
        
        // Get the IdRepo providers
        idServices = IdServicesFactory.getDataStoreServices();
    }
    
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
            dsServices.addListener((SSOToken) AccessController
                .doPrivileged(AdminTokenAction.getInstance()), this, null);
            IdRepoListener.addRemoteListener(new IdRepoEventListener());
            
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
    
    public String createAMTemplate(String token, String entryDN,
        int objectType, String serviceName, Map attributes, int priority)
        throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.createAMTemplate(ssoToken, entryDN, objectType,
                serviceName, attributes, priority);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public void createEntry(String token, String entryName, int objectType,
        String parentDN, Map attributes) throws AMRemoteException,
        SSOException, RemoteException {
        
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dsServices.createEntry(ssoToken, entryName, objectType, parentDN,
                attributes);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public boolean doesEntryExists(String token, String entryDN)
    throws AMRemoteException, SSOException, RemoteException {
        SSOToken ssoToken = tm.createSSOToken(token);
        return dsServices.doesEntryExists(ssoToken, entryDN);
    }
    
    public String getAMTemplateDN(String token, String entryDN, int objectType,
        String serviceName, int type) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getAMTemplateDN(ssoToken, entryDN, objectType,
                serviceName, type);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public Map getAttributes3(String token, String entryDN,
        boolean ignoreCompliance, boolean byteValues, int profileType)
        throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getAttributes(ssoToken, entryDN,
                ignoreCompliance, byteValues, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
    }
    
    public Map getAttributes1(String token, String entryDN, int profileType)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getAttributes(ssoToken, entryDN, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public Map getAttributes2(String token, String entryDN, Set attrNames,
        int profileType) throws AMRemoteException, SSOException,
        RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getAttributes(ssoToken, entryDN, attrNames,
                profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public Map getAttributesByteValues1(String token, String entryDN,
        int profileType) throws AMRemoteException, SSOException,
        RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getAttributesByteValues(ssoToken, entryDN,
                profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public Map getAttributesByteValues2(String token, String entryDN,
        Set attrNames, int profileType) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getAttributesByteValues(ssoToken, entryDN,
                attrNames, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    
    public Set getAttributesForSchema(String objectclass)
    throws RemoteException {
        return dsServices.getAttributesForSchema(objectclass);
    }
    
    
    public String getCreationTemplateName(int objectType)
    throws RemoteException {
        return dsServices.getCreationTemplateName(objectType);
    }
    
    public Map getDCTreeAttributes(String token, String entryDN, Set attrNames,
        boolean byteValues, int objectType) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getDCTreeAttributes(ssoToken, entryDN, attrNames,
                byteValues, objectType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public String getDeletedObjectFilter(int objecttype)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            return complianceServices.getDeletedObjectFilter(objecttype);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public Map getExternalAttributes(String token, String entryDN,
        Set attrNames, int profileType) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getExternalAttributes(ssoToken, entryDN,
                attrNames, profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public LinkedList getGroupFilterAndScope(String token, String entryDN,
        int profileType) throws AMRemoteException, SSOException,
        RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            String[] array = dsServices.getGroupFilterAndScope(ssoToken,
                entryDN, profileType);
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
    
    public Set getMembers(String token, String entryDN, int objectType)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getMembers(ssoToken, entryDN, objectType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public String getNamingAttr(int objectType, String orgDN)
    throws RemoteException {
        return dsServices.getNamingAttribute(objectType, orgDN);
    }
    
    public String getObjectClassFromDS(int objectType) throws RemoteException {
        return dsServices.getObjectClass(objectType);
    }
    
    public int getObjectType(String token, String dn) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getObjectType(ssoToken, dn);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public String getOrganizationDN(String token, String entryDN)
    throws AMRemoteException, RemoteException, SSOException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getOrganizationDN(ssoToken, entryDN);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public String verifyAndGetOrgDN(String token, String entryDN,
        String childDN) throws AMRemoteException, RemoteException,
        SSOException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.verifyAndGetOrgDN(ssoToken, entryDN, childDN);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public String getOrgDNFromDomain(String token, String domain)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dcTreeServices.getOrganizationDN(ssoToken, domain);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public String getOrgSearchFilter(String entryDN) throws RemoteException {
        return dsServices.getOrgSearchFilter(entryDN);
    }
    
    public Set getRegisteredServiceNames(String token, String entryDN)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            // SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getRegisteredServiceNames(null, entryDN);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public String getSearchFilterFromTemplate(int objectType, String orgDN,
        String searchTemplateName) throws RemoteException {
        return dsServices.getSearchFilterFromTemplate(objectType, orgDN,
            searchTemplateName);
    }
    
    public Set getTopLevelContainers(String token) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getTopLevelContainers(ssoToken);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public boolean isAncestorOrgDeleted(String token, String dn,
        int profileType) throws AMRemoteException, SSOException,
        RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return complianceServices.isAncestorOrgDeleted(ssoToken, dn,
                profileType);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public void modifyMemberShip(String token, Set members, String target,
        int type, int operation) throws AMRemoteException, SSOException,
        RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dsServices.modifyMemberShip(ssoToken, members, target, type,
                operation);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public void registerService(String token, String orgDN, String serviceName)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dsServices.registerService(ssoToken, orgDN, serviceName);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public void removeAdminRole(String token, String dn, boolean recursive)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dsServices.removeAdminRole(ssoToken, dn, recursive);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public void removeEntry(String token, String entryDN, int objectType,
        boolean recursive, boolean softDelete) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dsServices.removeEntry(ssoToken, entryDN, objectType, recursive,
                softDelete);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public String renameEntry(String token, int objectType, String entryDN,
        String newName, boolean deleteOldName) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.renameEntry(ssoToken, objectType, entryDN,
                newName, deleteOldName);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public Set search1(String token, String entryDN, String searchFilter,
        int searchScope) throws AMRemoteException, SSOException,
        RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.search(ssoToken, entryDN, searchFilter,
                searchScope);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
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
            AMSearchResults results = dsServices.search(tm
                .createSSOToken(token), entryDN, searchFilter, sc,
                attrNames);
            // Convert results to Map
            Map answer = results.getResultAttributes();
            if (answer == null) {
                answer = new HashMap();
            }
            answer.put(com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_COUNT,
                Integer.toString(results.getTotalResultCount()));
            answer.put(
                com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_RESULTS,
                results.getSearchResults());
            answer.put(com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_CODE,
                Integer.toString(results.getErrorCode()));
            return (answer);
        } catch (AMException amex) {
            debug.error("DMI::search(with SearchControl):  " + amex);
            throw convertException(amex);
        }
    }
    
    public Map search3(String token, String entryDN, String searchFilter,
        List sortKeys, int startIndex, int beforeCount, int afterCount,
        String jumpTo, int timeOut, int maxResults, int scope,
        boolean allAttributes, Set attrNamesSet) throws AMRemoteException,
        SSOException, RemoteException {
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
        
        String[] attrNames = new String[attrNamesSet.size()];
        attrNames = (String[]) attrNamesSet.toArray(attrNames);
        
        // Perform the search
        try {
            AMSearchResults results = dsServices.search(tm
                .createSSOToken(token), entryDN, searchFilter, sc,
                attrNames);
            // Convert results to Map
            Map answer = results.getResultAttributes();
            if (answer == null) {
                answer = new HashMap();
            }
            answer.put(com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_COUNT,
                Integer.toString(results.getTotalResultCount()));
            answer.put(
                com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_RESULTS,
                results.getSearchResults());
            answer.put(com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_CODE,
                Integer.toString(results.getErrorCode()));
            return (answer);
        } catch (AMException amex) {
            debug.error("DMI::search(with SearchControl):  " + amex);
            throw convertException(amex);
        }
    }
    
    public void setAttributes(String token, String entryDN, int objectType,
        Map stringAttributes, Map byteAttributes, boolean isAdd)
        throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dsServices.setAttributes(ssoToken, entryDN, objectType,
                stringAttributes, byteAttributes, isAdd);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
    }
    
    public void setGroupFilter(String token, String entryDN, String filter)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dsServices.setGroupFilter(ssoToken, entryDN, filter);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public void unRegisterService(String token, String entryDN, int objectType,
        String serviceName, int type) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            // TODO FIX LATER
            dsServices.unRegisterService(ssoToken, entryDN, objectType,
                serviceName, type);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public void updateUserAttribute(String token, Set members,
        String staticGroupDN, boolean toAdd) throws AMRemoteException,
        SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            dsServices.updateUserAttribute(ssoToken, members, staticGroupDN,
                toAdd);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    public void verifyAndDeleteObject(String token, String dn)
    throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            complianceServices.verifyAndDeleteObject(ssoToken, dn);
        } catch (AMException amex) {
            debug.error("Caught Exception:  " + amex);
            throw convertException(amex);
        }
        
    }
    
    private AMRemoteException convertException(AMException amex) {
        String ldapErrCodeString = null;
        if ((ldapErrCodeString = amex.getLDAPErrorCode()) == null) {
            
            return new AMRemoteException(amex.getMessage(),
                amex.getErrorCode(), 0, copyObjectArrayToStringArray(amex
                .getMessageArgs()));
        } else {
            return new AMRemoteException(amex.getMessage(),
                amex.getErrorCode(), Integer.parseInt(ldapErrCodeString),
                copyObjectArrayToStringArray(amex.getMessageArgs()));
        }
    }
    
    private String[] copyObjectArrayToStringArray(Object[] objArray) {
        if ((objArray != null) && (objArray.length != 0)) {
            int count = objArray.length;
            String[] strArray = new String[count];
            for (int i = 0; i < count; i++) {
                strArray[i] = (String) objArray[i];
            }
            return strArray;
        }
        return null;
    }
    
    public Map getAttributes4(String token, String entryDN, Set attrNames,
        boolean ignoreCompliance, boolean byteValues, int profileType)
        throws AMRemoteException, SSOException, RemoteException {
        try {
            SSOToken ssoToken = tm.createSSOToken(token);
            return dsServices.getAttributes(ssoToken, entryDN, attrNames,
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
    
    public void assignService_idrepo(String token, String type, String name,
        String serviceName, String stype, Map attrMap, String amOrgName,
        String amsdkDN) throws RemoteException, IdRepoException,
        SSOException {
        SSOToken ssoToken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        SchemaType schemaType = new SchemaType(stype);
        idServices.assignService(ssoToken, idtype, name, serviceName,
            schemaType, attrMap, amOrgName, amsdkDN);
        
    }
    
    public String create_idrepo(String token, String type, String name,
        Map attrMap, String amOrgName) throws RemoteException,
        IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return IdUtils.getUniversalId(idServices.create(stoken, idtype, name,
            attrMap, amOrgName));
    }
    
    public void delete_idrepo(String token, String type, String name,
        String orgName, String amsdkDN) throws RemoteException,
        IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        idServices.delete(stoken, idtype, name, orgName, amsdkDN);
        
    }
    
    public Set getAssignedServices_idrepo(String token, String type,
        String name, Map mapOfServiceNamesAndOCs, String amOrgName,
        String amsdkDN) throws RemoteException, IdRepoException,
        SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return idServices.getAssignedServices(stoken, idtype, name,
            mapOfServiceNamesAndOCs, amOrgName, amsdkDN);
    }
    
    public Map getAttributes1_idrepo(String token, String type, String name,
        Set attrNames, String amOrgName, String amsdkDN)
        throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        Map res = idServices.getAttributes(stoken, idtype, name, attrNames,
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
    
    public Map getAttributes2_idrepo(String token, String type, String name,
        String amOrgName, String amsdkDN) throws RemoteException,
        IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        Map res = idServices.getAttributes(stoken, idtype, name, amOrgName,
            amsdkDN);
        
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
    
    public Set getMembers_idrepo(String token, String type, String name,
        String amOrgName, String membersType, String amsdkDN)
        throws RemoteException, IdRepoException, SSOException {
        Set results = new HashSet();
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        IdType mtype = IdUtils.getType(membersType);
        Set idSet = idServices.getMembers(stoken, idtype, name, amOrgName,
            mtype, amsdkDN);
        if (idSet != null) {
            Iterator it = idSet.iterator();
            while (it.hasNext()) {
                AMIdentity id = (AMIdentity) it.next();
                results.add(IdUtils.getUniversalId(id));
            }
        }
        return results;
    }
    
    public Set getMemberships_idrepo(String token, String type, String name,
        String membershipType, String amOrgName, String amsdkDN)
        throws RemoteException, IdRepoException, SSOException {
        Set results = new HashSet();
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        IdType mtype = IdUtils.getType(membershipType);
        Set idSet = idServices.getMemberships(stoken, idtype, name, mtype,
            amOrgName, amsdkDN);
        if (idSet != null) {
            Iterator it = idSet.iterator();
            while (it.hasNext()) {
                AMIdentity id = (AMIdentity) it.next();
                results.add(IdUtils.getUniversalId(id));
            }
        }
        return results;
    }
    
    public Map getServiceAttributes_idrepo(String token, String type,
        String name, String serviceName, Set attrNames, String amOrgName,
        String amsdkDN) throws RemoteException, IdRepoException,
        SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return idServices.getServiceAttributes(stoken, idtype, name,
            serviceName, attrNames, amOrgName, amsdkDN);
    }
    
    public Set getSupportedOperations_idrepo(String token, String type,
        String amOrgName) throws RemoteException, IdRepoException,
        SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        Set opSet = idServices
            .getSupportedOperations(stoken, idtype, amOrgName);
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
    
    public Set getSupportedTypes_idrepo(String token, String amOrgName)
    throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        Set typeSet = idServices.getSupportedTypes(stoken, amOrgName);
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
    
    public boolean isExists_idrepo(String token, String type, String name,
        String amOrgName) throws RemoteException, SSOException,
        IdRepoException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return idServices.isExists(stoken, idtype, name, amOrgName);
        
    }
    
    public boolean isActive_idrepo(String token, String type, String name,
        String amOrgName, String amsdkDN) throws RemoteException,
        IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        return idServices.isActive(stoken, idtype, name, amOrgName, amsdkDN);
        
    }
    
    public void modifyMemberShip_idrepo(String token, String type, String name,
        Set members, String membersType, int operation, String amOrgName)
        throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        IdType mtype = IdUtils.getType(membersType);
        idServices.modifyMemberShip(stoken, idtype, name, members, mtype,
            operation, amOrgName);
    }
    
    public void modifyService_idrepo(String token, String type, String name,
        String serviceName, String stype, Map attrMap, String amOrgName,
        String amsdkDN) throws RemoteException, IdRepoException,
        SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        SchemaType schematype = new SchemaType(stype);
        idServices.modifyService(stoken, idtype, name, serviceName, schematype,
            attrMap, amOrgName, amsdkDN);
    }
    
    public void removeAttributes_idrepo(String token, String type, String name,
        Set attrNames, String amOrgName, String amsdkDN)
        throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        idServices.removeAttributes(stoken, idtype, name, attrNames, amOrgName,
            amsdkDN);
    }
    
    public Map search1_idrepo(String token, String type, String pattern,
        Map avPairs, boolean recursive, int maxResults, int maxTime,
        Set returnAttrs, String amOrgName) throws RemoteException,
        IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
            IdType idtype = IdUtils.getType(type);
        return search2_idrepo(token, type, pattern, maxTime, maxResults,
            returnAttrs, (returnAttrs == null), 0, avPairs, recursive,
            amOrgName);
    }
    
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
        ctrl.setReturnAttributes(returnAttrs);
        ctrl.setTimeOut(maxTime);
        IdSearchOpModifier modifier = (filterOp == IdRepo.OR_MOD) ?
            IdSearchOpModifier.OR : IdSearchOpModifier.AND;
        ctrl.setSearchModifiers(modifier, avPairs);
        IdSearchResults idres = idServices.search(stoken, idtype, pattern,
            ctrl, amOrgName);
        return IdSearchResultsToMap(idres);
    }
    
    public void setAttributes_idrepo(String token, String type, String name,
        Map attributes, boolean isAdd, String amOrgName, String amsdkDN)
        throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        idServices.setAttributes(stoken, idtype, name, attributes, isAdd,
            amOrgName, amsdkDN, true);
    }
    
    public void setAttributes2_idrepo(String token, String type, String name,
        Map attributes, boolean isAdd, String amOrgName, String amsdkDN,
        boolean isString) throws RemoteException, IdRepoException,
        SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        idServices.setAttributes(stoken, idtype, name, attributes, isAdd,
            amOrgName, amsdkDN, isString);
    }
    
    public void unassignService_idrepo(String token, String type, String name,
        String serviceName, Map attrMap, String amOrgName, String amsdkDN)
        throws RemoteException, IdRepoException, SSOException {
        SSOToken stoken = tm.createSSOToken(token);
        IdType idtype = IdUtils.getType(type);
        idServices.unassignService(stoken, idtype, name, serviceName, attrMap,
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
    
    public void deRegisterNotificationURL_idrepo(String notificationID)
    throws RemoteException {
        notificationURLs.remove(notificationID);
        
    }
    
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
        Map notifications = new HashMap(notificationURLs); // Make a copy
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
                    com.iplanet.am.sdk.remote.RemoteServicesImpl
                    .SDK_SERVICE)
                    : new NotificationSet(
                    com.iplanet.am.sdk.remote.RemoteServicesImpl
                    .IDREPO_SERVICE);
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
        Map attrMaps = new HashMap();
        Set idStrings = new HashSet();

        Map answer1 = res.getResultAttributes();
        Set ids = res.getSearchResults();
        if (ids != null) {
            Iterator it = ids.iterator();
            while (it.hasNext()) {
                AMIdentity id = (AMIdentity) it.next();
                String idStr = IdUtils.getUniversalId(id);
                idStrings.add(idStr);
                Map attrMap = (Map) answer1.get(id);
                if (attrMap != null) {
                    Map cattrMap = new HashMap();
                    for (Iterator items = attrMap.keySet().iterator();
                        items.hasNext();) {
                        Object item = items.next();
                        cattrMap.put(item.toString(), attrMap.get(item));
                    }
                    attrMaps.put(idStr, attrMap);
                }
            }
        }
        answer.put(com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_RESULTS,
            idStrings);
        answer.put(com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_CODE,
            new Integer(res.getErrorCode()));
        answer.put(com.iplanet.am.sdk.remote.RemoteServicesImpl.AMSR_ATTRS,
            attrMaps);
        return (answer);
    }
    
    public Map getConfigMap() {
        
        return null;
    }
    
    public void setConfigMap(Map cmap) {
        
    }
}

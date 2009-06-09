/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: DataStore.java,v 1.19 2009-06-09 09:44:27 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.common.configuration.ServerConfiguration;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.EntitlementThreadPool;
import com.sun.identity.entitlement.Evaluate;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.entitlement.ReferralPrivilege;
import com.sun.identity.entitlement.ResourceSaveIndexes;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import com.sun.identity.entitlement.SubjectAttributesManager;
import com.sun.identity.entitlement.interfaces.IThreadPool;
import com.sun.identity.entitlement.util.NetworkMonitor;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.BufferedIterator;
import com.sun.identity.shared.ldap.LDAPDN;
import com.sun.identity.shared.ldap.util.DN;
import com.sun.identity.shared.stats.Stats;
import com.sun.identity.sm.DNMapper;
import com.sun.identity.sm.SMSDataEntry;
import com.sun.identity.sm.SMSEntry;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class *talks* to SMS to get the configuration information.
 */
public class DataStore {
    public static final String POLICY_STORE = "default";
    public static final String REFERRAL_STORE = "referrals";

    private static final String SERVICE_NAME = "PolicyIndex";
    private static final String INDEX_COUNT = "indexCount";
    private static final String REFERRAL_INDEX_COUNT = "referralIndexCount";
    private static final String REALM_DN_TEMPLATE =
         "ou={0},ou=default,ou=OrganizationConfig,ou=1.0,ou=" + SERVICE_NAME +
         ",ou=services,{1}";
    private static final String SUBJECT_INDEX_KEY = "subjectindex";
    private static final String HOST_INDEX_KEY = "hostindex";
    private static final String PATH_INDEX_KEY = "pathindex";
    private static final String PATH_PARENT_INDEX_KEY = "pathparentindex";
    private static final String SERIALIZABLE_INDEX_KEY = "serializable";
    private static final String SUBJECT_FILTER_TEMPLATE =
        "(" + SMSEntry.ATTR_XML_KEYVAL + "=" + SUBJECT_INDEX_KEY + "={0})";
    private static final String HOST_FILTER_TEMPLATE =
        "(" + SMSEntry.ATTR_XML_KEYVAL + "=" + HOST_INDEX_KEY + "={0})";
    private static final String PATH_FILTER_TEMPLATE =
        "(" + SMSEntry.ATTR_XML_KEYVAL + "=" + PATH_INDEX_KEY + "={0})";
    private static final String PATH_PARENT_FILTER_TEMPLATE =
        "(" + SMSEntry.ATTR_XML_KEYVAL + "=" + PATH_PARENT_INDEX_KEY + "={0})";

    private static final NetworkMonitor DB_MONITOR_PRIVILEGE =
        NetworkMonitor.getInstance("dbLookupPrivileges");
    private static final NetworkMonitor DB_MONITOR_REFERRAL =
        NetworkMonitor.getInstance("dbLookupReferrals");
    private static IThreadPool threadPool = new EntitlementThreadPool();
    private static final String currentServerInstance =
        SystemProperties.getServerInstanceName();
    
    // count of number of policies per realm
    static HashMap<String, Integer> policiesPerRealm =
        new HashMap<String, Integer>();
    static HashMap<String, Integer> referralsPerRealm =
        new HashMap<String, Integer>();
    
    static {
        // Initialize statistics collection
        Stats stats = Stats.getInstance("Entitlements");
        EntitlementsStats es = new EntitlementsStats(stats);
        stats.addStatsListener(es);
    }

    
    /**
     * Returns distingished name of a privilege.
     *
     * @param name Privilege name.
     * @param realm Realm name.
     * @param indexName Index name.
     * @return the distingished name of a privilege.
     */
    public static String getPrivilegeDistinguishedName(
        String name,
        String realm,
        String indexName) {
        return "ou=" + name + "," + getSearchBaseDN(realm, indexName);
    }

    /**
     * Returns the base search DN.
     *
     * @param realm Realm name.
     * @param indexName Index name.
     * @return
     */
    public static String getSearchBaseDN(String realm, String indexName) {
        if (indexName == null) {
            indexName = POLICY_STORE;
        }
        Object[] args = {indexName, DNMapper.orgNameToDN(realm)};
        return MessageFormat.format(REALM_DN_TEMPLATE, args);
    }

    private String createDefaultSubConfig(
        SSOToken adminToken,
        String realm,
        String indexName)
        throws SMSException, SSOException {
        if (indexName == null) {
            indexName = POLICY_STORE;
        }
        ServiceConfig orgConf = getOrgConfig(adminToken, realm);

        Set<String> subConfigNames = orgConf.getSubConfigNames();
        if (!subConfigNames.contains(indexName)) {
            orgConf.addSubConfig(indexName, "indexes", 0,
                Collections.EMPTY_MAP);
        }
        ServiceConfig defSubConfig = orgConf.getSubConfig(indexName);
        return defSubConfig.getDN();
    }

    private ServiceConfig getOrgConfig(SSOToken adminToken, String realm)
        throws SMSException, SSOException {
        ServiceConfigManager mgr = new ServiceConfigManager(
            SERVICE_NAME, adminToken);
        ServiceConfig orgConf = mgr.getOrganizationConfig(realm, null);
        if (orgConf == null) {
            mgr.createOrganizationConfig(realm, null);
        }
        return orgConf;
    }

    private void updateIndexCount(
        SSOToken adminToken,
        String realm,
        int num,
        boolean referral) {
        try {
            String key = (referral) ? REFERRAL_INDEX_COUNT : INDEX_COUNT;

            ServiceConfig orgConf = getOrgConfig(adminToken, realm);
            Map<String, Set<String>> map = orgConf.getAttributes();
            Set<String> set = map.get(key);
            int count = num;

            if ((set != null) && !set.isEmpty()) {
                String strCount = (String) set.iterator().next();
                count += Integer.parseInt(strCount);
                set.clear();
            } else {
                set = new HashSet<String>();
                map.put(key, set);
            }

            set.add(Integer.toString(count));
            orgConf.setAttributes(map);

            if (referral) {
                referralsPerRealm.put(toRealm(realm), count);
            } else {
                policiesPerRealm.put(toRealm(realm), count);
            }
        } catch (NumberFormatException ex) {
            PrivilegeManager.debug.error("DataStore.updateIndexCount", ex);
        } catch (SMSException ex) {
            PrivilegeManager.debug.error("DataStore.updateIndexCount", ex);
        } catch (SSOException ex) {
            PrivilegeManager.debug.error("DataStore.updateIndexCount", ex);
        }
    }

    private static String toRealm(String orgName) {
        if (DN.isDN(orgName)) {
            String orgdn = new DN(orgName).toRFCString();
            String orgdnlc = orgdn.toLowerCase();

            // Check if orgdn is a hidden internal realm, if so return
            if (orgdnlc.startsWith(SMSEntry.SUN_INTERNAL_REALM_PREFIX)) {
                return orgName;
            }

            return DNMapper.orgNameToRealmName(orgName);
        } else {
            return orgName;
        }
    }

    private int getIndexCount(Subject adminSubject, String realm,
        boolean referral) {
        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);
        int count = 0;
        if (adminToken != null) {
            try {
                ServiceConfigManager mgr = new ServiceConfigManager(
                    SERVICE_NAME, adminToken);
                ServiceConfig orgConf = mgr.getOrganizationConfig(realm, null);
                if (orgConf != null) {
                    Map<String, Set<String>> map = orgConf.getAttributes();
                    Set<String> set = (referral) ?
                        map.get(REFERRAL_INDEX_COUNT) : map.get(INDEX_COUNT);
                    if ((set != null) && !set.isEmpty()) {
                        String strCount = (String) set.iterator().next();
                        count = Integer.parseInt(strCount);
                    }
                }
            } catch (NumberFormatException ex) {
                PrivilegeManager.debug.error("DataStore.getIndexCount", ex);
            } catch (SMSException ex) {
                PrivilegeManager.debug.error("DataStore.getIndexCount", ex);
            } catch (SSOException ex) {
                PrivilegeManager.debug.error("DataStore.getIndexCount", ex);
            }
        }
        return count;
    }

    int getNumberOfPolicies(Subject subject, String realm) {
        int totalPolicies = 0;
        Integer tp = policiesPerRealm.get(realm);
        if (tp == null) {
            totalPolicies = getIndexCount(subject, realm, false);
            policiesPerRealm.put(realm, totalPolicies);
        } else {
            totalPolicies = tp.intValue();
        }

        return (totalPolicies);
    }

    int getNumberOfReferrals(Subject subject, String realm) {
        int referralCnt = 0;
        Integer tp = referralsPerRealm.get(realm);
        if (tp == null) {
            referralCnt = getIndexCount(subject, realm, true);
            referralsPerRealm.put(realm, referralCnt);
        } else {
            referralCnt = tp.intValue();
        }

        return (referralCnt);
    }

    /**
     * Adds a privilege.
     *
     * @param adminSubject Admin Subject who has the rights to write to
     *        datastore.
     * @param realm Realm name.
     * @param p Privilege object.
     * @return the DN of added privilege.
     * @throws com.sun.identity.entitlement.EntitlementException if privilege
     * cannot be added.
     */
    public String add(Subject adminSubject, String realm, Privilege p)
        throws EntitlementException {

        ResourceSaveIndexes indexes =
            p.getEntitlement().getResourceSaveIndexes(adminSubject, realm);
        Set<String> subjectIndexes =
            SubjectAttributesManager.getSubjectSearchIndexes(p);

        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);
        String dn = null;
        try {
            createDefaultSubConfig(adminToken, realm, null);
            dn = getPrivilegeDistinguishedName(p.getName(), realm, null);

            SMSEntry s = new SMSEntry(adminToken, dn);
            Map<String, Set<String>> map = new HashMap<String, Set<String>>();

            Set<String> searchable = new HashSet<String>();
            map.put(SMSEntry.ATTR_XML_KEYVAL, searchable);

            for (String i : indexes.getHostIndexes()) {
                searchable.add(HOST_INDEX_KEY + "=" + i);
            }
            for (String i : indexes.getPathIndexes()) {
                searchable.add(PATH_INDEX_KEY + "=" + i);
            }
            for (String i : indexes.getParentPathIndexes()) {
                searchable.add(PATH_PARENT_INDEX_KEY + "=" + i);
            }
            for (String i : subjectIndexes) {
                searchable.add(SUBJECT_INDEX_KEY + "=" + i);
            }

            Set<String> set = new HashSet<String>(2);
            map.put(SMSEntry.ATTR_KEYVAL, set);
            set.add(SERIALIZABLE_INDEX_KEY + "=" + p.toJSONObject().toString());

            Set<String> setObjectClass = new HashSet<String>(4);
            map.put(SMSEntry.ATTR_OBJECTCLASS, setObjectClass);
            setObjectClass.add(SMSEntry.OC_TOP);
            setObjectClass.add(SMSEntry.OC_SERVICE_COMP);

            Set<String> info = new HashSet<String>(8);

            String privilegeName = p.getName();
            if (privilegeName != null) {
                info.add(Privilege.NAME_ATTRIBUTE + "=" + privilegeName);
            }

            String privilegeDesc = p.getDescription();
            if (privilegeDesc != null) {
                info.add(Privilege.DESCRIPTION_ATTRIBUTE + "=" + privilegeDesc);
            }

            String createdBy = p.getCreatedBy();
            if (createdBy != null) {
                info.add(Privilege.CREATED_BY_ATTRIBUTE + "=" + createdBy);
            }

            String lastModifiedBy = p.getLastModifiedBy();
            if (lastModifiedBy != null) {
                info.add(Privilege.LAST_MODIFIED_BY_ATTRIBUTE + "=" +
                    lastModifiedBy);
            }

            long creationDate = p.getCreationDate();
            if (creationDate > 0) {
                String data = Long.toString(creationDate) + "=" +
                    Privilege.CREATION_DATE_ATTRIBUTE;
                info.add(data);
                info.add("|" + data);
            }

            long lastModifiedDate = p.getLastModifiedDate();
            if (lastModifiedDate > 0) {
                String data = Long.toString(lastModifiedDate) + "=" +
                    Privilege.LAST_MODIFIED_DATE_ATTRIBUTE;
                info.add(data);
                info.add("|" + data);
            }
            map.put("ou", info);

            s.setAttributes(map);
            s.save();
            updateIndexCount(adminToken, realm, 1, false);
        } catch (JSONException e) {
            throw new EntitlementException(210, e);
        } catch (SSOException e) {
            throw new EntitlementException(210, e);
        } catch (SMSException e) {
            throw new EntitlementException(210, e);
        }
        return dn;
    }
    /**
     * Adds a referral.
     *
     * @param adminSubject Admin Subject who has the rights to write to
     *        datastore.
     * @param realm Realm name.
     * @param referral Referral Privilege object.
     * @return the DN of added privilege.
     * @throws EntitlementException if privilege cannot be added.
     */
    public String addReferral(
        Subject adminSubject,
        String realm,
        ReferralPrivilege referral
    ) throws EntitlementException {
        ResourceSaveIndexes indexes = referral.getResourceSaveIndexes(
            adminSubject, realm);
        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);
        String dn = null;
        try {
            createDefaultSubConfig(adminToken, realm, REFERRAL_STORE);
            dn = getPrivilegeDistinguishedName(referral.getName(), realm,
                REFERRAL_STORE);

            SMSEntry s = new SMSEntry(adminToken, dn);
            Map<String, Set<String>> map = new HashMap<String, Set<String>>();

            Set<String> searchable = new HashSet<String>();
            map.put(SMSEntry.ATTR_XML_KEYVAL, searchable);

            for (String i : indexes.getHostIndexes()) {
                searchable.add(HOST_INDEX_KEY + "=" + i);
            }
            for (String i : indexes.getPathIndexes()) {
                searchable.add(PATH_INDEX_KEY + "=" + i);
            }
            for (String i : indexes.getParentPathIndexes()) {
                searchable.add(PATH_PARENT_INDEX_KEY + "=" + i);
            }

            Set<String> set = new HashSet<String>(2);
            map.put(SMSEntry.ATTR_KEYVAL, set);
            set.add(SERIALIZABLE_INDEX_KEY + "=" + referral.toJSON());

            Set<String> setObjectClass = new HashSet<String>(4);
            map.put(SMSEntry.ATTR_OBJECTCLASS, setObjectClass);
            setObjectClass.add(SMSEntry.OC_TOP);
            setObjectClass.add(SMSEntry.OC_SERVICE_COMP);

            Set<String> info = new HashSet<String>(8);

            String privilegeName = referral.getName();
            if (privilegeName != null) {
                info.add(Privilege.NAME_ATTRIBUTE + "=" + privilegeName);
            }

            String privilegeDesc = referral.getDescription();
            if (privilegeDesc != null) {
                info.add(Privilege.DESCRIPTION_ATTRIBUTE + "=" + privilegeDesc);
            }

            String createdBy = referral.getCreatedBy();
            if (createdBy != null) {
                info.add(Privilege.CREATED_BY_ATTRIBUTE + "=" + createdBy);
            }

            String lastModifiedBy = referral.getLastModifiedBy();
            if (lastModifiedBy != null) {
                info.add(Privilege.LAST_MODIFIED_BY_ATTRIBUTE + "=" +
                    lastModifiedBy);
            }

            long creationDate = referral.getCreationDate();
            if (creationDate > 0) {
                String data = Long.toString(creationDate) + "=" +
                    Privilege.CREATION_DATE_ATTRIBUTE;
                info.add(data);
                info.add("|" + data);
            }

            long lastModifiedDate = referral.getLastModifiedDate();
            if (lastModifiedDate > 0) {
                String data = Long.toString(lastModifiedDate) + "=" +
                    Privilege.LAST_MODIFIED_DATE_ATTRIBUTE;
                info.add(data);
                info.add("|" + data);
            }
            map.put("ou", info);

            s.setAttributes(map);
            s.save();
            updateIndexCount(adminToken, realm, 1, true);
        } catch (SSOException e) {
            throw new EntitlementException(270, e);
        } catch (SMSException e) {
            throw new EntitlementException(270, e);
        }
        return dn;
    }

    /**
     * Removes privilege.
     *
     * @param adminSubject Admin Subject who has the rights to write to
     *        datastore.
     * @param realm Realm name.
     * @param name Privilege name.
     * @param notify <code>true</code> to send notification.
     * @throws com.sun.identity.entitlement.EntitlementException if privilege
     * cannot be removed.
     */
    public void remove(
        Subject adminSubject,
        String realm,
        String name,
        boolean notify
    ) throws EntitlementException {
        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);

        if (adminToken == null) {
            Object[] arg = {name};
            throw new EntitlementException(55, arg);
        }

        String dn = null;
        try {
            dn = getPrivilegeDistinguishedName(name, realm, null);

            if (SMSEntry.checkIfEntryExists(dn, adminToken)) {
                SMSEntry s = new SMSEntry(adminToken, dn);
                s.delete();
                updateIndexCount(adminToken, realm, -1, false);

                if (notify) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(NotificationServlet.ATTR_NAME, name);
                    params.put(NotificationServlet.ATTR_REALM_NAME, realm);
                    Notifier notifier = new Notifier(
                        NotificationServlet.PRIVILEGE_DELETED, params);
                    threadPool.submit(notifier);
                }
            }
        } catch (SMSException e) {
            Object[] arg = {dn};
            throw new EntitlementException(51, arg, e);
        } catch (SSOException e) {
            throw new EntitlementException(10, null, e);
        }

    }

    /**
     * Removes referral privilege.
     *
     * @param adminSubject Admin Subject who has the rights to write to
     *        datastore.
     * @param realm Realm name.
     * @param name Referral privilege name.
     * @param notify <code>true</code> to send notification.
     * @throws EntitlementException if privilege cannot be removed.
     */
    public void removeReferral(
        Subject adminSubject,
        String realm,
        String name,
        boolean notify
    ) throws EntitlementException {
        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);

        if (adminToken == null) {
            Object[] arg = {name};
            throw new EntitlementException(55, arg);
        }

        String dn = null;
        try {
            dn = getPrivilegeDistinguishedName(name, realm, REFERRAL_STORE);

            if (SMSEntry.checkIfEntryExists(dn, adminToken)) {
                SMSEntry s = new SMSEntry(adminToken, dn);
                s.delete();
                updateIndexCount(adminToken, realm, -1, true);

                if (notify) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(NotificationServlet.ATTR_NAME, name);
                    params.put(NotificationServlet.ATTR_REALM_NAME, realm);
                    Notifier notifier = new Notifier(
                        NotificationServlet.REFERRAL_DELETED, params);
                    threadPool.submit(notifier);
                }
            }
        } catch (SMSException e) {
            Object[] arg = {dn};
            throw new EntitlementException(51, arg, e);
        } catch (SSOException e) {
            throw new EntitlementException(10, null, e);
        }
    }


    /**
     * Returns a set of privilege names that satifies a search filter.
     *
     * @parma adminSubject Subject who has the rights to read datastore.
     * @param realm Realm name
     * @param filter Search filter.
     * @param numOfEntries Number of max entries.
     * @param sortResults <code>true</code> to have result sorted.
     * @param ascendingOrder <code>true</code> to have result sorted in
     * ascending order.
     * @return a set of privilege names that satifies a search filter.
     * @throws EntityExistsException if search failed.
     */
    public Set<String> search(
        Subject adminSubject,
        String realm,
        String filter,
        int numOfEntries,
        boolean sortResults,
        boolean ascendingOrder
    ) throws EntitlementException {
        Set<String> results = new HashSet<String>();

        try {
            SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);

            if (adminToken == null) {
                throw new EntitlementException(216);
            }

            String baseDN = getSearchBaseDN(realm, null);

            if (SMSEntry.checkIfEntryExists(baseDN, adminToken)) {
                Set<String> dns = SMSEntry.search(adminToken, baseDN, filter);
                for (String dn : dns) {
                    if (!areDNIdentical(baseDN, dn)) {
                        String rdns[] = LDAPDN.explodeDN(dn, true);
                        if ((rdns != null) && rdns.length > 0) {
                            results.add(rdns[0]);
                        }
                    }
                }
            } else {
                return Collections.EMPTY_SET;
            }
        } catch (SMSException ex) {
            throw new EntitlementException(215, ex);
        }
        return results;
    }
    
    /**
     * Returns a set of referral privilege names that satifies a search filter.
     *
     * @parma adminSubject Subject who has the rights to read datastore.
     * @param realm Realm name
     * @param filter Search filter.
     * @param numOfEntries Number of max entries.
     * @param sortResults <code>true</code> to have result sorted.
     * @param ascendingOrder <code>true</code> to have result sorted in
     * ascending order.
     * @return a set of privilege names that satifies a search filter.
     * @throws EntityExistsException if search failed.
     */
    public Set<String> searchReferral(
        Subject adminSubject,
        String realm,
        String filter,
        int numOfEntries,
        boolean sortResults,
        boolean ascendingOrder
    ) throws EntitlementException {
        Set<String> results = new HashSet<String>();

        try {
            SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);

            if (adminToken == null) {
                throw new EntitlementException(216);
            }

            String baseDN = getSearchBaseDN(realm, REFERRAL_STORE);

            if (SMSEntry.checkIfEntryExists(baseDN, adminToken)) {
                Set<String> dns = SMSEntry.search(adminToken, baseDN, filter);
                for (String dn : dns) {
                    if (!areDNIdentical(baseDN, dn)) {
                        String rdns[] = LDAPDN.explodeDN(dn, true);
                        if ((rdns != null) && rdns.length > 0) {
                            results.add(rdns[0]);
                        }
                    }
                }
            } else {
                return Collections.EMPTY_SET;
            }
        } catch (SMSException ex) {
            throw new EntitlementException(215, ex);
        }
        return results;
    }

    private static boolean areDNIdentical(String dn1, String dn2) {
        DN dnObj1 = new DN(dn1);
        DN dnObj2 = new DN(dn2);
        return dnObj1.equals(dnObj2);
    }

    /**
     * Returns a set of privilege that satifies the resource and subject
     * indexes.
     *
     * @param adminSubject Subject who has the rights to read datastore.
     * @param realm Realm name
     * @param iterator Buffered iterator to have the result fed to it.
     * @param indexes Resource search indexes.
     * @param subjectIndexes Subject search indexes.
     * @param bSubTree <code>true</code> to do sub tree search
     * @param excludeDNs Set of DN to be excluded from the search results.
     * @return a set of privilege that satifies the resource and subject
     * indexes.
     */
    public Set<Evaluate> search(
        Subject adminSubject,
        String realm,
        BufferedIterator iterator,
        ResourceSearchIndexes indexes,
        Set<String> subjectIndexes,
        boolean bSubTree,
        Set<String> excludeDNs
    ) throws EntitlementException {
        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);
        Set<Evaluate> results = searchPrivileges(adminToken, realm,
            iterator, indexes, subjectIndexes, bSubTree, excludeDNs);
        results.addAll(searchReferral(adminToken, realm, iterator,
            indexes, bSubTree, excludeDNs));
        return results;
    }

    private Set<Evaluate> searchPrivileges(
        SSOToken adminToken,
        String realm,
        BufferedIterator iterator,
        ResourceSearchIndexes indexes,
        Set<String> subjectIndexes,
        boolean bSubTree,
        Set<String> excludeDNs
    ) throws EntitlementException {
        Set<Evaluate> results = new HashSet<Evaluate>();
        String filter = getFilter(indexes, subjectIndexes, bSubTree);
        String baseDN = getSearchBaseDN(realm, null);

        if (filter != null) {
            if (adminToken == null) {
                Object[] arg = {baseDN};
                throw new EntitlementException(56, arg);
            }
            long start = DB_MONITOR_PRIVILEGE.start();

            try {
                Iterator i = SMSEntry.search(
                    adminToken, baseDN, filter, excludeDNs);
                while (i.hasNext()) {
                    SMSDataEntry e = (SMSDataEntry)i.next();
                    Privilege privilege = Privilege.getInstance(
                        new JSONObject(e.getAttributeValue(
                        SERIALIZABLE_INDEX_KEY)));
                    iterator.add(privilege);
                    results.add(privilege);
                }
            } catch (JSONException e) {
                Object[] arg = {baseDN};
                throw new EntitlementException(52, arg, e);
            } catch (SMSException e) {
                Object[] arg = {baseDN};
                throw new EntitlementException(52, arg, e);
            }

            DB_MONITOR_PRIVILEGE.end(start);
        }
        return results;
    }

    /**
     * Returns a set of referral privilege that satifies the resource and
     * subject indexes.
     *
     * @param adminSubject Subject who has the rights to read datastore.
     * @param realm Realm name
     * @param iterator Buffered iterator to have the result fed to it.
     * @param indexes Resource search indexes.
     * @param bSubTree <code>true</code> to do sub tree search
     * @param excludeDNs Set of DN to be excluded from the search results.
     * @return a set of privilege that satifies the resource and subject
     * indexes.
     */
    public Set<ReferralPrivilege> searchReferral(
        SSOToken adminToken,
        String realm,
        BufferedIterator iterator,
        ResourceSearchIndexes indexes,
        boolean bSubTree,
        Set<String> excludeDNs
    ) throws EntitlementException {
        Set<ReferralPrivilege> results = new HashSet<ReferralPrivilege>();
        String filter = getFilter(indexes, null, bSubTree);
        String baseDN = getSearchBaseDN(realm, REFERRAL_STORE);

        if (filter != null) {
            if (adminToken == null) {
                Object[] arg = {baseDN};
                throw new EntitlementException(56, arg);
            }

            long start = DB_MONITOR_REFERRAL.start();
            try {
                Iterator i = SMSEntry.search(
                    adminToken, baseDN, filter, excludeDNs);
                while (i.hasNext()) {
                    SMSDataEntry e = (SMSDataEntry)i.next();
                    ReferralPrivilege referral = ReferralPrivilege.getInstance(
                        new JSONObject(e.getAttributeValue(
                        SERIALIZABLE_INDEX_KEY)));
                    iterator.add(referral);
                    results.add(referral);
                }
                iterator.isDone();
            } catch (JSONException e) {
                Object[] arg = {baseDN};
                throw new EntitlementException(52, arg, e);
            } catch (SMSException e) {
                Object[] arg = {baseDN};
                throw new EntitlementException(52, arg, e);
            }

            DB_MONITOR_REFERRAL.end(start);
        }
        return results;
    }

    private String getFilter(
        ResourceSearchIndexes indexes,
        Set<String> subjectIndexes,
        boolean bSubTree
    ) {
        StringBuffer filter = new StringBuffer();

        if ((subjectIndexes != null) && !subjectIndexes.isEmpty()) {
            filter.append("(|");
            for (String i : subjectIndexes) {
                Object[] o = {i};
                filter.append(MessageFormat.format(SUBJECT_FILTER_TEMPLATE, o));
            }
            filter.append(")");
        }

        Set<String> hostIndexes = indexes.getHostIndexes();
        if ((hostIndexes != null) && !hostIndexes.isEmpty()) {
            filter.append("(|");
            for (String h : indexes.getHostIndexes()) {
                Object[] o = {h};
                filter.append(MessageFormat.format(HOST_FILTER_TEMPLATE, o));
            }
            filter.append(")");
        }

        if (bSubTree) {
            Set<String> parentPathIndexes = indexes.getParentPathIndexes();
            filter.append("(|");
            if ((parentPathIndexes != null) && !parentPathIndexes.isEmpty()) {
                for (String p : parentPathIndexes) {
                    Object[] o = {p};
                    filter.append(MessageFormat.format(
                        PATH_PARENT_FILTER_TEMPLATE, o));
                }
            }
            filter.append(")");
        } else {
            Set<String> pathIndexes = indexes.getPathIndexes();
            if ((pathIndexes != null) && !pathIndexes.isEmpty()) {
                filter.append("(|");
                for (String p : pathIndexes) {
                    Object[] o = {p};
                    filter.append(MessageFormat.format(
                        PATH_FILTER_TEMPLATE, o));
                }
                filter.append(")");
            }
        }

        String result = filter.toString();
        return (result.length() > 0) ? "(&" + result + ")" : null;
    }

    public class Notifier implements Runnable {
        private String action;
        private Map<String, String> params;

        public Notifier(String action, Map<String, String> params) {
            this.action = action;
            this.params = params;
        }

        public void run() {
            try {
                SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                    AdminTokenAction.getInstance());
                Set<String> serverURLs =
                    ServerConfiguration.getServerInfo(adminToken);

                for (String url : serverURLs) {
                    int idx = url.indexOf("|");
                    if (idx != -1) {
                        url = url.substring(0, idx);
                    }

                    if (!url.equals(currentServerInstance)) {
                        String strURL = url + NotificationServlet.CONTEXT_PATH +
                            "/" + action;

                        StringBuffer buff = new StringBuffer();
                        boolean bFirst = true;
                        for (String k : params.keySet()) {
                            if (bFirst) {
                                bFirst = false;
                            } else {
                                buff.append("&");
                            }
                            buff.append(URLEncoder.encode(k, "UTF-8"))
                                .append("=")
                                .append(URLEncoder.encode(params.get(k),
                                    "UTF-8"));
                        }
                        postRequest(strURL, buff.toString());
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                PrivilegeManager.debug.error("DataStore.notifyChanges", ex);
            } catch (IOException ex) {
                PrivilegeManager.debug.error("DataStore.notifyChanges", ex);
            } catch (SMSException ex) {
                PrivilegeManager.debug.error("DataStore.notifyChanges", ex);
            } catch (SSOException ex) {
                PrivilegeManager.debug.error("DataStore.notifyChanges", ex);
            }
        }

        private String postRequest(String strURL, String data)
            throws IOException {

            OutputStreamWriter wr = null;
            BufferedReader rd = null;

            try {
                URL url = new URL(strURL);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                wr = new OutputStreamWriter(
                    conn.getOutputStream());
                wr.write(data);
                wr.flush();

                rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
                StringBuffer result = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } finally {
                if (wr != null) {
                    wr.close();
                }
                if (rd != null) {
                    rd.close();
                }
            }
        }
    }
}

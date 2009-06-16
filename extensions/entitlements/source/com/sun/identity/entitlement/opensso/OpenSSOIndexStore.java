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
 * $Id: OpenSSOIndexStore.java,v 1.14 2009-06-16 10:37:45 veiming Exp $
 */
package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.Application;
import com.sun.identity.entitlement.ApplicationManager;
import com.sun.identity.entitlement.ApplicationTypeManager;
import com.sun.identity.entitlement.EntitlementConfiguration;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.IPrivilege;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeIndexStore;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.entitlement.ReferralPrivilege;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import com.sun.identity.entitlement.SubjectAttributesManager;
import com.sun.identity.entitlement.interfaces.IThreadPool;
import com.sun.identity.entitlement.util.PrivilegeSearchFilter;
import com.sun.identity.policy.PolicyConfig;
import com.sun.identity.policy.PolicyManager;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.BufferedIterator;
import com.sun.identity.sm.DNMapper;

import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;


public class OpenSSOIndexStore extends PrivilegeIndexStore {
    private static final PolicyCache policyCache;
    private static final PolicyCache referralCache;
    private static final Map<String, IndexCache> indexCaches;
    private static final Map<String, IndexCache> referralIndexCaches;
    private static final int indexCacheSize;
    private static final DataStore dataStore = new DataStore();

    // Initialize the caches
    static {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        Subject adminSubject = SubjectUtils.createSubject(adminToken);
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            adminSubject, "/");
        Set<String> setPolicyCacheSize = ec.getConfiguration(
            EntitlementConfiguration.POLICY_CACHE_SIZE);
        String policyCacheSize = ((setPolicyCacheSize != null) &&
            !setPolicyCacheSize.isEmpty()) ?
                setPolicyCacheSize.iterator().next() : null;
        policyCache = (policyCacheSize != null) ? new PolicyCache(getNumeric(
            policyCacheSize, 100000)) : new PolicyCache(100000);
        referralCache = (policyCacheSize != null) ? new PolicyCache(getNumeric(
            policyCacheSize, 100000)) : new PolicyCache(100000);

        Set<String> setIndexCacheSize = ec.getConfiguration(
            EntitlementConfiguration.INDEX_CACHE_SIZE);
        String indexCacheSizeString = ((setIndexCacheSize != null) &&
            !setIndexCacheSize.isEmpty()) ?
                setIndexCacheSize.iterator().next() : null;
        indexCacheSize = getNumeric(indexCacheSizeString, 100000);
        indexCaches = new HashMap<String, IndexCache>();
        referralIndexCaches = new HashMap<String, IndexCache>();
    }

    // Instance variables
    private String realmDN;
    private IndexCache indexCache;
    private IndexCache referralIndexCache;
    private EntitlementConfiguration entitlementConfig;

    /**
     * Constructor.
     *
     * @param realm Realm Name
     */
    public OpenSSOIndexStore(Subject adminSubject, String realm) {
        super(adminSubject, realm);
        realmDN = DNMapper.orgNameToDN(realm);
        entitlementConfig = EntitlementConfiguration.getInstance(
            adminSubject, realm);

        // Get Index caches based on realm
        synchronized (indexCaches) {
            indexCache = indexCaches.get(realmDN);
            if (indexCache == null) {
                indexCache = new IndexCache(indexCacheSize);
                indexCaches.put(realmDN, indexCache);
            }
        }
        synchronized (referralIndexCaches) {
            referralIndexCache = referralIndexCaches.get(realmDN);
            if (referralIndexCache == null) {
                referralIndexCache = new IndexCache(indexCacheSize);
                referralIndexCaches.put(realmDN, referralIndexCache);
            }
        }
    }

    private static int getNumeric(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Adds a set of privileges to the data store. Proper indexes will be
     * created to speed up policy evaluation.
     *
     * @param privileges Privileges to be added.
     * @throws com.sun.identity.entitlement.EntitlementException if addition
     * failed.
     */
    public void add(Set<IPrivilege> privileges)
        throws EntitlementException {

        for (IPrivilege p : privileges) {
            if (p instanceof Privilege) {
                add((Privilege)p);
            } else if (p instanceof ReferralPrivilege) {
                add((ReferralPrivilege)p);
            }
        }
    }

    private void add(Privilege privilege) throws EntitlementException {
        Subject adminSubject = getAdminSubject();
        String realm = getRealm();
        privilege.canonicalizeResources(adminSubject,
            DNMapper.orgNameToRealmName(realm));
        dataStore.add(adminSubject, realmDN, privilege);
        entitlementConfig.addSubjectAttributeNames(
            privilege.getEntitlement().getApplicationName(),
            SubjectAttributesManager.getRequiredAttributeNames(privilege));
    }

    private void add(ReferralPrivilege referral)
        throws EntitlementException {
        Subject adminSubject = getAdminSubject();
        String realm = getRealm();

        // clone so that canonicalized resource name will be localized.
        ReferralPrivilege clone = (ReferralPrivilege)referral.clone();
        clone.canonicalizeResources(adminSubject,
            DNMapper.orgNameToRealmName(realm));
        dataStore.addReferral(adminSubject, realm, clone);
    }

    /**
     * Deletes a set of privileges from data store.
     *
     * @param privileges Privileges to be deleted.
     * @throws com.sun.identity.entitlement.EntitlementException if deletion
     * failed.
     */
    public void delete(String privilegeName)
        throws EntitlementException {
        delete(privilegeName, true);
    }

    /**
     * Deletes a referral privilege from data store.
     *
     * @param privileges Privileges to be deleted.
     * @throws com.sun.identity.entitlement.EntitlementException if deletion
     * failed.
     */
    public void deleteReferral(String privilegeName)
        throws EntitlementException {
        deleteReferral(privilegeName, true);
    }

    /**
     * Deletes a privilege from data store.
     *
     * @param privilegeName name of privilege to be deleted.
     * @throws com.sun.identity.entitlement.EntitlementException if deletion
     * failed.
     */
    public void delete(Set<IPrivilege> privileges)
        throws EntitlementException {
        Subject adminSubject = getAdminSubject();
        String realm = getRealm();

        for (IPrivilege p : privileges) {
            String dn = delete(p.getName(), true);
            indexCache.clear(p.getResourceSaveIndexes(
                adminSubject, DNMapper.orgNameToRealmName(realm)), dn);
        }
    }

    public String delete(String privilegeName, boolean notify)
        throws EntitlementException {
        Subject adminSubject = getAdminSubject();
        String realm = getRealm();
        String dn = DataStore.getPrivilegeDistinguishedName(
            privilegeName, realm, null);
        dataStore.remove(adminSubject, realmDN, privilegeName, notify);
        policyCache.decache(dn, realmDN);
        return dn;
    }

    public String deleteReferral(String privilegeName, boolean notify)
        throws EntitlementException {
        Subject adminSubject = getAdminSubject();
        String realm = getRealm();
        String dn = DataStore.getPrivilegeDistinguishedName(
            privilegeName, realm, DataStore.REFERRAL_STORE);
        dataStore.removeReferral(adminSubject, realm, privilegeName, notify);
        referralCache.decache(dn, realmDN);
        return dn;
    }

    private void cache(
        IPrivilege eval,
        Set<String> subjectSearchIndexes,
        String realm
    ) throws EntitlementException {
        if (eval instanceof Privilege) {
            cache((Privilege)eval, subjectSearchIndexes, realm);
        } else if (eval instanceof ReferralPrivilege) {
            cache((ReferralPrivilege)eval, realm);
        }
    }

    private void cache(
        Privilege p,
        Set<String> subjectSearchIndexes,
        String realm
    ) throws EntitlementException {
        String dn = DataStore.getPrivilegeDistinguishedName(
            p.getName(), realm, null);
        String realmName = DNMapper.orgNameToRealmName(realm);
        indexCache.cache(p.getEntitlement().getResourceSaveIndexes(
            getAdminSubject(), realmName), subjectSearchIndexes, dn);
        policyCache.cache(dn, p, realmDN);
    }

    private void cache(
        ReferralPrivilege p,
        String realm
    ) throws EntitlementException {
        String dn = DataStore.getPrivilegeDistinguishedName(
            p.getName(), realm, DataStore.REFERRAL_STORE);
        referralIndexCache.cache(p.getResourceSaveIndexes(getAdminSubject(),
            DNMapper.orgNameToRealmName(realm)), null, dn);
        referralCache.cache(dn, p, realmDN);
    }

    /**
     * Returns an iterator of matching privilege objects.
     *
     * @param indexes Resource search indexes.
     * @param subjectIndexes Subject search indexes.
     * @param bSubTree <code>true</code> for sub tree evaluation.
     * @param threadPool Thread pool for executing threads.
     * @return an iterator of matching privilege objects.
     * @throws com.sun.identity.entitlement.EntitlementException if results
     * cannot be obtained.
     */
    public Iterator<IPrivilege> search(ResourceSearchIndexes indexes,
        Set<String> subjectIndexes, boolean bSubTree, IThreadPool threadPool)
        throws EntitlementException {
        // TODO determine if search results should be threaded
        boolean isThreaded = false;
        BufferedIterator iterator = (isThreaded) ? new BufferedIterator() :
            new SimpleIterator();
        Set setDNs = searchPrivileges(indexes, subjectIndexes, bSubTree,
            iterator);
        setDNs.addAll(searchReferrals(indexes, bSubTree, iterator));

        if (doDSSearch()) {
            SearchTask st = new SearchTask(this, iterator, indexes,
                subjectIndexes, bSubTree, setDNs);
            if (isThreaded) {
                threadPool.submit(st);
            } else {
                st.run();
            }
        } else {
            iterator.isDone();
        }
        return iterator;
    }

    private boolean doDSSearch() {
        // TODO handling of fully cached policies must be re-evaluated
        /*
        String realm = getRealm();
        Subject sbj = getAdminSubject();

        // check if PolicyCache has all the entries for the realm
        int cacheEntries = policyCache.getCount(realm);
        int totalPolicies = dataStore.getNumberOfPolicies(sbj, realm);
        if ((totalPolicies > 0) &&(cacheEntries < totalPolicies)) {
            return true;
        }

        cacheEntries = referralCache.getCount(realm);
        int totalReferrals = dataStore.getNumberOfReferrals(sbj, realm);
        if ((totalReferrals > 0) && (cacheEntries < totalReferrals)) {
            return true;
        }
        */
        return true;
    }

    private Set<String> searchReferrals(ResourceSearchIndexes indexes,
        boolean bSubTree, BufferedIterator iterator)
    {
        Set<String> setDNs = referralIndexCache.getMatchingEntries(indexes,
            null, bSubTree);
        for (Iterator<String> i = setDNs.iterator(); i.hasNext();) {
            String dn = (String) i.next();
            ReferralPrivilege r = referralCache.getReferral(dn);
            if (r != null) {
                iterator.add(r);
            } else {
                i.remove();
            }
        }
        return setDNs;
    }

    private Set<String> searchPrivileges(ResourceSearchIndexes indexes,
        Set<String> subjectIndexes, boolean bSubTree, BufferedIterator iterator)
    {
        Set<String> setDNs = indexCache.getMatchingEntries(indexes,
            subjectIndexes, bSubTree);
        for (Iterator<String> i = setDNs.iterator(); i.hasNext();) {
            String dn = (String) i.next();
            Privilege p = policyCache.getPolicy(dn);
            if (p != null) {
                iterator.add(p);
            } else {
                i.remove();
            }
        }
        return setDNs;
    }

    /**
     * Returns a set of privilege names that satifies a search filter.
     *
     * @param filters Search filters.
     * @param boolAnd <code>true</code> to have filters as exclusive.
     * @param numOfEntries Number of max entries.
     * @param sortResults <code>true</code> to have result sorted.
     * @param ascendingOrder <code>true</code> to have result sorted in
     * ascending order.
     * @return a set of privilege names that satifies a search filter.
     * @throws EntitlementException if search failed.
     */
    public Set<String> searchPrivilegeNames(
        Set<PrivilegeSearchFilter> filters,
        boolean boolAnd,
        int numOfEntries,
        boolean sortResults,
        boolean ascendingOrder
    ) throws EntitlementException {
        StringBuffer strFilter = new StringBuffer();
        if (filters.isEmpty()) {
            strFilter.append("(ou=*)");
        } else {
            if (filters.size() == 1) {
                strFilter.append(filters.iterator().next().getFilter());
            } else {
                if (boolAnd) {
                    strFilter.append("(&");
                } else {
                    strFilter.append("(|");
                }
                for (PrivilegeSearchFilter psf : filters) {
                    strFilter.append(psf.getFilter());
                }
                strFilter.append(")");
            }
        }
        Subject adminSubject = getAdminSubject();
        String realm = getRealm();

        return dataStore.search(adminSubject, realm, strFilter.toString(),
            numOfEntries, sortResults, ascendingOrder);
    }

    /**
     * Returns a set of referral privilege names that satifies a search filter.
     *
     * @param filters Search filters.
     * @param boolAnd <code>true</code> to have filters as exclusive.
     * @param numOfEntries Number of max entries.
     * @param sortResults <code>true</code> to have result sorted.
     * @param ascendingOrder <code>true</code> to have result sorted in
     * ascending order.
     * @return a set of referral privilege names that satifies a search filter.
     * @throws EntitlementException if search failed.
     */
    public Set<String> searchReferralPrivilegeNames(
        Set<PrivilegeSearchFilter> filters,
        boolean boolAnd,
        int numOfEntries,
        boolean sortResults,
        boolean ascendingOrder
    ) throws EntitlementException {
        return searchReferralPrivilegeNames(filters, getAdminSubject(),
            getRealm(), boolAnd, numOfEntries, sortResults, ascendingOrder);
    }

    /**
     * Returns a set of referral privilege names that matched a set of search
     * criteria.
     *
     * @param filters Set of search filter (criteria).
     * @param boolAnd <code>true</code> to be inclusive.
     * @param numOfEntries Number of maximum search entries.
     * @param sortResults <code>true</code> to have the result sorted.
     * @param ascendingOrder  <code>true</code> to have the result sorted in
     *        ascending order.
     * @return a set of referral privilege names that matched a set of search
     *         criteria.
     * @throws EntitlementException if search failed.
     */
    public Set<String> searchReferralPrivilegeNames(
        Set<PrivilegeSearchFilter> filters,
        Subject adminSubject,
        String currentRealm,
        boolean boolAnd,
        int numOfEntries,
        boolean sortResults,
        boolean ascendingOrder
    ) throws EntitlementException {
        StringBuffer strFilter = new StringBuffer();
        if (filters.isEmpty()) {
            strFilter.append("(ou=*)");
        } else {
            if (filters.size() == 1) {
                strFilter.append(filters.iterator().next().getFilter());
            } else {
                if (boolAnd) {
                    strFilter.append("(&");
                } else {
                    strFilter.append("(|");
                }
                for (PrivilegeSearchFilter psf : filters) {
                    strFilter.append(psf.getFilter());
                }
                strFilter.append(")");
            }
        }
        return dataStore.searchReferral(adminSubject, currentRealm,
            strFilter.toString(), numOfEntries, sortResults, ascendingOrder);
    }

    /**
     * Returns a set of resources that are referred to this realm.
     *
     * @param applicationTypeName Application type name,
     * @return a set of resources that are referred to this realm.
     * @throws EntitlementException if resources cannot be returned.
     */
    @Override
    public Set<String> getReferredResources(String applicationTypeName)
        throws EntitlementException {
        String realm = getRealm();
        if (realm.equals("/")) {
            return Collections.EMPTY_SET;
        }
        Subject adminSubject = getAdminSubject();
        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);

        try {
            Set<String> results = new HashSet<String>();
            Set<String> realms = getPeerRealms(realm);
            realms.addAll(getParentRealms(realm));
            String filter = "(&(ou=" + DataStore.REFERRAL_APPLS + "="
                + applicationTypeName + ")(ou=" + DataStore.REFERRAL_REALMS +
                "=" + realm + "))";

            Map<String, Set<ReferralPrivilege>> referrals = new
                HashMap<String, Set<ReferralPrivilege>>();
            for (String rlm : realms) {
                referrals.put(rlm, dataStore.searchReferrals(adminToken, rlm,
                    filter));
            }

            for (String rlm : referrals.keySet()) {
                Set<ReferralPrivilege> rPrivileges = referrals.get(rlm);

                for (ReferralPrivilege r : rPrivileges) {
                    Map<String, Set<String>> map =
                        r.getOriginalMapApplNameToResources();
                    for (String a : map.keySet()) {
                        Application appl = ApplicationManager.getApplication(
                            adminSubject, rlm, a);
                        if (appl.getApplicationType().getName().equals(
                            applicationTypeName)) {
                            results.addAll(map.get(a));
                        }
                    }
                }
            }

            results.addAll(getOrgAliasMappingResources(
                realm, applicationTypeName));

            return results;
        } catch (SMSException ex) {
            PrivilegeManager.debug.error(
                "OpenSSOIndexStore.getReferredResources", ex);
            Object[] param = {realm};
            throw new EntitlementException(275, param);
        }
    }

    private Set<String> getParentRealms(String realm) throws SMSException {
        Set<String> results = new HashSet<String>();
        Subject adminSubject = getAdminSubject();
        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);
        OrganizationConfigManager ocm = new OrganizationConfigManager(
            adminToken, realm);
        while (true) {
            ocm = ocm.getParentOrgConfigManager();
            String name = DNMapper.orgNameToRealmName(
                ocm.getOrganizationName());
            results.add(name);
            if (name.equals("/")) {
                break;
            }
        }
        return results;
    }

    private Set<String> getPeerRealms(String realm) throws SMSException {
        Subject adminSubject = getAdminSubject();
        SSOToken adminToken = SubjectUtils.getSSOToken(adminSubject);
        OrganizationConfigManager ocm = new OrganizationConfigManager(
            adminToken, realm);
        OrganizationConfigManager parentOrg = ocm.getParentOrgConfigManager();
        String base = DNMapper.orgNameToRealmName(
            parentOrg.getOrganizationName());
        if (!base.endsWith("/")) {
            base += "/";
        }
        Set<String> results = new HashSet<String>();
        Set<String> subrealms = parentOrg.getSubOrganizationNames();
        for (String s : subrealms) {
            results.add(base + s);
        }
        results.remove(getRealm());
        return results;
    }

    private Set<String> getOrgAliasMappingResources(
        String realm, String applicationTypeName
    ) throws SMSException {
        Set<String> results = new HashSet<String>();

        if (applicationTypeName.equalsIgnoreCase(
            ApplicationTypeManager.URL_APPLICATION_TYPE_NAME)) {
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());

            if (isOrgAliasMappingResourceEnabled(adminToken)) {
                OrganizationConfigManager m = new
                    OrganizationConfigManager(adminToken, realm);
                Map<String, Set<String>> map = m.getAttributes(
                    PolicyManager.ID_REPO_SERVICE);
                Set<String> orgAlias = map.get(PolicyManager.ORG_ALIAS);

                if ((orgAlias != null) && !orgAlias.isEmpty()) {
                    for (String s : orgAlias) {
                        results.add(PolicyManager.ORG_ALIAS_URL_HTTPS_PREFIX +
                            s.trim() + PolicyManager.ORG_ALIAS_URL_SUFFIX);
                        results.add(PolicyManager.ORG_ALIAS_URL_HTTP_PREFIX +
                            s.trim() + PolicyManager.ORG_ALIAS_URL_SUFFIX);
                    }
                }
            }
        }
        return results;
    }

    private boolean isOrgAliasMappingResourceEnabled(SSOToken adminToken) {
        try {
            ServiceSchemaManager ssm = new ServiceSchemaManager(
                PolicyConfig.POLICY_CONFIG_SERVICE, adminToken);
            ServiceSchema globalSchema = ssm.getGlobalSchema();
            Map<String, Set<String>> map =
                globalSchema.getAttributeDefaults();
            Set<String> values = map.get(
                PolicyConfig.ORG_ALIAS_MAPPED_RESOURCES_ENABLED);
            if ((values != null) && !values.isEmpty()) {
                String val = values.iterator().next();
                return Boolean.valueOf(val);
            } else {
                return false;
            }
        } catch (SMSException ex) {
            PrivilegeManager.debug.error(
                "OpenSSOIndexStore.isOrgAliasMappingResourceEnabled", ex);
            return false;
        } catch (SSOException ex) {
            PrivilegeManager.debug.error(
                "OpenSSOIndexStore.isOrgAliasMappingResourceEnabled", ex);
            return false;
        }
    }

    String getRealmDN() {
        return (realmDN);
    }

    public class SearchTask implements Runnable {

        private OpenSSOIndexStore parent;
        private BufferedIterator iterator;
        private ResourceSearchIndexes indexes;
        private Set<String> subjectIndexes;
        private boolean bSubTree;
        private Set<String> excludeDNs;

        public SearchTask(
            OpenSSOIndexStore parent,
            BufferedIterator iterator,
            ResourceSearchIndexes indexes,
            Set<String> subjectIndexes,
            boolean bSubTree,
            Set<String> excludeDNs
        ) {
            this.parent = parent;
            this.iterator = iterator;
            this.indexes = indexes;
            this.subjectIndexes = subjectIndexes;
            this.bSubTree = bSubTree;
            this.excludeDNs = excludeDNs;
        }

        public void run() {
            try {
                Set<IPrivilege> results = dataStore.search(
                    parent.getAdminSubject(), parent.getRealmDN(), iterator,
                    indexes, subjectIndexes, bSubTree, excludeDNs);
                for (IPrivilege eval : results) {
                    parent.cache(eval, subjectIndexes, parent.getRealmDN());
                }
            } catch (EntitlementException ex) {
                iterator.isDone();
                PrivilegeManager.debug.error(
                    "OpenSSOIndexStore.SearchTask.runPolicy", ex);
            }
        }
    }
}

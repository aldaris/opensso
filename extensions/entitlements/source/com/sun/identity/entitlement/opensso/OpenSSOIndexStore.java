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
 * $Id: OpenSSOIndexStore.java,v 1.3 2009-05-19 23:50:15 veiming Exp $
 */
package com.sun.identity.entitlement.opensso;

import com.sun.identity.entitlement.EntitlementConfiguration;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeIndexStore;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import com.sun.identity.entitlement.SubjectAttributesManager;
import com.sun.identity.entitlement.interfaces.IThreadPool;
import com.sun.identity.entitlement.util.PrivilegeSearchFilter;
import com.sun.identity.shared.BufferedIterator;
import java.util.Iterator;
import java.util.Set;


public class OpenSSOIndexStore extends PrivilegeIndexStore {
    private PolicyCache policyCache;
    private IndexCache indexCache;
    private DataStore dataStore = new DataStore();
    private String realm;

    /**
     * Constructor.
     *
     * @param realm Realm Name
     */
    public OpenSSOIndexStore(String realm) {
        this.realm = realm;
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            realm);

        Set<String> setPolicyCacheSize = ec.getConfiguration(
            EntitlementConfiguration.POLICY_CACHE_SIZE);
        String policyCacheSize = ((setPolicyCacheSize != null) &&
            !setPolicyCacheSize.isEmpty()) ?
                setPolicyCacheSize.iterator().next() : null;
        policyCache = (policyCacheSize != null) ? new PolicyCache(getNumeric(
            policyCacheSize, 100000)) : new PolicyCache(100000);

        Set<String> setIndexCacheSize = ec.getConfiguration(
            EntitlementConfiguration.INDEX_CACHE_SIZE);
        String indexCacheSize = ((setIndexCacheSize != null) &&
            !setIndexCacheSize.isEmpty()) ?
                setIndexCacheSize.iterator().next() : null;
        indexCache = (indexCacheSize != null) ? new IndexCache(getNumeric(
            indexCacheSize, 100000)) : new IndexCache(100000);
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
    public void add(Set<Privilege> privileges)
        throws EntitlementException {
        for (Privilege p : privileges) {
            p.canonicalizeResources();
            dataStore.add(realm, p);
            EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            realm);
            ec.addSubjectAttributeNames(p.getEntitlement().getApplicationName(),
                SubjectAttributesManager.getRequiredAttributeNames(p));
        }
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
     * Deletes a privilege from data store.
     *
     * @param privilegeName name of privilege to be deleted.
     * @throws com.sun.identity.entitlement.EntitlementException if deletion
     * failed.
     */
    public void delete(Set<Privilege> privileges)
        throws EntitlementException {
        for (Privilege p : privileges) {
            String dn = delete(p.getName(), true);
            indexCache.clear(p.getEntitlement().getResourceSaveIndexes(), dn);
        }
    }

    public String delete(String privilegeName, boolean notify)
        throws EntitlementException {
        String dn = DataStore.getPrivilegeDistinguishedName(
            privilegeName, realm, null);
        dataStore.remove(realm, privilegeName, notify);
        policyCache.decache(dn);
        return dn;
    }

    private void cache(Privilege p, String realm)
        throws EntitlementException {
        String dn = DataStore.getPrivilegeDistinguishedName(
            p.getName(), realm, null);
        indexCache.cache(p.getEntitlement().getResourceSaveIndexes(), dn);
        policyCache.cache(dn, p);
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
    public Iterator<Privilege> search(
        ResourceSearchIndexes indexes,
        Set<String> subjectIndexes,
        boolean bSubTree,
        IThreadPool threadPool
    ) throws EntitlementException {
        BufferedIterator iterator = new BufferedIterator();
        Set<String> setDNs = indexCache.getMatchingEntries(indexes,
            subjectIndexes, bSubTree);
        for (Iterator i = setDNs.iterator(); i.hasNext();) {
            String dn = (String) i.next();
            Privilege p = policyCache.getPolicy(dn);
            if (p != null) {
                iterator.add(p);
            } else {
                i.remove();
            }
        }
        SearchTask st = new SearchTask(realm, this, iterator, indexes,
            subjectIndexes, bSubTree, setDNs);
        threadPool.submit(st);
        return iterator;
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
        return dataStore.search(realm, strFilter.toString(), numOfEntries,
            sortResults, ascendingOrder);
    }

    public class SearchTask implements Runnable {

        private String realm;
        private OpenSSOIndexStore parent;
        private BufferedIterator iterator;
        private ResourceSearchIndexes indexes;
        private Set<String> subjectIndexes;
        private boolean bSubTree;
        private Set<String> excludeDNs;

        public SearchTask(
            String realm,
            OpenSSOIndexStore parent,
            BufferedIterator iterator,
            ResourceSearchIndexes indexes,
            Set<String> subjectIndexes,
            boolean bSubTree,
            Set<String> excludeDNs
        ) {
            this.realm = realm;
            this.parent = parent;
            this.iterator = iterator;
            this.indexes = indexes;
            this.subjectIndexes = subjectIndexes;
            this.bSubTree = bSubTree;
            this.excludeDNs = excludeDNs;
        }

        public void run() {
            try {
                Set<Privilege> results = parent.dataStore.search(realm,
                        iterator, indexes, subjectIndexes, bSubTree,
                        excludeDNs);
                for (Privilege p : results) {
                    parent.cache(p, realm);
                }
            } catch (EntitlementException ex) {
                //TOFIX
            }
        }
    }
}

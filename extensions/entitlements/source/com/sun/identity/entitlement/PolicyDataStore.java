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
 * $Id: PolicyDataStore.java,v 1.5 2009-04-01 00:21:29 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.BufferedIterator;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class PolicyDataStore implements IPolicyDataStore {

    private PolicyCache policyCache = new PolicyCache(
            EntitlementService.getNumericAttributeValue(
            EntitlementService.POLICY_CACHE_SIZE));
    private IndexCache indexCache = new IndexCache(
            EntitlementService.getNumericAttributeValue(
            EntitlementService.POLICY_CACHE_SIZE));
    private DataStore dataStore = new DataStore();

    public void add(Privilege p)
            throws EntitlementException {
        dataStore.add(p.getEntitlement().getResourceSaveIndexes(), p);
    }

    public void delete(Privilege p)
            throws EntitlementException {
        String dn = DataStore.getDN(p);
        dataStore.delete(p.getName());
        policyCache.decache(dn);
        indexCache.clear(p.getEntitlement().getResourceSaveIndexes(), dn);
    }

    private void cache(Privilege p)
            throws EntitlementException {
        String dn = DataStore.getDN(p);
        indexCache.cache(p.getEntitlement().getResourceSaveIndexes(), dn);
        policyCache.cache(dn, p);
    }

    private void decache(Privilege p)
            throws EntitlementException {
        policyCache.decache(p.getName());
    }

    public Iterator<Privilege> search(
            ResourceSearchIndexes indexes,
            boolean bSubTree)
            throws EntitlementException {
        BufferedIterator<Privilege> iterator =
                new BufferedIterator<Privilege>();
        Set<String> setDNs = indexCache.getMatchingEntries(indexes, bSubTree);
        for (Iterator i = setDNs.iterator(); i.hasNext();) {
            String dn = (String) i.next();
            Privilege p = policyCache.getPolicy(dn);
            if (p != null) {
                iterator.add(p);
            } else {
                i.remove();
            }
        }
        ThreadPool.submit(new SearchTask(this, iterator, indexes, bSubTree,
                setDNs));
        return iterator;
    }

    public class SearchTask implements Runnable {

        private PolicyDataStore parent;
        private BufferedIterator<Privilege> iterator;
        private ResourceSearchIndexes indexes;
        private boolean bSubTree;
        private Set<String> excludeDNs;

        public SearchTask(
                PolicyDataStore parent,
                BufferedIterator<Privilege> iterator,
                ResourceSearchIndexes indexes,
                boolean bSubTree,
                Set<String> excludeDNs) {
            this.parent = parent;
            this.iterator = iterator;
            this.indexes = indexes;
            this.bSubTree = bSubTree;
            this.excludeDNs = excludeDNs;
        }

        public void run() {
            try {
                Set<Privilege> results = parent.dataStore.search(
                        iterator, indexes, bSubTree, excludeDNs);
                for (Privilege p : results) {
                    parent.cache(p);
                }
            } catch (EntitlementException ex) {
                //TOFIX
            }
        }
    }
}

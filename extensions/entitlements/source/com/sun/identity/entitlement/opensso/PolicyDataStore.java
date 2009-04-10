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
 * $Id: PolicyDataStore.java,v 1.3 2009-04-10 23:36:06 veiming Exp $
 */
package com.sun.identity.entitlement.opensso;

import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.PolicyConfigFactory;
import com.sun.identity.entitlement.interfaces.IPolicyDataStore;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import com.sun.identity.entitlement.SubjectAttributesManager;
import com.sun.identity.entitlement.ThreadPool;
import com.sun.identity.entitlement.interfaces.IPolicyConfig;
import com.sun.identity.shared.BufferedIterator;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class PolicyDataStore implements IPolicyDataStore {

    private PolicyCache policyCache;
    private IndexCache indexCache;
    private DataStore dataStore = new DataStore();

    public PolicyDataStore() {
        IPolicyConfig policyConfig = PolicyConfigFactory.getPolicyConfig();
        policyCache = new PolicyCache(getNumeric(
            policyConfig.getAttributeValue(IPolicyConfig.POLICY_CACHE_SIZE),
            100000));
        indexCache = new IndexCache(getNumeric(
            policyConfig.getAttributeValue(IPolicyConfig.INDEX_CACHE_SIZE),
            100000));
    }

    private static int getNumeric(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public void add(Privilege p)
        throws EntitlementException {
        dataStore.add(p);
        IPolicyConfig config = PolicyConfigFactory.getPolicyConfig();
        config.addSubjectAttributeNames("/",
            SubjectAttributesManager.getRequiredAttributeNames(p)); //TOFIX realm
    }

    public void delete(Privilege p)
            throws EntitlementException {
        String dn = DataStore.getDistinguishedName(p.getName());
        dataStore.delete(p.getName());
        policyCache.decache(dn);
        indexCache.clear(p.getEntitlement().getResourceSaveIndexes(), dn);
    }

    private void cache(Privilege p)
            throws EntitlementException {
        String dn = DataStore.getDistinguishedName(p.getName());
        indexCache.cache(p.getEntitlement().getResourceSaveIndexes(), dn);
        policyCache.cache(dn, p);
    }

    private void decache(Privilege p)
            throws EntitlementException {
        policyCache.decache(p.getName());
    }

    public Iterator<Privilege> search(
            ResourceSearchIndexes indexes,
            Set<String> subjectIndexes,
            boolean bSubTree)
            throws EntitlementException {
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
        ThreadPool.submit(new SearchTask(this, iterator, indexes, 
            subjectIndexes, bSubTree, setDNs));
        return iterator;
    }

    public class SearchTask implements Runnable {

        private PolicyDataStore parent;
        private BufferedIterator iterator;
        private ResourceSearchIndexes indexes;
        private Set<String> subjectIndexes;
        private boolean bSubTree;
        private Set<String> excludeDNs;

        public SearchTask(
            PolicyDataStore parent,
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
                Set<Privilege> results = parent.dataStore.search(
                        iterator, indexes, subjectIndexes, bSubTree,
                        excludeDNs);
                for (Privilege p : results) {
                    parent.cache(p);
                }
            } catch (EntitlementException ex) {
                //TOFIX
            }
        }
    }
}

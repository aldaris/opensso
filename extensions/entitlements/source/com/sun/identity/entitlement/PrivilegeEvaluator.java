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
 * $Id: PrivilegeEvaluator.java,v 1.4 2009-03-28 06:45:28 veiming Exp $
 */
package com.sun.identity.entitlement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 *
 * @author dennis
 */
class PrivilegeEvaluator implements IPolicyEvaluator {
    private Subject adminSubject;
    private Subject subject;
    private String applicationName;
    private Entitlement entitlement;
    private Map<String, Set<String>> envParameters;
    private ResourceSearchIndexes indexes;
    private List<Entitlement> resultQ = new LinkedList<Entitlement>();
    private List<Entitlement> mergedResults = new ArrayList<Entitlement>();
    private Application application;
    private int counter;
    private int maxCounter = -1;
    private EntitlementCombiner entitlementCombiner;
    private boolean subTree;

    public boolean hasEntitlement(
        Subject adminSubject,
        Subject subject,
        String applicationName,
        Entitlement entitlement,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {
        this.adminSubject = adminSubject;
        this.subject = subject;
        this.applicationName = applicationName;
        this.entitlement = entitlement;
        this.envParameters = envParameters;
        entitlementCombiner = getApplication().getEntitlementCombiner();
        this.subTree = false;

        //separate threads, PIP
        indexes = entitlement.getResourceSearchIndexes();
        ThreadPool.submit(new EvaluationTask(this, false));

        synchronized (this) {
            while ((maxCounter == -1) && (maxCounter != counter)) {
                while (!resultQ.isEmpty()) {
                    mergeEntitlement(resultQ.remove(0));
                    counter++;
                    //combine entitlement;
                }
                try {
                    wait();
                } catch (InterruptedException ex) {
                    //TOFIX
                }
            }
        }

        //TOFIX
        return false;
    }

    private void mergeEntitlement(Entitlement ent) {
        
    }
    
    private Application getApplication() {
        if (application == null) {
            ApplicationManager.getApplication(applicationName);
        }
        return application;
    }

    public List<Entitlement> getEntitlements(
        Subject adminSubject,
        Subject subject,
        String applicationName,
        String resourceName,
        Map<String, Set<String>> envParameters,
        boolean recursive
    ) throws EntitlementException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    class EvaluationTask implements Runnable {
        final PrivilegeEvaluator parent;
        private boolean bSubTree;

        EvaluationTask(PrivilegeEvaluator parent, boolean bSubTree) {
            this.parent = parent;
            this.bSubTree = bSubTree;
        }

        public void run() {
            try {
                int count = 0;
                IPolicyDataStore ds =
                    PolicyDataStoreFactory.getInstance().getDataStore();
                for (Iterator<Privilege> i = ds.search(parent.indexes,
                    bSubTree); i.hasNext();
                ) {
                    ThreadPool.submit(new PrivilegeTask(parent, i.next())); //TOFIX
                    count++;
                }
                parent.maxCounter = count;
                synchronized (parent) {
                    parent.notify();
                }
            } catch (EntitlementException ex) {
                //TOFIX
            }
        }
    }

    class PrivilegeTask implements Runnable {
        final PrivilegeEvaluator parent;
        private Privilege privilege;

        PrivilegeTask(PrivilegeEvaluator parent, Privilege privilege) {
            this.parent = parent;
            this.privilege = privilege;
        }

        public void run() {
            try {
                List<Entitlement> entitlements = privilege.getEntitlements(
                    parent.subject, parent.applicationName,
                    parent.envParameters, parent.subTree);
                synchronized(parent) {
                    parent.resultQ.addAll(entitlements);
                    parent.notify();
                }
            } catch (EntitlementException ex) {
                //TOFIX
            }
        }
    }


}

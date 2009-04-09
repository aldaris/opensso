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
 * $Id: PrivilegeEvaluator.java,v 1.8 2009-04-09 13:15:02 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyEvaluator;
import com.sun.identity.entitlement.interfaces.IPolicyDataStore;
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
    private String resourceName;
    private Map<String, Set<String>> envParameters;
    private ResourceSearchIndexes indexes;
    private List<List<Entitlement>> resultQ = new
        LinkedList<List<Entitlement>>();
    private Application application;
    private int counter;
    private int maxCounter = -1;
    private EntitlementCombiner entitlementCombiner;
    private boolean subTree;

    private void init(
        Subject adminSubject,
        Subject subject,
        String applicationName,
        String resourceName,
        Set<String> actionValues,
        Map<String, Set<String>> envParameters,
        boolean subTree
    ) throws EntitlementException {
        this.adminSubject = adminSubject;
        this.subject = subject;
        this.applicationName = applicationName;
        this.resourceName = resourceName;
        this.envParameters = envParameters;
        entitlementCombiner = getApplication().getEntitlementCombiner();
        entitlementCombiner.init(applicationName, resourceName,
            actionValues, subTree);
        this.subTree = subTree;

    }

    public boolean hasEntitlement(
        Subject adminSubject,
        Subject subject,
        String applicationName,
        Entitlement entitlement,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {
        init(adminSubject, subject, applicationName,
            entitlement.getResourceName(), 
            entitlement.getActionValues().keySet(), envParameters, false);

        //separate threads, PIP
        indexes = entitlement.getResourceSearchIndexes();
        List<Entitlement> results = getEntitlements();
        Entitlement result = results.get(0);
        for (String action : entitlement.getActionValues().keySet()) {
            Boolean b = result.getActionValue(action);
            if ((b == null) || !b.booleanValue()) {
                return false;
            }
        }
        return true;
    }

    public List<Entitlement> evaluate(
        Subject adminSubject,
        Subject subject,
        String applicationName,
        String resourceName,
        Map<String, Set<String>> envParameters,
        boolean recursive
    ) throws EntitlementException {
        init(adminSubject, subject, applicationName,
            resourceName, null, envParameters, recursive);

        //separate threads, PIP
        indexes = getApplication().getResourceSearchIndex(resourceName); //TOFIX

        return getEntitlements();
    }

    private List<Entitlement> getEntitlements() {
        ThreadPool.submit(new EvaluationTask(this, subTree));

        synchronized (this) {
            boolean isDone = false;
            while ((maxCounter == -1) || ((maxCounter != counter) && !isDone)) {
                while (!resultQ.isEmpty() && !isDone) {
                    entitlementCombiner.add(resultQ.remove(0));
                    isDone = entitlementCombiner.isDone();
                    counter++;
                }
                if ((maxCounter != counter) && !isDone) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        //TOFIX
                    }
                }
            }
        }
        return entitlementCombiner.getResults();
    }

    
    private Application getApplication() {
        if (application == null) {
            application = ApplicationManager.getApplication(
                "/", applicationName); //TOFIX: realm and NPE
        }
        return application;
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
                    SubjectAttributesManager.getSubjectSearchFilter(
                        parent.subject), bSubTree); i.hasNext();
                ) {
                    ThreadPool.submit(new PrivilegeTask(parent, i.next()));
                    //TOFIX: ThreadPool?
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
                List<Entitlement> entitlements = privilege.evaluate(
                    parent.subject, parent.resourceName,
                    parent.envParameters, parent.subTree);
                synchronized(parent) {
                    parent.resultQ.add(entitlements);
                    parent.notify();
                }
            } catch (EntitlementException ex) {
                //TOFIX
            }
        }
    }


}

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
 * $Id: PrivilegeEvaluator.java,v 1.10 2009-04-26 07:20:36 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyDataStore;
import com.sun.identity.entitlement.interfaces.IThreadPool;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * This class evaluates entitlements of a subject for a given resource
 * and a environment paramaters.
 */
class PrivilegeEvaluator {
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
    private boolean recursive;
    private IThreadPool threadPool;
    private EntitlementException eException;

    /**
     * Initializes the evaluator.
     *
     * @param adminSubject Administrator subject which is used for evcaluation.
     * @param subject Subject to be evaluated.
     * @param applicationName Application Name.
     * @param resourceName Rsource name.
     * @param actions Action names.
     * @param envParameters Environment parameters.
     * @param recursive <code>true</code> for sub tree evaluation
     * @throws com.sun.identity.entitlement.EntitlementException if
     * initialization fails.
     */
    private void init(
        Subject adminSubject,
        Subject subject,
        String applicationName,
        String resourceName,
        Set<String> actions,
        Map<String, Set<String>> envParameters,
        boolean recursive
    ) throws EntitlementException {
        this.adminSubject = adminSubject;
        this.subject = subject;
        this.applicationName = applicationName;
        this.resourceName = resourceName;
        this.envParameters = envParameters;
        entitlementCombiner = getApplication().getEntitlementCombiner();
        entitlementCombiner.init(applicationName, resourceName,
            actions, recursive);
        this.recursive = recursive;
        threadPool = new ThreadPool(); //TOFIX
    }

    /**
     * Returrns <code>true</code> if the subject has privilege to have the
     * given entitlement.
     *
     * @param adminSubject Administrator subject which is used for evcaluation.
     * @param subject Subject to be evaluated.
     * @param applicationName Application Name.
     * @param entitlement Entitlement to be evaluated.
     * @param envParameters Environment parameters.
     * @return <code>true</code> if the subject has privilege to have the
     * given entitlement.
     * @throws com.sun.identity.entitlement.EntitlementException if
     * evaluation fails.
     */
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

        indexes = entitlement.getResourceSearchIndexes();
        List<Entitlement> results = evaluate();
        Entitlement result = results.get(0);
        for (String action : entitlement.getActionValues().keySet()) {
            Boolean b = result.getActionValue(action);
            if ((b == null) || !b.booleanValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returrns list of entitlements which is entitled to a subject.
     *
     * @param adminSubject Administrator subject which is used for evcaluation.
     * @param subject Subject to be evaluated.
     * @param applicationName Application Name.
     * @param resourceName Resource name.
     * @param envParameters Environment parameters.
     * @param recursive <code>true</code> for sub tree evaluation.
     * @return <code>true</code> if the subject has privilege to have the
     * given entitlement.
     * @throws com.sun.identity.entitlement.EntitlementException if
     * evaluation fails.
     */
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
        indexes = getApplication().getResourceSearchIndex(resourceName);
        return evaluate();
    }

    private List<Entitlement> evaluate()
        throws EntitlementException {
        threadPool.submit(new EvaluationTask(this, recursive));

        synchronized (this) {
            boolean isDone = (eException != null);
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
                        Evaluator.debug.error("PrivilegeEvaluator.evaluate", ex);
                    }
                }
            }

            if (eException != null) {
                throw eException;
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
                        parent.subject), bSubTree, threadPool); i.hasNext();
                ) {
                    threadPool.submit(new PrivilegeTask(parent, i.next()));
                    count++;
                }
                parent.maxCounter = count;
                synchronized (parent) {
                    parent.notify();
                }
            } catch (EntitlementException ex) {
                parent.eException = ex;
                synchronized (parent) {
                    parent.notify();
                }
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
                    parent.envParameters, parent.recursive);
                synchronized(parent) {
                    parent.resultQ.add(entitlements);
                    parent.notify();
                }
            } catch (EntitlementException ex) {
                parent.eException = ex;
                synchronized (parent) {
                    parent.notify();
                }
            }
        }
    }
}

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
 * $Id: EvaluatorThread.java,v 1.7 2009-03-25 23:50:16 veiming Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.ThreadPool;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.policy.interfaces.ResourceName;
import com.sun.identity.shared.debug.Debug;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class get policy decision of a single resource. Since there can be
 * multiple policy evaluations, they are done is different threads.
 */
public class EvaluatorThread implements Runnable {
    protected PolicyEvaluatorAdaptor parent;
    private PolicyDecisionTask tasks;
    private int counter;
    private SSOToken token;
    private ServiceType serviceType;
    private String resourceName;
    private Set<String> actionNames;
    private Map<String, Set<String>> envParameters;
    private ResourceName resComparator;
    protected Set<Policy> policies;
    protected Exception exception;
    private Set<PolicyDecisionTask.Task> policyEvalTasks;
    private boolean done;
    final Object lock = new Object();
    private long startTime;

    EvaluatorThread(
        PolicyEvaluatorAdaptor parent,
        Set<Policy> policies,
        SSOToken token,
        ServiceType serviceType,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) {
        this.parent = parent;
        this.token = token;
        this.serviceType = serviceType;
        this.envParameters = envParameters;
        this.resourceName = resourceName;

        this.policies = new HashSet<Policy>();
        if (policies != null) {
            this.policies.addAll(policies);
        }
        tasks = new PolicyDecisionTask();
        resComparator = serviceType.getResourceNameComparator();
        this.actionNames = serviceType.getActionNames();

        startTime = System.currentTimeMillis();
    }

    public void run() {
        policyEvalTasks = new HashSet<PolicyDecisionTask.Task>();

        for (Policy p : policies) {
            PolicyDecisionTask.Task task = tasks.addTask(
                resComparator, p, resourceName);
            policyEvalTasks.add(task);
        }
        counter = policies.size();

        if (counter > 0) {
            for (PolicyDecisionTask.Task task : policyEvalTasks) {
                Runner eval = new Runner(this,
                    task, token, serviceType, actionNames,
                    envParameters);
                ThreadPool.submit(eval);
            }

            synchronized (lock) {
                if (counter > 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        //TOFIX
                        }
                }
            }
        }
        done = true;
        synchronized (parent.lock) {
            parent.lock.notify();
        }

        Debug.getInstance("dennist").error("dennis thr " + Long.toString(System.currentTimeMillis() - startTime));
    }

    Exception getException() {
        return exception;
    }

    Set<PolicyDecisionTask.Task> getPolicyDecisions() {
        return (done) ? policyEvalTasks : null;
    }

    private class Runner implements Runnable {
        private EvaluatorThread parent;
        private PolicyDecisionTask.Task task;
        private SSOToken token;
        private ServiceType serviceType;
        private Set<String> actionNames;
        private Map<String, Set<String>> envParameters;

        private Runner (
            EvaluatorThread parent,
            PolicyDecisionTask.Task task,
            SSOToken token,
            ServiceType serviceType,
            Set<String> actionNames,
            Map<String, Set<String>> envParameters) {
            this.parent = parent;
            this.token = token;
            this.task = task;
            this.serviceType = serviceType;
            this.actionNames = actionNames;
            this.envParameters = envParameters;
        }

        public void run() {
            try {
                long start = System.currentTimeMillis();
                task.policyDecision = task.policy.getPolicyDecision(
                    token, serviceType.getName(), task.resource,
                    actionNames, envParameters);
                Debug.getInstance("dennispe").error(
                    "dennisp " + Long.toString(System.currentTimeMillis() - start));
            } catch (SSOException e) {
                parent.exception = e;
            } catch (PolicyException e) {
                parent.exception = e;
            }
            synchronized (parent.lock) {
                parent.counter--;
                if (parent.counter == 0) {
                    parent.lock.notify();
                }
            }
        }
    }
}

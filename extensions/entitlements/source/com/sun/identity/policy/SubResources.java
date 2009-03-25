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
 * $Id: SubResources.java,v 1.14 2009-03-25 06:42:54 veiming Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.ThreadPool;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import com.sun.identity.entitlement.util.ResourceNameSplitter;
import com.sun.identity.policy.interfaces.ResourceName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class gets policy decisions for a set of resources in a resource
 * tree.
 */
public class SubResources implements Runnable {
    private Set<String> resources;
    private PolicyDecisionTask tasks;
    private Set<PolicyDecisionTask.Task> policyEvalTasks;
    private Map<String, Set<PolicyDecisionTask.Task>> resToTasks;
    int tasksCount = 0;

    protected PolicyEvaluatorAdaptor parent;
    private SSOToken token;
    private ServiceType serviceType;
    private String rootResource;
    private Set<String> actionNames;
    private Map<String, Set<String>> envParameters;
    protected Set<Policy> policies;
    protected Exception exception;
    final Object lock = new Object();
    private boolean done;


    SubResources(
        PolicyEvaluatorAdaptor parent,
        SSOToken token,
        ServiceType serviceType,
        String rootResource,
        Set<String> actionNames,
        Map<String, Set<String>> envParameters,
        Set<Policy> policies
    ) {
        this.parent = parent;
        this.token = token;
        this.serviceType = serviceType;
        this.rootResource = rootResource;
        this.actionNames = actionNames;
        this.envParameters = envParameters;
        this.policies = policies;
    }

    Exception getException() {
        return exception;
    }

    Set<PolicyDecisionTask.Task> getResults() {
        return (done) ? policyEvalTasks : null;
    }

    public void run() {
        policyEvalTasks = new HashSet<PolicyDecisionTask.Task>();
        tasks = new PolicyDecisionTask();
        resToTasks = new HashMap<String, Set<PolicyDecisionTask.Task>>();
        ResourceName resComparator = serviceType.getResourceNameComparator();
        createResourceSet(rootResource, resComparator);
        
        for (String r : resources) {
            evaluateResource(resComparator, r);
        }
        
        if (!resToTasks.isEmpty()) {
            tasksCount = policyEvalTasks.size();

            synchronized (lock) {
                for (PolicyDecisionTask.Task task : policyEvalTasks) {
                    EvaluatorThread eval = new EvaluatorThread(
                        this, task, token, serviceType, actionNames,
                        envParameters);
                    ThreadPool.submit(eval);
                }

                while (tasksCount > 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        exception = ex;
                        tasksCount = 0;
                    }
                }
            }
        }
        done = true;
        synchronized(parent.lock) {
            parent.lock.notify();
        }
    }
    
    private void createResourceSet(
        String rootResource,
        ResourceName resComparator) {
        resources = new HashSet<String>();
        for (Policy policy : policies) {
            Set<String> ruleNames = policy.getRuleNames();
            for (String ruleName : ruleNames) {
                try {
                    Rule rule = policy.getRule(ruleName);
                    String res = rule.getResourceName();
                    ResourceMatch match = resComparator.compare(
                        rootResource, res, true);
                    
                    if (!match.equals(ResourceMatch.NO_MATCH) &&
                        !match.equals(ResourceMatch.SUPER_RESOURCE_MATCH)) {
                        resources.add(rule.getResourceName());
                    }
                } catch (PolicyException e) {
                    // ignore
                }
            }
        }
    }
    
    private Set<Policy> search(Set<String> hostIndexes, Set<String>pathIndexes){
        Set<Policy> searchResults  = new HashSet<Policy>();
        IIndexCache cache = parent.getIndexCache();
        cache.getPolicies(hostIndexes, pathIndexes, null, searchResults, null);
        return searchResults;
    }

    private void evaluateResource(
        ResourceName resComparator,
        String resource
    ) {
        ResourceSearchIndexes comp = ResourceNameSplitter.split(resource);
        Set<Policy> searchResult = search(
            comp.getHostIndexes(), comp.getPathIndexes());
        if (!searchResult.isEmpty()) {
            Set<PolicyDecisionTask.Task> set = new HashSet();

            for (Policy p : searchResult) {
                PolicyDecisionTask.Task task = tasks.addTask(
                    resComparator, p, resource);
                set.add(task);
                policyEvalTasks.add(task);
            }
                
            if (!set.isEmpty()) {
                resToTasks.put(resource, set);
            }
        }
    }
    
    private class EvaluatorThread implements Runnable {
        private SubResources parent;
        private PolicyDecisionTask.Task task;
        private SSOToken token;
        private ServiceType serviceType;
        private Set<String> actionNames;
        private Map<String, Set<String>> envParameters;

        private EvaluatorThread(
            SubResources parent,
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
                task.policyDecision = task.policy.getPolicyDecision(
                    token, serviceType.getName(), task.resource,
                    actionNames, envParameters);
            } catch (SSOException e) {
                // TOFIX
            } catch (PolicyException e) {
                // TOFIX
            }
            parent.tasksCount--;

            synchronized (parent.lock) {
                parent.lock.notify();
            }
        }
    }
}

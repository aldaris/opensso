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
 * $Id: PrivilegeEvaluator.java,v 1.1 2009-03-25 06:42:51 veiming Exp $
 */
package com.sun.identity.entitlement;

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
    private int counter;
    private int maxCounter = -1;

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

        //separate threads, PIP
        indexes = entitlement.getResourceSearchIndexes();
        ThreadPool.submit(new EvaluationTask(this)); //TOFIX

        synchronized (this) {
            while (!resultQ.isEmpty()) {
                //combine entitlement;
            }
            if ((maxCounter != -1) && (maxCounter != counter)) {
                try {
                    wait();
                }
                catch (InterruptedException ex) {
                }
            }
        }

        //TOFIX
        return false;
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

        EvaluationTask(PrivilegeEvaluator parent) {
            this.parent = parent;
        }

        public void run() {
            try {
                int count = 0;
                IPolicyIndexDataStore ds =
                    PolicyIndexDataStoreFactory.getInstance().getDataStore();
                for (Iterator<Privilege> i = ds.search(parent.indexes);
                    i.hasNext();
                ) {
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

}

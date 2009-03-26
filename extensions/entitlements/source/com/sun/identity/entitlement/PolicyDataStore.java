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
 * $Id: PolicyDataStore.java,v 1.2 2009-03-26 22:50:10 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Iterator;

/**
 *
 * @author dennis
 */
public class PolicyDataStore implements IPolicyIndexDataStore {
    private static final String START_DN_TEMPLATE =
         "ou=default,ou=GlobalConfig,ou=1.0,ou=PolicyIndex,ou=services,{0}";
    private static final String DN_TEMPLATE = "ou={1}," + START_DN_TEMPLATE;

    private PolicyCache policyCache = new PolicyCache();
    private IndexCache indexCache = new IndexCache();

    public void add(Privilege p)
        throws EntitlementException {
        // figure out the dn of p
        // get ResourceSaveIndex
        //call indexCache.cache
        // call policyCache.cache
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(String name)
        throws EntitlementException {
        // figure out the dn of p
        // get ResourceSaveIndex
        //call indexCache.delete
        // call policyCache.delete
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<Privilege> search(ResourceSearchIndexes indexes)
        throws EntitlementException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

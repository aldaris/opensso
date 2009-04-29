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
 * $Id: IPolicyDataStore.java,v 1.5 2009-04-29 11:43:12 veiming Exp $
 */

package com.sun.identity.entitlement.interfaces;

import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import com.sun.identity.entitlement.util.PrivilegeSearchFilter;
import java.util.Iterator;
import java.util.Set;

/**
 * This interface defines the methods required to store policy indexes
 * in datastore.
 */
public interface IPolicyDataStore {

    /**
     * Adds a privilege to the data store. Proper indexes will be created
     * to speed up policy evaluation.
     *
     * @param privilege Privilege to be added.
     * @throws com.sun.identity.entitlement.EntitlementException if addition
     * failed.
     */
    void add(Privilege privilege) throws EntitlementException;

    /**
     * Deletes a privilege from data store.
     *
     * @param privilege Privilege to be deleted.
     * @throws com.sun.identity.entitlement.EntitlementException if deletion
     * failed.
     */
    void delete(Privilege privilege)
        throws EntitlementException;

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
    Iterator<Privilege> search(
        ResourceSearchIndexes indexes,
        Set<String> subjectIndexes,
        boolean bSubTree,
        IThreadPool threadPool
    ) throws EntitlementException;

//TOFIX
    Set<String> searchPrivilegeNames(
        Set<PrivilegeSearchFilter> filters, boolean boolAnd,
        int numOfEntries, boolean sortResults, boolean ascendingOrder);
}

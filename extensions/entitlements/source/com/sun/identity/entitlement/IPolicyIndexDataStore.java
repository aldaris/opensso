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
 * $Id: IPolicyIndexDataStore.java,v 1.3 2009-01-22 07:54:45 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.io.Serializable;
import java.util.Set;

/**
 * This interface defines the methods required to store policy indexes
 * in datastore.
 */
public interface IPolicyIndexDataStore {
    /**
     * Adds an index entry.
     * 
     * @param name Name of the entry to be added.
     * @param hostIndex Host index.
     * @param pathIndex Path index.
     * @param policy Policy Object.
     * @throws EntitlementException if the entry already exists.
     */
    void add(
        String name, 
        Set<String> hostIndex, 
        Set<String> pathIndex, 
        Serializable policy
    ) throws EntitlementException;
    
    /**
     *  Delete an idex entry.
     * 
     * @param name Name of the entry to be deleted.
     * @throws EntitlementException if deletion fails.
     */
    void delete(String name)
        throws EntitlementException;

    /**
     * Searches for policy objects.
     * 
     * @param hostIndexes Set of Host indexes.
     * @param pathIndexes Set of Path indexes.
     * @return a set of matching policy objects.
     * @throws EntitlementException if search operation fails.
     */
    Set<Object> search(Set<String> hostIndexes, Set<String> pathIndexes)
        throws EntitlementException;
}

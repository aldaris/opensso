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
 * $Id: IIndexCache.java,v 1.1 2009-02-10 19:31:03 veiming Exp $
 */

package com.sun.identity.policy;

import java.util.Set;

/**
 *
 * @author dennis
 */
public interface IIndexCache {
    /**
     * Returns policies associated with a host index.
     *
     * @param idx host index
     * @return Set of policies associated with this index.
     */
    Set<Policy> getHostIndex(String idx);

    /**
     * Returns policies associated with a path index.
     *
     * @param idx path index
     * @return Set of policies associated with this index.
     */
    Set<Policy> getPathIndex(String idx);

    /**
     * Returns policies associated with a path parent index.
     *
     * @param idx path parent index
     * @return Set of policies associated with this index.
     */
    Set<Policy> getPathParentIndex(String idx);
}

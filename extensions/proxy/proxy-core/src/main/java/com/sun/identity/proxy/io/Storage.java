/* The contents of this file are subject to the terms
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
 * $Id: Storage.java,v 1.1 2009-10-15 07:07:58 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.IOException;

/**
 * Provides a facility to store and access record objects.
 *
 * @author Paul C. Bryan
 */
public interface Storage
{
    /**
     * Allocates a new empty record, and returns its identifier.
     *
     * @return an identifier unique to the store for the newly created record.
     * @throws IOException if an I/O exception occurs.
     */
    public String create() throws IOException;

    /**
     * Opens a record with the specified identifier. A record is closed by
     * calling its <tt>close</tt> method. If a record does not exist with
     * the specified identifier, <tt>null</tt> is returned.
     *
     * @param id the record identifier.
     * @return the matching record, or <tt>null</tt> if not found.
     * @throws IOException if an I/O exception occurs.
     */
    public Record open(String id) throws IOException;

    /**
     * Returns whether a record with the specified identifier exists.
     *
     * @param id the record identifier.
     * @return <tt>true</tt> if record exists.
     */
    public boolean exists(String id) throws IOException;

    /**
     * Removes a record with the specified identifier. If a record with
     * the specified identifier does not exist, then calling this method
     * has no effect.
     *
     * @param id the record identifier.
     */
    public void remove(String id) throws IOException;
}


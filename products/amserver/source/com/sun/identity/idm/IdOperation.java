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
 * $Id: IdOperation.java,v 1.1 2005-11-01 00:31:08 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm;

/* iPlanet-PUBLIC-CLASS */
/**
 * The class <code>IdOperation</code> defines the types of operations
 * supported on managed identities, and provides static constants for these
 * operation. Currently defined operations on objects are
 * <code>IdOperation.READ</code>, <code>
 * IdOperation.EDIT</code>, <code>
 * IdOperation.CREATE</code>,
 * <code> IdOperation.DELETE
 * </code> and <code> IdOperation.SERVICE </code>.
 * The usage of the respective operations are defined along with their
 * declaration.
 */

public class IdOperation {

    private String op;

    public IdOperation(String operation) {
        op = operation;
    }

    /**
     * The <code> READ </code> operation is supported by default for all
     * supported identities for all the plugins. This operation means that the
     * <code> IdRepo SPI </code> for the configured plugins related to reading
     * identity attributes will be invoked.
     */
    public static final IdOperation READ = new IdOperation("read");

    /**
     * The <code> EDIT </code> operation is supported only for the plugins
     * configured for modifying and deleting attributes from the supported
     * identities. This means that the <code> IdRepo SPI
     * </code> for the
     * configured plugins will be called for all modify attribute operations.
     */

    public static final IdOperation EDIT = new IdOperation("edit");

    /**
     * The <code> CREATE </code> operation is supported only for the plugins
     * configured for creating identities. Not all the configured identities for
     * a given <code> IdRepo plugin </code> might be supported. It is possible
     * that a plugin might support read operations on all <code> IdType </code>
     * but create operations only on the <code> IdType.USER </code>. In this
     * case the create operation for that plugin is only called for user
     * identities.
     */
    public static final IdOperation CREATE = new IdOperation("create");

    /**
     * The <code> DELETE </code> operation is supported only for the plugins
     * configured for creating identities. Not all the configured identities for
     * a given <code> IdRepo plugin </code> might be supported. It is possible
     * that a plugin might support read operations on all <code> IdType </code>
     * but create or delete operations only on the <code> IdType.USER </code>.
     * In this case the delete operation for that plugin is only called for user
     * identities.
     */

    public static final IdOperation DELETE = new IdOperation("delete");

    /**
     * The <code> SERVICE </code> operation is supported only for service
     * related functions on an identity. Not all the configured identities for a
     * plugin might support services for all identities. It is possible that
     * service operations are supported only for one identity type for a plugin,
     * say <code> IdType.USER </code>. In this case, all service related
     * operations like assignService, unassignService, modifyService etc. are
     * only called for user objects for that plugin.
     */
    public static final IdOperation SERVICE = new IdOperation("service");

    public boolean equals(Object opObject) {
        if (opObject instanceof IdOperation) {
            return (((IdOperation) opObject).op.equalsIgnoreCase(this.op));
        }
        return (false);
    }

    public String toString() {
        return ("Operation: " + op);
    }

    /**
     * Returns the hash code of the object
     */
    public int hashCode() {
        return op.hashCode();
    }

    public String getName() {
        return op;
    }

}

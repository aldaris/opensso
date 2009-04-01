/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: PrivilegeManager.java,v 1.3 2009-04-01 00:21:29 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import java.util.Set;
import javax.security.auth.Subject;

/**
 * Class to manage entitlement privileges: to add, remove, modify privilege
 */
public abstract class PrivilegeManager {

    /**
     * Returns instance of configured <code>PrivilegeManager</code>
     * @param subject subject that would be used for the privilege management 
     * operations
     * @return instance of configured <code>PrivilegeManager</code>
     */
    static public PrivilegeManager getInstance(Subject subject) {
        PrivilegeManager pm = null;
        try {
            //TODO: read the class name from configuration
            pm = (PrivilegeManager) Class.forName(
                    "com.sun.identity.policy.PolicyPrivilegeManager").
                    newInstance();
            pm.initialize(subject);
        } catch (ClassNotFoundException cnfe) {
            //TODO: add debug
        } catch (InstantiationException ie) {
            //TODO: add debug
        } catch (IllegalAccessException iae) {
            //TODO: add debug
        }
        return pm;
    }

    public PrivilegeManager() {
    }

    /**
     * Initializes the object
     * @param subject subject to initilialize the privilege manager with
     */
    public abstract void initialize(Subject subject);

    /**
     * Returns a privilege
     * @param privilegeName name for the privilege to be returned
     * @throws com.sun.identity.entitlement.EntitlementException if there is
     * privilege could not be
     */
    public abstract Privilege getPrivilege(String privilegeName)
            throws EntitlementException;

    /**
     * Adds a privilege
     * @param privilege privilege to be added
     * @throws com.sun.identity.entitlement.EntitlementException if the 
     * privilege could not be added
     */
    public void addPrivilege(Privilege privilege)
            throws EntitlementException {
    }

    /**
     * Removes a privilege
     * @param privilegeName name of the privilege to be removed
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public void removePrivilege(String privilegeName)
            throws EntitlementException {
    }

    /**
     * Modifies a privilege
     * @param privilege the privilege to be modified
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public void modifyPrivilege(Privilege privilege)
            throws EntitlementException {
    }

    /**
     * Returns privilege names
     * @return privilege names
     * @throws com.sun.identity.entitlement.EntitlementException if there
     * is an error
     */
    public abstract Set<String> getPrivilegeNames() throws EntitlementException;

        /**
     * Returns privilege names matching the pattern
     * @param pattern pattern to match the privilege names
     * @return privilege names matching the pattern
     * @throws com.sun.identity.entitlement.EntitlementException if there
     * is an error
     */
    public abstract Set<String> getPrivilegeNames(String pattern)
            throws EntitlementException;
}

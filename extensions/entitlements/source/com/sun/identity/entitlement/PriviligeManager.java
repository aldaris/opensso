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
 * $Id: PriviligeManager.java,v 1.1 2009-01-12 22:08:38 dillidorai Exp $
 */
package com.sun.identity.entitlement;

/**
 * Class to manage entitlement priviliges: to add, remove, modify privilige
 */
public class PriviligeManager {

    /**
     * Adds a privilige
     * @param privilige privilige to be added
     * @throws com.sun.identity.entitlement.EntitlementException if the 
     * privilige could not be added
     */
    public void addPrivilige(Privilige privilige)
            throws EntitlementException {
    }

    /**
     * Removes a privilige
     * @param priviligeName name of the privilige to be removed
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public void removePrivilige(String priviligeName)
            throws EntitlementException {
    }

    /**
     * Modifies a privilige
     * @param privilige the privilige to be modified
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public void modifyPrivilige(Privilige privilige)
            throws EntitlementException {
    }
}

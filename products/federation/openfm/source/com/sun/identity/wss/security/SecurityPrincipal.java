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
 * $Id: SecurityPrincipal.java,v 1.1 2007-03-23 00:02:03 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.security;

import  java.security.Principal;


/**
 * This class <code>SecurityPrincipal</code> exposes the authenticated
 * principal via the message level security.
 * @supported.all.api
 */
public class SecurityPrincipal implements Principal {

    private String name;

    /**
     * Default Constructor
     * @param name the name of the principal
     */
    public SecurityPrincipal(String name) {
        this.name = name;
    }

    /**
     * Compares with given object.
     * @return false if the given object is not equal to this principal
     */
    public boolean equals(Object o) {
        return false;
    }

    /**
     * Returns the name of the principal.
     * @return the name of the principal.
     */
    public String getName() {
        return name;
    }

    /**
     * Converts to string.
     * @return String the principal name
     */
    public String toString() {
       return name;
    }
}

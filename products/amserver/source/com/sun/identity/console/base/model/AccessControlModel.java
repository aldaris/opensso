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
 * $Id: AccessControlModel.java,v 1.1 2006-11-30 00:44:43 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.console.base.model;

import java.util.Set;

/**
 * This interface defines the method for view access of a tab in 
 * Administration Console.
 */
public interface AccessControlModel {
    String ANY_SERVICE = "ANY_SERVICE";

    /**
     * Returns true if a page can be viewed.
     *
     * @param permissions Permissions associated to the page.
     * @param accessLevel Level of access i.e. either global or realm level.
     * @param realmName Currently view realm Name.
     * @param delegateUI true if this is a delegation administration page.
     * @return true if a page can be viewed.
     */
    boolean canView(
	Set permissions,
	String accessLevel,
	String realmName,
	boolean delegateUI);
}

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
 * $Id: AMAdminConstants.java,v 1.2 2006-11-30 00:44:43 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base.model;

/**
 * This interface contains a set of constants used by console classes.
 */
public interface AMAdminConstants {
    /**
     * Console Debug file name
     */
    String CONSOLE_DEBUG_FILENAME = "jsfConsole";

    /**
     * Default resource bundle name
     */
    String DEFAULT_RESOURCE_BUNDLE = "jsfConsole";

    /**
     * Login URL
     */
    String URL_LOGIN = "/UI/Login";

    /**
     * Logout URL
     */
    String URL_LOGOUT = "/UI/Logout";

    /**
     * Read permission.
     */
    String PERMISSION_READ = "READ";

    /**
     * Write permission.
     */
    String PERMISSION_MODIFY = "MODIFY";

    /**
     * Delegation permission.
     */
    String PERMISSION_DELEGATE = "DELEGATE";

    /**
     * Adminstration Console service name.
     */
    String ADMIN_CONSOLE_SERVICE = "iPlanetAMAdminConsoleService";

    /**
     * Federation Managemenet Enable Attribute Name. This attribute is found in
     * Administration Console Service.
     */
    String CONSOLE_FED_ENABLED_ATTR ="iplanet-am-admin-console-liberty-enabled";
}

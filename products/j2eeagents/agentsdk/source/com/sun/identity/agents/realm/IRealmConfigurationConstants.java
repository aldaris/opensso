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
 * $Id: IRealmConfigurationConstants.java,v 1.1 2006-09-29 00:05:33 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.realm;

/**
 * The interface for defining agent realm configuration constants
 */
public interface IRealmConfigurationConstants {
    
    public static final String CONFIG_FILTERED_ROLES_ENABLED =
        "filtered.roles.enable";
    
    public static final String CONFIG_FETCH_TYPE = 
        "privileged.attribute.type";
    
    public static final String CONFIG_PRIVILEGED_ATTR_CASE =
            "privileged.attribute.tolowercase";
    
    public static final String CONFIG_BYPASS_USER_LIST =
        "bypass.principal";

    public static final String CONFIG_PRIVILEGED_ATTRIBUTE_MAPPING_ENABLED =
        "privileged.attribute.mapping.enable";

    public static final boolean DEFAULT_PRIVILEGED_ATTRIBUTE_MAPPING_ENABLED =
        false;

    public static final String CONFIG_PRIVILEGED_ATTRIBUTE_MAPPING =
        "privileged.attribute.mapping";
    
    public static final String CONFIG_VERIFICATION_HANDLERS =
        "verification.handler";
    
    public static final String CONFIG_DEFAULT_PRIVILEGE_ATTR_LIST =
        "default.privileged.attribute";
    
    public static final String CONFIG_PRIVILEGED_SESSION_ATTR_LIST =
        "privileged.session.attribute";
    
    public static final int DEFAULT_FETCH_LEVEL = 1; 
    
    public static final boolean DEFAULT_FILTERED_ROLES_ENABLED = false;

}

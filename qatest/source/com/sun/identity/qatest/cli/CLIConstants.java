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
 * $Id: CLIConstants.java,v 1.1 2007-05-31 19:39:31 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.cli;

/**
 * <code>CLIConstants</code> contains strings for the supported 
 * sub-commands of the fmadm CLI.
 */
public interface CLIConstants {
    /**
     * String for the "create-realm" sub-command
     */
    public static final String CREATE_REALM_SUBCOMMAND = "create-realm";
    
    /**
     * String for the "list-realms" sub-command
     */
    public static final String LIST_REALMS_SUBCOMMAND = "list-realms";
    
    /**
     * String for the "delete-realm" sub-command
     */
    public static final String DELETE_REALM_SUBCOMMAND = "delete-realm";
    
    /**
     * String for the "create-identity" sub-command
     */
    public static final String CREATE_IDENTITY_SUBCOMMAND = "create-identity";
    
    /**
     * String for the "list-identities" sub-command
     */
    public static final String LIST_IDENTITIES_SUBCOMMAND = "list-identities";
    
    /**
     * String for the "delete-identities" sub-command
     */
    public static final String DELETE_IDENTITIES_SUBCOMMAND = 
            "delete-identities";
    
    /**
     * String for the "add-member" sub-command
     */
    public static final String ADD_MEMBER_SUBCOMMAND = "add-member";
    
    /**
     * String for the "remove-member" sub-command
     */
    public static final String REMOVE_MEMBER_SUBCOMMAND = "remove-member";
    
    /**
     * String for the "show-members" sub-command
     */
    public static final String SHOW_MEMBERS_SUBCOMMAND = "show-members";
    
    /**
     * String for the "show-memberships" sub-command
     */
    public static final String SHOW_MEMBERSHIPS_SUBCOMMAND = "show-memberships";
    
    /**
     * String for the "add-attribute-defaults" sub-command
     */
    public static final String ADD_ATTRIBUTE_DEFAULTS_SUBCOMMAND = 
            "add-attribute-defaults";
    
    /**
     * String for the "add-attributes" sub-command
     */
    public static final String ADD_ATTRIBUTES_SUBCOMMAND = "add-attributes";
    
    /**
     * String for the "add-circle-of-trust-member" sub-command
     */
    public static final String ADD_CIRCLE_OF_TRUST_MEMBER_SUBCOMMAND = 
            "add-circle-of-trust-member";
    
    /**
     * String for the "add-plugin-interface" sub-command
     */
    public static final String ADD_PLUGIN_INTERFACE_SUBCOMMAND = 
            "add-plugin-interface";
    
    /**
     * String for the "add-realm-attributes" sub-command
     */
    public static final String ADD_REALM_ATTRIBUTES_SUBCOMMAND = 
            "add-realm-attributes";
    
    /**
     * String for the "add-resource-bundle" sub-command
     */
    public static final String ADD_RESOURCE_BUNDLE_SUBCOMMAND = 
            "add-resource-bundle";
    
    /**
     * String for the "add-service-identity" sub-command
     */
    public static final String ADD_SERVICE_IDENTITY_SUBCOMMAND = 
            "add-service-idenity";
    
    /**
     * String for the "add-sub-schema" sub-command
     */
    public static final String ADD_SUB_SCHEMA_SUBCOMMAND = "add-sub-schema";
    
    /**
     * String for the "create-auth-configuration" sub-command
     */
    public static final String CREATE_AUTH_CONFIGURATION_SUBCOMMAND = 
            "create-auth-configuration";
    
    /**
     * String for the "create-auth-instance" sub-command
     */
    public static final String CREATE_AUTH_INSTANCE_SUBCOMMAND = 
            "create-auth-instance";
    
    /**
     * String for the "create-circle-of-trust" sub-command
     */
    public static final String CREATE_CIRCLE_OF_TRUST_SUBCOMMAND = 
            "create-circle-of-trust";
    
    /**
     * String for the "create-datastore" sub-command
     */
    public static final String CREATE_DATASTORE_SUBCOMMAND = "create-datastore";
    
    /**
     * String for the "create-metadata-template" sub-command
     */
    public static final String CREATE_METADATA_TEMPLATE_SUBCOMMAND = 
            "create-metadata-template";
    
    /**
     * String for the "create-policies" sub-command
     */
    public static final String CREATE_POLICIES_SUBCOMMAND = "create-policies";
    
    /**
     * String for the "create-service" sub-command
     */
    public static final String CREATE_SERVICE_SUBCOMMAND = "create-service";
    
    /**
     * String for the "create-serverconfig-xml" sub-command
     */
    public static final String CREATE_SERVERCONFIG_XML_SUBCOMMAND = 
            "create-serverconfig-xml";
    
    /**
     * String for the "create-subconfiguration" sub-command
     */
    public static final String CREATE_SUB_CONFIGURATION_SUBCOMMAND = 
            "create-sub-configuration";
    
    /**
     * String for the "show-identity-types" sub-command
     */
    public static final String SHOW_IDENTITY_TYPES_SUBCOMMAND = 
            "show-identity-types";    
}

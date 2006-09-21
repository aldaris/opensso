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
 * $Id: AccessManager.java,v 1.3 2006-09-20 23:59:55 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.definition;


import com.sun.identity.cli.annotation.DefinitionClassInfo;
import com.sun.identity.cli.annotation.Macro;
import com.sun.identity.cli.annotation.SubCommandInfo;
import com.sun.identity.cli.annotation.ResourceStrings;

public class AccessManager {
    @DefinitionClassInfo(
        productName="Sun Java System Access Manager",
        version="8.0",
        resourceBundle="AccessManagerCLI")
    private String product;

    @ResourceStrings(
        string="resourcebundle-not-found=Resource Bundle not found.\nmissing-attributevalues=attributevalues and datafile options are missing.\nmissing-choicevalues=choicevalues and datafile options are missing."
    )
    private String resourcestrings;

    @Macro(
        mandatoryOptions="adminid|u|s|Administrator ID of running the command.@password|w|s|Password of administrator.@password-file|f|s|File name that contains password of administrator.",
        optionalOptions="",
        optionAliases="password|password-file")
    private String authentication;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.BulkOperations",
        description="Do multiple requests in one command.",
        mandatoryOptions="datafile|D|s|Name of file that contains commands and options.",
        optionAliases="",
        macro="authentication",
        optionalOptions="continue|c|u|Continue processing the rest of the request when preceeding request is errornous.",
        resourceStrings="bulk-op-empty-datafile=Data file, {0} is empty.\nunmatch-quote=Unmatched '.\nunmatch-doublequote=Unmatched \".")
    private String do_batch;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.SessionCommand",
        description="List Sessions.",
        mandatoryOptions="host|t|s|Host Name.",
        optionAliases="",
        macro="authentication",
        optionalOptions="filter|x|s|Filter (Pattern).@quiet|q|s|Do not prompt for session invalidation.",
        resourceStrings="session-invalid-host-name=Invalid Host Name {0}. Expected format is <protocol>://<host>:<port>.\nsizeLimitExceeded=Search size limit exceeded. Please refine your search.\ntimeLimitExceeded=Search time limit exceeded. Please refine your search.\nsession-no-sessions=There are no valid sessions.\nsession-current-session=[Current Session]\nsession-index=Index:\nsession-userId=User Id:\nsession-time-remain=Time Remain:\nsession-max-session-time=Max Session Time:\n\nsession-idle-time=Idle Time:\nsession-max-idle-time=Max Idle Time:\nsession-to-invalidate=To invalidate sessions, enter the index numbers\nsession-cr-to-exit=[CR without a number to exit]:\nsession-selection-not-in-list=Your selection is not in the session list.\nsession-io-exception-reading-input=IO Exception reading input:\nsession-destroy-session-succeeded=Destroy Session Succeeded.")
    private String list_sessions;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.AddResourceBundle",
        description="Add resource bundle to data store.",
        mandatoryOptions="bundlename|b|s|Resource Bundle Name.@bundlefilename|B|s|Resource bundle physical file name.",
        optionAliases="",
        macro="authentication",
        optionalOptions="bundlelocale|o|s|Locale of the resource bundle.",
        resourceStrings="resourcebundle-added=Resource Bundle is added.")
    private String add_resource_bundle;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.GetResourceBundle",
        description="List resource bundle in data store.",
        mandatoryOptions="bundlename|b|s|Resource Bundle Name.",
        optionAliases="",
        macro="authentication",
        optionalOptions="bundlelocale|o|s|Locale of the resource bundle.",
        resourceStrings="resourcebundle-returned=Resource Bundle is returned.")
    private String list_resource_bundle;
    
 
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.DeleteResourceBundle",
        description="Remove resource bundle from data store.",
        mandatoryOptions="bundlename|b|s|Resource Bundle Name.",
        optionAliases="",
        macro="authentication",
        optionalOptions="bundlelocale|o|s|Locale of the resource bundle.",
        resourceStrings="resourcebundle-deleted=Resource Bundle is deleted.")
    private String remove_resource_bundle;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.LoadSchema",
        description="Create a new service in server.",
        mandatoryOptions="xmlfile|X|m|XML file(s) that contains schema.",
        optionAliases="",
        macro="authentication",
        optionalOptions="continue|c|u|Continue adding service if one or more previous service cannot be added.",
        resourceStrings="schema-added=Service is added.\nschema-failed=Service is not added.")
    private String create_service;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.DeleteService",
        description="Delete service from the server.",
        mandatoryOptions="servicename|s|m|Service Name(s).",
        optionAliases="",
        macro="authentication",
        optionalOptions="continue|c|u|Continue deleting service if one or more previous services cannot be deleted.@deletepolicyrule|r|u|Delete policy rule.",
        resourceStrings="service-deleted=Service is deleted.\nservice-deletion-failed=Service is not deleted.\ndelete-service-no-policy-rules=There are no policy rules.\ndelete-service-no-policy-schema=There are no policy schema.\ndelete-service-delete-policy-rules=Delete policy rules.")
    private String delete_service;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.UpdateService",
        description="Update service.",
        mandatoryOptions="xmlfile|X|m|XML file(s) that contains schema.",
        optionAliases="",
        macro="authentication",
        optionalOptions="continue|c|u|Continue updating service if one or more previous services cannot be updated.",
        resourceStrings="service-updated=Schema is updated.\nservice-updated-failed=Schema is not updated.")
    private String update_service;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.AddAttributeSchema",
        description="Add attribute schema to an existing service.",
        mandatoryOptions="servicename|s|s|Service Name.@schematype|t|s|Schema Type.@attributeschemafile|F|m|XML file containing attribute schema definition.",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-added=Attribute schema is added.\nadd-attribute-schema-failed=Attribute schema is not added.")
    private String add_attribute;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.SMSMigration",
        description="Migrate organization to realm.",
        mandatoryOptions="entrydn|e|s|Distinguished name of organization to be migrated.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="sms-migration-succeed=Migration succeeded.")
    private String do_migration70;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.CreateRealm",
        description="Create realm.",
        mandatoryOptions="realm|e|s|Name of realm to be created.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="create-realm-succeed=Realm is created.")
    private String create_realm;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.DeleteRealm",
        description="Delete realm.",
        mandatoryOptions="realm|e|s|Name of realm to be deleted.",
        optionAliases="",
        macro="authentication",
        optionalOptions="recursive|r|u|Delete descendent realms recursively.",
        resourceStrings="delete-realm-succeed=Realm is deleted.")
    private String delete_realm;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.SearchRealms",
        description="List realms by name.",
        mandatoryOptions="realm|e|s|Name of realm where search begins.",
        optionAliases="",
        macro="authentication",
        optionalOptions="pattern|p|s|Filter pattern.@recursive|r|u|Search recursively",
        resourceStrings="search-realm-succeed=Search completed.\nsearch-realm-no-results=There are no realms.\nsearch-realm-results={0}")
    private String list_realms;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmAssignService",
        description="Add service to a realm.",
        mandatoryOptions="realm|e|s|Name of realm.@servicename|s|s|Service Name.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.",
        resourceStrings="assign-service-to-realm-succeed=Service, {1} is added to realm, {0}.")
    private String add_service_realm;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmGetAssignedServices",
        description="Show services in a realm.",
        mandatoryOptions="realm|e|s|Name of realm.",
        optionAliases="",
        macro="authentication",
        optionalOptions="mandatory|y|u|Include Mandatory services.",
        resourceStrings="realm-get-assigned-services-succeed=Services are returned.\nrealm-get-assigned-services-no-services=There no services in this realm.\nrealm-get-assigned-services-results={0}")
    private String show_realm_services;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmGetAssignableServices",
        description="List the assignable services to a realm.",
        mandatoryOptions="realm|e|s|Name of realm.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="realm-getassignable-services-succeed=Assignable Services are returned.\nrealm-getassignable-services-no-services=There no assignable services for this realm.\nrealm-getassignable-services-result={0}")
    private String list_realm_assignable_services;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmUnassignService",
        description="Remove service from a realm.",
        mandatoryOptions="realm|e|s|Name of realm.@servicename|s|s|Name of service to be removed.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="unassign-service-from-realm-succeed=Service, {1} is removed from realm, {0}.\nunassign-service-from-realm-service-not-assigned=Service, {1} is not added to realm, {0}.")
    private String remove_service_realm;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmGetAttributeValues",
        description="Get realm property values.",
        mandatoryOptions="realm|e|s|Name of realm.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="get-attr-values-of-realm-succeed=Attribute values is returned.\nget-attr-values-of-realm-no-values=There are no attribute values.\nget-attr-values-of-realm-result={0}={1}")
    private String get_realm;
    
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmGetServiceAttributeValues",
        description="Show realm's service attribute values.",
        mandatoryOptions="realm|e|s|Name of realm.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="get-service-attr-values-of-realm-succeed=Service attribute values is returned.\nget-service-attr-values-of-realm-result={0}={1}")
    private String show_realm_service_attributes;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmRemoveAttribute",
        description="Delete attribute from a realm.",
        mandatoryOptions="realm|e|s|Name of realm.@servicename|s|s|Name of service.@attributename|a|s|Name of attribute to be removed.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="remove-attribute-from-realm-succeed=Attribute is removed.")
    private String delete_realm_attribute;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmModifyService",
        description="Set service attribute values in a realm.",
        mandatoryOptions="realm|e|s|Name of realm.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.",
        resourceStrings="modify-service-of-realm-succeed=Service, {0} under {1} is modified.\nmodify-service-of-realm-not-assigned=Service, {1} is not modified because it is not added to {0}.")
    private String set_service_attribute;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmAddAttributeValues",
        description="Add attribute value to a realm.",
        mandatoryOptions="realm|e|s|Name of realm.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.",
        resourceStrings="add-attribute-values-realm-succeed=Attribute values are added.")
    private String add_realm_attributes;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmSetAttributeValues",
        description="Set attribute values of a realm.",
        mandatoryOptions="realm|e|s|Name of realm.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.",
        resourceStrings="set-attribute-values-realm-succeed=Attribute values are set.")
    private String set_realm_attributes;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmCreatePolicy",
        description="Create policies in a realm.",
        mandatoryOptions="realm|e|s|Name of realm.@xmlfile|X|s|Name of file that contains policy XML definition.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="create-policy-in-realm-succeed=Policies are created under realm, {0}.")
    private String create_policies;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmDeletePolicy",
        description="Delete policies from a realm.",
        mandatoryOptions="realm|e|s|Name of realm.@policynames|p|m|Names of policy to be deleted.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="delete-policy-in-realm-succeed=Policies are deleted under realm, {0}.")
    private String delete_policies;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.realm.RealmGetPolicy",
        description="List policy definitions in a realm.",
        mandatoryOptions="realm|e|s|Name of realm.",
        optionAliases="",
        macro="authentication",
        optionalOptions="policynames|p|m|Names of policy. This can be an wildcard. All policy definition in the realm will be returned if this option is not provided.@outfile|o|s|Filename where policy definition will be printed to. Definition will be printed in standard output if this option is not provided.",
        resourceStrings="get-policy-in-realm-succeed=Policy definitions are returned under realm, {0}.\nget-policy-in-realm-no-policies=There are not matching policies under realm, {0}.")
    private String list_policies;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.RemoveAttributeDefaults",
        description="Remove default attribute values in schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributenames|a|m|Attribute name(s).",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="schema-remove-attribute-defaults-succeed=Schema attribute defaults are removed.\nschema-sub-schema-does-not-exists=Sub Schema does not exist, {0}.\nsupported-schema-type=Unsupported Schema Type, {0}.")
    private String remove_attribute_defaults;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.AddAttributeDefaults",
        description="Add default attribute values in schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.@subschemaname|c|s|Name of sub schema.",
        resourceStrings="schema-add-attribute-defaults-succeed=Schema attribute defaults are added.")
    private String add_attribute_defaults;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.GetAttributeDefaults",
        description="Show default attribute values in schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.@attributenames|a|m|Attribute name(s).",
        resourceStrings="schema-get-attribute-defaults-succeed=Schema attribute defaults are returned.\nschema-get-attribute-defaults-no-matching-attr=There are no attribute values.\nschema-get-attribute-defaults-result={0}={1}")
    private String show_attribute_defaults;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetAttributeDefaults",
        description="Set default attribute values in schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.@attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.",
        resourceStrings="schema-set-attribute-defaults-succeed=Schema attribute defaults are set.")
    private String set_attribute_defaults;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetAttributeSchemaChoiceValues",
        description="Set choice values of attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributename|a|s|Name of attribute.",
        optionAliases="",
        macro="authentication",
        optionalOptions="add|p|b|Set this flag to append the choice values to existing ones.@subschemaname|c|s|Name of sub schema.@datafile|D|s|Name of file that contains attribute values data.@choicevalues|k|m|Choice value e.g. o102=Inactive.",
        resourceStrings="attribute-schema-set-choice-value-succeed=Choice Values are set.")
    private String set_attribute_choice_values;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetAttributeSchemaBooleanValues",
        description="Set boolean values of attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributename|a|s|Name of attribute.@truevalue|e|s|Value for true.@truei18nkey|k|s|Internationalization key for true value.@falsevalue|z|s|Value for false.@falsei18nkey|j|s|Internationalization key for false value.",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-set-boolean-values-succeed=Boolean Values are set.")
    private String set_attribute_boolean_values;
    
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.RemoveAttributeSchemaChoiceValues",
        description="Remove choice values from attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributename|a|s|Name of attribute.@choicevalues|k|m|Choice values e.g. Inactive",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-remove-choice-value-succeed=Choice Values are removed.")
    private String remove_attribute_choice_values;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.ModifyAttributeSchemaType",
        description="Set type member of attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@type|p|s|Attribute Schema Type",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-modify-type-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_type;
    
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.ModifyAttributeSchemaUIType",
        description="Set UI type member of attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@uitype|p|s|Attribute Schema UI Type",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-modify-ui-type-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_ui_type;

   @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.ModifyAttributeSchemaSyntax",
        description="Set syntax member of attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@syntax|x|s|Attribute Schema Syntax",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-modify-syntax-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_syntax;
   
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.ModifyAttributeSchemaI18nKey",
        description="Set i18nKey member of attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@i18nkey|k|s|Attribute Schema I18n Key",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-modify-i18n-key-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_i18n_key;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.ModifyAttributeSchemaPropertiesViewBeanURL",
        description="Set properties view bean URL member of attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@url|r|s|Attribute Schema Properties View Bean URL",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-modify-properties-view-bean-url-key-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_view_bean_url;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.ModifyAttributeSchemaAny",
        description="Set any member of attribute schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@any|y|s|Attribute Schema Any value",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-modify-any-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_any;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.RemoveAttributeSchemaDefaultValues",
        description="Delete attribute schema default values.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@defaultvalues|e|m|Default value(s) to be deleted",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-remove-default-values-succeed=Attribute Schema, {3} is modified.")
    private String delete_attribute_default_values;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetAttributeSchemaValidator",
        description="Set attribute schema validator.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@validator|r|s|validator class name",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-set-validator-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_validator;
    
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetAttributeSchemaStartRange",
        description="Set attribute schema start range.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@range|r|s|Start range",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-set-start-range-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_start_range;
    
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetAttributeSchemaEndRange",
        description="Set attribute schema end range.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|s|Name of attribute schema@range|r|s|End range",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="attribute-schema-set-end-range-succeed=Attribute Schema, {3} is modified.")
    private String set_attribute_end_range;
     
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.RemoveAttributeSchemas",
        description="Delete attribute schemas from a service",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@attributeschema|a|m|Name of attribute schema to be removed.",
        optionAliases="",
        macro="authentication",
        optionalOptions="subschemaname|c|s|Name of sub schema.",
        resourceStrings="remove-attribute-schema-succeed=Attribute schema, {3} is removed.")
    private String delete_attribute;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetServiceSchemaI18nKey",
        description="Set service schema i18n key.",
        mandatoryOptions="servicename|s|s|Name of service.@i18nkey|k|s|I18n Key.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="service-schema-set-i18n-key-succeed=Service Schema, {0} is modified.")
    private String set_service_i18n_key;

     @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetServiceSchemaPropertiesViewBeanURL",
        description="Set service schema properties view bean URL.",
        mandatoryOptions="servicename|s|s|Name of service.@url|r|s|Service Schema Properties View Bean URL",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="service-schema-set-properties-view-bean-url-succeed=Service Schema, {0} is modified.")
    private String set_service_view_bean_url;

     @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetServiceRevisionNumber",
        description="Set service schema revision number.",
        mandatoryOptions="servicename|s|s|Name of service.@revisionnumber|r|s|Revision Number",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="service-schema-set-revision-number-succeed=Service Schema, {0} is modified.")
    private String set_revision_number;
     
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.GetServiceRevisionNumber",
        description="Get service schema revision number.",
        mandatoryOptions="servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="service-schema-get-revision-number-succeed=Revision number of service {0} is {1}.")
    private String get_revision_number;
     
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.AddSubConfiguration",
        description="Create a new sub configuration.",
        mandatoryOptions="servicename|s|s|Name of service.@subconfigname|g|s|Name of sub configuration.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.@realm|e|s|Name of realm (Sub Configuration shall be added to global configuration if this option is not provided).@subconfigid|b|s|ID of parent configuration(Sub Configuration shall be added to root configuration if this option is not provided).",
        resourceStrings="add-sub-configuration-succeed=Sub Configuration, {0} is added.\nadd-sub-configuration-to-realm-succeed=Sub Configuration, {1} is added to realm, {0}")
    private String create_sub_configuration;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.DeleteSubConfiguration",
        description="Remove Sub Configuration.",
        mandatoryOptions="servicename|s|s|Name of service.@subconfigname|g|s|Name of sub configuration.",
        optionAliases="",
        macro="authentication",
        optionalOptions="realm|e|s|Name of realm (Sub Configuration shall be added to global configuration if this option is not provided).",
        resourceStrings="delete-sub-configuration-succeed=Sub Configuration, {0} is deleted.\ndelete-sub-configuration-to-realm-succeed=Sub Configuration, {1} is deleted from realm, {0}")
    private String delete_sub_configuration;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.ModifySubConfiguration",
        description="Set sub configuration.",
        mandatoryOptions="servicename|s|s|Name of service.@subconfigname|g|s|Name of sub configuration.@operation|o|s|Operation (either add/set/modify) to be performed on the sub configuration.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.@realm|e|s|Name of realm (Sub Configuration shall be added to global configuration if this option is not provided).",
        resourceStrings="modify-sub-configuration-succeed=Sub Configuration, {0} is modified.\nmodify-sub-configuration-to-realm-succeed=Sub Configuration, {1} is modify in realm, {0}\nmodify-sub-configuration-invalid-operation=Invalid operation, supported operation are 'add', 'delete' and 'set'.")
    private String set_sub_configuration;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.AddSubSchema",
        description="Add sub schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@filename|F|s|Name of file that contains the schema",
        optionAliases="",
        macro="authentication",
        optionalOptions="@subschemaname|c|s|Name of sub schema.",
        resourceStrings="add-subschema-succeed=Sub Schema, {2} of type, {1} is added to service {0}.")
    private String add_sub_schema;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.RemoveSubSchema",
        description="Remove sub schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@subschemanames|a|m|Name(s) of sub schema to be removed.",
        optionAliases="",
        macro="authentication",
        optionalOptions="@subschemaname|c|s|Name of parent sub schema.",
        resourceStrings="remove-subschema-succeed={3} of Sub Schema, {2} of type, {1} is removed from service {0}.")
    private String remove_sub_schema;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.ModifyInheritance",
        description="Set Inheritance value of Sub Schema.",
        mandatoryOptions="servicename|s|s|Name of service.@schematype|t|s|Type of schema.@subschemaname|c|s|Name of sub schema.@inheritance|r|s|Value of Inheritance.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="modify-inheritance-succeed=Inheritance of Sub Schema, {2} of type, {1} in service {0} is modified.")
    private String set_inheritance;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.AddPluginInterface",
        description="Add Plug-in interface to service.",
        mandatoryOptions="servicename|s|s|Name of service.@interfacename|i|s|Name of interface.@pluginname|g|s|Name of Plug-in.@i18nkey|k|s|Plug-in I18n Key.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="add-plugin-interface-succeed=Plug-in interface, {1} is add to service, {0}.")
    private String add_plugin_interface;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.schema.SetPluginSchemaPropertiesViewBeanURL",
        description="Set properties view bean URL of plug-in schema.",
        mandatoryOptions="servicename|s|s|Name of service.@interfacename|i|s|Name of interface.@pluginname|g|s|Name of Plug-in.@url|r|s|Properties view bean URL.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="set-properties-viewbean-url-plugin-schema-succeed=Properties View Bean of Plug-in schema, {1} of service, {0} is set.")
    private String set_plugin_schema_view_bean_url;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.CreateIdentity",
        description="Create identity in a realm",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="datafile|D|s|Name of file that contains the attribute values.",
        resourceStrings="create-identity-succeed=Identity, {2} of type {1} is created in realm, {0}.\nmulti-identity-failed=Multiple identities of name, {2} of type {1} in realm, {0} found.\nidentity-not-found=Cannot find identity of name, {2} of type {1} in realm, {0}.\ninvalid-identity-type=Invalid identity type.\ndoes-not-support-creation={0} does not support identity creation of type, {1}.")
    private String create_identity;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.DeleteIdentities",
        description="Delete identities in a realm",
        mandatoryOptions="realm|e|s|Name of realm.@idnames|i|m|Names of identites.@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="delete-identities-succeed=Identities of type {1} is deleted in realm, {0}.")
    private String delete_identities;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.SearchIdentities",
        description="List identities in a realm",
        mandatoryOptions="realm|e|s|Name of realm.@filter|x|s|Filter (Pattern).@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="recursive|r|u|Do recursive search.",
        resourceStrings="search-identities-succeed=Search of Identities of type {1} in realm, {0} succeed.\nformat-search-identities-results={0} ({1})")
    private String list_identities;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetAllowedIdOperations",
        description="Show the allowed operations of an identity a realm",
        mandatoryOptions="realm|e|s|Name of realm.@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="get-allowed-ops-succeed=Allowed operations of {1} in realm, {0} is printed.\nallowed-ops-result={0}")
    private String show_identity_operations;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetSupportedIdTypes",
        description="Show the supported identity type in a realm",
        mandatoryOptions="realm|e|s|Name of realm.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="get-supported-idtypes-succeed=Supported identity type in realm, {0} is printed.\nsupported-type-result={0}\nno-supported-idtype=There are no supported identity type.")
    private String show_identity_types;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetAssignableServices",
        description="List the assignable service to an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="get-assignable-services-succeed=Assignable services of identity, {2} of type, {1} in realm, {0} is printed.\nassignable-service-result={0}\nrealm-does-not-support-service=realm, {0} does not support services.\nno-service-assignable=There are no assignable services.")
    private String list_identity_assignable_services;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetAssignedServices",
        description="Show the service in an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="get-assigned-services-succeed=Services of identity, {2} of type, {1} in realm, {0} is printed.\nassigned-service-result={0}\nno-service-assigned=There are no services.")
    private String show_identity_services;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetServiceAttributes",
        description="Show the service attribute values of an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="get-service-attributes-succeed={3}, services attribute values of identity, {2} of type, {1} in realm, {0} is printed.\nidrepo-service-attribute-result={0}={1}\nidrepo-no-service-attributes=There are no service attribute values.")
    private String show_identity_service_attributes;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetAttributes",
        description="Get identity property values",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="@attributenames|a|m|Attribute name(s). All attribute values shall be returned if the option is not provided.",
        resourceStrings="idrepo-get-attributes-succeed=Attribute values of identity, {2} of type, {1} in realm, {0} is printed.\nidrepo-attribute-result={0}={1}\nidrepo-no-attributes=There are no attribute values.")
    private String get_identity;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetMemberships",
        description="Show the memberships of an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.@membershipidtype|m|s|Membership identity type.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-get-memberships-succeed=Memberships of identity, {2} of type, {1} in realm, {0} is printed.\nidrepo-memberships-result={0} ({1})\nidrepo-no-memberships=Identity has no memberships.\nidrepo-cannot-be-member={0} cannot have membership of identity type, {1}")
    private String show_memberships;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetMembers",
        description="Show the memberships of an identity.",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.@membershipidtype|m|s|Membership identity type.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-get-members-succeed=Members of identity, {2} of type, {1} in realm, {0} is printed.\nidrepo-members-result={0} ({1})\nidrepo-no-members=Identity has no members.\nidrepo-cannot-be-member={0} cannot have members of identity type, {1}")
    private String show_members;
/*
    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.IsMember",
        description="Determine if an identity is a member of another identity",
        mandatoryOptions="realm|e|s|Name of realm.@memberidname|m|s|Name of identity that is member.@memberidtype|y|s|Type of Identity of member such as User, Role and Group.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-get-ismember-succeed=Membership is determine.\nidrepo-ismembers-positive-result={0} is a member of {1}.\nidrepo-ismembers-negative-result={0} is not a member of {1}.\n")
    private String show_identity_member_of;*/

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.AddMember",
        description="Add an identity as member of another identity",
        mandatoryOptions="realm|e|s|Name of realm.@memberidname|m|s|Name of identity that is member.@memberidtype|y|s|Type of Identity of member such as User, Role and Group.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-get-addmember-succeed={0} is now member of {1}.\n")
    private String add_member;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.RemoveMember",
        description="Remove membership of identity from another identity",
        mandatoryOptions="realm|e|s|Name of realm.@memberidname|m|s|Name of identity that is member.@memberidtype|y|s|Type of Identity of member such as User, Role and Group.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-get-removemember-succeed={0} is now not member of {1}.\n")
    private String remove_member;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.AssignService",
        description="Add Service to an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="datafile|D|s|Name of file that contains attribute values data.",
        resourceStrings="idrepo-assign-service-succeed={3} is added to identity {2} of type, {1} in realm, {0}.\n")
    private String add_service_identity;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.UnassignService",
        description="Remove Service from an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-unassign-service-succeed={3} is removed from identity {2} of type, {1} in realm, {0}.\n")
    private String remove_service_identity;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.ModifyService",
        description="Set service attribute values of an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.@servicename|s|s|Name of service.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.",
        resourceStrings="idrepo-modify-service-succeed=Attribute values of service, {3} of identity {2} of type, {1} in realm, {0} is modified.\n")
    private String set_identity_service_attribute;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.SetAttributeValues",
        description="Set attribute values of an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="attributevalues|a|m|Attribute values e.g. homeaddress=here.@datafile|D|s|Name of file that contains attribute values data.",
        resourceStrings="idrepo-set-attribute-values-succeed=Attribute values of identity, {2} of type, {1} in realm, {0} is modified.\n")
    private String set_identity_attributes;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.GetPrivileges",
        description="Show privileges assigned to an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-get-privileges-succeed=Privileges of identity, {2} of type, {1} in realm, {0} is printed.\nprivilege-result={0}\nno-privileges=There are no privileges.")
    private String show_privileges;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.AddPrivileges",
        description="Add privileges to an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.@privileges|g|m|Name of privileges to be added.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-add-privileges-succeed=Privileges are add to identity, {2} of type, {1} in realm, {0}.\ndelegation-already-has-privilege={0} already has privilege, {1}")
    private String add_privileges;

    @SubCommandInfo(
        implClassName="com.sun.identity.cli.idrepo.RemovePrivileges",
        description="Remove privileges from an identity",
        mandatoryOptions="realm|e|s|Name of realm.@idname|i|s|Name of identity.@idtype|t|s|Type of Identity such as User, Role and Group.@privileges|g|m|Name of privileges to be removed.",
        optionAliases="",
        macro="authentication",
        optionalOptions="",
        resourceStrings="idrepo-remove-privileges-succeed=Privileges are removed from identity, {2} of type, {1} in realm, {0}.\ndelegation-does-not-have-privilege={0} does not have privilege, {1}")
    private String remove_privileges;
}

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
 * $Id: UpgradeUtils.java,v 1.1 2008-01-18 08:03:12 bina Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.upgrade;

import com.iplanet.am.admin.cli.Main;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.util.Crypt;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.internal.InvalidAuthContextException;
import com.sun.identity.common.LDAPUtils;
import com.sun.identity.common.configuration.ConfigurationException;
import com.sun.identity.common.configuration.ServerConfiguration;
import com.sun.identity.common.configuration.SiteConfiguration;
import com.sun.identity.common.configuration.UnknownPropertyNameException;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdType;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.locale.Locale;
import com.sun.identity.sm.AttributeSchema;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.SchemaType;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceManager;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javax.security.auth.login.LoginException;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.util.LDIF;

/**
 * This class contains utilities to upgrade the service schema
 * configuration to be compatible with FAM.
 * 
 * TODO: 
 * 1.add localization 
 * 2. logs
 * 3. use constants where possible.
 * 
 */
public class UpgradeUtils {

    final static String SCHEMA_TYPE_GLOBAL = "global";
    final static String SCHEMA_TYPE_ORGANIZATION = "organization";
    final static String SCHEMA_TYPE_DYNAMIC = "dynamic";
    final static String SCHEMA_TYPE_USER = "user";
    final static String SCHEMA_TYPE_POLICY = "policy";
    final static String AUTH_SERVICE_NAME = "iPlanetAMAuthService";
    final static String AUTH_ATTR_NAME = "iplanet-am-auth-authenticators";
    final static int AUTH_SUCCESS =
            com.sun.identity.authentication.internal.AuthContext.AUTH_SUCCESS;
    static SSOToken ssoToken;
    public static Debug debug = Debug.getInstance("famUpgrade");
    private static String dsHostName;
    private static int dsPort;
    private static String bindDN = null;
    private static String bindPasswd = null;
    private static String deployURI = null;
    private static String dsAdminPwd;
    private static LDAPConnection ld = null;
    private static String basedir;
    private static String stagingDir;
    private static String rootSuffix;

    // will be passed on from the main upgrade class
    static String adminDN = null;
    static String adminPasswd = null;
    // the following value will be passed down from the Main Upgrade program.
    // default dsMnanager dn.
    static String dsManager = "cn=Directory Manager";

    static {
        // TODO change this 
        System.setProperty("com.iplanet.am.version", "8.0");
        System.setProperty("installTime", "true");
        System.setProperty("donotIncludeSMSAuthModule", "true");
    }

    /**
     * Returns the SSOToken. 
     *
     * @return Admin Token.
     */
    public static SSOToken getSSOToken() {
        String classMethod = "UpgradeUtils:getSSOToken";
        if (ssoToken == null) {
            ssoToken = ldapLoginInternal(bindDN, bindPasswd);
        }
        if (ssoToken != null) {
            try {
                String principal = ssoToken.getProperty("Principal");
                if (debug.messageEnabled()) {
                    debug.message(classMethod +
                            "Principal in SSOToken :" + principal);
                }
            } catch (Exception e) {
                debug.error("Error creating SSOToken ", e);
            }
        }
        return ssoToken;
    }

    /**
     * Create a new service in server.
     *
     * @param xmlfileName Name of the schema XML file to be loaded.
     */
    public static void createService(String fileName) throws UpgradeException {
        String classMethod = "UpgradeUtils:createService";
        log("Creating Service" + fileName);
        FileInputStream fis = null;
        try {
            ServiceManager ssm = getServiceManager();
            fis = new FileInputStream(fileName);
            ssm.registerServices(fis);
        } catch (FileNotFoundException fe) {
            throw new UpgradeException("File Not found");
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid SSOToken");
        } catch (SMSException e) {
            throw new UpgradeException("Unable to load service schema ");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ie) {
                //igore if file input stream cannot be closed.
                }
            }
        }
    }

    /**
     * Add attribute schema to an existing service.
     *
     * @param servicename Service Name.
     * @param schematype Schema Type.
     * @param attributeschemafile
     *        XML file containing attribute schema definition.
     * @param subschemaname Name of sub schema.
     */
    public static void addAttributeToSchema(
            String serviceName,
            String schemaType,
            String attributeSchemaFile) throws UpgradeException {
        String classMethod = "UpgradeUtils:addAttributeToSchema";
        log("Adding attributeschema" + attributeSchemaFile +
                "for service" + serviceName);
        FileInputStream fis = null;
        ServiceSchema ss = getServiceSchema(serviceName, null, schemaType);
        try {
            fis = new FileInputStream(attributeSchemaFile);
            ss.addAttributeSchema(fis);
        } catch (IOException ioe) {
            throw new UpgradeException("Incorrect file name");
        } catch (SMSException sme) {
            throw new UpgradeException("Cannot add attribute exception");
        } catch (SSOException ssoe) {
            throw new UpgradeException("invalid sso token");
        }
    }

    /**
     * Add attribute schema to an existing service.
     *
     * @param servicename Service Name.
     * @param schematype Schema Type.
     * @param attributeschemafile XML file containing attribute schema 
     * definition.
     * @param subschemaname Name of sub schema.
     */
    public static void addAttributeToSubSchema(
            String serviceName,
            String subSchemaName,
            String schemaType,
            String attributeSchemaFile) throws UpgradeException {
        String classMethod = "UpgradeUtils:addAttributeSchema";
        log(classMethod + "Adding attribute schema : " + attributeSchemaFile);
        log(" to subSchema " + subSchemaName + " to service " + serviceName);
        FileInputStream fis = null;
        ServiceSchema ss =
                getServiceSchema(serviceName, subSchemaName, schemaType);
        try {
            fis = new FileInputStream(attributeSchemaFile);
            ss.addAttributeSchema(fis);
        } catch (IOException ioe) {
            throw new UpgradeException("Incorrect file name");
        } catch (SMSException sme) {
            throw new UpgradeException("Cannot add attribute exception");
        } catch (SSOException ssoe) {
            throw new UpgradeException("invalid sso token");
        }
    }

    /**
     * Sets default values of an existing attribute.
     * The existing values will be overwritten with the new values.
     *
     * @param serviceName name of the service
     * @param subSchemaName name of the subschema
     * @param schemaType the schemaType
     * @param attributeName name of the attribute
     * @param defaultValues a set of values to be added to the attribute
     * @exception <code>UpgradeException</code> if there is an error.
     */
    public static void setAttributeDefaultValues(
            String serviceName,
            String subSchemaName,
            String schemaType,
            String attributeName,
            Set defaultValues) throws UpgradeException {
        String classMethod = "UpgradeUtils:setAttributeDefaultValues";
        ServiceSchema ss =
                getServiceSchema(serviceName, subSchemaName, schemaType);
        try {
            Map attributeDefaults = ss.getAttributeDefaults();
            ss.setAttributeDefaults(attributeName, defaultValues);
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid SSOToken");
        } catch (SMSException sme) {
            throw new UpgradeException(
                    "Unable to set attribute default values");
        }
    }

    /*
     * Add attribute default values to an existing attribute.
     * The existing attribute values will be updated with new values.
     *
     * @param serviceName name of the service
     * @param subSchemaName name of the subschema
     * @param schemaType the schemaType
     * @param attributeName name of the attribute
     * @param defaultValues a set of values to be added to the attribute
     * @exception <code>UpgradeException</code> if there is an error.
     */
    public static void addAttributeDefaultValues(
            String serviceName,
            String subSchemaName,
            String schemaType,
            String attributeName,
            Set defaultValues) throws UpgradeException {
        String classMethod = "UpgradeUtils:addAttributeDefaultValues";
        log(classMethod + "Updating attribute default values");
        log("in :" + serviceName + "for attribute: " + attributeName);
        ServiceSchema ss =
                getServiceSchema(serviceName, subSchemaName, schemaType);
        try {
            Map attributeDefaults = ss.getAttributeDefaults();
            Set oldAttrValues = (Set) attributeDefaults.get(attributeName);
            Set newAttrValues =
                    ((oldAttrValues == null) || oldAttrValues.isEmpty())
                    ? new HashSet() : new HashSet(oldAttrValues);
            newAttrValues.addAll(defaultValues);
            ss.setAttributeDefaults(attributeName, newAttrValues);
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid SSOToken");
        } catch (SMSException sme) {
            throw new UpgradeException("Failed to add attribute default " +
                    "values");
        }
    }
    /*
     * Add attribute choice values to an existing attribute.
     * The existing attribute values will be updated with new choice values.
     *
     * @param serviceName name of the service
     * @param subSchemaName name of the subschema
     * @param schemaType the schemaType
     * @param attributeName name of the attribute
     * @param choiceValuesMap a set of choice values values to
     *        be added to the attribute, the key is the i18NKey and
     *        the values it the choice value
     * @exception <code>UpgradeException</code> if there is an error.
     */

    public static void addAttributeChoiceValues(
            String serviceName,
            String subSchemaName,
            String schemaType,
            String attributeName,
            Map choiceValuesMap)
            throws UpgradeException {
        String classMethod = "UpgradeUtils.addAttributeChoiceValues";
        try {
            ServiceSchema ss =
                    getServiceSchema(serviceName, subSchemaName, schemaType);
            AttributeSchema attrSchema = ss.getAttributeSchema(attributeName);
            addChoiceValues(attrSchema, choiceValuesMap);
        } catch (SSOException ssoe) {
            throw new UpgradeException("Error getting SSOToken ");
        } catch (SMSException sme) {
            throw new UpgradeException("Error updating choice values ");
        }
    }

    /**
     * Add choice values to an attribute .
     */
    protected static void addChoiceValues(
            AttributeSchema attrSchema,
            Map choiceValMap) throws SMSException, SSOException {
        for (Iterator i = choiceValMap.keySet().iterator(); i.hasNext();) {
            String i18nKey = (String) i.next();
            Set valueSet = (Set) choiceValMap.get(i18nKey);
            String value = (String) valueSet.iterator().next();
            /*String value = 
            (String)(((Set) choiceValMap.get(i18nKey)).iterator().next());*/
            attrSchema.addChoiceValue(value, i18nKey);
        }
    }

    /*
     * Remove attribute values from sub realms.
     * The existing attribute values will be updated.
     *
     * @param serviceName name of the service
     * @param attributeName name of the attribute
     * @param attrVals a set of attrubute values to be removed.
     * @exception <code>UpgradeException</code> if there is an error.
     */
    public static void removeAttributeValuesFromRealms(
            String serviceName,
            String attributeName,
            Set attrVals) throws UpgradeException {
        String classMethod = "UpgradeUtils:removeAttributeValuesFromRealms";
        log(classMethod + "Removing attribute values from :" + attributeName);
        try {
            // get a list of realms - recursively.
            OrganizationConfigManager ocm =
                    new OrganizationConfigManager(ssoToken, rootSuffix);
            Set realms = ocm.getSubOrganizationNames("*", true);
            Iterator i = realms.iterator();
            while (i.hasNext()) {
                String realm = (String) i.next();
                ocm = new OrganizationConfigManager(ssoToken, realm);
                ocm.removeAttributeValues(serviceName, attributeName, attrVals);
            }
        } catch (SMSException sme) {
            throw new UpgradeException("Error updating attribute values ");
        }
    }

    /*
     * Add attribute values to sub realms.
     * The existing attribute values will be updated.
     *
     * @param serviceName name of the service
     * @param attributeName name of the attribute
     * @param attrValues a set of attrubute values to be added.
     * @exception <code>UpgradeException</code> if there is an error.
     */
    public static void addAttributeValuesToRealms(
            String serviceName,
            String attributeName,
            Set attrValues) throws UpgradeException {
        try {
            // get a list of realms .
            OrganizationConfigManager ocm =
                    new OrganizationConfigManager(ssoToken, rootSuffix);
            Set realms = ocm.getSubOrganizationNames("*", true);
            Iterator i = realms.iterator();
            while (i.hasNext()) {
                String realm = (String) i.next();
                ocm = new OrganizationConfigManager(ssoToken, realm);
                ocm.addAttributeValues(serviceName, attributeName, attrValues);
            }
        } catch (SMSException sme) {
            throw new UpgradeException("Error updating attribute values ");
        }
    }

    /**
     * Removes attribute schema from an existing service.
     *
     * @param serviceName Service Name.
     * @param schemaType Schema Type.
     * @param atttrubuteName name of the attributes to be deleted.
     * @param subSchemaName Name of sub schema.
     * @throws <code>UpgradeException</code> if there is an error.
     */
    public static void removeAttributeSchema(
            String serviceName,
            String schemaType,
            String attributeName,
            String subSchemaName) throws UpgradeException {
        String classMethod = "UpgradeUtils:removeAttributeSchema:";
        ServiceSchema ss = null;
        try {
            ss = getServiceSchema(serviceName, subSchemaName, schemaType);
            ss.removeAttributeSchema(attributeName);
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid sso Token ");
        } catch (SMSException sme) {
            throw new UpgradeException("Unable to remove attribute schema");
        }
    }

    /**
     * Removes attribute schema from an existing service.
     *
     * @param serviceName Service Name.
     * @param schemaType Schema Type.
     * @param attributeSchemaNames a list of attribute schemas to be removed. 
     * @param subSchemaName Name of sub schema.
     * @throws <code>UpgradeException</code> if there is an error.
     */
    public static void removeAttributeSchemas(
            String serviceName,
            String schemaType,
            List attributeSchemaNames,
            String subSchemaName) throws UpgradeException {
        String classMethod = "UpgradeUtils:removeAttributeSchema:";
        ServiceSchema ss = null;
        try {
            for (Iterator i = attributeSchemaNames.iterator(); i.hasNext();) {
                String attributeSchemaName = (String) i.next();
                ss = getServiceSchema(serviceName, subSchemaName, schemaType);
                ss.removeAttributeSchema(attributeSchemaName);
            }
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid sso Token ");
        } catch (SMSException sme) {
            throw new UpgradeException("Unable to remove attribute schema");
        }
    }

    /**
     * Migrate organization to realm.
     * TODO
     */
    public static void doMigration70() throws Exception {
    //TODO
    }

    /** 
     * Sets the I18N File Name .
     *
     * @param serviceName name of the service.
     * @param i18NFileName the i18NFileName attribute value.
     * @throws <code>UpgradeException</code> when there is an error.
     */
    public static void seti18NFileName(
            String serviceName,
            String value) throws UpgradeException {
        try {
            ServiceSchemaManager ssm = getServiceSchemaManager(serviceName);
            ssm.setI18NFileName(value);
            log(serviceName + " :Setting I18NFileName " + value);
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid SSOToken ");
        } catch (SMSException sme) {
            throw new UpgradeException("Error setting i18NFileName value");
        }
    }

    /** 
     * Sets the service revision number.
     *
     * @param serviceName name of the service.
     * @param revisionNumber the revisionNumber of the service.
     * @throws <code>UpgradeException</code> if there is an error.
     */
    public static void setServiceRevision(
            String serviceName,
            String revisionNumber) throws UpgradeException {
        try {
            ServiceSchemaManager ssm = getServiceSchemaManager(serviceName);
            ssm.setRevisionNumber(Integer.parseInt(revisionNumber));
            log(serviceName +
                    ":Setting Service Revision Number" + revisionNumber);
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid SSOToken ");
        } catch (SMSException sme) {
            throw new UpgradeException("Error setting serviceRevision value");
        }
    }

    /**
     * Updates the values of the <code>any</code> attribute in the attribute
     * schema.
     * 
     * @param serviceName the service name where the attribute exists.
     * @param subSchema the subschema name.
     * @param schemaType the schema type
     * @param attrName the attribute name.
     * @param value the value of the <code>any</code> attribute
     * @throws UpgradeException if there is an error.
     */
    public static void modifyAnyInAttributeSchema(
            String serviceName,
            String subSchema,
            String schemaType,
            String attrName,
            String value) throws UpgradeException {
        try {
            ServiceSchema ss =
                    getServiceSchema(serviceName, subSchema, schemaType);
            AttributeSchema attrSchema = ss.getAttributeSchema(attrName);
            attrSchema.setAny(value);
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid token");
        } catch (SMSException sme) {
            throw new UpgradeException("Error setting any attribute");
        }
    }

    /**
     * Returns the current service revision number .
     *
     * @param serviceName name of the service.
     * @return revisionNumber the service revision number.
     */
    public static int getServiceRevision(String serviceName) {
        int revisionNumber = -1;
        ServiceSchemaManager ssm = getServiceSchemaManager(serviceName);
        if (ssm != null) {
            revisionNumber = ssm.getRevisionNumber();
        }
        return revisionNumber;
    }

    /**
     * Returns true if the value of realmMode attribute is true.
     * If there is an error retreiving the attribute a false will be
     * assumed.
     * 
     * @return true if realmMode attribute value is true otherwise false.
     */
    public static boolean isRealmMode() {
        boolean isRealmMode = false;
        getSSOToken();
        try {
            //TODO define constants.
            String serviceName = "sunIdentityRepositoryService";
            ServiceSchemaManager sm = getServiceSchemaManager(serviceName);
            ServiceSchema ss = sm.getSchema(SCHEMA_TYPE_GLOBAL);
            Map attributeDefaults = ss.getAttributeDefaults();
            if (attributeDefaults.containsKey("realmMode")) {
                HashSet hashSet = (HashSet) attributeDefaults.get("realmMode");
                String value = (String) (hashSet.iterator().next());
                log("realmMode is : " + value);
            }
        //TODO catch the specific exception
        } catch (Exception e) {
            log("Error retreiving the attribute");
        }
        return isRealmMode;
    }

    /**
     * Removes choice values from attribute schema.
     *
     * @param serviceName Name of service.
     * @param schemaType Type of schema.
     * @param attributeName Name of attribute.
     * @param choiceValues Choice values e.g. Inactive
     * @param subSchema Name of sub schema.
     * @throws UpgradeException if there is an error.
     */
    public static void removeAttributeChoiceValues(
            String serviceName,
            String schemaType,
            String attributeName,
            Set choiceValues,
            String subSchema) throws UpgradeException {
        try {
            ServiceSchema ss =
                    getServiceSchema(serviceName, subSchema, schemaType);
            AttributeSchema attrSchema =
                    ss.getAttributeSchema(attributeName);
            for (Iterator i = choiceValues.iterator(); i.hasNext();) {
                String choiceValue = (String) i.next();
                attrSchema.removeChoiceValue(choiceValue);
            }
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid SSOToken");
        } catch (SMSException sme) {
            throw new UpgradeException("Error removing attribute choice vals");
        }
    }

    /**
     * Removes attributes default values.
     * 
     * @param serviceName name of the service
     * @param schemaType the schema type
     * @param attributeName name of the attribute 
     * @param defaultValues a set of values to be removed 
     * @param subSchema name of the sub schema
     * @throws UpgradeException if there is an error
     */
    public static void removeAttributeDefaultValues(
            String serviceName,
            String schemaType,
            String attributeName,
            Set defaultValues,
            String subSchema) throws UpgradeException {
        try {
            ServiceSchema ss =
                    getServiceSchema(serviceName, subSchema, schemaType);
            AttributeSchema attrSchema =
                    ss.getAttributeSchema(attributeName);
            for (Iterator i = defaultValues.iterator(); i.hasNext();) {
                String defaultValue = (String) i.next();
                attrSchema.removeDefaultValue(defaultValue);
            }
        } catch (SSOException ssoe) {
            throw new UpgradeException("Invalid SSOToken");
        } catch (SMSException sme) {
            throw new UpgradeException("Error removing attribute default vals");
        }
    }

    /**
     * Adds sub schema to a service.
     *
     * @param servicename Name of service.
     * @param subSchema the subschema name.
     * @param schemaType the schema type.
     * @param filename Name of file that contains the sub schema
     * @throws UpgradeExcpetion if there is an error
     */
    public static void addSubSchema(
            String serviceName,
            String subSchema,
            String schemaType,
            String fileName) throws UpgradeException {
        try {
            ServiceSchema ss =
                    getServiceSchema(serviceName, subSchema, schemaType);
            ss.addSubSchema(new FileInputStream(fileName));
        } catch (IOException ioe) {
            throw new UpgradeException("Error reading schema file ");
        } catch (SSOException ssoe) {
            throw new UpgradeException("invalid sso token");
        } catch (SMSException ssoe) {
            throw new UpgradeException("error creating subschema");
        }
    }

    /**
     * Adds SubConfiguration to a service.
     * 
     * @param serviceName the service name
     * @param svcConfigName the service config 
     * @param subConfigName the subconfig name 
     * @param subConfigID the subconfig id
     * @param attrValues a map of attribute value pairs to be added to the
     *        subconfig.
     * @param priority the priority value 
     * @throws UpgradeException if there is an error.
     */
    public static void addSubConfiguration(
            String serviceName,
            String svcConfigName,
            String subConfigName,
            String subConfigID,
            Map attrValues, int priority) throws UpgradeException {
        try {
            ServiceConfigManager scm =
                    new ServiceConfigManager(serviceName, ssoToken);
            ServiceConfig sc = scm.getGlobalConfig(svcConfigName);
            if (sc != null) {
                sc.addSubConfig(subConfigName, subConfigID,
                        priority, attrValues);
            } else {
                throw new UpgradeException("error adding subconfig");
            }
        } catch (SSOException ssoe) {
            throw new UpgradeException("invalid sso token");
        } catch (SMSException ssoe) {
            throw new UpgradeException("error adding subconfig");
        }
    }

    /**
     * Loads the ldif changes to the directory server.
     * 
     * @param ldifFileName the name of the ldif file.
     * TODO: change the method to throw exception 
     */
    public static void loadLdif(String ldifFileName) {
        try {
            LDIF ldif = new LDIF(ldifFileName);
            ld = getLDAPConnection();
            LDAPUtils.createSchemaFromLDIF(ldif, ld);
        } catch (IOException ioe) {
            log("cannot find file . Error loading ldif");
        } catch (LDAPException le) {
            log("Error loading ldif");
        }
    }

    /**
     * Helper method to return Ldap connection
     *
     * @return Ldap connection
     */
    private static LDAPConnection getLDAPConnection() {
        log("DSHOST : " + dsHostName);
        log("DSPORT : " + dsPort);
        log("dsManager: " + dsManager);
        log("dsAdminPwd : " + dsAdminPwd);
        if (ld == null) {
            try {
                ld = new LDAPConnection();
                ld.setConnectTimeout(300);
                ld.connect(3, dsHostName, dsPort, dsManager, dsAdminPwd);
            } catch (LDAPException e) {
                disconnectDServer();
                ld = null;
                log("Error getting LDAP Connection");
            }
        }
        return ld;
    }

    /**
     * Helper method to disconnect from Directory Server.
     */
    private static void disconnectDServer() {
        if ((ld != null) && ld.isConnected()) {
            try {
                ld.disconnect();
                ld = null;
            } catch (LDAPException e) {
                debug.message("Error disconnecting ", e);
            }
        }
    }


// Legacy code to support older upgrade data based on amAdmin dtd.
// These should not be used for the new data since these will be
// deprecated along with amAdmin.
// therefore not adding javadocs for these.

    // import service data
    public static void importServiceData(
            String fileName)
            throws UpgradeException {
        String[] args = new String[8];
        args[0] = "--runasdn";
        args[1] = adminDN;
        args[2] = "-w";
        args[3] = adminPasswd;
        args[4] = "-c";
        args[5] = "-v";
        args[6] = "-t";
        args[7] = fileName;
        invokeAdminCLI(args);
    }

    // import service data multiple files
    public static void importServiceData(
            String[] fileList) throws UpgradeException {
        int len = fileList.length;
        String[] args = new String[7 + len];
        args[0] = "--runasdn";
        args[1] = adminDN;
        args[2] = "-w";
        args[3] = adminPasswd;
        args[4] = "-c";
        args[5] = "-v";
        args[6] = "-t";
        for (int i = 0; i < len; i++) {
            args[7 + i] = fileList[i];
        }
        invokeAdminCLI(args);
    }

    // import new service schema multiple files
    public static void importNewServiceSchema(
            String[] fileList) throws UpgradeException {

        int len = fileList.length;
        String[] args = new String[8 + len];
        args[0] = "--runasdn";
        args[1] = adminDN;
        args[2] = "-w";
        args[3] = adminPasswd;
        args[4] = "-c";
        args[5] = "-v";
        args[6] = "-s";
        for (int i = 0; i < len; i++) {
            args[7 + i] = fileList[i];
        }
        invokeAdminCLI(args);
    }
    // import new service schema for a single file.
    public static void importNewServiceSchema(
            String fileList) throws UpgradeException {

        String[] args = new String[8];
        args[0] = "--runasdn";
        args[1] = adminDN;
        args[2] = "-w";
        args[3] = adminPasswd;
        args[4] = "-c";
        args[5] = "-v";
        args[6] = "-s";
        args[7] = fileList;
        invokeAdminCLI(args);
    }


    // getAttributeValue - retrieve attribute value
    public void getAttributeValue(String fileName) throws UpgradeException {
        String[] args = new String[8];
        args[0] = "--runasdn";
        args[1] = adminDN;
        args[2] = "-w";
        args[3] = adminPasswd;
        args[4] = "-c";
        args[5] = "-v";
        args[6] = "-t";
        args[7] = fileName;
        invokeAdminCLI(args);
    }

    /**
     * TODO:
     * Logs messages to standard out and debug file.
     * @param message the message to be logged
     */
    public static void log(String message) {
    //TODO
    }

    /**
     * Returns the absolute path of new service schema xml file.
     * 
     * @param fileName name of the service xml.
     * @return the absolute path of the file.
     */
    public static String getNewServiceNamePath(String fileName) {
        StringBuffer sb = new StringBuffer();
        sb.append(basedir).append(File.separator).
                append("xml").append(File.separator).
                append(fileName);
        return sb.toString();

    }

    /**
     * Returns the absolute path of the <code>serverdefaults</code>
     * properties file. This file is located in the staging directory 
     * under WEB-INF/classes.
     * 
     * @return the absolute path of the file.
     */
    public static String getServerDefaultsPath() {

        StringBuffer sb = new StringBuffer();
        sb.append(stagingDir).append(File.separator).
                append("WEB-INF").append(File.separator).
                append("classes").append(File.separator).
                append(File.separator).append("serverdefaults.properties");

        return sb.toString();
    }

    /**
     * Returns the absolute path of the sms template files.
     * properties file. This file is located in the staging directory 
     * under WEB-INF/template/sms.
     * 
     * @return the absolute path of the file.
     */
    public static String getServiceTemplateDir(String SCHEMA_FILE) {

        StringBuffer sb = new StringBuffer();
        sb.append(stagingDir).append(File.separator).
                append("WEB-INF/template/sms").append(SCHEMA_FILE);

        return sb.toString();
    }

    /**
     * Returns the absolute path of service schema xml file.
     * The new service schema file will be located in the
     * staging directory under WEB-INF/classes.
     * 
     * @param serviceName name of the service.
     * @param fileName name of the file.
     * @return the absolute path of the file.
     */
    public static String getAbsolutePath(String serviceName, String fileName) {
        //TODO add constant for static part.
        StringBuffer sb = new StringBuffer();
        sb.append(basedir).append(File.separator).append("upgrade").append(File.separator).append("services").append(File.separator).append(serviceName).append(File.separator).append("data").append(File.separator).append(fileName);

        return sb.toString();
    }

    /**
     * Returns the ssoToken used for admin operations.
     * NOTE: this might be replaced later.
     * @param bindUser the user distinguished name.
     * @param bindPwd the user password
     * @return the <code>SSOToken</code>
     */
    private static SSOToken ldapLoginInternal(
            String bindUser,
            String bindPwd) {

        SSOToken ssoToken = null;
        try {
            com.sun.identity.authentication.internal.AuthContext ac =
                    getLDAPAuthContext(bindUser, bindPwd);
            if (ac.getLoginStatus() == AUTH_SUCCESS) {
                ssoToken = ac.getSSOToken();
            } else {
                ssoToken = null;
            }
        } catch (LoginException le) {
            log("Error creating SSOToken" + le.getMessage());

        } catch (InvalidAuthContextException iace) {
            ssoToken = null;
            log("Error creating SSOToken" + iace.getMessage());
        }
        return ssoToken;
    }

    // return the authcontext
    private static com.sun.identity.authentication.internal.AuthContext 
    getLDAPAuthContext(
            String bindUser,
            String bindPwd) throws LoginException {
        com.sun.identity.authentication.internal.AuthPrincipal principal =
                new com.sun.identity.authentication.internal.AuthPrincipal(
                bindUser);
        com.sun.identity.authentication.internal.AuthContext authContext =
                new com.sun.identity.authentication.internal.AuthContext(
                principal, bindPwd.toCharArray());
        return authContext;
    }

    // legacy code to invoke amadmin cli
    static void invokeAdminCLI(String[] args) throws UpgradeException {
        /*
         * Set the property to inform AdminTokenAction that
         * "amadmin" CLI is executing the program
         */
        SystemProperties.initializeProperties(
                AdminTokenAction.AMADMIN_MODE, "true");

        // Initialize Crypt class
        Crypt.checkCaller();

        Main dpa = new Main();
        try {
            dpa.parseCommandLine(args);
            dpa.runCommand();
            log("successful");
        } catch (Exception eex) {
            throw new UpgradeException("operation failed");
        }
    }

    // return the properties
    public static Properties getProperties(String file) {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (FileNotFoundException fe) {
            log("File Not found" + file);
        } catch (IOException ie) {
            log("Error reading file" + file);
        }
        return properties;
    }

    /**
     * Checks the service scheam for existance of an attribute.
     * 
     * @param serviceName name of the service.
     * @param attributeName the attribute name
     * @param schemaType the schema type
     * @return true if attrbute exist else false.
     * @throws UpgradeException if there is an error
     */
    public static boolean attributeExists(
            String serviceName,
            String attributeName,
            String schemaType)
            throws UpgradeException {
        boolean isExists = false;
        try {
            ServiceSchemaManager sm = getServiceSchemaManager(serviceName);
            ServiceSchema ss = sm.getSchema(schemaType);
            Map attributeDefaults = ss.getAttributeDefaults();
            if (attributeDefaults.containsKey(attributeName)) {
                HashSet hashSet =
                        (HashSet) attributeDefaults.get(attributeName);
                String value = (String) (hashSet.iterator().next());
                log("Attribute Value : " + value);
                isExists = true;
            }
        } catch (SMSException sme) {
            throw new UpgradeException("Error getting attribute value");
        }
        return isExists;
    }

    /**
     * Returns a value of an attribute.
     * This method assumes that the attribute is single valued.
     * 
     * @param serviceName name of the service.
     * @param attributeName name of the attribute.
     * @param schemaType the schema type.
     * @return the value of the attribute 
     * @throws UpgradeException if there is an error.
     */
    public static String getAttributeValueString(
            String serviceName,
            String attributeName,
            String schemaType) throws UpgradeException {
        String value = null;
        try {
            ServiceSchemaManager sm = getServiceSchemaManager(serviceName);
            ServiceSchema ss = sm.getSchema(schemaType);
            String attributeValue = null;
            Map attributeDefaults = ss.getAttributeDefaults();
            if (attributeDefaults.containsKey(attributeName)) {
                HashSet hashSet =
                        (HashSet) attributeDefaults.get(attributeName);
                value = (String) (hashSet.iterator().next());
            }
        } catch (SMSException sme) {
            throw new UpgradeException("Error getting attr value");
        }
        return value;
    }

    /**
     * Returns a set of values of an attribute.
     * 
     * @param serviceName name of the service.
     * @param attributeName the attribute name.
     * @param  schemaType the schema type.
     * @return a set of values for the attribute.
     * @throws UpgradeException if there is an error.
     */
    public static Set getAttributeValue(String serviceName,
            String attributeName,
            String schemaType) throws UpgradeException {

        Set attrValues = Collections.EMPTY_SET;
        try {
            ServiceSchemaManager sm = getServiceSchemaManager(serviceName);
            ServiceSchema ss = sm.getSchema(schemaType);
            String attributeValue = null;
            Map attributeDefaults = ss.getAttributeDefaults();
            if (attributeDefaults.containsKey(attributeName)) {
                attrValues = (HashSet) attributeDefaults.get(attributeName);
            }
        } catch (SMSException sme) {
            throw new UpgradeException("Unable to get attribute value");
        }
        return attrValues;
    }

    /**
     * Creates a site configuration.
     * 
     * @param siteURL the site URL.
     * @param accessPoints a set of access points for the site.
     * @throws UpgradeException if there is an error.
     */
    public static void createSite(String siteURL,
            Set accessPoints) throws UpgradeException {
        try {
            SiteConfiguration.createSite(ssoToken, siteURL,
                    siteURL, accessPoints);
        } catch (ConfigurationException ce) {
            throw new UpgradeException("Unable to create Service instance");
        } catch (SMSException sme) {
            throw new UpgradeException("Unable to add to site");
        } catch (SSOException ssoe) {
            throw new UpgradeException("invalid ssotoken");
        }
    }

    /**
     * Returns the server instance name.
     * The server instance is the server name appended with the
     * deployURI.
     * 
     * @param serverName name of the server
     * @return the server instance name.
     */
    public static String getServerInstance(String serverName) {
        return serverName + File.separator + deployURI;
    }

    /** 
     * Creates a service instance.
     * 
     * @param serverInstance the server instance value
     * @param serverId the server identifier
     * @throws UpgradeException if there is an error.
     */
    public static void createServiceInstance(
            String serverInstance, String serverId)
            throws UpgradeException {
        try {
            ServerConfiguration.createServerInstance(
                    ssoToken, serverInstance,
                    serverId, Collections.EMPTY_SET, "");
        } catch (UnknownPropertyNameException uce) {
            throw new UpgradeException("Unknwon property ");
        } catch (ConfigurationException ce) {
            throw new UpgradeException("Unable to create Service instance");
        } catch (SMSException sme) {
            throw new UpgradeException("Unable to create Service instance");
        } catch (SSOException ssoe) {
            throw new UpgradeException("invalid ssotoken");
        }
    }

    /**
     * Adds server to a site.
     *
     * @param serverInstance Name of the server instance.
     * @param siteId Identifier of the site.
     * @throws UpgradeException if there is an error.
     */
    public static void addToSite(
            String serverInstance,
            String siteId) throws UpgradeException {
        try {
            ServerConfiguration.addToSite(ssoToken, serverInstance, siteId);
        } catch (SMSException sme) {
            throw new UpgradeException("Unable to add to site");
        } catch (SSOException ssoe) {
            throw new UpgradeException("Unable to add to site");
        }
    }

    // TODO ADD JAVADICS for the following later.
    // the following methods might change.
    public static void setBindDN(String dn) {
        bindDN = dn;
    }

    public static void setDeployURI(String uri) {
        deployURI = uri;
    }

    public static void setBindPass(String pass) {
        bindPasswd = pass;
    }

    public static void setDSHost(String dsHost) {
        dsHostName = dsHost;
    }

    public static void setDSPort(int port) {
        dsPort = port;
    }

    public static void setDirMgrDN(String dn) {
        dsManager = dn;
    }

    public static void setdirPass(String pass) {
        dsAdminPwd = pass;
    }

    public static void setBaseDir(String dir) {
        basedir = dir;
    }

    public static void setStagingDir(String dir) {
        stagingDir = dir;
    }
    // END TODO 
    /**
     * Returns the <code>ServiceSchemaManager</code> for a service.
     * 
     * @param serviceName the service name
     * @return the <code>ServiceSchemaManager</code> of the service.
     */
    public static ServiceSchemaManager getServiceSchemaManager(
            String serviceName) {
        return getServiceSchemaManager(serviceName, ssoToken);
    }

    /**
     * Returns the <code>ServiceSchemaManager</code> for a service.
     * 
     * @param serviceName the service name
     * @param the admin SSOToken.
     * @return the <code>ServiceSchemaManager</code> of the service.
     */
    protected static ServiceSchemaManager getServiceSchemaManager(
            String serviceName,
            SSOToken ssoToken) {
        ServiceSchemaManager mgr = null;
        if (serviceName != null) {
            try {
                mgr = new ServiceSchemaManager(serviceName, ssoToken);
            } catch (SSOException e) {
                log("SchemaCommand.getServiceSchemaManager" + e.getMessage());
            } catch (SMSException e) {
                log("SchemaCommand.getServiceSchemaManager" + e.getMessage());
            }
        }
        return mgr;
    }

    /**
     * Returns the <code>ServiceSchema</code> of a service.
     * 
     * @param serviceName the service name
     * @param subSchemaName the sub schema.
     * @param schemaType the schema type.
     * @return the <code>ServiceSchema</code> object.
     * @throws UpgradeException if there is an error.
     */
    static ServiceSchema getServiceSchema(String serviceName,
            String subSchemaName, String schemaType)
            throws UpgradeException {
        ServiceSchema ss = null;
        try {
            SchemaType sschemaType = getSchemaType(schemaType);
            ServiceSchemaManager ssm = getServiceSchemaManager(serviceName);
            ss = ssm.getSchema(schemaType);
            if (subSchemaName != null) {
                ss = ss.getSubSchema(subSchemaName);
            }
        } catch (SMSException sme) {
            throw new UpgradeException("Cannot get service schema");
        }
        return ss;
    }

    /**
     * Returns the <code>SchemaType</code>
     * 
     * @param schemaTypeName the schema type string value
     * @return the <code>SchemaType</code> object.
     */
    private static SchemaType getSchemaType(String schemaTypeName) {
        SchemaType schemaType = null;
        if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_GLOBAL)) {
            schemaType = SchemaType.GLOBAL;
        } else if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_ORGANIZATION)) {
            schemaType = SchemaType.ORGANIZATION;
        } else if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_DYNAMIC)) {
            schemaType = SchemaType.DYNAMIC;
        } else if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_USER)) {
            schemaType = SchemaType.USER;
        } else if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_POLICY)) {
            schemaType = SchemaType.POLICY;
        }
        return schemaType;
    }

    /** 
     * Returns the <code>ServiceManager</code>.
     * 
     * @return the <code>ServiceManager</code> object.
     * @throws <code>UpgradeException</cpde> if there is an error.
     */
    private static ServiceManager getServiceManager() throws UpgradeException {
        ServiceManager ssm = null;
        try {
            ssm = new ServiceManager(ssoToken);
        } catch (SMSException e) {
            throw new UpgradeException("Error creating Service manager");
        } catch (SSOException e) {
            throw new UpgradeException("Invalid SSOToken");
        }
        return ssm;
    }

    /**
     * Adds module names to the list of authenticators in core auth
     * service.
     * 
     * @param moduleName a set of authentication module names.
     * @throws UpgradeException if there is an error.
     */
    public static void updateAuthenticatorsList(Set moduleName)
            throws UpgradeException {
        addAttributeDefaultValues(AUTH_SERVICE_NAME, null, SCHEMA_TYPE_GLOBAL,
                AUTH_ATTR_NAME, moduleName);
    }
}

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
 * $Id: FederationManagerCLI.java,v 1.7 2007-08-02 16:48:35 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common.cli;

import com.sun.identity.qatest.cli.CLIConstants;
import com.sun.identity.qatest.cli.CommandMessages;
import com.sun.identity.qatest.cli.FederationManagerCLIConstants;
import com.sun.identity.qatest.cli.GlobalConstants;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

/**
 * <code>AccessMangerCLI</code> is a utility class which allows the user
 * to invoke the famadm CLI to perform operations which correspond to supported
 * sub-commands of famadm (e.g. create-realm, delete-realm, list-realms,
 * create-identity, delete-identity, list-identities, etc.).
 */
public class FederationManagerCLI extends CLIUtility 
        implements CLIConstants, FederationManagerCLIConstants, 
        GlobalConstants {

    private String passwdFile;
    private boolean usePasswdFile;
    private boolean useDebugOption;
    private boolean useVerboseOption;
    private boolean useLongOptions;
    private long commandTimeout;
    private static int SUBCOMMAND_VALUE_INDEX = 1;
    private static int ADMIN_ID_ARG_INDEX = 2;
    private static int ADMIN_ID_VALUE_INDEX = 3;
    private static int PASSWORD_ARG_INDEX = 4;
    private static int PASSWORD_VALUE_INDEX = 5;
    private static int DEFAULT_COMMAND_TIMEOUT = 20;
    
    /**
     * Create a new instance of <code>FederationManagerCLI</code>
     * @param useDebug - a flag indicating whether to add the debug option
     * @param useVerbose - a flag indicating whether to add the verbose option
     * @param useLongOpts - a flag indicating whether long options 
     * (e.g. --realm) should be used
     */
    public FederationManagerCLI(boolean useDebug, boolean useVerbose, 
            boolean useLongOpts)
    throws Exception {
        super(cliPath + System.getProperty("file.separator") + "famadm");    
        useLongOptions = useLongOpts;
        try {
            addAdminUserArgs();
            createPasswordFile();  
            addPasswordArgs();
            useDebugOption = useDebug;
            useVerboseOption = useVerbose;
            commandTimeout = (new Long(timeout).longValue()) * 1000;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;         
        }        
    }
     
    /**
     * Sets the "--adminid" and admin user ID arguments in the argument list.
     */
    private void addAdminUserArgs() {
        String adminArg;
        if (useLongOptions) {
            adminArg = PREFIX_ARGUMENT_LONG + ARGUMENT_ADMIN_ID;
        } else {
            adminArg = PREFIX_ARGUMENT_SHORT + SHORT_ARGUMENT_ADMIN_ID;
        }
        setArgument(ADMIN_ID_ARG_INDEX, adminArg);
        setArgument(ADMIN_ID_VALUE_INDEX, adminUser);
    }
    
    /**
     * Sets "--passwordfile" and the password file path in the argument list.
     */
    private void addPasswordArgs() {
        String passwordArg;
        
        if (useLongOptions) {
            passwordArg = PREFIX_ARGUMENT_LONG + ARGUMENT_PASSWORD_FILE;
        } else {
            passwordArg = PREFIX_ARGUMENT_SHORT + SHORT_ARGUMENT_PASSWORD_FILE;
        }
        
        setArgument(PASSWORD_ARG_INDEX, passwordArg);
        setArgument(PASSWORD_VALUE_INDEX, passwdFile);
    }
    
    /**
     * Adds the "--debug" argument to the argument list.
     */
    private void addDebugArg() {
        String debugArg;
        if (useLongOptions) {
            debugArg = PREFIX_ARGUMENT_LONG + DEBUG_ARGUMENT;
        } else {
            debugArg = PREFIX_ARGUMENT_SHORT + SHORT_DEBUG_ARGUMENT;
        }
        addArgument(debugArg);
    }
    
    /**
     * Adds the "--verbose" argument to the arugment list.
     */
    private void addVerboseArg() {
        String verboseArg;
        if (useLongOptions) {
            verboseArg = PREFIX_ARGUMENT_LONG + VERBOSE_ARGUMENT;
        } else {
            verboseArg = PREFIX_ARGUMENT_SHORT + SHORT_VERBOSE_ARGUMENT;
        }
        addArgument(verboseArg);
    }
    
    /**
     * Adds the "--locale" arugment and the locale value to the argument list.
     */
    private void addLocaleArgs() {
        String localeArg;
        if (useLongOptions) {
            localeArg = PREFIX_ARGUMENT_LONG + LOCALE_ARGUMENT;
        } else {
            localeArg = PREFIX_ARGUMENT_SHORT + SHORT_LOCALE_ARGUMENT;
        }
        addArgument(localeArg);
        addArgument(localeValue);
    }
    
    /**
     * Adds the global arguments (--debug, --verbose, --locale) to the argument
     * list if they are specified.
     */
    private void addGlobalOptions() {
        if (useDebugOption) {
            addDebugArg();
        }
        if (useVerboseOption) {
            addVerboseArg();
        }
        if (localeValue != null) {
            addLocaleArgs();
        }
    }
    
    /**
     * Creates a password file in the platform specific temporary directory.  
     * The file is marked to be deleted on exit.
     */
    private void createPasswordFile() {
        try {
            ResourceBundle rb_amconfig = ResourceBundle.getBundle(
                    TestConstants.TEST_PROPERTY_AMCONFIG);
            String passFileDir = getBaseDir() + fileseparator + 
                    rb_amconfig.getString(TestConstants.KEY_ATT_SERVER_NAME) + 
                    fileseparator + "built" + fileseparator + "classes" + 
                    fileseparator;
            File passFile = File.createTempFile("passwd", ".txt", 
                    new File(passFileDir)); 
            passFile.deleteOnExit();
            PrintWriter fileWriter = new PrintWriter(passFile);
            fileWriter.print(adminPassword);
            fileWriter.flush();
            fileWriter.close();
            setPasswordFile(passFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    /**
     * Adds the "--realm" argument and realm value to the argument list
     * @param realm - the realm value to add to the argument list
     */
    private void addRealmArguments(String realm) {
        String realmArg;
        if (useLongOptions) {
            realmArg = PREFIX_ARGUMENT_LONG + REALM_ARGUMENT;
        } else {
            realmArg = PREFIX_ARGUMENT_SHORT + SHORT_REALM_ARGUMENT;
        }
        addArgument(realmArg);
        addArgument(realm);
    }
    
    /**
     * Adds the "--recursive" argument to the argument list.
     */
    private void addRecursiveArgument() {
        String recursiveArg;
        if (useLongOptions) {
            recursiveArg = PREFIX_ARGUMENT_LONG + RECURSIVE_ARGUMENT;
        } else {
            recursiveArg = PREFIX_ARGUMENT_SHORT + SHORT_RECURSIVE_ARGUMENT;
        }
        addArgument(recursiveArg);
    }
    
    /**
     * Adds the "--filter" argument to the argument list.
     */
    private void addFilterArguments(String filter) {
        String filterArg;
        if (useLongOptions) {
            filterArg = PREFIX_ARGUMENT_LONG + FILTER_ARGUMENT;
        } else {
            filterArg = PREFIX_ARGUMENT_SHORT + SHORT_FILTER_ARGUMENT;
        }
        addArgument(filterArg);
        addArgument(filter);
    }
    
    /**
     * Adds the "--idname" and identity name arguments to the argument list.
     */
    private void addIdnameArguments(String name) {
        String idnameArg;
        if (useLongOptions) {
            idnameArg = PREFIX_ARGUMENT_LONG + ID_NAME_ARGUMENT;
        } else {
            idnameArg = PREFIX_ARGUMENT_SHORT + SHORT_ID_NAME_ARGUMENT;
        }
        addArgument(idnameArg);
        addArgument(name);
    }
    
    /**
     * Adds the "--idtype" and identity type arguments to the argument list.
     */
    private void addIdtypeArguments(String type) {
        String idtypeArg;
        if (useLongOptions) {
            idtypeArg = PREFIX_ARGUMENT_LONG + ID_TYPE_ARGUMENT;
        } else {
            idtypeArg = PREFIX_ARGUMENT_SHORT + SHORT_ID_TYPE_ARGUMENT;
        }
        addArgument(idtypeArg);
        addArgument(type);
    }
    
    /**
     * Adds the "--memberidname" and member identity name arguments to the 
     * argument list.
     */
    private void addMemberIdnameArguments(String name) {
        String idnameArg;
        if (useLongOptions) {
            idnameArg = PREFIX_ARGUMENT_LONG + MEMBER_ID_NAME_ARGUMENT;
        } else {
            idnameArg = PREFIX_ARGUMENT_SHORT + SHORT_MEMBER_ID_NAME_ARGUMENT;
        }
        addArgument(idnameArg);
        addArgument(name);        
    }
    
    /**
     * Adds the "--memberidtype" and member identity type arguments to the 
     * argument list.
     */
    private void addMemberIdtypeArguments(String type) {
        String idtypeArg;
        if (useLongOptions) {
            idtypeArg = PREFIX_ARGUMENT_LONG + MEMBER_ID_TYPE_ARGUMENT;
        } else {
            idtypeArg = PREFIX_ARGUMENT_SHORT + SHORT_MEMBER_ID_TYPE_ARGUMENT;
        }
        addArgument(idtypeArg);
        addArgument(type);        
    }
    
    /**
     * Adds the "--membershipidtype" and membership identity type arguments to 
     * the argument list.
     */
    private void addMembershipIdtypeArguments(String type) {
        String idtypeArg;
        if (useLongOptions) {
            idtypeArg = PREFIX_ARGUMENT_LONG + MEMBERSHIP_ID_TYPE_ARGUMENT;
        } else {
            idtypeArg = PREFIX_ARGUMENT_SHORT + 
                    SHORT_MEMBERSHIP_ID_TYPE_ARGUMENT;
        }
        addArgument(idtypeArg);
        addArgument(type);        
    }    
    
    /**
     * Create a new realm.
     * 
     * @param realmToCreate - the name of the realm to be created
     * @return the exit status of the "create-realm" command
     */
    public int createRealm(String realmToCreate) 
    throws Exception {
        setSubcommand(CREATE_REALM_SUBCOMMAND);
        addRealmArguments(realmToCreate);
        addGlobalOptions();
        return (executeCommand(commandTimeout));
    }
    
    /**
     * Delete a realm.
     *
     * @param realmToDelete - the name of the realm to be deleted
     * @param recursiveDelete - a flag indicating whether the realms beneath 
     * realmToDelete should be recursively deleted as well
     * @return the exit status of the "delete-realm" command
     */
    public int deleteRealm(String realmToDelete, boolean recursiveDelete) 
    throws Exception {
        setSubcommand(DELETE_REALM_SUBCOMMAND);
        addRealmArguments(realmToDelete);
        if (recursiveDelete) {
            addRecursiveArgument();
        }
        addGlobalOptions();
        return (executeCommand(commandTimeout)); 
    }
    
    /**
     * Delete a realm without using recursion.
     * @param realmToDelete - the name of the realm to be deleted.
     * @return the exit status of the "delete-realm" command
     */
    public int deleteRealm(String realmToDelete) 
    throws Exception {
        return (deleteRealm(realmToDelete, false));
    }
    
    /**
     * List the realms which exist under a realm.
     *
     * @param startRealm - the realm from which to start the search
     * @param filter - a string containing a filter which will be used to 
     * restrict the realms that are returned (e.g. "*realms")
     * @param recursiveSearch - a boolean which should be set to "false" to 
     * perform a single level search or "true" to perform a recursive search
     * @return the exit status of the "list-realms" command
     */
    public int listRealms(String startRealm, String filter, 
            boolean recursiveSearch) 
    throws Exception {
        setSubcommand(LIST_REALMS_SUBCOMMAND);
        addRealmArguments(startRealm);        
        if (filter != null) {
            addFilterArguments(filter);
        }
        if (recursiveSearch) {
            addRecursiveArgument();
        }
        addGlobalOptions();
        return (executeCommand(commandTimeout)); 
    }
    
    /**
     * Perform a listing of realms with no filter
     * @param startRealm - the realm from which to start the search
     * @param recursiveSearch - a boolean which should be set to "false" to 
     * perform a single level search or "true" to perform a recursive search
     * @return the exit status of the "list-realms" command
     */
    public int listRealms (String startRealm, boolean recursiveSearch) 
    throws Exception {
        return listRealms(startRealm, null, recursiveSearch);
    }
    
    /**
     * Perform a non-recursive listing of realms with no filter
     * @param startRealm - the realm from which to start the search
     * @return the exit status of the "list-realms" command
     */
    public int listRealms (String startRealm) 
    throws Exception {
        return listRealms(startRealm, null, false);
    }
    
    /**
     * Create an identity in a realm
     * @param realm - the realm in which to create the identity
     * @param name - the name of the identity to be created
     * @param type - the type of identity to be created (e.g. "User", "Role", 
     * and "Group")
     * @param attributeValues - a string containing the attribute values for the 
     * identity to be created
     * @param useAttributeValues - a boolean flag indicating whether the 
     * "--attributevalues" option should be used 
     * @param useDatafile - a boolean flag indicating whether the attribute 
     * values should be written to a file and passed to the CLI using the 
     * "--datafile <file-path>" arguments
     * @return the exit status of the "create-identity" command
     */
    public int createIdentity(String realm, String name, String type, 
            List attributeValues, boolean useAttributeValues, 
            boolean useDatafile) 
    throws Exception {
        setSubcommand(CREATE_IDENTITY_SUBCOMMAND);
        addRealmArguments(realm);
        addIdnameArguments(name);
        addIdtypeArguments(type);
        
        if (attributeValues != null) {
            if (useAttributeValues) {
                addAttributevaluesArguments(attributeValues);
            }
            if (useDatafile) {
                addDatafileArguments(attributeValues, "attrValues", ".txt");
            }
        }
        addGlobalOptions();
        return (executeCommand(commandTimeout));
    }       
    
    /**
     * Create an identity in a realm
     * @param realm - the realm in which to create the identity
     * @param name - the name of the identity to be created
     * @param type - the type of identity to be created (e.g. "User", "Role", 
     * and "Group")
     * @param attributeValues - a string containing the attribute values for the 
     * identity to be created
     * @param useAttributeValues - a boolean flag indicating whether the 
     * "--attributevalues" option should be used 
     * @param useDatafile - a boolean flag indicating whether the attribute 
     * values should be written to a file and passed to the CLI using the 
     * "--datafile <file-path>" arguments
     * @return the exit status of the "create-identity" command
     */
    public int createIdentity(String realm, String name, String type, 
            String attributeValues, boolean useAttributeValues, 
            boolean useDatafile) 
    throws Exception {
        setSubcommand(CREATE_IDENTITY_SUBCOMMAND);
        addRealmArguments(realm);
        addIdnameArguments(name);
        addIdtypeArguments(type);
        
        if (attributeValues != null) {
            ArrayList attributeList = new ArrayList();
            
            if (useAttributeValues) {
                addAttributevaluesArguments(attributeValues);
            } 
            if (useDatafile) {
                addDatafileArguments(attributeValues, "attrValues", ".txt");
            }
        }
        addGlobalOptions();
        return (executeCommand(commandTimeout));
    }
    
    /**
     * Create an identity in a realm using the "--attribute-values" argument
     * @param realm - the realm in which to create the identity
     * @param name - the name of the identity to be created
     * @param type - the type of identity to be created (e.g. "User", "Role", 
     * and "Group")
     * @param attributeValues - a semi-colon delimited string containing the 
     * attribute values for the identity to be created
     * @return the exit status of the "create-identity" command
     */
    public int createIdentity(String realm, String name, String type, 
            String attributeValues)
    throws Exception {
        return (createIdentity(realm, name, type, attributeValues, true, 
                false));
    }
    
    /**
     * Create an identity in a realm using the "--attribute-values" argument
     * @param realm - the realm in which to create the identity
     * @param name - the name of the identity to be created
     * @param type - the type of identity to be created (e.g. "User", "Role", 
     * and "Group")
     * @return the exit status of the "create-identity" command
     */
    public int createIdentity(String realm, String name, String type)
    throws Exception {
        String emptyString = null;
        return (createIdentity(realm, name, type, emptyString, false, false));
    }    
    
    /**
     * Delete one or more identities in a realm
     * @param realm - the realm from which the identies should be deleted
     * @param names - one or more identity names to be deleted
     * @param type - the type of the identity (identities) to be deleted
     * @return the exit status of the "delete-identities" command
     */
    public int deleteIdentities(String realm, String names, String type)
    throws Exception {
        setSubcommand(DELETE_IDENTITIES_SUBCOMMAND);
        addRealmArguments(realm);
        addIdnamesArguments(names);
        addIdtypeArguments(type);
        addGlobalOptions();
        return (executeCommand(commandTimeout));
    }
    
    /**
     * List the identities in a particular realm
     * @param realm - the realm in which to start the search for identities
     * @param filter - the filter to apply in the search for identities
     * @param idtype - the type of identities (e.g. "User", "Group", "Role") for
     * which the search sould be performed
     * @return the exit status of the "list-identities" command
     */
    public int listIdentities(String realm, String filter, String idtype)
    throws Exception {
        setSubcommand(LIST_IDENTITIES_SUBCOMMAND);
        addRealmArguments(realm);
        addFilterArguments(filter);
        addIdtypeArguments(idtype);
        addGlobalOptions();
        return (executeCommand(commandTimeout));
    }
    
    /**
     * Add an identity as a member of another identity.
     * @param realm - the realm in which the member identity should be added.
     * @param memberName - the name of the identity which should be added as a
     * member.
     * @param memberType - the type of the identity which should be added as a 
     * member.
     * @param idName - the name of the identity in which the member should be 
     * added.
     * @param idType - the type of the identity in which the member should be 
     * added.
     * @return the exit status of the "add-member" command
     */
    public int addMember(String realm, String memberName, String memberType, 
            String idName, String idType)
    throws Exception {
        setSubcommand(ADD_MEMBER_SUBCOMMAND);
        addRealmArguments(realm);
        addMemberIdnameArguments(memberName);
        addMemberIdtypeArguments(memberType);
        addIdnameArguments(idName);
        addIdtypeArguments(idType);
        addGlobalOptions();
        return (executeCommand(commandTimeout));
    }
    
    /**
     * Remove an identity as a member of another identity.
     * @param realm - the realm in which the member identity should be removed.
     * @param memberName - the name of the identity which should be removed as a
     * member.
     * @param memberType - the type of the identity which should be removed as a 
     * member.
     * @param idName - the name of the identity in which the member should be 
     * removed.
     * @param idType - the type of the identity in which the member should be 
     * removed.
     * @return the exit status of the "remove-member" command
     */
    public int removeMember(String realm, String memberName, String memberType, 
            String idName, String idType)
    throws Exception {
        setSubcommand(REMOVE_MEMBER_SUBCOMMAND);
        addRealmArguments(realm);
        addMemberIdnameArguments(memberName);
        addMemberIdtypeArguments(memberType);
        addIdnameArguments(idName);
        addIdtypeArguments(idType);
        addGlobalOptions();
        return (executeCommand(commandTimeout));       
    }
    
    /**
     * Display the member identities of an identity.
     * @param realm - the realm in which the identity exists.
     * @param membershipType - the identity type of the member which should be
     * displayed.
     * @param idName - the name of the identity for which the members should be 
     * displayed.
     * @param idType - the type of the identity for which the members should be 
     * displayed.
     * @return the exit status of the "show-members" command.
     */
    public int showMembers(String realm, String membershipType, String idName, 
            String idType)
    throws Exception {
        setSubcommand(SHOW_MEMBERS_SUBCOMMAND);
        addRealmArguments(realm);
        addMembershipIdtypeArguments(membershipType);
        addIdnameArguments(idName);
        addIdtypeArguments(idType);
        addGlobalOptions();
        return (executeCommand(commandTimeout));            
    } 
    
    /**
     * Display the identities of which an identity is a member.
     * @param realm - the realm in which the identity exists.
     * @param membershipType - the type of membership which should be displayed.
     * @param idName - the identity name for which the memberships should be 
     * displayed.
     * @param idType - the identity type for which the memberships should be 
     * displayed.
     * @return the exit status of the "show-memberships" command.
     */
    public int showMemberships(String realm, String membershipType, 
            String idName, String idType)
    throws Exception {
        setSubcommand(SHOW_MEMBERSHIPS_SUBCOMMAND);
        addRealmArguments(realm);
        addMembershipIdtypeArguments(membershipType);
        addIdnameArguments(idName);
        addIdtypeArguments(idType);
        addGlobalOptions();
        return (executeCommand(commandTimeout));             
    }        
    
    /**
     * Execute the command specified by the subcommand and teh argument list
     * @param subcommand - the CLI subcommand to be executed
     * @param argList - a String containing a list of arguments separated by 
     * semicolons (';').
     * @return the exit status of the CLI command
     */
    public int executeCommand(String subcommand, String argList)
    throws Exception {
        setSubcommand(subcommand);
        String [] args = argList.split(";");
        for (String arg: args) {
            addArgument(arg);
        }
        addGlobalOptions();
        return (executeCommand(commandTimeout));
    }
    
    /**
     * Iterate through a list containing attribute values and add the 
     * "--attributevalues" argument and a list of one attribute name/value pairs
     * to the argument list
     * @param valueList - a List object containing one or more attribute 
     * name/value pairs.
     */
    private void addAttributevaluesArguments(List valueList) 
    throws Exception {
       String attributesArg;
       if (useLongOptions) {
           attributesArg = PREFIX_ARGUMENT_LONG + ATTRIBUTE_VALUES_ARGUMENT;
       } else {
           attributesArg = PREFIX_ARGUMENT_SHORT + 
                   SHORT_ATTRIBUTE_VALUES_ARGUMENT;
       }
       addArgument(attributesArg);
       
       Iterator i = valueList.iterator();
       while (i.hasNext()) {
           addArgument((String)i.next());
       }
    }    
    
    
    /**
     * Parse a string containing attribute values and add the 
     * "--attributevalues" and a list of one attribute name/value pairs to the
     * argument list
     * 
     */
    private void addAttributevaluesArguments(String values) 
    throws Exception {
       StringTokenizer tokenizer = new StringTokenizer(values, ";");        
       ArrayList attList = new ArrayList(tokenizer.countTokens());
       
       while (tokenizer.hasMoreTokens()) {
           attList.add(tokenizer.nextToken());
       }
       addAttributevaluesArguments(attList);
    }

    /**
     * Create a datafile and add the "--datafile" and file path arguments to the
     * argument list.
     * @param valueList - a list containing attribute name value pairs separated 
     * by semi-colons (';')
     * @param filePrefix - a string containing a prefix for the datafile that 
     * will be created
     * @param fileSuffix - a string containing a suffix for the datafile that 
     * will be created
     */
    private void addDatafileArguments(List valueList, String filePrefix,
            String fileSuffix)
    throws Exception {
        StringBuffer valueBuffer = new StringBuffer();
        Iterator i = valueList.iterator();
        while (i.hasNext()) {
            valueBuffer.append((String)i.next());
            if (i.hasNext()) {
                valueBuffer.append(";");
            }
        }
        String values = valueBuffer.toString();
        addDatafileArguments(values, filePrefix, fileSuffix);
    }    
    
    /**
     * Create a datafile and add the "--datafile" and file path arguments to the
     * argument list.
     * @param values - a string containing attribute name value pairs separated 
     * by semi-colons (';')
     * @param filePrefix - a string containing a prefix for the datafile that 
     * will be created
     * @param fileSuffix - a string containing a suffix for the datafile that 
     * will be created
     */
    private void addDatafileArguments(String values, String filePrefix,
            String fileSuffix)
    throws Exception {
        Map attributeMap = parseStringToMap(values.replaceAll("\"",""));
        ResourceBundle rb_amconfig = 
                ResourceBundle.getBundle(TestConstants.TEST_PROPERTY_AMCONFIG);
        String attFileDir = getBaseDir() + fileseparator + 
                rb_amconfig.getString(TestConstants.KEY_ATT_SERVER_NAME) + 
                fileseparator + "built" + fileseparator + "classes" + 
                fileseparator;
        String attFile = attFileDir + filePrefix + 
                (new Integer(new Random().nextInt())).toString() + fileSuffix;
        createFileFromMap(attributeMap, attFile);
        String dataFileArg;
        if (useLongOptions) {
            dataFileArg = PREFIX_ARGUMENT_LONG + DATA_FILE_ARGUMENT;
        } else {
            dataFileArg = PREFIX_ARGUMENT_SHORT + SHORT_DATA_FILE_ARGUMENT;
        }
        addArgument(dataFileArg);
        addArgument(attFile);
    }
    
    /**
     * Add the "--idnames" argument and value to the argument list
     */
    private void addIdnamesArguments(String names) {
        String idnamesArg;
        if (useLongOptions) {
            idnamesArg = PREFIX_ARGUMENT_LONG + ID_NAMES_ARGUMENT;
        } else {
            idnamesArg = PREFIX_ARGUMENT_SHORT + SHORT_ID_NAME_ARGUMENT;
        }

        addArgument(idnamesArg);
        
        StringTokenizer tokenizer = new StringTokenizer(names);
        while (tokenizer.hasMoreTokens()) {
            addArgument(tokenizer.nextToken());
        }
    }
       
    /**
     * Retrieve the supported identity types for a particular realm
     * @param realm - the realm for which the supported identity types should be
     * retrieved
     * @return an array String containing the identity types that were returned
     * or null if the show-identity-types command times out or returns a 
     * non-zero exit status
     */
    public String[] showIdentityTypes(String realm) 
    throws Exception {
        setSubcommand(SHOW_IDENTITY_TYPES_SUBCOMMAND);
        addRealmArguments(realm);
        if (executeCommand(commandTimeout) == 0) {
            return ((String [])tokenizeOutputBuffer().toArray());
        } else {
            return null;
        }
    }
    
    /**
     * Sets the sub-command in the second argument of the argument list
     * @param command - the sub-command value to be stored
     */
    private void setSubcommand(String command) { 
        setArgument(SUBCOMMAND_VALUE_INDEX, command);
    }
    
    /**
     * Sets the user ID of the user that will execute the CLI.
     * @param  user - the user ID of the CLI
     */
    private void setAdminUser(String user) { adminUser = user; }
    
    /**
     * Sets the password for the admin user that will execute the CLI
     * @param passwd - the value of the admin user's password
     */
    private void setAdminPassword(String passwd) { adminPassword = passwd; }
    
    /**
     * Sets the member variable passwdFile to the name of the file containing
     * the CLI user's password for use with the "--passwordfile" argument.
     * @param fileName - the file containing the CLI user's password
     */
    private void setPasswordFile(String fileName) { passwdFile = fileName; }
    
    /**
     * Clear all arguments following the value of the admin user's password
     * or the password file.  Removes all sub-command specific arguments.
     */
    public void resetArgList() {
        clearArguments(PASSWORD_VALUE_INDEX);
    }
    
    /**
     * Check to see if a realm exists using the "famadm list-realms" command
     * @param realmsToFind - the realm or realms to find in the output of 
     * "famadm list-realms".  Multiple realms should be separated by semi-colons
     * (';').
     * @return a boolean value of true if the realm(s) is(are) found and false 
     * if one or more realms is not found.
     */
    public boolean findRealms(String startRealm, String filter, 
            boolean recursiveSearch, String realmsToFind)
    throws Exception {
        boolean realmsFound = true;
        
        if ((realmsToFind != null) && (realmsToFind.length() > 0)) {
            if (listRealms(startRealm, filter, recursiveSearch) == 0) {                    
                StringTokenizer tokenizer = new StringTokenizer(realmsToFind, 
                        ";");
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (token != null) {
                        if (token.length() > 1) {
                            String searchRealm = token.substring(1);
                            if (!findStringInOutput(searchRealm)) {
                                log(logLevel, "findRealms", "Realm " + 
                                        searchRealm + " was not found.");
                                realmsFound = false;
                            } else {
                                log(logLevel, "findRealms", "Realm " + 
                                  searchRealm + " was found.");
                            }
                        } else {
                            log(Level.SEVERE, "findRealms", "Realm " + token + 
                                    " should be longer than 1 character.");
                            realmsFound = false;
                        }
                    } else {
                        log(Level.SEVERE, "findRealms", "Realm " + token + 
                                " in realmsToFind is null.");
                        realmsFound = false;
                    }
                }
            } else {
                log(Level.SEVERE, "findRealms", 
                        "famadm list-realms command failed");
                realmsFound = false;
            }
            logCommand("findRealms");
        } else {
            log(Level.SEVERE, "findRealms", "realmsToFind is null or empty");
            realmsFound = false;
        }
        return realmsFound;
    }
    
    /**
     * Check to see if a realm exists using the "famadm list-realms" command
     * @param realmsToFind - the realm or realms to find in the output of 
     * "famadm list-realms".  Multiple realms should be separated by semi-colons
     * (';').
     * @return a boolean value of true if the realm(s) is(are) found and false 
     * if one or more realms is not found.
     */
    public boolean findRealms(String realmsToFind)
    throws Exception {
        return(findRealms(TestCommon.realm, "*", true, realmsToFind));
    }  
    
    /**
     * Check to see if an identity exists using the "famadm list-identities" 
     * command.
     * @param startRealm - the realm in which to find identities
     * @param filter - the filter that will be applied in the search
     * @param type - the type of identities (User, Group, Role) for which the 
     * search will be performed
     * @param idsToFind - the identity or identities to find in the output of 
     * "famadm list-identities".  Multiple identities should be separated by 
     * a space (' ').
     * @return a boolean value of true if the identity(ies) is(are) found and 
     * false if one or more identities is not found.
     */
    public boolean findIdentities(String startRealm, String filter, String type,
            String idsToFind)
    throws Exception {
        boolean idsFound = true;
        
        if ((idsToFind != null) && (idsToFind.length() > 0)) {
            if (listIdentities(startRealm, filter, type) == 0) {
                String [] ids = idsToFind.split(";");
                for (int i=0; i < ids.length; i++) {
                    String token = ids[i];
                    String rootDN = "";
                    if (token != null) {
                        if (startRealm.equals(TestCommon.realm)) {
                            rootDN = TestCommon.basedn; 
                        } else {
                            String [] realms = startRealm.split("/");
                            StringBuffer buffer = new StringBuffer();
                            for (int j = realms.length-1; j >= 0; j--) {
                                if (realms[j].length() > 0) {
                                    buffer.append("o=" + realms[j] + ",");
                                }
                            }
                            buffer.append("ou=services,").
                                    append(TestCommon.basedn);
                            rootDN = buffer.toString();
                        }
                        if (token.length() > 0) {
                            String idString = token + " (id=" + token + ",ou=" + 
                                    type.toLowerCase() + "," + rootDN + ")";
                            if (!findStringInOutput(idString)) {
                                log(logLevel, "findIdentities", "String \'" + 
                                        idString + "\' was not found.");
                                idsFound = false;
                            } else {
                                log(logLevel, "findIdentities", type + 
                                        " identity " + token + " was found.");
                            }
                        } else {
                            log(Level.SEVERE, "findIdentities", 
                                    "The identity to find is empty.");
                            idsFound = false;
                        }
                    } else {
                        log(Level.SEVERE, "findIdentities", 
                                "Identity in idsToFind is null.");
                        idsFound = false;
                    }
                }
            } else {
                log(Level.SEVERE, "findIdentities", 
                        "famadm list-identities command failed");
                idsFound = false;
            }
            logCommand("findIdentities");
        } else {
            log(Level.SEVERE, "findIdentities", "idsToFind is null or empty");
            idsFound = false;
        }
        return idsFound;
    }

   /**
     * Check to see if an identity exists using the "famadm show-members" 
     * command.
     * @param startRealm - the realm in which to find identities
     * @param filter - the filter that will be applied in the search
     * @param type - the type of identities (User, Group, Role) for which the 
     * search will be performed
     * @param membersToFind - the member identity or identities to find in the 
     * output of "famadm show-members".  Multiple identities should be separated 
     * by a semicolon (';').
     * @return a boolean value of true if the member(s) is(are) found and 
     * false if one or more members is not found.
     */
    public boolean findMembers(String startRealm, String idName, String idType,
            String memberType, String membersToFind)
    throws Exception {
        boolean membersFound = true;
        
        if ((membersToFind != null) && (membersToFind.length() > 0)) {
            if (showMembers(startRealm, memberType, idName, idType) == 0) {
                String [] members = membersToFind.split(";");
                for (int i=0; i < members.length; i++) {
                    String rootDN = "";
                    if (members[i] != null) {
                        if (startRealm.equals(TestCommon.realm)) {
                            rootDN = TestCommon.basedn; 
                        } else {
                            String [] realms = startRealm.split("/");
                            StringBuffer realmBuffer = new StringBuffer();
                            for (int j = realms.length-1; j >= 0; j--) {
                                if (realms[j].length() > 0) {
                                    realmBuffer.append("o=" + realms[j] + ",");
                                }
                            }
                            realmBuffer.append("ou=services,").
                                    append(TestCommon.basedn);
                            rootDN = realmBuffer.toString();
                        }
                        if (members[i].length() > 0) {
                            StringBuffer idBuffer = 
                                    new StringBuffer(members[i]);
                            idBuffer.append(" (id=").append(members[i]).
                                    append(",ou=").
                                    append(memberType.toLowerCase()).
                                    append(",").append(rootDN).append(")");
                            if (!findStringInOutput(idBuffer.toString())) {
                                log(Level.FINEST, "findMember", 
                                        "String \'" + idBuffer.toString() + 
                                        "\' was not found.");
                                membersFound = false;
                            } else {
                                log(Level.FINEST, "findMembers", memberType + 
                                        " identity " + members[i] + 
                                        " was found.");
                            }
                        } else {
                            log(Level.SEVERE, "findMembers", 
                                    "The member to find is empty.");
                            membersFound = false;
                        }
                    } else {
                        log(Level.SEVERE, "findMembers", 
                                "Identity in membersToFind is null.");
                        membersFound = false;
                    }
                }
            } else {
                log(Level.SEVERE, "findMembers", 
                        "famadm show-members command failed");
                membersFound = false;
            }
            logCommand("findMembers");
        } else {
            log(Level.SEVERE, "findMembers", "membersToFind is null or empty");
            membersFound = false;
        }
        return membersFound;
    }    

    /**
     * Check to see if an identity exists using the "famadm show-memberships" 
     * command.
     * @param startRealm - the realm in which to find identities.
     * @param idName - the name of the identity for which memberships
     * should be found.
     * @param idType - the type of the identity (User) for which memberships
     * should be found.
     * @param membershipType - the types of memberships (Group, Role) which
     * should be found.
     * @param membershipsToFind - the memberships which are expected to be 
     * found in the output of "famadm show-memberships".  Multiple 
     * memberships should be separated by a semicolon (';').
     * @return a boolean value of true if the membership(s) is(are) found and 
     * false if one or more memberships is not found.
     */
    public boolean findMemberships(String startRealm, String idName,
            String idType, String membershipType, String membershipsToFind)
    throws Exception {
        boolean membershipsFound = true;

        if ((membershipsToFind != null) && (membershipsToFind.length() > 0)) {
            if (showMemberships(startRealm, membershipType, idName, 
                    idType) == 0) {
                String [] memberships = membershipsToFind.split(";");
                for (int i=0; i < memberships.length; i++) {
                    String rootDN = "";
                    if (memberships[i] != null) {
                        if (startRealm.equals(TestCommon.realm)) {
                            rootDN = TestCommon.basedn; 
                        } else {
                            String [] realms = startRealm.split("/");
                            StringBuffer realmBuffer = new StringBuffer();
                            for (int j = realms.length-1; j >= 0; j--) {
                                if (realms[j].length() > 0) {
                                    realmBuffer.append("o=" + realms[j] + ",");
                                }
                            }
                            realmBuffer.append("ou=services,").
                                    append(TestCommon.basedn);
                            rootDN = realmBuffer.toString();
                        }
                        if (memberships[i].length() > 0) {
                            StringBuffer idBuffer = 
                                    new StringBuffer(memberships[i]);
                            idBuffer.append(" (id=").append(memberships[i]).
                                    append(",ou=").
                                    append(membershipType.toLowerCase()).
                                    append(",").append(rootDN).append(")");
                            if (!findStringInOutput(idBuffer.toString())) {
                                log(Level.FINEST, "findMemberships", 
                                        "String \'" + idBuffer.toString() + 
                                        "\' was not found.");
                                membershipsFound = false;
                            } else {
                                log(Level.FINEST, "findMemberships", 
                                        membershipType + " identity " + 
                                        memberships[i] + " was found.");
                            }
                        } else {
                            log(Level.SEVERE, "findMemberships", 
                                    "The membership to find is empty.");
                            membershipsFound = false;
                        }
                    } else {
                        log(Level.SEVERE, "findMemberships", 
                                "Identity in membersToFind is null.");
                        membershipsFound = false;
                    }
                }
            } else {
                log(Level.SEVERE, "findMemberships", 
                        "famadm show-memberships command failed");
                membershipsFound = false;
            }
            logCommand("findMemberships");
        } else {
            log(Level.SEVERE, "findMemberships", 
                    "membersToFind is null or empty");
            membershipsFound = false;
        }
        return membershipsFound;
    }
}

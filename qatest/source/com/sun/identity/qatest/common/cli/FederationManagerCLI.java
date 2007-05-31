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
 * $Id: FederationManagerCLI.java,v 1.1 2007-05-31 19:40:24 cmwesley Exp $
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
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

/**
 * <code>AccessMangerCLI</code> is a utility class which allows the user
 * to invoke the fmadm CLI to perform operations which correspond to supported
 * sub-commands of fmadm (e.g. create-realm, delete-realm, list-realms,
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
     * Creates a new instance of <code>FederationManagerCLI</code>
     * @param createPasswdFile - a flag indicating whether to create a password
     *  file
     * @param useDebug - a flag indicating whether to add the debug option
     * @param useVerbose - a flag indicating whether to add the verbose option
     * @param useLongOpts - a flag indicating whether long options 
     * (e.g. --realm) should be used
     */
    public FederationManagerCLI(boolean createPasswdFile, boolean useDebug, 
            boolean useVerbose, boolean useLongOpts) 
    throws Exception {
        super(cliPath + System.getProperty("file.separator") + "fmadm");    
        useLongOptions = useLongOpts;
        try {
            addAdminUserArgs();
            usePasswdFile = createPasswdFile;
            if (usePasswdFile) {
               createPasswordFile();  
            }
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
     * Creates a new instance of <code>FederationManagerCLI</code> using less 
     * arguments.
     * Retrieves the administration user and password from AMClient.properties.
     * Turns off debug and verbose modes.  Does not set a locale value.
     * @param path - The absolute path to the amadm utility.
     */
    public FederationManagerCLI(boolean createPasswdFile) 
    throws Exception {
        this(createPasswdFile, false, false, true);
    }
    
    /**
     * Creates a new instance of <code>FederationManagerCLI</code> using less 
     * arguments.  Retrieves the administration user and password from 
     * AMClient.properties.  Does not create a password file.  Turns off debug 
     * and verbose modes.  Does not set a locale value.
     */
    public FederationManagerCLI() 
    throws Exception {
        this(false, false, false, true);
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
     * Sets "--password" and admin user's password or "--passwordfile" and the 
     * password file path in the argument list.
     */
    private void addPasswordArgs() {
        String passwordArg;
        String passwordValue;
        
        if (!usePasswdFile) {
            if (useLongOptions) {
                passwordArg = PREFIX_ARGUMENT_LONG + ARGUMENT_PASSWORD;
            } else {
                passwordArg = PREFIX_ARGUMENT_SHORT + SHORT_ARGUMENT_PASSWORD;
            }
            passwordValue = adminPassword;
            
        } else {
            if (useLongOptions) {
                passwordArg = PREFIX_ARGUMENT_LONG + ARGUMENT_PASSWORD_FILE;
            } else {
                passwordArg = PREFIX_ARGUMENT_SHORT + 
                        SHORT_ARGUMENT_PASSWORD_FILE;
            }
            passwordValue = passwdFile;
        }
        
        setArgument(PASSWORD_ARG_INDEX, passwordArg);
        setArgument(PASSWORD_VALUE_INDEX, passwordValue);
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
     * @recursiveDelete - a flag indicating whether the realms beneath 
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
        if (filter.length() > 0) {
            addFilterArguments(filter);
        }
        addRealmArguments(startRealm);
        if (recursiveSearch) {
            addRecursiveArgument();
        }
        addGlobalOptions();
        return (executeCommand(commandTimeout)); 
    }
    
    /**
     * Perform a non-recursive listing of realms with no filter
     * @param startRealm - the realm from which to start the search
     * @return the exit status of the "list-realms" command
     */
    public int listRealms (String startRealm) 
    throws Exception {
        return listRealms(startRealm, "", false);
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
     * Check to see if a realm exists using the "fmadm list-realms" command
     * @param realmsToFind - the realm or realms to find in the output of 
     * "fmadm list-realms".  Multiple realms should be separated by semi-colons
     * (';').
     * @return a boolean value of true if the realm(s) is(are) found and false 
     * if one or more realms is not found.
     */
    public boolean findRealms(String realmsToFind)
    throws Exception {
        boolean realmsFound = true;
        
        if ((realmsToFind != null) && (realmsToFind.length() > 0)) {
            if (listRealms("/", "*", true) == 0) {                    
                StringTokenizer tokenizer = new StringTokenizer(realmsToFind, 
                        ";");
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (token != null) {
                        if (token.length() > 1) {
                            String searchRealm = token.substring(1);
                            if (!findStringInOutput(searchRealm)) {
                                log(logLevel, "findRealms", "Realm " + token + 
                                        " was not found.");
                                realmsFound = false;
                            } else {
                                log(logLevel, "findRealms", "Realm " + token + 
                                        " was found.");
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
                        "fmadm list-realms command failed");
                realmsFound = false;
            }
        } else {
            log(Level.SEVERE, "findRealms", "realmsToFind is null or empty");
            realmsFound = false;
        }
        return realmsFound;
    }
}

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
 * $Id: CreateIdentityTest.java,v 1.1 2007-06-20 18:56:57 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.cli;

import com.sun.identity.qatest.common.cli.FederationManagerCLI;
import com.sun.identity.qatest.common.TestCommon;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Reporter;

/**
 * <code>CreateIdentityTest</code> is used to execute tests involving the 
 * create-identity sub-command of fmadm.  This class allows the user to execute
 * "fmadm create-identity" with a variety or arguments (e.g with short or long 
 * options, with a password file or password argument, with a locale argument,
 * with a list of attributes or a datafile containing attributes, etc.) 
 * and a variety of input values.  The properties file 
 * <code>CreateIdentityTest.properties</code> contains the input values which 
 * are read by this class.
 */
public class CreateIdentityTest extends TestCommon {
    
    private String locTestName;
    private ResourceBundle rb;
    private String setupRealms;
    private String setupIdentities;
    private String realmForId;
    private String idNameToCreate;
    private String idTypeToCreate;
    private String idAttributeValues;
    private boolean usePasswordFile;
    private boolean useVerboseOption;
    private boolean useDebugOption;
    private boolean useLongOptions;
    private boolean useAttributeValuesOption;
    private boolean useDatafileOption;
    private String expectedMessage;
    private String expectedExitCode;
    private String description;
    private FederationManagerCLI cli;
    
    /** 
     * Creates a new instance of CreateIdentityTest 
     */
    public CreateIdentityTest() {
        super("CreateIdentityTest");
    }
    
    /**
     * This method is intended to provide initial setup.
     * Creates any realms specified in the setup-realms property in the 
     * CreateIdentityTest.properties.
     */
    @Parameters({"testName"})
    @BeforeClass(groups={"ff-local", "ldapv3-local", "ds-local"})
    public void setup(String testName) 
    throws Exception {
        Object[] params = {testName};
        entering("setup", params);
        try {
            locTestName = testName;
            rb = ResourceBundle.getBundle("CreateIdentityTest");
            setupRealms = (String)rb.getString(locTestName + 
                    "-create-setup-realms");
            setupIdentities = (String)rb.getString(locTestName + 
                    "-create-setup-identities");
            usePasswordFile = ((String)rb.getString(locTestName + 
                    "-use-password-file")).equals("true");
            useVerboseOption = ((String)rb.getString(locTestName + 
                    "-use-verbose-option")).equals("true");
            useDebugOption = ((String)rb.getString(locTestName + 
                    "-use-debug-option")).equals("true");
            useLongOptions = ((String)rb.getString(locTestName + 
                    "-use-long-options")).equals("true");
                
            log(logLevel, "setup", "use-password-file: " + usePasswordFile);
            log(logLevel, "setup", "use-verbose-option: " + useVerboseOption);
            log(logLevel, "setup", "use-debug-option: " + useDebugOption);
            log(logLevel, "setup", "use-long-options: " + useLongOptions);
            log(logLevel, "setup", "create-setup-realms: " + setupRealms);
            log(logLevel, "setup", "create-setup-identities: " + 
                    setupIdentities);
             
            Reporter.log("UsePasswordFile: " + usePasswordFile);
            Reporter.log("UseDebugOption: " + useDebugOption);
            Reporter.log("UseVerboseOption: " + useVerboseOption);
            Reporter.log("UseLongOptions: " + useLongOptions);
            Reporter.log("SetupRealms: " + setupRealms);
            Reporter.log("SetupIdentities: " + setupIdentities);

            cli = new FederationManagerCLI(usePasswordFile, useDebugOption, 
                    useVerboseOption, useLongOptions);
            
            if (setupRealms != null) {
                if (setupRealms.length() > 0) {
                    StringTokenizer tokenizer = new StringTokenizer(setupRealms, 
                            ";");
                    while (tokenizer.hasMoreTokens()) {
                        cli.createRealm(tokenizer.nextToken());
                        cli.logCommand("setup");
                        cli.resetArgList();
                    }
                }
            }
            
            if (setupIdentities != null) {
                if (setupIdentities.length() > 0) {
                    StringTokenizer idTokenizer = 
                            new StringTokenizer(setupIdentities, "|");
                    while (idTokenizer.hasMoreTokens()) {
                        StringTokenizer tokenizer = 
                                new StringTokenizer(idTokenizer.nextToken(), 
                                ",");
                        if (tokenizer.countTokens() >= 3) {
                            String idRealm = tokenizer.nextToken();
                            String idName = tokenizer.nextToken();
                            String idType = tokenizer.nextToken();
                            String idAttributes = null;
                            if (tokenizer.hasMoreTokens()) {
                                idAttributes = tokenizer.nextToken();
                                cli.createIdentity(idRealm, idName, idType, 
                                    idAttributes);
                            } else {
                                cli.createIdentity(idRealm, idName, idType);
                            }
                            cli.logCommand("setup");
                        } else {
                            log(Level.SEVERE, "setup", "The setup identity " + 
                                    setupIdentities + 
                                    " must have a realm, an " +
                                    "identity name, and an identity type");
                            assert false;
                        }
                    }
                }
            }
            exiting("setup");
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } 
    }
    
    /**
     * This method is used to execute tests involving "fmadm create-identities"
     * using input data from the CreateIdentityTest.properties file.
     */
    @Test(groups={"ff-local", "ldapv3-local", "ds-local"})
    public void testIdentityCreation() 
    throws Exception {
        entering("testIdentityCreation", null);
        boolean stringsFound = false;
        boolean idFound = false;
        
        try {
            expectedMessage = (String) rb.getString(locTestName + 
                    "-message-to-find");
            expectedExitCode = (String) rb.getString(locTestName + 
                    "-expected-exit-code");
            realmForId = (String) rb.getString(locTestName + 
                    "-create-identity-realm");
            idNameToCreate = (String) rb.getString(locTestName + 
                    "-create-identity-name");
            idTypeToCreate = (String) rb.getString(locTestName + 
                    "-create-identity-type");
            useAttributeValuesOption = ((String)rb.getString(locTestName + 
                    "-use-attribute-values-option")).equals("true");
            useDatafileOption = ((String)rb.getString(locTestName + 
                    "-use-datafile-option")).equals("true"); 
            idAttributeValues = (String) rb.getString(locTestName + 
                    "-create-identity-attributes");
            description = (String) rb.getString(locTestName + "-description");

            log(logLevel, "testIdentityCreation", "description: " + 
                    description);
            log(logLevel, "testIdentityCreation", "use-password-file: " + 
                    usePasswordFile);
            log(logLevel, "testIdentityCreation", "use-debug-option: " + 
                    useDebugOption);
            log(logLevel, "testIdentityCreation", "use-verbose-option: " + 
                    useVerboseOption);
            log(logLevel, "testIdentityCreation", "use-long-options: " + 
                    useLongOptions);
            log(logLevel, "testIdentityCreation", "message-to-find: " + 
                    expectedMessage);
            log(logLevel, "testIdentityCreation", "expected-exit-code: " + 
                    expectedExitCode);
            log(logLevel, "testIdentityCreation", "create-identity-realm: " + 
                    realmForId);
            log(logLevel, "testIdentityCreation", "create-identity-name: " + 
                    idNameToCreate);
            log(logLevel, "testIdentityCreation", "create-identity-type: " + 
                    idTypeToCreate);
            log(logLevel, "testIdentityCreation", 
                    "use-attribute-values-option: " + useAttributeValuesOption);
            log(logLevel, "testIdentityCreation", "use-datafile-option: " + 
                    useDatafileOption);
            log(logLevel, "testIdentityCreation", 
                    "create-identity-attributes: " + idAttributeValues);

            Reporter.log("TestName: " + locTestName);
            Reporter.log("Description: " + description);
            Reporter.log("UsePasswordFile: " + usePasswordFile);
            Reporter.log("UseDebugOption: " + useDebugOption);
            Reporter.log("UseVerboseOption: " + useVerboseOption);
            Reporter.log("UseLongOptions: " + useLongOptions);
            Reporter.log("ExpectedMessage: " + expectedMessage);
            Reporter.log("ExpectedExitCode: " + expectedExitCode);
            Reporter.log("IdRealmToCreate: " + realmForId);
            Reporter.log("IdNameToCreate: " + idNameToCreate);
            Reporter.log("IdTypeToCreate: " + idTypeToCreate);
            Reporter.log("UseAttributeValuesOption: " + 
                    useAttributeValuesOption);
            Reporter.log("UseDatafileOption: " + useDatafileOption);
            Reporter.log("IdAttributeValues: " + idAttributeValues);
            
            int commandStatus = cli.createIdentity(realmForId, idNameToCreate, 
                    idTypeToCreate, idAttributeValues, useAttributeValuesOption,
                    useDatafileOption);
            cli.logCommand("testIdentityCreation");
            cli.resetArgList();

            if (idNameToCreate.length() > 0) {
                FederationManagerCLI listCLI = 
                        new FederationManagerCLI(usePasswordFile, 
                        useDebugOption, useVerboseOption, useLongOptions);
                idFound = listCLI.findIdentities(realmForId, idNameToCreate, 
                        idTypeToCreate, idNameToCreate);
                log(logLevel, "testIdentityCreation", idTypeToCreate + 
                        "identity " + idNameToCreate + " Found: " + idFound);
            }

            if (expectedExitCode.equals("0")) {
                stringsFound = cli.findStringsInOutput(expectedMessage, ";");
                log(logLevel, "testIdentityCreation", "Output Messages Found: " + 
                        stringsFound);
                assert (commandStatus == 
                    new Integer(expectedExitCode).intValue()) && stringsFound &&
                        idFound;
            } else {
                stringsFound = cli.findStringsInError(expectedMessage, ";");
                log(logLevel, "testIdentityCreation", "Error Messages Found: " + 
                        stringsFound);
                assert (commandStatus == 
                    new Integer(expectedExitCode).intValue()) && stringsFound;
            }     
            exiting("testIdentityCreation");
        } catch (Exception e) {
            log(Level.SEVERE, "testIdentityCreation", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } 
    }
    
    /**
     * This method remove any realms that were created during the setup and
     * testIdentityCreation methods using "fmadm delete-identities".
     */
    @AfterClass(groups={"ff-local", "ldapv3-local", "ds-local"})
    public void cleanup() 
    throws Exception {
        entering("cleanup", null);
        try {            
            log(logLevel, "cleanup", "usePasswordFile: " + usePasswordFile);
            log(logLevel, "cleanup", "useDebugOption: " + useDebugOption);
            log(logLevel, "cleanup", "useVerboseOption: " + useVerboseOption);
            log(logLevel, "cleanup", "useLongOptions: " + useLongOptions);
            
            Reporter.log("UsePasswordFile: " + usePasswordFile);
            Reporter.log("UseDebugOption: " + useDebugOption);
            Reporter.log("UseVerboseOption: " + useVerboseOption);
            Reporter.log("UseLongOptions: " + useLongOptions);
            
            if (!idNameToCreate.equals("")) {
                log(logLevel, "cleanup", "identityToDelete: "  + 
                        idNameToCreate);
                Reporter.log("IdentityNameToDelete: " + idNameToCreate);
                cli.deleteIdentities(realmForId, idNameToCreate, 
                        idTypeToCreate);
                cli.logCommand("cleanup");
                cli.resetArgList();
            }
            
            if (setupIdentities != null) {
                if (setupIdentities.length() > 0) {
                    StringTokenizer idTokenizer = 
                            new StringTokenizer(setupIdentities, "|");
                    while (idTokenizer.hasMoreTokens()) {
                        StringTokenizer tokenizer = 
                                new StringTokenizer(idTokenizer.nextToken(), 
                                ",");
                        if (tokenizer.countTokens() >= 3) {
                            String idRealm = tokenizer.nextToken();
                            String idName = tokenizer.nextToken();
                            String idType = tokenizer.nextToken();
                            cli.deleteIdentities(idRealm, idName, idType);
                            cli.resetArgList();
                        } else {
                            log(Level.SEVERE, "cleanup", "The setup identity " + 
                                    setupIdentities + " must have a realm, " +
                                    "an identity name, and an identity type");
                            assert false;
                        }
                    }
                }
            }

            if (setupRealms != null) {
                if (setupRealms.length() > 0) {
                    StringTokenizer tokenizer = new StringTokenizer(setupRealms, 
                            ";");
                    List realmList = getListFromTokens(tokenizer);
                    int numOfRealms = realmList.size();
                    for (int i=numOfRealms-1; i>=0; i--) {
                        cli.deleteRealm((String)realmList.get(i));
                        cli.logCommand("cleanup");
                        cli.resetArgList();
                    }
                }
            }            
            
            exiting("cleanup");
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } 
    }
}

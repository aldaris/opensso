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
 * $Id: CreateRealmTest.java,v 1.3 2007-06-15 20:49:34 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.cli;

import com.sun.identity.qatest.common.cli.FederationManagerCLI;
import com.sun.identity.qatest.common.TestCommon;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Reporter;

/**
 * <code>CreateRealmTest</code> is used to execute tests involving the 
 * create-realm sub-command of fmadm.  This class allows the user to execute
 * "fmadm create-realm" with a variety or arguments (e.g with short or long 
 * options, with a password file or password argument, with a locale argument,
 * etc.) and a variety of input values.  The properties file 
 * <code>CreateRealmTest.properties</code> contains the input values which are 
 * read by this class.
 */
public class CreateRealmTest extends TestCommon {
    
    private String locTestName;
    private ResourceBundle rb;
    private String setupRealms;
    private String realmToCreate;
    private boolean usePasswordFile;
    private boolean useVerboseOption;
    private boolean useDebugOption;
    private boolean useLongOptions;
    private String expectedMessage;
    private String expectedExitCode;
    private String description;
    private FederationManagerCLI cli;
    
    /** 
     * Creates a new instance of CreateRealmTest 
     */
    public CreateRealmTest() {
        super("CreateRealmTest");
    }
    
    /**
     * This method is intended to provide initial setup.
     * Creates any realms specified in the setup-realms property in the 
     * createRealmTest.properties.
     */
    @Parameters({"testName"})
    @BeforeClass(groups={"ff-local", "ldapv3-local", "ds-local"})
    public void setup(String testName) 
    throws Exception {
        Object[] params = {testName};
        entering("setup", params);
        try {
            locTestName = testName;
            rb = ResourceBundle.getBundle("CreateRealmTest");
            setupRealms = (String)rb.getString(locTestName + 
                    "-create-setup-realms");
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
             
            Reporter.log("UsePasswordFile: " + usePasswordFile);
            Reporter.log("UseDebugOption: " + useDebugOption);
            Reporter.log("UseVerboseOption: " + useVerboseOption);
            Reporter.log("UseLongOptions: " + useLongOptions);
            Reporter.log("SetupRealms: " + setupRealms);

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
            exiting("setup");
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } 
    }
    
    /**
     * This method is used to execute tests involving "fmadm create-realm"
     * using input data from the CreateRealmTest.properties file.
     */
    @Test(groups={"ff-local", "ldapv3-local", "ds-local"})
    public void testRealmCreation() 
    throws Exception {
        entering("completeRealmCreation", null);
        boolean stringsFound = false;
        boolean realmFound = false;
        
        try {
            expectedMessage = (String) rb.getString(locTestName + 
                    "-message-to-find");
            expectedExitCode = (String) rb.getString(locTestName + 
                    "-expected-exit-code");
            realmToCreate = (String) rb.getString(locTestName + 
                    "-create-realm");
            description = (String) rb.getString(locTestName + "-description");

            log(logLevel, "testRealmCreation", "description: " + description);
            log(logLevel, "testRealmCreation", "use-password-file: " + 
                    usePasswordFile);
            log(logLevel, "testRealmCreation", "use-debug-option: " + 
                    useDebugOption);
            log(logLevel, "testRealmCreation", "use-verbose-option: " + 
                    useVerboseOption);
            log(logLevel, "testRealmCreation", "use-long-options: " + 
                    useLongOptions);
            log(logLevel, "testRealmCreation", "message-to-find: " + 
                    expectedMessage);
            log(logLevel, "testRealmCreation", "expected-exit-code: " + 
                    expectedExitCode);
            log(logLevel, "testRealmCreation", "create-realm: " + 
                    realmToCreate);

            Reporter.log("TestName: " + locTestName);
            Reporter.log("Description: " + description);
            Reporter.log("UsePasswordFile: " + usePasswordFile);
            Reporter.log("UseDebugOption: " + useDebugOption);
            Reporter.log("UseVerboseOption: " + useVerboseOption);
            Reporter.log("UseLongOptions: " + useLongOptions);
            Reporter.log("ExpectedMessage: " + expectedMessage);
            Reporter.log("ExpectedExitCode: " + expectedExitCode);
            Reporter.log("RealmToCreate: " + realmToCreate);
            
            int commandStatus = cli.createRealm(realmToCreate);
            cli.logCommand("testRealmCreation");
            cli.resetArgList();

            if (realmToCreate.length() > 0) {
                FederationManagerCLI listCLI = 
                        new FederationManagerCLI(usePasswordFile, useDebugOption, 
                        useVerboseOption, useLongOptions);
                realmFound = listCLI.findRealms(realmToCreate);
                log(logLevel, "testRealmCreation", "Realm " + realmToCreate + 
                        " Found: " + realmFound);
            }

            if (expectedExitCode.equals("0")) {
                stringsFound = cli.findStringsInOutput(expectedMessage, ";");
                log(logLevel, "testRealmCreation", "Output Messages Found: " + 
                        stringsFound);
                assert (commandStatus == 
                    new Integer(expectedExitCode).intValue()) && stringsFound &&
                        realmFound;
            } else {
                stringsFound = cli.findStringsInError(expectedMessage, ";");
                log(logLevel, "testRealmCreation", "Error Messages Found: " + 
                        stringsFound);
                assert (commandStatus == 
                    new Integer(expectedExitCode).intValue()) && stringsFound;
            }     
            exiting("testRealmCreation");
        } catch (Exception e) {
            log(Level.SEVERE, "testRealmCreation", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } 
    }
    
    /**
     * This method remove any realms that were created during the setup and
     * testRealmCreation methods using "fmadm delete-realm".
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
            
            if (!realmToCreate.equals("")) {
                log(logLevel, "cleanup", "realmToDelete: "  + realmToCreate);
                Reporter.log("RealmToDelete: " + realmToCreate);
                cli.deleteRealm(realmToCreate, true);
                cli.logCommand("cleanup");
                cli.resetArgList();
            }
            
            if (!setupRealms.equals("")) {
                StringTokenizer tokenizer = new StringTokenizer(setupRealms, 
                        ";");
                String setupRealmToDelete = tokenizer.nextToken();
                log(logLevel, "cleanup", "setupRealmToDelete: " + 
                        setupRealmToDelete);
                Reporter.log("SetupRealmToDelete: " + setupRealmToDelete);
                cli.deleteRealm(setupRealmToDelete, true);
                cli.logCommand("cleanup");
                cli.resetArgList();
            }
            exiting("cleanup");
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } 
    }
}

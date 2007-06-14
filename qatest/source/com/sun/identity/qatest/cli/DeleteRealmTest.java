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
 * $Id: DeleteRealmTest.java,v 1.2 2007-06-14 21:39:47 cmwesley Exp $
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
public class DeleteRealmTest extends TestCommon {
    
    private String locTestName;
    private ResourceBundle rb;
    private String setupRealms;
    private String realmToDelete;
    private String realmsDeleted;
    private String realmsExisting;
    private boolean usePasswordFile;
    private boolean useVerboseOption;
    private boolean useDebugOption;
    private boolean useLongOptions;
    private boolean useRecursiveOption;
    private String expectedMessage;
    private String expectedExitCode;
    private String description;
    private FederationManagerCLI cli;
    
    /** 
     * Creates a new instance of CreateRealmTest 
     */
    public DeleteRealmTest() {
        super("DeleteRealmTest");
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
            rb = ResourceBundle.getBundle("DeleteRealmTest");
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
    public void testRealmDeletion() 
    throws Exception {
        entering("testRealmDeletion", null);
        boolean stringsFound = false;
        boolean removedRealmsFound = false;
        boolean existingRealmsFound = true;
        
        try {
            description = (String) rb.getString(locTestName + "-description");
            useRecursiveOption = ((String)rb.getString(locTestName + 
                    "-use-recursive-option")).equals("true");
            expectedMessage = (String) rb.getString(locTestName + 
                    "-message-to-find");
            expectedExitCode = (String) rb.getString(locTestName + 
                    "-expected-exit-code");
            realmToDelete = (String) rb.getString(locTestName + 
                    "-delete-realm");
            realmsDeleted = (String) rb.getString(locTestName + 
                    "-realms-deleted");
            realmsExisting = (String) rb.getString(locTestName + 
                    "-realms-existing");
            
            log(logLevel, "testRealmCreation", "description: " + description);
            log(logLevel, "testRealmDeletion", "use-password-file: " + 
                    usePasswordFile);
            log(logLevel, "testRealmDeletion", "use-debug-option: " + 
                    useDebugOption);
            log(logLevel, "testRealmDeletion", "use-verbose-option: " + 
                    useVerboseOption);
            log(logLevel, "testRealmDeletion", "use-long-options: " + 
                    useLongOptions);
            log(logLevel, "testRealmDeletion", "use-recursive-option: " + 
                    useRecursiveOption);
            log(logLevel, "testRealmDeletion", "message-to-find: " + 
                    expectedMessage);
            log(logLevel, "testRealmDeletion", "expected-exit-code: " + 
                    expectedExitCode);
            log(logLevel, "testRealmDeletion", "delete-realm: " + 
                    realmToDelete);
            log(logLevel, "testRealmDeletion", "existing-realms: " + 
                    realmsExisting);
            log(logLevel, "testRealmDeletion", "realms-deleted: " + 
                    realmsDeleted);

            Reporter.log("TestName: " + locTestName);
            Reporter.log("Description: " + description);            
            Reporter.log("UsePasswordFile: " + usePasswordFile);
            Reporter.log("UseDebugOption: " + useDebugOption);
            Reporter.log("UseVerboseOption: " + useVerboseOption);
            Reporter.log("UseLongOptions: " + useLongOptions);
            Reporter.log("ExpectedMessage: " + expectedMessage);
            Reporter.log("ExpectedExitCode: " + expectedExitCode);
            Reporter.log("RealmToDelete: " + realmToDelete);
            Reporter.log("RealmsRemaining: " + realmsExisting);
            Reporter.log("RealmsDeleted: " + realmsDeleted);
            
            int commandStatus = cli.deleteRealm(realmToDelete, 
                    useRecursiveOption);
            cli.logCommand("testRealmDeletion");
            cli.resetArgList();

            String delimiter = "*" + System.getProperty("line.separator");
            if (realmToDelete.indexOf("*") != -1) {
                delimiter = System.getProperty("line.separator");
            }
            
            if (expectedExitCode.equals("0")) {
                stringsFound = cli.findStringsInOutput(expectedMessage, 
                        delimiter);
                log(logLevel, "testRealmDeletion", "Output Messages Found: " + 
                        stringsFound);
            } else {
                stringsFound = cli.findStringsInError(expectedMessage, 
                        delimiter); 
                log(logLevel, "testRealmDeletion", "Error Messages Found: " + 
                        stringsFound);
            }            
 
            
            if ((realmsDeleted != null) && (realmsDeleted.length() > 0)) {
                removedRealmsFound = cli.findRealms(realmsDeleted);
                cli.resetArgList();
            }
            
            if ((realmsExisting != null) && (realmsExisting.length() > 0)) {
                FederationManagerCLI listCLI = 
                    new FederationManagerCLI(usePasswordFile, useDebugOption,
                    useVerboseOption, useLongOptions);
                existingRealmsFound = listCLI.findRealms(realmsExisting);
            }            
                         
            if (expectedExitCode.equals("0")) {
                assert (commandStatus == 
                    new Integer(expectedExitCode).intValue()) && stringsFound &&
                        !removedRealmsFound && existingRealmsFound;
            } else {
                assert (commandStatus == 
                    new Integer(expectedExitCode).intValue()) && stringsFound &&
                        existingRealmsFound;
            }
            
            exiting("testRealmDeletion");
        } catch (Exception e) {
            log(Level.SEVERE, "testRealmDeletion", e.getMessage(), null);
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

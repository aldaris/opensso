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
 * $Id: CreateIdentitiesTest.java,v 1.1 2007-09-04 21:43:26 bt199000 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idm;

import com.sun.identity.idm.IdRepoException;
import com.sun.identity.qatest.common.IDMCommon;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Level;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Reporter;

/**
 * <code>CreateIdentityTest</code> executes test cases to create identities with
 * identity type user, role, filtered role, group, and agent.  This class reads 
 * test data in createIdentitiesTest.properties file, executes the tests to 
 * create one or more identities based on identity types, verifies the status, 
 * and removes all identities previously created. 
 */
public class CreateIdentitiesTest extends IDMCommon {
    private int testNumber;
    private String testName;
    private Map cfgMap;
    private IdentitiesTest idObj;
    private String testSetupName;
    private String testSetupType;
    private String expectedErrorCode;
    private String expectedErrorMessage;
    
    /**
     * Creates a new instance of CreateIdentitiesTest
     */
    public CreateIdentitiesTest() {
        super("CreateIdentitiesTest");
    }
    
    /**
     * This method provides the initial test set up.
     */
    @BeforeClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    @Parameters({"testNum"})
    public void setup(String testNum)
    throws Exception {
        Object[] params = {testNum};
        entering("setup", params);
        try {
            testNumber = Integer.parseInt(testNum);
            testName=IDMConstants.IDM_TESTCASES_CREATE_NAME;
            log(Level.FINE, "setup",
                    "Start " + testName + "test case number " + testNumber);
            String prefixTestName = IDMConstants.IDM_TESTCASES_PREFIX +
                    testNum + ".";
            cfgMap = getDataFromCfgFile(prefixTestName, testName);
            log(Level.FINEST, "setup", "Config data = " + cfgMap.toString());
            idObj = new IdentitiesTest(testNumber, testName, cfgMap);
            testSetupName =
                    idObj.getParams(IDMConstants.IDM_KEY_SETUP_NAME);
            testSetupType =
                    idObj.getParams(IDMConstants.IDM_KEY_SETUP_TYPE);
            expectedErrorCode = idObj.
                    getParams(IDMConstants.IDM_KEY_EXPECTED_ERROR_CODE);
            expectedErrorMessage = idObj.
                    getParams(IDMConstants.IDM_KEY_EXPECTED_ERROR_MESSAGE);
            Reporter.log("TestName: " + testName + testNumber);
            Reporter.log("Description: " + 
                    idObj.getParams(IDMConstants.IDM_KEY_DESCRIPTION));
            Reporter.log("IdName: " + 
                    idObj.getParams(IDMConstants.IDM_KEY_IDENTITY_NAME));
            Reporter.log("IdType: " + 
                    idObj.getParams(IDMConstants.IDM_KEY_IDENTITY_TYPE));
            Reporter.log("SetupName: " + testSetupName);
            Reporter.log("SetupType: " + testSetupType);
            Reporter.log("ExpectedErrorCode: " + expectedErrorCode);
            Reporter.log("ExpectedErrorMessage: " + expectedErrorMessage);
            Reporter.log(" ");
            if (testSetupName != null)
                assert(idObj.create(testSetupName, testSetupType));
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("setup");
    }
    
    /* 
     * This test case creates one or multiple identities
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void testCreateIdentities()
    throws Exception {
        entering("testCreateIdentities", null);
        try {
            assert(idObj.create());
        } catch (IdRepoException idre) {
            if (checkIDMExpectedErrorMessageCode(idre, expectedErrorMessage,
                    expectedErrorCode))
                assert true;
            else {
                log(Level.SEVERE, "testCreateIdentities", idre.getMessage() + 
                        " " + idre.getErrorCode());
                idre.printStackTrace();
                throw idre;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "testCreateIdentities", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("testCreateIdentities");
    }
    
    /**
     * This method provides the test clean up.
     */
    @AfterClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        if (testSetupName != null)
            assert(idObj.delete(testSetupName, testSetupType));
        if (idObj.doesIdentityExists())
            assert(idObj.delete());
        exiting("cleanup");
    }
}


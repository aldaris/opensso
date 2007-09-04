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
 * $Id: UpdateIdentitiesTest.java,v 1.1 2007-09-04 21:43:27 bt199000 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idm;

import com.sun.identity.qatest.common.IDMCommon;
import java.text.MessageFormat;
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
 * <code>UpdateIdentitiesTest</code> executes test cases to update identities' 
 * attributes with identity type user, filtered role, and agent.  This class 
 * reads test data in updateIdentitiesTest.properties file, creates one or more
 * identities in the setup method, executes the tests to update one or more 
 * identities attributes, verifies the status, and removes all identities 
 * previously created. 
 */
public class UpdateIdentitiesTest extends IDMCommon {
    private int testNumber;
    private String testName;
    private Map cfgMap;
    private IdentitiesTest idObj;
    private String testSetupName;
    private String testSetupType;
    private String expectedErrorCode;
    private String expectedErrorMessage;
    
    /**
     * Creates a new instance of UpdateIdentitiesTest
     */
    public UpdateIdentitiesTest() {
        super("UpdateIdentitiesTest");
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
            testName=IDMConstants.IDM_TESTCASES_UPDATE_NAME;
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
            expectedErrorCode = 
                    idObj.getParams(IDMConstants.IDM_KEY_EXPECTED_ERROR_CODE);
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
    
    /**
     * This test case updates an identitie with identity attributes
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void testUpdateIdentities()
    throws Exception {
        entering("testUpdateIdentities", null);
        try {
            assert(idObj.update());
        } catch ( Exception e) {
            log(Level.SEVERE, "testUpdateIdentities", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("testUpdateIdentities");
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
        exiting("cleanup");
    }
}


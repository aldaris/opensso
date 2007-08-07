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
 * $Id: PolicyTests.java,v 1.2 2007-08-07 23:35:23 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.policy;

import com.iplanet.sso.SSOToken;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.AgentsCommon;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Reporter;
import java.util.Map;

/**
 * This class has the methods to create and evaluate policies using client
 * policy evaluation API
 */
public class PolicyTests extends TestCommon {
    
    private int polIdx;
    private int evalIdx;
    private String strSetup;
    private String strCleanup;
    private AgentsCommon mpc;
    private ResourceBundle rbg;
    private ResourceBundle rbp;
    private String strLocRB = "PolicyTests";
    private String strGblRB = "PolicyGlobal";
    
    /**
     * Class constructor. No arguments
     */
    
    public PolicyTests()
    throws Exception {
        super("PolicyTests");
        mpc = new AgentsCommon();
        rbg = ResourceBundle.getBundle(strGblRB);
        rbp = ResourceBundle.getBundle(strLocRB);
    }
    
    /**
     * This method sets up all the required identities, generates the xmls and
     * creates the policies in the server
     */
    @Parameters({"policyIdx", "evaluationIdx", "setup", "cleanup"})
    @BeforeClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void setup(String policyIdx, String evaluationIdx, String setup
            , String cleanup)
            throws Exception {
        Object[] params = {policyIdx, evaluationIdx, setup, cleanup};
        entering("setup", params);
        try {
            polIdx = new Integer(policyIdx).intValue();
            evalIdx = new Integer(evaluationIdx).intValue();
            strSetup = setup;
            strCleanup = cleanup;
            if (strSetup.equals("true")) {
                mpc.createIdentities(strLocRB, polIdx);
                mpc.createPolicyXML(strGblRB, strLocRB, polIdx, strLocRB +
                        ".xml");
                mpc.createPolicy(strLocRB + ".xml");
                Thread.sleep(75000);
            }
        }catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("setup");
    }
    
    /**
     * This method evaluates the policies using the client policy evalaution API
     * Policy_sub, Policy_ldapFilter, Policy_sub_exclude, Policy_Wildcard
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void evaluatePolicyAPI()
    throws Exception {
        entering("evaluatePolicyAPI", null);
        String strEvalIdx = strLocRB + polIdx + ".evaluation" + evalIdx;
        String resource = rbg.getString(rbp.getString(strEvalIdx +
                ".resource"));
        String usernameIdx = rbp.getString(strEvalIdx + ".username");
        String username = rbp.getString(usernameIdx);
        String passwordIdx = rbp.getString(strEvalIdx + ".password");
        String password = rbp.getString(passwordIdx);
        String action = rbp.getString(strEvalIdx + ".action");
        String expResult = rbp.getString(strEvalIdx + ".expectedResult");
        String description = rbp.getString(strEvalIdx + ".description");
        int idIdx = mpc.getIdentityIndex(usernameIdx);
        Map map = mpc.getPolicyEnvParamMap(strLocRB, polIdx, evalIdx);
        
        Reporter.log("Test description: " + description);
        Reporter.log("Resource: " + resource);
        Reporter.log("Username: " + username);
        Reporter.log("Password: " + password);
        Reporter.log("Action: " + action);
        Reporter.log("Env Param: " + map);
        Reporter.log("Expected Result: " + expResult);
        
        SSOToken userToken = getToken(username, password, realm);
        mpc.setProperty(strLocRB, userToken, polIdx, idIdx);
        mpc.evaluatePolicyThroughAPI(resource, userToken, action, map,
                expResult, idIdx);
        
        exiting("evaluatePolicyAPI");
    }
    
    /**
     * This method cleans all the identities and policies  that were setup
     */
    @AfterClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        try {
            if (strCleanup.equals("true")) {
                mpc.deleteIdentities(strLocRB, polIdx);
                mpc.deletePolicies(strLocRB, polIdx);
            }
        }catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("cleanup");
    }
}

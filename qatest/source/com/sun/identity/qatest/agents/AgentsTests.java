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
 * $Id: AgentsTests.java,v 1.1 2007-06-19 21:59:09 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.agents;

import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.AgentsCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.FederationManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class AgentsTests extends TestCommon {
    
    private String loginURL;
    private String logoutURL;
    private String fmadmURL;
    private FederationManager fmadm;
    private WebClient webClient;
    private int polIdx;
    private int evalIdx;
    private String strSetup;
    private String strCleanup;
    private AgentsCommon mpc;
    private boolean executeAgainstOpenSSO;
    private ResourceBundle rbg;
    private ResourceBundle rbp;
    private String strLocRB = "AgentsTests";
    private String strGblRB = "agentsGlobal";

    public AgentsTests() 
    throws Exception{
        super("AgentsTests");
        mpc = new AgentsCommon();
        rbg = ResourceBundle.getBundle(strGblRB);
        rbp = ResourceBundle.getBundle(strLocRB);
        executeAgainstOpenSSO = new Boolean(rbg.getString(strGblRB +
                ".executeAgainstOpenSSO")).booleanValue();
    }
    
    @BeforeSuite(groups={"client"})
    public void createAgentProfile() 
    throws Exception {
        entering("createAgentProfile", null);
        if (executeAgainstOpenSSO) {
            String agentId = rbg.getString(strGblRB + ".agentId");
            String agentPassword = rbg.getString(strGblRB + ".agentPassword");
            loginURL = protocol + ":" + "//" + host + ":" + port + uri +
                "/UI/Login";
            logoutURL = protocol + ":" + "//" + host + ":" + port + uri +
                "/UI/Logout";
            fmadmURL = protocol + ":" + "//" + host + ":" + port + uri;
            try {
                List list = new ArrayList();
                list.add("userpassword=" + agentPassword);
                list.add("sunIdentityServerDeviceStatus=Active");
                fmadm = new FederationManager(fmadmURL);
                webClient = new WebClient();
                consoleLogin(webClient, loginURL, adminUser, adminPassword);
                fmadm.createIdentity(webClient, realm, agentId, "Agent", list);
            } catch (Exception e) {
                log(Level.SEVERE, "AgentsCommon", e.getMessage(), null);
                e.printStackTrace();
                throw e;
            } finally {
                consoleLogout(webClient, logoutURL);
            }
        } else
            log(logLevel, "createAgentProfile",
                    "Executing against non OpenSSO Install");
        exiting("createAgentProfile");
    }

    @Parameters({"policyIdx","evaluationIdx","setup","cleanup"})
    @BeforeClass(groups={"client"})
    public void setup(String policyIdx, String evaluationIdx, String setup
            , String cleanup)
    throws Exception {
        Object[] params = {policyIdx, evaluationIdx, setup, cleanup};
        entering("setup", params);
        if (executeAgainstOpenSSO) {
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
                }
            } catch (Exception e) {
                log(Level.SEVERE, "setup", e.getMessage(), null);
                e.printStackTrace();
                throw e;
            }
        } else
            log(logLevel, "setup", "Executing against non OpenSSO Install");
        exiting("setup");
    }
    
    @Test(groups={"client"})
    public void evaluatePolicyAgents()
    throws Exception {
        entering("evaluatePolicyAgents", null);
        String strEvalIdx = strLocRB + polIdx + ".evaluation" + evalIdx;
        String resource = rbg.getString(rbp.getString(strEvalIdx +
                ".resource"));
        String usernameIdx = rbp.getString(strEvalIdx + ".username");
        String username = rbp.getString(usernameIdx);
        String passwordIdx = rbp.getString(strEvalIdx + ".password");
        String password = rbp.getString(passwordIdx);
        String expResult = rbp.getString(strEvalIdx + ".expectedResult");
        String description = rbp.getString(strEvalIdx + ".description");
     
        Reporter.log("Test description: " + description);   
        Reporter.log("Resource: " + resource);   
        Reporter.log("Username: " + username);   
        Reporter.log("Password: " + password);   
        Reporter.log("Expected Result: " + expResult);   

        mpc.evaluatePolicyThroughAgents(resource, username, password,
                expResult);

        exiting("evaluatePolicyAgents");
    }

    @AfterClass(groups={"client"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        if (executeAgainstOpenSSO) {
            try {
                if (strCleanup.equals("true")) {
                    mpc.deleteIdentities(strLocRB, polIdx);
                    mpc.deletePolicies(strLocRB, polIdx);
                }
            } catch (Exception e) {
                log(Level.SEVERE, "cleanup", e.getMessage(), null);
                e.printStackTrace();
                throw e;
            }
        } else
            log(logLevel, "cleanup", "Executing against non OpenSSO Install");
        exiting("cleanup");
    }

    @AfterSuite(groups={"client"})
    public void deleteAgentProfile()
    throws Exception {
        entering("deleteAgentProfile", null);
        if (executeAgainstOpenSSO) {
            String agentId = rbg.getString(strGblRB + ".agentId");
            loginURL = protocol + ":" + "//" + host + ":" + port + uri +
                "/UI/Login";
            logoutURL = protocol + ":" + "//" + host + ":" + port + uri +
                "/UI/Logout";
            fmadmURL = protocol + ":" + "//" + host + ":" + port + uri;
            try {
                List list = new ArrayList();
                list.add(agentId);
                fmadm = new FederationManager(fmadmURL);
                webClient = new WebClient();
                consoleLogin(webClient, loginURL, adminUser, adminPassword);
                fmadm.deleteIdentities(webClient, realm, list, "Agent");
            } catch (Exception e) {
                log(Level.SEVERE, "AgentsCommon", e.getMessage(), null);
                e.printStackTrace();
                throw e;
            } finally {
                consoleLogout(webClient, logoutURL);
            }
        } else
            log(logLevel, "cleanup", "Executing against non OpenSSO Install");
        exiting("deleteAgentProfile");
    }
}

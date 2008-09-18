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
 * $Id: SAMLv1xProfileTests.java,v 1.2 2008-09-18 17:43:19 sridharev Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.samlv1x;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.SAMLv1Common;
import com.sun.identity.qatest.common.SAMLv2Common;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.webtest.DefaultTaskHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.Map;
import java.util.ResourceBundle;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class to test the new SAMLv1x Profiles
 */
public class SAMLv1xProfileTests extends TestCommon {

    private WebClient spWebClient;
    private WebClient idpWebClient;
    private Map<String, String> configMap;
    private String baseDir;
    private String xmlfile;
    private DefaultTaskHandler task1;
    private HtmlPage wpage;
    private FederationManager fmSP;
    private FederationManager fmIDP;
    public WebClient webClient;
    private String spurl;
    private String idpurl;
    private String testName;
    private String testType;
    private String testInit;
    private String spmetaAliasname;
    private String idpmetaAliasname;

    /**
     * Constructor SAMLV2AttributeQueryTests
     */
    public SAMLv1xProfileTests() {
        super("SAMLv1xProfileTests");
    }

    /**
     * Configures the SP and IDP load meta for the SAMLV2AttributeQueryTests 
     * tests to execute
     */
    @Parameters({"ptestName", "ptestType", "ptestInit"})
    @BeforeClass(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void setup(String ptestName, String ptestType, String ptestInit)
            throws Exception {
        ArrayList list;
        try {
            testName = ptestName;
            testType = ptestType;
            testInit = ptestInit;
            ResourceBundle rb_amconfig = ResourceBundle.getBundle(
                    TestConstants.TEST_PROPERTY_AMCONFIG);
            baseDir = getBaseDir() + SAMLv2Common.fileseparator 
                    + rb_amconfig.getString(TestConstants.KEY_ATT_SERVER_NAME) 
                    + SAMLv2Common.fileseparator + "built" 
                    + SAMLv2Common.fileseparator + "classes" 
                    + SAMLv2Common.fileseparator;
            configMap = new HashMap<String, String>();
            SAMLv2Common.getEntriesFromResourceBundle("samlv2" + fileseparator 
                    + "samlv2TestData", configMap);
            SAMLv2Common.getEntriesFromResourceBundle("samlv2" + fileseparator 
                    + "samlv2TestConfigData", configMap);
            SAMLv2Common.getEntriesFromResourceBundle("samlv1x" + fileseparator 
                    + "SAMLv1xProfileTests", configMap);
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT) +
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            spmetaAliasname = configMap.get(TestConstants.KEY_SP_METAALIAS);
            idpmetaAliasname = configMap.get(TestConstants.KEY_IDP_METAALIAS);
            getWebClient();
            // Create sp users
            consoleLogin(spWebClient, spurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(spurl);
            list = new ArrayList();
            list.add("sn=" + configMap.get(TestConstants.KEY_SP_USER));
            list.add("cn=" + configMap.get(TestConstants.KEY_SP_USER));
            list.add("userpassword=" +
                    configMap.get(TestConstants.KEY_SP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            list.add("mail=" + configMap.get(TestConstants.KEY_SP_USER) + "@" 
                    + spmetaAliasname);
            if (FederationManager.getExitCode(fmSP.createIdentity(spWebClient,
                    configMap.get(TestConstants.KEY_SP_EXECUTION_REALM),
                    configMap.get(TestConstants.KEY_SP_USER), 
                    "User", list)) != 0) {
                log(Level.SEVERE, "setup", "createIdentity famadm command" +
                        " failed");
                assert false;
            }
            // Create idp users
            idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_IDP_PORT) +
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);

            consoleLogin(idpWebClient, idpurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            fmIDP = new FederationManager(idpurl);
            list.clear();
            list.add("sn=" + configMap.get(TestConstants.KEY_IDP_USER));
            list.add("cn=" + configMap.get(TestConstants.KEY_IDP_USER));
            list.add("userpassword=" +
                    configMap.get(TestConstants.KEY_IDP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            list.add("mail=" + configMap.get(TestConstants.KEY_IDP_USER) + "@" 
                    + idpmetaAliasname);
            if (FederationManager.getExitCode(fmIDP.createIdentity(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM),
                    configMap.get(TestConstants.KEY_IDP_USER), 
                    "User", list)) != 0) {
                log(Level.SEVERE, "setup", "createIdentity famadm command " +
                        "failed for IDP");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(spWebClient, spurl + "/UI/Logout");
            consoleLogout(idpWebClient, idpurl + "/UI/Logout");
        }
        exiting("setup");
    }

    /**
     * Execute the AssertionQuery tests
     */
    @Test(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void samlv1xTest()
            throws Exception {
        try {
            String result = null;
            Reporter.log("Test Description: This test  " + testName +
                    " will run to make sure " +
                    " with test Type : " + testType +
                    " for  : " + testInit + "" +
                    " will work fine");
            if (testInit.equalsIgnoreCase("sp")) {
                result = configMap.get(TestConstants.KEY_IDP_USER) + "@" 
                        + idpmetaAliasname;
            } else {
                result = configMap.get(TestConstants.KEY_SP_USER) + "@" 
                        + spmetaAliasname;
            }
            Thread.sleep(5000);
            xmlfile = baseDir + testName + ".xml";
            configMap.put(TestConstants.KEY_SSO_RESULT, result);
            SAMLv1Common.getxmlSSO(xmlfile, configMap, testType, testInit);
            Thread.sleep(5000);
            log(Level.FINEST, "samlv1xTest", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            wpage = task1.execute(webClient);
            Thread.sleep(5000);
            if (!wpage.getWebResponse().getContentAsString().contains(result)) {
                log(Level.SEVERE, "samlv1xTest", "Couldn't " +
                        "signon users");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv1xTest", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(spWebClient, spurl + "/UI/Logout");
            consoleLogout(idpWebClient, idpurl + "/UI/Logout");
        }
    }

    /**
     * Create the webClient which will be used for the rest of the tests.
     */
    @BeforeClass(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void getWebClient()
            throws Exception {
        try {
            webClient = new WebClient(BrowserVersion.MOZILLA_1_0);
            spWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
            idpWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        } catch (Exception e) {
            log(Level.SEVERE, "getWebClient", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Clean up and deleted the created users for this test
     */
    @AfterClass(groups = {"ds_ds_sec", "ff_ds_sec"})
    public void cleanup()
            throws Exception {
        entering("cleanup", null);
        ArrayList list;
        WebClient webcClient = new WebClient();
        try {
            consoleLogin(spWebClient, spurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(spurl);
            list = new ArrayList();
            list.add(configMap.get(TestConstants.KEY_SP_USER));
            if (FederationManager.getExitCode(fmSP.deleteIdentities(spWebClient,
                    configMap.get(TestConstants.KEY_SP_EXECUTION_REALM),
                    list, "User")) != 0) {
                log(Level.SEVERE, "setup", "deleteIdentity famadm command" +
                        " failed");
                assert false;
            }
            consoleLogin(idpWebClient, idpurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            fmIDP = new FederationManager(idpurl);
            list.clear();
            list.add(configMap.get(TestConstants.KEY_IDP_USER));
            if (FederationManager.getExitCode(fmIDP.deleteIdentities(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM),
                    list, "User")) != 0) {
                log(Level.SEVERE, "setup", "deleteIdentity famadm command " +
                        "failed");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webcClient, spurl + "/UI/Logout");
            consoleLogout(webcClient, idpurl + "/UI/Logout");
        }
    }
}

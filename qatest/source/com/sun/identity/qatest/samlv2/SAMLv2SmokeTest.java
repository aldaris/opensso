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
 * $Id: SAMLv2SmokeTest.java,v 1.5 2007-08-07 23:35:25 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.samlv2;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.SAMLv2Common;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.webtest.DefaultTaskHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class tests Atrifact, HTTP-Redirect, POST & SOAP profiles with
 * SP & IDP initiated
 */
public class SAMLv2SmokeTest extends TestCommon {
    
    public WebClient webClient;
    private Map<String, String> configMap;
    private String baseDir ;
    private String xmlfile;
    private DefaultTaskHandler task1;
    private HtmlPage page1;
    private FederationManager fmSP;
    private FederationManager fmIDP;
    
    /**
     * This is constructor for this class.
     */
    public SAMLv2SmokeTest() {
        super("SAMLv2SmokeTest");
    }
    
    /**
     * This setup method creates required users.
     */
    @BeforeClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void setup() 
    throws Exception {
        URL url;
        HtmlPage page;
        ArrayList list;
        try {
            log(logLevel, "setup", "Entering");
            //Upload global properties file in configMap
            ResourceBundle rb_amconfig = ResourceBundle.getBundle(
                    TestConstants.TEST_PROPERTY_AMCONFIG);
            baseDir = getBaseDir() + SAMLv2Common.fileseparator
                    + rb_amconfig.getString(TestConstants.KEY_ATT_SERVER_NAME)
                    + SAMLv2Common.fileseparator + "built"
                    + SAMLv2Common.fileseparator + "classes"
                    + SAMLv2Common.fileseparator;
            configMap = new HashMap<String, String>();
            SAMLv2Common.getEntriesFromResourceBundle("samlv2TestConfigData",
                    configMap);
            SAMLv2Common.getEntriesFromResourceBundle("samlv2TestData",
                    configMap);
            SAMLv2Common.getEntriesFromResourceBundle("samlv2SmokeTest",
                    configMap);
            log(logLevel, "setup", "ConfigMap is : " + configMap );
            
            // Create sp users
            String spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT) +
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            getWebClient();
            consoleLogin(webClient, spurl,
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(spurl);
            list = new ArrayList();
            list.add("sn=" + configMap.get(TestConstants.KEY_SP_USER));
            list.add("cn=" + configMap.get(TestConstants.KEY_SP_USER));
            list.add("userpassword=" +
                    configMap.get(TestConstants.KEY_SP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            fmSP.createIdentity(webClient,
                    configMap.get(TestConstants.KEY_SP_REALM),
                    configMap.get(TestConstants.KEY_SP_USER), "User", list);
            consoleLogout(webClient, spurl + "/UI/Logout");
            
            // Create idp users
            String idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_IDP_PORT) +
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
            consoleLogin(webClient, idpurl,
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            
            fmIDP = new FederationManager(idpurl);
            list.clear();
            list.add("sn=" + configMap.get(TestConstants.KEY_IDP_USER));
            list.add("cn=" + configMap.get(TestConstants.KEY_IDP_USER));
            list.add("userpassword=" +
                    configMap.get(TestConstants.KEY_IDP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            fmIDP.createIdentity(webClient,
                    configMap.get(TestConstants.KEY_IDP_REALM),
                    configMap.get(TestConstants.KEY_IDP_USER), "User", list);
            consoleLogout(webClient, idpurl + "/UI/Logout");
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("setup");
    }
    
    /**
     * Create the webClient which will be used for the rest of the tests.
     */
    @BeforeClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void getWebClient() 
    throws Exception {
        try {
            webClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        } catch (Exception e) {
            log(Level.SEVERE, "getWebClient", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Run saml2 profile testcase 1.
     * @DocTest: SAML2|Perform SP initiated sso.
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void testSPSSOInit()
    throws Exception {
        entering("testSPSSOInit", null);
        try {
            log(logLevel, "testSPSSOInit", "Running: testSPSSOInit");
            getWebClient();
            xmlfile = baseDir + "test1spssoinit.xml";
            SAMLv2Common.getxmlSPInitSSO(xmlfile, configMap, "artifact", false);
            log(logLevel, "testSPSSOInit", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPSSOInit", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPSSOInit");
    }
    
    /**
     * Run saml2 slo
     * @DocTest: SAML2|Perform SP initiated slo.
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"},
    dependsOnMethods={"testSPSSOInit"})
    public void testSPSLO()
    throws Exception {
        entering("testSPSLO", null);
        try {
            log(logLevel, "testSPSLO", "Running: testSPSLO");
            xmlfile = baseDir + "test2spslo.xml";
            SAMLv2Common.getxmlSPSLO(xmlfile, configMap, "http");
            log(logLevel, "testSPSSOInit", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPSLO", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPSLO");
    }
    
    /**
     * Run saml2 termination
     * @DocTest: SAML2|Perform SP initiated termination
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"},
    dependsOnMethods={"testSPSLO"})
    public void testSPTerminate()
    throws Exception {
        entering("testSPTerminate", null);
        try {
            log(logLevel, "testSPTerminate", "Running: testSPTerminate");
            xmlfile = baseDir + "test3spterminate.xml";
            SAMLv2Common.getxmlSPTerminate(xmlfile, configMap, "http");
            log(logLevel, "testSPTerminate", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPTerminate", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPTerminate");
    }
    
    /**
     * Run saml2 profile .
     * @DocTest: SAML2|Perform idp initiated sso.
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"},
    dependsOnMethods={"testSPTerminate"})
    public void testIDPSSO()
    throws Exception {
        entering("testIDPSSO", null);
        try {
            log(logLevel, "testIDPSSO", "\nRunning: testIDPSSO\n");
            getWebClient();
            xmlfile = baseDir + "test7idplogin.xml";
            SAMLv2Common.getxmlIDPLogin(xmlfile, configMap);
            log(logLevel, "testIDPSSO", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
            xmlfile = baseDir + "test7idpssoinit.xml";
            SAMLv2Common.getxmlIDPInitSSO(xmlfile, configMap, "artifact",
                    false);
            log(logLevel, "testIDPSSO", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPSSO", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPSSO");
    }
    
    /**
     * Run saml2 slo
     * @DocTest: SAML2|Perform idp initiated slo.
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"},
    dependsOnMethods={"testIDPSSO"})
    public void testIDPSLO()
    throws Exception {
        entering("testIDPSLO", null);
        try {
            log(logLevel, "testIDPSLO", "Running: testIDPSLO");
            xmlfile = baseDir + "test8idpslo.xml";
            SAMLv2Common.getxmlIDPSLO(xmlfile, configMap, "http");
            log(logLevel, "testIDPSLO", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPSLO", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPSLO");
    }
    
    /**
     * Run saml2 termination
     * @DocTest: SAML2|Perform  idp initiated termination
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"},
    dependsOnMethods={"testIDPSLO"})
    public void testIDPTerminate()
    throws Exception {
        entering("testIDPTerminate", null);
        try {
            log(logLevel, "testIDPTerminate", "Running: testIDPTerminate");
            xmlfile = baseDir + "test9idpterminate.xml";
            SAMLv2Common.getxmlIDPTerminate(xmlfile, configMap, "http");
            log(logLevel, "testIDPTerminate", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPTerminate", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPTerminate");
    }
    
    /**
     * Run saml2 profile testcase 4.
     * @DocTest: SAML2|Perform SP initiated sso with post profile.
     */
    @Test(groups={"ds_ds_sec","ff_ds_sec"},
    dependsOnMethods={"testIDPTerminate"})
    public void testSPSSOInitPost()
    throws Exception {
        entering("testSPSSOInitPost", null);
        try {
            log(logLevel, "testSPSSOInitPost", "Running: testSPSSOInitPost");
            getWebClient();
            xmlfile = baseDir + "test4spssoinit.xml";
            SAMLv2Common.getxmlSPInitSSO(xmlfile, configMap, "post", false);
            log(logLevel, "testSPSSOInitPost", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPSSOInitPost", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPSSOInitPost");
    }
    
    /**
     * Run saml2 slo
     * @DocTest: SAML2|Perform SP initiated slo with soap profile.
     */
    @Test(groups={"ds_ds_sec","ff_ds_sec"},
    dependsOnMethods={"testSPSSOInitPost"})
    public void testSPSLOSOAP()
    throws Exception {
        entering("testSPSLOSOAP", null);
        try {
            log(logLevel, "testSPSLOSOAP", "Running: testSPSLOSOAP");
            xmlfile = baseDir + "test5spslo.xml";
            SAMLv2Common.getxmlSPSLO(xmlfile, configMap, "soap");
            log(logLevel, "testSPSLOSOAP", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPSLOSOAP", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPSLOSOAP");
    }
    
    /**
     * Run saml2 termination
     * @DocTest: SAML2|Perform SP initiated termination with soap profile
     */
    @Test(groups={"ds_ds_sec","ff_ds_sec"},
    dependsOnMethods={"testSPSLOSOAP"})
    public void testSPTerminateSOAP()
    throws Exception {
        entering("testSPTerminateSOAP", null);
        try {
            log(logLevel, "testSPTerminateSOAP", "Running: " +
                    "testSPTerminateSOAP");
            xmlfile = baseDir + "test6spterminate.xml";
            SAMLv2Common.getxmlSPTerminate(xmlfile, configMap, "soap");
            log(logLevel, "testSPTerminateSOAP", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPTerminateSOAP", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPTerminateSOAP");
    }
    
    
    /**
     * Run saml2 profile testcase 1.
     * @DocTest: SAML2|Perform  idp initiated sso with post profile.
     */
    @Test(groups={"ds_ds_sec","ff_ds_sec"},
    dependsOnMethods={"testSPTerminateSOAP"})
    public void testIDPSSOInitPost()
    throws Exception {
        entering("testIDPSSOInitPost", null);
        try {
            log(logLevel, "testIDPSSOInitPost", "Running: testIDPSSOInitPost");
            getWebClient();
            xmlfile = baseDir + "test10idplogin.xml";
            SAMLv2Common.getxmlIDPLogin(xmlfile, configMap);
            log(logLevel, "testIDPSSOInitPost", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
            
            xmlfile = baseDir + "test10idpssoinit.xml";
            SAMLv2Common.getxmlIDPInitSSO(xmlfile, configMap, "post", false);
            log(logLevel, "testIDPSSOInitPost", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPSSOInitPost", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPSSOInitPost");
    }
    
    /**
     * Run saml2 slo
     * @DocTest: SAML2|Perform  idp initiated slo with soap profile .
     */
    @Test(groups={"ds_ds_sec","ff_ds_sec"},
    dependsOnMethods={"testIDPSSOInitPost"})
    public void testIDPSLOSOAP()
    throws Exception {
        entering("testIDPSLOSOAP", null);
        try {
            log(logLevel, "testIDPSLOSOAP", "Running: testIDPSLOSOAP");
            
            xmlfile = baseDir + "test11idpslo.xml";
            SAMLv2Common.getxmlSPSLO(xmlfile, configMap, "soap");
            log(logLevel, "testIDPSLOSOAP", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPSLOSOAP", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPSLOSOAP");
    }
    
    /**
     * Run saml2 termination
     * @DocTest: SAML2|Perform  idp initiated termination with soap profile
     */
    @Test(groups={"ds_ds_sec","ff_ds_sec"},
    dependsOnMethods={"testIDPSLOSOAP"})
    public void testIDPTerminateSOAP()
    throws Exception {
        entering("testIDPTerminateSOAP", null);
        try {
            log(logLevel, "testIDPTerminateSOAP",
                    "Running: testIDPTerminateSOAP");
            xmlfile = baseDir + "test12idpterminate.xml";
            SAMLv2Common.getxmlSPTerminate(xmlfile, configMap, "soap");
            log(logLevel, "testIDPTerminateSOAP", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPTerminateSOAP", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPTerminateSOAP");
    }
    
    /**
     * Cleanup methods deletes all the users which were created in setup
     */
    @AfterClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        ArrayList idList;
        try {
            getWebClient();
            // delete sp users
            String spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT) +
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            consoleLogin(webClient, spurl,
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(spurl);
            idList = new ArrayList();
            idList.add(configMap.get(TestConstants.KEY_SP_USER));
            log(logLevel, "cleanup", "sp users to delete :" +
                    configMap.get(TestConstants.KEY_SP_USER));
            fmSP.deleteIdentities(webClient,
                    configMap.get(TestConstants.KEY_SP_REALM), idList,
                    "User");
            consoleLogout(webClient, spurl + "/UI/Logout");
            
            // Create idp users
            String idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_IDP_PORT) +
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
            consoleLogin(webClient, idpurl,
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            fmIDP = new FederationManager(idpurl);
            idList = new ArrayList();
            idList.add(configMap.get(TestConstants.KEY_IDP_USER));
            log(logLevel, "cleanup", "idp users to delete :" +
                    configMap.get(TestConstants.KEY_IDP_USER));
            fmIDP.deleteIdentities(webClient,
                    configMap.get(TestConstants.KEY_IDP_REALM), idList,
                    "User");
            consoleLogout(webClient, idpurl + "/UI/Logout");
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("cleanup");
    }
    
}

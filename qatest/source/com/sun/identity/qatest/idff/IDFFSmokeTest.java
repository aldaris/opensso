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
 * $Id: IDFFSmokeTest.java,v 1.2 2007-06-19 22:54:19 mrudulahg Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idff;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.IDFFCommon;
import com.sun.identity.qatest.common.MultiProtocolCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.webtest.DefaultTaskHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class tests IDFF Federation, SLO, SSO, Name registration & Termination 
 * 1. SP Initiated Federation
 * 2. SP Initiated SLO
 * 3. SP Initiated SSO 
 * 4. SP Initiated Name Registration
 * 5. SP Initiated Termination
 * 6. IDP Initiated SLO. As IDP init federation is not supported, 
 * SP init federation is performed first to follow IDP init SLO. 
 * 7. IDP Initiated Name registration. 
 * 8. IDP Initiated Termination
 */
public class IDFFSmokeTest extends IDFFCommon {
    
    private WebClient webClient;
    private FederationManager fmSP;
    private FederationManager fmIDP;
    private DefaultTaskHandler task;
    private HtmlPage page;
    private Map<String, String> configMap;
    private String  baseDir;
    private URL url;
    private String xmlfile;
    private String spurl;
    private String idpurl;
    private String spmetadata;
    private String spmetadataext;
    private String idpmetadata;
    private String idpmetadataext;

    /** Creates a new instance of IDFFSmokeTest */
    public IDFFSmokeTest() {
        super("IDFFSmokeTest");
    }
    
    /**
     * Create the webClient 
     */
    private void getWebClient()
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
     * This is setup method. It creates required users for test
     */
    @Parameters({"profile"})
    @BeforeClass(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void setup(String strProfile)
    throws Exception {
        Object[] params = {strProfile};
        entering("setup", params);
        List<String> list;
        try {
            ResourceBundle rb_amconfig = ResourceBundle.getBundle(
                    TestConstants.TEST_PROPERTY_AMCONFIG);
            baseDir = getBaseDir() + System.getProperty("file.separator")
                    + rb_amconfig.getString(TestConstants.KEY_ATT_SERVER_NAME)
                    + System.getProperty("file.separator") + "built"
                    + System.getProperty("file.separator") + "classes"
                    + System.getProperty("file.separator");
            //Upload global properties file in configMap
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("idffTestConfigData");
            configMap.putAll(getMapFromResourceBundle("idffTestData"));
            configMap.putAll(getMapFromResourceBundle("idffSmokeTest"));
            log(logLevel, "setup", "Map is " + configMap);
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT) +
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_IDP_PORT) +
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
            } catch (Exception e) {
                log(Level.SEVERE, "setup", e.getMessage(), null);
                e.printStackTrace();
                throw e;
            }
        try {
            getWebClient();
            list = new ArrayList();
            consoleLogin(webClient, spurl,
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            consoleLogin(webClient, idpurl, configMap.get(
                    TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(spurl);
            fmIDP = new FederationManager(idpurl);
            
            list = new ArrayList();
            list.add("sn=" + configMap.get(TestConstants.KEY_SP_USER));
            list.add("cn=" + configMap.get(TestConstants.KEY_SP_USER));
            list.add("userpassword=" +
                    configMap.get(TestConstants.KEY_SP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            fmSP.createIdentity(webClient,
                    configMap.get(TestConstants.KEY_SP_REALM),
                    configMap.get(TestConstants.KEY_SP_USER), "User", list);
            log(logLevel, "setup", "SP user created is " + list);
            
            // Create idp users
            list.clear();
            list.add("sn=" + configMap.get(TestConstants.KEY_IDP_USER));
            list.add("cn=" + configMap.get(TestConstants.KEY_IDP_USER));
            list.add("userpassword=" +
                    configMap.get(TestConstants.KEY_IDP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            fmIDP.createIdentity(webClient,
                    configMap.get(TestConstants.KEY_IDP_REALM),
                    configMap.get(TestConstants.KEY_IDP_USER), "User", list);
            log(logLevel, "setup", "IDP user created is " + list);
            
            //if profile is set to post, change the metadata & run the tests. 
            log(logLevel, "setup", "Profile is set to " + strProfile);
            if (strProfile.equals("post")) {
                setSPSSOProfile(webClient, fmSP, configMap, "post");
           }
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl + "/UI/Logout");
            consoleLogout(webClient, idpurl + "/UI/Logout");
        }
        exiting("setup");
    }
    
    /**
     * @DocTest: IDFF|Perform SP initiated federation.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void testSPInitFederation()
    throws Exception {
        entering("testSPInitFederation", null);
        try {
            log(logLevel, "testSPInitFederation", 
                    "Running: testSPInitFederation");
            getWebClient();
            log(logLevel, "testSPInitFederation", "Login to SP with " + 
                    TestConstants.KEY_SP_USER);
            consoleLogin(webClient, spurl, 
                    configMap.get(TestConstants.KEY_SP_USER),
                    configMap.get(TestConstants.KEY_SP_USER_PASSWORD));
            xmlfile = baseDir + "testspinitfederation.xml";
            getxmlSPIDFFFederate(xmlfile, configMap);
            log(logLevel, "testSPInitFederation", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPInitFederation", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPInitFederation");
    }
   
    /**
     * @DocTest: IDFF|Perform SP initiated SLO.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"},
    dependsOnMethods={"testSPInitFederation"})
    public void testSPInitSLO()
    throws Exception {
        entering("testSPInitSLO", null);
        try {
            log(logLevel, "testSPInitSLO", "Running: testSPInitSLO");
            xmlfile = baseDir + "testspinitslo.xml";
            getxmlSPIDFFLogout(xmlfile, configMap);
            log(logLevel, "testSPInitSLO", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPInitSLO", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPInitSLO");
    }
   
    /**
     * @DocTest: IDFF|Perform SP initiated SSO.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"},
    dependsOnMethods={"testSPInitSLO"})
    public void testSPInitSSO()
    throws Exception {
        entering("testSPInitSSO", null);
        try {
            log(logLevel, "testSPInitSSO", "Running: testSPInitSSO");
            log(logLevel, "testSPInitSSO", "Login to IDP with " + 
                    TestConstants.KEY_IDP_USER);
            consoleLogin(webClient, idpurl, 
                    configMap.get(TestConstants.KEY_IDP_USER),
                    configMap.get(TestConstants.KEY_IDP_USER_PASSWORD));
            xmlfile = baseDir + "testspinitsso.xml";
            getxmlSPIDFFSSO(xmlfile, configMap);
            log(logLevel, "testSPInitSSO", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPInitSSO", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPInitSSO");
    }
   
    /**
     * @DocTest: IDFF|Perform SP initiated Name registration.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"},
    dependsOnMethods={"testSPInitSSO"})
    public void testSPInitNameReg()
    throws Exception {
        entering("testSPInitNameReg", null);
        try {
            log(logLevel, "testSPInitNameReg", "Running: testSPInitNameReg");
            xmlfile = baseDir + "testspinitnamereg.xml";
            getxmlSPIDFFNameReg(xmlfile, configMap);
            log(logLevel, "testSPInitNameReg", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPInitNameReg", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPInitNameReg");
    }
   
    /**
     * @DocTest: IDFF|Perform SP initiated Termination.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"},
    dependsOnMethods={"testSPInitNameReg"})
    public void testSPInitTerminate()
    throws Exception {
        entering("testSPInitTerminate", null);
        try {
            log(logLevel, "testSPInitTerminate", 
                    "Running: testSPInitTerminate");
            xmlfile = baseDir + "testspinitterminate.xml";
            getxmlSPIDFFTerminate(xmlfile, configMap);
            log(logLevel, "testSPInitTerminate", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testSPInitTerminate", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPInitTerminate");
    }

    /**
     * @DocTest: IDFF|Perform IDP initiated SLO.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"},
    dependsOnMethods={"testSPInitTerminate"})
    public void testIDPInitSLO()
    throws Exception {
        entering("testIDPInitSLO", null);
        try {
            log(logLevel, "testIDPInitSLO", "Running: testIDPInitSLO");
            xmlfile = baseDir + "testspinitfederation.xml";
            getWebClient();
            consoleLogin(webClient, spurl, 
                    configMap.get(TestConstants.KEY_SP_USER),
                    configMap.get(TestConstants.KEY_SP_USER_PASSWORD));
            getxmlSPIDFFFederate(xmlfile, configMap);
            log(logLevel, "testIDPInitSLO", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
            xmlfile = baseDir + "testidpinitslo.xml";
            getxmlIDPIDFFLogout(xmlfile, configMap);
            log(logLevel, "testIDPInitSLO", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPInitSLO", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPInitSLO");
    }
   
    /**
     * @DocTest: IDFF|Perform IDP initiated Name registration.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"},
    dependsOnMethods={"testIDPInitSLO"})
    public void testIDPInitNameReg()
    throws Exception {
        entering("testIDPInitNameReg", null);
        try {
            log(logLevel, "testIDPInitNameReg", "Running: testIDPInitNameReg");
            consoleLogin(webClient, idpurl, 
                    configMap.get(TestConstants.KEY_IDP_USER),
                    configMap.get(TestConstants.KEY_IDP_USER_PASSWORD));
            xmlfile = baseDir + "testidpinitnamereg.xml";
            getxmlIDPIDFFNameReg(xmlfile, configMap);
            log(logLevel, "testIDPInitNameReg", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPInitNameReg", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPInitNameReg");
    }
   
    /**
     * @DocTest: IDFF|Perform IDP initiated Termination.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"},
    dependsOnMethods={"testIDPInitNameReg"})
    public void testIDPInitTerminate()
    throws Exception {
        entering("testIDPInitTerminate", null);
        try {
            log(logLevel, "testIDPInitTerminate", "Running: " +
                    "testIDPInitTerminate");
            xmlfile = baseDir + "testspinitterminate.xml";
            getxmlIDPIDFFTerminate(xmlfile, configMap);
            log(logLevel, "testIDPInitTerminate", "Run " + xmlfile);
            task = new DefaultTaskHandler(xmlfile);
            page = task.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPInitTerminate", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPInitTerminate");
    }

    /**
     * This methods deletes all the users as part of cleanup
     */
    @Parameters({"profile"})
    @AfterClass(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void cleanup(String strProfile)
    throws Exception {
        Object[] params = {strProfile};
        entering("cleanup", params);
        String spurl;
        String idpurl;
        ArrayList idList;
        try {
            log(logLevel, "Entering Cleanup ", null);
            getWebClient();
            
            // delete sp users
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT)
                    + configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_IDP_PORT) +
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
            } catch (Exception e) {
                log(Level.SEVERE, "cleanup", e.getMessage(), null);
                e.printStackTrace();
                throw e;
            }
        try {
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
            
            // Create idp users
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
            
            //Change the profile in ext metadata to artifact & run the tests. 
            log(logLevel, "setup", "Profile is set to " + strProfile);
            if (strProfile.equals("post")) {
                setSPSSOProfile(webClient, fmSP, configMap, "artifact");
           }
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl + "/UI/Logout");
            consoleLogout(webClient, idpurl + "/UI/Logout");
        }
        exiting("cleanup");
    }
}
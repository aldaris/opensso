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
 * $Id: SAMLv2RelayStateTests.java,v 1.1 2007-05-29 18:37:30 mrudulahg Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.samlv2;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.SAMLv2Common;
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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This class tests samlv2 scenarios with relay state
 * 1. SP Initiated SSO with relay state
 * 2. SP Initiated SSO with relay state with POST/SOAP profile
 * 3. IDP Initiated SSO with relay state
 * 4. IDP Initiated SSO with relay state with POST/SOAP profile
 * 5. SP Initiated SLO with relay state
 * 6. SP Initiated SLO with relay state with POST/SOAP profile
 * 7. IDP Initiated SLO with relay state
 * 8. IDP Initiated SLO with relay state with POST/SOAP profile
 * 9. SP Initiated SSO & SLO with relay states
 * 10. SP Initiated SSO & SLO with relay states with POST/SOAP profile
 * 11. IDP Initiated SSO & SLO with relay states
 * 12. IDP Initiated SSO & SLO with relay states with POST/SOAP profile
 */
public class SAMLv2RelayStateTests extends TestCommon {
    
    private WebClient webClient;
    private FederationManager fmSP;
    private FederationManager fmIDP;
    private DefaultTaskHandler task;
    private Map<String, String> configMap;
    private Map<String, String> usersMap;
    ArrayList spuserlist = new ArrayList();
    ArrayList idpuserlist = new ArrayList();
    private String  baseDir;
    private HtmlPage page;
    private URL url;
    
    /** Creates a new instance of SAMLv2RelayStateTests */
    public SAMLv2RelayStateTests() {
        super("SAMLv2RelayStateTests");
    }
    
    /**
     * Create the webClient which should be run before each test.
     */
    @BeforeMethod(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec",
    "ldapv3_sec"})
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
    @BeforeClass(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void setup()
    throws Exception {
        List<String> list;
        String spurl;
        String idpurl;
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
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
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
                log(Level.SEVERE, "getWebClient", e.getMessage(), null);
                e.printStackTrace();
                throw e;
            }
        try {
            getWebClient();
            list = new ArrayList();
            consoleLogin(webClient, spurl,
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            
            fmSP = new FederationManager(spurl);
            consoleLogin(webClient, idpurl, configMap.get(
                    TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            fmIDP = new FederationManager(idpurl);
            
            usersMap = new HashMap<String, String>();
            usersMap = getMapFromResourceBundle("samlv2relaystatetests");
            Integer totalUsers = new Integer(
                    (String)usersMap.get("totalScenarios"));
            for (int i = 1; i < totalUsers + 1; i++) {
                //create sp user first
                list.clear();
                list.add("sn=" + usersMap.get(TestConstants.KEY_SP_USER + i));
                list.add("cn=" + usersMap.get(TestConstants.KEY_SP_USER + i));
                list.add("userpassword=" + usersMap.get(
                        TestConstants.KEY_SP_USER_PASSWORD + i));
                list.add("inetuserstatus=Active");
                fmSP.createIdentity(webClient,
                        configMap.get(TestConstants.KEY_SP_REALM),
                        usersMap.get(TestConstants.KEY_SP_USER + i), "User",
                        list);
                spuserlist.add(usersMap.get(TestConstants.KEY_SP_USER + i));
                
                //create idp user
                list.clear();
                list.add("sn=" + usersMap.get(TestConstants.KEY_IDP_USER + i));
                list.add("cn=" + usersMap.get(TestConstants.KEY_IDP_USER + i));
                list.add("userpassword=" + usersMap.get(
                        TestConstants.KEY_IDP_USER_PASSWORD + i));
                list.add("inetuserstatus=Active");
                fmIDP.createIdentity(webClient,
                        configMap.get(TestConstants.KEY_IDP_REALM),
                        usersMap.get(TestConstants.KEY_IDP_USER + i), "User",
                        list);
                idpuserlist.add(usersMap.get(TestConstants.KEY_IDP_USER + i));
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
     * @DocTest: SAML2|SP Init SSO with RelayState set
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2SPInitSSORS()
    throws Exception {
        entering("samlv2SPInitSSORS", null);
        try {
            log(logLevel, "\nRunning: samlv2SPInitSSORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 1));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 1));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 1));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 1));
            configMap.put(TestConstants.KEY_SSO_INIT_RESULT,
                    usersMap.get(TestConstants.KEY_SSO_INIT_RESULT + 1));
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate1"));
            log(logLevel, "samlv2SPInitSSORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2spinitssors_ssoinit", 
                    "samlv2spinitssors_slo", "samlv2spinitssors_terminate"};
            String ssoxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlSPInitSSO(ssoxmlfile, configMap, "artifact",
                    false);
            configMap.remove("urlparams");
            String sloxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlSPSLO(sloxmlfile, configMap, "http");
            String termxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlSPTerminate(termxmlfile, configMap, "http");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2SPInitSSORS",
                        "Executing xml file: " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2SPInitSSORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2SPInitSSORS");
    }
    
    /**
     * @DocTest: SAML2|SP Init SSO with RelayState with post/soap binding
     */
    @Test(groups={"ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2SPInitPostSSORS()
    throws Exception {
        entering("samlv2SPInitPostSSORS", null);
        try {
            log(logLevel, "\nRunning: samlv2SPInitPostSSORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 2));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 2));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 2));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 2));
            configMap.put(TestConstants.KEY_SSO_INIT_RESULT,
                    usersMap.get(TestConstants.KEY_SSO_INIT_RESULT + 2));
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate2"));
            log(logLevel, "samlv2SPInitPostSSORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2spinitpostssors_ssoinit", 
            "samlv2spinitpostssors_slo", "samlv2spinitpostssors_terminate"};
            String ssoxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlSPInitSSO(ssoxmlfile, configMap, "post", false);
            configMap.remove("urlparams");
            String sloxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlSPSLO(sloxmlfile, configMap, "soap");
            String termxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlSPTerminate(termxmlfile, configMap, "soap");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2SPInitPostSSORS",
                        "Executing xml file: " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2SPInitPostSSORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2SPInitPostSSORS");
    }
    
    /**
     * @DocTest: SAML2|IDP initiated SSO RelayState set.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2IDPInitSSORS()
    throws Exception {
        entering("samlv2IDPInitSSORS", null);
        try {
            log(logLevel, "\nRunning: samlv2IDPInitSSORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 3));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 3));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 3));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 3));
            configMap.put(TestConstants.KEY_SSO_INIT_RESULT,
                    usersMap.get(TestConstants.KEY_SSO_INIT_RESULT + 3));
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate3"));
            log(logLevel, "samlv2IDPInitSSORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2idpinitssors_idplogin", 
                    "samlv2idpinitssors_ssoinit", "samlv2idpinitssors_slo", 
                    "samlv2idpinitssors_terminate"};
            String loginxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlIDPLogin(loginxmlfile, configMap);
            String ssoxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlIDPInitSSO(ssoxmlfile, configMap, "artifact",
                    false);
            configMap.remove("urlparams");
            String sloxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlIDPSLO(sloxmlfile, configMap, "http");
            String termxmlfile = baseDir + arrActions[3] + ".xml";
            SAMLv2Common.getxmlIDPTerminate(termxmlfile, configMap, "http");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2IDPInitSSORS",
                        "Executing xml file: " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2IDPInitSSORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2IDPInitSSORS");
    }
    
    /**
     * @DocTest: SAML2|IDP Init SSO with RelayState set with POST/SOAP binding
     */
    @Test(groups={"ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2IDPInitPostSSORS()
    throws Exception {
        entering("samlv2IDPInitPostSSORS", null);
        try {
            log(logLevel, "Running: samlv2IDPInitPostSSORS", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 4));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 4));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 4));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 4));
            configMap.put(TestConstants.KEY_SSO_INIT_RESULT,
                    usersMap.get(TestConstants.KEY_SSO_INIT_RESULT + 4));
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate4"));
            log(logLevel, "samlv2IDPInitPostSSORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2idpinitpostssors_idplogin", 
                    "samlv2idpinitpostssors_ssoinit",
                    "samlv2idpinitpostssors_slo", 
                    "samlv2idpinitpostssors_terminate"};
            String loginxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlIDPLogin(loginxmlfile, configMap);
            String ssoxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlIDPInitSSO(ssoxmlfile, configMap, "post", false);
            configMap.remove("urlparams");
            String sloxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlIDPSLO(sloxmlfile, configMap, "soap");
            String termxmlfile = baseDir + arrActions[3] + ".xml";
            SAMLv2Common.getxmlIDPTerminate(termxmlfile, configMap, "soap");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2IDPInitPostSSORS",
                        "Executing xml file:  " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2IDPInitPostSSORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2IDPInitPostSSORS");
    }
    
    /**
     * @DocTest: SAML2|SP Init SLO with RelayState set
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2SPInitSLORS()
    throws Exception {
        entering("samlv2SPInitSLORS", null);
        try {
            log(logLevel, "\nRunning: samlv2SPInitSLORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 5));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 5));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 5));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 5));
            configMap.put(TestConstants.KEY_SP_SLO_RESULT,
                    usersMap.get(TestConstants.KEY_SP_SLO_RESULT + 5));
            log(logLevel, "samlv2SPInitSLORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2spinitslors_ssoinit", 
                    "samlv2spinitslors_slo", "samlv2spinitslors_terminate"};
            String ssoxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlSPInitSSO(ssoxmlfile, configMap, "artifact",
                    false);
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate5"));
            String sloxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlSPSLO(sloxmlfile, configMap, "http");
            configMap.remove("urlparams");
            String termxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlSPTerminate(termxmlfile, configMap, "http");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2SPInitSLORS",
                        "Executing xml file:  " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2SPInitSLORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2SPInitSLORS");
    }
    
    /**
     * @DocTest: SAML2|SP Init SLO with RelayState with post/soap binding
     */
    @Test(groups={"ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2SPInitPostSLORS()
    throws Exception {
        entering("samlv2SPInitPostSLORS", null);
        try {
            log(logLevel, "\nRunning: samlv2SPInitPostSLORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 6));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 6));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 6));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 6));
            configMap.put(TestConstants.KEY_SP_SLO_RESULT,
                    usersMap.get(TestConstants.KEY_SP_SLO_RESULT + 6));
            log(logLevel, "samlv2SPInitPostSLORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2spinitpostslors_ssoinit", 
                    "samlv2spinitpostslors_slo",
                    "samlv2spinitpostslors_terminate"};
            String ssoxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlSPInitSSO(ssoxmlfile, configMap, "post", false);
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate6"));
            String sloxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlSPSLO(sloxmlfile, configMap, "soap");
            configMap.remove("urlparams");
            String termxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlSPTerminate(termxmlfile, configMap, "soap");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2SPInitPostSLORS",
                        "Executing xml file:  " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2SPInitPostSLORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2SPInitPostSLORS");
    }
    
    /**
     * @DocTest: SAML2|IDP initiated SLO with RelayState set.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2IDPInitSLORS()
    throws Exception {
        entering("samlv2IDPInitSLORS", null);
        try {
            log(logLevel, "\nRunning: samlv2IDPInitSLORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 7));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 7));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 7));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 7));
            configMap.put(TestConstants.KEY_IDP_SLO_RESULT,
                    usersMap.get(TestConstants.KEY_IDP_SLO_RESULT + 7));
            log(logLevel, "samlv2IDPInitSLORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2idpinitslors_idplogin", 
                    "samlv2idpinitslors_ssoinit", "samlv2idpinitslors_slo", 
                    "samlv2idpinitslors_terminate"};
            String loginxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlIDPLogin(loginxmlfile, configMap);
            String ssoxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlIDPInitSSO(ssoxmlfile, configMap, "artifact",
                    false);
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate1"));
            String sloxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlIDPSLO(sloxmlfile, configMap, "http");
            configMap.remove("urlparams");
            String termxmlfile = baseDir + arrActions[3] + ".xml";
            SAMLv2Common.getxmlIDPTerminate(termxmlfile, configMap, "http");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2IDPInitSLORS",
                        "Executing xml file: " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2IDPInitSLORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2IDPInitSLORS");
    }
    
    /**
     * @DocTest: SAML2|IDP Init SLO with Relay state POST/SOAP binding
     */
    @Test(groups={"ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2IDPInitPostSLORS()
    throws Exception {
        entering("samlv2IDPInitPostSLORS", null);
        try {
            log(logLevel, "\nRunning: samlv2IDPInitPostSLORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 8));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 8));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 8));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 8));
            configMap.put(TestConstants.KEY_IDP_SLO_RESULT,
                    usersMap.get(TestConstants.KEY_IDP_SLO_RESULT + 8));
            log(logLevel, "samlv2IDPInitPostSLORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2idpinitpostslors_idplogin", 
                    "samlv2idpinitpostslors_ssoinit",
                    "samlv2idpinitpostslors_slo", 
                    "samlv2idpinitpostslors_terminate"};
            String loginxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlIDPLogin(loginxmlfile, configMap);
            String ssoxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlIDPInitSSO(ssoxmlfile, configMap, "post", false);
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate8"));
            String sloxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlIDPSLO(sloxmlfile, configMap, "soap");
            configMap.remove("urlparams");
            String termxmlfile = baseDir + arrActions[3] + ".xml";
            SAMLv2Common.getxmlIDPTerminate(termxmlfile, configMap, "soap");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2IDPInitPostSLORS",
                        "Executing xml file:  " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2IDPInitPostSLORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2IDPInitPostSLORS");
    }
    
    /**
     * @DocTest: SAML2|SP Init SSO/SLO with diff RelayState set
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2SPInitSSOSLORS()
    throws Exception {
        entering("samlv2SPInitSSOSLORS", null);
        try {
            log(logLevel, "\nRunning: samlv2SPInitSSOSLORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 9));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 9));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 9));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 9));
            configMap.put(TestConstants.KEY_SSO_INIT_RESULT,
                    usersMap.get(TestConstants.KEY_SSO_INIT_RESULT + 9));
            configMap.put(TestConstants.KEY_SP_SLO_RESULT,
                    usersMap.get(TestConstants.KEY_SP_SLO_RESULT + 9));
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate9"));
            log(logLevel, "samlv2SPInitSSOSLORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2spinitssoslors_ssoinit", 
                    "samlv2spinitssoslors_slo",
                    "samlv2spinitssoslors_terminate"};
            String ssoxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlSPInitSSO(ssoxmlfile, configMap, "artifact",
                    false);
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate9"));
            String sloxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlSPSLO(sloxmlfile, configMap, "http");
            configMap.remove("urlparams");
            String termxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlSPTerminate(termxmlfile, configMap, "http");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2SPInitSSOSLORS",
                        "Executing xml file:  " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2SPInitSSOSLORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2SPInitSSOSLORS");
    }
    
    /**
     * @DocTest: SAML2|SP Init SSO/SLO with diff RelayState set POST/SOAP binding
     */
    @Test(groups={"ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2SPInitPostSSOSLORS()
    throws Exception {
        entering("samlv2SPInitPostSSOSLORS", null);
        try {
            log(logLevel, "\nRunning: samlv2SPInitPostSSOSLORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 10));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 10));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 10));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 10));
            configMap.put(TestConstants.KEY_SSO_INIT_RESULT,
                    usersMap.get(TestConstants.KEY_SSO_INIT_RESULT + 10));
            configMap.put(TestConstants.KEY_SP_SLO_RESULT,
                    usersMap.get(TestConstants.KEY_SP_SLO_RESULT + 10));
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate10"));
            log(logLevel, "samlv2SPInitPostSSOSLORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2spinitpostssoslors_ssoinit", 
                    "samlv2spinitpostssoslors_slo",
                    "samlv2spinitpostssoslors_terminate"};
            String ssoxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlSPInitSSO(ssoxmlfile, configMap, "artifact",
                    false);
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate10"));
            String sloxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlSPSLO(sloxmlfile, configMap, "http");
            configMap.remove("urlparams");
            String termxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlSPTerminate(termxmlfile, configMap, "http");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2SPInitPostSSOSLORS",
                        "Executing xml file:  " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2SPInitPostSSOSLORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2SPInitPostSSOSLORS");
    }
    
    /**
     * @DocTest: SAML2|IDP initiated SSO/SLO with diff RelayState set.
     */
    @Test(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2IDPInitSSOSLORS()
    throws Exception {
        entering("samlv2IDPInitSSOSLORS", null);
        try {
            log(logLevel, "\nRunning: samlv2IDPInitSSOSLORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 11));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 11));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 11));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 11));
            configMap.put(TestConstants.KEY_SSO_INIT_RESULT,
                    usersMap.get(TestConstants.KEY_SSO_INIT_RESULT + 11));
            configMap.put(TestConstants.KEY_IDP_SLO_RESULT,
                    usersMap.get(TestConstants.KEY_IDP_SLO_RESULT + 11));
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate11"));
            log(logLevel, "samlv2IDPInitSSOSLORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2idpinitssoslors_idplogin", 
                    "samlv2idpinitssoslors_ssoinit",
                    "samlv2idpinitssoslors_slo", 
                    "samlv2idpinitssoslors_terminate"};
            String loginxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlIDPLogin(loginxmlfile, configMap);
            String ssoxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlIDPInitSSO(ssoxmlfile, configMap, "artifact",
                    false);
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate11"));
            String sloxmlfile = baseDir + arrActions[2] + ".xml";
            SAMLv2Common.getxmlIDPSLO(sloxmlfile, configMap, "http");
            configMap.remove("urlparams");
            String termxmlfile = baseDir + arrActions[3] + ".xml";
            SAMLv2Common.getxmlIDPTerminate(termxmlfile, configMap, "http");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2IDPInitSSOSLORS",
                        "Executing xml file: " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2IDPInitSSOSLORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2IDPInitSSOSLORS");
    }
    
    /**
     * @DocTest: SAML2|IDP initiated SSO/SLO with diff RelayState set
     * with POST/SOAP binding.
     */
    @Test(groups={"ff_sec", "ds_sec", "ldapv3_sec"})
    public void samlv2IDPInitPostSSOSLORS()
    throws Exception {
        entering("samlv2IDPInitPostSSOSLORS", null);
        try {
            log(logLevel, "\nRunning: samlv2IDPInitPostSSOSLORS\n", null);
            
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2TestData"));
            configMap.put(TestConstants.KEY_SP_USER,
                    usersMap.get(TestConstants.KEY_SP_USER + 12));
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_SP_USER_PASSWORD + 12));
            configMap.put(TestConstants.KEY_IDP_USER,
                    usersMap.get(TestConstants.KEY_IDP_USER + 12));
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD,
                    usersMap.get(TestConstants.KEY_IDP_USER_PASSWORD + 12));
            configMap.put(TestConstants.KEY_SSO_INIT_RESULT,
                    usersMap.get(TestConstants.KEY_SSO_INIT_RESULT + 12));
            configMap.put(TestConstants.KEY_IDP_SLO_RESULT,
                    usersMap.get(TestConstants.KEY_IDP_SLO_RESULT + 12));
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate12"));
            log(logLevel, "samlv2IDPInitPostSSOSLORS", "Map:" + configMap);
            
            //Create xml's for each actions.
            String[] arrActions = {"samlv2idpinitpostssoslors_idplogin", 
                    "samlv2idpinitpostssoslors_ssoinit",
                    "samlv2idpinitpostssoslors_slo", 
                    "samlv2idpinitpostssoslors_terminate"};
            String loginxmlfile = baseDir + arrActions[0] + ".xml";
            SAMLv2Common.getxmlIDPLogin(loginxmlfile, configMap);
            String ssoxmlfile = baseDir + arrActions[1] + ".xml";
            SAMLv2Common.getxmlIDPInitSSO(ssoxmlfile, configMap, "artifact",
                    false);
            configMap.put("urlparams", "RelayState="
                    + usersMap.get("relaystate12"));
            String sloxmlfile = baseDir + arrActions[2] + ".xml";
            configMap.remove("urlparams");
            SAMLv2Common.getxmlIDPSLO(sloxmlfile, configMap, "http");
            String termxmlfile = baseDir + arrActions[3] + ".xml";
            SAMLv2Common.getxmlIDPTerminate(termxmlfile, configMap, "http");
            
            for (int i = 0; i < arrActions.length; i++) {
                log(logLevel, "samlv2IDPInitPostSSOSLORS",
                        "Executing xml file: " + arrActions[i]);
                task = new DefaultTaskHandler(baseDir + arrActions[i] + ".xml");
                page = task.execute(webClient);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "samlv2IDPInitPostSSOSLORS", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("samlv2IDPInitPostSSOSLORS");
    }
    
    
    /**
     * This methods deletes all the users as part of cleanup
     */
    @AfterClass(groups={"ff", "ds", "ldapv3", "ff_sec", "ds_sec", "ldapv3_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        String spurl;
        String idpurl;
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
            log(logLevel, "cleanup", "sp users to delete : " + spuserlist);
            consoleLogin(webClient, spurl, configMap.get(
                    TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(spurl);
            fmSP.deleteIdentities(webClient, configMap.get(
                    TestConstants.KEY_SP_REALM),
                    spuserlist, "User");
            
            // Create idp users
            log(logLevel, "cleanup", "idp users to delete : " + idpuserlist);
            consoleLogin(webClient, idpurl, configMap.get(
                    TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(idpurl);
            fmSP.deleteIdentities(webClient, configMap.get(
                    TestConstants.KEY_IDP_REALM),
                    idpuserlist, "User");
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

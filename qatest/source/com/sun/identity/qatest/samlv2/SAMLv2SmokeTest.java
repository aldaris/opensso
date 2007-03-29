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
 * $Id: SAMLv2SmokeTest.java,v 1.1 2007-03-29 21:40:53 mrudulahg Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.samlv2;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.sun.identity.qatest.common.AccessManager;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.SAMLv2Common;
import com.sun.identity.qatest.common.webtest.DefaultTaskHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.testng.Reporter;
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
    private AccessManager amC;

    public SAMLv2SmokeTest() {
        super("SAMLv2SmokeTest");
    }

    /**
     * This setup method creates required users. 
     */
    @BeforeClass(groups={"client"})
    public void setup() throws Exception {
        URL url;
        HtmlPage page;
        ArrayList list;
        try {
            log(logLevel, "setup", "Entering");
            //Upload global properties file in configMap
            baseDir = getBaseDir() + "/built/classes/";
            configMap = new HashMap<String, String>();
            SAMLv2Common.getEntriesFromResourceBundle("samlv2Test", configMap);
            SAMLv2Common.getEntriesFromResourceBundle("samlv2SmokeTest", 
                    configMap);
            Reporter.log("ConfigMap is : " + configMap );
    
            // Create sp users 
            String spurl = configMap.get("sp_proto") + "://" + 
                    configMap.get("sp_host") + ":" + configMap.get("sp_port")
                    + configMap.get("sp_deployment_uri");
            getWebClient();
            try{
                consoleLogin(webClient, spurl, configMap.get("sp_admin"), 
                        configMap.get("sp_adminpw"));
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
            amC = new AccessManager(spurl);
            list = new ArrayList();
            list.add("sn=" + configMap.get("sp_user"));
            list.add("cn=" + configMap.get("sp_user"));
            list.add("userpassword=" + configMap.get("sp_userpw"));
            amC.createIdentity(webClient, configMap.get("sp_realm"), 
                    configMap.get("sp_user"), "User",
                    list);
            String splogoutxmlfile = baseDir +  "setupsplogout.xml";
            SAMLv2Common.getxmlSPLogout(splogoutxmlfile, configMap);            
            task1 = new DefaultTaskHandler(splogoutxmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }

            // Create idp users 
            String idpurl = configMap.get("idp_proto") + "://" + 
                    configMap.get("idp_host") + ":" + configMap.get("idp_port")
                    + configMap.get("idp_deployment_uri");
            try{
                consoleLogin(webClient, idpurl, configMap.get("idp_admin"), 
                        configMap.get("idp_adminpw"));
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
    
            amC = new AccessManager(idpurl);
            list.clear();
            list.add("sn=" + configMap.get("idp_user"));
            list.add("cn=" + configMap.get("idp_user"));
            list.add("userpassword=" + configMap.get("idp_userpw"));
            amC.createIdentity(webClient, configMap.get("idp_realm"), 
                    configMap.get("idp_user"), "User", list);
            String idplogoutxmlfile = baseDir +  "setupidplogout.xml";
            SAMLv2Common.getxmlSPLogout(idplogoutxmlfile, configMap);            
            task1 = new DefaultTaskHandler(idplogoutxmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }

        }catch(Exception e) {
            log(Level.SEVERE, "setup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("setup");
    }

    /**
     * Create the webClient which will be used for the rest of the tests. 
     */
    @BeforeClass(groups={"client"})
    public void getWebClient() throws Exception {
        try {
            webClient = new WebClient(BrowserVersion.MOZILLA_1_0);
       }catch(Exception e) {
            log(Level.SEVERE, "getWebClient", e.getMessage(), null);
            e.printStackTrace();
            throw e;
       }
    }
    
    /**
     * Run saml2 profile testcase 1.
     * @DocTest: SAML2|Perform SP initiated sso. 
     */
    @Test(groups={"client"})
    public void testSPSSOInit()
        throws Exception {
        entering("testSPSSOInit", null);
        try {
            Reporter.log("Running: testSPSSOInit");
            getWebClient();
            
            xmlfile = baseDir + "test1spssoinit.xml";
            SAMLv2Common.getxmlSPInitSSO(xmlfile, configMap, "artifact");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @Test(groups={"client"}, dependsOnMethods={"testSPSSOInit"})
    public void testSPSLO()
        throws Exception {
        entering("testSPSLO", null);
        try {
            Reporter.log("Running: testSPSLO");
            
            xmlfile = baseDir + "test2spslo.xml";
            SAMLv2Common.getxmlSPSLO(xmlfile, configMap, "http");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @Test(groups={"client"}, dependsOnMethods={"testSPSLO"})
    public void testSPTerminate()
        throws Exception {
        entering("testSPTerminate", null);
        try {
            Reporter.log("Running: testSPTerminate");
            
            xmlfile = baseDir + "test3spterminate.xml";
            SAMLv2Common.getxmlSPTerminate(xmlfile, configMap, "http");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
        } catch (Exception e) {
            log(Level.SEVERE, "testSPTerminate", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPTerminate");
    }
   
    /**
     * Run saml2 profile testcase 4.
     * @DocTest: SAML2|Perform SP initiated sso with post profile. 
     */
    @Test(groups={"client"}, dependsOnMethods={"testSPTerminate"})
    public void testSPSSOInitPost()
        throws Exception {
        entering("testSPSSOInitPost", null);
        try {
            Reporter.log("Running: testSPSSOInitPost");
            getWebClient();
            xmlfile = baseDir + "test4spssoinit.xml";
            SAMLv2Common.getxmlSPInitSSO(xmlfile, configMap, "post");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
            page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @Test(groups={"client"}, dependsOnMethods={"testSPSSOInitPost"})
    public void testSPSLOSOAP()
        throws Exception {
        entering("testSPSLOSOAP", null);
        try {
            Reporter.log("Running: testSPSLOSOAP");
            
            xmlfile = baseDir + "test5spslo.xml";
            SAMLv2Common.getxmlSPSLO(xmlfile, configMap, "soap");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @Test(groups={"client"}, dependsOnMethods={"testSPSLOSOAP"})
    public void testSPTerminateSOAP()
        throws Exception {
        entering("testSPTerminateSOAP", null);
        try {
            Reporter.log("Running: testSPTerminateSOAP");
            
            xmlfile = baseDir + "test6spterminate.xml";
            SAMLv2Common.getxmlSPTerminate(xmlfile, configMap, "soap");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
        } catch (Exception e) {
            log(Level.SEVERE, "testSPTerminateSOAP", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testSPTerminateSOAP");
    }
            
    /**
     * Run saml2 profile .
     * @DocTest: SAML2|Perform idp initiated sso. 
     */
    @Test(groups={"client"}, dependsOnMethods={"testSPTerminateSOAP"})
    public void testIDPSSO()
        throws Exception {
        entering("testIDPSSO", null);
        try {
            Reporter.log("\nRunning: testIDPSSO\n");
            getWebClient();
            xmlfile = baseDir + "test7idplogin.xml";
            SAMLv2Common.getxmlIDPLogin(xmlfile, configMap);            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }

            xmlfile = baseDir + "test7idpssoinit.xml";
            SAMLv2Common.getxmlIDPInitSSO(xmlfile, configMap, "artifact");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @Test(groups={"client"}, dependsOnMethods={"testIDPSSO"})
    public void testIDPSLO()
        throws Exception {
        entering("testIDPSLO", null);
        try {
            Reporter.log("Running: testIDPSLO");
            
            xmlfile = baseDir + "test8idpslo.xml";
            SAMLv2Common.getxmlIDPSLO(xmlfile, configMap, "http");     
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @Test(groups={"client"}, dependsOnMethods={"testIDPSLO"})
    public void testIDPTerminate()
        throws Exception {
        entering("testIDPTerminate", null);
        try {
            Reporter.log("Running: testIDPTerminate");
            
            xmlfile = baseDir + "test9idpterminate.xml";
            SAMLv2Common.getxmlIDPTerminate(xmlfile, configMap, "http");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
        } catch (Exception e) {
            log(Level.SEVERE, "testIDPTerminate", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("testIDPTerminate");
    }
            
    /**
     * Run saml2 profile testcase 1.
     * @DocTest: SAML2|Perform  idp initiated sso with post profile. 
     */
    @Test(groups={"client"}, dependsOnMethods={"testIDPTerminate"})
    public void testIDPSSOInitPost()
        throws Exception {
        entering("testIDPSSOInitPost", null);
        try {
            Reporter.log("Running: testIDPSSOInitPost");
            getWebClient();
            xmlfile = baseDir + "test10idplogin.xml";
            SAMLv2Common.getxmlIDPLogin(xmlfile, configMap);            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
            
            xmlfile = baseDir + "test10idpssoinit.xml";
            SAMLv2Common.getxmlIDPInitSSO(xmlfile, configMap, "post");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @Test(groups={"client"}, dependsOnMethods={"testIDPSSOInitPost"})
    public void testIDPSLOSOAP()
        throws Exception {
        entering("testIDPSLOSOAP", null);
        try {
            Reporter.log("Running: testIDPSLOSOAP");
            
            xmlfile = baseDir + "test11idpslo.xml";
            SAMLv2Common.getxmlSPSLO(xmlfile, configMap, "soap");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @Test(groups={"client"}, dependsOnMethods={"testIDPSLOSOAP"})
    public void testIDPTerminateSOAP()
        throws Exception {
        entering("testIDPTerminateSOAP", null);
        try {
            Reporter.log("Running: testIDPTerminateSOAP");
            xmlfile = baseDir + "test12idpterminate.xml";
            SAMLv2Common.getxmlSPTerminate(xmlfile, configMap, "soap");            
            Reporter.log("Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            try{
                page1 = task1.execute(webClient);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
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
    @AfterClass(groups={"client"})
    public void cleanup()
        throws Exception {
        entering("cleanup", null);
        HtmlPage page;
        URL url;
        ArrayList idList;
        try {
            getWebClient();
            // delete sp users 
            String spurl = configMap.get("sp_proto") + "://" + 
                    configMap.get("sp_host") + ":" + configMap.get("sp_port")
                    + configMap.get("sp_deployment_uri");
            try{
                consoleLogin(webClient, spurl, configMap.get("sp_admin"), 
                        configMap.get("sp_adminpw"));
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
            amC = new AccessManager(spurl);
            idList = new ArrayList();
            idList.add(configMap.get("sp_user"));
            Reporter.log("sp users to delete :" + configMap.get("sp_user"));
            amC.deleteIdentities(webClient, configMap.get("sp_realm"), 
                    idList, "User");
            url = new URL(spurl + "/UI/Logout");
            try{
                page = (HtmlPage)webClient.getPage(url);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }

            // Create idp users 
            String idpurl = configMap.get("idp_proto") + "://" + 
                    configMap.get("idp_host") + ":" + configMap.get("idp_port")
                    + configMap.get("idp_deployment_uri");
            try{
                consoleLogin(webClient, idpurl, configMap.get("idp_admin"), 
                        configMap.get("idp_adminpw"));
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            } 
            amC = new AccessManager(idpurl);
            idList = new ArrayList();
            idList.add(configMap.get("idp_user"));
            Reporter.log("idp users to delete :" + configMap.get("idp_user"));
            amC.deleteIdentities(webClient, configMap.get("idp_realm"), 
                    idList, "User");
            url = new URL(idpurl + "/UI/Logout");
            try{
                page = (HtmlPage)webClient.getPage(url);
            }catch (ScriptException e){
                //Do nothing if there is javascript exception
            }
            
        } catch(Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("cleanup");
    }

}

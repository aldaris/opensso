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
 * $Id: WSFedSmokeTest.java,v 1.4 2008-06-26 20:27:37 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.wsfed;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.WSFedCommon;
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
 * This class tests SP SSO, SLO & IDP initiated SLO
 */
public class WSFedSmokeTest extends WSFedCommon {
    
    public WebClient webClient;
    private Map<String, String> configMap;
    private String baseDir;
    private String xmlfile;
    private DefaultTaskHandler task1;
    private HtmlPage page1;
    private URL url;
    private FederationManager fmSP;
    private FederationManager fmIDP;
    private String fileseparator;
    private String spurl;
    private String idpurl;
    
    /**
     * This is constructor for this class.
     */
    public WSFedSmokeTest() {
        super("WSFedSmokeTest");
    }
    
    /**
     * This setup method creates required users.
     */
    @BeforeClass(groups={"ds_ds_sec", "ff_ds_sec"})
    public void setup()
    throws Exception {
        ArrayList list;
        try {
            entering("setup", null);
            fileseparator = System.getProperty("file.separator");
            //Upload global properties file in configMap
            ResourceBundle rb_amconfig = ResourceBundle.getBundle(
                    TestConstants.TEST_PROPERTY_AMCONFIG);
            baseDir = getBaseDir() + fileseparator
                    + rb_amconfig.getString(TestConstants.KEY_ATT_SERVER_NAME)
                    + fileseparator + "built" + fileseparator + "classes"
                    + fileseparator;
            configMap = new HashMap<String, String>();
            configMap = getMapFromResourceBundle("wsfed" + fileseparator +
                    "WSFedTestConfigData");
            configMap.putAll(getMapFromResourceBundle("wsfed" + fileseparator +
                    "WSFedTestData"));
            configMap.putAll(getMapFromResourceBundle("wsfed" + fileseparator +
                    "WSFedSmokeTest"));
            log(Level.FINEST, "setup", "ConfigMap is : " + configMap );
            
            // Create sp users
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT) +
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            getWebClient();
            consoleLogin(webClient, spurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(spurl);
            list = new ArrayList();
            list.add("sn=" + configMap.get(TestConstants.KEY_SP_USER));
            list.add("cn=" + configMap.get(TestConstants.KEY_SP_USER));
            list.add("userpassword=" +
                    configMap.get(TestConstants.KEY_SP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            if (FederationManager.getExitCode(fmSP.createIdentity(webClient,
                    configMap.get(TestConstants.KEY_SP_EXECUTION_REALM),
                    configMap.get(TestConstants.KEY_SP_USER), "User", list))
                    != 0) {
               log(Level.SEVERE, "setup", "createIdentity famadm command" +
                       " failed");
                assert false;
            }
            
            // Create idp users
            idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_IDP_PORT) +
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
            consoleLogin(webClient, idpurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            
            fmIDP = new FederationManager(idpurl);
            list.clear();
            list.add("sn=" + configMap.get(TestConstants.KEY_IDP_USER));
            list.add("cn=" + configMap.get(TestConstants.KEY_IDP_USER));
            list.add("userpassword=" +
                    configMap.get(TestConstants.KEY_IDP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            if (FederationManager.getExitCode(fmIDP.createIdentity(webClient,
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM),
                    configMap.get(TestConstants.KEY_IDP_USER), "User", list))
                    != 0) {
               log(Level.SEVERE, "setup", "createIdentity famadm command" +
                       " failed");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl + "/UI/Logout");
            consoleLogout(webClient, idpurl + "/UI/Logout");
        }
        exiting("setup");
    }
    
    /**
     * Create the webClient which will be used for the rest of the tests.
     */
    @BeforeClass(groups={"ds_ds_sec", "ff_ds_sec"})
    public void getWebClient()
    throws Exception {
        try {
            webClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        } catch (Exception e) {
            log(Level.SEVERE, "getWebClient", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Run WSFed SP initiated SSO.
     */
    @Test(groups={"ds_ds_sec", "ff_ds_sec"})
    public void wsfedSPSSOInit()
    throws Exception {
        entering("wsfedSPSSOInit", null);
        try {
            log(Level.FINE, "wsfedSPSSOInit",
                    "Running: wsfedSPSSOInit");
            getWebClient();
            log(Level.FINE, "wsfedSPSSOInit", "Login to SP with " +
                    configMap.get(TestConstants.KEY_SP_USER));
            xmlfile = baseDir + "wsfedsmoketest_SPSSOInit.xml";
            getxmlSPInitSSO(xmlfile, configMap);
            log(Level.FINE, "wsfedSPSSOInit", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
        } catch (Exception e) {
            log(Level.SEVERE, "wsfedSPSSOInit", e.getMessage());
            consoleLogout(webClient, spurl + "/UI/Logout");
            consoleLogout(webClient, idpurl + "/UI/Logout");
            e.printStackTrace();
            throw e;
        }
        exiting("wsfedSPSSOInit");
    }
    
    /**
     * Run WSFed SP initiated SLO.
     */
    @Test(groups={"ds_ds_sec", "ff_ds_sec"}, 
    dependsOnMethods={"wsfedSPSSOInit"})
    public void wsfedSPSLOInit()
    throws Exception {
        entering("wsfedSPSLOInit", null);
        try {
            log(Level.FINE, "wsfedSPSLOInit",
                    "Running: wsfedSPSLOInit");
            getWebClient();
            xmlfile = baseDir + "wsfedsmoketest_SPSLOInit.xml";
            getxmlSPInitSLO(xmlfile, configMap);
            log(Level.FINE, "wsfedSPSLOInit", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
            url = new URL(spurl);
            HtmlPage page = (HtmlPage)webClient.getPage(url);
            if (getHtmlPageStringIndex(page, "Authentication") != -1) {
                log(Level.FINE, "wsfedSPSLOInit", "SP side logout was " +
                        "successful");
            } else {
                log(Level.FINE, "wsfedSPSLOInit", "SP side logout wasnot " +
                        "successful");
                assert false;
            }
            url = new URL(idpurl);
            page = (HtmlPage)webClient.getPage(url);
            if (getHtmlPageStringIndex(page, "Authentication") != -1) {
                log(Level.FINE, "wsfedSPSLOInit", "IDP side logout was " +
                        "successful");
            } else {
                log(Level.FINE, "wsfedSPSLOInit", "IDP side logout wasnot " +
                        "successful");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "wsfedSPSLOInit", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl + "/UI/Logout");
            consoleLogout(webClient, idpurl + "/UI/Logout");
        }
        exiting("wsfedSPSLOInit");
    }
    
    /**
     * Run wsfed IDP init SLO
     */
    @Test(groups={"ds_ds_sec", "ff_ds_sec"}, 
    dependsOnMethods={"wsfedSPSLOInit"})
    public void wsfedIDPSLOInit()
    throws Exception {
        entering("wsfedIDPSLOInit", null);
        try {
            log(Level.FINE, "wsfedIDPSLOInit",
                    "Running: wsfedIDPSLOInit");
            xmlfile = baseDir + "wsfedsmoketest_SPSLOInit.xml";
            getxmlSPInitSSO(xmlfile, configMap);
            log(Level.FINE, "wsfedIDPSLOInit", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
            xmlfile = baseDir + "wsfedsmoketest_IDPSLOInit.xml";
            getxmlIDPInitSLO(xmlfile, configMap);
            log(Level.FINE, "wsfedIDPSLOInit", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            page1 = task1.execute(webClient);
            url = new URL(spurl);
            HtmlPage page = (HtmlPage)webClient.getPage(url);
            if (getHtmlPageStringIndex(page, "Authentication") != -1) {
                log(Level.FINE, "wsfedSPSLOInit", "SP side logout was " +
                        "successful");
            } else {
                log(Level.FINE, "wsfedSPSLOInit", "SP side logout wasnot " +
                        "successful");
                assert false;
            }
            url = new URL(idpurl);
            page = (HtmlPage)webClient.getPage(url);
            if (getHtmlPageStringIndex(page, "Authentication") != -1) {
                log(Level.FINE, "wsfedSPSLOInit", "IDP side logout was " +
                        "successful");
            } else {
                log(Level.FINE, "wsfedSPSLOInit", "IDP side logout wasnot " +
                        "successful");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "wsfedIDPSLOInit", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl + "/UI/Logout");
            consoleLogout(webClient, idpurl + "/UI/Logout");
        }
        exiting("wsfedIDPSLOInit");
    }
    
    /**
     * This methods deletes all the users as part of cleanup
     */
    @AfterClass(groups={"ds_ds_sec", "ff_ds_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        ArrayList idList;
        try {
            log(Level.FINE, "cleanup", "Entering Cleanup");
            getWebClient();
            consoleLogin(webClient, spurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            idList = new ArrayList();
            idList.add(configMap.get(TestConstants.KEY_SP_USER));
            log(Level.FINE, "cleanup", "sp users to delete :" +
                    configMap.get(TestConstants.KEY_SP_USER));
            if (FederationManager.getExitCode(fmSP.deleteIdentities(webClient,
                    configMap.get(TestConstants.KEY_SP_EXECUTION_REALM), idList,
                    "User")) != 0) {
                log(Level.SEVERE, "cleanup", "deleteIdentities famadm command" +
                        " failed");
                assert false;
            }
            
            consoleLogin(webClient, idpurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            idList = new ArrayList();
            idList.add(configMap.get(TestConstants.KEY_IDP_USER));
            log(Level.FINE, "cleanup", "idp users to delete :" +
                    configMap.get(TestConstants.KEY_IDP_USER));
            if (FederationManager.getExitCode(fmIDP.deleteIdentities(webClient,
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM), idList,
                    "User")) != 0) {
                log(Level.SEVERE, "cleanup", "deleteIdentities famadm command" +
                        " failed");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl + "/UI/Logout");
            consoleLogout(webClient, idpurl + "/UI/Logout");
        }
        exiting("cleanup");
    }
}

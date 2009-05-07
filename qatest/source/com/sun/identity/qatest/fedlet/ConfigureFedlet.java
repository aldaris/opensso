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
 * $Id: ConfigureFedlet.java,v 1.2 2009-05-07 22:27:57 vimal_67 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.fedlet;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.FedletCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.cli.JarUtility;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * This class creates cot, creates and configures SAMLv2 IDP configuration and
 * creates fedlet and configures it. 
 */
public class ConfigureFedlet extends TestCommon {
    private HtmlPage page;    
    private WebClient idpWebClient;
    private Map<String, String> configMap;    
    private Server server;
    public String groupName = "";    
    private String clientURL;    
    String fedletidpurl;
    String fedleturl;    
    private JarUtility jarUti;
    File zipLocation;
    File UnzipLocation;
    private ResourceBundle rb_clientGlobal;    
    
    /** Creates a new instance of configureFedlet */
    public ConfigureFedlet() {
        super("ConfigureFedlet");
    }
    
    /**
     * Create the webClient which should be run before each test.
     */
    private void getWebClient() throws Exception {
        try {            
            idpWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        } catch(Exception e) {
            log(Level.SEVERE, "getWebClient", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Creates fedlet and configures it. 
     * @DocTest: SAML2|Configures IDP by loading metadata on both sides.
     */
    @Parameters({"groupName"})
    @BeforeSuite(groups = {"s1ds", "ldapv3", "ad", "jdbc", "amsdk", "s1ds_sec",
        "ldapv3_sec", "ad_sec", "jdbc_sec", "amsdk_sec"})
    public void configureFedlet(String strGroupName)
    throws Exception {
        Object[] params = {strGroupName};
        entering("configureFedlet", params);
        try {                      
            //Upload global properties file in configMap
            configMap = new HashMap<String, String>();  
            jarUti = new JarUtility();            
            getWebClient();
            
            log(Level.FINEST, "configureFedlet", "GroupName received from " +
                    "testng is " + strGroupName);            
            FedletCommon.getEntriesFromResourceBundle("fedlet" +
                    fileseparator + "FedletTests", configMap);
            FedletCommon.getEntriesFromResourceBundle("AMConfig", configMap);
            rb_clientGlobal = ResourceBundle.getBundle("config/default" +
                    fileseparator + "ClientGlobal");
            log(Level.FINEST, "configureFedlet", "Map:" + configMap);
            
            fedletidpurl = configMap.get(TestConstants.KEY_AMC_PROTOCOL)
                    + "://" + configMap.get(TestConstants.KEY_AMC_HOST) + ":"
                    + configMap.get(TestConstants.KEY_AMC_PORT)
                    + configMap.get(TestConstants.KEY_AMC_URI);
        } catch(Exception e) {
            log(Level.SEVERE, "configureFedlet", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        try {            
            // checking opensso idp server hostname with the qatest hostname
            String hostname = "";
            String idp_hostname = "";
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getCanonicalHostName();
            log(Level.FINEST, "configureFedlet", "QATest Hostname: " +
                        hostname);
            int index = configMap.get(TestConstants.KEY_AMC_HOST).indexOf(".");
            if (index != -1) {
                idp_hostname = configMap.get(
                        TestConstants.KEY_AMC_HOST).substring(0, index);
                log(Level.FINEST, "configureFedlet", "OpenSSO IDP Hostname: " +
                        idp_hostname);
            }            
            if (!hostname.contains(idp_hostname)) {
                log(Level.SEVERE, "configureFedlet", "The Fedlet has to be " +
                        "run on the same machine as the OpenSSO IDP");
                assert false;            
            }
            
            //idp side create cot, load idp metadata
            FederationManager idpfm = new FederationManager(fedletidpurl);
            IDMCommon idmC = new IDMCommon();            
            
            consoleLogin(idpWebClient, fedletidpurl + "/UI/Login",
                    (String)configMap.get(TestConstants.KEY_ATT_AMADMIN_USER),
                    (String)configMap.get(
                    TestConstants.KEY_ATT_AMADMIN_PASSWORD));
            
            //If execution_realm is different than root realm (/)
            //then create the realm
            idmC.createSubRealms(idpWebClient, idpfm, configMap.get(
                    TestConstants.KEY_ATT_EXECUTION_REALM));
            
            HtmlPage idpcotPage = idpfm.listCots(idpWebClient,
                    configMap.get(TestConstants.KEY_ATT_EXECUTION_REALM));
            if (FederationManager.getExitCode(idpcotPage) != 0) {
                log(Level.SEVERE, "configureFedlet", "listCots famadm command" +
                        " failed");
                assert false;
            }
            if (idpcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_FEDLET_COT))) {
                log(Level.FINEST, "configureFedlet", "COT exists at IDP side");
            } else {
                if (FederationManager.getExitCode(idpfm.createCot(idpWebClient,
                        configMap.get(TestConstants.KEY_FEDLET_COT),
                        configMap.get(TestConstants.KEY_ATT_EXECUTION_REALM),
                        null, null)) != 0) {
                    log(Level.SEVERE, "configureFedlet", "Couldn't create " +
                            "COT at IDP side");
                    log(Level.SEVERE, "configureFedlet", "createCot famadm" +
                            " command failed");
                    assert false;
                }
            }
            
            String[] idpMetadata = {"",""};
            HtmlPage idpEntityPage = idpfm.listEntities(idpWebClient,
                    configMap.get(TestConstants.KEY_ATT_EXECUTION_REALM),
                    "saml2");
            if (FederationManager.getExitCode(idpEntityPage) != 0) {
                log(Level.SEVERE, "configureFedlet", "listEntities famadm" +
                        " command failed");
                assert false;
            }
            if (!idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(
                    TestConstants.KEY_FEDLETIDP_ENTITY_NAME))) {
                log(Level.FINEST, "configureFedlet", "idp entity doesnt" +
                        " exist. Get template & create the entity");
                if (strGroupName.contains("sec")) {
                    idpMetadata = FedletCommon.importMetadata(
                            idpWebClient,  configMap, true, "IDP");
                } else {
                    idpMetadata = FedletCommon.importMetadata(
                            idpWebClient, configMap, false, "IDP");
                }
                
                log(Level.FINEST, "configureFedlet", "idp metadata" +
                        idpMetadata[0]);
                log(Level.FINEST, "configureFedlet", "idp Ext metadata" +
                        idpMetadata[1]);
                if ((idpMetadata[0].equals(null)) || (
                        idpMetadata[1].equals(null))) {
                    log(Level.SEVERE, "configureFedlet", "Couldn't configure " +
                            "IDP");
                    assert false;
                }
            } 
            // Export to get the metadata.
            HtmlPage idpExportEntityPage;
            if (strGroupName.contains("sec")) {
                idpExportEntityPage = idpfm.exportEntity(idpWebClient,
                        configMap.get(TestConstants.KEY_FEDLETIDP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_ATT_EXECUTION_REALM),
                        true, true, true, "saml2");
            } else {
                idpExportEntityPage = idpfm.exportEntity(idpWebClient,
                        configMap.get(TestConstants.KEY_FEDLETIDP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_ATT_EXECUTION_REALM),
                        false, true, true, "saml2");
            }               
                
            if (FederationManager.getExitCode(idpExportEntityPage) != 0) {
                log(Level.SEVERE, "configureFedlet", "exportEntity famadm" +
                        " command failed");
                assert false;
            }
            idpMetadata[0] = FedletCommon.getMetadataFromPage(
                    idpExportEntityPage);
            idpMetadata[1] = FedletCommon.getExtMetadataFromPage(
                    idpExportEntityPage);
            
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    configMap.get(TestConstants.KEY_FEDLET_COT), "");
            log(Level.FINEST, "configureFedlet", "idp metadata" +
                    idpMetadata[0]);
            log(Level.FINEST, "configureFedlet", "idp Ext metadata" +
                    idpMetadata[1]);      
            
            // Fedlet 
            fedleturl = fedletidpurl + "/task/CreateFedlet";
            URL fedUrl = new URL(fedleturl);
            String strWarType = configMap.get(
                    TestConstants.KEY_FEDLET_WAR_TYPE);
            String warFile = configMap.get(
                    TestConstants.KEY_FEDLET_WAR_LOCATION);      
            String client_domain = rb_clientGlobal.getString(
                    "client_domain_name");
            int deployPort;
            String deployURI = "";
            
            if (strWarType.equals("internal")) { 
                deployPort = getUnusedPort();                
                log(Level.FINEST, "configureFedlet", "Deploy Port: " +
                        deployPort);
                deployURI = "/fedlet";                      
                clientURL = protocol + "://" + hostname + client_domain +
                        ":" + deployPort + deployURI;              
                log(Level.FINE, "configureFedlet", "Client URL: " + clientURL);             
    
                // creating fedlet                
                File file = new File(configMap.get(
                        TestConstants.KEY_ATT_CONFIG_DIR) + "/myfedlets" + "/" +
                        configMap.get(TestConstants.KEY_FEDLET_NAME));
                boolean entityExist = file.exists();                   
                if (!entityExist) {               
                        CreateFedlet(fedUrl);                   
                } else {
                    file = new File(configMap.get(
                            TestConstants.KEY_ATT_CONFIG_DIR) + "/myfedlets" +
                            "/" + configMap.get(TestConstants.KEY_FEDLET_NAME) +
                            "/" + "Fedlet.zip");
                    boolean zipExist = file.exists();                                        
                    if (!zipExist) {                        
                        deleteDirectory(configMap.get(
                            TestConstants.KEY_ATT_CONFIG_DIR) + "/myfedlets" +
                            "/" + configMap.get(TestConstants.KEY_FEDLET_NAME));
                        deleteDirectory(configMap.get(
                            TestConstants.KEY_ATT_CONFIG_DIR) + "/myfedlets");
                        CreateFedlet(fedUrl);                
                    }                    
                }
                
                Thread.sleep(45000);
                int count = 0;
                zipLocation = new File(configMap.get(
                            TestConstants.KEY_ATT_CONFIG_DIR) + "/myfedlets" +
                            "/" + configMap.get(TestConstants.KEY_FEDLET_NAME) +
                            "/" + "Fedlet.zip");
                UnzipLocation = new File(configMap.get(
                            TestConstants.KEY_ATT_CONFIG_DIR) + "/myfedlets" +
                            "/" + configMap.get(TestConstants.KEY_FEDLET_NAME));
                while (count < 6) {
                    if (zipLocation.exists()) {
                        log(Level.FINEST, "configureFedlet", "Fedlet.zip " +
                                "Found");
                        log(Level.FINEST, "configureFedlet", "Count: " + count);
                        break;
                    } else {
                        log(Level.FINEST, "configureFedlet", "Waiting for " +
                                "the Fedlet.zip");
                        Thread.sleep(10000);
                    }
                    count++;
                }
                if (zipLocation.exists()) {
                    jarUti.expandWar(zipLocation, UnzipLocation);
                } else {
                    assert false;
                }
                Thread.sleep(15000);
                warFile = configMap.get(
                        TestConstants.KEY_ATT_CONFIG_DIR) + "/myfedlets" +
                        "/" + configMap.get(TestConstants.KEY_FEDLET_NAME) +
                        "/" + "fedlet.war";
                             
                // deploying fedlet.war on jetty and starting hte jetty server               
                server = new Server();
                Connector connector = new SelectChannelConnector();                                
                log(Level.FINEST, "configureFedlet", "Jetty Deploy port: " + 
                        deployPort);
                connector.setPort(new Integer(deployPort).intValue());   
                log(Level.FINEST, "configureFedlet", "Jetty Deploy host: " +
                        hostname + client_domain);
                connector.setHost(hostname + client_domain);
                server.addConnector(connector);
                WebAppContext wac = new WebAppContext();                         
                log(Level.FINEST, "configureFedlet", "Jetty Deploy URI: " +
                        deployURI);
                wac.setContextPath(deployURI);            
                log(Level.FINEST, "configureFedlet", "Client URL: " +
                        clientURL);
                if (new File(warFile).exists()) {
                    log(Level.FINEST, "configureFedlet", "WAR File: " +
                            warFile);
                    wac.setParentLoaderPriority(true);
                    wac.setWar(warFile);
                    server.setHandler(wac);
                    server.setStopAtShutdown(true);
                    log(Level.FINEST, "configureFedlet",
                            "Deploying war and starting jetty server");
                    server.start();                
                    log(Level.FINEST, "configureFedlet", "Deployed war and " +
                            "started jetty server");                            
                } else {
                    log(Level.SEVERE, "startServer", "The client war file" + 
                            warFile + " does not exist. " +
                            " Please verify the value of the war_file");
                    assert false;
                }    
                
            } else {            
                clientURL = warFile;
                CreateFedlet(fedUrl);                
                Thread.sleep(6000);               
                
                log(Level.FINEST, "configureFedlet", "Configuring an " +
                        "external war"); 
                try {
                    page = (HtmlPage)idpWebClient.getPage(clientURL);
                } catch(com.gargoylesoftware.htmlunit.
                        FailingHttpStatusCodeException e) {
                    log(Level.SEVERE, "configureFedlet", clientURL + 
                            " cannot be reached.");
                    e.printStackTrace();
                    assert false;
                }                       
            }         
            
            // configuring fedlet war
            page = (HtmlPage)idpWebClient.getPage(clientURL);
            log(Level.FINEST, "Fedlet Page:",
                    page.getWebResponse().getContentAsString());
            
            // checking if the fedlet is already configured
            if (!page.getWebResponse().getContentAsString().contains(
                    "Fedlet (SP) Configuration Directory")) {
                page = (HtmlPage)idpWebClient.getPage(clientURL +
                    "/index.jsp?CreateConfig=true");
                log(Level.FINEST, "Fedlet Config Page:", 
                    page.getWebResponse().getContentAsString());
                if (!page.getWebResponse().getContentAsString().contains(
                        "Fedlet configuration created")) {
                    log(Level.SEVERE, "Failed to configure Fedlet. The page " +
                            "is: ", page.getWebResponse().getContentAsString());
                    assert false;
                }                     
            }            
            
            page = (HtmlPage)idpWebClient.getPage(clientURL + "/index.jsp");
            log(Level.FINEST, "Fedlet Index Page:", 
                    page.getWebResponse().getContentAsString());       
            
        } catch (Exception e) {
            log(Level.SEVERE, "configureFedlet", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(idpWebClient, fedletidpurl);
        }
        exiting("configureFedlet");
    }
    
    /* 
     * Create Fedlet
     */ 
    private void CreateFedlet(URL fedurl) 
            throws FailingHttpStatusCodeException, Exception {
        try {
            HtmlTextInput txtattrMapAssertion;
            HtmlTextInput txtattrMapName;
            HtmlSubmitInput addButton;
            HtmlPage btnaddpage;            
            Map amap = new HashMap();           
            
            amap = parseStringToRegularMap(
                    configMap.get(TestConstants.KEY_FEDLET_ATT_MAP), ",");           
            log(Level.FINEST, "CreateFedlet", "Attributes Map:" + amap); 
            
            page = (HtmlPage) idpWebClient.getPage(fedurl);            
            HtmlForm form = (HtmlForm) page.getForms().get(0);

            log(Level.FINEST, "CreateFedlet", "Page:" + 
                    page.getWebResponse().getContentAsString());
            HtmlTextInput txtname = (HtmlTextInput) form.getInputByName(
                    "CreateFedlet.tfEntityId");
            txtname.setValueAttribute(configMap.get(
                    TestConstants.KEY_FEDLET_NAME));
            HtmlTextInput txtdestURL = (HtmlTextInput) form.getInputByName(
                    "CreateFedlet.tfAssertConsumer");            
            txtdestURL.setValueAttribute(clientURL);
            
            // attributes Map    
            Set aset = amap.keySet();
            Iterator iter = aset.iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();                   
                String value = (String) amap.get(key);                 
            
                txtattrMapAssertion = (HtmlTextInput)form.getInputByName(
                        "CreateFedlet.tfAttrMappingAssertion");
                txtattrMapAssertion.setValueAttribute(key);
                txtattrMapName = (HtmlTextInput)form.getInputByName(
                        "CreateFedlet.tfAttrMappingName");
                txtattrMapName.setValueAttribute(value);
                addButton = (HtmlSubmitInput) form.getInputByName(
                        "CreateFedlet.btnAddAttrMapping");
                btnaddpage = (HtmlPage) addButton.click();            
            } 
            
            HtmlSubmitInput createButton = 
                    (HtmlSubmitInput) form.getInputByName("Create" +
                    "Fedlet.button1");
            HtmlPage fedpage = (HtmlPage) createButton.click();  
            log(Level.FINEST, "CreateFedlet", "Fedlet Page:" + 
                    fedpage.getWebResponse().getContentAsString());
        } catch (IOException ex) {
            Logger.getLogger(
                    ConfigureFedlet.class.getName()).log(
                    Level.SEVERE, null, ex);
            
        } catch (Exception e) {
            log(Level.SEVERE, "CreateFedlet", e.getMessage());
            e.printStackTrace();            
            throw e;
        }       
    }   
    
    /*
     * parse string to Regular Map
     */
    private Map parseStringToRegularMap(String str, String mTok)
    throws Exception {
        entering("parseStringToRegularMap", null);
        Map map = new HashMap();
        StringTokenizer st = new StringTokenizer(str, mTok);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int idx = token.indexOf("=");
            if (idx != -1) {      
                String attrName = token.substring(0, idx);
                String attrValue = token.substring(idx+1, token.length());
                map.put(attrName, attrValue);                               
            }
        }        
        exiting("parseStringToRegularMap");
        return map;
    }
}

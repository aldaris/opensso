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
 * "Portions Copyrighted [year] [ of copyright owner]"
 *
 * $Id: StockQuoteSampleTest.java,v 1.3 2009-06-29 17:07:41 nithyas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.wss;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.idm.IdType;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.SMSCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.webtest.DefaultTaskHandler;
import com.sun.identity.wss.provider.DiscoveryConfig;
import com.sun.identity.wss.provider.ProviderConfig;
import com.sun.identity.wss.provider.TrustAuthorityConfig;
import com.sun.identity.wss.security.PasswordCredential;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class tests securing the resquest for different security tokens. This
 * includes both WS-* and Liberty tokens. Further this also test configuring
 * providers using ProviderConfig class. This class verifies the security by
 * accessing the StockQuoteClient
 */
public class StockQuoteSampleTest extends TestCommon {
    
    private int testIndex;
    private String strTestDescription;
    private String strClientURL;
    private String strExpResult;
    private String strWSCId;
    private String strWSPId;
    private String baseDir;
    private ResourceBundle rbp;
    private ResourceBundle rbg;
    private SSOToken token;
    private IDMCommon idmc;
    private SMSCommon smsc;
    private boolean isLibertyToken;
    private TrustAuthorityConfig taconfig;
    private TrustAuthorityConfig stsconfig;
    private DefaultTaskHandler task;
    private String strLocRB = "StockQuoteSampleTest";
    private String strGlbRB = "StockQuoteSampleGlobal";
    private String strUser = "sampletestuser";
    private String strSTSSecurity;
    private String strSTSConfigName;
    private String strUserAuth;
    private String strEvaluateClient;
    private WebClient webClient;
    
    /**
     * Default constructor. Creates admintoken and helper class instances.
     */
    public StockQuoteSampleTest()
    throws Exception{
        super("StockQuoteSampleTest");
        rbp = ResourceBundle.getBundle("wss" + fileseparator + strLocRB);
        rbg = ResourceBundle.getBundle("wss" + fileseparator + strGlbRB);
        token = getToken(adminUser, adminPassword, basedn);
        idmc = new IDMCommon();
        smsc = new SMSCommon(token);
        
    }
    
    /**
     * Updates bootstrap security mechanism in Discovery service to null:X509
     * and creates users.
     */
    @BeforeClass(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void createUser()
    throws Exception {
        try {
            Set set = new HashSet();
            set.add(getBootsrapDiscoEntry("2005-02:null:X509"));
            smsc.updateSvcSchemaAttribute(
                    "sunIdentityServerDiscoveryService",
                    "sunIdentityServerBootstrappingDiscoEntry", set, "Global");
            
            Map map = new HashMap();
            set = new HashSet();
            set.add(strUser);
            map.put("sn", set);
            set = new HashSet();
            set.add(strUser);
            map.put("cn", set);
            set = new HashSet();
            set.add(strUser);
            map.put("userpassword", set);
            set = new HashSet();
            set.add("Active");
            map.put("inetuserstatus", set);
            idmc.createIdentity(token, realm, IdType.USER, strUser, map);
        } catch(Exception e) {
            log(Level.SEVERE, "createUser", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Changes the runtime application user from UrlAccessAgent to amadmin
     */
    @BeforeSuite(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void changeRunTimeUser() throws
            Exception{
        try {
            SSOToken runtimeToken = null;
            runtimeToken = (SSOToken) AccessController.doPrivileged(
                    AdminTokenAction.getInstance());
            log(Level.FINEST, "changeRunTimeUser", "Runtime user name is" +
                    runtimeToken.getPrincipal().getName());
            SSOTokenManager.getInstance().destroyToken(runtimeToken);
            try{
                
                SSOTokenManager.getInstance().refreshSession(runtimeToken);
            } catch (SSOException s) {
                log(Level.FINEST, "RefreshSession Exception", s.getMessage());
            }
            SystemProperties.initializeProperties("com.sun.identity." +
                    "agents.app.username", adminUser);
            SystemProperties.initializeProperties("com.iplanet.am." +
                    "service.password", adminPassword);
            
            SSOToken newToken = null;
            newToken = (SSOToken) AccessController.doPrivileged(
                    AdminTokenAction.getInstance());
            log(Level.FINEST, "changeRunTimeUser", "Runtime user name after " +
                    "change \n" + runtimeToken.getPrincipal().getName());
        } catch(Exception e) {
            log(Level.SEVERE, "changeRunTimeUser", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Creates agent profiles for web service providers and web service clients.
     */
    @Parameters({"testIdx", "STSSecurity", "UserAuth", "EvaluateClient"})
    @BeforeClass(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void setup(String testIdx, String STSSecurity, String UserAuth,
            String EvaluateClient)
            throws Exception {
        Object[] params = {testIdx, STSSecurity, UserAuth, EvaluateClient};
        entering("setup", params);
        try {
            
            testIndex = new Integer(testIdx).intValue();
            strSTSSecurity = STSSecurity;
            strUserAuth = UserAuth;
            strEvaluateClient = EvaluateClient;
            
            strTestDescription = rbp.getString(strLocRB + testIndex
                    + ".description");
            log(Level.FINEST, "setup", "Description: " + strTestDescription);
            
            strClientURL = rbg.getString(strGlbRB + ".clienturl");
            log(Level.FINEST, "setup", "Client URL: " + strClientURL);
            
            strSTSConfigName = rbg.getString(strGlbRB + ".stsconfigname");
            log(Level.FINEST, "setup", "STS config Name: " + strSTSConfigName);
            
            strExpResult = rbp.getString(strLocRB + testIndex + ".passmessage");
            log(Level.FINEST, "setup", "Expected Result: " + strExpResult);
            
            isLibertyToken = new Boolean(rbp.getString(strLocRB + testIndex +
                    ".isLibertyToken")).booleanValue();
            log(Level.FINEST, "setup", "Is Liberty Token: " + isLibertyToken);
            
            strWSCId = createAgentProfile(testIndex, "WSC");
            log(Level.FINEST, "setup", "WSC Agent Id: " + strWSCId);
            
            strWSPId = createAgentProfile(testIndex, "WSP");
            log(Level.FINEST, "setup", "WSP Agent Id: " + strWSPId);
            
            if (isLibertyToken)
                registerWSPWithDisco(strWSPId);
        } catch(Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        exiting("setup");
    }
    
    /**
     * Accesses the StockQuoteClient and submits the request and verifies the
     * expected result
     */
    @Test(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void evaluateStockQuoteClient()
    throws Exception {
        entering("evaluateStockQuoteClient", null);
        ProviderConfig stockPc = ProviderConfig.getProvider(strWSCId,
                ProviderConfig.WSC);
        if (strEvaluateClient.equals("true")) {
            try {
                if (strSTSSecurity.equals("true")) {
                    List listSecMech = new ArrayList();
                    String secMechanism = "urn:sun:wss:sts:security";
                    listSecMech.add(secMechanism);
                    stockPc.setSecurityMechanisms(listSecMech);
                    
                    stsconfig = TrustAuthorityConfig.getConfig(strSTSConfigName,
                            TrustAuthorityConfig.STS_TRUST_AUTHORITY);
                    TrustAuthorityConfig.saveConfig(stsconfig);
                    stockPc.setTrustAuthorityConfig(stsconfig);
                    ProviderConfig.saveProvider(stockPc);
                    stockPc = ProviderConfig.getProvider(strWSCId,
                            ProviderConfig.WSC);                  
                }
                webClient = new WebClient();
                URL cmdUrl = new URL(strClientURL);
                HtmlPage page = (HtmlPage) webClient.getPage(cmdUrl);
                HtmlForm form = (HtmlForm) page.getFormByName("GetQuote");
                
                HtmlTextInput txtagentname = (HtmlTextInput) form.
                        getInputByName("symbol");
                txtagentname.setValueAttribute("JAVA");
                
                HtmlPage returnPage = (HtmlPage) form.submit();
                log(Level.FINEST, "evaluateStockQuoteClient",
                        " Page after request submission \n" +
                        returnPage.getWebResponse().getContentAsString());
                
                int iIdx = getHtmlPageStringIndex(returnPage, strExpResult);
                assert (iIdx != -1);
                
            } catch (Exception e) {
                log(Level.SEVERE, "evaluateStockQuoteClient", e.getMessage());
                e.printStackTrace();
                throw e;
            } finally {
                Reporter.log("Test Description: "  + strTestDescription);
            }
	    Thread.sleep(1000);            
            exiting("evaluateStockQuoteClient");
        }
    }
    
    /**
     * Configures the WSC profile to get the from STS service and
     * accesses the StockQuoteClient and submits the request and verifies the
     * expected result
     */
    @Test(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"},
    dependsOnMethods = {"evaluateStockQuoteClient"})
    public void evaluateStockQuoteClientWithEndUser()
    throws Exception {
        entering("evaluateStockQuoteClientWithEndUser", null);
        ProviderConfig stockPc = ProviderConfig.getProvider(strWSCId,
                ProviderConfig.WSC);
        if (strUserAuth.equals("true")) {
            try {
                if (strSTSSecurity.equals("true")) {                    
                    List listSecMech = new ArrayList();
                    String secMechanism = "urn:sun:wss:sts:security";
                    listSecMech.add(secMechanism);
                    stockPc.setSecurityMechanisms(listSecMech);
                    stsconfig = TrustAuthorityConfig.getConfig(strSTSConfigName,
                            TrustAuthorityConfig.STS_TRUST_AUTHORITY);
                    TrustAuthorityConfig.saveConfig(stsconfig);
                    stockPc.setTrustAuthorityConfig(stsconfig);
                    ProviderConfig.saveProvider(stockPc);
                    stockPc = ProviderConfig.getProvider(strWSCId,
                            ProviderConfig.WSC);                    
                }
                
                ProviderConfig stockPC = null ;
                ProviderConfig wspPC = null ;
                String xmlFile = "generateUserAuthenticateXML" + testIndex +
                        ".xml";
                String xmlFileLocation = getTestBase() +
                        System.getProperty("file.separator") + "wss" +
                        System.getProperty("file.separator") + xmlFile;
                
                stockPC = ProviderConfig.getProvider("StockService",
                        ProviderConfig.WSC);
                stockPC.setForceUserAuthentication(true);
                ProviderConfig.saveProvider(stockPC);
                generateUserAuthenticateXML(adminUser, adminPassword,
                        xmlFileLocation, strExpResult);
                task = new DefaultTaskHandler(xmlFileLocation);
                webClient = new WebClient();
                HtmlPage page = task.execute(webClient);
                log(Level.FINEST, "evaluateStockQuoteClientWithEndUser",
                        "evaluateStockQuoteClientWithEndUser page after " +
                        "login\n" + page.getWebResponse().getContentAsString());
                
                if (getHtmlPageStringIndex(page, strExpResult) == -1)
                    assert false;
            } catch (Exception e) {
                log(Level.SEVERE, "evaluateStockQuoteClientWithEndUser",
                        e.getMessage());
                e.printStackTrace();
                throw e;
            } finally {
                Reporter.log("Test Description: "  + strTestDescription );
            }
            exiting("evaluateStockQuoteClient WithEndUser");
        }
    }
    
    /**
     * Deletes the agent profiles for webservice clients and providers.
     */
    @AfterClass(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        
        log(Level.FINEST, "cleanup", "WSC Agent Id: " + strWSCId + "WSC");
        log(Level.FINEST, "cleanup", "WSP Agent Id: " + strWSPId + "WSP");
        
        if (isLibertyToken) {
            unregisterWSPWithDisco("urn:wsp");
            ProviderConfig.deleteProvider("localDisco", "Discovery");
        }
        ProviderConfig.deleteProvider(strWSCId, ProviderConfig.WSC);
        ProviderConfig.deleteProvider(strWSPId, ProviderConfig.WSP);
        
        exiting("cleanup");
    }
    
    /**
     * Deletes users and resets bootstrap security mechanism in Discovery
     * service to null:null.
     */
    @AfterClass(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void deleteUser()
    throws Exception {
        idmc.deleteIdentity(token, realm, IdType.USER, strUser);
        Set set = new HashSet();
        set.add(getBootsrapDiscoEntry("2003-08:null:null"));
        smsc.updateSvcSchemaAttribute("sunIdentityServerDiscoveryService",
                "sunIdentityServerBootstrappingDiscoEntry", set, "Global");
        destroyToken(token);
    }
    
    /**
     * Registers webservice agent with Discovery service.
     */
    private void registerWSPWithDisco(String name)
    throws Exception {
        entering("registerWSPWithDisco", null);
        DiscoveryConfig discoConfig = (DiscoveryConfig)taconfig;
        ProviderConfig pc = ProviderConfig.getProvider(name, "WSP");
        discoConfig.registerProviderWithTA(pc, pc.getServiceType());
        exiting("registerWSPWithDisco");
    }
    
    /**
     * unregisters webservice agent with Discovery service.
     */
    private void unregisterWSPWithDisco(String name)
    throws Exception {
        entering("unregisterWSPWithDisco", null);
        DiscoveryConfig discoConfig = (DiscoveryConfig)taconfig;
        discoConfig.unregisterProviderWithTA(name);
        exiting("unregisterWSPWithDisco");
    }
    
    /**
     * Creates agent profile for webservice clients and providers.
     */
    private String createAgentProfile(int testIndex, String agentType)
    throws Exception {
        try {
            ProviderConfig pc = null ;
            String strIdx =  strLocRB + testIndex + "." +
                    agentType.toLowerCase() + ".";
            log(Level.FINEST, "createAgentProfile",
                    "Property file string index: " + strIdx);
            
            String name = rbp.getString(strIdx + "name");
            String secMechanism = rbp.getString(strIdx + "secMechanism");
            boolean hasUserCredential = new Boolean(rbp.getString(strIdx +
                    "hasUserCredential")).booleanValue();
            boolean isRequestSigned = new Boolean(rbp.getString(strIdx +
                    "isRequestSigned")).booleanValue();
            boolean isRequestEncrypted = new Boolean(rbp.getString(strIdx +
                    "isRequestEncrypted")).booleanValue();
            boolean isResponseSigVerified = new Boolean(rbp.getString(strIdx +
                    "isResponseSigVerified")).booleanValue();
            boolean isResponseDecrypted = new Boolean(rbp.getString(strIdx +
                    "isResponseDecrypted")).booleanValue();
            boolean keystoreUsage = new Boolean(rbp.getString(strIdx +
                    "keystoreUsage")).booleanValue();
            boolean keepPrivateSecHeaderInMsg = new Boolean(rbp.getString(strIdx +
                    "keepPrivateSecHeaderInMsg")).booleanValue();
            String svcType = rbp.getString(strIdx + "svcType");
            
            log(Level.FINEST, "createAgentProfile", "name: " + name);
            log(Level.FINEST, "createAgentProfile", "secMechanism: " +
                    secMechanism);
            log(Level.FINEST, "createAgentProfile", "hasUserCredential: " +
                    hasUserCredential);
            log(Level.FINEST, "createAgentProfile", "isRequestSigned: " +
                    isRequestSigned);
            log(Level.FINEST, "createAgentProfile", "isRequestEncrypted: " +
                    isRequestEncrypted);
            log(Level.FINEST, "createAgentProfile", "isResponseSigVerified: " +
                    isResponseSigVerified);
            log(Level.FINEST, "createAgentProfile", "isResponseDecrypted: " +
                    isResponseDecrypted);
            log(Level.FINEST, "createAgentProfile", "keystoreUsage: " +
                    keystoreUsage);
            log(Level.FINEST, "createAgentProfile", "keepPrivateSecHeaderInMsg: " +
                    keepPrivateSecHeaderInMsg);
            log(Level.FINEST, "createAgentProfile", "svcType: " +
                    svcType);
            log(Level.FINEST, "createAgentProfile", "isLibertyToken: " +
                    isLibertyToken);
            if (agentType.equals("WSP")) {
                pc = ProviderConfig.getProvider(name, ProviderConfig.WSP);
            } else if  (agentType.equals("WSC")) {
                pc = ProviderConfig.getProvider(name, ProviderConfig.WSC);
            }
            
            List listSec = new ArrayList();
            listSec.add(secMechanism);
            pc.setSecurityMechanisms(listSec);
            if (hasUserCredential) {
                List listUsers = new ArrayList();
                int noOfCred = new Integer(rbp.getString(strIdx +
                        "noUserCredential")).intValue();
                String strUsername;
                String strPassword;
                PasswordCredential cred;
                for (int i = 0; i < noOfCred; i++) {
                    strUsername = rbp.getString(strIdx + "UserCredential" + i +
                            ".username");
                    strPassword = rbp.getString(strIdx + "UserCredential" + i +
                            ".password");
                    cred = new PasswordCredential(strUsername, strPassword);
                    listUsers.add(cred);
                }
                log(Level.FINEST, "createAgentProfile", "UserCredential: " +
                        listUsers);
                pc.setUsers(listUsers);
            }
            pc.setRequestSignEnabled(isRequestSigned);
            pc.setRequestEncryptEnabled(isRequestEncrypted);
            pc.setResponseSignEnabled(isResponseSigVerified);
            pc.setResponseEncryptEnabled(isResponseDecrypted);
            pc.setDefaultKeyStore(keystoreUsage);
            pc.setPreserveSecurityHeader(keepPrivateSecHeaderInMsg);
            pc.setServiceType(svcType);
            pc.setKeyAlias(keyAlias);
            pc.setPublicKeyAlias(keyAlias);
            pc.setWSPEndpoint("default");
            
            if (agentType.equals("WSC")) {
                if (isLibertyToken) {
                    //Trust AuthoritiyConfig
                    taconfig = TrustAuthorityConfig.getConfig("localDisco",
                            TrustAuthorityConfig.DISCOVERY_TRUST_AUTHORITY);
                    taconfig.setEndpoint(protocol + ":" + "//" + host + ":" +
                            port + uri + "/Liberty/disco");
                    TrustAuthorityConfig.saveConfig(taconfig);
                    pc.setTrustAuthorityConfig(taconfig);
                }
            }
            ProviderConfig.saveProvider(pc);
            if (agentType.equals("WSP")) {
                log(Level.FINEST, "createAgentProfile",
                        "WSP provider is exists()\n" +
                        ProviderConfig.isProviderExists(name,
                        ProviderConfig.WSP));
                ProviderConfig WSPpc = null ;
                WSPpc = ProviderConfig.getProvider(name, ProviderConfig.WSP);
            }
            if (agentType.equals("WSC")) {
                log(Level.FINEST, "createAgentProfile",
                        "WSC provider is exists()\n" +
                        ProviderConfig.isProviderExists(name,
                        ProviderConfig.WSC));
                ProviderConfig WSCpc = null ;
                WSCpc = ProviderConfig.getProvider(name, ProviderConfig.WSC);
            }
            return (name);
        } catch(Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Creates the XML for updating the Discovery service security bootstrap
     * entry.
     */
    private String getBootsrapDiscoEntry(String strSec) {
        StringBuffer sb = new StringBuffer(1024);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"");
        sb.append("standalone=\"yes\"?>");
        sb.append("<DiscoEntry xmlns=" +
                "\"urn:com:sun:identityserver:liberty:ws:disco:discoentry\">");
        sb.append("<ResourceOffering xmlns=\"urn:liberty:disco:2003-08\">");
        sb.append("<ResourceID>");
        sb.append("</ResourceID>");
        sb.append("<ServiceInstance>");
        sb.append("<ServiceType>urn:liberty:disco:2003-08");
        sb.append("</ServiceType>");
        sb.append("<ProviderID>" +  protocol + ":" + "//" + host + ":" +
                port + uri + "/Liberty/disco");
        sb.append("</ProviderID>");
        sb.append("<Description>");
        sb.append("<SecurityMechID>urn:liberty:security:" + strSec);
        sb.append("</SecurityMechID>");
        sb.append("<Endpoint>" +  protocol + ":" + "//" + host + ":" + port +
                uri + "/Liberty/disco");
        sb.append("</Endpoint>");
        sb.append("</Description>");
        sb.append("</ServiceInstance>");
        sb.append("</ResourceOffering>");
        sb.append("</DiscoEntry>");
        log(Level.FINEST, "getBootsrapDiscoEntry", "Discovery Bootstrap" +
                " Resource Offering:" + sb.toString());
        return (sb.toString());
    }
    
    /**
     * Generates XML for end user authentication testcases.
     */
    private void generateUserAuthenticateXML( String username,
            String password, String xmlFile, String result)
            throws Exception {
        
        FileWriter fstream = new FileWriter(xmlFile);
        BufferedWriter out = new BufferedWriter(fstream);
        
        log(Level.FINEST, "generateUserAuthenticateXML", "Result: " + result);
        log(Level.FINEST, "generateUserAuthenticateXML", "Username: "
                + username);
        log(Level.FINEST, "generateUserAuthenticateXML", "Password: "
                + password);
        log(Level.FINEST, "generateUserAuthenticateXML", "XML File: "
                + xmlFile);
        
        out.write("<url href=\"" + strClientURL);
        out.write("\">");
        out.write(newline);
        out.write("<form name=\"Login\" IDButton=\"\" >");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + username + "\" />");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\"" + password + "\" />");
        out.write(newline);
        out.write("</form>");
        out.write("<form name=\"GetQuote\" IDButton=\"\" >");
        out.write(newline);
        out.write("<input name=\"symbol\" value=\"" + "JAVA" + "\" />");
        out.write(newline);
        out.write("<result text=\"" + result + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }    
}

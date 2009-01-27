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
 * $Id: SAMLv2AttributeQueryTests.java,v 1.5 2009-01-27 00:14:08 nithyas Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.samlv2;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.MultiProtocolCommon;
import com.sun.identity.qatest.common.SAMLv2Common;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.webtest.DefaultTaskHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.Map;
import java.util.ResourceBundle;
import java.net.URL;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class to test the new SAMLv2 Attribute Query Profile
 * AttributeQuery can be done with, These attributes will be returned
 * from the IDP, IDP attributeMap should be modifed in order to get
 * the user attributes, then SP can query for these attributes.
 * (1) no Attributes - All the attibutes will be returned
 * (2) Named attribute - particular named attribute will be returned
 * (3) Valued attribute - Attribute with a particular value will be returned.
 */
public class SAMLv2AttributeQueryTests extends TestCommon {
    
    private WebClient spWebClient;
    private WebClient idpWebClient;
    private Map<String, String> configMap;
    private String baseDir ;
    private String xmlfile;
    private DefaultTaskHandler task1;
    private HtmlPage wpage;
    private FederationManager fmSP;
    private FederationManager fmIDP;
    public  WebClient webClient;
    private String spurl;
    private String idpurl;
    private String fedSSOURL;
    private String spmetadata;
    private String idpmetadata;
    private String testName;
    private String testType;
    private String testAttribute;
    private String metaAliasname;
    private String ATTRIB_MAP_DEFAULT = "<Attribute name=\""
            + "attributeMap\"/>\n";
    private String ATTRIB_MAP_VALUE = "<Attribute name=\""
            + "attributeMap\">\n"
            + "            <Value>mail=mail</Value>\n"
            + "            <Value>givenname=givenname</Value>\n"
            + "            <Value>postaladdress=postaladdress</Value>\n"
            + "            <Value>employeenumber=employeenumber</Value>\n"
            + "            <Value>sunIdentityMSISDNNumber=sunIdentityMSISDNNumber" 
            +"</Value>\n"
            + "            <Value>telephonenumber=telephonenumber</Value>\n"
            + "            <Value>cn=cn</Value>\n"
            + "            <Value>sn=sn</Value>\n"
            + "            <Value>facsimileTelephoneNumber=facsimileTelephoneNumber" 
            +"</Value>\n"
            + "            <Value>homePhone=homePhone</Value>\n"
            + "            <Value>homePostalAddress=homePostalAddress</Value>\n"
            + "            <Value>mobile=mobile</Value>\n"
            + "            <Value>pager=pager</Value>\n"
            + "            <Value>postofficebox=postofficebox</Value>\n"
            + "            <Value>secretary=secretary</Value>\n"
            + "            <Value>street=street</Value>\n"
            + "        </Attribute>";
    private String ATTRIB_X509_DEFAULT = "<Attribute name=\""
            + "x509SubjectDataStoreAttrName\">\n"
            + "            <Value/>\n"
            + "        </Attribute>";
    private String ATTRIB_X509_VALUE = "<Attribute name=\""
            + "x509SubjectDataStoreAttrName\">\n"
            + "            <Value>mail=mail</Value>\n"
            + "            <Value>givenname=givenname</Value>\n"
            + "            <Value>postaladdress=postaladdress</Value>\n"
            + "            <Value>employeenumber=employeenumber</Value>\n"
            + "            <Value>sunIdentityMSISDNNumber=sunIdentityMSISDNNumber" 
            +"</Value>\n"
            + "            <Value>telephonenumber=telephonenumber</Value>\n"
            + "            <Value>cn=cn</Value>\n"
            + "            <Value>sn=sn</Value>\n"
            + "            <Value>facsimileTelephoneNumber=facsimileTelephoneNumber" 
            +"</Value>\n"
            + "            <Value>homePhone=homePhone</Value>\n"
            + "            <Value>homePostalAddress=homePostalAddress</Value>\n"
            + "            <Value>mobile=mobile</Value>\n"
            + "            <Value>pager=pager</Value>\n"
            + "            <Value>postofficebox=postofficebox</Value>\n"
            + "            <Value>secretary=secretary</Value>\n"
            + "            <Value>street=street</Value>\n"
            + "        </Attribute>";
    
    /**
     * Constructor SAMLV2AttributeQueryTests
     */
    public  SAMLv2AttributeQueryTests() {
        super("SAMLv2AttributeQueryTests");
    }
    
    /**
     * Configures the SP and IDP load meta for the SAMLV2AttributeQueryTests
     * tests to execute
     */
    @Parameters({"ptestName", "ptestType", "pAttribute"})
    @BeforeClass(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void setup(String ptestName, String ptestType, String pAttribute)
    throws Exception {
        HtmlPage page;
        ArrayList list;
        try {
            testName = ptestName;
            testType = ptestType;
            testAttribute = pAttribute;
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
            SAMLv2Common.getEntriesFromResourceBundle("samlv2" + fileseparator 
                    + "SAMLv2AttributeQueryTests", configMap);
            configMap.put(TestConstants.KEY_SP_USER, "sp" + testName);
            configMap.put(TestConstants.KEY_SP_USER_PASSWORD, "sp" + testName);
            configMap.put(TestConstants.KEY_IDP_USER, "idp" + testName);
            configMap.put(TestConstants.KEY_IDP_USER_PASSWORD, "idp" + 
                    testName);
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT) +
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
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
            if (FederationManager.getExitCode(fmSP.createIdentity(spWebClient,
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
            list.add("givenname=" + configMap.get(KEY_IDP_GIVEN_NAME));
            list.add("mail=" + 
                    configMap.get(KEY_IDP_USER_MAIL ));
            list.add("employeenumber=" + 
                    configMap.get(KEY_IDP_USER_EMPLOYEE));
            list.add("postaladdress=" + 
                    configMap.get(KEY_IDP_USER_POSTAL));
            list.add("telephonenumber=" + 
                    configMap.get(TestConstants.KEY_IDP_USER_TELE));
            list.add("homePhone=" + 
                    configMap.get(TestConstants.KEY_IDP_USER_HOMEPHONE));
            list.add("homePostalAddress=" + 
                    configMap.get(TestConstants.KEY_IDP_USER_HOMEPOSTAL));
            list.add("mobile=" + 
                    configMap.get(TestConstants.KEY_IDP_USER_MOBILE));
            list.add("facsimileTelephoneNumber=" + 
                    configMap.get(TestConstants.KEY_IDP_USER_TELEPHONE));
            list.add("pager=" + 
                    configMap.get(TestConstants.KEY_IDP_USER_PAGER));
            list.add("secretary=" + 
                    configMap.get(TestConstants.KEY_IDP_USER_SECRETARY));
            list.add("sunIdentityMSISDNNumber=" + 
                    configMap.get(TestConstants.KEY_IDP_USER_MSISDN));
            if (FederationManager.getExitCode(fmIDP.createIdentity(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM),
                    configMap.get(TestConstants.KEY_IDP_USER), "User", list))
                    != 0) {
                log(Level.SEVERE, "setup", "createIdentity famadm command " +
                        "failed");
                assert false;
            }
            //Federated SSO URL
            fedSSOURL = spurl + "/saml2/jsp/spSSOInit.jsp?metaAlias=" +
                    configMap.get(TestConstants.KEY_SP_METAALIAS) +
                    "&idpEntityID=" +
                    configMap.get(TestConstants.KEY_IDP_ENTITY_NAME);
        } catch(Exception e) {
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
     * Enable Attribute Query
     */
    @BeforeClass(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void attributeQueryMapSetup()
    throws Exception {
        entering("attributeQueryMapSetup", null);
        try {
            configMap = new HashMap<String, String>();
            getWebClient();
            configMap = getMapFromResourceBundle("samlv2" + fileseparator 
                    + "samlv2TestConfigData");
            configMap.putAll(getMapFromResourceBundle("samlv2" + fileseparator 
                    + "samlv2TestData"));
            log(Level.FINEST, "attributeQueryMapSetup", "Map:" + configMap);        
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT) +
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_IDP_PORT) +
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        try {
            //get sp & idp extended metadata
            FederationManager spfm = new FederationManager(spurl);
            FederationManager idpfm = new FederationManager(idpurl);
            consoleLogin(webClient, spurl + "/UI/Login", configMap.get(
                    TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            
            HtmlPage spmetaPage = spfm.exportEntity(webClient,
                    configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_SP_EXECUTION_REALM),
                    false, false, true, "saml2");
            if (FederationManager.getExitCode(spmetaPage) != 0) {
                log(Level.SEVERE, "setup", "exportEntity famadm " +
                        "command failed");
                assert false;
            }
            spmetadata = MultiProtocolCommon.getExtMetadataFromPage(spmetaPage);
            String spmetadataMod = spmetadata.replaceAll(ATTRIB_MAP_DEFAULT,
                    ATTRIB_MAP_VALUE);
            spmetadataMod = spmetadataMod.replaceAll(ATTRIB_X509_DEFAULT,
                    ATTRIB_X509_VALUE);
            log(Level.FINEST, "attributeQueryMapSetup", "Modified" +
                    " metadata:" + spmetadataMod);
            if (FederationManager.getExitCode(spfm.deleteEntity(webClient,
                    configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_SP_EXECUTION_REALM),
                    true, "saml2")) != 0) {
                log(Level.FINEST, "attributeQueryMapSetup", "Deletion of " +
                        "Extended entity failed");
                log(Level.FINEST, "attributeQueryMapSetup", "deleteEntity" +
                        " famadm command failed");
                assert(false);
            }
            if (FederationManager.getExitCode(spfm.importEntity(webClient,
                    configMap.get(TestConstants.KEY_SP_EXECUTION_REALM), "",
                    spmetadataMod, "", "saml2")) != 0) {
                log(Level.SEVERE, "attributeQueryMapSetup", "Failed to" +
                        " import extended metadata");
                log(Level.SEVERE, "attributeQueryMapSetup", "importEntity" +
                        " famadm command failed");
                assert(false);
            }
            consoleLogin(webClient, idpurl + "/UI/Login", configMap.get(
                    TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            
            HtmlPage idpmetaPage = idpfm.exportEntity(webClient,
                    configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM), 
                    false, false,
                    true, "saml2");
            if (FederationManager.getExitCode(idpmetaPage) != 0) {
                log(Level.SEVERE, "setup", "exportEntity famadm " +
                        "command failed");
                assert false;
            }
            idpmetadata =
                    MultiProtocolCommon.getExtMetadataFromPage(idpmetaPage);
            String idpmetadataMod = idpmetadata.replaceAll(ATTRIB_MAP_DEFAULT,
                    ATTRIB_MAP_VALUE);
            idpmetadataMod = idpmetadataMod.replaceAll(ATTRIB_X509_DEFAULT,
                    ATTRIB_X509_VALUE);
            log(Level.FINEST, "attributeQueryMapSetup", "Modified IDP" +
                    " metadata:" + idpmetadataMod);
            
            if (FederationManager.getExitCode(idpfm.deleteEntity(webClient,
                    configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM),
                    true, "saml2")) != 0) {
                log(Level.SEVERE, "attributeQueryMapSetup", "Deletion of" +
                        " idp Extended entity failed");
                log(Level.SEVERE, "attributeQueryMapSetup", "deleteEntity" +
                        " famadm command failed");
                assert(false);
            }
            if (FederationManager.getExitCode(idpfm.importEntity(webClient,
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM), "",
                    idpmetadataMod, "", "saml2")) != 0) {
                log(Level.SEVERE, "attributeQueryMapSetup", "Failed to" +
                        " import idp extended metadata");
                log(Level.SEVERE, "attributeQueryMapSetup", "importEntity" +
                        " famadm command failed");
                assert(false);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "attributeQueryMapSetup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl + "/UI/Logout");
            consoleLogout(webClient, idpurl + "/UI/Logout");
        }
        exiting("attributeQueryMapSetup");
    }
    
    /**
     * Create the webClient which will be used for the rest of the tests.
     */
    @BeforeClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", 
      "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
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
     * Execute the AttributeQuery tests
     */
    @Test(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void attributeQueryTest()
    throws Exception {
        String attrQueryURL = null;
        String ssopage;
        String mnipage;
        HtmlPage resultPage;
        String result;
        String expresult;
        try {
            Reporter.log("Test Description: This test  "+ testName + 
                    " will run to make sure " + 
                    " Attribute Query with test Type : " + testType +
                    " for the attribute : "  + testAttribute + 
                    " will work fine");
            ssopage = configMap.get(TestConstants.KEY_SSO_RESULT);
            mnipage = configMap.get(TestConstants.KEY_TERMINATE_RESULT);
            //Federate the users
            xmlfile = baseDir + testName + ".xml";
            SAMLv2Common.getxmlSPInitSSO(xmlfile, configMap, "artifact",
                    false, false);
            log(Level.FINEST, "attributeQueryTest", "Run " + xmlfile);
            task1 = new DefaultTaskHandler(xmlfile);
            wpage = task1.execute(webClient);
            if(!wpage.getWebResponse().getContentAsString().contains(ssopage)) {
                log(Level.SEVERE, "attributeQueryTest", "Couldn't " +
                        "federate users");
                assert false;
            }
            Thread.sleep(5000);
            // Single Signon
            log(Level.FINEST, "attributeQueryTest", "FederatedURL" + fedSSOURL);
            URL surl = new URL(fedSSOURL);
            HtmlPage spage = (HtmlPage)webClient.getPage(surl);
            if(!spage.getWebResponse().getContentAsString().contains(ssopage)) {
                log(Level.SEVERE, "attributeQueryTest", "Failed" + "SSO");
                assert false;
            }
            metaAliasname = configMap.get(TestConstants.KEY_SP_METAALIAS);
            if (testType.equalsIgnoreCase("noAttr")) {
                attrQueryURL = spurl + "/attrQuerytest.jsp?metaAlias=" + 
                        metaAliasname + "&idpEntityID=" +
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME) +
                        "&requestType=noAttr&attrName=na&attrValue=na";
                log(Level.FINEST, "attributeQueryTest", "NoAttr case :" 
                        + attrQueryURL);
            } else if (testType.equalsIgnoreCase("attrNamed")) {
                attrQueryURL = spurl + "/attrQuerytest.jsp?metaAlias=" + 
                        metaAliasname + "&idpEntityID=" +
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME)
                        + "&requestType=attrNamed&attrName=" + testAttribute
                        + "&attrValue=na";
                log(Level.FINEST, "attributeQueryTest", "attrNamed case :" 
                        + attrQueryURL);
            } else if (testType.equalsIgnoreCase("attrVal")) {
                String value = getExpectedResult(testAttribute);
                attrQueryURL = spurl + "/attrQuerytest.jsp?metaAlias=" + 
                        metaAliasname + "&idpEntityID=" +
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME) +
                        "&requestType=attrVal&attrName=na&attrValue=" + value;
                 log(Level.FINEST, "attributeQueryTest", "attrVal case :" 
                        + attrQueryURL);
            }
            log(Level.FINEST, "attributeQueryTest", "AttributeQueryURL" + 
                    attrQueryURL);
            URL aqurl = new URL(attrQueryURL);
            resultPage = (HtmlPage)webClient.getPage(aqurl);
            result = resultPage.getWebResponse().getContentAsString();
            expresult = getExpectedResult(testAttribute);
            log(Level.FINEST, "attributeQueryTest", "Expected Result" + 
                    expresult);
            if(!resultPage.getWebResponse().getContentAsString().
                    contains(expresult)) {
                log(Level.SEVERE, "attributeQueryTest", "Couldn't " +
                        "Get results");
                assert false;
            }
            log(Level.FINEST, "attributeQueryTest", "Result Page" + 
                    resultPage.asXml());
        } catch (Exception e){
            log(Level.SEVERE, "attributeQueryTest", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Clean up and deleted the created users for this test
     */
    @AfterClass(groups={"ldapv3_sec", "s1ds_sec", "ad_sec", "amsdk_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        ArrayList list;
        WebClient webcClient = new WebClient();
        try {
            consoleLogin(webcClient, spurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            fmSP = new FederationManager(spurl);
            list = new ArrayList();
            list.add(configMap.get(TestConstants.KEY_SP_USER));
            if (FederationManager.getExitCode(fmSP.deleteIdentities(webcClient,
                    configMap.get(TestConstants.KEY_SP_EXECUTION_REALM),
                    list , "User")) != 0) {
                log(Level.SEVERE, "setup", "deleteIdentity famadm command" +
                        " failed");
                assert false;
            }
            consoleLogin(webcClient, idpurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            fmIDP = new FederationManager(idpurl);
            list.clear();
            list.add(configMap.get(TestConstants.KEY_IDP_USER));
            if (FederationManager.getExitCode(fmIDP.deleteIdentities(webcClient,
                    configMap.get(TestConstants.KEY_IDP_EXECUTION_REALM),
                    list, "User")) != 0) {
                log(Level.SEVERE, "setup", "deleteIdentity famadm command " +
                        "failed");
                assert false;
            }
        } catch(Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webcClient, spurl + "/UI/Logout");
            consoleLogout(webcClient, idpurl + "/UI/Logout");
        }
    }
    
    private String getExpectedResult(String attrString) {
        String expected = "";
        
        if (attrString.equalsIgnoreCase("sn")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER);
        } else if (attrString.equalsIgnoreCase("cn")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER);
        } else if (attrString.equalsIgnoreCase("givenname")) {
            expected = configMap.get(TestConstants.KEY_IDP_GIVEN_NAME);
        } else if (attrString.equalsIgnoreCase("mail")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_MAIL );
        } else if (attrString.equalsIgnoreCase("employee")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_EMPLOYEE);
        } else if (attrString.equalsIgnoreCase("postal")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_POSTAL);
        } else if (attrString.equalsIgnoreCase("tele")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_TELE);
        } else if (attrString.equalsIgnoreCase("homephone")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_HOMEPHONE);
        } else if (attrString.equalsIgnoreCase("homepostal")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_HOMEPOSTAL);
        } else if (attrString.equalsIgnoreCase("mobile")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_MOBILE);
        } else if (attrString.equalsIgnoreCase("telephone")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_TELEPHONE);
        } else if (attrString.equalsIgnoreCase("pager")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_PAGER);
        } else if (attrString.equalsIgnoreCase("secretary")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_SECRETARY);
        } else if (attrString.equalsIgnoreCase("msi")) {
            expected = configMap.get(TestConstants.KEY_IDP_USER_MSISDN);
        } 
        return expected;
    }
}

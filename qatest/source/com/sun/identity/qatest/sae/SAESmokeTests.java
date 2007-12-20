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
 * $Id: SAESmokeTests.java,v 1.2 2007-12-20 22:43:12 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.sae;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.MultiProtocolCommon;
import com.sun.identity.qatest.common.SAMLv2Common;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
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
 * This class tests the following: 
 */
public class SAESmokeTests extends TestCommon {
    
    public WebClient webClient;
    private Map<String, String> configMap;
    private Map<String, String> usersMap;
    ArrayList spuserlist = new ArrayList();
    ArrayList idpuserlist = new ArrayList();
    private String  baseDir;
    private String spurl;
    private String idpurl;
    private String spmetadata;
    private String spmetadataext;
    private String idpmetadata;
    private String idpmetadataext;
    private String idpsaeAppSecretList;
    private String spsaeAppSecretList;
    private String saeSPLogoutUrl;
    private String sMode;
    private String ssoStr;
    private String sloStr;
    private ResourceBundle saeConfig;
    private FederationManager idpfm;
    private FederationManager spfm;
    private HtmlPage page;
    private HtmlPage result;
    private HtmlForm form;

    private  String AUTO_FED_ENABLED_FALSE = "<Attribute name=\""
            +  "autofedEnabled" + "\">\n"
            +  "            <Value>false</Value>\n"
            +  "        </Attribute>\n";
    private  String AUTO_FED_ENABLED_TRUE = "<Attribute name=\""
            +  "autofedEnabled" + "\">\n"
            +  "            <Value>true</Value>\n"
            +  "        </Attribute>\n";
    
    private String AUTO_FED_ATTRIB_DEFAULT = "<Attribute name=\""
            +  "autofedAttribute\">\n"
            +  "            <Value/>\n"
            +  "        </Attribute>";
    private String AUTO_FED_ATTRIB_VALUE = "<Attribute name=\""
            +  "autofedAttribute\">\n"
            +  "            <Value>branch</Value>\n"
            +  "        </Attribute>";
    
    private String ATTRIB_MAP_DEFAULT = "<Attribute name=\""
            +  "attributeMap\">\n"
            +  "            <Value/>\n"
            +  "        </Attribute>";
    private String ATTRIB_MAP_VALUE = "<Attribute name=\""
            +  "attributeMap\">\n"
            +  "            <Value>mail=mail</Value>\n"
            +  "            <Value>branch=branch</Value>\n"
            +  "        </Attribute>";

    private String IDP_COT;
    private String SP_COT;

    private String SAE_APP_SECRET_LIST_DEFAULT = "<Attribute name=\""
            +  "saeAppSecretList\"/>";
    private String IDP_SAE_APP_SECRET_LIST;
    private String SP_SAE_APP_SECRET_LIST;

    private String SP_SAE_LOGOUT_URL_DEFAULT = "<Attribute name=\""
            +  "saeSPLogoutUrl\"/>";
    private String SP_SAE_LOGOUT_URL;
    
    /** Creates a new instance of SAESmokeTests */
    public SAESmokeTests() {
        super("SAESmokeTests");
        saeConfig = ResourceBundle.getBundle("SAESmokeTests");
    }
    
    /**
     * This is setup method. It creates required users for test
     */
    @Parameters({"secMode"})
    @BeforeClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void setup(String secMode)
    throws Exception {
        Object[] params = {secMode};
        entering("setup", null);
        sMode = secMode;
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
            configMap = getMapFromResourceBundle("saeTestConfigData");
            log(Level.FINEST, "setup", "Config Map is " + configMap);
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_SP_PORT) +
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) +
                    "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":" +
                    configMap.get(TestConstants.KEY_IDP_PORT) +
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);

            webClient = new WebClient();

            list = new ArrayList();
            consoleLogin(webClient, spurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            
            consoleLogin(webClient, idpurl + "/UI/Login", configMap.get(
                    TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));

            spfm = new FederationManager(spurl);
            idpfm = new FederationManager(idpurl);
            
            // create sp user first
            list.clear();
            list.add("sn=" + saeConfig.getString(TestConstants.KEY_SP_USER));
            list.add("cn=" + saeConfig.getString(TestConstants.KEY_SP_USER));
            list.add("userpassword=" + saeConfig.getString(
                    TestConstants.KEY_SP_USER_PASSWORD));
            list.add("inetuserstatus=Active");
            log(Level.FINEST, "setup", "SP user to be created is " + list);
            spfm.createIdentity(webClient, configMap.get(
                    TestConstants.KEY_SP_REALM),
                    saeConfig.getString(TestConstants.KEY_SP_USER), "User",
                    list);
            spuserlist.add(saeConfig.getString(TestConstants.KEY_SP_USER));
            
            HtmlPage spmetaPage = spfm.exportEntity(webClient,
                    configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_SP_REALM),
                    false, false, true, "saml2");
            spmetadataext = 
                    MultiProtocolCommon.getExtMetadataFromPage(spmetaPage);
            String spmetadataextMod =
                    spmetadataext.replaceAll(AUTO_FED_ENABLED_FALSE,
                    AUTO_FED_ENABLED_TRUE);
            spmetadataextMod =
                    spmetadataextMod.replaceAll(AUTO_FED_ATTRIB_DEFAULT,
                    AUTO_FED_ATTRIB_VALUE);
            spmetadataextMod = spmetadataextMod.replaceAll(ATTRIB_MAP_DEFAULT,
                    ATTRIB_MAP_VALUE);

            spmetadataextMod = spmetadataextMod.replaceAll(
                    "hosted=\"true\"", "hosted=\"false\"");
            spmetadataextMod = spmetadataextMod.replaceAll(
                    "hosted=\"1\"", "hosted=\"0\"");

            IDP_COT = "<Attribute name=\"cotlist\">\n"
            +  "            <Value>" +  configMap.get(TestConstants.KEY_IDP_COT)
            + "</Value>\n" +  "        </Attribute>";

            SP_COT = "<Attribute name=\"cotlist\">\n"
            +  "            <Value>" +  configMap.get(TestConstants.KEY_SP_COT)
            + "</Value>\n"  +  "        </Attribute>";

            spmetadataextMod = spmetadataextMod.replaceAll(SP_COT, IDP_COT);

            // load sp extended metadata on idp
            HtmlPage deleteExtEntity = idpfm.deleteEntity(webClient,
                    configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_SP_REALM),
                    true, "saml2" );
            if (!deleteExtEntity.getWebResponse().getContentAsString().
                    contains("Configuration is deleted for entity, " +
                    configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                log(Level.FINEST, "setup", "Deletion of Extended " +
                        "entity failed:" + deleteExtEntity.getWebResponse().
                        getContentAsString());
                assert(false);
            }

            HtmlPage importMeta = idpfm.importEntity(webClient,
                    configMap.get(TestConstants.KEY_SP_REALM), "",
                    spmetadataextMod, "", "saml2");
            if (!importMeta.getWebResponse().getContentAsString().
                    contains("Import file, web.")) {
                log(Level.FINEST, "setup", "Failed to import extended " +
                        "metadata:" + importMeta.getWebResponse().
                        getContentAsString());
                assert(false);
            }

            spmetadataextMod = spmetadataextMod.replaceAll(
                    "hosted=\"false\"", "hosted=\"true\"");
            spmetadataextMod = spmetadataextMod.replaceAll(
                    "hosted=\"0\"", "hosted=\"1\"");

            spmetadataextMod = spmetadataextMod.replaceAll(IDP_COT, SP_COT);

            if (secMode.equals("symmetric")) {
                spsaeAppSecretList = spurl +
                    "/" + saeConfig.getString("spAppURL") +
                    "|type=" + secMode + 
                    "|secret=" + saeConfig.getString("sharedSecretKey");
            } else if (secMode.equals("asymmetric")) {
                spsaeAppSecretList = spurl +
                    "/" + saeConfig.getString("spAppURL") +
                    "|type=" + secMode + 
                    "|pubkeyalias=" + saeConfig.getString("sp_keyalias");
            }
            log(Level.FINEST, "setup", "spsaeAppSecretList=" +
                    spsaeAppSecretList);
            SP_SAE_APP_SECRET_LIST = "<Attribute name=\""
            +  "saeAppSecretList\">\n"
            +  "            <Value>url=" + spsaeAppSecretList + "</Value>\n"
            +  "        </Attribute>";

            spmetadataextMod =
                    spmetadataextMod.replaceAll(SAE_APP_SECRET_LIST_DEFAULT,
                    SP_SAE_APP_SECRET_LIST);
            saeSPLogoutUrl = spurl +
                    "/" + saeConfig.getString("spAppURL");
            SP_SAE_LOGOUT_URL = "<Attribute name=\"" +  "saeSPLogoutUrl\">\n"
            +  "            <Value>" + saeSPLogoutUrl + "</Value>\n"
            +  "        </Attribute>";

            spmetadataextMod =
                    spmetadataextMod.replaceAll(SP_SAE_LOGOUT_URL_DEFAULT,
                    SP_SAE_LOGOUT_URL);
            log(Level.FINEST, "setup", "Modified metadata:" +
                    spmetadataextMod);

            // load sp extended metadata on sp
            deleteExtEntity = spfm.deleteEntity(webClient,
                    configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_SP_REALM),
                    true, "saml2" );
            if (!deleteExtEntity.getWebResponse().getContentAsString().
                    contains("Configuration is deleted for entity, " +
                    configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                log(Level.FINEST, "setup", "Deletion of Extended " +
                        "entity failed" + deleteExtEntity.getWebResponse().
                        getContentAsString());
                assert(false);
            }

            importMeta = spfm.importEntity(webClient,
                    configMap.get(TestConstants.KEY_SP_REALM), "",
                    spmetadataextMod, "", "saml2");
            if (!importMeta.getWebResponse().getContentAsString().
                    contains("Import file, web.")) {
                log(Level.FINEST, "setup", "Failed to import extended " +
                        "metadata:" + importMeta.getWebResponse().
                        getContentAsString());
                assert(false);
            }

            HtmlPage idpmetaPage = idpfm.exportEntity(webClient,
                    configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_IDP_REALM), false, false,
                    true, "saml2");
            idpmetadataext = MultiProtocolCommon.getExtMetadataFromPage(
                    idpmetaPage);
            if (secMode.equals("symmetric")) {
                idpsaeAppSecretList = idpurl +
                    "/" + saeConfig.getString("idpAppURL") +
                    "|type=" + secMode +
                    "|secret=" + saeConfig.getString("sharedSecretKey");
            } else if (secMode.equals("asymmetric")) {
                idpsaeAppSecretList = idpurl +
                    "/" + saeConfig.getString("idpAppURL") +
                    "|type=" + secMode + 
                    "|pubkeyalias=" + saeConfig.getString("idp_keyalias");
            }
            log(Level.FINEST, "setup", "idpsaeAppSecretList=" +
                    idpsaeAppSecretList);
            IDP_SAE_APP_SECRET_LIST = "<Attribute name=\""
            +  "saeAppSecretList\">\n"
            +  "            <Value>url=" + idpsaeAppSecretList + "</Value>\n"
            +  "        </Attribute>";
            String idpmetadataextMod =
                    idpmetadataext.replaceAll(SAE_APP_SECRET_LIST_DEFAULT,
                    IDP_SAE_APP_SECRET_LIST);
            log(Level.FINEST, "setup", "Modified metadata:" +
                    idpmetadataextMod);

            // load idp extended metadata on idp
            deleteExtEntity = idpfm.deleteEntity(webClient,
                    configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_IDP_REALM),
                    true, "saml2" );
            if (!deleteExtEntity.getWebResponse().getContentAsString().
                    contains("Configuration is deleted for entity, " +
                    configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                log(Level.FINEST, "setup",  "Deletion of Extended " +
                        "entity failed:" + deleteExtEntity.getWebResponse().
                        getContentAsString());
                assert(false);
            }

            importMeta = idpfm.importEntity(webClient,
                    configMap.get(TestConstants.KEY_IDP_REALM), "",
                    idpmetadataextMod, "", "saml2");
            if (!importMeta.getWebResponse().getContentAsString().
                    contains("Import file, web.")) {
                log(Level.FINEST, "setup", "Failed to import extended " +
                        "metadata:" + importMeta.getWebResponse().
                        getContentAsString());
                assert(false);
            }

            idpmetadataextMod = idpmetadataextMod.replaceAll(
                    "hosted=\"true\"", "hosted=\"false\"");
            idpmetadataextMod = idpmetadataextMod.replaceAll(
                    "hosted=\"1\"", "hosted=\"0\"");

            idpmetadataextMod = idpmetadataextMod.replaceAll(IDP_COT, SP_COT);

            // load idp extended metadata on sp
            deleteExtEntity = spfm.deleteEntity(webClient,
                    configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                    configMap.get(TestConstants.KEY_IDP_REALM),
                    true, "saml2" );
            if (!deleteExtEntity.getWebResponse().getContentAsString().
                    contains("Configuration is deleted for entity, " +
                    configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                log(Level.FINEST, "setup", "Deletion of Extended " +
                        "entity failed:" + deleteExtEntity.getWebResponse().
                        getContentAsString());
                assert(false);
            }

            importMeta = spfm.importEntity(webClient,
                    configMap.get(TestConstants.KEY_IDP_REALM), "",
                    idpmetadataextMod, "", "saml2");
            if (!importMeta.getWebResponse().getContentAsString().
                    contains("Import file, web.")) {
                log(Level.FINEST, "setup", "Failed to import extended " +
                        "metadata:" + importMeta.getWebResponse().
                        getContentAsString());
                assert(false);
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
    
    @Test(groups={"ds_ds", "ff_ds"})
    public void symmetricSSOWithProfileIgnored()
    throws Exception {
        entering("symmetricSSOWithProfileIgnored", null);
        try {
            enableUserProfile("SP", spfm, "ignore");
            enableUserProfile("IDP", idpfm, "ignore");
            setSPAppData("symmetric");
            generateSSOSLOURL("symmetric");
            page = (HtmlPage) webClient.getPage(ssoStr);
            assert (getHtmlPageStringIndex(page,
                    saeConfig.getString("mail_attribute")) != -1);
            assert (getHtmlPageStringIndex(page,
                    saeConfig.getString("branch_attribute")) != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "symmetricSSOWithProfileIgnored", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("symmetricSSOWithProfileIgnored");
    }

    @Test(groups={"ds_ds", "ff_ds"},
    dependsOnMethods={"symmetricSSOWithProfileIgnored"})
    public void symmetricSLOWithProfileIgnored()
    throws Exception {
        entering("symmetricSLOWithProfileIgnored", null);
        try {
            page = (HtmlPage) webClient.getPage(sloStr);
            result = (HtmlPage) webClient.getPage(getLogoutURL(page));
            assert (getHtmlPageStringIndex(result, "Generate URL") != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "symmetricSLOWithProfileIgnored", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("symmetricSLOWithProfileIgnored");
    }

    @Test(groups={"ds_ds", "ff_ds"},
    dependsOnMethods={"symmetricSLOWithProfileIgnored"})
    public void symmetricSSOWithProfileRequired()
    throws Exception {
        entering("symmetricSSOWithProfileRequired", null);
        try {
            enableUserProfile("SP", spfm, "false");
            generateSSOSLOURL("symmetric");
            page = (HtmlPage) webClient.getPage(ssoStr);
            form = page.getFormByName("Login");
            HtmlHiddenInput txt1 =
                    (HtmlHiddenInput)form.getInputByName("IDToken1");
            txt1.setValueAttribute(saeConfig.getString("sp_user"));
            HtmlHiddenInput txt2 =
                    (HtmlHiddenInput)form.getInputByName("IDToken2");
            txt2.setValueAttribute(saeConfig.getString("sp_userpw"));
            page = (HtmlPage)form.submit();
            assert (getHtmlPageStringIndex(page,
                    saeConfig.getString("mail_attribute")) != -1);
            assert (getHtmlPageStringIndex(page,
                    saeConfig.getString("branch_attribute")) != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "symmetricSSOWithProfileRequired",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("symmetricSSOWithProfileRequired");
    }

    @Test(groups={"ds_ds", "ff_ds"},
    dependsOnMethods={"symmetricSSOWithProfileRequired"})
    public void symmetricSLOWithProfileRequired()
    throws Exception {
        entering("symmetricSLOWithProfileRequired", null);
        try {
            page = (HtmlPage) webClient.getPage(sloStr);
            result = (HtmlPage) webClient.getPage(getLogoutURL(page));
            assert (getHtmlPageStringIndex(result, "Generate URL") != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "symmetricSLOWithProfileRequired",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("symmetricSLOWithProfileRequired");
    }

    @Test(groups={"ds_ds_sec", "ff_ds_sec"})
    public void asymmetricSSOWithProfileIgnored()
    throws Exception {
        entering("asymmetricSSOWithProfileIgnored", null);
        try {
            enableUserProfile("SP", spfm, "ignore");
            enableUserProfile("IDP", idpfm, "ignore");
            setSPAppData("asymmetric");
            generateSSOSLOURL("asymmetric");
            page = (HtmlPage) webClient.getPage(ssoStr);
            assert (getHtmlPageStringIndex(page,
                    saeConfig.getString("mail_attribute")) != -1);
            assert (getHtmlPageStringIndex(page,
                    saeConfig.getString("branch_attribute")) != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "asymmetricSSOWithProfileIgnored",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("asymmetricSSOWithProfileIgnored");
    }

    @Test(groups={"ds_ds_sec", "ff_ds_sec"},
    dependsOnMethods={"asymmetricSSOWithProfileIgnored"})
    public void asymmetricSLOWithProfileIgnored()
    throws Exception {
        entering("asymmetricSLOWithProfileIgnored", null);
        try {
            page = (HtmlPage) webClient.getPage(sloStr);
            result = (HtmlPage) webClient.getPage(getLogoutURL(page));
            assert (getHtmlPageStringIndex(result, "Generate URL") != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "asymmetricSLOWithProfileIgnored",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("asymmetricSLOWithProfileIgnored");
    }

    @Test(groups={"ds_ds_sec", "ff_ds_sec"},
    dependsOnMethods={"asymmetricSLOWithProfileIgnored"})
    public void asymmetricSSOWithProfileRequired()
    throws Exception {
        entering("asymmetricSSOWithProfileRequired", null);
        try {
            enableUserProfile("SP", spfm, "false");
            generateSSOSLOURL("asymmetric");
            page = (HtmlPage) webClient.getPage(ssoStr);
            form = page.getFormByName("Login");
            HtmlHiddenInput txt1 =
                    (HtmlHiddenInput)form.getInputByName("IDToken1");
            txt1.setValueAttribute(saeConfig.getString("sp_user"));
            HtmlHiddenInput txt2 =
                    (HtmlHiddenInput)form.getInputByName("IDToken2");
            txt2.setValueAttribute(saeConfig.getString("sp_userpw"));
            page = (HtmlPage)form.submit();
            assert (getHtmlPageStringIndex(page,
                    saeConfig.getString("mail_attribute")) != -1);
            assert (getHtmlPageStringIndex(page,
                    saeConfig.getString("branch_attribute")) != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "asymmetricSSOWithProfileRequired",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("asymmetricSSOWithProfileRequired");
    }

    @Test(groups={"ds_ds_sec", "ff_ds_sec"},
    dependsOnMethods={"asymmetricSSOWithProfileRequired"})
    public void asymmetricSLOWithProfileRequired()
    throws Exception {
        entering("asymmetricSLOWithProfileRequired", null);
        try {
            page = (HtmlPage) webClient.getPage(sloStr);
            result = (HtmlPage) webClient.getPage(getLogoutURL(page));
            assert (getHtmlPageStringIndex(result, "Generate URL") != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "asymmetricSLOWithProfileRequired",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("asymmetricSLOWithProfileRequired");
    }

    /**
     * This methods deletes the users and sets the default authentication
     * profile at SP and IDP as Required.
     */
    @AfterClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);

        try {
            webClient = new WebClient();
            consoleLogin(webClient, spurl + "/UI/Login", configMap.get(
                    TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            spfm.deleteIdentities(webClient, configMap.get(
                    TestConstants.KEY_SP_REALM),
                    spuserlist, "User");
            enableUserProfile("SP", spfm, "false");
            enableUserProfile("IDP", idpfm, "false");
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl + "/UI/Logout");
        }

        exiting("cleanup");
    }

    /**
     * Sets the default user authenication profile under global auth
     * configuration.
     */
    private void enableUserProfile(String id, FederationManager fmadm,
            String profile)
    throws Exception {
        try {
            HtmlPage serviceAtt = null;
            List listDyn = new ArrayList();
            listDyn.add("iplanet-am-auth-dynamic-profile-creation=" + profile);
            webClient = new WebClient();
            if (id.equals("SP")) {
                consoleLogin(webClient, spurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
                serviceAtt = fmadm.setServiceAttributes(webClient,
                    configMap.get(TestConstants.KEY_SP_REALM),
                    "iPlanetAMAuthService", listDyn);
            } else if (id.equals("IDP")) {
                consoleLogin(webClient, idpurl + "/UI/Login",
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    configMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
                serviceAtt = fmadm.setServiceAttributes(webClient,
                    configMap.get(TestConstants.KEY_IDP_REALM),
                    "iPlanetAMAuthService", listDyn);
            }
            if (serviceAtt.getWebResponse().getContentAsString().
                contains("is modified")) {
                log(Level.FINE, "enableUserProfile",
                    "Successfully enabled " + profile + " user creation");
            } else {
                log(Level.SEVERE, "enableUserProfile",
                    "Couldn't enable " + profile + " user creation" +
                    serviceAtt.getWebResponse().getContentAsString());
                assert(false);
            }
        } catch (Exception e) {
                log(Level.SEVERE, "enableUserProfile", e.getMessage());
                e.printStackTrace();
                throw e;
        } finally {
                if (id.equals("SP"))
                    consoleLogout(webClient, spurl + "/UI/Logout");
                else if (id.equals("IDP"))
                    consoleLogout(webClient, idpurl + "/UI/Logout");
        }
    }

    /**
     * Sets the values in properties file, which is consumed by the SP App.
     * A jsp deployed on the SP App container is used to create and write the
     * properties file.
     */
    private void setSPAppData(String secMode)
    throws Exception {
        log(Level.FINEST, "setSPAppData", "spurl: " + spurl);
        String spAppCreateDataFileURL = saeConfig.getString("spAppCreateDataFileURL");
        log(Level.FINEST, "setSPAppData", "spAppCreateDataFileURL: " + spAppCreateDataFileURL);
        page = (HtmlPage) webClient.getPage(spurl + "/" + spAppCreateDataFileURL);

        form = (HtmlForm)page.getForms().get(0);

        log(Level.FINEST, "setSPAppData", "secMode: " + secMode);
        ((HtmlTextInput)form.getInputByName("cryptotype")).
                setValueAttribute(secMode);

        String sharedSecret = saeConfig.getString("sharedSecret");
        log(Level.FINEST, "setSPAppData", "sharedSecret: " + sharedSecret);
        ((HtmlTextInput)form.getInputByName("secret")).
                setValueAttribute(sharedSecret);

        if (secMode.equals("asymmetric")) {
            String sp_keyalias = saeConfig.getString("sp_keyalias");
            log(Level.FINEST, "setSPAppData", "sp_keyalias: " + sp_keyalias);
            ((HtmlTextInput)form.getInputByName("secret")).
                    setValueAttribute(saeConfig.getString("sp_keyalias"));

            String sp_keystore = saeConfig.getString("sp_keystore");
            log(Level.FINEST, "setSPAppData", "sp_keystore: " + sp_keystore);
            ((HtmlTextInput)form.getInputByName("keystore")).
                    setValueAttribute(sp_keystore);

            String sp_keypass = saeConfig.getString("sp_keypass");
            log(Level.FINEST, "setSPAppData", "sp_keypass: " + sp_keypass);
            ((HtmlTextInput)form.getInputByName("keypass")).
                    setValueAttribute(sp_keypass);

            String sp_storepass = saeConfig.getString("sp_storepass");
            log(Level.FINEST, "setSPAppData", "sp_storepass: " + sp_storepass);
            ((HtmlTextInput)form.getInputByName("privkeypass")).
                    setValueAttribute(sp_storepass);
        }
        result = (HtmlPage)form.submit();
    }

    /**
     * Generates the SSO and SLO url's from the main IDP App JSP. Basically
     * this page is obtained when I clicks on Generate URL link on the main IDP
     * app page.
     */
    private HtmlPage generateSSOSLOURL(String secMode)
    throws Exception {
        log(Level.FINEST, "generateSSOSLOURL", "secMode: " + secMode);

        log(Level.FINEST, "generateSSOSLOURL", "idpurl: " + idpurl);
        String idpAppURL = saeConfig.getString("idpAppURL");
        log(Level.FINEST, "generateSSOSLOURL", "idpAppURL: " + idpAppURL);
        page = (HtmlPage) webClient.getPage(idpurl + "/" + idpAppURL);

        form = (HtmlForm)page.getForms().get(0);

        String idp_userid = saeConfig.getString("idp_userid");
        log(Level.FINEST, "generateSSOSLOURL", "idp_userid: " + idp_userid);
        ((HtmlTextInput)form.getInputByName("userid")).
                setValueAttribute(idp_userid);

        String mail_attribute = saeConfig.getString("mail_attribute");
        log(Level.FINEST, "generateSSOSLOURL", "mail_attribute: " + mail_attribute);
        ((HtmlTextInput)form.getInputByName("mail")).
                setValueAttribute(mail_attribute);

        String branch_attribute = saeConfig.getString("branch_attribute");
        log(Level.FINEST, "generateSSOSLOURL", "branch_attribute: " + branch_attribute);
        ((HtmlTextInput)form.getInputByName("branch")).
                setValueAttribute(branch_attribute);

        String spAppURL = saeConfig.getString("spAppURL");
        log(Level.FINEST, "generateSSOSLOURL", "spAppURL: " + spAppURL);
        ((HtmlTextInput)form.getInputByName("spapp")).
                setValueAttribute(spurl + "/" +
                spAppURL);

        String idpAppHandler = saeConfig.getString("idpAppHandler");
        log(Level.FINEST, "generateSSOSLOURL", "idpAppHandler: " + idpAppHandler);
        ((HtmlTextInput)form.getInputByName("saeurl")).
                setValueAttribute(idpurl + "/" +
                idpAppHandler + "/metaAlias/" +
                configMap.get(TestConstants.KEY_IDP_HOST));

        ((HtmlTextInput)form.getInputByName("idpappname")).
                setValueAttribute(idpurl + "/" +
                idpAppURL);

        ((HtmlTextInput)form.getInputByName("cryptotype")).
                setValueAttribute(secMode);

        String sharedSecret = saeConfig.getString("sharedSecret");
        log(Level.FINEST, "generateSSOSLOURL", "sharedSecret: " + sharedSecret);
        ((HtmlTextInput)form.getInputByName("secret")).
                setValueAttribute(sharedSecret);

        if (secMode.equals("asymmetric")) {
            String idp_keyalias = saeConfig.getString("idp_keyalias");
            log(Level.FINEST, "generateSSOSLOURL", "idp_keyalias: " + idp_keyalias);
            ((HtmlTextInput)form.getInputByName("secret")).
                    setValueAttribute(idp_keyalias);

            String idp_keystore = saeConfig.getString("idp_keystore");
            log(Level.FINEST, "generateSSOSLOURL", "idp_keystore: " + idp_keystore);
            ((HtmlTextInput)form.getInputByName("keystore")).
                    setValueAttribute(idp_keystore);

            String idp_keypass = saeConfig.getString("idp_keypass");
            log(Level.FINEST, "generateSSOSLOURL", "idp_keypass: " + idp_keypass);
            ((HtmlTextInput)form.getInputByName("keypass")).
                    setValueAttribute(idp_keypass);

            String idp_storepass = saeConfig.getString("idp_storepass");
            log(Level.FINEST, "generateSSOSLOURL", "idp_storepass: " + idp_storepass);
            ((HtmlTextInput)form.getInputByName("privkeypass")).
                    setValueAttribute(idp_storepass);
        }
        result = (HtmlPage)form.submit();
        log(Level.FINEST, "generateSSOSLOURL", "Generate URL Page:\n" +
                result.asXml());
        List list = result.getAnchors();
        String anchStr = list.get(2).toString();
        ssoStr = anchStr.substring(anchStr.indexOf("=") + 2,
                anchStr.length() - 3);
        log(Level.FINEST, "generateSSOSLOURL", "SSO URL:" + ssoStr);
        anchStr = list.get(3).toString();
        sloStr = anchStr.substring(anchStr.indexOf("=") + 2,
                anchStr.length() - 3);
        log(Level.FINEST, "generateSSOSLOURL", "SLO URL:" + sloStr);
        return (result);
    }

    /**
     * Generates the Logout url from the second IDP App JSP. This page is
     * generated when one clicks on the page which lists the SSO and SLO url's.
     * This shows up when one clicks on SLO url.
     */
    private String getLogoutURL(HtmlPage page)
    throws Exception {
        List list = page.getAnchors();
        String anchStr = list.get(2).toString();
        String logoutStr = anchStr.substring(anchStr.indexOf("=") + 2,
                anchStr.length() - 3);
        log(Level.FINEST, "getLogoutURL", "Logout URL:" + logoutStr);
        return (logoutStr);
    }
}

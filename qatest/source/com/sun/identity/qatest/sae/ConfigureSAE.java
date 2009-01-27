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
 * $Id: ConfigureSAE.java,v 1.8 2009-01-27 00:11:58 nithyas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.sae;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.MultiProtocolCommon;
import com.sun.identity.qatest.common.SAMLv2Common;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class creates COT  and loads SAE metadata on both IDP and SP.
 */
public class ConfigureSAE extends TestCommon {
    private WebClient spWebClient;
    private WebClient idpWebClient;
    private Map<String, String> configMap;
    public String groupName="";
    
    /** Creates a new instance of ConfigureSAE */
    public ConfigureSAE() {
        super("ConfigureSAE");
    }
    
    /**
     * Create the webClient which should be run before each test.
     */
    private void getWebClient() throws Exception {
        try {
            spWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
            idpWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        } catch(Exception e) {
            log(Level.SEVERE, "getWebClient", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Configure sp & idp
     * @DocTest: SAE|Configure SP & IDP by loading metadata on both sides.
     */
    @Parameters({"groupName"})
    @BeforeSuite(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void configureSAE(String strGroupName)
    throws Exception {
        Object[] params = {strGroupName};
        entering("configureSAE", params);
        String spurl = null;
        String idpurl = null;
        try {
            URL url;
            HtmlPage page;
            //Upload global properties file in configMap
            configMap = new HashMap<String, String>();
            getWebClient();
            
            log(Level.FINEST, "configureSAE", "GroupName received from " +
                    "testng is " + strGroupName);
            SAMLv2Common.getEntriesFromResourceBundle("sae" + fileseparator + 
                    "saeTestConfigData", configMap);
            log(Level.FINEST, "configureSAE", "Map:" + configMap);
            
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL)
            + "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":"
                    + configMap.get(TestConstants.KEY_SP_PORT)
                    + configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL)
            + "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":"
                    + configMap.get(TestConstants.KEY_IDP_PORT)
                    + configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
            
            FederationManager spfm = new FederationManager(spurl);
            FederationManager idpfm = new FederationManager(idpurl);
            
            //on sp side create cot, load sp metadata
            consoleLogin(spWebClient, spurl + "/UI/Login",
                    (String)configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    (String)configMap.get(
                    TestConstants.KEY_SP_AMADMIN_PASSWORD));
            
            HtmlPage spcotPage = spfm.listCots(spWebClient,
                    configMap.get(TestConstants.KEY_SP_REALM));
            if (FederationManager.getExitCode(spcotPage) != 0) {
               log(Level.SEVERE, "configureSAE", "listCots famadm command" +
                       " failed");
               assert false;
            }
            if (!spcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_COT))) {
                if (FederationManager.getExitCode(spfm.createCot(spWebClient,
                        configMap.get(TestConstants.KEY_SP_COT),
                        configMap.get(TestConstants.KEY_SP_REALM),
                        null, null)) != 0) {
                    log(Level.SEVERE, "configureSAE", "Couldn't create " +
                            "COT at SP side");
                    log(Level.SEVERE, "configureSAE", "createCot famadm" +
                            " command failed");
                    assert false;
                }
            } else {
                log(Level.FINEST, "configureSAE", "COT exists at SP side");
            }
            
            String spMetadata[] = {"",""};
            HtmlPage spEntityPage = spfm.listEntities(spWebClient,
                    configMap.get(TestConstants.KEY_SP_REALM), "saml2");
            if (FederationManager.getExitCode(spEntityPage) != 0) {
               log(Level.SEVERE, "configureSAE", "listEntities famadm" +
                       " command failed");
               assert false;
            }
            if (!spEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                log(Level.FINEST, "configureSAE", "sp entity doesnt exist. " +
                        "Get template & create the entity");
                if (strGroupName.contains("sec")) {
                    spMetadata = SAMLv2Common.configureSP(spWebClient,
                            configMap, true);
                } else {
                    spMetadata = SAMLv2Common.configureSP(spWebClient,
                            configMap, false);
                }
                if ((spMetadata[0].equals(null)) ||
                        (spMetadata[1].equals(null))) {
                    log(Level.SEVERE, "configureSAE", "Couldn't configure " +
                            "SP");
                    assert false;
                }
            } else {
                //If entity exists, export to get the metadata.
                HtmlPage spExportEntityPage = spfm.exportEntity(spWebClient,
                        configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_SP_REALM), true, true,
                        true, "saml2");
                if (FederationManager.getExitCode(spExportEntityPage) != 0) {
                   log(Level.SEVERE, "configureSAE", "exportEntity famadm" +
                           " command failed");
                   assert false;
                }
                spMetadata[0] = SAMLv2Common.getMetadataFromPage(
                        spExportEntityPage);
                spMetadata[1] = SAMLv2Common.getExtMetadataFromPage(
                        spExportEntityPage);
            }
            spMetadata[1] = spMetadata[1].replaceAll(
                    configMap.get(TestConstants.KEY_SP_COT), "");
            log(Level.FINEST, "configureSAE", "sp metadata" + spMetadata[0]);
            log(Level.FINEST, "configureSAE", "sp Ext metadata" +
                    spMetadata[1]);
            
            //idp side create cot, load idp metadata
            consoleLogin(idpWebClient, idpurl + "/UI/Login",
                    (String)configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    (String)configMap.get(
                    TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            
            HtmlPage idpcotPage = idpfm.listCots(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_REALM));
            if (FederationManager.getExitCode(idpcotPage) != 0) {
               log(Level.SEVERE, "configureSAE", "createMetadataTempl famadm" +
                       " command failed");
               assert false;
            }
            if (idpcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_COT))) {
                log(Level.FINEST, "configureSAE", "COT exists at IDP side");
            } else {
                if (FederationManager.getExitCode(idpfm.createCot(idpWebClient,
                        configMap.get(TestConstants.KEY_IDP_COT),
                        configMap.get(TestConstants.KEY_IDP_REALM),
                        null, null)) != 0) {
                    log(Level.SEVERE, "configureSAE", "Couldn't create " +
                            "COT at IDP side");
                    log(Level.SEVERE, "configureSAE", "createCot famadm" +
                            " command failed");
                    assert false;
                }
            }
            
            String[] idpMetadata = {"",""};
            HtmlPage idpEntityPage = idpfm.listEntities(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_REALM), "saml2");
            if (FederationManager.getExitCode(idpEntityPage) != 0) {
               log(Level.SEVERE, "configureSAE", "listEntities famadm command" +
                       " failed");
               assert false;
            }
            if (!idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_ENTITY_NAME)))
            {
                log(Level.FINEST, "configureSAE", "idp entity doesnt exist. " +
                        "Get template & create the entity");
                if (strGroupName.contains("sec")) {
                    idpMetadata = SAMLv2Common.configureIDP(idpWebClient,
                            configMap, true);
                } else {
                    idpMetadata = SAMLv2Common.configureIDP(idpWebClient,
                            configMap, false);
                }
                
                log(Level.FINEST, "configureSAE", "idp metadata" +
                        idpMetadata[0]);
                log(Level.FINEST, "configureSAE", "idp Ext metadata" +
                        idpMetadata[1]);
                if ((idpMetadata[0].equals(null)) || (
                        idpMetadata[1].equals(null))) {
                    log(Level.SEVERE, "configureSAE", "Couldn't configure " +
                            "IDP");
                    assert false;
                }
             } else {
                //If entity exists, export to get the metadata.
                HtmlPage idpExportEntityPage = idpfm.exportEntity(idpWebClient,
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_IDP_REALM), true, true,
                        true, "saml2");
                if (FederationManager.getExitCode(idpExportEntityPage) != 0) {
                   log(Level.SEVERE, "configureSAE", "exportEntity famadm" +
                           " command failed");
                   assert false;
                }
                idpMetadata[0] = SAMLv2Common.getMetadataFromPage(
                        idpExportEntityPage);
                idpMetadata[1] = SAMLv2Common.getExtMetadataFromPage(
                        idpExportEntityPage);
            }
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    configMap.get(TestConstants.KEY_IDP_COT), "");
            log(Level.FINEST, "configureSAE", "idp metadata" +
                    idpMetadata[0]);
            log(Level.FINEST, "configureSAE", "idp Ext metadata" +
                    idpMetadata[1]);
            
            //load spmetadata on idp
            if (idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                log(Level.FINEST, "configureSAE", "sp entity exists at idp. " +
                        "Delete & load the metadata ");
                if (FederationManager.getExitCode(idpfm.deleteEntity(
                        idpWebClient, 
                        configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_SP_REALM), false,
                        "saml2")) == 0) {
                    log(Level.FINEST, "configureSAE", "Delete sp entity on " +
                            "IDP side");
                } else {
                    log(Level.SEVERE, "configureSAE", "Couldnt delete sp " +
                            "entity on IDP side");
                    log(Level.SEVERE, "configureSAE", "deleteEntity famadm" +
                            " command failed");
                    assert false;
                }
            }
            spMetadata[1] = spMetadata[1].replaceAll(
                    "hosted=\"true\"", "hosted=\"false\"");
            spMetadata[1] = spMetadata[1].replaceAll(
                    "hosted=\"1\"", "hosted=\"0\"");
            if (FederationManager.getExitCode(idpfm.importEntity(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_REALM), spMetadata[0],
                    spMetadata[1],
                    (String)configMap.get(TestConstants.KEY_IDP_COT), "saml2"))
                    != 0) {
                log(Level.SEVERE, "configureSAE", "Couldn't import SP " +
                        "metadata on IDP side");
                log(Level.SEVERE, "configureSAE", "importEntity famadm" +
                        " command failed");
                assert false;
            }
            //load idpmetadata on sp
            if (spEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_ENTITY_NAME)))
            {
                log(Level.FINEST, "configureSAE", "idp entity exists at sp. " +
                        "Delete & load the metadata ");
                if (FederationManager.getExitCode(spfm.deleteEntity(spWebClient,
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_IDP_REALM), false,
                        "saml2")) == 0) {
                    log(Level.FINEST, "configureSAE", "Delete idp entity on " +
                            "SP side");
                } else {
                    log(Level.SEVERE, "configureSAE", "Couldnt delete idp " +
                            "entity on SP side");
                    log(Level.SEVERE, "configureSAE", "deleteEntity famadm" +
                            " command failed");
                    assert false;
                }
            }
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    "hosted=\"true\"", "hosted=\"false\"");
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    "hosted=\"1\"", "hosted=\"0\"");
            if (FederationManager.getExitCode(spfm.importEntity(spWebClient,
                    configMap.get(TestConstants.KEY_SP_REALM), idpMetadata[0],
                    idpMetadata[1],
                    (String)configMap.get(TestConstants.KEY_SP_COT), "saml2"))
                    != 0) {
                log(Level.SEVERE, "configureSAE", "Couldn't import IDP " +
                        "metadata on SP side");
                log(Level.SEVERE, "configureSAE", "importEntity famadm" +
                        " command failed");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "configureSAE", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(spWebClient, spurl);
            consoleLogout(idpWebClient, idpurl);
        }
        exiting("configureSAE");
    }
    
    /**
     * This method fills map with SP configuration data which is needed by
     * TestCommon.configureProduct method.
     */
    private void getSPConfigurationMap(Map spMap, Map confMap)
    throws Exception {
        try {
            spMap.put("serverurl",confMap.get(TestConstants.KEY_SP_PROTOCOL)
            + ":" + "//" + confMap.get(TestConstants.KEY_SP_HOST) + ":"
                    + confMap.get(TestConstants.KEY_SP_PORT));
            spMap.put("serveruri",
                    confMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI));
            spMap.put(TestConstants.KEY_ATT_COOKIE_DOMAIN,
                    confMap.get(TestConstants.KEY_SP_COOKIE_DOMAIN));
            spMap.put(TestConstants.KEY_ATT_CONFIG_DIR,
                    confMap.get(TestConstants.KEY_SP_CONFIG_DIR));
            spMap.put(TestConstants.KEY_ATT_AMADMIN_PASSWORD,
                    confMap.get(TestConstants.KEY_SP_AMADMIN_PASSWORD));
            spMap.put(TestConstants.KEY_ATT_CONFIG_DATASTORE,
                    confMap.get(TestConstants.KEY_SP_DATASTORE));
            spMap.put(TestConstants.KEY_ATT_AM_ENC_PWD,
                    confMap.get(TestConstants.KEY_SP_ENC_KEY));
            spMap.put(TestConstants.KEY_ATT_DIRECTORY_SERVER,
                    confMap.get(TestConstants.KEY_SP_DIRECTORY_SERVER));
            spMap.put(TestConstants.KEY_ATT_DIRECTORY_PORT,
                    confMap.get(TestConstants.KEY_SP_DIRECTORY_PORT));
            spMap.put(TestConstants.KEY_ATT_CONFIG_ROOT_SUFFIX,
                    confMap.get(TestConstants.KEY_SP_CONFIG_ROOT_SUFFIX));
            spMap.put(TestConstants.KEY_ATT_DS_DIRMGRDN,
                    confMap.get(TestConstants.KEY_SP_DS_DIRMGRDN));
            spMap.put(TestConstants.KEY_ATT_DS_DIRMGRPASSWD,
                    confMap.get(TestConstants.KEY_SP_DS_DIRMGRPASSWORD));
            spMap.put(TestConstants.KEY_ATT_LOAD_UMS,
                    confMap.get(TestConstants.KEY_SP_LOAD_UMS));
        } catch(Exception e) {
            log(Level.SEVERE, "getSPConfigurationMap", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * This method fills map with IDP configuration data which is needed by
     * TestCommon.configureProduct method.
     */
    private void getIDPConfigurationMap(Map idpMap, Map confMap)
    throws Exception {
        try {
            idpMap.put("serverurl",confMap.get(TestConstants.KEY_IDP_PROTOCOL)
            + ":" + "//" + confMap.get(TestConstants.KEY_IDP_HOST) + ":"
                    + confMap.get(TestConstants.KEY_IDP_PORT));
            idpMap.put("serveruri",
                    confMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI));
            idpMap.put(TestConstants.KEY_ATT_COOKIE_DOMAIN,
                    confMap.get(TestConstants.KEY_IDP_COOKIE_DOMAIN));
            idpMap.put(TestConstants.KEY_ATT_CONFIG_DIR,
                    confMap.get(TestConstants.KEY_IDP_CONFIG_DIR));
            idpMap.put(TestConstants.KEY_ATT_AMADMIN_PASSWORD,
                    confMap.get(TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            idpMap.put(TestConstants.KEY_ATT_CONFIG_DATASTORE,
                    confMap.get(TestConstants.KEY_IDP_DATASTORE));
            idpMap.put(TestConstants.KEY_ATT_AM_ENC_PWD,
                confMap.get(TestConstants.KEY_IDP_ENC_KEY));
            idpMap.put(TestConstants.KEY_ATT_DIRECTORY_SERVER,
                    confMap.get(TestConstants.KEY_IDP_DIRECTORY_SERVER));
            idpMap.put(TestConstants.KEY_ATT_DIRECTORY_PORT,
                    confMap.get(TestConstants.KEY_IDP_DIRECTORY_PORT));
            idpMap.put(TestConstants.KEY_ATT_CONFIG_ROOT_SUFFIX,
                    confMap.get(TestConstants.KEY_IDP_CONFIG_ROOT_SUFFIX));
            idpMap.put(TestConstants.KEY_ATT_DS_DIRMGRDN,
                    confMap.get(TestConstants.KEY_IDP_DS_DIRMGRDN));
            idpMap.put(TestConstants.KEY_ATT_DS_DIRMGRPASSWD,
                    confMap.get(TestConstants.KEY_IDP_DS_DIRMGRPASSWORD));
            idpMap.put(TestConstants.KEY_ATT_LOAD_UMS,
                    confMap.get(TestConstants.KEY_SP_LOAD_UMS));
        } catch(Exception e) {
            log(Level.SEVERE, "getIDPConfigurationMap", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

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
 * $Id: ConfigureIDFF.java,v 1.5 2008-01-18 00:42:52 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idff;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.IDFFCommon;
import com.sun.identity.qatest.common.MultiProtocolCommon;
import com.sun.identity.qatest.common.SAMLv2Common;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * This class configures SP & IDP deployed war's if it hasn't done so.
 * Also it creates COT on both instances, loads IDFF meta on both side with
 * one as SP & one as IDP.
 */
public class ConfigureIDFF extends TestCommon {
    private WebClient spWebClient;
    private WebClient idpWebClient;
    private Map<String, String> configMap;
    private Map<String, String> spConfigMap;
    private Map<String, String> idpConfigMap;
    
    /** Creates a new instance of ConfigureIDFF */
    public ConfigureIDFF() {
        super("ConfigureIDFF");
    }
    
    /**
     * Create the webClient which should be run before each test.
     */
    private void getWebClient()
    throws Exception {
        try {
            spWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
            idpWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        } catch (Exception e) {
            log(Level.SEVERE, "getWebClient", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Configure sp & idp
     * @DocTest: IDFF|Configure SP & IDP by loading metadata on both sides.
     */
    @Parameters({"groupName"})
    @BeforeSuite(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void ConfigureIDFF(String strGroupName)
    throws Exception {
        Object[] params = {strGroupName};
        entering("ConfigureIDFF", params);
        try {
            URL url;
            HtmlPage page;
            //Upload global properties file in configMap
            configMap = new HashMap<String, String>();
            spConfigMap = new HashMap<String, String>();
            idpConfigMap = new HashMap<String, String>();
            getWebClient();
            
            log(logLevel, "ConfigureIDFF", "GroupName received from " +
                    "testng is " + strGroupName);
            configMap = getMapFromResourceBundle("idffTestConfigData");
            log(logLevel, "ConfigureIDFF", "Map:" + configMap);
            
            String spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL)
            + "://" + configMap.get(TestConstants.KEY_SP_HOST) + ":"
                    + configMap.get(TestConstants.KEY_SP_PORT)
                    + configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            String idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL)
            + "://" + configMap.get(TestConstants.KEY_IDP_HOST) + ":"
                    + configMap.get(TestConstants.KEY_IDP_PORT)
                    + configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
            
            url = new URL(spurl);
            page = (HtmlPage)spWebClient.getPage(url);
            spConfigMap = MultiProtocolCommon.getSPConfigurationMap(configMap);
            boolean spConfigResult = configureProduct(spConfigMap);
            if (spConfigResult) {
                log(logLevel, "ConfigureIDFF", spurl + "is configured" +
                        "Proceed with IDFF SP configuration");
            } else {
                log(logLevel, "ConfigureIDFF", spurl + "is not " +
                        "configured successfully. " +
                        "Exiting the IDFF configuration");
                assert false;
            }
            
            url = new URL(idpurl);
            page = (HtmlPage)idpWebClient.getPage(url);
            idpConfigMap = MultiProtocolCommon.getIDPConfigurationMap(configMap);
            boolean idpConfigResult = configureProduct(idpConfigMap);
            if (idpConfigResult) {
                log(logLevel, "ConfigureIDFF", idpurl + "is configured" +
                        "Proceed with IDFF IDP configuration");
            } else {
                log(logLevel, "ConfigureIDFF", idpurl + "is not " +
                        "configured successfully. " +
                        "Exiting the IDFF configuration");
                assert false;
            }
            
            FederationManager spfm = new FederationManager(spurl);
            FederationManager idpfm = new FederationManager(idpurl);
            
            //on sp side create cot, load sp metadata
            consoleLogin(spWebClient, spurl + "/UI/Login",
                    (String)configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    (String)configMap.get(
                    TestConstants.KEY_SP_AMADMIN_PASSWORD));
            
            HtmlPage spcotPage = spfm.listCots(spWebClient,
                    configMap.get(TestConstants.KEY_SP_REALM));
            if (!spcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_COT))) {
                spcotPage = spfm.createCot(spWebClient,
                        configMap.get(TestConstants.KEY_SP_COT),
                        configMap.get(TestConstants.KEY_SP_REALM),
                        null, null);
                if (!spcotPage.getWebResponse().getContentAsString().
                        contains("Circle of trust, "
                        + configMap.get(TestConstants.KEY_SP_COT)
                        + " is created.")) {
                    log(Level.SEVERE, "ConfigureIDFF", "Couldn't create " +
                            "COT at SP side " +
                            spcotPage.getWebResponse().getContentAsString(),
                            null);
                    assert false;
                }
            } else {
                log(logLevel, "ConfigureIDFF", "COT exists at SP side", null);
            }
            
            String spMetadata[]= {"",""};
            HtmlPage spEntityPage = spfm.listEntities(spWebClient,
                    configMap.get(TestConstants.KEY_SP_REALM), "idff");
            if (!spEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                log(logLevel, "ConfigureIDFF", "sp entity doesnt exist. " +
                        "Get template & create the entity", null);
                if (strGroupName.contains("sec")) {
                    spMetadata = MultiProtocolCommon.configureSP(spWebClient,
                            configMap, "idff", true);
                } else {
                    spMetadata = MultiProtocolCommon.configureSP(spWebClient,
                            configMap, "idff", false);
                }
                if ((spMetadata[0].equals(null)) || (spMetadata[1].
                        equals(null))) {
                    log(Level.SEVERE, "ConfigureIDFF", "Couldn't configure " +
                            "SP", null);
                    assert false;
                }
            } else {
                //If entity exists, export to get the metadata.
                HtmlPage spExportEntityPage = spfm.exportEntity(spWebClient,
                        configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_SP_REALM), false, true,
                        true, "idff");
                spMetadata[0] = MultiProtocolCommon.getMetadataFromPage(
                        spExportEntityPage);
                spMetadata[1] = MultiProtocolCommon.getExtMetadataFromPage(
                        spExportEntityPage);
            }
            spMetadata[1] = spMetadata[1].replaceAll(
                    configMap.get(TestConstants.KEY_SP_COT), "");
            log(logLevel, "ConfigureIDFF", "sp metadata" + spMetadata[0],
                    null);
            log(logLevel, "ConfigureIDFF", "sp Ext metadata" + spMetadata[1],
                    null);
            
            //idp side create cot, load idp metadata
            consoleLogin(idpWebClient, idpurl + "/UI/Login",
                    (String)configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    (String)configMap.get(
                    TestConstants.KEY_IDP_AMADMIN_PASSWORD));
            
            HtmlPage idpcotPage = idpfm.listCots(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_REALM));
            if (idpcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_COT))) {
                log(logLevel, "ConfigureIDFF", "COT exists at IDP side",
                        null);
            } else {
                idpcotPage = idpfm.createCot(idpWebClient,
                        configMap.get(TestConstants.KEY_IDP_COT),
                        configMap.get(TestConstants.KEY_IDP_REALM),
                        null, null);
                if (!idpcotPage.getWebResponse().getContentAsString().
                        contains("Circle of trust, " +
                        configMap.get(TestConstants.KEY_IDP_COT)
                        + " is created.")) {
                    log(Level.SEVERE, "ConfigureIDFF", "Couldn't create " +
                            "COT at IDP side", null);
                    assert false;
                }
            }
            
            String[] idpMetadata = {"",""};
            HtmlPage idpEntityPage = idpfm.listEntities(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_REALM), "idff");
            if (!idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                log(logLevel, "ConfigureIDFF", "idp entity doesnt exist. " +
                        "Get template & create the entity", null);
                if (strGroupName.contains("sec")) {
                    idpMetadata = MultiProtocolCommon.configureIDP(idpWebClient,
                            configMap, "idff", true);
                } else {
                    idpMetadata = MultiProtocolCommon.configureIDP(idpWebClient,
                            configMap, "idff", false);
                }
                
                log(logLevel, "ConfigureIDFF", "idp metadata" +
                        idpMetadata[0], null);
                log(logLevel, "ConfigureIDFF", "idp Ext metadata" +
                        idpMetadata[1], null);
                if ((idpMetadata[0].equals(null)) || (
                        idpMetadata[1].equals(null))) {
                    log(Level.SEVERE, "ConfigureIDFF", "Couldn't configure " +
                            "IDP", null);
                    assert false;
                }
            } else {
                //If entity exists, export to get the metadata.
                HtmlPage idpExportEntityPage = idpfm.exportEntity(idpWebClient,
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_IDP_REALM), false, true,
                        true, "idff");
                idpMetadata[0] = MultiProtocolCommon.getMetadataFromPage(
                        idpExportEntityPage);
                idpMetadata[1] = MultiProtocolCommon.getExtMetadataFromPage(
                        idpExportEntityPage);
            }
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    configMap.get(TestConstants.KEY_IDP_COT), "");
            log(logLevel, "ConfigureIDFF", "idp metadata" +
                    idpMetadata[0], null);
            log(logLevel, "ConfigureIDFF", "idp Ext metadata" +
                    idpMetadata[1], null);
            
            //load spmetadata on idp
            if (idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                log(logLevel, "ConfigureIDFF", "sp entity exists at idp. " +
                        "Delete & load the metadata ", null);
                HtmlPage spDeleteEntityPage = idpfm.deleteEntity(idpWebClient,
                        configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_SP_REALM), false,
                        "idff");
                if (spDeleteEntityPage.getWebResponse().getContentAsString().
                        contains("Descriptor is deleted for entity, " +
                        configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                    log(logLevel, "ConfigureIDFF", "Delete sp entity on " +
                            "IDP side", null);
                } else {
                    log(logLevel, "ConfigureIDFF", "Couldnt delete sp " +
                            "entity on IDP side", null);
                    assert false;
                }
            }
            spMetadata[1] = spMetadata[1].replaceAll(
                    "hosted=\"true\"", "hosted=\"false\"");
            spMetadata[1] = spMetadata[1].replaceAll(
                    "hosted=\"1\"", "hosted=\"0\"");
            HtmlPage importSPMeta = idpfm.importEntity(idpWebClient,
                    configMap.get(TestConstants.KEY_IDP_REALM), spMetadata[0],
                    spMetadata[1],
                    (String)configMap.get(TestConstants.KEY_IDP_COT), "idff");
            if (!importSPMeta.getWebResponse().getContentAsString().
                    contains("Import file, web.")) {
                log(Level.SEVERE, "ConfigureIDFF", "Couldn't import SP " +
                        "metadata on IDP side" + importSPMeta.getWebResponse().
                        getContentAsString(), null);
                assert false;
            }
            //load idpmetadata on sp
            if (spEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                log(logLevel, "ConfigureIDFF", "idp entity exists at sp. " +
                        "Delete & load the metadata ", null);
                HtmlPage idpDeleteEntityPage = spfm.deleteEntity(spWebClient,
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_IDP_REALM), false,
                        "idff");
                if (idpDeleteEntityPage.getWebResponse().getContentAsString().
                        contains("Descriptor is deleted for entity, " +
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                    log(logLevel, "ConfigureIDFF", "Delete idp entity on " +
                            "SP side", null);
                } else {
                    log(logLevel, "ConfigureIDFF", "Couldnt delete idp " +
                            "entity on SP side", null);
                    assert false;
                }
            }
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    "hosted=\"true\"", "hosted=\"false\"");
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    "hosted=\"1\"", "hosted=\"0\"");
            HtmlPage importIDPMeta = spfm.importEntity(spWebClient,
                    configMap.get(TestConstants.KEY_SP_REALM), idpMetadata[0],
                    idpMetadata[1],
                    (String)configMap.get(TestConstants.KEY_SP_COT), "idff");
            if (!importIDPMeta.getWebResponse().getContentAsString().
                    contains("Import file, web.")) {
                log(Level.SEVERE, "ConfigureIDFF", "Couldn't import IDP " +
                        "metadata on SP side" + importIDPMeta.getWebResponse().
                        getContentAsString(), null);
                assert false;
            }
            consoleLogout(spWebClient, spurl);
            consoleLogout(idpWebClient, idpurl);
        } catch (Exception e) {
            log(Level.SEVERE, "ConfigureIDFF", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("ConfigureIDFF");
    }
}

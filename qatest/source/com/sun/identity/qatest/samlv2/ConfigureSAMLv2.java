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
 * $Id: ConfigureSAMLv2.java,v 1.1 2007-05-08 16:52:42 mrudulahg Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.samlv2;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.SAMLv2Common;
import com.sun.identity.qatest.common.TestCommon;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ConfigureSAMLv2 extends TestCommon {
    private WebClient spWebClient;
    private WebClient idpWebClient;
    private Map<String, String> configMap;
    private Map<String, String> spConfigMap;
    private Map<String, String> idpConfigMap;
    public String groupName="";
    
    /** Creates a new instance of ConfigureSAMLv2 */
    public ConfigureSAMLv2() {
        super("ConfigureSAMLv2");
    }
    
    /**
     * Create the webClient which should be run before each test.
     */
    private void getWebClient() throws Exception {
        try {
            spWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
            idpWebClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        } catch(Exception e) {
            log(Level.SEVERE, "getWebClient", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Configure sp & idp
     * @DocTest: SAML2|Configure SP & IDP by loading metadata on both sides.
     */
    @Parameters({"groupName"})
    @BeforeTest(groups={"samlv2_ff", "samlv2_ds", "samlv2_ldapv3", 
    "samlv2_sec_ff", "samlv2_sec_ds", "samlv2_sec_ldapv3"})
    public void configureSAMLv2(String strGroupName)
    throws Exception {
        Object[] params = {strGroupName};
        entering("configureSAMLv2", params);
        try {
            URL url;
            HtmlPage page;
            //Upload global properties file in configMap
            configMap = new HashMap<String, String>();
            spConfigMap = new HashMap<String, String>();
            idpConfigMap = new HashMap<String, String>();
            getWebClient();
            
            log(logLevel, "configureSAMLv2", "GroupName received from " +
                    "testng is " + strGroupName);
            SAMLv2Common.getEntriesFromResourceBundle("samlv2Test", configMap);
            log(logLevel, "configureSAMLv2", "Map:" + configMap);
            
            String spurl = configMap.get("sp_proto") + "://" +
                    configMap.get("sp_host") + ":" + configMap.get("sp_port")
                    + configMap.get("sp_deployment_uri");
            String idpurl = configMap.get("idp_proto") + "://" +
                    configMap.get("idp_host") + ":" + configMap.get("idp_port")
                    + configMap.get("idp_deployment_uri");
         
            url = new URL(spurl);
            page = (HtmlPage)spWebClient.getPage(url);
            getSPConfigurationMap(spConfigMap, configMap);
            boolean spConfigResult = configureProduct(spConfigMap);
            if (spConfigResult) {
                log(logLevel, "configureSAMLv2", spurl + "is configured" +
                        "Proceed with SAMLv2 SP configuration");
            } else {
                log(logLevel, "configureSAMLv2", spurl + "is not " +
                        "configured successfully. " +
                        "Exiting the SAMLv2 configuration");
                assert false;
            }
             
            url = new URL(idpurl);
            page = (HtmlPage)idpWebClient.getPage(url);
            getIDPConfigurationMap(idpConfigMap, configMap);
            boolean idpConfigResult = configureProduct(idpConfigMap);
            if (idpConfigResult) {
                log(logLevel, "configureSAMLv2", idpurl + "is configured" +
                        "Proceed with SAMLv2 IDP configuration");
            } else {
                log(logLevel, "configureSAMLv2", idpurl + "is not " +
                        "configured successfully. " +
                        "Exiting the SAMLv2 configuration");
                assert false;
            }

            FederationManager spfm = new FederationManager(spurl);
            FederationManager idpfm = new FederationManager(idpurl);
            
            //on sp side create cot, load sp metadata
            consoleLogin(spWebClient, spurl, (String)configMap.get("sp_admin"),
                    (String)configMap.get("sp_adminpw"));
            
            HtmlPage spcotPage = spfm.listCircleOfTrusts(spWebClient,
                    configMap.get("sp_realm"),"saml2");
            if(!spcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get("sp_cot"))) {
                spcotPage = spfm.createCircleOfTrust(spWebClient,
                        configMap.get("sp_cot"), configMap.get("sp_realm"), 
                        null, null, "saml2");
                if(!spcotPage.getWebResponse().getContentAsString().
                        contains("Circle of trust, " + configMap.get("sp_cot")
                        + " is created.")) {
                    log(Level.SEVERE, "configureSAMLv2", "Couldn't create " +
                            "COT at SP side", null);
                    assert false;
                }
            } else {
                log(logLevel, "configureSAMLv2", "COT exists at SP side", null);
            }
            
            String spMetadata[]= {"",""};
            HtmlPage spEntityPage = spfm.listEntities(spWebClient,
                    configMap.get("sp_realm"), "saml2");
            if(!spEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get("sp_entity_name"))) {
                log(logLevel, "configureSAMLv2", "sp entity doesnt exist. " +
                        "Get template & create the entity", null);
                if(strGroupName.contains("sec")) {
                    spMetadata = SAMLv2Common.configureSP(spWebClient, 
                            configMap, true);
                } else {
                    spMetadata = SAMLv2Common.configureSP(spWebClient, 
                            configMap, false);
                }
                if((spMetadata[0].equals(null))||(spMetadata[1].equals(null))){
                    log(Level.SEVERE, "configureSAMLv2", "Couldn't configure " +
                            "SP", null);
                    assert false;
                }
            } else {
                //If entity exists, export to get the metadata.
                HtmlPage spExportEntityPage = spfm.exportEntity(spWebClient,
                        configMap.get("sp_entity_name"),
                        configMap.get("sp_realm"), false, true, true, "saml2");
                spMetadata[0] = SAMLv2Common.getMetadataFromPage(
                        spExportEntityPage);
                spMetadata[1] = SAMLv2Common.getExtMetadataFromPage(
                        spExportEntityPage);
            }
            spMetadata[1] = spMetadata[1].replaceAll(
                    configMap.get("sp_cot"), "");
            log(logLevel, "configureSAMLv2", "sp metadata" + spMetadata[0], 
                    null);
            log(logLevel, "configureSAMLv2", "sp Ext metadata" + spMetadata[1], 
                    null);
            
            //idp side create cot, load idp metadata
            consoleLogin(idpWebClient, idpurl, 
                    (String)configMap.get("idp_admin"),
                    (String)configMap.get("idp_adminpw"));
            
            HtmlPage idpcotPage = idpfm.listCircleOfTrusts(idpWebClient,
                    configMap.get("idp_realm"),"saml2");
            if(idpcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get("idp_cot"))) {
                log(logLevel, "configureSAMLv2", "COT exists at IDP side" +
                        idpcotPage.getWebResponse().getContentAsString(),
                        null);
            }else{
                idpcotPage = idpfm.createCircleOfTrust(idpWebClient,
                        configMap.get("idp_cot"), configMap.get("idp_realm"), 
                        null, null, "saml2");
                if(!idpcotPage.getWebResponse().getContentAsString().
                        contains("Circle of trust, " + configMap.get("idp_cot")
                        + " is created.")) {
                    log(Level.SEVERE, "configureSAMLv2", "Couldn't create " +
                            "COT at IDP side", null);
                    assert false;
                }
            }
            
            String[] idpMetadata ={"",""};
            HtmlPage idpEntityPage = idpfm.listEntities(idpWebClient,
                    configMap.get("idp_realm"), "saml2");
            if(!idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get("idp_entity_name"))) {
                log(logLevel, "configureSAMLv2", "idp entity doesnt exist. " +
                        "Get template & create the entity", null);
                if(strGroupName.contains("sec")) {
                    idpMetadata = SAMLv2Common.configureIDP(idpWebClient, 
                            configMap, true);
                } else {
                    idpMetadata = SAMLv2Common.configureIDP(idpWebClient, 
                            configMap, false);
                }
                
                log(logLevel, "configureSAMLv2", "idp metadata" +
                        idpMetadata[0], null);
                log(logLevel, "configureSAMLv2", "idp Ext metadata" +
                        idpMetadata[1], null);
                if((idpMetadata[0].equals(null))||(
                        idpMetadata[1].equals(null)))
                {
                    log(Level.SEVERE, "configureSAMLv2", "Couldn't configure " +
                            "IDP", null);
                    assert false;
                }
            } else {
                //If entity exists, export to get the metadata.
                HtmlPage idpExportEntityPage = idpfm.exportEntity(idpWebClient,
                        configMap.get("idp_entity_name"),
                        configMap.get("idp_realm"), false, true, true, "saml2");
                idpMetadata[0] = SAMLv2Common.getMetadataFromPage(
                        idpExportEntityPage);
                idpMetadata[1] = SAMLv2Common.getExtMetadataFromPage(
                        idpExportEntityPage);
            }
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    configMap.get("idp_cot"), "");
            log(logLevel, "configureSAMLv2", "idp metadata" +
                    idpMetadata[0], null);
            log(logLevel, "configureSAMLv2", "idp Ext metadata" +
                    idpMetadata[1], null);
            
            //load spmetadata on idp
            if(idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get("sp_entity_name"))) {
                log(logLevel, "configureSAMLv2", "sp entity exists at idp. " +
                        "Delete & load the metadata ", null);
                HtmlPage spDeleteEntityPage = idpfm.deleteEntity(idpWebClient,
                        configMap.get("sp_entity_name"),
                        configMap.get("sp_realm"), false, "saml2");
                if(spDeleteEntityPage.getWebResponse().getContentAsString().
                        contains("Descriptor is deleted for entity, " +
                        configMap.get("sp_entity_name"))) {
                    log(logLevel, "configureSAMLv2", "Delete sp entity on " +
                            "IDP side", null);
                } else {
                    log(logLevel, "configureSAMLv2", "Couldnt delete sp " +
                            "entity on IDP side", null);
                    assert false;
                }
            }
            spMetadata[1] = spMetadata[1].replaceAll(
                    "hosted=\"true\"", "hosted=\"false\"");
            spMetadata[1] = spMetadata[1].replaceAll(
                    "hosted=\"1\"", "hosted=\"0\"");
            HtmlPage importSPMeta = idpfm.importEntity(idpWebClient, 
                    configMap.get("idp_realm"), spMetadata[0], spMetadata[1], 
                    (String)configMap.get("idp_cot"), "saml2");
            if(!importSPMeta.getWebResponse().getContentAsString().
                    contains("Import file, web.")) {
                log(Level.SEVERE, "configureSAMLv2", "Couldn't import SP " +
                        "metadata on IDP side" + importSPMeta.getWebResponse().
                        getContentAsString(), null);
                assert false;
            }
            //load idpmetadata on sp
            if(spEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get("idp_entity_name"))) {
                log(logLevel, "configureSAMLv2", "idp entity exists at sp. " +
                        "Delete & load the metadata ", null);
                HtmlPage idpDeleteEntityPage = spfm.deleteEntity(spWebClient,
                        configMap.get("idp_entity_name"),
                        configMap.get("idp_realm"), false, "saml2");
                if(idpDeleteEntityPage.getWebResponse().getContentAsString().
                        contains("Descriptor is deleted for entity, " +
                        configMap.get("idp_entity_name"))) {
                    log(logLevel, "configureSAMLv2", "Delete idp entity on " +
                            "SP side", null);
                } else {
                    log(logLevel, "configureSAMLv2", "Couldnt delete idp " +
                            "entity on SP side", null);
                    assert false;
                }
            }
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    "hosted=\"true\"", "hosted=\"false\"");
            idpMetadata[1] = idpMetadata[1].replaceAll(
                    "hosted=\"1\"", "hosted=\"0\"");
            HtmlPage importIDPMeta = spfm.importEntity(spWebClient, 
                    configMap.get("sp_realm"), idpMetadata[0], idpMetadata[1], 
                    (String)configMap.get("sp_cot"), "saml2");
            if(!importIDPMeta.getWebResponse().getContentAsString().
                    contains("Import file, web.")) {
                log(Level.SEVERE, "configureSAMLv2", "Couldn't import IDP " +
                        "metadata on SP side" + importIDPMeta.getWebResponse().
                        getContentAsString(), null);
                assert false;
            }
            consoleLogout(spWebClient, spurl);
            consoleLogout(idpWebClient, idpurl);
        } catch (Exception e) {
            log(Level.SEVERE, "configureSAMLv2", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("configureSAMLv2");
    }
    
    /**
     * This method fills map with SP configuration data which is needed by 
     * TestCommon.configureProduct method. 
     */
    private void getSPConfigurationMap(Map spMap, Map confMap) 
    throws Exception {
        try {
             spMap.put("serverurl",confMap.get("sp_proto") + ":" + "//" + 
                     confMap.get("sp_host") + ":" + confMap.get("sp_port"));
             spMap.put("serveruri",confMap.get("sp_deployment_uri"));
             spMap.put("cookiedomain",confMap.get("sp_cookie_domain"));
             spMap.put("configdir",confMap.get("sp_config_dir"));
             spMap.put("adminPassword",confMap.get("sp_adminpw"));
             spMap.put("datastore",confMap.get("sp_datastore"));
             spMap.put("dirservername",confMap.get("sp_directory_server"));
             spMap.put("dirserverport",confMap.get("sp_directory_port"));
             spMap.put("dirserversuffixconfigdata",
                     confMap.get("sp_config_root_suffix"));
             spMap.put("dirserversuffixsmdata",
                     confMap.get("sp_sm_root_suffix"));
             spMap.put("dirserveradmindn",confMap.get("sp_ds_dirmgrdn"));
             spMap.put("dirserveradminpassword",
                     confMap.get("sp_ds_dirmgrpasswd"));
             spMap.put("dirloadums",confMap.get("sp_load_ums"));
        } catch(Exception e) {
            log(Level.SEVERE, "getspConfigurationMap", e.getMessage(), null);
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
             idpMap.put("serverurl",confMap.get("idp_proto") + ":" + "//" + 
                     confMap.get("idp_host") + ":" + confMap.get("idp_port"));
             idpMap.put("serveruri",confMap.get("idp_deployment_uri"));
             idpMap.put("cookiedomain",confMap.get("idp_cookie_domain"));
             idpMap.put("configdir",confMap.get("idp_config_dir"));
             idpMap.put("adminPassword",confMap.get("idp_adminpw"));
             idpMap.put("datastore",confMap.get("idp_datastore"));
             idpMap.put("dirservername",confMap.get("idp_directory_server"));
             idpMap.put("dirserverport",confMap.get("idp_directory_port"));
             idpMap.put("dirserversuffixconfigdata",
                     confMap.get("idp_config_root_suffix"));
             idpMap.put("dirserversuffixsmdata",
                     confMap.get("idp_sm_root_suffix"));
             idpMap.put("dirserveradmindn",confMap.get("idp_ds_dirmgrdn"));
             idpMap.put("dirserveradminpassword",
                     confMap.get("idp_ds_dirmgrpasswd"));
             idpMap.put("dirloadums",confMap.get("idp_load_ums"));
        } catch(Exception e) {
            log(Level.SEVERE, "getidpConfigurationMap", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
    }
}

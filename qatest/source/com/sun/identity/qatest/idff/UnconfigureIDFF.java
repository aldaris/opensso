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
 * $Id: UnconfigureIDFF.java,v 1.3 2007-08-07 23:35:22 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idff;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.testng.annotations.AfterSuite;

/**
 * This class removes the configuration on SP & IDP 
 * It removes SP & IDP entities on both sides, and also removes the COT. 
 */
public class UnconfigureIDFF extends TestCommon {
    private WebClient webClient;
    private Map<String, String> configMap;
    private String spurl;
    private String idpurl;
    
    /** Creates a new instance of UnconfigureIDFF */
    public UnconfigureIDFF () {
        super("UnconfigureIDFF");
    }
    
    /**
     * Create the webClient which should be run before each test.
     */
    private void getWebClient() 
    throws Exception {
        try {
            webClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        } catch (Exception e) {
            log (Level.SEVERE, "getWebClient", e.getMessage(), null);
            e.printStackTrace ();
            throw e;
        }
    }
    
    /**
     * Configure sp & idp
     * @DocTest: IDFF|Unconfigure SP & IDP by deleting entities & COT's 
     */
    @AfterSuite (groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void UnconfigureIDFF()
    throws Exception {
        entering("UnconfigureIDFF", null);
        String spurl;
        String idpurl;
        try {
            //Upload global properties file in configMap
            configMap = new HashMap<String, String>();
            getWebClient();
            
            configMap = getMapFromResourceBundle("idffTestConfigData");
            log (logLevel, "UnconfigureIDFF", "Map:" + configMap);
            
            spurl = configMap.get(TestConstants.KEY_SP_PROTOCOL) + "://" + 
                    configMap.get(TestConstants.KEY_SP_HOST) + ":" + 
                    configMap.get(TestConstants.KEY_SP_PORT) + 
                    configMap.get(TestConstants.KEY_SP_DEPLOYMENT_URI);
            idpurl = configMap.get(TestConstants.KEY_IDP_PROTOCOL) + "://" + 
                    configMap.get(TestConstants.KEY_IDP_HOST) + ":" + 
                    configMap.get(TestConstants.KEY_IDP_PORT) + 
                    configMap.get(TestConstants.KEY_IDP_DEPLOYMENT_URI);
            
            consoleLogin (webClient, spurl,
                    (String)configMap.get(TestConstants.KEY_SP_AMADMIN_USER),
                    (String)configMap.get(
                    TestConstants.KEY_SP_AMADMIN_PASSWORD));
            consoleLogin (webClient, idpurl,
                    (String)configMap.get(TestConstants.KEY_IDP_AMADMIN_USER),
                    (String)configMap.get(
                    TestConstants.KEY_IDP_AMADMIN_PASSWORD));
        } catch (Exception e) {
            log (Level.SEVERE, "UnconfigureIDFF", e.getMessage(), null);
            e.printStackTrace ();
            throw e;
        }
        
        try{
            FederationManager spfm = new FederationManager(spurl);
            FederationManager idpfm = new FederationManager(idpurl);
            
            HtmlPage idpEntityPage = idpfm.listEntities(webClient,
                    configMap.get (TestConstants.KEY_IDP_REALM), "idff");
            //Delete IDP & SP entities on IDP
            if (idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                log (logLevel, "UnconfigureIDFF", "IDP entity exists at IDP. ",
                        null);
                HtmlPage idpDeleteEntityPage = idpfm.deleteEntity (webClient,
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_IDP_REALM), false,
                        "idff");
                if (idpDeleteEntityPage.getWebResponse().getContentAsString().
                        contains("Descriptor is deleted for entity, " +
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                    log (logLevel, "UnconfigureIDFF", "Delete IDP entity on " +
                            "IDP side", null);
                } else {
                    log (logLevel, "UnconfigureIDFF", "Couldnt delete IDP " +
                            "entity on IDP side", null);
                    assert false;
                }
            }
            
            if (idpEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                log (logLevel, "UnconfigureIDFF", "SP entity exists at IDP. ",
                        null);
                HtmlPage spDeleteEntityPage = idpfm.deleteEntity (webClient,
                        configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_IDP_REALM), false,
                        "idff");
                if (spDeleteEntityPage.getWebResponse().getContentAsString().
                        contains ("Descriptor is deleted for entity, " +
                        configMap.get (TestConstants.KEY_SP_ENTITY_NAME))) {
                    log (logLevel, "UnconfigureIDFF", "Deleted SP entity on " +
                            "IDP side", null);
                } else {
                    log (logLevel, "UnconfigureIDFF", "Couldnt delete SP " +
                            "entity on IDP side", null);
                    assert false;
                }
            }
            
            //Delete COT on IDP side.
            HtmlPage idpcotPage = idpfm.listCircleOfTrusts(webClient,
                    configMap.get(TestConstants.KEY_IDP_REALM));
            if (idpcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_COT))) {
                log (logLevel, "UnconfigureIDFF", "COT exists at IDP side",
                        null);
                idpcotPage = idpfm.deleteCircleOfTrust(webClient,
                        configMap.get(TestConstants.KEY_IDP_COT),
                        configMap.get(TestConstants.KEY_IDP_REALM));
                if (!idpcotPage.getWebResponse().getContentAsString().
                        contains("Circle of trust, " +
                        configMap.get(TestConstants.KEY_IDP_COT)
                        + " is deleted.")) {
                    log (logLevel, "UnconfigureIDFF", "Couldn't delete " +
                            "COT at IDP side" +
                            idpcotPage.getWebResponse().getContentAsString(), 
                            null);
                } else {
                    log (logLevel, "UnconfigureIDFF", "Deleted COT " +
                            "at IDP side", null);                    
                }
            }
            
            HtmlPage spEntityPage = spfm.listEntities (webClient,
                    configMap.get (TestConstants.KEY_SP_REALM), "idff");
            //Delete SP & IDP entities on sp
            if (spEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                log (logLevel, "UnconfigureIDFF", "SP entity exists at SP. ",
                        null);
                HtmlPage spDeleteEntityPage = spfm.deleteEntity(webClient,
                        configMap.get(TestConstants.KEY_SP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_SP_REALM), false,
                        "idff");
                if (spDeleteEntityPage.getWebResponse().getContentAsString().
                        contains("Descriptor is deleted for entity, " +
                        configMap.get(TestConstants.KEY_SP_ENTITY_NAME))) {
                    log (logLevel, "UnconfigureIDFF", "Deleted SP entity on " +
                            "SP side", null);
                } else {
                    log (logLevel, "UnconfigureIDFF", "Couldnt delete idp " +
                            "entity on SP side", null);
                    assert false;
                }
            }
            
            if (spEntityPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                log (logLevel, "UnconfigureIDFF", "IDP entity exists at SP. ", 
                        null);
                HtmlPage idpDeleteEntityPage = spfm.deleteEntity(webClient,
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME),
                        configMap.get(TestConstants.KEY_SP_REALM), false,
                        "idff");
                if (idpDeleteEntityPage.getWebResponse().getContentAsString ().
                        contains("Descriptor is deleted for entity, " +
                        configMap.get(TestConstants.KEY_IDP_ENTITY_NAME))) {
                    log (logLevel, "UnconfigureIDFF", "Deleted IDP entity on " +
                            "SP side", null);
                } else {
                    log (logLevel, "UnconfigureIDFF", "Couldnt delete IDP " +
                            "entity on SP side", null);
                    assert false;
                }
            }
            
            //Delete COT on sp side.
            HtmlPage spcotPage = spfm.listCircleOfTrusts(webClient,
                    configMap.get(TestConstants.KEY_SP_REALM));
            if (spcotPage.getWebResponse().getContentAsString().
                    contains(configMap.get(TestConstants.KEY_SP_COT))) {
                spcotPage = spfm.deleteCircleOfTrust (webClient,
                        configMap.get(TestConstants.KEY_SP_COT),
                        configMap.get(TestConstants.KEY_SP_REALM));
                if (!spcotPage.getWebResponse().getContentAsString().
                        contains ("Circle of trust, "
                        + configMap.get (TestConstants.KEY_SP_COT)
                        + " is deleted.")) {
                    log (logLevel, "UnconfigureIDFF", "Couldn't delete " +
                            "COT at SP side" +
                            spcotPage.getWebResponse().getContentAsString(), 
                            null);
                    assert false;
                } else {
                    log (logLevel, "UnconfigureIDFF", "Deleted COT " +
                            "at SP side", null);                    
                }
            }
        } catch (Exception e) {
            log (Level.SEVERE, "UnconfigureIDFF", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, spurl);
            consoleLogout(webClient, idpurl);
        }
        exiting ("UnconfigureIDFF");
    }
}

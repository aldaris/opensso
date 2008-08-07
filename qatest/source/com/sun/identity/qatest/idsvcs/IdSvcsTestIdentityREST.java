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
 * $Id: IdSvcsTestIdentityREST.java,v 1.2 2008-08-07 20:52:43 vimal_67 Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idsvcs;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.IdSvcsCommon;
import com.sun.identity.qatest.common.TestCommon;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class contains generic methods to test Identity REST service interfaces. 
 * It retrieves the parameters and attributes from the 
 * properties file and generates two maps. These maps are passed on to the 
 * common methods to call the REST operations like create, search, read, update
 * delete, isTokenValid, authenticate and attributes
 */
public class IdSvcsTestIdentityREST extends TestCommon {

    private ResourceBundle rb_amconfig;
    private ResourceBundle rbid;
    private TextPage page;
    private IdSvcsCommon idsvcsc;
    private WebClient webClient;
    private String idsProp = "IdSvcsTestIdentityREST";
    private int index;
    private String strTestRealm;
    private String strSetup;
    private String strCleanup;
    private String admToken = "";
    private String userToken = "";
        
    /**
     * Class constructor Definition
     */
    public IdSvcsTestIdentityREST()
            throws Exception {
        super("IdSvcsTestIdentityREST");
        rb_amconfig = ResourceBundle.getBundle("AMConfig");
        strTestRealm = rb_amconfig.getString("execution_realm");
        rbid = ResourceBundle.getBundle("idsvcs" + 
                fileseparator + idsProp);
        idsvcsc = new IdSvcsCommon();
    }
    
    /**
     * Creates required setup
     */
    @Parameters({"testNumber", "setup", "cleanup"})
    @BeforeClass(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void setup(String testNumber, String setup, 
            String cleanup) throws Exception {
        Object[] params = {testNumber, setup, cleanup};
        entering("setup", params);
        try {
            index = new Integer(testNumber).intValue();
            strSetup = setup;
            strCleanup = cleanup;
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("setup");
    }

    /**
     * Calling Identity REST Operations through common URL method
     */
    @Test(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testIdSvcsREST()
            throws Exception {
        entering("testIdSvcsREST", null);
        try {
            int i = 0;
            int operations = 0;
            webClient = new WebClient();
            admToken = idsvcsc.authenticateREST(adminUser, adminPassword); 
                operations = new Integer(rbid.getString(idsProp + index + 
                        "." + "operations")).intValue();
                String description = rbid.getString(idsProp + index + "." +
                        "description");
                String expResult = rbid.getString(idsProp + index + "." +
                        "expectedresult");
                Reporter.log("TestCase ID: " + idsProp + index);
                Reporter.log("Test description: " + description);
                Reporter.log("Expected Result: " + expResult);
                
                while (i < operations) {
                    String operationName = rbid.getString(idsProp + index +
                            "." + "operation" + i + "." + "name"); 
                    Reporter.log("Operation: " + operationName);
                    if (operationName.equals("create")) {
                        Map anmap = new HashMap(); 
                        Map pmap = new HashMap();  
                        String identity_name = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "identity_name");
                        String identity_type = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "identity_type");
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "attributes");
                        pmap.put("identity_name", identity_name);
                        pmap.put("identity_type", identity_type);
                        pmap.put("identity_realm", 
                                URLEncoder.encode(strTestRealm));
                        anmap = getAttributes(attributes);
                        page = idsvcsc.commonURLREST(operationName, pmap, 
                                anmap, admToken);
                        log(Level.FINEST, "testIdSvcsREST - Create", "Page: " +
                                page.getContent());
                        Reporter.log("Create Identity: " + identity_name); 
                        Reporter.log("Type: " + identity_type);
                                                           
                    } else if (operationName.equals("search")) {
                        Map anmap = new HashMap();    
                        Map pmap = new HashMap();     
                        String filter = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "filter");
                        String attributes = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "attributes");
                        String exist = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "exist");
                        pmap.put("filter", filter);
                        anmap = getAttributes(attributes);
                        page = idsvcsc.commonURLREST(operationName, pmap, 
                                anmap, admToken);
                        log(Level.FINEST, "testIdSvcsREST - Search", "Page: " +
                                page.getContent());
                        
                        // search filter contains "*"
                        if (filter.contains("*")) {
                            String identities = rbid.getString(idsProp + 
                                    index + "." + "operation" + i +
                                    "." + "identities");
                            String identity_type = rbid.getString(idsProp + 
                                    index + "." + "operation" + i + 
                                    "." + "identity_type");
                            String[] iden = getArrayOfString(identities);
                            if (exist.equals("yes")) {
                                idsvcsc.commonSearchREST(identity_type, 
                                        admToken, page, filter, 
                                        iden, Boolean.TRUE);
                            } else {
                                idsvcsc.commonSearchREST(identity_type, 
                                        admToken, page, filter,
                                        iden, Boolean.FALSE);
                            }                                        
                        } 
                        
                        // search filter does not contain "*"
                        else {
                            String str = page.getContent();
                            filter = filter + "\n";
                            if (exist.equals("yes")) {
                                if (!str.contains(filter)) {
                                    log(Level.SEVERE, "testIdSvcsREST - Search", 
                                        "Identity does not exists: " + filter);
                                    assert false; 
                                }
                            } else {
                                if (str.contains(filter)) {
                                    log(Level.SEVERE, "testIdSvcsREST - Search", 
                                        "Identity exists: " + filter);
                                    assert false; 
                                }
                            } 
                        }
                    } else if (operationName.equals("read")) {
                        Map anmap = new HashMap();   
                        Map pmap = new HashMap();    
                        String identity_name = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "identity_name");
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "attributes");
                        anmap = getAttributes(attributes);
                        pmap.put("name", identity_name);
                        page = idsvcsc.commonURLREST(operationName, pmap, 
                                anmap, admToken);
                        log(Level.FINEST, "testIdSvcsREST - Read", "Page: " +
                                page.getContent());
                        Reporter.log("Read Attributes: " + identity_name);
                    
                    } else if (operationName.equals("update")) {
                        Map anmap = new HashMap();    
                        Map pmap = new HashMap();     
                        String identity_name = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "identity_name");
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "attributes");
                        anmap = getAttributes(attributes);
                        pmap.put("identity_name", identity_name);
                        page = idsvcsc.commonURLREST(operationName, pmap, 
                                anmap, admToken);
                        log(Level.FINEST, "testIdSvcsREST - Update", "Page: " +
                                page.getContent());
                        Reporter.log("Update Attributes: " + identity_name);
                    
                    } else if (operationName.equals("delete") &&
                            strCleanup.equals("false")) {
                        Map anmap = new HashMap(); 
                        Map pmap = new HashMap();  
                        String identity_name = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "identity_name");
                        String identity_type = rbid.getString(idsProp +
                                index + "." + "operation" + i + 
                                "." + "identity_type");
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "attributes");
                        anmap = getAttributes(attributes);
                        pmap.put("identity_name", identity_name);
                        pmap.put("identity_type", identity_type);
                        page = idsvcsc.commonURLREST(operationName, pmap, 
                                anmap, admToken);
                        log(Level.FINEST, "testIdSvcsREST - Delete", "Page: " +
                                page.getContent());
                        Reporter.log("Delete Identity: " + identity_name); 
                        Reporter.log("Type: " + identity_type);
                    
                    } else if (operationName.equals("isTokenValid")) {
                        Map anmap = new HashMap();
                        Map pmap = new HashMap(); 
                        String attributes = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "attributes");
                        anmap = getAttributes(attributes);
                        String paramName = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "parameter_name");
                        String userType = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "parameter_name");
                        if (userType.equals("normaluser")) {
                            if (paramName.equals("tokenid")) { 
                                pmap.put("tokenid", 
                                        URLEncoder.encode(userToken, "UTF-8"));
                            } else {
                                pmap.put("iPlanetDirectoryPro", 
                                        URLEncoder.encode(userToken, "UTF-8"));
                            }
                            page = idsvcsc.commonURLREST(operationName, pmap, 
                                anmap, userToken);
                            
                            // Releasing user token
                            idsvcsc.commonLogOutREST(userToken);
                        } else {
                            if (paramName.equals("tokenid")){ 
                                pmap.put("tokenid", 
                                        URLEncoder.encode(admToken, "UTF-8"));
                            } else {
                                pmap.put("iPlanetDirectoryPro",
                                        URLEncoder.encode(admToken, "UTF-8"));
                            }
                            page = idsvcsc.commonURLREST(operationName, pmap, 
                                anmap, admToken);
                        }
                        log(Level.FINEST, "testIdSvcsREST - isTokenValid",
                                "Page: " + page.getContent());
                        Reporter.log("Identity: " + userType); 
                        Reporter.log("isTokenvalid: " + paramName);
                        String str = "boolean=true" + "\n";
                        log(Level.FINEST, "testIdSvcsREST", "Page: " +
                            page.getContent());
                        if (!page.getContent().equals(str)) 
                            assert false;
                                         
                    } else if (operationName.equals("authenticate")) {
                        String username = rbid.getString(idsProp + index + 
                                "." + "operation" + i + "." + "username");
                        String password = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "password");
                        userToken = idsvcsc.authenticateREST(username, 
                                password);
                        Reporter.log("Username: " + username);
                        Reporter.log("Password: " + password);
                    } else if (operationName.equals("attributes")) {
                        Map anmap = new HashMap(); 
                        Map pmap = new HashMap();  
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "attributes");
                        anmap = getAttributes(attributes);
                        page = idsvcsc.commonURLREST(operationName, pmap, 
                                anmap, admToken);
                        log(Level.FINEST, "testIdSvcsREST - Attributes", 
                                "Page: " + page.getContent());
                    } else {
                        log(Level.FINEST, "testIdSvcsREST", 
                                "Not a Valid REST Operation");
                    }
                    i++;
                }
                
        } catch (Exception e) {
            log(Level.SEVERE, "testIdSvcsREST", e.getMessage());
            e.printStackTrace();
            cleanup();
            throw e;
        } finally {
            
            //Releasing adminUser token
            idsvcsc.commonLogOutREST(admToken);
        }
        exiting("testIdSvcsREST");
    }
        
    /**
     * Cleanup method. This method:
     * (a) Delete users
     * (b) Deletes policies
     */
    @AfterClass(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void cleanup()
            throws Exception {
        entering("cleanup", null);
        try {
            admToken = idsvcsc.authenticateREST(adminUser, adminPassword);
            if (strCleanup.equals("true")) {
                int i = 0;
                int operations = 0;
                operations = new Integer(rbid.getString(idsProp + index +
                        "." + "operations")).intValue();
                while (i < operations) {
                    String operationName = rbid.getString(idsProp + index + 
                            "." + "operation" + i + "." + "name"); 
                    Map anmap = new HashMap();
                    Map pmap = new HashMap(); 
                    String identity_name = rbid.getString(idsProp + index +
                            "." + "operation" + i + "." + "identity_name");
                    String identity_type = rbid.getString(idsProp + index + 
                            "." + "operation" + i + "." + "identity_type");
                    String attributes = rbid.getString(idsProp + index +
                            "." + "operation" + i + "." + "attributes");
                    anmap = getAttributes(attributes);
                    pmap.put("identity_name", identity_name);
                    pmap.put("identity_type", identity_type);
                    page = idsvcsc.commonURLREST(operationName, pmap, 
                       anmap, admToken); 
                    i++;
                }
            }
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
                        
            //Releasing adminUser token
            idsvcsc.commonLogOutREST(admToken);
        }
        exiting("cleanup");
    }
    
    /**
     * Get Attributes
     */
    private Map getAttributes(String atts){
        String token = "";
        Map mp = new HashMap();
        StringTokenizer strTokenComma = new StringTokenizer(atts, ",");
        while (strTokenComma.hasMoreTokens()){
            token = strTokenComma.nextToken();
            String akey = token.substring(0, token.indexOf("="));
            String avalue = token.substring(token.indexOf("=") + 1, 
                    token.length());
            mp.put(akey, avalue);
        }
        return mp;
    }
        
}

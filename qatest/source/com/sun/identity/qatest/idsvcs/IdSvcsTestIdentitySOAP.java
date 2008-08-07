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
 * $Id: IdSvcsTestIdentitySOAP.java,v 1.1 2008-08-07 20:54:30 vimal_67 Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idsvcs;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.IdSvcsCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.idsvcs.Attribute;
import com.sun.identity.qatest.idsvcs.IdentityDetails;
import com.sun.identity.qatest.idsvcs.IdentityServicesImpl;
import com.sun.identity.qatest.idsvcs.IdentityServicesImplService_Impl;
import com.sun.identity.qatest.idsvcs.Token;
import com.sun.identity.qatest.idsvcs.UserDetails;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class contains generic methods to test Identity SOAP service interfaces. 
 * It retrieves the parameters and attributes from the properties file and is 
 * passed in to the SOAP operations like create, search, read, update, delete,
 * isTokenValid, authenticate and attributes
 */
public class IdSvcsTestIdentitySOAP extends TestCommon {

    private IdentityServicesImplService_Impl service;
    private IdentityServicesImpl isimpl;
    private ResourceBundle rb_amconfig;
    private ResourceBundle rbid;
    private String admTokenSOAP = "";
    private String idsProp = "IdSvcsTestIdentitySOAP";
    private String strCleanup;
    private String strSetup;
    private String strTestRealm;
    private TextPage page;
    private Token userTokenSOAP = null;
    private WebClient webClient;
    private int index;
      
    /**
     * Class constructor Definition
     */
    public IdSvcsTestIdentitySOAP()
            throws Exception {
        super("IdSvcsTestIdentitySOAP");
        rb_amconfig = ResourceBundle.getBundle("AMConfig");
        strTestRealm = rb_amconfig.getString("execution_realm");
        rbid = ResourceBundle.getBundle("idsvcs" + 
                fileseparator + idsProp);
    }
    
    /**
     * Setup method takes three parameters testNumber, setup and cleanup and 
     * initializes the variables and Identity Services Implementation class 
     * objects
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
            service = new IdentityServicesImplService_Impl();
            isimpl = service.getIdentityServicesImplPort();
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("setup");
    }

    /**
     * This method calls the SOAP operations create, search, read, update, 
     * delete, isTokenValid, authenticate and attributes through Identity 
     * Service Implementation class. Each operation takes arguments from the 
     * parameters defined in the properties file
     */
    @Test(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testIdSvcsSOAP()
            throws Exception {
        entering("testIdSvcsSOAP", null);
        
        // authenticate using admin user
        Token token = isimpl.authenticate(adminUser, adminPassword, 
                        "realm=" + strTestRealm);
        try {
            int i = 0;
            int operations = 0;
            webClient = new WebClient();
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
                        IdentityDetails identity = new IdentityDetails();
                        Attribute[] attrArray = null;
                        String identity_name = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "identity_name");
                        String identity_type = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "identity_type");
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "attributes");
                        Reporter.log("Create Identity: " + identity_name); 
                        Reporter.log("Type: " + identity_type);
                        identity.setName(identity_name);
                        identity.setType(identity_type);
                        identity.setRealm(strTestRealm);
                        attrArray = getAttributes(attributes);
                        identity.setAttributes(attrArray);      
                        isimpl.create(identity, token);
                        log(Level.FINEST, "testIdSvcsSOAP", operationName);
                                                                               
                    } else if (operationName.equals("search")) {
                        IdentityDetails identity = new IdentityDetails();
                        Attribute[] attrArray = null; 
                        String searchArrayResult = "";
                        String filter = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "filter");
                        String attributes = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "attributes");
                        String exist = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "exist");
                        Reporter.log("Search filter: " + filter); 
                        attrArray = getAttributes(attributes);
                        String[] searchArray = isimpl.search(filter, attrArray,
                                token);
                        for (int t = 0; t < searchArray.length; t++) {
                            if (t == searchArray.length -1) {
                                searchArrayResult = searchArrayResult + 
                                    searchArray[t];
                            } else {
                                searchArrayResult = searchArrayResult + 
                                    searchArray[t] + ",";
                            }
                        }
                        log(Level.FINEST, "testIdSvcsSOAP" + operationName, 
                                    searchArrayResult);
                        
                        // verifying search if filter contains "*"
                        if (filter.contains("*")) {
                            String identities = rbid.getString(idsProp +
                                    index + "." + "operation" + i +
                                    "." + "identities");
                            String identity_type = rbid.getString(idsProp + 
                                    index + "." + "operation" + i + 
                                    "." + "identity_type");
                            String[] iden = getArrayOfString(identities);
                            if (exist.equals("yes")) {
                                for (int p = 0; p < iden.length; p++) {
                                    log(Level.FINEST, "testIdSvcsSOAP" + 
                                        operationName, iden[p]);
                                    if (!searchArrayResult.contains(iden[p]))
                                        assert false;   
                                }
                            } else {
                                for (int p=0; p < iden.length; p++) {
                                    log(Level.FINEST, "testIdSvcsSOAP" + 
                                        operationName, iden[p]);
                                    if (searchArrayResult.contains(iden[p]))
                                        assert false;   
                                }
                            }
                        } 
                        
                        // verifying search if filter does not contain "*"
                        else {
                            if (exist.equals("yes")) {
                                if (!filter.equals(searchArrayResult))
                                        assert false;   
                            } else {
                                if (filter.equals(searchArrayResult))
                                        assert false;   
                            }
                        } 
                                                                     
                    } else if (operationName.equals("read")) {
                        Attribute[] attrArray = null;
                        String identity_name = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "identity_name");
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "attributes");
                        Reporter.log("Read Attributes: " + identity_name);
                        attrArray = getAttributes(attributes);
                        IdentityDetails id = isimpl.read(identity_name,
                                attrArray, token);
                        log(Level.FINEST, "testIdSvcsSOAP",
                                  operationName);
                        if (!id.getName().equals(identity_name)) 
                            assert false;
                        
                        // checking each attribute
                        Attribute[] attr = id.getAttributes();
                        for (int m = 0; m < attr.length; m++) {
                            log(Level.FINEST, "testIdSvcsSOAP",
                                "Attribute name: " + attr[m].getName());
                            String attrKey = attr[m].getName();
                            String attrValues = "";
                            String[] vals = attr[m].getValues();
                            for (int n = 0; n < vals.length; n++) {
                                log(Level.FINEST, "testIdSvcsSOAP",
                                "Attribute value: " + vals[n]);
                                if (n == vals.length - 1){
                                    attrValues = attrValues + vals[n];
                                } else {
                                    attrValues = attrValues + vals[n] + "&";
                                }
                            }
                            String attrResult = attrKey + "=" + attrValues;
                            log(Level.FINEST, "testIdSvcsSOAP",
                                "Attribute Result: " + attrResult);
                            if (!attributes.contains(attrResult)) 
                                assert false;
                        }
                                            
                    } else if (operationName.equals("update")) {
                        IdentityDetails identity = new IdentityDetails();
                        Attribute[] attrArray = null; 
                        String identity_name = rbid.getString(idsProp + 
                                index + "." + "operation" + i +
                                "." + "identity_name");
                        String identity_type = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "identity_type");
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "attributes");
                        Reporter.log("Update Attributes: " + identity_name);
                        identity.setName(identity_name);
                        identity.setType(identity_type);
                        identity.setRealm(strTestRealm);
                        attrArray = getAttributes(attributes);
                        identity.setAttributes(attrArray);      
                        isimpl.update(identity, token);
                        log(Level.FINEST, "testIdSvcsSOAP", operationName);
                                            
                    } else if (operationName.equals("delete") &&
                            strCleanup.equals("false")) {
                        IdentityDetails identity = new IdentityDetails();
                        String identity_name = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "identity_name");
                        String identity_type = rbid.getString(idsProp +
                                index + "." + "operation" + i + 
                                "." + "identity_type");
                        Reporter.log("Delete Identity: " + identity_name); 
                        Reporter.log("Type: " + identity_type);
                        identity.setName(identity_name);
                        identity.setType(identity_type);
                        isimpl.delete(identity, token);
                        log(Level.FINEST, "testIdSvcsSOAP", operationName);
                                            
                    } else if (operationName.equals("isTokenValid")) {
                        Boolean bool = false;
                        String paramName = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "parameter_name");
                        String userType = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "parameter_name");
                        Reporter.log("Identity: " + userType); 
                        Reporter.log("isTokenvalid: " + paramName);
                        if (userType.equals("normaluser")) {
                            bool = isimpl.isTokenValid(userTokenSOAP);
                            
                            // releasing token
                            isimpl.logout(userTokenSOAP);
                        } else {
                            bool = isimpl.isTokenValid(token);
                        }
                        log(Level.FINEST, "testIdSvcsSOAP" + 
                                operationName, bool);
                        if (!bool) 
                            assert false;
                                         
                    } else if (operationName.equals("authenticate")) {
                        String username = rbid.getString(idsProp + index + 
                                "." + "operation" + i + "." + "username");
                        String password = rbid.getString(idsProp + index +
                                "." + "operation" + i + "." + "password");
                        Reporter.log("Username: " + username);
                        Reporter.log("Password: " + password);
                        userTokenSOAP = isimpl.authenticate(username, password,
                                "realm=" + strTestRealm);
                        log(Level.FINEST, "testIdSvcsSOAP",
                                operationName);
                        String tokenString = userTokenSOAP.getId();
                        log(Level.FINEST, "testIdSvcsSOAP", "Token ID: " +
                                tokenString);
                                                
                    } else if (operationName.equals("attributes")) {
                        String identity_name = rbid.getString(idsProp + index + 
                                "." + "operation" + i + "." + "identity_name");
                        String userpassword = rbid.getString(idsProp + index + 
                                "." + "operation" + i + "." + "userpassword");
                        Token attrUserToken = isimpl.authenticate(identity_name,
                                userpassword, "realm=" + strTestRealm);
                        Reporter.log("Username: " + identity_name);
                        Reporter.log("Password: " + userpassword);
                        String tokID = attrUserToken.getId();
                        log(Level.FINEST, "testIdSvcsSOAP",
                                "Token ID: " + tokID);
                        Reporter.log("Token String: " + tokID);
                        String attributes = rbid.getString(idsProp + 
                                index + "." + "operation" + i + 
                                "." + "attributes");
                        String[] attributeNames = getAttributesStr(attributes);
                        log(Level.FINEST, "testIdSvcsSOAP",
                                  attributeNames);
                        UserDetails ud = isimpl.attributes(attributeNames, 
                                attrUserToken);
                        log(Level.FINEST, "testIdSvcsSOAP",
                                  operationName);
                        Attribute[] attr = ud.getAttributes();
                        for (int m = 0; m < attr.length; m++) {
                            log(Level.FINEST, "testIdSvcsSOAP",
                                "Attribute name: " + attr[m].getName());
                            String[] vals = attr[m].getValues();
                            for (int n = 0; n < vals.length; n++) {
                                log(Level.FINEST, "testIdSvcsSOAP",
                                "Attribute value: " + vals[n]);
                                if (vals[n].indexOf(identity_name + 
                                        "_alias") == -1) 
                                    assert false;
                            }
                        }
                        
                        // releasing attribute user token
                        isimpl.logout(attrUserToken);
                        
                    } else {
                        log(Level.FINEST, "testIdSvcsSOAP", 
                                "Not a Valid SOAP Operation");
                    }
                    i++;
                }
                
        } catch (Exception e) {
            log(Level.SEVERE, "testIdSvcsSOAP", e.getMessage());
            e.printStackTrace();
            cleanup();
            throw e;
        } finally {
            
            // releasing admin user token
            isimpl.logout(token);
        }
        exiting("testIdSvcsSOAP");
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
        
        //authenticate using admin user
        Token admTokenSOAP = isimpl.authenticate(adminUser, adminPassword,
                "realm=" + strTestRealm);
        try {
            if (strCleanup.equals("true")) {
                int i = 0;
                int operations = 0;
                operations = new Integer(rbid.getString(idsProp + index +
                        "." + "operations")).intValue();
                while (i < operations) {
                    IdentityDetails identity = new IdentityDetails();
                    String identity_name = rbid.getString(idsProp + index +
                            "." + "operation" + i + "." + "identity_name");
                    String identity_type = rbid.getString(idsProp + index + 
                            "." + "operation" + i + "." + "identity_type");
                    Reporter.log("Delete Identity: " + identity_name); 
                    Reporter.log("Type: " + identity_type);
                    identity.setName(identity_name);
                    identity.setType(identity_type);
                    isimpl.delete(identity, admTokenSOAP);
                    log(Level.FINEST, "testIdSvcsSOAP", "delete");
                    i++;
                }
            }
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
                        
            // releasing admin user token
            isimpl.logout(admTokenSOAP);
        }
        exiting("cleanup");
    }
    
    /**
     * Get Attributes as Array of Attribute
     */
    private Attribute[] getAttributes(String attString){
        String token = "";
        Attribute[] ret = null;
        List attributeList = null;
        StringTokenizer strTokenComma = new StringTokenizer(attString, ",");
        while (strTokenComma.hasMoreTokens()){
            token = strTokenComma.nextToken();
            String akey = token.substring(0, token.indexOf("="));
            String avalue = token.substring(token.indexOf("=") + 1, 
                    token.length());
            
            // tokenizing the multiple values
            StringTokenizer strTokenAmp = new StringTokenizer(avalue, "&");
            String[] avalues = new String[strTokenAmp.countTokens()];
            int i = 0;
            while (strTokenAmp.hasMoreTokens()) {
                avalues[i] = strTokenAmp.nextToken();
                i++;
            }
            Attribute attribute = new Attribute();
            attribute.setName(akey); 
            attribute.setValues(avalues); 
                if (attributeList == null) {
                    attributeList = new ArrayList();
                }
            attributeList.add(attribute);
        }
        ret = new Attribute[attributeList.size()]; 
        attributeList.toArray(ret); 
        return ret;
    }
       
}

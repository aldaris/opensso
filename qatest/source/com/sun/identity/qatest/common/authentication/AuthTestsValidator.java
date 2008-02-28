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
 * $Id: AuthTestsValidator.java,v 1.5 2008-02-28 04:04:25 inthanga Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.common.authentication;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.authentication.CreateTestXML;
import com.sun.identity.qatest.common.webtest.DefaultTaskHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.testng.annotations.Test;

/**
 * <code>AuthTestsValidator</code> is a helper class to validate the given
 * Test by logging into the System and checks the success
 * and failure by checking the appropiate goto URLs in each case
 * The validation is performed by <code>WebTest</code>
 *
 */
public class AuthTestsValidator extends TestCommon {

    private Map mapValidate;
    private WebClient webClient;
    private String baseDir;
    private String testURL;
    private String testLogoutURL;
    private IDMCommon idmc;

    /**
     * Default constructor for AuthTestsValidator
     * @param map contains data that is required validate tests
     */
    public AuthTestsValidator(Map testMap) 
    throws Exception {
        super("AuthTestsValidator");
        mapValidate = testMap;
        testURL = getLoginURL("/");
        testLogoutURL = protocol + ":" + "//" + host + ":" + port +
                        uri + "/UI/Logout";

        baseDir = getTestBase();
        log(Level.FINEST, "AuthTestsValidator", "BaseDir:" + baseDir);
        mapValidate.put("url", testURL);
        mapValidate.put("baseDir", baseDir);
        idmc = new IDMCommon();
    }

    /**
     * Performs Positive Service based service Login
     */
    public void testServicebasedPositive() 
    throws Exception {
        try {
            boolean isNegative = false;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createServiceXML(mapValidate, isNegative);
            log(Level.FINEST, "testServicebasedPositive", 
                    "testServicebasedPositive XML file:" + xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            webClient = new WebClient();
            Page page = task.execute(webClient);   
            log(Level.FINEST, "testServicebasedPositive", 
                    "testServicebasedPositive page after login" +
                    page.getWebResponse().getContentAsString());  
        } catch (Exception e) {
            log(Level.SEVERE, "testServicebasedPositive", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Negative Service based service Login
     */
    public void testServicebasedNegative() 
    throws Exception {
        try {
            boolean isNegative = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createServiceXML(mapValidate, isNegative);
            log(Level.FINEST, "testServicebasedNegative",
                    "testServicebasedNegative XML file:" + xmlFile);
            webClient = new WebClient();
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);            
            log(Level.FINEST, "testServicebasedNegative",
                    "testServicebasedNegative page after login" + 
                    page.getWebResponse().getContentAsString());            
        } catch (Exception e) {
            log(Level.SEVERE, "testServicebasedNegative", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Module based Login with positive data
     */
    public void testModulebasedPostive() 
    throws Exception {
        try {
            boolean isNegative = false;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createModuleXML(mapValidate, isNegative);
            log(Level.FINEST, "testModulebasedPostive", 
                    "testModulebasedPostive XML file:" + xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            webClient = new WebClient();
            Page page = task.execute(webClient);   
            log(Level.FINEST, "testModulebasedPostive", 
                    "testModulebasedPostive page after login" + 
                    page.getWebResponse().getContentAsString());  
        } catch (Exception e) {
            log(Level.SEVERE, "testModulebasedPostive", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Module based Login with negative data
     */
    public void testModulebasedNegative() 
    throws Exception {
        try {
            boolean isNegative = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createModuleXML(mapValidate, isNegative);
            log(Level.FINEST, "testModulebasedNegative",
                    "testModulebasedNegative XML file:" + xmlFile);
            webClient = new WebClient();
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);            
            log(Level.FINE, "testModulebasedNegative",
                    "testModulebasedNegative page after login" +
                    page.getWebResponse().getContentAsString());            
        } catch (Exception e) {
            log(Level.SEVERE, "testModulebasedNegative", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Module based Login with goto param
     */
    public void testModuleGoto() 
    throws Exception {
        try {
            boolean isNegative = false;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createModuleGotoXML(mapValidate,
                    isNegative);
            log(Level.FINEST, "testModuleGoto Xml file:", xmlFile);
            webClient = new WebClient();
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);            
            log(Level.FINEST, "testModuleGoto",
                    "testModuleGoto page after login" +
                    page.getWebResponse().getContentAsString());
        } catch (Exception e) {
            log(Level.SEVERE, "testModuleGoto", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Module based Login with gotoOnFail param
     */
    public void testModuleGotoOnFail() 
    throws Exception {
        try {
            boolean isNegative = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createModuleGotoXML(mapValidate,
                    isNegative);
            log(Level.FINEST, "testModuleGotoOnFail",
                    "testModuleGotoOnFail XML file:" + xmlFile);
            webClient = new WebClient();
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);           
            log(Level.FINEST, "testModuleGotoOnFail", 
                    "testModuleGotoOnFail after login" + 
                    page.getWebResponse().getContentAsString());
        } catch (Exception e) {
            log(Level.SEVERE, "testModuleGotoOnFail", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Account lockout tests
     */
    public void testAccountLockout() 
    throws Exception {
        webClient = new WebClient();
        try {
            boolean isWarn = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createLockoutXML(mapValidate, isWarn);
            log(Level.FINEST, "testAccountLockout", 
                    "testAccountLockout XML file:" + xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);            
            log(Level.FINEST, "testAccountLockout", 
                    "testAccountLockout page after login" + 
                    page.getWebResponse().getContentAsString());
        } catch (Exception e) {
            log(Level.SEVERE, "testAccountLockout", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Accountlockout warning tests
     */
    public void testAccountLockWarning()
    throws Exception {
        webClient = new WebClient();
        try {
            boolean isWarn = false;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createLockoutXML(mapValidate, isWarn);
            log(Level.FINEST, "testAccountLockWarning",
                    "testAccountLockWarning XML file:" + xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);            
            log(Level.FINEST, "testAccountLockWarning",
                    "testAccountLockWarning page after login" + 
                    page.getWebResponse().getContentAsString());
        } catch (Exception e) {
            log(Level.SEVERE, "testAccountLockWarning", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Account lockout and verifies the inetuser status after the
     * lockout
     */
    public void testAccountLockoutUserStatus(String username)
    throws Exception {
        webClient = new WebClient();
        Map attrMap = new HashMap();
        try {
            boolean isWarn = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createLockoutXML(mapValidate, isWarn);
            log(Level.FINEST, "testAccountLockoutUserStatus", 
                    "testAccountLockoutUserStatus XML file:" + xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);            
            log(Level.FINEST, "testAccountLockoutUserStatus",
                    "testAccountLockoutUserStatus page after Login" +
                    page.getWebResponse().getContentAsString());
            //now verify the user attibutes
            attrMap = idmc.getIdentityAttributes(username, realm);
            if (attrMap.containsKey("inetuserstatus")) {
                Set userSet = (Set) attrMap.get("inetuserstatus");
                for (Iterator itr = userSet.iterator(); itr.hasNext();) {
                    String userStatus = (String) itr.next();
                    if (userStatus.equals("Inactive")) {
                        log(Level.FINE, "testAccountLockoutUserStatus",
                                "ValidationPass" + userStatus);
                        assert true;
                    } else {
                        log(Level.FINE, "testAccountLockoutUserStatus",
                                "ValidationFail" + userStatus);
                        assert false;
                    }
                }
            }
        } catch (Exception e) {
            log(Level.SEVERE, "testAccountLockoutUserStatus",
                    e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Account lockout user and verifies the custom attributes
     * after lockout
     */
    public void testAccountLockoutUserAttr(String username, String attrName,
            String attrValue)
    throws Exception {
        webClient = new WebClient();
        Map attrMap = new HashMap();
        try {
            boolean isWarn = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createLockoutXML(mapValidate, isWarn);
            log(Level.FINEST, "testAccountLockoutUserAttribute",
                    "testAccountLockoutUserAttribute XML file" + xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);           
            log(Level.FINEST, "testAccountLockoutUserAttr page after login",
                    "testAccountLockoutUserAttr After login" + 
                    page.getWebResponse().getContentAsString());
            //now verify the user attibutes
            attrMap = idmc.getIdentityAttributes(username, realm);
            if (attrMap.containsKey(attrName)) {
                Set attrSet = (Set) attrMap.get(attrName);
                for (Iterator itr = attrSet.iterator(); itr.hasNext();) {
                    String attrVal = (String) itr.next();
                    if (attrVal.equals(attrValue)) {
                        log(Level.FINEST, "testAccountLockoutUserAttr",
                                "ValidationPass" + attrVal);
                        assert true;
                    } else {
                        log(Level.FINEST, "testAccountLockoutUserAttr",
                                "ValidationFail" + attrVal);
                        assert false;
                    }
                }
            }
        } catch (Exception e) {
            log(Level.SEVERE, "testAccountLockoutUserAttr", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs login with Authentication attributes and verifies the user
     * attibutes. This is performed for the module based authenticaation
     */
    public void testUserLoginAuthAttribute(Map userAttrMap)
    throws Exception {
        webClient = new WebClient();
        Map idmAttrMap = new HashMap();
        boolean isNegative = false;
        String userattrName;
        Iterator attIterator;
        String userattrVal;
        Set idmattrSet;
        String idmattrVal;
        try {
            CreateTestXML testXML = new CreateTestXML();
            String userName = (String) mapValidate.get("userName");
            String userPassword = (String) mapValidate.get("password");
            String moduleSubConfig =
                    (String) mapValidate.get("modulesubConfig");
            String redirectURL = (String) mapValidate.get("url") + "?" +
                    "module=" + moduleSubConfig;
            mapValidate.put("redirectURL", redirectURL);

            //now verify the user attibutes
            idmAttrMap = idmc.getIdentityAttributes(userName, realm);
            log(Level.FINEST, "testUserLoginAuthAttribute", "idmAttrMap" +
                    idmAttrMap);
            log(Level.FINEST, "testUserLoginAuthAttribute", "userAttrMap" +
                    userAttrMap);
            for (attIterator = userAttrMap.keySet().iterator();
                    attIterator.hasNext();) {
                userattrName = (String) attIterator.next();
                if (userattrName.equals("userpassword")) {
                    continue;
                }
                Set userattrSet = (Set) userAttrMap.get(userattrName);
                for (Iterator iter = userattrSet.iterator(); iter.hasNext();) {
                    userattrVal = (String) iter.next();
                    if (idmAttrMap.containsKey(userattrName)) {
                        idmattrSet = (Set) idmAttrMap.get(userattrName);
                        for (Iterator itr = idmattrSet.iterator();
                                itr.hasNext();) {
                            idmattrVal = (String) itr.next();
                            if (idmattrVal.equals(userattrVal)) {
                                log(Level.FINE,
                                        "testUserLoginAuthAttribute",
                                        "ValidationPass" + userattrVal);
                                assert true;
                            } else {
                                log(Level.FINE,
                                        "testUserLoginAuthAttribute",
                                        "ValidationFail" + userattrVal);
                                assert false;
                            }
                        }
                    }
                }
            }
            String xmlFile = testXML.createModuleXML(mapValidate, isNegative);
            log(Level.FINEST, "testUserLoginAuthAttribute",
                    "testUserLoginAuthAttribute XML file" + xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);            
            log(Level.FINEST, "testUserLoginAuthAttribute", 
                    "testUserLoginAuthAttribute page after login" +
                    page.getWebResponse().getContentAsString());
        } catch (Exception e) {
            log(Level.SEVERE, "testUserLoginAuthAttribute", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }

    /**
     * Performs Profile tests warning tests
     */
    public void testProfile() 
    throws Exception {
        webClient = new WebClient();
        try {
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createProfileXML(mapValidate);
            log(Level.FINEST, "testProfile", "testprofile XML file"
                     + xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);           
            log(Level.FINEST, "testProfile", "testProfile page after login" +
                    page.getWebResponse().getContentAsString());
        } catch (Exception e) {
            log(Level.SEVERE, "testProfile", e.getMessage());
            e.printStackTrace();
        } finally {
            consoleLogout(webClient, testLogoutURL);
        }
    }
}

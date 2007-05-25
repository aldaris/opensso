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
 * $Id: AuthTestsValidator.java,v 1.1 2007-05-25 22:03:46 sridharev Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common.authentication;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.CreateTestXML;
import com.sun.identity.qatest.common.webtest.DefaultTaskHandler;
import java.util.Map;
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
    private String  baseDir;
    private String testURL;
    private String testLogoutURL;
    
    /**
     * Default constructor for AuthTestsValidator
     * @param map contains data that is required validate tests
     */
    public AuthTestsValidator(Map testMap) throws Exception {
        super("AuthTestsValidator");
        mapValidate = testMap;
        testURL = protocol + ":" + "//" + host + ":" + port + uri;
        testLogoutURL = testURL + "/UI/Logout";
        baseDir = getBaseDir();
        log(logLevel,"AuthTestsValidator", "BaseDir:" + baseDir);
        mapValidate.put("url", testURL);
        mapValidate.put("baseDir", baseDir);
    }
    
    /**
     * Performs Positive Service based service Login
     */
    public void testServicebasedPositive(){
        try {
            boolean isNegative = false;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createServiceXML(mapValidate, isNegative);
            log(logLevel, "testServicebasedPositive", xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            webClient = new WebClient();
            Page page = task.execute(webClient);
            consoleLogout(webClient, testLogoutURL);
        } catch (Exception e) {
            log(logLevel.SEVERE,"testServicebasedPositive",
                    e.getMessage(), null);
            e.printStackTrace();
        }
    }
    
    /**
     * Performs Negative Service based service Login
     */
    public void testServicebasedNegative(){
        try {
            boolean isNegative = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createServiceXML(mapValidate, isNegative);
            log(logLevel, "testServicebasedNegative", xmlFile);
            webClient = new WebClient();
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);
            WebResponse wresponse = page.getWebResponse();
            String resString = wresponse.getContentAsString();
            log(logLevel, "testServicebasedNegative", resString);
            consoleLogout(webClient, testLogoutURL);
        } catch (Exception e) {
            log(logLevel.SEVERE, "testServicebasedNegative",
                    e.getMessage(), null);
            e.printStackTrace();
        }
    }
    
    /**
     * Performs Module based Login with positive data
     */
    public void testModulebasedPostive(){
        try {
            boolean isNegative = false;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createModuleXML(mapValidate, isNegative);
            log(logLevel, "testModulebasedPostive", xmlFile);
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            webClient = new WebClient();
            Page page = task.execute(webClient);
            consoleLogout(webClient, testLogoutURL);
        } catch (Exception e) {
            log(logLevel.SEVERE, "testModulebasedPostive",
                    e.getMessage(), null);
            e.printStackTrace();
        }
    }
    
    /**
     * Performs Module based Login with negative data
     */
    public void testModulebasedNegative(){
        try {
            boolean isNegative = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createModuleXML(mapValidate, isNegative);
            log(logLevel, "testModulebasedNegative", xmlFile);
            webClient = new WebClient();
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);
            WebResponse wresponse = page.getWebResponse();
            String resString = wresponse.getContentAsString();
            log(logLevel, "testModulebasedNegative", resString);
            consoleLogout(webClient, testLogoutURL);
        } catch (Exception e) {
            log(logLevel.SEVERE, "testModulebasedNegative",
                    e.getMessage(), null);
            e.printStackTrace();
        }
    }
    
    /**
     * Performs Module based Login with goto param
     */
    public void testModuleGoto(){
        try {
            boolean isNegative = false;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createModuleGotoXML(mapValidate, isNegative);
            log(logLevel, "testModuleGoto", xmlFile);
            webClient = new WebClient();
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);
            WebResponse wresponse = page.getWebResponse();
            String resString = wresponse.getContentAsString();
            log(logLevel, "testModuleGoto", resString);
            consoleLogout(webClient, testLogoutURL);
        } catch (Exception e) {
            log(logLevel.SEVERE, "testModuleGoto",
                    e.getMessage(), null);
            e.printStackTrace();
        }
    }
    
    /**
     * Performs Module based Login with gotoOnFail param
     */
    public void testModuleGotoOnFail(){
        try {
            boolean isNegative = true;
            CreateTestXML testXML = new CreateTestXML();
            String xmlFile = testXML.createModuleGotoXML(mapValidate, isNegative);
            log(logLevel, "testModuleGotoOnFail", xmlFile);
            webClient = new WebClient();
            DefaultTaskHandler task = new DefaultTaskHandler(xmlFile);
            Page page = task.execute(webClient);
            WebResponse wresponse = page.getWebResponse();
            String resString = wresponse.getContentAsString();
            log(logLevel, "testModuleGotoOnFail", resString);
            consoleLogout(webClient, testLogoutURL);
        } catch (Exception e) {
            log(logLevel.SEVERE, "testModuleGotoOnFail",
                    e.getMessage(), null);
            e.printStackTrace();
        }
    }
}

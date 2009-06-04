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
 * $Id: UICommon.java,v 1.1 2009-06-04 23:05:55 grathinam Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * This class contains common methods for testing console 
 */
public class UICommon extends TestCommon {
    
    private ResourceBundle rbg;
    private String strGblRB = "configuratorGlobal";
    private String strSeleniumPort;
    private int seleniumPort;
    private Character browserType;
    private String browserLocation;
    public  static Selenium selenium;
    private String url;
    
    /**
     * Class constructor definition. Instantiates resource bundles
     */
    public UICommon() {
        super("UICommon");
        rbg = ResourceBundle.getBundle("userinterface" + fileseparator 
                + strGblRB);
        strSeleniumPort = rbg.getString("selenium_port");
        seleniumPort = new Integer(strSeleniumPort).intValue();
        browserLocation = rbg.getString("browser_location");
        browserType = browserLocation.charAt(1);
        url = protocol + "://" + host + ":"+ port + uri;
    }
    
     /** 
     * Launches the browser with a new Selenium session
     * @param url to be opened by the browser
     */
    public void startSelenium(){
        startSelenium(url, browserLocation);
        seleniumOpenUrl(url);
    }
    
    /** 
     * Launches the browser with a new Selenium session
     * @param url to be opened by the browser
     */
    public void startSelenium(String url, String browserLoc){
        entering("startSelenium", null);
        selenium = new DefaultSelenium("localhost", seleniumPort, 
                browserLoc, url);
        selenium.start();     
        exiting("startSelenium");
    }
    
    /**
     * Method to open the url
     * @param url
     */
    public void seleniumOpenUrl(String url) {
        String testurl = null;
        StringTokenizer st = new StringTokenizer(url, "/");
        while (st.hasMoreTokens()) {
            testurl = st.nextToken();
        }              
        selenium.open(testurl);
        selenium.waitForPageToLoad("30000");
    }
    
    /**
     * Method to login for admin users
     * @throws java.lang.Exception
     */
    public void login () throws Exception {
        login(adminUser, adminPassword);
    }
    /**
     * Method to login to OpenSSO Console
     * @throws java.lang.Exception
     */
    public void login(String userName, String password) throws Exception { 
        Thread.sleep(5000);
        selenium.type("IDToken1", userName);
        if (browserType.equals('f')) {
            selenium.typeKeys("IDToken2", password);
        } else {
            selenium.type("IDToken2", password);
            selenium.fireEvent("IDToken2", "keyup");
        }
        selenium.click("Login.Submit");
        selenium.waitForPageToLoad("30000");
        if (userName.equals("amadmin")) {
            for (int second = 0;; second++) {
                if (second >= 60) {
                    SeleneseTestCase.fail("timeout");
                }
                try {
                    if (selenium.isElementPresent("//div[@id='TskPge']" +
                            "/table/tbody/tr[2]/td[2]")) {
                        log(Level.FINEST, "login", "Log in Passed");
                        break;
                    }
                } catch (Exception e) {
                    log(Level.SEVERE, "login", "Log in failed");
                    e.printStackTrace();
                }
                Thread.sleep(1000);
            }
        }
    }
    
    /**
     * Method to get selenium Instance
     * @return selenium instance
     */
    public Selenium getSeleniumInstance() {
        return selenium;
    }

    /**
     * Ends the test session, killing the browser
     */
    public void stopSelenium() {
        entering("stopSelenium", null);
        selenium.stop();
        exiting("stopSelenium");
    }
    
    /**
     * Method to find the browser type
     * @return browser type
     */
    public Character getBrowserType() {
        return browserType;
    }
    
    /**
     * This method sets the default base state
     * @param stateValue default state value
     */
    public void defaultBaseState(String stateValue, String realmName){
        boolean accessControl = false;
        // if not in common tasks page
        if (selenium == null) {
            try {
                startSelenium();
                log(Level.FINEST, "defaultBaseState", "Log out button is" +
                        "is not visible");
                login(adminUser, adminPassword);
            } catch (Exception e) {
                log(Level.SEVERE, "defaultBaseState", "Log in failed");
                e.printStackTrace();
            }
        }
        if (!(selenium.isElementPresent("link=Access Control"))) {
            // already link is selected
            if (selenium.isElementPresent("Entities.button1")) {
                selenium.click("Entities.button1");
                selenium.waitForPageToLoad("30000");
                accessControl = true;
                log(Level.FINEST, "defaultBaseState", "In Subjects Page");
            } else if (selenium.isElementPresent("link=Log Out")) {
                // if logout button is present
                logout();
                log(Level.FINEST, "defaultBaseState", "Log out button is " +
                        "visible");
                try {
                    Thread.sleep(1000);
                    selenium.click("link=Return to Login page");
                    login(adminUser, adminPassword);
                } catch (Exception e) {
                    log(Level.SEVERE, "defaultBaseState", "Log in failed");
                    e.printStackTrace();
                }
            } else {
                // if logout button is not visible in the page
                try {
                    stopSelenium();
                    startSelenium();
                    log(Level.FINEST, "defaultBaseState", "Log out button is" +
                            "is not visible");
                    login(adminUser, adminPassword);         
                } catch (Exception e) {
                    log(Level.SEVERE, "defaultBaseState", "Log in failed");
                    e.printStackTrace();
                }
            }
        }
        if (!accessControl) {
            selenium.click("link=Access Control");
            selenium.waitForPageToLoad("30000");
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (realmName.equals("/")) {
            selenium.click("link=/ (Top Level Realm)");
        } else {
            String rName = "";
            StringTokenizer tokenizer = new StringTokenizer(realmName, "/");
            while (tokenizer.hasMoreTokens()) {
                rName = tokenizer.nextToken();
            }
            String realmLink = "link=" + rName;
            if (!selenium.isTextPresent(rName)) {
                createRealm(realmName);
            }
            selenium.click(realmLink);
        }
        selenium.waitForPageToLoad("30000");
        if (stateValue.equals("Subjects")) {
            selenium.click("link=Subjects");
            selenium.waitForPageToLoad("30000");
        }
    }
   
    /**
     * Method to logout from the console
     */
    public void logout() {
        selenium.click("link=Log Out");
        SeleneseTestCase.assertTrue(selenium.getConfirmation().matches
                ("^Log out of the OpenSSO[\\s\\S]\nYou might " +
                "need to close any associated windows\\.$"));
        selenium.waitForPageToLoad("30000");         
    }
    
    /**
     * Method to create a new user
     * @param attributes
     */
    public void createUser(String attributes) {
        String testName = "testcreateuser";
        String expResult = " ";
        createUser(attributes, testName, expResult);
    }
    
    /**
     * Method to create a new user
     * @param attributes for creating a new user
     * @param testName 
     * @param expResult
     */
    public void createUser(String attributes, String testName, 
            String expResult) {
        Map<String, String> userMap = getUserDetails(attributes);
        selenium.click("Entities.tblButtonAdd");
        selenium.waitForPageToLoad("30000");
        if (userMap.containsKey("id")) {
            selenium.type("psLbl1", userMap.get("id"));
        } 
        if (userMap.containsKey("firstName")) {
            selenium.type("psLbl2", userMap.get("firstName"));
        } 
        if (userMap.containsKey("lastName")) {
            selenium.type("psLbl3", userMap.get("lastName"));
        } 
        if (userMap.containsKey("fullName")) {
            selenium.type("psLbl4", userMap.get("fullName"));
        }
        if (userMap.containsKey("password")) {
            selenium.type("psLbl5", userMap.get("password"));
        }
        if (userMap.containsKey("confirmPassword")) {
            selenium.type("EntityAdd.userpassword_confirm",
                    userMap.get("confirmPassword"));
        }
        if (userMap.containsKey("confirmPassword")) {
            if (userMap.get("userStatus").equals("Active")) {
                selenium.click("psLbl7");
            } else {
                selenium.click("psLbl72");
            }
        }
        selenium.click("EntityAdd.button1");
        selenium.waitForPageToLoad("30000");
        if (testName.equals("testcreateuser")) {
            SeleneseTestCase.assertTrue(selenium.isTextPresent
                    (userMap.get("id")));       
        } else {
            SeleneseTestCase.assertEquals(expResult, 
                    selenium.getText("//td/table/tbody/tr/td/div[1]"));
            selenium.click("EntityAdd.button2");
            selenium.waitForPageToLoad("30000");  
        }
        
    }
    /**
     * Update the user
     * @param userName full name of the user
     * @param updateFieldName 
     * @param updateValue
     * @throws java.lang.Exception
     */
    public void updateUser(String id, String attributes, String realmName) 
            throws Exception {
        boolean error = false;
        Map <String, String> userMap = getUserDetails(attributes);
        String userName = "";
        boolean paginationImg = false;
        if (selenium.isElementPresent("Entities.tblSearch.PaginationImage")) {
            if (selenium.isVisible("Entities.tblSearch.PaginationImage")) {
                selenium.click("Entities.tblSearch.PaginationImage");
                selenium.waitForPageToLoad("30000");
                paginationImg = true;
            }
        }
        int userCount = getNumberOfRows("User ( )");
        for (int index = 0; index < userCount; index++) {
            String tableName = "//div[10]/table." + (2 + index) + "." + 1;                 
            String tableId = "//div[10]/table." + (2 + index) + "." + 2;           
            if ((id.equals(selenium.getTable(tableId)))) {
                userName = selenium.getTable(tableName);
                break;
            }
        }
        String userLink = "link=" + userName;
        selenium.click(userLink);
        selenium.waitForPageToLoad("30000");
        if (userMap.containsKey("firstName")) {
            selenium.type("psLbl1", userMap.get("firstName"));
            setUserValues();
        }
        if (userMap.containsKey("lastName")) {
            selenium.type("psLbl2", userMap.get("lastName"));
            setUserValues();
        }
        if (userMap.containsKey("fullName")) {
            selenium.type("psLbl3", userMap.get("fullName"));
            setUserValues();
        }
        if (userMap.containsKey("userStatus")) {
            if (userMap.get("userStatus").equals("Active")) {
                selenium.click("psLbl8");
            } else {
                selenium.click("psLbl82");
            }
            setUserValues();
        }
       if (userMap.containsKey("password")) {
            selenium.click("link=Edit");
            selenium.waitForPopUp("newwindow", "30000");
            selenium.selectWindow("name=newwindow");
            selenium.waitForPageToLoad("30000");
            selenium.type("psLbl1", userMap.get("password"));
            selenium.type("psLbl2", userMap.get("confirmPassword"));
            selenium.click("UMChangeUserPassword.button1");
            selenium.waitForPageToLoad("30000");
            if ("Error\n The passwords you entered do not match.".equals
                            (selenium.getTable("//td/table.0.0"))) {
            SeleneseTestCase.assertTrue(selenium.getTable
                    ("//td/table.0.0"), true);
            selenium.click("UMChangeUserPassword.button2");
            selenium.waitForPageToLoad("30000");
            error = true;
            } else {
                for (int second = 0;; second++) {
                    if (second >= 60) {
                        SeleneseTestCase.fail("timeout");
                    }
                    try {
                        if ("Information\n Password was changed.".equals
                                (selenium.getTable("//td/table.0.0"))) {
                            break;
                        }
                    } catch (Exception e) {
                    }
                    Thread.sleep(1000);
                }
            }
            selenium.click("UMChangeUserPassword.button3");
            selenium.selectWindow("null");
        }       
        selenium.click("EntityEdit.button3");
        selenium.waitForPageToLoad("30000");
        if ((userMap.containsKey("fullName"))) {
            String link = "link=" + userMap.get("fullName");
            Thread.sleep(30000);
            selenium.click(link);
        } else {
            selenium.click(userLink);
        }
        selenium.waitForPageToLoad("30000");
        for (int second = 0;; second++) {
            if (second >= 60) {
                SeleneseTestCase.fail("timeout");
            }
            try {
                if (selenium.isElementPresent("//td[2]/div")) {
                    log(Level.FINEST, "updateUser", "Edit page Loaded");
                    break;
                }
            } catch (Exception e) {
                log(Level.SEVERE, "updateUser", "Edit page is not loaded");
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }
        if (userMap.containsKey("firstName")) {
            if (!(userMap.get("firstName").equals(" "))) {
                SeleneseTestCase.assertEquals(userMap.get("firstName"),
                        selenium.getValue("psLbl1"));
            }
        }
        if (userMap.containsKey("lastName")) {
            if (!(userMap.get("lastName").equals(" "))) {
                SeleneseTestCase.assertEquals(userMap.get("lastName"),
                        selenium.getValue("psLbl2"));
            }
        }
        if (userMap.containsKey("fullName")) {
            if (!(userMap.get("fullName").equals(" "))) {
                SeleneseTestCase.assertEquals(userMap.get("fullName"),
                        selenium.getValue("psLbl3"));
            }
        }
        if (userMap.containsKey("userStatus")) {
            if (userMap.get("userStatus").equals("Active")) {
                SeleneseTestCase.assertEquals("on", selenium.getValue
                    ("id=psLbl8"));
            } else {
                SeleneseTestCase.assertEquals("on", selenium.getValue
                    ("id=psLbl82"));
            }
        } 
        selenium.click("EntityEdit.button3");
        selenium.waitForPageToLoad("30000");
        if (!(error)) {
            logout();
            selenium.click("link=Return to Login page");
            selenium.waitForPageToLoad("30000");    
            login(id, userMap.get("password"));
            Thread.sleep(5000);
            SeleneseTestCase.assertEquals("OpenSSO", selenium.getTitle());
            defaultBaseState("Subjects", realmName);
        }
        if (paginationImg) {
            selenium.click("Entities.tblSearch.PaginationImage");
            selenium.waitForPageToLoad("30000");
        }
    }
    
    /**
     * Method to set user values
     */
    public void setUserValues() {
        selenium.click("EntityEdit.button1");
        selenium.waitForPageToLoad("6000");
        if ((selenium.getTable("//td/table.0.0")).startsWith("Error")) {
            SeleneseTestCase.assertTrue(selenium.getTable
                    ("//td/table.0.0"), true);
            selenium.click("EntityEdit.button2");
            selenium.waitForPageToLoad("30000");
        } else {
            for (int second = 0;; second++) {
                try {
                    if (second >= 60) {
                        SeleneseTestCase.fail("timeout");
                    }
                    try {
                        if ("Information\n Profile was updated.".equals
                                (selenium.getTable("//td/table.0.0"))) {
                            break;
                        }
                    } catch (Exception e) {
                    }
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }    
    
    /**
     * Method to delete an user
     * @param userName
     * @param id
     * @throws java.lang.Exception
     */
    public void deleteUser(String userId) throws Exception {
        boolean elementPresent = false;
        int userCount = getNumberOfRows("User ( )");
        String checkBoxIdx = "";
        for (int index = 0; index < userCount; index++) {
            String tableId = "//div[10]/table." + (2 + index) + "." + 2;
            if (userId.equals(selenium.getTable(tableId))) {
                int checkBoxCount = 3 + index ;
                checkBoxIdx = "xpath=/html/body/form/div[10]/table/tbody/tr[" 
                        + checkBoxCount + "]/td[1]/input[1]"; 
                elementPresent = true;
                break;
            }
        } 
        if (elementPresent) {
            selenium.click(checkBoxIdx);
            selenium.click("Entities.tblButtonDelete");
            for (int second = 0;; second++) {
                if (second >= 60) {
                    SeleneseTestCase.fail("timeout");
                }
                try {
                    if (selenium.isElementPresent("//td/table/tbody/tr/" +
                            "td/div[2]")) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(1000);
            }
            Thread.sleep(5000);
            SeleneseTestCase.assertFalse(selenium.isTextPresent(userId));
        }
    }

    /**
     * Method to get the user details
     * @param attributes
     * @return
     */
    public Map getUserDetails(String attributes) {
        Map<String, String> userMap = new HashMap<String, String>();
        String firstName = "";
        String id = "";
        String lastName = "";
        String fullName = "";
        String password = "";
        String confirmPassword = "";
        String userStatus = "";
        StringTokenizer st;
        String token;   
        StringTokenizer tokenizer = new StringTokenizer(attributes, ",");
        ArrayList<String> attrList = new ArrayList<String>
                (tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            attrList.add(tokenizer.nextToken());
        }   
        for (String prop : attrList) {
            st = new StringTokenizer(prop, "=");
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (token.equals("id")) {
                    id = st.nextToken();
                    userMap.put("id", id);
                } else if (token.equals("firstName")) {
                    firstName = st.nextToken();
                    userMap.put("firstName", firstName);
                } else if (token.equals("lastName")) {
                    lastName = st.nextToken();
                    userMap.put("lastName", lastName);
                } else if (token.equals("fullName")) {
                    fullName = st.nextToken();
                    userMap.put("fullName", fullName);
                } else if (token.equals("password")) {
                    password = st.nextToken();
                    userMap.put("password", password);
                } else if (token.equals("confirmPassword")) {
                    confirmPassword = st.nextToken();
                    userMap.put("confirmPassword", confirmPassword);
                } else if (token.equals("userStatus")) {
                    userStatus = st.nextToken();
                    userMap.put("userStatus", userStatus);
                } 
            }
        }      
        return userMap;
    }
    
    /**
     * Method to create a new role
     * @param attributes
     * @param testName
     * @param expResult
     */
    public void createRole(String attributes, String testName, String expResult)
    {
        String roleId = "";
        StringTokenizer tokenizer = new StringTokenizer(attributes, "=");
        while (tokenizer.hasMoreTokens()) {
            tokenizer.nextToken();
            roleId = tokenizer.nextToken();
        }
        selenium.click("Entities.tblButtonAdd");
        selenium.waitForPageToLoad("30000");
        if (roleId != null) {
            if (browserType.equals('f')) {
                selenium.typeKeys("psLbl1", roleId);
            } else {
                selenium.type("psLbl1", roleId);
                selenium.fireEvent("psLbl1", "keyup");
            }
        }
        selenium.click("EntityAdd.button1");
        selenium.waitForPageToLoad("30000");
        if (testName.equals("testcreaterole")) {
            SeleneseTestCase.assertTrue(selenium.isTextPresent(roleId)); 
        } else {
            SeleneseTestCase.assertEquals(expResult, 
                    selenium.getText("//td/table/tbody/tr/td/div[1]"));
            selenium.click("EntityAdd.button2");
            selenium.waitForPageToLoad("30000"); 
        }
    }
    
    /**
     * Method to delete a role
     * @param roleId
     * @throws java.lang.Exception
     */
    public void deleteRole(String roleId) throws Exception {
        int roleCount = getNumberOfRows("Role ( )");
        String checkBoxIdx = "";
        for (int index = 0; index < roleCount; index++) {
            String tableId = "//div[10]/table." + (2 + index) + ".1";
            if (roleId.equals(selenium.getTable(tableId))) {
                int checkBoxCount = 3 + index;
                checkBoxIdx = "xpath=/html/body/form/div[10]/table/" +
                        "tbody/tr[" + checkBoxCount + "]/td[1]/input[1]";
                break;
            }
        }
        selenium.click(checkBoxIdx);
        selenium.click("Entities.tblButtonDelete");
        for (int second = 0;; second++) {
            if (second >= 60) {
                SeleneseTestCase.fail("timeout");
            }
            try {
                if (selenium.isElementPresent("//td/table/tbody/tr/td/div[2]"))
                {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }
        SeleneseTestCase.assertFalse(selenium.isTextPresent(roleId));
    }
    
    
    /**
     * Method to create a filtered role
     * @param attributes
     * @param testName
     * @param expResult
     */
    public void createFilteredRole(String attributes, String testName, 
            String expResult) {
        String filterId = "";
        String filter ="";
        Map<String, String> filterMap = getFilterDetails(attributes);
        if (filterMap.containsKey("filterId"))
            filterId = filterMap.get("filterId");
        if (filterMap.containsKey("filter"))
            filter = filterMap.get("filter");
        selenium.click("Entities.tblButtonAdd");
        selenium.waitForPageToLoad("30000");
        selenium.type("psLbl1", filterId);
        if (filterId != null) {
            if (browserType.equals('f')) {
                selenium.type("psLbl2", "");
                selenium.typeKeys("psLbl2", filter);
            } else {
                selenium.type("psLbl2", filter);
                selenium.fireEvent("psLbl2", "keyup");
            }
        }
        selenium.click("EntityAdd.button1");
        selenium.waitForPageToLoad("30000");
        if (testName.equals("testcreatefilteredrole")) {
            SeleneseTestCase.assertTrue(selenium.isTextPresent(filterId)); 
        } else {
            SeleneseTestCase.assertEquals(expResult, 
                    selenium.getText("//td/table/tbody/tr/td/div[1]"));
            selenium.click("EntityAdd.button2");
            selenium.waitForPageToLoad("30000"); 
        }
    }
    
    /**
     * Method to update the filtered role
     * @param attributes
     * @throws java.lang.Exception
     */
    public void updateFilteredRole(String attributes) throws Exception{
        String filterId = "";
        String filter ="";
        String filterName ="";
        Map<String, String> filterMap = getFilterDetails(attributes);
        if (filterMap.containsKey("filterId"))
            filterId = filterMap.get("filterId");
        if (filterMap.containsKey("filter"))
            filter = filterMap.get("filter");    
        int rowCount = getNumberOfRows("Filtered Role ( )");
        for (int index = 0; index < rowCount; index++) {
            String tableName = "//div[10]/table." + (2 + index) + "." + 1;                 
            String tableId = "//div[10]/table." + (2 + index) + "." + 2;           
            if ((filterId.equals(selenium.getTable(tableId)))) {
                filterName = selenium.getTable(tableName);
                break;
            }
        }
        String filterLink = "link=" + filterName;
        selenium.click(filterLink);
        selenium.waitForPageToLoad("30000");
        selenium.type("psLbl1", filter);
        selenium.click("EntityEdit.button1");
        for (int second = 0;; second++) {
            if (second >= 60) {
                SeleneseTestCase.fail("timeout");
            }
            try {
                if (selenium.isElementPresent("//td/table/tbody/tr/td/div[2]" 
                        )) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }
        selenium.click("EntityEdit.button3");
        selenium.waitForPageToLoad("30000");
        selenium.click(filterLink);
        selenium.waitForPageToLoad("30000");
        SeleneseTestCase.assertEquals(filter, selenium.getValue("psLbl1"));
        selenium.click("EntityEdit.button3");
        selenium.waitForPageToLoad("30000");
    }
    
    /**
     * Method to get the filter details
     * @param attributes
     * @return
     */
    public Map<String, String> getFilterDetails(String attributes) {
        Map<String, String> filterMap = new HashMap<String, String>();
        String filter = "";
        String filterId = "";
        StringTokenizer st;
        String token;
        StringTokenizer tokenizer = new StringTokenizer(attributes, ",");
        ArrayList<String> attrList = new ArrayList<String>
                (tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            attrList.add(tokenizer.nextToken());
        }
        for (String prop : attrList) {
            st = new StringTokenizer(prop, "=", true);
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (token.equals("filter")) {
                    st.nextToken();
                    filter = st.nextToken();
                    filter = filter + st.nextToken() + st.nextToken();
                    filterMap.put("filter", filter);
                } else if (token.equals("filterId")) {
                    st.nextToken();
                    filterId = st.nextToken();
                    filterMap.put("filterId", filterId);
                }
            }
        }
        return filterMap;
    }
    
    
    /**
     * Method to delete the filtered role
     * @param filterId
     * @throws java.lang.Exception
     */
    public void deleteFilteredRole(String filterId) throws Exception {        
        int rowCount = getNumberOfRows("Filtered Role ( )");
        String checkBoxIdx = "";
        for (int index = 0; index < rowCount; index++) {
            String tableId = "//div[10]/table." + (2 + index) + ".1";
            if (filterId.equals(selenium.getTable(tableId))) {
                int checkBoxCount = 3 + index ;
                checkBoxIdx = "xpath=/html/body/form/div[10]/table/tbody/tr[" 
                        + checkBoxCount + "]/td[1]/input[1]"; 
                break;
            }
        }      
        selenium.click(checkBoxIdx);
        selenium.click("Entities.tblButtonDelete");
        for (int second = 0;; second++) {
            if (second >= 60) {
                SeleneseTestCase.fail("timeout");
            }
            try {
                if (selenium.isElementPresent("//td/table/tbody/tr/td/div[2]"))
                {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }
        SeleneseTestCase.assertFalse(selenium.isTextPresent(filterId));
    }
    
    /**
     * Method to list users in filtered role
     * @param userAttributes
     * @param filterAttributes
     */
    public void searchFilteredRole(String testName, String attributes,
            String expectedResult) 
            throws Exception{
        String filterId = "";
        Map<String, String> filterMap = getFilterDetails(attributes);
        if (filterMap.containsKey("filterId")) {
                filterId = filterMap.get("filterId");
            }
        if (testName.equals("testfilteredroleusers")) {
            
            String filterName = "";
            
            Map<String, String> userMap = getUserDetails(attributes);
            String user = userMap.get("fullName") + "(" + userMap.get
                    ("id") + ")";
            boolean existingUser = false; 
            int rowCount = getNumberOfRows("Filtered Role ( )");
            for (int index = 0; index < rowCount; index++) {
                String tableName = "//div[10]/table." + (2 + index) + "." + 1;
                String tableId = "//div[10]/table." + (2 + index) + "." + 2;
                if ((filterId.equals(selenium.getTable(tableId)))) {
                    filterName = selenium.getTable(tableName);
                    break;
                }
            }

            String filterLink = "link=" + filterName;
            selenium.click(filterLink);
            selenium.waitForPageToLoad("30000");
            for (int second = 0;; second++) {
                if (second >= 60) {
                    SeleneseTestCase.fail("timeout");
                }
                try {
                    if (selenium.isElementPresent("link=User")) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(1000);
            }
            selenium.click("link=User");
            selenium.waitForPageToLoad("30000");
            Thread.sleep(5000);
            String selectOptions = selenium.getTable("//div[8]/table.0.0");
            StringTokenizer tokenizer = new StringTokenizer(selectOptions, " ");
            while (tokenizer.hasMoreTokens()) {
                tokenizer.nextToken().equals(user);
                existingUser = true;
                break;
            }
            selenium.click("link=General");
            Thread.sleep(5000);
            selenium.click("EntityEdit.button3");
            selenium.waitForPageToLoad("30000");
            if (existingUser) {
                SeleneseTestCase.assertTrue(existingUser);
            } else {
                SeleneseTestCase.assertFalse(existingUser);
            }
        } else {
            searchIdentity(filterId, expectedResult);
        }
    }

    /**
     * Method to search for user, group, filteredrole or role
     * @param id
     * @param expResult
     */
    public void searchIdentity(String id, String expResult) {
        boolean existingIdentity = false;
        selenium.type("Entities.tfFilter", id);
        selenium.click("Entities.btnSearch");
        selenium.waitForPageToLoad("30000");
        existingIdentity = selenium.isTextPresent(id);
        selenium.type("Entities.tfFilter", "*");
        selenium.click("Entities.btnSearch");
        selenium.waitForPageToLoad("30000");
        if (expResult.equals("existingidentity")) {
            SeleneseTestCase.assertTrue(existingIdentity);
        } else {
            SeleneseTestCase.assertFalse(existingIdentity);
        }
    }
    
    /**
     * Method to add an user member to role
     * @param attributes
     * @throws java.lang.Exception
     */
    public void addMemberToRole(String attributes) throws Exception{
         try {
            addMember("role", attributes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method to add  an user member to group
     * @param attributes
     * @throws java.lang.Exception
     */
    public void addMemberToGroup(String attributes) throws Exception {
        try {
            addMember("group", attributes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method to add a member
     * @param idType
     * @param attributes
     * @throws java.lang.Exception
     */
    public void addMember(String idType, String attributes) throws Exception{
        Map <String, String> attrMap = getAttributeDetails(attributes);
        String roleId = "";
        String id = "";
        String fullName = ""; 
        String groupId = "";
        if (attrMap.containsKey("id")) {
            id = attrMap.get("id");
        } if (attrMap.containsKey("roleId")) {
            roleId = attrMap.get("roleId");
        } if (attrMap.containsKey("fullName")) {
            fullName = attrMap.get("fullName");
        } if (attrMap.containsKey("groupId")) {
            groupId = attrMap.get("groupId");
        } 
        String selectedUser = fullName + "(" + id + ")";
        String userLabel = "label=" + selectedUser;
        String selectedRole = roleId + "(" + roleId + ")";
        String selectedGroup = groupId + "(" + groupId + ")";
        String roleLink = "link=" + roleId;
        String groupLink = "link=" + groupId;
        if (idType.equals("role"))
            selenium.click(roleLink);
        else if (idType.equals("group"))
            selenium.click(groupLink);
        selenium.waitForPageToLoad("30000");
	selenium.click("link=User");
        selenium.waitForPageToLoad("30000");
        Thread.sleep(3000);
        selenium.select("EntityMembers.addRemoveMembers." +
                "AvailableListBox", userLabel);
        selenium.click("EntityMembers.addRemoveMembers.AddButton");
        Thread.sleep(1000);
        selenium.click("EntityMembers.button1");
        selenium.waitForPageToLoad("30000");
        selenium.click("EntityMembers.button3");
        selenium.waitForPageToLoad("30000");
        for (int second = 0;; second++) {
            if (second >= 60) {
                SeleneseTestCase.fail("timeout");
            }
            try {
                if (selenium.isElementPresent("Entities.button1")) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }
        selenium.click("link=User");
	selenium.waitForPageToLoad("30000");
        selenium.click("link=" + fullName);
        selenium.waitForPageToLoad("30000");
        if (idType.equals("role"))
            selenium.click("link=Role");
        else if (idType.equals("group"))
            selenium.click("link=Group");
	selenium.waitForPageToLoad("30000");
        Thread.sleep(3000);
        boolean entityExist = false;
        if (idType.equals("role"))
            entityExist = isEntityExist("//td/div/table.1.3", selectedRole);
        else if (idType.equals("group"))
            entityExist = isEntityExist("//td/div/table.1.3", selectedGroup);
        SeleneseTestCase.assertTrue(entityExist);
        selenium.click("EntityMembership.button3");
        selenium.waitForPageToLoad("30000");
        deleteUser(id);
        if (idType.equals("role"))
            selenium.click("link=Role");
        else if (idType.equals("group"))
            selenium.click("link=Group");
        selenium.waitForPageToLoad("30000");
        if (idType.equals("role"))
            selenium.click(roleLink);
        else if (idType.equals("group"))
            selenium.click(groupLink);
        selenium.waitForPageToLoad("30000");
        selenium.click("link=User");
        selenium.waitForPageToLoad("30000");
        Thread.sleep(3000);
        boolean userExist = isEntityExist("//td/div/table.4.0", selectedUser);
        selenium.click("EntityMembers.button3");
	selenium.waitForPageToLoad("30000"); 
        SeleneseTestCase.assertFalse(userExist);
    }
    
    /**
     * Method to test whether an entity exists in dialog box
     * @param tableId
     * @param selectedEntity
     * @return
     */
    public boolean isEntityExist(String tableId, String selectedEntity) {
        String selectedRows = selenium.getTable(tableId);
        StringTokenizer tokenizer = new StringTokenizer(selectedRows, " ");
        boolean entityExists = false;
         while (tokenizer.hasMoreTokens()) {
            if (selectedEntity.equals(tokenizer.nextToken())) {
                entityExists = true;
                break;
            }
        }
        return entityExists;
    }
    
    /**
     * Method to get userId, groupId and fullName
     * @param attributes
     * @return
     */
    public Map<String, String> getAttributeDetails(String attributes) {
        Map<String, String> attrMap = new HashMap<String, String>();
        StringTokenizer st;
        String token;   
        StringTokenizer tokenizer = new StringTokenizer(attributes, ",");
        ArrayList<String> attrList = new ArrayList<String>
                (tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            attrList.add(tokenizer.nextToken());
        }   
        for (String prop : attrList) {
            st = new StringTokenizer(prop, "=");
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (token.equals("id")) {
                    attrMap.put("id", st.nextToken());
                } else if (token.equals("fullName")) {
                    attrMap.put("fullName", st.nextToken());
                } else if (token.equals("roleId")) {
                    attrMap.put("roleId", st.nextToken());
                } else if (token.equals("groupId")) {
                    attrMap.put("groupId", st.nextToken());
                } 
            }
        }
        return attrMap;
    }
   
    /**
     * Method to create a new Group
     * @param attributes
     * @param testName
     * @param expResult
     */
    public void createGroup(String attributes, String testName,
             String expResult)
    {
        String groupId = "";
        StringTokenizer tokenizer = new StringTokenizer(attributes, "=");
        while (tokenizer.hasMoreTokens()) {
            tokenizer.nextToken();
            groupId = tokenizer.nextToken();
        }
        selenium.click("Entities.tblButtonAdd");
        selenium.waitForPageToLoad("30000");
        if (groupId != null) {
            if (browserType.equals('f')) {
                selenium.typeKeys("psLbl1", groupId);
            } else {
                selenium.type("psLbl1", groupId);
                selenium.fireEvent("psLbl1", "keyup");
            }
        }
        selenium.click("EntityAdd.button1");
        selenium.waitForPageToLoad("30000");
        if (testName.equals("testcreategroup")) {
            SeleneseTestCase.assertTrue(selenium.isTextPresent(groupId)); 
        } else {
            SeleneseTestCase.assertEquals(expResult, 
                    selenium.getText("//td/table/tbody/tr/td/div[1]"));
            selenium.click("EntityAdd.button2");
            selenium.waitForPageToLoad("30000"); 
        }
    }
    
    /**
     * Method to delete a group
     * @param groupId
     * @throws java.lang.Exception
     */
    public void deleteGroup(String groupId) throws Exception {
        int groupCount = getNumberOfRows("Group ( )");
        String checkBoxIdx = "";
        for (int index = 0; index < groupCount; index++) {
            String tableId = "//div[10]/table." + (2 + index) + ".1";
            if (groupId.equals(selenium.getTable(tableId))) {
                int checkBoxCount = 3 + index;
                checkBoxIdx = "xpath=/html/body/form/div[10]/table/" +
                        "tbody/tr[" + checkBoxCount + "]/td[1]/input[1]";
                break;
            }
        }
        selenium.click(checkBoxIdx);
        selenium.click("Entities.tblButtonDelete");
        Thread.sleep(2000);
        SeleneseTestCase.assertFalse(selenium.isTextPresent(groupId));
    }

    /**
     * Method to get Number of rows in a table
     * @param caption
     * @return
     */
    public int getNumberOfRows(String caption) {
        String tableId = "//caption";
        int rowCount = getNumberOfRows(caption, tableId);
        return rowCount;
    }
    
    /**
     * Method to get number of rows in a table
     * @param caption
     * @param tableId
     * @return
     */
    public int getNumberOfRows(String caption, String tableId) {
        String role = "";
        role = selenium.getText(tableId); 
        StringTokenizer tokenizer = new StringTokenizer(role, caption);
        String token = "";
        int count = tokenizer.countTokens();
        for (int i = 0; i < count; i++) {
            token = tokenizer.nextToken();
        }
        int rowCount = Integer.parseInt(token);
        return rowCount;
    }
    
    /**
     * Method to delete an entity provider
     * @param providerName
     * @throws java.lang.Exception
     */
    public void deleteEntityProvider(String providerName) throws Exception {
        boolean elementPresent = false;
        String tableName = "xpath=/html/body/div[@id='main']/form/div[9]/" +
                "div[4]/div[2]/" + "table/caption";
        String caption = "Entity Providers ( Item(s))";
        int entityCount = getNumberOfRows(caption, tableName);
        String checkBoxIdx = "";
        for (int index = 0; index < entityCount; index++) {
            String tableId = "//div[@id='main']/form/div[9]/div[4]/" +
                    "div[2]/table." + (2 + index) + "." + 1;
            if (providerName.equals(selenium.getTable(tableId))) {
                int checkBoxCount = 3 + index ;
                checkBoxIdx = "xpath=/html/body/div[@id='main']/form/div[9]/" +
                        "div[4]/div[2]/table/tbody/tr[" + checkBoxCount +
                        "]/td[1]/input[1]"; 
                elementPresent = true;
                break;
            }
        }  
        if (elementPresent) {
            selenium.click(checkBoxIdx);
            Thread.sleep(1000);
            selenium.click("Federation.deleteEntityButton");
            for (int second = 0;; second++) {
                if (second >= 60) {
                    SeleneseTestCase.fail("timeout");
                }
                try {
                    if (selenium.isElementPresent("//div[@id='main']/form/" +
                            "table[1]/tbody/tr/td/table/tbody/tr/td/div[1]")) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(1000);
            }
        }
    } 
    
    /**
     * Method to delete the circle of trust
     * @param circleOfTrust
     * @throws java.lang.Exception
     */
    public void deleteCircleOFTrust(String circleOfTrust) throws Exception {
        String tableName = "//div[@id='main']/form/div[9]/div[3]/div[2]" +
                "/table/caption";
        String caption = "Circle of Trust ( Item(s)))";
        boolean elementPresent = false;
        int entityCount = getNumberOfRows(caption, tableName);
        String checkBoxIdx = "";
        for (int index = 0; index < entityCount; index++) {
            String tableId = "//div[@id='main']/form/div[9]/div[3]/div[2]/" +
                    "table." + (2 + index) + "." + 1;
            if (circleOfTrust.equals(selenium.getTable(tableId))) {
                int checkBoxCount = 3 + index ;
                checkBoxIdx = "xpath=/html/body/div[@id='main']/form/div[9]/" +
                        "div[3]/div[2]/table/tbody/tr[" + checkBoxCount +
                        "]/td[1]/input[1]"; 
                elementPresent = true;
                break;
            }
        }  
        if (elementPresent) {
            selenium.click(checkBoxIdx);
            selenium.click("Federation.deleteCOTButton");
            for (int second = 0;; second++) {
                if (second >= 60) {
                    SeleneseTestCase.fail("timeout");
                }
                try {
                    if (selenium.isElementPresent("//div[@id='main']/form/" +
                            "table[1]/tbody/tr/td/table/tbody/tr/td/div[2]")) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(1000);
            }
        }       
    }
    
    /**
     * Method to create a realm
     * @param realmName
     */
    public void createRealm(String realmName) {        
        try {
            if (selenium.isElementPresent("link=Access Control")) {
                selenium.click("link=Access Control");
                selenium.waitForPageToLoad("30000");
            }
            String rName = "";
            StringTokenizer tokenizer = new StringTokenizer(realmName, "/");
            while (tokenizer.hasMoreTokens()) {
                rName = tokenizer.nextToken();
            }
            if (!(selenium.isTextPresent(rName))) {
                selenium.click("RMRealm.tblButtonAdd");
                selenium.waitForPageToLoad("30000");
                selenium.type("psLbl1", rName);
                selenium.click("RMRealmAdd.button1");
                selenium.waitForPageToLoad("30000");
                for (int second = 0;; second++) {
                    if (second >= 60) {
                        SeleneseTestCase.fail("timeout");
                    }
                    try {
                        if (selenium.isTextPresent(rName)) {
                            break;
                        }
                    } catch (Exception e) {
                    }
                    Thread.sleep(1000);
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * Method to delete a realm
     * @param realmName
     * @throws java.lang.Exception
     */
    public void deleteRealm(String realmName) throws Exception {
        String caption = "Realms ( Item(s))";
        String rName = "";
        StringTokenizer tokenizer = new StringTokenizer(realmName, "/");
        while (tokenizer.hasMoreTokens()) {
            rName = tokenizer.nextToken();
        }
        int realmCount = getNumberOfRows(caption);
        String checkBoxIdx = "";
        for (int index = 0; index < realmCount; index++) {
            String tableId = "//div[11]/table." + (2 + index) + ".1";
            if (rName.equals(selenium.getTable(tableId))) {
                int checkBoxCount = 3 + index;
                checkBoxIdx = "xpath=/html/body/form/div[11]/table/" +
                        "tbody/tr[" + checkBoxCount + "]/td[1]/input[1]";
                break;
            }
        }
        selenium.click(checkBoxIdx);
        selenium.click("RMRealm.tblButtonDelete");
        selenium.waitForPageToLoad("30000");
        for (int second = 0;; second++) {
            if (second >= 60) {
                SeleneseTestCase.fail("timeout");
            }
            try {
                if (selenium.isElementPresent
                        ("//td/table/tbody/tr/td/div[1]")) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }
        SeleneseTestCase.assertFalse(selenium.isTextPresent(rName));  
    }
    
     /**
     * Method to set the selenium Instance
     * @param seleniumInstance
     */
    public void setSeleniumInstance(Selenium seleniumInstance) {
        selenium = seleniumInstance;
    }
    
    /**
     * Method to get the test uri for selenium
     * @param deployURI
     * @return
     */
     public String getTestURI(String deployURI) {
        String testuri = null;
        StringTokenizer st = new StringTokenizer(deployURI, "/");
        while (st.hasMoreTokens()) {
            testuri = st.nextToken();
        }       
        return testuri;
    } 
     
     /**
     * This method return value of test case property based on the key
     * @param key test case property key
     * @return test case property value
     */
    public String getParams(Map<String, String> resourceMap, 
            String prefixTestName, String key) {
        String paramKey = prefixTestName + "." + key;
        String value = "";
        if (resourceMap.containsKey(paramKey)) {
            value = resourceMap.get(paramKey);
        }
        return value;
    }
    
     /**
     * This method return value of test case property based on the key
     * @param key test case property key
     * @return test case property value
     */
    public String getParams(Map<String, String> resourceMap, 
            String prefixTestName, String key, int idx) {
        String paramKey = prefixTestName + "." + key + "." + idx;
        String value = "";
        if (resourceMap.containsKey(paramKey)) {
            value = resourceMap.get(paramKey);
        }
        return value;
    }
     
}

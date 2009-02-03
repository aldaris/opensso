/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 *
 * @author cmwesley
 */
public class NewSessionArgTest extends TestCommon {

    private AuthTestConfigUtil moduleConfig;
    private IDMCommon idmc;
    private List moduleDataList;
    private ResourceBundle testRb;
    private String configrbName = "authenticationConfigData";
    private String absoluteRealm;
    private String moduleServiceName;
    private String moduleSubConfigName;
    private String moduleSubConfigId;
    private String testUser;
    private String testPassword;


    public NewSessionArgTest() {
        super("NewSessionArgTest");
        moduleConfig = new AuthTestConfigUtil(configrbName);
        idmc = new IDMCommon();
    }

    @Parameters({"testRealm", "testModule"})
    @BeforeClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad",
        "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void setup(String testRealm, String testModule)
    throws Exception {
        Object[] params = {testRealm, testModule};
        entering("setup", params);

        try {
            testRb = ResourceBundle.getBundle("authentication" + fileseparator +
                    "NewSessionArgTest");

            testUser = testRb.getString("am-auth-newsessionarg-user");
            testPassword = testRb.getString("am-auth-newsessionarg-password");

            log(Level.FINEST, "setup", "testModule = " + testModule);
            log(Level.FINEST, "setup", "testUser = " + testUser);
            log(Level.FINEST, "setup", "testPassword = " + testPassword);

            Reporter.log("TestModule: " + testModule);
            Reporter.log("TestUser: " + testUser);
            Reporter.log("TestPassword: " + testPassword);

            absoluteRealm = testRealm;
            if (!testRealm.equals("/")) {
                if (testRealm.indexOf("/") != 0) {
                    absoluteRealm = "/" + testRealm;
                }
                log(Level.FINE, "setup", "Creating the sub-realm " + testRealm);
                moduleConfig.createRealms(absoluteRealm);
            }

            moduleDataList = moduleConfig.getModuleDataAsList(testModule);
            moduleServiceName = (String)testRb.getString(testModule +
                            ".module_servicename");
            moduleSubConfigName = (String)testRb.getString(testModule +
                            ".module_SubConfigname");
            moduleSubConfigId = (String)testRb.getString(testModule +
                            ".module_SubConfigid");

            log(Level.FINE, "setup", "Creating the authentication module " +
                    moduleSubConfigId);
            moduleConfig.createModuleInstances(testRealm, moduleServiceName,
                        moduleSubConfigName, moduleDataList, moduleSubConfigId);

            log(Level.FINE, "setup", "Creating user " + testUser + "...");
            List<String> userList = new ArrayList<String>();
            userList.add("sn=" + testUser);
            userList.add("cn=" + testUser);
            userList.add("userpassword=" + testPassword);
            userList.add("inetuserstatus=Active");            
            moduleConfig.setTestConfigRealm(testRealm);
            moduleConfig.createUser(userList, testUser);
            exiting("setup");
        } catch (Exception e) {
            cleanup(testRealm, testModule);
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } 
    }

    @Parameters({"testRealm", "testModule"})
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec",
        "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void testNewSessionArg(String testRealm, String testModule)
    throws Exception {
        Object[] params = {testRealm, testModule};
        entering("testNewSessionArg", params);
        String baseLoginString = null;
        String loginString = null;
        String newSessionString = null;
        WebClient wc = new WebClient();

        try {
            String authSuccessTitle =
                    testRb.getString("am-auth-newsessionarg-auth-success-msg");
            baseLoginString = protocol + ":" + "//" + host + ":" + port + uri +
                    "/UI/Login?";
            StringBuffer loginBuffer = new StringBuffer(baseLoginString);
            if (!testRealm.equals("/")) {
                loginBuffer.append("realm=").append(testRealm).append("&");
            }
            loginBuffer.append("module=").append(moduleSubConfigId).
                    append("&IDToken1=").append(testUser);
            
            if (!testModule.equals("anonymous")) {
                loginBuffer.append("&IDToken2=").append(testPassword);
            }

            loginString = loginBuffer.toString();
            log(Level.FINEST, "testNewSessionArg", "Zero page login URL = " +
                    loginString);
            URL loginURL = new URL(loginString);
            HtmlPage page = (HtmlPage)wc.getPage(loginURL);
            String afterLoginTitle = page.getTitleText();

            log(Level.FINEST, "testNewSessionArg", "The page title after " +
                    "authentication = " + afterLoginTitle);
            if (afterLoginTitle.equals(authSuccessTitle)) {
                newSessionString = baseLoginString + "arg=newsession";
                URL newSessionURL = new URL(newSessionString);
                HtmlPage newSessionPage =
                        (HtmlPage)wc.getPage(newSessionURL);
                String loginMsg =
                        testRb.getString("am-auth-newsessionarg-login-msg");
                assert getHtmlPageStringIndex(newSessionPage, loginMsg) != -1;
            } else {
                log(Level.SEVERE, "testNewSessionArg",
                        "Unexpected page title after authentication = " +
                        afterLoginTitle);
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "testNewSessionArg", e.getMessage(), params);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(wc, protocol + ":" + "//" + host + ":" + port + uri +
                    "/UI/Logout");
        }
        exiting("testNewSessionArg");
    }

    @Parameters({"testRealm", "testModule"})
    @AfterClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad",
        "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void cleanup(String testRealm, String testModule)
    throws Exception {
        Object[] params = {testModule, testRealm};
        entering("cleanup", params);
        SSOToken adminToken = null;
        SSOToken realmToken = null;


        try {
            log(Level.FINEST, "cleanup", "TestRealm: " + testRealm);
            log(Level.FINEST, "cleanup", "TestModule: " + testModule);
            Reporter.log("TestRealm: " + testRealm);
            Reporter.log("TestModule: " + testModule);

            List<IdType> idTypeList = new ArrayList();
            idTypeList.add(IdType.USER);
            List<String> idNameList = new ArrayList();
            idNameList.add(testUser);
            log(Level.FINE, "cleanup", "Deleting the user " + testUser);
            adminToken = getToken(adminUser, adminPassword, realm);
            idmc.deleteIdentity(adminToken, absoluteRealm, idTypeList,
                    idNameList);

            log(Level.FINE, "cleanup", "Deleting the auth module " +
                    moduleSubConfigId);
            moduleConfig.deleteModuleInstances(testRealm, moduleServiceName,
                    moduleSubConfigName);

            if (!absoluteRealm.equals("/")) {
                if (absoluteRealm.indexOf("/") != 0) {
                    absoluteRealm = "/" + testRealm;
                }
                log(Level.FINE, "cleanup", "Deleting the sub-realm " +
                        absoluteRealm);
                realmToken = getToken(adminUser, adminPassword, realm);
                idmc.deleteRealm(realmToken, absoluteRealm);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (adminToken != null) {
                destroyToken(adminToken);
            }
            if (realmToken != null) {
                destroyToken(realmToken);
            }
        }
        exiting("cleanup");
    }
}

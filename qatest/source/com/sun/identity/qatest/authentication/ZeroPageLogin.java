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
 * $Id: ZeroPageLogin.java,v 1.1 2007-02-06 19:55:33 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.TestCommon;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Tests zero page logins for module, service, org
 * realm, user and level objects.
 */
public class ZeroPageLogin extends TestCommon
{
    public ZeroPageLogin() {
        super("ZeroPageLogin");
    }

    /*
    * Tests zero page login for a module. This is a positive test
    */
    @Parameters({"module","passMsg"})
    @Test(groups = {"client"})
    public void testModuleLoginPositive(String module, String passMsg)
        throws Exception {
        Object[] params = {module};
        entering("testModuleLoginPositive", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + uri + 
                    "/UI/Login?module=" + module + "&IDToken1=" + adminUser + 
                    "&IDToken2=" + adminPassword;
            log(logLevel, "testModuleLoginPositive", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testModuleLoginPositive", page.getTitleText());
            assert page.getTitleText().equals(passMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testModuleLoginPositive", e.getMessage(), 
                    params);
            e.printStackTrace();
            throw e;
        }
        exiting("testModuleLoginPositive");
    }

    /*
    * Tests zero page login for a module. This is a negative test
    */
    @Parameters({"module","failMsg"})
    @Test(groups = {"client"})
    public void testModuleLoginNegative(String module, String failMsg)
        throws Exception {
        Object[] params = {module};
        entering("testModuleLoginNegative", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":"  + "//" + host + ":" + port + 
                    uri + "/UI/Login?module=" + module + "&IDToken1=" +
                    adminUser + "&IDToken2=" + adminPassword + "negative";
            log(logLevel, "testModuleLoginNegative", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testModuleLoginNegative", page.getTitleText());
            assert page.getTitleText().equals(failMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testModuleLoginNegative", e.getMessage(), 
                    params);
            e.printStackTrace();
            throw e;
        }
        exiting("testModuleLoginNegative");
    }

    /*
    * Tests zero page login for a service. This is a positive test
    */
    @Parameters({"service","passMsg"})
    @Test(groups = {"client"})
    public void testServiceLoginPositive(String service, String passMsg)
        throws Exception {
        Object[] params = {service};
        entering("testServiceLoginPositive", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + 
                    uri + "/UI/Login?service=" + service + "&IDToken1=" + 
                    adminUser + "&IDToken2=" + adminPassword;
            log(logLevel, "testServiceLoginPositive", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testServiceLoginPositive", page.getTitleText());
            assert page.getTitleText().equals(passMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testServiceLoginPositive", e.getMessage(), 
                    params);
            e.printStackTrace();
            throw e;
        }
        exiting("testServiceLoginPositive");
    }

    /*
    * Tests zero page login for a service. This is a negative test
    */
    @Parameters({"service","failMsg"})
    @Test(groups = {"client"})
    public void testServiceLoginNegative(String service, String failMsg)
        throws Exception {
        Object[] params = {service};
        entering("testServiceLoginNegative", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + 
                    uri + "/UI/Login?service=" + service + "&IDToken1=" + 
                    adminUser + "&IDToken2=" + adminPassword + "negative";
            log(logLevel, "testServiceLoginNegative", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testServiceLoginNegative", page.getTitleText());
            assert page.getTitleText().equals(failMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testServiceLoginNegative", e.getMessage(), 
                    params);
            e.printStackTrace();
            throw e;
        }
        exiting("testServiceLoginNegative");
    }

    /*
    * Tests zero page login for a organization. This is a positive test
    */
    @Parameters({"org","passMsg"})
    @Test(groups = {"client"})
    public void testOrgLoginPositive(String org, String passMsg)
        throws Exception {
        Object[] params = {org};
        entering("testOrgLoginPositive", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + 
                    uri + "/UI/Login?org=" + org + "&IDToken1=" + adminUser + 
                    "&IDToken2=" + adminPassword;
            log(logLevel, "testOrgLoginPositive", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testOrgLoginPositive", page.getTitleText());
            assert page.getTitleText().equals(passMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testOrgLoginPositive", e.getMessage(), params);
            e.printStackTrace();
            throw e;
        }
        exiting("testOrgLoginPositive");
    }

    /*
    * Tests zero page login for a organization. This is a negative test
    */
    @Parameters({"org","failMsg"})
    @Test(groups = {"client"})
    public void testOrgLoginNegative(String org, String failMsg)
        throws Exception {
        Object[] params = {org};
        entering("testOrgLoginNegative", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + 
                    uri + "/UI/Login?org=" + org + "&IDToken1=" + adminUser + 
                    "&IDToken2=" + adminPassword + "negative";
            log(logLevel, "testOrgLoginNegative", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testOrgLoginNegative", page.getTitleText());
            assert page.getTitleText().equals(failMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testOrgLoginNegative", e.getMessage(), 
                    params);
            e.printStackTrace();
            throw e;
        }
        exiting("testOrgLoginNegative");
    }

    /*
    * Tests zero page login for a realm. This is a positive test
    */
    @Parameters({"realm","passMsg"})
    @Test(groups = {"client"})
    public void testRealmLoginPositive(String realm, String passMsg)
        throws Exception {
        Object[] params = {realm};
        entering("testRealmLoginPositive", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + 
                    uri + "/UI/Login?realm=" + realm + "&IDToken1=" + 
                    adminUser + "&IDToken2=" + adminPassword;
            log(logLevel, "testRealmLoginPositive", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testRealmLoginPositive", page.getTitleText());
            assert page.getTitleText().equals(passMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testRealmLoginPositive", e.getMessage(), 
                    params);
            e.printStackTrace();
            throw e;
        }
        exiting("testRealmLoginPositive");
    }

    /*
    * Tests zero page login for a realm. This is a negative test
    */
    @Parameters({"realm","failMsg"})
    @Test(groups = {"client"})
    public void testRealmLoginNegative(String realm, String failMsg)
        throws Exception {
        Object[] params = {realm};
        entering("testRealmLoginNegative", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + uri +
                    "/UI/Login?realm=" + realm + "&IDToken1=" + adminUser + 
                    "&IDToken2=" + adminPassword + "negative";
            log(logLevel, "testRealmLoginNegative", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testRealmLoginNegative", page.getTitleText());
            assert page.getTitleText().equals(failMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testRealmLoginNegative", e.getMessage(), params);
            e.printStackTrace();
            throw e;
        }
        exiting("testRealmLoginNegative");
    }

    /*
    * Tests zero page login for a level. This is a positive test
    */
    @Parameters({"level","passMsg"})
    @Test(groups = {"client"})
    public void testLevelLoginPositive(String level, String passMsg)
        throws Exception {
        Object[] params = {level};
        entering("testLevelLoginPositive", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + uri +
                    "/UI/Login?level=" + level + "&IDToken1=" + adminUser + 
                    "&IDToken2=" + adminPassword;
            log(logLevel, "testLevelLoginPositive", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testLevelLoginPositive", page.getTitleText());
            assert page.getTitleText().equals(passMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testLevelLoginPositive", e.getMessage(), params);
            e.printStackTrace();
            throw e;
        }
        exiting("testLevelLoginPositive");
    }

    /*
    * Tests zero page login for a level. This is a negative test
    */
    @Parameters({"level","failMsg"})
    @Test(groups = {"client"})
    public void testLevelLoginNegative(String level, String failMsg)
        throws Exception {
        Object[] params = {level};
        entering("testLevelLoginNegative", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + uri +
                    "/UI/Login?level=" + level + "&IDToken1=" + adminUser + 
                    "&IDToken2=" + adminPassword + "negative";
            log(logLevel, "testLevelLoginNegative", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testLevelLoginNegative", page.getTitleText());
            assert page.getTitleText().equals(failMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testLevelLoginNegative", e.getMessage(), params);
            e.printStackTrace();
            throw e;
        }
        exiting("testLevelLoginNegative");
    }

    /*
    * Tests zero page login for a user. This is a positive test
    */
    @Parameters({"user","passMsg"})
    @Test(groups = {"client"})
    public void testUserLoginPositive(String user, String passMsg)
        throws Exception {
        Object[] params = {user};
        entering("testUserLoginPositive", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + uri +
                    "/UI/Login?" + "IDToken1=" + adminUser + "&IDToken2=" +
                    adminPassword;
            log(logLevel, "testUserLoginPositive", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testUserLoginPositive", page.getTitleText());
            assert page.getTitleText().equals(passMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testUserLoginPositive", e.getMessage(), params);
            e.printStackTrace();
            throw e;
        }
        exiting("testUserLoginPositive");
    }

    /*
    * Tests zero page login for a user. This is a negative test
    */
    @Parameters({"user","failMsg"})
    @Test(groups = {"client"})
    public void testUserLoginNegative(String user, String failMsg)
        throws Exception {
        Object[] params = {user};
        entering("testUserLoginNegative", params);
        try {
            WebClient wc = new WebClient();
            String strTest = protocol + ":" + "//" + host + ":" + port + uri +
                    "/UI/Login?" + "IDToken1=" + adminUser + "&IDToken2=" +
                    adminPassword + "negative";
            log(logLevel, "testUserLoginNegative", strTest);
            URL url = new URL(strTest);
            HtmlPage page = (HtmlPage)wc.getPage( url );
            log(logLevel, "testUserLoginNegative", page.getTitleText());
            assert page.getTitleText().equals(failMsg);
        } catch (Exception e) {
            log(Level.SEVERE, "testUserLoginNegative", e.getMessage(), params);
            e.printStackTrace();
            throw e;
        }
        exiting("testUserLoginNegative");
    }
}

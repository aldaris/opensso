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
 * $Id: IdSvcsREST.java,v 1.1 2008-01-22 18:53:59 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idsvcs;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.PolicyCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.TestCommon;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class tests the REST interfaces associated with Identity Web Services
 */
public class IdSvcsREST extends TestCommon {
    
    private ResourceBundle rb_amconfig;
    private String baseDir;
    private String serverURI;
    private String polName = "idsvcsRESTPolicyTest";
    private String userName = "idsvcsresttest";
    private HtmlPage page;
    private SSOToken admintoken;
    private SSOToken usertoken;
    private IDMCommon idmc;
    private PolicyCommon pc;
    private WebClient webClient;

    /**
     * Creates common objects.
     */
    public IdSvcsREST()
    throws Exception {
        super("IdSvcsREST");
        rb_amconfig = ResourceBundle.getBundle("AMConfig");
        admintoken = getToken(adminUser, adminPassword, basedn);
        idmc = new IDMCommon();
        pc = new PolicyCommon();
        baseDir = getBaseDir() + System.getProperty("file.separator")
            + rb_amconfig.getString(TestConstants.KEY_ATT_SERVER_NAME)
            + System.getProperty("file.separator") + "built"
            + System.getProperty("file.separator") + "classes"
            + System.getProperty("file.separator");
    }
    
    /**
     *  Creates required users and policy.
     */
    @BeforeClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void setup()
    throws Exception {
        entering("setup", null);

        serverURI = protocol + ":" + "//" + host + ":" + port + uri;

        Map map = new HashMap();
        Set set = new HashSet();
        set.add(userName);
        map.put("sn", set);
        set = new HashSet();
        set.add(userName);
        map.put("cn", set);
        set = new HashSet();
        set.add(userName);
        map.put("userpassword", set);
        set = new HashSet();
        set.add("Active");
        map.put("inetuserstatus", set);
        set = new HashSet();
        set.add(userName + "alias1");
        set.add(userName + "alias2");
        set.add(userName + "alias3");
        map.put("iplanet-am-user-alias-list", set);

        idmc.createIdentity(admintoken, realm, IdType.USER, userName, map);

        String xmlFile = "idsvcs-rest-policy-test.xml";
        createPolicyXML(xmlFile);
        pc.createPolicy(xmlFile, realm);

        exiting("setup");
    }

    /**
     * This test validates the authentication REST interface for super admin
     * user
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testSuperAdminAuthenticate()
    throws Exception {
        entering("testSuperAdminAuthenticate", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + adminUser +
                    "&password=" + adminPassword);
            log(Level.FINEST, "testSuperAdminAuthenticate", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            SSOTokenManager stMgr = SSOTokenManager.getInstance();
            usertoken = stMgr.createSSOToken(s1);
            if (!validateToken(usertoken))
                assert false;
        } catch (Exception e) {
            log(Level.SEVERE, "testSuperAdminAuthenticate", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (validateToken(usertoken))
                destroyToken(usertoken);
            Reporter.log("This test validates the authentication REST" +
                    " interface for super admin user");
        }
        exiting("testSuperAdminAuthenticate");
    }

    /**
     * This test validates the authentication REST interface for a normal user
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserAuthenticate()
    throws Exception {
        entering("testNormalUserAuthenticate", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserAuthenticate", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            SSOTokenManager stMgr = SSOTokenManager.getInstance();
            usertoken = stMgr.createSSOToken(s1);
            if (!validateToken(usertoken))
                assert false;
        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserAuthenticate", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (validateToken(usertoken))
                destroyToken(usertoken);
            Reporter.log("This test validates the authentication REST" +
                    " interface for a normal user");
        }
        exiting("testNormalUserAuthenticate");
    }

    /**
     * This test validates the authorization REST interface for a normal user
     * where policy resource has allow for both GET and POST request. The
     * action under test is GET.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserPolAAGet()
    throws Exception {
        entering("testNormalUserPolAAGet", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserPolAAGet", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authorize?uri=http://www.idsvcs1.com:80" +
                    "&action=GET&subjectid=" + URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, "boolean=true") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserPolAAGet", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the authorization REST" +
                    " interface for a normal user where policy resource has" +
                    " allow for both GET and POST request. The action under" +
                    " test is GET.");
        }
        exiting("testNormalUserPolAAGet");
    }

    /**
     * This test validates the authorization REST interface for a normal user
     * where policy resource has allow for both GET and POST request. The
     * action under test is POST.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserPolAAPost()
    throws Exception {
        entering("testNormalUserPolAAPost", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserPolAAPost", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authorize?uri=http://www.idsvcs1.com:80" +
                    "&action=POST&subjectid=" + URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, "boolean=true") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserPolAAPost", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the authorization REST" +
                    " interface for a normal user where policy resource" +
                    " has allow for both GET and POST request. The action" +
                    " under test is POST.");
        }
        exiting("testNormalUserPolAAPost");
    }

    /**
     * This test validates the authorization REST interface for a normal user
     * where policy resource has allow for GET and deny for POST request. The
     * action under test is GET.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserPolADGet()
    throws Exception {
        entering("testNormalUserPolADGet", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserPolADGet", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authorize?uri=http://www.idsvcs2.com:80" +
                    "&action=GET&subjectid=" + URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, "boolean=true") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserPolADGet", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the authorization REST" +
                    " interface for a normal user where policy resource has" +
                    " allow for GET and deny for POST request. The action" +
                    " under test is GET.");
        }
        exiting("testNormalUserPolADGet");
    }

    /**
     * This test validates the authorization REST interface for a normal user
     * where policy resource has allow for GET and deny for POST request. The
     * action under test is POST.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserPolADPost()
    throws Exception {
        entering("testNormalUserPolADPost", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserPolADPost", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authorize?uri=http://www.idsvcs2.com:80" +
                    "&action=POST&subjectid=" + URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, "boolean=false") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserPolADPost", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the authorization REST" +
                    " interface for a normal user where policy resource has" +
                    " allow for GET and deny for POST request. The action" +
                    " under test is POST.");
        }
        exiting("testNormalUserPolADPost");
    }

    /**
     * This test validates the authorization REST interface for a normal user
     * where policy resource has deny for GET and allow for POST request. The
     * action under test is GET.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserPolDAGet()
    throws Exception {
        entering("testNormalUserPolDAGet", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserPolDAGet", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authorize?uri=http://www.idsvcs3.com:80" +
                    "&action=GET&subjectid=" + URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, "boolean=false") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserPolDAGet", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the authorization REST" +
                    " interface for a normal user where policy resource has" +
                    " deny for GET and allow for POST request. The action" +
                    " under test is GET.");
        }
        exiting("testNormalUserPolDAGet");
    }

    /**
     * This test validates the authorization REST interface for a normal user
     * where policy resource has deny for GET and allow for POST request. The
     * action under test is POST.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserPolDAPost()
    throws Exception {
        entering("testNormalUserPolDAPost", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserPolDAPost", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authorize?uri=http://www.idsvcs3.com:80" +
                    "&action=POST&subjectid=" + URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, "boolean=true") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserPolDAPost", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the authorization REST" +
                    " interface for a normal user where policy resource has" +
                    " deny for GET and allow for POST request. The action" +
                    " under test is POST.");
        }
        exiting("testNormalUserPolDAPost");
    }

    /**
     * This test validates the authorization REST interface for a normal user
     * where policy resource has deny for both GET and POST request. The
     * action under test are both GET.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserPolDDGet()
    throws Exception {
        entering("testNormalUserPolDDGet", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserPolDDGet", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authorize?uri=http://www.idsvcs4.com:80" +
                    "&action=GET&subjectid=" + URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, "boolean=false") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserPolDDGet", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the authorization REST" +
                    " interface for a normal user where policy resource has" +
                    " deny for both GET and POST request. The action under" +
                    " test are both GET.");
        }
        exiting("testNormalUserPolDDGet");
    }

    /**
     * This test validates the authorization REST interface for a normal user
     * where policy resource has deny for both GET and POST request. The action
     * under test is POST.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserPolDDPost()
    throws Exception {
        entering("testNormalUserPolDDPost", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName + 
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserPolDDPost", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authorize?uri=http://www.idsvcs4.com:80" +
                    "&action=POST&subjectid=" + URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, "boolean=false") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserPolDDPost", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the authorization REST" +
                    " interface for a normal user where policy resource has" +
                    " deny for both GET and POST request. The action under" +
                    " test are both POST.");
        }
        exiting("testNormalUserPolDDPost");
    }

    /**
     * This test validates the attributes REST interface for a normal user. The
     * current tests validates the retrival of multivalued attribute
     * iplanet-am-user-alias-list.
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testNormalUserAttributes()
    throws Exception {
        entering("testNormalUserAttributes", null);
        try {
            webClient = new WebClient();
            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/authenticate?username=" + userName +
                    "&password=" + userName);
            log(Level.FINEST, "testNormalUserAttributes", "Token:\n" +
                    page.asXml());
            String s0 = page.asXml();
            int i1 = s0.indexOf("=");
            int i2 = s0.indexOf("</body");
            String s1 = s0.substring(i1 + 1, i2).trim();

            page = (HtmlPage)webClient.getPage(serverURI +
                    "/identity/attributes?subjectid=" +
                    URLEncoder.encode(s1, "UTF-8"));
            if (getHtmlPageStringIndex(page, userName + "alias1") == -1)
                assert false;
            if (getHtmlPageStringIndex(page, userName + "alias2") == -1)
                assert false;
            if (getHtmlPageStringIndex(page, userName + "alias3") == -1)
                assert false;

        } catch (Exception e) {
            log(Level.SEVERE, "testNormalUserAttributes", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("This test validates the attributes REST" +
                    " interface for a normal user. The current tests" +
                    " validates the retrival of multivalued attribute" +
                    " iplanet-am-user-alias-list.");
        }
        exiting("testNormalUserAttributes");
    }

    /**
     * Cleanup method. This method:
     * (a) Delete users
     * (b) Deletes policies
     */
    @AfterClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        try {
            log(Level.FINEST, "cleanup", "Deleting User: " + userName);
            Reporter.log("Deleting User :" + userName);
            idmc.deleteIdentity(admintoken, realm, IdType.USER, userName);

            log(Level.FINEST, "cleanup", "Deleting Policy: " + polName);
            Reporter.log("Deleting Policy :" + polName);
            pc.deletePolicy(polName, realm);

            if (validateToken(admintoken))
                destroyToken(admintoken);
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("cleanup");
    }

    /**
     * Generates XML for creating the policy.
     */
    private void createPolicyXML(String xmlFile)
    throws Exception {
        FileWriter fstream = new FileWriter(baseDir + xmlFile);
        BufferedWriter out = new BufferedWriter(fstream);

        out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        out.write(newline);
        out.write("<!DOCTYPE Policies");
        out.write(newline);
        out.write("PUBLIC \"-//Sun Java System Access Manager 7.1 2006Q3");
        out.write("Admin CLI DTD//EN\"");
        out.write(newline);
        out.write("\"jar://com/sun/identity/policy/policyAdmin.dtd\">");
        out.write(newline);

        out.write("<Policies>");
        out.write(newline);

        out.write("<Policy name=\"" + polName + "\" referralPolicy=\"false\"");
        out.write(" active=\"true\">");
        out.write(newline);

        out.write("<Rule name=\"idsvcs1\">");
        out.write(newline);
        out.write("<ServiceName name=\"iPlanetAMWebAgentService\"/>");
        out.write(newline);
        out.write("<ResourceName name=\"http://www.idsvcs1.com:80\"/>");
        out.write(newline);
        out.write("<AttributeValuePair>");
        out.write(newline);
        out.write("<Attribute name=\"POST\"/>");
        out.write(newline);
        out.write("<Value>allow</Value>");
        out.write(newline);
        out.write("</AttributeValuePair>");
        out.write(newline);
        out.write("<AttributeValuePair>");
        out.write(newline);
        out.write("<Attribute name=\"GET\"/>");
        out.write(newline);
        out.write("<Value>allow</Value>");
        out.write(newline);
        out.write("</AttributeValuePair>");
        out.write(newline);
        out.write("</Rule>");
        out.write(newline);

        out.write("<Rule name=\"idsvcs2\">");
        out.write(newline);
        out.write("<ServiceName name=\"iPlanetAMWebAgentService\"/>");
        out.write(newline);
        out.write("<ResourceName name=\"http://www.idsvcs2.com:80\"/>");
        out.write(newline);
        out.write("<AttributeValuePair>");
        out.write(newline);
        out.write("<Attribute name=\"POST\"/>");
        out.write(newline);
        out.write("<Value>deny</Value>");
        out.write(newline);
        out.write("</AttributeValuePair>");
        out.write(newline);
        out.write("<AttributeValuePair>");
        out.write(newline);
        out.write("<Attribute name=\"GET\"/>");
        out.write(newline);
        out.write("<Value>allow</Value>");
        out.write(newline);
        out.write("</AttributeValuePair>");
        out.write(newline);
        out.write("</Rule>");
        out.write(newline);

        out.write("<Rule name=\"idsvcs3\">");
        out.write(newline);
        out.write("<ServiceName name=\"iPlanetAMWebAgentService\"/>");
        out.write(newline);
        out.write("<ResourceName name=\"http://www.idsvcs3.com:80\"/>");
        out.write(newline);
        out.write("<AttributeValuePair>");
        out.write(newline);
        out.write("<Attribute name=\"POST\"/>");
        out.write(newline);
        out.write("<Value>allow</Value>");
        out.write(newline);
        out.write("</AttributeValuePair>");
        out.write(newline);
        out.write("<AttributeValuePair>");
        out.write(newline);
        out.write("<Attribute name=\"GET\"/>");
        out.write(newline);
        out.write("<Value>deny</Value>");
        out.write(newline);
        out.write("</AttributeValuePair>");
        out.write(newline);
        out.write("</Rule>");
        out.write(newline);

        out.write("<Rule name=\"idsvcs4\">");
        out.write(newline);
        out.write("<ServiceName name=\"iPlanetAMWebAgentService\"/>");
        out.write(newline);
        out.write("<ResourceName name=\"http://www.idsvcs4.com:80\"/>");
        out.write(newline);
        out.write("<AttributeValuePair>");
        out.write(newline);
        out.write("<Attribute name=\"POST\"/>");
        out.write(newline);
        out.write("<Value>deny</Value>");
        out.write(newline);
        out.write("</AttributeValuePair>");
        out.write(newline);
        out.write("<AttributeValuePair>");
        out.write(newline);
        out.write("<Attribute name=\"GET\"/>");
        out.write(newline);
        out.write("<Value>deny</Value>");
        out.write(newline);
        out.write("</AttributeValuePair>");
        out.write(newline);
        out.write("</Rule>");
        out.write(newline);

        out.write("<Subjects name=\"idsvcssubjects\" description=\"\">");
        out.write(newline);
        out.write("<Subject name=\"idsvcssubj\" type=\"AuthenticatedUsers\"");
        out.write(" includeType=\"inclusive\">");
        out.write(newline);
        out.write("</Subject>");
        out.write(newline);
        out.write("</Subjects>");
        out.write(newline);

        out.write("</Policy>");
        out.write(newline);
        out.write("</Policies>");
        out.close();
    }
}

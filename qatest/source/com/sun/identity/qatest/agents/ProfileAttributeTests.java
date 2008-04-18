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
 * $Id: ProfileAttributeTests.java,v 1.1 2008-04-18 20:04:03 nithyas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.agents;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.AgentsCommon;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.SMSCommon;
import com.sun.identity.qatest.common.SMSConstants;
import com.sun.identity.qatest.common.TestCommon;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import org.testng.Reporter;

/**
 * This class tests Header attributes related to Profile
 * Attributes are tested using a webapp or a cgi script
 * which can read the header attributes in the browser. Attributes
 * are tested for new and updated values for different profile.
 */

public class ProfileAttributeTests extends TestCommon {
    
    private boolean executeAgainstOpenSSO;
    private String logoutURL;
    private String strScriptURL;
    private String strLocRB = "HeaderAttributeTests";
    private String strGblRB = "agentsGlobal";
    private String resource;
    private URL url;
    private WebClient webClient;
    private int polIdx;
    private int resIdx;
    private int iIdx;
    private AgentsCommon mpc;
    private AMIdentity amid;
    private IDMCommon idmc;
    private SMSCommon smsc;
    private ResourceBundle rbg;
    private SSOToken usertoken;
    private SSOToken admintoken;
    private int sleepTime = 2000;
    private int pollingTime;
    private String strAgentType;
    private String strHeaderFetchMode;
    private String agentId;
    
    /**
     * Instantiated different helper class objects
     */
    public ProfileAttributeTests() 
    throws Exception{
        super("ProfileAttributeTests");
        mpc = new AgentsCommon();
        idmc = new IDMCommon();
        rbg = ResourceBundle.getBundle(strGblRB);
        executeAgainstOpenSSO = new Boolean(rbg.getString(strGblRB +
                ".executeAgainstOpenSSO")).booleanValue();
        pollingTime = new Integer(rbg.getString(strGblRB +
                ".pollingInterval")).intValue();
        admintoken = getToken(adminUser, adminPassword, basedn);
        smsc = new SMSCommon(admintoken);
        logoutURL = protocol + ":" + "//" + host + ":" + port + uri +
                "/UI/Logout";
    }

    /**
     * Two Argument constructor initialising the ScriptURL 
     * and resource being tested
     */
    public ProfileAttributeTests(String strScriptURL, String strResource) 
      throws Exception {
        this();
        url = new URL(strScriptURL);
        resource = strResource;
   }
    
     /**
     * Evaluates newly created static single valued profile attribute
     */
    public void evaluateNewSingleValuedProfileAttribute()
    throws Exception {
        entering("evaluateNewSingleValuedProfileAttribute", null);
        webClient = new WebClient();
        try {
            Reporter.log("Resource: " + url);   
            Reporter.log("Username: " + "pauser");   
            Reporter.log("Password: " + "pauser");   
            Reporter.log("Expected Result: " + "HTTP_PROFILE_CN:pauser"); 
            idmc.createIdentity(admintoken, realm, IdType.ROLE, "parole1",
                    new HashMap());
            idmc.addUserMember(admintoken, "pauser", "parole1", IdType.ROLE);
            Map map = new HashMap();
            Set set = new HashSet();
            set.add("(objectclass=person)");
            map.put("nsRoleFilter", set);
            idmc.createIdentity(admintoken, realm, IdType.FILTEREDROLE,
                    "filparole1", map);
            HtmlPage page = consoleLogin(webClient, resource, "pauser",
                    "pauser");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "HTTP_PROFILE_CN:pauser");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateNewSingleValuedProfileAttribute",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("evaluateNewSingleValuedProfileAttribute");
    }

    /**
     * Evaluates newly created static multi valued profile attribute
     */
    public void evaluateNewMultiValuedProfileAttribute()
    throws Exception {
        entering("evaluateNewMultiValuedProfileAttribute", null);
        webClient = new WebClient();
        try {
            Reporter.log("Resource: " + url);   
            Reporter.log("Username: " + "pauser");   
            Reporter.log("Password: " + "pauser");   
            Reporter.log("Expected Result: " + 
                    "HTTP_PROFILE_ALIAS:pauseralias1|pauseralias2"); 
            HtmlPage page = consoleLogin(webClient, resource, "pauser",
                    "pauser");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page
                    , "HTTP_PROFILE_ALIAS:pauseralias1|pauseralias2");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateNewMultiValuedProfileAttribute",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("evaluateNewMultiValuedProfileAttribute");
    }

    /**
     * Evaluates newly created dynamic multi valued profile attribute related
     * to static roles
     */
    public void evaluateNewNsRoleProfileAttribute()
    throws Exception {
        entering("evaluateNewNsRoleProfileAttribute", null);
        webClient = new WebClient();
        try {
            Reporter.log("Resource: " + url);   
            Reporter.log("Username: " + "pauser");   
            Reporter.log("Password: " + "pauser");   
            HtmlPage page = consoleLogin(webClient, resource, "pauser",
                    "pauser");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            String strNsrole = "HTTP_PROFILE_NSROLE:" +
                    "cn=containerdefaulttemplaterole," + basedn +
                    "|cn=filparole1," + basedn + "|cn=parole1," + basedn;
            log(Level.FINEST, "evaluateNewNsRoleProfileAttribute",
                    "NSROLE: " + strNsrole);
            iIdx = getHtmlPageStringIndex(page, strNsrole);
            Reporter.log("Expected Result: " + strNsrole); 
            assert (iIdx != -1);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "cn=parole1");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateNewNsRoleProfileAttribute",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("evaluateNewNsRoleProfileAttribute");
    }

    /**
     * Evaluates newly created dynamic multi valued profile attribute related
     * to dynamic roles
     */
    public void evaluateNewFilteredRoleProfileAttribute()
    throws Exception {
        entering("evaluateNewFilteredRoleProfileAttribute", null);
        webClient = new WebClient();
        try {
            HtmlPage page = consoleLogin(webClient, resource, "pauser",
                    "pauser");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            String strNsrole = "HTTP_PROFILE_NSROLE:" +
                    "cn=containerdefaulttemplaterole," + basedn +
                    "|cn=filparole1," + basedn + "|cn=parole1," + basedn;
            log(Level.FINEST, "evaluateNewFilteredRoleProfileAttribute",
                    "NSROLE: " + strNsrole);
            Reporter.log("Resource: " + url);   
            Reporter.log("Username: " + "pauser");   
            Reporter.log("Password: " + "pauser");   
            Reporter.log("Expected Result: " + strNsrole); 
            iIdx = getHtmlPageStringIndex(page, strNsrole);
            assert (iIdx != -1);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "cn=filparole1");
            assert (iIdx != -1);
            iIdx = -1;
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateNewFilteredRoleProfileAttribute",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("evaluateNewFilteredRoleProfileAttribute");
    }

    /**
     * Evaluates updated static single valued profile attribute
     */
    public void evaluateUpdatedSingleValuedProfileAttribute()
    throws Exception {
        entering("evaluateUpdatedSingleValuedProfileAttribute", null);
        webClient = new WebClient();
        try {
            HtmlPage page = consoleLogin(webClient, resource, "pauser",
                    "pauser");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "HTTP_PROFILE_CN:pauser");
            assert (iIdx != -1);
            Map map = new HashMap();
            Set set = new HashSet();
            set.add("pauserupdated");
            map.put("cn", set);
            log(Level.FINEST, "evaluateUpdatedSingleValuedProfileAttribute",
                    "Update Attribute List: " + map);
            idmc.modifyIdentity(idmc.getFirstAMIdentity(admintoken, "pauser",
                    IdType.USER, realm), map);
            usertoken = getToken("pauser", "pauser", basedn);
            set = idmc.getIdentityAttribute(usertoken, "iPlanetAMUserService",
                    "cn");
            assert (set.contains("pauserupdated"));
            boolean isFound = false;
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < pollingTime &&
                !isFound) {
                page = (HtmlPage)webClient.getPage(url);
                iIdx = -1;
                iIdx = getHtmlPageStringIndex(page, "HTTP_PROFILE_CN:" +
                    "pauserupdated",false);
                if (iIdx != -1)
                    isFound = true;
            }
            Reporter.log("Resource: " + url);   
            Reporter.log("Username: " + "pauser");   
            Reporter.log("Password: " + "pauser");   
            Reporter.log("Expected Result: " + "HTTP_PROFILE_CN:" +
                    "pauserupdated"); 
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "HTTP_PROFILE_CN:" +
                    "pauserupdated");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateUpdatedSingleValuedProfileAttribute",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
            destroyToken(usertoken);
        }
        exiting("evaluateUpdatedSingleValuedProfileAttribute");
    }

    /**
     * Evaluates updated static multi valued profile attribute
     */
    public void evaluateUpdatedMultiValuedProfileAttribute()
    throws Exception {
        entering("evaluateUpdatedMultiValuedProfileAttribute", null);

        webClient = new WebClient();
        try {
            HtmlPage page = consoleLogin(webClient, resource, "pauser",
                    "pauser");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "HTTP_PROFILE_ALIAS:" +
                    "pauseralias1|pauseralias2");
            assert (iIdx != -1);
            Map map = new HashMap();
            Set set = new HashSet();
            set.add("pauseralias3");
            map.put("iplanet-am-user-alias-list", set);
            log(Level.FINEST, "evaluateUpdatedMultiValuedProfileAttribute",
                    "Update Attribute List: " + map);
            idmc.modifyIdentity(idmc.getFirstAMIdentity(admintoken, "pauser",
                    IdType.USER, realm), map);
            boolean isFound = false;
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < pollingTime &&
                !isFound) {
                page = (HtmlPage)webClient.getPage(url);
                iIdx = -1;
                iIdx = getHtmlPageStringIndex(page, "HTTP_PROFILE_ALIAS:" +
                    "pauseralias3", false);
                if (iIdx != -1)
                    isFound = true;
            }
            Reporter.log("Resource: " + url);   
            Reporter.log("Username: " + "pauser");   
            Reporter.log("Password: " + "pauser");   
            Reporter.log("Expected Result: " + "HTTP_PROFILE_ALIAS:" +
                    "pauseralias3"); 
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "HTTP_PROFILE_ALIAS:" +
                    "pauseralias3");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateUpdatedMultiValuedProfileAttribute",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("evaluateUpdatedMultiValuedProfileAttribute");
    }

    /**
     * Evaluates updated dynamic multi valued profile attribute related to
     * static roles
     */
    public void evaluateUpdatedNsRoleProfileAttribute()
    throws Exception {
        entering("evaluateUpdatedNsRoleProfileAttribute", null);
        webClient = new WebClient();
        try {
            HtmlPage page = consoleLogin(webClient, resource, "pauser",
                    "pauser");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            String strNsrole = "HTTP_PROFILE_NSROLE:" +
                    "cn=containerdefaulttemplaterole," + basedn +
                    "|cn=filparole1," + basedn + "|cn=parole1," + basedn;
            log(Level.FINEST, "evaluateUpdatedNsRoleProfileAttribute",
                    "NSROLE: "+ strNsrole);
            iIdx = getHtmlPageStringIndex(page, strNsrole);
            assert (iIdx != -1);
            iIdx = getHtmlPageStringIndex(page, "cn=parole1");
            assert (iIdx != -1);
            idmc.createIdentity(admintoken, realm, IdType.ROLE, "parole2",
                    new HashMap());
            idmc.addUserMember(admintoken, "pauser", "parole2", IdType.ROLE);
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            strNsrole = "HTTP_PROFILE_NSROLE:" +
                    "cn=containerdefaulttemplaterole," + basedn +
                    "|cn=filparole1," + basedn +
                    "|cn=parole1," + basedn + "|cn=parole2," + basedn;
            log(Level.FINEST, "evaluateUpdatedNsRoleProfileAttribute",
                    "NSROLE: "+ strNsrole);
            boolean isFound = false;
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < pollingTime &&
                !isFound) {
                page = (HtmlPage)webClient.getPage(url);
                iIdx = -1;
                iIdx = getHtmlPageStringIndex(page, strNsrole, false);
                if (iIdx != -1)
                    isFound = true;
            }
            Reporter.log("Resource: " + url);   
            Reporter.log("Username: " + "pauser");   
            Reporter.log("Password: " + "pauser");   
            Reporter.log("Expected Result: " + strNsrole); 
            iIdx = getHtmlPageStringIndex(page, strNsrole);
            assert (iIdx != -1);
            iIdx = getHtmlPageStringIndex(page, "cn=parole2");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateUpdatedNsRoleProfileAttribute",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("evaluateUpdatedNsRoleProfileAttribute");
    }

    /**
     * Evaluates updated dynamic multi valued profile attribute related to
     * dynamic roles
     */
    public void evaluateUpdatedFilteredRoleProfileAttribute()
    throws Exception {
        entering("evaluateUpdatedFilteredRoleProfileAttribute", null);
        webClient = new WebClient();
        try {
            HtmlPage page = consoleLogin(webClient, resource, "pauser",
                    "pauser");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "cn=filparole1");
            assert (iIdx != -1);
            Map map = new HashMap();
            Set set = new HashSet();
            set.add("(mail=abc@def.com)");
            map.put("nsRoleFilter", set);
            log(Level.FINEST, "evaluateUpdatedFilteredRoleProfileAttribute",
                    "Update Attribute List: " + map);
            log(Level.FINEST, "evaluateUpdatedFilteredRoleProfileAttribute", 
                    "Recreating adminToken");
            destroyToken(admintoken);
            admintoken = getToken(adminUser, adminPassword, basedn);
            idmc.modifyIdentity(idmc.getFirstAMIdentity(admintoken,
                    "filparole1", IdType.FILTEREDROLE, realm), map);
            boolean isFound = false;
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < pollingTime &&
                !isFound) {
                page = (HtmlPage)webClient.getPage(url);
                iIdx = -1;
                iIdx = getHtmlPageStringIndex(page, "cn=filparole1", false);
                if (iIdx != -1) {
                    isFound = true;
                    log (Level.FINE, "evaluateUpdatedFilteredRole" + 
                            "ProfileAttribute", "Found cn=filparole1 after " + 
                            "modifying identity");
                }
            }
            Reporter.log("Resource: " + url);   
            Reporter.log("Username: " + "pauser");   
            Reporter.log("Password: " + "pauser");   
            Reporter.log("Expected Result: " + "cn=filparole1"); 
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "cn=filparole1");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateUpdatedFilteredRoleProfileAttribute",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("evaluateUpdatedFilteredRoleProfileAttribute");
    }

    /**
     * Deletes policies, identities and updates service attributes to default
     * values.
     */
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        try {
            if ((idmc.searchIdentities(admintoken, "pauser",
                    IdType.USER)).size() != 0)
                idmc.deleteIdentity(admintoken, realm, IdType.USER, "pauser");
            if (idmc.searchIdentities(admintoken, "parole1",
                    IdType.ROLE).size() != 0)
                idmc.deleteIdentity(admintoken, realm, IdType.ROLE, "parole1");
            if (idmc.searchIdentities(admintoken, "parole2",
                    IdType.ROLE).size() != 0)
                idmc.deleteIdentity(admintoken, realm, IdType.ROLE, "parole2");
             if (idmc.searchIdentities(admintoken, "filparole1",
                    IdType.FILTEREDROLE).size() != 0)
                idmc.deleteIdentity(admintoken, realm, IdType.FILTEREDROLE,
                                "filparole1");
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            destroyToken(admintoken);
        }
        exiting("cleanup");
    }
}

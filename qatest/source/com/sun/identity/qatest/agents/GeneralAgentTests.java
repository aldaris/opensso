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
 * $Id: GeneralAgentTests.java,v 1.1 2007-09-24 20:32:57 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.agents;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.AgentsCommon;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.TestCommon;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class tests the following features for agents:
 * (1) remote user from session token
 * (2) remote user anonymous
 * (3) not enfroced by including the cgi script
 * (4) session notification events
       o session termination
       o logout
 * (5) access denied url
 * (6) case ignore for resource
 */
public class GeneralAgentTests extends TestCommon {
    
    private boolean executeAgainstOpenSSO;
    private String strScriptURL;
    private String logoutURL;
    private String strLocRB = "GeneralAgentTests";
    private String strGblRB = "agentsGlobal";
    private String resourceProtected;
    private String resourceNotProtected;
    private String resourceCase;
    private URL url;
    private WebClient webClient;
    private int polIdx;
    private int iIdx;
    private AgentsCommon mpc;
    private IDMCommon idmc;
    private ResourceBundle rbg;
    private SSOToken usertoken;
    private SSOToken admintoken;
    private HtmlPage page;

    /**
     * Instantiated different helper class objects
     */
    public GeneralAgentTests() 
    throws Exception{
        super("GeneralAgentTests");
        mpc = new AgentsCommon();
        idmc = new IDMCommon();
        rbg = ResourceBundle.getBundle(strGblRB);
        executeAgainstOpenSSO = new Boolean(rbg.getString(strGblRB +
                ".executeAgainstOpenSSO")).booleanValue();
        admintoken = getToken(adminUser, adminPassword, basedn);
    }
    
    /**
     * Sets up policy and creates users required by the policy
     */
    @Parameters({"policyIdx", "resourcePIdx", "resourceNPIdx",
    "resourceCaseIdx"})
    @BeforeClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void setup(String policyIdx, String resourcePIdx,
            String resourceNPIdx, String resourceCaseIdx)
    throws Exception {
        Object[] params = {policyIdx, resourcePIdx, resourceNPIdx,
        resourceCaseIdx};
        entering("setup", params);

        logoutURL = protocol + ":" + "//" + host + ":" + port + uri +
            "/UI/Logout";

        strScriptURL = rbg.getString(strGblRB + ".headerEvalScriptName");
        log(Level.FINEST, "setup", "Header Script URL: " + strScriptURL);
        url = new URL(strScriptURL);

        int resPIdx = new Integer(resourcePIdx).intValue();
        int resNPIdx = new Integer(resourceNPIdx).intValue();
        int resCaseIdx = new Integer(resourceCaseIdx).intValue();

        resourceProtected = rbg.getString(strGblRB + ".resource" + resPIdx);
        resourceNotProtected = rbg.getString(strGblRB + ".resource" + resNPIdx);
        resourceCase = rbg.getString(strGblRB + ".resource" + resCaseIdx);

        log(Level.FINEST, "setup", "Protected Resource Name: " +
                resourceProtected);
        log(Level.FINEST, "setup", "Unprotected Resource Name: " +
                resourceNotProtected);
        log(Level.FINEST, "setup", "Case sensitive Resource Name: " +
                resourceCase);

        polIdx = new Integer(policyIdx).intValue();
        mpc.createIdentities(strLocRB, polIdx);
        if (executeAgainstOpenSSO) {
            mpc.createPolicyXML(strGblRB, strLocRB, polIdx, strLocRB + ".xml");
            log(Level.FINEST, "setup", "Policy XML:\n" + strLocRB + ".xml");
            mpc.createPolicy(strLocRB + ".xml");
        } else
            log(Level.FINE, "setup", "Executing against non OpenSSO Install");

        exiting("setup");
    }
    
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void evaluateRemoteUser()
    throws Exception {
        entering("evaluetRemoteUser", null);
     
        webClient = new WebClient();
        try {
            page = consoleLogin(webClient, resourceProtected,
                    "generalagenttests",
                    "generalagenttests");
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page,
                    "REMOTE_USER : generalagenttests");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluetRemoteUser",
                    e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }

        exiting("evaluetRemoteUser");
    }

    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void evaluateAnonymous()
    throws Exception {
        entering("evaluateAnonymous", null);

        webClient = new WebClient();
        try {
            URL urlLoc = new URL(resourceNotProtected);
            page = (HtmlPage)webClient.getPage(urlLoc);
            log(Level.FINEST, "evaluateAnonymous", "Resource Page :\n" +
                    page.asXml());
            page = (HtmlPage)webClient.getPage(url);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page, "REMOTE_USER : anonymous");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateAnonymous",
                    e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        }

        exiting("evaluateAnonymous");
    }

    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void evaluateNotEnforced()
    throws Exception {
        entering("evaluateNotEnforced", null);

        webClient = new WebClient();
        try {
            URL urlLoc = new URL(resourceNotProtected);
            page = (HtmlPage)webClient.getPage(urlLoc);
            log(Level.FINEST, "evaluateNotEnforced", "Resource Page :\n" +
                    page.asXml());
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page,
                    "Notenforced Page");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateNotEnforced",
                    e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        }

        exiting("evaluateNotEnforced");
    }

    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void evaluateCaseSensitive()
    throws Exception {
        entering("evaluateCaseSensitive", null);

        webClient = new WebClient();
        try {
            page = consoleLogin(webClient, resourceProtected,
                    "generalagenttests", "generalagenttests");
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page,
                    "Allow Page");
            assert (iIdx != -1);
            URL urlLoc = new URL(resourceCase);
            page = (HtmlPage)webClient.getPage(urlLoc);
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page,
                    "Access Denied");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateCaseSensitive",
                    e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }

        exiting("evaluateCaseSensitive");
    }

    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void evaluateSessionTermination()
    throws Exception {
        entering("evaluateSessionTermination", null);

        webClient = new WebClient();
        try {
            page = consoleLogin(webClient, resourceProtected,
                    "generalagenttests", "generalagenttests");
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page,
                    "Allow Page");
            assert (iIdx != -1);
            SSOToken ssotoken = getUserToken(admintoken, "generalagenttests");
            destroyToken(admintoken, ssotoken);
            Thread.sleep(5000);
            HtmlPage page = consoleLogin(webClient, resourceProtected,
                    "generalagenttests",
                    "generalagenttests");
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page,
                    "Allow Page");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateSessionTermination",
                    e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }

        exiting("evaluateSessionTermination");
    }

    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void evaluateSessionLogout()
    throws Exception {
        entering("evaluateSessionLogout", null);

        webClient = new WebClient();
        try {
            page = consoleLogin(webClient, resourceProtected,
                    "generalagenttests",
                    "generalagenttests");
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page,
                    "Allow Page");
            assert (iIdx != -1);
            consoleLogout(webClient, logoutURL);
            Thread.sleep(5000);
            page = consoleLogin(webClient, resourceProtected,
                    "generalagenttests",
                    "generalagenttests");
            iIdx = -1;
            iIdx = getHtmlPageStringIndex(page,
                    "Allow Page");
            assert (iIdx != -1);
        } catch (Exception e) {
            log(Level.SEVERE, "evaluateSessionLogout",
                    e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }

        exiting("evaluateSessionLogout");
    }

    @AfterClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);

        if (executeAgainstOpenSSO)
            mpc.deletePolicies(strLocRB, polIdx);
        else 
            log(Level.FINE, "cleanup", "Executing against non OpenSSO Install");

        idmc.deleteIdentity(admintoken, realm, IdType.USER,
                "generalagenttests");
        destroyToken(admintoken);
        exiting("cleanup");
    }
}

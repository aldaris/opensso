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
 * $Id: TestCommon.java,v 1.2 2007-03-20 22:02:30 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.authentication.AuthContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import org.testng.Reporter;

/**
 * This class is the base for all <code>OpenSSO</code> QA testcases.
 * It has commonly used methods.
 */
public class TestCommon implements TestConstants
{
    private String logEntryTemplate;
    private String className;
    static private ResourceBundle rb_amconfig;
    static protected String adminUser;
    static protected String adminPassword;
    static protected String basedn;
    static protected String host;
    static protected String protocol;
    static protected String port;
    static protected String uri; 
    static protected String realm; 
    static protected Level logLevel;
    static private Logger logger;

    static {
        try {
            rb_amconfig = ResourceBundle.getBundle(
                    TestConstants.TEST_PROPERTY_AMCONFIG);
            logger = Logger.getLogger("com.sun.identity.qatest");
            FileHandler fileH = new FileHandler("logs");
            SimpleFormatter simpleF = new SimpleFormatter();
            fileH.setFormatter(simpleF);
            logger.addHandler(fileH);
            String logL = rb_amconfig.getString(TestConstants.KEY_LOG_LEVEL);
            if ((logL != null)) {
                logger.setLevel(Level.parse(logL));
            }
            logLevel = logger.getLevel();
            adminUser = rb_amconfig.getString(TestConstants.KEY_AMADMIN_USER); 
            adminPassword = rb_amconfig.getString(
                    TestConstants.KEY_AMADMIN_PASSWORD); 
            basedn = rb_amconfig.getString(TestConstants.KEY_BASEDN); 
            protocol = rb_amconfig.getString(TestConstants.KEY_PROTOCOL); 
            host = rb_amconfig.getString(TestConstants.KEY_HOST); 
            port = rb_amconfig.getString(TestConstants.KEY_PORT); 
            uri = rb_amconfig.getString(TestConstants.KEY_URI); 
            realm = rb_amconfig.getString(TestConstants.KEY_REALM); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TestCommon() {
    }

    protected TestCommon(String componentName) {
        logEntryTemplate = this.getClass().getName() + ".{0}: {1}";
        className = this.getClass().getName();
    }

    /**
     * Writes a log entry for entering a test method.
     */
    protected void entering(String methodName, Object[] params) {
        if (params != null) {
            logger.entering(className, methodName, params);
        } else {
            logger.entering(className, methodName);
        }
    }

    /**
     * Writes a log entry for exiting a test method.
     */
    protected void exiting(String methodName) {
        logger.exiting(className, methodName);
    }

    /**
     * Writes a log entry.
     */
    protected void log(Level level, String methodName, Object message) {
        Object[] args = {methodName, message};
        logger.log(level, MessageFormat.format(logEntryTemplate, args));
    }

    /**
     * Writes a log entry.
     */
    protected void log(
        Level level,
        String methodName,
        String message,
        Object[] params
    ) {
        Object[] args = {methodName, message};
        logger.log(level, MessageFormat.format(logEntryTemplate, args), params);
    }

    /**
     * Writes a log entry for testng report
     */
    protected void logTestngReport(Map m) {
        Set s = m.keySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            String value = (String)m.get(key);
            Reporter.log(key + "=" + value);
        }
    }

    /**
     * Returns single sign on token.
     */
    protected SSOToken getToken(String name, String password, String basedn)
        throws Exception {
        log(logLevel, "getToken", name);
        log(logLevel, "getToken", password);
        log(logLevel, "getToken", basedn);
        AuthContext authcontext = new AuthContext(basedn);
        authcontext.login();
        javax.security.auth.callback.Callback acallback[] = 
                authcontext.getRequirements();
        for (int i = 0; i < acallback.length; i++){
            if (acallback[i] instanceof NameCallback) {
                NameCallback namecallback = (NameCallback)acallback[i];
                namecallback.setName(name);
            }
            if (acallback[i] instanceof PasswordCallback) {
                PasswordCallback passwordcallback = 
                        (PasswordCallback)acallback[i];
                passwordcallback.setPassword(password.toCharArray());
            }
        }

        authcontext.submitRequirements(acallback);
        if (authcontext.getStatus() == 
                com.sun.identity.authentication.AuthContext.Status.SUCCESS)
            log(logLevel, "getToken", "Successful authentication ....... ");
        SSOToken ssotoken = authcontext.getSSOToken();
        log(logLevel, "getToken", 
                (new StringBuilder()).append("TOKENCREATED>>> ").
                append(ssotoken).toString());
        return ssotoken;
    }

    /**
     * Validate single sign on token.
     */
    protected boolean validateToken(SSOToken ssotoken)
        throws Exception {
        log(logLevel, "validateToken", "Inside validate token");
        SSOTokenManager stMgr = SSOTokenManager.getInstance();
        boolean bVal = stMgr.isValidToken(ssotoken);
        if (bVal)
            log(logLevel, "validateToken", "Token is Valid");
        else
            log(logLevel, "validateToken", "Token is Invalid");
        return bVal;
    }

    /**
     * Destroys single sign on token.
     */
    protected void destroyToken(SSOToken ssotoken)
        throws Exception {
        log(logLevel, "destroyToken", "Inside destroy token");
        SSOTokenManager stMgr = SSOTokenManager.getInstance();
        if (stMgr.isValidToken(ssotoken))
            stMgr.destroyToken(ssotoken);
    }

    /**
     * Returns the base directory where code base is
     * checked out.
     */
    protected String getBaseDir()
        throws Exception {
        log(logLevel, "getBaseDir", "Inside getBaseDir");
        File file = new File (".");
        String strCD = file.getCanonicalPath();
        log(logLevel, "getBaseDir", "Current Directory:" + strCD);
        return (strCD);
    }

    /**
     * Reads a file containing data-value pairs and returns that as a list object.
     */
    protected List getListFromFile(String fileName)
        throws Exception {
        ArrayList list = null;
        if (fileName != null) {
            list = new ArrayList();
            BufferedReader input = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line=input.readLine()) != null) {
                list.add(line);
            }
            log(logLevel, "getListFromFile", "List :" + list);
            if (input != null)
                input.close();
        }
        return (list);
    }

    /**
     * Login to console using htmlunit
     */ 
    public void consoleLogin(
        WebClient webclient,
        String amUrl,
        String amadmUser,
        String amadmPassword
    ) throws Exception {
        entering("consoleLogin", null);
        log(logLevel, "consoleLogin", "JavaScript Enabled:" +
                webclient.isJavaScriptEnabled());
        log(logLevel, "consoleLogin", "Redirect Enabled:" +
                webclient.isRedirectEnabled());
        URL url = new URL(amUrl);
        HtmlPage page = (HtmlPage)webclient.getPage(amUrl);
        log(logLevel, "consoleLogin", page.getTitleText());
        HtmlForm form = page.getFormByName("Login");
        HtmlHiddenInput txt1 =
                (HtmlHiddenInput)form.getInputByName("IDToken1");
        txt1.setValueAttribute(amadmUser);
        HtmlHiddenInput txt2 =
                (HtmlHiddenInput)form.getInputByName("IDToken2");
        txt2.setValueAttribute(amadmPassword);
        form.submit();
        exiting("consoleLogin");
    }


    /**
     * Returns a map of String to Set of String from a formatted string.
     * The format is
     * <pre>
     * &lt;key1&gt;=&lt;value11&gt;,&lt;value12&gt;...,&lt;value13&gt;;
     * &lt;key2&gt;=&lt;value21&gt;,&lt;value22&gt;...,&lt;value23&gt;; ...
     * &lt;keyn&gt;=&lt;valuen1&gt;,&lt;valuen2&gt;...,&lt;valuen3&gt;
     * </pre>
     */
    public static Map<String, Set<String>> parseStringToMap(String str) {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        StringTokenizer st = new StringTokenizer(str, ";");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int idx = token.indexOf("=");
            if (idx != -1) {
                Set<String> set = new HashSet<String>();
                map.put(token.substring(0, idx).trim(), set);
                StringTokenizer st1 = new StringTokenizer(
                    token.substring(idx+1), ",");
                while (st1.hasMoreTokens()) {
                    set.add(st1.nextToken().trim());
                }
            }
        }
        return map;
    }

    /**
     * Returns set of string. This is a convenient method for adding a set of
     * string into a map. In this project, we usually have the
     * <code>Map&lt;String, Set&lt;String&gt;&gt; and many times, we just
     * want to add a string to the map.
     */
    public static Set<String> putSetIntoMap(
        String key,
        Map<String, Set<String>> map,
        String value
    ) {
        Set<String> set = new HashSet<String>();
        set.add(value);
        map.put(key, set);
        return set;
    }
}

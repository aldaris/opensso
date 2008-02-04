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
 * $Id: Wizard.java,v 1.4 2008-02-04 20:57:20 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.config.pojos.LDAPStore;
import com.sun.identity.config.util.AjaxPage;
import com.sun.identity.setup.AMSetupServlet;
import com.sun.identity.setup.HttpServletRequestWrapper;
import com.sun.identity.setup.HttpServletResponseWrapper;
import com.sun.identity.setup.SetupConstants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import net.sf.click.control.ActionLink;
import net.sf.click.Context;

public class Wizard extends AjaxPage {

    public int startingTab = 1;

    public ActionLink createConfigLink = 
        new ActionLink("createConfig", this, "createConfig" );
    public ActionLink testUrlLink = 
        new ActionLink("testNewInstanceUrl", this, "testNewInstanceUrl" );
    public ActionLink pushConfigLink = 
        new ActionLink("pushConfig", this, "pushConfig" );

    private String cookieDomain = null;
    private String hostName = getHostName();
    private String userName = "cn=Directory Manager";
    private String password = "";
    private int port = AMSetupServlet.getUnusedPort(hostName,50389, 1000);;
    private String rootSuffix = "dc=opensso,dc=java,dc=net";
    private String dataStore = SetupConstants.SMS_EMBED_DATASTORE;
    
    public void onInit() {       
    }
        
    /**
     * This is the 'execute' operation for the entire wizard.  This method 
     * aggregates all data submitted across the wizard pages here in one lump 
     * and hands it off to the back-end for processing.
     */
    public boolean createConfig() {
        HttpServletRequest req = getContext().getRequest();
        
        HttpServletRequestWrapper request = 
            new HttpServletRequestWrapper(getContext().getRequest());          
        HttpServletResponseWrapper response =                
            new HttpServletResponseWrapper(getContext().getResponse());        
        
        // get the admin password. use the same value for password and confirm
        // value because they were validated in the input screen
        String adminPassword = (String)getContext().getSessionAttribute(
            SetupConstants.CONFIG_VAR_ADMIN_PWD);        
        request.addParameter(SetupConstants.CONFIG_VAR_ADMIN_PWD, adminPassword);
        request.addParameter(SetupConstants.CONFIG_VAR_CONFIRM_ADMIN_PWD, adminPassword);
        
        // get the agent password. same value used for password and confirm
        // because they were validated in the input screen.
        String agentPassword = (String)getContext().getSessionAttribute(
            SetupConstants.CONFIG_VAR_AMLDAPUSERPASSWD);
        request.addParameter(SetupConstants.CONFIG_VAR_AMLDAPUSERPASSWD, agentPassword);
        request.addParameter(SetupConstants.CONFIG_VAR_AMLDAPUSERPASSWD_CONFIRM, agentPassword);
        
        // set configuration repository information
        LDAPStore config = (LDAPStore)getContext().getSessionAttribute( 
            Step2.LDAP_STORE_SESSION_KEY);       
        if (config != null) {                          
            hostName = config.getHostName();
            dataStore =  SetupConstants.SMS_DS_DATASTORE;
            port = config.getHostPort();
            userName = config.getUsername();
            password = config.getPassword();
            rootSuffix = config.getBaseDN();
        }
        
        request.addParameter(SetupConstants.CONFIG_VAR_DIRECTORY_SERVER_HOST, hostName);
        request.addParameter(SetupConstants.CONFIG_VAR_DATA_STORE, dataStore);
        request.addParameter(
                SetupConstants.CONFIG_VAR_DIRECTORY_SERVER_PORT, port + ""); 
        request.addParameter(SetupConstants.CONFIG_VAR_DS_MGR_DN, userName);
        request.addParameter(SetupConstants.CONFIG_VAR_DS_MGR_PWD, password);
        request.addParameter(SetupConstants.CONFIG_VAR_ROOT_SUFFIX, rootSuffix);
       
                
        // user store repository
        LDAPStore userStore = (LDAPStore)getContext().getSessionAttribute( 
            Step4.LDAP_STORE_SESSION_KEY);

        if (userStore != null) {                       
            Map store = new HashMap();            
            store.put(SetupConstants.CONFIG_VAR_DIRECTORY_SERVER_HOST,
                userStore.getHostName());
            store.put(SetupConstants.CONFIG_VAR_DIRECTORY_SERVER_PORT, 
                userStore.getHostPort()+"");
            store.put(SetupConstants.CONFIG_VAR_DS_MGR_DN, userStore.getUsername());
            store.put(SetupConstants.CONFIG_VAR_DS_MGR_PWD, userStore.getPassword());                       
            
            request.addParameter("UserStore", store);
        }
        
        // site configuration is passed as a map of the site information 
        Map siteConfig = new HashMap(5);
        String loadBalancerHost = (String)getContext().getSessionAttribute( 
            SetupConstants.LB_SITE_NAME);
        String primaryURL = (String)getContext().getSessionAttribute(
            SetupConstants.LB_PRIMARY_URL);
        if (loadBalancerHost != null) {
            siteConfig.put(SetupConstants.LB_SITE_NAME, loadBalancerHost);
            siteConfig.put(SetupConstants.LB_PRIMARY_URL, primaryURL);
            request.addParameter(
                SetupConstants.CONFIG_VAR_SITE_CONFIGURATION, siteConfig);
        }


        // server properties
        request.addParameter(
            SetupConstants.CONFIG_VAR_SERVER_HOST, getHostName());
        request.addParameter(
            SetupConstants.CONFIG_VAR_SERVER_PORT, ""+req.getServerPort());
        request.addParameter(
            SetupConstants.CONFIG_VAR_SERVER_URI, req.getRequestURI());
        request.addParameter(
            SetupConstants.CONFIG_VAR_SERVER_URL, 
            req.getRequestURL().toString());        
        request.addParameter(
            SetupConstants.CONFIG_VAR_ENCRYPTION_KEY, 
            AMSetupServlet.getRandomString());

        String cookie = 
            (String)getContext().getSessionAttribute("cookieDomain");
        if (cookie == null) {
            cookie = getCookieDomain();
        }
        request.addParameter(SetupConstants.CONFIG_VAR_COOKIE_DOMAIN, cookie);       
        
        String locale = 
            (String)getContext().getSessionAttribute("platformLocale");
        if (locale == null) {
            locale = SetupConstants.DEFAULT_PLATFORM_LOCALE;
        }
        request.addParameter(SetupConstants.CONFIG_VAR_PLATFORM_LOCALE, locale);

        String base = 
            (String)getContext().getSessionAttribute("configDirectory");
        if (base == null) {
            base = getBaseDir();
        }
        request.addParameter(SetupConstants.CONFIG_VAR_BASE_DIR, base);
                   
        try {
            if (AMSetupServlet.processRequest(request, response)) {
                writeToResponse("true");           
            } else {
                writeToResponse(AMSetupServlet.getErrorMessage());
            }
        } catch (Exception e) {
            writeToResponse("Error during configuration. Consult debug files for more information");
        }
        
        setPath(null);
        return false;
    }

    /**
     * In a multi-instance configuration, this method will push the the new 
     * instance configuration to that instance.
     */
    public boolean pushConfig() {

        //String newInstanceUrl = (String)getContext().getSessionAttribute( Step2.NEW_INSTANCE_URL_SESSION_KEY );
//        if ( newInstanceUrl == null ) {
//            throw new IllegalStateException( "This method should only be called by html/javascript after a user " +
//                "has specified a 'New Instance URL' in a multi-instance configuration." );
//        }

//        try {
//            getConfigurator().pushConfiguration( newInstanceUrl );
//            writeToResponse("true");
//        } catch ( Exception e ) {
//            writeToResponse(e.getMessage());
//        }
//
//        setPath(null);
        return false;
    }
    

}

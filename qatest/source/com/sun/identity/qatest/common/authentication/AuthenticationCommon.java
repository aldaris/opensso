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
 * $Id: AuthenticationCommon.java,v 1.11 2009-02-03 19:54:20 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.SMSCommon;
import com.sun.identity.qatest.common.SMSConstants;
import com.sun.identity.qatest.common.TestCommon;
import java.net.URL;
import java.util.logging.Level;
import com.iplanet.sso.SSOToken;

/**
 * This class contains helper method related to Authentication.
 */
public class AuthenticationCommon extends TestCommon {
    
    SSOToken ssoToken;
    SMSCommon smsCommon;
    
    public AuthenticationCommon() {
        super("AuthenticationCommon");
        try{
            ssoToken = getToken(adminUser, adminPassword, basedn);
            smsCommon = new SMSCommon(ssoToken);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

   /**
     * Tests zero page login for anonymous authentication for given mode.
     * @param wc - the web client simulating the user's browser.
     * @param user - the user ID that will be authenticated.
     * @param mode - the type of authentication being used (e.g. module, user,
     * authlevel, role, service, etc.)
     * @param modeValue - the value for the authentication type being used.
     * @param passMsg - the message which should appear in the resulting browser
     * page when successful.
     */
    public void testZeroPageLogin(WebClient wc, String user, String mode,
            String modeValue, String passMsg)
    throws Exception {
        testZeroPageLogin(wc, user, null, mode, modeValue, passMsg);
    }

    /**
     * Tests zero page login for given mode. 
     * @param wc - the web client simulating the user's browser.
     * @param user - the user ID that will be authenticated.
     * @param password - the password for "user".
     * @param mode - the type of authentication being used (e.g. module, user,
     * authlevel, role, service, etc.)
     * @param modeValue - the value for the authentication type being used.
     * @param passMsg - the message which should appear in the resulting browser
     * page when successful.
     */
    public void testZeroPageLogin(WebClient wc, String user, String password, 
            String mode, String modeValue, String passMsg)
    throws Exception {
        Object[] params = {user, password, mode, modeValue, passMsg};
        entering("testZeroPageLogin", params);
        String baseLoginString = null;        
        String loginString = null;
        
        try {
            baseLoginString = protocol + ":" + "//" + host + ":" + port + uri +
                    "/UI/Login?";
            StringBuffer loginBuffer = new StringBuffer(baseLoginString);
            loginBuffer.append(mode).append("=").append(modeValue).
                    append("&IDToken1=").append(user);
            
            if (password != null) {
                loginBuffer.append("&IDToken2=").append(password);
            }

            loginString = loginBuffer.toString();             
            log(Level.FINEST, "testZeroPageLogin", loginString);
            URL url = new URL(loginString);
            HtmlPage page = (HtmlPage)wc.getPage(url);
            
            log(Level.FINEST, "testZeroPageLogin", "Title of resulting page = "
                    + page.getTitleText());
            // Tests for everything if mode is not set to "role" or the 
            // configured plugin is of type amsdk. 
            if (!mode.equalsIgnoreCase("role") || 
                    smsCommon.isPluginConfigured(ssoToken,
                    SMSConstants.UM_DATASTORE_SCHEMA_TYPE_AMSDK, realm )) {             
                assert this.getHtmlPageStringIndex(page, passMsg) != -1;
            } else {
                log(Level.FINEST, "testZeroPageLogin", 
                        "Role based test is skipped for non amsdk plugin ...");
            }   
        } catch (Exception e) {
            log(Level.SEVERE, "testZeroPageLogin", e.getMessage(), params);
            e.printStackTrace();
            throw e;
        }
        exiting("testZeroPageLogin");
    }

    /**
     * Retrieve the SMSCommon instance.
     * @return <code>SMSCommon</code>
     */
    public SMSCommon getSMSCommon() { return smsCommon; }
    
    /**
     * Returns the profile attribute based on the profile test performed
     * @param profile - the value which indicates how the profile creation
     * should be set.
     * @return a String containing the profile creation attribute name/value
     * pair to update in the authentication service.
     */
    public String getProfileAttribute(String profile){
        String profileAttribute = null;
        if (profile.equals("dynamic")) {
            profileAttribute = "true";
        } else if(profile.equals("required")) {
            profileAttribute = "false";
        } else {
            profileAttribute = "ignore";
        }
        return profileAttribute;
    }
}

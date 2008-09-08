/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: WSSReplayPasswd.java,v 1.1 2008-09-08 23:04:26 mallas Exp $
 *
 */

package com.sun.identity.authentication.spi;

import com.iplanet.services.util.Crypt;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is used to set the encrypted password as a session property.
 * This is a convenient class primarily used for web services security 
 * user name token profile where the end user password is encrypted.  
 */
public class WSSReplayPasswd implements AMPostAuthProcessInterface {
   
    private static final String PASSWORD_TOKEN = "IDToken2";
    
    /** 
     * Post processing on successful authentication.
     * @param requestParamsMap contains HttpServletRequest parameters
     * @param request HttpServlet  request
     * @param response HttpServlet response
     * @param ssoToken user's session
     * @throws AuthenticationException if there is an error while setting
     * the session paswword property
     */
    public void onLoginSuccess(Map requestParamsMap,
        HttpServletRequest request,
        HttpServletResponse response,
        SSOToken ssoToken) throws AuthenticationException {

        String userpasswd = request.getParameter(PASSWORD_TOKEN);
        try {
            if (userpasswd != null) {
                ssoToken.setProperty("EncryptedUserPassword", 
                       Crypt.encrypt(userpasswd));
            }
        } catch (SSOException sse) {
            System.out.println("WSSReplayPasswd.onLoginSuccess: " +
                    "sso exception" + sse.getMessage());
        }
    }

    /** 
     * Post processing on failed authentication.
     * @param requestParamsMap contains HttpServletRequest parameters
     * @param req HttpServlet request
     * @param res HttpServlet response
     * @throws AuthenticationException if there is an error
     */
    public void onLoginFailure(Map requestParamsMap,
        HttpServletRequest req,
        HttpServletResponse res) throws AuthenticationException {
           
    }

    /** 
     * Post processing on Logout.
     * @param req HttpServlet request
     * @param res HttpServlet response
     * @param ssoToken user's session
     * @throws AuthenticationException if there is an error
     */
    public void onLogout(HttpServletRequest req,
        HttpServletResponse res,
        SSOToken ssoToken) throws AuthenticationException {           
    }
}

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
 * $Id: AuthenticatedCommand.java,v 1.1 2006-05-31 21:49:40 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli;


import com.iplanet.sso.SSOToken;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * This is the base class for all commands that require a user to be 
 * authenticated in order to execute a command.
 */
public abstract class AuthenticatedCommand extends CLICommandBase {
    private String adminUserID;
    private String adminID;
    private String adminPassword;
    private SSOToken ssoToken;

    /**
     * Authenticates the administrator. Dervived classes needs to
     * call this method from the dervived method,
     * <code>handleRequest(RequestContext rc)</code>.
     *
     * @param rc Request Context.
     * @throws CLIException if authentication fails.
     */
    public void handleRequest(RequestContext rc)
        throws CLIException
    {
        super.handleRequest(rc);
        adminID = getStringOptionValue(
            AccessManagerConstants.ARGUMENT_ADMIN_ID);
        adminUserID = getUserID();
        adminPassword = getPassword();
    }

    private String getUserID() {
        String userId = null;
        StringTokenizer st = new StringTokenizer(adminID, ",");
        if (st.hasMoreTokens()) {
            String strUID = st.nextToken();
            int idx = strUID.indexOf('=');
            if (idx != -1) {
                String namingAttr = strUID.substring(0, idx);
                if (namingAttr.equals("uid")) {
                    userId = strUID.substring(idx+1);
                }
            }
        }
        return userId;
    }

    private String getPassword()
        throws CLIException
    {
        String password = getStringOptionValue(
            AccessManagerConstants.ARGUMENT_PASSWORD);
        if (password == null) {
            password = CLIUtil.getFileContent(
                getStringOptionValue(
                    AccessManagerConstants.ARGUMENT_PASSWORD_FILE), true);
        }
        return password;
    }

    protected String getAdminUserID() {
        return adminUserID;
    }

    protected String getAdminPassword() {
        return adminPassword;
    }

    protected String getAdminID() {
        return adminID;
    }

    protected SSOToken getAdminSSOToken() {
        return ssoToken;
    }

    protected void ldapLogin()
        throws CLIException
    {
        Authenticator auth = Authenticator.getInstance();
        String bindUser = getAdminUserID();
        if (bindUser == null) {
            bindUser = getAdminID();
        }
        ssoToken = auth.ldapLogin(getCommandManager(), bindUser,
            getAdminPassword());
    }

    protected void writeLog(
        int type,
        Level level,
        String msgid,
        String[] msgdata
    ) throws CLIException {
        CommandManager mgr = getCommandManager();
        LogWriter.log(mgr, type, level, msgid, msgdata,getAdminSSOToken());
    }
}

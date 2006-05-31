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
 * $Id: Authenticator.java,v 1.1 2006-05-31 21:49:40 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli;



import com.iplanet.am.util.Locale;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.internal.InvalidAuthContextException;
import com.sun.identity.security.AdminTokenAction;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;

/**
 * This class is responsible for authentication of a user.
 */
class Authenticator {
    private static final String LOGIN_STATUS = "iplanet-am-user-login-status";
    private static final String ACCOUNT_LIFE = "iplanet-am-user-account-life";
    private static final String STRING_ACTIVE = "active";

    private Map<String, Map<String, SSOToken>> ssoTokens = 
        new HashMap<String, Map<String, SSOToken>>();

    private static Set<String> ACTIVE_STATE_ATTRIBUTES = new HashSet<String>(4);
    private static Authenticator instance = new Authenticator();

    static {
	ACTIVE_STATE_ATTRIBUTES.add(LOGIN_STATUS);
	ACTIVE_STATE_ATTRIBUTES.add(ACCOUNT_LIFE);
    }

    private Authenticator() {
    }

    public static Authenticator getInstance() {
        return instance;
    }

    public AuthContext sessionBasedLogin(
        CommandManager mgr,
        String bindUser,
        String bindPwd
    ) throws CLIException
    {
        String[] param = {bindUser};
        LogWriter.log(mgr, LogWriter.LOG_ACCESS, Level.INFO,
            "ATTEMPT_LOGIN", param, null);
	try {
	    AuthContext ac = sessionBasedLoginInternal(
                mgr, bindUser, bindPwd);
            LogWriter.log(mgr, LogWriter.LOG_ACCESS, Level.INFO, 
                "SUCCEED_LOGIN", param, null);
            return ac;
	} catch (CLIException e) {
            String[] params = {bindUser, e.getMessage()};
	    LogWriter.log(mgr, LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_LOGIN", params, null);
	    throw e;
	}
    }

    private AuthContext sessionBasedLoginInternal(
        CommandManager mgr,
        String bindUser,
        String bindPwd
    ) throws CLIException {
	AuthContext lc = getAuthContext(mgr);
	processCallback(mgr, lc, bindUser, bindPwd);

	try {
	    lc.getSSOToken();
	} catch (Exception e) {
            ResourceBundle rb = mgr.getResourceBundle();
	    throw new CLIException(
                rb.getString("exception-session-based-login-failed"),
                ExitCodes.SESSION_BASED_LOGIN_FAILED);
	}

        return lc;
    }

    SSOToken ldapLogin(CommandManager mgr, String bindUser, String bindPwd)
        throws CLIException
    {
        SSOToken ssoToken = getSSOTokenFromCache(bindUser, bindPwd);
        if (ssoToken == null) {
            IOutput outputWriter = mgr.getOutputWriter();
            ResourceBundle rb = mgr.getResourceBundle();
            if (mgr.isVerbose()) {
                outputWriter.printlnMessage(
                    rb.getString("verbose-authenticating"));
            }

            String[] param = {bindUser};
            String installTime = SystemProperties.get(
                AdminTokenAction.AMADMIN_MODE, "false");

            if (installTime.equalsIgnoreCase("false")) {
                LogWriter.log(mgr, LogWriter.LOG_ACCESS, Level.INFO,
                    "ATTEMPT_LOGIN", param, null);
                try {
                    AuthContext lc = sessionBasedLoginInternal(
                        mgr, bindUser, bindPwd);
                    ssoToken = lc.getSSOToken();
                } catch (Exception e) {
                    ssoToken = ldapLoginInternal(mgr, bindUser, bindPwd);
                }
            }

            /*
             * Even if installTime is false, ssoToken could be null if none of
             * the access manager servers are running.
             * Hence check for ssoToken and use local AuthContext
             */
            if (ssoToken == null) {
                ssoToken = ldapLoginInternal(mgr, bindUser, bindPwd);
            }

            LogWriter.log(mgr, LogWriter.LOG_ACCESS, Level.INFO,
                "SUCCEED_LOGIN", param, null);
            if (mgr.isVerbose()) {
                outputWriter.printlnMessage(
                    rb.getString("verbose-authenticated"));
            }
            cacheSSOToken(bindUser, bindPwd, ssoToken);
        }

        return ssoToken;
    }

    private synchronized void cacheSSOToken(
        String bindUser,
        String bindPwd,
        SSOToken ssoToken
    ) {
        Map<String, SSOToken> map = ssoTokens.get(bindUser);
        if (map == null) {
            map = new HashMap<String, SSOToken>();
            ssoTokens.put(bindUser, map);
        }
        map.put(bindPwd, ssoToken);
    }

    private synchronized SSOToken getSSOTokenFromCache(
        String bindUser,
        String bindPwd
    ) {
        SSOToken ssoToken = null;
        Map<String, SSOToken> map = ssoTokens.get(bindUser);
        if (map != null) {
            ssoToken = map.get(bindPwd);
        }
        return ssoToken;
    }

    private SSOToken ldapLoginInternal(
        CommandManager mgr,
        String bindUser,
        String bindPwd
    ) throws CLIException {
        SSOToken ssoToken = null;
        ResourceBundle rb = mgr.getResourceBundle();

	try {
	    com.sun.identity.authentication.internal.AuthContext ac =
		getLDAPAuthContext(bindUser, bindPwd);
	    if (ac.getLoginStatus() ==
	    com.sun.identity.authentication.internal.AuthContext.AUTH_SUCCESS)
	    {
		ssoToken = ac.getSSOToken();
	    } else {
		throw new CLIException(rb.getString(
                    "exception-LDAP-login-failed"),
                    ExitCodes.LDAP_LOGIN_FAILED);
	    }
	} catch (LoginException le) {
            String[] params = {bindUser, le.getMessage()};
	    LogWriter.log(mgr, LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_LOGIN", params, null);
            throw new CLIException(rb.getString(
                "exception-LDAP-login-failed"), ExitCodes.LDAP_LOGIN_FAILED);
	} catch (InvalidAuthContextException iace) {
            String[] params = {bindUser, iace.getMessage()};
	    LogWriter.log(mgr, LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_LOGIN", params, null);
            throw new CLIException(rb.getString(
                "exception-LDAP-login-failed"), ExitCodes.LDAP_LOGIN_FAILED);
	}
        return ssoToken;
    }

    private com.sun.identity.authentication.internal.AuthContext
	getLDAPAuthContext(
        String bindUser,
        String bindPwd
    ) throws LoginException
    {
	com.sun.identity.authentication.internal.AuthPrincipal principal =
	    new com.sun.identity.authentication.internal.AuthPrincipal(
		bindUser);
	com.sun.identity.authentication.internal.AuthContext authContext =
	     new com.sun.identity.authentication.internal.AuthContext(
		principal, bindPwd.toCharArray());
	return authContext;
    }

    private AuthContext getAuthContext(CommandManager mgr)
	throws CLIException
    {
        ResourceBundle rb = mgr.getResourceBundle();
	try {
	    AuthContext lc = new AuthContext("/");
	    lc.login(AuthContext.IndexType.MODULE_INSTANCE, "LDAP");
	    return lc;
	} catch (LoginException le) {
	    le.printStackTrace();
	    throw new CLIException(rb.getString("exception-LDAP-login-failed"),
                ExitCodes.SESSION_BASED_LOGIN_FAILED);
	}
    }
    
    private void processCallback(
        CommandManager mgr,
	AuthContext lc,
	String bindUser,
	String bindPwd
    ) throws CLIException
    {
        ResourceBundle rb = mgr.getResourceBundle();
	while (lc.hasMoreRequirements()) {
	    Callback[] callbacks =  lc.getRequirements();
	    
	    if (callbacks != null) {
		setCallbackValues(callbacks, bindUser, bindPwd);
		lc.submitRequirements(callbacks);
	    }
	}
	
	if (lc.getStatus() != AuthContext.Status.SUCCESS) {
	    throw new CLIException(rb.getString("exception-LDAP-login-failed"),
                ExitCodes.SESSION_BASED_LOGIN_FAILED);
	}
    }
    
    private void setCallbackValues(
        Callback[] callbacks,
        String bindUser,
	String bindPwd
    ) {
	for (int i = 0; i < callbacks.length; i++) {
	    if (callbacks[i] instanceof NameCallback) {
		NameCallback nc = (NameCallback)callbacks[i];
		nc.setName(bindUser);
	    } else if (callbacks[i] instanceof PasswordCallback) {
		PasswordCallback pc = (PasswordCallback) callbacks[i];
		char pswd[] = bindPwd.toCharArray();
		pc.setPassword(pswd);
	    }
	}
    }

    private static String getStringValue(Map map, String key) {
	String strValue = null;
	if ((map != null) && !map.isEmpty()) {
	    Set set = (Set)map.get(key);
	    if ((set != null) && !set.isEmpty()) {
		strValue = (String)set.iterator().next();
	    }
	}
	return strValue;
    }

    private static boolean isExpired(String strDate) {
	boolean expired = false;
	if ((strDate != null) && (strDate.trim().length() > 0)) {
	    Date exprDate = Locale.parseNormalizedDateString(strDate);
	    expired = exprDate.before(new Date());
	}
	return expired;
    }
}

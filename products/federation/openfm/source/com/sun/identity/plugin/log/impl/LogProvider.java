/**
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
 * $Id: LogProvider.java,v 1.1 2006-10-30 23:18:06 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.plugin.log.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.security.AccessController;
import java.text.MessageFormat;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.configuration.SystemPropertiesManager;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;

import com.sun.identity.authentication.internal.AuthPrincipal;

import com.sun.identity.log.Logger;
import com.sun.identity.log.LogRecord;
import com.sun.identity.log.messageid.LogMessageProvider;
import com.sun.identity.log.messageid.MessageProviderFactory;

import com.sun.identity.plugin.log.LogException;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.security.AdminTokenAction;

/**
 * This class is the AM implementation of the Open Federation Logger interface.
 */

public class LogProvider implements com.sun.identity.plugin.log.Logger {
    
    protected Logger accessLogger = null;
    protected Logger errorLogger = null;

    private String ACCESS_LOG_NAME = null;
    private String ERROR_LOG_NAME  = null;
    private LogMessageProvider msgProvider = null;

    private static Debug debug = Debug.getInstance("amLogProvider");
    private static boolean logStatus = false;
    private static SSOToken authSSOToken = null;

    static {
        authSSOToken = (SSOToken) AccessController.doPrivileged(
		AdminTokenAction.getInstance());

        String status = SystemPropertiesManager.get(
            com.sun.identity.shared.Constants.AM_LOGSTATUS);
        logStatus = (status != null) && status.equalsIgnoreCase("ACTIVE");
    }

    /**
     * Initializes the logging for the component.
     *
     * @param componentName the component name.
     * @exception LogException if there is an error
     *	  during initialization.
     */
    public void init(String componentName) throws LogException {
        ACCESS_LOG_NAME = new StringBuffer().append(componentName)
                                            .append(".access").toString();
        ERROR_LOG_NAME  = new StringBuffer().append(componentName)
                                            .append(".error").toString();
        accessLogger = 
                (com.sun.identity.log.Logger) Logger.getLogger(ACCESS_LOG_NAME);
        errorLogger =
                (com.sun.identity.log.Logger) Logger.getLogger(ERROR_LOG_NAME);
        try {
            msgProvider = MessageProviderFactory.getProvider(componentName);
        } catch (IOException ioe) {
            debug.error("<init>: unable to create log message provider", ioe);
        }
    }
    
    /**
     * Logs message to the access logs. 
     *
     * @param level the log level , these are based on those
     *		defined in java.util.logging.Level, the values for
     *		level can be any one of the following : <br>
     *          <ul>
     *		- SEVERE (highest value) <br>
     *		- WARNING <br>
     *		- INFO <br>
     *		- CONFIG <br>
     *		- FINE <br>
     *		- FINER <br>
     *		- FINEST (lowest value) <br>
     *          </ul>
     * @param messageID the message or a message identifier.
     * @param data string array of dynamic data to be replaced in the message.
     * @param session the User's session object
     * @exception LogException if there is an error.
     */
    
    public void access(Level level,
                       String messageID,
                       String data[],
                       Object session) throws LogException {
        if (isAccessLoggable(level)) {
            SSOToken ssoToken = null;
            if (session != null) {
                try {
                    String sid = SessionManager.getProvider().getSessionID(
                        session);
                    ssoToken = 
                            SSOTokenManager.getInstance().createSSOToken(sid);
                } catch (SessionException se) {
                    debug.message("Error getting session provider: " , se);
                } catch (SSOException soe) {
                    debug.message("Error creating SSOToken: " , soe);
                }
            }
            SSOToken realToken=(ssoToken != null) ? ssoToken : authSSOToken;
            LogRecord lr = msgProvider.createLogRecord(
                    messageID,data,realToken);
            if (lr != null) {
                accessLogger.log(lr, authSSOToken);
            }
        }
   }
    
    
    /**
     * Logs error messages to the error logs.
     *
     * @param level the log level , these are based on those
     *		defined in java.util.logging.Level, the values for
     *          level can be any one of the following : <br>
     *          <ul>
     *          - SEVERE (highest value) <br>
     *          - WARNING <br>
     *          - INFO <br>
     *          - CONFIG <br>
     *          - FINE <br>
     *          - FINER <br>
     *          - FINEST (lowest value) <br>
     *          </ul>
     * @param messageId the message or a message identifier.
     * @param data string array of dynamic data to be replaced in the message.
     * @param session the User's Session object.
     * @exception LogException if there is an error.
     */
    public void error(Level level,String messageId,String data[],
            Object session) throws LogException {
        
        if (isErrorLoggable(level)) {
            SSOToken ssoToken = null;
            if (session != null) {
                try {
                    String sid = SessionManager.getProvider().getSessionID(
                        session);
                    ssoToken = 
                            SSOTokenManager.getInstance().createSSOToken(sid);
                } catch (SessionException se) {
                    debug.message("Error getting session provider :" , se);
                } catch (SSOException soe) {
                    debug.message("Error creating SSOToken :" , soe);
                }
            }
            SSOToken realToken =
                    (ssoToken != null) ? ssoToken : authSSOToken;
            LogRecord lr = msgProvider.createLogRecord(
                    messageId,data,realToken);
            if (lr != null) {
                errorLogger.log(lr, authSSOToken);
            }
        }
    }
    
    /**
     * Checks if the logging is enabled.
     *
     * @return true if logging is enabled.
     */
    public boolean isLogEnabled() {
        return logStatus;
    }
    
    /**
     * Checks if an access message of the given level would actually be logged
     * by this logger. This check is based on the Logger's effective level.
     *
     * @param level a message logging level defined in java.util.logging.Level.
     * @return true if the given message level is currently being logged.
     */
    public boolean isAccessLoggable(Level level) {
        if (authSSOToken==null || !logStatus) {
            return false;
	}
        return accessLogger.isLoggable(level);
    }
    
    /**
     * Checks if an error message of the given level would actually be logged
     * by this logger. This check is based on the Logger's effective level.
     *
     * @param level a message logging level defined in java.util.logging.Level.
     * @return true if the given message level is currently being logged.
     */
    public boolean isErrorLoggable(Level level) {
       if (authSSOToken==null || !logStatus) {
            return false;
	}
        return errorLogger.isLoggable(level);

    }
}

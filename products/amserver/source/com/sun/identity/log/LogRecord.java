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
 * $Id: LogRecord.java,v 1.1 2006-03-31 05:07:04 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.common.Constants;
import com.sun.identity.log.spi.Debug;

/**
 * Extension to the JDK1.4 <code>LogRecord</code> to include the
 * <code>logInfo</code> <code>HashMap</code> and methods to store and retrieve
 * data from this <code>logInfo</code> Map. The <code>logInfo</code> Map is
 * supposed to be used by the client to fill in log-details which
 * will be used by the Formatter to construct the actual log string.
 *
 * For <code>JDK1.4</code> <code>LogRecord</code> please refer to 
 * <pre>
 * http://java.sun.com/j2se/1.4.1/docs/api/java/util/logging/LogRecord.html
 * </pre>
 * @supported.api
 */
public class LogRecord extends java.util.logging.LogRecord {
    /**
     * Construct the <code>LogRecord</code> with the given Level and message
     * values.
     *
     * @param level The log Level
     * @param msg The message string
     *
     * @supported.api
     */
    public LogRecord(Level level, String msg) {
        super(level,msg);
    }
    
    /**
     * Construct the <code>LogRecord</code> with the given Level and message
     * values.
     *
     * @param level The log Level.
     * @param msg The message string.
     * @param token The single sign-on token which will be used to fill in
     *        details like client IP address into the <code>LogRecord</code>.
     * @supported.api
     */
    public LogRecord(Level level, String msg, Object token) {
        this(level,msg);
        SSOToken ssoToken = null;
        if (token instanceof SSOToken) {
            ssoToken = (SSOToken)token;
        }
        String clientDomain = null;
        String clientID     = null;
        String ipAddress    = null;
        String hostName     = null;
        
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*
         * these are the compulsory fields ... to be logged even if there are
         * exceptions while getting domain, loginid, ipaddr, hostname
         */
        addLogInfo(LogConstants.TIME, sdf.format(date));
        addLogInfo(LogConstants.DATA, getMessage());
        addLogInfo(LogConstants.LOG_LEVEL, getLevel().toString());

        String tokenID = ssoToken.getTokenID().toString();

        try {
            /* get the context ID here, and addLogInfo() it... */
            String ctxID = ssoToken.getProperty(Constants.AM_CTX_ID);
            if ((ctxID != null) && (ctxID.length() > 0)) {
                addLogInfo(LogConstants.CONTEXT_ID, ctxID);
            }

            /*
             *  using the SSOToken, get the hostname first, as
             *  getting the IPAddr appears to use an Inet call using
             *  the hostname...
             *
             *  if com.sun.identity.log.resolveHostName=false, then
             *  IPAddr field will end up "Not Available"
             */
            hostName  = ssoToken.getHostName();

            if (Logger.resolveHostName) {
                java.net.InetAddress ipAddr = ssoToken.getIPAddress();
                if (ipAddr != null) {
                    /*
                     * getting a leading "/" from InetAddress.getByName(host)
                     * in SSOTokenImpl.java when "host" is an IPaddress.
                     */
                    ipAddress = ipAddr.getHostAddress();

                    /*
                     *  if no hostname returned, or only IP address,
                     *  try getting hostname from InetAddr
                     */
                    if ((hostName == null) ||
                        ((ipAddress != null) && (ipAddress.equals(hostName))))
                    {
                        hostName = ipAddr.getHostName();
                    }
                }
           }
            clientDomain = ssoToken.getProperty("cdomain");
            if (clientDomain == null || clientDomain.length() == 0) {
                clientDomain = ssoToken.getProperty("Organization");
            } 
            clientID = ssoToken.getPrincipal().getName();
        } catch (SSOException se) {
            /*
             *  internal auth session doesn't have IPaddr, so stacktrace
             *  was filling up amLog debug file.
             */
            Debug.error("LogRecord:LogRecord:SSOException: " + se.getMessage());
        }
        
        addLogInfo(LogConstants.DOMAIN, clientDomain);
        addLogInfo(LogConstants.LOGIN_ID, clientID);
        addLogInfo(LogConstants.IP_ADDR, ipAddress);
        addLogInfo(LogConstants.HOST_NAME, hostName);
        addLogInfo(LogConstants.LOGIN_ID_SID, tokenID);
    }

    /**
     * Constructor for auth logging
     * @param level The log Level.
     * @param msg The message string.
     * @param properties The Hashtable containing the properties
     *        for the LogRecord.
     */

    public LogRecord(Level level, String msg, Hashtable properties) {
        this(level,msg);
        String clientDomain = (String)properties.get(LogConstants.DOMAIN);
        String clientID     = (String)properties.get(LogConstants.LOGIN_ID);
        String ipAddress    = (String)properties.get(LogConstants.IP_ADDR); 
        String loginIDSid   = (String)properties.get(LogConstants.LOGIN_ID_SID);
        String moduleName   = (String)properties.get(LogConstants.MODULE_NAME);
        String contextID    = (String)properties.get(LogConstants.CONTEXT_ID);
        String messageID    = (String)properties.get(LogConstants.MESSAGE_ID);
        String hostName = ipAddress;
        if (ipAddress != null) {
            try {
                if (Logger.resolveHostName) {
                    hostName =
                        java.net.InetAddress.getByName(ipAddress).getHostName();
                } else {
                    hostName = ipAddress;
                }
            } catch (Exception e) {
               Debug.error("LogRecord:LogRecord:Unable to get Host for:" +
                   ipAddress);
            }
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*
         * these are the compulsory fields ... to be logged even if there are
         * exceptions while getting domain, loginid, ipaddr, hostname
         */
        addLogInfo(LogConstants.TIME, sdf.format(date));
        addLogInfo(LogConstants.DATA, getMessage());
        addLogInfo(LogConstants.LOG_LEVEL, getLevel().toString());
        addLogInfo(LogConstants.DOMAIN, clientDomain);
        addLogInfo(LogConstants.LOGIN_ID, clientID);
        addLogInfo(LogConstants.IP_ADDR, ipAddress);
        addLogInfo(LogConstants.HOST_NAME, hostName);
        addLogInfo(LogConstants.LOGIN_ID_SID, loginIDSid);
        addLogInfo(LogConstants.MODULE_NAME, moduleName);
        /* if they're implemented... */
        if ((messageID != null) && (messageID.length() > 0)) {
            addLogInfo(LogConstants.MESSAGE_ID, messageID);
        }
        if ((contextID != null) && (contextID.length() > 0)) {
            addLogInfo(LogConstants.CONTEXT_ID, contextID);
        }
    }

    private Map logInfoMap = new HashMap();
    
    /**
     * Adds to the log information map, the field key and its corresponding
     * value.
     *
     * @param key The key which will be used by the formatter to determine if
     *        this piece of info is supposed to be added to the log string
     *        according to the selected log fields.
     * @param value The value which may form a part of the actual log-string.
     * @supported.api
     */
    public void addLogInfo(String key,Object value) {
        logInfoMap.put(key,value);
    }
    
    /**
     * Convenience method to set the log information map.
     *
     * @param logInfoMap Handler to the map which contains the log info
     * @supported.api
     */
    public void setLogInfoMap(Map logInfoMap) {
        this.logInfoMap = logInfoMap;
    }
    /**
     * Returns the log information map which contains the set of fields and
     * their corresponding values.
     *
     * @return The log information map.
     * @supported.api
     */
    public Map getLogInfoMap() {
        return logInfoMap;
    }
}

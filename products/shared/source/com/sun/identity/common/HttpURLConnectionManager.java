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
 * $Id: HttpURLConnectionManager.java,v 1.1 2007-12-10 19:15:37 beomsuk Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLStreamHandler;
import com.sun.identity.protocol.AMURLStreamHandlerFactory;
import com.sun.identity.shared.Constants;
import com.sun.identity.shared.configuration.SystemPropertiesManager;
import com.sun.identity.shared.debug.Debug;

/**
 * The <code>HttpURLConnectionManager</code> class is used to get
 * <code>HttpURLConnection</code> instances and set connection timeout
 * if it supported by the JDK
 */

public class HttpURLConnectionManager {
  
    private static Debug debug = Debug.getInstance("PLLClient");
    private static int READTIMEOUT = 30000;
    private static final String URL_READTIMEOUT = 
           "com.sun.identity.url.readTimeout";
    private static Method method;
    private static Object[] args;
    private static String prot_handler_string = null;
    private static AMURLStreamHandlerFactory stFactory = 
            new AMURLStreamHandlerFactory();
    
    static {
        String rto = SystemPropertiesManager.get(URL_READTIMEOUT);
        prot_handler_string = 
                SystemPropertiesManager.get(Constants.PROTOCOL_HANDLER, null);
        if (debug.messageEnabled()) {
            debug.message("Configured Protocol Handler : " + 
                    prot_handler_string);
        }
        
        if (rto != null && rto.length() > 0) {
            try {
                READTIMEOUT = Integer.valueOf(rto).intValue();
                if (debug.messageEnabled()) { 
                    debug.message("HttpURLConnectionManager.<init>: " + 
                        "Set READTIMEOUT to " + READTIMEOUT);
                }
            } catch (Exception e) {
                debug.error("HttpURLConnectionManager.<init>: Fail to read " +
                        URL_READTIMEOUT + " set READTIMEOUT to the default " +
                        READTIMEOUT, e);
            }

            try {
                Method getlocalserver = Class.forName(
                        "com.iplanet.services.naming.WebtopNaming").
                        getMethod("getLocalServer", null);
                
                String serverURL = (String) getlocalserver.invoke(null, null);
                if (serverURL != null) {
                    URL url = new URL(serverURL);
                    HttpURLConnection conn = 
                            (HttpURLConnection)url.openConnection();
                    Class[] param = { Integer.TYPE };
                    method = conn.getClass().getMethod("setReadTimeout", param);
                    args = new Object[] { new Integer(READTIMEOUT) };
                } else if (debug.messageEnabled()) {
                    debug.message("HttpURLConnectionManager.<init>: " + 
                        "Server URL is not available");
                }
            } catch (NoSuchMethodException e) {
                debug.warning("HttpURLConnectionManager.<init>: " +
                    "setReadTimeout is not supported by the JVM", e);
            } catch (Exception e) {
                debug.error("HttpURLConnectionManager.<init>: " + 
                    "Failed to find setReadTimeout method ", e);
            }
        }
    }
    
    /**
     * Get the <code>HttpURLConnection</code> and set read timeout when possible
     * @param url The <code>URL</code> to open connection with
     * @exception IOException when calling <code>URL.openConnection</code> fails
     * @return A <code>HttpURLConnection</code>.
     */
    public static HttpURLConnection getConnection(URL url) throws IOException {
        if ((prot_handler_string != null) && 
                url.getProtocol().equalsIgnoreCase("https")) {
            url = new URL(url, url.toExternalForm(), 
                    stFactory.createURLStreamHandler("https"));
        }
        
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        if (method != null) {
            try {
                method.invoke(conn, args);
                if (debug.messageEnabled()) {
                    debug.message("HttpURLConnectionManager.getConnection: set read timeout to " + READTIMEOUT);
                }
            } catch(IllegalAccessException e) {
                debug.error("HttpURLConnectionManager.getConnection: Failed to set read timeout", e);
            } catch(IllegalArgumentException e) {
                debug.error("HttpURLConnectionManager.getConnection: Failed to set read timeout", e);
            } catch(InvocationTargetException e) {
                debug.error("HttpURLConnectionManager.getConnection: Failed to set read timeout", e);
            }
        }
        return conn;
    }
}

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
 * $Id: JAXRPCUtil.java,v 1.1 2005-11-01 00:31:15 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.jaxrpc;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.rpc.Stub;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.naming.URLNotFoundException;
import com.iplanet.services.naming.WebtopNaming;

/**
 * The class <code>JAXRPCUtil</code> provides functions to get JAXRPC stubs to
 * a valid Identity Server. The function
 * <code>getRemoteStub(String serviceName)</code> returns a JAXRPC stub to the
 * service. It is expected that the service caches the stub and re-uses it until
 * the server has failed. Upon server failure, the service needs to call the
 * function <code>serverFailed
 * (String serviceName)</code>, and the next call
 * to <code>
 * getRemoteStub(String serviceName)</code> will check for next valid
 * server and will return a stub that is currently active or throws
 * <code>java.rmi.RemoteException</code> if no servers are available.
 */

public class JAXRPCUtil {

    // Static constants
    final static String JAXRPC_URL = "com.sun.identity.jaxrpc.url";

    final static String JAXRPC_SERVICE = "jaxrpc";

    public final static String SMS_SERVICE = "SMSObjectIF";

    // Static variables
    private static HashMap remoteStubs = new HashMap();

    private static String validRemoteURL;

    private static boolean serverFailed = true;

    private static Debug debug = SOAPClient.debug;

    /**
     * Returns a valid URL endpoint for the given servie name. If no valid
     * servers are found, it throws <code>java.rmi.RemoteException</code>
     */
    public static String getValidURL(String serviceName) throws RemoteException 
    {
        // Validate service name
        if (serviceName == null) {
            // Service name should not be null
            if (debug.warningEnabled()) {
                debug.warning("JAXRPCUtil: Service name is null");
            }
            throw (new RemoteException("invalid-service-name"));
        }

        // Check if there is a valid server
        if (serverFailed) {
            validRemoteURL = getValidServerURL();
        }
        if (validRemoteURL == null) {
            // No valid servers where found, throw a RemoteExeption
            // Debug a warning
            if (debug.warningEnabled()) {
                debug.warning("JAXRPCUtil: No vaild server found");
            }
            throw (new RemoteException("no-server-found"));
        }
        return (validRemoteURL + serviceName);
    }

    /**
     * Returns a valid JAXRPC end point for the given service name. If no valid
     * servers are found, it throws <code>java.rmi.RemoteException</code>.
     */
    public static Object getRemoteStub(String serviceName)
            throws RemoteException {
        Object answer = null;
        if (serverFailed || (answer = remoteStubs.get(serviceName)) == null) {
            answer = getValidStub(serviceName);
            serverFailed = false;
        }
        return (answer);
    }

    /**
     * Sets the service to be failed.
     */
    public static void serverFailed(String serviceName) {
        if (serviceName.startsWith(validRemoteURL)) {
            serverFailed = true;
        } else {
            // Could be serviceName
            remoteStubs.remove(serviceName);
        }
    }

    protected synchronized static Object getValidStub(String serviceName)
            throws RemoteException {
        Object stub = getServiceEndPoint(getValidURL(serviceName));
        // Add to cache
        remoteStubs.put(serviceName, stub);
        return (stub);
    }

    // Protected static methods
    protected static String getValidServerURL() {
        // Check if the properties has been set
        String servers = SystemProperties.get(JAXRPC_URL);
        if (servers != null) {
            StringTokenizer st = new StringTokenizer(servers, ",");
            while (st.hasMoreTokens()) {
                String surl = st.nextToken();
                if (!surl.endsWith("/"))
                    surl += "/";
                if (isServerValid(surl)) {
                    return (surl);
                }
            }
        } else {
            // Get the list of platform servers from naming
            Enumeration sl = null;
            try {
                sl = WebtopNaming.getPlatformServerList().elements();
            } catch (Exception e) {
                // Unable to get platform server list
                if (debug.warningEnabled()) {
                    debug.warning("JAXRPCUtil:getValidServerURL: "
                            + "Unable to get platform server", e);
                }
                return (null);
            }

            while (sl != null && sl.hasMoreElements()) {
                try {
                    URL url = new URL((String) sl.nextElement());
                    URL weburl = WebtopNaming.getServiceURL(JAXRPC_SERVICE, url
                            .getProtocol(), url.getHost(), Integer.toString(url
                            .getPort()));
                    String surl = weburl.toString();
                    if (!surl.endsWith("/"))
                        surl += "/";
                    if (isServerValid(surl)) {
                        return (surl);
                    }
                } catch (MalformedURLException me) {
                    if (debug.warningEnabled()) {
                        debug.warning("JAXRPCUtil:getValidServerURL: ", me);
                    }
                } catch (URLNotFoundException me) {
                    if (debug.warningEnabled()) {
                        debug.warning("JAXRPCUtil:getValidServerURL: ", me);
                    }
                }
            }
        }
        return (null);
    }

    protected static Object getServiceEndPoint(String iurl) {
        if (debug.messageEnabled()) {
            debug.message("JAXRPCUtil Endpoint URL: " + iurl);
        }

        // Obtaining the stub for JAX-RPC and setting the endpoint URL
        Stub s = null;
        try {
            // Due to compilation errors, this function has been
            // made to use reflections
            Class imsClass = Class.forName(
                    "com.sun.identity.jaxrpc.IdentityManagementServices_Impl");
            Object imsImpl = imsClass.newInstance();
            Method method = null;
            if (iurl.endsWith(SMS_SERVICE)) {
                // Obtain the method "getSMSObjectIFPort" and invoke it
                method = imsClass.getMethod("getSMSObjectIFPort", null);
            } // %%% Add other service names here

            // Obtain the stub to be returned
            s = (Stub) method.invoke(imsImpl, null);
        } catch (ClassNotFoundException cnfe) {
            // Debug it
            if (debug.warningEnabled()) {
                debug.warning("JAXRPCUtil: unable to find class "
                        + "IdentityManagementServices_Impl", cnfe);
            }
        } catch (InstantiationException ne) {
            // Debug it
            if (debug.warningEnabled()) {
                debug.warning("JAXRPCUtil: unable to instantiate class "
                        + "IdentityManagementServices_Impl", ne);
            }
        } catch (IllegalAccessException iae) {
            // Debug it
            if (debug.warningEnabled()) {
                debug.warning("JAXRPCUtil: Illegal access to class "
                        + "IdentityManagementServices_Impl", iae);
            }
        } catch (Throwable t) {
            // Catch the remaining
            if (debug.warningEnabled()) {
                debug.warning("JAXRPCUtil:getServiceEndPoint exception", t);
            }
        }

        // Set the remote URL for the service
        if (s != null) {
            s._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, iurl);
        }
        return (s);
    }

    protected static boolean isServerValid(String url) {
        try {
            if (!url.endsWith(SMS_SERVICE)) {
                url += SMS_SERVICE;
            }
            SOAPClient client = new SOAPClient();
            client.setURL(url);
            client.send(client.encodeMessage("checkForLocal", null), null);
        } catch (Exception e) {
            // Server is not valid
            if (debug.messageEnabled()) {
                debug.message("JAXRPCUtil: Connection to URL: " + url
                        + " failed", e);
            }
            return (false);
        }
        return (true);
    }
}

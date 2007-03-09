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
 * $Id: AMSystemConfig.java,v 1.3 2007-03-09 05:51:00 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base.model;

import com.iplanet.am.util.SystemProperties;
import com.sun.identity.shared.Constants;
import com.sun.identity.sm.SMSEntry;

import netscape.ldap.LDAPDN;
import netscape.ldap.util.DN;
import netscape.ldap.util.RDN;

/* - NEED NOT LOG - */

/**
 * <code>AMSystemConfig</code> is contains system configuration information
 */
public class AMSystemConfig
    implements AMAdminConstants 
{
    /** 
     * Server deployment URI 
     */
    public static String serverDeploymentURI =
        SystemProperties.get(Constants.AM_SERVICES_DEPLOYMENT_DESCRIPTOR);

    /**   
     * Console deployment URI 
     */
    public static String consoleDeploymentURI =
        SystemProperties.get(Constants.AM_CONSOLE_DEPLOYMENT_DESCRIPTOR);

    /** 
     * Server protocol
     */
    public static String serverProtocol = SystemProperties.get(
        Constants.AM_SERVER_PROTOCOL);

    /** 
     * Server host name
     */
    public static String serverHost = SystemProperties.get(
        Constants.AM_SERVER_HOST);

    /** 
     * Server port name 
     */
    public static String serverPort = SystemProperties.get(
        Constants.AM_SERVER_PORT);

    /** 
     * Server URL
     */
    public static String serverURL = serverProtocol + "://" + serverHost + ":"
        + serverPort;

    /** 
     * Determines if console is remote 
     */
    public static boolean isConsoleRemote = Boolean.valueOf(
        SystemProperties.get(Constants.AM_CONSOLE_REMOTE)).booleanValue();

    /** 
     * default organization 
     */
    public static String defaultOrg = SMSEntry.getRootSuffix();

    /** 
     * version 
     */
    public static String version =
        normalizeString(SystemProperties.get(Constants.AM_VERSION));

    public static String normalizeString(String tmp) {
        if (tmp != null) {
            // parse string to remove any extraneous space
            String[] dns = LDAPDN.explodeDN(tmp, false);
            if ((dns != null) && (dns.length > 0)) {
                int len = dns.length -1;
                DN root = new DN(dns[len]);

                for (int i = len -1; i >= 0; --i) {
                    root.addRDN(new RDN(dns[i]));
                }

                tmp = root.toString();
            }
        } else {
            tmp = "";
        }
        return tmp;
    }
}

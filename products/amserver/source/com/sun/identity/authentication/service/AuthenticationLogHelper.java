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
 * $Id: AuthenticationLogHelper.java,v 1.1 2006-01-28 09:16:34 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.authentication.service;

import java.util.Hashtable;

import com.iplanet.sso.SSOToken;

/**
 * Defines the interface that allows authentication services to log messages.
 * The implementation of this interface is determined during runtime.
 *
 * @see AuthenticationLogHelperFactory
 */

public interface AuthenticationLogHelper {
    /**
     * Logs informational message to authentication log file.
     *
     * @param messageId Identifier of the log entry.
     * @param data String array of log data.
     * @param properties Map single sign on token attribute to its values.
     *        If this map is <code>null</code>, attribute values will be
     *        retrieved from <code>ssoToken</code>.
     * @param ssoToken Single Sign On Token to be used for logging the entry.
     */
    void logMessage(
        String messageId,
        String data[],
        Hashtable properties,
        SSOToken ssoToken);

    /**
     * Logs error message to authentication log file.
     *
     * @param messageId Identifier of the log entry.
     * @param data String array of log data.
     * @param properties Map single sign on token attribute to its values.
     *        If this map is <code>null</code>, attribute values will be
     *        retrieved from <code>ssoToken</code>.
     * @param ssoToken Single Sign On Token to be used for logging the entry.
     */
    void logError(
        String messageId,
        String data[],
        Hashtable properties,
        SSOToken ssoToken);
}

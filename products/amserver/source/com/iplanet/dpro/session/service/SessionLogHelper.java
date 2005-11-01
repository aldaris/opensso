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
 * $Id: SessionLogHelper.java,v 1.1 2005-11-01 00:29:56 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.dpro.session.service;

import java.util.logging.Level;

import com.iplanet.sso.SSOToken;

/**
 * Allows the <code>SessionService</code> to log message via the logging
 * subsystem represented by an implementation of this interface. This interface
 * is used to log regular messages as well as system level error messages using
 * the provided methods. An implementation of this interface can be located
 * during runtime using the factory method in
 * <code>SessionLogHelperFactory</code> class.
 */
public interface SessionLogHelper {

    /**
     * Logs an informational message in the Session log file. This message is
     * constructed using the given <code>id</code> and populated with
     * information obtained using the given internal session.
     * 
     * @param sess
     *            the internal session to be used for populating message data.
     * @param id
     *            the message identifier used to construct the message.
     * @param sessionServiceToken
     *            the application SSO token representing the session service.
     */
    public void logMessage(InternalSession sess, String id,
            SSOToken sessionServiceToken);

    /**
     * Logs an error message in the Session error log file. This message is
     * constructed using the given <code>id</code>. The suggested
     * <code>level</code> is used for further classification of this message.
     * 
     * @param id
     *            the message identifier used to construct the message.
     * @param level
     *            the level of the message.
     * @param sessionServiceToken
     *            the application SSO token representing the session service.
     */
    public void logSystemMessage(String id, Level level,
            SSOToken sessionServiceToken);

}

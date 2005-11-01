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
 * $Id: SessionLogHelperFactory.java,v 1.1 2005-11-01 00:29:56 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.dpro.session.service;

import com.iplanet.am.util.SystemProperties;

/**
 * A factory to access the <code>SessionLogHelper</code> instance during
 * runtime. This factory uses the configuration key
 * <code>com.sun.identity.session.loghelper</code> to identify the
 * implementation of <code>SessionLogHelper</code> interface that will be used
 * at runtime.
 */
public class SessionLogHelperFactory {

    /**
     * The configuration key used for identifying the class that implements the
     * <code>SessionLogHelper</code> interface.
     */
    public static final String CONFIG_SESSION_LOG_HELPER = 
        "com.sun.identity.session.loghelper";

    /**
     * The default implementation to be used in case no value is specified in
     * the configuration.
     */
    public static final String DEFAULT_SESSION_LOG_HELPER = 
        "com.iplanet.dpro.session.service.SessionLogHelperImpl";

    /** The singleton instance of <code>SessionLogHelper</code> to be used. */
    private static SessionLogHelper sessionLogHelper;

    /**
     * Provides access to the configured implementation of
     * <code>SessionLogHelper</code> interface. An instance of this
     * implementation is constructed during static initialization of this
     * factory and is kept as a singleton throughout its lifecycle.
     * 
     * @return the configured implementation of <code>SessionLogHelper</code>.
     */
    public static SessionLogHelper getSessionLogHelper() {
        return sessionLogHelper;
    }

    static {
        try {
            String className = SystemProperties.get(CONFIG_SESSION_LOG_HELPER,
                    DEFAULT_SESSION_LOG_HELPER);

            sessionLogHelper = (SessionLogHelper) Class.forName(className)
                    .newInstance();

        } catch (Exception ex) {
            SessionService.sessionDebug.error(
                    "Failed to initialize SessionLogHelperFactory", ex);
        }
    }
}

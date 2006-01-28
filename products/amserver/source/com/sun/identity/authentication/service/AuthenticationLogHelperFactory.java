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
 * $Id: AuthenticationLogHelperFactory.java,v 1.1 2006-01-28 09:16:36 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.authentication.service;

import com.iplanet.am.util.SystemProperties;

/**
 * A factory to access <code>AuthenticationLogHelper</code> instance.
 * This factory uses the configuration key
 * <code>com.sun.identity.authentication.loghelper</code> to identify the
 * implementation of <code>AuthenticationLogHelper</code> interface; 
 * instantiates this class; and returns the instance for logging purposes.
 */
public class AuthenticationLogHelperFactory {

    /**
     * The configuration key used for identifying the implemenation class of
     * <code>AuthenticationLogHelper</code> interface.
     */
    public static final String CONFIG_LOG_HELPER =
        "com.sun.identity.authentication.loghelper";

    /**
     * The default implementation to be used in case no value is specified in
     * the configuration.
     */
    public static final String DEFAULT_LOG_HELPER =
"com.sun.identity.authentication.service.AuthenticationLogHelperImpl";

    /**
     * Singleton instance of <code>AuthenticationLogHelper</code>.
     */
     private static AuthenticationLogHelper logHelper;

     static {
        String className = SystemProperties.get(CONFIG_LOG_HELPER,
            DEFAULT_LOG_HELPER);
            
        try {
            logHelper = (AuthenticationLogHelper)Class.forName(className)
                .newInstance();
        } catch (Exception e) {
            AuthD.debug.error("Failed to instantiate : " + className 
                + e.toString());
        } 
     }

     private AuthenticationLogHelperFactory() {
     }

     /**
      * Returns an instance of <code>AuthenticationLogHelper</code>.
      * This instance is instantiated during static initialization of this
      * factory and is kept as a singleton throughout its lifecycle.
      *
      * @return an instance of <code>AuthenticationLogHelper</code>.
      */
    public static AuthenticationLogHelper getLogHelper() {
        return logHelper;
    }
}

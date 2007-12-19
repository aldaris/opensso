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
 * $Id: IClientConfigurationKeyConstants.java,v 1.2 2007-12-19 21:04:35 sean_brydon Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.arch;

/**
 * Constants used for identifying Client SDK Configuration keys.
 */
public interface IClientConfigurationKeyConstants {
    
   /**
    * Configuration key for locating the AMAgent file when secondary
    * configuration is in use.
    */
    public static final String SDKPROP_AMAGENT_LOCATION =
        "com.sun.identity.agents.config.location";
    
   /**
    * Configuration key for application user name.
    */
    public static final String SDKPROP_APP_USERNAME =
        "com.sun.identity.agents.app.username";
    
   /**
    * Configuration key for application password (plain text)
    */
    public static final String SDKPROP_APP_PASSWORD = 
        "com.iplanet.am.service.secret";
    
    /**
     * Client encryption key
     */
     public static final String SDKPROP_CLIENT_ENC_KEY = 
         "com.sun.identity.client.encryptionKey";
    
    /**
     * Configuration key for debug level property.
     */
     public static final String SDKPROP_DEBUG_LEVEL = 
         "com.iplanet.services.debug.level";
     
    /**
     * Configuration key for SSO Token Cookie Name property.
     */
     public static final String SDKPROP_SSO_COOKIE_NAME =
         "com.iplanet.am.cookie.name";
     
    /**
     * Configuration key for Session notification URL.
     */
     public static final String SDKPROP_SESSION_NOTIFICATION_URL =
         "com.iplanet.am.notification.url";
     
     public static final String SDKPROP_SESSION_POLLING_ENABLE =
         "com.iplanet.am.session.client.polling.enable";
     
    /**
     * Configuration key for Policy Notification URL.
     */
     public static final String SDKPROP_POLICY_NOTIFICATION_URL =
         "com.sun.identity.agents.notification.url";

    /**
     * Configuration key for Policy notification enabled flag.
     */
     public static final String SDKPROP_POLICY_NOTIFICATION_ENABLE =
         "com.sun.identity.agents.notification.enabled";
     
    /**
     * Configuration key for Client Notification URL for agent to receive
     * notification messages for policy, session, and agent configuration
     * changes.
     */
     public static final String SDKPROP_CLIENT_NOTIFICATION_URL =
         "com.sun.identity.client.notification.url";
}

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
 * $Id: TestConstants.java,v 1.1 2007-02-06 19:55:34 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

public interface TestConstants {

    /**
     * Property name for AMConfig.properties file
     */
    String TEST_PROPERTY_AMCONFIG = "AMConfig";

    /**
     * Property name for logging level.
     */
    String KEY_LOG_LEVEL = "qatest.log.level";

    /**
     * Property key for <code>amadmin</code> user.
     */
    String KEY_AMADMIN_USER = "qatest.amadmin.user";

    /**
     * Property key for <code>amadmin</code> user password.
     */
    String KEY_AMADMIN_PASSWORD = "qatest.amadmin.password";

    /**
     * Property key for <code>com.iplanet.am.defaultOrg</code>.
     */
    String KEY_BASEDN = "com.iplanet.am.defaultOrg";

    /**
     * Property key for <code>planet.am.server.protocol</code>.
     */
    String KEY_PROTOCOL = "com.iplanet.am.server.protocol";

    /**
     * Property key for <code>com.iplanet.am.server.host</code>.
     */
    String KEY_HOST = "com.iplanet.am.server.host";

    /**
     * Property key for <code>com.iplanet.am.server.port</code>.
     */
    String KEY_PORT = "com.iplanet.am.server.port";

    /**
     * Property key for
     * <code>com.iplanet.am.services.deploymentDescriptor</code>.
     */
    String KEY_URI = "com.iplanet.am.services.deploymentDescriptor";

    /**
     * Property key for <code>realm</code>.
     */
    String KEY_REALM = "qatest.realm";
} 

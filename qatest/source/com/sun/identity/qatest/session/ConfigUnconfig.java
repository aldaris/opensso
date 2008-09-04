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
 * $Id: ConfigUnconfig.java,v 1.1 2008-09-04 16:33:36 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.session;

import com.sun.identity.qatest.common.TestCommon;
import java.util.Map;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This class starts and stops the notification server.
 */
public class ConfigUnconfig extends TestCommon {
    Map notificationMap;
    
    /**
     * Creates a new instance of ConfigUnconfig
     */
    public ConfigUnconfig() {
        super("ConfigUnconfig");
    }
    
    /**
     * Start the notification (jetty) server for getting notifications from the
     * server.
     */
    @BeforeSuite(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void startServer()
    throws Exception {
        entering("startServer", null);
        notificationMap = startNotificationServer();
        exiting("startServer");
    }

    /**
     * Stop the notification (jetty) server for getting notifications from the
     * server.
     */
    @AfterSuite(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void stopServer()
    throws Exception {
        entering("stopServer", null);
        stopNotificationServer(notificationMap);
        exiting("stopServer");
    }
}

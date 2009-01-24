/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * "Portions Copyrighted [year] [name of copyght owner]"
 *
 * $Id: WSSCache.java,v 1.1 2009-01-24 01:31:26 mallas Exp $
 *
 */

package com.sun.identity.wss.security.handler;

import com.sun.identity.common.PeriodicCleanUpMap;
import com.sun.identity.wss.security.WSSConstants;
import com.sun.identity.wss.security.WSSUtils;
import com.sun.identity.common.SystemConfigurationUtil;

/**
 * This class <code>WSSCache</code> is a cache holder for the WSS
 * tokens.
 */
public class WSSCache {
    
    public static int interval = WSSConstants.CACHE_CLEANUP_INTERVAL_DEFAULT;
    
    static {
        String intervalStr = SystemConfigurationUtil.getProperty(
                     WSSConstants.CACHE_CLEANUP_INTERVAL);
        try {
            if (intervalStr != null && intervalStr.length() != 0) {
                interval = Integer.parseInt(intervalStr);
                if (interval < 0) {
                    interval =
                        WSSConstants.CACHE_CLEANUP_INTERVAL_DEFAULT;
                }
            }
        } catch (NumberFormatException e) {
            if (WSSUtils.debug.messageEnabled()) {
                WSSUtils.debug.message("WSSCache static: "
                    + "invalid cleanup interval. Using default.");
            }
        }
    }
    
    public static PeriodicCleanUpMap nonceCache = new PeriodicCleanUpMap(
        interval * 1000, interval * 1000);
    
    

}

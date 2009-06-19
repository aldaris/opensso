/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: SsoServerIdRepoSvcImpl.java,v 1.1 2009-06-19 02:23:16 bigfatrat Exp $
 *
 */

package com.sun.identity.monitoring;

import com.sun.identity.shared.debug.Debug;
import com.sun.management.snmp.agent.SnmpMib;
import javax.management.MBeanServer;

/**
 * This class extends the "SsoServerIdRepoSvc" class.
 */
public class SsoServerIdRepoSvcImpl extends SsoServerIdRepoSvc {
    private static Debug debug = null;
    private static String myMibName;

    /**
     * Constructor
     */
    public SsoServerIdRepoSvcImpl (SnmpMib myMib) {
        super(myMib);
        myMibName = myMib.getMibName();
        init(myMib, null);
    }

    public SsoServerIdRepoSvcImpl (SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
        myMibName = myMib.getMibName();
        init(myMib, server);
    }

    private void init(SnmpMib myMib, MBeanServer server) {
        if (debug == null) {
            debug = Debug.getInstance("amMonitoring");
        }
        SsoServerIdRepoSearchCacheHits = new Long(0);
        SsoServerIdRepoSearchRqts = new Long(0);
        SsoServerIdRepoCacheHits = new Long(0);
        SsoServerIdRepoCacheEntries = new Long(0);
        SsoServerIdRepoGetRqts = new Long(0);
    }

    /*
     * corresponds to idm's updateSearchHitCount, which
     * increments totalSearchHits and totalIntervalHits
     */
    public void incSearchCacheHits (long cacheEntries) {
        long li = SsoServerIdRepoSearchCacheHits.longValue();
        li++;
        SsoServerIdRepoSearchCacheHits = Long.valueOf(li);
        SsoServerIdRepoCacheEntries = Long.valueOf(cacheEntries);
    }

    /*
     * corresponds to idm's incrementGetRequestCount, which
     * increments totalGetRequests and intervalCount
     */
    public void incGetRqts (long cacheEntries) {
        long li = SsoServerIdRepoGetRqts.longValue();
        li++;
        SsoServerIdRepoGetRqts = Long.valueOf(li);
        SsoServerIdRepoCacheEntries = Long.valueOf(cacheEntries);
    }

    /*
     * corresponds to idm's updateGetHitCount, which
     * increments totalGetCacheHits and totalIntervalHits
     */
    public void incCacheHits (long cacheEntries) {
        long li = SsoServerIdRepoCacheHits.longValue();
        li++;
        SsoServerIdRepoCacheHits = Long.valueOf(li);
        SsoServerIdRepoCacheEntries = Long.valueOf(cacheEntries);
    }

    /*
     * corresponds to idm's incrementSearchRequestCount, which
     * increments totalSearchRequests and intervalCount
     */
    public void incSearchRqts (long cacheEntries) {
        long li = SsoServerIdRepoSearchRqts.longValue();
        li++;
        SsoServerIdRepoSearchRqts = Long.valueOf(li);
        SsoServerIdRepoCacheEntries = Long.valueOf(cacheEntries);
    }
}

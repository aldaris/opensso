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
 * $Id: CacheStats.java,v 1.1 2005-11-01 00:29:24 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.Stats;
import com.iplanet.am.util.StatsListener;

// Class used for synchronization purpose.
class CacheStats implements StatsListener {

    int intervalCount = 0; // interval counter

    long totalGetRequests = 0; // Overall get requests

    long totalCacheHits = 0; // Overall cache hits

    long totalIntervalHits = 0; // Hits during interval

    static Stats stats = null;

    static CacheStats instance = null;

    static final String CACHE_STATS_FILE_NAME = "amSDKStats";

    static Debug debug = AMCommonUtils.debug;

    static {
        stats = Stats.getInstance(CACHE_STATS_FILE_NAME);
        instance = new CacheStats();
        if (stats.isEnabled()) {
            stats.addStatsListener(instance);
            if (debug.messageEnabled()) {
                debug.message("CacheStats.static{} Stats : "
                        + stats.isEnabled()
                        + " SDK cache stats listener added.");
            }
        }
    }

    /**
     * Method returns a CacheStats object. A listener would have been added only
     * if cache stats was enabled.
     */
    protected static CacheStats getInstance() {
        return instance;
    }

    protected CacheStats() {
        // Do nothing
    }

    protected void updateHitCount() {
        if (stats.isEnabled()) {
            synchronized (this) {
                totalCacheHits++;
                totalIntervalHits++;
            }
        }
    }

    protected void incrementRequestCount() {
        if (stats.isEnabled()) {
            synchronized (this) {
                totalGetRequests++;
                intervalCount++;
            }
        }
    }

    protected synchronized int getIntervalCount() {
        return intervalCount;
    }

    public synchronized void printStats() {
        // Get the cache manager instance. If the flow comes here then
        // the instance should definitely be AMCacheManager instance.
        AMCacheManager cManager = (AMCacheManager) AMDirectoryWrapper
                .getInstance();

        // Print Stats information
        stats.record("SDK Cache Statistics" + "\n--------------------"
                + "\nNumber of requests during this interval: " + intervalCount
                + "\nNumber of Cache Hits during this interval: "
                + totalIntervalHits + "\nHit ratio for this interval: "
                + (double) totalIntervalHits / (double) intervalCount
                + "\nTotal number of requests since server start: "
                + totalGetRequests
                + "\nTotal number of Cache Hits since server start: "
                + totalCacheHits + "\nOverall Hit ratio: "
                + (double) totalCacheHits / (double) totalGetRequests
                + "\nTotal Cache Size: " + cManager.getCachesize() + "\n");

        // Reset interval hits to 0
        intervalCount = 0;
        totalIntervalHits = 0;
    }
}

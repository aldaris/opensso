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
 * $Id: FSArtifactStats.java,v 1.1 2006-10-30 23:14:18 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.federation.services;

import com.sun.identity.shared.stats.StatsListener;
import java.util.Map;

/**
 * Manages statistics of artifacts.
 */
public class FSArtifactStats implements StatsListener {

    private Map table;
    private String providerId;

    /**
     * Constructs a <code>FSArtifactStats</code> object for a given provider.
     * @param table Map of artifact and assertion id
     * @param providerId provider id
     */
    public FSArtifactStats(Map table, String providerId) {
        this.table = table;
        this.providerId = providerId;
    }

    /**
     * Prints the number of artifacts in the table for a provider when the 
     * stats time interval elapsed.
     */
    public void printStats() {
        if (table.size() != 0 ) {
            FSAssertionManager.artStats.record(
                "Number of artifact in table for provider " +
                providerId + " : " + table.size());
        } else {
            FSAssertionManager.artStats.record(
                "No artifact found in table for provider " + providerId + "."); 
        }
    }
}

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
 * $Id: FSSessionMapStats.java,v 1.1 2006-10-30 23:14:25 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.federation.services;

import com.sun.identity.shared.stats.StatsListener;
import java.util.Map;

/**
 * Handles statistics for session maps.
 */
public class FSSessionMapStats implements StatsListener {

     private Map table;
     private String name;
     private String providerId;

     /**
      * Constructs a new <code>FSSessionMapStats</code> object.
      * @param table a map whose statistics to be run
      * @param name name of the map
      * @param providerId hosted provider ID
      */
     public FSSessionMapStats(Map table, String name, String providerId) {
         this.table = table;
         this.name = name;
         this.providerId = providerId;
     }

     /**
      * Prints the statistics of the map.
      */
     public void printStats() {
         if (table.size() != 0 ) {
             FSSessionManager.sessStats.record("Number of entries in " + name 
                  + " table for provider " + providerId + " : " 
                  + table.size());
         } else {
             FSSessionManager.sessStats.record("No entry found in " + name 
                  + " table for provider " + providerId + "."); 
         }
     }
}

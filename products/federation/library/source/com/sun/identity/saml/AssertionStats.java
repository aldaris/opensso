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
 * $Id: AssertionStats.java,v 1.1 2006-10-30 23:15:33 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml;

import java.util.Map;
import com.sun.identity.shared.stats.StatsListener;

public class AssertionStats implements StatsListener {

     private Map assertionMap;

     public AssertionStats(Map am) {
         assertionMap = am;
     }

     public void printStats() {
         if (!assertionMap.isEmpty()) {
             SAMLStatsAccessor.getAccessor().setTotalAssertions(
                 assertionMap.size());
             AssertionManager.assStats.record("Size of Assertion Map:" + 
                 Integer.toString(assertionMap.size()));
         } else {
             AssertionManager.assStats.record("No Assertions found in Map"); 
         }
     }
}

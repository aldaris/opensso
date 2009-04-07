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
 * $Id: AndConditionTest.java,v 1.3 2009-04-07 19:00:48 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.Test;

/**
 *
 * @author dillidorai
 */
public class AndConditionTest {

    @Test
    public void testConstruction() throws Exception {

        IPCondition ipc = new IPCondition("100.100.100.100", "200.200.200.200");
        ipc.setPConditionName("ip1");
        DNSNameCondition dnsc = new DNSNameCondition("*.sun.com");
        dnsc.setPConditionName("ip2");
        TimeCondition tc = new TimeCondition("08:00", "16:00",
                "mon", "fri");
        tc.setStartDate("01/01/2001");
        tc.setEndDate("02/02/2002");
        tc.setEnforcementTimeZone("PST");
        tc.setPConditionName("tc1");
        UnittestLog.logMessage(
                "AndConditionTest.testConstruction():" + "tc.toString()="
                + tc.toString());

        Set<EntitlementCondition> conditions
                = new HashSet<EntitlementCondition>();
        conditions.add(ipc);
        conditions.add(dnsc);
        conditions.add(tc);
        AndCondition ac = new AndCondition(conditions);
        UnittestLog.logMessage(
                "AndConditionTest.testConstruction():" + "ac.toString()="
                + ac.toString());

    }

    public static void main(String[] args) throws Exception {
        new AndConditionTest().testConstruction();
        UnittestLog.flush(new Date().toString());
    }
}

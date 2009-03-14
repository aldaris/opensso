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
 * $Id: OrConditionTest.java,v 1.1 2009-03-14 03:08:00 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;

import com.sun.identity.entitlement.IPCondition;
import com.sun.identity.entitlement.OrCondition;
import com.sun.identity.entitlement.TimeCondition;
import java.util.Date;

import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.Test;

/**
 *
 * @author dillidorai
 */
public class OrConditionTest {

    @Test
    public void testConstruction() throws Exception {

        IPCondition ipc = new IPCondition("*.sun.com",
                "100.100.100.100", "200.200.200.200");
        ipc.setPConditionName("ip1");
        UnittestLog.logMessage(
                "OrConditionTest.testConstruction():" + "ipc.toString()=" + ipc.toString());

        TimeCondition tc = new TimeCondition("08:00", "16:00",
                "mon", "fri");
        tc.setStartDate("01/01/2001");
        tc.setEndDate("02/02/2002");
        tc.setEnforcementTimeZone("PST");
        tc.setPConditionName("tc1");
        UnittestLog.logMessage(
                "OrConditionTest.testConstruction():" + "tc.toString()=" + tc.toString());

        Set<EntitlementCondition> conditions = new HashSet<EntitlementCondition>();
        conditions.add(ipc);
        conditions.add(tc);
        OrCondition oc = new OrCondition(conditions);
        UnittestLog.logMessage(
                "OrConditionTest.testConstruction():" + "oc.toString()=" + oc.toString());

    }

    public static void main(String[] args) throws Exception {
        new OrConditionTest().testConstruction();
        UnittestLog.flush(new Date().toString());
    }
}

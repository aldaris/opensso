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
 * $Id: IPConditionTest.java,v 1.2 2009-04-07 19:00:48 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;

import java.util.Date;
import org.testng.annotations.Test;

/**
 *
 * @author dillidorai
 */
public class IPConditionTest {

    @Test
    public void testConstruction() throws Exception {
        IPCondition ipc = new IPCondition("100.100.100.100", "200.200.200.200");
        ipc.setPConditionName("ip1");
        DNSNameCondition dnsc = new DNSNameCondition("*.sun.com");
        dnsc.setPConditionName("ip2");

        IPCondition ipc1 = new IPCondition();
        ipc1.setState(ipc.getState());
        UnittestLog.logMessage(
            "IPConditionTest.testConstruction():" + "ipc1.toString()=" +
            ipc1.toString());
        UnittestLog.logMessage(
            "IPConditionTest.testConstruction():" + 
            "resetting startIp, endIp");
        DNSNameCondition dnsc1 = new DNSNameCondition();
        dnsc1.setState(dnsc.getState());

        dnsc1.setDomainNameMask("*.red.iplanet.com");
        ipc1.setStartIp("101.101.101.101");
        ipc1.setEndIp("201.201.201.201");
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + "ipc1.toString()=" + ipc1.toString());
    }

    public static void main(String[] args) throws Exception {
        new IPConditionTest().testConstruction();
        UnittestLog.flush(new Date().toString());
    }
}

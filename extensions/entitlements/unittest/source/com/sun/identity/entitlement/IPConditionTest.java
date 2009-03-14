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
 * $Id: IPConditionTest.java,v 1.1 2009-03-14 03:07:59 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;

import com.sun.identity.entitlement.IPCondition;
import java.util.Date;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

/**
 *
 * @author dillidorai
 */
public class IPConditionTest {

    @Test
    public void testConstruction() throws Exception {
        IPCondition ipc = new IPCondition("*.sun.com",
                "100.100.100.100", "200.200.200.200");
        //ipc.setPConditionName("ip1");
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + "ipc.toString()=" + ipc.toString());

        IPCondition ipc1 = new IPCondition();
        ipc1.setState(ipc.getState());
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + "ipc1.toString()=" + ipc1.toString());


        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + "resetting dns mask, startIp, endIp");
        ipc1.setDomainNameMask("*.red.iplanet.com");
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

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
 * $Id: IPConditionTest.java,v 1.3 2009-04-16 00:58:09 dillidorai Exp $
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
        String startIp = "100.100.100.100";
        String endIp = "200.200.200.200";
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " startIp=" + startIp);
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " endIp=" + endIp);
        IPCondition ipc = new IPCondition(startIp, endIp);
        ipc.setPConditionName("ip1");

        String readIp = ipc.getStartIp();
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " read startIp=" + readIp);
        if (!startIp.equals(readIp)) {
            UnittestLog.logMessage(
                    "IPConditionTest.testConstruction():" + " read startIp" + " did not tequal startIp set in constructor");
            throw new Exception("IPConditionTest.testConstruction():" + " read startIp" + " did not tequal startIp set in constructor");
        }

        readIp = ipc.getEndIp();
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " read endIp=" + readIp);
        if (!endIp.equals(readIp)) {
            UnittestLog.logMessage(
                    "IPConditionTest.testConstruction():" + " read startIp" + " did not tequal startIp set in constructor");
            throw new Exception("IPConditionTest.testConstruction():" + " read endIp" + " did not tequal endIp set in constructor");
        }

        startIp = "120.120.120.120";
        endIp = "220.220.220.220";
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " resetting startIp=" + startIp);
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " resetting endIp=" + endIp);
        ipc.setStartIp(startIp);
        ipc.setEndIp(endIp);

        readIp = ipc.getStartIp();
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " read startIp=" + readIp);
        if (!startIp.equals(readIp)) {
            UnittestLog.logMessage(
                    "IPConditionTest.testConstruction():" + " read startIp" + " did no tequal startIp set");
            throw new Exception("IPConditionTest.testConstruction():" + " read startIp" + " did no tequal startIp set");
        }

        readIp = ipc.getEndIp();
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " read endIp=" + readIp);
        if (!endIp.equals(readIp)) {
            UnittestLog.logMessage(
                    "IPConditionTest.testConstruction():" + " read startIp" + " did no tequal startIp set");
            throw new Exception("IPConditionTest.testConstruction():" + " read endIp" + " did not tequal endIp set");
        }

        String dnsName = "*.sun.com";
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " dnsName=" + dnsName);
        DNSNameCondition dnsc = new DNSNameCondition(dnsName);
        dnsc.setPConditionName("ip2");

        String rdnsName = dnsc.getDomainNameMask();
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " read dnsName=" + rdnsName);
        if (!dnsName.equals(rdnsName)) {
            UnittestLog.logMessage(
                    "IPConditionTest.testConstruction():" + " read dnsName" + " did not tequal dnsName set in constructor");
            throw new Exception("IPConditionTest.testConstruction():" + " read dnsName" + " did not tequal dnsName set in constructor");
        }
        dnsName = "*.iplanet.com";
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " resetting dnsName=" + dnsName);
        dnsc.setDomainNameMask(dnsName);
        rdnsName = dnsc.getDomainNameMask();
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " read dnsName=" + rdnsName);
        if (!dnsName.equals(rdnsName)) {
            UnittestLog.logMessage(
                    "IPConditionTest.testConstruction():" + " read dnsName" +
                    " did not tequal dnsName set");
            throw new Exception("IPConditionTest.testConstruction():" + " read dnsName" + " did not tequal dnsName set");
        }

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

        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + " getStartIp()=" + ipc1.getStartIp());
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + "getEndIp()=" + ipc1.getEndIp());
        UnittestLog.logMessage(
                "IPConditionTest.testConstruction():" + "getDomainNameMask()=" + dnsc1.getDomainNameMask());


    }

    public static void main(String[] args) throws Exception {
        new IPConditionTest().testConstruction();
        UnittestLog.flush(new Date().toString());
    }
}

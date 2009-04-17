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
 * $Id: NotSubjectTest.java,v 1.1 2009-04-17 23:57:03 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;

import java.util.Date;
import org.testng.annotations.Test;

/**
 *
 * @author dillidorai
 */
public class NotSubjectTest {

    @Test
    public void testConstruction() throws Exception {

        UserSubject us1 = new UserSubject("user11");
        us1.setPSubjectName("u1");
        UserSubject us2 = new UserSubject("user12");
        us2.setPSubjectName("u1");
        GroupSubject gs1 = new GroupSubject("group11");
        gs1.setPSubjectName("g1");
        GroupSubject gs2 = new GroupSubject("group12");
        gs1.setPSubjectName("g1");
        GroupSubject gs3 = new GroupSubject("group31");
        gs1.setPSubjectName("g3");
        RoleSubject rs1 = new RoleSubject("role1");
        rs1.setPSubjectName("r1");
        NotSubject ns1 = new NotSubject(rs1);
        ns1.setPSubjectName("r1");
        UnittestLog.logMessage(
                "NotSubjectTest.testConstruction():" + "ns1.toString()="
                + ns1.toString());
        NotSubject ns2 = new NotSubject();
        ns2.setState(ns1.getState());

        UnittestLog.logMessage(
                "OrSubjectTest.testConstruction():" + "ns2.toString()="
                + ns2.toString());

        boolean result = ns1.equals(ns2);
        UnittestLog.logMessage(
                "NotSubjectTest.testConstruction():"
                + "NotSubject with setState="
                + "equals NotSubject with getState():" + result);
        if (!result) {
            UnittestLog.logMessage(
                    "NotSubjectTest.testConstruction():"
                    + "NotSubject with setState="
                    + "does not equal NotSubject with getState()");

            throw new Exception("NotSubjectTest.testConstruction():"
                    + "NotSubject with setState="
                    + "does not equal NotSubject with getState()");

        }
    }

    public static void main(String[] args) throws Exception {
        new NotSubjectTest().testConstruction();
        UnittestLog.flush(new Date().toString());
    }
}

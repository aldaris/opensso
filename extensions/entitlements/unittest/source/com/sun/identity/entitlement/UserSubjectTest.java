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
 * $Id: UserSubjectTest.java,v 1.4 2009-04-21 13:08:03 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;

import java.util.Date;

import org.testng.annotations.Test;

/**
 *
 * @author dillidorai
 */
public class UserSubjectTest {

    @Test
    public void testConstruction() throws Exception {
        UserSubject us = new UserSubject("user1");
        us.setPSubjectName("u1");
        UnittestLog.logMessage(
                "UserSubjectTest.testConstruction():" + "us.toString()="
                + us.toString());

        UserSubject us1 = new UserSubject();
        us1.setState(us.getState());
        UnittestLog.logMessage(
                "UserSubject.testConstruction():" + "us1.toString()="
                + us1.toString());


        UnittestLog.logMessage(
                "UserSubjectTest.testConstruction():" + "resetting user name");
        us1.setID("user2");
        UnittestLog.logMessage(
                "UserSubjectTest.testConstruction():" + "us1.toString()="
                + us1.toString());

        UserSubject us3 = new UserSubject("user1");
        UnittestLog.logMessage(
                "UserSubjectTest.testConstruction(): equality test for false:"
                + us.equals(us3));
        us3.setPSubjectName("u1");
        boolean result = us.equals(us3);
        UnittestLog.logMessage(
                "UserSubjectTest.testConstruction(): equality test for true:"
                + result);
        if (!result) {
             UnittestLog.logMessage(
                "UserSubjectTest.testConstruction(): equality test for true failed");
             throw new Exception(
                     "UserSubjectTest.testConstruction(): equality test for true failed");
        }

        result = us1.equals(us3);
        UnittestLog.logMessage(
                "UserSubjectTest.testConstruction(): equality test for false:"
                + result);
        if (result) {
             UnittestLog.logMessage(
                "UserSubjectTest.testConstruction(): "
                + " equality test for false failed");
             throw new Exception(
                     "UserSubjectTest.testConstruction(): "
                     +  "equality test for false failed");
        }
    }

    public static void main(String[] args) throws Exception {
        new UserSubjectTest().testConstruction();
        UnittestLog.flush(new Date().toString());
    }
}

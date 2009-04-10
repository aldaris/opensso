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
 * $Id: GroupSubjectTest.java,v 1.2 2009-04-10 00:58:59 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;

import com.sun.identity.entitlement.GroupSubject;
import java.util.Date;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

/**
 *
 * @author dillidorai
 */
public class GroupSubjectTest {

    @Test
    public void testConstruction() throws Exception {
        GroupSubject group1 = new GroupSubject("grouper1");
        group1.setPSubjectName("g1");
        UnittestLog.logMessage(
                "GroupSubjectTest.testConstruction():" + "group.toString()=" 
                + group1.toString());

        GroupSubject group11 = new GroupSubject();
        group11.setState(group1.getState());
        UnittestLog.logMessage(
                "GroupSubject.testConstruction():" + "group11.toString()="
                + group11.toString());
         boolean result = group11.equals(group1);
         UnittestLog.logMessage(
                "GroupSubject.testConstruction():" + "equals test for true:"
                + result);
         if (!result) {
             UnittestLog.logMessage(
                "GroupSubject.testConstruction():"
                    + "equals test for true failed");
              throw new Exception("GroupSubject.testConstruction():"
                    + "equals test for true failed");
         }

        UnittestLog.logMessage(
                "GroupSubjectTest.testConstruction():"
                + "resetting grouper name");
        group1.setGroup("group1");
        UnittestLog.logMessage(
                "GroupSubjectTest.testConstruction():" + "group1.toString()="
                + group1.toString());
        result = group11.equals(group1);
        UnittestLog.logMessage(
                "GroupSubject.testConstruction():" + "equals test for false:" 
                + result);
        if (result) {
             UnittestLog.logMessage(
                "GroupSubject.testConstruction():"
                    + "equals test for false failed");
              throw new Exception("GroupSubject.testConstruction():"
                    + "equals test for false failed");
         }
    }

    public static void main(String[] args) throws Exception {
        new GroupSubjectTest().testConstruction();
        UnittestLog.flush(new Date().toString());
    }
}

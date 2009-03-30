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
 * $Id: EntitlementTest.java,v 1.2 2009-03-30 18:58:59 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;

import com.sun.identity.entitlement.Entitlement;
import java.util.Date;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;

/**
 *
 * @author dillidorai
 */
public class EntitlementTest {

    private static String SERVICE_NAME = "iPlanetAMWebAgentService";

    @Test
    public void testConstruction() throws Exception {
        Map<String, Boolean> actionValues = new HashMap<String, Boolean>();
        actionValues.put("POST", Boolean.TRUE);
        String resourceName = "http://www.sun.com";
        Entitlement ent = new Entitlement(SERVICE_NAME,
                resourceName, actionValues);
        Set<String> excludedResourceNames = new HashSet<String>();
        excludedResourceNames.add("http://www.sun.com/hr");
        excludedResourceNames.add("http://www.sun.com/legal");
        ent.setExcludedResourceNames(excludedResourceNames);
        ent.setName("entitlement1");
        UnittestLog.logMessage(
                "EntitlementTest.testConstruction():" + "ent.toString()="
                + ent.toString());
        Entitlement ent1 = new Entitlement(SERVICE_NAME,
                resourceName, actionValues);
        UnittestLog.logMessage(
                "EntitlementTest.testConstruction():equality test for false:"
                + ent.equals(ent1) );
        ent1.setExcludedResourceNames(excludedResourceNames);
        ent1.setName("entitlement1");
         UnittestLog.logMessage(
                "EntitlementTest.testConstruction():equality test for true:"
                + ent.equals(ent1) );
        

    }

    public static void main(String[] args) throws Exception {
        new EntitlementTest().testConstruction();
        UnittestLog.flush(new Date().toString());
    }
}

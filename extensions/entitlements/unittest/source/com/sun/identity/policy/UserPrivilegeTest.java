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
 * $Id: UserPrivilegeTest.java,v 1.1 2009-06-16 19:18:23 dillidorai Exp $
 */
package com.sun.identity.policy;

import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdType;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author dillidorai
 */
public class UserPrivilegeTest {

    SSOToken adminToken = null;
    AMIdentityRepository amir = null;
    AMIdentity user = null;

    @Test
    public void setup() throws Exception {
        adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        amir = new AMIdentityRepository(adminToken, "/");
        String name = "user1";
        Map attrMap = new HashMap();
        Set cnVals = new HashSet();
        cnVals.add(name);
        attrMap.put("cn", cnVals);

        Set snVals = new HashSet();
        snVals.add(name);
        attrMap.put("sn", snVals);

        Set nameVals = new HashSet();
        nameVals.add(name);
        attrMap.put("givenname", nameVals);

        Set passworVals = new HashSet();
        passworVals.add(name);
        attrMap.put("userpassword", passworVals);

        user = amir.createIdentity(IdType.USER, name, attrMap);
    }

    @AfterClass
    public void cleanup() throws Exception {
        Set identities = new HashSet();
        identities.add(user.getUniversalId());
        amir.deleteIdentities(identities);
    }

    @Test(dependsOnMethods={"setup"})
    public void testUpdateEmailAddress() throws Exception {
        Map attrMap = new HashMap();
        Set mailVals = new HashSet();
        mailVals.add("user1@sun.com");
        attrMap.put("mail", mailVals);
        user.setAttributes(attrMap);
        user.store();

    }

    @Test(dependsOnMethods={"testUpdateEmailAddress"})
    public void testReadEmailAddress() throws Exception {
        Set attrNames = new HashSet();
        attrNames.add("mail");
        Map attrMap = user.getAttributes(attrNames);
        Set mailVals = (Set)attrMap.get("mail");
        if (mailVals == null) {
            throw new Exception("mail values null");
        }
        if (!mailVals.contains("user1@sun.com")) {
            throw new Exception("mail value does not match");
        }
    }




}

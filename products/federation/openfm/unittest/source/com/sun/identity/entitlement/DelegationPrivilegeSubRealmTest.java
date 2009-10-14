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
 * $Id: DelegationPrivilegeSubRealmTest.java,v 1.1 2009-10-14 03:18:40 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.sm.OrganizationConfigManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

/**
 *
 * @author dennis
 */
public class DelegationPrivilegeSubRealmTest extends DelegationPrivilegeTest {
    private static final String REALM_NAME = "DelegationPrivilegeSubRealmTest";
    private static final String REFERRAL_NAME =
        "DelegationPrivilegeSubRealmTestReferral";

    @Override
    protected void init() {
        realm = REALM_NAME;
        testParams = new HashMap<String, String>();
        testParams.put("DELEGATE_PRIVILEGE_NAME",
            "DelegationPrivilegeSubRealmTestDelegatePrivilege");
        testParams.put("DELEGATED_RESOURCE",
            "http://www.delegationprivilegesubrealmtest.com/sub/*");
        testParams.put("DELEGATED_SUB_RESOURCE",
            "http://www.delegationprivilegesubrealmtest.com/sub/sub/*");
        testParams.put("DELEGATED_USER",
            "DelegationPrivilegeSubRealmTestDelegatedUser");
        testParams.put("NON_DELEGATED_USER",
            "DelegationPrivilegeSubRealmTestNonDelegatedUser");
    }

    @BeforeTest
    @Override
    public void setup() throws Exception {
        OrganizationConfigManager ocm = new OrganizationConfigManager(
            adminToken, "/");
        ocm.createSubOrganization(REALM_NAME, Collections.EMPTY_MAP);

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        Set<String> set = new HashSet<String>();
        map.put(ApplicationTypeManager.URL_APPLICATION_TYPE_NAME, set);
        set.add("http://www.delegationprivilegesubrealmtest.com/*");

        Set<String> realms = new HashSet<String>();
        realms.add(REALM_NAME);
        ReferralPrivilege referral =
            new ReferralPrivilege(REFERRAL_NAME, map, realms);
        ReferralPrivilegeManager mgr = new ReferralPrivilegeManager("/",
            SubjectUtils.createSubject(adminToken));
        mgr.add(referral);
        super.setup();
    }

    @AfterTest
    @Override
    public void cleanup() throws Exception {
        super.cleanup();
        ReferralPrivilegeManager mgr = new ReferralPrivilegeManager("/",
            SubjectUtils.createSubject(adminToken));
        mgr.delete(REFERRAL_NAME);
        OrganizationConfigManager ocm = new OrganizationConfigManager(
            adminToken, "/");
        ocm.deleteSubOrganization(REALM_NAME, true);
    }
}

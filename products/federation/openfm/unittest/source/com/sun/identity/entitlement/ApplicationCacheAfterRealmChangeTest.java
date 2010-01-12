/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: ApplicationCacheAfterRealmChangeTest.java,v 1.1 2010-01-12 07:27:29 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.idm.IdConstants;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.OrganizationConfigManager;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author dennis
 */
public class ApplicationCacheAfterRealmChangeTest {
    private static final String SUB_REALM =
        "/ApplicationCacheAfterRealmChangeTest";
    private SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
    private Subject adminSubject = SubjectUtils.createSubject(adminToken);
    private boolean migrated = EntitlementConfiguration.getInstance(
        adminSubject, "/").migratedToEntitlementService();

    @BeforeClass
    public void setup()
        throws Exception {

        if (!migrated) {
            return;
        }

        OrganizationConfigManager ocm = new OrganizationConfigManager(
            adminToken, "/");
        String subRealm = SUB_REALM.substring(1);
        ocm.createSubOrganization(subRealm, Collections.EMPTY_MAP);
    }

    @AfterClass
    public void cleanup() throws Exception {
        if (!migrated) {
            return;
        }
        OrganizationConfigManager ocm = new OrganizationConfigManager(
            adminToken, "/");
        String subRealm = SUB_REALM.substring(1);
        ocm.deleteSubOrganization(subRealm, true);
    }

    @Test
    public void test() throws Exception {
        if (!migrated) {
            return;
        }
        Application appl = ApplicationManager.getApplication(adminSubject,
            SUB_REALM, ApplicationTypeManager.URL_APPLICATION_TYPE_NAME);
        Set<String> resources = appl.getResources();
        if ((resources != null) && !resources.isEmpty()) {
            throw new Exception("ApplicationCacheAfterRealmChangeTest: " +
                "application resources should be empty");
        }

        OrganizationConfigManager ocm = new OrganizationConfigManager(
            adminToken, SUB_REALM);

        Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();
        Set<String> setAlias = new HashSet<String>();
        setAlias.add("www.ApplicationCacheAfterRealmChangeTest.com");
        attributes.put("sunOrganizationAliases", setAlias);
        Set<String> setStatus = new HashSet<String>();
        setStatus.add("Active");
        attributes.put("sunOrganizationStatus", setStatus);

        ocm.setAttributes(IdConstants.REPO_SERVICE, attributes);

        appl = ApplicationManager.getApplication(adminSubject,
            SUB_REALM, ApplicationTypeManager.URL_APPLICATION_TYPE_NAME);
        resources = appl.getResources();
        if ((resources == null) || resources.isEmpty()) {
            throw new Exception("ApplicationCacheAfterRealmChangeTest: " +
                "application resources should NOT be empty");
        }
    }

}

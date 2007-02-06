/* The contents of this file are subject to the terms
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
 * $Id: IdmCommon.java,v 1.1 2007-02-06 19:55:33 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class contains helper methods related to identity objects
 */
public class IdmCommon extends TestCommon{

    public IdmCommon() {
        super("IdmCommon");
    }

    public AMIdentity createIdentity(
        SSOToken ssoToken,
        String parentRealm,
        IdType idType,
        String entityName,
        Map values
    ) throws IdRepoException, SSOException {
        AMIdentityRepository repo = new AMIdentityRepository(
            ssoToken, parentRealm);
        AMIdentity amid = repo.createIdentity(idType, entityName, values);
        return amid;
    }

    public AMIdentity getIdentity(
        SSOToken ssoToken,
        String parentRealm,
        IdType idType,
        String entityName
    ) throws IdRepoException, SSOException {
        AMIdentityRepository repo = new AMIdentityRepository(
            ssoToken, parentRealm);
        return new AMIdentity(ssoToken, entityName, idType, parentRealm, null);
    }

    public void modifyIdentity(AMIdentity amid, Map values)
        throws IdRepoException, SSOException {
        amid.setAttributes(values);
        amid.store();
    }

    public void deleteIdentity(
        SSOToken ssoToken,
        String parentRealm,
        IdType idType,
        String entityName
    ) throws IdRepoException, SSOException {
        AMIdentityRepository repo = new AMIdentityRepository(
            ssoToken, parentRealm);
        repo.deleteIdentities(getAMIdentity(
            ssoToken, entityName, idType, parentRealm));
        IdSearchResults results = repo.searchIdentities(idType, entityName,
            new IdSearchControl());
        Set resultSets = results.getSearchResults();
        assert resultSets.isEmpty();
    }

    public Set<AMIdentity> getAMIdentity(
        SSOToken ssoToken,
        String name,
        IdType idType,
        String realm
    ) {
        Set<AMIdentity> set = new HashSet<AMIdentity>();
        set.add(new AMIdentity(ssoToken, name, idType, realm, null));
        return set;
    }

    public AMIdentity createDummyUser(
        SSOToken ssoToken,
        String parentRealm,
        String entityName,
        String suffix
    ) throws IdRepoException, SSOException {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        putSetIntoMap("sn", map, "sn" + suffix);
        putSetIntoMap("cn", map, "cn" + suffix);
        putSetIntoMap("userpassword", map, entityName + suffix);
        putSetIntoMap("inetuserstatus", map, "Active");
        return createIdentity(ssoToken, parentRealm, IdType.USER, entityName + suffix,
            map);
    }

    public String getParentRealm(String realm) {
        int idx = realm.lastIndexOf("/");
        if (idx == -1) {
            throw new RuntimeException("Incorrect Realm, " + realm);
        }
        return (idx == 0) ? "/" : realm.substring(0, idx);
    }

    public void createSubRealm(SSOToken ssoToken, String realm)
        throws SSOException, SMSException
    {
        if ((realm != null) && !realm.equals("/")) {
            String parentRealm = getParentRealm(realm);
            createSubRealm(ssoToken, parentRealm);
            OrganizationConfigManager orgMgr = new OrganizationConfigManager(
                ssoToken, parentRealm);
            int idx = realm.lastIndexOf("/");
            try {
                orgMgr.createSubOrganization(realm.substring(idx+1), null);
            } catch (SMSException e) {
                //ignore if the sub organization already exists.
            }
        }
    }

    public void deleteRealm(SSOToken ssoToken, String realm)
        throws SSOException
    {
        if ((realm != null) && !realm.equals("/")) {
            String parentRealm = getParentRealm(realm);
            try {
                OrganizationConfigManager orgMgr = new
                    OrganizationConfigManager(ssoToken, parentRealm);
                int idx = realm.lastIndexOf("/");
                orgMgr.deleteSubOrganization(realm.substring(idx+1), true);
            } catch (SMSException e) {
                //ignore if the sub organization already exists.
            }
            deleteRealm(ssoToken, parentRealm);
        }
    }
}

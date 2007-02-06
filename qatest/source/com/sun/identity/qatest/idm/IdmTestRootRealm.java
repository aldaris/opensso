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
 * $Id: IdmTestRootRealm.java,v 1.1 2007-02-06 19:55:34 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.idm;

import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.TestCommon;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class tests creation of different types of AMIdentity objects.
 */
public class IdmTestRootRealm extends TestCommon {

    private SSOToken ssotoken;

    public IdmTestRootRealm() {
        super("IdmTestRootRealm");
    }

    /**
    * Creates and validates AMIdentity object of type USER
    * at root realm.
    */
    @Test(groups={"client"})
    public void testUSER()
        throws Exception {
        entering("testUSER", null);
        try {
            ssotoken = getToken(adminUser, adminPassword, basedn);

            IdSearchResults idsearchresults = null;
            AMIdentity amidentity = null;
            AMIdentityRepository amidentityrepository = 
                    new AMIdentityRepository(ssotoken, basedn);;

            HashMap hashmap = new HashMap();
            HashSet hashset = new HashSet();
            hashset.add("idmTestUser");
            hashmap.put("userpassword", hashset);
            hashset = new HashSet();
            hashset.add("idmTestUser");
            hashmap.put("uid", hashset);
            amidentityrepository.createIdentity(IdType.USER, "idmTestUser", 
                    hashmap);
            idsearchresults = amidentityrepository.searchIdentities(IdType.USER,
                    "idmTestUser", new IdSearchControl());
            Iterator iterator = idsearchresults.getSearchResults().iterator();
            while (iterator.hasNext())
                amidentity = (AMIdentity)iterator.next();
            log(logLevel, "testUSER", amidentity.getName());
            assert amidentity.getName().equals("idmtestuser");
            destroyToken(ssotoken);
        } catch(Exception e) {
            log(Level.SEVERE, "testUSER", e.getMessage(), null);
            e.printStackTrace();
            destroyToken(ssotoken);
            throw e;
        }
        exiting("testUSER");
    }

    /**
    * Deletes all AMIdentity objects
    */
    @AfterClass(groups={"client"})
    public void cleanup()
        throws Exception {
        entering("cleanup", null);
        try {
            Object obj = null;
            ssotoken = getToken(adminUser, adminPassword, basedn);
            AMIdentityRepository amidentityrepository = 
                    new AMIdentityRepository(ssotoken, basedn);
            IdSearchResults idsearchresults = 
                    amidentityrepository.searchIdentities(IdType.USER, "idm*", 
                    new IdSearchControl());
            if (idsearchresults.getSearchResults().size() != 0) {
                HashSet hashset = new HashSet();
                AMIdentity amidentity;
                Iterator iterator = 
                        idsearchresults.getSearchResults().iterator();
                while (iterator.hasNext()) {
                    amidentity = (AMIdentity)iterator.next();
                    hashset.add(amidentity);
                }
                amidentityrepository.deleteIdentities(IdType.USER, hashset);
            }
            destroyToken(ssotoken);
        } catch(Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            destroyToken(ssotoken);
            throw e;
        }
        exiting("cleanup");
    }
}

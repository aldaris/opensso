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
 * $Id: AMIdentityTestBase.java,v 1.1 2006-09-06 18:25:19 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import com.sun.identity.test.common.CollectionUtils;
import com.sun.identity.test.common.FileHelper;
import com.sun.identity.test.common.TestBase;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class tests the <code>com.sun.identity.idm.AMIdentity</code> class.
 */
public class AMIdentityTestBase extends TestBase {
    /**
     * Creates realm before the test suites are executed.
     *
     * @throws SMSException if realm cannot be created.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid.
     */
    @Parameters({"parent-realm"})
    @BeforeSuite(groups = {"api"})
    public void suiteSetup(String parentRealm)
        throws SMSException, SSOException {
        if ((parentRealm != null) && !parentRealm.equals("/")) {
            OrganizationConfigManager orgMgr = getParentOrgConfigManager(
                getAdminSSOToken(), parentRealm);
            int idx = parentRealm.lastIndexOf("/");
            try {
                orgMgr.createSubOrganization(
                    parentRealm.substring(idx+1), null);
            } catch (SMSException e) {
                //ignore if the sub organization already exists.
            }
        }
    }

    /**
     * Creates realm and <code>AMIdenity</code> object before the testcases are
     * executed.
     *
     * @throws IdRepoException if <code>AMIdenity</code> object cannot be 
     *         created.
     * @throws SMSException if realm cannot be created.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid.
     */
    @Parameters({"parent-realm", "entity-type", "entity-name",
        "entity-creation-attributes"})
    @BeforeTest(groups = {"api"})
    public void setup(
        String parentRealm,
        String idType,
        String entityName,
        String createAttributes
    ) throws IdRepoException, SMSException, SSOException {
        IdType type = IdUtils.getType(idType);
        Map values = CollectionUtils.parseStringToMap(createAttributes);

        AMIdentity amid = createIdentity(parentRealm, type, entityName, values);
        assert amid.getName().equals(entityName);
        assert amid.getType().equals(type);
        assert amid.getRealm().equals(parentRealm);
        assert amid.getAttributes().equals(values);
        assert amid.isActive();
        assert amid.isExists();
    }
    
    /**
     * Assigning and deassigning services to <code>AMIdentity</code> object.
     *
     * @throws IdRepoException if cannot access to <code>AMIdentity</code>
     *         object.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name",
        "entity-modify-service1-name", "entity-modify-service1-attributes"})
    @Test(groups = {"api", "service"})
    public void assignUnassignService(
        String parentRealm,
        String idType,
        String entityName,
        String strServiceNames,
        String svcModificationAttrs
    ) throws IdRepoException, SSOException {
        AMIdentity amid = getIdentity(parentRealm,
            IdUtils.getType(idType), entityName);
        Set assignableServices = amid.getAssignableServices();
        
        if (!assignableServices.isEmpty()) {
            for (Iterator i = assignableServices.iterator(); i.hasNext(); ) {
                amid.assignService((String)i.next(), Collections.EMPTY_MAP);
            }
            Set temp = amid.getAssignableServices();
            assert temp.isEmpty();
            temp = amid.getAssignedServices();
            assert temp.equals(assignableServices);

            for (Iterator i = assignableServices.iterator(); i.hasNext(); ) {
                amid.unassignService((String)i.next());
            }
            temp = amid.getAssignableServices();
            assert temp.equals(assignableServices);
        }
    
        Set<String> serviceNames = CollectionUtils.parseStringToSet(
            strServiceNames);
        if ((serviceNames != null) && !serviceNames.isEmpty()) {
            String serviceName = serviceNames.iterator().next();
            amid.assignService(serviceName, Collections.EMPTY_MAP);
            Map<String, Set<String>> values = 
                CollectionUtils.parseStringToMap(svcModificationAttrs);
            amid.modifyService(serviceName, values);
            Map verification = amid.getServiceAttributes(serviceName);
            assert verification.equals(svcModificationAttrs);
        }
    }

    /**
     * Modifies attributes
     *
     * @throws IdRepoException if cannot access to <code>AMIdentity</code>
     *         object.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name",
        "entity-modify-attributes"})
    @Test(groups = {"api"})
    public void modifyAttributes(
        String parentRealm,
        String idType,
        String entityName,
        String modificationAttributes
    ) throws IdRepoException, SSOException {
        Map<String, Set<String>> values = CollectionUtils.parseStringToMap(
            modificationAttributes);
        if (!values.isEmpty()) {
            AMIdentity amid = getIdentity(parentRealm,
                IdUtils.getType(idType), entityName);
            modifyIdentity(amid, values);
            Map verification = amid.getAttributes(
                values.keySet());
            assert verification.equals(modificationAttributes);
        }
    }

    /**
     * Sets and gets binary attributes
     *
     * @throws IdRepoException if cannot access to <code>AMIdentity</code>
     *         object.
     * @throws SSOException if the super administrator Single Sign On is
     *         invalid.
     */
    @Parameters({"parent-realm", "entity-type", "entity-name",
        "entity-binary-attributes"})
    @Test(groups = {"api", "user-base"})
    public void setGetBinaryAttributes(
        String parentRealm,
        String idType,
        String entityName,
        String fileName
    ) throws IOException, IdRepoException, SSOException {
        byte[] content = FileHelper.getBinary(fileName);
        AMIdentity amid = getIdentity(parentRealm,
            IdUtils.getType(idType), entityName);
        Map<String, byte[][]> map = new HashMap<String, byte[][]>();
        byte[][] values = new byte[1][];
        map.put("telephonenumber", values);
        values[0] = content;
        amid.setBinaryAttributes(map);
        
        Set<String> set = new HashSet<String>();
        set.add("telephonenumber");
        Map verify = amid.getBinaryAttributes(set);
        assert verify.equals(map);
    }
        
    /**
     * Passes an null (Map) to the modify attribute API.
     *
     * @throws IdRepoException if cannot access to <code>AMIdentity</code>
     *         object.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name"})
    @Test(groups = {"api"})
    public void modifyWithNullValues(
        String parentRealm,
        String idType,
        String entityName
    ) throws IdRepoException, SSOException {
        AMIdentity amid = getIdentity(parentRealm,
            IdUtils.getType(idType), entityName);
        modifyIdentity(amid, null);
    }

    /**
     * Adds and removes members from the <code>AMIdentity</code> object.
     *
     * @throws IdRepoException if cannot access to <code>AMIdentity</code>
     *         object.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name"})
    @Test(groups = {"api", "memberships"})
    public void assignUnassignMembers(
        String parentRealm,
        String idType,
        String entityName
    ) throws IdRepoException, SSOException {
        AMIdentity amid1 = createDummyUser(parentRealm, entityName, "1");
        AMIdentity amid2 = createDummyUser(parentRealm, entityName, "2");
        AMIdentity amid3 = createDummyUser(parentRealm, entityName, "3");
        AMIdentity amid = getIdentity(parentRealm,
            IdUtils.getType(idType), entityName);
        
        amid.addMember(amid1);
        // add twice
        amid.addMember(amid1);

        assert amid.isMember(amid1);
        amid.addMember(amid2);
        assert amid.isMember(amid1);
        assert amid.isMember(amid2);
        amid.addMember(amid3);
        assert amid.isMember(amid1);
        assert amid.isMember(amid2);
        assert amid.isMember(amid3);

        Set<AMIdentity> set = new HashSet<AMIdentity>();
        set.add(amid2);
        set.add(amid3);

        amid.removeMember(amid1);
        assert !amid.isMember(amid1);

        Set members = amid.getMembers(IdType.USER);
        assert members.equals(set);

        amid.removeMembers(set);
        assert !amid.isMember(amid1);
        assert !amid.isMember(amid2);
        assert !amid.isMember(amid3);
        deleteIdentity(parentRealm, IdType.USER, entityName + "1");
        deleteIdentity(parentRealm, IdType.USER, entityName + "2");
        deleteIdentity(parentRealm, IdType.USER, entityName + "3");
    }
    

    /**
     * Creates <code>AMIdentity</code> twice.
     *
     * @throws IdRepoException if <code>AMIdentity</code> object cannot be
     *         created.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name",
        "entity-creation-attributes"})
    @Test(groups = {"api"}, expectedExceptions={IdRepoException.class})
    public void createIdentityTwice(
        String parentRealm,
        String idType,
        String entityName,
        String createAttributes
    ) throws IdRepoException, SSOException {
        IdType type = IdUtils.getType(idType);
        Map values = CollectionUtils.parseStringToMap(createAttributes);
        createIdentity(parentRealm, type, entityName, values);
    }

    /**
     * Creates <code>AMIdentity</code> twice with long name.
     *
     * @throws IdRepoException if <code>AMIdentity</code> object cannot be
     *         created.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name",
        "entity-creation-attributes"})
    @Test(groups = {"api"}, expectedExceptions={IdRepoException.class})
    public void createIdenityWithLongName(
        String parentRealm,
        String idType,
        String entityName,
        String createAttributes
    ) throws IdRepoException, SSOException {
        String name = entityName;
        for (int i = 0; i < 100; i++) {
            name += entityName;
        }
        IdType type = IdUtils.getType(idType);
        Map values = CollectionUtils.parseStringToMap(createAttributes);
        createIdentity(parentRealm, type, name, values);
    }


    /**
     * Creates <code>AMIdentity</code> twice with no name.
     *
     * @throws IdRepoException if <code>AMIdentity</code> object cannot be
     *         created.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-creation-attributes"})
    @Test(groups = {"api"}, expectedExceptions={IdRepoException.class})
    public void createIdenityWithNoName(
        String parentRealm,
        String idType,
        String createAttributes
    ) throws IdRepoException, SSOException {
        IdType type = IdUtils.getType(idType);
        Map values = CollectionUtils.parseStringToMap(createAttributes);
        createIdentity(parentRealm, type, "", values);
    }


    /**
     * Set required values of required attributes in <code>AMIdentity</code>
     * to null.
     *
     * @throws IdRepoException if <code>AMIdentity</code> object cannot be
     *         modified.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name",
        "entity-required-attributes"})
    @Test(groups = {"api", "ldap"}, expectedExceptions={IdRepoException.class})
    public void nullifyRequiredAttribute(
        String parentRealm,
        String idType,
        String entityName,
        String requiredAttributes
    ) throws IdRepoException, SSOException, SMSException {
        Set<String> setRequiredAttributes = CollectionUtils.parseStringToSet(
            requiredAttributes);
        if (!setRequiredAttributes.isEmpty()) {
            Map<String, Set<String>> emptyValues =
                CollectionUtils.getEmptyValuesMap(setRequiredAttributes);
            AMIdentity amid = getIdentity(parentRealm,
                IdUtils.getType(idType), entityName);
            amid.setAttributes(emptyValues);
            amid.store();
        }
    }

    /**
     * Removes membership of <code>AMIdentity</code> itself from it.
     *
     * @throws IdRepoException if membership removal failed.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name"})
    @Test(groups = {"api", "memberships"}, 
        expectedExceptions={IdRepoException.class}) 
    public void addItselfAsMember(
        String parentRealm,
        String idType,
        String entityName
    ) throws IdRepoException, SSOException {
        AMIdentity amid = getIdentity(parentRealm,
            IdUtils.getType(idType), entityName);
        amid.removeMember(amid);
    }
    
    
    /**
     * Tests <code>isExists</code> method.
     *
     * @throws IdRepoException if cannot access to <code>AMIdentity</code> 
     *         object.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid. 
     */
    @Parameters({"parent-realm", "entity-type", "entity-name", 
        "entity-creation-attributes"})
    @Test(groups = {"api"})
    public void verifyExistence(
        String parentRealm,
        String idType,
        String entityName,
        String createAttributes
    ) throws IdRepoException, SSOException, SMSException {
        Map values = CollectionUtils.parseStringToMap(createAttributes);
        IdType type = IdUtils.getType(idType);
        AMIdentity a = createIdentity(parentRealm, type, entityName + "exist", 
            values);
        deleteIdentity(parentRealm, type, entityName + "exist");
        assert !a.isExists();
    }

    /**
     * Removes <code>AMIdentity</code> object after suite test is
     * done.
     * 
     * @throws IdRepoException if <code>AMIdenity</code> object cannot be 
     *         deleted.
     * @throws SMSException if realm cannot be deleted.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid.
     */
    @Parameters({"parent-realm", "entity-type", "entity-name"})
    @AfterTest(groups = {"api"})
    public void tearDown(
        String parentRealm,
        String idType,
        String entityName
    ) throws IdRepoException, SSOException, SMSException {
        deleteIdentity(parentRealm, IdUtils.getType(idType), entityName);
    }
    
    /**
     * Removes realm after suite test is done.
     * 
     * @throws SMSException if realm cannot be deleted.
     * @throws SSOException if the super administrator Single Sign On is 
     *         invalid.
     */
    @Parameters({"parent-realm"})
    @AfterSuite(groups = {"api"})
    public void suiteTearDown(String parentRealm)
        throws SSOException, SMSException {
        if ((parentRealm != null) && !parentRealm.equals("/")) {
            OrganizationConfigManager orgMgr = getParentOrgConfigManager(
                getAdminSSOToken(), parentRealm);
            int idx = parentRealm.lastIndexOf("/");
            orgMgr.deleteSubOrganization(parentRealm.substring(idx+1), true);
        }
    }

    private AMIdentity createIdentity(
        String parentRealm,
        IdType idType,
        String entityName,
        Map values
    ) throws IdRepoException, SSOException {
        SSOToken ssoToken = getAdminSSOToken();
        AMIdentityRepository repo = new AMIdentityRepository(
            ssoToken, parentRealm);
        AMIdentity amid = repo.createIdentity(idType, entityName, values);
        return amid;
    }

    private AMIdentity getIdentity(
        String parentRealm,
        IdType idType,
        String entityName
    ) throws IdRepoException, SSOException {
        SSOToken ssoToken = getAdminSSOToken();
        AMIdentityRepository repo = new AMIdentityRepository(
            ssoToken, parentRealm);
        return new AMIdentity(ssoToken, entityName, idType, parentRealm, null);
    }

    private void modifyIdentity(AMIdentity amid, Map values)
        throws IdRepoException, SSOException {
        amid.setAttributes(values);
        amid.store();
    }

    private void deleteIdentity(
        String parentRealm,
        IdType idType,
        String entityName
    ) throws IdRepoException, SSOException {
        SSOToken ssoToken = getAdminSSOToken();
        AMIdentityRepository repo = new AMIdentityRepository(
            ssoToken, parentRealm);
        repo.deleteIdentities(getAMIdentity(
            ssoToken, entityName, idType, parentRealm));
        IdSearchResults results = repo.searchIdentities(idType, entityName,
            new IdSearchControl());
        Set resultSets = results.getSearchResults();
        assert resultSets.isEmpty();
    }

    private Set<AMIdentity> getAMIdentity(
        SSOToken ssoToken,
        String name,
        IdType idType,
        String realm
    ) {
        Set<AMIdentity> set = new HashSet<AMIdentity>();
        set.add(new AMIdentity(ssoToken, name, idType, realm, null));
        return set;
    }
    
    private AMIdentity createDummyUser(
        String parentRealm, 
        String entityName, 
        String suffix
    ) throws IdRepoException, SSOException {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        CollectionUtils.putSetIntoMap("sn", map, "sn" + suffix);
        CollectionUtils.putSetIntoMap("cn", map, "cn" + suffix);
        CollectionUtils.putSetIntoMap("userpassword", map, suffix);
        CollectionUtils.putSetIntoMap("inetuserstatus", map, "Active");
        return createIdentity(parentRealm, IdType.USER, entityName + suffix, 
            map);
    }
   
    private OrganizationConfigManager getParentOrgConfigManager(
        SSOToken ssoToken,
        String parentRealm
    ) throws SMSException {
        int idx = parentRealm.lastIndexOf("/");
        if (idx == -1) {
            throw new RuntimeException("Incorrect Realm, " + parentRealm);
        }
        return new OrganizationConfigManager(ssoToken,
            (idx == 0) ? "/" : parentRealm.substring(0, idx));
    }
}

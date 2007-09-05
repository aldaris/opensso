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
 * $Id: IdentitiesTest.java,v 1.2 2007-09-05 19:59:39 bt199000 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

/**
 * <code>IdentitiesTest</code> contains the methods to create, delete, search,
 * and update identities with identity types user, role, filtered role, group,
 * and agent.  They are used to execute idm test cases.
 */
package com.sun.identity.qatest.idm;

import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.IDMCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class IdentitiesTest extends IDMCommon {
    private SSOToken ssoToken;
    private String testDescription;
    private String testRealm;
    private String testIdType;
    private String testPassword;
    private String testIdName;
    private String testReturnCode;
    private String testSetupIdName;
    private String testSetupIdType;
    private String testIdAttr;
    private String testExpectedErrCode;
    private String testExpectedErrMsg;
    private String testExpectedResult;
    private String testMemberName;
    private String testMemberType;
    private String testCaseName;
    private int testCaseNumber;
    private Map testCaseMap;
    private String prefixStr;
    
    /**
     * Empty Class constructor.
     */
    public IdentitiesTest() {
        super("IdentitiesTest");
    }
    /**
     * Creates a new instance of IdentitiesTest
     * @param tcIndex test index number in propperties file
     * @param tcName test case name
     * @param testcaseMap map contains test cases parameters
     */
    public IdentitiesTest(int tcIndex, String tcName, Map tcMap)
    throws Exception {
        super("IdentitiesTest");
        testCaseName = tcName;
        testCaseNumber = tcIndex;
        testCaseMap = tcMap;
        testDescription = getParams(IDMConstants.IDM_KEY_DESCRIPTION);
        testRealm = getParams(IDMConstants.IDM_KEY_REALM_NAME);
        testIdType = getParams(IDMConstants.IDM_KEY_IDENTITY_TYPE);
        testPassword = getParams(IDMConstants.IDM_KEY_IDENTITY_PASSWORD);
        testIdName = getParams(IDMConstants.IDM_KEY_IDENTITY_NAME);
        testSetupIdName = getParams(IDMConstants.IDM_KEY_SETUP_NAME);
        testSetupIdType = getParams(IDMConstants.IDM_KEY_SETUP_TYPE);
        testIdAttr = getParams(IDMConstants.IDM_KEY_IDENTITY_ATTRIBUTE);
        testExpectedErrCode =
                getParams(IDMConstants.IDM_KEY_EXPECTED_ERROR_CODE);
        testExpectedErrCode =
                getParams(IDMConstants.IDM_KEY_EXPECTED_ERROR_MESSAGE);
        testExpectedResult = getParams(IDMConstants.IDM_KEY_EXPECTED_RESULT);
        testMemberName = getParams(IDMConstants.IDM_KEY_IDENTITY_MEMBER_NAME);
        testMemberType = getParams(IDMConstants.IDM_KEY_IDENTITY_MEMBER_TYPE);
        log(Level.FINEST, "IdentitiesTest", "Description = " + testDescription);
        log(Level.FINEST, "IdentitiesTest", "Realm = " + testRealm);
        log(Level.FINEST, "IdentitiesTest", "Type = " + testIdType);
        log(Level.FINEST, "IdentitiesTest", "User name = " + testIdName);
        log(Level.FINEST, "IdentitiesTest", "Password = " + testPassword);
        log(Level.FINEST, "IdentitiesTest", "Set up name = " + testSetupIdName);
        try {
            ssoToken = getToken(adminUser, adminPassword, basedn);
            if (!validateToken(ssoToken)) {
                log(Level.SEVERE, "IdentitiesTest", "Sso token is invalid");
                assert false;
            }
        } catch (Exception e) {
            e.getStackTrace();
            throw e;
        }
    }
    
    /*
     * This method creates an identity with default values from properties file
     * @return true if identity is created successfully
     */
    public boolean create()
    throws Exception {
        return createImpl(this.testIdName, this.testIdType);
    }
    
    /*
     * This method creates one or more identities with the same type
     * @param idName identity names.  To create multiple identity, concatenate
     *  the identity name with character ";" i.e. user1;user2;user3;user4
     * @param idType identity type - user, role, agent, filteredrole, group
     * @return true if identity is created successfully
     */
    public boolean create(String idName, String idType)
    throws Exception {
        entering("create", null);
        boolean opSuccess = false;
        idType = (idType == null)? testIdType : idType;
        Object idTypeArr[] = getAttributeList(idType,
                IDMConstants.IDM_KEY_SEPARATE_CHARACTER).toArray();
        Object idNameArr[] = getAttributeList(idName,
                IDMConstants.IDM_KEY_SEPARATE_CHARACTER).toArray();
        log(Level.FINEST, "create", idNameArr.toString());
        for (int i = 0; i < idNameArr.length; i++) {
            idType = (idTypeArr.length == 1) ? idType :
                idTypeArr[i].toString();
            if (!createImpl(idNameArr[i].toString(), idType)) {
                opSuccess = false;
                // Terminate if any of identity creation failed.
                break;
            } else
                opSuccess = true;
        }
        exiting("create");
        return opSuccess;
    }
    
    /*
     * This method creates a identity with given name and type and verifies
     * that user exists.
     * @return true if identity is created successfully
     */
    private boolean createImpl(String idName, String idType)
    throws Exception {
        entering("createImpl", null);
        boolean opSuccess = false;
        Map userAttrMap = setDefaultIdAttributes(idType, idName);
        log(Level.FINE, "createImpl", "Creating identity " + idType +
                " name " + idName + "...");
        log(Level.FINEST, "createImpl", "realm = " + testRealm
                + " type = " + getIdType(idType).getName() +
                " attributes = " + userAttrMap.toString());
        createIdentity(ssoToken, testRealm, getIdType(idType), idName,
                userAttrMap);
        opSuccess = (doesIdentityExists(idName, idType)) ? true : false;
        if (opSuccess)
            log(Level.FINE, "createImpl", idType + " " + idName +
                    " is created successfully.");
        else
            log(Level.FINE, "createImpl", "Failed to create " + idType +
                    " " + idName);
        exiting("createImpl");
        return opSuccess;
    }
    
    /*
     * This method searches for an identity with default value of name and type
     */
    public boolean search()
    throws Exception {
        return search(this.testIdName, this.testIdType);
    }
    
    /*
     * This method searches for an identity with given identity name and type
     * @param idName identity name or pattern
     * @param idType identity type
     */
    public boolean search(String idName, String idType)
    throws Exception {
        entering("search", null);
        boolean opSuccess = false;
        log(Level.FINE, "search", "Searching identity " + idType +
                " name or pattern " + idName + "...");
        Set idResult = searchIdentities(ssoToken, idName, getIdType(idType), 
                testRealm);
        List idNameList = getAttributeList(testExpectedResult,
                IDMConstants.IDM_KEY_SEPARATE_CHARACTER);
        log(Level.FINEST, "search", "Search result " + idResult.toString() +
                " expected result " + idNameList.toString());
        Iterator iIter = idResult.iterator();
        
        if (idResult.isEmpty() && idNameList.isEmpty())
            opSuccess = true;
        else {
            AMIdentity amid;
            while (iIter.hasNext()) {
                amid = (AMIdentity)iIter.next();
                if (!idNameList.contains(amid.getName())) {
                    opSuccess = false;
                    break;
                } else
                    opSuccess = true;
            }
        }
        if (opSuccess)
            log(Level.FINE, "search", idType + " " + idName +
                    " is searched and found successfully.");
        else
            log(Level.FINE, "create", "Failed to search " + idType +
                    " " + idName);
        exiting("search");
        return opSuccess;
    }
    
    /*
     * This method updates an identity with default value of identity name and
     * type in the properties file.  It also gets the updated attributes from
     * the properties file.
     * @return true if identity is updated successfully
     */
    public boolean update()
    throws Exception {
        return update(this.testIdName, this.testIdType,
                setIDAttributes(testIdAttr));
    }
    
    /*
     * This method updates an identity with identity name, type, and attributes
     * map.
     * @param idName identity name
     * @param idType identity type - user, agent, role, filtered role, group
     * @param attrMap map of identity attributes i.e. [userpassword = newone]
     * @return true if identity is updated successfully
     */
    public boolean update(String idName, String idType, Map attrMap)
    throws Exception {
        entering("update", null);
        boolean opSuccess = false;
        log(Level.FINE, "update", "Updating identity " + idType +
                " name " + idName + "...");
        log(Level.FINEST, "update", "Attributes from input file" +
                attrMap.toString());
        AMIdentity uId = getFirstAMIdentity(ssoToken, idName,
                getIdType(idType), testRealm);
        modifyIdentity(uId, attrMap);
        // Verification steps
        Set keys = attrMap.keySet();
        Iterator keyIter = keys.iterator();
        String key;
        Set expectedVal;
        Set updatedVal;
        while (keyIter.hasNext()) {
            key = (String)keyIter.next();
            expectedVal = (Set)attrMap.get(key);
            // Ignore if attribute is userpassword
            if (!key.equals("userpassword")) {
                updatedVal = uId.getAttribute(key);
                log(Level.FINEST, "update","key =" + key +
                        " updated value = " + updatedVal.toString() +
                        " expected value = " + expectedVal.toString());
                if (!updatedVal.equals(expectedVal)) {
                    opSuccess = false;
                    break;
                } else
                    opSuccess = true;
            }
        }
        if (opSuccess)
            log(Level.FINE, "update", idType + " " + idName +
                    " is updated successfully.");
        else
            log(Level.FINE, "update", "Failed to update  " + idType +
                    " " + idName);
        exiting("update");
        return (opSuccess);
    }
    
    /*
     * This method deletes an identity with default values of identity name and
     * type from the properties file
     * @return true if identity is deleted successfully
     */
    public boolean delete()
    throws Exception {
        return delete(this.testIdName, this.testIdType);
    }
    
    /*
     * This method deletes one or multiple identities with identity name and
     * type
     * @param idName identity name
     * @param idType identity type - user, agent, role, filtered role, group
     * @return true if identity is deleted successfully
     */
    public boolean delete(String idName, String idType)
    throws Exception {
        entering("delete", null);
        boolean opSuccess = false;
        // If type is null, use default value in the properties file
        idType = (idType == null)? testIdType : idType;
        List idTypeList = getAttributeList(idType,
                IDMConstants.IDM_KEY_SEPARATE_CHARACTER);
        List idNameList = getAttributeList(idName,
                IDMConstants.IDM_KEY_SEPARATE_CHARACTER);
        log(Level.FINEST, "delete", idNameList.toString());
        log(Level.FINE, "delete", "Deleting identity " + idType +
                " name " + idName + "...");
        Iterator iterName = idNameList.iterator();
        Iterator iterType = idTypeList.iterator();
        List newidTypeList = new ArrayList();
        // Generate a list of IdType identity type.  If there is less number of
        // identity type in the list compare to number of identities, fill it
        // with default type.
        while(iterName.hasNext()) {
            if (iterType.hasNext())
                newidTypeList.add(getIdType((String)iterType.next()));
            else
                newidTypeList.add(getIdType(testIdType));
            iterName.next();
        }
        deleteIdentity(ssoToken, testRealm, newidTypeList, idNameList);
        Iterator iterN = idNameList.iterator();
        Iterator iterT = newidTypeList.iterator();
        while (iterN.hasNext()) {
            if (doesIdentityExists((String)iterN.next(),
                    (IdType)iterT.next())) {
                opSuccess = false;
                break;
            } else
                opSuccess = true;
        }
        if (opSuccess)
            log(Level.FINE, "delete", idType + " " + idName +
                    " is deleted successfully.");
        else
            log(Level.FINE, "delete", "Failed to delete " + idType +
                    " " + idName);
        exiting("delete");
        return opSuccess;
    }
    
    /*
     * This method adds an user member to an identity with default values of
     * identity name, type, and member name in the properties file
     * @return true if member is added to dentity successfully
     */
    public boolean addMembers()
    throws Exception {
        return addMembers(this.testIdName, this.testIdType,
                this.testMemberName);
    }
    
    /*
     * This method addes an user member to an identity with identity name, type,
     * and member name.
     * @param idName identity name
     * @param idType identity type
     * @param memberName user member name
     * @return true if member is added to dentity successfully
     */
    public boolean addMembers(String idName, String idType, String memberName)
    throws Exception {
        entering("addMembers", null);
        boolean opSuccess = false;
        log(Level.FINE, "addMembers", "Adding a user member name " +
                memberName + " to " + idType + " " + idName + "...");
        // Add user member to role or group identity
        addUserMember(ssoToken, memberName, idName, getIdType(idType), 
                testRealm);
        // Verification step.  Check to make sure user is member of an identity
        AMIdentity memid = getFirstAMIdentity(ssoToken, memberName,
                IdType.USER, testRealm);
        AMIdentity id = getFirstAMIdentity(ssoToken, idName,
                getIdType(idType), testRealm);
        opSuccess = memid.isMember(id);
        if (opSuccess)
            log(Level.FINE, "addMembers", "User member " + memberName +
                    " is added to " + idType + " " + idName + " successfully");
        else
            log(Level.FINE, "addMembers", "Failed to add member");
        exiting("addMembers");
        return opSuccess;
    }
    
    /*
     * This method checks if an identity exists.  It uses defaults value of
     * identity name and type in the properties file.
     * @return true if identity exists
     */
    public boolean doesIdentityExists()
    throws Exception {
        return doesIdentityExists(this.testIdName, this.testIdType);
    }
    
    /*
     * This method checks if an identity exists.  It accepts identity name and
     * type from the arguments.
     * @param idName identity name
     * @param idType identity type - user, agent, role, filtered role, group
     * @return true if identity exists
     */
    public boolean doesIdentityExists(String idName, String idType)
    throws Exception {
        return doesIdentityExists(idName, getIdType(idType));
    }
    
    /*
     * This method checks if an identity exists.  It accepts identity name and
     * type from the arguments.
     * @param idName identity name
     * @param idType Idtype of identity type
     * @return true if identity exists
     */
    public boolean doesIdentityExists(String idName, IdType idType)
    throws Exception {
        entering("doesIdentityExists", null);
        boolean idFound = false;
        Set idRes = searchIdentities(ssoToken, idName, idType, testRealm);
        Iterator iter = idRes.iterator();
        AMIdentity amIdentity;
        while (iter.hasNext()) {
            amIdentity = (AMIdentity) iter.next();
            if (amIdentity.getName().equals(idName)) {
                idFound = true;
                break;
            }
            log(Level.FINEST, "searchIdentities", "Search result - name: " +
                    amIdentity.getName());
        }
        exiting("doesIdentityExists");
        return (idFound);
    }
    
    /*
     * This method return value of test case property based on the key
     * @param key test case property key
     * @return test case property value
     */
    protected String getParams(String key) {
        prefixStr = IDMConstants.IDM_TESTCASES_PREFIX + testCaseNumber + "." +
                testCaseName + ".";
        return (String)testCaseMap.get(prefixStr + key);
    }
    
    /*
     * This method return type IdType of identity type
     */
    private IdType getIdType(String gidtType)
    throws Exception {
        if (gidtType.equals("user"))
            return IdType.USER;
        else if (gidtType.equals("role"))
            return IdType.ROLE;
        else if (gidtType.equals("filteredrole"))
            return IdType.FILTEREDROLE;
        else if (gidtType.equals("agent"))
            return IdType.AGENT;
        else if (gidtType.equals("group"))
            return IdType.GROUP;
        else {
            log(Level.SEVERE, "getIdType", "Invalid id type " + gidtType);
            assert false;
            return null;
        }
    }
    
    /*
     * This method creates a map of identity attributes
     */
    private Map setIDAttributes(String idAttrList)
    throws Exception {
        log(Level.FINEST, "setIDAttributes", "Attributes string " + idAttrList);
        Map tempAttrMap = new HashMap();
        Map idAttrMap = getAttributeMap(testIdAttr,
                IDMConstants.IDM_KEY_SEPARATE_CHARACTER);
        Set keys = idAttrMap.keySet();
        Iterator keyIter = keys.iterator();
        String key;
        String value;
        Set idAttrSet;
        while (keyIter.hasNext()) {
            key = (String)keyIter.next();
            value = (String)idAttrMap.get(key);
            putSetIntoMap(key, tempAttrMap, value);
        }
        return tempAttrMap;
    }
    
    /*
     * This method create a map with default identity attributes.  This map
     * is used to create an identity
     */
    private Map setDefaultIdAttributes(String siaType, String idName)
    throws Exception {
        Map<String, Set<String>> tempMap = new HashMap<String, Set<String>>();
        if (siaType.equals("user")) {
            putSetIntoMap("sn", tempMap, idName);
            putSetIntoMap("cn", tempMap, idName);
            putSetIntoMap("userpassword", tempMap, testPassword);
            putSetIntoMap("inetuserstatus", tempMap, "Active");
        } else if (siaType.equals("agent")) {
            putSetIntoMap("userpassword", tempMap, testPassword);
            putSetIntoMap("sunIdentityServerDeviceStatus", tempMap, "Active"); 
            putSetIntoMap("inetuserstatus", tempMap, "Active");
        } else if (siaType.equals("filteredrole")) {
            putSetIntoMap("cn", tempMap, idName);
            putSetIntoMap("nsRoleFilter", tempMap,
                    "(objectclass=inetorgperson)");
        } else if (siaType.equals("role") || siaType.equals("group")) {
            log(Level.FINE, "setIdAttributes", "Type is " + siaType +
                    ". No attribute is set");
        } else {
            log(Level.SEVERE, "setIdAttributes", "Invalid identity type " +
                    siaType);
            assert false;
        }
        return tempMap;
    }
}

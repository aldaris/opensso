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
 * $Id: PolicyCommon.java,v 1.4 2007-06-26 19:00:59 arunav Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.StringBuffer;
import java.util.logging.Level;

/**
 * This class has common methods to create Identities and Policies
 */
public class PolicyCommon extends TestCommon {
    
    private Map identityMap = new HashMap();
    
    /**
     * Constructor for PolicyCommon class. Its an empty constructor.
     */
    public PolicyCommon() {
        super("PolicyCommon");
    }
    
    /**
     * Create multiple Identites from the map
     * @param Map object with Identity creation parameters.
     */
    public void createIds( Map testIdentityMap) throws Exception  {
        log(logLevel, "createIds" ,"Starting to create Identities");
        try {
            int i ;
            int j = 0 ;
            identityMap = testIdentityMap;
            Integer testCount = (Integer)identityMap.get("testcount");
            String url = protocol + ":" + "//" + host + ":" + port +
                    uri ;
            String logoutUrl = protocol + ":" + "//" + host + ":" + port
                    + uri + "/UI/Logout";
            FederationManager am = new FederationManager(url);
            WebClient webClient = new WebClient();
            consoleLogin(webClient, url, adminUser, adminPassword);
            HtmlPage userCheckPage;
            log(logLevel, "createIds", "IdentityMap" + identityMap);
            log(logLevel, "createIds", "test count" + testCount);
            
           /*
            * get the password/userid/realm keys (no need to check for avai
            * lability)
            * 1) Loop thru the attr and add to the list
            * 2) Now check for the getidentity and verfiy for the user
            *    avaiability
            * 3) Create the user using the above params
            * 4) Now check for containsKey(MemberofRole). if key present,
            *    getIdentity (role)
            * 5) If not present, create the role and add this
                 member to the role
            * 6) Repeat the same for group
            * 7) Flrole to be added
            */
            
            for (i = 0; i < testCount; i++){
                String uName = (String)identityMap.get("test" + i +
                        ".Identity." + "username");
                String psWord = (String)identityMap.get("test" + i +
                        ".Identity." + "password");
                String type = (String)identityMap.get("test" + i +
                        ".Identity.type");
                Integer attributeCount = new Integer((String)identityMap.get
                        ("test" + i + ".Identity.attributecount"));
                String rlmName = (String)identityMap.get("test" + i +
                        ".Identity." +  "realmname");
                List attrList = new ArrayList();
                attrList.add("userpassword=" + psWord);
                log(logLevel,"createIds" , "attr count" + attributeCount);
                for (j = 0; j < attributeCount; j++){
                    String attrName = (String)identityMap.get("test" + i +
                            ".Identity" + "." + "attribute" + j +".name");
                    String attrValue = (String)identityMap.get("test" + i +
                            ".Identity" + "." + "attribute" + j + ".value");
                    attrList.add(attrName + "=" + attrValue);
                }
                
                //now verify the user and add the user if not present
                userCheckPage = am.listIdentities(webClient, rlmName, uName,
                        type);
                String xmlString = userCheckPage.asXml();
                if (xmlString.contains(uName)) {
                    log(logLevel, "createIds", "User already exists:"
                            + uName);
                }else {
                    log(logLevel, "createIds", "User is not exists:"
                            + uName);
                    userCheckPage = am.createIdentity(webClient, rlmName,
                            uName, type, attrList);
                    userCheckPage.cleanUp();
                    userCheckPage = am.listIdentities(webClient, rlmName, uName,
                            type);
                    xmlString = userCheckPage.asXml();
                    if (xmlString.contains(uName)) {
                        log(logLevel, "createIds", "User " +
                                "is created successfully:" + uName);
                    }else {
                        log(logLevel, "createIds", "User " +
                                "is not created successfully:" + uName);
                        assert false;
                    }
                }
                
               /*
                * now verify if the user is memberof group and create group and
                * add the user
                */
                if (identityMap.containsKey("test" + i + ".Identity" +
                        ".memberOfGroup")) {
                    String grpName = (String)identityMap.get("test" + i +
                            ".Identity" + "." + "memberOfGroup");
                    HtmlPage groupCheckPage = am.listIdentities(webClient,
                            rlmName, grpName, "Group");
                    if (groupCheckPage.asXml().contains(grpName)) {
                        log(logLevel, "createIds", "group exists:"
                                + "add the member" + grpName);
                        am.addMember(webClient, rlmName, uName,
                                type, grpName, "GROUP");
                    }else {
                        List grpAttrList = new ArrayList();
                        log(logLevel, "createIds", "group is not found:"
                                + "creating the group" + grpName);
                        groupCheckPage = am.createIdentity(webClient, rlmName,
                                grpName, "Group", grpAttrList);
                        groupCheckPage = am.listIdentities(webClient,
                                rlmName, grpName, "Group");
                        if (groupCheckPage.asXml().contains(grpName)) {
                            log(logLevel, "createIds", "group is " +
                                    "created successfully." +
                                    "Now add the member" + grpName);
                            am.addMember(webClient, rlmName, uName,
                                    type, grpName, "GROUP");
                            log(logLevel, "createIds", uName +
                                    "member is  added successfully" + grpName);
                        }
                    }
                }
                
                /*
                 * now verify if the user is memberof role and create role and
                 * add the user
                 */
                if (identityMap.containsKey("test" + i + ".Identity." +
                        "memberOfRole")){
                    String roleName = (String)identityMap.get("test" + i +
                            ".Identity." + "memberOfRole");
                    HtmlPage roleCheckPage = am.listIdentities(webClient,
                            rlmName, roleName, "Role");
                    if (roleCheckPage.asXml().contains(roleName)) {
                        log(logLevel, "createIds", "Role already exists"
                                + roleName);
                        am.addMember(webClient, rlmName, uName, type, roleName,
                                "Role");
                        log(logLevel, "createIds", "added member to " +
                                "the role" + roleName);
                    }else {
                        List roleAttrList = new ArrayList();
                        log(logLevel,"createIds","Role does not" +
                                " exists. :Creating the role"  + roleName);
                        roleCheckPage = am.createIdentity(webClient, rlmName,
                                roleName, "Role", roleAttrList);
                        roleCheckPage = am.listIdentities(webClient,
                                rlmName, roleName, "Role");
                        if (roleCheckPage.asXml().contains(roleName)) {
                            log(logLevel, "createIds",
                                    "Role Created successfully." +
                                    "Now adding the member" + roleName);
                            am.addMember(webClient, rlmName, uName,
                                    type, roleName, "Role");
                            log(logLevel, "createIds", uName +
                                    "member is  added successfully" + roleName);
                        }
                    }
                }
            }
            consoleLogout(webClient, logoutUrl);
        } catch(Exception e) {
            log(logLevel, "createIds", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Delete multiple Identites from the map
     * @param Map object with Identity deletion parameters.
     */
    public void deleteIds(Map testIdentityMap)
    throws Exception{
        identityMap = testIdentityMap;
        log(logLevel, "deleteIds" ,"Starting deleteIds");
        try{
            int i ;
            int j = 0;
            Integer testCount = (Integer)identityMap.get("testcount");
            String url = protocol + ":" + "//" + host + ":" +
                    port + uri ;
            String logoutUrl = protocol + ":" + "//" + host + ":" + port
                    + uri + "/UI/Logout";
            FederationManager am = new FederationManager(url);
            WebClient webClient = new WebClient();
            consoleLogin(webClient, url, adminUser, adminPassword);
            HtmlPage htmlpage;
            List idList = new ArrayList();
            
           /*
            * Loop thru the map and delete the users and their associated
            * groups
            */
            for (i = 0; i < testCount; i++) {
                String uName = (String)identityMap.get("test" + i +
                        ".Identity.username");
                String type = (String)identityMap.get("test" + i
                        + ".Identity.type");
                String rlmName = (String)identityMap.get("test" + i +
                        ".Identity." + "realmname");
                idList.add(uName);
                for (Iterator itr = idList.iterator(); itr.hasNext();) {
                    log(logLevel, "deleteIds", (String) itr.next());
                }
                
                // now verify the user and delete the user if already present
                am.deleteIdentities(webClient, rlmName, idList, type);
                htmlpage = am.listIdentities(webClient,
                        rlmName, uName, "USER");
                String xmlString = htmlpage.asXml();
                if (xmlString.contains(uName)) {
                    log(logLevel, "deleteIds", "User is not deleted:" +
                            uName);
                    assert false;
                } else{
                    log(logLevel, "deleteIds",
                            "User is deleted properly:" + uName);
                }
                idList.clear();
                
                // verify the user and delete the group if already present
                if (identityMap.containsKey("test" + i + ".Identity" +
                        ".memberOfGroup")){
                    String grpName = (String)identityMap.get("test" + i +
                            ".Identity" + "." + "memberOfGroup");
                    HtmlPage groupCheckPage = am.listIdentities(webClient,
                            rlmName, grpName, "Group");
                    log(logLevel,"deleteIds" , groupCheckPage.asXml());
                    if (groupCheckPage.asXml().contains(grpName)) {
                        log(logLevel, "deleteIds", "Group Needs to " +
                                "be deleted" + ":" + grpName);
                        idList.add(grpName);
                        am.deleteIdentities(webClient,rlmName,idList,"Group");
                        groupCheckPage = am.listIdentities(webClient,
                                rlmName, grpName,"Group");
                        if (groupCheckPage.asXml().contains(grpName)) {
                            log(logLevel, "deleteIds", "Group delete:"
                                    + "is not success" + grpName);
                            assert false;
                        }else {
                            log(logLevel, "deleteIds", "Group is already" +
                                    "deleted:" + grpName);
                        }
                    }
                }
                idList.clear();
                if (identityMap.containsKey("test" + i + ".Identity" + "." +
                        "memberOfRole")){
                    String roleName = (String)identityMap.get("test" + i +
                            ".Identity" + "." + "memberOfRole");
                    HtmlPage roleCheckPage = am.listIdentities(webClient,
                            rlmName, roleName, "Role");
                    if (roleCheckPage.asXml().contains(roleName)) {
                        log(logLevel, "deleteIds", "Role need to be" +
                                " deleted:" + roleName);
                        idList.add(roleName);
                        am.deleteIdentities(webClient, rlmName, idList, "Role");
                        roleCheckPage = am.listIdentities(webClient,
                                rlmName, roleName, "Role");
                        if (roleCheckPage.asXml().contains(roleName)) {
                            log(logLevel, "deleteIds", "Role is "+
                                    "not deleted:" + roleName);
                            assert false;
                        }
                    }else{
                        log(logLevel, "deleteIds", "Role" +
                                " is already deleted:" + roleName);
                    }
                }
            }
            consoleLogout(webClient, logoutUrl);
        } catch(Exception e){
            e.getMessage();
            e.printStackTrace();
        }
    }//deleteIds
    
    /**
     * creates policy from a given policy name (with out xml extension)
     * @param string with policy name to be created .
     */
    public void createPolicy(String sceName)
    throws Exception {
        String scenarioname = sceName;
        try{
            String url = protocol + ":" + "//" + host + ":"
                    + port + uri ;
            log(logLevel, "createPolicy - URL", url);
            String logoutUrl = protocol + ":" + "//" + host + ":" + port
                    + uri + "/UI/Logout";
            FederationManager am = new FederationManager(url);
            WebClient webClient = new WebClient();
            String fileSeparator = System.getProperty("file.separator");
            consoleLogin(webClient, url, adminUser, adminPassword);
            String createPolicyXMLFile = scenarioname + ".xml";
            log(logLevel, "createPolicy", createPolicyXMLFile);
            String absFileName = getBaseDir() + fileSeparator + "xml" +
                    fileSeparator + "policy" + fileSeparator +
                    createPolicyXMLFile;
            log(logLevel, "createPolicy", absFileName);
            String policyXML = null;
            if (absFileName != null) {
                StringBuffer contents = new StringBuffer();
                BufferedReader input = new BufferedReader
                        (new FileReader(absFileName));
                String line = null;
                while ((line = input.readLine()) != null) {
                    contents.append(line + "\n");
                }
                if (input != null)
                    input.close();
                policyXML = contents.toString();
            }
            log(logLevel, "createPolicy", absFileName);
            HtmlPage policyCheckPage = am.createPolicies(webClient,
                    realm, policyXML);
            String xmlString = policyCheckPage.asXml();
            if (xmlString.contains("Policies are created")) {
                log(logLevel, "createPolicy", "create policy is" +
                        "success" + absFileName);
                log(logLevel, "createPolicy", xmlString);
            }else{
                log(logLevel, "createPolicy",
                        "not success" + absFileName);
                log(logLevel, "createPolicy", xmlString);
                assert false;
            }
            consoleLogout(webClient, logoutUrl);
        } catch(Exception e) {
            log(Level.SEVERE, "createPolicy", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * deletes policies from a given policy name and policyCount
     * @param string with policy name to be created .
     */
    public void deletePolicies(String poliName, Integer poliCount)
    throws Exception {
        String policyName = poliName;
        Integer pCount = poliCount;
        try{
            List pList = new ArrayList();
            for (int p = 0; p < pCount; p++){
                String pName = policyName + p;
                pList.add(pName);
            }
            String url = protocol + ":" + "//" + host + ":"
                    + port + uri ;
            String logoutUrl = protocol + ":" + "//" + host + ":" + port
                    + uri + "/UI/Logout";
            FederationManager am = new FederationManager(url);
            WebClient webClient = new WebClient();
            consoleLogin(webClient, url, adminUser, adminPassword);
            HtmlPage policyCheckPage  = am.deletePolicies(webClient,
                    realm, pList);
            String xmlString = policyCheckPage.asXml();
            if (!xmlString.contains("Policies are deleted")) {
                log(logLevel, "deletePolicy", "Delete Policy" +
                        "is not success" + policyName );
                log(logLevel, "deletePolicies", xmlString);
                assert false;
            }else{
                log(logLevel, "deletePolicies", "is success" +
                        policyName);
                log(logLevel, "deletePolicies", xmlString);
            }
            log(logLevel,"deletePolicies","cleaned up the policies");
            consoleLogout(webClient, logoutUrl);
        } catch(Exception e) {
            log(Level.SEVERE, "deletePolicies", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * sets the requested properties in the sso token   
     * @param SSOToken, Map, int 
     */
    public void setProperty(SSOToken userToken, Map testIdentityMap, int i)
    throws Exception {
        int j ;
        try {
            Integer spCount = new Integer((String)testIdentityMap.get
                    ("test" + i + ".Identity.spcount"));
            for (j = 0; j < spCount; j++){
                String spName = (String)testIdentityMap.get("test" + i +
                        ".Identity" + "." + "sp" + j +".name");
                String spValue = (String)testIdentityMap.get("test" + i +
                        ".Identity" + "." + "sp" + j + ".value");
                String userName =  (String)testIdentityMap.get("test" + i +
                        ".Identity"  + ".username");                
                userToken.setProperty(spName, spValue);
                String spGetValue = userToken.getProperty(spName);
                if (spGetValue == spValue) {
                    log(logLevel, "setProperty", "Session Property is set"
                            + "Correctly:" + spGetValue);
                } else {
                    log(logLevel, "setProperty", "Session Property is not"
                            + " set Correctly:" + spGetValue);
                }
            }
        } catch (SSOException ex) {
            ex.printStackTrace();
        }
    }

}


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
 * $Id: DelegationCommon.java,v 1.1 2008-02-08 08:22:44 kanduls Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.common;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.delegation.DelegationConstants;
import com.sun.identity.sm.OrganizationConfigManager;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 *This class contains helper methods to executed delegation testcases
 */
public class DelegationCommon extends IDMCommon {
    
    /**
     * famadm.jsp url
     */
    private String fmadmURL;
    
    /**
     * Federation Manager admin object
     */
    private FederationManager fmadm;
    
    /**
     * webClient object used to access html content.
     */
    private WebClient webClient;
    
    /**
     * Federation Manager login URL
     */
    protected String loginURL;
    
    /**
     * Federation Manager logout URL
     */
    protected String logoutURL;
    
    
    /** Creates a new instance of DelegationCommon */
    public DelegationCommon(String componentName) {
        super(componentName);
        loginURL = protocol + ":" + "//" + host + ":" + port + uri +
                "/UI/Login";
        logoutURL = protocol + ":" + "//" + host + ":" + port + uri +
                "/UI/Logout";
        fmadmURL = protocol + ":" + "//" + host + ":" + port + uri;
        fmadm = new FederationManager(fmadmURL);
    }
    
    /**
     * This function adds privileges to identity using famadm.jsp
     * @param ssoToken ssotoken of the identity
     * @param idName identity Name
     * @param idType identity Type
     * @param realmName Realm name in which the identity exists
     * @param privileges List of privileges to add
     * @return true if privileges added successfully
     */
    public boolean addPrivileges(SSOToken ssotoken, String idName,
            IdType idType,
            String realmName,
            List privileges)
    throws Exception {
        boolean status = false;
        try {
            webClient = new WebClient();
            consoleLogin(webClient, loginURL, adminUser, adminPassword);
            if (privileges != null) {
                HtmlPage addPrivilegesPage ;
                addPrivilegesPage = fmadm.addPrivileges(webClient, realmName,
                        idName, idType.getName(), privileges);
                if (FederationManager.getExitCode(addPrivilegesPage) != 0) {
                    log(Level.SEVERE, "addPrivileges",
                            "addPrivilages famadm command failed");
                    assert false;
                }
                if (getHtmlPageStringIndex(addPrivilegesPage,
                        "Privileges are add to identity") != -1) {
                    status = true;
                    log(Level.FINE, "addPrivileges", "Privilege " +
                            privileges.toString() + " are added successfully");
                } else if (getHtmlPageStringIndex(addPrivilegesPage,
                        "already has privilege") != -1) {
                    status = true;
                    log(Level.FINE, "addPrivileges", "Privilege " +
                            privileges.toString() + " already exists");
                } else {
                    log(Level.FINE, "addPrivileges",
                            "Failed to add privilege " + privileges.toString());
                }
            } else {
                log(Level.FINE, "addPrivileges", "No privilege is specified");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        return status;
    }
    
    /**
     * This function removes privileges to identity using famadm.jsp
     * @param ssoToken ssotoken of the identity
     * @param idName identity Name
     * @param idType identity Type
     * @param realmName Realm name in which the identity exists
     * @param privileges List of privileges to remove
     * @return true if the privileges are removed.
     */
    public boolean removePrivileges(SSOToken ssotoken, String idName,
            IdType idType,
            String realmName,
            List privileges)
    throws Exception {
        boolean status = false;
        try {
            webClient = new WebClient();
            consoleLogin(webClient, loginURL, adminUser, adminPassword);
            if (privileges != null){
                HtmlPage removePrivilegesPage ;
                removePrivilegesPage = fmadm.removePrivileges(webClient,
                        realmName, idName, idType.getName(), privileges);
                if (FederationManager.getExitCode(removePrivilegesPage) != 0) {
                    log(Level.SEVERE, "removePrivileges",
                            "removePrivilages famadm command failed");
                    assert false;
                }
                if (getHtmlPageStringIndex(removePrivilegesPage,
                        "Privileges are removed from identity") != -1) {
                    status = true;
                    log(Level.FINE, "removePrivileges", "Privilege " +
                            privileges.toString() +
                            " are removed successfully");
                } else {
                    log(Level.FINE, "removePrivileges",
                            "Failed to remove privilege " +
                            privileges.toString());
                }
            } else {
                log(Level.FINE, "removePrivileges",
                        "No privilege is specified");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        return status;
    }
    
    /**
     * This function shows privileges for identity using famadm.jsp
     * @param ssoToken ssotoken of the identity
     * @param idName identity Name
     * @param idType identity Type
     * @param realmName Realm name in which the identity exists
     * @return true if privileges shown.
     */
    public boolean showPrivileges(SSOToken ssotoken, String idName,
            IdType idType,
            String realmName)
    throws Exception {
        boolean status = false;
        try {
            webClient = new WebClient();
            consoleLogin(webClient, loginURL, adminUser, adminPassword);
            HtmlPage showPrivilegesPage ;
            showPrivilegesPage = fmadm.showPrivileges(webClient, realmName,
                    idName, idType.getName());
            if (FederationManager.getExitCode(showPrivilegesPage) != 0) {
                log(Level.SEVERE, "showPrivileges",
                        "showPrivilages famadm command failed");
                assert false;
            }
            if (getHtmlPageStringIndex(showPrivilegesPage,
                    "Privileges of identity") != -1) {
                status = true;
            } else {
                status = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        return status;
    }
    
    /**
     * This method adds privileges to identity by converting the prvileges to
     * a List.
     * @param idName identity Name
     * @param idType identity Type
     * @param privileges privileges to be added
     * @param ssoToken admin SSOToken
     * @param realmName Realm name in which identity exists.
     * @return true if privileges added successfully
     */
    public boolean addPrivilegesToId(String idName, String idType,
            String privileges,
            SSOToken ssoToken,
            String realmName)
    throws Exception {
        List privList = getAttributeList(privileges,
                DelegationConstants.IDM_KEY_SEPARATE_CHARACTER);
        return addPrivileges(ssoToken, idName, getIdType(idType),
                realmName, privList);
    }
    
    /**
     * This method removes privileges to identity
     * @param idName identity Name
     * @param idType identity Type
     * @param privileges privileges to be remvoed
     * @param SSOToken admin SSOToken
     * @param realmName realm in which identityExists.
     * @return true if privileges removed successfully
     */
    public boolean removePrivilegesFromId(String idName, String idType,
            String privileges,
            SSOToken ssoToken,
            String realmName)
    throws Exception {
        List privList = getAttributeList(privileges,
                DelegationConstants.IDM_KEY_SEPARATE_CHARACTER);
        return removePrivileges(ssoToken, idName, getIdType(idType),
                realmName, privList);
    }
    
    /**
     * This method will delete the realms recursively
     * @param adminSSOToken SSO token for
     * @return true if the deletion is success.
     */
    public boolean deleteRealmsRecursively(SSOToken adminSSOToken)
    throws Exception {
        boolean status = false;
        try {
            boolean recursive = true ;
            OrganizationConfigManager ocm = new OrganizationConfigManager(
                    adminSSOToken, realm);
            Set results = ocm.getSubOrganizationNames("*", recursive);
            log(logLevel, "deleteRealmsRecursively", "Found realms: "
                    + results);
            Object[] realms = results.toArray();
            webClient = new WebClient();
            consoleLogin(webClient, loginURL, adminUser, adminPassword);
            HtmlPage deleteRealmsPage ;
            for (int i = realms.length-1; i >= 0; i--) {
                String delRealmName = realms[i].toString();
                deleteRealmsPage =
                        fmadm.deleteRealm(webClient, delRealmName, recursive);
                if (FederationManager.getExitCode(deleteRealmsPage) != 0) {
                    log(Level.SEVERE, "deleteRealms",
                            "deleteRealms famadm command failed");
                    assert false;
                }
                log(logLevel, "deleteRealmsRecursively", "Realm: " +
                        delRealmName);
                if (getHtmlPageStringIndex(deleteRealmsPage,
                        "Realm is deleted") != -1) {
                    status = true;
                } else {
                    status = false;
                }
            }
        } catch (Exception e) {
            log(Level.SEVERE, "deleteRealmsRecursively",
                    "Error deleting realm.");
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        return status;
    }
}

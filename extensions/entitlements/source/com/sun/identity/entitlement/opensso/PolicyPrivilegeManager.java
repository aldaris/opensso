/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: PolicyPrivilegeManager.java,v 1.13 2009-05-26 21:20:06 veiming Exp $
 */
package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.EntitlementConfiguration;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.PolicyDataStore;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.policy.Policy;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.PolicyManager;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * Implementaton of <code>PrivilegeManager</code> that saves privileges
 * as <code>com.sun.identity.policy</code> objects
 */
public class PolicyPrivilegeManager extends PrivilegeManager {
    private static boolean migratedToEntitlementSvc = false;
    private String realm = "/";
    private PolicyManager pm;

    static {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            SubjectUtils.createSubject(adminToken), "/");
        migratedToEntitlementSvc = ec.migratedToEntitlementService();
    }

    /**
     * Creates instance of <code>PolicyPrivilegeManager</code>
     */
    public PolicyPrivilegeManager() {
    }

    /**
     * Initializes the object
     * @param subject subject that would be used for privilege management
     * operations
     */
    @Override
    public void initialize(String realm, Subject subject) {
        super.initialize(realm, subject);
        this.realm = realm;
        SSOToken ssoToken = SubjectUtils.getSSOToken(subject);
        if (!migratedToEntitlementSvc) {
            try {
                pm = new PolicyManager(ssoToken, realm);
            } catch (SSOException e) {
                PrivilegeManager.debug.error(
                    "PolicyPrivilegeManager.initialize", e);
            } catch (PolicyException e) {
                PrivilegeManager.debug.error(
                    "PolicyPrivilegeManager.initialize", e);
            }
        }
    }

    /**
     * Returns a privilege
     * @param privilegeName name for the privilege to be returned
     * @throws com.sun.identity.entitlement.EntitlementException if there is
     * an error
     */
    public Privilege getPrivilege(String privilegeName)
        throws EntitlementException {
        Privilege privilege = null;
        try {
            Policy policy = null;
            
            if (!migratedToEntitlementSvc) {
                policy = pm.getPolicy(privilegeName);
            } else {
                PolicyDataStore pdb = PolicyDataStore.getInstance();
                policy = (Policy)pdb.getPolicy(getAdminSubject(), getRealm(),
                    privilegeName);
                //TODO XACML
            }

            Set<Privilege> privileges =
                PrivilegeUtils.policyToPrivileges(policy);
            if ((privileges != null) && !privileges.isEmpty()) {
                privilege = privileges.iterator().next();
            }
        } catch (PolicyException pe) {
            throw new EntitlementException(102, pe);
        } catch (SSOException ssoe) {
            throw new EntitlementException(102, ssoe);
        }
        return privilege;
    }

    /**
     * Adds a privilege
     *
     * @param privilege privilege to be added
     * @throws com.sun.identity.entitlement.EntitlementException if the
     * privilege could not be added
     */
    @Override
    public void addPrivilege(Privilege privilege)
        throws EntitlementException {
        super.addPrivilege(privilege);
        String name = privilege.getName();

        try {
            Policy policy = PrivilegeUtils.privilegeToPolicy(realm, privilege);
            if (!migratedToEntitlementSvc) {
                pm.addPolicy(policy);
            } else {
                PolicyDataStore pdb = PolicyDataStore.getInstance();
                pdb.addPolicy(getAdminSubject(), getRealm(), policy);
            }
        } catch (PolicyException e) {
            Object[] params = {name};
            throw new EntitlementException(202, params, e);
        } catch (SSOException e) {
            Object[] params = {name};
            throw new EntitlementException(202, params, e);
        }
    }

    /**
     * Removes a privilege
     * @param privilegeName name of the privilege to be removed
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    @Override
    public void removePrivilege(String privilegeName)
            throws EntitlementException {
        try {
            if (!migratedToEntitlementSvc) {
                pm.removePolicy(privilegeName);
            } else {
                PolicyDataStore pdb = PolicyDataStore.getInstance();
                pdb.removePolicy(getAdminSubject(), getRealm(), privilegeName);
            }
        } catch (PolicyException e) {
            Object[] params = {privilegeName};
            throw new EntitlementException(205, params, e);
        } catch (SSOException e) {
            Object[] params = {privilegeName};
            throw new EntitlementException(205, params, e);
        }
    }

    /**
     * Modifies a privilege
     * @param privilege the privilege to be modified
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    @Override
    public void modifyPrivilege(Privilege privilege)
            throws EntitlementException {
        super.modifyPrivilege(privilege);
        String privilegeName = privilege.getName();

        try {
            if (!migratedToEntitlementSvc) {
                pm.removePolicy(privilege.getName());
                pm.addPolicy(PrivilegeUtils.privilegeToPolicy(realm, privilege));
            } else {
                PolicyDataStore pdb = PolicyDataStore.getInstance();
                pdb.modifyPolicy(getAdminSubject(), getRealm(),
                    PrivilegeUtils.privilegeToPolicy(realm, privilege));
            }
        } catch (PolicyException e) {
            Object[] params = {privilegeName};
            throw new EntitlementException(206, params, e);
        } catch (SSOException e) {
            Object[] params = {privilegeName};
            throw new EntitlementException(206, params, e);
        }
    }

    /**
     * Returns the XML representation of this privilege.
     *
     * @param name Privilege name.
     * @return XML representation of this privilege.
     * @throws EntitlementException if privilege is not found, or cannot
     * be obtained.
     */
    @Override
    public String getPrivilegeXML(String name)
        throws EntitlementException {
	/* TODO: restore the behaviour after implementing XACMLPrivilegeManager
        try {
            Policy policy = null;

            if (!migratedToEntitlementSvc) {
                policy = pm.getPolicy(name);
            } else {
                PolicyDataStore pdb = PolicyDataStore.getInstance();
                policy = (Policy)pdb.getPolicy(getRealm(), name);
            }

            return policy.toXML();
        } catch (PolicyException pe) {
            throw new EntitlementException(102, pe);
        } catch (SSOException ssoe) {
            throw new EntitlementException(102, ssoe);
        }
	*/
	Privilege privilege = getPrivilege(name);
	return com.sun.identity.entitlement.xacml3.PrivilegeUtils.toXACML(privilege);
    }
}





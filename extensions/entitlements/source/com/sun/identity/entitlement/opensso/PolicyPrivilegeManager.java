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
 * $Id: PolicyPrivilegeManager.java,v 1.2 2009-04-10 22:40:01 veiming Exp $
 */
package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.EntitlementException;
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

    private PolicyManager pm;

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
    public void initialize(Subject subject) {
        SSOToken ssoToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        try {
            pm = new PolicyManager(ssoToken);
        } catch (SSOException ssoe) {
            //TOFIX
        } catch (PolicyException pe) {
            //TOFIX
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
            Policy policy = pm.getPolicy(privilegeName);
            privilege = PrivilegeUtils.policyToPrivilege(policy);
        } catch (PolicyException pe) {
            //TOFIX
        } catch (SSOException ssoe) {
            //TOFIX
        }
        return privilege;
    }

    /**
     * Adds a privilege
     * @param privilege privilege to be added
     * @throws com.sun.identity.entitlement.EntitlementException if the
     * privilege could not be added
     */
    @Override
    public void addPrivilege(Privilege privilege)
            throws EntitlementException {
        try {
            Policy policy = PrivilegeUtils.privilegeToPolicy(privilege);
            pm.addPolicy(policy);
        } catch (PolicyException pe) {
            //TOFIX
        } catch (SSOException ssoe) {
            //TOFIX
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
            pm.removePolicy(privilegeName);
        } catch (PolicyException pe) {
            //TOFIX
        } catch (SSOException ssoe) {
            //TOFIX
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
        try {
            pm.removePolicy(privilege.getName());
            pm.addPolicy(PrivilegeUtils.privilegeToPolicy(privilege));
        } catch (PolicyException pe) {
            //TODO: record, wrap and propogate
        } catch (SSOException ssoe) {
            //TODO: record, wrap and propogate
        }
    }

    /**
     * Returns privilege names
     * @return privilege names
     * @throws com.sun.identity.entitlement.EntitlementException if there
     * is an error
     */
    public Set<String> getPrivilegeNames() throws EntitlementException {
        Set<String> names = null;
        try {
            names = pm.getPolicyNames();
        } catch (PolicyException pe) {
            //TODO: record, wrap and propogate
        } catch (SSOException ssoe) {
            //TODO: record, wrap and propogate
        }
        return names;
    }

    /**
     * Returns privilege names matching the pattern
     * @param pattern pattern to match the privilege names
     * @return privilege names matching the pattern
     * @throws com.sun.identity.entitlement.EntitlementException if there
     * is an error
     */
    public Set<String> getPrivilegeNames(String pattern) throws EntitlementException {
        Set<String> names = null;
        try {
            names = pm.getPolicyNames(pattern);
        } catch (PolicyException pe) {
            //TODO: record, wrap and propogate
        } catch (SSOException ssoe) {
            //TODO: record, wrap and propogate
        }
        return names;
    }
}





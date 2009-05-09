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
 * $Id: PolicyDataStore.java,v 1.7 2009-05-09 01:08:45 veiming Exp $
 */

package com.sun.identity.entitlement;

/**
 * This class implements method to persist policy in datastore.
 */
public abstract class PolicyDataStore {
    private static PolicyDataStore instance;

    static {
        try {
            //TOFIX
            Class clazz = Class.forName(
                "com.sun.identity.entitlement.opensso.OpenSSOPolicyDataStore");
            instance = (PolicyDataStore)clazz.newInstance();
        } catch (InstantiationException e) {
            PrivilegeManager.debug.error("PolicyDataStore.<init>", e);
        } catch (IllegalAccessException e) {
            PrivilegeManager.debug.error("PolicyDataStore.<init>", e);
        } catch (ClassNotFoundException e) {
            PrivilegeManager.debug.error("PolicyDataStore.<init>", e);
        }
    }

    public static PolicyDataStore getInstance() {
        return instance;
    }

    /**
     * Adds policy.
     *
     * @param realm Realm name.
     * @param policy policy object.
     */
    public abstract void addPolicy(String realm, Object policy)
        throws EntitlementException;

    /**
     * Modifies policy.
     *
     * @param realm Realm name.
     * @param policy policy object.
     */
    public abstract void modifyPolicy(String realm, Object policy)
        throws EntitlementException;

    /**
     * Returns policy object.
     *
     * @param realm Realm name.
     * @param name Policy name.
     */
    public abstract Object getPolicy(String realm, String name)
        throws EntitlementException;

    /**
     * Removes policy.
     *
     * @param realm Realm name.
     * @param name Policy name.
     */
    public abstract void removePolicy(String realm, String name)
        throws EntitlementException;
}

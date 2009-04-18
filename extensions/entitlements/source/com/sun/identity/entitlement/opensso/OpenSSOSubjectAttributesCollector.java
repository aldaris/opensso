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
 * $Id: OpenSSOSubjectAttributesCollector.java,v 1.3 2009-04-18 00:05:10 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.SubjectAttributesCollector;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 *
 * @author dennis
 */
public class OpenSSOSubjectAttributesCollector
    implements SubjectAttributesCollector {

    public Map<String, Set<String>> getAttributes(
        Subject subject,
        Set<String> attrNames
    ) throws EntitlementException {
        String uuid = SubjectUtils.getPrincipalId(subject);
        try {
            Map<String, Set<String>> results = new
                HashMap<String, Set<String>>();
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            AMIdentity amid = new AMIdentity(adminToken, uuid);

            Set<String> set = new HashSet<String>(2);
            set.add(uuid);
            results.put(NAMESPACE_IDENTITY, set);

            Set<String> primitiveAttrNames = getAttributeNames(attrNames,
                NAMESPACE_ATTR);
            if (!primitiveAttrNames.isEmpty()) {
                Map<String, Set<String>> primitiveAttrValues =
                    amid.getAttributes(primitiveAttrNames);
                for (String name : primitiveAttrValues.keySet()) {
                    Set<String> values = primitiveAttrValues.get(name);
                    if (values != null) {
                        results.put(NAMESPACE_ATTR + name, values);
                    }
                }
            }

            Set<String> membershipAttrNames = getAttributeNames(attrNames,
                NAMESPACE_MEMBERSHIP);
            if (!membershipAttrNames.isEmpty()) {
                for (String m : membershipAttrNames) {
                    IdType type = IdUtils.getType(m);

                    if (type != null) {
                        Set<AMIdentity> memberships = amid.getMemberships(type);

                        if (memberships != null) {
                            Set<String> setMemberships = new HashSet<String>();
                            for (AMIdentity a : memberships) {
                                setMemberships.add(a.getUniversalId());
                            }
                            results.put(NAMESPACE_MEMBERSHIP + m,
                                setMemberships);
                        }
                    }
                }
            }

            Set<Object> publicCreds = subject.getPublicCredentials();
            publicCreds.add(results);
            return results;
        } catch (SSOException e) {
            //TOFIX
        } catch (IdRepoException e) {
            //TOFIX
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_MAP;
    }

    private Set<String> getAttributeNames(Set<String> attrNames, String ns) {
        Set<String> results = new HashSet<String>();
        int len = ns.length();
        for (String s : attrNames) {
            if (s.startsWith(ns)) {
                results.add(s.substring(len));
            }
        }
        return results;
    }


    public boolean hasAttribute(
        Subject subject,
        String attrName,
        String attrValue
    ) throws EntitlementException {
        String uuid = SubjectUtils.getPrincipalId(subject);
        try {
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            AMIdentity amid = new AMIdentity(adminToken, uuid);
            if (attrName.startsWith(NAMESPACE_ATTR)) {
                Set<String> values = amid.getAttribute(attrName.substring(
                    NAMESPACE_ATTR.length()));
                return (values != null) ? values.contains(attrValue) : false;
            } else if (attrName.startsWith(NAMESPACE_MEMBERSHIP)) {
                IdType type = IdUtils.getType(attrName.substring(
                    NAMESPACE_MEMBERSHIP.length()));
                if (type != null) {
                    AMIdentity parent = new AMIdentity(adminToken,
                        attrValue); //TOFIX: realm
                    if (parent.getType().equals(type)) {
                        Set<String> members = parent.getMembers(IdType.USER);
                        return members.contains(amid.getUniversalId());
                    }
                }
            }
        } catch (IdRepoException e) {
            //TOFIX
        } catch (SSOException e) {
            //TOFIX
        }
        return false;
    }

}

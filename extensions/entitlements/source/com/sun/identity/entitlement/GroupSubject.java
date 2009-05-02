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
 * $Id: GroupSubject.java,v 1.10 2009-05-02 08:53:59 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
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
 * This class represents group identity for membership check
 */
public class GroupSubject extends EntitlementSubjectImpl {
    private static final long serialVersionUID = -403250971215465050L;

    /**
     * Constructor.
     */
    public GroupSubject() {
        super();
    }

    /**
     * Constructor.
     *
     * @param group the uuid of the group who is member of the 
     *        EntitlementSubject.
     */
    public GroupSubject(String group) {
        super(group);
    }

    /**
     * Constructs GroupSubject
     *
     * @param group the uuid of the group who is member of the
     *        EntitlementSubject
     * @param pSubjectName subject name as used in OpenSSO policy,
     *        this is releavant only when GroupSubject was created from
     *        OpenSSO policy Subject
     */
    public GroupSubject(String group, String pSubjectName) {
        super(group, pSubjectName);
    }

    /**
     * Returns <code>SubjectDecision</code> of
     * <code>EntitlementSubject</code> evaluation
     * @param subject EntitlementSubject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>SubjectDecision</code> of
     * <code>EntitlementSubject</code> evaluation
     * @throws com.sun.identity.entitlement,  EntitlementException in case
     * of any error
     */
    public SubjectDecision evaluate(
        SubjectAttributesManager mgr,
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment)
        throws EntitlementException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        boolean satified = false;

        try {
            AMIdentity idGroup = IdUtils.getIdentity(adminToken, getID());
            Set<IdType> supportedType = IdType.GROUP.canHaveMembers();
            for (IdType type : supportedType) {
                if (isMember(subject, type, idGroup)) {
                    satified = true;
                    break;
                }
            }
        } catch (IdRepoException e) {
            Evaluator.debug.error("GroupSubject.evaluate", e);
        } catch (SSOException e) {
            Evaluator.debug.error("GroupSubject.evaluate", e);
        }

        return new SubjectDecision(satified, Collections.EMPTY_MAP);
    }
    
    private boolean isMember(
        Subject subject,
        IdType type, 
        AMIdentity idGroup
    ) throws IdRepoException, SSOException {
        Set<AMIdentity> members = idGroup.getMembers(type);
        for (AMIdentity amid : members) {
            if (hasPrincipal(subject, amid.getUniversalId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns search index attributes.
     *
     * @return search index attributes.
     */
    public Map<String, Set<String>> getSearchIndexAttributes() {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>(4);
        {
            Set<String> set = new HashSet<String>();
            set.add(getID());
            map.put(SubjectAttributesCollector.NAMESPACE_MEMBERSHIP +
                IdType.GROUP.getName(), set);
        }
        {
            Set<String> set = new HashSet<String>();
            set.add(SubjectAttributesCollector.ATTR_NAME_ALL_ENTITIES);
            map.put(SubjectAttributesCollector.NAMESPACE_IDENTITY, set);
        }
        
        return map;
    }

    /**
     * Returns required attribute names.
     * 
     * @return required attribute names.
     */
    public Set<String> getRequiredAttributeNames() {
        return(Collections.EMPTY_SET);
    }
}

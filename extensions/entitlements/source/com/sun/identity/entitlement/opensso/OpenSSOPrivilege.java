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
 * $Id: OpenSSOPrivilege.java,v 1.1 2009-04-02 22:13:39 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeType;
import com.sun.identity.entitlement.ResourceAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 *
 * @author dennis
 */
public class OpenSSOPrivilege extends Privilege {

    /**
     * Constructs entitlement privilege
     * @param name name of the privilege
     * @param eSubject EntitlementSubject used for membership check
     * @param eCondition EntitlementCondition used for constraint check
     * @param eResourceAttributes Resource1Attributes used to get additional
     * result attributes
     */
    public OpenSSOPrivilege(
        String name,
        Entitlement entitlement,
        EntitlementSubject eSubject,
        EntitlementCondition eCondition,
        Set<ResourceAttributes> eResourceAttributes
    ) {
        super(name, entitlement, eSubject, eCondition, eResourceAttributes);
    }

    @Override
    public PrivilegeType getType() {
        return PrivilegeType.OPENSSO;
    }

    @Override
    public String getNativePolicy() {
        return null; //TOFIX;
    }

    @Override
    public boolean hasEntitlement(Subject subject, Entitlement e)
        throws EntitlementException {
        return false;
    }

    @Override
    public List<Entitlement> getEntitlements(
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment,
        boolean recursive
    ) throws EntitlementException {
        return Collections.EMPTY_LIST;
    }

}

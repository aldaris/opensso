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
 * $Id: IAmRealmMembershipCache.java,v 1.1 2006-09-29 00:05:17 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.realm;

import java.util.Set;

import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.common.SSOValidationResult;

/**
 * The interface for agent realm membership cache implementation
 */
public interface IAmRealmMembershipCache {
    public abstract void initialize() throws AgentException;

    public abstract Set getMembershipFromCache(String userName);

    public abstract void addMembershipCacheEntry(String userName,
            Set membership, SSOValidationResult ssoValidationResult)
            throws AgentException;
}

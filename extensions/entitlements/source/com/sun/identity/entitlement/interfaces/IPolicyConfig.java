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
 * $Id: IPolicyConfig.java,v 1.3 2009-04-09 13:15:02 veiming Exp $
 */

package com.sun.identity.entitlement.interfaces;

import com.sun.identity.entitlement.ApplicationInfo;
import com.sun.identity.entitlement.ApplicationTypeInfo;
import java.util.Set;

/**
 *
 * @author dennis
 */
public interface IPolicyConfig {
    String POLICY_THREAD_SIZE = "threadSize";
    String POLICY_CACHE_SIZE = "policyCacheSize";
    String INDEX_CACHE_SIZE = "indexCacheSize";
    String RESOURCE_COMPARATOR = "resourceComparator";

    Set<ApplicationInfo> getApplications(String realm);
    Set<ApplicationTypeInfo> getApplicationTypes();
    String getAttributeValue(String attributeName);
    Set<String> getAttributeValues(String attributeName);
    Set<String> getSubjectAttributeNames(String realm);
    void addSubjectAttributeNames(String realm, Set<String> names);
}

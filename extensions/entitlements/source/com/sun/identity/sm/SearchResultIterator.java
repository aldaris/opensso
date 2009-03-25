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
 * $Id: SearchResultIterator.java,v 1.1 2009-03-25 17:52:30 veiming Exp $
 */

package com.sun.identity.sm;

import com.sun.identity.common.CaseInsensitiveHashMap;
import com.sun.identity.entitlement.DataStoreEntry;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.ldap.LDAPAttribute;
import com.sun.identity.shared.ldap.LDAPAttributeSet;
import com.sun.identity.shared.ldap.LDAPEntry;
import com.sun.identity.shared.ldap.LDAPException;
import com.sun.identity.shared.ldap.LDAPSearchResults;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SearchResultIterator implements Iterator {
    private LDAPSearchResults results;
    private Set<String> excludeDNs;
    private boolean hasExcludeDNs;
    private DataStoreEntry current;

    public SearchResultIterator(
        LDAPSearchResults results,
        Set<String> excludeDNs
    ) {
        this.results = results;
        this.excludeDNs = excludeDNs;
        hasExcludeDNs = (excludeDNs != null) && !excludeDNs.isEmpty();
    }

    public boolean hasNext() {
        if (!results.hasMoreElements()) {
            return false;
        }
        try {
            LDAPEntry entry = results.next();
            String dn = entry.getDN();
            if (hasExcludeDNs) {
                while (excludeDNs.contains(dn)) {
                    if (results.hasMoreElements()) {
                        entry = results.next();
                        dn = entry.getDN();
                    } else {
                        entry = null;
                    }
                }
            }

            if (entry != null) {
                current = new DataStoreEntry(dn, convertLDAPAttributeSetToMap(
                    entry.getAttributeSet()));
            }
            return (current != null);
        } catch (LDAPException ldape) {
            Debug.getInstance("amSMSLdap").error("SearchResultIterator.hasNext",
                ldape);
        }
        return false;
    }

    public Object next() {
        DataStoreEntry tmp = current;
        current = null;
        return tmp;
    }

    public void remove() {
        //not supported.
    }

    public static Map convertLDAPAttributeSetToMap(LDAPAttributeSet attrSet) {
        Map answer = null;

        if (attrSet != null) {
            for (Enumeration enums = attrSet.getAttributes(); enums
                    .hasMoreElements();) {
                LDAPAttribute attr = (LDAPAttribute) enums.nextElement();
                String attrName = attr.getName();

                if (attr != null) {
                    Set values = new HashSet();
                    String[] value = attr.getStringValueArray();

                    for (int i = 0; i < value.length; i++) {
                        values.add(value[i]);
                    }
                    if (answer == null) {
                        answer = new CaseInsensitiveHashMap(10);
                    }
                    answer.put(attrName, values);
                }
            }
        }
        return (answer);
    }
}

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
 * $Id: SMSDataEntry.java,v 1.2 2009-03-28 06:45:30 veiming Exp $
 */

package com.sun.identity.sm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class SMSDataEntry {
    private String dn;
    private Map<String, Set<String>> attributeValues;

    public SMSDataEntry(String dn, Map<String, Set<String>> attributeValues) {
        this.dn = dn;
        this.attributeValues = new HashMap<String, Set<String>>();
        parseAttributeValues(attributeValues);
    }

    public String getDN() {
        return dn;
    }

    private void parseAttributeValues(Map<String, Set<String>> raw) {
        parseAttributeValues(raw.get(SMSEntry.ATTR_XML_KEYVAL));
        parseAttributeValues(raw.get(SMSEntry.ATTR_KEYVAL));
    }

    private void parseAttributeValues(Set<String> raw) {
        for (String s : raw) {
            int idx = s.indexOf('=');
            if (idx != -1) {
                String name = s.substring(0, idx);
                String value = s.substring(idx+1);

                Set<String> set = attributeValues.get(name);
                if (set == null) {
                    set = new HashSet<String>();
                    attributeValues.put(name, set);
                }
                set.add(value);
            }
        }
    }

    public Set<String> getAttributeValues(String attributeName) {
        return attributeValues.get(attributeName);
    }

    public String getAttributeValue(String attributeName) {
        Set<String> val = attributeValues.get(attributeName);
        return ((val != null) && !val.isEmpty()) ? val.iterator().next() :
            null;
    }
}

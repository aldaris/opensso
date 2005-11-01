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
 * $Id: CaseInsensitiveHashMap.java,v 1.1 2005-11-01 00:30:55 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A case insensitive hash map with case preservation. If key is a String, a
 * case insensitive hash code is used for hashing but original case of the key
 * is preserved.
 */
public class CaseInsensitiveHashMap extends HashMap {
    public CaseInsensitiveHashMap() {
        super();
    }

    public CaseInsensitiveHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CaseInsensitiveHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveHashMap(Map t) {
        putAll(t);
    }

    public boolean containsKey(Object key) {
        boolean retval;
        if (key instanceof String) {
            CaseInsensitiveKey ciKey = new CaseInsensitiveKey((String) key);
            retval = super.containsKey(ciKey);
        } else {
            retval = super.containsKey(key);
        }
        return retval;
    }

    public Object get(Object key) {
        Object retval;
        if (key instanceof String) {
            CaseInsensitiveKey ciKey = new CaseInsensitiveKey((String) key);
            retval = super.get(ciKey);
        } else {
            retval = super.get(key);
        }
        return retval;
    }

    /**
     * @return a case insensitive hash set of keys.
     */
    public Set keySet() {
        Set keys = super.keySet();
        CaseInsensitiveHashSet ciSet = new CaseInsensitiveHashSet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            // keys are already CaseInsensitiveKey's so we can just add it.
            ciSet.add(iter.next());
        }
        return ciSet;
    }

    public Object put(Object key, Object value) {
        Object retval;
        if (key instanceof String) {
            CaseInsensitiveKey ciKey = new CaseInsensitiveKey((String) key);
            retval = super.put(ciKey, value);
        } else {
            retval = super.put(key, value);
        }
        return retval;
    }

    public Object remove(Object key) {
        Object retval;
        if (key instanceof String) {
            CaseInsensitiveKey ciKey = new CaseInsensitiveKey((String) key);
            retval = super.remove(ciKey);
        } else {
            retval = super.remove(key);
        }
        return retval;
    }

    /*
     * public static void main(String[] args) { CaseInsensitiveHashMap hm = new
     * CaseInsensitiveHashMap(); hm.put("One", "une"); hm.put("tWo", "deux");
     * System.out.println(hm.get("ONE")); System.out.println(hm.get("TWO"));
     * java.util.HashMap m = new java.util.HashMap(); m.put("oNe", "uno");
     * m.put("Two", "dos"); CaseInsensitiveHashMap cm = new
     * CaseInsensitiveHashMap(m); System.out.println(cm.get("ONE"));
     * System.out.println(cm.get("TWO")); }
     */

}

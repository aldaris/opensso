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
 * $Id: StringListMap.java,v 1.2 2009-10-14 16:58:32 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A map for which keys are strings, and values are lists of strings.
 *
 * @author Paul C. Bryan
 * @credit Paul Sandoz (influenced by the com.sun.jersey.core.util.MultiValuedMapImpl class)
 */
public class StringListMap extends LinkedHashMap<String, List<String>>
{
    /**
     * Adds the specified string value to the list for the specified key. If no
     * list for the key yet exists in the map, a new list is created and added.
     *
     * @param key the key for which the specified value is to be added
     * @param value the string value to be added for the specified key.
     */
    public void add(String key, String value) {
        List<String> list = get(key);
        if (list == null) {
            list = new LinkedList<String>();
            put(key, list);
        }

        list.add(value);
    }

    /**
     * Adds the specified string values to the list for the specified key. If
     * no list for the key yet exists in the map, a new list is created and added.
     *
     * @param key the key for which the specified values are to be added
     * @param values the string values to be added for the specified key.
     */
    public void add(String key, List<String> values) {
        List<String> list = get(key);
        if (list == null || list.size() == 0) {
            put(key, values);
        }
        else {
            list.addAll(values);
        }
    }

    /**
     * TODO: Description.
     *
     * @param key TODO.
     * @return TODO.
     */
    public String first(String key) {
        List<String> list = get(key);
        if (list == null || list.size() == 0) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    /**
     * TODO: Description.
     *
     * @param key TODO.
     * @param value TODO.
     */
    public void put(String key, String value) {
        remove(key);
        add(key, value);
    }

    /**
     * TODO: Description.
     *
     * @param key TODO.
     * @return TODO.
     */
    @Override
    public List<String> remove(Object key)
    {
        if (!(key instanceof Set)) {
            return super.remove(key);
        }

// FIXME: can make the generic more type-safe?
        List<String> values = new LinkedList<String>();
        for (String k : ((Set<String>)key)) {
            List<String> v = remove(k);
            if (v != null) {
                values.addAll(v);
            }
        }            

        return values;
    }
}


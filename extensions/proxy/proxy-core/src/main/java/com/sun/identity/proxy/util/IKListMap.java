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
 * $Id: IKListMap.java,v 1.1 2009-10-06 01:05:20 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A sub-class of ListMap for which keys are case-insensitive.
 *
 * @author Paul C. Bryan
 * @credit Juergen Hoeller (influenced by the org.springframework.util.LinkedCaseInsensitiveMap class)
 * @credit Paul Sandoz (influenced by the com.sun.jersey.core.util.MultiValuedMapImpl class)
 */
public class IKListMap extends ListMap
{
    /** TODO: Description. */
    private final HashMap<String, String> keyMap = new HashMap<String, String>();

    /**
     * TODO: Description.
     */
    @Override
    public void clear() {
        keyMap.clear();
        super.clear();
    }

    /**
     * TODO: Description.
     *
     * @param key TODO.
     * @return TODO.
     */
    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String && keyMap.containsKey(((String)key).toLowerCase()));
    }

    /**
     * TODO: Description.
     *
     * @param key TODO.
     * @param value TODO.
     * @return TODO.
     */
    @Override
    public List<String> put(String key, List<String> value)
    {
        String cased = keyMap.get(key.toLowerCase());

        if (cased == null) {
            keyMap.put(key.toLowerCase(), key);
            cased = key;
        }

        return super.put(cased, value);
    }

    /**
     * TODO: Description.
     *
     * @param key TODO.
     * @return TODO.
     */
    @Override
    public List<String> get(Object key) {
        List<String> value = null;
        if (key instanceof String) {
            value = super.get(keyMap.get(((String)key).toLowerCase()));
        }
        return value;
    }

    /**
     * TODO: Description.
     *
     * @param key TODO.
     * @return TODO.
     */
    @Override
    public List<String> remove(Object key) {

        List<String> values = null;

        if (key instanceof String) {
            values = super.remove(keyMap.remove(((String)key).toLowerCase()));
        }

// FIXME: can make the generic more type-safe?
        else if (key instanceof Set) {
            values = new LinkedList<String>();
            for (String k : ((Set<String>)key)) {
                List<String> v = remove(k);
                if (v != null) {
                    values.addAll(v);
                }
            }            
        }

        return values;
    }
}


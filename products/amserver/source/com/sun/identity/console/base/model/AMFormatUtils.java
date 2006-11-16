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
 * $Id: AMFormatUtils.java,v 1.1 2006-11-16 04:31:08 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * <code>AMFormatUtils</code> provides a set of formating methods
 */
public class AMFormatUtils
    implements AMAdminConstants
{
    /**
     * Sorts a set of attributes by the <code>i18n</code> key for each entry.
     * The attributes passed in are of type <code>AMAttributeSchema</code>.
     *
     * @param unordered set of attributes.
     * @param userLocale locale of current user.
     * @return list of attribute schemas ordered by their <code>i18n</code>
     *         keys.
     */
    public static List sortAttributes(Set unordered, Locale userLocale) {
        Collator collator = Collator.getInstance(userLocale);
        AttributeI18NKeyComparator c = new AttributeI18NKeyComparator(
            collator);
        List ordered = new ArrayList(unordered);
        Collections.sort(ordered, c);
        return ordered;
    }

    /**
     * Sorts items in a set.
     *
     * @param set to sort.
     * @param locale of user.
     * @return list of sorted items.
     */
    public static List sortItems(Collection collection, Locale locale) {
        List sorted = Collections.EMPTY_LIST;
        if ((collection != null) && !collection.isEmpty()) {
            sorted = new ArrayList(collection);
            Collator collator = Collator.getInstance(locale);
            Collections.sort(sorted, collator);
        }
        return sorted;
    }

    /**
     * Reverses key to value String-String map i.e. key of map becomes
     * the value; and value becomes key for each entry.
     *
     * @param map Map to reverse.
     * @return reversed map.
     */
    public static Map reverseStringMap(Map map) {
        Map mapReverse = Collections.EMPTY_MAP;
        if ((map != null) && !map.isEmpty()) {
            mapReverse = new HashMap(map.size() *2);
            for (Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
                String key = (String)iter.next();
                mapReverse.put(map.get(key), key);
            }
        }
        return mapReverse;
    }

    /**
     * Sorts keys in a map ordered by its value (String).
     *
     * @param map to sort
     * @param locale of user
     * @return a list of sorted keys
     */
    public static List sortMapByValue(Map map, Locale locale) {
        List listSorted = Collections.EMPTY_LIST;

        if ((map != null) && !map.isEmpty()) {
            Map mapReverse = reverseStringMap(map);
            List sortedKey = sortItems(mapReverse.keySet(), locale);
            listSorted = new ArrayList(sortedKey.size());
            Iterator iter = sortedKey.iterator();

            while (iter.hasNext()) {
                String key = (String) iter.next();
                listSorted.addAll((Set) mapReverse.get(key));
            }
        }

        return listSorted;
    }

    /**
     * Replaces a string with another string in a String object.
     *
     * @param originalString original String.
     * @param token string to be replaced.
     * @param newString new string to replace token.
     * @return a String object after replacement.
     */
    public static String replaceString(
        String originalString,
        String token,
        String newString
    ) {
        int lenToken = token.length();
        int idx = originalString.indexOf(token);

        while (idx != -1) {
            originalString = originalString.substring(0, idx) +
                newString + originalString.substring(idx +lenToken);
            idx = originalString.indexOf(token, idx + lenToken);
        }

        return originalString;
    }
}

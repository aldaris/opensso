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
 * $Id: COTUtils.java,v 1.1 2006-10-30 23:13:59 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cot;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.sun.identity.shared.debug.Debug;
import java.util.logging.Level;

/**
 * This class contains circle of trust utilities.
 */
public class COTUtils {
    
    public static final String RESOURCE_BUNDLE_NAME = "libCOT";
    public static final String IDFF = COTConstants.IDFF;
    public static final String SAML2 = COTConstants.SAML2;
    public static Debug debug = Debug.getInstance("fmCOT");
    public static final String COT_TYPE_ATTR = "sun-fm-cot-type";
    
    /**
     * Default Constructor.
     */
    public COTUtils() {
    }
    
    /**
     * Get the first value of set by given key searching in the given map.
     * return null if <code>attrMap</code> is null or <code>key</code>
     * is null.
     *
     * @param attrMap Map of attributes name and their values 
     *                in the circle of trust service. The key
     *                is the attribute name and the value is
     *                a Set.
     * @param key the attribute name to be retrieved.
     * @return the first value of the attribute in the value set.
     */
    public static String getFirstEntry(Map attrMap, String key) {
        String retValue = null;
        
        if ((attrMap != null) && !attrMap.isEmpty()) {
            Set valueSet = (Set)attrMap.get(key);
            
            if ((valueSet != null) && !valueSet.isEmpty()) {
                retValue = (String)valueSet.iterator().next();
            }
        }
        
        return retValue;
    }
    
    /**
     * Adds a set of a given value to a map. Set will not be added if
     * <code>attrMap</code> is null or <code>value</code> is null or
     * <code>key</code> is null.
     *
     * @param attrMap Map of which set is to be added.
     * @param key Key of the entry to be added.
     * @param value Value to be added to the Set.
     */
    public static void fillEntriesInSet(Map attrMap, String key, String value) {
        if ((key != null) && (value != null) && (attrMap != null)) {
            Set valueSet = new HashSet();
            valueSet.add(value);
            attrMap.put(key, valueSet);
        }
    }
    
    /**
     * Checks if the circle of trust is valid. The valid values
     * are idff or saml2.
     *
     * @param cotType the circle of trust type.
     * @return true if value is idff or saml2.
     */
    public static boolean isValidCOTType(String cotType) {       
        boolean isValid =((cotType != null &&  cotType.trim().length() > 0)
        && (cotType.equalsIgnoreCase(COTConstants.IDFF)
        || (cotType.equalsIgnoreCase(COTConstants.SAML2))));
        if (!isValid) {
            String[] data = { cotType };
            LogUtil.error(Level.INFO,LogUtil.INVALID_COT_TYPE,data);
        }
        
        return isValid;
    }
}

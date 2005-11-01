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
 * $Id: AMNamingAttrManager.java,v 1.1 2005-11-01 00:29:09 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import java.util.HashMap;
import java.util.Map;

import netscape.ldap.util.DN;

import com.iplanet.am.util.Debug;

/**
 * A class to manage the naming attribute related information. This class stores
 * the naming attribute information in the in its cache.
 */
public class AMNamingAttrManager {

    // Debug object
    static Debug debug = AMCommonUtils.debug;

    static Map namingAttrMap = new HashMap();

    public static String getNamingAttr(int objectType) {
        return getNamingAttr(objectType, null);
    }

    /**
     * Gets the naming attribute after reading it from the corresponding
     * creation template. If not found, a default value will be used
     */
    public static String getNamingAttr(int objectType, String orgDN) {
        String cacheKey = (new Integer(objectType)).toString() + ":"
                + (new DN(orgDN)).toRFCString().toLowerCase();
        if (namingAttrMap.containsKey(cacheKey)) {
            return ((String) namingAttrMap.get(cacheKey));
        } else {
            String nAttr = AMDirectoryWrapper.getInstance().dMgr.getNamingAttr(
                    objectType, orgDN);
            if (nAttr != null) {
                namingAttrMap.put(cacheKey, nAttr);
            }
            return nAttr;
        }
    }

}

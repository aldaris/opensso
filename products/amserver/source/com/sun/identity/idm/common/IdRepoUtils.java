/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems, Inc. All Rights Reserved.
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
 * $Id: IdRepoUtils.java,v 1.1 2009-04-23 23:05:55 hengming Exp $
 */

package com.sun.identity.idm.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.iplanet.am.sdk.AMHashMap;


/**
 * This class provides common utility functions for IdRepo.
 */
public class IdRepoUtils {

    private static Set defaultPwdAttrs = null;
    static {
        defaultPwdAttrs = new HashSet();
        defaultPwdAttrs.add("userpassword");
        defaultPwdAttrs.add("unicodepwd");
    }

    /**
     * Returns an attribute map with all the password attributes being masked.
     * 
     * @param attrMap an attribute map
     * @param pwdAttrs a set of password attribute names
     *
     * @return an attribute map with all the password attributes being masked.
     */
    public static Map getAttrMapWithoutPasswordAttrs(Map attrMap,
        Set pwdAttrs) {

        if ((attrMap == null) || (attrMap.isEmpty())) {
            return attrMap;
        }

        Set allPwdAttrs = null;
        if ((pwdAttrs == null) || (pwdAttrs.isEmpty())) {
            allPwdAttrs = defaultPwdAttrs;
        } else {
            allPwdAttrs = new HashSet();
            allPwdAttrs.addAll(defaultPwdAttrs);
            allPwdAttrs.addAll(pwdAttrs);
        }

        AMHashMap returnAttrMap = null;
        for(Iterator iter = allPwdAttrs.iterator(); iter.hasNext(); ) {
            String pwdAttr = (String)iter.next();
            if (attrMap.containsKey(pwdAttr)) {
                if (returnAttrMap == null) {
                    returnAttrMap = new AMHashMap();
                    returnAttrMap.copy(attrMap);
                }
                returnAttrMap.put(pwdAttr, "xxx...");
            }
        }

        return (returnAttrMap == null ? attrMap : returnAttrMap);
    }
}

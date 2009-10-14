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
 * $Id: StringUtil.java,v 1.1 2009-10-14 08:57:06 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class StringUtil
{
    /**
     * TODO: Description.
     *
     * @param delim TODO.
     * @param elements TODO.
     * @return TODO.
     */
    public static String join(String delim, Iterable<?> elements) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<?> i = elements.iterator(); i.hasNext();) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    /**
     * TODO: Description.
     *
     * @param delim TODO.
     * @param elements TODO.
     * @return TODO.
     */
    public static String join(String delim, Object... elements) {
        return join(delim, Arrays.asList(elements));
    }
}


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
 * $Id: EntityUtils.java,v 1.3 2006-08-25 21:20:44 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.entity;

import com.iplanet.am.sdk.AMException;
import com.sun.identity.shared.debug.Debug;

public class EntityUtils {

    public static Debug debug = Debug.getInstance("amRemoteEntity");

    /**
     * Constructor
     */
    private EntityUtils() {
    }

    protected static EntityException convertException(AMException amex) {
        return new EntityException(amex.getMessage(), amex.getErrorCode());
    }
}

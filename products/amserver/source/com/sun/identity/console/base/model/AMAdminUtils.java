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
 * $Id: AMAdminUtils.java,v 1.2 2006-11-30 00:44:43 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base.model;

import com.iplanet.am.util.AdminUtils;
import com.iplanet.services.util.Crypt;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.authentication.internal.AuthPrincipal;
import com.sun.identity.security.ISSecurityPermission;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.sm.AttributeSchema;
import com.sun.identity.sm.SchemaType;
import com.sun.identity.sm.ServiceSchemaManager;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.SMSException;
import java.util.Collections;
import java.util.Set;

/**
 * This provides a set helper methods to access
 * Access and Service Management SDKs.
 */
public class AMAdminUtils {
    private static Debug debug = Debug.getInstance(
        AMAdminConstants.CONSOLE_DEBUG_FILENAME);

    /**
     * Returns the first <code>String</code> element from the given set.
     * If the set is empty, or null, an empty string will be returned.
     *
     * @param set where element resides
     * @return first String element from the set.
     */
    public static String getFirstElement(Set set) {
        return ((set != null) && !set.isEmpty())
            ? (String)set.iterator().next(): "";
    }

    /**
     * Returns the default value of an attribute from the service schema.
     *
     * @param svcSchemaMgr Service schema manager.
     * @param type Schema type.
     * @param attribute Attribute name.
     * @return values of service schema attribute.
     * @throws SMSException if operations fails.
     */
    public static Set getAttribute(
        ServiceSchemaManager svcSchemaMgr,
        SchemaType type,
        String attribute)
        throws SMSException
    {
        Set value = null;
        ServiceSchema schema = svcSchemaMgr.getSchema(type);
        if (schema != null) {
            AttributeSchema as = schema.getAttributeSchema(attribute);
            if (as != null) {
                value = as.getDefaultValues();
            }
        }
        return (value == null) ? Collections.EMPTY_SET : value;
    }
}

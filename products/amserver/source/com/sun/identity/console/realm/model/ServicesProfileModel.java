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
 * $Id: ServicesProfileModel.java,v 1.1 2007-02-07 20:25:57 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.realm.model;

import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMServiceProfileModel;
import java.util.Map;

/* - NEED NOT LOG - */

public interface ServicesProfileModel
    extends AMServiceProfileModel
{
    /**
     * Assigns service to a realm.
     *
     * @param map Map of attribute name to Set of attribute values.
     * @throws AMConsoleException if values cannot be set.
     */
    void assignService(Map map)
        throws AMConsoleException;

    /**
     * Set attributes of an service.
     *
     * @param attrValues Map of attribute name to its values.
     * @throws AMConsoleException if values cannot be set.
     */
    void setAttributes(Map attrValues)
        throws AMConsoleException;

    /**
     * Returns defaults attribute values.
     *
     * @return defaults attribute values.
     */
    Map getDefaultAttributeValues();

    /**
     * Returns true if a service has displayable organizational attributes.
     *
     * @return true if a service has displayable organizational attributes.
     */
    boolean hasOrganizationAttributes();
}

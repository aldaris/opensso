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
 * $Id: AssignableDynamicGroupGetNumOfNestedGroupsReq.java,v 1.1 2007-06-06 05:55:44 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.admin.cli;

import com.iplanet.am.sdk.AMAssignableDynamicGroup;
import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMStoreConnection;
import com.iplanet.sso.SSOException;
import java.util.Set;

class AssignableDynamicGroupGetNumOfNestedGroupsReq
    extends GroupGetNumOfNestedGroupsReq
{
    AssignableDynamicGroupGetNumOfNestedGroupsReq(String targetDN) {
        super(targetDN);
    }

    protected long getNumberOfNestedGroups(AMStoreConnection dpConnection)
        throws AdminException
    {
        try {
            AMAssignableDynamicGroup grp =
                dpConnection.getAssignableDynamicGroup(targetDN);
            Set nestedGroups = grp.getNestedGroupDNs();
            return nestedGroups.size();
        } catch (AMException ame) {
            throw new AdminException(ame.toString());
        } catch (SSOException ssoe) {
            throw new AdminException(ssoe.toString());
        }
    }
}

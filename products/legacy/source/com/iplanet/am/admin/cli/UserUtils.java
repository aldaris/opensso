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
 * $Id: UserUtils.java,v 1.1 2007-06-06 05:56:07 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.admin.cli;

import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMUser;
import com.iplanet.am.sdk.AMStoreConnection;
import com.iplanet.sso.SSOException;
import com.iplanet.am.util.PrintUtils;

/**
 * The <code>AdminUtils</code> class provides common user helper methods.
 */
class UserUtils {
    /**
     * Prints user information on line.
     *
     * @param prnUtl Print writer.
     * @param userDN Set of group distinguished names.
     * @param connection Store connection object.
     * @param isDNsOnly true to print DNs information only.
     */
    static void printUserInformation(PrintUtils prnUtl, String userDN,
        AMStoreConnection connection, boolean isDNsOnly)
        throws AdminException
    {
        try {
            AdminReq.writer.println("  " + userDN);

            if (!isDNsOnly) {
                AMUser user = connection.getUser(userDN);
                prnUtl.printAVPairs(user.getAttributes(), 2);
            }
        } catch (AMException ame) {
            throw new AdminException(ame.toString());
        } catch (SSOException ssoe) {
            throw new AdminException(ssoe.toString());
        }
    }
}

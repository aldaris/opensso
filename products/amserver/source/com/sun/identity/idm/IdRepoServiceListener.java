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
 * $Id: IdRepoServiceListener.java,v 1.1 2005-11-01 00:31:10 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm;

import com.iplanet.am.sdk.AMDirectoryManager;
import com.iplanet.am.sdk.AMDirectoryWrapper;
import com.iplanet.am.util.Debug;
import com.sun.identity.sm.ServiceListener;

public class IdRepoServiceListener implements ServiceListener {

    Debug debug = AMIdentityRepository.debug;

    public IdRepoServiceListener() {
        // do nothing
    }

    /**
     * Notification for global config changes to IdRepoService
     */
    public void globalConfigChanged(String serviceName, String version,
            String groupName, String serviceComponent, int type) {
        AMDirectoryManager amdm = AMDirectoryWrapper.getInstance();
        if (debug.messageEnabled()) {
            debug.message("IdRepoServiceListener: Global Config " +
                    "changed called");
        }
        amdm.cleanupIdRepoPlugins();

        // Clear IdUtils.getOrganization(...) cache
        IdUtils.clearOrganizationNamesCache();
    }

    /**
     * Notification for organization config changes to IdRepoService
     */
    public void organizationConfigChanged(String serviceName, String version,
            String orgName, String groupName, String serviceComponent, int type)
    {
        AMDirectoryManager amdm = AMDirectoryWrapper.getInstance();
        if (debug.messageEnabled()) {
            debug.message("IdRepoServiceListener: Org Config changed called");
        }
        amdm.cleanupIdRepoPlugins();

        // Clear IdUtils.getOrganization(...) cache
        IdUtils.clearOrganizationNamesCache();
    }

    /**
     * Notification for schema changes to IdRepoService
     */
    public void schemaChanged(String serviceName, String version) {
        AMDirectoryManager amdm = AMDirectoryWrapper.getInstance();
        if (debug.messageEnabled()) {
            debug.message("IdRepoServiceListener: Schema changed called");
        }
        amdm.cleanupIdRepoPlugins();

        // Clean up cached schema plugin names
        AMDirectoryManager.idRepoServiceSchemaChanged();
    }
}

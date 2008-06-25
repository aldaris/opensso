/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: IdRepoServiceListener.java,v 1.7 2008-06-25 05:43:29 qcheng Exp $
 *
 */

package com.sun.identity.idm;

import com.sun.identity.shared.debug.Debug;
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
        IdServices idServices = IdServicesFactory.getDataStoreServices();
        if (debug.messageEnabled()) {
            debug.message("IdRepoServiceListener: Global Config changed"
                    + " called");
        }
        if (serviceComponent.equals("") || serviceComponent.equals("/")) {
            return;
        }
        // FIXME: Clients don't have to call this !!
        if (!serviceComponent.startsWith("/users/") && 
            !serviceComponent.startsWith("/roles/")
        ) {
            if (type != 1) {
                idServices.clearIdRepoPlugins();
            }
        }

        // Clear IdUtils.getOrganization(...) cache
        IdUtils.clearOrganizationNamesCache();
    }

    /**
     * Notification for organization config changes to IdRepoService
     */
    public void organizationConfigChanged(String serviceName, String version,
            String orgName, String groupName, String serviceComponent, int type)
    {
        IdServices idServices = IdServicesFactory.getDataStoreServices();
        if (debug.messageEnabled()) {
            debug.message("IdRepoServiceListener: Org Config changed called");
        }
        // ignore componenet is "". 
        // if component is /" and type is 2(delete), need to remove hidden
        // plugins.
        if ((type == ServiceListener.REMOVED) &&
                serviceComponent.equals("/")) {
            idServices.clearIdRepoPlugins(orgName, serviceComponent, type);
        } else if (!serviceComponent.equals("") && 
                   !serviceComponent.equals("/")) {
            idServices.clearIdRepoPlugins(orgName, serviceComponent, type);
        }

        // Clear IdUtils.getOrganization(...) cache
        IdUtils.clearOrganizationNamesCache();
    }

    /**
     * Notification for schema changes to IdRepoService
     */
    public void schemaChanged(String serviceName, String version) {
        IdServices idServices = IdServicesFactory.getDataStoreServices();
        if (debug.messageEnabled()) {
            debug.message("IdRepoServiceListener: Schema changed called");
        }
        idServices.clearIdRepoPlugins();

        // Clean up cached schema plugin names
        idServices.reloadIdRepoServiceSchema();
    }
}

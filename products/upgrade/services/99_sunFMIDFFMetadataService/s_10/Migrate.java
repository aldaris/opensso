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
 * $Id: Migrate.java,v 1.1 2008-01-24 00:39:36 bina Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

import com.sun.identity.upgrade.MigrateTasks;
import com.sun.identity.upgrade.UpgradeException;
import com.sun.identity.upgrade.UpgradeUtils;

/**
 * Creates new service schema for <code>sunFMIDFFMetadataService</code>.
 * This service replaces the iPlanetAMProviderConfigService. 
 * Migration of data from old service to this service is required.
 * This class is invoked during migration from older versions
 * of Access Manager to the latest version.
 */

/**
 * Creates new service schema for <code>sunFMIDFFMetadataService</code> service.
 * This class is invoked during migration from older versions
 * of Access Manager to the latest version.
 */
public class Migrate implements MigrateTasks {

    final String SCHEMA_FILE = "fmIDFF.xml";
    
    /**
     * Creates service schema for <code>sunFAMSTSService</code> service.
     *
     * @return true if service creation is successful otherwise false.
     */
    public boolean migrateService() {
        boolean isSuccess = true;
        try {
            String fileName = UpgradeUtils.getNewServiceNamePath(SCHEMA_FILE);
            UpgradeUtils.createService(fileName);
            isSuccess = true;
        } catch (UpgradeException e) {
            UpgradeUtils.debug.error("Error creating service schema", e);
        }
        return isSuccess;
    }

    /**
     * Post Migration operations.
     * TODO: load data from ProviderConfig to this service
     * @return true if successful else error.
     */
    public boolean postMigrateTask() {
        return true;
    }

    /**
     * Pre Migration operations.
     * TODO: read data from iPlanetAMProviderConfigService 
     *
     * @return true if successful else error.
     */
    public boolean preMigrateTask() {
        return true;
    }
}

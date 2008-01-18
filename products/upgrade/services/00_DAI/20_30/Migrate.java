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
 * $Id: Migrate.java,v 1.1 2008-01-18 08:10:19 bina Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

import com.sun.identity.upgrade.MigrateTasks;
import com.sun.identity.upgrade.UpgradeException;
import com.sun.identity.upgrade.UpgradeUtils;

/**
 * Migration for the DAI Service.
 * This class is invoked during migration from older versions
 * of Access Manager to the latest version.
 */
public class Migrate implements MigrateTasks {

    static final String SERVICE_NAME = "DAI";
    static final String SERVICE_DIR = "00_DAI/20_30/";
    static final String SCHEMA_FILE = "ums_addschema.xml";
    //static final String LDIF_FILE = "DAI_ds_remote_schema.ldif";
    static final String LDIF_FILE = "DAI.ldif";

    /**
     * Loads the ldif and service changes for the DAI Service
     *
     * @return true if successful otherwise false.
     */
    public boolean migrateService() {
        // Add Attribute Schema
        boolean isSuccess = false;
        try {
        //load ldif file 
        UpgradeUtils.log("servicedir is :" + SERVICE_DIR);
        UpgradeUtils.log("servicename is :" + SERVICE_NAME);
        String ldifPath =
                UpgradeUtils.getAbsolutePath(SERVICE_DIR, LDIF_FILE);
        UpgradeUtils.log("ldifPath is :" + ldifPath);
        UpgradeUtils.loadLdif(ldifPath);
        String fileName =
                UpgradeUtils.getAbsolutePath(SERVICE_DIR, SCHEMA_FILE);
        UpgradeUtils.log("fileName is :" + fileName);
        UpgradeUtils.log("Calling importServiceData");
        UpgradeUtils.importServiceData(fileName);
        isSuccess = true;
        } catch (UpgradeException e) {
        UpgradeUtils.debug.message("Error loading data :" + SERVICE_NAME,e);
        UpgradeUtils.log("Error loading data :" + 
        SERVICE_NAME+e.getMessage());
        }
        return isSuccess;
    }

    /**
     * Post Migration operations.
     *
     * @return true if successful else error.
     */
    public boolean postMigrateTask() {
        return true;
    }

    /**
     * Pre Migration operations.
     *
     * @return true if successful else error.
     */
    public boolean preMigrateTask() {
        return true;
    }
}

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
 * $Id: Migrate.java,v 1.1 2008-01-24 00:08:11 bina Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

import com.sun.identity.upgrade.MigrateTasks;
import com.sun.identity.upgrade.UpgradeUtils;
import com.sun.identity.upgrade.UpgradeException;
import java.util.HashMap;
import java.util.Map;

/**
 * Updates <code>iPlanetAMAuthCertService</code> service schema.
 * This class is invoked during migration from older versions
 * of Access Manager to the latest version.
 */
public class Migrate implements MigrateTasks {

    static final String SERVICE_NAME = "iPlanetAMAuthCertService";
    static final String SERVICE_DIR = "50_iPlanetAMAuthCertService/30_40";
    static final String SCHEMA_TYPE = "Organization";
    static final String SUB_SCHEMA = "serverconfig";
    static final String SCHEMA_FILE = "amAuthCert_addAttrs.xml";

    /**
     * Updates <code>iPlanetAMAuthCertService</code> service schema.
     *
     * @return true if successful otherwise false.
     */
    public boolean migrateService() {
        boolean isSuccess = false;
        try {
            String fileName =
                    UpgradeUtils.getAbsolutePath(SERVICE_DIR, SCHEMA_FILE);
            UpgradeUtils.addAttributeToSchema(
                    SERVICE_NAME, SCHEMA_TYPE, fileName);
            UpgradeUtils.addAttributeToSubSchema(SERVICE_NAME, SUB_SCHEMA,
                    SCHEMA_TYPE, fileName);

            // update attribute choice values
            String attributeName = "iplanet-am-auth-cert-user-profile-mapper";
            Map choiceValMap = new HashMap();
            choiceValMap.put("choiceNone", "none");
            UpgradeUtils.addAttributeChoiceValues(SERVICE_NAME, SUB_SCHEMA,
                    SCHEMA_TYPE, attributeName, choiceValMap);

            // TODO : iterate thru subrealms to remove 
            // attribute iplanet-am-auth-cert-ldap-profile-id from all
            // subrealms
            isSuccess = true;
        } catch (UpgradeException e) {
            UpgradeUtils.debug.error("Error loading data:" + SERVICE_NAME, e);
        }
        return isSuccess;
    }

    /**
     * Post Migration operations.
     *
     * @return true if successful else error.
     */
    public boolean postMigrateTask() {
        // remove attribute schema - TODO , this would also
        // required deleting this attribute in all instances
        // before doing this.
        //String attrName = "iplanet-am-auth-cert-ldap-profile-id";
        //UpgradeUtils.removeAttributeSchema(attrName);
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

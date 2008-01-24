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
 * $Id: Migrate.java,v 1.1 2008-01-24 00:41:51 bina Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

import com.sun.identity.upgrade.MigrateTasks;
import com.sun.identity.upgrade.UpgradeException;
import com.sun.identity.upgrade.UpgradeUtils;

/**
 * Updates <code>sunAMIdentityRepository</code> service schema.
 * This class is invoked during migration from older versions
 * of Access Manager to the latest version.
 * TODO : this is for FAM 8.0 , don't have enough info on this one
 * and the ldif changes.
 */
public class Migrate implements MigrateTasks {

    final static String SERVICE_NAME = "sunAMIdentityRepositoryService";
    final static String SERVICE_DIR = "50_sunAMIdentityRepositoryService/20_30";
    final static String SCHEMA_FILE = "idRepoService_addAttrs.xml";
    final static String ATTR1 = "sun-idrepo-ldapv3-config-user-objectclass";
    final static String ATTR2 = "sun-idrepo-ldapv3-config-user-attributes";
    final static String ATTR3 =
            "sun-idrepo-ldapv3-config-people-container-name";
    final static String ATTR4 = "sun-idrepo-ldapv3-config-agent-attributes";
    final static String ATTR5 = "sun-idrepo-ldapv3-config-isactive";
    final static String ATTR6 = "sun-idrepo-ldapv3-config-active";
    final static String ATTR7 = "sun-idrepo-ldapv3-config-inactive";
    final static String ATTR8 = "sun-idrepo-ldapv3-config-agent-attributes";
    final static String ATTR9 = "sunFilesObjectClasses";
    final static String ORG_ATTR1 = "sunOrganizationAliases";
    // add subconfig to global config
    // subconfig name - agent
    // id = SupportedIdentities
    // attrname = canBeMemberOf
    // value = agentgroup
    // add subconfig to global config
    // subconfig name -agentonly 
    // id = SupportedIdentities
    // attrname = servicename
    // value = AgentService
    // add subconfig to global config
    // subconfig name -agentgroup
    // id = SupportedIdentities
    // attrname = servicename
    // value = AgentService
    // attrname = canHaveMembers
    // value = agent
    // attrname = canAddMembers
    // value = agent
    final static String schemaType = "Global";

    /**
     * Updates the <code>sunIdentityRepostioryService<code> service schema.
     *
     * @return true if successful otherwise false.
     */
    public boolean migrateService() {
        /*TODO 
        boolean isSuccess = false;
        try {
        isSuccess=true;
        } catch (UpgradeException e) {
        UpgradeUtils.debug.error("Error loading data:" + SERVICE_NAME,e);
        }*/
        //return isSuccess;
        return true;
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

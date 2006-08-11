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
 * $Id: SMSMigration.java,v 1.3 2006-08-11 00:42:26 rarcot Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import netscape.ldap.LDAPDN;
import netscape.ldap.util.DN;

import com.iplanet.services.ldap.DSConfigMgr;
import com.iplanet.services.ldap.LDAPUser;
import com.iplanet.services.ldap.ServerInstance;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.providers.dpro.SSOProviderBundle;
import com.sun.identity.authentication.internal.AuthContext;
import com.sun.identity.authentication.internal.AuthPrincipal;

public class SMSMigration {

    public static void main(String args[]) {

        try {
            SSOToken token = getSSOToken(args);

            // (1) Copy sunServiceSchema attribute from service node to
            // all version nodes
            ServiceManager sm = new ServiceManager(token);
            Iterator subEntries = sm.getServiceNames().iterator();
            while (subEntries.hasNext()) {
                String sName = (String) subEntries.next();
                CachedSMSEntry cachedEntry = CachedSMSEntry.getInstance(token,
                        "ou=" + sName + SMSEntry.COMMA + SMSEntry.SERVICES_RDN
                                + SMSEntry.COMMA + SMSEntry.baseDN, null);
                SMSEntry entry = cachedEntry.getSMSEntry();
                System.out.println("\nMigrating Service Name: " + sName);
                String[] serviceSchema = entry
                        .getAttributeValues(SMSEntry.ATTR_SCHEMA);
                Iterator versions = entry.subEntries("*", 0, false, false)
                        .iterator();
                while (versions.hasNext()) {
                    SMSEntry e = (SMSEntry) versions.next();
                    try {
                        if (serviceSchema != null) {
                            e.setAttribute(SMSEntry.ATTR_SCHEMA, serviceSchema);
                        }
                    } catch (Exception le) {
                        // Ignore it
                    }
                    // System.out.println("SMSEntry: " + e);
                    e.save();
                    CachedSMSEntry cachedE = CachedSMSEntry.getInstance(token,
                            e.getDN(), null);
                    cachedE.refresh(e);
                    String version = LDAPDN.explodeDN(e.getDN(), true)[0]; 
                    System.out.println("\tVersion: " + version);
                    // Migrate service config data
                    migrateConfigData(token, sName, version);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void migrateConfigData(SSOToken token, String serviceName,
            String version) throws SMSException, SSOException {
        boolean rootOrg;
        ServiceSchemaManager ssm = new ServiceSchemaManager(token, serviceName,
                version);
        ServiceSchema gss = ssm.getGlobalSchema();
        ServiceSchema oss = ssm.getOrganizationSchema();
        // Search for entries with service name and version
        String[] objs = { serviceName, version };
        Iterator results = SMSEntry.search(
            MessageFormat.format(
                SMSEntry.FILTER_PATTERN_SERVICE, (Object[])objs)).iterator();
        while (results.hasNext()) {
            SMSEntry smsEntry = (SMSEntry) results.next();
            String dn = smsEntry.getDN();
            // Get parent's parent DN and check for root DN
            rootOrg = false;
            DN pDN = (new DN((new DN(dn)).getParent().toRFCString()));
            String parentDN = (new DN(pDN.getParent().toRFCString()))
                    .toRFCString();
            if (parentDN.equalsIgnoreCase("ou=services," + SMSEntry.baseDN)) {
                rootOrg = true;
            }
            // Get the org DN
            String orgDN = (new DN(parentDN)).getParent().toRFCString();
            // Create the base nodes for the service
            CreateServiceConfig.checkBaseNodesForOrg(token, orgDN, serviceName,
                    version);
            if (!smsEntry.isNewEntry()) {
                if (rootOrg && (gss != null)) {
                    System.out.println("\tMigrating Global Config Data: " + dn);
                    migrateConfigData(token, gss, smsEntry,
                            CreateServiceConfig.GLOBAL_CONFIG_NODE);
                }
                if (oss != null) {
                    System.out.println("\tMigrating Org Config Data: " + dn);
                    migrateConfigData(token, oss, smsEntry,
                            CreateServiceConfig.ORG_CONFIG_NODE);
                }
            }
        }
    }

    private static void migrateConfigData(SSOToken token, ServiceSchema ss,
            SMSEntry smsEntry, String configName) throws SMSException,
            SSOException {
        // Construct the ou=default node
        String rootDN = "ou=default," + configName + smsEntry.getDN();
        SMSEntry newRootSMSEntry = new SMSEntry(token, rootDN);
        copyAttributesAndCreate(ss, smsEntry, newRootSMSEntry);

        // Copy sub entries
        migrateSubConfigEntries(token, ss, smsEntry, newRootSMSEntry);
    }

    private static void migrateSubConfigEntries(SSOToken t, ServiceSchema ss,
            SMSEntry pSMSEntry, SMSEntry newpSMSEntry) throws SMSException,
            SSOException {
        // Check for sub-entries, get its ServiceSchema and create them
        Iterator subSMSEntries = pSMSEntry.subEntries("*", 0, false, false)
                .iterator();
        while (subSMSEntries.hasNext()) {
            SMSEntry subSMSEntry = (SMSEntry) subSMSEntries.next();
            String subEntryName = 
                LDAPDN.explodeDN(subSMSEntry.getDN(), true)[0];
            if (subEntryName.equalsIgnoreCase("GlobalConfig")
                    || subEntryName.equalsIgnoreCase("OrganizationConfig")
                    || subEntryName.equalsIgnoreCase("Instances")
                    || subEntryName.equalsIgnoreCase("PluginConfig")) {
                continue;
            }
            String subSchemaName = subEntryName;
            // Check if Service ID is present
            String[] ids = subSMSEntry
                    .getAttributeValues(SMSEntry.ATTR_SERVICE_ID);
            if (ids != null) {
                subSchemaName = ids[0];
            }
            ServiceSchema sss = ss.getSubSchema(subSchemaName);
            if (sss == null) {
                // %%% Because of bug in Policy, try with subEntry name
                if ((sss = ss.getSubSchema(subEntryName)) == null) {
                    continue;
                }
            }
            String newSubEntryDN = "ou=" + subEntryName + ","
                    + newpSMSEntry.getDN();
            SMSEntry newSubSMSEntry = new SMSEntry(t, newSubEntryDN);
            copyAttributesAndCreate(sss, subSMSEntry, newSubSMSEntry);
            migrateSubConfigEntries(t, sss, subSMSEntry, newSubSMSEntry);
        }
    }

    private static void copyAttributesAndCreate(ServiceSchema ss,
            SMSEntry oldSMSEntry, SMSEntry newSMSEntry) throws SMSException,
            SSOException {
        if (newSMSEntry.isNewEntry()) {
            newSMSEntry
                    .addAttribute(SMSEntry.ATTR_OBJECTCLASS, SMSEntry.OC_TOP);
            newSMSEntry.addAttribute(SMSEntry.ATTR_OBJECTCLASS,
                    SMSEntry.OC_SERVICE_COMP);
        }
        // Copy attributes
        Set validAttributeNames = ss.getAttributeSchemaNames();
        Map oldAttributes = SMSUtils.getAttrsFromEntry(oldSMSEntry);
        Map newAttributes = new HashMap();
        for (Iterator items = oldAttributes.keySet().iterator(); items
                .hasNext();) {
            String attrName = (String) items.next();
            if (validAttributeNames.contains(attrName)) {
                newAttributes.put(attrName, oldAttributes.get(attrName));
            }
        }
        if (!newAttributes.isEmpty()) {
            SMSUtils.setAttributeValuePairs(newSMSEntry, newAttributes, ss
                    .getSearchableAttributeNames());
        }
        // Check for priority, serviceID
        String[] objs;
        if ((objs = oldSMSEntry.getAttributeValues(SMSEntry.ATTR_SERVICE_ID)) 
                != null) 
        {
            if (!objs[0].equals(ss.getName())) {
                // Bug in the 5.0 Policy code which had incorrect service id
                // System.out.println("Inconsistent Service IDs: " + objs[0] +
                // " Vs. " + ss.getName());
                objs[0] = ss.getName();
            }
            newSMSEntry.setAttribute(SMSEntry.ATTR_SERVICE_ID, objs);
        }
        if ((objs = oldSMSEntry.getAttributeValues(SMSEntry.ATTR_PRIORITY)) 
                != null) 
        {
            newSMSEntry.setAttribute(SMSEntry.ATTR_PRIORITY, objs);
        }
        newSMSEntry.save();
    }

    // Currently, return the SSOToken of the admin configured in
    // serverconfig.xml. Must be modified to obtain the user name
    // and password and create a SSO token
    private static SSOToken getSSOToken(String[] args) throws SSOException {
        try {
            DSConfigMgr cfgMgr = DSConfigMgr.getDSConfigMgr();
            ServerInstance serInstance = cfgMgr
                    .getServerInstance(LDAPUser.Type.AUTH_ADMIN);
            AuthPrincipal user = new AuthPrincipal(serInstance.getAuthID());
            AuthContext authCtx = new AuthContext(user, serInstance.getPasswd()
                    .toCharArray());
            return (authCtx.getSSOToken());
        } catch (Exception e) {
            throw new SSOException(SSOProviderBundle.rbName, "invalidadmin",
                    null);
        }
    }
}

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
 * $Id: IdRepoListener.java,v 1.4 2006-11-04 00:08:25 kenwho Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm;

import com.iplanet.am.sdk.AMEvent;
import com.iplanet.am.sdk.AMObjectListener;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.jaxrpc.SOAPClient;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import netscape.ldap.LDAPDN;
import netscape.ldap.util.DN;

/**
 * This class provides the implementation for listening to change events in
 * Identity Repository.
 * 
 */
public final class IdRepoListener {

    private Map configMap = null;

    private static AMObjectListener remoteListener = null;

    private static Debug debug = Debug.getInstance("idrepoListener");

    protected static SOAPClient sclient;
    static {
        sclient = new SOAPClient("dummy");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMObjectListener#allObjectsChanged()
     */
    public void allObjectsChanged() {
        if (debug.messageEnabled()) {
            debug.message("IdRepoListener: allObjectsChanged Called!");
        }

        IdServices idServices = IdServicesFactory.getDataStoreServices();
        if (idServices instanceof IdCachedServices) {
            // If Caching was enabled - then clear the cache!!
            ((IdCachedServices) idServices).clearCache();
        }

        // Get the list of listeners setup with idRepo
        String org = (String) configMap.get("realm");
        ArrayList list = (ArrayList) AMIdentityRepository.listeners.get(org);
        // Update any listeners registered with IdRepo
        if (list != null) {
            int size = list.size();
            for (int j = 0; j < size; j++) {
                IdEventListener l = (IdEventListener) list.get(j);
                l.allIdentitiesChanged();
            }
        }
        if (remoteListener != null) {
            remoteListener.allObjectsChanged();
        }
    }

    public void objectChanged(String name, int type, Map cMap) {
        if (debug.messageEnabled()) {
            debug.message("objectChanged called = name:: " + name + "  type:: "
                    + type + "\n  configmap = " + configMap);
        }
        // Get the list of listeners setup with idRepo
        String org = (String) configMap.get("realm");
        ArrayList list = (ArrayList) AMIdentityRepository.listeners.get(org);

        IdServices idServices = IdServicesFactory.getDataStoreServices();
        boolean dirtyCache = false;
        if (idServices instanceof IdCachedServices) {
            // If Caching was enabled - then clear the cache!!
            dirtyCache = true;
        }
        String[] changed = getChangedIds(name, cMap);
        for (int i = 0; i < changed.length; i++) {

            if (dirtyCache) {
                ((IdCachedServices) idServices).dirtyCache(changed[i],
                        type, false, false,
                        Collections.EMPTY_SET);
            }

            // Update any listeners registered with IdRepo
            if (list != null) {
                int size = list.size();
                for (int j = 0; j < size; j++) {
                    IdEventListener l = (IdEventListener) list.get(j);
                    switch (type) {
                    case AMEvent.OBJECT_CHANGED:
                    case AMEvent.OBJECT_ADDED:
                        l.identityChanged(changed[i]);
                        break;
                    case AMEvent.OBJECT_REMOVED:
                        l.identityDeleted(changed[i]);
                        break;
                    case AMEvent.OBJECT_RENAMED:
                        l.identityRenamed(changed[i]);
                    }
                }
            }
            if (remoteListener != null) {
                remoteListener.objectChanged(changed[i], type, configMap);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMObjectListener#getConfigMap()
     */
    public Map getConfigMap() {

        return configMap;
    }

    public static void addRemoteListener(AMObjectListener l) {
        remoteListener = l;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMObjectListener#setConfigMap()
     */
    public void setConfigMap(Map cMap) {
        configMap = cMap;
    }

    public void setServiceAttributes(String sName, Map attrs)
            throws IdRepoException {
        String realm = (String) configMap.get("realm");
        String pluginName = (String) configMap.get("plugin-name");
        if (realm == null || pluginName == null) {
            AMIdentityRepository.debug.error(
                    "IdRepoListener.setServiveAttribute: realm or plugin name"
                    + " is null");
            Object[] args = { sName, IdType.ROLE.getName() };
            throw new IdRepoException(IdRepoBundle.BUNDLE_NAME, "105", args);
        }
        try {
            SSOToken token = (SSOToken) AccessController
                    .doPrivileged(AdminTokenAction.getInstance());
            ServiceConfigManager scm = new ServiceConfigManager(token,
                    IdConstants.REPO_SERVICE, "1.0");
            ServiceConfig sc = scm.getOrganizationConfig(realm, null);
            if (sc == null) {
                return;
            }
            /*
             * Set sNames = sc.getSubConfigNames("*",pluginName); if (sNames ==
             * null || sNames.isEmpty()) { AMIdentityRepository.debug.error(
             * "IdRepoListener.setServiveAttribute: plugin not configured");
             * Object [] args = {sName, IdType.ROLE.getName()}; throw new
             * IdRepoException(IdRepoBundle.BUNDLE_NAME, "105", args); } String
             * currentPluginName = (String) sNames.iterator().next();
             */
            ServiceConfig subConfig = sc.getSubConfig(pluginName);
            if (subConfig == null) {
                return;
            }
            Map attributes = subConfig.getAttributes();
            Set vals = (Set) attributes.get(IdConstants.SERVICE_ATTRS);
            if (vals == null || vals == Collections.EMPTY_SET) {
                vals = new HashSet();
            }
            String mapStr = sclient.encodeMap("result", attrs);
            vals = new HashSet();
            vals.add(mapStr);
            attributes.put(IdConstants.SERVICE_ATTRS, vals);
            subConfig.setAttributes(attributes);
        } catch (SMSException smse) {
            AMIdentityRepository.debug.error(
                    "IdRepoListener: Unable to set service attributes", smse);
            Object[] args = { sName, IdType.ROLE.getName() };
            throw new IdRepoException(IdRepoBundle.BUNDLE_NAME, "105", args);
        } catch (SSOException ssoe) {
            AMIdentityRepository.debug.error(
                    "IdRepoListener: Unable to set service attributes", ssoe);
            Object[] args = { sName, IdType.ROLE.getName() };
            throw new IdRepoException(IdRepoBundle.BUNDLE_NAME, "105", args);
        }
    }

    private String[] getChangedIds(String name, Map configMap) {
        int size = IdUtils.supportedTypes.size();
        // If configMap is null, then this is a "remote" cache update
        if (configMap == null) {
            String ct[] = new String[1];
            ct[0] = name;
            return ct;
        }
        String changedTypes[] = new String[size];
        int i = 0;
        if (configMap == null || configMap.isEmpty()) {
            changedTypes[i] = name;
            return changedTypes;
        }
        String realm = (String) configMap.get("realm");
        String Amsdk = (String) configMap.get("amsdk");
        boolean isAmsdk = (Amsdk == null) ? false : true;

        Iterator it = IdUtils.supportedTypes.iterator();
        while (it.hasNext()) {
            IdType itype = (IdType) it.next();
            String n = DN.isDN(name) ? LDAPDN.explodeDN(name, true)[0] : name;
            String id = "id=" + n + ",ou=" + itype.getName() + "," + realm;
            if (isAmsdk) {
                id = id + ",amsdkdn=" + name;
            }
            changedTypes[i] = id;
            i++;
        }
        return changedTypes;
    }
}

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
 * $Id: AMIdRepoListener.java,v 1.1 2005-11-01 00:29:08 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import netscape.ldap.util.DN;

import com.iplanet.am.util.Debug;

/**
 * This class provides the implementation for listening to change events in
 * Identity Repository.
 */
public class AMIdRepoListener implements AMObjectListener {

    private AMDirectoryManager dsManager;

    private Debug debug = AMEventManager.debug;

    AMIdRepoListener() {
        dsManager = AMDirectoryWrapper.getInstance();
    }

    public void objectChanged(String name, int eventType, Map configMap) {
        // Add a debug message
        if (debug.messageEnabled()) {
            debug.message("AMIdRepoListener.objectChanged(): name: " + name
                    + " type: " + eventType);
        }
        // Normalize the DN
        String normalizedDN = (new DN(name)).toRFCString().toLowerCase();

        // Update the cache manager
        dsManager.dirtyCache(AMCacheManager.sdkCache, normalizedDN, eventType,
                false, false, Collections.EMPTY_SET);
        AMStoreConnection.updateCache(normalizedDN, eventType);

        try {
            // TODO: What is the Deleted OrgCache? See if this can be eliminated
            if (AMCompliance.isComplianceUserDeletionEnabled()) {
                AMCompliance.cleanDeletedOrgCache(normalizedDN);
            }

            if (AMDCTree.isRequired()) { // TODO: Needs to use the generic
                                            // Cache
                AMDCTree.cleanDomainMap(normalizedDN);
            }
        } catch (AMException ame) {
            if (debug.warningEnabled()) {
                debug.warning("AMIdRepoListener.objectChanged() "
                        + "AMException occured: ", ame);
            }
        }

        // Notify affected objects above the event
        AMObjectImpl.notifyEntryEvent(normalizedDN, eventType, false);
        Iterator it = AMSDKRepo.listeners.iterator();
        while (it.hasNext()) {
            IdRepoListener l = (IdRepoListener) it.next();
            Map cMap = l.getConfigMap();
            l.objectChanged(normalizedDN, eventType, cMap);
        }
    }

    public void objectsChanged(String parentName, int eventType, Set attrNames,
            Map configMap) {
        // Add a debug message
        if (debug.messageEnabled()) {
            debug.message("AMIdRepoListener.objectsChanged(): "
                    + "parentName: " + parentName + " type: " + eventType
                    + "\n config map= " + configMap);
        }
        // Normalize the DN
        String dn = (new DN(parentName)).toRFCString().toLowerCase();

        // Update the cache manager
        dsManager.dirtyCache(AMCacheManager.sdkCache, dn, eventType, true,
                false, attrNames);
        AMStoreConnection.updateCache(dn, eventType);

        try {
            // TODO: What is the Deleted OrgCache? See if this can be eliminated
            if (AMCompliance.isComplianceUserDeletionEnabled()) {
                AMCompliance.cleanDeletedOrgCache(dn);
            }

            if (AMDCTree.isRequired()) { // TODO: Needs to use the generic
                                            // Cache
                AMDCTree.cleanDomainMap(dn);
            }
        } catch (AMException ame) {
            if (debug.warningEnabled()) {
                debug.warning("AMIdRepoListener.objectsChanged() "
                        + "AMException occured: ", ame);
            }
        }

        // Notify affected objects above the event
        AMObjectImpl.notifyEntryEvent(dn, eventType, true);
        Iterator it = AMSDKRepo.listeners.iterator();
        while (it.hasNext()) {
            IdRepoListener l = (IdRepoListener) it.next();
            l.allObjectsChanged();
            break;
        }
    }

    public void permissionsChanged(String orgName, Map configMap) {
        // Add a debug message
        if (debug.messageEnabled()) {
            debug.message("AMIdRepoListener.permissionsChanged(): "
                    + "orgName: " + orgName);
        }
        // Normalize the DN
        String dn = (new DN(orgName)).toRFCString().toLowerCase();

        // Update the cache manager
        dsManager.dirtyCache(AMCacheManager.sdkCache, dn,
                AMEvent.OBJECT_CHANGED, false, true, Collections.EMPTY_SET);

        // Update AMStoreConnection cache
        AMStoreConnection.updateCache(dn, AMEvent.OBJECT_CHANGED);

        // Notify affected objects above the event
        AMObjectImpl.notifyACIChangeEvent(dn, AMEvent.OBJECT_CHANGED);
        Iterator it = AMSDKRepo.listeners.iterator();
        while (it.hasNext()) {
            IdRepoListener l = (IdRepoListener) it.next();
            l.allObjectsChanged();
            break;
        }
    }

    public void allObjectsChanged() {
        debug.error("AMIdRepoListener: Received all objects changed event "
                + "from event service");

        AMDirectoryManager cm = AMCacheManager.getInstance();
        cm.clearCache(AMCacheManager.sdkCache);
        AMEvent amEvent = new AMEvent(AMStoreConnection.rootSuffix);
        AMObjectImpl.notifyAffectedDNs(AMStoreConnection.rootSuffix, amEvent);
        Iterator it = AMSDKRepo.listeners.iterator();
        while (it.hasNext()) {
            IdRepoListener l = (IdRepoListener) it.next();
            l.allObjectsChanged();
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMObjectListener#getConfigMap()
     */
    public Map getConfigMap() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMObjectListener#setConfigMap()
     */
    public void setConfigMap(Map cMap) {

    }
}

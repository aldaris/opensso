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
 * $Id: IdRepoEventListener.java,v 1.2 2006-06-16 19:36:38 rarcot Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk.remote;

import java.util.Map;
import java.util.Set;

import com.iplanet.am.sdk.AMObjectListener;

public class IdRepoEventListener implements AMObjectListener {

    public void allObjectsChanged() {
        DirectoryManagerImpl.processEntryChanged(
                EventListener.ALL_OBJECTS_CHANGED, "", 0, null, false);

    }

    public Map getConfigMap() {
        return null;
    }

    public void objectChanged(String name, int type, Map configMap) {
        DirectoryManagerImpl.processEntryChanged(EventListener.OBJECT_CHANGED,
                name, type, null, false);

    }

    public void objectsChanged(String parentNames, int type, Set attrNames,
            Map configMap) {
        DirectoryManagerImpl.processEntryChanged(EventListener.OBJECTS_CHANGED,
                parentNames, type, attrNames, false);

    }

    public void permissionsChanged(String orgName, Map configMap) {
        DirectoryManagerImpl.processEntryChanged(
                EventListener.PERMISSIONS_CHANGED, orgName, 0, null, false);

    }

    public void setConfigMap(Map cmap) {

    }
}

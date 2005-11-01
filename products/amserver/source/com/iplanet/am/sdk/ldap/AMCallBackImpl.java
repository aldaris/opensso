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
 * $Id: AMCallBackImpl.java,v 1.1 2005-11-01 00:29:26 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.iplanet.am.sdk.AMCallBack;
import com.iplanet.am.sdk.AMConstants;
import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMServiceUtils;
import com.iplanet.am.util.Debug;
import com.iplanet.services.ldap.AttrSet;
import com.iplanet.sso.SSOToken;
import com.sun.identity.sm.SchemaType;
import com.sun.identity.sm.ServiceConfig;

/**
 * This class has all the utility methods which determine the external pre-post
 * processing classes for (user, org, role, group) operations. It also provides
 * methods to execute the external implementations by performing a call back of
 * the methods corresponding to the operation in progress.
 */
public class AMCallBackImpl implements AMConstants {

    static Debug debug = CommonUtils.debug;

    private static final String PRE_POST_PROCESSING_MODULES_ATTR = 
        "iplanet-am-admin-console-pre-post-processing-modules";

    private static final String EXTERNAL_ATTRIBUTES_FETCH_ENABLED_ATTR = 
        "iplanet-am-admin-console-external-attribute-fetch-enabled";

    // Variable to store the imp instances for post processing
    private static Hashtable callBackObjects = new Hashtable();

    // Operation Types
    public static final int CREATE = 1;

    public static final int DELETE = 2;

    public static final int MODIFY = 3;

    public static SSOToken internalToken = CommonUtils.getInternalToken();

    private static Set getOrgConfigAttribute(String orgDN, String attrName) {
        // Obtain the ServiceConfig
        try {
            // Get the org config
            ServiceConfig sc = AMServiceUtils.getOrgConfig(internalToken,
                    orgDN, ADMINISTRATION_SERVICE);
            if (sc != null) {
                Map attributes = sc.getAttributes();
                return (Set) attributes.get(attrName);
            } else {
                return getDefaultGlobalConfig(attrName);
            }
        } catch (Exception ee) {
            return getDefaultGlobalConfig(attrName);
        }
    }

    private static Set getDefaultGlobalConfig(String attrName) {
        // Org Config may not exist. Get default values
        if (debug.messageEnabled()) {
            debug.message("AMCallBackImpl.getPrePostImpls() "
                    + "Organization config for service ("
                    + ADMINISTRATION_SERVICE + "," + attrName
                    + ") not found. Obtaining default service "
                    + "config values ..");
        }
        try {
            Map defaultValues = AMServiceUtils.getServiceConfig(CommonUtils
                    .getInternalToken(), ADMINISTRATION_SERVICE,
                    SchemaType.ORGANIZATION);
            if (defaultValues != null) {
                return (Set) defaultValues.get(attrName);
            }
        } catch (Exception e) {
            if (debug.warningEnabled()) {
                debug.warning("AMCallBackImpl.getPrePostProcessClasses(): "
                        + "Unable to get Pre/Post Processing information", e);
            }
        }
        return null;
    }

    private static Set getPrePostImpls(String orgDN) {
        return getOrgConfigAttribute(orgDN, PRE_POST_PROCESSING_MODULES_ATTR);
    }

    public static boolean isExistsPrePostPlugins(String orgDN) {
        Set plugins = getPrePostImpls(orgDN);
        return ((plugins != null) && (!plugins.isEmpty()));
    }

    public static boolean isExternalGetAttributeEnabled(String orgDN) {
        Set values = getOrgConfigAttribute(orgDN,
                EXTERNAL_ATTRIBUTES_FETCH_ENABLED_ATTR);

        boolean enabled = false;
        if (values != null && !values.isEmpty()) {
            String val = (String) values.iterator().next();
            enabled = (val.equalsIgnoreCase("true"));
        }

        debug.message("AMCallBackImpl.isExternalGetAttributeEnabled() = "
                + enabled);

        return enabled;
    }

    private static AMCallBack instantiateClass(String className) {
        try {
            return ((AMCallBack) Class.forName(className).newInstance());
        } catch (ClassNotFoundException c) {
            debug.error("AMCallBackImpl.instantiateClass(): Unable to "
                    + "locate class " + className, c);
        } catch (Exception e) {
            debug.error("AMCallBackImpl.instantiateClass(): Unable to "
                    + "instantiate class " + className, e);
        }
        return null;
    }

    /**
     * Returns an instantiated call back object. If not already instantiated
     * trys to instantiate. If not successful a null value is returned.
     */
    private static AMCallBack getCallBackObject(String className) {
        AMCallBack callBack = (AMCallBack) callBackObjects.get(className);
        if (callBack == null) { // Not yet instantiated. Instantiate now
            callBack = instantiateClass(className);
            if (callBack != null) {
                callBackObjects.put(className, callBack);
            }
        }
        return callBack;
    }

    public static Map getAttributes(SSOToken token, String entryDN,
            Set attrNames, String orgDN) {

        if (!isExternalGetAttributeEnabled(orgDN)) {
            return null;
        }

        Set implSet = getPrePostImpls(orgDN);
        if (implSet != null && !implSet.isEmpty()) {
            Map attributes = new HashMap();
            Iterator itr = implSet.iterator();
            while (itr.hasNext()) {
                String className = (String) itr.next();
                AMCallBack impl = getCallBackObject(className);
                if (impl == null) {
                    continue;
                }
                Map implAttrs = impl.getAttributes(token, entryDN, attrNames);
                if (implAttrs != null && !implAttrs.isEmpty()) {
                    attributes = CommonUtils.mergeMaps(implAttrs, attributes);
                }
            }
            return attributes;
        }
        return null;
    }

    public static Map preProcess(SSOToken token, String entryDN, String orgDN,
            Map oldAttrMap, Map newAttrMap, int operation, int objectType,
            boolean softDelete) throws AMException {
        Set implSet = getPrePostImpls(orgDN);
        if (implSet != null && !implSet.isEmpty()) {
            // Post processing impls present
            // Iterate through the Pre-Processing Impls and execute
            Iterator itr = implSet.iterator();
            while (itr.hasNext()) {
                String className = (String) itr.next();
                AMCallBack impl = getCallBackObject(className);
                if (impl == null) {
                    continue;
                }
                try {
                    Map map;
                    switch (operation) {
                    case CREATE:
                        map = impl.preProcessCreate(token, entryDN, newAttrMap,
                                objectType);
                        newAttrMap = ((map == null) ? newAttrMap : map);
                        break;
                    case MODIFY:
                        map = impl.preProcessModify(token, entryDN, oldAttrMap,
                                newAttrMap, objectType);
                        newAttrMap = ((map == null) ? newAttrMap : map);
                        break;
                    case DELETE:
                        impl.preProcessDelete(token, entryDN, oldAttrMap,
                                softDelete, objectType);
                        break;
                    }
                } catch (AMException ae) {
                    // Exception thrown by the external impl
                    debug.error("AMCallBackImpl.preProcess(): Preprocessing"
                            + "impl " + className
                            + " exception thrown by impl:", ae);
                    throw ae;
                }
            }
            return newAttrMap;
        }
        // At this point oldAttrSet should be returned only if newAttrSet is
        // not null as newAttrSet will be the latest one needed for updation
        return ((newAttrMap != null) ? newAttrMap : oldAttrMap);
    }

    // TODO: Remove this. Use the Maps interface only
    public static AttrSet preProcess(SSOToken token, String entryDN,
            String orgDN, AttrSet oldAttrSet, AttrSet newAttrSet,
            int operation, int objectType, boolean softDelete)
            throws AMException {
        Set implSet = getPrePostImpls(orgDN);
        if (implSet != null && !implSet.isEmpty()) {
            // Post processing impls present
            // Iterate through the Pre-Processing Impls and execute
            Iterator itr = implSet.iterator();
            Map newAttrMap = CommonUtils.attrSetToMap(newAttrSet);
            Map oldAttrMap = CommonUtils.attrSetToMap(oldAttrSet);

            while (itr.hasNext()) {
                String className = (String) itr.next();
                AMCallBack impl = getCallBackObject(className);
                if (impl == null) {
                    continue;
                }
                try {
                    Map map;
                    switch (operation) {
                    case CREATE:
                        map = impl.preProcessCreate(token, entryDN, newAttrMap,
                                objectType);
                        newAttrMap = ((map == null) ? newAttrMap : map);
                        break;
                    case MODIFY:
                        map = impl.preProcessModify(token, entryDN, oldAttrMap,
                                newAttrMap, objectType);
                        newAttrMap = ((map == null) ? newAttrMap : map);
                        break;
                    case DELETE:
                        impl.preProcessDelete(token, entryDN, oldAttrMap,
                                softDelete, objectType);
                        break;
                    }
                } catch (AMException ae) {
                    // Exception thrown by the external impl
                    debug.error("AMCallBackImpl.preProcess(): Preprocessing"
                            + "impl " + className
                            + " exception thrown by impl:", ae);
                    throw ae;
                }
            }
            return CommonUtils.mapToAttrSet(newAttrMap);
        }
        // At this point oldAttrSet should be returned only if newAttrSet is
        // not null as newAttrSet will be the latest one needed for updation
        return ((newAttrSet != null) ? newAttrSet : oldAttrSet);
    }

    // TODO: Remove this. Use the Maps interface only
    public static void postProcess(SSOToken token, String entryDN,
            String orgDN, AttrSet oldAttrSet, AttrSet newAttrSet,
            int operation, int objectType, boolean softDelete)
            throws AMException {
        // Use the external impls instantiated at the time of pre-processing
        Set implSet = getPrePostImpls(orgDN);
        if ((implSet != null) && (!implSet.isEmpty())) {
            Map newAttrMap = CommonUtils.attrSetToMap(newAttrSet);
            Map oldAttrMap = CommonUtils.attrSetToMap(oldAttrSet);
            // Iterate through the Pre-Processing Impls and execute
            Iterator itr = implSet.iterator();
            while (itr.hasNext()) {
                String className = (String) itr.next();
                AMCallBack impl = getCallBackObject(className);
                if (impl == null) {
                    continue;
                }
                try {
                    switch (operation) {
                    case CREATE:
                        impl.postProcessCreate(token, entryDN, newAttrMap,
                                objectType);
                        break;
                    case MODIFY:
                        impl.postProcessModify(token, entryDN, oldAttrMap,
                                newAttrMap, objectType);
                        break;
                    case DELETE:
                        impl.postProcessDelete(token, entryDN, oldAttrMap,
                                softDelete, objectType);
                        break;
                    }
                } catch (AMException ae) {
                    // Exception thrown by the external impl
                    debug.error("AMCallBackImpl.postProcess(): Preprocessing"
                            + "impl " + impl.getClass().getName()
                            + " exception thrown: ", ae);
                }
            }
        }
    }

    public static void postProcess(SSOToken token, String entryDN,
            String orgDN, Map oldAttrMap, Map newAttrMap, int operation,
            int objectType, boolean softDelete) throws AMException {
        // Use the external impls instantiated at the time of pre-processing
        Set implSet = getPrePostImpls(orgDN);
        if ((implSet != null) && (!implSet.isEmpty())) {
            // Iterate through the Pre-Processing Impls and execute
            Iterator itr = implSet.iterator();
            while (itr.hasNext()) {
                String className = (String) itr.next();
                AMCallBack impl = getCallBackObject(className);
                if (impl == null) {
                    continue;
                }
                try {
                    switch (operation) {
                    case CREATE:
                        impl.postProcessCreate(token, entryDN, newAttrMap,
                                objectType);
                        break;
                    case MODIFY:
                        impl.postProcessModify(token, entryDN, oldAttrMap,
                                newAttrMap, objectType);
                        break;
                    case DELETE:
                        impl.postProcessDelete(token, entryDN, oldAttrMap,
                                softDelete, objectType);
                        break;
                    }
                } catch (AMException ae) {
                    // Exception thrown by the external impl
                    debug.error("AMCallBackImpl.postProcess(): Preprocessing"
                            + "impl " + impl.getClass().getName()
                            + " exception thrown: ", ae);
                }
            }
        }
    }

    /**
     * Special method for pre processing memberShip modification for roles &
     * groups.
     */
    public static Set preProcessModifyMemberShip(SSOToken token,
            String entryDN, String orgDN, Set members, int operation,
            int objectType) throws AMException {
        Set implSet = getPrePostImpls(orgDN);
        if (implSet != null && !implSet.isEmpty()) {
            // Post processing impls present
            // Iterate through the PrePost-Processing plugins and execute
            Iterator itr = implSet.iterator();
            while (itr.hasNext()) {
                String className = (String) itr.next();
                AMCallBack impl = getCallBackObject(className);
                if (impl == null) {
                    continue;
                }
                try {
                    switch (operation) {
                    case ADD_MEMBER:
                        members = impl.preProcessAddUser(token, entryDN,
                                members, objectType);
                        break;
                    case REMOVE_MEMBER:
                        members = impl.preProcessRemoveUser(token, entryDN,
                                members, objectType);
                        break;
                    }
                } catch (AMException ae) {
                    // Exception thrown by the external impl
                    debug.error("AMCallBackImpl.preProcessModifyMemberShip():"
                            + " Preprocessing impl " + className
                            + " exception " + "thrown by impl:", ae);
                    throw ae;
                }
            }
        }
        return members;
    }

    /**
     * Special method for post processing memberShip modification for roles &
     * groups.
     */
    public static void postProcessModifyMemberShip(SSOToken token,
            String entryDN, String orgDN, Set members, int operation,
            int objectType) throws AMException {
        // Use the external impls instantiated at the time of pre-processing
        Set implSet = getPrePostImpls(orgDN);
        if ((implSet != null) && (!implSet.isEmpty())) {
            // Iterate through the PrePost-Processing plugins and execute
            Iterator itr = implSet.iterator();
            while (itr.hasNext()) {
                String className = (String) itr.next();
                AMCallBack impl = getCallBackObject(className);
                if (impl == null) {
                    continue;
                }
                try {
                    switch (operation) {
                    case ADD_MEMBER:
                        impl.postProcessAddUser(token, entryDN, members,
                                objectType);
                        break;
                    case REMOVE_MEMBER:
                        impl.postProcessRemoveUser(token, entryDN, members,
                                objectType);
                        break;
                    }
                } catch (AMException ae) {
                    // Exception thrown by the external impl
                    debug.error(
                            "AMCallBackImpl.postProcessModifyMemberShip()"
                                    + ": Preprocessing impl "
                                    + impl.getClass().getName()
                                    + " exception thrown: ", ae);
                }
            }
        }
    }
}

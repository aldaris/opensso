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
 * $Id: DefaultIDPAuthnContextMapper.java,v 1.5 2008-03-12 16:54:07 exu Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.plugins;

import com.sun.identity.saml2.assertion.AssertionFactory;
import com.sun.identity.saml2.assertion.AuthnContext;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.profile.IDPCache;
import com.sun.identity.saml2.profile.IDPSSOUtil;
import com.sun.identity.saml2.protocol.AuthnRequest;
import com.sun.identity.saml2.protocol.RequestedAuthnContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/** 
 * This class is an out of the box default implementation of interface
 * <code>IDPAuthnContextMapper</code>.
 */ 

public class DefaultIDPAuthnContextMapper 
    implements IDPAuthnContextMapper {

    public static SAML2MetaManager metaManager =
                                       SAML2Utils.getSAML2MetaManager();

    private static String DEFAULT = "default";
 
   /**
    * Constructor
    */
    public DefaultIDPAuthnContextMapper() {
    }

   /** 
    * Returns an <code>IDPAuthnContextInfo</code> object.
    *
    * @param authnRequest the <code>AuthnRequest</code> from the 
    * Service Provider
    * @param idpEntityID the Entity ID of the Identity Provider    
    * @param realm the realm to which the Identity Provider belongs
    * 
    * @return an <code>IDPAuthnContextInfo</code> object
    * @throws SAML2Exception if an error occurs.
    */
    public IDPAuthnContextInfo getIDPAuthnContextInfo(
        AuthnRequest authnRequest,
        String idpEntityID,
        String realm) 
        throws SAML2Exception {

        String classMethod = 
            "DefaultIDPAuthnContextMapper.getIDPAuthnContextInfo: ";

        RequestedAuthnContext requestedAuthnContext = null;
        List requestedClassRefs = null;
        String requestedClassRef = null;

        if (authnRequest != null) {
            requestedAuthnContext = authnRequest.getRequestedAuthnContext();
            if (requestedAuthnContext != null) {
                requestedClassRefs = 
                    requestedAuthnContext.getAuthnContextClassRef();
                if ((requestedClassRefs != null) 
                    && (requestedClassRefs.size() != 0)) {
                    // pick the first one for now
                    requestedClassRef = (String)requestedClassRefs.get(0);
                }
            }
        }   
        Map classRefSchemesMap = null;
        if (IDPCache.classRefSchemesHash != null) {
            classRefSchemesMap = (Map) IDPCache.classRefSchemesHash.get(
                idpEntityID + "|" + realm);
        }
        if (classRefSchemesMap == null || classRefSchemesMap.isEmpty()) {
            updateAuthnContextMapping(realm, idpEntityID);
            classRefSchemesMap = (Map) IDPCache.classRefSchemesHash.get(
                idpEntityID + "|" + realm);
            if (classRefSchemesMap == null) {
                classRefSchemesMap = new HashMap();
            }
        }
        Set authTypeAndValues = null;
        String classRef = null;
        if (requestedClassRef != null) {
            classRef = requestedClassRef;
            authTypeAndValues = (Set) classRefSchemesMap.get(requestedClassRef);
        }
        if (authTypeAndValues == null) {
            // no matching authnContextClassRef found in config, or
            // no valid requested authn class ref, use the default
            authTypeAndValues = (Set) classRefSchemesMap.get(DEFAULT);
            classRef = (String) IDPCache.defaultClassRefHash.get(
                idpEntityID + "|" + realm);
        }
        if (classRef == null) {
            classRef = SAML2Constants.CLASSREF_PASSWORD_PROTECTED_TRANSPORT;
        }
        AuthnContext authnContext = 
            AssertionFactory.getInstance().createAuthnContext();
        authnContext.setAuthnContextClassRef(classRef);
        IDPAuthnContextInfo info = new IDPAuthnContextInfo(
            authnContext, authTypeAndValues); 
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(classMethod +
                "requested AuthnContextClassRef=" + requestedClassRef + 
                "\nreturned AuthnContextClassRef=" + classRef + 
                "\nauthTypeAndValues=" + authTypeAndValues);
        }
        return info;
    } 

    /**
     * Returns <code>AuthnContext</code> that matches the authenticated level.
     * @param authLevel user authenticated level
     * @param realm the realm to which the Identity Provider belongs
     * @param idpEntityID the Entity ID of the Identity Provider    
     *
     * @return <code>AuthnContext</code> object that matches authenticated
     *  level. Return default AuthnContext if authLevel is <code>null</code>.
     * @throws SAML2Exception if an error occurs.
     */
    public AuthnContext getAuthnContextFromAuthLevel(
        String authLevel, String realm, String idpEntityID)
        throws SAML2Exception
    {
        String classRef = null;
        
        Map classRefLevelMap = null;
        if (IDPCache.classRefLevelHash != null) {
            classRefLevelMap = (Map) IDPCache.classRefLevelHash.get(
                idpEntityID + "|" + realm);
        }
        if (classRefLevelMap == null || classRefLevelMap.isEmpty()) {
            updateAuthnContextMapping(realm, idpEntityID);
            classRefLevelMap = (Map) IDPCache.classRefLevelHash.get(
                idpEntityID + "|" + realm);
            if (classRefLevelMap == null) {
                classRefLevelMap = new HashMap();
            }
        }
        if ((authLevel != null) && (authLevel.length() != 0)) {
            try {
                int level = Integer.parseInt(authLevel);
                Iterator iter = classRefLevelMap.keySet().iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    Integer value = (Integer) classRefLevelMap.get(key);
                    if (value != null && (level == value.intValue())) {
                        classRef = key;
                        break;
                    }
                }
            } catch (NumberFormatException ne) {
                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message(
                        "DefaultIDPAuthnContextMapper.getAuthnContextFromLevel:"
                        + " input authLevel is not valid.", ne);
                }
            }
        }
        if (classRef == null) {
            classRef = (String)IDPCache.defaultClassRefHash.get(
                idpEntityID + "|" + realm);
            if (classRef == null) {
                classRef = SAML2Constants.CLASSREF_PASSWORD_PROTECTED_TRANSPORT;
            }
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                "DefaultIDPAuthnContext.getClassRefFromLevel: authLevel=" +
                authLevel + ", classRef=" + classRef +
                ", classRefLevelMap=" + classRefLevelMap);
        }
        AuthnContext result = 
            AssertionFactory.getInstance().createAuthnContext();
        result.setAuthnContextClassRef(classRef);
        return result;
    }

   /** 
    * Returns true if the specified AuthnContextClassRef matches a list of
    * requested AuthnContextClassRef.
    *
    * @param authnRequest a list of requested AuthnContextClassRef's
    * @param acClassRef AuthnContextClassRef
    * @param comparison the type of comparison
    * @param realm the realm to which the Identity Provider belongs
    * @param idpEntityID the Entity ID of the Identity Provider    
    * 
    * @return true if the specified AuthnContextClassRef matches a list of
    *     requested AuthnContextClassRef
    */
    public boolean isAuthnContextMatching(List requestedACClassRefs,
        String acClassRef, String comparison, String realm,
        String idpEntityID) {

        if ((comparison == null) || (comparison.length() == 0) ||
            (comparison.equals("exact"))) {
            for(Iterator iter = requestedACClassRefs.iterator();
                iter.hasNext();) {

                String requstedACClassRef = (String)iter.next();
                if (requstedACClassRef.equals(acClassRef)) {
                    return true;
                }
            }
            return false;
        }

        Map acClassRefLevelMap = null;
        if (IDPCache.classRefLevelHash != null) {
            acClassRefLevelMap = (Map) IDPCache.classRefLevelHash.get(
                idpEntityID + "|" + realm);
        }
        if (acClassRefLevelMap == null || acClassRefLevelMap.isEmpty()) {
            updateAuthnContextMapping(realm, idpEntityID);
            acClassRefLevelMap = (Map) IDPCache.classRefLevelHash.get(
                idpEntityID + "|" + realm);
            if (acClassRefLevelMap == null) {
                acClassRefLevelMap = new HashMap();
            }
        }
        Integer levelInt = (Integer)acClassRefLevelMap.get(acClassRef);
        int level = (levelInt == null) ? 0 : levelInt.intValue();

        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("DefaultIDPAuthnContextMapper." +
                "isAuthnContextMatching: acClassRef = " + acClassRef +
                ", level = " + level + ", comparison = " + comparison);
        }
        if (comparison.equals("minimum")) {
            for(Iterator iter = requestedACClassRefs.iterator();
                iter.hasNext();) {

                String requstedACClassRef = (String)iter.next();
                Integer requestedLevelInt =
                    (Integer)acClassRefLevelMap.get(requstedACClassRef);
                int requestedLevel = (requestedLevelInt == null) ?
                    0 : requestedLevelInt.intValue();

                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message("DefaultIDPAuthnContextMapper." +
                        "isAuthnContextMatching: requstedACClassRef = " +
                        requstedACClassRef + ", level = " + requestedLevel);
                }

                if (level >= requestedLevel) {
                    return true;
                }
            }
            return false;
        } else if (comparison.equals("better")) {
            for(Iterator iter = requestedACClassRefs.iterator();
                iter.hasNext();) {

                String requstedACClassRef = (String)iter.next();
                Integer requestedLevelInt =
                    (Integer)acClassRefLevelMap.get(requstedACClassRef);
                int requestedLevel = (requestedLevelInt == null) ?
                    0 : requestedLevelInt.intValue();

                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message("DefaultIDPAuthnContextMapper." +
                        "isAuthnContextMatching: requstedACClassRef = " +
                        requstedACClassRef + ", level = " + requestedLevel);
                }

                if (level <= requestedLevel) {
                    return false;
                }
            }
            return true;
        } else if (comparison.equals("maximum")) {
            for(Iterator iter = requestedACClassRefs.iterator();
                iter.hasNext();) {

                String requstedACClassRef = (String)iter.next();
                Integer requestedLevelInt =
                    (Integer)acClassRefLevelMap.get(requstedACClassRef);
                int requestedLevel = (requestedLevelInt == null) ?
                    0 : requestedLevelInt.intValue();

                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message("DefaultIDPAuthnContextMapper." +
                        "isAuthnContextMatching: requstedACClassRef = " +
                        requstedACClassRef + ", level = " + requestedLevel);
                }

                if (level <= requestedLevel) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    private void updateAuthnContextMapping(String realm, String idpEntityID) {

        List values = SAML2Utils.getAllAttributeValueFromSSOConfig(realm,
            idpEntityID, SAML2Constants.IDP_ROLE,
            SAML2Constants.IDP_AUTHNCONTEXT_CLASSREF_MAPPING);
        Map classRefLevelMap = new LinkedHashMap();
        String defaultClassRef = null;
        Map classRefSchemesMap = new LinkedHashMap();

        if ((values != null) && (values.size() != 0)) {
            for (int i = 0; i < values.size(); i++) {
                boolean isDefault = false;
                String value = ((String) values.get(i)).trim();
                if (value.endsWith("|" + DEFAULT)) {
                    value = value.substring(0, value.length()-DEFAULT.length());
                    isDefault = true;
                }
 
                StringTokenizer st = new StringTokenizer(value, "|");

                if (st.hasMoreTokens()) {
                    String classRef = st.nextToken().trim();
                    Set authTypeAndValues = new HashSet();
                    if (st.hasMoreTokens()) {
                        String level = st.nextToken();
                        if (level.indexOf("=") == -1) {
                            try {
                                Integer authLevel = new Integer(level);
                                classRefLevelMap.put(classRef, authLevel);
                                if (isDefault && 
                                    !classRefLevelMap.containsKey(DEFAULT)) 
                                {
                                    classRefLevelMap.put(DEFAULT, authLevel);
                                    defaultClassRef = classRef;
                                }
                            } catch (NumberFormatException nfe) {
                                if (SAML2Utils.debug.messageEnabled()) {
                                    SAML2Utils.debug.message(
                                       "DefaultIDPAuthnContextMapper." +
                                       "getACClassRefLevelMap:", nfe);
                                }
                            }
                        } else {
                            // this is not a level, but a auth scheme def.
                            if (level.trim().length() != 0) {
                                authTypeAndValues.add(level);
                            }
                        }
                        while (st.hasMoreTokens()) {
                            String authTypeAndValue = st.nextToken().trim();
                            if (authTypeAndValue.length() != 0) {
                                authTypeAndValues.add(authTypeAndValue);
                            }
                        }
                    }
                    classRefSchemesMap.put(classRef, authTypeAndValues);
                    if (isDefault) {
                        classRefSchemesMap.put(DEFAULT, authTypeAndValues);
                    }
                }
            }
        }

        String key = idpEntityID + "|" + realm;

        if (!classRefSchemesMap.isEmpty()) {
            IDPCache.classRefSchemesHash.put(key, classRefSchemesMap);
        }
        if (!classRefLevelMap.isEmpty()) {
            IDPCache.classRefLevelHash.put(key, classRefLevelMap);
        }
        if (defaultClassRef != null) {
            IDPCache.defaultClassRefHash.put(key, defaultClassRef);
        }
    }

}

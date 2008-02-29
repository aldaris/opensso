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
 * $Id: DefaultIDPAuthnContextMapper.java,v 1.4 2008-02-29 00:22:04 exu Exp $
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
import com.sun.identity.saml2.profile.IDPSSOUtil;
import com.sun.identity.saml2.protocol.AuthnRequest;
import com.sun.identity.saml2.protocol.RequestedAuthnContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

        Map attrs = null;
        Set authTypeAndValues = null;
        IDPAuthnContextInfo info = null;
        RequestedAuthnContext requestedAuthnContext = null;
        AuthnContext authnContext = null;
        List requestedClassRefs = null;
        String requestedClassRef = null;
        List classRefs = null;
        String classRef = null;

        try {
            IDPSSOConfigElement config = metaManager.getIDPSSOConfig(
                                          realm, idpEntityID);
            attrs = SAML2MetaUtils.getAttributes(config);
        } catch (SAML2MetaException sme) {
            SAML2Utils.debug.error(classMethod +
                   "get IDPSSOConfig failed:", sme);
            throw new SAML2Exception(sme.getMessage());
        }
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
        List values = (List) attrs.get(
                    SAML2Constants.IDP_AUTHNCONTEXT_CLASSREF_MAPPING);
        if ((values != null) && (values.size() != 0)) {
            String defaultValue = null;
            if (requestedClassRef != null) {
                for (int i = 0; i < values.size(); i++) {
                    String value = ((String) values.get(i)).trim();
                    if (SAML2Utils.debug.messageEnabled()) {
                        SAML2Utils.debug.message(classMethod +
                            "configured mapping=" + value); 
                    }
                    if (value.endsWith("|"+ DEFAULT)) {
                        value = value.substring(
                            0, value.length()-DEFAULT.length());
                        defaultValue = value;
                    }
                    StringTokenizer st = new StringTokenizer(value, "|");
                    if (st.hasMoreTokens()) {
                        // the first element is an AuthnContextClassRef 
                        classRef = ((String)st.nextToken()).trim();
                        if (classRef.equals(requestedClassRef)) {
                            authTypeAndValues = new HashSet();
                            if (st.hasMoreTokens()) {
                                String level = st.nextToken().trim();
                                // check if level is realy authLevel
                                if (level.indexOf("=") != -1) {
                                    authTypeAndValues.add(level);
                                }
                                
                                while (st.hasMoreTokens()) {
                                    String authTypeAndValue = 
                                        st.nextToken().trim();
                                    if (authTypeAndValue.length() != 0) {
                                        authTypeAndValues.add(authTypeAndValue);
                                    }
                                }
                            }
                            break;
                        }
                    } 
                }
            }
            if (authTypeAndValues == null) {
                // no matching authnContextClassRef found in config, or
                // no valid requested authn class ref, use the default
                if (defaultValue != null) {
                    StringTokenizer st = new StringTokenizer(defaultValue, "|");
                    if (st.hasMoreTokens()) {
                        // the first element is an AuthnContextClassRef 
                        classRef = ((String)st.nextToken()).trim();
                        authTypeAndValues = new HashSet();
                        if (st.hasMoreTokens()) {
                            String level = st.nextToken().trim();
                            // check if level is realy authLevel
                            if (level.indexOf("=") != -1) {
                                authTypeAndValues.add(level);
                            }
                            while (st.hasMoreTokens()) {
                                String authTypeAndValue = st.nextToken().trim();
                                if (authTypeAndValue.length() != 0) {
                                    authTypeAndValues.add(authTypeAndValue);
                                }
                            }
                        }
                    }
                } else {
                    classRef = 
                        SAML2Constants.CLASSREF_PASSWORD_PROTECTED_TRANSPORT;
                }
            }
            authnContext = 
                AssertionFactory.getInstance().createAuthnContext();
            authnContext.setAuthnContextClassRef(classRef);
            info = new IDPAuthnContextInfo(authnContext, authTypeAndValues); 
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(classMethod +
                    "requested AuthnContextClassRef=" + requestedClassRef + 
                    "\nreturned AuthnContextClassRef=" + classRef + 
                    "\nauthTypeAndValues=" + authTypeAndValues);
            }
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
        
        Map levelAcClassRefMap = getLevelACClassRefMap(realm, idpEntityID);
        if ((authLevel != null) && (authLevel.length() != 0)) {
            classRef = (String)levelAcClassRefMap.get(authLevel);
        }
        if (classRef == null) {
            classRef = (String)levelAcClassRefMap.get(DEFAULT);
            if (classRef == null) {
                classRef = SAML2Constants.CLASSREF_PASSWORD_PROTECTED_TRANSPORT;
            }
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

        Map acClassRefLevelMap = getACClassRefLevelMap(realm, idpEntityID);
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

    private Map getACClassRefLevelMap(String realm, String idpEntityID) {

        List values = SAML2Utils.getAllAttributeValueFromSSOConfig(realm,
            idpEntityID, SAML2Constants.IDP_ROLE,
            SAML2Constants.IDP_AUTHNCONTEXT_CLASSREF_MAPPING);

        Map resultMap = new HashMap();

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
                    if (st.hasMoreTokens()) {
                        String level = st.nextToken();
                        try {
                            Integer authLevel = new Integer(level);
                            resultMap.put(classRef, authLevel);
                            if (isDefault && !resultMap.containsKey(DEFAULT)) {
                                resultMap.put(DEFAULT, authLevel);
                            }
                        } catch (NumberFormatException nfe) {
                            if (SAML2Utils.debug.messageEnabled()) {
                                SAML2Utils.debug.message(
                                   "DefaultIDPAuthnContextMapper." +
                                   "getACClassRefLevelMap:", nfe);
                            }
                        }
                    }
                }
            }
        }

        return resultMap;
    }

    private Map getLevelACClassRefMap(String realm, String idpEntityID) {

        List values = SAML2Utils.getAllAttributeValueFromSSOConfig(realm,
            idpEntityID, SAML2Constants.IDP_ROLE,
            SAML2Constants.IDP_AUTHNCONTEXT_CLASSREF_MAPPING);

        Map resultMap = new HashMap();

        if ((values != null) && (values.size() != 0)) {
            for (int i = 0; i < values.size(); i++) {
                boolean isDefault = false;
                String value = ((String) values.get(i)).trim();
                if (value.endsWith("|" + DEFAULT)) {
                    value = value.substring(0, value.length()-DEFAULT.length());
                    isDefault = true;
                }

                StringTokenizer st = new StringTokenizer(value, "|");
                String classRef = null;
                String authLevel = null;
                if (st.hasMoreTokens()) {
                    classRef = st.nextToken().trim();
                    if (st.hasMoreTokens()) {
                        String secondToken = st.nextToken().trim();
                        if (secondToken.indexOf("=") == -1) {
                            resultMap.put(secondToken, classRef);
                        }
                    }
                    if (isDefault && !resultMap.containsKey(DEFAULT)) {
                        resultMap.put(DEFAULT, classRef);
                    }
                }
            }
        }

        return resultMap;
    }
}

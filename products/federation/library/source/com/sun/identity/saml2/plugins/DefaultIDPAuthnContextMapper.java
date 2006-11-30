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
 * $Id: DefaultIDPAuthnContextMapper.java,v 1.2 2006-11-30 05:47:40 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
import java.util.HashSet;
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
            if (requestedClassRef != null) {
                for (int i = 0; i < values.size(); i++) {
                    String value = ((String) values.get(i)).trim();
                    if (SAML2Utils.debug.messageEnabled()) {
                        SAML2Utils.debug.message(classMethod +
                            "configured mapping=" + value); 
                    }
                    StringTokenizer st = new StringTokenizer(value, "|");
                    if (st.hasMoreTokens()) {
                        // the first element is an AuthnContextClassRef 
                        classRef = ((String)st.nextToken()).trim();
                        if (classRef.equals(requestedClassRef)) {
                            authTypeAndValues = new HashSet();
                            while (st.hasMoreTokens()) {
                                String authTypeAndValue = 
                                    ((String)st.nextToken()).trim();
                                if (authTypeAndValue.length() != 0) {
                                    authTypeAndValues.add(authTypeAndValue);
                                }
                            }
                            break;
                        }
                    } 
                }
            }
            if (authTypeAndValues == null) {
                // no matching authnContextClassRef found in config, or
                // no valid requested authn class ref, use the first 
                // one in  the config 
                String value = ((String) values.get(0)).trim();
                StringTokenizer st = new StringTokenizer(value, "|");
                if (st.hasMoreTokens()) {
                    // the first element is an AuthnContextClassRef 
                    classRef = ((String)st.nextToken()).trim();
                    authTypeAndValues = new HashSet();
                    while (st.hasMoreTokens()) {
                        String authTypeAndValue = 
                            ((String)st.nextToken()).trim();
                        if (authTypeAndValue.length() != 0) {
                            authTypeAndValues.add(authTypeAndValue);
                        }
                    }
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
}

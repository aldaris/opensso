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
 * $Id: DefaultIDPAttributeMapper.java,v 1.4 2007-08-17 22:48:11 exu Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.plugins;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.plugin.datastore.DataStoreProviderException;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionException;

import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.assertion.AssertionFactory;
import com.sun.identity.saml2.assertion.Attribute;

/**
 * This class <code>DefaultAttributeMapper</code> implements the
 * <code>IDPAttributeMapper</code> to return the SAML <code>Attribute</code>
 * objects that may be inserted in the SAML Assertion.
 * This IDP attribute mapper reads the attribute map configuration defined
 * in the hosted IDP configuration and construct the SAML
 * <code>Attribute</code> objects. If the mapped values are not present in
 * the data store, this will try to read from the Single sign-on token.
 */
public class DefaultIDPAttributeMapper extends DefaultAttributeMapper 
     implements IDPAttributeMapper {

    /**
     * Constructor
     */
    public DefaultIDPAttributeMapper() {
        debug.message("DefaultIDPAttributeMapper.Constructor");
    }

    /**
     * Returns list of SAML <code>Attribute</code> objects for the 
     * IDP framework to insert into the generated <code>Assertion</code>. 
     * @param session Single sign-on session.
     * @param hostEntityID <code>EntityID</code> of the hosted entity.
     * @param remoteEntityID <code>EntityID</code> of the remote entity.
     * @param realm name of the realm.
     * @exception SAML2Exception if any failure.
     */
    public List getAttributes(
        Object session,
        String hostEntityID,
        String remoteEntityID,
        String realm 
    ) throws SAML2Exception {
 
        if(hostEntityID == null) {
           throw new SAML2Exception(bundle.getString(
                 "nullHostEntityID"));
        }

        if(realm == null) {
           throw new SAML2Exception(bundle.getString(
                 "nullHostEntityID"));
        }
       
        if(session == null) {
           throw new SAML2Exception(bundle.getString(
                 "nullSSOToken"));
        }

        try {
            if(!SessionManager.getProvider().isValid(session)) {
               if(debug.warningEnabled()) {
                  debug.warning("DefaultIDPAttributeMapper.getAttributes: " +
                  "Invalid session");
               }
               return null;
            }
            Map configMap = getConfigAttributeMap(realm, remoteEntityID, SP);
            if (debug.messageEnabled()) {
                debug.message("DefaultIDPAttributeMapper.getAttr:" +
                    "remote SP attribute map = " + configMap);
            }
            if (configMap == null || configMap.isEmpty()) {
                configMap = getConfigAttributeMap(realm, hostEntityID, IDP);
                if (configMap == null || configMap.isEmpty()) {
                    if (debug.messageEnabled()) {
                        debug.message("DefaultIDPAttributeMapper.getAttr:" +
                            "Configuration map is not defined.");
                    }
                    return null;
                }
                if (debug.messageEnabled()) {
                    debug.message("DefaultIDPAttributeMapper.getAttributes:" +
                        "hosted IDP attribute map=" + configMap);
               }
            }

            List attributes = new ArrayList();
            
            Set localAttributes = new HashSet();
            localAttributes.addAll(configMap.values());
            Map valueMap = null;

            try {
                valueMap = dsProvider.getAttributes(
                     SessionManager.getProvider().getPrincipalName(session),
                     localAttributes); 
            } catch (DataStoreProviderException dse) {
                if(debug.warningEnabled()) {
                   debug.warning("DefaultIDPAttributeMapper.getAttributes: "+
                   "Datastore exception", dse);
                }
                //continue to check in ssotoken.
            }

            Iterator iter = configMap.keySet().iterator();
            while(iter.hasNext()) {
                String samlAttribute = (String)iter.next();
                String localAttribute = (String)configMap.get(samlAttribute);
                String[] localAttributeValues = null;
                if(valueMap != null && !valueMap.isEmpty()) {
                   Set values = (Set)valueMap.get(localAttribute); 
                   if(values == null || values.isEmpty()) {
                      if(debug.messageEnabled()) {
                         debug.message("DefaultIDPAttributeMapper.getAttribute:"
                         + " user profile does not have value for " + 
                         localAttribute + " but is going to check ssotoken:");
                      }
                   } else {
                      localAttributeValues = (String[])
                          values.toArray(new String[values.size()]);
                   }
                } 
                if (localAttributeValues == null) {
                    localAttributeValues = SessionManager.
                        getProvider().getProperty(session, localAttribute);
                }

                if ((localAttributeValues == null) ||
                    (localAttributeValues.length == 0))
                {
                    if (debug.messageEnabled()) {
                        debug.message("DefaultIDPAttributeMapper.getAttribute:"
                            + " user does not have " + localAttribute);
                    }
                    continue;
                }

                attributes.add(
                    getSAMLAttribute(samlAttribute, localAttributeValues));
            }
            return attributes;      
        } catch (SAML2Exception sme) {
            debug.error("DefaultIDPAttribute.getAttributes: " +
            "SAML Exception", sme);
            throw new SAML2Exception(sme);

        } catch (SessionException se) {
            debug.error("DefaultIDPAttribute.getAttributes: " +
            "SessionException", se);
            throw new SAML2Exception(se);
        }

    }

    /**
     * Returns the SAML <code>Attribute</code> object.
     * @param name attribute name.
     * @param values attribute values.
     * @exception SAML2Exception if any failure.
     */
    protected Attribute getSAMLAttribute(String name, String[] values)
      throws SAML2Exception {

         if(name == null) {
            throw new SAML2Exception(bundle.getString(
                  "nullInput"));
         }

         AssertionFactory factory = AssertionFactory.getInstance();
         Attribute attribute =  factory.createAttribute();

         attribute.setName(name);
         if(values != null) {
            List list = new ArrayList();
            for (int i=0; i<values.length; i++) {
                list.add(XMLUtils.escapeSpecialCharacters(
                    values[i]));
            }
            attribute.setAttributeValueString(list);
         }
         return attribute;
    }
}

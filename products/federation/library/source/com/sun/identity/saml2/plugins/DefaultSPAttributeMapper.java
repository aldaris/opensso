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
 * $Id: DefaultSPAttributeMapper.java,v 1.1 2006-10-30 23:16:31 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.plugins;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.saml2.assertion.Attribute;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Iterator;


/**
 * This class <code>DefaultSPAttribute</code> implements
 * <code>SPAttributeMapper</code> for mapping the assertion attributes
 * to local attributes configured in the provider configuration.
 */
public class DefaultSPAttributeMapper extends DefaultAttributeMapper 
     implements SPAttributeMapper {

    /**
     * Constructor.
     */
    public DefaultSPAttributeMapper() { 
        debug.message("DefaultSPAttributeMapper.constructor");
        role = SP;
    }

    /**
     * Returns attribute map for the given list of <code>Attribute</code>
     * objects. 
     * @param attributes list <code>Attribute</code>objects.
     * @param userID universal identifier or distinguished name(DN) of the user.
     * @param hostEntityID <code>EntityID</code> of the hosted provider.
     * @param remoteEntityID <code>EntityID</code> of the remote provider. 
     * @param realm realm name.
     * @return a map of mapped attribute value pair. This map has the
     *         key as the attribute name and the value as the attribute value
     * @exception SAML2Exception if any failure.
     */ 
    public Map getAttributes(
        List attributes,
        String userID,
        String hostEntityID,
        String remoteEntityID, 
        String realm
    ) throws SAML2Exception {

        if(attributes == null || attributes.size() == 0) {
           throw new SAML2Exception(bundle.getString(
                 "nullAttributes")); 
        }

        if(hostEntityID == null) {
           throw new SAML2Exception(bundle.getString(
                 "nullHostEntityID"));
        }

        if(realm == null) {
           throw new SAML2Exception(bundle.getString(
                 "nullRealm"));
        }
 
        try {
            Map map = new HashMap();
            Map configMap = getConfigAttributeMap(realm, hostEntityID);

            for(Iterator iter = attributes.iterator(); iter.hasNext();) {

                Attribute attribute = (Attribute)iter.next();
                Set values = new HashSet(); 
                values.addAll(attribute.getAttributeValueString());
                String attributeName = attribute.getName();

                String localAttribute = (String)configMap.get(attributeName);
                if(localAttribute != null && localAttribute.length() > 0) {
                   map.put(localAttribute, values);  
                } else {
                   map.put(attributeName, values); 
                }
             }
             return map;

        } catch(SAML2Exception se) {
            debug.error("DefaultSPAccountMapper.getAttributes:MetaException",
                       se);  
            throw new SAML2Exception(se.getMessage());
        }

    }

}

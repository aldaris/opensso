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
 * $Id: DefaultAttributeMapper.java,v 1.1 2006-10-30 23:16:29 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.plugins;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.plugin.datastore.DataStoreProvider;

import com.sun.identity.saml2.assertion.Attribute;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.jaxb.entityconfig.BaseConfigType;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Collections;

/**
 * This class <code>DefaultAttribute</code> is the base class for 
 * <code>DefaultSPAttributeMapper</code> and 
 * <code>DefaultIDPAttributeMapper</code> for sharing the common 
 * functionalities.
 */
public class DefaultAttributeMapper {

    protected static SAML2MetaManager metaManager = null;
    protected static Debug debug = SAML2Utils.debug;
    protected static ResourceBundle bundle = SAML2Utils.bundle;
    protected static DataStoreProvider dsProvider = null;
    protected static final String IDP = "IDP";
    protected static final String SP = "SP";
    protected String role = null;

    static {
        try {
            dsProvider = SAML2Utils.getDataStoreProvider();
            metaManager = new SAML2MetaManager();
        } catch (Exception ex) {
            debug.error("DefaultAttributeMapper.static init failed.", ex);
        }
    }

    /**
     * Constructor.
     */
    public DefaultAttributeMapper() {}

    /**
     * Returns the attribute map by parsing the configured map in hosted
     * provider configuration
     * @param realm realm name.
     * @param hostEntityID <code>EntityID</code> of the hosted provider.
     * @return a map of local attributes configuration map.
     *        This map will have a key as the SAML attribute name and the value
     *        is the local attribute. 
     * @exception <code>SAML2Exception</code> if any failured.
     */
    public Map getConfigAttributeMap(
         String realm, String hostEntityID) throws SAML2Exception {

        if(realm == null) {
           throw new SAML2Exception(bundle.getString(
             "nullRealm"));
        }

        if(hostEntityID == null) {
           throw new SAML2Exception(bundle.getString(
             "nullHostEntityID"));
        }

        try {
            BaseConfigType config = null;
            if(role.equals(SP)) {
               config = metaManager.getSPSSOConfig(realm, hostEntityID);
            } else {
               config = metaManager.getIDPSSOConfig(realm, hostEntityID);
            }


            if(config == null) {
               if(debug.warningEnabled()) {
                  debug.warning("DefaultAttributeMapper.getConfigAttribute" +
                  "Map: configuration is not defined.");
               }
               return Collections.EMPTY_MAP;
            }

            Map attribConfig = SAML2MetaUtils.getAttributes(config);
            List mappedAttributes = 
                 (List)attribConfig.get(SAML2Constants.ATTRIBUTE_MAP);

            if(mappedAttributes == null || mappedAttributes.size() == 0) {
               if(debug.messageEnabled()) {
                  debug.message("DefaultAttributeMapper.getConfigAttributeMap:"
                  + "Attribute map is not defined for entity: " + hostEntityID);
               }
               return Collections.EMPTY_MAP; 
            }
            Map map = new HashMap();

            for(Iterator iter = mappedAttributes.iterator(); iter.hasNext();) {
                String entry = (String)iter.next();

                if(entry.indexOf("=") == -1) {
                   if(debug.messageEnabled()) {
                      debug.message("DefaultAttributeMapper.getConfig" +
                      "AttributeMap: Invalid entry." + entry);
                   }
                   continue;
                }

                StringTokenizer st = new StringTokenizer(entry, "="); 
                map.put(st.nextToken(), st.nextToken());
            }
            return map;

        } catch(SAML2MetaException sme) {
            debug.error("DefaultAttributeMapper.getConfigAttributeMap: " +
            "Meta Exception", sme);
            throw new SAML2Exception(sme.getMessage());

        }
    }

}

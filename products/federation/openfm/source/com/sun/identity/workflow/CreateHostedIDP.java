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
 * $Id: CreateHostedIDP.java,v 1.5 2008-03-12 15:14:09 veiming Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.workflow;

import com.sun.identity.cot.COTException;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Creates Hosted Identity Provider.
 */
public class CreateHostedIDP
    extends Task {
    public CreateHostedIDP() {
    }
    
    /**
     * Creates hosted identity provider.
     *
     * @param locale Locale of the Request
     * @param params Map of creation parameters.
     */
    public String execute(Locale locale, Map params)
        throws WorkflowException {
        validateParameters(params);
        String metadataFile = getString(params, ParameterKeys.P_META_DATA);
        boolean hasMetaData = (metadataFile != null) && 
            (metadataFile.trim().length() > 0);
        String metadata = null;
        String extendedData = null;

        if (hasMetaData) {
            String extendedDataFile = getString(params, 
                ParameterKeys.P_EXENDED_DATA);
            metadata = getContent(metadataFile, locale);
            extendedData = getContent(extendedDataFile, locale);
        } else {
            String entityId = getString(params, ParameterKeys.P_ENTITY_ID);
            String metaAlias = generateMetaAlias(getString(params, 
                ParameterKeys.P_REALM));
            Map map = new HashMap();
            map.put(CreateSAML2HostedProviderTemplate.P_IDP, metaAlias);
            map.put(CreateSAML2HostedProviderTemplate.P_IDP_E_CERT, 
                getString(params, ParameterKeys.P_IDP_E_CERT));
            map.put(CreateSAML2HostedProviderTemplate.P_IDP_S_CERT, 
                getString(params, ParameterKeys.P_IDP_S_CERT));

            try {
                metadata = CreateSAML2HostedProviderTemplate.
                    buildMetaDataTemplate(entityId, map);
                extendedData = CreateSAML2HostedProviderTemplate.
                    createExtendedDataTemplate(entityId, map);
            } catch (SAML2MetaException e) {
                return e.getMessage();
            }
        }

        String[] results = ImportSAML2MetaData.importData(
            null, metadata, extendedData);
        String realm = results[0];
        String entityId = results[1];

        String cot = getString(params, ParameterKeys.P_COT);
        if ((cot != null) && (cot.length() > 0)) {
            try {
                AddProviderToCOT.addToCOT(realm, cot, entityId);
            } catch (COTException e) {
                throw new WorkflowException(e.getMessage());
            }
        }
        try {
            List attrMapping = getAttributeMapping(params);
            if (!attrMapping.isEmpty()) {
                SAML2MetaManager manager = new SAML2MetaManager();
                EntityConfigElement config = 
                    manager.getEntityConfig(realm, entityId);
                IDPSSOConfigElement ssoConfig = 
                    manager.getIDPSSOConfig(realm, entityId);

                Map attribConfig = SAML2MetaUtils.getAttributes(ssoConfig);
                List mappedAttributes = (List)attribConfig.get(
                    SAML2Constants.ATTRIBUTE_MAP);
                mappedAttributes.addAll(attrMapping);
                manager.setEntityConfig(realm, config);
            }
        } catch (SAML2MetaException e) {
            throw new WorkflowException(e.getMessage());
        }
        return getMessage("idp.configured", locale) + "|||realm=" + realm;
    }
    
    private List getAttributeMapping(Map params) {
        List list = new ArrayList();
        String strAttrMapping = getString(params, ParameterKeys.P_ATTR_MAPPING);
        if ((strAttrMapping != null) && (strAttrMapping.length() > 0)) {
            StringTokenizer st = new StringTokenizer(strAttrMapping, "|");
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s.length() > 0) {
                    list.add(s);
                }
            }
        }
        return list;
    }
    
    private String generateMetaAlias(String realm)
        throws WorkflowException {
        try {
            Set metaAliases = new HashSet();
            SAML2MetaManager mgr = new SAML2MetaManager();
            metaAliases.addAll(
                mgr.getAllHostedIdentityProviderMetaAliases(realm));
            metaAliases.addAll(
                mgr.getAllHostedServiceProviderMetaAliases(realm));
            String metaAlias = (realm.equals("/")) ? "/idp" : realm + "/idp";
            int counter = 1;

            while (metaAliases.contains(metaAlias)) {
                metaAlias = metaAlias + Integer.toString(counter);
                counter++;
            }
            return metaAlias;
        } catch (SAML2MetaException e) {
            throw new WorkflowException(e.getMessage());
        }
    }

    private void validateParameters(Map params)
        throws WorkflowException {
        String metadata = getString(params, ParameterKeys.P_META_DATA);
        boolean hasMetaData = (metadata != null) &&
            (metadata.trim().length() > 0);
        String extendedData = getString(params, ParameterKeys.P_EXENDED_DATA);
        boolean hasExtendedData = (extendedData != null) &&
            (extendedData.trim().length() > 0);
        
        if ((hasMetaData && !hasExtendedData) ||
            (!hasMetaData && hasExtendedData)
            ) {
            throw new WorkflowException("both-meta-extended-data-required",
                null);
        }
        if ((params.size() == 3) &&
            params.containsKey(ParameterKeys.P_META_DATA) &&
            params.containsKey(ParameterKeys.P_EXENDED_DATA) &&
            !hasMetaData && !hasExtendedData
            ) {
            throw new WorkflowException("both-meta-extended-data-required",
                null);
        }

        String cotname = getString(params, ParameterKeys.P_COT);
        if ((cotname == null) || (cotname.trim().length() == 0)) {
            throw new WorkflowException("missing-cot", null);
        }
            
        if (!hasMetaData && !hasExtendedData) {
            String realm = getString(params, ParameterKeys.P_REALM);
            if ((realm == null) || (realm.trim().length() == 0)) {
                throw new WorkflowException("missing-realm", null);
            }
            
            String entityId = getString(params, ParameterKeys.P_ENTITY_ID);
            if ((entityId == null) || (entityId.trim().length() == 0)) {
                throw new WorkflowException("missing-entity-id", null);
            }
        }
    }
}

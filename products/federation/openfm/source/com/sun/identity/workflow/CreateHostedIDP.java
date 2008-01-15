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
 * $Id: CreateHostedIDP.java,v 1.1 2008-01-15 06:44:20 veiming Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.workflow;

import com.sun.identity.cot.COTException;
import com.sun.identity.saml2.meta.SAML2MetaException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
            metadata = getFileContent(metadataFile);
            extendedData = getFileContent(extendedDataFile);
        } else {
            String entityId = getString(params, ParameterKeys.P_ENTITY_ID);
            String metaAlias = getString(params, ParameterKeys.P_META_ALIAS);
            Map map = new HashMap();
            map.put(CreateSAML2HostedProviderTemplate.P_IDP, metaAlias);
            map.put(CreateSAML2HostedProviderTemplate.P_IDP_E_CERT,
                getString(params, ParameterKeys.P_IDP_E_CERT));
            map.put(CreateSAML2HostedProviderTemplate.P_IDP_S_CERT,
                getString(params, ParameterKeys.P_IDP_S_CERT));
                
            try {
                metadata =
                    CreateSAML2HostedProviderTemplate.buildMetaDataTemplate(
                    entityId, map);
                extendedData =
                   CreateSAML2HostedProviderTemplate.createExtendedDataTemplate(
                    entityId, map);
            } catch (SAML2MetaException e) {
                return e.getMessage();
            }
        }
            
        String[] results = ImportSAML2MetaData.importData(null, metadata,
            extendedData);
        String realm = results[0];
            
        String cot = getString(params, ParameterKeys.P_COT);
        if ((cot != null) && (cot.length() > 0)) {
            try {
                String entityId = results[1];
                AddProviderToCOT.addToCOT(realm, cot, entityId);
            } catch (COTException e) {
                throw new WorkflowException(e.getMessage());
            }
        }
        return getMessage("idp.configured", locale) + "|||realm=" + realm;
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
        if ((params.size() == 2) &&
            params.containsKey(ParameterKeys.P_META_DATA) &&
            params.containsKey(ParameterKeys.P_EXENDED_DATA) &&
            !hasMetaData && !hasExtendedData
            ) {
            throw new WorkflowException("both-meta-extended-data-required",
                null);
        }
        
        if (!hasMetaData && !hasExtendedData) {
            String realm = getString(params, ParameterKeys.P_REALM);
            if ((realm == null) || (realm.trim().length() == 0)) {
                throw new WorkflowException("missing-realm", null);
            }
            
            String metaAlias = getString(params, ParameterKeys.P_META_ALIAS);
            if ((metaAlias == null) || (metaAlias.trim().length() == 0)) {
                throw new WorkflowException("missing-metaalias", null);
            } else {
                String metaalias = metaAlias;
                if (realm.equals("/")) {
                    if (!metaalias.startsWith("/")) {
                        Object[] param = {metaalias};
                        throw new WorkflowException(
                            "metaalias-no-prefix-with-realm", param);
                    }
                    metaalias = metaalias.substring(1);
                } else {
                    if (!metaalias.startsWith(realm + "/")) {
                        Object[] param = {metaalias};
                        throw new WorkflowException(
                            "metaalias-no-prefix-with-realm", param);
                    }
                    metaalias = metaalias.substring(realm.length() +1);
                }
                
                if (metaalias.indexOf("/") != -1) {
                    Object[] param = {metaalias};
                    throw new WorkflowException(
                        "invalid-metaalias-slash", param);
                }
            }
            
            String entityId = getString(params, ParameterKeys.P_ENTITY_ID);
            if ((entityId == null) || (entityId.trim().length() == 0)) {
                throw new WorkflowException("missing-entity-id", null);
            }
            
            String cotname = getString(params, ParameterKeys.P_COT);
            if ((cotname == null) || (cotname.trim().length() == 0)) {
                throw new WorkflowException("missing-cot", null);
            }
            
        }
    }
}

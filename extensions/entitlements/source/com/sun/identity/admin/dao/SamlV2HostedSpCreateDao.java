/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: SamlV2HostedSpCreateDao.java,v 1.2 2009-06-20 08:41:58 asyhuang Exp $
 */
package com.sun.identity.admin.dao;

import com.sun.identity.cot.COTException;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.workflow.AddProviderToCOT;
import com.sun.identity.workflow.CreateSAML2HostedProviderTemplate;
import com.sun.identity.workflow.ImportSAML2MetaData;
import com.sun.identity.workflow.MetaTemplateParameters;
import com.sun.identity.workflow.WorkflowException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SamlV2HostedSpCreateDao
        implements Serializable {

    public SamlV2HostedSpCreateDao() {
    }

    public void createSamlv2HostedSp(
            String realm,
            String entityId,
            String cot,
            boolean defAttrMappings) {

        String metadata = null;
        String extendedData = null;

        String metaAlias = generateMetaAliasForSP(realm);

        Map map = new HashMap();
        map.put(MetaTemplateParameters.P_SP, metaAlias);

        try {
            metadata =
                    CreateSAML2HostedProviderTemplate.buildMetaDataTemplate(
                    entityId, map, ""); // TODO getRequestURL(params)?? need to handle LB case. have to use request url to create metadata.
            extendedData =
                    CreateSAML2HostedProviderTemplate.createExtendedDataTemplate(
                    entityId, map, ""); //TODO getRequestURL(params)?? need to handle LB case. have to use request url to create metadata.
        } catch (SAML2MetaException e) {
            throw new RuntimeException(e);
        }

        try {
            String[] results = ImportSAML2MetaData.importData(realm, metadata, extendedData);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        if ((cot != null) && (cot.length() > 0)) {
            try {
                AddProviderToCOT.addToCOT(realm, cot, entityId);
            } catch (COTException e) {
                throw new RuntimeException(e);
            }
        }
        if (defAttrMappings) {
            addAttributeMappingSetting(realm, entityId);
        }
 
    }

    public void importSamlv2HostedSp(
            String cot,
            String stdMetadata,
            String extMetadata,
            boolean defAttrMappings) {
        String realm = null;
        String entityId = null;

        try {
            String[] results = ImportSAML2MetaData.importData(null, stdMetadata, extMetadata);
            realm = results[0];
            entityId = results[1];
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        if ((cot != null) && (cot.length() > 0)) {
            try {
                AddProviderToCOT.addToCOT(realm, cot, entityId);
            } catch (COTException e) {
                throw new RuntimeException(e);
            }
        }
        if (defAttrMappings) {
            addAttributeMappingSetting(realm, entityId);
        }

    }

    private void addAttributeMappingSetting(String realm, String entityId) {
        List attrMapping = new ArrayList(1);
        attrMapping.add("*=*");

        try {
            SAML2MetaManager manager = new SAML2MetaManager();
            EntityConfigElement config = manager.getEntityConfig(
                    realm, entityId);
            SPSSOConfigElement ssoConfig = manager.getSPSSOConfig(
                    realm, entityId);
            Map attribConfig = SAML2MetaUtils.getAttributes(ssoConfig);
            List mappedAttributes = (List) attribConfig.get(
                    SAML2Constants.ATTRIBUTE_MAP);
            mappedAttributes.addAll(attrMapping);
            manager.setEntityConfig(realm, config);
        } catch (SAML2MetaException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateMetaAliasForSP(String realm) {

        try {
            Set metaAliases = new HashSet();
            SAML2MetaManager mgr = new SAML2MetaManager();
            metaAliases.addAll(
                    mgr.getAllHostedIdentityProviderMetaAliases(realm));
            metaAliases.addAll(
                    mgr.getAllHostedServiceProviderMetaAliases(realm));
            String metaAliasBase = (realm.equals("/")) ? "/sp" : realm + "/sp";
            String metaAlias = metaAliasBase;
            int counter = 1;

            while (metaAliases.contains(metaAlias)) {
                metaAlias = metaAliasBase + Integer.toString(counter);
                counter++;
            }
            return metaAlias;
        } catch (SAML2MetaException e) {
            throw new RuntimeException(e);
        }
    }
}


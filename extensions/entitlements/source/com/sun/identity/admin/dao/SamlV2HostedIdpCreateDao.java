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
 * $Id: SamlV2HostedIdpCreateDao.java,v 1.4 2009-07-02 17:44:14 asyhuang Exp $
 */
package com.sun.identity.admin.dao;

import com.iplanet.am.util.SystemProperties;
import com.sun.identity.cot.COTException;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaConstants;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaSecurityUtils;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.shared.Constants;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.workflow.AddProviderToCOT;
import com.sun.identity.workflow.CreateSAML2HostedProviderTemplate;
import com.sun.identity.workflow.ImportSAML2MetaData;
import com.sun.identity.workflow.MetaTemplateParameters;
import com.sun.identity.workflow.WorkflowException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SamlV2HostedIdpCreateDao
        implements Serializable {

    public SamlV2HostedIdpCreateDao() {
    }

    public void createSamlv2HostedIdp(
            String realm,
            String entityId,
            String cot,
            String key,
            List attrMapping) {

        String metadata = null;
        String extendedData = null;

        String metaAlias = generateMetaAliasForIDP(realm);

        Map map = new HashMap();
        map.put(MetaTemplateParameters.P_IDP, metaAlias);
        map.put(MetaTemplateParameters.P_IDP_S_CERT, key);

        try {
            metadata =
                    CreateSAML2HostedProviderTemplate.buildMetaDataTemplate(
                    entityId, map, getRequestURL());
            extendedData =
                    CreateSAML2HostedProviderTemplate.createExtendedDataTemplate(
                    entityId, map, getRequestURL());
        } catch (SAML2MetaException e) {
            throw new RuntimeException(e);
        }

        try {
            String[] results = ImportSAML2MetaData.importData(null, metadata, extendedData);
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

        if (!attrMapping.isEmpty()) {
            addAttributeMapping(realm, entityId, attrMapping);
        }

    }

    public void importSamlv2HostedIdp(
            String cot,
            String stdMetadata,
            String extMetadata,
            List attrMapping) {
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
        if (!attrMapping.isEmpty()) {
            addAttributeMapping(realm, entityId, attrMapping);
        }
    }

    private void addAttributeMapping(String realm, String entityId, List attrMapping) {
        try {

            SAML2MetaManager manager = new SAML2MetaManager();
            EntityConfigElement config =
                    manager.getEntityConfig(realm, entityId);
            IDPSSOConfigElement ssoConfig =
                    manager.getIDPSSOConfig(realm, entityId);

            Map attribConfig = SAML2MetaUtils.getAttributes(ssoConfig);
            List mappedAttributes = (List) attribConfig.get(
                    SAML2Constants.ATTRIBUTE_MAP);
            mappedAttributes.addAll(attrMapping);
            manager.setEntityConfig(realm, config);

        } catch (SAML2MetaException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateMetaAliasForIDP(String realm) {
        try {
            Set metaAliases = new HashSet();
            SAML2MetaManager mgr = new SAML2MetaManager();
            metaAliases.addAll(
                    mgr.getAllHostedIdentityProviderMetaAliases(realm));
            metaAliases.addAll(
                    mgr.getAllHostedServiceProviderMetaAliases(realm));
            String metaAliasBase = (realm.equals("/")) ? "/idp" : realm + "/idp";
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

    private String getRequestURL() {
        boolean isConsoleRemote = Boolean.valueOf(
                SystemProperties.get(Constants.AM_CONSOLE_REMOTE)).booleanValue();
        if (isConsoleRemote) {
            return SystemProperties.getServerInstanceName();
        } else {
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String uri = req.getRequestURI().toString();
            int idx = uri.indexOf('/', 1);
            uri = uri.substring(0, idx);
            return req.getScheme() + "://" + req.getServerName() +
                    ":" + req.getServerPort() + uri;
        }
    }

    public boolean valideEntityName(String entityName) {
        return true;
    }

    public boolean valideaExtendedMataFormat(String extended) {

        EntityConfigElement configElt = null;

        if (extended != null) {
            Object obj;
            try {
                obj = SAML2MetaUtils.convertStringToJAXB(extended);
            } catch (JAXBException ex) {
                return false;
            }
            configElt = (obj instanceof EntityConfigElement) ? (EntityConfigElement) obj : null;
            if (configElt == null) {
                return false;
            }
        }
        return true;

    }

    public boolean validateMetaDataFormat(String metadata) {

        EntityDescriptorElement descriptor = null;
        if (metadata != null) {
            try {
                descriptor = getEntityDescriptorElement(metadata);
            } catch (SAML2MetaException ex) {
                return false;
            } catch (JAXBException ex) {
                return false;
            } catch (WorkflowException ex) {
                return false;
            }
            if (descriptor == null) {
                return false;
            }
        }

        return true;
    }

    static EntityDescriptorElement getEntityDescriptorElement(String metadata)
            throws SAML2MetaException, JAXBException, WorkflowException {
        Debug debug = Debug.getInstance("workflow");
        Document doc = XMLUtils.toDOMDocument(metadata, debug);

        if (doc == null) {
            throw new WorkflowException(
                    "import-entity-exception-invalid-descriptor", null);
        }

        Element docElem = doc.getDocumentElement();

        if ((!SAML2MetaConstants.ENTITY_DESCRIPTOR.equals(
                docElem.getLocalName())) ||
                (!SAML2MetaConstants.NS_METADATA.equals(
                docElem.getNamespaceURI()))) {
            throw new WorkflowException(
                    "import-entity-exception-invalid-descriptor", null);
        }
        SAML2MetaSecurityUtils.verifySignature(doc);
        workaroundAbstractRoleDescriptor(doc);
        Object obj = SAML2MetaUtils.convertNodeToJAXB(doc);

        return (obj instanceof EntityDescriptorElement) ? (EntityDescriptorElement) obj : null;
    }

    private static void workaroundAbstractRoleDescriptor(Document doc) {
        Debug debug = Debug.getInstance("workflow");
        NodeList nl = doc.getDocumentElement().getElementsByTagNameNS(
                SAML2MetaConstants.NS_METADATA, SAML2MetaConstants.ROLE_DESCRIPTOR);
        int length = nl.getLength();
        if (length == 0) {
            return;
        }

        for (int i = 0; i < length; i++) {
            Element child = (Element) nl.item(i);
            String type = child.getAttributeNS(SAML2Constants.NS_XSI, "type");
            if (type != null) {
                if ((type.equals(
                        SAML2MetaConstants.ATTRIBUTE_QUERY_DESCRIPTOR_TYPE)) ||
                        (type.endsWith(":" +
                        SAML2MetaConstants.ATTRIBUTE_QUERY_DESCRIPTOR_TYPE))) {

                    String newTag = type.substring(0, type.length() - 4);

                    String xmlstr = XMLUtils.print(child);
                    int index = xmlstr.indexOf(
                            SAML2MetaConstants.ROLE_DESCRIPTOR);
                    xmlstr = "<" + newTag + xmlstr.substring(index +
                            SAML2MetaConstants.ROLE_DESCRIPTOR.length());
                    if (!xmlstr.endsWith("/>")) {
                        index = xmlstr.lastIndexOf("</");
                        xmlstr = xmlstr.substring(0, index) + "</" + newTag +
                                ">";
                    }

                    Document tmpDoc = XMLUtils.toDOMDocument(xmlstr, debug);
                    Node newChild =
                            doc.importNode(tmpDoc.getDocumentElement(), true);
                    child.getParentNode().replaceChild(newChild, child);
                }
            }
        }
    }
}


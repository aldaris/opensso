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
 * $Id: SamlV2RemoteSpCreateDao.java,v 1.1 2009-06-17 23:45:42 asyhuang Exp $
 */
package com.sun.identity.admin.dao;

import com.sun.identity.cot.COTException;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaConstants;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaSecurityUtils;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.workflow.AddProviderToCOT;
import com.sun.identity.workflow.ImportSAML2MetaData;
import com.sun.identity.workflow.WorkflowException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SamlV2RemoteSpCreateDao
        implements Serializable {

    public SamlV2RemoteSpCreateDao() {
    }

    public void importSamlv2RemoteSp(
            String cot,
            String stdMetadata,
            String extMetadata) {
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
    }

    public void importSamlv2RemoteSp(
            String cot,
            String stdMetadata) {
        String realm = null;
        String entityId = null;

        try {

            EntityDescriptorElement e = null;
            try {
                e = getEntityDescriptorElement(stdMetadata);
            } catch (SAML2MetaException ex) {
                Logger.getLogger(SamlV2RemoteSpCreateDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(SamlV2RemoteSpCreateDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            String eId = e.getEntityID();
            String extMetadata = createExtendedDataTemplate(
                    eId, false);
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
    }

    private EntityDescriptorElement getEntityDescriptorElement(String metadata)
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

    private void workaroundAbstractRoleDescriptor(Document doc) {
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

    private String createExtendedDataTemplate(
            String entityID,
            boolean hosted) {

        StringBuffer buff = new StringBuffer();
        String strRemote = (hosted) ? "1" : "0";
        buff.append(
                "<EntityConfig xmlns=\"urn:sun:fm:SAML:2.0:entityconfig\"\n" +
                "    xmlns:fm=\"urn:sun:fm:SAML:2.0:entityconfig\"\n" +
                "    hosted=\"" + strRemote + "\"\n" +
                "    entityID=\"" + entityID + "\">\n\n" +
                "    <SPSSOConfig>\n" +
                "    </SPSSOConfig>\n" +
                "</EntityConfig>\n");
        return buff.toString();
    }
}


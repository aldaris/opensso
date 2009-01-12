/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: ExportSAML2MetaData.java,v 1.3 2009-01-12 05:33:45 hengming Exp $
 *
 */

package com.sun.identity.workflow;

import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaConstants;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaSecurityUtils;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.shared.xml.XMLUtils;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import org.w3c.dom.Document;

/**
 * Export SAML2 Metadata.
 */
public class ExportSAML2MetaData {

    private ExportSAML2MetaData() {
    }

    public static String exportStandardMeta(String realm, String entityID,
        boolean sign) 
        throws WorkflowException {

        try {
            SAML2MetaManager metaManager = new SAML2MetaManager();
            EntityDescriptorElement descriptor =
                metaManager.getEntityDescriptor(realm, entityID);

            String xmlstr = null;
            if (descriptor == null) {
                return null;
            }

            if (sign) {
                SPSSOConfigElement spConfig = metaManager.getSPSSOConfig(
                    realm, entityID);
                IDPSSOConfigElement idpConfig = metaManager.getIDPSSOConfig(
                    realm, entityID);
                Document doc = SAML2MetaSecurityUtils.sign(descriptor,
                    spConfig, idpConfig);
                if (doc != null) {
                    xmlstr = XMLUtils.print(doc);
                }
            }
            if (xmlstr == null) {
                xmlstr = SAML2MetaUtils.convertJAXBToString(descriptor);
                xmlstr = SAML2MetaSecurityUtils.formatBase64BinaryElement(
                    xmlstr);
            }
            xmlstr = workaroundAbstractRoleDescriptor(xmlstr);
            return xmlstr;
        } catch (SAML2MetaException e) {
            throw new WorkflowException(e.getMessage());
        } catch (JAXBException e) {
            throw new WorkflowException(e.getMessage());
        }
    }
    
    public static String exportExtendedMeta(String realm, String entityID) 
        throws WorkflowException {
        try {
            String result = null;
            SAML2MetaManager metaManager = new SAML2MetaManager();
            EntityConfigElement config = metaManager.getEntityConfig(
                realm, entityID);
            if (config != null) {
                OutputStream os = new ByteArrayOutputStream();
                SAML2MetaUtils.convertJAXBToOutputStream(config, os);
                result = os.toString();
            }
            return result;
        } catch (JAXBException e) {
            throw new WorkflowException(e.getMessage());
        } catch (SAML2MetaException e) {
            throw new WorkflowException(e.getMessage());
        }
    }
    
    private static String workaroundAbstractRoleDescriptor(String xmlstr) {
        int index =
            xmlstr.indexOf(":" +SAML2MetaConstants.ATTRIBUTE_QUERY_DESCRIPTOR);
        if (index == -1) {
            return xmlstr;
        }

        int index2 = xmlstr.lastIndexOf("<", index);
        if (index2 == -1) {
            return xmlstr;
        }

        String prefix = xmlstr.substring(index2 + 1, index);
        String type =  prefix + ":" +
            SAML2MetaConstants.ATTRIBUTE_QUERY_DESCRIPTOR_TYPE;

        xmlstr = xmlstr.replaceAll("<" + prefix + ":" +
            SAML2MetaConstants.ATTRIBUTE_QUERY_DESCRIPTOR,
            "<" + SAML2MetaConstants.ROLE_DESCRIPTOR + " " +
            SAML2Constants.XSI_DECLARE_STR + " xsi:type=\"" + type + "\"");
        xmlstr = xmlstr.replaceAll("</" + prefix + ":" +
            SAML2MetaConstants.ATTRIBUTE_QUERY_DESCRIPTOR,
            "</" + SAML2MetaConstants.ROLE_DESCRIPTOR);
        return xmlstr;
    }
}

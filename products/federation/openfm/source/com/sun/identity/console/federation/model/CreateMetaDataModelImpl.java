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
 * $Id: CreateMetaDataModelImpl.java,v 1.1 2008-04-11 00:10:12 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.federation.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.federation.meta.IDFFMetaException;
import com.sun.identity.federation.meta.IDFFMetaManager;
import com.sun.identity.federation.meta.IDFFMetaUtils;
import com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.workflow.CreateIDFFMetaDataTemplate;
import com.sun.identity.workflow.CreateSAML2HostedProviderTemplate;
import com.sun.identity.workflow.CreateWSFedMetaDataTemplate;
import com.sun.identity.workflow.ImportSAML2MetaData;
import com.sun.identity.workflow.WorkflowException;
import com.sun.identity.wsfederation.common.WSFederationConstants;
import com.sun.identity.wsfederation.jaxb.entityconfig.FederationConfigElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import com.sun.identity.wsfederation.meta.WSFederationMetaException;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.meta.WSFederationMetaUtils;
import java.security.cert.CertificateEncodingException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

public class CreateMetaDataModelImpl extends AMModelBase
    implements CreateMetaDataModel 
{
    /**
     * Creates a simple model using default resource bundle.
     *
     * @param req HTTP Servlet Request
     * @param map of user information
     */
    public CreateMetaDataModelImpl(HttpServletRequest req,  Map map) {
        super(req, map);
    }

    /**
     * Creates a SAMLv2 provider.
     *
     * @param realm Realm Name.
     * @param entityId Entity Id.
     * @param values   Map of property name to values.
     */
    public void createSAMLv2Provider(String realm, String entityId, Map values)
        throws AMConsoleException {
        try {
            String metadata = CreateSAML2HostedProviderTemplate.
                buildMetaDataTemplate(entityId, values);
            String extendedData = CreateSAML2HostedProviderTemplate.
                createExtendedDataTemplate(entityId, values);
            ImportSAML2MetaData.importData(realm, metadata, extendedData);
        } catch (WorkflowException ex) {
            throw new AMConsoleException(ex.getMessage());
        } catch (SAML2MetaException ex) {
            throw new AMConsoleException(ex.getMessage());
        }
    }

    /**
     * Creates a IDFF provider.
     *
     * @param realm Realm Name.
     * @param entityId Entity Id.
     * @param values   Map of property name to values.
     */
    public void createIDFFProvider(String realm, String entityId, Map values)
        throws AMConsoleException {
        try {
            IDFFMetaManager metaManager = new IDFFMetaManager(
                getUserSSOToken());
            String metadata = CreateIDFFMetaDataTemplate.
                createStandardMetaTemplate(entityId, values);
            String extendedData = CreateIDFFMetaDataTemplate.
                createExtendedMetaTemplate(entityId, values);
            EntityDescriptorElement descriptor = (EntityDescriptorElement)
                IDFFMetaUtils.convertStringToJAXB(metadata);
            EntityConfigElement configElt = (EntityConfigElement)
                IDFFMetaUtils.convertStringToJAXB(extendedData);
            metaManager.createEntityDescriptor(realm, descriptor);
            metaManager.createEntityConfig(realm, configElt);
        } catch (JAXBException ex) {
            throw new AMConsoleException(ex.getMessage());
        } catch (IDFFMetaException ex) {
            throw new AMConsoleException(ex.getMessage());
        }
    }

    /**
     * Creates a WS Federation provider.
     *
     * @param realm Realm Name.
     * @param entityId Entity Id.
     * @param values   Map of property name to values.
     */
    public void createWSFedProvider(String realm, String entityId, Map values)
        throws AMConsoleException {
        try {
            String metadata = 
                CreateWSFedMetaDataTemplate.createStandardMetaTemplate(
                entityId, values);
            String extendedData = 
                CreateWSFedMetaDataTemplate.createExtendedMetaTemplate(
                entityId, values);
        
            FederationElement elt = (FederationElement) 
                WSFederationMetaUtils.convertStringToJAXB(metadata);
            String federationID = elt.getFederationID();
            if (federationID == null) {
                federationID = WSFederationConstants.DEFAULT_FEDERATION_ID;
            }
            WSFederationMetaManager.createFederation(realm, elt);
            
            FederationConfigElement cfg = (FederationConfigElement)
                WSFederationMetaUtils.convertStringToJAXB(extendedData);
            WSFederationMetaManager.createEntityConfig(realm, cfg);
        } catch (WSFederationMetaException ex) {
            throw new AMConsoleException(ex.getMessage());
        } catch (JAXBException ex) {
            throw new AMConsoleException(ex.getMessage());
        } catch (CertificateEncodingException ex) {
            throw new AMConsoleException(ex.getMessage());
        }
    }
}

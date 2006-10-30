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
 * $Id: ExportMetaData.java,v 1.1 2006-10-30 23:18:00 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.federation.cli;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.federation.jaxb.entityconfig.IDPDescriptorConfigElement;
import com.sun.identity.federation.jaxb.entityconfig.SPDescriptorConfigElement;
import com.sun.identity.federation.meta.IDFFMetaException;
import com.sun.identity.federation.meta.IDFFMetaManager;
import com.sun.identity.federation.meta.IDFFMetaUtils;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaSecurityUtils;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import javax.xml.bind.JAXBException;
import org.w3c.dom.Document;

/**
 * Export Meta Data.
 */
public class ExportMetaData extends AuthenticatedCommand {
    private static Debug debug = SAML2MetaUtils.debug;
    private static final String ARGUMENT_REALM = "realm";
    private static final String ARGUMENT_ENTITY_ID = "entityid";
    private static final String ARGUMENT_SIGN = "sign";
    private static final String ARGUMENT_METADATA = "metadata";
    private static final String ARGUMENT_EXTENDED_DATA = "extended";
    
    private String realm;
    private String entityID;
    private boolean sign;
    private String metadata;
    private String extendedData;

    /**
     * Exports Meta Data.
     *
     * @param rc Request Context.
     * @throws CLIException if unable to process this request.
     */
    public void handleRequest(RequestContext rc) 
        throws CLIException {
        super.handleRequest(rc);
        ldapLogin();
        realm = getStringOptionValue(ARGUMENT_REALM, "/");
        entityID = getStringOptionValue(ARGUMENT_ENTITY_ID);
        sign = isOptionSet(ARGUMENT_SIGN);
        metadata = getStringOptionValue(ARGUMENT_METADATA);
        extendedData = getStringOptionValue(ARGUMENT_EXTENDED_DATA);

        if ((metadata == null) && (extendedData == null)) {
            throw new CLIException(
                getResourceString("export-entity-exception-no-datafile"),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
        
        String spec = FederationManager.getIDFFSubCommandSpecification(rc);
        if (spec.equals(FederationManager.DEFAULT_SPECIFICATION)) {
            handleSAML2Request(rc);
        } else if (spec.equals(FederationManager.IDFF_SPECIFICATION)) {
            handleIDFFRequest(rc);
        } else {
            throw new CLIException(
                getResourceString("unsupported-specification"),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
    
    private void handleSAML2Request(RequestContext rc)
        throws CLIException {        
        if (metadata != null) {
            if (sign) {
                runExportMetaSign();
            } else {
                runExportMeta();
            }
        }
        
        if (extendedData != null) {
            runExportExtended();
        }
    }
    
    private void handleIDFFRequest(RequestContext rc)
        throws CLIException {   
        if (metadata != null) {
            if (sign) {
                runIDFFExportMetaSign();
            } else {
                runIDFFExportMeta();
            }
        }
        
        if (extendedData != null) {
            runIDFFExportExtended();
        }
    }

    private void runExportMetaSign()
        throws CLIException
    {
        PrintWriter pw = null;
        Object[] objs = {metadata};

        try {
            SAML2MetaManager metaManager = new SAML2MetaManager();
            EntityDescriptorElement descriptor =
                metaManager.getEntityDescriptor(realm, entityID);
            
            if (descriptor == null) {
                Object[] objs2 = {entityID, realm};
                throw new CLIException(MessageFormat.format(
                    getResourceString(
                        "export-entity-exception-entity-descriptor-not-exist"),
                    objs2), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
            
            SPSSOConfigElement spConfig = metaManager.getSPSSOConfig(
                realm, entityID);
            IDPSSOConfigElement idpConfig = metaManager.getIDPSSOConfig(
                realm, entityID);
            Document doc = SAML2MetaSecurityUtils.sign(
                descriptor, spConfig, idpConfig);
            if (doc == null) {
                runExportMeta();
                return;
            } else {
                String xmlstr = XMLUtils.print(doc);
                pw = new PrintWriter(new FileWriter(metadata));
                pw.print(xmlstr);
                getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString(
                        "export-entity-export-descriptor-succeeded"), objs));
            }
        } catch (SAML2MetaException e) {
            debug.error("ExportMetaData.runExportMetaSign", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException jaxbe) {
            Object[] objs3 = {entityID, realm};
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "export-entity-exception-invalid_descriptor"), objs3),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IOException e) {
            debug.error("ExportMetaData.runExportMetaSign", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (pw !=null ) {
                pw.close();
            }
        }
    }

    private void runIDFFExportMetaSign()
        throws CLIException
    {
        PrintWriter pw = null;
        Object[] objs = {metadata};

        try {
            IDFFMetaManager metaManager = new IDFFMetaManager(
                getAdminSSOToken());
            com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement
                descriptor = metaManager.getEntityDescriptor(entityID);
            
            if (descriptor == null) {
                Object[] objs2 = {entityID, realm};
                throw new CLIException(MessageFormat.format(
                    getResourceString(
                        "export-entity-exception-entity-descriptor-not-exist"),
                    objs2), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
            
            SPDescriptorConfigElement spConfig = 
                metaManager.getSPDescriptorConfig(entityID);
            IDPDescriptorConfigElement idpConfig = 
                metaManager.getIDPDescriptorConfig(entityID);

            Document doc = null;
/*
 * TODO: Signing
 * Document doc = SAML2MetaSecurityUtils.sign(
                descriptor, spConfig, idpConfig);
 */
            if (doc == null) {
                runExportMeta();
                return;
            } else {
                String xmlstr = XMLUtils.print(doc);
                pw = new PrintWriter(new FileWriter(metadata));
                pw.print(xmlstr);
                getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString(
                        "export-entity-export-descriptor-succeeded"), objs));
            }
        } catch (IDFFMetaException e) {
            debug.error("ExportMetaData.runIDFFExportMetaSign", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);

        } catch (IOException e) {
            debug.error("ExportMetaData.runIDFFExportMetaSign", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (pw !=null ) {
                pw.close();
            }
        }
    }

    private void runExportMeta() 
        throws CLIException
    {
        PrintWriter pw = null;
        Object[] objs = {metadata};
        Object[] objs2 = {entityID, realm};
        
        try {
            SAML2MetaManager metaManager = new SAML2MetaManager();
            EntityDescriptorElement descriptor =
                metaManager.getEntityDescriptor(realm, entityID);
            if (descriptor == null) {
                throw new CLIException(MessageFormat.format(getResourceString(
                    "export-entity-exception-entity-descriptor-not-exist"),
                    objs2), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
            
            String xmlstr = SAML2MetaUtils.convertJAXBToString(descriptor);
            xmlstr = SAML2MetaSecurityUtils.formatBase64BinaryElement(xmlstr);
            pw = new PrintWriter(new FileWriter(metadata));
            pw.print(xmlstr);
            getOutputWriter().printlnMessage(MessageFormat.format(
                getResourceString(
                "export-entity-export-descriptor-succeeded"), objs));
        } catch (SAML2MetaException e) {
            debug.error("ExportMetaData.runExportMeta", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IOException e) {
            debug.error("ExportMetaData.runExportMeta", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException e) {
            debug.warning("ExportMetaData.runExportMeta", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                "export-entity-exception-invalid_descriptor"), objs2),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IllegalArgumentException e) {
            debug.warning("ExportMetaData.runExportMeta", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                "export-entity-exception-invalid_descriptor"), objs2),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (pw !=null ) {
                pw.close();
            }
        }
    }
    
    private void runIDFFExportMeta() 
        throws CLIException
    {
        PrintWriter pw = null;
        Object[] objs = {metadata};
        Object[] objs2 = {entityID, realm};
        
        try {
            IDFFMetaManager metaManager = new IDFFMetaManager(
                getAdminSSOToken());
            com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement
                descriptor = metaManager.getEntityDescriptor(entityID);
            if (descriptor == null) {
                throw new CLIException(MessageFormat.format(getResourceString(
                    "export-entity-exception-entity-descriptor-not-exist"),
                    objs2), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
            
            String xmlstr = IDFFMetaUtils.convertJAXBToString(descriptor);
            xmlstr = SAML2MetaSecurityUtils.formatBase64BinaryElement(xmlstr);
            pw = new PrintWriter(new FileWriter(metadata));
            pw.print(xmlstr);
            getOutputWriter().printlnMessage(MessageFormat.format(
                getResourceString(
                "export-entity-export-descriptor-succeeded"), objs));
        } catch (IDFFMetaException e) {
            debug.error("ExportMetaData.runIDFFExportMeta", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IOException e) {
            debug.error("ExportMetaData.runIDFFExportMeta", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException e) {
            debug.warning("ExportMetaData.runIDFFExportMeta", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                "export-entity-exception-invalid_descriptor"), objs2),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IllegalArgumentException e) {
            debug.warning("ExportMetaData.runExportMeta", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                "export-entity-exception-invalid_descriptor"), objs2),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (pw !=null ) {
                pw.close();
            }
        }
    }

    private void runExportExtended()
        throws CLIException
    {
        OutputStream os = null;
        Object[] objs = {extendedData};
        Object[] objs2 = {entityID, realm};
        
        try {
            SAML2MetaManager metaManager = new SAML2MetaManager();
            
            EntityConfigElement config =
                metaManager.getEntityConfig(realm, entityID);
            if (config == null) {
                throw new CLIException(MessageFormat.format(getResourceString(
                    "export-entity-exception-entity-config-not-exist"),
                    objs2), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
            
            os = new FileOutputStream(extendedData);
            SAML2MetaUtils.convertJAXBToOutputStream(config, os);
            getOutputWriter().printlnMessage(MessageFormat.format(
                getResourceString(
                "export-entity-export-config-succeeded"), objs));
        } catch (SAML2MetaException e) {
            debug.error("ExportMetaData.runExportExtended", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (FileNotFoundException e) {
            debug.warning("ExportMetaData.runExportExtended", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException e) {
            debug.warning("ExportMetaData.runExportExtended", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IllegalArgumentException e) {
            debug.warning("ExportMetaData.runExportExtended", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                "export-entity-exception-invalid-config"), objs2),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (os !=null ) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private void runIDFFExportExtended()
        throws CLIException {
        OutputStream os = null;
        Object[] objs = {extendedData};
        Object[] objs2 = {entityID, realm};
        
        try {
            IDFFMetaManager metaManager = new IDFFMetaManager(
                getAdminSSOToken());
            com.sun.identity.federation.jaxb.entityconfig.EntityConfigElement
                config = metaManager.getEntityConfig(entityID);
            if (config == null) {
                throw new CLIException(MessageFormat.format(getResourceString(
                    "export-entity-exception-entity-config-not-exist"),
                    objs2), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
            
            os = new FileOutputStream(extendedData);
            String xmlString = IDFFMetaUtils.convertJAXBToString(config);
            os.write(xmlString.getBytes());
            getOutputWriter().printlnMessage(MessageFormat.format(
                getResourceString(
                "export-entity-export-config-succeeded"), objs));
        } catch (IDFFMetaException e) {
            debug.warning("ExportMetaData.runIDFFExportExtended", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IOException e) {
            debug.warning("ExportMetaData.runIDFFExportExtended", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException e) {
            debug.warning("ExportMetaData.runIDFFExportExtended", e);
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IllegalArgumentException e) {
            debug.warning("ExportMetaData.runIDFFExportExtended", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                "export-entity-exception-invalid-config"), objs2),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (os !=null ) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}

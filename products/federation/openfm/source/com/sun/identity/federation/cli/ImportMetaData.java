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
 * $Id: ImportMetaData.java,v 1.3 2007-02-16 02:02:52 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.federation.cli;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.CommandManager;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.federation.meta.IDFFMetaException;
import com.sun.identity.federation.meta.IDFFMetaManager;
import com.sun.identity.federation.meta.IDFFMetaUtils;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.cot.CircleOfTrustManager;
import com.sun.identity.cot.COTException;
import com.sun.identity.cot.COTUtils;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaSecurityUtils;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import javax.xml.bind.JAXBException;
import org.w3c.dom.Document;

/**
 * Import Meta Data.
 */
public class ImportMetaData extends AuthenticatedCommand {
    static Debug debug = SAML2MetaUtils.debug;
    private String metadata;
    private String extendedData;
    private String cot;
    private String realm = "/";
    private String spec;
    private boolean webAccess;

    /**
     * Imports Meta Data.
     *
     * @param rc Request Context.
     * @throws CLIException if unable to process this request.
     */
    public void handleRequest(RequestContext rc) 
        throws CLIException {
        super.handleRequest(rc);
        ldapLogin();
        metadata = getStringOptionValue(FedCLIConstants.ARGUMENT_METADATA);
        extendedData = getStringOptionValue(
            FedCLIConstants.ARGUMENT_EXTENDED_DATA);
        cot = getStringOptionValue(FedCLIConstants.ARGUMENT_COT);

        if ((metadata == null) && (extendedData == null)) {
            throw new CLIException(
                getResourceString("import-entity-exception-no-datafile"),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }

        CommandManager mgr = getCommandManager();
        String url = mgr.getWebEnabledURL();
        webAccess = (url != null) && (url.length() > 0);
        
        spec = FederationManager.getIDFFSubCommandSpecification(rc);
        if (spec.equals(FederationManager.DEFAULT_SPECIFICATION)) {
            handleSAML2Request(rc);
        } else if (spec.equals(FedCLIConstants.IDFF_SPECIFICATION)) {
            handleIDFFRequest(rc);
        } else {
            throw new CLIException(
                getResourceString("unsupported-specification"),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
    
    private void handleSAML2Request(RequestContext rc)
        throws CLIException {
        try {
            SAML2MetaManager metaManager = new SAML2MetaManager();
            String entityID = null;
            
            if (metadata != null) {
                entityID = importMetaData(metaManager);
            }
            if (extendedData != null) {
                importExtendedData(metaManager);
            }

            if ((cot != null) && (cot.length() > 0) &&
                (entityID != null) && (entityID.length() > 0)) {
                CircleOfTrustManager cotManager = new CircleOfTrustManager();
                cotManager.addCircleOfTrustMember(realm, cot, spec, entityID);
            }
        } catch (COTException e) {
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SAML2MetaException e) {
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
    
    private void handleIDFFRequest(RequestContext rc)
        throws CLIException {
        try {
            IDFFMetaManager metaManager = new IDFFMetaManager(
                getAdminSSOToken());
            String entityID = null;
            
            if (metadata != null) {
                entityID = importIDFFMetaData(metaManager);
            }
            if (extendedData != null) {
                importIDFFExtendedData(metaManager, entityID);
            }

            if ((cot != null) && (cot.length() > 0) &&
                (entityID != null) && (entityID.length() > 0)) {
                CircleOfTrustManager cotManager = new CircleOfTrustManager();
                cotManager.addCircleOfTrustMember(realm, cot, spec,entityID);
            }
        } catch (IDFFMetaException e) {
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (COTException e) {
            throw new CLIException(e.getMessage(),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }

    private String importMetaData(SAML2MetaManager metaManager)
        throws SAML2MetaException, CLIException
    {
        InputStream is = null;
        String out = (webAccess) ? "web" : metadata;
        Object[] objs = { out };
        String entityID = null;
        
        try {
            Object obj;
            Document doc;
            if (webAccess) {
                obj = SAML2MetaUtils.convertStringToJAXB(metadata);
                doc = XMLUtils.toDOMDocument(metadata, debug);
            } else {
                is = new FileInputStream(metadata);
                doc = XMLUtils.toDOMDocument(is, debug);
                obj = SAML2MetaUtils.convertNodeToJAXB(doc);
            }

            if (obj instanceof EntityDescriptorElement) {
                EntityDescriptorElement descriptor =
                    (EntityDescriptorElement)obj;
                entityID = descriptor.getEntityID();
                SAML2MetaSecurityUtils.verifySignature(doc);
                metaManager.createEntityDescriptor(realm, descriptor);
                getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString("import-entity-succeeded"), objs));
            }
            return entityID;
        } catch (FileNotFoundException e) {
            throw new CLIException(MessageFormat.format(
                getResourceString("file-not-found"), objs),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException e) {
            debug.message("ImportMetaData.importMetaData", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "import-entity-exception-invalid-descriptor-file"),
                objs), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IllegalArgumentException e) {
            debug.message("ImportMetaData.importMetaData", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "import-entity-exception-invalid-descriptor-file"),
                objs), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (is !=null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    //do not if the file cannot be closed.
                }
            }
        }
    }

    private String importIDFFMetaData(IDFFMetaManager metaManager)
        throws IDFFMetaException, CLIException
    {
        InputStream is = null;
        String out = (webAccess) ? "web" : metadata;
        Object[] objs = { out };
        String entityID = null;
        
        try {
            Object obj;
            if (webAccess) {
                obj = IDFFMetaUtils.convertStringToJAXB(metadata);
            } else {
                is = new FileInputStream(metadata);
                Document doc = XMLUtils.toDOMDocument(is, debug);
                obj = IDFFMetaUtils.convertNodeToJAXB(doc);
            }

            if (obj instanceof
                com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement) {
                com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement
                    descriptor =
                 (com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement)
                    obj;
                entityID = descriptor.getProviderID();
                //TODO: signature
                //SAML2MetaSecurityUtils.verifySignature(doc);
                //
                metaManager.createEntityDescriptor(descriptor);
                getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString("import-entity-succeeded"), objs));
            }
            return entityID;
        } catch (FileNotFoundException e) {
            throw new CLIException(MessageFormat.format(
                getResourceString("file-not-found"), objs),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException e) {
            debug.message("ImportMetaData.importMetaData", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "import-entity-exception-invalid-descriptor-file"),
                objs), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IllegalArgumentException e) {
            debug.message("ImportMetaData.importMetaData", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "import-entity-exception-invalid-descriptor-file"),
                objs), ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (is !=null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    //do not if the file cannot be closed.
                }
            }
        }
    }
   
    private void importExtendedData(SAML2MetaManager metaManager)
        throws SAML2MetaException, CLIException
    {
        String out = (webAccess) ? "web" : extendedData;
        Object[] objs = { out };
        InputStream is = null;

        try {
            Object obj = null;
            if (webAccess) {
                obj = SAML2MetaUtils.convertStringToJAXB(extendedData);
            } else {
                is = new FileInputStream(extendedData);
                obj = SAML2MetaUtils.convertInputStreamToJAXB(is);
            }

            if (obj instanceof EntityConfigElement) {
                metaManager.createEntityConfig(realm,
                    (EntityConfigElement)obj);
                getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString("import-entity-succeeded"), objs));
            }
        } catch (FileNotFoundException e) {
            throw new CLIException(MessageFormat.format(
                getResourceString("file-not-found"), objs),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException e) {
            debug.message("ImportMetaData.importExtendedData", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "import-entity-exception-invalid-config-file"), objs),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IllegalArgumentException e) {
            debug.message("ImportMetaData.importExtendedData", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "import-entity-exception-invalid-config-file"), objs),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (is !=null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    //do not if the file cannot be closed.
                }
            }
        }
    }

    private void importIDFFExtendedData(
        IDFFMetaManager metaManager,
        String entityID
    ) throws IDFFMetaException, CLIException {
        String out = (webAccess) ? "web" : extendedData;
        Object[] objs = { out };

        try {
            Object obj;

            if (webAccess) {
                obj = IDFFMetaUtils.convertStringToJAXB(extendedData);
            } else {
                obj = IDFFMetaUtils.convertStringToJAXB(
                    getFileContent(extendedData));
            }

            if (obj instanceof 
               com.sun.identity.federation.jaxb.entityconfig.EntityConfigElement
            ) {
                metaManager.createEntityConfig(
             (com.sun.identity.federation.jaxb.entityconfig.EntityConfigElement)
                    obj);
                getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString("import-entity-succeeded"), objs));
            }
        } catch (IOException e) {
            throw new CLIException(MessageFormat.format(
                getResourceString("file-not-found"), objs),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (JAXBException e) {
            debug.message("ImportMetaData.importExtendedData", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "import-entity-exception-invalid-config-file"), objs),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (IllegalArgumentException e) {
            debug.message("ImportMetaData.importExtendedData", e);
            throw new CLIException(MessageFormat.format(
                getResourceString(
                    "import-entity-exception-invalid-config-file"), objs),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }

    private static String getFileContent(String fileName)
        throws IOException {
        BufferedReader br = null;
        StringBuffer buff = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while (line != null) {
                buff.append(line).append("\n");
                line = br.readLine();
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return buff.toString();
    }

}

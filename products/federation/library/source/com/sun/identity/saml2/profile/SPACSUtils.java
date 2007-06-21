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
 * $Id: SPACSUtils.java,v 1.6 2007-06-21 23:01:41 superpat7 Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.saml2.profile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.identity.common.SystemConfigurationUtil;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.shared.encode.Base64;

import com.sun.identity.saml.common.SAMLConstants;
import com.sun.identity.saml.xmlsig.KeyProvider;
import com.sun.identity.saml2.assertion.Advice;
import com.sun.identity.saml2.assertion.AssertionFactory;
import com.sun.identity.saml2.assertion.Issuer;
import com.sun.identity.saml2.assertion.Assertion;
import com.sun.identity.saml2.assertion.AttributeStatement;
import com.sun.identity.saml2.assertion.NameID;
import com.sun.identity.saml2.assertion.EncryptedID;
import com.sun.identity.saml2.assertion.EncryptedAttribute;

import com.sun.identity.saml2.common.AccountUtils;
import com.sun.identity.saml2.common.NameIDInfo;
import com.sun.identity.saml2.common.NameIDInfoKey;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.jaxb.metadata.ArtifactResolutionServiceElement;
import com.sun.identity.saml2.jaxb.metadata.IDPSSODescriptorElement;
import com.sun.identity.saml2.key.KeyUtil;
import com.sun.identity.saml2.logging.LogUtil;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.protocol.Artifact;
import com.sun.identity.saml2.protocol.ArtifactResolve;
import com.sun.identity.saml2.protocol.ArtifactResponse;
import com.sun.identity.saml2.protocol.ProtocolFactory;
import com.sun.identity.saml2.protocol.Response;
import com.sun.identity.saml2.protocol.Status;
import com.sun.identity.saml2.plugins.SPAccountMapper;
import com.sun.identity.saml2.plugins.SPAttributeMapper;

import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionProvider;

import java.security.PrivateKey;

/**
 * This class is the work engine for SP Assertion Consumer Service.
 */
public class SPACSUtils {

    private SPACSUtils() {}

    /**
     * Retrieves <code>SAML</code> <code>Response</code> from http request.
     * It handles three cases:
     * <pre>
     * 1. using http method get using request parameter "resID".
     *    This is the case after local login is done.
     * 2. using http method get using request parameter "SAMLart".
     *    This is the case for artifact profile.
     * 3. using http method post. This is the case for post profile.
     * </pre>
     * 
     * @param request http servlet request
     * @param response http servlet response
     * @param orgName realm or organization name the service provider resides in
     * @param hostEntityId Entity ID of the hosted service provider
     * @param metaManager <code>SAML2MetaManager</code> instance.
     * @return <code>ResponseInfo</code> instance.
     * @throws SAML2Exception,IOException if it fails in the process.
     */
    public static ResponseInfo getResponse(
                                HttpServletRequest request,
                                HttpServletResponse response,
                                String orgName,
                                String hostEntityId,
                                SAML2MetaManager metaManager)
                throws SAML2Exception,IOException
    {
        ResponseInfo respInfo = null;
        String method = request.getMethod();
        if (method.equals("GET")) {
            respInfo = getResponseFromGet(request, response, orgName,
                                hostEntityId, metaManager);
        } else if (method.equals("POST")) {
            respInfo = new ResponseInfo(getResponseFromPost(request, response),
                                true);
        } else {
            // not supported
            response.sendError(response.SC_METHOD_NOT_ALLOWED,
                        SAML2Utils.bundle.getString("notSupportedHTTPMethod"));
            throw new SAML2Exception(
                        SAML2Utils.bundle.getString("notSupportedHTTPMethod"));
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPACSUtils.getResponse: got response="
                + respInfo.getResponse().toXMLString(true, true));
        }
        return respInfo;
    }

    /**
     * Retrieves <code>SAML Response</code> from http Get. 
     * It first uses parameter resID to retrieve <code>Response</code>. This is
     * the case after local login;
     * If resID is not defined, it then uses <code>SAMLart</code> http 
     * parameter to retrieve <code>Response</code>.
     */
    private static ResponseInfo getResponseFromGet(
                                HttpServletRequest request,
                                HttpServletResponse response,
                                String orgName,
                                String hostEntityId,
                                SAML2MetaManager metaManager)
                throws SAML2Exception,IOException
    {
        ResponseInfo respInfo = null;
        String resID = request.getParameter("resID");
        if (resID != null && resID.length() != 0) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("SPACSUtils.getResponseFromGet: resID="
                        + resID);
            }
            synchronized (SPCache.responseHash) {
                respInfo = (ResponseInfo) SPCache.responseHash.remove(resID);
            }
            if (respInfo == null) {
                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message("SPACSUtils.getResponseFromGet: "
                        + "couldn't find Response from resID.");
                }
                String[] data = {resID};
                LogUtil.error(Level.INFO,
                                LogUtil.RESPONSE_NOT_FOUND_FROM_CACHE,
                                data,
                                null);
                response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        SAML2Utils.bundle.getString("SSOFailed"));
                throw new SAML2Exception(
                        SAML2Utils.bundle.getString("SSOFailed"));
            }
            return respInfo;
        }

        String samlArt = request.getParameter(SAML2Constants.SAML_ART);
        if (samlArt == null || samlArt.trim().length() == 0) {
            SAML2Utils.debug.error("SPACSUtils.getResponseFromGet: Artifact "
                + "string is empty.");
            LogUtil.error(Level.INFO,
                        LogUtil.MISSING_ARTIFACT,
                        null,
                        null);
            response.sendError(response.SC_BAD_REQUEST,
                        SAML2Utils.bundle.getString("missingArtifact"));
            throw new SAML2Exception(
                        SAML2Utils.bundle.getString("missingArtifact"));
        }

        // Try to get source ID and endpointIndex, and then
        // decide which IDP and which artifact resolution service

        Artifact art = null;
        try {
            art = ProtocolFactory.getInstance().createArtifact(samlArt.trim());
            String[] data = {samlArt.trim()};
            LogUtil.access(Level.INFO,
                        LogUtil.RECEIVED_ARTIFACT,
                        data,
                        null);
        } catch (SAML2Exception se) {
            SAML2Utils.debug.error("SPACSUtils.getResponseFromGet: "
                 + "Unable to decode and parse artifact string:" + samlArt);
            response.sendError(response.SC_BAD_REQUEST,
                        SAML2Utils.bundle.getString("errorObtainArtifact"));
            throw se;
        }

        String idpEntityID = getIDPEntityID(
                                art, response, orgName, metaManager);
        IDPSSODescriptorElement idp = null;
        try {
            idp = metaManager.getIDPSSODescriptor(orgName, idpEntityID);
        } catch (SAML2MetaException se) {
            String[] data = {orgName, idpEntityID};
            LogUtil.error(Level.INFO,
                        LogUtil.IDP_META_NOT_FOUND,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                    se.getMessage());
            throw se;
        }

        String location = getIDPArtifactResolutionServiceUrl(
                        art.getEndpointIndex(),
                        idpEntityID,
                        idp,
                        response);

        return new ResponseInfo(
                getResponseFromArtifact(art, location, hostEntityId,
                        idpEntityID, idp, response, orgName, metaManager),
                false);
    }

    // Retrieves response using artifact profile.
    private static Response getResponseFromArtifact(Artifact art,
                                                String location,
                                                String hostEntityId,
                                                String idpEntityID,
                                                IDPSSODescriptorElement idp,
                                                HttpServletResponse response,
                                                String orgName,
                                                SAML2MetaManager sm)
        throws SAML2Exception,IOException
    {
        // create ArtifactResolve message
        ArtifactResolve resolve = null;
        SOAPMessage resMsg = null;
        try {
            resolve = ProtocolFactory.getInstance().createArtifactResolve();
            resolve.setID(SAML2Utils.generateID());
            resolve.setVersion(SAML2Constants.VERSION_2_0);
            resolve.setIssueInstant(new Date());
            resolve.setArtifact(art);
            resolve.setDestination(location);
            Issuer issuer = AssertionFactory.getInstance().createIssuer();
            issuer.setValue(hostEntityId);
            resolve.setIssuer(issuer);
            String needArtiResolveSigned =
                SAML2Utils.getAttributeValueFromSSOConfig(
                                orgName,
                                idpEntityID,
                                SAML2Constants.IDP_ROLE,
                                SAML2Constants.WANT_ARTIFACT_RESOLVE_SIGNED);
                                                        
            if (needArtiResolveSigned != null &&
                needArtiResolveSigned.equals("true")) {
                // or save it somewhere?
                String signAlias = getAttributeValueFromSPSSOConfig(
                                orgName,
                                hostEntityId,
                                sm,
                                SAML2Constants.SIGNING_CERT_ALIAS);
                if (signAlias == null) {
                    throw new SAML2Exception(
                        SAML2Utils.bundle.getString("missingSigningCertAlias"));
                }
                KeyProvider kp = KeyUtil.getKeyProviderInstance();
                if (kp == null) {
                    throw new SAML2Exception(
                        SAML2Utils.bundle.getString("nullKeyProvider"));
                }
                resolve.sign(kp.getPrivateKey(signAlias),
                                kp.getX509Certificate(signAlias));
            }

            String resolveString = resolve.toXMLString(true, true);
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("SPACSUtils.getResponseFromGet: "
                    + "ArtifactResolve=" + resolveString);
            }

            SOAPConnection con = SAML2Utils.scf.createConnection();
            SOAPMessage msg = SAML2Utils.createSOAPMessage(resolveString);

            IDPSSOConfigElement config = null;
            config = sm.getIDPSSOConfig(orgName, idpEntityID);
            location = SAML2Utils.fillInBasicAuthInfo(
                config, location);
            resMsg = con.call(msg, location);
        } catch (SAML2Exception s2e) {
            SAML2Utils.debug.error("SPACSUtils.getResponseFromGet: "
                + "couldn't create ArtifactResolve:", s2e);
            String[] data = {hostEntityId, art.getArtifactValue()};
            LogUtil.error(Level.INFO,
                        LogUtil.CANNOT_CREATE_ARTIFACT_RESOLVE,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                    SAML2Utils.bundle.getString("errorCreateArtifactResolve"));
            throw s2e;
        } catch (SOAPException se) {
            SAML2Utils.debug.error("SPACSUtils.getResponseFromGet: "
                + "couldn't get ArtifactResponse. SOAP error:",se);
            String[] data = {hostEntityId, location};
            LogUtil.error(Level.INFO,
                        LogUtil.CANNOT_GET_SOAP_RESPONSE,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                    SAML2Utils.bundle.getString("errorInSOAPCommunication"));
            throw new SAML2Exception(se.getMessage());
        }

        Response result = getResponseFromSOAP(
                        resMsg, resolve, response, idpEntityID,
                        idp, orgName, hostEntityId, sm);
        String[] data = {hostEntityId, idpEntityID,
                        art.getArtifactValue(), ""};
        if (LogUtil.isAccessLoggable(Level.FINE)) {
            data[3] = result.toXMLString();
        }
        LogUtil.access(Level.INFO,
                        LogUtil.GOT_RESPONSE_FROM_ARTIFACT,
                        data,
                        null);
        return result;
    }

    // Finds the IDP who sends the artifact;
    private static String getIDPEntityID(
                Artifact art,
                HttpServletResponse response,
                String orgName,
                SAML2MetaManager metaManager)
                throws SAML2Exception,IOException
    {
        String sourceID = art.getSourceID();
        // find the idp
        String idpEntityID = null;
        try {
            Iterator iter =
                metaManager.getAllRemoteIdentityProviderEntities(orgName).
                        iterator();
            String tmpSourceID = null;
            while (iter.hasNext()) {
                idpEntityID = (String) iter.next();
                tmpSourceID = SAML2Utils.generateSourceID(idpEntityID);
                if (sourceID.equals(tmpSourceID)) {
                    break;
                }
                idpEntityID = null;
            }
            if (idpEntityID == null) {
                SAML2Utils.debug.error("SPACSUtils.getResponseFromGet: Unable "
                    + "to find the IDP based on the SourceID in the artifact");
                String[] data = {art.getArtifactValue(), orgName};
                LogUtil.error(Level.INFO,
                        LogUtil.IDP_NOT_FOUND,
                        data,
                        null);
                throw new SAML2Exception(
                    SAML2Utils.bundle.getString("cannotFindIDP"));
            }
        } catch (SAML2Exception se) {
            String[] data = {art.getArtifactValue(), orgName};
            LogUtil.error(Level.INFO,
                        LogUtil.IDP_NOT_FOUND,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                    se.getMessage());
            throw se;
        }
        return idpEntityID;
    }

    // Retrieves the ArtifactResolutionServiceURL for an IDP.
    private static String getIDPArtifactResolutionServiceUrl(
                int endpointIndex,
                String idpEntityID,
                IDPSSODescriptorElement idp,
                HttpServletResponse response)
                throws SAML2Exception,IOException
    {
        // find the artifact resolution service url
        List arsList=idp.getArtifactResolutionService();
        ArtifactResolutionServiceElement ars = null;
        String location = null;
        String defaultLocation = null;
        String firstLocation = null;
        int index;
        boolean isDefault = false;
        for (int i=0; i<arsList.size(); i++) {
            ars = (ArtifactResolutionServiceElement)arsList.get(i);
            location = ars.getLocation();
            //String binding = ars.getBinding();
            index = ars.getIndex();
            isDefault = ars.isIsDefault();
            if (index == endpointIndex) {
                break;
            }
            if (isDefault) {
                defaultLocation = location;
            }
            if (i==0) {
                firstLocation = location;
            }
            location = null;
        }
        if (location == null || location.length() == 0) {
            location = defaultLocation;
            if (location == null || location.length() == 0) {
                location = firstLocation;
                if (location == null || location.length() == 0) {
                    SAML2Utils.debug.error("SPACSUtils: Unable to get the "
                        + "location of artifact resolution service for "
                        + idpEntityID);
                    String[] data = {idpEntityID};
                    LogUtil.error(Level.INFO,
                                LogUtil.ARTIFACT_RESOLUTION_URL_NOT_FOUND,
                                data,
                                null);
                    response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        SAML2Utils.bundle.getString(
                            "cannotFindArtifactResolutionUrl"));
                    throw new SAML2Exception(
                        SAML2Utils.bundle.getString(
                            "cannotFindArtifactResolutionUrl"));
                }
            }
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPACSUtils: IDP artifact resolution "
                + "service url =" + location);
        }
        return location;
    }

    /**
     * Obtains <code>SAML Response</code> from <code>SOAPBody</code>.
     * Used by Artifact profile.
     */
    private static Response getResponseFromSOAP(SOAPMessage resMsg,
                                                ArtifactResolve resolve,
                                                HttpServletResponse response,
                                                String idpEntityID,
                                                IDPSSODescriptorElement idp,
                                                String orgName,
                                                String hostEntityId,
                                                SAML2MetaManager sm)
                throws SAML2Exception,IOException
    {
        String method = "SPACSUtils.getResponseFromSOAP:";
        Element resElem = null;
        try {
            resElem = SAML2Utils.getSamlpElement(resMsg, "ArtifactResponse");
        } catch (SAML2Exception se) {
            String[] data = {idpEntityID};
            LogUtil.error(Level.INFO,
                        LogUtil.SOAP_ERROR,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                    se.getMessage());
            throw se; 
        }
        ArtifactResponse artiResp = null;
        try {
            artiResp = ProtocolFactory.getInstance().
                createArtifactResponse(resElem);
        } catch (SAML2Exception se) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(method + "Couldn't create "
                        + "ArtifactResponse:", se);
            }
            String[] data = {idpEntityID};
            LogUtil.error(Level.INFO,
                        LogUtil.CANNOT_INSTANTIATE_ARTIFACT_RESPONSE,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        se.getMessage());
            throw se;
        }

        if (artiResp == null) {
            String[] data = {idpEntityID};
            LogUtil.error(Level.INFO,
                        LogUtil.MISSING_ARTIFACT_RESPONSE,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                SAML2Utils.bundle.getString("missingArtifactResponse"));
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("missingArtifactResponse"));
        } else {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(method + "Received ArtifactResponse:"
                        + artiResp.toXMLString(true, true));
            }
        }

        // verify ArtifactResponse
        String wantArtiRespSigned = getAttributeValueFromSPSSOConfig(
                                orgName,
                                hostEntityId,
                                sm,
                                SAML2Constants.WANT_ARTIFACT_RESPONSE_SIGNED);
        if (wantArtiRespSigned != null && wantArtiRespSigned.equals("true")) {
            X509Certificate cert = KeyUtil.getVerificationCert(
                                                idp, idpEntityID, true);
            if (!artiResp.isSigned() || !artiResp.isSignatureValid(cert)) {
                if (SAML2Utils.debug.messageEnabled()) {
                   SAML2Utils.debug.message(method 
                        + "ArtifactResponse's signature is invalid.");
                }
                String[] data = {idpEntityID};
                LogUtil.error(Level.INFO,
                        LogUtil.ARTIFACT_RESPONSE_INVALID_SIGNATURE,
                        data,
                        null);
                response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                    SAML2Utils.bundle.getString("invalidSignature"));
                throw new SAML2Exception(
                    SAML2Utils.bundle.getString("invalidSignature"));
            }
        }

        String inResponseTo = artiResp.getInResponseTo();
        if (inResponseTo == null || !inResponseTo.equals(resolve.getID())) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(method 
                    + "ArtifactResponse's InResponseTo is invalid.");
            }
            String[] data = {idpEntityID};
            LogUtil.error(Level.INFO,
                        LogUtil.ARTIFACT_RESPONSE_INVALID_INRESPONSETO,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                SAML2Utils.bundle.getString("invalidInResponseTo"));
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("invalidInResponseTo"));
        }

        Issuer idpIssuer = artiResp.getIssuer();
        if (idpIssuer == null || !idpIssuer.getValue().equals(idpEntityID)) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(method 
                    + "ArtifactResponse's Issuer is invalid.");
            }
            String[] data = {idpEntityID};
            LogUtil.error(Level.INFO,
                        LogUtil.ARTIFACT_RESPONSE_INVALID_ISSUER,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                SAML2Utils.bundle.getString("invalidIssuer"));
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("invalidIssuer"));
        }

        // check time?

        Status status = artiResp.getStatus();
        if (status == null || !status.getStatusCode().getValue().equals(
                                        SAML2Constants.STATUS_SUCCESS))
        {
            String statusCode =
                (status == null)?"":status.getStatusCode().getValue();
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(method 
                    + "ArtifactResponse's status code is not success."
                    + statusCode);
            }
            String[] data = {idpEntityID, ""};
            if (LogUtil.isErrorLoggable(Level.FINE)) {
                data[1] = statusCode;
            }
            LogUtil.error(Level.INFO,
                        LogUtil.ARTIFACT_RESPONSE_INVALID_STATUS_CODE,
                        data,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                SAML2Utils.bundle.getString("invalidStatusCode"));
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("invalidStatusCode"));
        } 

        try {
            return ProtocolFactory.getInstance().createResponse(
                                artiResp.getAny());
        } catch (SAML2Exception se) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message(method 
                    + "couldn't instantiate Response:", se);
            }
            String[] data = {idpEntityID};
            LogUtil.error(Level.INFO,
                        LogUtil.CANNOT_INSTANTIATE_RESPONSE_ARTIFACT,
                        data,
                        null);
            response.sendError(
                response.SC_INTERNAL_SERVER_ERROR, se.getMessage());
            throw se;
        }
    }

    // Obtains SAML Response from POST.
    private static Response getResponseFromPost(HttpServletRequest request,
                                HttpServletResponse response)
                        throws SAML2Exception,IOException
    {
        SAML2Utils.debug.message("SPACSUtils:getResponseFromPost");
        String samlResponse = request.getParameter(
                        SAML2Constants.SAML_RESPONSE);
        if (samlResponse == null) {
            LogUtil.error(Level.INFO,
                        LogUtil.MISSING_SAML_RESPONSE_FROM_POST,
                        null,
                        null);
            response.sendError(response.SC_BAD_REQUEST,
                SAML2Utils.bundle.getString("missingSAMLResponse"));
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("missingSAMLResponse"));
        }

        // Get Response back
        // decode the Response
        Response resp = null;
        ByteArrayInputStream bis = null;
        try {
            byte[] raw = Base64.decode(samlResponse);
            if (raw != null) {
                bis = new ByteArrayInputStream(raw);
                Document doc = XMLUtils.toDOMDocument(bis, SAML2Utils.debug);
                if (doc != null) {
                    resp = ProtocolFactory.getInstance().
                        createResponse(doc.getDocumentElement());
                }
            }
        } catch (SAML2Exception se) {
            SAML2Utils.debug.error("SPACSUtils.getResponse: Exception "
                + "when instantiating SAMLResponse:", se);
            LogUtil.error(Level.INFO,
                        LogUtil.CANNOT_INSTANTIATE_RESPONSE_POST,
                        null,
                        null);
            response.sendError(response.SC_BAD_REQUEST,
                SAML2Utils.bundle.getString("errorObtainResponse"));
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("errorObtainResponse"));

        } catch (Exception e) {
            SAML2Utils.debug.error("SPACSUtils.getResponse: Exception "
                + "when decoding SAMLResponse:", e);
            LogUtil.error(Level.INFO,
                        LogUtil.CANNOT_DECODE_RESPONSE,
                        null,
                        null);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                SAML2Utils.bundle.getString("errorDecodeResponse"));
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("errorDecodeResponse"));
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception ie) {
                    if (SAML2Utils.debug.messageEnabled()) {
                        SAML2Utils.debug.message("SPACSUtils.getResponse: "
                            + "Exception when close the input stream:", ie);
                    }
                }
            }
        }
        
        String[] data = {""};
        if (LogUtil.isAccessLoggable(Level.FINE)) {
            data[0] = resp.toXMLString();
        }
        LogUtil.access(Level.INFO,
                        LogUtil.GOT_RESPONSE_FROM_POST,
                        data,
                        null);
        return resp;
    }

    /**
     * Authenticates user with <code>Response</code>.
     * Auth session upgrade will be called if input session is
     * not null.
     * Otherwise, saml2 auth module is called. The name of the auth module
     * is retrieved from <code>SPSSOConfig</code>. If not found, "SAML2" will
     * be used.
     *
     * @param request HTTP Servlet request
     * @param response HTTP Servlet response.
     * @param metaAlias metaAlias for the service provider
     * @param session input session object. It could be null.
     * @param respInfo <code>ResponseInfo</code> to be verified.
     * @param realm realm or organization name of the service provider.
     * @param hostEntityId hosted service provider Entity ID.
     * @param metaManager <code>SAML2MetaManager</code> instance for meta
     *                operation.
     * @return <code>Object</code> which holds result of the session.
     * @throws SAML2Exception if the processing failed.
     */
    public static Object processResponse(
        HttpServletRequest request, HttpServletResponse response,
        String metaAlias, Object session, ResponseInfo respInfo,
        String realm, String hostEntityId, SAML2MetaManager metaManager
    ) throws SAML2Exception {

        String classMethod = "SPACSUtils.processResponse: ";
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(classMethod + "Response : " +
                                     respInfo.getResponse());
        }        
        Map smap = null;
        // check Response/Assertion and get back a Map of relevant data
        smap = SAML2Utils.verifyResponse(
            respInfo.getResponse(), realm, hostEntityId,
            respInfo.getIsPOSTBinding());
        
        com.sun.identity.saml2.assertion.Subject assertionSubject =
            (com.sun.identity.saml2.assertion.Subject)
            smap.get(SAML2Constants.SUBJECT);
        NameID nameId = assertionSubject.getNameID();
        EncryptedID encId = assertionSubject.getEncryptedID();
        Assertion authnAssertion =
            (Assertion) smap.get(SAML2Constants.POST_ASSERTION);
        String sessionIndex = (String)smap.get(SAML2Constants.SESSION_INDEX);
        /** TODO: deal with this and session upgrade later
        Integer authLevel = (Integer) smap.get(SAML2Constants.AUTH_LEVEL);
        if (authLevel != null) {
            int value = authLevel.intValue();
            if (value >= 0) {
                setAuthLevel(value);
            }
        }
        */
        Long maxSessionTime = (Long) smap.get(SAML2Constants.MAX_SESSION_TIME);
        String inRespToResp = (String) smap.get(SAML2Constants.IN_RESPONSE_TO);
        List assertions = (List) smap.get(SAML2Constants.ASSERTIONS);
        
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(classMethod + "Assertions : " +
                                     assertions);
        }
        
        SPSSOConfigElement spssoconfig =
            metaManager.getSPSSOConfig(realm, hostEntityId);

        Map attributes = SAML2MetaUtils.getAttributes(spssoconfig);

        // get mappers
        SPAccountMapper acctMapper = null;
        if (session == null) {
            acctMapper = getSPAccountMapper(attributes);
        }
        SPAttributeMapper attrMapper =
            getSPAttributeMapper(attributes);
        
        boolean needAttributeEncrypted = false;
        boolean needNameIDEncrypted = false;
        String assertionEncryptedAttr =
            SAML2Utils.getAttributeValueFromSPSSOConfig(
                spssoconfig,
                SAML2Constants.WANT_ASSERTION_ENCRYPTED);
        if (assertionEncryptedAttr == null ||
            !assertionEncryptedAttr.equals("true"))
        {
            String attrEncryptedStr =
                SAML2Utils.getAttributeValueFromSPSSOConfig(
                    spssoconfig,
                    SAML2Constants.WANT_ATTRIBUTE_ENCRYPTED);
            if (attrEncryptedStr != null &&
                attrEncryptedStr.equals("true"))
            {
                needAttributeEncrypted = true;
            }
            String idEncryptedStr =
                SAML2Utils.getAttributeValueFromSPSSOConfig(
                    spssoconfig,
                    SAML2Constants.WANT_NAMEID_ENCRYPTED);
            if (idEncryptedStr != null &&
                idEncryptedStr.equals("true"))
            {
                needNameIDEncrypted = true;
            }
        }
        PrivateKey decryptionKey = KeyUtil.getDecryptionKey(spssoconfig);
        if (needNameIDEncrypted && encId == null) {
            SAML2Utils.debug.error(classMethod +
                                   "process: NameID was not encrypted.");
            throw new SAML2Exception(SAML2Utils.bundle.getString(
                "nameIDNotEncrypted"));
        }
        if (encId != null) {
            nameId = encId.decrypt(decryptionKey);
        }
        String userName = null;
        SessionProvider sessionProvider = null;
        try {
            sessionProvider = SessionManager.getProvider();
        } catch (SessionException se) {
            throw new SAML2Exception(se);
        }
        if (session != null) {
            try {
                userName = sessionProvider.
                    getPrincipalName(session);
            } catch (SessionException se) {
                throw new SAML2Exception(se);
            }
        }
        if (userName == null) {
            userName = acctMapper.getIdentity(
                authnAssertion, hostEntityId, realm);
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                classMethod + "process: userName =[" + userName + "]");
        }
        List attrs = null;
        String remoteHostId = null;
        for (Iterator it = assertions.iterator(); it.hasNext(); ) {
            Assertion assertion = (Assertion)it.next();
            remoteHostId = assertion.getIssuer().getValue();
            List origAttrs = getSAMLAttributes(assertion,
                                               needAttributeEncrypted,
                                               decryptionKey);
            if (origAttrs != null && !origAttrs.isEmpty()) {
                if (attrs == null) {
                    attrs = new ArrayList();
                }
                attrs.addAll(origAttrs);
            }
        }
        Map attrMap = null;
        if (attrs != null) {
            attrMap = attrMapper.getAttributes(attrs, userName,
                                               hostEntityId, remoteHostId, realm);
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                classMethod + "process: remoteHostId = " + remoteHostId);
            SAML2Utils.debug.message(
                classMethod + "process: attrMap = " + attrMap);
        }
        // return error code for local user login
        if ((userName == null) || (userName.length() == 0)) {
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("noUserMapping"));
        }
        
        boolean isFedInfoExists =
            SAML2Utils.isFedInfoExists(
                userName,hostEntityId,remoteHostId, nameId);
        // TODO: check if this few lines are needed
        /*
            DN dnObject = new DN(userName);
            String [] array = dnObject.explodeDN(true);
            userName = array[0];
        */
        boolean isPersistent = SAML2Utils.isPersistentNameID(nameId);
        boolean writeFedInfo = false;        
        if (!isFedInfoExists && isPersistent) {
            writeFedInfo = true;
        }        
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                classMethod + "userName : " + userName);
            SAML2Utils.debug.message(
                classMethod + "isPersistent : " + isPersistent);
            SAML2Utils.debug.message(
                classMethod + "isFedInfoExists : " + isFedInfoExists);
            SAML2Utils.debug.message(
                classMethod + "writeFedInfo : " + writeFedInfo);
        }
        if (inRespToResp != null && inRespToResp.length() != 0) {
            SPCache.requestHash.remove(inRespToResp);
        }
        Map sessionInfoMap = new HashMap();
        sessionInfoMap.put(SessionProvider.REALM, realm);
        sessionInfoMap.put(SessionProvider.PRINCIPAL_NAME, userName);
        //TODO: sessionInfoMap.put(SessionProvider.AUTH_LEVEL, "0");
        try {
            session = sessionProvider.createSession(
                sessionInfoMap, request, response, null);
            setAttrMapInSession(sessionProvider, attrMap, session);
            setDiscoBootstrapCredsInSSOToken(sessionProvider, authnAssertion,
                session);
        } catch (SessionException se) {
            throw new SAML2Exception(se);
        }
        // set metaAlias
        String[] values = { metaAlias };
        try {
            sessionProvider.setProperty(
                session, SAML2Constants.SP_METAALIAS, values);
        } catch (SessionException se) {
            throw new SAML2Exception(se);
        }
                    
        NameIDInfo info = new NameIDInfo(
            hostEntityId, remoteHostId, nameId,
            SAML2Constants.SP_ROLE, true);
        // write fed info into data store
        if (writeFedInfo) {
            AccountUtils.setAccountFederation(info, userName);
            String[] data = {userName, ""};
            if (LogUtil.isAccessLoggable(Level.FINE)) {
                data[1] = info.toValueString();
            }
            LogUtil.access(Level.INFO,
                           LogUtil.FED_INFO_WRITTEN,
                           data,
                           null);
        }
        // save info in memory for logout
        saveInfoInMemory(
            sessionProvider, session, sessionIndex, info);

        return session;
    }

    /**
     * Gets the <code>SPAccountMapper</code>
     *
     * @param attributes the Attribute Map
     * @return the <code>SPAccountMapper
     * @throws SAML2Exception if the processing failed. 
     */
    private static SPAccountMapper getSPAccountMapper(
        Map attributes) throws SAML2Exception {
            
        SPAccountMapper acctMapper = null;
        List acctMapperList = (List)attributes.get(
            SAML2Constants.SP_ACCOUNT_MAPPER);
        if (acctMapperList != null) {
            try {
                acctMapper = (SPAccountMapper)
                    (Class.forName((String)acctMapperList.get(0)).
                     newInstance());
                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message(
                        "SPACSUtils.getSPAccountMapper: mapper = " +
                        (String)acctMapperList.get(0));
                }
            } catch (ClassNotFoundException cfe) {
                throw new SAML2Exception(cfe);
            } catch (InstantiationException ie) {
                throw new SAML2Exception(ie);
            } catch (IllegalAccessException iae) {
                throw new SAML2Exception(iae);
            }
        }
        if (acctMapper == null) {
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("failedAcctMapper"));
        }
        return acctMapper;
    }

    /**
     * Gets the <code>SPAttributeMapper</code>
     *
     * @param attributes the Attribute Map
     * @return the <code>SPAttributeMapper
     * @throws SAML2Exception if the processing failed. 
     */
    private static SPAttributeMapper getSPAttributeMapper(
        Map attributes) throws SAML2Exception {
            
        SPAttributeMapper attrMapper = null;
        List attrMapperList = (List)attributes.get(
            SAML2Constants.SP_ATTRIBUTE_MAPPER);
        if (attrMapperList != null) {
            try {
                attrMapper = (SPAttributeMapper)
                    (Class.forName((String)attrMapperList.get(0)).
                     newInstance());
            } catch (ClassNotFoundException cfe) {
                throw new SAML2Exception(cfe);
            } catch (InstantiationException ie) {
                throw new SAML2Exception(ie);
            } catch (IllegalAccessException iae) {
                    throw new SAML2Exception(iae);
            }
        }
        if (attrMapper == null) {
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("failedAttrMapper"));
        }
        return attrMapper;
    }

    private static void saveInfoInMemory(
        SessionProvider sessionProvider, Object session,
        String sessionIndex, NameIDInfo info) throws SAML2Exception {
        
        String infoKeyString = (new NameIDInfoKey(
            info.getNameIDValue(),
            info.getHostEntityID(),
            info.getRemoteEntityID())).toValueString();
        String infoKeyAttribute =
            AccountUtils.getNameIDInfoKeyAttribute();
        String[] fromToken = null;
        try {
            fromToken = sessionProvider.
                getProperty(session, infoKeyAttribute);
            if (fromToken == null || fromToken.length == 0 ||
                fromToken[0] == null || fromToken[0].length() == 0) {
                String[] values = { infoKeyString };
                sessionProvider.setProperty(
                    session, infoKeyAttribute, values);
            } else {
                if (fromToken[0].indexOf(infoKeyString) == -1) {
                    String[] values = { fromToken[0] +
                                        SAML2Constants.SECOND_DELIM +
                                        infoKeyString }; 
                    sessionProvider.setProperty(
                        session, infoKeyAttribute, values);
                }
            }
        } catch (SessionException sessE) {
            throw new SAML2Exception(sessE);
        }
        String tokenID = sessionProvider.getSessionID(session);
        List fedSessions = (List)
            SPCache.fedSessionListsByNameIDInfoKey.get(infoKeyString);
        if (fedSessions == null) {
            synchronized (SPCache.fedSessionListsByNameIDInfoKey) {
                fedSessions = (List)
                SPCache.fedSessionListsByNameIDInfoKey.get(infoKeyString);
                if (fedSessions == null) {
                    fedSessions = new ArrayList();
                }
            }  
            synchronized (fedSessions) {
                fedSessions.add(new SPFedSession(sessionIndex, tokenID,
                    info));
                SPCache.fedSessionListsByNameIDInfoKey.put(
                    infoKeyString, fedSessions);
            }
        } else {
            synchronized (fedSessions) {
                Iterator iter = fedSessions.iterator();
                boolean found = false;
                while (iter.hasNext()) {
                    SPFedSession temp = (SPFedSession) iter.next();
                    if (temp.idpSessionIndex.equals(sessionIndex)) {
                        temp.spTokenID = tokenID;
                        found = true;
                        break;
                    }
                }    
                if (!found) {
                    fedSessions.add(
                        new SPFedSession(sessionIndex, tokenID, info));
                    SPCache.fedSessionListsByNameIDInfoKey.put(
                        infoKeyString, fedSessions);
                }
           }    
        }
        SPCache.fedSessionListsByNameIDInfoKey.put(infoKeyString,
                                                   fedSessions);
        try {
            sessionProvider.addListener(
                session, new SPSessionListener(infoKeyString, tokenID));
        } catch (SessionException e) {
            SAML2Utils.debug.error(
                "SPACSUtils.saveInfoInMemory: "+
                "Unable to add session listener.");
        }
    }
    
    /** Sets the attribute map in the session
     *
     *  @param sessionProvider Session provider
     *  @param attrMap the Attribute Map
     *  @param session the valid session object
     *  @throws com.sun.identity.plugin.session.SessionException 
     */
    public static void setAttrMapInSession(
        SessionProvider sessionProvider,
        Map attrMap, Object session)
        throws SessionException {
        if (attrMap != null && !attrMap.isEmpty()) {
            Set entrySet = attrMap.entrySet();
            for(Iterator iter = entrySet.iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry)iter.next();
                String attrName = (String)entry.getKey();
                Set attrValues = (Set)entry.getValue();
                if(attrValues != null && !attrValues.isEmpty()) {
                   sessionProvider.setProperty(
                       session, attrName,
                       (String[]) attrValues.toArray(
                       new String[attrValues.size()]));
                   if (SAML2Utils.debug.messageEnabled()) {
                       SAML2Utils.debug.message(
                           "SPACSUtils.setAttrMapInSessioin: AttrMap:" +
                           attrName + " , " + attrValues);
                   }
                }
            }
        }
    }

    /** Sets Discovery bootstrap credentials in the SSOToken
     *
     *  @param sessionProvider session provider.
     *  @param asserion assertion.
     *  @param session the valid session object.
     */
    private static void setDiscoBootstrapCredsInSSOToken(
        SessionProvider sessionProvider, Assertion assertion, Object session)
        throws SessionException, SAML2Exception {

        if (assertion == null) {
            return;
        }

        Set discoBootstrapCreds = null;
        Advice advice = assertion.getAdvice();
        if (advice != null) {
            List creds = advice.getAdditionalInfo();
            if ((creds != null) && !creds.isEmpty()) {
                if (discoBootstrapCreds == null) {
                    discoBootstrapCreds = new HashSet();
                }
                discoBootstrapCreds.addAll(creds);
            }
        }

        if (discoBootstrapCreds != null) {
            sessionProvider.setProperty(session,
                SAML2Constants.DISCOVERY_BOOTSTRAP_CREDENTIALS,
                (String[])discoBootstrapCreds.toArray(
                new String[discoBootstrapCreds.size()]));
        }
    }

    /**
     * Obtains relay state. Retrieves the relay state from relay state cache.
     * If input relay state is null, retrieve it from <code>SPSSOConfig</code>.
     *
     * @param relayStateID relay state value received from http request.
     * @param orgName realm or organization name the service provider resides in
     * @param hostEntityId Entity ID of the hosted service provider
     * @param sm <code>SAML2MetaManager</code> instance.
     * @return final relay state. Or <code>null</code> if the input 
     *         relayStateID is null and no default relay state is configured.
     */
    public static String getRelayState(
        String relayStateID,
        String orgName,
        String hostEntityId,
        SAML2MetaManager sm
    ) {
        String relayStateUrl = null;

        if ((relayStateID != null) && (relayStateID.trim().length() != 0)) {
            CacheObject cache = (CacheObject)SPCache.relayStateHash.remove(
                relayStateID);

            if (cache != null) {
                relayStateUrl = (String)cache.getObject();
            }

            if ((relayStateUrl == null) || (relayStateUrl.trim().length() == 0)
            ) {
                relayStateUrl = relayStateID;
            }
        }
        
        if (relayStateUrl == null || relayStateUrl.trim().length() == 0) {
            relayStateUrl = getAttributeValueFromSPSSOConfig(
                orgName, hostEntityId, sm, SAML2Constants.DEFAULT_RELAY_STATE);
        }
        
        return relayStateUrl;
    }

    /**
     * Retrieves intermediate redirect url from SP sso config. This url is used
     * if you want to goto some place before the final relay state.
     *
     * @param orgName realm or organization name the service provider resides in
     * @param hostEntityId Entity ID of the hosted service provider
     * @param sm <code>SAML2MetaManager</code> instance.
     * @return intermediate redirect url; or <code>null</code> if the url is
     *                is not configured or an error occured during the retrieval
     *                process.
     */
    public static String getIntermediateURL(String orgName,
                                        String hostEntityId,
                                        SAML2MetaManager sm)
    {
        return getAttributeValueFromSPSSOConfig(orgName, hostEntityId, sm,
                                        SAML2Constants.INTERMEDIATE_URL);
    }

    /**
     * Saves response for later retrieval and retrieves local auth url from
     * <code>SPSSOConfig</code>.
     * If the url does not exist, generate one from request URI.
     * If still cannot get it, (shouldn't happen), get it from
     * <code>AMConfig.properties</code>.
     *
     * @param orgName realm or organization name the service provider resides in
     * @param hostEntityId Entity ID of the hosted service provider
     * @param sm <code>SAML2MetaManager</code> instance to perform meta
     *                operation.
     * @param respInfo to be cached <code>ResponseInfo</code>.
     * @param requestURI http request URI.
     * @return local login url.
     */
    public static String prepareForLocalLogin(
                                        String orgName,
                                        String hostEntityId,
                                        SAML2MetaManager sm,
                                        ResponseInfo respInfo,
                                        String requestURI)
    {
        String localLoginUrl = getAttributeValueFromSPSSOConfig(
                orgName, hostEntityId, sm, SAML2Constants.LOCAL_AUTH_URL);
        if ((localLoginUrl == null) || (localLoginUrl.length() == 0)) {
            // get it from request
            try {
                int index = requestURI.indexOf("Consumer/metaAlias");
                if (index != -1) {
                    localLoginUrl = requestURI.substring(0, index)
                        + "UI/Login?org="
                        + orgName;
                }
            } catch (IndexOutOfBoundsException e) {
                localLoginUrl = null;
            }
            if ((localLoginUrl == null) || (localLoginUrl.length() == 0)) {
                // shouldn't be here, but in case
                localLoginUrl =
                        SystemConfigurationUtil.getProperty(SAMLConstants.SERVER_PROTOCOL)
                        + "://"
                        + SystemConfigurationUtil.getProperty(SAMLConstants.SERVER_HOST)
                        + SystemConfigurationUtil.getProperty(SAMLConstants.SERVER_PORT)
                        + "/UI/Login?org="
                        + orgName;
            }
        }
        synchronized (SPCache.responseHash) {
           SPCache.responseHash.put(respInfo.getResponse().getID(), 
               respInfo);
        }   
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPACSUtils:prepareForLocalLogin: " +
                "localLoginUrl = " + localLoginUrl);
        }
        return localLoginUrl;
    }

    /**
     * Retrieves attribute value for a given attribute name from 
     * <code>SPSSOConfig</code>.
     * @param orgName realm or organization name the service provider resides in
     * @param hostEntityId hosted service provider's Entity ID.
     * @param sm <code>SAML2MetaManager</code> instance to perform meta
     *                operations.
     * @param attrName name of the attribute whose value ot be retrived.
     * @return value of the attribute; or <code>null</code> if the attribute
     *                if not configured, or an error occured in the process.
     */ 
    private static String getAttributeValueFromSPSSOConfig(String orgName,
                                                        String hostEntityId,
                                                        SAML2MetaManager sm,
                                                        String attrName)
    {
        String result = null;
        try {
            SPSSOConfigElement config = sm.getSPSSOConfig(orgName,
                                                        hostEntityId);
            if (config == null) {
                return null;
            }
            Map attrs = SAML2MetaUtils.getAttributes(config);
            List value = (List) attrs.get(attrName);
            if (value != null && value.size() != 0) {
                result = ((String) value.iterator().next()).trim();
            }
        } catch (SAML2MetaException sme) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("SPACSUtils.getAttributeValueFromSPSSO"
                        + "Config:", sme);
            }
            result = null;
        }
        return result;
    }

    // gets the attributes from AttibuteStates in the assertions.
    private static List getSAMLAttributes(Assertion assertion,
                                boolean needAttributeEncrypted,
                                PrivateKey decryptionKey)
    {
        List attrList = null;
        if (assertion != null) {
            List statements = assertion.getAttributeStatements();
            if (statements != null && statements.size() > 0 ) {
                for (Iterator it = statements.iterator(); it.hasNext(); ) {
                    AttributeStatement statement =
                        (AttributeStatement)it.next();
                    List attributes = statement.getAttribute();
                    if (needAttributeEncrypted &&
                        attributes != null && attributes.isEmpty())
                    {
                        SAML2Utils.debug.error("Attribute not encrypted.");
                        return null;
                    }
                    if (attributes != null) {
                        if (attrList == null) {
                            attrList = new ArrayList();
                        }
                        attrList.addAll(attributes);
                    }
                    List encAttrs = statement.getEncryptedAttribute();
                    if (encAttrs != null) {
                        for (Iterator encIter = encAttrs.iterator();
                                encIter.hasNext(); )
                        {
                            if (attrList == null) {
                                attrList = new ArrayList();
                            }
                            try {
                                attrList.add(
                                    ((EncryptedAttribute) encIter.next()).
                                        decrypt(decryptionKey));
                            } catch (SAML2Exception se) {
                                SAML2Utils.debug.error("Decryption error:", se);
                                return null;
                            }
                        }
                    }
                }
            }
        }
        return attrList;
    }
}

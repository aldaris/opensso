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
 * $Id: DoManageNameID.java,v 1.1 2006-10-30 23:16:34 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.profile;
import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;

import com.sun.identity.shared.encode.URLEncDec;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionProvider;
import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.saml.common.SAMLUtils;
import com.sun.identity.saml.xmlsig.KeyProvider;
import com.sun.identity.saml2.assertion.AssertionFactory;
import com.sun.identity.saml2.assertion.EncryptedID;
import com.sun.identity.saml2.assertion.Issuer;
import com.sun.identity.saml2.assertion.NameID;
import com.sun.identity.saml2.common.AccountUtils;
import com.sun.identity.saml2.common.NameIDInfo;
import com.sun.identity.saml2.common.NameIDInfoKey;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.saml2.jaxb.metadata.IDPSSODescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.KeyDescriptorType;
import com.sun.identity.saml2.jaxb.metadata.ManageNameIDServiceElement;
import com.sun.identity.saml2.jaxb.metadata.SPSSODescriptorElement;
import com.sun.identity.saml2.key.EncInfo;
import com.sun.identity.saml2.key.KeyUtil;
import com.sun.identity.saml2.logging.LogUtil;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.plugins.IDPAccountMapper;
import com.sun.identity.saml2.plugins.SPAccountMapper;
import com.sun.identity.saml2.protocol.ManageNameIDRequest;
import com.sun.identity.saml2.protocol.ManageNameIDResponse;
import com.sun.identity.saml2.protocol.ProtocolFactory;
import com.sun.identity.saml2.protocol.Status;

/**
 * This class reads the query parameters and the required
 * processing logic for sending ManageNameIDRequest
 * from SP to IDP.
 */

public class DoManageNameID {
    final static String className = "DoManageNameID:";
    static ProtocolFactory pf = ProtocolFactory.getInstance();
    static AssertionFactory af = AssertionFactory.getInstance();
    static SOAPConnectionFactory scf = null;
    static MessageFactory mf = null;
    static SAML2MetaManager metaManager = null;
    static KeyProvider keyProvider = KeyUtil.getKeyProviderInstance(); 
    static Debug debug = SAML2Utils.debug;
    static SessionProvider sessionProvider = null;
    
    static {
        try {
            scf = SOAPConnectionFactory.newInstance();
            mf = MessageFactory.newInstance();
            metaManager= new SAML2MetaManager();
            sessionProvider = SessionManager.getProvider();
        } catch (SOAPException se) {
            debug.error(SAML2Utils.bundle.getString("errorSOAPFactory"), se);
        } catch (SAML2MetaException se) {
            debug.error(SAML2Utils.bundle.getString("errorMetaManager"), se);
        } catch (SessionException sessE) {
            debug.error("Error retrieving session provider.", sessE);
        }
    }
    
    private static void logError(String msgID, String key, String value) {
        debug.error(SAML2Utils.bundle.getString(msgID));
        String[] data = {value};
        LogUtil.error(Level.INFO, key, data, null);
    }
    
    /**
     * Parses the request parameters and builds the ManageNameID
     * Request to sent to remote Entity.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @param metaAlias entityID of hosted entity.
     * @param remoteEntityID entityID of remote entity.
     * @param paramsMap Map of all other parameters.
     * @throws SAML2Exception if error initiating request to remote entity.
     */
    public static void initiateManageNameIDRequest(
        HttpServletRequest request,
        HttpServletResponse response,
        String metaAlias,
        String remoteEntityID,
        Map paramsMap) throws SAML2Exception {
            
        String method = "initiateManageNameIDRequest: ";
        
        if (metaAlias == null) {
            logError("MetaAliasNotFound", 
                            LogUtil.MISSING_META_ALIAS, metaAlias);
            throw new SAML2Exception(
                        SAML2Utils.bundle.getString("nullEntityID"));
        }
                
        if (remoteEntityID == null)  {
            logError("nullRemoteEntityID", 
                            LogUtil.MISSING_ENTITY, remoteEntityID);
            throw new SAML2Exception(
                        SAML2Utils.bundle.getString("nullRemoteEntityID"));
        }

        Object session = 
           SAML2Utils.checkSession(request, response, metaAlias, paramsMap);
        String realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
        String hostEntity = null;
        String hostEntityRole = SAML2Utils.getHostEntityRole(paramsMap);
        if (session == null) {
            if (debug.messageEnabled()) {
                debug.message(method + "Session is missing." + 
                            "redirect to the authentication service");
            }
            // the user has not logged in yet, 
            // redirect to the authentication service
            try {
                hostEntity = metaManager.getEntityByMetaAlias(metaAlias);
                SAML2Utils.redirectAuthentication(request, response, 
                                realm, hostEntity, hostEntityRole);
            } catch (IOException ioe) {
                logError("UnableToRedirectToAuth", 
                                LogUtil.REDIRECT_TO_AUTH, null);
                throw new SAML2Exception(ioe.toString());
            }
            return;
        } 
        
        if (debug.messageEnabled()) {
            debug.message(method + "Meta Alias is : "+ metaAlias);
            debug.message(method + "Remote EntityID is : " + remoteEntityID);
        }
        
        try {
                String binding = 
                    SAML2Utils.getParameter(paramsMap, SAML2Constants.BINDING); 
        
            ManageNameIDServiceElement mniService =
                        getMNIServiceElement(realm, remoteEntityID, 
                                             hostEntityRole, binding);
            if (binding == null) {
                binding = mniService.getBinding();
            }

            if (binding == null) {
                logError("UnableTofindBinding", LogUtil.METADATA_ERROR, null);
                    throw new SAML2Exception(
                        SAML2Utils.bundle.getString("UnableTofindBinding"));
                }

            String mniURL = null;
            if (mniService != null) {
                mniURL = mniService.getLocation();
            }
            
            if (mniURL == null) {
                logError("mniServiceNotFound", LogUtil.METADATA_ERROR, null);
                throw new SAML2Exception(
                    SAML2Utils.bundle.getString("mniServiceNotFound"));
            }
            
            // create ManageNameIDRequest 
            ManageNameIDRequest mniRequest = createTerminateRequest(session, 
                            metaAlias, hostEntityRole, remoteEntityID, mniURL);

            String relayState = SAML2Utils.getParameter(paramsMap,
                             SAML2Constants.RELAY_STATE);
            saveMNIRequestInfo(request, response, paramsMap, 
                        mniRequest, relayState, hostEntityRole);

            hostEntity = metaManager.getEntityByMetaAlias(metaAlias);
            
            String mniRequestXMLString= null;
            
            if (binding.equalsIgnoreCase(SAML2Constants.HTTP_REDIRECT)) {
                mniRequestXMLString= mniRequest.toXMLString(true,true);
                       doMNIByHttpRedirect(mniRequestXMLString, 
                                    mniURL,
                                    relayState,
                                    realm, 
                                    hostEntity,
                                    hostEntityRole, 
                                    remoteEntityID,        
                                    response);
            } else if (binding.equalsIgnoreCase(SAML2Constants.SOAP)) {
                signMNIRequest(mniRequest, realm, hostEntity, 
                           hostEntityRole, remoteEntityID);
                mniRequestXMLString= mniRequest.toXMLString(true,true);

                BaseConfigType config = null;
                if (hostEntityRole.equalsIgnoreCase(SAML2Constants.SP_ROLE)) {
                    config = metaManager.getIDPSSOConfig(realm, remoteEntityID);
                } else {
                    config = metaManager.getSPSSOConfig(realm, remoteEntityID);
                }
                mniURL = SAML2Utils.fillInBasicAuthInfo(config, mniURL);
                doMNIBySOAP(mniRequestXMLString,
                            mniURL, 
                            metaAlias,
                            hostEntityRole);
            }
        } catch (IOException ioe) {
            logError("errorCreatingMNIRequest", 
                            LogUtil.CANNOT_INSTANTIATE_MNI_REQUEST, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("errorCreatingMNIRequest"));
        } catch (SAML2MetaException sme) {
            logError("metaDataError", LogUtil.METADATA_ERROR, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("metaDataError"));            
        } catch (SessionException ssoe) {
             logError("invalidSSOToken", LogUtil.INVALID_SSOTOKEN, null);
             throw new SAML2Exception(
                     SAML2Utils.bundle.getString("invalidSSOToken"));       
        }
    }

    /**
     * Returns binding information of MNI Service for remote entity 
     * from request or meta configuration.
     *
     * @param request the HttpServletRequest.
     * @param metaAlias entityID of hosted entity.
     * @param hostEntityRole Role of hosted entity.
     * @param remoteEntityID entityID of remote entity.
     * @return return true if the processing is successful.
     * @throws SAML2Exception if no binding information is configured.
     */
    public static String getMNIBindingInfo(HttpServletRequest request,
                                 String metaAlias,
                                 String hostEntityRole,
                                 String remoteEntityID)
                                 throws SAML2Exception {
        String binding = request.getParameter(SAML2Constants.BINDING);

        try {
            if (binding == null) {
                String realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
                ManageNameIDServiceElement mniService =
                    getMNIServiceElement(realm, remoteEntityID,
                                       hostEntityRole, null);
                if (mniService != null) {
                    binding = mniService.getBinding();
                }
            }
        } catch (SessionException e) {
            logError("invalidSSOToken", LogUtil.INVALID_SSOTOKEN, null);
            throw new SAML2Exception(
                        SAML2Utils.bundle.getString("invalidSSOToken"));       
        }
        
        if (binding == null) {
            logError("UnableTofindBinding", 
                             LogUtil.METADATA_ERROR, null);
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("UnableTofindBinding"));
        }
        return binding;
    }
    
    private static void signMNIRequest(ManageNameIDRequest mniRequest, 
                                   String realm, String hostEntity, 
                                   String hostEntityRole, String remoteEntity) 
        throws SAML2Exception {
        signMNIRequest(mniRequest, realm, hostEntity, 
                       hostEntityRole, remoteEntity, false);
    }
    
    private static void signMNIRequest(ManageNameIDRequest mniRequest, 
                                  String realm, String hostEntity,
                                  String hostEntityRole, String remoteEntity,
                                  boolean includeCert) 
        throws SAML2Exception {
        String method = "signMNIRequest : ";
        boolean needRequestSign = false;
        
        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
            needRequestSign = 
                SAML2Utils.getWantMNIRequestSigned(realm, remoteEntity, 
                           SAML2Constants.SP_ROLE);
        } else {
            needRequestSign = 
                SAML2Utils.getWantMNIRequestSigned(realm, remoteEntity, 
                           SAML2Constants.IDP_ROLE);
        }
        
        if (needRequestSign == false) {
            if (debug.messageEnabled()) {
                debug.message(method + "MNIRequest doesn't need to be signed.");
            }
            return;
        }
        
        String alias = 
            SAML2Utils.getSigningCertAlias(realm, hostEntity, hostEntityRole);
        
        if (debug.messageEnabled()) {
            debug.message(method + "realm is : "+ realm);
            debug.message(method + "hostEntity is : " + hostEntity);
            debug.message(method + "Host Entity role is : " + hostEntityRole);
            debug.message(method + "remoteEntity is : " + remoteEntity);
            debug.message(method + "Cert Alias is : " + alias);
            debug.message(method + "MNI Request before sign : " 
                            + mniRequest.toXMLString(true, true));
        }
        PrivateKey signingKey = keyProvider.getPrivateKey(alias);
        X509Certificate signingCert = null;
        if (includeCert) {
            signingCert = keyProvider.getX509Certificate(alias);
        }
        
        if (signingKey != null) {
            mniRequest.sign(signingKey, signingCert);
        } else {
            logError("missingSigningCertAlias", 
                             LogUtil.METADATA_ERROR, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("missingSigningCertAlias"));
        }
        
        if (debug.messageEnabled()) {
            debug.message(method + "MNI Request after sign : " 
                                + mniRequest.toXMLString(true, true));
        }
    }

    private static boolean verifyMNIRequest(ManageNameIDRequest mniRequest, 
                                         String realm, String remoteEntity, 
                                    String hostEntity, String hostEntityRole)
        throws SAML2Exception, SessionException {
        String method = "verifyMNIRequest : ";
        if (debug.messageEnabled()) {
            debug.message(method + "realm is : "+ realm);
            debug.message(method + "remoteEntity is : " + remoteEntity);
            debug.message(method + "Host Entity role is : " + hostEntityRole);
        }
        
        boolean needVerifySignature = 
                SAML2Utils.getWantMNIRequestSigned(realm, hostEntity, 
                           hostEntityRole);
        
        if (needVerifySignature == false) {
            if (debug.messageEnabled()) {
                debug.message(method+"MNIRequest doesn't need to be verified.");
            }
            return true;
        }
                
        boolean valid = false;
        X509Certificate signingCert = null;
        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
            SPSSODescriptorElement spSSODesc =
                metaManager.getSPSSODescriptor(realm, remoteEntity);
            signingCert = 
                KeyUtil.getVerificationCert(spSSODesc, remoteEntity, false);
        } else {
            IDPSSODescriptorElement idpSSODesc = 
                 metaManager.getIDPSSODescriptor(realm, remoteEntity);
            signingCert = 
                    KeyUtil.getVerificationCert(idpSSODesc, remoteEntity, true);
        }

        if (signingCert != null) {
            valid = mniRequest.isSignatureValid(signingCert);
                if (debug.messageEnabled()) {
                debug.message(method + "Signature is : " + valid);
            }
        } else {
            logError("missingSigningCertAlias.", 
                             LogUtil.METADATA_ERROR, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("missingSigningCertAlias"));
        }
        
        return valid;
    }
    
    private static void signMNIResponse(ManageNameIDResponse mniResponse, 
                                         String realm, String hostEntity, 
                               String hostEntityRole, String remoteEntity) 
        throws SAML2Exception {
        signMNIResponse(mniResponse, realm, hostEntity, 
                        hostEntityRole, remoteEntity, false); 
    }
    
    private static void signMNIResponse(ManageNameIDResponse mniResponse, 
                                         String realm, String hostEntity, 
                               String hostEntityRole, String remoteEntity,
                                                      boolean includeCert) 
        throws SAML2Exception {
        String method = "signMNIResponse : ";
        boolean needResponseSign = false;
        
        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
            needResponseSign = 
                SAML2Utils.getWantMNIResponseSigned(realm, remoteEntity, 
                           SAML2Constants.SP_ROLE);
        } else {
            needResponseSign = 
                SAML2Utils.getWantMNIResponseSigned(realm, remoteEntity, 
                           SAML2Constants.IDP_ROLE);
        }
        
        if (needResponseSign == false) {
            if (debug.messageEnabled()) {
                debug.message(method+"MNIResponse doesn't need to be signed.");
            }
            return;
        }
        
        String alias = 
            SAML2Utils.getSigningCertAlias(realm, hostEntity, hostEntityRole);
        if (debug.messageEnabled()) {
            debug.message(method + "realm is : "+ realm);
            debug.message(method + "hostEntity is : " + hostEntity);
            debug.message(method + "Host Entity role is : " + hostEntityRole);
            debug.message(method + "Cert Alias is : " + alias);
            debug.message(method + "MNI Response before sign : " 
                            + mniResponse.toXMLString(true, true));
        }
        PrivateKey signingKey = keyProvider.getPrivateKey(alias);
        X509Certificate signingCert = null;
        if (includeCert) {
            signingCert = keyProvider.getX509Certificate(alias);
        }
        
        if (signingKey != null) {
            mniResponse.sign(signingKey, signingCert);
        } else {
            logError("missingSigningCertAlias", 
                             LogUtil.METADATA_ERROR, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("missingSigningCertAlias"));
        }
        
        if (debug.messageEnabled()) {
                debug.message(method + "MNI Response after sign : " 
                                    + mniResponse.toXMLString(true, true));
        }
    }

    private static boolean verifyMNIResponse(ManageNameIDResponse mniResponse, 
                                            String realm, String remoteEntity, 
                                     String hostEntity, String hostEntityRole) 
        throws SAML2Exception, SessionException {
        String method = "verifyMNIResponse : ";
        if (debug.messageEnabled()) {
            debug.message(method + "realm is : "+ realm);
            debug.message(method + "remoteEntity is : " + remoteEntity);
            debug.message(method + "Host Entity role is : " + hostEntityRole);
        }
        
        boolean needVerifySignature = 
                SAML2Utils.getWantMNIResponseSigned(realm, hostEntity, 
                                hostEntityRole);
        
        if (needVerifySignature == false) {
            if (debug.messageEnabled()) {
                debug.message(method + 
                        "MNIResponse doesn't need to be verified.");
            }
            return true;
        }
                
        boolean valid = false;
        X509Certificate signingCert = null;
        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
            SPSSODescriptorElement spSSODesc =
                metaManager.getSPSSODescriptor(realm, remoteEntity);
            signingCert = 
                    KeyUtil.getVerificationCert(spSSODesc, remoteEntity, false);
        } else {
            IDPSSODescriptorElement idpSSODesc = 
                     metaManager.getIDPSSODescriptor(realm, remoteEntity);
            signingCert = 
                KeyUtil.getVerificationCert(idpSSODesc, remoteEntity, true);
        }
        
        if (signingCert != null) {
            valid = mniResponse.isSignatureValid(signingCert);
                if (debug.messageEnabled()) {
                debug.message(method + "Signature is : " + valid);
                }
        } else {
            logError("missingSigningCertAlias", 
                             LogUtil.METADATA_ERROR, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("missingSigningCertAlias"));
        }
        
        return valid;
    }
    
    private static void saveMNIRequestInfo(HttpServletRequest request, 
                    HttpServletResponse response, 
                    Map paramsMap, 
                    ManageNameIDRequest mniRequest, 
                    String relayState,
                    String hostEntityRole) throws SAML2Exception {
        String method = "saveMNIRequestInfo: ";
        if (debug.messageEnabled()) {
            debug.message(method + "hostEntityRole : " + hostEntityRole);
        }
        
        ManageNameIDRequest reqForSave = mniRequest;
        if (mniRequest.getEncryptedID() != null) {
            reqForSave = (ManageNameIDRequest)
               pf.createManageNameIDRequest(mniRequest.toXMLString(true, true));
            mniRequest.setNameID(null);
        }
        
        ManageNameIDRequestInfo reqInfo = 
            new ManageNameIDRequestInfo(request, response, reqForSave,
                             relayState, paramsMap);
        
        Object session = null;
        try {
            session = sessionProvider.getSession(request);
        } catch (SessionException se) {
            debug.error(method, se);
            throw new SAML2Exception(se);
        }
        paramsMap.put(SAML2Constants.SESSION, session);

        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.SP_ROLE)) {
            SPCache.mniRequestHash.put(mniRequest.getID(), reqInfo);
        } else {
            IDPCache.mniRequestHash.put(mniRequest.getID(), reqInfo);
        }
    }
    
    /**
     * Parses the request parameters and process the ManageNameID
     * Request from the remote entity.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @param paramsMap Map of all other parameters.
     * @throws SAML2Exception if error occurred while processing the request.
     * @throws SessionException if error processing the request from remote entity.
     * @throws ServletException if request length is invalid.
     */
    public static void processHttpRequest(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Map paramsMap) 
        throws SAML2Exception, SessionException, ServletException {
        String method = "processHttpRequest: ";
        String metaAlias = null;
        String remoteEntityID = null;
        String queryString = null;
        
        // handle DOS attack
        SAMLUtils.checkHTTPContentLength(request);
        String requestURL = request.getRequestURI();
        metaAlias = SAML2MetaUtils.getMetaAliasByUri(requestURL);
        if (metaAlias == null) {
            logError("MetaAliasNotFound", 
                             LogUtil.MISSING_META_ALIAS, metaAlias);
            throw new SAML2Exception(
                 SAML2Utils.bundle.getString("MetaAliasNotFound"));
        }

        // Retrieve ManageNameIDRequest 
        ManageNameIDRequest mniRequest = getMNIRequest(request);
        remoteEntityID = mniRequest.getIssuer().getValue();
        if (remoteEntityID == null)  {
            logError("nullRemoteEntityID", 
                            LogUtil.MISSING_ENTITY, remoteEntityID);
            throw new SAML2Exception(
                 SAML2Utils.bundle.getString("nullRemoteEntityID"));
        }

        String realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
        String hostEntity = metaManager.getEntityByMetaAlias(metaAlias);
        String hostRole = SAML2Utils.getHostEntityRole(paramsMap);
        boolean needToVerify = 
            SAML2Utils.getWantMNIRequestSigned(realm, hostEntity, hostRole);
        if (needToVerify == true) {
            queryString = request.getQueryString();
            boolean valid = 
                    SAML2Utils.verifyQueryString(queryString, realm,
                            hostRole, remoteEntityID);
            if (valid == false) {
                logError("invalidSignInRequest", 
                                LogUtil.MNI_REQUEST_INVALID_SIGNATURE, null);
                throw new SAML2Exception(
                    SAML2Utils.bundle.getString("invalidSignInRequest"));
            }
        }
        
        String relayState =
                request.getParameter(SAML2Constants.RELAY_STATE);

        if (debug.messageEnabled()) {
            debug.message(method + "Meta Alias is : "+ metaAlias);
            debug.message(method + "Remote EntityID is : " + remoteEntityID);
            debug.message(method + "Host Entity role is : " + hostRole);
            debug.message(method + "Relay state is : " + relayState);
        }
        
        try {
            ManageNameIDServiceElement mniService =
                    getMNIServiceElement(realm, remoteEntityID, 
                                 hostRole, SAML2Constants.HTTP_REDIRECT);
            
            ManageNameIDResponse mniResponse = processManageNameIDRequest(
                            mniRequest,
                            metaAlias,
                            remoteEntityID,
                            paramsMap);
            
            String mniURL = mniService.getResponseLocation();
            
            sendMNIResponse(response, mniResponse, mniURL, relayState, realm, 
                            hostEntity, hostRole, remoteEntityID);        
        } catch (SAML2MetaException e) {
            logError("metaDataError", LogUtil.METADATA_ERROR, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("metaDataError"));            
        } catch (SessionException e) {
            logError("invalidSSOToken", LogUtil.INVALID_SSOTOKEN, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("invalidSSOToken"));       
        }
    }

    /**
     * Parses the request parameters and process the ManageNameID
     * Request from the remote entity.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @param paramsMap Map of all other parameters.
     * @throws SAML2Exception if error occurred while processing the request.
     * @throws IOException if error generation DOM from input stream.
     * @throws SOAPException if error generating soap message.
     * @throws SessionException if session is invalid.
     * @throws ServletException if request length is invalid.
     */
    public static void processSOAPRequest(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Map paramsMap) 
        throws SAML2Exception, IOException, SOAPException, 
               SessionException, ServletException {
        String method = "processSOAPRequest: ";
        String metaAlias = null;
        String remoteEntityID = null;
        String requestURL = request.getRequestURI();
        String hostEntityRole = SAML2Utils.getHostEntityRole(paramsMap);

        // handle DOS attack
        SAMLUtils.checkHTTPContentLength(request);
        metaAlias = SAML2MetaUtils.getMetaAliasByUri(requestURL);
        if (metaAlias == null) {
            logError("MetaAliasNotFound", 
                                 LogUtil.MISSING_META_ALIAS, metaAlias);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("MetaAliasNotFound"));
        }
        
        // Retrieve a SOAPMessage
        SOAPMessage message = SAML2Utils.getSOAPMessage(request);

        ManageNameIDRequest mniRequest = getMNIRequest(message);
        remoteEntityID = mniRequest.getIssuer().getValue();
        if (remoteEntityID == null)  {
            logError("nullRemoteEntityID", 
                                 LogUtil.MISSING_ENTITY, metaAlias);
            throw new SAML2Exception(
                 SAML2Utils.bundle.getString("nullRemoteEntityID"));
        }

        String realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
        String hostEntity = metaManager.getEntityByMetaAlias(metaAlias);
        if (debug.messageEnabled()) {
            debug.message(method + "Meta Alias is : "+ metaAlias);
            debug.message(method + "Host EntityID is : " + hostEntity);
            debug.message(method + "Remote EntityID is : " + remoteEntityID);
        }
            
        boolean valid = verifyMNIRequest(mniRequest, realm, remoteEntityID, 
                            hostEntity, hostEntityRole);
        if (valid == false)  {
            logError("invalidSignInRequest", 
                         LogUtil.MNI_REQUEST_INVALID_SIGNATURE, metaAlias);
            throw new SAML2Exception(
                      SAML2Utils.bundle.getString("invalidSignInRequest"));
        }
        
        ManageNameIDResponse mniResponse = processManageNameIDRequest(
                        mniRequest,
                        metaAlias,
                        remoteEntityID,
                        paramsMap);
        
        signMNIResponse(mniResponse, realm, hostEntity, 
                        hostEntityRole, remoteEntityID);

        SOAPMessage reply = SAML2Utils.createSOAPMessage(
            mniResponse.toXMLString(true, true));
        if (reply != null) {
            /*  Need to call saveChanges because we're
             * going to use the MimeHeaders to set HTTP
             * response information. These MimeHeaders
             * are generated as part of the save. */
            if (reply.saveRequired()) {
                reply.saveChanges();
            }
        
            response.setStatus(HttpServletResponse.SC_OK);
            SAML2Utils.putHeaders(reply.getMimeHeaders(), response);
            // Write out the message on the response stream
            OutputStream os = response.getOutputStream();
            reply.writeTo(os);
            os.flush();
        } else {
            logError("errorObtainResponse", 
                                 LogUtil.CANNOT_INSTANTIATE_MNI_RESPONSE, null);
            throw new SAML2Exception(
                           SAML2Utils.bundle.getString("errorObtainResponse"));
        }
    }

    /**
     * Parses the request parameters and builds the Authentication
     * Request to sent to the IDP.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @param paramsMap Map of all other parameters.
     * @return return true if the processing is successful.
     * @throws SAML2Exception if error initiating request to IDP.
     */
    public static boolean processManageNameIDResponse(
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Map paramsMap)
                               throws SAML2Exception {
        String method = "processManageNameIDResponse: ";
        boolean success = false;
        String requestURL = request.getRequestURI();
        String metaAlias = SAML2MetaUtils.getMetaAliasByUri(requestURL);
        if (metaAlias == null) {
            logError("MetaAliasNotFound", LogUtil.MISSING_META_ALIAS, null);
            throw new SAML2Exception(
                 SAML2Utils.bundle.getString("MetaAliasNotFound"));
        }
        String hostRole = SAML2Utils.getHostEntityRole(paramsMap);
        
        String relayState =
                    request.getParameter(SAML2Constants.RELAY_STATE);
        String mniRes =
                    request.getParameter(SAML2Constants.SAML_RESPONSE);
        
        String mniResStr = SAML2Utils.decodeFromRedirect(mniRes);
        if (mniResStr == null) {
            logError("nullDecodedStrFromSamlResponse", 
                                 LogUtil.CANNOT_DECODE_RESPONSE, null);
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("nullDecodedStrFromSamlResponse"));
        }

        if (debug.messageEnabled()) {
            debug.message(method + "Meta Alias is : "+ metaAlias);
            debug.message(method + "Host role is : " + hostRole);
            debug.message(method + "Relay state is : " + relayState);
            debug.message(method + "MNI Response : " + mniResStr);
        }
                    
        ManageNameIDResponse mniResponse = null;
        try {
            mniResponse = pf.createManageNameIDResponse(mniResStr);
            String realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
            String hostEntity = metaManager.getEntityByMetaAlias(metaAlias);
            String remoteEntityID = mniResponse.getIssuer().getValue();
                Issuer resIssuer = mniResponse.getIssuer();
                String requestId = mniResponse.getInResponseTo();
            SAML2Utils.verifyResponseIssuer(
                            realm, hostEntity, resIssuer, requestId);
                            
            boolean needToVerify = 
               SAML2Utils.getWantMNIResponseSigned(realm, hostEntity, hostRole);
            if (needToVerify == true) {
                String queryString = request.getQueryString();
                boolean valid = SAML2Utils.verifyQueryString(queryString, realm,
                                hostRole, remoteEntityID);
                if (valid == false) {
                    logError("invalidSignInResponse", 
                            LogUtil.MNI_RESPONSE_INVALID_SIGNATURE, null);
                        throw new SAML2Exception(SAML2Utils.bundle.getString(
                            "invalidSignInResponse"));
                }
            }
            
            success = checkMNIResponse(mniResponse, metaAlias, hostRole);
        } catch (SessionException e) {
            logError("invalidSSOToken", 
                                 LogUtil.INVALID_SSOTOKEN, null);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("invalidSSOToken"));
        }
        
        if (debug.messageEnabled()) {
            debug.message(method + "Request success : " + success);
        }
        
        return success;
    }
    private static ManageNameIDResponse processManageNameIDRequest(
                                       ManageNameIDRequest mniRequest,
                                       String metaAlias,
                                       String remoteEntityID,
                                       Map paramsMap) {
        String method = "processManageNameIDRequest: ";
        Status status = null;
        SPAccountMapper spAcctMapper = null;
        IDPAccountMapper idpAcctMapper = null;
        ManageNameIDResponse mniResponse = null;
        String hostEntityID = null;
        String realm = null;
        String hostRole = null;
        String userID = null; 
        
        try {
            realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
            hostEntityID = metaManager.getEntityByMetaAlias(metaAlias);
            hostRole = SAML2Utils.getHostEntityRole(paramsMap);
            if (debug.messageEnabled()) {
                debug.message(method + "Host EntityID is : "+ hostEntityID);
                debug.message(method + "Host role is : " + hostRole);
                debug.message(method + "Realm  is : " + realm);
            }
            
                Issuer reqIssuer = mniRequest.getIssuer();
                String requestId = mniRequest.getID();
            SAML2Utils.verifyRequestIssuer(
                            realm, hostEntityID, reqIssuer, requestId);
            if (hostRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
                idpAcctMapper = 
                    SAML2Utils.getIDPAccountMapper(realm, hostEntityID);
                userID = 
                    idpAcctMapper.getIdentity(mniRequest, hostEntityID, realm);
            } else if (hostRole.equalsIgnoreCase(SAML2Constants.SP_ROLE)) {
                spAcctMapper = 
                    SAML2Utils.getSPAccountMapper(realm, hostEntityID);
                userID = 
                    spAcctMapper.getIdentity(mniRequest, hostEntityID, realm);
            }
                
            if (mniRequest.getTerminate()) {
                if (hostRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
                    removeIDPFedSession(remoteEntityID);
                } else {
                    NameID nameID = getNameIDFromMNIRequest(mniRequest, realm, 
                                        hostEntityID, hostRole);
                    String infoKeyString = 
                            new NameIDInfoKey(nameID.getValue(), 
                                    nameID.getSPNameQualifier(), 
                                    nameID.getNameQualifier()).toValueString(); 
                    removeSPFedSession(infoKeyString);
                }
                
                boolean removed = 
                        removeFedAccount(userID, hostEntityID, remoteEntityID);
                if (removed) {
                    status = 
                        SAML2Utils.generateStatus(SAML2Constants.STATUS_SUCCESS,
                            SAML2Utils.bundle.getString("requestSuccess"));
                }
            } else {
                status = 
                    SAML2Utils.generateStatus(SAML2Constants.RESPONDER_ERROR, 
                        SAML2Utils.bundle.getString("unsupportedRequest"));
            }
        } catch (Exception e) {
            status = SAML2Utils.generateStatus(
                         SAML2Constants.RESPONDER_ERROR, e.toString());
        }
        
        try {
            String responseID = SAML2Utils.generateID();
            if (responseID == null) {
                debug.error(
                        SAML2Utils.bundle.getString("failedToGenResponseID"));
            }
            mniResponse = pf.createManageNameIDResponse();
            mniResponse.setStatus(status);
            mniResponse.setID(responseID);
            mniResponse.setInResponseTo(mniRequest.getID());
            mniResponse.setVersion(SAML2Constants.VERSION_2_0);
            mniResponse.setIssueInstant(new Date());
            mniResponse.setIssuer(SAML2Utils.createIssuer(hostEntityID));
        } catch (SAML2Exception e) {
            debug.error("Error : ", e);
        }
        
        return mniResponse;
    }
    
    private static void sendMNIResponse(HttpServletResponse response,
                                           ManageNameIDResponse mniResponse, 
                                           String mniURL,
                                           String relayState,
                                           String realm, 
                                           String hostEntity,
                                           String hostEntityRole, 
                                           String remoteEntity)        
        throws SAML2Exception {
        String method = "sendMNIResponse: ";
            
        try {
            String mniResXMLString = mniResponse.toXMLString(true, true);
            // encode the xml string
            String encodedXML = SAML2Utils.encodeForRedirect(mniResXMLString);
                
            StringBuffer queryString = 
                        new StringBuffer().append(SAML2Constants.SAML_RESPONSE)
                                          .append(SAML2Constants.EQUAL)
                                          .append(encodedXML);
                
            if (relayState != null && relayState.length() > 0 
                                && relayState.getBytes("UTF-8").length <= 80) {
                queryString.append("&").append(SAML2Constants.RELAY_STATE)
                           .append("=").append(URLEncDec.encode(relayState));
            }
            if (debug.messageEnabled()) {
                debug.message(method + "MNI Response is : " + mniResXMLString);
                debug.message(method + "Relay State is : " + relayState);
            }
                
            boolean needToSign = false; 
            if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
                needToSign = 
                    SAML2Utils.getWantMNIResponseSigned(realm, remoteEntity, 
                                   SAML2Constants.SP_ROLE);
            } else {
                needToSign = 
                    SAML2Utils.getWantMNIResponseSigned(realm, remoteEntity, 
                                   SAML2Constants.IDP_ROLE);
            }
                
            String signedQueryString = queryString.toString();
            if (needToSign == true) {
                if (debug.messageEnabled()) {
                    debug.message(method + 
                                    "QueryString has need to be signed.");
                }
                signedQueryString = 
                    SAML2Utils.signQueryString(signedQueryString, realm, 
                                   hostEntity, hostEntityRole);
            }
                        
            String redirectURL = new StringBuffer().append(mniURL).append("?")
                                          .append(signedQueryString).toString();
            if (debug.messageEnabled()) {
                debug.message(method + "redirectURL is : " + redirectURL);
            }
                
            response.sendRedirect(redirectURL);
        } catch (java.io.IOException ioe) {
            if (debug.messageEnabled()) {
                debug.message("Exception when redirecting to " +
                            relayState, ioe);
            }
        }
    }

    static private ManageNameIDRequest createTerminateRequest(
                        Object session,
                        String metaAlias,
                        String hostEntityRole,
                        String remoteEntityID,
                        String destination) 
        throws SAML2Exception {
        String method = "createTerminateRequest: ";
        ManageNameIDRequest mniRequest = null;
        NameID nameID = null;
        String userID = null;
        String realm = null;
        String hostEntityID = null;
        
        try {
            realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
            hostEntityID = metaManager.getEntityByMetaAlias(metaAlias);
            userID = sessionProvider.getPrincipalName(session);
            nameID = getNameID(userID, hostEntityID, remoteEntityID);
        } catch (SessionException e) {
            logError("invalidSSOToken", 
                               LogUtil.INVALID_SSOTOKEN, null);
            throw new SAML2Exception(
                      SAML2Utils.bundle.getString("invalidSSOToken"));       
        }
        if (debug.messageEnabled()) {
            debug.message(method + "MetaAlias : " + metaAlias);
            debug.message(method + "Host EntityID : " + hostEntityID);
            debug.message(method + "User ID : " + userID);
            debug.message(method + "NameID : " + nameID.toXMLString());
        }
        
        mniRequest = pf.createManageNameIDRequest();
        
        mniRequest.setID(SAML2Utils.generateID());
        mniRequest.setVersion(SAML2Constants.VERSION_2_0);
        mniRequest.setDestination(destination);
        mniRequest.setIssuer(SAML2Utils.createIssuer(hostEntityID));
        mniRequest.setIssueInstant(new Date());
        setNameIDForMNIRequest(mniRequest, nameID, realm, hostEntityID, 
                               hostEntityRole, remoteEntityID);
        mniRequest.setTerminate(true);
        
        return mniRequest;
    }
    
    static private ManageNameIDRequest getMNIRequest(HttpServletRequest request)
        throws SAML2Exception {
        String samlRequest =
                request.getParameter(SAML2Constants.SAML_REQUEST);
                
        if (samlRequest == null) {
            logError("nullManageIDRequest", 
                         LogUtil.CANNOT_INSTANTIATE_MNI_REQUEST , samlRequest);
            throw new SAML2Exception(
                          SAML2Utils.bundle.getString("nullManageIDRequest"));
        }
        
        String decodedStr = SAML2Utils.decodeFromRedirect(samlRequest);
                
        if (decodedStr == null) {
            logError("nullDecodedStrFromSamlRequest", 
                     LogUtil.CANNOT_DECODE_REQUEST , samlRequest);
            throw new SAML2Exception(
                  SAML2Utils.bundle.getString("nullDecodedStrFromSamlRequest"));
        }   
        
        return pf.createManageNameIDRequest(decodedStr);
    }
    
    // This is the application code for handling the message.
    static private ManageNameIDRequest getMNIRequest(SOAPMessage message)
                throws SAML2Exception {
        Element reqElem = SAML2Utils.getSamlpElement(message, 
            "ManageNameIDRequest");
        ManageNameIDRequest manageRequest = 
            pf.createManageNameIDRequest(reqElem);
        return manageRequest;
    }
    
    static private void doMNIByHttpRedirect(
        String mniRequestXMLString,
        String mniURL,
        String relayState,
        String realm, 
        String hostEntity,
        String hostEntityRole, 
        String remoteEntity,        
        HttpServletResponse response) throws SAML2Exception, IOException {
        String method = "doMNIByHttpRedirect: ";
        // encode the xml string
        String encodedXML = SAML2Utils.encodeForRedirect(mniRequestXMLString);
        
        StringBuffer queryString = 
                new StringBuffer().append(SAML2Constants.SAML_REQUEST)
                                  .append(SAML2Constants.EQUAL)
                                  .append(encodedXML);
        
        if (relayState != null && relayState.length() > 0 
                         && relayState.getBytes("UTF-8").length <= 80) {
            queryString.append("&").append(SAML2Constants.RELAY_STATE)
                           .append("=").append(URLEncDec.encode(relayState));
        }
        
        boolean needToSign = false; 
        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
            needToSign = 
                SAML2Utils.getWantMNIRequestSigned(realm, remoteEntity, 
                                   SAML2Constants.SP_ROLE);
        } else {
            needToSign = 
                SAML2Utils.getWantMNIRequestSigned(realm, remoteEntity, 
                                   SAML2Constants.IDP_ROLE);
        }
        
        String signedQueryString = queryString.toString();
        if (needToSign == true) {
            signedQueryString = SAML2Utils.signQueryString(signedQueryString, 
                            realm, hostEntity, hostEntityRole);
        }
        
        String redirectURL = 
                new StringBuffer().append(mniURL).append("?")
                                  .append(signedQueryString).toString();
        if (debug.messageEnabled()) {
            debug.message(method + "MNIRequestXMLString : " 
                                          + mniRequestXMLString);
            debug.message(method + "MNIRedirectURL : " + mniURL);
        }
        
        response.sendRedirect(redirectURL);
    }

    static private boolean doMNIBySOAP(
                        String mniRequestXMLString,
                        String mniURL,
                        String metaAlias,
                        String hostRole) throws SAML2Exception {

        String method = "doMNIBySOAP: ";
        boolean success = false;

        if (debug.messageEnabled()) {
            debug.message(method + "MNIRequestXMLString : " 
                                          + mniRequestXMLString);
            debug.message(method + "MNIRedirectURL : " + mniURL);
        }
        
        SOAPMessage resMsg = null;
        try {
            resMsg = SAML2Utils.sendSOAPMessage(mniRequestXMLString, mniURL);
        } catch (SOAPException se) {
            debug.error(SAML2Utils.bundle.getString("invalidSOAPMessge"), se);
            return false;
        }
        
        Element mniRespElem = SAML2Utils.getSamlpElement(resMsg,
             "ManageNameIDResponse");
        ManageNameIDResponse mniResponse = 
            mniResponse = pf.createManageNameIDResponse(mniRespElem);
        
        if (debug.messageEnabled()) {
            if (mniResponse != null) {
                debug.message(method + "ManageNameIDResponse without "+
                    "SOAP envelope:\n" + mniResponse.toXMLString());
            }
        }

        try {
            String realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
            String hostEntity = metaManager.getEntityByMetaAlias(metaAlias);
            String remoteEntityID = mniResponse.getIssuer().getValue();
                Issuer resIssuer = mniResponse.getIssuer();
                String requestId = mniResponse.getInResponseTo();
            SAML2Utils.verifyResponseIssuer(
                            realm, hostEntity, resIssuer, requestId);
                    
            boolean validSign = 
                    verifyMNIResponse(mniResponse, realm, remoteEntityID, 
                                        hostEntity, hostRole);
            if (validSign == false) {
                logError("invalidSignInResponse", 
                         LogUtil.CANNOT_INSTANTIATE_MNI_RESPONSE , null);
                throw new SAML2Exception(
                      SAML2Utils.bundle.getString("invalidSignInResponse"));
            }
            success = checkMNIResponse(mniResponse, metaAlias, hostRole);
        } catch (SessionException e) {
            debug.error(SAML2Utils.bundle.getString("invalidSSOToken"), e);
            throw new SAML2Exception(e.toString());
        }
        
        if (debug.messageEnabled()) {
            debug.message(method + "Request success : " + success);
        }
        return success;
    }

    private static boolean checkMNIResponse(ManageNameIDResponse mniResponse,
                    String metaAlias,  String hostRole)
        throws SAML2Exception, SessionException {
        boolean success = false;
        
        String remoteEntityID = mniResponse.getIssuer().getValue();
        String requestID = mniResponse.getInResponseTo();
        String hostEntity = metaManager.getEntityByMetaAlias(metaAlias);
        ManageNameIDRequestInfo reqInfo = 
                            getMNIRequestInfo(requestID, hostRole);
        if (reqInfo == null) {
            logError("invalidInResponseToInResponse", 
                     LogUtil.INVALID_MNI_RESPONSE , null);
            throw new SAML2Exception(
                 SAML2Utils.bundle.getString("invalidInResponseToInResponse"));
        }
                    
        String retCode = 
            mniResponse.getStatus().getStatusCode().getValue();
        if (retCode.equalsIgnoreCase(SAML2Constants.STATUS_SUCCESS)) {
            Object session = reqInfo.getSession();
            if (session == null) {
                logError("nullSSOToken", LogUtil.INVALID_SSOTOKEN , null);
                throw new SAML2Exception(
                        SAML2Utils.bundle.getString("nullSSOToken"));
            }
            String userID = sessionProvider.getPrincipalName(session);
                        
            if (hostRole.equalsIgnoreCase(SAML2Constants.SP_ROLE)) {
                String nameIDValue = 
                    reqInfo.getManageNameIDRequest().getNameID().getValue();
                NameIDInfoKey infoKey = 
                    new NameIDInfoKey(nameIDValue,
                                              hostEntity, remoteEntityID);
                String infoKeyStr = infoKey.toValueString();
                removeSPFedSession(infoKeyStr);
                removeInfoKeyFromSession(session, infoKeyStr);
            } else {
                removeIDPFedSession(remoteEntityID);
            }

            success = removeFedAccount(userID, hostEntity, remoteEntityID);
        } else {
            logError("mniFailed", LogUtil.INVALID_MNI_RESPONSE , null);
            throw new SAML2Exception(SAML2Utils.bundle.getString("mniFailed"));
        }
        
        return success;
    }

    private static ManageNameIDRequestInfo getMNIRequestInfo(
        String requestID, String hostRole) {
        ManageNameIDRequestInfo reqInfo = null;
        if (hostRole.equalsIgnoreCase(SAML2Constants.SP_ROLE)) {
            reqInfo = (ManageNameIDRequestInfo)
                          SPCache.mniRequestHash.remove(requestID);
        } else if (hostRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
            reqInfo = (ManageNameIDRequestInfo)
                          IDPCache.mniRequestHash.remove(requestID);
        }
        return reqInfo;
    }
    
    static private boolean removeFedAccount(String userID,
                    String hostEntityID,
                    String remoteEntityID) throws SAML2Exception {
        NameIDInfo nameInfo = AccountUtils.getAccountFederation(
            userID, hostEntityID, remoteEntityID);

        return AccountUtils.removeAccountFederation(nameInfo, userID);
    }
    
    private static ManageNameIDServiceElement getMNIServiceElement(
                    String realm, String entityID,  
                    String hostEntityRole, String binding)
        throws SAML2MetaException, SessionException, SAML2Exception {
        ManageNameIDServiceElement mniService = null;
        String method = "getMNIServiceElement: ";
        
        if (debug.messageEnabled()) {
            debug.message(method + "Realm : " + realm);
            debug.message(method + "Entity ID : " + entityID);
            debug.message(method + "Host Entity Role : " + hostEntityRole);
        }

        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.SP_ROLE)) {
            mniService = getIDPManageNameIDConfig(realm, entityID, binding);
        } else if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)){
            mniService = getSPManageNameIDConfig(realm, entityID, binding);
        } else {
            logError("nullHostEntityRole", 
                             LogUtil.MISSING_ENTITY_ROLE , null);
            throw new SAML2Exception(
                SAML2Utils.bundle.getString("nullHostEntityRole"));
        }
        
        return mniService;
    }

    static private NameID getNameID(String userID, 
                        String hostEntityID,
                        String remoteEntityID) throws SAML2Exception {
        String method = "getNameID: ";
        NameID nameID = null;
        NameIDInfo nameIDInfo = AccountUtils.getAccountFederation(userID,
                        hostEntityID, remoteEntityID);
        if (nameIDInfo != null) {
            nameID = nameIDInfo.getNameID();
            if (debug.messageEnabled()) {
                debug.message(method + "Returned NameID for " + userID + ":");
                debug.message(nameID.toXMLString());
            }
        } else {
            debug.error(SAML2Utils.bundle.getString("nullNameID"));
            throw new SAML2Exception(SAML2Utils.bundle.getString("nullNameID"));
        }
        
        return nameID;
    }    
    
    static private void setNameIDForMNIRequest(ManageNameIDRequest mniRequest, 
                          NameID nameID, String realm, String hostEntity,
                          String hostEntityRole, String remoteEntity)
        throws SAML2Exception {
        String method = "setNameIDForMNIRequest: ";
        boolean needEncryptIt = false;
        
        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
            needEncryptIt = 
                SAML2Utils.getWantNameIDEncrypted(realm, remoteEntity, 
                           SAML2Constants.SP_ROLE);
        } else {
            needEncryptIt = 
                SAML2Utils.getWantNameIDEncrypted(realm, remoteEntity, 
                           SAML2Constants.IDP_ROLE);
        }
        
        if (needEncryptIt == false) {
            if (debug.messageEnabled()) {
                debug.message(method + "NamID doesn't need to be encrypted.");
            }
            mniRequest.setNameID(nameID);
            return;
        }
        
        EncInfo encryptInfo = null;
        KeyDescriptorType keyDescriptor = null;
        if (hostEntityRole.equalsIgnoreCase(SAML2Constants.IDP_ROLE)) {
            SPSSODescriptorElement spSSODesc =
                metaManager.getSPSSODescriptor(realm, remoteEntity);
            keyDescriptor = KeyUtil.getKeyDescriptor(spSSODesc, "encryption");
            encryptInfo = KeyUtil.getEncInfo(spSSODesc, remoteEntity, false);
        } else {
            IDPSSODescriptorElement idpSSODesc = 
                 metaManager.getIDPSSODescriptor(realm, remoteEntity);
            keyDescriptor = KeyUtil.getKeyDescriptor(idpSSODesc, "encryption");
            encryptInfo = KeyUtil.getEncInfo(idpSSODesc, remoteEntity, true);
        }

        if (debug.messageEnabled()) {
            debug.message(method + "realm is : "+ realm);
            debug.message(method + "hostEntity is : " + hostEntity);
            debug.message(method + "Host Entity role is : " + hostEntityRole);
            debug.message(method + "remoteEntity is : " + remoteEntity);
        }
        
        if (encryptInfo == null) {
            logError("UnableToFindEncryptKeyInfo", 
                             LogUtil.METADATA_ERROR , null);
            throw new SAML2Exception(
                     SAML2Utils.bundle.getString("UnableToFindEncryptKeyInfo"));
        }
        
        X509Certificate certificate = KeyUtil.getCert(keyDescriptor);
        PublicKey recipientPublicKey = certificate.getPublicKey();
        EncryptedID encryptedID = nameID.encrypt(recipientPublicKey, 
                                  encryptInfo.getDataEncAlgorithm(), 
                                   encryptInfo.getDataEncStrength(), 
                                                      remoteEntity);
        // This non-encrypted NameID will be removed just 
        // after saveMNIRequestInfo and just before it send to 
        mniRequest.setNameID(nameID);
        mniRequest.setEncryptedID(encryptedID);
    }    

    static private NameID getNameIDFromMNIRequest(ManageNameIDRequest request, 
                        String realm, String hostEntity, String hostEntityRole)
        throws SAML2Exception {
        String method = "getNameIDFromMNIRequest: ";
        String alias = null;
        
        boolean needDecryptIt = SAML2Utils.getWantNameIDEncrypted(realm, 
                                               hostEntity, hostEntityRole);
        
        if (needDecryptIt == false) {
            if (debug.messageEnabled()) {
                debug.message(method + "NamID doesn't need to be decrypted.");
            }
            return request.getNameID();
        }
        
        alias = SAML2Utils.getEncryptionCertAlias(realm, hostEntity, 
                               hostEntityRole);

        if (debug.messageEnabled()) {
            debug.message(method + "realm is : "+ realm);
            debug.message(method + "hostEntity is : " + hostEntity);
            debug.message(method + "Host Entity role is : " + hostEntityRole);
            debug.message(method + "Cert Alias is : " + alias);
        }
        
        PrivateKey privateKey = keyProvider.getPrivateKey(alias);
        EncryptedID encryptedID = request.getEncryptedID();
        
        return encryptedID.decrypt(privateKey);
    }    

    /**
     * Returns first ManageNameID configuration in an entity under
     * the realm.
     * @param realm The realm under which the entity resides.
     * @param entityId ID of the entity to be retrieved.
     * @param binding bind type need to has to be matched.
     * @return <code>ManageNameIDServiceElement</code> for the entity or null
     * @throws SAML2MetaException if unable to retrieve the first identity
     *                            provider's SSO configuration.
     * @throws SessionException invalid or expired single-sign-on session
     */
    static public ManageNameIDServiceElement getIDPManageNameIDConfig(
                                                 String realm, 
                                                 String entityId,
                                                 String binding)
        throws SAML2MetaException, SessionException {
        ManageNameIDServiceElement mni = null;

        IDPSSODescriptorElement idpSSODesc = 
                    metaManager.getIDPSSODescriptor(realm, entityId);
        if (idpSSODesc == null) {
                debug.error(SAML2Utils.bundle.getString("noIDPEntry"));
            return null;
        }

        List list = idpSSODesc.getManageNameIDService();

        if ((list != null) && !list.isEmpty()) {
            if (binding == null) {
                return (ManageNameIDServiceElement)list.get(0);
            }
            Iterator it = list.iterator();
            while (it.hasNext()) {
                mni = (ManageNameIDServiceElement)it.next();  
                if (binding.equalsIgnoreCase(mni.getBinding())) {
                    break;
                }
            }
        }

        return mni;
    }

    /**
     * Returns first ManageNameID configuration in an entity under
     * the realm.
     * @param realm The realm under which the entity resides.
     * @param entityId ID of the entity to be retrieved.
     * @param binding bind type need to has to be matched.
     * @return <code>ManageNameIDServiceElement</code> for the entity or null
     * @throws SAML2MetaException if unable to retrieve the first identity
     *                            provider's SSO configuration.
     * @throws SessionException invalid or expired single-sign-on session.
     */
    static public ManageNameIDServiceElement getSPManageNameIDConfig(
                                                String realm, String entityId,
                                                String binding)
        throws SAML2MetaException, SessionException {
        ManageNameIDServiceElement mni = null;

        SPSSODescriptorElement spSSODesc = 
                          metaManager.getSPSSODescriptor(realm, entityId);
        if (spSSODesc == null) {
            return null;
        }

        List list = spSSODesc.getManageNameIDService();

        if ((list != null) && !list.isEmpty()) {
            if (binding == null) {
                return (ManageNameIDServiceElement)list.get(0);
            }
            Iterator it = list.iterator();
            while (it.hasNext()) {
                mni = (ManageNameIDServiceElement)it.next();  
                if (binding.equalsIgnoreCase(mni.getBinding())) {
                    break;
                }
            }
        }

        return mni;
    }
    
    static private void removeSPFedSession(String infoKey)
        throws SessionException {
        String method = "removeSPFedSession ";
                
        if (SPCache.fedSessionListsByNameIDInfoKey != null) {
            SPCache.fedSessionListsByNameIDInfoKey.remove(infoKey);
        } else {
            if (debug.messageEnabled()) {
                debug.message(method + 
                    "SPCache.fedSessionListsByNameIDInfoKey is null.");
            }
            return;
        }
    }

    static private void removeIDPFedSession(String spEntity)
        throws SessionException {
        String method = "removeIDPFedSession ";
        Enumeration keys = null;
        String idpSessionIndex = null;
        IDPSession idpSession = null;
        
        if (IDPCache.idpSessionsByIndices != null) {
            keys = IDPCache.idpSessionsByIndices.keys();
        } else {
            if (debug.messageEnabled()) {
                debug.message(method+"IDPCache.idpSessionsByIndices is null.");
            }

            return;
        }
        
        if (keys == null) {
            if (debug.messageEnabled()) {
                debug.message(method + 
                   "IDPCache.idpSessionsByIndices return null.");
            }
            return;
        }
        
        while (keys.hasMoreElements()) {
            NameIDandSPpair nameIDPair = null;
            idpSessionIndex = (String)keys.nextElement();   
            idpSession = (IDPSession)IDPCache.
                    idpSessionsByIndices.get(idpSessionIndex);
            if (idpSession != null) {
                List nameIDSPlist = idpSession.getNameIDandSPpairs();
                if (nameIDSPlist != null) {
                    Iterator iter = nameIDSPlist.listIterator();
                    while (iter.hasNext()) {
                        nameIDPair = (NameIDandSPpair)iter.next();
                        String spID = nameIDPair.getSPEntityID();
                        if (spID.equalsIgnoreCase(spEntity)) {
                            iter.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    static private void removeInfoKeyFromSession(
        Object session, String infoKey) throws SessionException {
        
        String method = "removeInfoKeyFromSession ";
        String infoKeyString = null;
        String[] values = sessionProvider.getProperty(
            session, AccountUtils.getNameIDInfoKeyAttribute());
        if (values != null && values.length > 0) {
            infoKeyString = values[0];
        }
        if (infoKeyString == null) {
            if (debug.messageEnabled()) {
                debug.message(method+"InfoKeyString from session is null.");
            }
            return;
        }

        if (debug.messageEnabled()) {
            debug.message(method+"InfoKeyString from session : " 
                                + infoKeyString);
            debug.message(method+"InfoKey need to delete : " + infoKey);
        }

        StringTokenizer st =
                new StringTokenizer(infoKeyString, SAML2Constants.SECOND_DELIM);
        StringBuffer newInfoKey = new StringBuffer();
        if (st != null && st.hasMoreTokens()) {
            while (st.hasMoreTokens()) {
                String tmpInfoKey = (String)st.nextToken();
                debug.message(method+"InfoKey from session : " + tmpInfoKey);
                if (infoKey.equals(tmpInfoKey)) {
                    continue;
                }
                
                if (newInfoKey.length() > 0){
                    newInfoKey.append(SAML2Constants.SECOND_DELIM);
                }
                newInfoKey.append(tmpInfoKey);
            }
            if (debug.messageEnabled()) {
                debug.message(method+"New InfoKey to session : " 
                                + newInfoKey.toString());
            }
            String[] v = { newInfoKey.toString() };
            sessionProvider.setProperty(
                session, AccountUtils.getNameIDInfoKeyAttribute(), v);
            if (debug.messageEnabled()) {
                debug.message(method+"New InfoKey from session : " +
                    sessionProvider.getProperty(
                        session, AccountUtils.getNameIDInfoKeyAttribute()));
            }
        } else {
            debug.message(method+"No InfoKey to remove.");
            return;
        }
    }    
}

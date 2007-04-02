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
 * $Id: SPSSOFederate.java,v 1.5 2007-04-02 23:34:25 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.profile;

import com.sun.identity.shared.encode.URLEncDec;
import com.sun.identity.shared.datastruct.OrderedSet;
import com.sun.identity.saml.xmlsig.KeyProvider;
import com.sun.identity.saml2.assertion.AssertionFactory;
import com.sun.identity.saml2.assertion.Issuer;
import com.sun.identity.saml2.logging.LogUtil;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.common.QuerySignatureUtil;
import com.sun.identity.saml2.key.KeyUtil;
import com.sun.identity.saml2.protocol.ProtocolFactory;
import com.sun.identity.saml2.protocol.AuthnRequest;
import com.sun.identity.saml2.protocol.Extensions;
import com.sun.identity.saml2.protocol.NameIDPolicy;
import com.sun.identity.saml2.protocol.RequestedAuthnContext;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.jaxb.metadata.AssertionConsumerServiceElement;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.IDPSSODescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.SPSSODescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.SingleSignOnServiceElement;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.plugins.SPAuthnContextMapper;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.logging.Level;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class reads the query parameters and performs the required
 * processing logic for sending Authentication Request
 * from SP to IDP.
 *
 */

public class SPSSOFederate {
 
    static SAML2MetaManager sm = null;
    static {
        try {
            sm = new SAML2MetaManager();
        } catch (SAML2MetaException sme) {
            SAML2Utils.debug.error("SPSSOFederate: Error retreiving metadata"
                                    ,sme);
        }
    }

    /**
     * Parses the request parameters and builds the Authentication
     * Request to sent to the IDP.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @param metaAlias metaAlias to locate the service providers.
     * @param idpEntityID entityID of Identity Provider.
     * @param paramsMap Map of all other parameters.The key in the
     *              map are of the type String. The values in the paramsMap
     *              are of the type List.
     *              Some of the possible keys are:RelayState,NameIDFormat,
     *              binding, AssertionConsumerServiceIndex,
     *              AttributeConsumingServiceIndex (currently not supported),
     *              isPassive, ForceAuthN, AllowCreate, Destination,
     *              AuthnContextDeclRef, AuthnContextClassRef,
     *              AuthComparison, Consent (currently not supported).
     * @throws SAML2Exception if error initiating request to IDP.
     */
    public static void initiateAuthnRequest(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String metaAlias,
                                 String idpEntityID,
                                 Map paramsMap) 
                                 throws SAML2Exception {

        try {
            // get the sp entity ID from the metaAlias
            String spEntityID = sm.getEntityByMetaAlias(metaAlias);
            String realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("SPSSOFederate : spEntityID is :" 
                                            + spEntityID);
                SAML2Utils.debug.message("SPSSOFederate realm is :" + realm);
            }
            initiateAuthnRequest(request,response,spEntityID,
                                 idpEntityID,realm,paramsMap);
        } catch (SAML2MetaException sme) {
            SAML2Utils.debug.error("SPSSOFederate: Error retreiving spEntityID"+
                                   " from MetaAlias",sme);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("metaAliasError"));
        }
    }

    /**
     * Parses the request parameters and builds the Authentication
     * Request to sent to the IDP.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @param spEntityID entityID of Service Provider.
     * @param idpEntityID entityID of Identity Provider.
     * @param paramsMap Map of all other parameters.The key in the
     *              map are the parameter names of the type String. 
     *              The values in the paramsMap are of the type List.
     *              Some of the possible keys are:RelayState,NameIDFormat,
     *              binding, AssertionConsumerServiceIndex,
     *              AttributeConsumingServiceIndex (currently not supported),
     *              isPassive, ForceAuthN, AllowCreate, Destination,
     *              AuthnContextDeclRef, AuthnContextClassRef,
     *              AuthComparison, Consent (currently not supported).
     * @throws SAML2Exception if error initiating request to IDP.
     */
    public static void initiateAuthnRequest(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String spEntityID,
                                 String idpEntityID,
                                 String realmName,
                                 Map paramsMap) 
                                 throws SAML2Exception {

        if (spEntityID == null) {
            SAML2Utils.debug.error("SPSSOFederate:Service Provider ID  "
                                   + " is missing.");
            String[] data = {spEntityID};
            LogUtil.error(Level.INFO,LogUtil.INVALID_SP,data,null);
            throw new SAML2Exception(
                 SAML2Utils.bundle.getString("nullSPEntityID"));
        }
        
        if (idpEntityID == null)  {
            SAML2Utils.debug.error("SPSSOFederate: Identity Provider ID " 
                                    + "is missing .");
            String[] data = { idpEntityID };
            LogUtil.error(Level.INFO,LogUtil.INVALID_IDP,data,null);
            throw new SAML2Exception(
                 SAML2Utils.bundle.getString("nullIDPEntityID"));
        }
        
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPSSOFederate: in initiateSSOFed");
            SAML2Utils.debug.message("SPSSOFederate: spEntityID is : "
                                      + spEntityID);
            SAML2Utils.debug.message("SPSSOFederate: idpEntityID : " 
                                      + idpEntityID);
        }
        
        String realm = getRealm(realmName);
        
        try {
            // Retreive MetaData 
            if (sm == null) {
                throw new SAML2Exception(
                    SAML2Utils.bundle.getString("errorMetaManager"));
            }
            SPSSOConfigElement spEntityCfg = 
                            sm.getSPSSOConfig(realm,spEntityID);
            Map spConfigAttrsMap=null;
            if (spEntityCfg != null) {
                spConfigAttrsMap = SAML2MetaUtils.getAttributes(spEntityCfg);
            }
             // get SPSSODescriptor
            SPSSODescriptorElement spsso = 
                                sm.getSPSSODescriptor(realm,spEntityID);

            if (spsso == null) {
                String[] data = { spEntityID };
                LogUtil.error(Level.INFO,LogUtil.SP_METADATA_ERROR,data,
                              null);
                throw new SAML2Exception(
                        SAML2Utils.bundle.getString("metaDataError"));
            }
            List extensionsList = getExtensionsList(sm,spEntityID,realm);
            
            // get IDP Descriptor
            IDPSSODescriptorElement idpsso =
                    sm.getIDPSSODescriptor(realm,idpEntityID);

            if (idpsso == null) {
                String[] data = { idpEntityID };
                LogUtil.error(Level.INFO,LogUtil.IDP_METADATA_ERROR,data,
                              null);
                throw new SAML2Exception(
                        SAML2Utils.bundle.getString("metaDataError"));
            }
            
            List ssoServiceList = idpsso.getSingleSignOnService();
            String ssoURL = getSSOURL(ssoServiceList);

            if (ssoURL == null || ssoURL.length() == 0) {
              String[] data = { idpEntityID };
              LogUtil.error(Level.INFO,LogUtil.SSO_NOT_FOUND,data,
                            null);
              throw new SAML2Exception(
                        SAML2Utils.bundle.getString("ssoServiceNotfound"));
            }
        
            // create AuthnRequest 
            AuthnRequest authnRequest = createAuthnRequest(realm,spEntityID,
                    paramsMap,spConfigAttrsMap,extensionsList,spsso,
                    ssoURL);
                
            String authReqXMLString = authnRequest.toXMLString(true,true);
        
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("SPSSOFederate: AuthnRequest:" 
                                          +authReqXMLString);
            }
            // encode the xml string
            String encodedXML = SAML2Utils.encodeForRedirect(authReqXMLString);
        
            StringBuffer queryString = 
                new StringBuffer().append(SAML2Constants.SAML_REQUEST)
                                  .append(SAML2Constants.EQUAL)
                                  .append(encodedXML);
        
            // Default URL if relayState not present? in providerConfig?
            // TODO get Default URL from metadata 
            String relayState = getParameter(paramsMap,
                                             SAML2Constants.RELAY_STATE);

            // check if relayState is present and get the unique
            // id which will be appended to the SSO URL before
            // redirecting.
            if (relayState != null && relayState.length()> 0) {
                String relayStateID = getRelayStateID(relayState,
                                                      authnRequest.getID());

                if (relayStateID != null && relayStateID.length() > 0) {
                    queryString.append("&").append(SAML2Constants.RELAY_STATE)
                               .append("=")
                               .append(URLEncDec.encode(relayStateID));
                }
            }
            StringBuffer redirectURL = 
                new StringBuffer().append(ssoURL).append("?");
            // sign the query string
            if ((idpsso != null && idpsso.isWantAuthnRequestsSigned()) ||
                (spsso != null && spsso.isAuthnRequestsSigned()) ) {
                String certAlias = 
                        getParameter(spConfigAttrsMap,
                                     SAML2Constants.SIGNING_CERT_ALIAS);
                    String signedQueryStr = 
                        signQueryString(queryString.toString(),certAlias);
                redirectURL.append(signedQueryStr);
            } else {
                redirectURL.append(queryString);
            }
            response.sendRedirect(redirectURL.toString());
            String[] data = { ssoURL };
            LogUtil.access(Level.INFO,LogUtil.REDIRECT_TO_SP,data,
                           null);
            AuthnRequestInfo reqInfo = 
                new AuthnRequestInfo(request,response,realm,spEntityID,
                                     idpEntityID,authnRequest,relayState,
                                     paramsMap);
            synchronized(SPCache.requestHash) {             
                SPCache.requestHash.put(authnRequest.getID(),reqInfo);
            } 
        } catch (IOException ioe) {
            SAML2Utils.debug.error("SPSSOFederate: Exception :",ioe);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("errorCreatingAuthnRequest"));
        } catch (SAML2MetaException sme) {
            SAML2Utils.debug.error("SPSSOFederate:Error retreiving metadata"
                                    ,sme);
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("metaDataError"));            
        }
    }

    /* Create NameIDPolicy Element */
    private static NameIDPolicy createNameIDPolicy(String spEntityID,
            String nameIdentifier,boolean allowCreate,
            SPSSODescriptorElement spsso)
            throws SAML2Exception {
        
        String nameID = SAML2Constants.PERSISTENT;
        if ((nameIdentifier != null) && (nameIdentifier.length() > 0)) {
            nameID = new StringBuffer()
                    .append(SAML2Constants.NAMEID_FORMAT_NAMESPACE)
                    .append(nameIdentifier).toString();

            if (nameID.equals(SAML2Constants.UNSPECIFIED)) {
                nameID = SAML2Constants.PERSISTENT;
            }
        }
        
        // get NameID Formats supported by SP from SPSSO Descriptor
        if (spsso != null) {
            List nameIDFormatList = spsso.getNameIDFormat();
            if (nameIDFormatList != null && !nameIDFormatList.isEmpty()
            && !nameIDFormatList.contains(nameID)) {
                // should be error or use default Persistent?
                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message("SPSSOFederate: NameIDFormat " 
                            + "not supported" +nameID);
                    SAML2Utils.debug.message("SPSSOFederate: Using Default : " +
                                        SAML2Constants.PERSISTENT);
                }
            }
        }
        NameIDPolicy nameIDPolicy =
                ProtocolFactory.getInstance().createNameIDPolicy();
        nameIDPolicy.setSPNameQualifier(spEntityID);
        nameIDPolicy.setAllowCreate(allowCreate);
        nameIDPolicy.setFormat(nameID);
        return nameIDPolicy;
    }
    
    /* Create Issuer */
    private static Issuer createIssuer(String spEntityID) 
                throws SAML2Exception {
        Issuer issuer = AssertionFactory.getInstance().createIssuer();
        issuer.setValue(spEntityID);
        return issuer;
    }
    
    /* Create AuthnRequest */
    private static AuthnRequest createAuthnRequest(String realmName,
        String spEntityID,
        Map paramsMap,
        Map spConfigMap,
        List extensionsList,
        SPSSODescriptorElement spsso,
        String ssourl
        ) throws SAML2Exception {
        // generate unique request ID
        String requestID = SAML2Utils.generateID();
        if ((requestID == null) || (requestID.length() == 0)) {
            throw new SAML2Exception(
                    SAML2Utils.bundle.getString("cannotGenerateID"));
        }

        // retrieve data from the params map and if not found get
        // default values from the SPConfig Attributes
        // destinationURI required if message is signed.
         String destinationURI= getParameter(paramsMap,
                                             SAML2Constants.DESTINATION);
         Boolean isPassive = doPassive(paramsMap,spConfigMap);
         Boolean isforceAuthn= isForceAuthN(paramsMap,spConfigMap);
         boolean allowCreate=isAllowCreate(paramsMap,spConfigMap);
         String consent = getParameter(paramsMap,SAML2Constants.CONSENT);
         Extensions extensions = createExtensions(extensionsList);
         String nameIdentifier =
                        getParameter(paramsMap,
                                     SAML2Constants.NAMEID_POLICY_FORMAT);
         // get NameIDPolicy Element 
         NameIDPolicy nameIDPolicy = 
                 createNameIDPolicy(spEntityID,nameIdentifier,
                                    allowCreate,spsso);
         Issuer issuer = createIssuer(spEntityID);
         Integer acsIndex = getIndex(paramsMap,SAML2Constants.ACS_URL_INDEX);
         Integer attrIndex = getIndex(paramsMap,SAML2Constants.ATTR_INDEX);
         
         String protocolBinding= getParameter(paramsMap,SAML2Constants.BINDING);
         OrderedSet acsSet = getACSUrl(spsso,protocolBinding);
         String acsURL = (String) acsSet.get(0);
         protocolBinding = (String)acsSet.get(1);

         RequestedAuthnContext reqAuthnContext = 
                                createReqAuthnContext(realmName,spEntityID,
                                                      paramsMap,spConfigMap);
         
         AuthnRequest authnReq = 
                ProtocolFactory.getInstance().createAuthnRequest();    
         if ((destinationURI == null) || (destinationURI.length() == 0)) {
             authnReq.setDestination(ssourl);
         } else {
             authnReq.setDestination(destinationURI);
         }
         authnReq.setConsent(consent);
         authnReq.setIsPassive(isPassive);
         authnReq.setForceAuthn(isforceAuthn);
         authnReq.setAttributeConsumingServiceIndex(attrIndex);
         authnReq.setAssertionConsumerServiceIndex(acsIndex);
         authnReq.setAssertionConsumerServiceURL(acsURL);
         authnReq.setProtocolBinding(protocolBinding);
         authnReq.setIssuer(issuer);
         authnReq.setNameIDPolicy(nameIDPolicy);
         authnReq.setRequestedAuthnContext(reqAuthnContext);
         if (extensions != null) {
               authnReq.setExtensions(extensions);
         }
        
        // Required attributes in authn request
        authnReq.setID(requestID);
        authnReq.setVersion(SAML2Constants.VERSION_2_0);
        authnReq.setIssueInstant(new Date());
        return authnReq;        
    }

    /* Returns the SingleSignOnService URL */
    static String getSSOURL(List ssoServiceList) {
         String ssoURL = null;
         String binding = SAML2Constants.HTTP_REDIRECT;
         if ((ssoServiceList != null) && (!ssoServiceList.isEmpty())) {
            Iterator i = ssoServiceList.iterator();
            while (i.hasNext()) {
                SingleSignOnServiceElement sso = 
                            (SingleSignOnServiceElement) i.next();
                if ((sso != null && sso.getBinding()!=null) && 
                            (sso.getBinding().equals(binding))) {
                    ssoURL = sso.getLocation();
                    break;
                }
                    
            }
         }
         if (SAML2Utils.debug.messageEnabled()) {
               SAML2Utils.debug.message("SPSSOFederate: "
                                         + " SingleSignOnService URL :" 
                                         + ssoURL);
         }
         return ssoURL;
    }
            
    /* Returns value of parameter in the SP SSO Config */
    private static Boolean getAttrValueFromMap(Map attrMap,String attrName) {
        Boolean boolVal = null;
        if (attrMap!=null && attrMap.size()> 0) {
            String attrVal = getParameter(attrMap,attrName);
            if ((attrVal != null) 
                && ( (attrVal.equals(SAML2Constants.TRUE)) 
                || (attrVal.equals(SAML2Constants.FALSE)))) {
                    boolVal = new Boolean(attrVal);
            }
        }
        return boolVal;
    }

    /**
     * Returns an Ordered Set containing the AssertionConsumerServiceURL
     * and AssertionConsumerServiceIndex.
     */
    static OrderedSet getACSUrl(SPSSODescriptorElement spsso,
                                        String binding) {
        String responseBinding = binding;
        if ((binding != null) && (binding.length() > 0) &&
                (binding.indexOf(SAML2Constants.BINDING_PREFIX) == -1)) {
            responseBinding = 
                new StringBuffer().append(SAML2Constants.BINDING_PREFIX)
                                  .append(binding).toString();
        }
        List acsList = spsso.getAssertionConsumerService();
        String acsURL=null;
        if (acsList != null && !acsList.isEmpty()) {
            Iterator ac = acsList.iterator();
            while (ac.hasNext()) {
                AssertionConsumerServiceElement ace =
                    (AssertionConsumerServiceElement) ac.next();
                if ((ace != null && ace.isIsDefault()) && 
                  (responseBinding == null || responseBinding.length() ==0 )) {
                    acsURL = ace.getLocation();
                    responseBinding = ace.getBinding();
                    break;
                } else if ((ace != null) &&
                       (ace.getBinding().equals(responseBinding))) {
                    acsURL = ace.getLocation();
                    break;
                }
            }
        }
        OrderedSet ol = new OrderedSet();
        ol.add(acsURL);
        ol.add(responseBinding);
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPSSOFederate: AssertionConsumerService :"
                                + " URL :" + acsURL);
            SAML2Utils.debug.message("SPSSOFederate: AssertionConsumerService :"
                                     + " Binding Passed in Query: " + binding);
            SAML2Utils.debug.message("SPSSOFederate: AssertionConsumerService :"
                                     + " Binding : " + responseBinding);
        }
        return ol;
    }

    /* Returns the realm */
    private static String getRealm(String realm) {
        return ((realm == null) || (realm.length() == 0)) ? "/" : realm;
    }

    /* Returns value of isPassive attribute */
    private static Boolean doPassive(Map paramsMap,Map spConfigAttrsMap){
        // get isPassive
        Boolean isPassive=Boolean.FALSE;
        String isPassiveStr =
                getParameter(paramsMap,SAML2Constants.ISPASSIVE);
        
        if ((isPassiveStr != null) &&
                ((isPassiveStr.equals(SAML2Constants.TRUE) ||
                (isPassiveStr.equals(SAML2Constants.FALSE))))) {
            isPassive = new Boolean(isPassiveStr);
        } else {
            isPassive = getAttrValueFromMap(spConfigAttrsMap,
                                            SAML2Constants.ISPASSIVE);
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPSSOFederate: isPassive : " + isPassive);
        }
        return (isPassive == null) ? Boolean.FALSE : isPassive;
    }

    /* Returns value of ForceAuthn */
    private static Boolean isForceAuthN(Map paramsMap,Map spConfigAttrsMap) {
        Boolean isforceAuthn= Boolean.FALSE;
        String forceAuthn = getParameter(paramsMap,SAML2Constants.FORCEAUTHN);
        if ((forceAuthn != null) && 
                ((forceAuthn.equals(SAML2Constants.TRUE) ||
                (forceAuthn.equals(SAML2Constants.FALSE))))) {
                isforceAuthn = new Boolean(forceAuthn);
        } else {
            isforceAuthn = getAttrValueFromMap(spConfigAttrsMap,
                                               SAML2Constants.FORCEAUTHN);
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPSSOFederate:ForceAuthn: " + forceAuthn);
        }
        return (isforceAuthn == null) ? Boolean.FALSE : isforceAuthn;
    }
    
    /* get value of AllowCreate */
    private static boolean isAllowCreate(Map paramsMap,Map spConfigAttrsMap) {
        //assuming default true? 
        boolean allowCreate=true;
        String allowCreateStr=getParameter(paramsMap,
                                           SAML2Constants.ALLOWCREATE);
        if ((allowCreateStr != null) &&
                ((allowCreateStr.equals(SAML2Constants.TRUE) ||
                (allowCreateStr.equals(SAML2Constants.FALSE))))
            ) {
            allowCreate = new Boolean(allowCreateStr).booleanValue();
        } else {
            Boolean val = getAttrValueFromMap(spConfigAttrsMap,
                                              SAML2Constants.ALLOWCREATE);
            if (val != null) {
                allowCreate = val.booleanValue();
            }
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPSSOFederate:AllowCreate:"+ allowCreate);
        }
        return allowCreate;
    }
    
    /* Returns the AssertionConsumerServiceURL Index */
    private static Integer getIndex(Map paramsMap,String attrName) {
        Integer attrIndex = null;
        String index = getParameter(paramsMap,attrName);
        if ((index != null) && (index.length() > 0)) {
          attrIndex = new Integer(index);
        }
        return attrIndex;      
    }
  
    /* Returns the query parameter value for the param specified */
    private static String getParameter(Map paramsMap,String attrName) {
        String attrVal = null;
        if ((paramsMap != null) && (!paramsMap.isEmpty())) { 
            List attrValList = (List)paramsMap.get(attrName);
            if (attrValList != null && !attrValList.isEmpty()) {
                attrVal = (String) attrValList.iterator().next();
            }
        }
        return attrVal;
    }
    
    /* Returns the extensions list */
    private static List getExtensionsList(SAML2MetaManager sm,
                                String entityID,String realm) {
        List extensionsList = null;
        try {
            EntityDescriptorElement ed = sm.getEntityDescriptor(realm,entityID);
            if (ed != null) {
                 com.sun.identity.saml2.jaxb.metadata.ExtensionsType ext =
                                    ed.getExtensions();
                if (ext != null) {
                    extensionsList = ext.getAny();
                }
            }
        } catch (SAML2Exception e) {
            SAML2Utils.debug.error("SPSSOFederate:Error retrieving " +
                                "EntityDescriptor");
        }
        return extensionsList;
    }
    
    private static com.sun.identity.saml2.protocol.Extensions 
    createExtensions(List extensionsList) throws SAML2Exception {
        com.sun.identity.saml2.protocol.Extensions extensions=null;
        if (extensionsList != null && !extensionsList.isEmpty()) {
            extensions = 
                        ProtocolFactory.getInstance().createExtensions();
            extensions.setAny(extensionsList);
        }
        return extensions;
    }


    private static String getRelayStateID(String relayState,
                                          String requestID) {
        String relayStateID = null;
        
        if (SPCache.relayStateHash != null) {
            Enumeration e = SPCache.relayStateHash.keys();
            while (e.hasMoreElements() && (relayStateID == null)) {
                String id = (String)e.nextElement();
                CacheObject cacheObj = (CacheObject)SPCache.relayStateHash.get(
                    id);
                if (cacheObj != null) {
                    String value = (String)cacheObj.getObject();
                    if ((value != null) && value.equals(relayState)) {
                        relayStateID = id;
                    }
                }
            }
        }
        if (relayStateID == null) {
            relayStateID = requestID;
            SPCache.relayStateHash.put(relayStateID, 
                new CacheObject(relayState));
        }
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                "SPSSOFederate.getRelayStateID: RelayStateHash : " + 
                SPCache.relayStateHash);
        }
        return relayStateID;
    }


   /* Creates RequestedAuthnContext Object */
   private static RequestedAuthnContext createReqAuthnContext(String realmName,
                                String spEntityID,Map paramsMap,
                                Map spConfigMap) {
        RequestedAuthnContext reqCtx = null;
        String className = null;
        if ((spConfigMap != null) && (!spConfigMap.isEmpty())) {
            List listVal = 
                (List) spConfigMap.get(
                            SAML2Constants.SP_AUTHCONTEXT_MAPPER);
            if (listVal != null && listVal.size() != 0) {
                className = ((String) listVal.iterator().next()).trim();
            }
        }

        SPAuthnContextMapper spAuthnContextMapper = 
            SAML2Utils.getSPAuthnContextMapper(realmName,spEntityID,className);

        try {
            reqCtx = 
                spAuthnContextMapper.getRequestedAuthnContext(
                            realmName,spEntityID,paramsMap);

        } catch (SAML2Exception e) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("SPSSOFederate:Error creating " +
                                         "RequestedAuthnContext",e);
            }
        }

        return reqCtx;
   }

   /** 
    * Signs the query string.
    */
   private static String signQueryString(String queryString,String certAlias)
        throws SAML2Exception {
        if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("SPSSOFederate:queryString:" 
                                            + queryString);
                SAML2Utils.debug.message("SPSSOFederate: certAlias :" 
                                            + certAlias);
        }
        KeyProvider kp = KeyUtil.getKeyProviderInstance();
        PrivateKey privateKey = kp.getPrivateKey(certAlias);
        return QuerySignatureUtil.sign(queryString,privateKey);
  }
}

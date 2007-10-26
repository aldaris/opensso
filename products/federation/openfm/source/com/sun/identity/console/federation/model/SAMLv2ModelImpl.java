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
 * $Id: SAMLv2ModelImpl.java,v 1.3 2007-10-26 00:08:11 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import javax.servlet.http.HttpServletRequest;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.IDPSSODescriptorElement;
import com.sun.identity.saml2.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.AttributeElement;
import com.sun.identity.saml2.jaxb.metadata.SingleSignOnServiceElement;
import com.sun.identity.saml2.jaxb.metadata.ArtifactResolutionServiceElement;
import com.sun.identity.saml2.jaxb.metadata.EndpointType;
import com.sun.identity.saml2.jaxb.metadata.SingleLogoutServiceElement;
import com.sun.identity.saml2.jaxb.metadata.ManageNameIDServiceElement;
import com.sun.identity.saml2.jaxb.metadata.SPSSODescriptorElement;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.jaxb.metadata.AssertionConsumerServiceElement;
import com.sun.identity.saml2.jaxb.metadata.XACMLAuthzDecisionQueryDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.XACMLPDPDescriptorElement;
import com.sun.identity.saml2.jaxb.entityconfig.XACMLAuthzDecisionQueryConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.XACMLPDPConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.AttributeType;
import com.sun.identity.saml2.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.ObjectFactory;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.XACMLAuthzServiceElement;
import com.sun.identity.console.federation.model.EntityModel;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SAMLv2ModelImpl extends EntityModelImpl implements SAMLv2Model {    
    private SAML2MetaManager metaManager;
    
    public SAMLv2ModelImpl(HttpServletRequest req, Map map) {
        super(req, map);
    }
    
    /**
     * Returns a map with standard identity provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with standard attribute values of Identity Provider.
     * @throws AMConsoleException if unable to retrieve the Identity Provider
     *     attrubutes based on the realm and entityName passed.
     */
    public Map getStandardIdentityProviderAttributes(
        String realm,
        String entityName
    ) throws AMConsoleException {
        Map map = new HashMap();
        IDPSSODescriptorElement idpssoDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            idpssoDescriptor = samlManager.getIDPSSODescriptor(realm,entityName);            
            if (idpssoDescriptor != null) {
                
                // retrieve WantAuthnRequestsSigned
                map.put(WANT_AUTHN_REQ_SIGNED,returnEmptySetIfValueIsNull(
                        idpssoDescriptor.isWantAuthnRequestsSigned()));
                
                // retrieve ProtocolSupportEnumeration
                map.put(PROTOCOL_SUPP_ENUM,
                        convertListToSet(
                        idpssoDescriptor.getProtocolSupportEnumeration()));
                
                //retrieve ArtifactResolutionService
                List artList = idpssoDescriptor.getArtifactResolutionService();
                if (!artList.isEmpty()) {
                    ArtifactResolutionServiceElement key =
                            (ArtifactResolutionServiceElement)artList.get(0);
                    map.put(ART_RES_LOCATION,
                            returnEmptySetIfValueIsNull(key.getLocation()));
                    map.put(ART_RES_INDEX,
                            returnEmptySetIfValueIsNull(Integer.toString(
                            key.getIndex())));
                    map.put(ART_RES_ISDEFAULT,
                            returnEmptySetIfValueIsNull(key.isIsDefault()));
                }
                
                //retrieve SingleLogoutService
                List logoutList = idpssoDescriptor.getSingleLogoutService();
                if (!logoutList.isEmpty()) {
                    SingleLogoutServiceElement slsElem1 =
                            (SingleLogoutServiceElement)logoutList.get(0);
                    map.put(SINGLE_LOGOUT_HTTP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            slsElem1.getLocation()));
                    map.put(SINGLE_LOGOUT_HTTP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                                slsElem1.getResponseLocation()));
                    SingleLogoutServiceElement slsElem2 =
                            (SingleLogoutServiceElement)logoutList.get(1);
                    map.put(SINGLE_LOGOUT_SOAP_LOCATION,
                            returnEmptySetIfValueIsNull(
                                slsElem2.getLocation()));
                }
                
                //retrieve ManageNameIDService
                List manageNameIdList = idpssoDescriptor.getManageNameIDService();
                if (!manageNameIdList.isEmpty()) {
                    ManageNameIDServiceElement mniElem1 =
                            (ManageNameIDServiceElement)manageNameIdList.get(0);
                    map.put(MANAGE_NAMEID_HTTP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem1.getLocation()));
                    map.put(MANAGE_NAMEID_HTTP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem1.getResponseLocation()));
                    ManageNameIDServiceElement mniElem2 =
                            (ManageNameIDServiceElement)manageNameIdList.get(1);
                    map.put(MANAGE_NAMEID_SOAP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem2.getLocation()));
                }
                
                //retrieve nameid format
                List NameIdFormatList = idpssoDescriptor.getNameIDFormat();
                if (!NameIdFormatList.isEmpty()) {
                    ArrayList name = new ArrayList(10);
                    Iterator it = NameIdFormatList.listIterator();
                    while (it.hasNext()) {
                        String tmp =(String) it.next();
                        String newtmp = (String)tmp.substring(42);
                        name.add(newtmp);
                    }
                    map.put(NAMEID_FORMAT, returnEmptySetIfValueIsNull(
                            convertListToSet(name)));
                }
                
                //retrieve SingleSignOnService
                List signonList = idpssoDescriptor.getSingleSignOnService();
                if (!signonList.isEmpty()) {
                    SingleSignOnServiceElement signElem1 =
                        (SingleSignOnServiceElement)signonList.get(0);
                    map.put(SINGLE_SIGNON_HTTP_LOCATION,
                        returnEmptySetIfValueIsNull(
                            signElem1.getLocation()));
                    SingleSignOnServiceElement signElem2 =
                            (SingleSignOnServiceElement)signonList.get(1);
                    map.put(SINGLE_SIGNON_SOAP_LOCATION,
                        returnEmptySetIfValueIsNull(
                            signElem2.getLocation()));
                }
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getIdentityProviderAttributes", e);
            throw new AMConsoleException(e.getMessage());
        }
        return map;
    }
    
    /**
     * Returns a map with extended identity provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with extended attribute values of Identity Provider.
     * @throws AMConsoleException if unable to retrieve the Identity Provider
     *     attrubutes based on the realm and entityName passed.
     */
    public Map getExtendedIdentityProviderAttributes(
        String realm,
        String entityName
    ) throws AMConsoleException {
        Map map = null;
        IDPSSOConfigElement idpssoConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            idpssoConfig = samlManager.getIDPSSOConfig(realm,entityName);
            BaseConfigType baseConfig = (BaseConfigType)idpssoConfig;
            map = SAML2MetaUtils.getAttributes(baseConfig);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getExtIdentityProviderAttributes", e);
            throw new AMConsoleException(e.getMessage());
        }
        return (map != null) ? map : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a map with standard service provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with standard attribute values of Service Provider.
     * @throws AMConsoleException if unable to retrieve the Service Provider
     *     attrubutes based on the realm and entityName passed.
     */
    public Map getStandardServiceProviderAttributes(
        String realm,
        String entityName
    ) throws AMConsoleException {
        Map map = new HashMap();
        SPSSODescriptorElement spssoDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            spssoDescriptor = samlManager.getSPSSODescriptor(realm,entityName);
            if (spssoDescriptor != null) {
                
                // retrieve WantAuthnRequestsSigned
                map.put(IS_AUTHN_REQ_SIGNED,
                        returnEmptySetIfValueIsNull(
                        spssoDescriptor.isAuthnRequestsSigned()));
                map.put(WANT_ASSERTIONS_SIGNED,
                        returnEmptySetIfValueIsNull(
                        spssoDescriptor.isWantAssertionsSigned()));
                map.put(SP_PROTOCOL_SUPP_ENUM,
                        convertListToSet(
                        spssoDescriptor.getProtocolSupportEnumeration()));
                
                //retrieve SingleLogoutService
                List splogoutList = spssoDescriptor.getSingleLogoutService();
                if (!splogoutList.isEmpty()) {
                    SingleLogoutServiceElement spslsElem1 =
                            (SingleLogoutServiceElement)splogoutList.get(0);
                    map.put(SP_SINGLE_LOGOUT_HTTP_LOCATION,
                            returnEmptySetIfValueIsNull(spslsElem1.getLocation()));
                    map.put(SP_SINGLE_LOGOUT_HTTP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            spslsElem1.getResponseLocation()));
                    SingleLogoutServiceElement spslsElem2 =
                            (SingleLogoutServiceElement)splogoutList.get(1);
                    map.put(SP_SINGLE_LOGOUT_SOAP_LOCATION,
                            returnEmptySetIfValueIsNull(spslsElem2.getLocation()));
                }
                
                //retrieve ManageNameIDService
                List manageNameIdList = spssoDescriptor.getManageNameIDService();
                if (!manageNameIdList.isEmpty()) {
                    ManageNameIDServiceElement mniElem1 =
                            (ManageNameIDServiceElement)manageNameIdList.get(0);
                    map.put(SP_MANAGE_NAMEID_HTTP_LOCATION,
                            returnEmptySetIfValueIsNull(mniElem1.getLocation()));
                    map.put(SP_MANAGE_NAMEID_HTTP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem1.getResponseLocation()));
                    ManageNameIDServiceElement mniElem2 =
                            (ManageNameIDServiceElement)manageNameIdList.get(1);
                    map.put(SP_MANAGE_NAMEID_SOAP_LOCATION,
                            returnEmptySetIfValueIsNull(mniElem2.getLocation()));
                    map.put(SP_MANAGE_NAMEID_SOAP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem2.getResponseLocation()));
                }
                
                //retrieve AssertionConsumerService
                List asconsServiceList =
                        spssoDescriptor.getAssertionConsumerService();
                if (!asconsServiceList.isEmpty()) {
                    AssertionConsumerServiceElement acsElem1 =
                            (AssertionConsumerServiceElement)
                            asconsServiceList.get(0);
                    map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_DEFAULT,
                            returnEmptySetIfValueIsNull(acsElem1.isIsDefault()));
                    map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_INDEX,
                            returnEmptySetIfValueIsNull(
                            Integer.toString(acsElem1.getIndex())));
                    map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_LOCATION,
                            returnEmptySetIfValueIsNull(acsElem1.getLocation()));
                    AssertionConsumerServiceElement acsElem2 =
                            (AssertionConsumerServiceElement)
                            asconsServiceList.get(1);
                    map.put(HTTP_POST_ASSRT_CONS_SERVICE_INDEX,
                            returnEmptySetIfValueIsNull(
                            Integer.toString(acsElem2.getIndex())));
                    map.put(HTTP_POST_ASSRT_CONS_SERVICE_LOCATION,
                            returnEmptySetIfValueIsNull(acsElem2.getLocation()));
                }
                //retrieve nameid format
                List NameIdFormatList = spssoDescriptor.getNameIDFormat();
                if (!NameIdFormatList.isEmpty()) {
                    ArrayList name = new ArrayList(10);
                    Iterator it = NameIdFormatList.listIterator();
                    while (it.hasNext()) {
                        String tmp =(String) it.next();
                        String newtmp = (String)tmp.substring(42);
                        name.add(newtmp);
                    }
                    map.put(NAMEID_FORMAT, returnEmptySetIfValueIsNull(
                            convertListToSet(name)));
                }
            }
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getStandardServiceProviderAttribute:", e);
            throw new AMConsoleException(e.getMessage());
        }
        return map;
    }
    
    /**
     * Returns a map with extended service provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with extended attribute values of Service Provider.
     * @throws AMConsoleException if unable to retrieve the Service Provider
     *     attrubutes based on the realm and entityName passed.
     */
    public Map getExtendedServiceProviderAttributes(
        String realm,
        String entityName
    ) throws AMConsoleException {
        Map map = null;
        SPSSOConfigElement spssoConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            spssoConfig = samlManager.getSPSSOConfig(realm,entityName);
            BaseConfigType baseConfig = (BaseConfigType)spssoConfig;
            map = SAML2MetaUtils.getAttributes(baseConfig);
        } catch (SAML2MetaException e) {
            debug.warning(
                "SAMLv2ModelImpl.getExtendedServiceProviderAttributes", e);
            throw new AMConsoleException(e.getMessage());
        }
        return (map != null) ? map : Collections.EMPTY_MAP;
    }
    
    /**
     * Saves the standard attribute values for the Identiy Provider.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param idpStdValues Map which contains the standard attribute values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setIDPStdAttributeValues(
        String realm,
        String entityName,
        Map idpStdValues 
    )  throws AMConsoleException {
        IDPSSODescriptorElement idpssoDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                    samlManager.getEntityDescriptor(realm,entityName);
            idpssoDescriptor = samlManager.getIDPSSODescriptor(realm,entityName);
            if (idpssoDescriptor != null) {
                boolean value = setToBoolean(
                        idpStdValues, WANT_AUTHN_REQ_SIGNED);
                idpssoDescriptor.setWantAuthnRequestsSigned(value);
                
                // save for Artifact Resolution Service
                String artLocation = getResult(
                        idpStdValues, ART_RES_LOCATION);
                String indexValue = getResult(idpStdValues, ART_RES_INDEX);
                boolean isDef = setToBoolean(idpStdValues, ART_RES_ISDEFAULT);
                List artList = idpssoDescriptor.getArtifactResolutionService();
                if (!artList.isEmpty()) {
                    ArtifactResolutionServiceElement elem =
                        (ArtifactResolutionServiceElement)artList.get(0);
                    elem.setLocation(artLocation);
                    elem.setIndex(Integer.parseInt(indexValue));
                    elem.setIsDefault(isDef);
                    idpssoDescriptor.getArtifactResolutionService().clear();
                    idpssoDescriptor.getArtifactResolutionService().add(elem);
                }
                
                // save for Single Logout Service - Http-Redirect
                String lohttpLocation = getResult(
                        idpStdValues, SINGLE_LOGOUT_HTTP_LOCATION);
                String lohttpRespLocation = getResult(
                        idpStdValues, SINGLE_LOGOUT_HTTP_RESP_LOCATION);
                String losoapLocation = getResult(
                        idpStdValues, SINGLE_LOGOUT_SOAP_LOCATION);
                List logList = idpssoDescriptor.getSingleLogoutService();
                if (!logList.isEmpty()) {
                    SingleLogoutServiceElement slsElem1 =
                            (SingleLogoutServiceElement)logList.get(0);
                    SingleLogoutServiceElement slsElem2 =
                            (SingleLogoutServiceElement)logList.get(1);
                    slsElem1.setLocation(lohttpLocation);
                    slsElem1.setResponseLocation(lohttpRespLocation);
                    slsElem2.setLocation(losoapLocation);
                    idpssoDescriptor.getSingleLogoutService().clear();
                    idpssoDescriptor.getSingleLogoutService().add(slsElem1);
                    idpssoDescriptor.getSingleLogoutService().add(slsElem2);
                }
                
                // save for Manage Name ID Service
                String mnihttpLocation = getResult(
                        idpStdValues, MANAGE_NAMEID_HTTP_LOCATION);
                String mnihttpRespLocation = getResult(
                        idpStdValues, MANAGE_NAMEID_HTTP_RESP_LOCATION);
                String mnisoapLocation = getResult(
                        idpStdValues, MANAGE_NAMEID_SOAP_LOCATION);
                List manageNameIdList = idpssoDescriptor.getManageNameIDService();
                if (!manageNameIdList.isEmpty()) {
                    ManageNameIDServiceElement mniElem1 =
                            (ManageNameIDServiceElement)manageNameIdList.get(0);
                    ManageNameIDServiceElement mniElem2 =
                            (ManageNameIDServiceElement)manageNameIdList.get(1);
                    mniElem1.setLocation(mnihttpLocation);
                    mniElem1.setResponseLocation(mnihttpRespLocation);
                    mniElem2.setLocation(mnisoapLocation);
                    idpssoDescriptor.getManageNameIDService().clear();
                    idpssoDescriptor.getManageNameIDService().add(mniElem1);
                    idpssoDescriptor.getManageNameIDService().add(mniElem2);
                }
                
                //save nameid format
                List NameIdFormatList = idpssoDescriptor.getNameIDFormat();
                if (!NameIdFormatList.isEmpty()) {
                    List listtoSave = convertSetToList(
                            (Set)idpStdValues.get(NAMEID_FORMAT));
                    idpssoDescriptor.getNameIDFormat().clear();
                    Iterator itt = listtoSave.listIterator();
                    while (itt.hasNext()) {
                        String tmp =(String) itt.next();
                        StringBuffer nameid = new StringBuffer(
                            "urn:oasis:names:tc:SAML:2.0:nameid-format:");
                        nameid.insert(42,tmp);
                        idpssoDescriptor.getNameIDFormat().add(nameid.toString());
                    }
                }
                
                //save for SingleSignOnService
                String ssohttpLocation = getResult(
                        idpStdValues, SINGLE_SIGNON_HTTP_LOCATION);
                String ssopostLocation = getResult(
                        idpStdValues, SINGLE_SIGNON_SOAP_LOCATION);
                List signonList = idpssoDescriptor.getSingleSignOnService();
                if (!signonList.isEmpty()) {
                    SingleSignOnServiceElement signElem1 =
                            (SingleSignOnServiceElement)signonList.get(0);
                    SingleSignOnServiceElement signElem2 =
                            (SingleSignOnServiceElement)signonList.get(1);
                    signElem1.setLocation(ssohttpLocation);
                    signElem2.setLocation(ssopostLocation);
                    idpssoDescriptor.getSingleSignOnService().clear();
                    idpssoDescriptor.getSingleSignOnService().add(signElem1);
                    idpssoDescriptor.getSingleSignOnService().add(signElem2);
                }
                entityDescriptor.
                        getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().
                            clear();
                entityDescriptor.
                        getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().
                            add(idpssoDescriptor);
                samlManager.setEntityDescriptor(realm, entityDescriptor);
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.setIDPStdAttributeValues", e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Saves the extended attribute values for the Identiy Provider.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param idpExtValues Map which contains the standard attribute values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setIDPExtAttributeValues(
        String realm,
        String entityName,
        Map idpExtValues,
        String location
    ) throws AMConsoleException {
        String role = EntityModel.IDENTITY_PROVIDER;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            
            //entityConfig is the extended entity configuration object
            EntityConfigElement entityConfig =
                    samlManager.getEntityConfig(realm,entityName);
            
            //TBD-- for remote cases
            /*if (entityConfig == null) {
                createExtendedObject(realm, fedId, location, role);
                entityConfig =
                    samlManager.getEntityConfig(realm,entityName);
            }*/
            IDPSSOConfigElement  idpssoConfig = samlManager.getIDPSSOConfig(
                    realm,entityName);            
            if (idpssoConfig != null) {
                updateBaseConfig(idpssoConfig, idpExtValues);
            }
            
            //saves the attributes by passing the new entityConfig object
            samlManager.setEntityConfig(realm,entityConfig);
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.setIDPExtAttributeValues", e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Saves the standard attribute values for the Service Provider.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param spStdValues Map which contains the standard attribute values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setSPStdAttributeValues(
        String realm,
        String entityName,
        Map spStdValues 
    ) throws AMConsoleException {
        SPSSODescriptorElement spssoDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                samlManager.getEntityDescriptor(realm,entityName);
            spssoDescriptor = samlManager.getSPSSODescriptor(realm,entityName);
            if (spssoDescriptor != null) {
                
                // save for Single Logout Service - Http-Redirect
                String lohttpLocation = getResult(
                        spStdValues, SP_SINGLE_LOGOUT_HTTP_LOCATION);
                String lohttpRespLocation = getResult(
                        spStdValues, SP_SINGLE_LOGOUT_HTTP_RESP_LOCATION);
                String losoapLocation = getResult(
                        spStdValues, SP_SINGLE_LOGOUT_SOAP_LOCATION);
                List logList = spssoDescriptor.getSingleLogoutService();
                if (!logList.isEmpty()) {
                    SingleLogoutServiceElement slsElem1 =
                            (SingleLogoutServiceElement)logList.get(0);                
                    SingleLogoutServiceElement slsElem2 =
                            (SingleLogoutServiceElement)logList.get(1);
                    slsElem1.setLocation(lohttpLocation);
                    slsElem1.setResponseLocation(lohttpRespLocation);
                    slsElem2.setLocation(losoapLocation);
                    spssoDescriptor.getSingleLogoutService().clear();
                    spssoDescriptor.getSingleLogoutService().add(slsElem1);
                    spssoDescriptor.getSingleLogoutService().add(slsElem2);
                }
                // save for Manage Name ID Service
                String mnihttpLocation = getResult(
                        spStdValues, SP_MANAGE_NAMEID_HTTP_LOCATION);
                String mnihttpRespLocation = getResult(
                        spStdValues, SP_MANAGE_NAMEID_HTTP_RESP_LOCATION);
                String mnisoapLocation = getResult(
                        spStdValues, SP_MANAGE_NAMEID_SOAP_LOCATION);
                String mnisoapResLocation = getResult(
                        spStdValues, SP_MANAGE_NAMEID_SOAP_RESP_LOCATION);
                List manageNameIdList = spssoDescriptor.getManageNameIDService();
                if (!manageNameIdList.isEmpty()) {
                    ManageNameIDServiceElement mniElem1 =
                            (ManageNameIDServiceElement)manageNameIdList.get(0);
                    ManageNameIDServiceElement mniElem2 =
                            (ManageNameIDServiceElement)manageNameIdList.get(1);
                    mniElem1.setLocation(mnihttpLocation);
                    mniElem1.setResponseLocation(mnihttpRespLocation);
                    mniElem2.setLocation(mnisoapLocation);
                    mniElem2.setResponseLocation(mnisoapResLocation);
                    spssoDescriptor.getManageNameIDService().clear();
                    spssoDescriptor.getManageNameIDService().add(mniElem1);
                    spssoDescriptor.getManageNameIDService().add(mniElem2);
                }
                //save for Assertion Consumer Service
                boolean isassertDefault = setToBoolean(
                        spStdValues, HTTP_ARTI_ASSRT_CONS_SERVICE_DEFAULT);
                String httpIndex = getResult(
                        spStdValues, HTTP_ARTI_ASSRT_CONS_SERVICE_INDEX);
                String httpLocation = getResult(
                        spStdValues, HTTP_ARTI_ASSRT_CONS_SERVICE_LOCATION);
                String postIndex =  getResult(
                        spStdValues, HTTP_POST_ASSRT_CONS_SERVICE_INDEX);
                String postLocation = getResult(
                        spStdValues, HTTP_POST_ASSRT_CONS_SERVICE_LOCATION);
                List asconsServiceList =
                        spssoDescriptor.getAssertionConsumerService();
                if (!asconsServiceList.isEmpty()) {
                    AssertionConsumerServiceElement acsElem1 =
                            (AssertionConsumerServiceElement)asconsServiceList.get(0);
                    AssertionConsumerServiceElement acsElem2 =
                            (AssertionConsumerServiceElement)asconsServiceList.get(1);
                    acsElem1.setIsDefault(isassertDefault);
                    acsElem1.setIndex(Integer.parseInt(httpIndex));
                    acsElem1.setLocation(httpLocation);
                    acsElem2.setIndex(Integer.parseInt(postIndex));
                    acsElem2.setLocation(postLocation);
                }
                //save nameid format
                List NameIdFormatList = spssoDescriptor.getNameIDFormat();
                if (!NameIdFormatList.isEmpty()) {
                    List listtoSave = convertSetToList(
                            (Set)spStdValues.get(NAMEID_FORMAT));
                    spssoDescriptor.getNameIDFormat().clear();
                    Iterator itt = listtoSave.listIterator();
                    while (itt.hasNext()) {
                        String tmp =(String) itt.next();
                        StringBuffer nameid = new StringBuffer(
                                "urn:oasis:names:tc:SAML:2.0:nameid-format:");
                        nameid.insert(42,tmp);
                        spssoDescriptor.getNameIDFormat().add(nameid.toString());
                    }
                }
                
                //save AuthenRequestsSigned
                boolean authnValue = setToBoolean(
                        spStdValues, IS_AUTHN_REQ_SIGNED);
                spssoDescriptor.setAuthnRequestsSigned(authnValue);
                
                //save WantAssertionsSigned
                boolean assertValue = setToBoolean(
                        spStdValues, WANT_ASSERTIONS_SIGNED);
                spssoDescriptor.setWantAssertionsSigned(assertValue);
                entityDescriptor.
                        getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().
                            clear();
                entityDescriptor.
                        getRoleDescriptorOrIDPSSODescriptorOrSPSSODescriptor().
                            add(spssoDescriptor);
                samlManager.setEntityDescriptor(realm, entityDescriptor);
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.setSPStdAttributeValues", e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Saves the extended attribute values for the Service Provider.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param spExtValues Map which contains the standard attribute values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setSPExtAttributeValues(
        String realm,
        String entityName,
        Map spExtValues,
        String location
    ) throws AMConsoleException {
        String role = EntityModel.SERVICE_PROVIDER;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            
            //entityConfig is the extended entity configuration object
            EntityConfigElement entityConfig =
                    samlManager.getEntityConfig(realm,entityName);
            
            //TBD -- for remote cases
            /*if (entityConfig == null) {
                createExtendedObject(realm, fedId, location, role);
                fed = WSFederationMetaManager.getEntityConfig(realm,fedId);
            }*/
            SPSSOConfigElement  spssoConfig = samlManager.getSPSSOConfig(
                realm,entityName);
            if (spssoConfig != null){
                updateBaseConfig(spssoConfig, spExtValues);
            }
            
            //saves the attributes by passing the new entityConfig object
            samlManager.setEntityConfig(realm,entityConfig);
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.setSPExtAttributeValues", e);
            throw new AMConsoleException(e.getMessage());
        }
    }
        
    /**
     * Updates the BaseConfigElement.
     *
     * @param baseConfig is the BaseConfigType passed.
     * @param values the Map which contains the new attribute/value pairs.
     * @throws AMConsoleException if update of baseConfig object fails.
     */
    private void updateBaseConfig(
        BaseConfigType baseConfig,
        Map values 
    ) throws AMConsoleException {
        ObjectFactory objFactory = new ObjectFactory();
        try {
            for (Iterator iter=values.keySet().iterator();
            iter.hasNext();) {
                
                // each key value pair has to be set in Attribute element
                AttributeElement avp = objFactory.createAttributeElement();
                String key = (String)iter.next();
                
                avp.setName(key);
                Set set = (Set) values.get(key);
                Iterator i = set.iterator();
                while ((i !=  null)&& (i.hasNext())) {
                    String val = (String) i.next();
                    avp.getValue().add(val);
                }
                
                //updates the BaseConfig Object
                baseConfig.getAttribute().add(avp);
            }
        } catch (JAXBException e) {
            debug.warning("SAMLv2ModelImpl.updateBaseConfig", e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Returns a Map of PEP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PEP descriptor data.
     */
    public Map getPEPDescriptor(String realm, String entityName) {
        Map data = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLAuthzDecisionQueryDescriptorElement xacmlAuthzDescriptor =
                    saml2Manager.getPolicyEnforcementPointDescriptor(
                    realm, entityName);            
            if (xacmlAuthzDescriptor != null) {
                data = new HashMap(10);
                
                //ProtocolSupportEnum
                data.put(ATTR_TXT_PROTOCOL_SUPPORT_ENUM,
                        returnEmptySetIfValueIsNull(
                        xacmlAuthzDescriptor.getProtocolSupportEnumeration()));                
                if (xacmlAuthzDescriptor.isWantAssertionsSigned()) {
                    data.put(ATTR_WANT_ASSERTION_SIGNED, "true");
                } else {
                    data.put(ATTR_WANT_ASSERTION_SIGNED, "false");
                }
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getPEPDescriptor", e);
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a Map of PDP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PDP descriptor data.
     */
    public Map getPDPDescriptor(String realm, String entityName) {
        Map data = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLPDPDescriptorElement xacmlPDPDescriptor =
                    saml2Manager.getPolicyDecisionPointDescriptor(
                    realm,
                    entityName);            
            if (xacmlPDPDescriptor != null) {
                data = new HashMap(10);
                
                //ProtocolSupportEnum
                data.put(ATTR_TXT_PROTOCOL_SUPPORT_ENUM,
                    returnEmptySetIfValueIsNull(
		    xacmlPDPDescriptor.getProtocolSupportEnumeration()));                
                List authzServiceList = 
                    xacmlPDPDescriptor.getXACMLAuthzService();
                if (authzServiceList.size() != 0) {
                    XACMLAuthzServiceElement authzService =
                        (XACMLAuthzServiceElement) authzServiceList.get(0);
                    data.put(ATTR_XACML_AUTHZ_SERVICE_BINDING,
                        returnEmptySetIfValueIsNull(
                        authzService.getBinding()));
                    data.put(ATTR_XACML_AUTHZ_SERVICE_LOCATION,
                        returnEmptySetIfValueIsNull(
                        authzService.getLocation()));
                }
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getPDPDescriptor", e); 
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }

    /**
     * Returns a <code>Map</code> containing the extended metadata for the PEP.
     *
     * @param realm where entity exists.
     * @param entityName name of entity descriptor.
     * @param location if the entity is remote or hosted.
     * @return key-value pair Map of PEP config data.
     */
    public Map getPEPConfig (
        String realm, 
        String entityName, 
        String location
    ) {
        Map data = null;
        List configList = null;
        String metaAlias = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLAuthzDecisionQueryConfigElement xacmlAuthzConfigElement =
                    saml2Manager.getPolicyEnforcementPointConfig(
                    realm, entityName);
            
            if (xacmlAuthzConfigElement != null) {
                data = new HashMap();
                configList = xacmlAuthzConfigElement.getAttribute();
                metaAlias = xacmlAuthzConfigElement.getMetaAlias();
                int size = configList.size();
                for (int i=0; i< size; i++) {
                    AttributeType atype = (AttributeType) configList.get(i);
                    String name = atype.getName();
                    java.util.List value = atype.getValue();
                    data.put(atype.getName(),
                        returnEmptySetIfValueIsNull(atype.getValue()));
                }
                data.put("metaAlias", metaAlias);
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getPEPConfig", e);
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a Map of PDP Config data. (Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PPP config data.
     */
    public Map getPDPConfig(String realm, String entityName, String location) {
        Map data = null;
        List configList = null;
        String metaAlias = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLPDPConfigElement xacmlPDPConfigElement =
                    saml2Manager.getPolicyDecisionPointConfig(
                    realm, entityName);
            if (xacmlPDPConfigElement != null) {
                data = new HashMap();
                configList = xacmlPDPConfigElement.getAttribute() ;
                metaAlias = xacmlPDPConfigElement.getMetaAlias();
                int size = configList.size();
                for (int i=0; i< size; i++) {
                    AttributeType atype = (AttributeType) configList.get(i);
                    String name = atype.getName();
                    java.util.List value = atype.getValue();
                    data.put(atype.getName(),
		        returnEmptySetIfValueIsNull(atype.getValue()));
                }
                data.put("metaAlias", metaAlias);
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getPDPConfig", e);
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Save standard metadata for PDP descriptor.
     *
     * @param realm realm of Entity.
     * @param entityName entity name of Entity Descriptor.
     * @param attrValues key-value pair Map of PDP standed data.
     * throws AMConsoleException if there is an error.
     */
    public void updatePDPDescriptor(
        String realm,
        String entityName,
        Map attrValues
    ) throws AMConsoleException {
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                saml2Manager.getEntityDescriptor(realm, entityName) ;
            XACMLPDPDescriptorElement pdpDescriptor =
                saml2Manager.getPolicyDecisionPointDescriptor(
                    realm,
                    entityName);
            
            if (pdpDescriptor != null) {
                List authzServiceList = pdpDescriptor.getXACMLAuthzService();
                if (authzServiceList.size() != 0) {
                    XACMLAuthzServiceElement authzService =
                        (XACMLAuthzServiceElement)authzServiceList.get(0);
                    authzService.setLocation((String)AMAdminUtils.getValue(
                        (Set)attrValues.get(
                            ATTR_XACML_AUTHZ_SERVICE_LOCATION)));
                }
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.updatePDPDescriptor", e);
        }
    }
    
    /**
     * Save extended metadata for PDP Config.
     *
     * @param realm realm of Entity.
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted.
     * @param attrValues key-value pair Map of PDP extended config.
     */
    public void updatePDPConfig(
        String realm,
        String entityName,
        String location,
        Map attrValues
    ) throws AMConsoleException, JAXBException {
        try {
            ObjectFactory objFactory = new ObjectFactory();
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLPDPConfigElement pdpEntityConfig =
                saml2Manager.getPolicyDecisionPointConfig(
                realm, entityName);            
            if (pdpEntityConfig == null) {
                throw new AMConsoleException("invalid.xacml.configuration");
            } else {
                List list = pdpEntityConfig.getAttribute();
                list.clear();
                Map values = convertSetToListInMap(attrValues);
                for (Iterator i = values.keySet().iterator(); i.hasNext(); ) {
                    String key = (String)i.next();
                    AttributeType atype = objFactory.createAttributeType();
                    atype.setName(key);
                    atype.getValue().addAll((List)values.get(key));
                    list.add(atype);
                }
            }
        } catch (SAML2MetaException e) {
            if (debug.warningEnabled()) {
            throw new AMConsoleException("SAMLv2ModelImpl.updatePDPConfig : "+
                    getErrorString(e));
            }
        }
    }
    
    /**
     * Save the standard metadata for PEP descriptor.
     *
     * @param realm realm of Entity.
     * @param entityName entity name of Entity Descriptor.
     * @param attrValues key-value pair Map of PEP descriptor data.
     * throws AMConsoleException if there is an error.
     */
    public void updatePEPDescriptor(
        String realm,
        String entityName,
        Map attrValues
    ) throws AMConsoleException {
        // TBD : currently, there is nothing to save
    }
    
    /**
     * Save the extended metadata for PEP Config.
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted
     * @param attrValues key-value pair Map of PEP extended config.
     */
    public void updatePEPConfig(
        String realm,
        String entityName,
        String location,
        Map attrValues
    ) throws AMConsoleException, JAXBException {
        try {
            ObjectFactory objFactory = new ObjectFactory();
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLAuthzDecisionQueryConfigElement pepEntityConfig =
                    saml2Manager.getPolicyEnforcementPointConfig(
                    realm, entityName);            
            if (pepEntityConfig == null) {
                throw new AMConsoleException("invalid.xacml.configuration");
            } else {
                List list = pepEntityConfig.getAttribute();
                list.clear();
                Map values = convertSetToListInMap(attrValues);
                for (Iterator i = values.keySet().iterator(); i.hasNext(); ) {
                    String key = (String)i.next();
                    AttributeType atype = objFactory.createAttributeType();
                    atype.setName(key);
                    atype.getValue().addAll((List)values.get(key));
                    list.add(atype);
                }
            }
        } catch (SAML2MetaException e) {
            throw new AMConsoleException(getErrorString(e));
        }
    }
    
    protected SAML2MetaManager getSAML2MetaManager() throws SAML2MetaException {
        if (metaManager == null) {
            metaManager = new SAML2MetaManager();
        }
        return metaManager;
    }
    
    /*
    private Set returnEmptySetIfValueIsNull(boolean b) {
        Set set = new HashSet(2);
        set.add(Boolean.toString(b));
        return set;
    }
    
    private Set returnEmptySetIfValueIsNull(Set set) {
        return (set != null) ? set : Collections.EMPTY_SET;
    }
    
    private Set returnEmptySetIfValueIsNull(String str) {
        Set set = Collections.EMPTY_SET;
        if (str != null) {
            set = new HashSet(2);
            set.add(str);
        }
        return set;
    }
    */
    
    private boolean setToBoolean(Map map, String value) {
        Set set = (Set)map.get(value);
        return ((set != null) && !set.isEmpty()) ?
            Boolean.parseBoolean((String)set.iterator().next()) : false;
    }
    
 
    
    /*    private Set returnEmptySetIfValueIsNull(List l) {
        Set set = new HashSet();
        int size = l.size();
        for (int i=0;i<size;i++){
            set.add(l.get(i));
        }
        return set;
    }


   
    private List returnEmptyListIfValueIsNull(String str) {
        List list = Collections.EMPTY_LIST;
        if (str != null) {
            list = new ArrayList(2);
            list.add(str);
        }
        return list;
    }
  
    private List returnEmptyListIfValueIsNull(List list) {
        return (list != null) ? list : Collections.EMPTY_LIST;
    }
    
    private Set convertListToSet(List list) {
        Set s = new HashSet();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            s.add(it.next());
        }
        return s;
    }
    
    private List convertSetToList(Set set) {
        List list = new ArrayList();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
    
    private Map convertSetToListInMap(Map map) {
        Map tmpMap = new HashMap();
        Set entries = map.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            tmpMap.put((String)entry.getKey(),
                    returnEmptyListIfValueIsNull(
                    convertSetToList((Set)entry.getValue())));
        }
        return tmpMap;
    }
         */


    private String getResult(Map map, String value) {
        Set set = (Set)map.get(value);
        Iterator  i = set.iterator();
        String val = null;
        while ((i !=  null) && (i.hasNext())) {
            val = (String)i.next();
        }
        return val;
    }
}

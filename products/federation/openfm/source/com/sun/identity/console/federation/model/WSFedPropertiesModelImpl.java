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
 * "Portions Copyrighted [year] [name of copyright owner]
 *
 * $Id: WSFedPropertiesModelImpl.java,v 1.4 2007-09-12 23:38:40 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.meta.WSFederationMetaException;
import com.sun.identity.wsfederation.meta.WSFederationMetaUtils;
import com.sun.identity.wsfederation.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.wsfederation.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import com.sun.identity.wsfederation.jaxb.entityconfig.FederationConfigElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.UriNamedClaimTypesOfferedElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.ClaimType;
import com.sun.identity.wsfederation.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.wsfederation.jaxb.entityconfig.AttributeElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.TokenIssuerEndpointElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.TokenIssuerNameElement;
import com.sun.identity.wsfederation.jaxb.entityconfig.ObjectFactory;
import com.sun.identity.wsfederation.jaxb.wsfederation.DescriptionType;

import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class WSFedPropertiesModelImpl extends AMModelBase
        implements WSFedPropertiesModel {
    private static Map GEN_DATA_MAP = new HashMap(6);
    private static Map GEN_DUAL_DATA_MAP = new HashMap(8);
    private static Map SPEX_DATA_MAP = new HashMap(24);
    private static Map IDPSTD_DATA_MAP = new HashMap(2);
    private static Map IDPEX_DATA_MAP = new HashMap(18);
    
    static {
        GEN_DATA_MAP.put(TF_DISPNAME, Collections.EMPTY_SET);
        GEN_DATA_MAP.put(TFTOKENISSUER_NAME, Collections.EMPTY_SET);
        GEN_DATA_MAP.put(TFTOKENISSUER_ENDPT, Collections.EMPTY_SET);
    }
    
    static {
        GEN_DUAL_DATA_MAP.put(TF_DISPNAME, Collections.EMPTY_SET);
        GEN_DUAL_DATA_MAP.put(TFIDPDISP_NAME, Collections.EMPTY_SET);
        GEN_DUAL_DATA_MAP.put(TFTOKENISSUER_NAME, Collections.EMPTY_SET);
        GEN_DUAL_DATA_MAP.put(TFTOKENISSUER_ENDPT, Collections.EMPTY_SET);
    }
    
    static {
        SPEX_DATA_MAP.put(TFSPAUTOFED_ENABLED, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFARTI_SIGNED, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFSPAUTOFED_ATTR, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFASSERTEFFECT_TIME, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFSPACCT_MAPPER, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFSPATTR_MAPPER, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFSPAUTHCONT_MAPPER, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFAUTHCONTCLASS_REFMAPPING, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFAUTHCONT_COMPARTYPE, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFSPATTR_MAP, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFRELAY_STATE, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFASSERT_TIMESKEW, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFACCT_REALM_COOKIE, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFACCT_REALM_SELECTION, Collections.EMPTY_SET);
        SPEX_DATA_MAP.put(TFACCT_HOMEREALM_DISC_SERVICE, Collections.EMPTY_SET);
    }
    
    static {
        IDPEX_DATA_MAP.put(TFSIGNCERT_ALIAS, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFAUTOFED_ENABLED, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPAUTOFED_ATTR, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPASSERT_TIME, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPAUTH_CONTMAPPER, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPACCT_MAPPER, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPATTR_MAPPER, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPATTR_MAP, Collections.EMPTY_SET);
    }
    
    static {
        // TBD-  once backend api is complete
        IDPSTD_DATA_MAP.put(TFCLAIM_TYPES, Collections.EMPTY_SET);
    }
    
    /** Creates a new instance of WSFedPropertiesModelImpl */
    public WSFedPropertiesModelImpl(HttpServletRequest req,  Map map) {
        super(req, map);
    }
    
    /**
     * Returns a map with service provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the Federation Id otherwise known as the entity id.
     * @return attribute values of SP based on realm and fedid passed.
     * @throws AMConsoleException if unable to retreive the Service Provider
     *     attrubutes based on the realm and fedid passed.
     */
    public Map getServiceProviderAttributes(String realm, String fedid)
    throws AMConsoleException {
        String classMethod =
                "WSFedPropertiesModelImpl.getServiceProviderAttributes:";
        SPSSOConfigElement spconfig = null;
        Map SPAttributes = null;
        try {
            spconfig = WSFederationMetaManager.getSPSSOConfig(realm,fedid);
            if (spconfig != null) {
                SPAttributes =  WSFederationMetaUtils.getAttributes(spconfig);
            }
        } catch (WSFederationMetaException e) {
            debug.warning
                    (classMethod +e);
            throw new AMConsoleException(e.getMessage());
        }
        return (SPAttributes != null) ? SPAttributes : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a map with identity provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the Federation Id otherwise known as the entity id.
     * @return attribute values of IDP based on realm and fedid passed.
     * @throws AMConsoleException if unable to retreive the Identity Provider
     *     attrubutes based on the realm and fedid passed.
     */
    public Map getIdentityProviderAttributes(String realm, String fedid)
    throws AMConsoleException {
        String classMethod =
                "WSFedPropertiesModelImpl.getIdentityProviderAttributes:";
        IDPSSOConfigElement idpconfig = null;
        Map IDPAttributes = null;
        try {
            idpconfig = WSFederationMetaManager.getIDPSSOConfig(realm,fedid);
            if (idpconfig != null) {
                IDPAttributes = WSFederationMetaUtils.getAttributes(idpconfig);
            }
        } catch (WSFederationMetaException e) {
            debug.warning
                    (classMethod+ e);
            throw new AMConsoleException(e.getMessage());
        }
        return (IDPAttributes != null) ? IDPAttributes : Collections.EMPTY_MAP;
        
    }
    
    /**
     * Returns FederationElement Object for the realm and fedid passed.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the Federation Id otherwise known as the entity id.
     * @return FederationElement Object for the realm and fedid passed.
     * @throws AMConsoleException if unable to retrieve the FederationElement
     *     Object.
     */
    public FederationElement getEntityDesc(String realm, String fedId)
    throws AMConsoleException {
        String classMethod = "WSFedPropertiesModelImpl.getEntityDesc:";
        FederationElement fedElem = null;
        try {
            fedElem =
                    WSFederationMetaManager.getEntityDescriptor(realm, fedId);
            if (fedElem == null) {
                throw new AMConsoleException(classMethod +
                        "invalid FederationElement : " +
                        fedId);
            }
        } catch (WSFederationMetaException e) {
            debug.warning(classMethod + e);
            throw new AMConsoleException(e.getMessage());
        }
        return fedElem;
    }
    
    /**
     * Returns TokenIssuerName for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return TokenIssuerName for the FederationElement passed.
     */
    public String getTokenName(FederationElement fedElem) {
        String tkname = null;
        tkname =  WSFederationMetaManager.getTokenIssuerName(fedElem);
        return tkname;
    }
    
    /**
     * Returns TokenIssuerEndPoint for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return TokenIssuerEndPoint for the FederationElement passed.
     */
    public String getTokenEndpoint(FederationElement fedElem) {
        String tkEndpt = null;
        tkEndpt =  WSFederationMetaManager.getTokenIssuerEndpoint(fedElem);
        return tkEndpt;
    }
    
    /**
     * Returns UriNamedClaimTypesOffered for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return UriNamedClaimTypesOffered for the FederationElement passed.
     */
    public Map getClaimType(FederationElement fedElem) {
        UriNamedClaimTypesOfferedElement claimTypes =
                WSFederationMetaManager.getUriNamedClaimTypesOffered(fedElem);
        DescriptionType desc = null;
        List claimList = null;
        List stringList  = new ArrayList();
        Map claimMap = new HashMap();
        Set uri = new HashSet(2);
        Set displayName = new HashSet(2);
        Set description = new HashSet(2);
        
        //assuming there is only 1 claim type object now
        if(claimTypes != null) {
            int iClaim = 0;
            int arr = 0;
            claimList = claimTypes.getClaimType();
            for(iClaim = 0; iClaim < claimList.size(); iClaim += 1) {
                ClaimType ct = (ClaimType)claimList.get(iClaim);
                uri.add(ct.getUri());
                displayName.add(ct.getDisplayName().getValue());
                desc = ct.getDescription();
                if (desc != null) {
                    description.add(desc.getValue());
                }
            }
            
            // TBD-- display format need to be decided
            claimMap.put("claimtypeDisplayUri", uri);
            claimMap.put("claimtypeDisplayName", displayName);
            claimMap.put("claimtypeDisplayDescr", desc);
        }
        return claimMap;
    }
    
    /**
     * Returns Signing Certificate for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return Signing Certificate for the FederationElement passed.
     */
    public byte[] getSignCert(FederationElement fedElem) {
        byte[] signCert = null;
        signCert = WSFederationMetaManager.getTokenSigningCertificate(fedElem);
        return signCert;
    }
    
    /**
     * Saves the attribute values from the General page.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param idpStdvalues has the General standard attribute value pairs.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setGenAttributeValues(
            String realm,
            String fedId,
            Map idpStdValues,
            String role,
            String location
            ) throws AMConsoleException {
        String tknissEndPt = null;
        String tknissName = null;
        Iterator it = idpStdValues.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key.equals(TFTOKENISSUER_ENDPT)) {
                HashSet set = (HashSet) idpStdValues.get(key);
                Iterator i = set.iterator();
                while ((i !=  null)&& (i.hasNext())) {
                    tknissEndPt = (String) i.next();
                }
            } else if (key.equals(TFTOKENISSUER_NAME)) {
                HashSet set = (HashSet) idpStdValues.get(key);
                Iterator i = set.iterator();
                while ((i !=  null)&& (i.hasNext())) {
                    tknissName = (String) i.next();
                }
            } else if (key.equals(TF_DISPNAME)) {
                HashSet set = (HashSet) idpStdValues.get(key);
                Map tmpMap = new HashMap(2);
                tmpMap.put(TF_DISPNAME, set);
                if (role.equals(EntityModel.SERVICE_PROVIDER)) {
                    setSPExtAttributeValues(realm, fedId, tmpMap, location);
                } else if (role.equals(EntityModel.IDENTITY_PROVIDER)) {
                    setIDPExtAttributeValues(realm, fedId, tmpMap, location);
                } else if (role.equals(DUAL)) {
                    setSPExtAttributeValues(realm, fedId, tmpMap, location);
                    HashSet idpset = 
                        (HashSet) idpStdValues.get(TFIDPDISP_NAME);
                    Map idptmpMap = new HashMap(2);
                    idptmpMap.put(TF_DISPNAME, idpset);
                    setIDPExtAttributeValues(
                        realm, fedId, idptmpMap, location);
                }
            }
        }
        try {
            
            //fedElem is standard metadata federation element under the realm.
            FederationElement fedElem =
                    WSFederationMetaManager.getEntityDescriptor(realm, fedId);
            if (fedElem == null) {
                throw new AMConsoleException("WSFedPropertiesModelImpl.setGenAttributeValues:" +
                        "invalid FederationElement : " +
                        fedId);
            }else {
                for (Iterator iter = fedElem.getAny().iterator();
                iter.hasNext(); ) {
                    Object o = iter.next();
                    if (o instanceof TokenIssuerEndpointElement) {
                        ((TokenIssuerEndpointElement)o).getAddress().
                                setValue(tknissEndPt);
                    } else if (o instanceof TokenIssuerNameElement) {
                        ((TokenIssuerNameElement)o).setValue(tknissName);
                    }
                }
                WSFederationMetaManager.setFederation(realm, fedElem);
            }
            
        } catch (WSFederationMetaException e) {
            debug.warning
                    ("WSFedPropertiesModelImpl.setGenAttributeValues:" + e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Saves the extended metadata attribute values for the SP.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param spExtvalues has the extended attribute value pairs of SP.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setSPExtAttributeValues(
            String realm,
            String fedId,
            Map spExtvalues,
            String location
            ) throws AMConsoleException {
        String classMethod =
                "WSFedPropertiesModelImpl.setSPExtAttributeValues:";
        String role = "SP";
        try {
            
            //fed is the extended entity configuration object under the realm
            FederationConfigElement fed =
                    WSFederationMetaManager.getEntityConfig(realm,fedId);
            if (fed == null) {
                createExtendedObject(realm, fedId, location, role);
                fed = WSFederationMetaManager.getEntityConfig(realm,fedId);
            }
            SPSSOConfigElement  spsso = getspsso(fed);
            if (spsso != null){
                BaseConfigType bcon = updateSPBaseConfig(spsso, spExtvalues);
            }
            //saves the attributes by passing the new fed object
            WSFederationMetaManager.setEntityConfig(realm,fed);
        }catch (JAXBException e) {
            debug.warning
                    (classMethod + e);
            throw new AMConsoleException(e.getMessage());
        } catch (WSFederationMetaException e) {
            debug.warning
                    (classMethod + e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Saves the standard attribute values for the SP.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param idpExtvalues has the extended attribute value pairs of IDP.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setIDPExtAttributeValues(
            String realm,
            String fedId,
            Map idpExtValues,
            String location
            ) throws AMConsoleException {
        String classMethod =
                "WSFedPropertiesModelImpl.setIDPExtAttributeValues:";
        String role = "IDP";
        try {
            
            // fed is the extended entity configuration under the realm
            FederationConfigElement fed =
                    WSFederationMetaManager.getEntityConfig(realm,fedId);
            if (fed == null) {
                createExtendedObject(realm, fedId, location, role);
                fed = WSFederationMetaManager.getEntityConfig(realm,fedId);
            }
            IDPSSOConfigElement  idpsso = getidpsso(fed);
            if (idpsso != null){
                BaseConfigType bcon =
                        updateIDPBaseConfig(idpsso, idpExtValues);
            }
            
            //saves the new configuration by passing new fed element created
            WSFederationMetaManager.setEntityConfig(realm,fed);
        }catch (JAXBException e) {
            debug.warning
                    (classMethod + e);
            throw new AMConsoleException(e.getMessage());
        } catch (WSFederationMetaException e) {
            debug.warning
                    (classMethod + e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Saves the standard attribute values for the IDP.
     *
     * @param fedElem is standard metadata object
     * @param Map idpStdValues contain standard attribute values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setIDPSTDAttributeValues(
            FederationElement fedElem,
            Map idpStdValues
            ) throws AMConsoleException {
        
        //TBD once backend is completed
    }
    
    private String SettoString(Set clSet) {
        String value = null;
        Iterator i = clSet.iterator();
        while ((i !=  null)&& (i.hasNext())) {
            value = (String) i.next();
        }
        return value;
    }
    
    /**
     * Retrieves the IDPSSOConfigElement .
     *
     * @param fed is the FederationConfigElement.
     * @return the corresponding IDPSSOConfigType Object.
     */
    private IDPSSOConfigElement getidpsso(FederationConfigElement fed) {
        List listFed = fed.getIDPSSOConfigOrSPSSOConfig();
        IDPSSOConfigElement idpsso = null;
        Iterator i = listFed.iterator();
        //TBD -- one config will have only one instance of
        //IDPSSOConfigElement ?????
        while (i.hasNext()) {
            BaseConfigType bc = (BaseConfigType) i.next();
            if (bc instanceof IDPSSOConfigElement) {
                idpsso = (IDPSSOConfigElement) bc;
                break;
            }
        }
        return idpsso;
    }
    
    /**
     * Retrieves the SPSSOConfigElement .
     *
     * @param fed is the FederationConfigElement.
     * @return the corresponding SPSSOConfigType Object.
     */
    private SPSSOConfigElement getspsso(FederationConfigElement fed) {
        List listFed = fed.getIDPSSOConfigOrSPSSOConfig();
        SPSSOConfigElement spsso = null;
        Iterator i = listFed.iterator();
        //TBD -- one config will have only one instance of
        //SPSSOConfigElement ?????
        while (i.hasNext()) {
            BaseConfigType bc = (BaseConfigType) i.next();
            if (bc instanceof SPSSOConfigElement) {
                spsso = (SPSSOConfigElement) bc;
                break;
            }
        }
        return spsso;
    }
    
    /**
     * Updates the IDPSSOConfigElement with the map of values passed.
     *
     * @param idpsso is the IDPSSOConfigElement passes.
     * @param values the Map which contains the new attribute/value pairs.
     * @return the corresponding BaseConfigType Object.
     * @throws AMConsoleException if update of idpsso object fails.
     */
    private BaseConfigType updateIDPBaseConfig(
            IDPSSOConfigElement idpsso,
            Map values
            ) throws AMConsoleException {
        String classMethod =
                "WSFedPropertiesModelImpl.updateIDPBaseConfig:";
        BaseConfigType bcon = (BaseConfigType)idpsso;
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
                //add Attribute element object to the BaseConfig Object
                bcon.getAttribute().add(avp);
            }
        } catch (JAXBException e) {
            debug.warning(classMethod + e);
            throw new AMConsoleException(e.getMessage());
        }
        return bcon;
    }
    
    /**
     * Updates the SPSSOConfigElement with the map of values passed.
     *
     * @param spsso is the IDPSSOConfigElement passes.
     * @param values the Map which contains the new attribute/value pairs.
     * @return the corresponding BaseConfigType Object.
     * @throws AMConsoleException if update of spsso object fails.
     */
    private BaseConfigType updateSPBaseConfig(
            SPSSOConfigElement spsso,
            Map values
            ) throws AMConsoleException {
        String classMethod =
                "WSFedPropertiesModelImpl.updateSPBaseConfig:";
        BaseConfigType bcon = (BaseConfigType)spsso;
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
                //add Attribute element object to the BaseConfig Object
                bcon.getAttribute().add(avp);
            }
        } catch (JAXBException e) {
            debug.warning(classMethod + e);
            throw new AMConsoleException(e.getMessage());
        }
        return bcon;
    }
    
    
    /**
     * Creates the extended config object when it does not exist.
     * @param realm to which the entity belongs.
     * @param fedId is the entity id.
     * @param location is either hosted or remote
     * @param role is SP, IDP or SP/IDP.
     * @throws WSFederationMetaException, JAXBException,
     *     AMConsoleException if saving of attribute value fails.
     */
    private void createExtendedObject(
            String realm,
            String fedId,
            String location,
            String role)
            throws WSFederationMetaException,
            JAXBException, AMConsoleException {
        String classMethod = "WSFedPropertiesModelImpl.createExtendedObject: ";
        try {
            ObjectFactory objFactory = new ObjectFactory();
            FederationElement edes =
                    WSFederationMetaManager.getEntityDescriptor(realm, fedId);
            if (edes == null) {
                debug.error(classMethod +"No such entity: " + fedId);
                String[] data = {realm, fedId};
                throw new WSFederationMetaException("fedId_invalid", data);
            }
            FederationConfigElement eConfig =
                    WSFederationMetaManager.getEntityConfig(realm, fedId);
            if (eConfig == null) {
                BaseConfigType bctype = null;
                FederationConfigElement ele =
                        objFactory.createFederationConfigElement();
                ele.setFederationID(fedId);
                if (location.equals("remote")) {
                    ele.setHosted(false);
                }
                List ll =
                        ele.getIDPSSOConfigOrSPSSOConfig();
                
                // Decide which role EntityDescriptorElement includes
                // Right now, it is either an SP or an IdP or dual role
                if (isDualRole(edes)) {
                    
                    //for dual role create both idp and sp config objects
                    BaseConfigType bctype_idp = null;
                    BaseConfigType bctype_sp = null;
                    bctype_idp = objFactory.createIDPSSOConfigElement();
                    bctype_sp = objFactory.createSPSSOConfigElement();
                    ll.add(bctype_idp);
                    ll.add(bctype_sp);
                }else if (role.equals("IDP")) {
                    bctype = objFactory.createIDPSSOConfigElement();
                    // bctype.getAttribute().add(atype);
                    ll.add(bctype);
                } else if (role.equals("SP")) {
                    bctype = objFactory.createSPSSOConfigElement();
                    
                    //bctype.getAttribute().add(atype);
                    ll.add(bctype);
                }
                WSFederationMetaManager.setEntityConfig(realm,ele);
                FederationConfigElement jdfg =
                        WSFederationMetaManager.getEntityConfig(realm,fedId);
            }
        }catch (JAXBException e) {
            debug.warning(classMethod + e);
            throw new AMConsoleException(e.getMessage());
        }catch (WSFederationMetaException e) {
            debug.warning(classMethod + e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Retrieves a count of the TokenIssuerEndpointElement
     *     which would help in determining whether dual role or not.
     * @param edes is the standard metadata object.
     *
     * @return count of the TokenIssuerEndpointElement
     */
    private boolean isDualRole(FederationElement edes) {
        int cnt = 0;
        boolean dual = false;
        if (edes != null) {
            for (Iterator iter = edes.getAny().iterator(); iter.hasNext(); ) {
                Object o = iter.next();
                if (o instanceof TokenIssuerEndpointElement) {
                    cnt++;
                }
            }
        }
        if (cnt>1)
        {
            dual = true;
        }
        return dual;
    }
    
    /**
     * Returns a map of wsfed general attributes.
     *
     * @return Map of wsfed general attributes.
     */
    public Map getGenAttributes() {
        return GEN_DATA_MAP;
    }
    
    /**
     * Returns a map of wsfed general attribute values for dual role.
     *
     * @return Map of wsfed general attribute values for dual role.
     */
    public Map getDualRoleAttributes() {
        return GEN_DUAL_DATA_MAP;
    }
    
    /**
     * Returns a map of Wsfed Service Provider extended attributes.
     *
     * @return Map of Wsfed Service Provider extended attributes.
     */
    public Map getSPEXDataMap() {
        return SPEX_DATA_MAP;
    }
    
    /**
     * Returns a map of ext Wsfed Identity Provider extended attributes.
     *
     * @return Map of Wsfed Identity Provider extended attributes.
     */
    public Map getIDPEXDataMap() {
        return IDPEX_DATA_MAP;
    }
    
    /**
     * Returns a map of Wsfed Identity Provider Standard attributes.
     *
     * @return Map of Wsfed Identity Provider Standard attributes.
     */
    public Map getIDPSTDDataMap() {
        return IDPSTD_DATA_MAP;
    }
    
}

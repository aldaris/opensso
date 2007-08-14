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
 * $Id: WSFedPropertiesModelImpl.java,v 1.2 2007-08-14 21:56:18 babysunil Exp $
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
    private static Map GEN_DATA_MAP = new HashMap(4);
    private static Map SPEX_DATA_MAP = new HashMap(24);
    private static Map SPSTD_DATA_MAP = new HashMap(2);
    private static Map IDPSTD_DATA_MAP = new HashMap(2);
    private static Map IDPEX_DATA_MAP = new HashMap(18);
    
    static {
        //TBD - save of display name will be completed once the backend api
        //is ready
        //GEN_DATA_MAP.put(TF_NAME, Collections.EMPTY_SET);
        GEN_DATA_MAP.put(TFTOKENISSUER_NAME, Collections.EMPTY_SET);
        GEN_DATA_MAP.put(TFTOKENISSUER_ENDPT, Collections.EMPTY_SET);
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
    }
    
    static {
        SPSTD_DATA_MAP.put(TFSSO_NOTIFENDPT, Collections.EMPTY_SET);
    }
    
    static {
        IDPEX_DATA_MAP.put(TFSIGNCERT_ALIAS, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFCLAIM_TYPES, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFAUTOFED_ENABLED, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPAUTOFED_ATTR, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPASSERT_TIME, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPAUTH_CONTMAPPER, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPACCT_MAPPER, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPATTR_MAPPER, Collections.EMPTY_SET);
        IDPEX_DATA_MAP.put(TFIDPATTR_MAP, Collections.EMPTY_SET);
    }
    
    static {
        // TBD-  for sign cert alias
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
        SPSSOConfigElement spconfig = null;
        Map SPAttributes = null;
        try {
            spconfig = WSFederationMetaManager.getSPSSOConfig(realm,fedid);
            if (spconfig != null) {
                SPAttributes =  WSFederationMetaUtils.getAttributes(spconfig);
            }
        } catch (WSFederationMetaException e) {
            debug.warning
                ("WSFedPropertiesModelImpl.getServiceProviderAttributes", e);
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
        IDPSSOConfigElement idpconfig = null;        
        Map IDPAttributes = null;
        try {
            idpconfig = WSFederationMetaManager.getIDPSSOConfig(realm,fedid);
            if (idpconfig != null) {
                IDPAttributes = WSFederationMetaUtils.getAttributes(idpconfig);
            }
        } catch (WSFederationMetaException e) {
            debug.warning
                ("WSFedPropertiesModelImpl.getIdentityProviderAttributes", e);
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
    public FederationElement getEntityDesc(String realm, String fedid)
        throws AMConsoleException {
        FederationElement fedelm = null;
        try {
            fedelm = WSFederationMetaManager.getEntityDescriptor(realm, fedid);
        } catch (WSFederationMetaException e) {
            debug.warning("WSFedPropertiesModelImpl.getEntityDesc", e);
            throw new AMConsoleException(e.getMessage());
        }
        return fedelm;
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
        String tkendpt = null;
        tkendpt =  WSFederationMetaManager.getTokenIssuerEndpoint(fedElem);
        return tkendpt;
    }
    
    /**
     * Returns UriNamedClaimTypesOffered for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return UriNamedClaimTypesOffered for the FederationElement passed.
     */
    public List getClaimType(FederationElement fedElem) {
        UriNamedClaimTypesOfferedElement claimTypes =
                WSFederationMetaManager.getUriNamedClaimTypesOffered(fedElem);
        List claimList = null;
        List stringList  = new ArrayList();
        String uri = null;
        String displayName = null;
        DescriptionType desc = null;
        String description = null;
        if(claimTypes != null) {
            int iClaim = 0;
            int arr = 0;
            claimList = claimTypes.getClaimType();
            for(iClaim = 0; iClaim < claimList.size(); iClaim += 1) {
                ClaimType ct = (ClaimType)claimList.get(iClaim);
                uri = ct.getUri();
                displayName = ct.getDisplayName().getValue();
                desc = ct.getDescription();
            }
            
            // TBD-- display format need to be decided
            stringList.add("Uri="+uri);
            stringList.add("Display Name="+displayName);
            stringList.add("Description="+description);
        }
        return stringList;
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
     * Returns SingleSignoutNotificationEndPoint for FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return SingleSignoutNotificationEndPoint for FederationElement passed.
     */
    public String getSingleSignoutNotificationEndPoint (
        FederationElement fedElem) {
        
        //TBD- once the api is available... Currently hardcoded
        //signCert =
        //WSFederationMetaManager.getSignoutNotificationEndPoint(fedElem);
        String ssoEndpt = "test";
        return ssoEndpt;
    }
    
    /**
     * Saves the extended metadata attribute values for the SP.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param spExtvalues has the extended attribute value pairs of SP.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setSPExtAttributeValues(
        String realm,
        String fedId,
        Map spExtvalues
    ) throws AMConsoleException {
        try {
            
            //fed is the extended entity configuration object under the realm
            FederationConfigElement fed =
                    WSFederationMetaManager.getEntityConfig(realm,fedId);
            SPSSOConfigElement  spsso = getspsso(fed);
            if (spsso != null){
                BaseConfigType bcon = updateSPBaseConfig(spsso, spExtvalues);
            }
            
            //saves the attributes by passing the new fed object
            WSFederationMetaManager.setEntityConfig(realm,fed);
            
        } catch (WSFederationMetaException e) {
            debug.warning
                    ("WSFedPropertiesModelImpl.setAttributeValues 1", e);
            throw new AMConsoleException(e.getMessage());
        }
        
    }
    
    /**
     * Saves the standard attribute values for the SP.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param idpExtvalues has the extended attribute value pairs of IDP.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setIDPExtAttributeValues(
        String realm,
        String fedId,
        Map idpExtValues
    ) throws AMConsoleException {
        try {
            
            // fed is the extended entity configuration under the realm
            FederationConfigElement fed =
                    WSFederationMetaManager.getEntityConfig(realm,fedId);
            IDPSSOConfigElement  idpsso = getidpsso(fed);
            if (idpsso != null){
               BaseConfigType bcon = updateIDPBaseConfig(idpsso, idpExtValues);
            }
            
            //saves the new configuration by passing new fed element created
            WSFederationMetaManager.setEntityConfig(realm,fed);
            
        } catch (WSFederationMetaException e) {
            debug.warning
                    ("WSFedPropertiesModelImpl.setAttributeValues 1", e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Saves the standard attribute values from the General page.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param idpStdvalues has the General standard attribute value pairs.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setGenAttributeValues(
        String realm,
        String fedId,
        Map idpStdValues
    ) throws AMConsoleException {
        
        String tknissEndPt = null;
        String tknissName = null;
        Iterator it = idpStdValues.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key.equals("TokenIssuerEndpoint")) {
                HashSet set = (HashSet) idpStdValues.get(key);
                Iterator i = set.iterator();
                while ((i !=  null)&& (i.hasNext())) {
                    tknissEndPt = (String) i.next();
                }
            }else if (key.equals("TokenIssuerName")) {
                HashSet set = (HashSet) idpStdValues.get(key);
                Iterator i = set.iterator();
                while ((i !=  null)&& (i.hasNext())) {
                    tknissName = (String) i.next();
                }
                //TBD -- for claimtype
            }
        }
        try {
            //fedElem is standard metadata federation element under the realm.
            FederationElement fedElem =
                    WSFederationMetaManager.getEntityDescriptor(realm, fedId);
            for (Iterator iter = fedElem.getAny().iterator();
            iter.hasNext(); ) {
                Object o = iter.next();
                if (o instanceof TokenIssuerEndpointElement) {
                    ((TokenIssuerEndpointElement)o).getAddress().
                            setValue(tknissEndPt);
                } else if (o instanceof TokenIssuerNameElement) {
                    ((TokenIssuerNameElement)o).setValue(tknissName);
                } else if (o instanceof UriNamedClaimTypesOfferedElement) {
                    //TBD
                }
            }
            WSFederationMetaManager.setFederation(realm, fedElem);
        } catch (WSFederationMetaException e) {
            debug.warning
                    ("WSFedPropertiesModelImpl.setAttributeValues 1", e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Saves the standard attribute values from the SP.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * param spStdvalues has the standard attribute value pairs of SP.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setSPSTDAttributeValues(
        String realm,
        String fedId,
        Map spStdValues
    ) throws AMConsoleException {
        //TBD - when API is checked in
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
        //IDPSSOConfigElement ?????
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
    private BaseConfigType updateIDPBaseConfig (
        IDPSSOConfigElement idpsso,
        Map values
    ) throws AMConsoleException {
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
            debug.warning("ImportEntityModel.updateBaseConfig", e);
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
    private BaseConfigType updateSPBaseConfig (
        SPSSOConfigElement spsso,
        Map values
    ) throws AMConsoleException {
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
            debug.warning("ImportEntityModel.updateBaseConfig", e);
            throw new AMConsoleException(e.getMessage());
        }
        return bcon;
    }
    
    /**
     * Returns a map of wsfed general attributes.
     *
     * @return Map of wsfed general attributes.
     */
    public Map getGenDataMap() {
        return GEN_DATA_MAP;
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
     * Returns a map of Wsfed Service Provider Standard attributes.
     *
     * @return Map of Wsfed Service Provider Standard attributes.
     */
    public Map getSPSTDDataMap() {
        return SPSTD_DATA_MAP;
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

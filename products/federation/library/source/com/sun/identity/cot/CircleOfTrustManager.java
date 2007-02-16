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
 * $Id: CircleOfTrustManager.java,v 1.3 2007-02-16 02:02:49 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.cot;

import javax.xml.bind.JAXBException;
import com.sun.identity.federation.meta.IDFFCOTUtils;
import com.sun.identity.federation.meta.IDFFMetaException;
import com.sun.identity.federation.meta.IDFFMetaManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.plugin.configuration.ConfigurationManager;
import com.sun.identity.plugin.configuration.ConfigurationInstance;
import com.sun.identity.plugin.configuration.ConfigurationException;
import com.sun.identity.saml2.meta.SAML2COTUtils;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import java.util.Collections;
import java.util.HashMap;


/**
 * This class has methods to manage the circle of trust.
 */
public class CircleOfTrustManager {
    
    private static final String SUBCONFIG_ID = "cot";
    private static final int SUBCONFIG_PRIORITY = 0;
    
    private static ConfigurationInstance configInst;
    private static Debug debug = COTUtils.debug;
    
    static {
        try {
            configInst = ConfigurationManager.getConfigurationInstance(
                    COTConstants.COT_CONFIG_NAME);
        } catch (ConfigurationException ce) {
            debug.error(
                    "COTManager.static: Unable to get COT service config",ce);
            configInst = null;
        }
        if (configInst != null) {
            try {
                configInst.addListener(new COTServiceListener());
            } catch (ConfigurationException ce) {
                debug.error("COTManager.static: Unable to add " +
                        "ConfigurationListener for COT service.",ce);
            }
        }
    }
    
    /**
     * Constructor for <code>COTManager</code>.
     *
     * @throws COTException if unable to construct <code>COTManager</code>.
     */
    public CircleOfTrustManager() throws COTException {
        if (configInst == null) {
            throw new COTException("nullConfig", null);
        }
    }
    
    
    /**
     * Creates a circle of trust.
     *
     * @param realm the realm under which the circle of trust will be created.
     * @param cotDescriptor the circle of trust descriptor object to be created.
     * @throws COTException if unable to create the circle of trust.
     */
    public void createCircleOfTrust(String realm,
            CircleOfTrustDescriptor cotDescriptor)
            throws COTException {
        String classMethod = "COTManager.createCircleOfTrust: ";
        if (cotDescriptor == null) {
            throw new COTException("nullCot", null);
        }
        String entityId = null;
        
        if (realm == null) {
            realm = "/";
        }
        
        String name = cotDescriptor.getCircleOfTrustName();
        if ((name == null) || (name.trim().length() == 0)) {
            String[] data = { realm };
            LogUtil.error(Level.INFO,
                    LogUtil.NO_COT_NAME_CREATE_COT_DESCRIPTOR,data);
            throw new COTException("invalidCOTName", null);
        }
        
        if (getAllCirclesOfTrust(realm).contains(name)) {
            debug.error(classMethod + "Circle of trust already exists" + name);
            String[] data = { name, realm };
            LogUtil.error(Level.INFO,
                    LogUtil.COT_EXISTS_CREATE_COT_DESCRIPTOR,data);
            
            throw new COTException("cotExists",data);
        }
        
        String cotType = cotDescriptor.getCircleOfTrustType();
        COTUtils.isValidCOTType(cotType);
        
        Map attrs = cotDescriptor.getAttributes();
        // Filter out the entityid which does not exist in the system
        Set tp = cotDescriptor.getTrustedProviders();
        if (tp != null && !tp.isEmpty()) {
            Set entityIds = getAllEntities(realm,cotType);
            if (entityIds == null || entityIds.isEmpty())  {
                cotDescriptor.setTrustedProviders(new HashSet());
                attrs = cotDescriptor.getAttributes();
            } else {
                if (entityIds.containsAll(tp)) {
                    if (entityIds.retainAll(tp)) {
                        if (debug.messageEnabled()) {
                            debug.message(classMethod + "Following entity id: "
                                    + entityIds +
                                    " are valid and will be added to the " +
                                    "circle of trust: " + name +
                                    ". The rest will be ignored.");
                        }
                        cotDescriptor.setTrustedProviders(entityIds);
                        attrs = cotDescriptor.getAttributes();
                    }
                }
            }
        }
        
        // update the extended entity config
        
        Set newTP = cotDescriptor.getTrustedProviders();
        
        updateEntityConfig(realm,name,cotType,newTP);
        
        // create the cot node
        try {
            configInst.createConfiguration(realm, name, attrs);
            if (debug.messageEnabled()) {
                debug.message( classMethod + "circle of trust is created.");
            }
            String[] data = {name, realm};
            LogUtil.access(Level.INFO,LogUtil.COT_DESCRIPTOR_CREATED,data);
        } catch (ConfigurationException e) {
            debug.error(classMethod, e);
            String[] data = { e.getMessage(), name, realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_CREATE_COT_DESCRIPTOR,data);
            throw new COTException(e);
        }
    }
    
    /**
     * Modifies the attributes of a circle of trust.
     *
     * @param realm the realm the circle of trust is in.
     * @param cotDescriptor circle of trust descriptor that contains
     *        the new set of attributes
     * @throws COTException if unable to modify the circle of trust.
     */
    public void modifyCircleOfTrust(String realm,
            CircleOfTrustDescriptor cotDescriptor)
            throws COTException {
        String classMethod = "COTManager.modifyCircleOfTrust :";
        if (cotDescriptor == null) {
            throw new COTException("nullCot", null);
        }
        
        if (realm == null) {
            realm = "/";
        }
        
        String name = cotDescriptor.getCircleOfTrustName();
        isValidCOTName(realm,name);
        
        String cotType = cotDescriptor.getCircleOfTrustType();
        COTUtils.isValidCOTType(cotType);
        
        try {
            Map attrs = cotDescriptor.getAttributes();
            configInst.setConfiguration(realm, name, attrs);
        } catch (ConfigurationException e) {
            debug.error(classMethod, e);
            String[] data = { e.getMessage(), name, realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_MODIFY_COT_DESCRIPTOR,data);
            
            throw new COTException(e);
        }
    }
    
    /**
     * Returns a set of names of all circle of trusts.
     *
     * @param realm The realm under which the circle of trust resides.
     * @return Set of names of all circle of trusts.
     * @throws COTException if unable to read circle of trust.
     */
    public Set getAllCirclesOfTrust(String realm,String cotType)
    throws COTException {
        Set valueSet = null;
        Set cotSet = new HashSet();
        if (realm == null) {
            realm = COTConstants.ROOT_REALM;
        }
        String classMethod = "COTManager.getAllCircleOfTrust: ";
        try {
            valueSet = configInst.getAllConfigurationNames(realm);
            if ((valueSet != null) && !valueSet.isEmpty()) {
                for (Iterator iter = valueSet.iterator(); iter.hasNext(); ) {
                    String name = (String)iter.next();
                    Map attrMap = configInst.getConfiguration(realm, name);
                    if (COTUtils.getFirstEntry(attrMap,COTConstants.COT_TYPE).
                            equalsIgnoreCase(cotType)) {
                        cotSet.add(name);
                    }
                }
            }
        } catch (ConfigurationException e) {
            debug.error(classMethod, e);
            String[] data = { e.getMessage(), realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_GET_ALL_COT_DESCRIPTOR,data);
            throw new COTException(e);
        }
        return cotSet;
    }
    
    /**
     * Returns a set of names of all circle of trusts.
     *
     * @param realm The realm under which the circle of trust resides.
     * @return Set of names of all circle of trusts.
     * @throws COTException if unable to read circle of trust.
     */
    public Set getAllCirclesOfTrust(String realm) throws COTException {
        Set valueSet = null;
        String classMethod = "COTManager.getAllCircleOfTrust: ";
        try {
            valueSet = configInst.getAllConfigurationNames(realm);
        } catch (ConfigurationException e) {
            debug.error(classMethod, e);
            String[] data = { e.getMessage(), realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_GET_ALL_COT_DESCRIPTOR,data);
            throw new COTException(e);
        }
        if (debug.messageEnabled()) {
            debug.message(classMethod + "trustedProviders are :" + valueSet);
        }
        return valueSet;
    }
    
    /**
     * Checks if the circle of trust type is IDFF or SAML2
     *
     * @param cotType the circle of trust type.
     * @retrun true if circle of trust type is IDFF or SAML2.
     * @throws COTException if the circle of trust type is not valid.
     */
    boolean isValidCOTType(String cotType) throws COTException {
        String classMethod = "COTManager:isValidCOTType";
        
        if (!COTUtils.isValidCOTType(cotType)) {
            debug.error(classMethod +"Invalid Circle of Trust Type " + cotType);
            String[] data = { cotType };
            throw new COTException("invalidCOTType",data);
        }
        return true;
    }
    
    /**
     * Returns a set of entity identities based on the circle of
     * trust type IDFF or SAML2
     *
     * @param realm the realm name
     * @param type the circle of trust type.
     * @throws COTExcepton if the circle of trust type is invalid.
     */
    Set getAllEntities(String realm,String type) throws COTException {
        Set entityIds = Collections.EMPTY_SET;
        if (type != null) {
            if (type.equalsIgnoreCase(COTConstants.IDFF)) {
                entityIds = getIDFFEntities();
            } else if (type.equalsIgnoreCase(COTConstants.SAML2)) {
                entityIds = getSAML2Entities(realm);
            } else {
                String[] data = { type };
                throw new COTException("invalidCOTType",data);
            }
        }
        return entityIds;
    }
    
    /**
     * Returns a set of all IDFF entity identifiers.
     */
    Set getIDFFEntities() throws COTException  {
        try {
            IDFFMetaManager idffMetaMgr = new IDFFMetaManager(null);
            return idffMetaMgr.getAllEntities();
        } catch (IDFFMetaException idffe) {
            throw new COTException(idffe);
        }
    }
    
    /**
     * Returns a set of all SAML2 identifiers
     */
    Set getSAML2Entities(String realm) throws COTException {
        try {
            SAML2MetaManager saml2MetaMgr = new SAML2MetaManager();
            return saml2MetaMgr.getAllEntities(realm);
        } catch (SAML2MetaException sme) {
            throw new COTException(sme);
        }
    }
    
    /**
     * Updates the trusted providers list in the entity configuration.
     * The Circle of Trust type determines whether the entiry is an
     * IDFF or SAML2 provider.
     *
     * @param realm the realm in which the entity configuration is in.
     * @param cotName the name of the circle of trust.
     * @param cotType the circle of trust type , IDFF or SAML2.
     * @param trustedProvider a set of trusted provider identifiers to
     *        be updated in the entity configuration.
     * @throws COTException if there is an error updating the entity
     *         configuration.
     */
    void updateEntityConfig(String realm,String cotName,String cotType,
            Set trustedProviders) throws COTException {
        if (cotType.equalsIgnoreCase(COTUtils.IDFF)) {
            updateIDFFEntityConfig(realm,cotName,trustedProviders);
        } else if (cotType.equalsIgnoreCase(COTUtils.SAML2)) {
            updateSAML2EntityConfig(realm,cotName,trustedProviders);
        } else {
            String[] args = { cotType };
            throw new COTException("invalidCOTType",args);
        }
    }
    
    /**
     * Updates the entity configuration.
     * The Circle of Trust type determines whether the entiry is an
     * IDFF or SAML2 provider.
     *
     * @param realm the realm in which the entity configuration is in.
     * @param cotName the name of the circle of trust.
     * @param cotType the circle of trust type , IDFF or SAML2.
     * @param entityID the entity identifier.
     * @throws COTException if there is an error updating the entity
     *         configuration.
     */
    void updateEntityConfig(String realm,String cotName,
            String cotType,String entityID) throws COTException,JAXBException {
        if (cotType.equalsIgnoreCase(COTUtils.IDFF)) {
            try {
                new IDFFCOTUtils().updateEntityConfig(cotName,entityID);
            } catch (IDFFMetaException idffe) {
                throw new COTException(idffe);
            }
        } else if (cotType.equalsIgnoreCase(COTUtils.SAML2)) {
            try {
                new SAML2COTUtils().updateEntityConfig(realm,cotName,entityID);
            } catch (SAML2MetaException idffe) {
                throw new COTException(idffe);
            }
        } else {
            String[] args = { cotType };
            throw new COTException("invalidCOTType",args);
        }
    }
    
    /**
     * Remove circle of trust from teh entity configuration.
     *
     * @param realm the realm name.
     * @param cotName the circle of trust name.
     * @param cotType the circle of trust type.
     * @param entityID the entity identifier to be updated.
     * @throws COTException if there is  error updating entity configuration.
     * @throws JAXBException if there is error retrieving entity configuration.
     */
    void removeFromEntityConfig(String realm,String cotName,String cotType,
            String entityID)
            throws COTException,JAXBException {
        if (cotType.equalsIgnoreCase(COTConstants.IDFF)) {
            try {
                new IDFFCOTUtils().removeFromEntityConfig(cotName,entityID);
            } catch (IDFFMetaException idme) {
                throw new COTException(idme);
            }
        } else if (cotType.equalsIgnoreCase(COTConstants.SAML2)) {
            try {
                new SAML2COTUtils().removeFromEntityConfig(realm,cotName,
                        entityID);
            } catch (SAML2MetaException sme) {
                throw new COTException(sme);
            }
        } else {
            String[] data = { cotType };
            throw new COTException("invalidCOTType",data);
        }
        
    }
    
    /**
     * Updates the IDFF Entity Configuration.
     *
     * @param realm the realm name.
     * @param cotName the circle of trust name.
     * @param trustedProviders set of trusted provider names.
     * @throws COTException if there is an error updating the configuration.
     */
    void updateIDFFEntityConfig(String realm,String cotName,
            Set trustedProviders) throws COTException {
        String classMethod = "COTManager:updateIDFFEntityConfig";
        IDFFCOTUtils idffCotUtils = new IDFFCOTUtils();
        String entityId = null;
        if (trustedProviders != null && !trustedProviders.isEmpty()) {
            for (Iterator iter =
                    trustedProviders.iterator();iter.hasNext();) {
                entityId = (String) iter.next();
                try {
                    idffCotUtils.updateEntityConfig(cotName, entityId);
                } catch (IDFFMetaException idfe) {
                    throw new COTException(idfe);
                } catch (JAXBException jbe) {
                    debug.error(classMethod, jbe);
                    String[] data = { jbe.getMessage(),cotName,
                    entityId,realm};
                    LogUtil.error(Level.INFO,
                            LogUtil.CONFIG_ERROR_CREATE_COT_DESCRIPTOR,
                            data);
                    throw new COTException(jbe);
                }
            }
        }
    }
    
    /**
     * Updates the SAML2 Entity Configuration.
     *
     * @param realm the realm name.
     * @param cotName the circle of trust name.
     * @param trustedProviders set of trusted provider names.
     * @throws COTException if there is an error updating the configuration.
     */
    void updateSAML2EntityConfig(String realm,String cotName,
            Set trustedProviders) throws COTException {
        String classMethod = "COTManager:updateSAML2EntityConfig";
        String entityId = null;
        SAML2COTUtils saml2CotUtils= new SAML2COTUtils();
        if (trustedProviders != null && !trustedProviders.isEmpty()) {
            for (Iterator iter = trustedProviders.iterator();
            iter.hasNext();) {
                entityId = (String) iter.next();
                try {
                    saml2CotUtils.updateEntityConfig(realm,cotName,
                            entityId);
                } catch (SAML2MetaException sme) {
                    throw new COTException(sme);
                } catch (JAXBException e) {
                    debug.error(classMethod, e);
                    String[] data = {e.getMessage(),cotName,entityId,realm};
                    LogUtil.error(Level.INFO,
                            LogUtil.CONFIG_ERROR_CREATE_COT_DESCRIPTOR,
                            data);
                    throw new COTException(e);
                }
            }
        }
    }
    
    /**
     * Adds entity identifier in a circle of trust under the realm.
     *
     * @param realm The realm under which the circle of trust will be
     *              modified.
     * @param name the name of the circle of trust
     * @param entityId the entity identifier
     * @throws COTException if unable to add member to the
     *         circle of trust.
     */
    public void addCircleOfTrustMember(String realm,String cotName,
            String cotType,String entityId)
            throws COTException {
        String classMethod = "COTManager.addCircleOfTrustMember: ";
        if (realm == null) {
            realm = "/";
        }
        if ((cotName == null) || (cotName.trim().length() == 0)) {
            String[] data = { realm };
            LogUtil.error(Level.INFO,
                    LogUtil.NULL_COT_NAME_ADD_COT_DESCRIPTOR,data);
            throw new COTException("invalidCOTName", null);
        }
        if ((entityId == null) || (entityId.trim().length() == 0)) {
            String[] data = { realm };
            LogUtil.error(Level.INFO,
                    LogUtil.NULL_ENTITYID_ADD_COT_DESCRIPTOR,data);
            throw new COTException("invalidEntityID", null);
        }
        try {
            Map attrs = configInst.getConfiguration(realm, cotName);
            //validate cot type
            isValidCotTypeInConfig(attrs,cotType);
            // add the cot to the entity config descriptor
            updateEntityConfig(realm, cotName,cotType, entityId);
            
            // add the entityid to the cot
            CircleOfTrustDescriptor cotDesc;
            if (attrs == null) {
                cotDesc = new CircleOfTrustDescriptor(cotName,cotType,"active");
            } else {
                cotDesc = new CircleOfTrustDescriptor(cotName,cotType,attrs);
            }
            
            if (!cotDesc.add(entityId)) {
                debug.error(classMethod +
                        "fail to add entityid to the circle of trust."
                        + entityId + " in Realm " + realm);
                String[] args = { realm , entityId };
                throw new COTException("addCOTFailed", args);
            } else {
                modifyCircleOfTrust(realm, cotDesc);
            }
        } catch (ConfigurationException e) {
            debug.error(classMethod, e);
            String[] data = { e.getMessage(), cotName, entityId, realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_ADD_COT_MEMBER,data);
            throw new COTException(e);
        } catch (JAXBException jbe) {
            debug.error(classMethod, jbe);
            String[] data = { jbe.getMessage(),cotName,entityId,
            realm};
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_CREATE_COT_DESCRIPTOR,
                    data);
            throw new COTException(jbe);
        }
    }
    
    /**
     * Removes entity from circle of trust under the realm.
     *
     * @param realm the realm to which the circle of trust belongs.
     * @param cotName  the circle of trust name.
     * @param entityId the entity identifier.
     * @throws COTException if there is an error removing entity from the
     *         circle of trust.
     */
    public void removeCircleOfTrustMember(String realm,String cotName,
            String cotType, String entityId)
            throws COTException {
        String classMethod = "COTManager.removeCircleOfTrustMember: ";
        if ((cotName == null) || (cotName.trim().length() == 0)) {
            String[] data = { cotName, realm };
            LogUtil.error(Level.INFO,
                    LogUtil.NULL_COT_NAME_REMOVE_COT_MEMBER,data);
            throw new COTException("invalidCOTName", null);
        }
        if ((entityId == null) || (entityId.trim().length() == 0)) {
            String[] data = {cotName, entityId, realm };
            LogUtil.error(Level.INFO,
                    LogUtil.NULL_ENTITYID_REMOVE_COT_MEMBER,data);
            throw new COTException("invalidEntityID", null);
        }
        
        if (realm == null) {
            realm = COTConstants.ROOT_REALM;
        }
        
        try {
            // Remove the cot from the cotlist attribute in
            // the entity config.
            removeFromEntityConfig(realm, cotName, cotType, entityId);
            
            // Remove entity id from the cot
            CircleOfTrustDescriptor cotDesc;
            Map attrs = configInst.getConfiguration(realm, cotName);
            if (attrs == null) {
                cotDesc = new CircleOfTrustDescriptor(cotName,cotType,
                        COTConstants.ACTIVE);
            } else {
                cotDesc = new CircleOfTrustDescriptor(cotName, cotType,attrs);
            }
            
            if (!cotDesc.remove(entityId)) {
                debug.error(classMethod +
                        "fail to remove entityid from the circle of trust." +
                        realm);
                String[] data = { entityId , realm };
                throw new COTException("removeCOTFailed", data);
            } else {
                modifyCircleOfTrust(realm, cotDesc);
            }
        } catch (ConfigurationException e) {
            debug.error(classMethod, e);
            String[] data = { e.getMessage(), cotName, entityId, realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_REMOVE_COT_MEMBER,data);
            throw new COTException(e);
        } catch  (JAXBException jaxbe) {
            debug.error(classMethod, jaxbe);
            String[] data = { jaxbe.getMessage(), cotName, entityId, realm};
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_REMOVE_COT_MEMBER,data);
            
            throw new COTException(jaxbe);
        }
    }
    
    /**
     * Lists trusted providers in a circle of trust under the realm.
     *
     * @param realm The realm under which the circle of trust will be
     *              modified.
     * @param cotName the name of the circle of trust
     * @return Set of trusted providers or null if no member in the
     *                circle of trust
     * @throws COTException if unable to list member in the
     *         circle of trust.
     */
    public Set listCircleOfTrustMember(String realm,String cotName,
            String cotType) throws COTException {
        String classMethod = "COTManager.listCircleOfTrustMember: ";
        if ((cotName == null) || (cotName.trim().length() == 0)) {
            String[] data = { realm };
            LogUtil.error(Level.INFO,
                    LogUtil.NULL_COT_NAME_LIST_COT,data);
            throw new COTException("invalidCOTName", null);
        }
        if (realm == null) {
            realm = "/";
        }
        Set trustedProviders = new HashSet();
        try {
            CircleOfTrustDescriptor cotDesc = null;
            Map attrs = configInst.getConfiguration(realm, cotName);
            if (attrs == null) {
                return null;
            } else {
                isValidCotTypeInConfig(attrs,cotType);
                cotDesc = new CircleOfTrustDescriptor(cotName,cotType,attrs);
                trustedProviders = cotDesc.getTrustedProviders();
            }
        } catch (ConfigurationException e) {
            debug.error(classMethod, e);
            String[] data = { e.getMessage(), cotName, realm};
            LogUtil.error(Level.INFO,LogUtil.CONFIG_ERROR_LIST_COT_MEMBER,data);
            throw new COTException(e);
        }
        return trustedProviders;
    }
    
    /**
     * Deletes the circle of trust under the realm.
     *
     * @param realm The realm under which the circle of trust resides.
     * @param cotName Name of the circle of trust.
     * @throws COTException if unable to delete the circle of trust.
     */
    public void deleteCircleOfTrust(String realm,String cotName,String cotType)
    throws COTException {
        String classMethod = "COTManager.deleteCircleOfTrust:" ;
        if (realm == null) {
            realm = "/";
        }
        String[] data = { cotName, realm };
        
        isValidCOTName(realm,cotName);
        isValidCOTType(cotType);
        
        try {
            Set trustProviders = null;
            Map attrs = configInst.getConfiguration(realm, cotName);
            
            if (attrs != null) {
                isValidCotTypeInConfig(attrs,cotType);
                CircleOfTrustDescriptor cotDesc=
                        new CircleOfTrustDescriptor(cotName, cotType , attrs);
                
                trustProviders = cotDesc.getTrustedProviders();
            }
            if (attrs == null || trustProviders == null ||
                    trustProviders.isEmpty()) {
                configInst.deleteConfiguration(realm, cotName, null);
                LogUtil.access(Level.INFO,LogUtil.COT_DESCRIPTOR_DELETED,data);
            } else {
                debug.error(classMethod + "Delete circle of trust" +
                        " is not allowed since it contains members.");
                LogUtil.error(Level.INFO,
                        LogUtil.HAS_ENTITIES_DELETE_COT_DESCRIPTOR,data);
                String[] args = { cotName , realm , cotType };
                throw new COTException("deleteCOTFailedHasMembers", args);
            }
        } catch (ConfigurationException e) {
            debug.error(classMethod, e);
            String[] args = { e.getMessage(), cotName, realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_DELETE_COT_DESCRIPTOR,args);
            throw new COTException(e);
        }
    }
    
    public CircleOfTrustDescriptor getCircleOfTrust(String realm,String name)
    throws COTException {
        return getCircleOfTrust(realm,name,COTUtils.SAML2);
    }
    /**
     * Returns the circle of trust under the realm.
     *
     * @param realm The realm under which the circle of trust resides.
     * @param name Name of the circle of trust.
     * @param cotType the circle of trust type indicating its a SAML2
     *        or IDFF.
     * @return <code>SAML2CircleOfTrustDescriptor</code> containing the
     * attributes of the given CircleOfTrust.
     * @throws SAML2MetaException if unable to retrieve the circle of trust.
     */
    public CircleOfTrustDescriptor getCircleOfTrust(String realm,String name,
            String cotType) throws COTException {
        String classMethod = "COTManager.getCircleOfTrust :";
        if (realm == null) {
            realm = "/";
        }
        isValidCOTName(realm,name);
        isValidCOTType(cotType);
        
        String[] data = { name, realm };
        
        CircleOfTrustDescriptor cotDesc = COTCache.getCircleOfTrust(realm,name);
        if (cotDesc != null) {
            LogUtil.access(Level.FINE,LogUtil.COT_FROM_CACHE,data);
        } else {
            try {
                Map attrs = configInst.getConfiguration(realm, name);
                if (attrs == null) {
                    cotDesc = new CircleOfTrustDescriptor(name,cotType,
                            COTConstants.ACTIVE);
                } else {
                    cotDesc = new CircleOfTrustDescriptor(name,cotType,attrs);
                }
                
                COTCache.putCircleOfTrust(realm, name, cotDesc);
                LogUtil.access(Level.INFO,LogUtil.COT_DESCRIPTOR_RETRIEVED,
                        data);
            } catch (ConfigurationException e) {
                debug.error(classMethod, e);
                data[0] = e.getMessage();
                data[1] = name;
                data[2] = realm;
                LogUtil.error(Level.INFO,
                        LogUtil.CONFIG_ERROR_GET_COT_DESCRIPTOR,data);
                throw new COTException(e);
            }
        }
        return cotDesc;
    }
    
    /**
     * Returns a set of names of all active circle of trusts.
     *
     * @param realm The realm under which the circle of trust resides.
     * @return Set of names of all active circle of trusts.
     * @throws COTException if the names of
     *         circle of trusts cannot be read.
     */
    public Set getAllActiveCirclesOfTrust(String realm)
    throws COTException {
        String classMethod = "COTManager.getAllActiveCirclesOfTrust: ";
        Set activeAuthDomains = new HashSet();
        
        try {
            Set valueSet = configInst.getAllConfigurationNames(realm);
            
            if ((valueSet != null) && !valueSet.isEmpty()) {
                for (Iterator iter = valueSet.iterator(); iter.hasNext(); ) {
                    String name = (String)iter.next();
                    Map attrMap = configInst.getConfiguration(realm, name);
                    
                    if (COTUtils.getFirstEntry(attrMap,
                            COTConstants.COT_STATUS).
                            equalsIgnoreCase(
                            COTConstants.ACTIVE)) {
                        activeAuthDomains.add(name);
                    }
                }
            }
        } catch (ConfigurationException se) {
            debug.error(classMethod, se);
            String[] data = { se.getMessage(), realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_GET_ALL_ACTIVE_COT,data);
            throw new COTException(se);
        }
        return activeAuthDomains;
    }
    
    /**
     * Returns a set of names of all active circle of trusts.
     *
     * @return Set of names of all active circle of trusts.
     * @throws SAML2MetaException if the names of
     *         circle of trusts cannot be read.
     */
    public Set getAllActiveIDFFCirclesOfTrust()
    throws COTException {
        String classMethod = "COTManager.getAllActiveCirclesOfTrust: ";
        Set activeIDFFCots = new HashSet();
        String realm = COTConstants.ROOT_REALM;
        
        try {
            Set valueSet = configInst.getAllConfigurationNames(realm);
            
            if ((valueSet != null) && !valueSet.isEmpty()) {
                for (Iterator iter = valueSet.iterator(); iter.hasNext(); ) {
                    String name = (String)iter.next();
                    Map attrMap = configInst.getConfiguration(realm, name);
                    String cotType = COTUtils.getFirstEntry(attrMap,
                            COTConstants.COT_TYPE);
                    String cotStatus = COTUtils.getFirstEntry(attrMap,
                            COTConstants.COT_STATUS);
                    
                    
                    if (isValidCOTType(cotType) && isActiveCOT(cotStatus)) {
                        activeIDFFCots.add(name);
                    }
                }
            }
        } catch (ConfigurationException se) {
            debug.error(classMethod, se);
            String[] data = { se.getMessage(), COTConstants.IDFF , realm };
            
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_GET_ALL_ACTIVE_COT,data);
            throw new COTException(se);
        }
        return activeIDFFCots;
    }
    
    
    /**
     * Determines if entity is in the circle of trust under the realm.
     *
     * @param realm The realm under which the circle of trust resides.
     * @param name Name of the Circle of Trust.
     * @param cotType the Circle of Trust type IDFF or SAML2
     * @param entityId the entity identifier
     * @throws COTException if unable to determine this entity in the
     *                                  circle of trust.
     */
    public boolean isInCircleOfTrust(String realm, String name, String cotType,
            String entityId) {
        Set tProviders = new HashSet();
        try {
            CircleOfTrustDescriptor cotd = getCircleOfTrust(realm,name,cotType);
            tProviders = cotd.getTrustedProviders();
        } catch (Exception me) {
            debug.error("COTManager.isInCircleOfTrust", me);
            String[] data = {me.getMessage(), name, entityId , realm };
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_RETREIVE_COT,
                    data);
        }
        return (tProviders !=null && tProviders.size() > 0
                && tProviders.contains(entityId)) ;
    }
    
    /**
     * Validates the circle of trust name.
     *
     * @param realm the realm the circle of trust is in.
     * @param name the circle of trust name.
     * @return true if circle of trust name is valid.
     * @throws <code>COTException</code> if the circle of trust name is invalid.
     */
    boolean isValidCOTName(String realm,String name) throws COTException {
        String classMethod = "COTManager:isValidCOTName";
        if ((name == null) || (name.trim().length() == 0) ||
                !getAllCirclesOfTrust(realm).contains(name)) {
            
            debug.error(classMethod + "invalid circle of trust name :" + name);
            String[] data = {realm , name};
            LogUtil.error(Level.INFO,LogUtil.INVALID_COT_NAME,data);
            throw new COTException("invalidCOTName", data);
        }
        return true;
    }
    
    /**
     * Validates the COT Type in the configuration.
     *
     * @param attrs map of  circle of trust attributes where
     *        key is the attribute name and value is the
     *        value of the attribute.
     * @param cotType the value of circle of trust type.
     * @throws COTException if the circle of trust type in
     *         the configuration is invalid.
     */
    boolean isValidCotTypeInConfig(Map attrs, String cotType)
        throws COTException {
        COTUtils.isValidCOTType(cotType);
        String value = COTUtils.getFirstEntry(attrs,COTConstants.COT_TYPE);
        if (value == null || !value.equalsIgnoreCase(cotType)) {
            String[] data = { cotType };
            throw new COTException("invalidCOTType",data);
        }
        
        return true;
    }
    
    /**
     * Checks if circle of trust status is active.
     */
    boolean isActiveCOT(String cotStatus) {
        return (cotStatus != null &&
                cotStatus.equalsIgnoreCase(COTConstants.ACTIVE));
    }
    
    /**
     * Returns a map of circle of trust name and the value
     * of the <code>sun-fm-trusted-providers</code> attribute
     * The key in the map is the circle of trust name and
     * value is a set of providers retreived from the attribute.
     *
     * @return a map where the key is the cirle of trust name
     *         and value is Set of providers retrieved from
     *         the  <code>sun-fm-trusted-providers</code> attribute.
     * @throws COTException if there is an error retrieving the
     *                      trusted providers.
     * TODO : cache this 
     */
    public Map getIDFFCOTProviderMapping() throws COTException {
        String classMethod = "COTManager.getAllActiveCirclesOfTrust: ";
        Map cotMap = new HashMap();
        String realm = COTConstants.ROOT_REALM;
        
        try {
            Set valueSet = configInst.getAllConfigurationNames(realm);
            
            if ((valueSet != null) && !valueSet.isEmpty()) {
                for (Iterator iter = valueSet.iterator(); iter.hasNext(); ) {
                    String name = (String)iter.next();
                    Map attrMap = configInst.getConfiguration(realm, name);
                    String cotType = COTUtils.getFirstEntry(attrMap,
                            COTConstants.COT_TYPE);
                    String cotStatus = COTUtils.getFirstEntry(attrMap,
                            COTConstants.COT_STATUS);
                    
                    
                    if ((cotType != null 
                            && cotType.equalsIgnoreCase(COTConstants.IDFF))
                            && isActiveCOT(cotStatus)) {
                        Set trustedProviders =
                                (Set) attrMap.get(
                                        COTConstants.COT_TRUSTED_PROVIDERS);
                        cotMap.put(name,trustedProviders);
                    }
                }
            }
        } catch (ConfigurationException se) {
            debug.error(classMethod, se);
            String[] data = { se.getMessage(), COTConstants.IDFF , realm };
            
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_GET_ALL_ACTIVE_COT,data);
            throw new COTException(se);
        }
        return cotMap;
    }
}

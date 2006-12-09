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
 * $Id: IDFFCOTUtils.java,v 1.2 2006-12-09 06:21:14 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.federation.meta;

import javax.xml.bind.JAXBException;
import com.sun.identity.cot.COTConstants;
import com.sun.identity.federation.jaxb.entityconfig.AttributeType;
import com.sun.identity.federation.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.federation.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.federation.jaxb.entityconfig.ObjectFactory;
import com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement;
import com.sun.identity.shared.debug.Debug;
import java.util.Iterator;
import java.util.List;

/**
 * This class utility methods to update circle of trust
 * attribute in the service and identity provider
 * configuration.
 */
public class IDFFCOTUtils {
    
    static final Debug debug = IDFFMetaUtils.debug;
    static final String COT_LIST = COTConstants.COT_LIST;
    
    
    /** Creates a new instance of IDFFCOTUtils */
    public IDFFCOTUtils() {
    }
    
    
    /**
     * Updates the entity config to add the circle of turst name to the
     * <code>cotlist</code> attribute. The Service Provider and Identity
     * Provider Configurations are updated.
     *
     * @param cotName the circle of trust name.
     * @entityID the name of the Entity identifier.
     * @throws IDFFMetaException if there is a configuration error when
     *         updating the configuration.
     * @throws JAXBException is there is an error updating the entity
     *          configuration.
     */
    public void updateEntityConfig(String cotName,String entityID)
    throws IDFFMetaException , JAXBException {
        String classMethod = "IDFFCOTUtils.updateEntityConfig: ";
        IDFFMetaManager idffMetaMgr = new IDFFMetaManager(null);
        ObjectFactory objFactory = new ObjectFactory();
        // Check whether the entity id existed in the DS
        EntityDescriptorElement entityDesc =
                idffMetaMgr.getEntityDescriptor(entityID);
        
        if (entityDesc == null) {
            debug.error(classMethod +" No such entity: " + entityID);
            String[] data = {entityID};
            throw new IDFFMetaException("invalidEntityID", data);
        }
        EntityConfigElement entityConfig =
                idffMetaMgr.getEntityConfig(entityID);
        if (entityConfig == null) {
            // create entity config and add the cot attribute
            BaseConfigType IDFFCOTUtils = null;
            AttributeType atype = objFactory.createAttributeType();
            atype.setName(COT_LIST);
            atype.getValue().add(cotName);
            // add to entityConfig
            entityConfig = objFactory.createEntityConfigElement();
            entityConfig.setEntityID(entityID);
            entityConfig.setHosted(false);
            // Decide which role EntityDescriptorElement includes
            // It could have one sp and one idp.
            if (IDFFMetaUtils.getSPDescriptor(entityDesc) != null) {
                IDFFCOTUtils = objFactory.createSPDescriptorConfigElement();
                IDFFCOTUtils.getAttribute().add(atype);
                entityConfig.getSPDescriptorConfig().add(IDFFCOTUtils);
            }
            if (IDFFMetaUtils.getIDPDescriptor(entityDesc) != null) {
                IDFFCOTUtils = objFactory.createIDPDescriptorConfigElement();
                IDFFCOTUtils.getAttribute().add(atype);
                entityConfig.getIDPDescriptorConfig().add(IDFFCOTUtils);
            }
            idffMetaMgr.setEntityConfig(entityConfig);
        } else {
            // update the sp and idp entity config
            List spConfigList = entityConfig.getSPDescriptorConfig();
            List idpConfigList = entityConfig.getIDPDescriptorConfig();
            updateCOTAttrInConfig(
                    spConfigList,cotName,entityConfig,objFactory,idffMetaMgr);
            updateCOTAttrInConfig(
                    idpConfigList,cotName,entityConfig,objFactory,idffMetaMgr);
        }
    }
    
    /**
     * Removes the circle trust name passed from the <code>cotlist</code>
     * list attribute in the Entity Config. The Service Provider and Identity
     * Provider Entity Configuration are updated.
     *
     * @param cotName the circle of trust name to be removed.
     * @param entityID the entity identifier of the provider.
     * @throws IDFFMetaException if there is an error updating the entity
     *          config.
     * @throws JAXBException if there is an error updating the entity config.
     */
    public void removeFromEntityConfig(String cotName,String entityID)
    throws IDFFMetaException, JAXBException {
        String classMethod = "IDFFCOTUtils.removeFromEntityConfig: ";
        IDFFMetaManager idffMetaMgr = new IDFFMetaManager(null);
        // Check whether the entity id existed in the DS
        EntityDescriptorElement entityDesc =
                idffMetaMgr.getEntityDescriptor(entityID);
        if (entityDesc == null) {
            debug.error(classMethod +"No such entity: " + entityID);
            String[] data = { entityID };
            throw new IDFFMetaException("invalidEntityID", data);
        }
        EntityConfigElement entityConfig =
                idffMetaMgr.getEntityConfig(entityID);
        if (entityConfig != null) {
            List spConfigList = entityConfig.getSPDescriptorConfig();
            List idpConfigList = entityConfig.getIDPDescriptorConfig();
            removeCOTNameFromConfig(spConfigList,cotName,
                    entityConfig,idffMetaMgr);
        }
    }
    
    /**
     * Checks if the name is contained in a list of values.
     */
    private boolean containsValue(List list, String name) {
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            if (((String) iter.next()).trim().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Updates the entity config to update the values of the
     * <code>cotlist</code> attribute.
     *
     * @param configList the list containing config elements.
     * @param cotName the circle of trust name.
     * @param entityConfig the <code>EntityConfigElement</code> object
     * @param objFactory the object factory object
     * @param idffMetaMgr the <code>IDFFMetaManager</code> object.
     * @throws <code>IDFFMetaException</code> if there is an error retrieving
     *         and updating the entityConfig.
     * @throws <code>JAXBException</code> if there is an error setting the
     *         config.
     */
    private void updateCOTAttrInConfig(List configList,String cotName,
            EntityConfigElement entityConfig,
            ObjectFactory objFactory,
            IDFFMetaManager idffMetaMgr)
            throws IDFFMetaException,JAXBException {
        boolean foundCOT = false;
        for (Iterator iter = configList.iterator(); iter.hasNext();) {
            BaseConfigType bConfig = (BaseConfigType)iter.next();
            List list = bConfig.getAttribute();
            for (Iterator iter2 = list.iterator(); iter2.hasNext();) {
                AttributeType avp = (AttributeType)iter2.next();
                if (avp.getName().trim().equalsIgnoreCase(COT_LIST)) {
                    foundCOT = true;
                    List avpl = avp.getValue();
                    if (avpl.isEmpty() ||!containsValue(avpl,cotName)) {
                        avpl.add(cotName);
                        idffMetaMgr.setEntityConfig(entityConfig);
                        break;
                    }
                }
            }
            // no cot_list in the original entity config
            if (!foundCOT) {
                AttributeType atype = objFactory.createAttributeType();
                atype.setName(COT_LIST);
                atype.getValue().add(cotName);
                list.add(atype);
                idffMetaMgr.setEntityConfig(entityConfig);
            }
        }
    }
    
    /**
     * Iterates through a list of entity config elements and
     * removes the circle trust name from the entity config.
     */
    private void removeCOTNameFromConfig(List configList,String cotName,
            EntityConfigElement entityConfig,
            IDFFMetaManager idffMetaMgr)
            throws IDFFMetaException {
        for (Iterator iter = configList.iterator(); iter.hasNext();) {
            BaseConfigType bConfig = (BaseConfigType)iter.next();
            List list = bConfig.getAttribute();
            for (Iterator iter2 = list.iterator(); iter2.hasNext();) {
                AttributeType avp = (AttributeType)iter2.next();
                if (avp.getName().trim().equalsIgnoreCase(COT_LIST)) {
                    List avpl = avp.getValue();
                    if (avpl != null && !avpl.isEmpty() &&
                            containsValue(avpl,cotName)) {
                        avpl.remove(cotName);
                        idffMetaMgr.setEntityConfig(entityConfig);
                        break;
                    }
                }
            }
        }
    }
}

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
 * $Id: IDFFEntityModelImpl.java,v 1.2 2007-08-03 23:12:25 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.federation.meta.IDFFMetaException;
import com.sun.identity.federation.meta.IDFFMetaManager;
import com.sun.identity.federation.meta.IDFFMetaUtils;
import com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement;
import com.sun.identity.liberty.ws.meta.jaxb.ObjectFactory;
import com.sun.identity.liberty.ws.meta.jaxb.ContactType;
import com.sun.identity.liberty.ws.meta.jaxb.OrganizationType;
import com.sun.identity.liberty.ws.meta.jaxb.AffiliationDescriptorType;
import com.sun.identity.liberty.ws.meta.jaxb.OrganizationNameType;
import com.sun.identity.liberty.ws.meta.jaxb.OrganizationDisplayNameType;
import com.sun.identity.liberty.ws.meta.jaxb.LocalizedURIType;
import com.sun.identity.federation.common.IFSConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

public class IDFFEntityModelImpl
    extends AMModelBase
    implements IDFFEntityModel 
{
    
    private IDFFMetaManager metaManager;
    private ObjectFactory objFactory;
    private Set realms = null;
    
    public IDFFEntityModelImpl(HttpServletRequest req, Map map) {
        super(req, map);
        try {
            realms = getRealmNames(getStartDN(), "*");
        } catch (AMConsoleException a) {
            debug.warning("IDFFEntityModel problem getting realm names");
            realms = Collections.EMPTY_SET;
        }
    }
    
    /**
     * Returns provider-affiliate common attribute values.
     * @param name Name of Entity Descriptor.
     * @return provider-affiliate common attribute values.
     * @throws IDFFMetaException if attribute values cannot be obtained.
     */
    public Map getCommonAttributeValues(String name)
        throws AMConsoleException 
    {
        Map values = new HashMap(26);
        String[] param = {name};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", param);
        
        try {
            IDFFMetaManager manager = getIDFFMetaManager();
            EntityDescriptorElement desc = manager.getEntityDescriptor(name);
            values.put(ATTR_VALID_UNTIL, returnEmptySetIfValueIsNull(
                desc.getValidUntil()));
            values.put(ATTR_CACHE_DURATION, returnEmptySetIfValueIsNull(
                desc.getCacheDuration()));
            
            getContactPersonAttributeValues(values, desc);
            getOrganizationAttributeValues(values, desc);
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", param);
        } catch (IDFFMetaException e) {
            String[] paramsEx = {name, getErrorString(e)};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES",
                paramsEx);
            throw new AMConsoleException(getErrorString(e));
        }
        return values;
    }
    
    /**
     * Modifies entity descriptor profile.
     *
     * @param name Name of entity descriptor.
     * @param map Map of attribute type to a Map of attribute name to values.
     * @throws AMConsoleException if profile cannot be modified.
     */
    public void modifyEntityProfile(String name, Map map)
        throws AMConsoleException 
    {
        String[] param = {name};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", param);
        
        try {
            IDFFMetaManager manager = getIDFFMetaManager();
            EntityDescriptorElement desc =  manager.getEntityDescriptor(name);
            
            desc.setValidUntil((String)AMAdminUtils.getValue(
                (Set)map.get(ATTR_VALID_UNTIL)));
            desc.setCacheDuration((String)AMAdminUtils.getValue(
                (Set)map.get(ATTR_CACHE_DURATION)));  //??
            
            modifyContactPerson(desc, map);
            modifyOrganization(desc, map);
            manager.setEntityDescriptor(desc);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", param);
        } catch (IDFFMetaException e) {
            String[] paramsEx = {name, getErrorString(e)};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR", paramsEx);
            throw new AMConsoleException(getErrorString(e));
        }
    }
    
    
    protected IDFFMetaManager getIDFFMetaManager()
        throws IDFFMetaException 
    {
        if (metaManager == null) {
            metaManager = new IDFFMetaManager(getUserSSOToken());
        }
        return metaManager;
    }
    
    protected ObjectFactory getObjectFactoryInstance()
        throws IDFFMetaException 
    {
        if (objFactory == null) {
            objFactory = new ObjectFactory();
        }
        return objFactory;
    }
    
    
    private void getContactPersonAttributeValues(
        Map values,
        EntityDescriptorElement desc) {
        ContactType contactPerson = desc.getContactPerson() ;
        if (contactPerson != null) {
            values.put(ATTR_CP_GIVENNAME, returnEmptySetIfValueIsNull(
                contactPerson.getGivenName()));
            values.put(ATTR_CP_LASTNAME, returnEmptySetIfValueIsNull(
                contactPerson.getSurName()));
            values.put(ATTR_CP_TYPE, returnEmptySetIfValueIsNull(
                contactPerson.getContactType()));
            values.put(ATTR_CP_COMPANY, returnEmptySetIfValueIsNull(
                contactPerson.getCompany()));
            values.put(ATTR_CP_PRINCIPAL_ID, returnEmptySetIfValueIsNull(
                contactPerson.getLibertyPrincipalIdentifier() ));
            values.put(ATTR_CP_EMAILS, returnEmptySetIfValueIsNull(
                convertListToSet(contactPerson.getEmailAddress())));
            values.put(ATTR_CP_PHONES, returnEmptySetIfValueIsNull(
                convertListToSet(contactPerson.getTelephoneNumber())));
        } else {
            values.put(ATTR_CP_GIVENNAME, Collections.EMPTY_SET);
            values.put(ATTR_CP_LASTNAME, Collections.EMPTY_SET);
            values.put(ATTR_CP_TYPE, Collections.EMPTY_SET);
            values.put(ATTR_CP_COMPANY, Collections.EMPTY_SET);
            values.put(ATTR_CP_PRINCIPAL_ID, Collections.EMPTY_SET);
            values.put(ATTR_CP_EMAILS, Collections.EMPTY_SET);
            values.put(ATTR_CP_PHONES, Collections.EMPTY_SET);
        }
    }
    
    private void getOrganizationAttributeValues(
        Map values,
        EntityDescriptorElement desc) {
        
        OrganizationType org = desc.getOrganization();
        
        if (org != null) {
            List orgNameList = (List) org.getOrganizationName();
            int sz = orgNameList.size();
            int i=0;
            
            //name
            Set newOrgName = new HashSet();
            for(i=0; i<sz; i++){
                String str = ((OrganizationNameType)
                orgNameList.get(i)).getLang();
                str = str + "|" + ((OrganizationNameType)
                orgNameList.get(i)).getValue();
                newOrgName.add(str);
            }
            values.put(ATTR_ORG_NAMES,
                returnEmptySetIfValueIsNull(newOrgName));
            
            //Organization display name
            List orgDisplayNameList = (List) org.getOrganizationDisplayName();
            sz = orgDisplayNameList.size();
            Set newOrgDisplayet = new HashSet();
            for(i=0; i<sz; i++){
                String str = ((OrganizationDisplayNameType)
                orgDisplayNameList.get(i)).getLang();
                str = str + "|" + ((OrganizationDisplayNameType)
                orgDisplayNameList.get(i)).getValue();
                newOrgDisplayet.add(str);
            }
            values.put(ATTR_ORG_DISPLAY_NAMES,
                returnEmptySetIfValueIsNull(newOrgDisplayet));
            
            //Organization URLs
            List orgURLList = (List) org.getOrganizationURL();
            sz = orgURLList.size();
            Set newOrgURLset = new HashSet();
            for(i=0; i<sz; i++){
                String str = ((LocalizedURIType)
                orgURLList.get(i)).getLang();
                str = str + "|" + ((LocalizedURIType)
                orgURLList.get(i)).getValue();
                newOrgURLset.add(str);
            }
            values.put(ATTR_ORG_URLS,
                returnEmptySetIfValueIsNull(newOrgURLset));
            
        } else {
            values.put(ATTR_ORG_NAMES, Collections.EMPTY_SET);
            values.put(ATTR_ORG_DISPLAY_NAMES, Collections.EMPTY_SET);
            values.put(ATTR_ORG_URLS, Collections.EMPTY_SET);
        }
    }
    
    private void modifyContactPerson(EntityDescriptorElement desc, Map map)
        throws AMConsoleException 
    {
        ContactType contactPerson = desc.getContactPerson();
        try{
            if(contactPerson==null){
                ObjectFactory obj = getObjectFactoryInstance();
                contactPerson = (ContactType)obj.createContactType();
            }
            String givenName = (String)AMAdminUtils.getValue(
                (Set)map.get(ATTR_CP_GIVENNAME));
            String surName = (String)AMAdminUtils.getValue(
                (Set)map.get(ATTR_CP_LASTNAME));
            String cpType = (String)AMAdminUtils.getValue(
                (Set)map.get(ATTR_CP_TYPE));
            String company = (String)AMAdminUtils.getValue(
                (Set)map.get(ATTR_CP_COMPANY));
            String principalId = (String)AMAdminUtils.getValue(
                (Set)map.get(ATTR_CP_PRINCIPAL_ID));
            
            if ((givenName.trim().length() > 0) ||
                (surName.trim().length() > 0) ||
                (company.trim().length() > 0) ||
                (principalId.trim().length() > 0)
            ) {
                contactPerson.setGivenName(givenName);
                contactPerson.setSurName(surName);
                contactPerson.setContactType(cpType);
                contactPerson.setCompany(company);
                contactPerson.setLibertyPrincipalIdentifier(principalId);
            }
            
            Set emails = (Set)map.get(ATTR_CP_EMAILS);
            contactPerson.getEmailAddress().clear();
            if (!emails.isEmpty()){
                for (Iterator iter = emails.iterator(); iter.hasNext(); ) {
                    contactPerson.getEmailAddress().add(iter.next());
                }
            }
            
            Set phones = (Set)map.get(ATTR_CP_PHONES);
            contactPerson.getTelephoneNumber().clear();
            if(!phones.isEmpty()){
                for (Iterator iter = phones.iterator(); iter.hasNext(); ) {
                    contactPerson.getTelephoneNumber().add(iter.next());
                }
            }
        } catch (IDFFMetaException e) {
             throw new AMConsoleException(getErrorString(e));
        } catch (JAXBException e) {
             throw new AMConsoleException(getErrorString(e));
        }
        desc.setContactPerson(contactPerson);
    }
    
    private void modifyOrganization(EntityDescriptorElement desc, Map map)
        throws AMConsoleException 
    {
        
        OrganizationType org = desc.getOrganization();       
        try{
            ObjectFactory obj = getObjectFactoryInstance();
            if(org==null){
                org = (OrganizationType) obj.createOrganizationType();
            }
            Set newOrgName = (Set)map.get(ATTR_ORG_NAMES);
            Set newOrgDisplay = (Set)map.get(ATTR_ORG_DISPLAY_NAMES);
            Set newOrgURLs = (Set)map.get(ATTR_ORG_URLS);
            
            org.getOrganizationName().clear();
            if (!newOrgName.isEmpty()) {
                for (Iterator iter = newOrgName.iterator(); iter.hasNext(); ) {
                    String str = (String) iter.next();
                    StringTokenizer st = new StringTokenizer(str, "|");
                    String token = st.nextToken().trim();
                    OrganizationNameType orgNameObj =
                        (OrganizationNameType) obj.createOrganizationNameType();
                    orgNameObj.setLang(token);
                    token = st.nextToken().trim();
                    orgNameObj.setValue(token);
                    org.getOrganizationName().add(orgNameObj);
                }
            }
            
            org.getOrganizationDisplayName().clear();
            if (!newOrgDisplay.isEmpty() ) {
                for (Iterator iter2 = newOrgDisplay.iterator();
                iter2.hasNext(); ) {
                    String str = (String) iter2.next();
                    
                    StringTokenizer st = new StringTokenizer(str, "|");
                    String token = st.nextToken().trim();
                    OrganizationDisplayNameType orgNameDisplayObj =
                        (OrganizationDisplayNameType)
                        obj.createOrganizationDisplayNameType();
                    orgNameDisplayObj.setLang(token);
                    token = st.nextToken().trim();
                    orgNameDisplayObj.setValue(token);
                    org.getOrganizationDisplayName().add(orgNameDisplayObj);
                }
            }
            
            org.getOrganizationURL().clear();
            if (!newOrgURLs.isEmpty()) {
                for (Iterator iter3 = newOrgURLs.iterator();
                iter3.hasNext(); ) {
                    String str = (String) iter3.next();
                    StringTokenizer st = new StringTokenizer(str, "|");
                    String token = st.nextToken().trim();
                    LocalizedURIType orgURLObj =
                        (LocalizedURIType) obj.createLocalizedURIType();
                    orgURLObj.setLang(token);
                    token = st.nextToken().trim();
                    orgURLObj.setValue(token);
                    org.getOrganizationURL().add(orgURLObj);
                }
            }                                  
        } catch (IDFFMetaException e) {
             throw new AMConsoleException(getErrorString(e));
        } catch (JAXBException e) {
             throw new AMConsoleException(getErrorString(e));            
        }
        desc.setOrganization(org);
    }
    
    private Set returnEmptySetIfValueIsNull(String str) {
        Set set = Collections.EMPTY_SET;
        if (str != null) {
            set = new HashSet(2);
            set.add(str);
        }
        return set;
    }
    
    private Set returnEmptySetIfValueIsNull(Set set) {
        return (set != null) ? set : Collections.EMPTY_SET;
    }
    
    public Set convertListToSet(List list) {
        Set s = new HashSet();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            s.add(it.next());
        }
        return s;
    }
}

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
 * $Id: FSAuthDomainsModelImpl.java,v 1.1 2007-06-11 22:05:58 asyhuang Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.federation.meta.IDFFMetaException;
import com.sun.identity.federation.meta.IDFFMetaManager;
import com.sun.identity.federation.meta.IDFFMetaUtils;
import com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement;
import com.sun.identity.cot.CircleOfTrustManager;
import com.sun.identity.cot.CircleOfTrustDescriptor;
import com.sun.identity.cot.COTException;
import com.sun.identity.cot.COTUtils;
import com.sun.identity.cot.COTConstants;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.console.base.model.AMFormatUtils;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import com.sun.identity.common.DisplayUtils;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.StringTokenizer;

public class FSAuthDomainsModelImpl
    extends AMModelBase
    implements FSAuthDomainsModel
{
    private CircleOfTrustManager cotManager;
    private static Map DATA_MAP = new HashMap(10);

    static {
	DATA_MAP.put(TF_NAME, Collections.EMPTY_SET);
	DATA_MAP.put(TF_DESCRIPTION, Collections.EMPTY_SET);
	DATA_MAP.put(TF_IDFF_WRITER_SERVICE_URL, Collections.EMPTY_SET);
	DATA_MAP.put(TF_IDFF_READER_SERVICE_URL, Collections.EMPTY_SET);
        DATA_MAP.put(TF_SAML2_WRITER_SERVICE_URL, Collections.EMPTY_SET);
	DATA_MAP.put(TF_SAML2_READER_SERVICE_URL, Collections.EMPTY_SET);        
	DATA_MAP.put(SINGLE_CHOICE_STATUS, Collections.EMPTY_SET);       
        DATA_MAP.put(SINGLE_CHOICE_REALM, Collections.EMPTY_SET);
    }    

    /**
     * Creates a simple model using default resource bundle. 
     *
     * @param req HTTP Servlet Request
     * @param map of user information
     */
    public FSAuthDomainsModelImpl(HttpServletRequest req,  Map map) {
        super(req, map);
    }

    /**
     * Returns authentication domains 
     *
     * @return a set of authentication domains 
     */
    public Set getAuthenticationDomains() {
        Set results = new HashSet ();  
        String realm = COTConstants.ROOT_REALM;
        try {
            CircleOfTrustManager manager = getCircleOfTrustManager();           
            Set realmSet = getRealmNames("/", "*");
            Set cotSet;
            String cotName;
            Iterator it = realmSet.iterator();
            while (it.hasNext()) {           
                 realm = (String) it.next();                
                 cotSet = manager.getAllCirclesOfTrust(realm);
                 Iterator it2 = cotSet.iterator();
                 while (it2.hasNext()) {
                     cotName = (String)it2.next();               
                     results.add(cotName);                               
                 }
             }
        } catch (COTException e) {
            String[] paramsEx = {realm, getErrorString(e)};
            logEvent("FEDERATION_EXCEPTION_GET_AUTH_DOMAINS", paramsEx);
            debug.warning(
                "FSAuthDomainsModelImpl.getAuthenticationDomains", e);
        } catch (AMConsoleException e){           
              debug.warning(
                "FSAuthDomainsModelImpl.getAuthenticationDomains", e);
        }
	return results;
    }

    public Set getCircleOfTrustDescriptors(){
        Set descSet = new HashSet();           
        String realm = COTConstants.ROOT_REALM;
        try {
            CircleOfTrustManager manager = getCircleOfTrustManager();          
            Set realmSet = getRealmNames("/", "*");
            Set cotSet;
            String cotName;
            Iterator it = realmSet.iterator();
            CircleOfTrustDescriptor descriptor;
            while (it.hasNext()) {              
                realm = (String) it.next();                
                cotSet = manager.getAllCirclesOfTrust(realm);
                Iterator it2 = cotSet.iterator();
                while (it2.hasNext()) {
                    cotName = (String)it2.next();         
                    descriptor = manager.getCircleOfTrust(realm, cotName);
                    descSet.add(descriptor);                                 
                }
             }
        } catch (COTException e) {

            String[] paramsEx = {realm, getErrorString(e)};
            logEvent("FEDERATION_EXCEPTION_GET_AUTH_DOMAINS", paramsEx);
            debug.warning(
                "FSAuthDomainsModelImpl.getAuthenticationDomains", e);
        } catch (AMConsoleException e){             
              debug.warning(
                "FSAuthDomainsModelImpl.getAuthenticationDomains", e);
        }        
        return descSet;
    }
    
    /**
     * Creates authentication domain.
     *
     * @param attrValues Map of attribute name to set of attribute values.
     * @throws AMConsoleException if authentication domain created.
     */
    public void createAuthenticationDomain(            
            Map attrValues, 
            Set providers)
	throws AMConsoleException
    {            
        String realm = (String)AMAdminUtils.getValue(
                (Set)attrValues.get(SINGLE_CHOICE_REALM));                  
        String status = (String)AMAdminUtils.getValue(
                (Set)attrValues.get(SINGLE_CHOICE_STATUS));        
        String name = (String)AMAdminUtils.getValue(
	    (Set)attrValues.get(TF_NAME));
	if (name.trim().length() == 0) {
	    throw new AMConsoleException(
		"authdomain.authentication.domain.name.missing.message");
	}
        String[] param = {name};
	logEvent("ATTEMPT_CREATE_AUTH_DOMAIN", param);        
	try {
	    CircleOfTrustDescriptor descriptor = 
                new CircleOfTrustDescriptor(name, realm, status);            
            descriptor.setTrustedProviders(providers);                                         
	    descriptor.setCircleOfTrustDescription(
		(String)AMAdminUtils.getValue(
		    (Set)attrValues.get(TF_DESCRIPTION)));
	    descriptor.setIDFFReaderServiceURL(
		(String)AMAdminUtils.getValue(
		    (Set)attrValues.get(TF_IDFF_READER_SERVICE_URL)));	  
            descriptor.setIDFFWriterServiceURL(
		(String)AMAdminUtils.getValue(
		    (Set)attrValues.get(TF_IDFF_WRITER_SERVICE_URL)));
            descriptor.setSAML2ReaderServiceURL(
		(String)AMAdminUtils.getValue(
		    (Set)attrValues.get(TF_SAML2_READER_SERVICE_URL)));	  
            descriptor.setSAML2WriterServiceURL(
		(String)AMAdminUtils.getValue(
		    (Set)attrValues.get(TF_SAML2_WRITER_SERVICE_URL)));                                          
	    CircleOfTrustManager manager = getCircleOfTrustManager();                      
            manager.createCircleOfTrust(realm,descriptor);                    
	    logEvent("SUCCEED_CREATE_AUTH_DOMAIN", param);            
	} catch (COTException e) {
	    String strError = getErrorString(e);
	    String[] paramsEx = {name, strError};           
	    logEvent("FEDERATION_EXCEPTION_CREATE_AUTH_DOMAIN", paramsEx);
	    throw new AMConsoleException(strError);
        } 
    }

    /**
     * Deletes authentication domains.
     *
     * @param names Name of authentication domains.
     * @throws AMConsoleException if authentication domains cannot be deleted.
     */
    public void deleteAuthenticationDomain(String realm, String cotName)
	throws AMConsoleException
    {                     
	
	String[] param = {realm, cotName};
	logEvent("ATTEMPT_DELETE_AUTH_DOMAINS", param);
	try {
            CircleOfTrustManager manager = getCircleOfTrustManager();
            manager.deleteCircleOfTrust(realm, cotName);             
	    logEvent("SUCCEED_DELETE_AUTH_DOMAIN", param);
	} catch (COTException e) {
	    String strError = getErrorString(e);
	    String[] paramsEx = {realm, cotName, strError};
	    logEvent("FEDERATION_EXCEPTION_DELETE_AUTH_DOMAIN", paramsEx);
	    throw new AMConsoleException(strError);
	}
    }

    private CircleOfTrustManager getCircleOfTrustManager()
	throws COTException
    {
	if (cotManager == null) {
	    cotManager = new  CircleOfTrustManager();
	}
	return cotManager;
    }

    /**
     * Returns attribute values.
     *
     * @param name Name of authentication domain.
     * @return attribute values.
     * @throws IDFFMetaException if attribute values cannot be 
     *         obtained.
     */
    public Map getAttributeValues(String realm, String name)
	throws AMConsoleException
    {      
	Map values = new HashMap(8);
	String[] param = {name};
	logEvent("ATTEMPT_GET_AUTH_DOMAIN_ATTR_VALUES", param);
	try {
	    CircleOfTrustManager manager = 
                    getCircleOfTrustManager();            
	    CircleOfTrustDescriptor desc = 
                    manager.getCircleOfTrust(realm, name);
	    values.put(TF_DESCRIPTION, AMAdminUtils.wrapInSet(
		desc.getCircleOfTrustDescription()));
	    values.put(TF_IDFF_WRITER_SERVICE_URL, AMAdminUtils.wrapInSet(
		desc.getIDFFWriterServiceURL()));
	    values.put(TF_IDFF_READER_SERVICE_URL, AMAdminUtils.wrapInSet(
		desc.getIDFFReaderServiceURL()));
            values.put(TF_SAML2_WRITER_SERVICE_URL, AMAdminUtils.wrapInSet(
		desc.getIDFFWriterServiceURL()));
	    values.put(TF_SAML2_READER_SERVICE_URL, AMAdminUtils.wrapInSet(
		desc.getIDFFReaderServiceURL()));
            values.put(SINGLE_CHOICE_REALM, AMAdminUtils.wrapInSet(
		desc.getCircleOfTrustRealm()));
	    values.put(SINGLE_CHOICE_STATUS, AMAdminUtils.wrapInSet(
		desc.getCircleOfTrustStatus()));            
	    logEvent("SUCCEED_GET_AUTH_DOMAIN_ATTR_VALUES", param);
	} catch (COTException e) {
	    String strError = getErrorString(e);
	    String[] paramsEx = {name, strError};
	    logEvent("FEDERATION_EXCEPTION_GET_AUTH_DOMAIN_ATTR_VALUES",
		paramsEx);
	    throw new AMConsoleException(strError);
	}
	return values;
    }

    /**
     * Set attribute values.
     *
     * @param name Name of authentication domain.
     * @param values Map of attribute name to value.
     * @throws IDFFMetaException if attribute values cannot be set.
     */
    public void setAttributeValues(String realm, String name, Map values)
	throws AMConsoleException
    {                        
	String[] param = {name};
	logEvent("ATTEMPT_MODIFY_AUTH_DOMAIN", param);	              
	try {            
	    CircleOfTrustManager manager = 
                getCircleOfTrustManager();            
	    CircleOfTrustDescriptor desc = 
                manager.getCircleOfTrust(realm, name);	    
	    desc.setCircleOfTrustDescription((String)AMAdminUtils.getValue(
		(Set)values.get(TF_DESCRIPTION)));           
	    desc.setIDFFWriterServiceURL((String)AMAdminUtils.getValue(
		(Set)values.get(TF_IDFF_WRITER_SERVICE_URL)));            
	    desc.setIDFFReaderServiceURL((String)AMAdminUtils.getValue(
		(Set)values.get(TF_IDFF_READER_SERVICE_URL)));            
            desc.setSAML2WriterServiceURL((String)AMAdminUtils.getValue(
		(Set)values.get(TF_SAML2_WRITER_SERVICE_URL)));            
	    desc.setSAML2ReaderServiceURL((String)AMAdminUtils.getValue(
		(Set)values.get(TF_SAML2_READER_SERVICE_URL)));            
	    desc.setCircleOfTrustStatus((String)AMAdminUtils.getValue(
		(Set)values.get(SINGLE_CHOICE_STATUS)));                         
	    manager.modifyCircleOfTrust(realm,desc);                       
	    logEvent("SUCCEED_MODIFY_AUTH_DOMAIN", param);
	} catch (COTException e) {
	    String strError = getErrorString(e);
	    String[] paramsEx = {name, strError};
	    logEvent("FEDERATION_EXCEPTION_MODIFY_AUTH_DOMAIN", paramsEx);
	    throw new AMConsoleException(strError);
	}
    }

    /**
     * Returns a map of authentication domain attributes.
     *
     * @return Map of authentication domain attributes.
     */
    public Map getDataMap() {
	return DATA_MAP;
    }

    /**
     * Returns a set of provider names.
     *
     * @return a set of provider names.
     * @throws AMConsoleException if provider names cannot be obtained.
     */
    public Set getAllProviderNames(String realm)
	throws AMConsoleException {        
        Set availableEntities = new HashSet();                  
        try {                                
            SAML2MetaManager saml2Mgr = new SAML2MetaManager();
            Set saml2Entities = saml2Mgr.getAllEntities(realm);                           
            Iterator it = saml2Entities.iterator();           
            while (it.hasNext()){                
                String entityId = (String) it.next();                   
                StringBuffer sb = new StringBuffer();
                sb.append(entityId).append("|saml2");                        
                availableEntities.add(sb.toString());     
            }                                               
	    logEvent("SUCCEED_GET_ALL_PROVIDER_NAMES", null);            
	    return (availableEntities != null) ? 
                availableEntities : Collections.EMPTY_SET;            	
	} catch (SAML2MetaException e) {
	    String strError = getErrorString(e);
	    String[] paramEx = {strError};
	    logEvent("ATTEMPT_GET_ALL_PROVIDER_NAMES", paramEx);
	    throw new AMConsoleException(strError);
	}
    }

    /**
     * Returns a set of provider names under a authentication domain.
     *
     * @param name Name of authentication domain.
     * @return a set of provider names under a authentication domain.
     * @throws AMConsoleException if provider names cannot be obtained.
     */
    public Set getTrustedProviderNames(String realm, String name)
	throws AMConsoleException {
	try {
	    String[] param = {name};
	    logEvent("ATTEMPT_GET_PROVIDER_NAMES_UNDER_AUTH_DOMAIN", param);
	    CircleOfTrustManager manager = getCircleOfTrustManager();	    
            CircleOfTrustDescriptor desc = manager.getCircleOfTrust(realm, name);
            Set providers = desc.getTrustedProviders();
	    logEvent("SUCCEED_GET_PROVIDER_NAMES_UNDER_AUTH_DOMAIN", param);
	    return (providers != null) ? providers : Collections.EMPTY_SET;
	} catch (COTException e) {
	    String strError = getErrorString(e);
	    String[] paramsEx = {name, strError};
	    logEvent(
		"FEDERATION_EXCEPTION_GET_PROVIDER_NAMES_UNDER_AUTH_DOMAIN",
		paramsEx);
	    throw new AMConsoleException(strError);
	}
    }

    /**
     * Adds providers.
     * @param realm realm of circle of trust
     * @param cotName Name of circle of trust
     * @param names Names provider to be added.
     * @throws AMConsoleException if provider cannot be added.
     */
    public void addProviders(String realm, String cotName, Collection names)
        throws AMConsoleException {       
        String cotType = COTConstants.SAML2;        
        String entityId = null;             
	String providerNames = AMAdminUtils.getString(names, ",", false);
	String[] params = {realm, cotName, providerNames};
	logEvent("ATTEMPT_ADD_PROVIDERS_TO_AUTH_DOMAIN", params);
	try {                 
            CircleOfTrustManager manager = getCircleOfTrustManager();          
            CircleOfTrustDescriptor cotDescriptor = 
                    manager.getCircleOfTrust(realm,cotName);
            Set existingEntity = cotDescriptor.getTrustedProviders();            
            if (existingEntity !=null){                        
                Iterator it = existingEntity.iterator();
                while (it.hasNext()){                             
                    String entityString = (String) it.next();                         
                    String delims = "|";
                    StringTokenizer tokens = 
                            new StringTokenizer(entityString, delims);
                    if(tokens.countTokens()==2){                   
                        entityId=tokens.nextToken();                                               
                        cotType=tokens.nextToken();                  
                        manager.removeCircleOfTrustMember(
                                realm,cotName,cotType, entityId);            
                    }
                }
            }
            
            if(names != null){
                int sz=names.size();             
                for (int i=0; i<sz; i++){
                    String entityString = (String) ((ArrayList)names).get(i);                      
                    String delims = "|";
                    StringTokenizer tokens = 
                            new StringTokenizer(entityString, delims);
                   
                   if(tokens.countTokens()==2){                   
                        entityId=tokens.nextToken();                                               
                        cotType=tokens.nextToken();                         
                        manager.addCircleOfTrustMember(
                                realm, cotName, cotType, entityId);                               
                   }
                }
            }                       
	    logEvent("SUCCEED_ADD_PROVIDERS_TO_AUTH_DOMAIN", params);
	} catch (COTException e) {           
	    String strError = getErrorString(e);
	    String[] paramsEx = {realm, cotName, providerNames, strError};
	    logEvent("FEDERATION_EXCEPTION_ADD_PROVIDERS_TO_AUTH_DOMAIN",
		paramsEx);
	    throw new AMConsoleException(strError);
	}
    }
    
     /*
     * Returns the realm names that match the specified filter value.
     *
     * @param base Base realm name for this search. null indicates root
     *        suffix.
     * @param filter Filter string.
     * @return realms names that match the filter.
     * @throws AMConsoleException if search fails.
     */
    public Set getRealmNames(String base, String filter)
        throws AMConsoleException
    {
        if ((base == null) || (base.length() == 0)) {
            base = getStartDN();
        }
        String[] param = {base};
        logEvent("ATTEMPT_GET_REALM_NAMES", param);
        try {
            OrganizationConfigManager orgMgr = 
                    new OrganizationConfigManager(getUserSSOToken(), base);
            logEvent("SUCCEED_GET_REALM_NAMES", param);
            return appendBaseDN(base,
                orgMgr.getSubOrganizationNames(filter, true), filter);
        } catch (SMSException e) {
            String strError = getErrorString(e);
            String[] paramsEx = {base, strError};
            logEvent("SMS_EXCEPTION_GET_REALM_NAMES", paramsEx);
            throw new AMConsoleException(strError);
        }
    }
         
    /**
     * Returns realm that have name matching 
     *
     * @param base Base realm name for this search. null indicates root
     *        suffix.
     * @return realm that have name matching  
     * @throws AMConsoleException if search fails.
     */
    public String getRealm(String name)
        throws AMConsoleException
    {
         String realm = null;                          
         Set cotDescSet = getCircleOfTrustDescriptors();
         CircleOfTrustDescriptor desc;
         for (Iterator iter = cotDescSet.iterator(); 
                iter.hasNext(); ) {
            desc = (CircleOfTrustDescriptor)iter.next();
            String cotName = desc.getCircleOfTrustName();
            if(cotName.equals(name)){
                realm = desc.getCircleOfTrustRealm();  
                break;
            }                          
         }                
         return realm;
    }

    /*
     * Search results are relative to the base (where the search was 
     * performed. Use this to add the base back to the search result, 
     * ending up with a fully qualified name.
     */
    private Set appendBaseDN(String base, Set results, String filter) {
        Set altered = new HashSet();
        String displayName = null;
        if (base.equals("/")) {
            displayName = AMFormatUtils.DNToName(this, getStartDSDN());
        } else {
            int idx = base.lastIndexOf("/");
            displayName = (idx != -1) ? base.substring(idx+1) : base;
        }
        if (DisplayUtils.wildcardMatch(displayName, filter)) {
            altered.add(base);
        }
        if ((results != null) && (!results.isEmpty())) {
            for (Iterator i = results.iterator(); i.hasNext(); ) {
                String name = (String)i.next();
                if (name.charAt(0) != '/') {
                    if (base.charAt(base.length() -1) == '/') {
                        altered.add(base + name);
                    } else {
                        altered.add(base + "/" + name);
                    }
                } else {
                    if (base.charAt(base.length() -1) == '/') {
                        altered.add(base.substring(0, base.length()-1) + name);
                    } else {
                        altered.add(base + name);
                    }
                }
            }
        }
        return altered;
    }

}

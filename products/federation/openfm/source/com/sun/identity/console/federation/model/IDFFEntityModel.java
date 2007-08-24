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
 * $Id: IDFFEntityModel.java,v 1.2 2007-08-24 18:17:11 asyhuang Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMConsoleException;
import java.util.Set;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface IDFFEntityModel
    extends AMModel 
{
    
    /**
     * Attribute Name for Entity Descriptor Description.
     */
    String ATTR_DESCRIPTION = "tfDescription";
    
    /**
     * Attribute Name for Entity Descriptor Valid Until.
     */
    String ATTR_VALID_UNTIL = "tfValidUntil";
    
    /**
     * Attribute Name for Entity Descriptor Cache Duration.
     */
    String ATTR_CACHE_DURATION = "tfCacheDuration";
    
    
    /**
     * Attribute Name for Entity Descriptor Contact Person Given Name.
     */
    String ATTR_CP_GIVENNAME = "tfContactPersonGivenName";
    
    /**
     * Attribute Name for Entity Descriptor Contact Person Last Name.
     */
    String ATTR_CP_LASTNAME = "tfContactPersonLastName";
    
    /**
     * Attribute Name for Entity Descriptor Contact Person Type.
     */
    String ATTR_CP_TYPE = "singleChoiceContactPersonType";
    
    /**
     * Attribute Name for Entity Descriptor Contact Person Company.
     */
    String ATTR_CP_COMPANY = "tfContactPersonCompany";
    
    /**
     * Attribute Name for Entity Descriptor Contact Person Principal ID.
     */
    String ATTR_CP_PRINCIPAL_ID = "tfContactPersonPrincipalID";
    
    /**
     * Attribute Name for Entity Descriptor Contact Person Email Addresses.
     */
    String ATTR_CP_EMAILS = "elistContactPersonEmails";
    
    /**
     * Attribute Name for Entity Descriptor Contact Person Phone Numbers.
     */
    String ATTR_CP_PHONES = "elistContactPersonPhones";
    
    /**
     * Attribute Name for Entity Descriptor Organization Names.
     */
    String ATTR_ORG_NAMES = "elistOrgNames";
    
    /**
     * Attribute Name for Entity Descriptor Organization Display Names.
     */
    String ATTR_ORG_DISPLAY_NAMES = "elistOrgDisplayNames";
    
    /**
     * Attribute Name for Entity Descriptor Organization URLs.
     */
    String ATTR_ORG_URLS = "elistOrgURLs";
    
    /**
     * Attribute name of affiliate ID.
     */
    String ATTR_AFFILIATE_ID = "tfAffiliateID";
    
    /**
     * Attribute name of affiliate Owner ID.
     */
    String ATTR_AFFILIATE_OWNER_ID = "tfAffiliateOwnerID";
    
    /**
     * Attribute name of affiliate's Valid Until.
     */
    String ATTR_AFFILIATE_VALID_UNTIL = "tfAffiliateValidUntil";
    
    /**
     * Attribute name of affiliate's Cache Duration.
     */
    String ATTR_AFFILIATE_CACHE_DURATION = "tfAffiliateCacheDuration";
    
    /**
     * Attribute name of Signing Key's Key Alias.
     */
    String ATTR_SIGNING_KEY_ALIAS = "tfSigningKeyAlias";
    
    /**
     * Attribute name of Encryption Key's Key Alias.
     */
    String ATTR_ENCRYPTION_KEY_ALIAS = "tfEncryptionKeyAlias";
    
    /**
     * Attribute name of Encryption Key's Key Size.
     */
    String ATTR_ENCRYPTION_KEY_SIZE = "tfEncryptionKeySize";
    
    /**
     * Attribute name of Encryption Key's Key Method.
     */
    String ATTR_ENCRYPTION_KEY_METHOD = "tfEncryptionMethod";
    
    /**
     * Attribute name of Affiliate Members.
     */
    String ATTR_AFFILIATE_MEMBERS = "arlistAffiliateMembers";
    
    
    
    /**
     * Returns provider-affiliate common attribute values.
     *
     * @param name Name of Entity Descriptor.
     * @return provider-affiliate common attribute values.
     * @throws AMConsoleException if attribute values cannot be obtained.
     */
    public Map getCommonAttributeValues(String name)
    throws AMConsoleException;
    
     /**
     * Modifies entity descriptor profile.
     *
     * @param name Name of entity descriptor.
     * @param map Map of attribute type to a Map of attribute name to values.
     * @throws AMConsoleException if profile cannot be modified.
     */
    public void modifyEntityProfile(String name, Map map)
        throws AMConsoleException;
    
    
     /**
     * Returns true if entity descriptor is an affiliate.
     *
     * @param name Name of entity descriptor.
     * @return true if entity descriptor is an affiliate.
     */
    public boolean isAffiliate(String name) throws AMConsoleException;
}


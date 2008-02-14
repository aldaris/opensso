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
 * $Id: EntityModel.java,v 1.8 2008-02-14 23:11:17 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMConsoleException;
import java.util.Set;
import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/* - NEED NOT LOG - */

public interface EntityModel 
    extends AMModel 
{
    public static final String LOCATION = "location";
    public static final String ROLE = "role";
    public static final String PROTOCOL = "protocol";
    public static final String REALM = "realm";
    public static final String WSFED = "WSFed";
    public static final String SAMLV2 = "SAMLv2";
    public static final String IDFF = "IDFF";
    public static final String HOSTED = "hosted";
    public static final String REMOTE = "remote";
    public static final String IDENTITY_PROVIDER = "IDP";
    public static final String SERVICE_PROVIDER = "SP";
    public static final String POLICY_DECISION_POINT_DESCRIPTOR = "PDP";
    public static final String POLICY_ENFORCEMENT_POINT_DESCRIPTOR = "PEP";
    public static final String GENERAL = "General";
    public static final String AFFILIATE = "Affiliate";
    public static final String SAML_ATTRAUTHORITY = "AttrAuthority";
    public static final String SAML_ATTRQUERY = "AttrQuery";
    public static final String SAML_AUTHNAUTHORITY = "AuthnAuthority";
        
   /**
     * Returns a map with all entities including data about
     * what realm, the roles, and location of each entity.
     *
     * @throws AMConsoleException if unable to retrieve the entities.
     */
    public Map getEntities() throws AMConsoleException;
    
    /**
     * Returns a map of all the samlv2 entities including data about
     * what realm, the roles, and location of each entity.
     *
     * @throws AMConsoleException if unable to retrieve the Samlv2 entities.
     */
    public Map getSAMLv2Entities() throws AMConsoleException;
    
    /**
     * Returns a map of all the idff entities including data about
     * what realm, the roles, and location of each entity.
     *
     * @throws AMConsoleException if unable to retrieve the IDFF entities.
     */
    public Map getIDFFEntities() throws AMConsoleException;
    
    /**
     * Returns a map of all the wsfed entities including data about
     * what realm, the roles, and location of each entity.
     *
     * @throws AMConsoleException if unable to retrieve the WSFED entities.
     */
    public Map getWSFedEntities() throws AMConsoleException;
    
    /**
     * Deletes the entity specified.
     *
     * @param name Name of entity descriptor.
     * @param protocol Protocol to which entity belongs.
     * @param realm the realm in which the entity resides.
     *
     * @throws AMConsoleException if unable to delete entitiy.
     */
    public void deleteEntities(String name, String protocol, String realm)
    throws AMConsoleException;
    
    /**
     * Creates an entity.
     *
     * @param data which contains the attributes of the entity to be created.
     * @throws AMConsoleException if unable to create entity.
     */
    public void createEntity(Map data) throws AMConsoleException;
    
    /*
     * Creates a list of tab entries dynamically based on the roles supported
     * for an entity.
     *
     *@param protocol the protocl which the entity belongs to.
     *@param name Name of entity descriptor.
     *@param realm the realm in which the entity resides.
     */
    public List getTabMenu(String protocol, String name, String realm);
    
    /**
     * Returns true if entity descriptor is an affiliate.
     *
     * @param realm the realm in which the entity resides.
     * @param name Name of entity descriptor.
     * @return true if entity descriptor is an affiliate.
     */
    public boolean isAffiliate(String realm, String name) 
        throws AMConsoleException;
}

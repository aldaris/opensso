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
 * $Id: SAMLv2Model.java,v 1.1 2007-10-09 01:11:12 asyhuang Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMConsoleException;
import java.util.Map;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

public interface SAMLv2Model
    extends AMModel {
    
    // XACML PDP/PEP 
    public static final String ATTR_TXT_PROTOCOL_SUPPORT_ENUM =
        "txtProtocolSupportEnum";
    public static final String ATTR_XACML_AUTHZ_SERVICE_BINDING =
        "XACMLAuthzServiceBinding";
    public static final String ATTR_XACML_AUTHZ_SERVICE_LOCATION =
        "XACMLAuthzServiceLocation";
    public static final String ATTR_WANT_ASSERTION_SIGNED =
        "wantAssertionSigned";
    
    public static final String ATTR_SIGNING_CERT_ALIAS = "signingCertAlias";
    public static final String ATTR_ENCRYPTION_CERT_ALIAS =
        "encryptionCertAlias";
    
    public static final String ATTR_BASIC_AUTH_ON = "basicAuthOn";
    public static final String ATTR_BASIC_AUTH_USER = "basicAuthUser";
    public static final String ATTR_BASIC_AUTH_PASSWORD = "basicAuthPassword";
    public static final String ATTR_WANT_XACML_AUTHZ_DECISION_QUERY_SIGNED =
        "wantXACMLAuthzDecisionQuerySigned";
    public static final String ATTR_WANT_ASSERTION_ENCRYPTED =
        "wantAssertionEncrypted";
    public static final String ATTR_COTLIST = "cotlist";
    
    /**
     * Returns a Map of PEP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PEP descriptor data.
     */
    public Map getPEPDescriptor(String realm, String entityName);
    
    /**
     * Returns a Map of PDP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PDP descriptor data.
     */
    public Map getPDPDescriptor(String realm, String entityName);
    
    /**
     * Returns a Map of PEP config data.(Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted     
     */
    public Map getPEPConfig(String realm, String entityName, String location);
    
    /**
     * Returns a Map of PDP Config data.(Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted
     */
    public Map getPDPConfig(String realm, String entityName, String location);
    
    /**
     * save data for PDP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param attrValues key-value pair Map of PDP standed data.
     * throws AMConsoleException if there is an error
     */    
    public void updatePDPDescriptor(
         String realm,
        String entityName,        
        Map attrValues
        ) throws AMConsoleException ;
    
     /**
     * save data for PDP Config data.(Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted
     * @param attrValues key-value pair Map of PDP extended config.
     */
    public void updatePDPConfig(
         String realm,
        String name,        
        String location,
        Map attrValues
        ) throws AMConsoleException, JAXBException ;
    
    /**
     * save data for PEP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param attrValues key-value pair Map of PEP descriptor data.
     * throws AMConsoleException if there is an error
     */    
    public void updatePEPDescriptor(
         String realm,
        String entityName,       
        Map attrValues
        ) throws AMConsoleException ;
    
        
     /**
     * save data for PEP Config data.(Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted
     * @param attrValues key-value pair Map of PEP extended config.
     */
    public void updatePEPConfig(
         String realm,
        String name,       
        String location,
        Map attrValues
        ) throws AMConsoleException, JAXBException ;
}

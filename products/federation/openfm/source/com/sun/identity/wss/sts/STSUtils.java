/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: STSUtils.java,v 1.8 2008-08-13 18:56:42 mrudul_uchil Exp $
 *
 */

package com.sun.identity.wss.sts;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ResourceBundle;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wss.security.WSSUtils;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.iplanet.sso.SSOToken;
import com.sun.identity.wss.sts.config.FAMSTSConfiguration;

/**
 * This class provides utility classes for STS Service and clients
 */
public class STSUtils {
    
    private static final String AGENT_TYPE_ATTR = "AgentType"; 
    private static final String WSP_ENDPOINT = "WSPEndpoint"; 
    public static Debug debug = Debug.getInstance("WebServicesSecurity");
    public static ResourceBundle bundle = ResourceBundle.getBundle("famSTS");

    /**
     * Returns the WSP agent attributes for a given end point.
     * @param endpoint the end point that is used in finding the
     *        Agent configuration. 
     * @param endPointAtrrName the attribute name for the service End Point
     *        attribute.
     * @param attrNames the attribute names set that is used to retrieve
     *        their values. If the attribute set is null, then it will 
     *        find all the attribute values.
     * @param agentType the type of the agent for e.g. ProviderConfig.WSC or
     *             ProviderConfig.WSP
     * @return Map the attribute values for a given attribute set and 
     *             with a given search pattern.
     *             If there is any exception, it returns an empty map. 
     */
    public static Map getAgentAttributes(
                 String endpoint, String endPointAtrrName, 
                 Set attrNames, String agentType) {
        try {
            SSOToken adminToken = WSSUtils.getAdminToken();
            AMIdentityRepository idRepo = 
                     new AMIdentityRepository(adminToken, "/");
            IdSearchControl control = new IdSearchControl();
            control.setAllReturnAttributes(true);
            control.setTimeOut(0);

            Map kvPairMap = new HashMap();
            Set set = new HashSet();
            set.add(agentType);
            kvPairMap.put(AGENT_TYPE_ATTR, set);

            set = new HashSet();
            set.add(endpoint);
            if ((endPointAtrrName == null) || (endPointAtrrName.length() == 0)) {
                endPointAtrrName = WSP_ENDPOINT;
            }
            kvPairMap.put(endPointAtrrName, set);

            control.setSearchModifiers(IdSearchOpModifier.OR, kvPairMap);

            IdSearchResults results = idRepo.searchIdentities(IdType.AGENTONLY,
               "*", control);
            Set agents = results.getSearchResults();
            if (!agents.isEmpty()) {
                Map attrs = (Map) results.getResultAttributes();
                AMIdentity provider = (AMIdentity) agents.iterator().next();
                Map agentConfig = null;
                if(attrNames != null) {
                   agentConfig = provider.getAttributes(attrNames);
                } else {
                   agentConfig = provider.getAttributes();
                }
                return agentConfig;
            }
            return new HashMap();
        } catch (Exception ex) {
            debug.error("STSUtils.getAgentAttributes: Exception", ex); 
            return new HashMap();
        }
    }
    
    public static Map getSTSSAMLAttributes(FAMSTSConfiguration stsConfig) {
        Map map = new HashMap();        
        Set set = null;
        
        Set attributes = stsConfig.getSAMLAttributeMapping();
        if(attributes != null) {           
           map.put("SAMLAttributeMapping", attributes);
        }
        
        String ns = stsConfig.getSAMLAttributeNamespace();
        if(ns != null) {
           set = new HashSet();
           set.add(ns);
           map.put("AttributeNamespace", set);
        }
        
        String nameIDMapper = stsConfig.getNameIDMapper();
        if(nameIDMapper != null) {
           set = new HashSet();
           set.add(nameIDMapper);
           map.put("NameIDMapper", set);
        }        
        set = new HashSet();
        set.add(Boolean.toString(stsConfig.shouldIncludeMemberships()));
        map.put("includeMemberships", set);
        return map;
    }
}

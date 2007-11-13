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
 * $Id: AgentConfiguration.java,v 1.2 2007-11-13 21:56:30 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common.configuration;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdConstants;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.AttributeSchema;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.SchemaType;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class provides agent configuration utilities.
 */
public class AgentConfiguration {

    private AgentConfiguration() {
    }

    /**
     * Returns a set of supported agent types.
     *
     * @return a set of supported agent types.
     */
    public static Set getAgentTypes()
        throws SMSException, SSOException
    {
        Set agentTypes = new HashSet();
        ServiceSchema ss = getOrganizationSchema();
        if (ss != null) {
            Set names = ss.getSubSchemaNames();
            if ((names != null) && !names.isEmpty()) {
                agentTypes.addAll(names);
            }
        }
        return agentTypes;
    }

    /**
     * Creates an agent group.
     *
     * @param ssoToken Single Sign On token that is to be used for creation.
     * @param agentGroupName Name of agent group.
     * @param agentType Type of agent group.
     * @param values Map of attribute name to its values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static void createAgentGroup(
        SSOToken ssoToken,
        String agentGroupName,
        String agentType,
        Map attrValues
    ) throws IdRepoException, SSOException, SMSException {
        createAgentGroup(ssoToken, "/", agentGroupName, agentType, attrValues);
    }

    /**
     * Creates an agent group.
     *
     * @param ssoToken Single Sign On token that is to be used for creation.
     * @param realm Name of realm where agent group is going to reside.
     * @param agentGroupName Name of agent group.
     * @param agentType Type of agent group.
     * @param values Map of attribute name to its values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    private static void createAgentGroup(
        SSOToken ssoToken,
        String realm,
        String agentGroupName,
        String agentType,
        Map attrValues
    ) throws IdRepoException, SSOException, SMSException {
        AMIdentityRepository amir = new AMIdentityRepository(
            ssoToken, realm);
        Map attributeValues = parseAttributeMap(agentType, attrValues);
        Set setAgentType = new HashSet(2);
        setAgentType.add(agentType);
        attributeValues .put(IdConstants.AGENT_TYPE, setAgentType);
        amir.createIdentity(IdType.AGENTGROUP, agentGroupName, attributeValues);
    }

    /**
     * Creates an agent.
     *
     * @param ssoToken Single Sign On token that is to be used for creation.
     * @param agentName Name of agent.
     * @param agentType Type of agent.
     * @param values Map of attribute name to its values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static void createAgent(
        SSOToken ssoToken,
        String agentName,
        String agentType,
        Map attrValues
    ) throws IdRepoException, SSOException, SMSException {
        createAgent(ssoToken, "/", agentName, agentType, attrValues);
    }

    /**
     * Creates an agent.
     *
     * @param ssoToken Single Sign On token that is to be used for creation.
     * @param realm Name of realm where agent is going to reside.
     * @param agentName Name of agent.
     * @param agentType Type of agent.
     * @param values Map of attribute name to its values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    private static void createAgent(
        SSOToken ssoToken,
        String realm,
        String agentName,
        String agentType,
        Map attrValues
    ) throws IdRepoException, SSOException, SMSException {
        AMIdentityRepository amir = new AMIdentityRepository(
            ssoToken, realm);
        Map attributeValues = parseAttributeMap(agentType, attrValues);
        Set setAgentType = new HashSet(2);
        setAgentType.add(agentType);
        attributeValues .put(IdConstants.AGENT_TYPE, setAgentType);
        amir.createIdentity(IdType.AGENTONLY, agentName, attributeValues);
    }
    
    /**
     * Updates agent attribute values.
     *
     * @param ssoToken Single Sign On token that is to be used for creation.
     * @param agentName Name of agent.
     * @param values Map of attribute name to its values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static void updateAgent(
        SSOToken ssoToken,
        String agentName,
        Map attrValues
    ) throws IdRepoException, SSOException, SMSException {
        updateAgent(ssoToken, "/", agentName, attrValues);
    }
    
    /**
     * Updates agent attribute values.
     *
     * @param ssoToken Single Sign On token that is to be used for creation.
     * @param realm Name of realm where agent resides.
     * @param agentName Name of agent.
     * @param values Map of attribute name to its values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    private static void updateAgent(
        SSOToken ssoToken,
        String realm,
        String agentName,
        Map attrValues
    ) throws IdRepoException, SSOException, SMSException {
        AMIdentity amid = new AMIdentity(ssoToken, agentName, 
            IdType.AGENTONLY, realm, null); 
        Map attributeValues = parseAttributeMap(getAgentType(amid), 
            attrValues);
        amid.setAttributes(attributeValues);
        amid.store();
    }
    
    /**
     * Updates agent group attribute values.
     *
     * @param ssoToken Single Sign On token that is to be used for creation.
     * @param agentName Name group of agent.
     * @param values Map of attribute name to its values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static void updateAgentGroup(
        SSOToken ssoToken,
        String agentGroupName,
        Map attrValues
    ) throws IdRepoException, SSOException, SMSException {
        updateAgentGroup(ssoToken, "/", agentGroupName, attrValues);
    }
    
    /**
     * Updates agent group attribute values.
     *
     * @param ssoToken Single Sign On token that is to be used for creation.
     * @param realm Name of realm where agent resides.
     * @param agentGroupName Name of agent group.
     * @param values Map of attribute name to its values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    private static void updateAgentGroup(
        SSOToken ssoToken,
        String realm,
        String agentGroupName,
        Map attrValues
    ) throws IdRepoException, SSOException, SMSException {
        AMIdentity amid = new AMIdentity(ssoToken, agentGroupName, 
            IdType.AGENTGROUP, realm, null); 
        Map attributeValues = parseAttributeMap(getAgentType(amid), 
            attrValues);
        amid.setAttributes(attributeValues);
        amid.store();
    }
    
    /**
     * Returns a set of attribute schemas of a given agent type.
     *
     * @param agentTypeName Name of agent type.
     * @return a set of attribute schemas of a given agent type.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static Set getAgentAttributeSchemas(String agentTypeName)
        throws SMSException, SSOException {
        Set attrSchemas = new HashSet();
        ServiceSchema ss = getOrganizationSchema();
        if (ss != null) {
            ServiceSchema ssType = ss.getSubSchema(agentTypeName);
            Set attrs = ssType.getAttributeSchemas();
            if ((attrs != null) && !attrs.isEmpty()) {
                attrSchemas.addAll(attrs);
            }
        }
        return attrSchemas;
    }

    /**
     * Returns agent group's attribute values.
     *
     * @param ssoToken Single Sign On token that is to be used for query.
     * @param agentGroupName Name of agent group.
     * @return agent group's attribute values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static Map getAgentGroupAttributes(
        SSOToken ssoToken,
        String agentGroupName
    ) throws IdRepoException, SMSException, SSOException {
        return getAgentGroupAttributes(ssoToken, "/", agentGroupName);
    }

    /**
     * Returns agent group's attribute values.
     *
     * @param ssoToken Single Sign On token that is to be used for query.
     * @param realm Name of realm where agent group resides.
     * @param agentGroupName Name of agent group.
     * @return agent group's attribute values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    private static Map getAgentGroupAttributes(
        SSOToken ssoToken,
        String realm,
        String agentGroupName
    ) throws IdRepoException, SMSException, SSOException {
        AMIdentity amid = new AMIdentity(ssoToken, agentGroupName,
            IdType.AGENTGROUP, realm, null);
        Map values = amid.getAttributes();
        return unparseAttributeMap(getAgentType(amid), values);
    }
    
    /**
     * Returns agent's attribute values.
     *
     * @param ssoToken Single Sign On token that is to be used for query.
     * @param agentName Name of agent.
     * @return agent's attribute values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static Map getAgentAttributes(SSOToken ssoToken, String agentName) 
        throws IdRepoException, SMSException, SSOException {
        return getAgentAttributes(ssoToken, "/", agentName);
    }
    
    /**
     * Returns agent's attribute values.
     *
     * @param ssoToken Single Sign On token that is to be used for query.
     * @param realm Name of realm where agent resides.
     * @param agentName Name of agent.
     * @return agent's attribute values.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    private static Map getAgentAttributes(
        SSOToken ssoToken,
        String realm,
        String agentName
    ) throws IdRepoException, SMSException, SSOException {
        AMIdentity amid = new AMIdentity(ssoToken, agentName,
            IdType.AGENTONLY, realm, null);
        Map values = amid.getAttributes();
        return unparseAttributeMap(getAgentType(amid), values);
    }
    
    private static String getAgentType(AMIdentity amid)
        throws IdRepoException, SSOException {
        Set setType = amid.getAttribute(IdConstants.AGENT_TYPE);
        return (String)setType.iterator().next();
    }
    
    private static Map parseAttributeMap(String agentType, Map attrValues)
        throws SMSException, SSOException {
        Map result = new HashMap();
        Set attributeSchemas = getAgentAttributeSchemas(agentType);
        
        if ((attributeSchemas != null) && !attributeSchemas.isEmpty()) {
            for (Iterator i = attributeSchemas.iterator(); i.hasNext(); ) {
                AttributeSchema as = (AttributeSchema)i.next();
                Set values = parseAttributeMap(as, attrValues);
                if (values != null) {
                    result.put(as.getName(), values);
                }
            }
        } else {
            result.putAll(attrValues);
        }
        
        return result;
    }

    private static Set parseAttributeMap(AttributeSchema as, Map attrValues) {
        Set results = null;
        String attrName = as.getName();

        if (as.getType().equals(AttributeSchema.Type.LIST)) {
            results = new HashSet();
            int lenAttrName = attrName.length();
            
            for (Iterator i = attrValues.keySet().iterator(); i.hasNext(); ) {
                String key = (String)i.next();
                if (key.startsWith(attrName)) {
                    String sKey = key.substring(lenAttrName);
                    Set set = (Set)attrValues.get(key);
                    
                    if (sKey.startsWith("[") && sKey.endsWith("]")) {
                        results.add(sKey + "=" + set.iterator().next());
                    } else if (attrName.equals(key)) {
                        // this is for special case, where attribute can be
                        // list and non list type
                        results.add(set.iterator().next());
                    }
                }
            }
            if (results.isEmpty()) {
                results = null;
            }
        } else {
            results = (Set)attrValues.get(attrName);
        }
        return results;
    }
    
    private static Map unparseAttributeMap(String agentType, Map attrValues)
        throws SMSException, SSOException {
        Map result = new HashMap();
        Set asListType = getAttributesSchemaNames(agentType,
            AttributeSchema.Type.LIST);
        Set asValidatorType = getAttributesSchemaNames(agentType,
            AttributeSchema.Type.VALIDATOR);
        
        if ((asListType != null) && !asListType.isEmpty()) {
            for (Iterator i = attrValues.keySet().iterator(); i.hasNext(); ) {
                String name = (String)i.next();
                Set values = (Set)attrValues.get(name);
                if (!asValidatorType.contains(name)) {
                    if (asListType.contains(name)) {
                        for (Iterator j = values.iterator(); j.hasNext(); ) {
                            String val = (String)j.next();
                            int idx = val.indexOf("]=");
                            
                            if (idx != -1) {
                                Set set = new HashSet(2);
                                set.add(val.substring(idx+2));
                                String indice = val.substring(0, idx+1);
                                indice = indice.replaceAll("=", "\\\\=");
                                result.put(name + indice, set);
                            } else {
                                Set set = new HashSet(2);
                                set.add(val);
                                // this is for special case, where attribute can
                                // be list and non list type
                                result.put(name, set);
                            }
                        }
                    } else {
                        result.put(name, values);
                    }
                }
            }
        } else {
            result.putAll(attrValues);
        }
        
        return result;
    }

    /**
     * Returns a set of attribute schema names whose schema match a given 
     * syntax.
     *
     * @param amid Identity Object. Agent Type is to be gotten from it.
     * @param syntax Syntax.
     * @return a set of attribute schema names whose schema match a given 
     * syntax.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static Set getAttributesSchemaNames(
        AMIdentity amid, 
        AttributeSchema.Syntax syntax
    ) throws SMSException, SSOException, IdRepoException {
        Set results = new HashSet();
        Set attributeSchemas = getAgentAttributeSchemas(getAgentType(amid));
        
        if ((attributeSchemas != null) && !attributeSchemas.isEmpty()) {
            for (Iterator i = attributeSchemas.iterator(); i.hasNext(); ) {
                AttributeSchema as = (AttributeSchema)i.next();
                if (as.getSyntax().equals(syntax)){
                    results.add(as.getName());
                }
            }
        }
        return results;
    }
    
    /**
     * Returns a set of attribute schema names whose schema match a given 
     * type.
     *
     * @param amid Identity Object. Agent Type is to be gotten from it.
     * @param type Type.
     * @return a set of attribute schema names whose schema match a given 
     * type.
     * @throws IdRepoException if there are Id Repository related errors.
     * @throws SSOException if the Single Sign On token is invalid or has
     *         expired.
     * @throws SMSException if there are errors in service management layers.
     */
    public static Set getAttributesSchemaNames(
        String agentType, 
        AttributeSchema.Type type
    ) throws SMSException, SSOException {
        Set results = new HashSet();
        Set attributeSchemas = getAgentAttributeSchemas(agentType);
        
        if ((attributeSchemas != null) && !attributeSchemas.isEmpty()) {
            for (Iterator i = attributeSchemas.iterator(); i.hasNext(); ) {
                AttributeSchema as = (AttributeSchema)i.next();
                if (as.getType().equals(type)){
                    results.add(as.getName());
                }
            }
        }
        return results;
    }

    private static ServiceSchema getOrganizationSchema()
        throws SMSException, SSOException {
        ServiceSchema ss = null;
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        ServiceSchemaManager ssm = new ServiceSchemaManager(
            IdConstants.AGENT_SERVICE, adminToken);
        if (ssm != null) {
            ss = ssm.getSchema(SchemaType.ORGANIZATION);
        }
        return ss;
    }
}

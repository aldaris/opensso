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
 * $Id: UserSelfCheckCondition.java,v 1.1 2006-04-26 05:14:50 dillidorai Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.policy.plugins;

import java.util.*;

import com.sun.identity.policy.interfaces.Condition;
import com.sun.identity.policy.ConditionDecision;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.Syntax;
import com.sun.identity.policy.PolicyEvaluator;
import com.sun.identity.policy.PolicyManager;
import com.sun.identity.policy.ResBundleUtils;
import com.sun.identity.common.CaseInsensitiveHashSet;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.idm.IdRepoException;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.am.util.Debug;

/**
 * The class <code>UserSelfCheckCondition</code> checks
 * if User is accessing his object in the datastore.
 * Additionally it checks if the attributes being accessed
 * allowed by the configuration.
 */

public class UserSelfCheckCondition implements Condition {

    // Attributes names that are allowed by the condition
    public static final String ATTRIBUTES = "attributes";

    //  Constants for constructing resource names
    static final String RESOURCE_PREFIX = "sms://";
    static final String RESOURCE_NAME =
        "/sunIdentityRepositoryService/1.0/application/";

    // Instance variables
    private Set attributes;
    private boolean allowAllAttributes;
    private Map properties;

    // Configuration property names
    private static List propertyNames;

    // Debug file
    Debug debug = Debug.getInstance(PolicyManager.POLICY_DEBUG_NAME);

    /** No argument constructor 
     */
    public UserSelfCheckCondition() {
      attributes = Collections.EMPTY_SET;
    }

    /**
     * Returns a set of property names for the condition.
     *
     * @return set of property names
     */
    public List getPropertyNames() {
        if (propertyNames == null) {
            List answer = new LinkedList();
            answer.add(ATTRIBUTES);
            propertyNames = Collections.unmodifiableList(answer);
        }
        return (propertyNames);
     }
 
     /**
      * Returns the syntax for a property name
      * @see com.sun.identity.policy.Syntax
      *
      * @param property <code>String</code> representing property name
      *
      * @return <code>Syntax<code> for the property name
      */
     public Syntax getPropertySyntax(String property)
     {
         return (Syntax.ANY);
     }
      
    /**
     * Gets the display name for the property name.
     * The <code>locale</code> variable could be used by the plugin to
     * customize the display name for the given locale.
     * The <code>locale</code> variable could be <code>null</code>, in which
     * case the plugin must use the default locale.
     *
     * @param property property name.
     * @param locale locale for which the property name must be customized.
     * @return display name for the property name.
     * @throws PolicyException if unable to get display name
     */
     public String getDisplayName(String property, Locale locale) 
       throws PolicyException
     {
         return property;
     }
 
     /**
      * Returns a set of valid values given the property name. This method
      * is called if the property Syntax is either the SINGLE_CHOICE or 
      * MULTIPLE_CHOICE.
      *
      * @param property <code>String</code> representing property name
      * @return Set of valid values for the property.
      * @exception PolicyException if unable to get the Syntax.
      */
     public Set getValidValues(String property) throws PolicyException
     {
         return (Collections.EMPTY_SET);
     }


    /** 
     *  Sets the properties of the condition.
     *  Evaluation of ConditionDecision is influenced by these properties.
     *  @param properties of the condition that governs
     *         whether a policy applies. The only defined property
     *         is <code>attributes</code>
     */
    public void setProperties(Map properties) throws PolicyException {
        if ( (properties == null) || ( properties.keySet() == null) ) {
            throw new PolicyException(ResBundleUtils.rbName,
                "properties_can_not_be_null_or_empty", null, null);
        }
        this.properties = Collections.unmodifiableMap(properties);

        //Check if attributes is set
        Object set = properties.get(ATTRIBUTES);
        if (set != null && set instanceof Set) {
            attributes = new CaseInsensitiveHashSet();
            attributes.addAll((Set) set);
        } else {
            String[] args = { ATTRIBUTES };
            throw new PolicyException(ResBundleUtils.rbName,
                "required_properties_can_not_be_null_or_empty", args, null);
        }

        // Check if all attributes are allowed
        if (attributes.contains("*")) {
            allowAllAttributes = true;
        } else {
            allowAllAttributes = false;
        }
        if (debug.messageEnabled()) {
            debug.message("UserSelfCheckCondition.setProperties():"
                    + "attributes = " + properties.get(ATTRIBUTES));
        }
    }


    /** Gets the properties of the condition.  
     *  @return  map view of properties that govern the 
     *           evaluation of  the condition decision
     *  @see #setProperties(Map)
     */
    public Map getProperties() {
        return properties;
    } 


    /**
     * Gets the decision computed by this condition object.
     *
     * @param token single sign on token of the user
     *
     * @param env request specific environment map of key/value pairs.
     *
     * @return the condition decision. The condition decision 
     *         encapsulates whether a policy applies for the request. 
     *
     * Policy framework continues evaluating a policy only if it 
     * applies to the request as indicated by the CondtionDecision. 
     * Otherwise, further evaluation of the policy is skipped. 
     *
     * @throws SSOException if the token is invalid
     */
    public ConditionDecision getConditionDecision(SSOToken token, Map env) 
            throws PolicyException, SSOException {
        boolean allowed = false;
        if (debug.messageEnabled()) {
            debug.message("UserSelfCheckCondition.getConditionDecision: " +
                "called with Token: " + token.getPrincipal().getName() +
                ", requestedResourcename: "
                + env.get(PolicyEvaluator.SUN_AM_REQUESTED_RESOURCE));
        }

        // Check if attributes in envMap are a subset of "attributes"
        boolean attributeCheckOk = allowAllAttributes;
        if (!attributeCheckOk) {
            Object o = env.get(ATTRIBUTES);
            if (o != null && o instanceof Set) {
                Set s = (Set) o;
                if (!s.isEmpty()) {
                    Set set = new CaseInsensitiveHashSet();
                    set.addAll((Set) o);
                    if (debug.messageEnabled()) {
                        debug.message("UserSelfCheckCondition." +
                             "getConditionDecision: Is attributes " +
                             set + " subset of config attrs: " + attributes);
                    }
                    if (attributes.containsAll(set)) {
                        attributeCheckOk = true;
                    }
                }
            } else if (debug.warningEnabled()) {
                debug.warning("UserSelfCheckCondition.getConditionDecision " +
                    "Invalid attribute set in env params");
            }
        }
        if (debug.messageEnabled()) {
            debug.message("UserSelfCheckCondition.getConditionDecision: " +
                "attributes check:" + attributeCheckOk);
        }
        if (attributeCheckOk) {
            // Construct the users' resource string
            StringBuffer name = new StringBuffer(100);
            name.append(RESOURCE_PREFIX);
            try {
                AMIdentity id = IdUtils.getIdentity(token);
                name.append(id.getRealm());
                name.append(RESOURCE_NAME);
                name.append(id.getType().getName()).append("/");
                name.append(id.getName());
            } catch (SSOException ssoe) {
                // Debug it
                if (debug.messageEnabled()) {
                    debug.message("UserSelfCheckCondition."
                        +"getConditionDecision: invalid sso token: " 
                        + ssoe.getMessage());
                }
            } catch (IdRepoException ide) {
                // Debug it
                if (debug.messageEnabled()) {
                    debug.message("UserSelfCheckCondition."
                        +"getConditionDecision IdRepo exception: ", ide);
                }
            }


            // Get the resource name from the env
            Object o = env.get(PolicyEvaluator.SUN_AM_REQUESTED_RESOURCE);
            if (debug.messageEnabled()) {
                debug.message("UserSelfCheckCondition.getConditionDecision:"
                    +" name: " + name + " resource: " + o);
            }
            if (o != null) {
                String resource = null;
                if (o instanceof String ) {
                    resource = (String) o;
                } else if (o instanceof Set) {
                    resource = (String) ((Set) o).iterator().next();
                } else if (debug.warningEnabled()) {
                    resource = "";
                    debug.warning("UserSelfCheckCondition."
                        +"getConditionDecision: Unable to get resource name");
                }

                // compare the resource and the name
                if (resource.equalsIgnoreCase(name.toString())) {
                    allowed = true;
                    if (debug.messageEnabled()) {
                        debug.message("UserSelfCheckCondition."
                            +"getConditionDecision: " + "returning true");
                    }
                } else if (debug.messageEnabled()) {
                    debug.message("UserSelfCheckCondition."
                        +"getConditionDecision:Resource names donot match: " 
                        + resource + " " + name);
                }
            }
        }
        return new ConditionDecision(allowed);
    }


    /**
     * Returns a copy of this object.
     *
     * @return a copy of this object
     */
    public Object clone() {
        UserSelfCheckCondition theClone = null;
        try {
            theClone = (UserSelfCheckCondition) super.clone();
            theClone.properties = Collections.unmodifiableMap(
                com.sun.identity.sm.SMSUtils.copyAttributes(properties));
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        return theClone;
    }

}

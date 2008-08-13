/**
* Copyright © 2008 Sun Microsystems, Inc. All rights reserved.  
* Sun Microsystems, Inc. has intellectual property rights relating to 
* technology embodied in the product that is described in this document. 
* In particular, and without limitation, these intellectual property 
* rights may include one or more of the U.S. patents listed at 
* http://www.sun.com/patents and one or more additional patents or pending 
* patent applications in the U.S. and in other countries.
* U.S. Government Rights - Commercial software. Government users are subject 
* to the Sun Microsystems, Inc. standard license agreement and applicable 
* provisions of the FAR and its supplements.
* Use is subject to license terms.
* This distribution may include materials developed by third parties.Sun, Sun 
* Microsystems, the Sun logo, Java and Solaris are trademarks or registered 
* trademarks of Sun Microsystems, Inc. in the U.S. and other countries. All 
* SPARC trademarks are used under license and are trademarks or registered 
* trademarks of SPARC International, Inc. in the U.S. and other countries.
* UNIX is a registered trademark in the U.S. and other countries, exclusively 
* licensed through X/Open Company, Ltd.
* 
* Copyright © 2008 Sun Microsystems, Inc. Tous droits réservés.
* Sun Microsystems, Inc. détient les droits de propriété intellectuels 
* relatifs à la technologie incorporée dans le produit qui est décrit 
* dans ce document. En particulier, et ce sans limitation, ces droits de 
* propriété intellectuelle peuvent inclure un ou plus des brevets américains 
* listés à l'adresse http://www.sun.com/patents et un ou les brevets 
* supplémentaires ou les applications de brevet en attente aux Etats - Unis 
* et dans les autres pays.
* L'utilisation est soumise aux termes du contrat de licence.
* Cette distribution peut comprendre des composants développés par des tierces 
* parties.
* Sun, Sun Microsystems, le logo Sun, Java et Solaris sont des marques de 
* fabrique ou des marques déposées de Sun Microsystems, Inc. aux 
* Etats-Unis et dans d'autres pays. Toutes les marques SPARC sont utilisées 
* sous licence et sont des marques de fabrique ou des marques déposées de 
* SPARC International, Inc. aux Etats-Unis et dans d'autres pays.
* UNIX est une marque déposée aux Etats-Unis et dans d'autres pays et 
* licenciée exlusivement par X/Open Company, Ltd.
*
* $Id: SampleCondition.java,v 1.1 2008-08-13 21:40:49 dillidorai Exp $
*/

package com.sun.identity.samples.policy;

import java.util.*;

import com.sun.identity.policy.interfaces.Condition;
import com.sun.identity.policy.ConditionDecision;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.PolicyManager;
import com.sun.identity.policy.Syntax;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;


/**
 * The class <code>SampleCondition</code> is a plugin 
 * implementation of <code>Condition</code> interface.
 * This condition object provides the policy framework with the 
 * condition decision based on the length of the user's name.
 */

public class SampleCondition implements Condition {

    /** Key that is used to define the minimum of the user name length  
     *  for which the policy would apply.  The value should be
     *  a Set with only one element. The element should be a 
     *  String, parsable as an integer.
     */

    public static final String USER_NAME_LENGTH = "userNameLength";

    private List propertyNames;
    private Map properties;
    private int nameLength;

    /** No argument constructor 
     */
    public SampleCondition() {
         propertyNames = new ArrayList();
         propertyNames.add(USER_NAME_LENGTH);
    }

     /**
      * Returns a set of property names for the condition.
      *
      * @return set of property names
      */

     public List getPropertyNames()
     {
         return propertyNames;
     }
 
     /**
      * Returns the syntax for a property name
      * @see com.sun.identity.policy.Syntax
      *
      * @param String property name
      *
      * @return <code>Syntax<code> for the property name
      */
     public Syntax getPropertySyntax(String property)
     {
         return (Syntax.ANY);
     }
      
     /**
      * Gets the display name for the property name.
      * The <code>locale</code> variable could be used by the
      * plugin to customize the display name for the given locale.
      * The <code>locale</code> variable could be <code>null</code>, in which 
      * case the plugin must use the default locale.
      *
      * @param String property name
      * @param Locale locale for which the property name must be customized
      * @return display name for the property name
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
      * @param String property name
      * @return Set of valid values for the property.
      * @exception PolicyException if unable to get the Syntax.
      */
     public Set getValidValues(String property) throws PolicyException
     {
         return (Collections.EMPTY_SET);
     }


    /** Sets the properties of the condition.
     *  Evaluation of ConditionDecision is influenced by these properties.
     *  @param properties the properties of the condition that governs
     *         whether a policy applies. The properties should
     *         define value for the key USER_NAME_LENGTH. The value should
     *         be a Set with only one element. The element should be
     *         a String, parsable as an integer. Please note that
     *         properties is not cloned by the method.
     *
     *  @throws PolicyException if properties is null or does not contain
     *          value for the key USER_NAME_LENGTH or the value of the key is
     *          not a Set with one String element that is parsable as
     *          an integer.
     */

    public void setProperties(Map properties) throws PolicyException {
        this.properties = (Map)((HashMap) properties);
        if ( (properties == null) || ( properties.keySet() == null) ) {
            throw new PolicyException("properties can not be null or empty");
        }

        //Check if the key is valid
        Set keySet = properties.keySet();
        Iterator keys = keySet.iterator();
        String key = (String) keys.next();
        if ( !USER_NAME_LENGTH.equals(key) ) {
            throw new PolicyException(
                "property " + USER_NAME_LENGTH + " is not defined");
        }

        // check if the value is valid
        Set nameLengthSet = (Set) properties.get(USER_NAME_LENGTH);
        if (( nameLengthSet == null ) || nameLengthSet.isEmpty() 
            || ( nameLengthSet.size() > 1 )) {
            throw new PolicyException(
                "property value is not defined or invalid");
        }

        Iterator nameLengths = nameLengthSet.iterator();
        String nameLengthString = null;
        nameLengthString = (String) nameLengths.next();
        try {
            nameLength = Integer.parseInt(nameLengthString);
        } catch (Exception e) {
            throw new PolicyException("name length value is not an integer");
        }
    }


    /** Get properties of this condition.
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
     *        SampleCondition doesn't use this parameter.
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

        String userDN = token.getPrincipal().getName();
        // user DN is in the format like "uid=username,ou=people,dc=example,dc=com"
        int beginIndex = userDN.indexOf("=");
        int endIndex = userDN.indexOf(",");
        if (beginIndex >= endIndex) {
            throw (new PolicyException("invalid user DN"));
        }

        String userName = userDN.substring(beginIndex+1, endIndex);
        if (userName.length() >= nameLength) {
            allowed = true;
        }

	return new ConditionDecision(allowed);
    }


    public Object clone() {
	Object theClone = null;
	try {
	    theClone = super.clone();
	} catch (CloneNotSupportedException e) {
            throw new InternalError();
	}
	return theClone;
    }

}

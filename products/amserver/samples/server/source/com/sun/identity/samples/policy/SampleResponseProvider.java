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
* $Id: SampleResponseProvider.java,v 1.1 2008-08-13 21:40:49 dillidorai Exp $
*/

package com.sun.identity.samples.policy;

import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.PolicyUtils;
import com.sun.identity.policy.PolicyConfig;
import com.sun.identity.policy.PolicyManager;
import com.sun.identity.policy.interfaces.ResponseProvider;
import com.sun.identity.policy.Syntax;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;

import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.idm.IdRepoException;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Collections;

/**
 * This class is an implementation of <code>ResponseProvider</code> interface. 
 * It takes as input the attribute for which values are to be fetched from
 * the access manager and sent back in the Policy Decision.
 * if the attribute does not exist in the use profile no value is sent
 * back in the response.
 * It relies on underlying Identity repository service to 
 * fetch the attribute values for the Subject(s) defined in the policy.
 * It computes a <code>Map</code> of response attributes
 * based on the <code>SSOToken</code>, resource name and  env map passed 
 * in the method call <code>getResponseDecision()</code>.
 *
 * Policy framework would make a call to the ResponseProvider in a 
 * policy only if the policy is applicable to a request as determined by 
 * <code>SSOToken</code>, resource name, <code>Subjects</code> and <code>Conditions
 * </code>.
 *
 */
public class SampleResponseProvider implements ResponseProvider {

    public static final String ATTRIBUTE_NAME = "AttributeName";

    private Map properties;
    private static List propertyNames = new ArrayList(1);

    private boolean initialized=false;
    private String orgName = null;

    static {
        propertyNames.add(ATTRIBUTE_NAME);
    }

    /**
     * No argument constructor.
     */
    public SampleResponseProvider () {

    }


    /** 
     * Initialize the SampleResponseProvider object by using the configuration
     * information passed by the Policy Framework.
     * @param configParams the configuration information
     * @exception PolicyException if an error occured during 
     * initialization of the instance
     */

    public void initialize(Map configParams) throws PolicyException {
        // get the organization name
        Set orgNameSet = (Set) configParams.get(
                                     PolicyManager.ORGANIZATION_NAME);
        if ((orgNameSet != null) && (orgNameSet.size() != 0)) {
            Iterator items = orgNameSet.iterator();
            orgName = (String) items.next();
        }
	/** 
         * Organization name is not used in this sample, but this is code
    	 * to illustrate how any other custom response provider can get data
         * out from the policy configuration service and use it in 
         * getResponseDecision() as necessary.
	 */
	initialized = true;
    }


    /**
     * Returns a list of property names for the responseprovider.
     *
     * @return <code>List</code> of property names
     */
    public List getPropertyNames()  {
         return propertyNames;
    }

    /**
     * Returns the syntax for a property name
     * @see com.sun.identity.policy.Syntax
     *
     * @param property property name
     *
     * @return <code>Syntax<code> for the property name
     */
    public Syntax getPropertySyntax(String property) {
        return (Syntax.LIST);
    }
	
    /**
     * Gets the display name for the property name.
     * The <code>locale</code> variable could be used by the plugin to
     * customize the display name for the given locale.
     * The <code>locale</code> variable could be <code>null</code>, in which
     * case the plugin must use the default locale.
     *
     * @param property property name
     * @param locale locale for which the property name must be customized
     * @return display name for the property name.
     * @throws PolicyException
     */
    public String getDisplayName(String property, Locale locale)
            throws PolicyException {
	return property;
    }

    /**
     * Returns a set of valid values given the property name. 
     *
     * @param property property name
     * from the PolicyConfig Service configured for the specified realm.
     * @return Set of valid values for the property.
     * @exception PolicyException if unable to get the Syntax.
     */
    public Set getValidValues(String property) throws PolicyException {
	if (!initialized) {
	    throw (new PolicyException("idrepo response provider not yet "
		+"initialized"));
	}
        return Collections.EMPTY_SET;
    }

    /** Sets the properties of the responseProvider plugin.
     *  This influences the response attribute-value Map that would be
     *  computed by a call to method <code>getResponseDecision(Map)</code>
     *  These attribute-value pairs are encapsulated in 
     *  <code>ResponseAttribute</code> element tag which is a child of the 
     *  <code>PolicyDecision</code> element in the PolicyResponse xml
     *  if the policy is applicable to the user for the resource, subject and
     *  conditions defined.
     *  @param properties the properties of the responseProvider
     *         Keys of the properties have to be String.
     *         Value corresponding to each key have to be a Set of String
     *         elements. Each implementation of ResponseProvider could add 
     *         further restrictions on the keys and values of this map.
     *  @throws PolicyException for any abnormal condition
     */
    public void setProperties(Map properties) throws PolicyException {
        if ( (properties == null) || ( properties.isEmpty()) ) {
            throw new PolicyException("Properties cannot be null or empty");
        }
        this.properties = properties;

        //Check if the keys needed for this provider are present namely
 	// ATTRIBUTE_NAME
	if (!properties.containsKey(ATTRIBUTE_NAME)) {
            throw new PolicyException("Missing required property");
	}
	/** 
         * Addtional validation on property name and values can be done
         * as per the individual use case
         */
    }

    /** Gets the properties of the responseprovider
     *  @return properties of the responseprovider
     *  @see #setProperties
     */
    public Map getProperties() {
	return (properties == null) 
		? null : Collections.unmodifiableMap(properties);
    }

    /**
     * Gets the response attributes computed by this ResponseProvider object,
     * based on the sso token and map of environment parameters
     *
     * @param token single-sign-on token of the user
     *
     * @param env specific environment map of key/value pairs
     * @return  a Map of response attributes.
     *          Keys of the Map are attribute names ATTRIBUTE_NAME or
     *          Value is a Set of Strings representing response attribute 
     *          values.
     *
     * @throws PolicyException if the decision could not be computed
     * @throws SSOException if SSO token is not valid
     *
     */
    public Map getResponseDecision(SSOToken token, 
            Map env) throws PolicyException, SSOException { 

	Map respMap = new HashMap();
	Set attrs = (Set)properties.get(ATTRIBUTE_NAME);
	Set values = null;
	if ((attrs != null) && !(attrs.isEmpty())) {
            try {
                if (token.getPrincipal() != null) {
                    AMIdentity id = IdUtils.getIdentity(token);
                    Map idRepoMap = id.getAttributes(attrs);
                    if (idRepoMap != null) {
                        for (Iterator iter = attrs.iterator(); iter.hasNext(); )
			{
                            String attrName = (String)iter.next();
                            values = new HashSet();
                            Set subValues = (Set)idRepoMap.get(attrName);
                            if (subValues != null) {
                                values.addAll(subValues);
                            }
			    respMap.put(attrName, values);
                        }
                    }
                } else {
                    throw (new PolicyException("SSOToken principal is null"));
                }
            } catch (IdRepoException ide) {
                throw new PolicyException(ide);
  	    }	
        }
	return respMap;
    }


    /**
     * Returns a copy of this object.
     *
     * @return a copy of this object
     */
    public Object clone() {
        SampleResponseProvider theClone = null;
        try {
            theClone = (SampleResponseProvider)super.clone();
        } catch (CloneNotSupportedException e) {
            // this should never happen
            throw new InternalError();
        }

        if (properties != null) {
            theClone.properties = new HashMap();
            Iterator iter = properties.keySet().iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                Set values = new HashSet();
                values.addAll((Set) properties.get(obj));
                theClone.properties.put(obj, values);
            }
        }
        return theClone;
    }
}

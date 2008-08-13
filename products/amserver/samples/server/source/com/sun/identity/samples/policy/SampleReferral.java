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
* $Id: SampleReferral.java,v 1.1 2008-08-13 21:40:49 dillidorai Exp $
*/

package com.sun.identity.samples.policy;

import java.io.*;
import java.util.*;

import com.sun.identity.policy.*;
import com.sun.identity.policy.interfaces.Referral;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.iplanet.am.util.SystemProperties;

public class SampleReferral implements Referral {

    static final String SEPARATOR = ":";
    static String PROPERTIES = "samples/policy/SampleReferral.properties";
    static String INSTALL_DIR = SystemProperties.get("com.iplanet.am.installdir");
    static Properties properties = new Properties();
    private String _name;
    private Set _values;

    /** No argument constructor */
    public SampleReferral() {
    }

    /**Initializes the referral with a map of Configuration parameters
     * @param configurationMap a map containing configuration 
     *        information. Each key of the map is a configuration
     *        parameter. Each value of the key would be a set of values
     *        for the parameter. The map is cloned and a reference to the 
     *        clone is stored in the referral
     */
    public void initialize(Map configurationMap) {
    }

    /**Sets the name of this referral 
     * @param name name of this referral
     */
    private void setName(String name) {
        _name = name;
    }

    /**Gets the name of this referral 
     * @return the name of this referral
     */
    private String getName() {
        return _name;
    }            

    /**Sets the values of this referral.
     * @param values a set of values for this referral
     *        Each element of the set has to be a String
     * @throws InvalidNameException if any value passed in the 
     * values is invalid
     */
    public void setValues(Set values) throws InvalidNameException {
        _values = values;
    }

    /**Gets the values of this referral 
     * @return the values of this referral
     *                Each element of the set would be a String
     */
    public Set getValues() {
        return _values;
    }

    /**
     * Returns the display name for the value for the given locale.
     * For all the valid values obtained through the methods
     * <code>getValidValues</code> this method must be called
     * by GUI and CLI to get the corresponding display name.
     * The <code>locale</code> variable could be used by the
     * plugin to customize
     * the display name for the given locale.
     * The <code>locale</code> variable
     * could be <code>null</code>, in which case the plugin must
     * use the default locale (most probabily en_US).
     * This method returns only the display name and should not
     * be used for the method <code>setValues</code>.
     * Alternatively, if the plugin does not have to localize
     * the value, it can just return the <code>value</code> as is.
     *
     * @param value one of the valid value for the plugin
     * @param locale locale for which the display name must be customized
     *
     * @exception NameNotFoundException if the given <code>value</code>
     * is not one of the valid values for the plugin
     */
    public String getDisplayNameForValue(String value, Locale locale)
	throws NameNotFoundException {
	return value;
    }

    /**Gets the valid values for this referral 
     * @param token SSOToken
     * @return <code>ValidValues</code> object
     * @throws SSOException, PolicyException
     */
    public ValidValues getValidValues(SSOToken token) 
            throws SSOException, PolicyException {
        return getValidValues(token, "*");
    }

    /**Gets the valid values for this referral 
     * matching a pattern
     * @param token SSOToken
     * @param pattern a pattern to match against the value
     * @return </code>ValidValues</code> object
     * @throws SSOException, PolicyException
     */
    public ValidValues getValidValues(SSOToken token, String pattern)
            throws SSOException, PolicyException {
        Set values = new HashSet();
        values.add(PROPERTIES);
        return (new ValidValues(ValidValues.SUCCESS,
                                values));
    }

    /**Gets the syntax for the value 
     * @param token SSOToken
     * @see com.sun.identity.policy.Syntax
     */
    public Syntax getValueSyntax(SSOToken token)
            throws SSOException, PolicyException {
        return (Syntax.SINGLE_CHOICE);
    }

    /**Gets the name of the ReferralType 
     * @return name of the ReferralType representing this referral
     */
    public String getReferralTypeName() 
    {
        return "SampleReferral";
    }

    /**Gets policy results 
     * @param token SSOToken
     * @param resourceType resource type
     * @param resourceName name of the resource 
     * @param actionNames a set of action names
     * @param envParameters a map of enivronment parameters.
     *        Each key is an environment parameter name.
     *        Each value is a set of values for the parameter.
     * @return policy decision
     * @throws SSOException
         * @throws PolicyException
     */
    public PolicyDecision getPolicyDecision(SSOToken token, String resourceType, 
	    String resourceName, Set actionNames, Map envParameters) 
            throws SSOException, PolicyException {

        PolicyDecision pd = new PolicyDecision();
        Iterator elements = _values.iterator();
        if (!elements.hasNext()) {
            return pd;
        }

        String fileName = (String)elements.next(); 
        fileName = INSTALL_DIR + "/" + fileName;
        try {
            InputStream is = new FileInputStream(fileName);
            if (is == null) {
                return pd;
            }
            properties.load(is);
        } catch (Exception e) {
            return pd;
        }

        String serviceName = getProperty("servicename");
        if (!serviceName.equals(resourceType)) {
            return pd;
        }

        String resName = getProperty("resourcename");
        if (!resName.equals(resourceName)) {
            return pd;
        }
       
        List actionNameList = getPropertyValues("actionnames");
        List actionValueList = getPropertyValues("actionvalues");

        int numOfActions = actionNameList.size();
        int numOfValues = actionValueList.size();

        if ((numOfActions == 0 || (numOfValues == 0) 
                               || numOfActions != numOfValues)) {
            return pd;
        } 

        Iterator namesIter = actionNameList.iterator();
        Iterator valuesIter = actionValueList.iterator();

        for (int i = 0; i < numOfActions; i++) {
            String actionName = (String)namesIter.next();
            String actionValue = (String)valuesIter.next();
            if (actionNames.contains(actionName)) {
                Set values = new HashSet();
                values.add(actionValue);
                ActionDecision ad = new ActionDecision(
                    actionName, values, null, Long.MAX_VALUE);
                pd.addActionDecision(ad);            
            }
        }
        return pd;
    }


    private String getProperty(String key)
    {
        return properties.getProperty(key);
    }


    private List getPropertyValues(String name) {
        List values = new ArrayList();
        String value = getProperty(name);
        if ( value != null ) {
            StringTokenizer st = new StringTokenizer(value, SEPARATOR);
            while ( st.hasMoreTokens() ) {
               values.add(st.nextToken());
            }
        }
        return values;
    }

    /** Gets resource names rooted at the given resource name for the given
     *  serviceType that could be governed by this referral 
     * @param token ssoToken sso token
     * @param serviceTypeName service type name
     * @param rsourceName resource name
     * @return names of sub resources for the given resourceName.
     *         The return value also includes the resourceName.
     *
     * @throws PolicyException
     * @throws SSOException
     */
    public Set getResourceNames(SSOToken token, String serviceTypeName, 
            String resourceName) throws PolicyException, SSOException {
        return null;
    }

}

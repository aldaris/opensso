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
 * $Id: DefaultLibrarySPAccountMapper.java,v 1.1 2007-08-01 21:04:05 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.wsfederation.plugins;

import java.security.PrivateKey;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;

import com.sun.identity.plugin.datastore.DataStoreProviderException;
import com.sun.identity.saml.assertion.Assertion;
import com.sun.identity.saml.assertion.Attribute;
import com.sun.identity.saml.assertion.AttributeStatement;
import com.sun.identity.saml.assertion.NameIdentifier;
import com.sun.identity.saml.assertion.Statement;
import com.sun.identity.saml.assertion.Subject;
import com.sun.identity.saml.assertion.SubjectStatement;
import com.sun.identity.saml.common.SAMLException;
import com.sun.identity.saml2.common.SAML2Constants;

import com.sun.identity.wsfederation.common.WSFederationException;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.profile.RequestSecurityTokenResponse;
import com.sun.identity.wsfederation.profile.SAML11RequestedSecurityToken;

/**
 * This class <code>DefaultLibrarySPAccountMapper</code> is the default 
 * implementation of the <code>SPAccountMapper</code> that is used
 * to map the <code>SAML</code> protocol objects to the user accounts.
 * at the <code>ServiceProvider</code> side of WSFederation plugin.
 * Custom implementations may extend from this class to override some
 * of these implementations if they choose to do so.
 */
public class DefaultLibrarySPAccountMapper extends DefaultAccountMapper 
       implements SPAccountMapper {

    private PrivateKey decryptionKey = null;

     /**
      * Default constructor
      */
     public DefaultLibrarySPAccountMapper() {
         debug.message("DefaultLibrarySPAccountMapper.constructor: ");
         role = SP;
     }

    /**
     * Returns the user's disntinguished name or the universal ID for the 
     * corresponding  <code>SAML</code> <code>Assertion</code>. This method
     * will be invoked by the <code>SAML</code> framework while processing
     * the <code>Assertion</code> and retrieves the identity information. 
     * The implementation of this method checks for
     * the user for the corresponding name identifier in the assertion.
     * If not found, then it will check if this is an auto federation case. 
     *
     * @param assertion <code>SAML</code> <code>Assertion</code> that needs
     *        to be mapped to the user.
     * @param hostEntityID <code>EntityID</code> of the hosted provider.
     * @param realm realm or the organization name that may be used to find
     *        the user information.
     * @return user's disntinguished name or the universal ID.
     * @exception WSFederationException if any failure.
     */
    public String getIdentity(
        RequestSecurityTokenResponse rstr,
        String hostEntityID,
        String realm
    ) throws WSFederationException {

        if(rstr == null) {
           throw new WSFederationException(bundle.getString(
                 "nullRstr"));
        }

        if(hostEntityID == null) {
           throw new WSFederationException(bundle.getString(
                 "nullHostEntityID"));
        }
        
        if(realm == null) {
           throw new WSFederationException(bundle.getString(
                 "nullRealm"));
        }

        SAML11RequestedSecurityToken rst 
            = (SAML11RequestedSecurityToken)rstr.getRequestedSecurityToken();

        Subject subject = null;
        Assertion assertion = rst.getAssertion();
        Iterator iter = assertion.getStatement().iterator();
        while (iter.hasNext()) {
            Statement statement = (Statement)iter.next();
            if (statement.getStatementType() ==
                Statement.AUTHENTICATION_STATEMENT) {
                subject = ((SubjectStatement)statement).getSubject();
                break;
            }
	}
        NameIdentifier nameID = subject.getNameIdentifier();
 
        String userID = null;
        String format = nameID.getFormat();
        
        String remoteEntityID = 
            WSFederationMetaManager.getEntityByTokenIssuerName(realm, 
            assertion.getIssuer());
        if(debug.messageEnabled()) {
            debug.message(
                "DefaultLibrarySPAccountMapper.getIdentity(Assertion):" +
                " realm = " + realm + " hostEntityID = " + hostEntityID);  
        }

        try {
            userID = dsProvider.getUserID(realm, 
                getSearchParameters(nameID, hostEntityID, remoteEntityID));
        } catch(DataStoreProviderException dse) {
            debug.error(
               "DefaultLibrarySPAccountMapper.getIdentity(Assertion): " +
               "DataStoreProviderException", dse);
            throw new WSFederationException(dse);
        }
        if(userID != null) {
            return userID;
        }

        //Check if this is an auto federation case.
        return getAutoFedUser(realm, hostEntityID, assertion);
    }

    /**
     * Returns user for the auto federate attribute.
     *
     * @param realm realm name.
     * @param entityID hosted <code>EntityID</code>.
     * @param <code>Assertion</code> from the identity provider.
     * @return auto federation mapped user from the assertion
     *         auto federation <code>AttributeStatement</code>.
     *         null if the statement does not have the auto federation 
     *         attribute.
     */ 
    protected String getAutoFedUser(String realm, 
           String entityID, Assertion assertion) throws WSFederationException {
        Set statements = assertion.getStatement();

        if(statements == null || statements.size() == 0) {
           if(debug.messageEnabled()) { 
              debug.message("DefaultLibrarySPAccountMapper.getAutoFedUser: " +
              "Assertion does not have statements.");
           }
           return null;
        }

        String autoFedEnable = getAttribute(realm, 
               entityID, SAML2Constants.AUTO_FED_ENABLED);

        if(autoFedEnable == null || autoFedEnable.equals("false")) {
           if(debug.messageEnabled()) { 
              debug.message("DefaultLibrarySPAccountMapper.getAutoFedUser: " +
              "Auto federation is disabled.");
           }
           return null;
        }
       
        String autoFedAttribute = getAttribute(realm, entityID,
               SAML2Constants.AUTO_FED_ATTRIBUTE);

        if(autoFedAttribute == null || autoFedAttribute.length() == 0) {
           if(debug.messageEnabled()) { 
              debug.message("DefaultLibrarySPAccountMapper.getAutoFedUser: " +
              "Auto federation attribute is not configured.");
           }
           return null;
        }
        
        Set autoFedAttributeValue = null;
        Iterator iter = statements.iterator();
        while(iter.hasNext()) {
           Statement statement = (Statement)iter.next();
           if ( statement.getStatementType()==Statement.ATTRIBUTE_STATEMENT) {
               autoFedAttributeValue = 
                  getAttribute((AttributeStatement)statement, autoFedAttribute, 
                  realm, entityID);
               if(autoFedAttributeValue != null && 
                  !autoFedAttributeValue.isEmpty()) {
                  break;
               }
           }
        }

        if(autoFedAttributeValue == null ||
           autoFedAttributeValue.isEmpty()) {
           if(debug.messageEnabled()) {
              debug.message("DefaultLibrarySPAccountMapper.getAutoFedUser: " +
              "Auto federation attribute is not specified in the assertion.");
           }
           return null;
        }

        DefaultSPAttributeMapper attributeMapper = 
                   new DefaultSPAttributeMapper();
        Map attributeMap = attributeMapper.getConfigAttributeMap(
                   realm, entityID);
        if(attributeMap == null && attributeMap.isEmpty()) {
           if(debug.messageEnabled()) {
              debug.message("DefaultLibrarySPAccountMapper.getAutoFedUser: " +
              "attribute map is not configured.");
           }
        }

        String autoFedMapAttribute = (String)attributeMap.get(autoFedAttribute);

        if(autoFedMapAttribute == null) {
           if(debug.messageEnabled()) {
              debug.message("DefaultLibrarySPAccountMapper.getAutoFedUser: " +
              "Auto federation attribute map is not specified in config.");
           }
           return null;
        }

        try {
            Map map = new HashMap();
            map.put(autoFedMapAttribute, autoFedAttributeValue);

            if(debug.messageEnabled()) {
               debug.message("DefaultLibrarySPAccountMapper.getAutoFedUser: " +
               "Search map: " + map);
            }

            String userId = dsProvider.getUserID(realm, map); 
            if (userId != null && userId.length() != 0) {
                return userId;
            } else {
                // check dynamic profile creation or ignore profile, if enabled,
                // return auto-federation attribute value as uid 
                if (isDynamicalOrIgnoredProfile(realm)) {
                    if(debug.messageEnabled()) {
                        debug.message(
                            "DefaultLibrarySPAccountMapper: dynamical user " +
                            "creation or ignore profile enabled : uid=" 
                            + autoFedAttributeValue); 
                    }
                    // return the first value as uid
                    return (String) autoFedAttributeValue.
                           iterator().next();
                }
            } 
        } catch (DataStoreProviderException dse) {

            if(debug.warningEnabled()) {
               debug.warning("DefaultLibrarySPAccountMapper.getAutoFedUser: " +
               "Datastore provider exception", dse);
            }
        }
        return null;

    }

    /**
     * Checks if dynamical profile creation or ignore profile is enabled.
     * @param realm realm to check the dynamical profile creation attributes.
     * @return true if dynamical profile creation or ignore profile is enabled,
     * false otherwise.
     */
    protected boolean isDynamicalOrIgnoredProfile(String realm) {
        return true;
    }

    /**
     * Returns the attribute name.
     */
    private Set getAttribute(
                AttributeStatement statement,
                String attributeName,
                String realm,
                String hostEntityID)
    {
        if (debug.messageEnabled()) {
            debug.message(
                "DefaultLibrarySPAccountMapper.getAttribute: attribute" +
                "Name =" + attributeName);
        }

        List list = statement.getAttribute();

        for(Iterator iter=list.iterator(); iter.hasNext();) {
            Attribute attribute = (Attribute)iter.next();
            if(!attributeName.equalsIgnoreCase(attribute.getAttributeName())) {
               continue;
            }

            List values = null;
            try {
                values = attribute.getAttributeValue();
            }
            catch (SAMLException se)
            {
                // Just ignore it and carry on - getAttributeValue doesn't
                // really throw an exception - it just says it does
            }
            if(values == null || values.size() == 0) {
               return null;
            }
            Set set = new HashSet();
            set.addAll(values); 
            return set; 
        }
        return null;
    }
}

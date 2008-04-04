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
 * $Id: TaskModelImpl.java,v 1.3 2008-04-04 04:30:19 veiming Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.task.model;

import com.sun.identity.cot.COTException;
import com.sun.identity.saml.xmlsig.JKSKeyProvider;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.cot.COTConstants;
import com.sun.identity.cot.CircleOfTrustManager;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;


public class TaskModelImpl
    extends AMModelBase
    implements TaskModel
{
    public TaskModelImpl(HttpServletRequest req, Map map) {
	super(req, map);
    }

    /**
     * Returns realm names.
     *
     * @return realm names.
     * @throws AMConsoleException if realm cannot be retrieved.
     */
    public Set getRealms() 
        throws AMConsoleException {
        Set results = new TreeSet();
        results.addAll(super.getRealmNames("/", "*"));
        results.add("/");
        return results;
    }

    /**
     * Returns a set of signing keys.
     *
     * @return a set of signing keys.
     */
    public Set getSigningKeys()
        throws AMConsoleException {
        try {
            Set keyEntries = new HashSet();
            JKSKeyProvider kp = new JKSKeyProvider();
            KeyStore ks = kp.getKeyStore();
            Enumeration e = ks.aliases();
            if (e != null) {
                while (e.hasMoreElements()) {
                    String alias = (String) e.nextElement();
                    if (ks.isKeyEntry(alias)) {
                        keyEntries.add(alias);
                    }
                }
            }
            return keyEntries;
        } catch (KeyStoreException e) {
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Returns a set of circle of trusts.
     * 
     * @param realm Realm.
     * @return a set of circle of trusts.
     * @throws AMConsoleException if unable to retrieve circle of trusts.
     */
    public Set getCircleOfTrusts(String realm) 
        throws AMConsoleException {
        try {
            CircleOfTrustManager mgr = new CircleOfTrustManager();
            return mgr.getAllCirclesOfTrust(realm);
        } catch (COTException ex) {
            throw new AMConsoleException(ex.getMessage());
        }
    }
    
    /**
     * Returns a set of entities in a circle of trust.
     * 
     * @param realm Realm.
     * @param cotName Name of circle of trust.
     * @return a set of entities in a circle of trust.
     * @throws AMConsoleException if unable to retrieve entities.
     */
    public Set getEntities(String realm, String cotName) 
        throws AMConsoleException {
        try {
            CircleOfTrustManager mgr = new CircleOfTrustManager();
            Set entities = mgr.listCircleOfTrustMember(realm, cotName, 
                COTConstants.SAML2);
            return (entities == null) ? Collections.EMPTY_SET : entities;
        } catch (COTException ex) {
            throw new AMConsoleException(ex.getMessage());
        }
    }
    
    /**
     * Returns a set of hosted IDP in a circle of trust.
     * 
     * @param realm Realm.
     * @param cotName Name of circle of trust.
     * @return a set of hosted IDP in a circle of trust.
     * @throws AMConsoleException if IDP cannot be returned.
     */
    public Set getHostedIDP(String realm, String cotName)
        throws AMConsoleException {
        return getEntities(realm, cotName, true, true);
    }
    
    /**
     * Returns a set of remote IDP in a circle of trust.
     * 
     * @param realm Realm.
     * @param cotName Name of circle of trust.
     * @return a set of remote IDP in a circle of trust.
     * @throws AMConsoleException if IDP cannot be returned.
     */
    public Set getRemoteIDP(String realm, String cotName)
        throws AMConsoleException {
        return getEntities(realm, cotName, true, false);
    }
    
    
    /**
     * Returns a set of hosted SP in a circle of trust.
     * 
     * @param realm Realm.
     * @param cotName Name of circle of trust.
     * @return a set of hosted SP in a circle of trust.
     * @throws AMConsoleException if IDP cannot be returned.
     */
    public Set getHostedSP(String realm, String cotName)
        throws AMConsoleException {
        return getEntities(realm, cotName, false, true);
    }
    
    /**
     * Returns a set of remote SP in a circle of trust.
     * 
     * @param realm Realm.
     * @param cotName Name of circle of trust.
     * @return a set of remote SP in a circle of trust.
     * @throws AMConsoleException if IDP cannot be returned.
     */
    public Set getRemoteSP(String realm, String cotName)
        throws AMConsoleException {
        return getEntities(realm, cotName, false, false);
    }

    private Set getEntities(
        String realm, 
        String cotName, 
        boolean bIDP, 
        boolean hosted
    ) throws AMConsoleException {
        try {
            SAML2MetaManager mgr = new SAML2MetaManager();
            Set entities = getEntities(realm, cotName);
            Set results = new HashSet();

            for (Iterator i = entities.iterator(); i.hasNext();) {
                String entityId = (String) i.next();
                EntityConfigElement elm = mgr.getEntityConfig(realm, entityId);
                if (elm.isHosted() == hosted) {
                    EntityDescriptorElement desc = mgr.getEntityDescriptor(
                        realm, entityId);
                    
                    if (bIDP) {
                        if (SAML2MetaUtils.getIDPSSODescriptor(desc) != null) {
                            results.add(entityId);
                        }
                    } else {
                        if (SAML2MetaUtils.getSPSSODescriptor(desc) != null) {
                            results.add(entityId);
                        }
                    }
                }
            }
            return results;
        } catch (SAML2MetaException ex) {
            throw new AMConsoleException(ex.getMessage());
        }
    }
    
    /**
     * Returns a map of realm to a map of circle of trust name to a set of
     * Hosted Identity Providers.
     * 
     * @return a map of realm to a map of circle of trust name to a set of
     *         Hosted Identity Providers.
     * @throws AMConsoleException if this map cannot be constructed.
     */
    public Map getRealmCotWithHostedIDPs() 
        throws AMConsoleException {
        Map map = new HashMap();
        Set realms = getRealms();
        for (Iterator i = realms.iterator(); i.hasNext(); )  {
            String realm = (String)i.next();
            
            Set cots = getCircleOfTrusts(realm);
            for (Iterator j = cots.iterator(); j.hasNext(); ) {
                String cotName = (String)j.next();
                Set idps = getHostedIDP(realm, cotName);
                
                if ((idps != null) && !idps.isEmpty()) {
                    Map r = (Map)map.get(realm);
                    if (r == null) {
                        r = new HashMap();
                        map.put(realm, r);
                    }
                    r.put(cotName, idps);
                }
            }
        }
        return map;
    }
}


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
 * $Id: SecurityMechanism.java,v 1.1 2007-03-23 00:02:02 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.security;

import org.w3c.dom.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

 
/**
 * This class exposes the <code>SecurityMechnism</code>s of the web services
 * security. 
 * @supported.all.api
 */
public class SecurityMechanism {

    // Initialize the security mechanism map
    private static Map map = new HashMap();

    /**
     * <code>URI</code> for the SAML Holder of Key security profile. 
     */
    public static final String WSS_NULL_SAML_HK_URI = 
               "urn:sun:wss:security:null:SAMLToken-HK";

    /**
     * <code>URI</code> for the SAML Holder of Key security profile with
     *   <code>TLS</code> or <code>SSL</code> enabled.
     */
    public static final String WSS_TLS_SAML_HK_URI = 
               "urn:sun:wss:security:TLS:SAMLToken-HK";

    /**
     * <code>URI</code> for the SAML Holder of Key security profile with
     *   <code>TLS</code> or <code>SSL</code>  and client auth enabled.
     */
    public static final String WSS_CLIENT_TLS_SAML_HK_URI = 
               "urn:sun:wss:security:ClientTLS:SAMLToken-HK";

    /**
     * <code>URI</code> for the SAML Sender vouches security profile. 
     */
    public static final String WSS_NULL_SAML_SV_URI = 
               "urn:sun:wss:security:null:SAMLToken-SV";

    /**
     * <code>URI</code> for the SAML Sender vouches security profile with
     *   <code>TLS</code> or <code>SSL</code> enabled.
     */
    public static final String WSS_TLS_SAML_SV_URI = 
               "urn:sun:wss:security:TLS:SAMLToken-SV";

    /**
     * <code>URI</code> for the SAML Sender vouches security profile with
     *   <code>TLS</code> or <code>SSL</code>  and client auth enabled.
     */
    public static final String WSS_CLIENT_TLS_SAML_SV_URI = 
               "urn:sun:wss:security:ClientTLS:SAMLToken-SV";

    /**
     * <code>URI</code> for the X509 token security profile. 
     */
    public static final String WSS_NULL_X509_TOKEN_URI = 
               "urn:sun:wss:security:null:X509Token";

    /**
     * <code>URI</code> for the X509 security profile with
     *   <code>TLS</code> or <code>SSL</code> enabled.
     */
    public static final String WSS_TLS_X509_TOKEN_URI = 
               "urn:sun:wss:security:TLS:X509Token";

    /**
     * <code>URI</code> for the X509 token security profile with
     *   <code>TLS</code> or <code>SSL</code>  and client auth enabled.
     */
    public static final String WSS_CLIENT_TLS_X509_TOKEN_URI = 
               "urn:sun:wss:security:ClientTLS:X509Token";

    /**
     * <code>URI</code> for the Username token security profile. 
     */
    public static final String WSS_NULL_USERNAME_TOKEN_URI = 
               "urn:sun:wss:security:null:UserNameToken";

    /**
     * <code>URI</code> for the username token security profile with
     *   <code>TLS</code> or <code>SSL</code> enabled.
     */
    public static final String WSS_TLS_USERNAME_TOKEN_URI = 
               "urn:sun:wss:security:TLS:UserNameToken";

    /**
     * <code>URI</code> for the username token security profile with
     *   <code>TLS</code> or <code>SSL</code>  and client auth enabled.
     */
    public static final String WSS_CLIENT_TLS_USERNAME_TOKEN_URI = 
               "urn:sun:wss:security:ClientTLS:UserNameToken";
    
    /**
     * <code>URI</code> for all the liberty security profiles.
     * This <code>URI</code> is mainly for the webservices clients where
     * the real security mechanims are found from the liberty discovery service.
     */
    public static final String LIBERTY_DS_SECURITY_URI = 
                               "urn:sun:liberty:discovery:security";

    /**
     * <code>URI</code> for the liberty X509 token security profile.
     */
    public static final String LIB_NULL_X509_TOKEN_URI = 
               "urn:liberty:security:2005-02:null:X509";

    /**
     * <code>URI</code> for the liberty SAML Bearer token security profile.
     */
    public static final String LIB_NULL_SAML_BEARER_TOKEN_URI = 
               "urn:liberty:security:2005-02:null:Bearer";

    /**
     * <code>URI</code> for the liberty SAML token security profile.
     */
    public static final String LIB_NULL_SAML_TOKEN_URI = 
               "urn:liberty:security:2005-02:null:SAML";

    /**
     * <code>URI</code> for the liberty X509 token security profile with TLS.
     */
    public static final String LIB_TLS_X509_TOKEN_URI = 
               "urn:liberty:security:2005-02:TLS:X509";

    /**
     * <code>URI</code> for the liberty SAML Bearer token security profile with
     *  SSL enabled.
     */
    public static final String LIB_TLS_SAML_BEARER_TOKEN_URI = 
               "urn:liberty:security:2005-02:TLS:Bearer";

    /**
     * <code>URI</code> for the liberty SAML token security profile with SSL.
     */
    public static final String LIB_TLS_SAML_TOKEN_URI = 
               "urn:liberty:security:2005-02:TLS:SAML";

    /**
     * <code>URI</code> for the liberty X509 token security profile with TLS 
     * and client auth enabled.
     */
    public static final String LIB_CLIENT_TLS_X509_TOKEN_URI = 
               "urn:liberty:security:2005-02:ClientTLS:X509";

    /**
     * <code>URI</code> for the liberty SAML Bearer token security profile with
     *  SSL and client auth enabled.
     */
    public static final String LIB_CLIENT_TLS_SAML_BEARER_TOKEN_URI = 
               "urn:liberty:security:2005-02:ClientTLS:Bearer";

    /**
     * <code>URI</code> for the liberty SAML token security profile with SSL 
     * and client auth enabled.
     */
    public static final String LIB_CLIENT_TLS_SAML_TOKEN_URI = 
               "urn:liberty:security:2005-02:ClientTLS:SAML";

    static {
        map.put("ClientTLS-SAML-HolderOfKey", WSS_CLIENT_TLS_SAML_HK_URI);
        map.put("TLS-SAML-HolderOfKey", WSS_TLS_SAML_HK_URI); 
        map.put("SAML-HolderOfKey", WSS_NULL_SAML_HK_URI); 
        map.put("ClientTLS-SAML-SenderVouches", WSS_CLIENT_TLS_SAML_SV_URI); 
        map.put("TLS-SAML-SenderVouches", WSS_TLS_SAML_SV_URI);
        map.put("SAML-SenderVouches", WSS_NULL_SAML_SV_URI); 
        map.put("X509Token", WSS_NULL_X509_TOKEN_URI);
        map.put("TLS-X509Token", WSS_TLS_X509_TOKEN_URI);
        map.put("ClientTLS-X509Token", WSS_CLIENT_TLS_X509_TOKEN_URI);
        map.put("UserNameToken", WSS_NULL_USERNAME_TOKEN_URI);
        map.put("TLS-UserNameToken", WSS_TLS_USERNAME_TOKEN_URI); 
        map.put("ClientTLS-UserNameToken", WSS_CLIENT_TLS_USERNAME_TOKEN_URI);
        map.put("LibertyDiscoverySecurity", LIBERTY_DS_SECURITY_URI);
        map.put("LibertyX509Token", LIB_NULL_X509_TOKEN_URI);
        map.put("LibertyBearerToken", LIB_NULL_SAML_BEARER_TOKEN_URI);
        map.put("LibertySAMLToken", LIB_NULL_SAML_TOKEN_URI);
        map.put("TLS-LibertyX509Token", LIB_TLS_X509_TOKEN_URI);
        map.put("TLS-LibertyBearerToken", LIB_TLS_SAML_BEARER_TOKEN_URI);
        map.put("TLS-LibertySAMLToken", LIB_TLS_SAML_TOKEN_URI);
        map.put("ClientTLS-LibertyX509Token", LIB_CLIENT_TLS_X509_TOKEN_URI);
        map.put("ClientTLS-LibertyBearerToken", 
                          LIB_CLIENT_TLS_SAML_BEARER_TOKEN_URI);
        map.put("ClientTLS-LibertySAMLToken", LIB_CLIENT_TLS_SAML_TOKEN_URI);
    }

    // The following defines Security mechanism objects.

    /**
     * Defines the security mechanism for the saml token holder of key
     * with SSL and client auth enabled.
     */
    public static final SecurityMechanism WSS_CLIENT_TLS_SAML_HK = 
                      new SecurityMechanism("ClientTLS-SAML-HolderOfKey");

    /**
     * Defines the security mechanism for the saml token holder of key
     * with SSL enabled.
     */
    public static final SecurityMechanism WSS_TLS_SAML_HK = 
                      new SecurityMechanism("TLS-SAML-HolderOfKey");

    /**
     * Defines the security mechanism for the saml token holder of key.
     */
    public static final SecurityMechanism WSS_NULL_SAML_HK = 
                      new SecurityMechanism("SAML-HolderOfKey");

    /**
     * Defines the security mechanism for the saml token sender vouches
     * with SSL and client auth enabled.
     */
    public static final SecurityMechanism WSS_CLIENT_TLS_SAML_SV = 
                      new SecurityMechanism("ClientTLS-SAML-SenderVouches");

    /**
     * Defines the security mechanism for the saml token sender vouches
     * with SSL enabled.
     */
    public static final SecurityMechanism WSS_TLS_SAML_SV = 
                      new SecurityMechanism("TLS-SAML-SenderVouches");

    /**
     * Defines the security mechanism for the saml token sender vouches.
     */
    public static final SecurityMechanism WSS_NULL_SAML_SV = 
                      new SecurityMechanism("SAML-SenderVouches");

    /**
     * Defines the security mechanism for the X509 token profile.
     */
    public static final SecurityMechanism WSS_NULL_X509_TOKEN = 
                      new SecurityMechanism("X509Token");

    /**
     * Defines the security mechanism for the X509 token profile with 
     * SSL enabled.
     */
    public static final SecurityMechanism WSS_TLS_X509_TOKEN = 
                      new SecurityMechanism("TLS-X509Token");

    /**
     * Defines the security mechanism for the X509 token profile with 
     * SSL and client auth enabled.
     */
    public static final SecurityMechanism WSS_CLIENT_TLS_X509_TOKEN = 
                      new SecurityMechanism("ClientTLS-X509Token");

    /**
     * Defines the security mechanism for the Username token profile.
     */
    public static final SecurityMechanism WSS_NULL_USERNAME_TOKEN = 
                      new SecurityMechanism("UserNameToken");

    /**
     * Defines the security mechanism for the Username token profile
     * with SSL enabled.
     */
    public static final SecurityMechanism WSS_TLS_USERNAME_TOKEN = 
                      new SecurityMechanism("TLS-UserNameToken");

    /**
     * Defines the security mechanism for the Username token profile
     * with SSL and client auth enabled.
     */
    public static final SecurityMechanism WSS_CLIENT_TLS_USERNAME_TOKEN = 
                      new SecurityMechanism("ClientTLS-UserNameToken");

    /**
     * Defines the security mechanism for the Liberty token profiles.
     */
    public static final SecurityMechanism LIBERTY_DS_SECURITY = 
                      new SecurityMechanism("LibertyDiscoverySecurity", true);
    /**
     * Defines the security mechanism for the Liberty x509 token profile.
     */
    public static final SecurityMechanism LIB_NULL_X509_TOKEN = 
                      new SecurityMechanism("LibertyX509Token", true);

    /**
     * Defines the security mechanism for the Liberty bearer token profile.
     */
    public static final SecurityMechanism LIB_NULL_SAML_BEARER_TOKEN = 
                      new SecurityMechanism("LibertyBearerToken", true);

    /**
     * Defines the security mechanism for the Liberty SAML token profile.
     */
    public static final SecurityMechanism LIB_NULL_SAML_TOKEN = 
                      new SecurityMechanism("LibertySAMLToken", true);

    /**
     * Defines the security mechanism for the Liberty x509 token profile
     *         with SSL enabled.
     */
    public static final SecurityMechanism LIB_TLS_X509_TOKEN = 
                      new SecurityMechanism("TLS-LibertyX509Token", true);

    /**
     * Defines the security mechanism for the Liberty bearer token profile
     *             with SSL enabled.
     */
    public static final SecurityMechanism LIB_TLS_SAML_BEARER_TOKEN = 
                      new SecurityMechanism("TLS-LibertyBearerToken", true);

    /**
     * Defines the security mechanism for the Liberty SAML token profile
     *         with SSL enabled.
     */
    public static final SecurityMechanism LIB_TLS_SAML_TOKEN = 
                      new SecurityMechanism("TLS-LibertySAMLToken", true);

    /**
     * Defines the security mechanism for the Liberty x509 token profile
     *         with SSL and client auth enabled.
     */
    public static final SecurityMechanism LIB_CLIENT_TLS_X509_TOKEN = 
                      new SecurityMechanism("ClientTLS-LibertyX509Token", true);

    /**
     * Defines the security mechanism for the Liberty bearer token profile
     *             with SSL and client auth enabled.
     */
    public static final SecurityMechanism LIB_CLIENT_TLS_SAML_BEARER_TOKEN = 
                  new SecurityMechanism("ClientTLS-LibertyBearerToken", true);

    /**
     * Defines the security mechanism for the Liberty SAML token profile
     *         with SSL and client auth enabled.
     */
    public static final SecurityMechanism LIB_CLIENT_TLS_SAML_TOKEN = 
                      new SecurityMechanism("ClientTLS-LibertySAMLToken", true);

    private String sechMech = null;
    private String uri = null;
    private boolean lookupEnabled = false;
    private boolean registerEnabled = false;

    /**
     * Constructor
     *
     * @param name the security mechanism name.
     *
     */ 
    public SecurityMechanism(String name) {
       this.sechMech = name;
       this.uri = (String)map.get(sechMech);
    }

    /**
     * Constructor 
     * @param name the name of the security mechanism.
     * @param useTA the boolean variable to let enable for the 
     *        trust authority look up or registration.
     */
    public SecurityMechanism(String name, boolean useTA) {
       this.sechMech = name;
       this.uri = (String)map.get(sechMech);
       this.lookupEnabled = useTA; 
       this.registerEnabled = useTA;
    }

    /** 
     * Returns the security mechanism name.
     *
     * @return the name of the security mechanism. 
     */
    public String getName() {
        return sechMech;
    }

    /**
     * Returns the <code>URI of the security mechanism.
     *
     * @return the name of the security mechanism.
     */
    public String getURI() {
        return uri;
    }

    /**
     * Checks if the Trust Authrotiy registration is required
     * for this security mechanism.
     *
     * @return true if the trust authority registration is required.
     */
    public boolean isTARegistrationRequired() {
        return registerEnabled;
    }

    /**
     * Checks if the Trust Authority lookup is required for this
     * security mechanism.
     *
     * @return true if the trust authority lookup is required.
     */
    public boolean isTALookupRequired() {
        return lookupEnabled;
    }

    /**
     * Returns the security mechanism for the corresponding
     * security mechanism <code>URI</code>.
     *
     * @return the security mechanism object.
     */
    public static SecurityMechanism getSecurityMechanism(String uri) {
        if(uri == null) {
           return null;
        }
        Iterator entries =  map.entrySet().iterator();
        while(entries.hasNext()) {
           Map.Entry entry = (Map.Entry)entries.next();
           String key = (String)entry.getKey();
           String value = (String)entry.getValue();
           if(value.equals(uri)) {
              if(uri.equals(LIBERTY_DS_SECURITY_URI) ||
                 uri.equals(LIB_NULL_X509_TOKEN_URI) ||
                 uri.equals(LIB_TLS_X509_TOKEN_URI) ||
                 uri.equals(LIB_CLIENT_TLS_X509_TOKEN_URI) ||
                 uri.equals(LIB_NULL_SAML_TOKEN_URI) ||
                 uri.equals(LIB_TLS_SAML_TOKEN_URI) ||
                 uri.equals(LIB_CLIENT_TLS_SAML_TOKEN_URI) ||
                 uri.equals(LIB_NULL_SAML_BEARER_TOKEN_URI) ||
                 uri.equals(LIB_TLS_SAML_BEARER_TOKEN_URI) ||
                 uri.equals(LIB_CLIENT_TLS_SAML_BEARER_TOKEN_URI)) {
                 return new SecurityMechanism(key, true);
              } else {
                 return new SecurityMechanism(key);
              }
           }
        }
        return null;
    }

    /**
     * Returns the list of liberty security mechanism URIs.
     * @return the list of liberty security mechanism URIs.
     */
    public static List getLibertySecurityMechanismURIs() {
        List list = new ArrayList();
        list.add(LIB_NULL_X509_TOKEN_URI);
        list.add(LIB_TLS_X509_TOKEN_URI);
        list.add(LIB_CLIENT_TLS_X509_TOKEN_URI);
        list.add(LIB_NULL_SAML_BEARER_TOKEN_URI);
        list.add(LIB_TLS_SAML_BEARER_TOKEN_URI);
        list.add(LIB_CLIENT_TLS_SAML_BEARER_TOKEN_URI);
        list.add(LIB_NULL_SAML_TOKEN_URI);
        list.add(LIB_TLS_SAML_TOKEN_URI);
        list.add(LIB_CLIENT_TLS_SAML_TOKEN_URI);
        return list;
    }

}

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: StsConfigurationDao.java,v 1.2 2009-09-30 22:01:30 ggennaro Exp $
 */

package com.sun.identity.admin.dao;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.model.StsConfigurationBean;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import com.sun.identity.wss.security.PasswordCredential;
import com.sun.identity.wss.security.WSSUtils;

public class StsConfigurationDao {

    private static final String SERVICE_NAME = "sunFAMSTSService";
    private static final String ISSUER = "stsIssuer";
    private static final String TOKEN_LIFETIME = "stsLifetime";
    private static final String KEY_ALIAS = "stsCertAlias";
    private static final String TOKEN_PLUGIN_CLASS_NAME 
                                = "com.sun.identity.wss.sts.clientusertoken";
    private static final String SECURITY_MECHANISMS = "SecurityMech";
    private static final String USERNAME_CREDENTIALS = "UserCredential";
    private static final String KERBEROS_DOMAIN = "KerberosDomain";
    private static final String KERBEROS_DOMAIN_SERVER = "KerberosDomainServer";
    private static final String KERBEROS_SERVICE_PRINCIPAL
                                = "KerberosServicePrincipal";
    private static final String KERBEROS_KEY_TAB_FILE = "KerberosKeyTabFile";
    private static final String SIGNING_REFERENCE_TYPE = "SigningRefType";
    private static final String AUTHENTICATION_CHAIN = "AuthenticationChain";    
    private static final String RESPONSE_SIGN = "isResponseSign";
    private static final String RESPONSE_ENCRYPT = "isResponseEncrypt";
    private static final String REQUEST_SIGN = "isRequestSign";     
    private static final String REQUEST_ENCRYPT = "isRequestEncrypt";
    private static final String REQUEST_HEADER_ENCRYPT 
                                = "isRequestHeaderEncrypt";
    private static final String ENCRYPTION_ALGORITHM = "EncryptionAlgorithm";
    private static final String ENCRYPTION_STRENGTH = "EncryptionStrength";
    private static final String PRIVATE_KEY_ALIAS = "privateKeyAlias";
    private static final String PUBLIC_KEY_ALIAS = "publicKeyAlias";
    private static final String NAME_ID_MAPPER = "NameIDMapper";
    private static final String INCLUDE_MEMBERSHIPS = "includeMemberships";
    private static final String ATTRIBUTE_NAMESPACE = "AttributeNamespace";
    private static final String TRUSTED_ISSUERS = "trustedIssuers";
    private static final String TRUSTED_IP_ADDRESSES = "trustedIPAddresses";
    private static final String SAML_ATTRIBUTE_MAPPING = "SAMLAttributeMapping";

    public static final String ENCRYPTION_ALGORITHM_AES = "AES";
    public static final String ENCRYPTION_ALGORITHM_DESEDE = "DESede";

    StsConfigurationDao() {
        // do nothing to force use of static methods
    }
    
    //--------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static public StsConfigurationBean retrieveConfig() {
        StsConfigurationBean stsConfigBean;
        SSOToken adminToken = WSSUtils.getAdminToken();

        try {
            
            ServiceSchemaManager scm 
                        = new ServiceSchemaManager(SERVICE_NAME, adminToken);
            ServiceSchema globalSchema = scm.getGlobalSchema();
            Map serviceAttributeMap = globalSchema.getAttributeDefaults();
            stsConfigBean = getBeanFromMap(serviceAttributeMap);
            
        } catch (SSOException ssoEx) {
            throw new RuntimeException(ssoEx);
        } catch (SMSException smsEx) {
            throw new RuntimeException(smsEx);
        }

        return stsConfigBean;
    }

    public void saveConfig(StsConfigurationBean stsConfigBean) {
        
    }

    //--------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static private StsConfigurationBean getBeanFromMap(Map map) {
        StsConfigurationBean s = new StsConfigurationBean();
        
        s.setIssuer(getStringValue(ISSUER, map));
        s.setTokenLifetime(getIntValue(TOKEN_LIFETIME, map));
        s.setKeyAlias(getStringValue(KEY_ALIAS, map));
        s.setTokenPluginClassName(getStringValue(TOKEN_PLUGIN_CLASS_NAME, map));
        s.setSecurityMechanisms(getListValue(SECURITY_MECHANISMS, map));
        s.setUserNameTokenCredentials(getPasswordCredentialsValue(map));
        s.setKerberosDomain(getStringValue(KERBEROS_DOMAIN, map));
        s.setKerberosDomainServer(getStringValue(KERBEROS_DOMAIN_SERVER, map));
        s.setKerberosServicePrincipal(getStringValue(KERBEROS_SERVICE_PRINCIPAL, map));
        s.setKerberosKeyTabFile(getStringValue(KERBEROS_KEY_TAB_FILE, map));
        s.setX509SigningReferenceType(getStringValue(SIGNING_REFERENCE_TYPE, map));
        s.setAuthenticationChain(getStringValue(AUTHENTICATION_CHAIN, map));
        s.setResponseSigned(getBooleanValue(RESPONSE_SIGN, map));
        s.setResponseEncrypted(getBooleanValue(RESPONSE_ENCRYPT, map));
        s.setRequestSigned(getBooleanValue(REQUEST_SIGN, map));
        s.setRequestEncrypted(getBooleanValue(REQUEST_ENCRYPT, map));
        s.setRequestHeaderEncrypted(getBooleanValue(REQUEST_HEADER_ENCRYPT, map));
        s.setEncryptionAlgorithm(getStringValue(ENCRYPTION_ALGORITHM, map));
        s.setEncryptionStrength(getIntValue(ENCRYPTION_STRENGTH, map));
        s.setPrivateKeyAlias(getStringValue(PRIVATE_KEY_ALIAS, map));
        s.setPublicKeyAlias(getStringValue(PUBLIC_KEY_ALIAS, map));
        s.setNameIdMapper(getStringValue(NAME_ID_MAPPER, map));
        s.setIncludeMemberships(getBooleanValue(INCLUDE_MEMBERSHIPS, map));
        s.setAttributeNamespace(getStringValue(ATTRIBUTE_NAMESPACE, map));
        s.setTrustedIpAddresses(getListValue(TRUSTED_IP_ADDRESSES, map));
        s.setTrustedIssuers(getListValue(TRUSTED_ISSUERS, map));
        s.setSamlAttributeMapping(getListValue(SAML_ATTRIBUTE_MAPPING, map));
        
        return s;
    }

    @SuppressWarnings("unchecked")
    static private boolean getBooleanValue(String keyName, Map map) {
        String value = getStringValue(keyName, map);
        return Boolean.valueOf(value).booleanValue();
    }

    @SuppressWarnings("unchecked")
    static private int getIntValue(String keyName, Map map) {
        String value = getStringValue(keyName, map);
        return value == null ? -1 : Integer.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    static private String getStringValue(String keyName, Map map) {
        
        if( map != null && map.get(keyName) instanceof Set ) {
            
            Set<String> values = (Set<String>) map.get(keyName);
            if( values != null && !values.isEmpty() )
                return (String)values.iterator().next();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static private ArrayList<String> getListValue(String keyName, Map map) {
        ArrayList<String> a = new ArrayList<String>();
        if( map != null && map.get(keyName) instanceof Set ) {

            Set<String> values = (Set<String>) map.get(keyName);
            if( values != null ) {
                a.addAll(values);
            }
        }
        return a;
    }

    /**
     * Retrieves a list of password credentials obtained from the STS
     * configuration under USERNAME_CREDENTIALS stored in following raw format
     * for each entry:
     * 
     *  UserName:test|UserPassword:test 
     *  
     * @param map   Service attribute map
     * @return ArrayList of PasswordCredential objects
     */
    @SuppressWarnings("unchecked")
    static private ArrayList<PasswordCredential> getPasswordCredentialsValue(Map map) {
        ArrayList<PasswordCredential> a = new ArrayList<PasswordCredential>();

        if( map.get(USERNAME_CREDENTIALS) != null ) {
            Set<String> values = (Set<String>)map.get(USERNAME_CREDENTIALS);
            Pattern p = Pattern.compile("UserName:(.+?)\\|UserPassword:(.+?)");
            
            for(String v : values) {
                StringTokenizer st = new StringTokenizer(v, ",");
                while( st.hasMoreTokens() ) {
                    String creds = st.nextToken();
                    Matcher m = p.matcher(creds);
                    if( m.matches() ) {
                        String username = m.group(1);
                        String password = m.group(2);
                        if( username != null && password != null ) {
                            PasswordCredential pc = new PasswordCredential(username, password);
                            a.add(pc);
                        }
                    }
                }
            }
        }
        return a;
    }

}

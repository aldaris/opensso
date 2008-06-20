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
 * $Id: FAMSTSConfiguration.java,v 1.4 2008-06-20 20:42:37 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.wss.sts.config;

import com.sun.xml.ws.api.security.trust.config.STSConfiguration;
import com.sun.xml.ws.api.security.trust.config.TrustSPMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import javax.security.auth.callback.CallbackHandler;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.locale.Locale;

import com.sun.identity.wss.sts.STSUtils;

import com.sun.identity.plugin.configuration.ConfigurationActionEvent;
import com.sun.identity.plugin.configuration.ConfigurationException;
import com.sun.identity.plugin.configuration.ConfigurationInstance;
import com.sun.identity.plugin.configuration.ConfigurationListener;
import com.sun.identity.plugin.configuration.ConfigurationManager;

public class FAMSTSConfiguration implements 
    STSConfiguration, ConfigurationListener {

    private static Map<String, TrustSPMetadata> spMap = 
        new HashMap<String, TrustSPMetadata>();
    private static String type;
    private static String issuer;
    private static boolean encryptIssuedToken = false;
    private static boolean encryptIssuedKey = true;
    private static long issuedTokenTimeout;
    private static String stsEndpoint;
    private static String certAlias;
    private static String clientUserToken;
    private static List secMech = null;
    private static boolean isResponseSign = false;
    private static boolean isResponseEncrypt = false;
    private static boolean isRequestSign = false;
    private static boolean isRequestEncrypt = false;
    private static boolean isRequestHeaderEncrypt = false;
    private static String privateKeyType;
    private static String privateKeyAlias;
    private static String publicKeyAlias;
    
    private CallbackHandler callbackHandler;
    
    private Map<String, Object> otherOptions = new HashMap<String, Object>();
    private static Set trustedIssuers = null;
    private static Set trustedIPAddresses = null;

    static final String CONFIG_NAME = "STS_CONFIG";
    static final String SERVICE_NAME = "sunFAMSTSService";

    static final String ISSUER = "stsIssuer";
    static final String END_POINT = "stsEndPoint";
    static final String ENCRYPT_ISSUED_KEY = "stsEncryptIssuedKey";
    static final String ENCRYPT_ISSUED_TOKEN = "stsEncryptIssuedToken";
    static final String LIFE_TIME = "stsLifetime";
    static final String TOKEN_IMPL_CLASS = "stsTokenImplClass";
    static final String CERT_ALIAS = "stsCertAlias";
    
    private static final String TRUSTED_ISSUERS = "trustedIssuers";
    private static final String TRUSTED_IP_ADDRESSES = "trustedIPAddresses";
    static final String CLIENT_USER_TOKEN = 
        "com.sun.identity.wss.sts.clientusertoken";
    static final String SEC_MECH = "SecurityMech";
    static final String RESPONSE_SIGN = "isResponseSign";
    static final String RESPONSE_ENCRYPT = "isResponseEncrypt";
    static final String REQUEST_SIGN = "isRequestSign";     
    static final String REQUEST_ENCRYPT = "isRequestEncrypt";
    static final String REQUEST_HEADER_ENCRYPT = "isRequestHeaderEncrypt";
    static final String PRIVATE_KEY_TYPE = "privateKeyType";
    static final String PRIVATE_KEY_ALIAS = "privateKeyAlias";
    static final String PUBLIC_KEY_ALIAS = "publicKeyAlias";

    private static Debug debug = STSUtils.debug;
    static ConfigurationInstance ci = null;

    static {
        try {
            ci = ConfigurationManager.getConfigurationInstance(CONFIG_NAME);
            ci.addListener(new FAMSTSConfiguration());
            setValues();
        } catch (ConfigurationException ce) {
             debug.error("FAMSTSConfiguration.static:", ce);
        }
    }

    /**
     * Default Constructor.
     */
    public FAMSTSConfiguration() {
    }

    /**
     * This method will be invoked when a component's 
     * configuration data has been changed. The parameters componentName,
     * realm and configName denotes the component name,
     * organization and configuration instance name that are changed 
     * respectively.
     *
     * @param e Configuration action event, like ADDED, DELETED, MODIFIED etc.
     */
    public void configChanged(ConfigurationActionEvent e) {
        if (debug.messageEnabled()) {
            debug.message("FAMSTSConfiguration: configChanged");
        }
        setValues();
    }

    /**
     * This method reads values from service schema.
     */
    static private void setValues() {
        String classMethod = "FAMSTSConfiguration.setValues:";
        Map attrMap = null;
        try {
            attrMap = ci.getConfiguration(null, null);
        } catch (ConfigurationException ce) {
            debug.error(classMethod, ce);
            return;
        }

        Set values = (Set)attrMap.get(ISSUER);
        if (values != null && !values.isEmpty()) {
            issuer = (String)values.iterator().next();
        }

        values = (Set)attrMap.get(END_POINT);
        if (values != null && !values.isEmpty()) {
            stsEndpoint = (String)values.iterator().next();
        }

        values = (Set)attrMap.get(ENCRYPT_ISSUED_KEY);
        if (values != null && !values.isEmpty()) {
            encryptIssuedKey = 
                Boolean.valueOf((String)values.iterator().next())
                .booleanValue();
        }

        values = (Set)attrMap.get(ENCRYPT_ISSUED_TOKEN);
        if (values != null && !values.isEmpty()) {
            encryptIssuedToken = 
                Boolean.valueOf((String)values.iterator().next())
                .booleanValue();
        }

        values = (Set)attrMap.get(LIFE_TIME);
        if (values != null && !values.isEmpty()) {
            issuedTokenTimeout = 
                Long.valueOf((String)values.iterator().next())
                .longValue();
        }

        values = (Set)attrMap.get(TOKEN_IMPL_CLASS);
        if (values != null && !values.isEmpty()) {
            type = (String)values.iterator().next();
        }

        values = (Set)attrMap.get(CERT_ALIAS);
        if (values != null && !values.isEmpty()) {
            certAlias = (String)values.iterator().next();
        }
        
        trustedIssuers = (Set)attrMap.get(TRUSTED_ISSUERS);
        trustedIPAddresses = (Set)attrMap.get(TRUSTED_IP_ADDRESSES);
        
        values = (Set)attrMap.get(CLIENT_USER_TOKEN);
        if (values != null && !values.isEmpty()) {
            clientUserToken = (String)values.iterator().next();
        }
        
        values = (Set)attrMap.get(SEC_MECH);
        if (values != null && !values.isEmpty()) {            
            if (secMech == null) {
               secMech = new ArrayList();
               secMech.addAll(values);
            } else {
               secMech.clear();
               secMech.addAll(values);
            }
                       
        }
        
        values = (Set)attrMap.get(RESPONSE_SIGN);
        if (values != null && !values.isEmpty()) {
            isResponseSign = 
                Boolean.valueOf((String)values.iterator().next())
                .booleanValue();
        }
        
        values = (Set)attrMap.get(RESPONSE_ENCRYPT);
        if (values != null && !values.isEmpty()) {
            isResponseEncrypt = 
                Boolean.valueOf((String)values.iterator().next())
                .booleanValue();
        }
        
        values = (Set)attrMap.get(REQUEST_SIGN);
        if (values != null && !values.isEmpty()) {
            isRequestSign = 
                Boolean.valueOf((String)values.iterator().next())
                .booleanValue();
        }
        
        values = (Set)attrMap.get(REQUEST_ENCRYPT);
        if (values != null && !values.isEmpty()) {
            isRequestEncrypt = 
                Boolean.valueOf((String)values.iterator().next())
                .booleanValue();
        }
        
        values = (Set)attrMap.get(REQUEST_HEADER_ENCRYPT);
        if (values != null && !values.isEmpty()) {
            isRequestHeaderEncrypt = 
                Boolean.valueOf((String)values.iterator().next())
                .booleanValue();
        }
        
        values = (Set)attrMap.get(PRIVATE_KEY_TYPE);
        if (values != null && !values.isEmpty()) {
            privateKeyType = (String)values.iterator().next();
        }
        
        values = (Set)attrMap.get(PRIVATE_KEY_ALIAS);
        if (values != null && !values.isEmpty()) {
            privateKeyAlias = (String)values.iterator().next();
        }
        
        values = (Set)attrMap.get(PUBLIC_KEY_ALIAS);
        if (values != null && !values.isEmpty()) {
            publicKeyAlias = (String)values.iterator().next();
        }

    }
    
    public void addTrustSPMetadata(final TrustSPMetadata data, 
                                   final String spEndpoint){
        spMap.put(spEndpoint, data);
    }
    
    public TrustSPMetadata getTrustSPMetadata(final String spEndpoint){

        FAMTrustSPMetadata data = new FAMTrustSPMetadata(spEndpoint);
        spMap.put(spEndpoint, data);

        return (TrustSPMetadata)spMap.get(spEndpoint);
    }
    
    public Set getTrustedIssuers() {
        return trustedIssuers;
    }
    
    public Set getTrustedIPAddresses() {
        return trustedIPAddresses;
    }
    
    public void setType(String type){
        this.type = type;
    } 
    
    public String getType(){
        return this.type;
    }
    
    public void setIssuer(String issuer){
        this.issuer = issuer;
    }
        
    public String getIssuer(){
        return this.issuer;
    }
      
    public void setEncryptIssuedToken(boolean encryptIssuedToken){
        this.encryptIssuedToken = encryptIssuedToken;
    }
    
    public boolean getEncryptIssuedToken(){
        return this.encryptIssuedToken;
    }
        
    public void setEncryptIssuedKey(boolean encryptIssuedKey){
        this.encryptIssuedKey = encryptIssuedKey;
    }
    
    public boolean getEncryptIssuedKey(){
        return this.encryptIssuedKey;
    }
        
    public void setIssuedTokenTimeout(long issuedTokenTimeout){
        this.issuedTokenTimeout = issuedTokenTimeout;
    }
    
    public long getIssuedTokenTimeout(){
        return this.issuedTokenTimeout;
    }
    
    public void setCallbackHandler(CallbackHandler callbackHandler){
        this.callbackHandler = callbackHandler;
    }
    
    public CallbackHandler getCallbackHandler(){
        return new FAMCallbackHandler(this.certAlias);
    }
    
    public void setClientUserTokenClass(String clientUserTokenClass){
        this.clientUserToken = clientUserTokenClass;
    }
        
    public String getClientUserTokenClass(){
        return this.clientUserToken;
    }
    
    /**
     * Returns the list of security mechanims that the STS service is configured.
     *
     * @return list of security mechanisms.
     */
    public List getSecurityMechanisms() {
         return this.secMech;
    }

    /**
     * Sets the list of security mechanisms.
     *
     * @param authMech the list of security mechanisms.
     */
    public void setSecurityMechanisms(List authMech) {
        this.secMech = authMech;
    }
    
    /**
     * Checks if the response needs to be signed or not.
     *
     * @return true if the response needs to be signed.
     */
    public boolean isResponseSignEnabled() {
        return this.isResponseSign;
    }

    /**
     * Sets the response sign enable flag.
     *
     * @param enable enables the response signing.
     */
    public void setResponseSignEnabled(boolean enable) {
         this.isResponseSign = enable;
    }
    
    /**
     * Checks if the response needs to be encrypted or not.
     *
     * @return true if the response needs to be encrypted.
     */
    public boolean isResponseEncryptEnabled() {
        return this.isResponseEncrypt;
    }

    /**
     * Sets the response encrypt enable flag.
     *
     * @param enable enables the response encryption.
     */
    public void setResponseEncryptEnabled(boolean enable) {
         this.isResponseEncrypt = enable;
    }
    
    /**
     * Checks if the request needs to be signed or not.
     *
     * @return true if the request needs to be signed.
     */
    public boolean isRequestSignEnabled() {
        return this.isRequestSign;
    }

    /**
     * Sets the request sign enable flag.
     *
     * @param enable enables the request signing.
     */
    public void setRequestSignEnabled(boolean enable) {
         this.isRequestSign = enable;
    }
    
    /**
     * Checks if the request needs to be encrypted or not.
     *
     * @return true if the request needs to be encrypted.
     */
    public boolean isRequestEncryptEnabled() {
        return this.isRequestEncrypt;
    }

    /**
     * Sets the request encrypt enable flag.
     *
     * @param enable enables the request encryption.
     */
    public void setRequestEncryptEnabled(boolean enable) {
         this.isRequestEncrypt = enable;
    }

    /**
     * Checks if the request header needs to be encrypted or not.
     *
     * @return true if the request header needs to be encrypted.
     */
    public boolean isRequestHeaderEncryptEnabled() {
        return this.isRequestHeaderEncrypt;
    }

    /**
     * Sets the request header encrypt enable flag.
     *
     * @param enable enables the request header encryption.
     */
    public void setRequestHeaderEncryptEnabled(boolean enable) {
        this.isRequestHeaderEncrypt = enable;
    }
    
    /**
     * Returns the key type for the security provider at STS service.
     * 
     * @return the key type of the security provider at STS service.
     */
    public String getPrivateKeyType() {
        return privateKeyType;
    }
   
    /**
     * Sets the key type for the security provider at STS service.
     * 
     * @param keyType the key type for the security provider at STS service.
     */
    public void setPrivateKeyType(String keyType) {
        this.privateKeyType = keyType;
    }

    /**
     * Returns the key alias for the security provider at STS service.
     * 
     * @return the key alias of the security provider at STS service.
     */
    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }
   
    /**
     * Sets the key alias for the security provider at STS service.
     * 
     * @param alias the key alias for the security provider at STS service.
     */
    public void setPrivateKeyAlias(String alias) {
        this.privateKeyAlias = alias;
    }

    /**
     * Returns the Public key alias for this provider's partner.
     * 
     * @return the Public key alias of the provider's partner.
     */
    public String getPublicKeyAlias() {
        return publicKeyAlias;
    }
   
    /**
     * Sets the Public key alias for this provider's partner.
     * 
     * @param alias the Public key alias for this provider's partner.
     */
    public void setPublicKeyAlias(String alias) {
        this.publicKeyAlias = alias;
    }
    
    public Map<String, Object> getOtherOptions(){
        return this.otherOptions;
    }
}

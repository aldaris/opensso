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
 * $Id: FAMSTSConfiguration.java,v 1.2 2007-09-13 07:24:22 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.wss.sts.config;

import com.sun.xml.ws.api.security.trust.config.STSConfiguration;
import com.sun.xml.ws.api.security.trust.config.TrustSPMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    
    private CallbackHandler callbackHandler;
    
    private Map<String, Object> otherOptions = new HashMap<String, Object>();

    static final String CONFIG_NAME = "STS_CONFIG";
    static final String SERVICE_NAME = "sunFAMSTSService";

    static final String ISSUER = "stsIssuer";
    static final String END_POINT = "stsEndPoint";
    static final String ENCRYPT_ISSUED_KEY = "stsEncryptIssuedKey";
    static final String ENCRYPT_ISSUED_TOKEN = "stsEncryptIssuedToken";
    static final String LIFE_TIME = "stsLifetime";
    static final String TOKEN_IMPL_CLASS = "stsTokenImplClass";
    static final String CERT_ALIAS = "stsCertAlias";

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
    
    public Map<String, Object> getOtherOptions(){
        return this.otherOptions;
    }
}

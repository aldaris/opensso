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
 * $Id: ProviderConfig.java,v 1.2 2007-04-06 21:06:42 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.wss.provider; 

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.util.Crypt;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.shared.Constants;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.wss.security.SecurityMechanism;


/**
 * This class <code>ProviderConfig</code> represents the web services
 * server provider or the web services client  provider configuration.  
 *
 * <p>All the static methods in this class are for the persistent 
 * operations.
 * @supported.all.api
 */
public abstract class ProviderConfig {

     /**
      * Constant to define the web services client type.
      */
     public static final String WSC = "WSC";

     /**
      * Constant to define the web services provider type.
      */
     public static final String WSP = "WSP";
     
    /**
     * Property for the web services provider configuration plugin.
     */
    public static final String WSS_PROVIDER_CONFIG_PLUGIN =
         "com.sun.identity.wss.provider.config.plugin";
 
     protected List secMech = null;
     protected String serviceURI = null;
     protected String providerName = null; 
     protected String wspEndpoint = null;
     protected String providerType = null;
     protected KeyStore keyStore = null;
     protected String keyAlias = null;
     protected boolean isResponseSigned = false;
     protected List trustAuthorities = null;
     protected String ksPasswd = null;
     protected String keyPasswd = null;
     protected String ksFile = null;
     protected Properties properties = new Properties();
     protected List usercredentials = null;
     protected String serviceType = null;
     protected boolean isDefaultKeyStore = false;

     private static Class adapterClass;

    /**
     * Returns the list of security mechanims that the provider is configured.
     *
     * @return list of security mechanisms.
     */
    public List getSecurityMechanisms() {
         return secMech;
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
     * Returns the name of the Provider.
     *
     * @return the provider name.
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Returns the value of the property.
     *
     * @return the value of the property.
     */
    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    /**
     * Sets the value for the given property in Provider Configuration.
     *
     * @param property the name of the being set.
     *
     * @param value the property value being set.
     */
    public void setProperty(String property, String value) {
        properties.put(property, value);
    }

    /**
     * Returns the endpoint of the web services provider.
     *
     * @return the endpoint of the web services provider.
     */
    public String getWSPEndpoint() {
        return wspEndpoint;
    }

    /**
     * Sets the web services provider endpoint.
     *
     * @param endpoint the web services provider endpoint.
     */
    public void setWSPEndpoint(String endpoint) {
        this.wspEndpoint = endpoint;
    }

    /**
     * Sets the service type.
     * @param serviceType the service type.
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Returns the service type.
     *
     * @return the service type.
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Sets the user credentials list.
     * @param list of <code>PasswordCredential</code>objects.
     */
    public void setUsers(List usercredentials) {
        this.usercredentials = usercredentials;
    }

    /**
     * Returns the list of <code>PasswordCredential</code>s of the user.
     *
     * @return the list of <code>PasswordCredential</code> objects.
     */
    public List getUsers() {
        return usercredentials;
    }

    /**
     * Returns the provider type. It will be {@link #WSP} or {@link #WSC}
     *
     * @return the provider type.
     */
    public String getProviderType() {
        return providerType;
    }

    /**
     * Returns the provider JKS <code>KeyStore</code> 
     *
     * @return the JKS <code>KeyStore</code>
     */
    public KeyStore getKeyStore() {
        return keyStore;
    }

    /**
     * Returns the keystore file.
     *
     * @return the keystore file name.
     */
    public String getKeyStoreFile() {
        return ksFile;
    }

    /**
     * Returns the keystore password.
     *
     * @return the keystore password.
     */
    public String getKeyStorePassword() {
        return Crypt.decrypt(ksPasswd);
    }

    /**
     * Returns the keystore encrypted password.
     *
     * @return the keystore encrypted password.
     */
    public String getKeyStoreEncryptedPasswd() {
         return ksPasswd;
    }

    /**
     * Returns the key password in the keystore.
     *
     * @return the key password in the keystore.
     */
    public String getKeyPassword() {
        return Crypt.decrypt(keyPasswd);
    }

    /**
     * Returns the keystore encrypted password.
     *
     * @return the keystore encrypted password.
     */
    public String getKeyEncryptedPassword() {
        return keyPasswd;
    }

    /**
     * Sets the keystore for this provider.
     * 
     * @param fileName the provider key store fully qualified file name.
     *
     * @param keyStorePassword the password required to access the key 
     *         store file.
     *
     * @param keyPassword the password required to access the key from the
     *        keystore.
     *
     * @exception ProviderException if the key store file does not exist
     *        or an invalid password. 
     */
    public void setKeyStore(String fileName, 
             String keyStorePassword, String keyPassword)
      throws ProviderException {

        this.ksFile = fileName;
        this.ksPasswd = Crypt.encrypt(keyStorePassword);
        this.keyPasswd = Crypt.encrypt(keyPassword);
        try {
            File file = new File(fileName);
            if(file.exists()) {
               InputStream inputStream = new FileInputStream(fileName);
               keyStore = KeyStore.getInstance("JKS");
               keyStore.load(inputStream, keyStorePassword.toCharArray());
            }
        } catch (Exception ex) {
            ProviderUtils.debug.error("ProviderConfig.setKeyStore: Could not" +
                 "set the key store file information", ex);
            throw new ProviderException(ProviderUtils.bundle.getString(
            "invalidKeyStore"));
        }
    }

    /**
     * Sets the keystore for this provider.
     * 
     * @param keyStore the provider key store.
     *
     * @param password the password required to access the key store file.
     *
     */
    public void setKeyStore(KeyStore keyStore, String password) {
        this.keyStore = keyStore;
        this.ksPasswd = password;
    }
     

    /**
     * Returns the key alias for this provider.
     * 
     * @return the key alias of the provider.
     */
    public String getKeyAlias() {
        return keyAlias;
    }
   
    /**
     * Sets the key alias for this provider.
     * 
     * @param alias the key alias for this provider.
     */
    public void setKeyAlias(String alias) {
        this.keyAlias = alias;
    }

    /**
     * Returns true if the provider uses default keystore.
     * @return true if the provider uses default keystore.
     */
    public boolean useDefaultKeyStore() {
        return isDefaultKeyStore;
    }

    /**
     * Sets the provider to use the default keystore.
     * @param set boolean variable to enable or disable to use the default
     *            keystore.
     */
    public void setDefaultKeyStore(boolean set) {
        this.isDefaultKeyStore = set;
    }

    /**
     * Returns the provider's trusted authorities list.
     *
     * @return the list of the <code>TrustAuthorityConfig</code> s. 
     */
    public List getTrustAuthorityConfigList() {
        return trustAuthorities;
    }

    /**
     * Sets the trusted authority configurations.
     * 
     * @param trustedAuthorities the list of 
     *            <code>TrustAuthorityConfig</code> s. 
     */
    public void setTrustAuthorityConfigList(List trustedAuthorities) {
       trustAuthorities  = trustedAuthorities;
    }

    /**
     * Checks if the response needs to be signed or not.
     *
     * @return true if the response need to be signed.
     */
    public boolean isResponseSignEnabled() {
        return isResponseSigned;
    }

    /**
     * Sets the response sign enable flag.
     *
     * @param enable enable the response signing.
     */
    public void setResponseSignEnabled(boolean enable) {
         isResponseSigned = enable;
    }

    /**
     * Stores the provider configuration
     *
     * @exception ProviderException if there is any failure.
     */
    protected abstract void store() throws ProviderException;

    /**
     * Deletes the provider configuration
     *
     * @exception ProviderException if there is any failure.
     */
    protected abstract void delete() throws ProviderException;

    /**
     * Checks if the provider configuration exists
     *
     * @return true if the provider exists.
     */
     protected abstract boolean isExists();

    /**
     * Initializes the provider.
     *
     * @param providerName the provider name.
     *
     * @param providerType the provider type.
     *
     * @param token Single Sign-on token.
     *
     * @exception ProviderException if there is any failure.
     */
    protected abstract void init(String providerName, 
          String providerType, SSOToken token) throws ProviderException;

    /**
     * Saves the Provider in the configuration repository.
     *
     * @param config the provider configuration.
     *
     * @exception ProviderException if the creation is failed.
     */
    public static void saveProvider(ProviderConfig config)
                  throws ProviderException {
        config.store();
    }

    /**
     * Returns the provider configuration for a given provider name.
     *
     * @param providerName the provider name.
     *
     * @param providerType the provider type.
     *
     * @exception ProviderException if unable to retrieve.
     */
    public static ProviderConfig getProvider(
           String providerName, String providerType) throws ProviderException {

         ProviderConfig pc = getConfigAdapter(); 
         SSOToken adminToken = getAdminToken();
         pc.init(providerName, providerType, adminToken);
         return pc; 
    }

    /**
     * Checks if the provider of given type does exists.
     * 
     * @param providerName the name of the provider.
     *
     * @param providerType type of the provider.
     *
     * @return true if the provider exists with a given name and type.
     */
    public static boolean isProviderExists(String providerName, 
                  String providerType) {
        try {
            ProviderConfig config = getProvider(providerName, providerType);
            return config.isExists();
        } catch (ProviderException pe) {
            ProviderUtils.debug.error("ProviderConfig.isProviderExists:: " +
            "Provider Exception ", pe);
            return false;
        }
    }

    /**
     * Removes the provider configuration.
     * 
     * @param providerName the name of the provider.
     * 
     * @param providerType the type of the provider.
     * 
     * @exception ProviderException if any failure.
     */
    public static void deleteProvider(
           String providerName, String providerType) throws ProviderException {

        ProviderConfig pc = getConfigAdapter();
        pc.init(providerName, providerType, getAdminToken());
        pc.delete();
    }

    /**
     * Returns the list of all available security mechanism objects.
     *
     * @return the list of <code>SecurityMechanism</code> objects.
     */ 
    public static List getAllSupportedSecurityMech() {
        List list = new ArrayList();
        list.add(SecurityMechanism.WSS_NULL_SAML_SV);
        list.add(SecurityMechanism.WSS_TLS_SAML_SV);
        list.add(SecurityMechanism.WSS_CLIENT_TLS_SAML_SV);
        list.add(SecurityMechanism.WSS_NULL_SAML_HK);
        list.add(SecurityMechanism.WSS_TLS_SAML_HK);
        list.add(SecurityMechanism.WSS_CLIENT_TLS_SAML_HK);
        list.add(SecurityMechanism.WSS_NULL_X509_TOKEN);
        list.add(SecurityMechanism.WSS_TLS_X509_TOKEN);
        list.add(SecurityMechanism.WSS_CLIENT_TLS_X509_TOKEN);
        list.add(SecurityMechanism.WSS_NULL_USERNAME_TOKEN);
        list.add(SecurityMechanism.WSS_TLS_USERNAME_TOKEN);
        list.add(SecurityMechanism.WSS_CLIENT_TLS_USERNAME_TOKEN);
        return list;
    }

    /**
     * Returns the list of message level security mechanism objects.
     *
     * @return the list of message level <code>SecurityMechanism</code> objects.
     */
    public static List getAllMessageLevelSecurityMech() {
        List list = new ArrayList();
        list.add(SecurityMechanism.WSS_NULL_SAML_SV);
        list.add(SecurityMechanism.WSS_NULL_SAML_HK);
        list.add(SecurityMechanism.WSS_NULL_X509_TOKEN);
        list.add(SecurityMechanism.WSS_NULL_USERNAME_TOKEN);
        return list;
    }

    private static ProviderConfig getConfigAdapter() throws ProviderException {
        if (adapterClass == null) {
            String adapterName =   SystemProperties.get(
                WSS_PROVIDER_CONFIG_PLUGIN, 
                "com.sun.identity.wss.provider.plugins.AgentProvider");
            try {
                adapterClass = Class.forName(adapterName);
            } catch (Exception ex) {
                 ProviderUtils.debug.error("ProviderConfig.getConfigAdapter: " +
                     "Failed in obtaining class", ex);
                 throw new ProviderException(
                     ProviderUtils.bundle.getString("initializationFailed"));
            }
        }
        try {
            return ((ProviderConfig) adapterClass.newInstance());
        } catch (Exception ex) {
             ProviderUtils.debug.error("ProviderConfig.getConfigAdapter: " +
                 "Failed in initialization", ex);
             throw new ProviderException(
                 ProviderUtils.bundle.getString("initializationFailed"));
        }
    }

    private  static SSOToken getAdminToken() throws ProviderException {
        SSOToken adminToken = null;
        try {
            adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            SSOTokenManager.getInstance().refreshSession(adminToken);
        } catch (SSOException se) {
            ProviderUtils.debug.message("ProviderConfig.getAdminToken:: " +
               "Trying second time ....");
            adminToken = (SSOToken) AccessController.doPrivileged(
               AdminTokenAction.getInstance());
        }
        return adminToken;
    }
}

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
 * $Id: TrustAuthorityConfig.java,v 1.6 2007-11-01 17:25:56 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.provider;

import java.security.AccessController;
import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.common.SystemConfigurationUtil;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.Constants;

/* iPlanet-PUBLIC-CLASS */

/**
 * This is an abstract that represents the configuration about a trusted
 * authority.
 *
 *<p>The trusted authority configuration is used to register the provider
 * configuration information at a trusted authority.
 *
 * <p> This class can be extended to define the trust authority config
 * such as discovery configuration, ws-trust etc.
 * 
 * @supported.all.api
 */
public abstract class TrustAuthorityConfig {

    protected String endpoint;
    protected String keyAlias;
    protected String name;
    protected String type;
    
    private static Class discoveryConfigClass;
    private static Class stsConfigClass;
    private static Debug debug = ProviderUtils.debug;

    /**
     * Property string for the web services discovery configuration plugin.
    */
    public static final String WSS_DISCOVERY_CONFIG_PLUGIN =
        "com.sun.identity.wss.discovery.config.plugin";
    
    /**
     * Property string for the web services sts configuration plugin.
     */
    public static final String WSS_STS_CONFIG_PLUGIN = 
        "com.sun.identity.wss.sts.config.plugin";
 
    /**
     * Discovery service configuration type.
     */
    public static final String DISCOVERY_TRUST_AUTHORITY = "Discovery";
    
    public static final String STS_TRUST_AUTHORITY = "STS";

    /**
     * Returns the trust authority name.
     * @return the name of the trust authority.
     */
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the trust authority type.
     * @return the type of the trust authority.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the trust authority type.
     * @param type the type of the trust authority.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the authority end point.
     *
     * @return the endpoint of the trust authority. 
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Sets the end point.
     *
     * @param endpoint the end point for the trust authority.
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Returns the key alias of the trust authority.
     * 
     * @return the key alias name.
     */
    public String getKeyAlias() {
        return keyAlias;
    }

    /**
     * Sets the key alias for the trust authority.
     *
     * @param keyAlias the key alias for the trust authority.
     */
    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias; 
    }
    
    /**
     * Initialize the trust authority.
     * @param name the name of the trust authority.
     * @param type the type of the trust authority.
     * @param ssoToken Single sign-on token.
     * @exception ProviderException if the initialization fails.
     */
    protected abstract void init(String name, String type, SSOToken ssoToken) 
        throws ProviderException;

    /**
     * Saves the trust authority configuration.
     * @exception ProviderException if the trust authority configuration 
     *            is unable to save.
     */
    protected abstract void store() throws ProviderException;

    /**
     * Deletes the trust authrority configuration.
     * @exception ProviderException
     */
    protected abstract void delete() throws ProviderException;

    /**
     * Returns the trust authority configuration object.
     *
     * @param name the name of the trust authority.
     * @param type the type of the trust authority. The type must have
     *         one of the following values.
     *         <p> {@link #DISCOVERY_TRUST_AUTHORITY}
     *         <p> {@link #STS_TRUST_AUTHORITY}
     * @exception ProviderException if any failure in 
     *                retrieving the trust authority configuration.
     */
    public static TrustAuthorityConfig getConfig(String name, String type)
        throws ProviderException {
        TrustAuthorityConfig config = null;

        if (DISCOVERY_TRUST_AUTHORITY.equals(type)) {
            config = getDiscoveryConfig();
            config.init(name, type, getAdminToken());
        } else  if(STS_TRUST_AUTHORITY.equals(type)) {
            config = getSTSConfig();
            config.init(name, type, getAdminToken());
        }else {
            throw new ProviderException(
               ProviderUtils.bundle.getString("unsupportedConfigType"));
        }
        return config;
    }

    /**
     * Saves the trust authority configuration.
     *
     * @param config the trust authority configuration.
     * @exception ProviderException if any failure in 
     *            saving the configuration.
     */
    public static void saveConfig(TrustAuthorityConfig config) 
        throws ProviderException {
        config.store();
    }

    /**
     * Deletes the trust authority configuration.
     *
     * @param name the name of the trust authority configuration.
     * @param type the type of the trust authority. The type must have
     *         one of the values.
     *         <p> {@link #DISCOVERY_TRUST_AUTHORITY}
     *
     * @exception ProviderException if any failure in 
     *            deleting the trust authority configuration.
     */
    public static void deleteConfig(String name, String type ) 
        throws ProviderException {
        TrustAuthorityConfig config = getConfig(name, type);
        config.delete();
    }

    private static DiscoveryConfig getDiscoveryConfig() 
        throws ProviderException {
        if (discoveryConfigClass == null) {
            String adapterName = SystemConfigurationUtil.getProperty(
                WSS_DISCOVERY_CONFIG_PLUGIN, 
                "com.sun.identity.wss.provider.plugins.DiscoveryAgent");
            try {
                discoveryConfigClass = Class.forName(adapterName);
            }  catch (Exception ex) {
                debug.error("TrustAuthorityConfig.getDiscoveryConfig: " +
                    " Failed in creating the discovery config class.");
                throw new ProviderException(ex.getMessage());
            }
        }
        try {
            return ((DiscoveryConfig) discoveryConfigClass.newInstance());
        } catch (Exception ex) {
            debug.error("TrustAuthorityConfig.getDiscoveryConfig: " +
                "Failed in initialization", ex);
            throw new ProviderException(ex.getMessage());
        }
    }
    
    private static STSConfig getSTSConfig() throws ProviderException {
        if (stsConfigClass == null) {
            String adapterName = SystemConfigurationUtil.getProperty(
                WSS_STS_CONFIG_PLUGIN, 
                "com.sun.identity.wss.provider.plugins.STSAgent");
            try {
                stsConfigClass = Class.forName(adapterName);
            }  catch (Exception ex) {
                debug.error("TrustAuthorityConfig.getSTSConfig: " +
                    " Failed in creating the STS config class.");
                throw new ProviderException(ex.getMessage());
            }
        }
        
        try {
            return ((STSConfig) stsConfigClass.newInstance());
        } catch (Exception ex) {
            debug.error("TrustAuthorityConfig.getSTSConfig: " +
                "Failed in initialization", ex);
            throw new ProviderException(ex.getMessage());
        }
    }

    private static SSOToken getAdminToken() throws ProviderException {
        SSOToken adminToken =  null;
        try {
            adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            SSOTokenManager.getInstance().refreshSession(adminToken);
        } catch (SSOException se) {
            ProviderUtils.debug.message(
                "TrustAuthorityConfig.getAdminToken: Trying second time...");
            adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        }
        return adminToken;
    }
}

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
 * $Id: AgentConfiguration.java,v 1.1 2006-09-28 23:21:03 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.arch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;


/**
 * <p>
 * Provides access to the configuration as set in the system. Underneath the
 * covers, the <code>AgentConfiguration</code> checks to see if the agent has
 * been deployed on a system where Access Manager or one of its components such
 * has FM has already been deployed. If so, the configuration file used would
 * be the applicable Access Manager configuration such as AMConfig.properties.
 * </p><p>
 * If the agent has been deployed on a system that does not have Access Manager
 * or any of its components such as FM, it uses the configuration file called
 * AMAgent.properties which it expects to find in the system classpath. Any 
 * properties necessary for the Access Manager's client SDK that is bundled 
 * with the agent are set by this class as system properties which in turn are
 * picked up by the client SDK classes where necessary. 
 * </p><p>
 * Configuration updates are available if the configuration setting 
 * <code>com.sun.identity.agents.j2ee.config.load.interval</code> has been set
 * to a non-zero positive value indicating the number of seconds after which
 * the system will poll to identify configuration changes. If this value is set
 * to zero, the configuration reloads will be disabled. Every time a 
 * configuration reload occurs, the value of the configuration setting that 
 * governs the reload interval is also recalculated, thereby making it possible
 * to dynamically disable reloads by setting the value to zero when reloads are
 * active. Note that in the event of a reload, only the configuration keys that
 * are designated for Agent operation are reloaded. These keys begin with the
 * <code>com.sun.identity.agents.config</code> prefix. The keys that do
 * not match this criteria are kept unchanged and reflect the values that were
 * present during system initialization. Certain keys which meet this crieteria
 * would still not be reloaded due to potential security concerns associated
 * with the swapping of the associated values.
 * </p><p>
 * General access methods in this class are package protected and thus cannot 
 * be directly invoked by classes that are not within the same package. It is
 * expected and required that all configuration access be done via a designated 
 * <code>Manager</code> of the subsystem which in turn acts as an intermediate
 * caching point for configuration values. Public methods are available for
 * configuration settings that are considered core settings.
 * </p>
 * 
 * @see com.sun.identity.agents.arch.Manager
 * @see com.sun.identity.agents.arch.IConfigurationListener
 */
public class AgentConfiguration implements 
        IAgentConfigurationConstants, 
        IConfigurationKeyConstants, 
        IClientConfigurationKeyConstants,
        IConfigurationDefaultValueConstants
{
    
   /**
    * A constant value used to identify the default web application context
    * for configuration settings that are application specific.
    */
    public static final String DEFAULT_WEB_APPLICATION_NAME = "DefaultWebApp";
    
   /**
    * Returns a header name that contains the client IP address. If no header
    * name is specified in the configuration file, this method will return
    * <code>null</code>.
    * 
    * @return a header name that contains client IP address or <code>null</code>
    * if no header name is specified.
    */
    public static String getClientIPAddressHeader() {
        return _clientIPAddressHeader;
    }
    
   /**
    * Returns a header name that contains the client hostname. If no header
    * name is specified in the configuration file, this method will return
    * <code>null</code>.
    * 
    * @return a header name that contains client hostname or <code>null</code>
    * if no header name is specified.
    */
    public static String getClientHostNameHeader() {
        return _clientHostNameHeader;
    }
    
   /**
    * Returns the name of the organization that can be used for authenticating
    * the Agent services. This value represents the organization name or the
    * realm name to which the Agent profile belongs.
    * 
    * @return the organization or realm name to which the Agent profile belongs.
    */
    public static String getOrganizationName() {
        return _organizationName;
    }    
    
   /**
    * Returns a boolean indicating if the notifications for Policy changes
    * have been set as enabled.
    * @return <code>true</code> if notifications for Policy are enabled, 
    * <code>false</code> otherwise.
    */
    public static boolean isPolicyNotificationEnabled() {
        return _policyNotificationEnabledFlag;
    }
    
   /**
    * Returns the URL that will be used by the Server to send policy 
    * notifications.
    * @return the policy notification URL.
    */
    public static String getPolicyNotificationURL() {
        return _policyNotificationURL;
    }
    
   /**
    * Returns a boolean indicating if the notificatiosn for Sessino changes have
    * been set as enabled.
    * 
    * @return <code>true</code> if notifications for Session are enabled, 
    * <code>false</code> otherwise.
    */
    public static boolean isSessionNotificationEnabled() {
        return _sessionNotificationEnabledFlag;
    }
    
   /**
    * Returns the URL that will be used by the Server to send session
    * notifications.
    * 
    * @return the session notification URL.
    */
    public static String getSessionNotificationURL() {
        return _sessionNotificationURL;
    }
    
   /**
    * Returns the name of the Access Manager Session property that is used by
    * the Agent runtime to identify the user-id of the current user.
    * 
    * @return the Session property name that identifies the user-id of the 
    * current user.
    */
    public static String getUserIdPropertyName() {
        return _userIdPropertyName;
    }
    
   /**
    * A method that ensures that any class that directly depends
    * upon Client SDK can first initialize the <code>AgentConfiguration</code>.
    * Failing to initialize the <code>AgentConfiguration</code> can result
    * in the malfunction of the Client SDK due to configuration dependancies.
    */
    public static void initialize() {
        // No processing requried
    }
    
   /**
    * Returns the name of the cookie or URI parameter that holds the users
    * SSO token.
    * 
    * @return the SSO token cookie or parameter name.
    */
    public static synchronized String getSSOTokenName() {
        return _ssoTokenCookieName;
    }
     
   /**
    * Returns the <code>ServiceResolver</code> instance associated with the
    * Agent runtime.
    * @return the configured <code>ServiceResolver</code>.
    */
    public static ServiceResolver getServiceResolver() {
        return _serviceResolver;
    }
    
   /**
    * Returns the <code>UserMapingMode</code> configured in the system. This
    * setting is not hot-swappable and is initialized during system 
    * initialization and never changed thereafter.
    * 
    * @return the configured <code>UserMappingMode</code>.
    */
    public static UserMappingMode getUserMappingMode() {
        return _userMappingMode;
    }
    
   /**
    * Returns the <code>AuditLogMode</code> configured in the system. This
    * setting is not hot-swappable and is initialized during system
    * initialization and never changed thereafeter.
    * 
    * @return the configured <code>AuditLogMode</code>.
    */
    public static AuditLogMode getAuditLogMode() {
        return _auditLogMode;
    }
     
   /**
    * Returns the user attribute value configured in the system. This setting
    * is not hot-swappable and is initialized during system initialization and
    * never changed thereafter.
    * 
    * @return the configured user attribute value.
    */
    public static String getUserAttributeName() {
        return _userAttributeName;
    }  
    
   /**
    * Returns <code>true</code> if the runtime is configured to use the user's
    * <code>DN</code> instead of the regular <code>userid</code> for 
    * identification purposes. This setting is not hot-swappable and is 
    * initialized during system initialization and never changed thereafter.
    * 
    * @return <code>true</code> if the system is configured to use the user's
    * <code>DN</code> for identification purposes, <code>false</code> otherwise.
    */
    public static boolean isUserPrincipalEnabled() {
        return _userPrincipalEnabled;
    }    
    
   /**
    * Allows other parts of the subsystem to register for configuration
    * change events by registering the specified 
    * <code>IConfigurationListener</code>.
    * 
    * @param listener the <code>IConfigurationListener</code> to be registered.
    */
    public static void addConfigurationListener(
            IConfigurationListener listener) {

        if(isLogMessageEnabled()) {
            logMessage("AgentConfiguration: Adding listener for : "
                       + listener.getName());
        }

        Vector configurationListeners = getModuleConfigurationListeners();

        synchronized(configurationListeners) {
            configurationListeners.add(listener);
        }
    }
    
    /**
     * Returns the application user name to be used to identify the Agent
     * runtime.
     * 
     * @return the application user name.
     */
     public static String getApplicationUser() {
         return _applicationUser;
     }
     
    /**
     * Returns the application password to be used to identify the Agent
     * runtime.
     * 
     * @return the application password.
     */
     public static String getApplicationPassword() {
         return _applicationPassword;
     }    
     
    /**
     * Returns the anonymous user name.
     * 
     * @return the user name for anonymous user.
     */
     public static String getAnonymousUserName() {
         return _anonymousUserName;
     }
    
   /**
    * Returns the configuration value corresponding to the specified 
    * <code>id</code> or the supplied <code>defaultValue</code> if not 
    * present.
    * 
    * @param id the configuration key to be looked up.
    * @param defaultValue the default value to be used in case no configuration
    * is specified for the given <code>id</code>.
    * 
    * @return the associated configuration value with the specified 
    * <code>id</code> or the <code>defaultValue</code> if no value is specified
    * in the configuration.
    */
    static String getProperty(String id, String defaultValue) {
        String value = getProperty(id);
        if (value == null) {
            value = defaultValue;
        }
        
        return value;
    }
    
   /**
    * Returns the configuration value corresponding to the specified 
    * <code>id</code> or <code>null</code> if no value is present.
    * 
    * @param id the configuration key to be looked up.
    * 
    * @return the associated configuration value with the specified
    * <code>id</code> or <code>null</code> if no value is specified in the
    * configuration.
    */
    static String getProperty(String id) {
        String result = null;
        String value = System.getProperty(id);
        if (value == null) {
            value = getPropertyInternal(id);
        }
        
        if (value != null && value.trim().length() > 0) {
            result = value.trim();
        }
        
        return result;
    }
    
   /**
    * Returns a <code>Properties</code> instance that holds all the available
    * configuration as available in the system.
    * 
    * @return the configuration as a <code>Properties</code> instance.
    */
    static Properties getAll() {
        Properties result = getAllInternal();
        Iterator it = System.getProperties().keySet().iterator();
        while (it.hasNext()) {
            String nextKey = (String) it.next();
            result.put(nextKey, System.getProperty(nextKey));
        }
        return result;
    }
        
    private synchronized static String getPropertyInternal(String id) {
        return getProperties().getProperty(id);
    }
    
    private synchronized static Properties getAllInternal() {
        Properties properties = new Properties();
        properties.putAll(getProperties());
        return properties;
    }    
    
    private static synchronized void setConfigurationFilePath() {
        if (!isInitialized()) {
            String result = null;
            if (usePrimaryConfiguration()) {
                URL resUrl = ClassLoader.getSystemResource(CONFIG_FILE_NAME);
                    if(resUrl == null) {
                        ClassLoader cl = 
                            Thread.currentThread().getContextClassLoader();
                        if(cl != null) {
                            resUrl = cl.getResource(CONFIG_FILE_NAME);
                        }
                    }
            
                    if (resUrl == null) {
                        throw new RuntimeException(
                                "Failed to get configuration file:"
                                 + CONFIG_FILE_NAME);
                    }
                    result = resUrl.getPath();                     
            } else {
                String configFilePath = SystemProperties.get(
                        SDKPROP_AMAGENT_LOCATION);
                if (configFilePath == null 
                        || configFilePath.trim().length() == 0) 
                {
                    throw new RuntimeException(
                            "Failed to load secondary configuration: "
                            + "No value specified for: "
                            + SDKPROP_AMAGENT_LOCATION);
                }
                
                File configFile = new File(configFilePath);
                if (!configFile.exists() || !configFile.isFile() 
                        || !configFile.canRead()) 
                {
                    throw new RuntimeException(
                            "Failed to load secondary configuration: " 
                            + "Invalid value specified for: "
                            + SDKPROP_AMAGENT_LOCATION + ", value: "
                            + configFilePath);
                }
                result = configFilePath;
            }
            if (result == null) {
                throw new RuntimeException(
                        "Failed to load secondary configuration");
            }
            setConfigFilePath(result);
        }
    }
    
    private static Properties getPropertiesFromConfigFile() 
    throws Exception {
        Properties result = new Properties();
        BufferedInputStream instream = null;
        try {
            instream = new BufferedInputStream(
                    new FileInputStream(getConfigFilePath()));
            result.load(instream);
        } catch (Exception ex) {
            throw ex;
        } finally {
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (Exception ex) {
                        // No handling required
                    }
                }
        }
        
        return result;
    }
    
    private static synchronized void bootStrapClientConfiguration() {
        if (!isInitialized()) {
            HashMap sysPropertyMap = null;
            setConfigurationFilePath();
                try {
                sysPropertyMap = new HashMap();
                Properties properties = getProperties();
                properties.clear();
                properties.putAll(getPropertiesFromConfigFile());

                if (usePrimaryConfiguration()) {
                    Iterator it = properties.keySet().iterator();
                    while (it.hasNext()) {
                        String nextKey = (String) it.next();
                        if (!nextKey.startsWith(AGENT_CONFIG_PREFIX)) {
                            String nextValue = 
                                    getProperties().getProperty(nextKey);
                            System.setProperty(nextKey, nextValue);
                            sysPropertyMap.put(nextKey, nextValue);
                        }
                    }
                } else {
                    properties.putAll(getAllSystemProperties());
                }

                String modIntervalString = getProperty(CONFIG_LOAD_INTERVAL);
                try {
                    long modInterval = Long.parseLong(modIntervalString);
                    setModInterval(modInterval * 1000L);
                } catch (NumberFormatException nfex) {
                    System.err.println(
                          "AgentConfiguration: Exception while reading "
                           + "new mod interval: \"" + modIntervalString + "\"");
                }
                markCurrent();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                throw new RuntimeException("Failed to load configuration: "
                        + ex.getMessage());
            }
        
                // Initilaize the Debug Engine
                setDebug(Debug.getInstance(IBaseModuleConstants.BASE_RESOURCE));
                if (isLogMessageEnabled()) {
                    logMessage("AgentConfiguration: Using " 
                    + (usePrimaryConfiguration()?"primary":"secondary")
                    + " configuration.");
            
                    if (sysPropertyMap != null) {
                        logMessage(
                            "AgentConfiguration: The following properties "
                            + "were added to system: " + sysPropertyMap);
                    } else {
                        logMessage(
                            "AgentConfiguration: No properties were added "
                            + " to system.");
                    }
            
                    if (usePrimaryConfiguration()) {
                        logMessage(
                            "AgentConfiguration: Mod Interval is set to: "
                            + getModInterval() + " ms.");
                    }
                }
        
                //Start the Configuration Monitor if necessary
                if (getModInterval() > 0L) {
                    Thread monitorThread = new Thread(
                            new ConfigurationMonitor(), "AgentConfigMonitor");
                    monitorThread.setDaemon(true);
                    monitorThread.setPriority(Thread.MIN_PRIORITY);
                    monitorThread.start();
                }
        }
    }
    
    private static synchronized void setConfigurationDisposition() {
        if (!isInitialized()) {
            String debugLevel = SystemProperties.get(SDKPROP_DEBUG_LEVEL);
                if (debugLevel == null) {
                    markUsePrimaryConfiguration();
                } else {
                    markUseSecondaryConfiguration();
                    getAllSystemProperties().putAll(SystemProperties.getAll());
                }
        }
    }
    
    private static synchronized void setServiceResolver() {
        if (!isInitialized()) {
            String serviceResolverClassName =
                    getProperty(CONFIG_SERVICE_RESOLVER);        
                try {
                    if (isLogMessageEnabled()) {
                        logMessage(
                            "AgentConfiguration: service resolver set to: "
                            + serviceResolverClassName);
                    }
                    _serviceResolver = (ServiceResolver) Class.forName(
                    serviceResolverClassName).newInstance();
                    
                    if (isLogMessageEnabled()) {
                        logMessage(
                               "AgentConfiguration: service resolver reports "
                               + "IDM available: " 
                               + _serviceResolver.isIDMAvailable());
                        logMessage(
                               "AgentConfiguration: service resolver reports "
                               + "EJBContext available: " 
                               + _serviceResolver.isEJBContextAvailable());
                    }
                } catch (Exception ex) {
                    logError(
                        "AgentConfiguration: Failed to set Service Resolver: "
                        + serviceResolverClassName, ex);
                    throw new RuntimeException(
                        "Failed to set Service Resolver: "
                        + serviceResolverClassName + ": "
                        + ex.getMessage());
                }
        }
    }
    
    private static synchronized void setUserMappingMode() {
        if (!isInitialized()) {
            UserMappingMode mode = UserMappingMode.get(
                getProperty(CONFIG_USER_MAPPING_MODE));
        
                if (mode == null) {
                    throw new RuntimeException("Unknown User Mapping Mode: "
                    + getProperty(CONFIG_USER_MAPPING_MODE));
                }
                _userMappingMode = mode;
        
                if (isLogMessageEnabled()) {        
                    logMessage("AgentConfiguration: User Mapping mode set to: " 
                    + _userMappingMode);
                }
        }
    }
    
    private static synchronized void setAuditLogMode() {
        if (!isInitialized()) {
            AuditLogMode mode = AuditLogMode.get(
                    getProperty(CONFIG_AUDIT_LOG_MODE));
        
                if (mode == null) {
                    throw new RuntimeException("Unknown Audit Log Mode: " 
                    + getProperty(CONFIG_AUDIT_LOG_MODE));
                }
                _auditLogMode = mode;
                if (isLogMessageEnabled()) {
                    logMessage("AgentConfiguration: Audit Log mode set to: "
                    + _auditLogMode);
                }
        }
    }
    
    private static synchronized void setAnonymousUserName() {
        if (!isInitialized()) {
            String name = getProperty(CONFIG_ANONYMOUS_USER_NAME);
                if (name == null || name.trim().length() == 0) {
                    name = DEFAULT_ANONYMOUS_USER_NAME;
                    if (isLogWarningEnabled()) {
                        logWarning(
                            "AgentConfiguration: No name specified for user "
                            +"anonymous. Using default: " + name);
                    }
                }
        
                _anonymousUserName = name;
                if (isLogMessageEnabled()) {
                    logMessage(
                        "AgentConfiguration: Anonymous user name set to: "
                        + _anonymousUserName);
                }
        }
    }
    
    private static synchronized void setUserAttributeName() {
        if (!isInitialized()) {
            String userAttributeName = getProperty(CONFIG_USER_ATTRIBUTE_NAME);
                if (userAttributeName == null || 
                userAttributeName.trim().length() == 0) 
                {
                    userAttributeName = DEFAULT_USER_ATTRIBUTE_NAME;
                    logError(
                        "AgentConfiguation: Unable to load user attribute name,"
                        + "Using default value: " + userAttributeName);
                }
                _userAttributeName = userAttributeName;
        
                if (isLogMessageEnabled()) {
                    logMessage(
                        "AgentConfiguration: User attribute name set to: "
                        + _userAttributeName);
                }
        }
    }
    
    private static synchronized void setUserPrincipalEnabledFlag() {
        if (!isInitialized()) {
            String userPrinsipalFlagString = getProperty(CONFIG_USER_PRINCIPAL);
        
                if (userPrinsipalFlagString == null || 
                        userPrinsipalFlagString.trim().length() == 0) {
                    userPrinsipalFlagString = DEFAULT_USE_DN;
                }
        
                _userPrincipalEnabled = (new Boolean(
                        userPrinsipalFlagString)).booleanValue();
        
                if (isLogMessageEnabled()) {
                    logMessage("AgentConfiguration: use-DN is set to: " 
                            + _userPrincipalEnabled);
                }
        }
    }
    
    private static synchronized void setSSOTokenName() {
        if (!isInitialized()) {
            _ssoTokenCookieName = getProperty(SDKPROP_SSO_COOKIE_NAME);
            if (_ssoTokenCookieName == null || 
                    _ssoTokenCookieName.trim().length() == 0) {
                throw new RuntimeException("Invalid SSO Cookie name set");
            }
        
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: SSO Token name set to: "
                        + _ssoTokenCookieName);
            }
        }
    }
    
    private static synchronized void setApplicationUser() {
        if (!isInitialized()) {
            _applicationUser = getProperty(SDKPROP_APP_USERNAME);
            if (_applicationUser == null || _applicationUser.trim().length()==0)
            {
                throw new RuntimeException(
                        "Invalid application user specified");
            }
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Application User: "
                        + _applicationUser);
            }
        }
    }
    
    private static synchronized void setApplicationPassword() {
            
        if (!isInitialized()) {
            try {
                    _crypt = ServiceFactory.getCryptProvider();
                if(_crypt != null) {
                    String encodedPass = getProperty(SDKPROP_APP_PASSWORD);
                    _applicationPassword = 
                        _crypt.decrypt(encodedPass);
                }
            } catch (Exception ex) {
                logError("AgentConfiguration: Unable to create new instance of "
                    + "Crypt class with exception ", ex);
            }

            if (_applicationPassword == null || 
                _applicationPassword.trim().length() == 0) {
                throw new RuntimeException(
                        "Invalid application password specified");
            }
        }
    }
    
    private static synchronized void setUserIdPropertyName() {
        if (!isInitialized()) {
            String propertyName = getProperty(CONFIG_USER_ID_PROPERTY);
                if (propertyName == null || propertyName.trim().length() == 0) {
                    propertyName = DEFAULT_USER_ID_PROPERTY;
                    if (isLogWarningEnabled()) {
                        logWarning(
                            "AgentConfiguration: No value specified for user"
                            +" property name. Using default: " + propertyName);
                    }
                }
                _userIdPropertyName = propertyName;
        
                if (isLogMessageEnabled()) {
                    logMessage("AgentConfiguration: User property name set to: "
                    + _userIdPropertyName);
                }
        }
    }
    
    private static synchronized void setSessionNotificationURL() {
        if (!isInitialized()) {
            String url = getProperty(SDKPROP_SESSION_NOTIFICATION_URL);
            if (url != null && url.trim().length() > 0) {
                _sessionNotificationURL = url;
            } else {
                if (isLogWarningEnabled()) {
                    logWarning(
                         "AgentConfiguration: No session notification URL set");
                }
            }
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Session notification URL: "
                        + _sessionNotificationURL);
            }
        }
    }
    
    private static synchronized void setSessionNotificationEnabledFlag() {
        if (!isInitialized()) {
            _sessionNotificationEnabledFlag = true;
            boolean pollingEnabled = false;
            String flag = getProperty(SDKPROP_SESSION_POLLING_ENABLE);
            if (flag != null && flag.trim().length() > 0) {
                pollingEnabled = (new Boolean(flag)).booleanValue();
            }
            
            _sessionNotificationEnabledFlag = !pollingEnabled;
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Session notification enable: " 
                        + _sessionNotificationEnabledFlag);
            }
        }        
    }
    
    private static synchronized void setPolicyNotificationURL() {
        if (!isInitialized()) {
            String url = getProperty(SDKPROP_POLICY_NOTIFICATION_URL);
            if (url != null && url.trim().length() > 0) {
                _policyNotificationURL = url;
            } else {
                if (isLogWarningEnabled()) {
                    logWarning(
                         "AgentConfiguration: No policy notification URL set");
                }
            }
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Policy notification URL: "
                        + _policyNotificationURL);
            }
        }
    }
    
    private static synchronized void setPolicyNotificationEnabledFlag() {
        if (!isInitialized()) {
            boolean enable = false;
            String flag = getProperty(SDKPROP_POLICY_NOTIFICATION_ENABLE);
            if (flag != null && flag.trim().length() > 0) {
                enable = (new Boolean(flag)).booleanValue();
            }
            
            _policyNotificationEnabledFlag = enable;
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Policy notification enable: " 
                        + _policyNotificationEnabledFlag);
            }
        }
    }
    
    private static synchronized void setClientIPAddressHeader() {
        if (!isInitialized()) {
            _clientIPAddressHeader = getProperty(CONFIG_CLIENT_IP_HEADER);
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Client IP Address Header: "
                        + _clientIPAddressHeader);
            }
        }
    }
    
    private static synchronized void setClientHostNameHeader() {
        if (!isInitialized()) {
            _clientHostNameHeader = getProperty(CONFIG_CLIENT_HOSTNAME_HEADER);
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Client Hostname Header: "
                        + _clientHostNameHeader);
            }
        }
    }
    
    private static synchronized void setOrganizationName() {
        if (!isInitialized()) {
            _organizationName = getProperty(CONFIG_ORG_NAME);
            if (_organizationName == null || 
                    _organizationName.trim().length() == 0) {
                logError("AgentConfiguration: Empty/missing organization name");
                throw new RuntimeException(
                        "Invalid organization name specified");
            }
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Organization name set to: "
                        + _organizationName);
            }
        }
    }
    
    private static synchronized void initializeConfiguration() {
        if (!isInitialized()) {
            setConfigurationDisposition();
            bootStrapClientConfiguration();
            setServiceResolver();
            setUserMappingMode();
            setAuditLogMode();
            setUserAttributeName();
            setUserPrincipalEnabledFlag();
            setSSOTokenName();
            setApplicationUser();
            setApplicationPassword();
            setUserIdPropertyName();
            setAnonymousUserName();
            setSessionNotificationURL();
            setSessionNotificationEnabledFlag();
            setPolicyNotificationURL();
            setPolicyNotificationEnabledFlag();
            setClientIPAddressHeader();
            setClientHostNameHeader();
            setOrganizationName();
            
            markInitialized();
        }
    }
    
    private static void logMessage(String msg) {
        getDebug().message(msg);
    }
    
    private static void logMessage(String msg, Throwable th) {
        getDebug().message(msg, th);
    }
    
    private static void logWarning(String msg) {
        getDebug().warning(msg);
    }
    
    private static void logWarning(String msg, Throwable th) {
        getDebug().warning(msg, th);
    }
    
    private static void logError(String msg) {
        getDebug().error(msg);
    }
    
    private static void logError(String msg, Throwable th) {
        getDebug().error(msg, th);
    }
    
    private static boolean isLogWarningEnabled() {
        return getDebug().warningEnabled();
    }
    
    private static boolean isLogMessageEnabled() {
        return getDebug().messageEnabled();
    }    
    
    private static void updateProperties() {
        if(needToRefresh()) {
            File configFile = new File(getConfigFilePath());
            if(getLastLoadTime() > configFile.lastModified()) {
                markCurrent();
            } else {
                if (loadProperties()) {
                    notifyModuleConfigurationListeners();
                }
            }
        }
    }
    
    private static void notifyModuleConfigurationListeners() {

        Vector configurationListeners = getModuleConfigurationListeners();
        if (isLogMessageEnabled()) {
            logMessage("AgentConfiguration: Notifying all listeners");
        }

        synchronized(configurationListeners) {
            for(int i = 0; i < configurationListeners.size(); i++) {
                IConfigurationListener nextListener =
                    (IConfigurationListener) configurationListeners.get(i);

                nextListener.configurationChanged();

                if(isLogMessageEnabled()) {
                    logMessage("AgentConfiguration: Notified listener for "
                               + nextListener.getName());
                }
            }
        }
    }    
    
    private synchronized static boolean loadProperties() {
        boolean result = false;
        try {
            Properties properties = new Properties();
            properties.clear();
            properties.putAll(getPropertiesFromConfigFile());
            if (useSecondaryConfiguration()) {
                properties.putAll(getAllSystemProperties());
            }
            String modIntervalString = properties.getProperty(
                    CONFIG_LOAD_INTERVAL);
            if (modIntervalString != null && 
                    modIntervalString.trim().length()>0) 
            {
                modIntervalString = modIntervalString.trim();
            } else {
                logWarning("AgentConfiguration: No mod interval setting found");
                modIntervalString = "0";
            }
            long modInterval = 0L;
            try {
                modInterval = Long.parseLong(modIntervalString);
            } catch (NumberFormatException nfex) {
                logWarning("AgentConfiguration: Exception while reading "
                        + "new mod interval: \"" + modIntervalString + "\"");
            }
            setModInterval(modInterval*1000L);
            getProperties().clear();
            getProperties().putAll(properties);
            markCurrent();
            result = true;            
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: loaded new configuration.");
            }
        } catch (Exception ex) {
            result = false;
            logError("AgentConfiguration: Exception during reload:", ex);
            logError("AgentConfiguration: Setting reload interval to 0");
            setModInterval(0L);
        }
        return result;
    }

    private static boolean needToRefresh() {
        return((System.currentTimeMillis() - getLastLoadTime())
               >= getModInterval());
    }
    
    
    private static class ConfigurationMonitor implements Runnable {

        public ConfigurationMonitor() {

            if(isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Monitor initialized");
            }
        }

        public void run() {

            if(isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Monitor started");
            }

            while(getModInterval() > 0L) {
                updateProperties();

                try {
                    Thread.sleep(getModInterval());
                } catch(InterruptedException ex) {
                    if(isLogMessageEnabled()) {
                        logMessage("AgentConfiguration: Monitor interrupted",
                                   ex);
                    }
                }
            }

            if(isLogMessageEnabled()) {
                logMessage("AgentConfiguration: Monitor is exiting");
            }
        }
    }    
    
    private static boolean isInitialized() {
        return _initialized;
    }
    
    private static void markInitialized() {
        if (!isInitialized()) {
            _initialized = true;
            if (isLogMessageEnabled()) {
                logMessage("AgentConfiguration: initialized.");
            }
        }
    }
    
    private static boolean usePrimaryConfiguration() {
        return _usePrimaryConfiguration;
    }
    
    private static boolean useSecondaryConfiguration() {
        return !usePrimaryConfiguration();
    }
    
    private static void markUsePrimaryConfiguration() {
        _usePrimaryConfiguration = true;
    }
    
    private static void markUseSecondaryConfiguration() {
        _usePrimaryConfiguration = false;
    }
    
    private static String getConfigFilePath() {
        return _configFilePath;
    }
    
    private static void setConfigFilePath(String configFilePath) {
        _configFilePath = configFilePath;
    }
    
    private static Properties getProperties() {
        return _properties;
    }
    
    private static void setDebug(Debug debug) {
        _debug = debug;
    }
    
    private static Debug getDebug() {
        return _debug;
    }  
    
    private static void setModInterval(long modInterval) {
        _modInterval = modInterval;
    }
    
    private static long getModInterval() {
        return _modInterval;
    }
    
    private static long getLastLoadTime() {
        return _lastLoadTime;
    }
    
    private static void markCurrent() {
        _lastLoadTime = System.currentTimeMillis();
    }    
    
    private static Vector getModuleConfigurationListeners() {
        return _moduleConfigListeners;
    }
    
    private static Properties getAllSystemProperties() {
        return _systemProperties;
    }
   
        
    private static boolean _usePrimaryConfiguration;
    private static boolean _initialized;
    private static String _configFilePath;
    private static Properties _properties = new Properties();
    private static Debug _debug;
    private static long _modInterval = 0L;
    private static long _lastLoadTime = 0L;
    private static Vector _moduleConfigListeners = new Vector();
    private static ServiceResolver _serviceResolver;
    private static UserMappingMode _userMappingMode = 
        UserMappingMode.MODE_USER_ID;
    private static String _userAttributeName;
    private static boolean _userPrincipalEnabled;
    private static String _ssoTokenCookieName;
    private static String _applicationUser;
    private static String _applicationPassword;
    private static String _userIdPropertyName;
    private static AuditLogMode _auditLogMode = AuditLogMode.MODE_BOTH;
    private static String _anonymousUserName;
    private static String _sessionNotificationURL;
    private static String _policyNotificationURL;
    private static boolean _policyNotificationEnabledFlag;
    private static boolean _sessionNotificationEnabledFlag;
    private static String _clientIPAddressHeader;
    private static String _clientHostNameHeader;
    private static String _organizationName;
    private static ICrypt _crypt;
    private static Properties _systemProperties = new Properties();
    
    static {
        initializeConfiguration();
    }
}

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
 * $Id: AmSDKRealm.java,v 1.2 2007-09-12 23:31:59 sean_brydon Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.realm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;

import com.iplanet.am.sdk.AMStoreConnection;
import com.iplanet.am.sdk.AMUser;
import com.iplanet.sso.SSOToken;
import com.sun.identity.agents.arch.AgentBase;
import com.sun.identity.agents.arch.AgentConfiguration;
import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.ISharedConfigurationKeyConstants;
import com.sun.identity.agents.arch.Manager;
import com.sun.identity.agents.arch.ServiceResolver;
import com.sun.identity.agents.common.CommonFactory;
import com.sun.identity.agents.common.ISSOTokenValidator;
import com.sun.identity.agents.common.SSOValidationResult;
import com.sun.identity.agents.util.TransportToken;


/**
 * The agent realm implementation that supports Access manager 6.3
 */
public class AmSDKRealm extends AmRealmBase implements IAmRealm {
    
    public AmSDKRealm(Manager manager) {
        super(manager);
    }
    
    public void initialize() throws AgentException {
        super.initialize();
        if (isLogMessageEnabled()) {
            logMessage("AmSDKRealm: Using AM-SDK APIs");
        }
        initBypassPrincipalList();
        initGlobalVerificationHandler();
        initDefaultPrivilegedAttributeList();
        initSessionPrivilegedAttributeList();
        initSSOTokenValidator();
        initFilteredRolesEnableFlag();
        initPrivilegedAttributeMappingEnableFlag();
        if (isPrivilegedAttributeMappingEnabled()) {
            initPrivilegedAttributeMap();
        }
        if (isLogMessageEnabled()) {
            logMessage("AmSDKRealm: Initialized.");
        }
    }
    
    public AmRealmAuthenticationResult authenticate(
            SSOValidationResult ssoValidationResult) {
        AmRealmAuthenticationResult result = AmRealmAuthenticationResult.FAILED;
        if (ssoValidationResult != null && ssoValidationResult.isValid()) {
            String userName = ssoValidationResult.getUserId();
            if (!isBypassed(userName)) {
                result = authenticateInternal(ssoValidationResult);
            } else {
                if(isLogMessageEnabled()) {
                    logMessage("AmSDKRealm: Bypassed authentication for user: "
                            + userName);
                }
            }
        }
        return result;
    }
    
    public AmRealmAuthenticationResult authenticate(
            String userName, String transportString) {
        AmRealmAuthenticationResult result = AmRealmAuthenticationResult.FAILED;
        try {
            if (!isBypassed(userName)) {
                SSOValidationResult ssoValidationResult =
                        getSSOTokenValidator().validate(transportString);
                
                if (ssoValidationResult.isValid()) {
                    if (userName.equals(ssoValidationResult.getUserId())) {
                        result = authenticateInternal(ssoValidationResult);
                    } else {
                        logError("AmSDKRealm: Username mismatch: given: "
                                + userName + ", expected: "
                                + ssoValidationResult.getUserId() +
                                ". Denying authentication.");
                    }
                } else {
                    if (isLogMessageEnabled()) {
                        logMessage("AmSDKRealm: session invalid for user: "
                                + userName);
                    }
                }
                processAuthenticationResult(userName, result, 
                        ssoValidationResult);
            } else {
                if(isLogMessageEnabled()) {
                    logMessage("AmSDKRealm: Bypassed authentication for user: "
                            + userName);
                }
            }
        } catch (Exception ex) {
            logError("AmSDKRealm: failed to authenticate user: "
                    + userName, ex);
            result = AmRealmAuthenticationResult.FAILED;
        }
        
        if (isLogMessageEnabled()) {
            logMessage("AmSDKRealm: authenticated user: " + userName
                    + ", attributes: " + result.getAttributes());
        }
        
        return result;
    }
    
    private AmRealmAuthenticationResult authenticateInternal(
            SSOValidationResult ssoValidationResult) {
        AmRealmAuthenticationResult result = AmRealmAuthenticationResult.FAILED;
        String userName = AgentConfiguration.getAnonymousUserName();
        try {
            if (ssoValidationResult != null && ssoValidationResult.isValid()) {
                userName = ssoValidationResult.getUserId();
                if (ssoValidationResult.isValid()) {
                    TransportToken token = 
                            ssoValidationResult.getTransportToken();
                    IExternalVerificationHandler handler = 
                            getVerificationHandler(
                            ssoValidationResult.getApplicationName());
                    if (handler.verify(userName, token, null)) {
                        HashSet attributeSet = new HashSet();
                        attributeSet.addAll(getDefaultPrivilegedAttributeSet());
                        AMUser user = getUser(
                                ssoValidationResult.getSSOToken());
                        
                        if (user != null) {
                            Set roles = user.getRoleDNs();
                            attributeSet.addAll( getPrivilegedMappedAttributesSet(roles)); 
                            if (isFilteredRolesEnabled()) {
                                Set filteredRoles = user.getFilteredRoleDNs();
                                attributeSet.addAll( getPrivilegedMappedAttributesSet(filteredRoles));
                            }
                            if (isSessionAttributeFetchEnabled()) {
                                String[] sessionAttributes =
                                        getSessionAttributes();
                                SSOToken ssoToken =
                                        ssoValidationResult.getSSOToken();
                                for (int i=0; i<sessionAttributes.length; i++) {
                                    String nextValue =
                                            ssoToken.getProperty(
                                            sessionAttributes[i]);
                                    if (nextValue != null &&
                                            nextValue.trim().length() > 0) {
                                        attributeSet.add(nextValue);
                                    }
                                }
                            }
                            result = new AmRealmAuthenticationResult(
                                    true, attributeSet);
                        } else {
                            throw new AgentException("Failed to find user: "
                                    + userName + "["
                                    + ssoValidationResult.getUserPrincipal() 
                                    + "]");
                        }
                    } else {
                        if (isLogMessageEnabled()) {
                            logMessage(
                                    "AmSDKRealm: external verfication failed "
                                    + "for user: " + userName);
                        }
                    }
                } else {
                    if (isLogMessageEnabled()) {
                        logMessage("AmSDKRealm: session invalid for user: "
                                + userName);
                    }
                }
                processAuthenticationResult(userName, result, 
                        ssoValidationResult);
            } else {
                if(isLogMessageEnabled()) {
                    logMessage("AmSDKRealm: Bypassed authentication for user: "
                            + userName);
                }
            }
        } catch (Exception ex) {
            logError("AmSDKRealm: failed to authenticate user: "
                    + userName, ex);
            result = AmRealmAuthenticationResult.FAILED;
        }
        
        if (isLogMessageEnabled()) {
            logMessage("AmSDKRealm: authenticated user: " + userName
                    + ", attributes: " + result.getAttributes());
        }
        
        return result;
    }
    
    /**
     * Iterate thru a set and swap current values for mapped 
     * values, if attribute mapping enabled.
     */
    private Set getPrivilegedMappedAttributesSet(Set originalSet) {
        Set returnSet = originalSet;
        if (isPrivilegedAttributeMappingEnabled()) {
            returnSet = new HashSet();
            if (originalSet != null && originalSet.size() > 0) {
                for (Iterator it = originalSet.iterator(); it.hasNext(); ) {
                  String mappedValue =  getPrivilegedMappedAttribute((String)it.next());
                  returnSet.add(mappedValue);
                }
            }
        }
        return returnSet;
    }
    /**
     * maps original Attribute to the one defined in AMAgent.properties.
     * It helps with some cases like handling special characters in original
     * attribute.
     */
    private String getPrivilegedMappedAttribute(String originalAttribute) {
        String mappedAttribute = originalAttribute;
        if (isPrivilegedAttributeMappingEnabled()) {
            Map privilegedAttributeMap = getPrivilegedAttributeMap();
            if (privilegedAttributeMap != null &&
                    originalAttribute != null) {
                mappedAttribute = (String)privilegedAttributeMap.get(
                        originalAttribute);
            }
            if (mappedAttribute == null) {
                mappedAttribute = originalAttribute;
            }
        }
        return mappedAttribute;
    }
    
    private void initPrivilegedAttributeMappingEnableFlag() {
        _privilegedAttributeMappingEnabled = getConfigurationBoolean(
                CONFIG_PRIVILEGED_ATTRIBUTE_MAPPING_ENABLED,
                DEFAULT_PRIVILEGED_ATTRIBUTE_MAPPING_ENABLED
                );
        if (isLogMessageEnabled()) {
            logMessage(
                    "AmSDKRealm: Using privileged attribute " 
                    + "mapping enabled flag: "
                    + _privilegedAttributeMappingEnabled);
        }
    }
    
    private void initPrivilegedAttributeMap() {
        _privilegedAttributeMap = getConfigurationMap(
                CONFIG_PRIVILEGED_ATTRIBUTE_MAPPING);
        if (isLogMessageEnabled()) {
            logMessage("AmSDKRealm: privileged attribute mapping: "+
                    _privilegedAttributeMap);
        }
    }
    
    private boolean isPrivilegedAttributeMappingEnabled() {
        return _privilegedAttributeMappingEnabled;
    }
    
    private Map getPrivilegedAttributeMap() {
        return _privilegedAttributeMap;
    }
    
    private AMUser getUser(SSOToken token) throws Exception {
        AMStoreConnection connection = new AMStoreConnection(token);
        return connection.getUser(token.getPrincipal().toString());
    }
    
    private void initSSOTokenValidator() throws AgentException {
        boolean urlDecodeSSOToken = getConfigurationBoolean(
                ISharedConfigurationKeyConstants.CONFIG_SSO_DECODE_FLAG);
        CommonFactory cf = new CommonFactory(getModule());
        ISSOTokenValidator validator = cf.newSSOTokenValidator(
                urlDecodeSSOToken);
        
        setSSOTokenValidator(validator);
    }
    
    private void initDefaultPrivilegedAttributeList() {
        String[] defaultAttributeList = getConfigurationStrings(
                CONFIG_DEFAULT_PRIVILEGE_ATTR_LIST);
        
        if (defaultAttributeList != null && defaultAttributeList.length >0) {
            for (int i=0; i<defaultAttributeList.length; i++) {
                String nextAttr = defaultAttributeList[i];
                if (nextAttr != null && nextAttr.trim().length() > 0) {
                    getDefaultPrivilegedAttributeSet().add(nextAttr);
                }
            }
        }
        
        if (isLogMessageEnabled()) {
            logMessage("AmSDKRealm: Default privileged attribute set: "
                    + getDefaultPrivilegedAttributeSet());
        }
    }
    
    private void initSessionPrivilegedAttributeList() {
        String [] sessionAttributeList = getConfigurationStrings(
                CONFIG_PRIVILEGED_SESSION_ATTR_LIST);
        ArrayList attributes = new ArrayList();
        if (sessionAttributeList != null && sessionAttributeList.length > 0) {
            for (int i=0; i<sessionAttributeList.length; i++) {
                String nextAttr = sessionAttributeList[i];
                if (nextAttr != null && nextAttr.trim().length() > 0) {
                    attributes.add(nextAttr);
                }
            }
        }
        
        String[] sessionAttributes = new String[attributes.size()];
        System.arraycopy(attributes.toArray(), 0, sessionAttributes, 0,
                attributes.size());
        setSessionAttributes(sessionAttributes);
    }
    
    private IExternalVerificationHandler getVerificationHandler(String appName)
    throws AgentException {
        IExternalVerificationHandler result = (IExternalVerificationHandler)
        getVerificationHandlers().get(appName);
        
        if (result == null) {
            synchronized (this) {
                result = (IExternalVerificationHandler)
                getVerificationHandlers().get(appName);
                if (result == null) {
                    String className = (String) getConfigurationMap(
                            CONFIG_VERIFICATION_HANDLERS).get(appName);
                    
                    boolean appHandlerFound = false;
                    if (className != null && className.trim().length() > 0) {
                        try {
                            result = (IExternalVerificationHandler)
                            Class.forName(className).newInstance();
                            
                            getVerificationHandlers().put(appName, result);
                            appHandlerFound = true;
                        } catch (Exception ex) {
                            throw new AgentException(
                                "Unable to load verification handler for app: "
                                + appName, ex);
                            
                        }
                    }
                    if (!appHandlerFound) {
                        result = getGlobalVerificationHandler();
                        getVerificationHandlers().put(appName, result);
                        
                        if (isLogMessageEnabled()) {
                            logMessage("AmSDKRealm: Unable to find verification"
                                    + " handler for app: "+ appName
                                    + ", using global handler");
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    private IExternalVerificationHandler getGlobalVerificationHandler() {
        return _globalVerificationHandler;
    }
    
    private void initGlobalVerificationHandler() throws AgentException {
        try {
            String className = getResolver().getGlobalVerificationHandlerImpl();
            _globalVerificationHandler = (IExternalVerificationHandler)
            Class.forName(className).newInstance();
            
            if (isLogMessageEnabled()) {
                logMessage("AmSDKRealm: Global verification handler set to: "
                        + _globalVerificationHandler);
            }
            
        } catch (Exception ex) {
            throw new AgentException(
                    "Unable to initialize global verification handler", ex);
        }
    }
    
    private void initBypassPrincipalList() {
        String[] bypassList = getConfigurationStrings(CONFIG_BYPASS_USER_LIST);
        if (bypassList != null && bypassList.length > 0) {
            for (int i=0; i<bypassList.length; i++) {
                if (bypassList[i] != null 
                        && bypassList[i].trim().length() > 0) {
                    getBypassPrincipalSet().add(bypassList[i]);
                }
            }
        }
    }
    
    private boolean isBypassed(String userName) {
        boolean result = false;
        if(getBypassPrincipalSet().contains(userName)) {
            result = true;
        }
        return result;
    }
    
    private boolean isFilteredRolesEnabled() {
        return _filteredRolesEnabled;
    }
    
    private void initFilteredRolesEnableFlag() {
        _filteredRolesEnabled = getConfigurationBoolean(
                CONFIG_FILTERED_ROLES_ENABLED, DEFAULT_FILTERED_ROLES_ENABLED);
        
        if (isLogMessageEnabled()) {
            logMessage("AmSDKRealm: Using filtered roles enabled flag: "
                    + _filteredRolesEnabled);
        }
    }
    
    private HashSet getBypassPrincipalSet() {
        return _bypassPrincipalSet;
    }
    
    private Hashtable getVerificationHandlers() {
        return _verificationHandlers;
    }
    
    private HashSet getDefaultPrivilegedAttributeSet() {
        return _defaultPrivilegedAttributeSet;
    }
    
    private ISSOTokenValidator getSSOTokenValidator() {
        return _ssoTokenValidator;
    }
    
    private void setSSOTokenValidator(ISSOTokenValidator validator) {
        _ssoTokenValidator = validator;
    }
    
    private boolean isSessionAttributeFetchEnabled() {
        boolean result = false;
        String[] sessionAttributes = getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.length > 0) {
            result = true;
        }
        return result;
    }
    
    private String[] getSessionAttributes() {
        return _sessionAttributes;
    }
    
    private void setSessionAttributes(String[] attributes) {
        _sessionAttributes = attributes;
        if (isLogMessageEnabled()) {
            StringBuffer buff = new StringBuffer("");
            for (int i=0; i<attributes.length; i++) {
                buff.append(" ").append(attributes[i]);
                if (i !=attributes.length -1) {
                    buff.append(",");
                }
            }
            logMessage("AmSDKRealm: Session attributes: " +buff.toString());
        }
    }
    
    private HashSet _bypassPrincipalSet = new HashSet();
    private IExternalVerificationHandler _globalVerificationHandler;
    private Hashtable _verificationHandlers = new Hashtable();
    private HashSet _defaultPrivilegedAttributeSet = new HashSet();
    private ISSOTokenValidator _ssoTokenValidator;
    private boolean _filteredRolesEnabled;
    private String[] _sessionAttributes;
    private Map _privilegedAttributeMap = null;
    private boolean _privilegedAttributeMappingEnabled;
}

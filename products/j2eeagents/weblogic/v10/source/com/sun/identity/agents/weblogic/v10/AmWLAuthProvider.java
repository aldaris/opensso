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
 * $Id: AmWLAuthProvider.java,v 1.1 2007-08-07 01:47:49 sean_brydon Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.weblogic.v10;

import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import weblogic.management.security.ProviderMBean;
import weblogic.security.provider.PrincipalValidatorImpl;
import weblogic.security.spi.IdentityAsserter;
import weblogic.security.spi.PrincipalValidator;
import weblogic.security.spi.AuthenticationProvider;
import weblogic.security.spi.SecurityServices;

import com.sun.identity.agents.realm.AmRealmManager;
import com.sun.identity.agents.arch.IModuleAccess;

/**
 * This class serves as an customized Authenication provider for WebLogic.
 *
 */
public class AmWLAuthProvider implements AuthenticationProvider {

    
    /**
     * Method declaration
     *
     * @param mbean 
     * @param services
     *
     */
    public void initialize(ProviderMBean mbean, SecurityServices services) {
        IModuleAccess modAccess = AmRealmManager.getModuleAccess();
        if (modAccess.isLogMessageEnabled()) {
            modAccess.logMessage(
                "AmWLAuthProvider: Initialize AMAuthProvider " + _agentType);
        }
        
        // FIXME
        // Looks like a classloader issue. The system class loader does
        // not seem to have an access to the class loader loading the mbeans
        controlFlag = LoginModuleControlFlag.OPTIONAL;
    }
    
    /**
     * Method declaration
     *
     * @return
     *
     * @see
     */
    public String getDescription() {
        return "Agent Authentication Provider";
    }
    
    /**
     * Method declaration
     *
     * @see
     */
    public void shutdown() {
        IModuleAccess modAccess = AmRealmManager.getModuleAccess();
        if (modAccess.isLogMessageEnabled()) {
            modAccess.logMessage(
                    "AmWLAuthProvider: AMAuthProvider.shutdown "+ _agentType);
        }
    }
    
    /**
     * Method declaration
     *
     * @return
     *
     * @see
     */
    public AppConfigurationEntry getLoginModuleConfiguration() {
        return getAppConfigurationEntry();
    }
    
    /**
     * Method declaration
     *
     * @return
     *
     * @see
     */
    public AppConfigurationEntry getAssertionModuleConfiguration() {
        return getAppConfigurationEntry();
    }
    
    /**
     * Method declaration
     *
     * @return
     *
     * @see
     */
    public IdentityAsserter getIdentityAsserter() {
        return null;
    }
    
    
    /**
     * Since we use the default WebLogic impl of the JAAS security principal,
     * we need to use the WebLogic impl of the principal validator
     *
     * @return
     *
     * @see
     */
    public PrincipalValidator getPrincipalValidator() {
        return new PrincipalValidatorImpl();
    }
    
    /**
     * Method declaration
     *
     * @return
     *
     * @see
     */
    private AppConfigurationEntry getAppConfigurationEntry() {
        AppConfigurationEntry entry =
                new AppConfigurationEntry(
                "com.sun.identity.agents.weblogic.v10.AmWLLoginModule",
                controlFlag, new HashMap());
        
        return entry;
    }
    
    private String		   description;
    private LoginModuleControlFlag controlFlag;
    private static String _agentType = "weblogic92";
}



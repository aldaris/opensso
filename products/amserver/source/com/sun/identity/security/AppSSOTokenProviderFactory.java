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
 * $Id: AppSSOTokenProviderFactory.java,v 1.1 2005-11-01 00:31:16 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security;

import com.iplanet.am.util.SystemProperties;

/**
 * An extensible factory implementation that can be used for instantiating
 * <code>AppSSOTokenProvider</code>s. Concrete subclasses of this class can
 * be plugged into the system using the configuration key
 * <code>com.sun.identity.appssoprovider.factory</code> set in the system
 * configuration file.
 */
public abstract class AppSSOTokenProviderFactory {

    /**
     * The configuration key used for plugging in a concrete implementation of
     * this factory.
     */
    public static final String CONFIG_APP_SSO_PROVIDER_FACTORY = 
        "com.sun.identity.appssoprovider.factory";

    /**
     * The default concrete implementation of this factory to be used in case no
     * implementation is configured for use.
     */
    public static final String DEFAULT_APP_SSO_PROVIDER_FACTORY = 
        "com.sun.identity.security.AppSSOTokenProviderFactoryImpl";

    /** The singleton instance of the concrete factory. */
    private static AppSSOTokenProviderFactory instance;

    /**
     * Creates a new instance of <code>AppSSOTokenProvider</code> using the
     * plugged in concrete implementation of this factory. This method delegates
     * to the {@link #newProvider(String, String)} method of the concrete
     * factory implementation in order to obtain the new instance.
     * 
     * @param username
     *            the user name supplied for token creation.
     * @param password
     *            the password supplied for token creation.
     * 
     * @return a newly created instance of <code>AppSSOTokenProvider</code>.
     */
    public static AppSSOTokenProvider getProvider(String username,
            String password) {
        return instance.newProvider(username, password);
    }

    /**
     * Creates a new instance of <code>AppSSOTokenProvider</code> using the
     * mechanism specific to the plugged in concrete implementation. Concrete
     * factories are subclasses of this class that provide an implementation
     * this method.
     * 
     * @param username
     *            the user name supplied for token creation.
     * @param password
     *            the password supplied for token creation.
     * 
     * @return a newly created instance of <code>AppSSOTokenProvider</code>.
     */
    protected abstract AppSSOTokenProvider newProvider(String username,
            String password);

    static {
        try {
            String implClass = SystemProperties.get(
                    CONFIG_APP_SSO_PROVIDER_FACTORY,
                    DEFAULT_APP_SSO_PROVIDER_FACTORY);

            instance = (AppSSOTokenProviderFactory) Class.forName(implClass)
                    .newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Initialization Failed", ex);
        }
    }

}

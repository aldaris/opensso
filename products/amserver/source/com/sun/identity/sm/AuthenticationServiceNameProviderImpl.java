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
 * $Id: AuthenticationServiceNameProviderImpl.java,v 1.1 2006-07-17 18:11:27 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import com.sun.identity.authentication.config.AMAuthenticationManager;
import java.util.Set;

/**
 * A concrete implementation of <code>AuthenticationServiceNameProvider</code>
 * that uses the 
 * <code>com.sun.identity.authentication.config.AMAuthenticationManager</code>
 * to retrieve the names of authentication module services that are loaded by
 * default.
 */
public class AuthenticationServiceNameProviderImpl implements
        AuthenticationServiceNameProvider {
    /**
     * Provides a collection of authentication module service names that are
     * loaded by default. This implementation uses the authentication 
     * service specific configuration manager to retrieve the relevant 
     * module service name information.
     * 
     * @return a <code>Set</code> of authentication module service names.
     */
    public Set getAuthenticationServiceNames() {        
        return AMAuthenticationManager.getAuthenticationServiceNames();
    }
}

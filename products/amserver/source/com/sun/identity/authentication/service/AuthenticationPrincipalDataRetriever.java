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
 * $Id: AuthenticationPrincipalDataRetriever.java,v 1.1 2006-01-28 09:16:37 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.authentication.service;

import java.util.Map;
import javax.security.auth.Subject;

/**
 * Defines the interface that allows Authentication service to retrieve the
 * required data from Authentication <code>Principal</code>, to be populated
 * in successful user authentication session.
 * 
 * The implementation of this interface is determined during runtime.
 *
 * @see AuthenticationPrincipalDataRetrieverFactory
 */

public interface AuthenticationPrincipalDataRetriever {
    
    /**
     * Returns the attribute map from the required Authentication module
     * <code>Principal</code>, to be set in the <code>SSOToken</code>.     
     *
     * @param authSubject Authenticated user <code>Subject</code>.
     * @return the Attribute Map.
     */
    Map getAttrMapForAuthenticationModule(Subject authSubject);
    
}

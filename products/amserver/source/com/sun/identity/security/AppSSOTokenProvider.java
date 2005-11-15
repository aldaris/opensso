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
 * $Id: AppSSOTokenProvider.java,v 1.2 2005-11-15 04:10:34 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security;

import com.iplanet.sso.SSOToken;

/**
 * This interface defines method to get application single sign on token. An use
 * case is that an application that uses
 * <code>com.sun.identity.policy.client.PolicyEvaluator</code> would pass an
 * implementation of this interface to construct an instance of
 * <code>com.sun.identity.policy.client.PolicyEvaluator</code>.
 */
/* iPlanet-PUBLIC-CLASS */
public interface AppSSOTokenProvider {
    /**
     * Returns application single sign on token.
     * 
     * @return application single sign on token.
     */
    public SSOToken getAppSSOToken();
}

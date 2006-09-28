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
 * $Id: AmSSOCache.java,v 1.1 2006-09-28 23:30:16 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.filter;

import javax.ejb.EJBContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Class AmSSOCache
 *
 *
 * @author
 * @version
 * @deprecated 
 */
public class AmSSOCache {
    
    /**
     * Returns the SSO Token for user using the given EJBContext.
     * @param context the EJBContext object
     * @return the SSO token string of the user
     * @deprecated 
     */
    public String getSSOTokenForUser(EJBContext context) {
        IAmSSOCache cache = AmFilterManager.getAmSSOCache();
        return cache.getSSOTokenForUser(context);
    }

    /**
     * Returns the SSO Token for user using the given HttpServletRequest.
     * @param request the HttpServletRequest object
     * @return the SSO token string of the user
     * @deprecated
     */
    public String getSSOTokenForUser(HttpServletRequest request) {
        IAmSSOCache cache = AmFilterManager.getAmSSOCache();
        return cache.getSSOTokenForUser(request);
    }
}

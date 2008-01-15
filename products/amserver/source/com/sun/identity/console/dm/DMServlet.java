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
 * $Id: DMServlet.java,v 1.1 2008-01-15 22:22:57 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.dm;

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestContextImpl;
import com.iplanet.jato.ViewBeanManager;
import com.sun.identity.console.base.ConsoleServletBase;

/**
 * Controller servlet for the User Management interface.
 */
public class DMServlet 
    extends ConsoleServletBase 
{
    public static final String DEFAULT_MODULE_URL = "../dm";
    public static String PACKAGE_NAME=
        getPackageName(DMServlet.class.getName());
    
    /**
     * Initialize request context and set the viewbean manager
     * @param requestContext current request context
     */
    protected void initializeRequestContext(RequestContext requestContext) {
        super.initializeRequestContext(requestContext);	
        ViewBeanManager viewBeanManager =
            new ViewBeanManager(requestContext,PACKAGE_NAME);
        ((RequestContextImpl)requestContext).setViewBeanManager(
            viewBeanManager);
    }
    
    /**
     * gets the modules URL
     *
     * @return Returns the module URL as String
     */
    public String getModuleURL() {
        return DEFAULT_MODULE_URL;
    }
}

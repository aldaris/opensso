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
 * $Id: EditServiceViewBean.java,v 1.1 2008-01-15 22:22:57 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.dm;

import com.sun.identity.console.realm.ServicesEditViewBean;
                    
public class EditServiceViewBean
    extends ServicesEditViewBean {
    public static final String DEFAULT_DISPLAY_URL =
	"/console/dm/EditService.jsp";

    /**
     * Creates a service profile view bean.
     */
    public EditServiceViewBean() {
	super("EditService", DEFAULT_DISPLAY_URL);
    }

    protected void forwardToServicesViewBean() {
	OrganizationServicesViewBean vb = (OrganizationServicesViewBean)
            getViewBean(OrganizationServicesViewBean.class);

	passPgSessionMap(vb);
	vb.forwardTo(getRequestContext());
    }
}

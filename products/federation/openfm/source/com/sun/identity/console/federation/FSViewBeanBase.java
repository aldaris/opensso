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
 * $Id: FSViewBeanBase.java,v 1.1 2007-03-14 19:33:21 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.sun.identity.console.base.AMPrimaryMastHeadViewBean;


public abstract class FSViewBeanBase
    extends AMPrimaryMastHeadViewBean 
{
    /**
     * Page session attribute name for entity name.
     */
    public static final String ENTITY_NAME = "entityName";

    /**
     * Creates a federation view bean base.
     *
     * @param name Name of page.
     */
    public FSViewBeanBase(String name) {
	super(name);
    }
}

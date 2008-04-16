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
 * $Id: UMUserDiscoveryDescriptionViewBeanBase.java,v 1.1 2008-04-16 00:27:59 asyhuang Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.user;

import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.service.SMDiscoveryDescriptionViewBeanBase;

public abstract class UMUserDiscoveryDescriptionViewBeanBase
    extends SMDiscoveryDescriptionViewBeanBase
{
    public UMUserDiscoveryDescriptionViewBeanBase(
	String pageName,
	String defaultDisplayURL
    ) {
	super(pageName, defaultDisplayURL);
    }

    /**
     * Handles cancel request.
     *
     * @param event Request Invocation Event.
     */
    public void handleButton2Request(RequestInvocationEvent event) {
        UMUserResourceOfferingViewBeanBase vb =
	    (UMUserResourceOfferingViewBeanBase)getReturnToViewBean();
        passPgSessionMap(vb);
        vb.forwardTo(getRequestContext());
    }
}

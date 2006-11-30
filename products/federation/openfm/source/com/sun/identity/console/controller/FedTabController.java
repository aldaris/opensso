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
 * $Id: FedTabController.java,v 1.1 2006-11-30 00:44:50 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.controller;

import com.sun.identity.console.base.model.AMAdminConstants;

/**
 * This class determines whether to show/hide th federation tab.
 */
public class FedTabController
    extends TabControllerBase
{
    private static FedTabController privateInstance = new FedTabController();

    static {
	privateInstance.addListener();
	privateInstance.updateStatus();
    }

    /**
     * Returns true if tab is visible.
     *
     * @return true if tab is visible.
     */
    public boolean isVisible() {
	return privateInstance.visible;
    }

    protected String getConfigAttribute() {
	return AMAdminConstants.CONSOLE_FED_ENABLED_ATTR;
    }
}

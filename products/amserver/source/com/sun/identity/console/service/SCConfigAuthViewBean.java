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
 * $Id: SCConfigAuthViewBean.java,v 1.1 2007-10-17 23:00:35 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.service;

import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.service.model.SCConfigModel;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import java.util.List;

public class SCConfigAuthViewBean extends SCConfigViewBean {

    public static final String DEFAULT_DISPLAY_URL =
        "/console/service/SCConfigAuth.jsp";

    private static final String SEC_AUTH = SCConfigModel.SEC_AUTH;
    private static final String TBL_AUTH = "tblAuth";

    private CCActionTableModel tblModelAuth;

    public SCConfigAuthViewBean() {
        super("SCConfigAuth", DEFAULT_DISPLAY_URL);
    }

    protected void createPropertyModel() {
        psModel = new CCPropertySheetModel(
            getClass().getClassLoader().getResourceAsStream(
            "com/sun/identity/console/propertySCAuthConfig.xml"));
        createTableModels();
        psModel.setModel(TBL_AUTH, tblModelAuth);
    }

    protected void createTableModels() {
        SCConfigModel model = (SCConfigModel)getModel();
        tblModelAuth = new CCActionTableModel(
            getClass().getClassLoader().getResourceAsStream(
            "com/sun/identity/console/tblSCConfigAuth.xml"));
        List svcNames = model.getServiceNames(SEC_AUTH);
        populateTableModel(tblModelAuth, svcNames, SEC_AUTH);
    }

    public void handleTblHrefAuthenticationRequest(RequestInvocationEvent event)
    {
        String name = (String)getDisplayFieldValue(TBL_HREF_PREFIX + SEC_AUTH);
        forwardToProfile(name);
    }
}

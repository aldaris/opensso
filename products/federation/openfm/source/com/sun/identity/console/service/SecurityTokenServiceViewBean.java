/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: SecurityTokenServiceViewBean.java,v 1.1 2008-08-13 22:34:06 asyhuang Exp $
 *
 */
package com.sun.identity.console.service;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.AMServiceProfileViewBeanBase;
import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMAdminConstants;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.service.model.SecurityTokenServiceModelImpl;
import com.sun.web.ui.view.alert.CCAlert;
import javax.servlet.http.HttpServletRequest;

public class SecurityTokenServiceViewBean
        extends AMServiceProfileViewBeanBase {

    public static final String DEFAULT_DISPLAY_URL =
            "/console/service/SecurityTokenService.jsp";
    protected static final String PROPERTIES =
            "propertyAttributes";
    
    public static final String PAGE_MODIFIED = "pageModified";

    /**
     * Creates a authentication domains view bean.
     */
    public SecurityTokenServiceViewBean() {
        super("SecurityTokenService",
                DEFAULT_DISPLAY_URL,
                "sunFAMSTSService");
    }

    protected void initialize() {
        if (!initialized) {
            super.initialize();
            initialized = true;
            createPropertyModel();
            createPageTitleModel();
            registerChildren();
        }
    }

    protected View createChild(String name) {
        return super.createChild(name);
    }

    public void beginDisplay(DisplayEvent event)
            throws ModelControlException {
        super.beginDisplay(event);       
        AMPropertySheet ps = (AMPropertySheet) getChild(PROPERTIES);
        ps.init();
    
        if (!isInlineAlertMessageSet()) {
            String flag = (String) getPageSessionAttribute(PAGE_MODIFIED);
            if ((flag != null) && flag.equals("1")) {
                setInlineAlertMessage(CCAlert.TYPE_INFO, "message.information",
                        "message.profile.modified");
            }
        }
    }

    protected void createPageTitleModel() {
        createThreeButtonPageTitleModel();
    }

    protected AMModel getModelInternal() {
        HttpServletRequest req =
                RequestManager.getRequestContext().getRequest();
        try {
            return new SecurityTokenServiceModelImpl(
                    req, getPageSessionAttributes());
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                    e.getMessage());
        }
        return null;
    }

    protected void createPropertyModel() {
        String xmlFileName = "com/sun/identity/console/propertySecurityTokenService.xml";
        String xml = AMAdminUtils.getStringFromInputStream(
                getClass().getClassLoader().getResourceAsStream(xmlFileName));

        propertySheetModel = new AMPropertySheetModel(xml);
        propertySheetModel.clear();       
    }

    /**
     * Handles save button request.
     * save
     * @param event Request invocation event
     */
    public void handleButton1Request(RequestInvocationEvent event)
            throws ModelControlException {
        removePageSessionAttribute(PAGE_MODIFIED);
        super.handleButton1Request(event);
    }

    /**
     * Handles page cancel request.
     * 
     * @param event Request invocation event
     */
    public void handleButton2Request(RequestInvocationEvent event)
            throws ModelControlException {
        removePageSessionAttribute(PAGE_MODIFIED);
        super.handleButton2Request(event);
    }

    /**
     * Handles reset request.
     *
     * @param event Request invocation event
     */
    public void handleButton3Request(RequestInvocationEvent event)
            throws ModelControlException, AMConsoleException {
        removePageSessionAttribute(PAGE_MODIFIED);
        backTrail();
        try {
            String name = (String) getPageSessionAttribute(
                    AMAdminConstants.SAVE_VB_NAME);
            SCConfigViewBean vb = (SCConfigViewBean) getViewBean(
                    Class.forName(name));
            passPgSessionMap(vb);
            vb.forwardTo(getRequestContext());
        } catch (ClassNotFoundException e) {
            debug.warning(
                    "SecurityTokenServiceViewBean.handleButton3Request:", e);
        }
    }
}

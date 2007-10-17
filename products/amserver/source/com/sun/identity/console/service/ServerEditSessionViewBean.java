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
 * $Id: ServerEditSessionViewBean.java,v 1.1 2007-10-17 23:00:40 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.service;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.Option;
import com.iplanet.jato.view.html.OptionList;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.service.model.ServerSiteModel;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.html.CCDropDownMenu;
import java.util.Set;

/**
 * Server Configuration, Session Tab.
 */
public class ServerEditSessionViewBean
    extends ServerEditViewBeanBase
{
    private static final String DEFAULT_DISPLAY_URL =
        "/console/service/ServerEditSession.jsp";

    /**
     * Creates a modify server view bean.
     */
    public ServerEditSessionViewBean() {
        super("ServerEditSession", DEFAULT_DISPLAY_URL);
    }
    
    protected String getPropertyXML() {
        return "com/sun/identity/console/propertyServerEditSession.xml";
    }
    
    /**
     * Handles modify server request.
     *
     * @param event Request invocation event
     */
    public void handleButton1Request(RequestInvocationEvent event)
        throws ModelControlException {
        submitCycle = true;
        modifyProperties();
        forwardTo();
    }
}

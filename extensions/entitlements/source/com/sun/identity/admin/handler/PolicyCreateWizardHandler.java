/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: PolicyCreateWizardHandler.java,v 1.33 2009-06-09 22:40:37 farble1670 Exp $
 */

package com.sun.identity.admin.handler;

import com.sun.identity.admin.Resources;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.ViewApplicationsBean;
import javax.faces.application.FacesMessage;

public class PolicyCreateWizardHandler extends PolicyWizardHandler {
    public String getFinishAction() {
        return "home";
    }

    public String getCancelAction() {
        return "home";
    }

    public String createAction() {
        ViewApplicationsBean vasb = ViewApplicationsBean.getInstance();
        if (vasb.getViewApplications() == null || vasb.getViewApplications().size() == 0) {
            MessageBean mb = new MessageBean();
            Resources r = new Resources();
            mb.setSummary(r.getString(this, "noApplicationsSummary"));
            mb.setDetail(r.getString(this, "noApplicationsDetail"));
            mb.setSeverity(FacesMessage.SEVERITY_ERROR);
            getMessagesBean().addMessageBean(mb);

            return null;
        } else {
            return "policy-create";
        }
    }
}

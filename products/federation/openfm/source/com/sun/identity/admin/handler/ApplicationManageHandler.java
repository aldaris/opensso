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
 * $Id: ApplicationManageHandler.java,v 1.1 2009-09-09 17:13:43 farble1670 Exp $
 */
package com.sun.identity.admin.handler;

import com.sun.identity.admin.dao.ViewApplicationDao;
import com.sun.identity.admin.model.ApplicationManageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.PhaseEventAction;
import com.sun.identity.admin.model.QueuedActionBean;
import com.sun.identity.admin.model.ViewApplication;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

public class ApplicationManageHandler implements Serializable {

    private ApplicationManageBean applicationManageBean;
    private QueuedActionBean queuedActionBean;
    private ViewApplicationDao viewApplicationDao;
    private MessagesBean messagesBean;

    public ViewApplication getViewApplication(ActionEvent event) {
        ViewApplication va = (ViewApplication) event.getComponent().getAttributes().get("viewApplication");
        assert (va != null);
        return va;
    }

    public void selectAllListener(ActionEvent event) {
        applicationManageBean.setSelectAll(!applicationManageBean.isSelectAll());
        for (ViewApplication va: applicationManageBean.getViewApplications()) {
            va.setSelected(applicationManageBean.isSelectAll());
        }
    }

    public void setQueuedActionBean(QueuedActionBean queuedActionBean) {
        this.queuedActionBean = queuedActionBean;
    }

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }

    public void setApplicationManageBean(ApplicationManageBean applicationManageBean) {
        this.applicationManageBean = applicationManageBean;
    }

    public void setViewApplicationDao(ViewApplicationDao viewApplicationDao) {
        this.viewApplicationDao = viewApplicationDao;
    }

    public void sortTableListener(ActionEvent event) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(true);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{applicationManageHandler.handleSort}");
        pea.setParameters(new Class[]{});
        pea.setArguments(new Object[]{});

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public void handleSort() {
        applicationManageBean.getApplicationManageTableBean().sort();
    }
}


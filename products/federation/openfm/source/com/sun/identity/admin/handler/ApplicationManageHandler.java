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
 * $Id: ApplicationManageHandler.java,v 1.4 2009-09-30 22:53:35 farble1670 Exp $
 */
package com.sun.identity.admin.handler;

import com.sun.identity.admin.Resources;
import com.sun.identity.admin.dao.ViewApplicationDao;
import com.sun.identity.admin.model.ApplicationManageBean;
import com.sun.identity.admin.model.FilterHolder;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.PhaseEventAction;
import com.sun.identity.admin.model.QueuedActionBean;
import com.sun.identity.admin.model.ViewApplication;
import com.sun.identity.admin.model.ViewFilterType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

public class ApplicationManageHandler implements Serializable {

    private ApplicationManageBean applicationManageBean;
    private QueuedActionBean queuedActionBean;
    private ViewApplicationDao viewApplicationDao;
    private MessagesBean messagesBean;
    private Map<String,ViewFilterType> viewFilterTypes;

    public ViewApplication getViewApplication(ActionEvent event) {
        ViewApplication va = (ViewApplication) event.getComponent().getAttributes().get("viewApplication");
        assert (va != null);
        return va;
    }

    public FilterHolder getFilterHolder(ActionEvent event) {
        FilterHolder fh = (FilterHolder) event.getComponent().getAttributes().get("filterHolder");
        assert (fh != null);
        return fh;
    }

    public void viewOptionsListener(ActionEvent event) {
        applicationManageBean.getViewOptionsPopupColumnsVisible().clear();
        applicationManageBean.getViewOptionsPopupColumnsVisible().addAll(applicationManageBean.getApplicationManageTableBean().getColumnsVisible());
        applicationManageBean.setViewOptionsPopupRows(applicationManageBean.getApplicationManageTableBean().getRows());

        applicationManageBean.setViewOptionsPopupVisible(true);
    }

    public void viewOptionsPopupCancelListener(ActionEvent event) {
        applicationManageBean.setViewOptionsPopupVisible(false);
    }

    public void viewOptionsPopupOkListener(ActionEvent event) {
        applicationManageBean.getApplicationManageTableBean().getColumnsVisible().clear();
        applicationManageBean.getApplicationManageTableBean().getColumnsVisible().addAll(applicationManageBean.getViewOptionsPopupColumnsVisible());
        applicationManageBean.getApplicationManageTableBean().setRows(applicationManageBean.getViewOptionsPopupRows());

        applicationManageBean.setViewOptionsPopupVisible(false);
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

    public void removeListener(ActionEvent event) {
        if (!applicationManageBean.isRemovePopupVisible()) {
            if (applicationManageBean.getSizeSelected() == 0) {
                MessageBean mb = new MessageBean();
                Resources r = new Resources();
                mb.setSummary(r.getString(this, "removeNoneSelectedSummary"));
                mb.setDetail(r.getString(this, "removeNoneSelectedDetail"));
                mb.setSeverity(FacesMessage.SEVERITY_ERROR);
                messagesBean.addMessageBean(mb);
            } else {
                applicationManageBean.setRemovePopupVisible(true);
            }
        } else {
            applicationManageBean.setRemovePopupVisible(false);
        }
    }

    public void removePopupOkListener(ActionEvent event) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(true);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{applicationManageHandler.handleRemoveAction}");
        pea.setParameters(new Class[]{});
        pea.setArguments(new Object[]{});

        queuedActionBean.getPhaseEventActions().add(pea);

        applicationManageBean.setRemovePopupVisible(false);
    }

    public void removePopupCancelListener(ActionEvent event) {
        applicationManageBean.setRemovePopupVisible(false);
    }

    public void handleRemoveAction() {
        Set<ViewApplication> removed = new HashSet<ViewApplication>();
        for (ViewApplication va : applicationManageBean.getViewApplications()) {
            if (va.isSelected()) {
                removed.add(va);
                viewApplicationDao.remove(va);
            }
            va.setSelected(false);
        }
        applicationManageBean.getViewApplications().removeAll(removed);
    }

    public Map<String, ViewFilterType> getViewFilterTypes() {
        return viewFilterTypes;
    }

    public void setViewFilterTypes(Map<String, ViewFilterType> viewFilterTypes) {
        this.viewFilterTypes = viewFilterTypes;
    }

    public void addViewFilterListener(ActionEvent event) {
        applicationManageBean.newFilterHolder();
        addResetEvent();
    }

    private void addResetEvent() {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(true);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{applicationManageHandler.handleReset}");
        pea.setParameters(new Class[]{});
        pea.setArguments(new Object[]{});

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public void handleReset() {
        applicationManageBean.reset();
    }

    public void viewFilterChangedListener(ValueChangeEvent event) {
        addResetEvent();
    }

    public void viewFilterChangedListener(ActionEvent event) {
        addResetEvent();
    }

    public void removeViewFilterListener(ActionEvent event) {
        FilterHolder fh = getFilterHolder(event);
        applicationManageBean.getFilterHolders().remove(fh);
        addResetEvent();
    }
}

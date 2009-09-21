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
 * $Id: ApplicationManageBean.java,v 1.2 2009-09-21 20:35:12 farble1670 Exp $
 */

package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.ViewApplicationDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplicationManageBean implements Serializable {
    private List<ViewApplication> viewApplications;
    private ViewApplicationDao viewApplicationDao;
    private ApplicationManageTableBean applicationManageTableBean = new ApplicationManageTableBean();
    private boolean selectAll = false;
    private boolean viewOptionsPopupVisible = false;
    private List<String> viewOptionsPopupColumnsVisible = new ArrayList<String>();
    private int viewOptionsPopupRows = 10;

    public List<ViewApplication> getViewApplications() {
        return viewApplications;
    }

    public void setViewApplicationDao(ViewApplicationDao viewApplicationDao) {
        this.viewApplicationDao = viewApplicationDao;
        reset();
    }

    public ApplicationManageTableBean getApplicationManageTableBean() {
        return applicationManageTableBean;
    }

    public void reset() {
        viewApplications = new ArrayList<ViewApplication>(viewApplicationDao.getViewApplications().values());
        applicationManageTableBean.setViewApplications(viewApplications);
        applicationManageTableBean.sort();
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public boolean isViewOptionsPopupVisible() {
        return viewOptionsPopupVisible;
    }

    public void setViewOptionsPopupVisible(boolean viewOptionsPopupVisible) {
        this.viewOptionsPopupVisible = viewOptionsPopupVisible;
    }

    public List<String> getViewOptionsPopupColumnsVisible() {
        return viewOptionsPopupColumnsVisible;
    }

    public void setViewOptionsPopupColumnsVisible(List<String> viewOptionsPopupColumnsVisible) {
        this.viewOptionsPopupColumnsVisible = viewOptionsPopupColumnsVisible;
    }

    public int getViewOptionsPopupRows() {
        return viewOptionsPopupRows;
    }

    public void setViewOptionsPopupRows(int viewOptionsPopupRows) {
        this.viewOptionsPopupRows = viewOptionsPopupRows;
    }
}

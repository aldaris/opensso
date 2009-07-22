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
 * $Id: ApplicationWizardBean.java,v 1.2 2009-07-22 20:32:17 farble1670 Exp $
 */

package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.dao.ViewApplicationTypeDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import static com.sun.identity.admin.model.ApplicationWizardStep.*;

public class ApplicationWizardBean extends WizardBean {
    private boolean nameEditable = false;
    private ApplicationBean applicationBean = new ApplicationBean();
    private Effect nameInputEffect;
    private Map<String,ViewApplicationType> viewApplicationTypeMap;

    @Override
    public void reset() {
        super.reset();

        applicationBean = new ApplicationBean();

        viewApplicationTypeMap = ViewApplicationTypeDao.getInstance().getViewApplicationTypeMap();
        applicationBean.setViewApplicationType(viewApplicationTypeMap.entrySet().iterator().next().getValue());
    }

    public String getViewApplicationTypeName() {
        return applicationBean.getViewApplicationType().getName();
    }

    public void setViewApplicationTypeName(String name) {
        ViewApplicationType vat = viewApplicationTypeMap.get(name);
        assert(vat != null);
        applicationBean.setViewApplicationType(vat);
    }

    public List<SelectItem> getViewApplicationTypeNameItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (Map.Entry<String,ViewApplicationType> entry: viewApplicationTypeMap.entrySet()) {
            SelectItem si = new SelectItem(entry.getValue().getName(), entry.getValue().getTitle());
            items.add(si);
        }

        return items;
    }

    public ApplicationBean getApplicationBean() {
        return applicationBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public Effect getNameInputEffect() {
        return nameInputEffect;
    }

    public void setNameInputEffect(Effect nameInputEffect) {
        this.nameInputEffect = nameInputEffect;
    }

    public boolean isNameEditable() {
        return nameEditable;
    }

    public void setNameEditable(boolean nameEditable) {
        this.nameEditable = nameEditable;
    }

    private String getPanelLabel(ApplicationWizardStep aws) {
        Resources r = new Resources();
        String label;

        switch (aws) {
            case NAME:
                label = r.getString(this, "namePanelLabel");
                break;

            case ACTIONS:
                // TODO: count
                label = r.getString(this, "actionsPanelLabel");
                break;

            case CONDITIONS:
                // TODO: count
                label = r.getString(this, "conditionsPanelLabel");
                break;

            case OVERRIDE:
                label = r.getString(this, "overridePanelLabel");
                break;

            case SUMMARY:
                label = r.getString(this, "summaryPanelLabel");
                break;

            default:
                throw new AssertionError("unhandled application wizard step: " + aws);
        }

        return label;
    }

    public String getNamePanelLabel() {
        return getPanelLabel(NAME);
    }

    public String getActionsPanelLabel() {
        return getPanelLabel(ACTIONS);
    }

    public String getConditionsPanelLabel() {
        return getPanelLabel(CONDITIONS);
    }

    public String getOverridePanelLabel() {
        return getPanelLabel(OVERRIDE);
    }

    public String getSummaryPanelLabel() {
        return getPanelLabel(SUMMARY);
    }
}

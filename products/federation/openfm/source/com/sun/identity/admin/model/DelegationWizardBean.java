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
 * $Id: DelegationWizardBean.java,v 1.3 2009-10-09 20:17:14 farble1670 Exp $
 */
package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import static com.sun.identity.admin.model.DelegationWizardStep.*;

public abstract class DelegationWizardBean extends WizardBean {

    private boolean nameEditable = false;
    private Effect nameInputEffect;
    private  DelegationBean delegationBean = new DelegationBean();
    private List<Resource> availableResources;
    private ViewApplicationsBean viewApplicationsBean;

    @Override
    public void reset() {
        super.reset();
        reset(true, null);
    }

    private void reset(boolean resetName, ViewApplicationType vat) {
        resetAvailableResources();
    }

    public List<Resource> getResources() {
        return delegationBean.getResources();
    }

    public void setResources(List<Resource> resources) {
        delegationBean.setResources(new ArrayList<Resource>());
        for (Resource r : resources) {
            int i = getAvailableResources().indexOf(r);
            assert (i != -1);
            r = getAvailableResources().get(i);
            delegationBean.getResources().add(r);
        }
    }

    public List<SelectItem> getAvailableResourceItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        if (getAvailableResources() != null) {
            for (Resource r : getAvailableResources()) {
                ApplicationResource ar = (ApplicationResource) r;
                items.add(new SelectItem(ar, ar.getTitle()));
            }
        }

        return items;
    }

    private void resetAvailableResources() {
        availableResources = new ArrayList<Resource>();
        for (ViewApplication va : viewApplicationsBean.getViewApplications().values()) {
            ApplicationResource ar = new ApplicationResource();
            ar.setName(va.getName());
            ar.getViewEntitlement().setResources(ar.getViewEntitlement().getAvailableResources());

            availableResources.add(ar);
        }
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

    private String getPanelLabel(DelegationWizardStep aws) {
        Resources r = new Resources();
        String label;

        // TODO: add counts to labal strings

        switch (aws) {
            case NAME:
                label = r.getString(this, "namePanelLabel");
                break;

            case RESOURCES:
                label = r.getString(this, "resourcesPanelLabel");
                break;

            case SUBJECTS:
                label = r.getString(this, "subjectsPanelLabel");
                break;

            case ACTIONS:
                label = r.getString(this, "actionsPanelLabel");
                break;

            case SUMMARY:
                label = r.getString(this, "summaryPanelLabel");
                break;

            default:
                throw new AssertionError("unhandled delegation wizard step: " + aws);
        }

        return label;
    }

    public String getNamePanelLabel() {
        return getPanelLabel(NAME);
    }

    public String getResourcesPanelLabel() {
        return getPanelLabel(RESOURCES);
    }

    public String getSubjectsPanelLabel() {
        return getPanelLabel(SUBJECTS);
    }

    public String getActionsPanelLabel() {
        return getPanelLabel(ACTIONS);
    }

    public String getSummaryPanelLabel() {
        return getPanelLabel(SUMMARY);
    }

    public DelegationBean getDelegationBean() {
        return delegationBean;
    }

    public void setDelegationBean(DelegationBean delegationBean) {
        this.delegationBean = delegationBean;
    }

    public List<Resource> getAvailableResources() {
        return availableResources;
    }

    public void setViewApplicationsBean(ViewApplicationsBean viewApplicationsBean) {
        this.viewApplicationsBean = viewApplicationsBean;
    }
}

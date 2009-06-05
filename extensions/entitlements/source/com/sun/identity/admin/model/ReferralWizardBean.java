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
 * $Id: ReferralWizardBean.java,v 1.4 2009-06-05 16:45:44 farble1670 Exp $
 */
package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.dao.RealmDao;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import org.apache.commons.collections.comparators.NullComparator;

public abstract class ReferralWizardBean extends WizardBean {

    private ReferralBean referralBean;
    private Effect nameInputEffect;
    private List<Resource> availableResources;
    private ViewApplicationsBean viewApplicationsBean;
    private List<RealmBean> availableRealmBeans = new ArrayList<RealmBean>();
    private List<RealmBean> selectedAvailableRealmBeans = new ArrayList<RealmBean>();
    private List<RealmBean> selectedRealmBeans = new ArrayList<RealmBean>();
    private String subjectFilter = "";
    private RealmDao realmDao;

    public ReferralWizardBean() {
        super();
    }

    public List<SelectItem> getAvailableResourceItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        if (getAvailableResources() != null) {
            for (Resource r : getAvailableResources()) {
                ReferralResource rr = (ReferralResource) r;
                items.add(new SelectItem(rr, rr.getTitle()));
            }
        }

        return items;
    }

    public List<SelectItem> getAvailableRealmBeanItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        if (availableRealmBeans != null) {
            for (RealmBean rb : availableRealmBeans) {
                items.add(new SelectItem(rb, rb.getTitle()));
            }
        }

        return items;
    }

    public abstract boolean isNameEditable();

    public ReferralBean getReferralBean() {
        return referralBean;
    }

    @Override
    public void reset() {
        super.reset();
        referralBean = new ReferralBean();
        resetAvailableResources();
        resetAvailableRealmBeans();
    }

    private void resetAvailableResources() {
        availableResources = new ArrayList<Resource>();
        for (ViewApplication va : viewApplicationsBean.getViewApplications().values()) {
            ReferralResource rr = new ReferralResource();
            rr.setName(va.getName());
            rr.getViewEntitlement().setResources(rr.getViewEntitlement().getAvailableResources());

            getAvailableResources().add(rr);
        }
    }

    public Effect getNameInputEffect() {
        return nameInputEffect;
    }

    public void setNameInputEffect(Effect nameInputEffect) {
        this.nameInputEffect = nameInputEffect;
    }

    public void setViewApplicationsBean(ViewApplicationsBean viewApplicationsBean) {
        this.viewApplicationsBean = viewApplicationsBean;
    }

    public List<Resource> getAvailableResources() {
        return availableResources;
    }

    public List<RealmBean> getAvailableRealmBeans() {
        return availableRealmBeans;
    }

    public List<RealmBean> getSelectedAvailableRealmBeans() {
        return selectedAvailableRealmBeans;
    }

    public void setSelectedAvailableRealmBeans(List<RealmBean> selectedAvailableRealmBeans) {
        this.selectedAvailableRealmBeans = selectedAvailableRealmBeans;
    }

    public List<RealmBean> getSelectedRealmBeans() {
        return selectedRealmBeans;
    }

    public void setSelectedRealmBeans(List<RealmBean> selectedRealmBeans) {
        this.selectedRealmBeans = selectedRealmBeans;
    }

    public String getSubjectFilter() {
        return subjectFilter;
    }

    public void resetAvailableRealmBeans() {
        availableRealmBeans = realmDao.getRealmBeans(null, subjectFilter);
        availableRealmBeans.removeAll(referralBean.getRealmBeans());
        selectedAvailableRealmBeans = new ArrayList<RealmBean>();
    }

    public void setSubjectFilter(String subjectFilter) {
        if (subjectFilter == null) {
            subjectFilter = "";
        }
        NullComparator n = new NullComparator();
        if (n.compare(this.subjectFilter, subjectFilter) != 0) {
            this.subjectFilter = subjectFilter;
            resetAvailableRealmBeans();
        }
    }

    public void setRealmDao(RealmDao realmDao) {
        this.realmDao = realmDao;
    }
}

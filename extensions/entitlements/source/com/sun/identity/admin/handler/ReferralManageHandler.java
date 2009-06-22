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
 * $Id: ReferralManageHandler.java,v 1.1 2009-06-22 14:53:20 farble1670 Exp $
 */

package com.sun.identity.admin.handler;

import com.sun.identity.admin.dao.ReferralDao;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.PhaseEventAction;
import com.sun.identity.admin.model.QueuedActionBean;
import com.sun.identity.admin.model.ReferralBean;
import com.sun.identity.admin.model.ReferralManageBean;
import com.sun.identity.admin.model.ReferralWizardBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

public class ReferralManageHandler implements Serializable {
    private ReferralManageBean referralManageBean;
    private QueuedActionBean queuedActionBean;
    private ReferralDao referralDao;
    private ReferralWizardBean referralEditWizardBean;
    private MessagesBean messagesBean;

    public void selectAllListener(ActionEvent event) {
        referralManageBean.setSelectAll(!referralManageBean.isSelectAll());
        for (ReferralBean rb : referralManageBean.getReferralBeans()) {
            rb.setSelected(referralManageBean.isSelectAll());
        }
    }

    public void sortTableListener(ActionEvent event) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(true);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{referralManageHandler.handleSort}");
        pea.setParameters(new Class[]{});
        pea.setArguments(new Object[]{});

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public void handleSort() {
        referralManageBean.getReferralManageTableBean().sort();
    }

    public void setReferralManageBean(ReferralManageBean referralManageBean) {
        this.referralManageBean = referralManageBean;
    }

    public void setQueuedActionBean(QueuedActionBean queuedActionBean) {
        this.queuedActionBean = queuedActionBean;
    }

    public void setReferralDao(ReferralDao referralDao) {
        this.referralDao = referralDao;
    }

    public void setReferralEditWizardBean(ReferralWizardBean referralEditWizardBean) {
        this.referralEditWizardBean = referralEditWizardBean;
    }

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }

    public ReferralBean getReferralBean(ActionEvent event) {
        ReferralBean rb = (ReferralBean) event.getComponent().getAttributes().get("referralBean");
        assert (rb != null);
        return rb;
    }

    public void editListener(ActionEvent event) {
        ReferralBean rb = getReferralBean(event);
        referralEditWizardBean.reset();
        referralEditWizardBean.setReferralBean(rb);
        referralEditWizardBean.setAllEnabled(true);
        referralEditWizardBean.gotoStep(3);
    }

}

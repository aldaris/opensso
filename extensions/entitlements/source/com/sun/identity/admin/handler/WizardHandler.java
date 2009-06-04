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
 * $Id: WizardHandler.java,v 1.10 2009-06-04 21:18:36 farble1670 Exp $
 */

package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.WizardBean;
import com.sun.identity.admin.model.WizardStepBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class WizardHandler implements Serializable {
    private WizardBean wizardBean = null;

    public WizardBean getWizardBean() {
        return wizardBean;
    }

    public void setWizardBean(WizardBean wizardBean) {
        this.wizardBean = wizardBean;
    }

    protected int getStep(ActionEvent event) {
        String val = (String) event.getComponent().getAttributes().get("step");
        int step = Integer.parseInt(val);

        return step;
    }

    protected int getGotoStep(ActionEvent event) {
        String val = (String) event.getComponent().getAttributes().get("gotoStep");
        int step = Integer.parseInt(val);

        return step;
    }

    public void gotoStepListener(ActionEvent event) {
        int gotoStep = getGotoStep(event);

        getWizardBean().gotoStep(gotoStep);
    }

    public void expandListener(ActionEvent event) {
        int step = getStep(event);
        int steps = getWizardBean().getSteps();

        assert(step <= steps-1);
        
        for (int i = 0; i < steps; i++) {
            WizardStepBean ws = getWizardBean().getWizardStepBeans()[i];
            if (i != step) {
                ws.setExpanded(false);
            }
        }
    }

    public void nextListener(ActionEvent event) {
        int step = getStep(event);
        int steps = getWizardBean().getSteps();

        assert (step <= steps-1);

        WizardStepBean current = getWizardBean().getWizardStepBeans()[step];
        current.setExpanded(false);

        WizardStepBean next = getWizardBean().getWizardStepBeans()[step+1];
        next.setEnabled(true);
        next.setExpanded(true);
    }

    public void previousListener(ActionEvent event) {
        int step = getStep(event);
        int steps = getWizardBean().getSteps();

        assert (step != 0);
        assert (step <= steps-1);

        WizardStepBean current = getWizardBean().getWizardStepBeans()[step];
        current.setExpanded(false);

        WizardStepBean previous = getWizardBean().getWizardStepBeans()[step-1];
        previous.setExpanded(true);
    }

    public String finishAction() {
        getWizardBean().reset();
        return "finish";
    }

    public String cancelAction() {
        getWizardBean().reset();
        return "cancel";
    }
}

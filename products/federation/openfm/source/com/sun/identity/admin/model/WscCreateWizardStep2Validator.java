/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * $Id: WscCreateWizardStep2Validator.java,v 1.2 2009-10-06 18:28:03 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.effect.InputFieldErrorEffect;
import com.sun.identity.admin.effect.MessageErrorEffect;
import java.net.MalformedURLException;
import java.net.URL;
import javax.faces.application.FacesMessage;

public class WscCreateWizardStep2Validator 
        extends WscCreateWizardStepValidator
{
    public WscCreateWizardStep2Validator(WizardBean wb) {
        super(wb);
    }

    @Override
    public boolean validate() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        SecurityTokenServiceType stst 
                = SecurityTokenServiceType.valueOf(wb.getStsType());

        switch(stst) {
            case OPENSSO:
                if( !validDeploymentUrl() ) {
                    return false;
                }
                break;
            case OTHER:
                if( !validEndPoint() ) {
                    return false;
                } else if( !validMexEndPoint() ) {
                    return false;
                }
                break;
            case NONE:
                break;
        }
        
        return true;
    }

    private boolean validDeploymentUrl() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        String deploymentUrl = wb.getOpenssoStsUrl();
        String pattern = ".{1,1024}?";
        try {
            @SuppressWarnings("unused")
            URL url = new URL(deploymentUrl);
            if( deploymentUrl != null && deploymentUrl.matches(pattern) ) {
                return true;
            }
        } catch (MalformedURLException ex) {
            // do nothing but flow through below for error message
        }

        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidDeploymentUrlSummary"));
        mb.setDetail(r.getString(this, "invalidDeploymentUrlDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        Effect e;
        e = new InputFieldErrorEffect();
        wb.setOpenssoStsUrlInputEffect(e);

        e = new MessageErrorEffect();
        wb.setOpenssoStsUrlMessageEffect(e);

        getMessagesBean().addMessageBean(mb);
        return false;
    }

    private boolean validEndPoint() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        String endPoint = wb.getStsProfileBean().getEndPoint();
        String pattern = ".{1,1024}?";

        try {
            @SuppressWarnings("unused")
            URL url = new URL(endPoint);
            if( endPoint != null && endPoint.matches(pattern) ) {
                return true;
            }
        } catch (MalformedURLException ex) {
            // do nothing but flow through below for error message
        }

        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidEndPointSummary"));
        mb.setDetail(r.getString(this, "invalidEndPointDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        Effect e;
        e = new InputFieldErrorEffect();
        wb.getStsProfileBean().setEndPointInputEffect(e);

        e = new MessageErrorEffect();
        wb.getStsProfileBean().setEndPointMessageEffect(e);

        getMessagesBean().addMessageBean(mb);
        return false;
    }

    private boolean validMexEndPoint() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        String mexEndPoint = wb.getStsProfileBean().getMexEndPoint();
        String pattern = ".{1,1024}?";

        try {
            @SuppressWarnings("unused")
            URL url = new URL(mexEndPoint);
            if( mexEndPoint != null && mexEndPoint.matches(pattern) ) {
                return true;
            }
        } catch (MalformedURLException ex) {
            // do nothing but flow through below for error message
        }

        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidMexEndPointSummary"));
        mb.setDetail(r.getString(this, "invalidMexEndPointDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        Effect e;
        e = new InputFieldErrorEffect();
        wb.getStsProfileBean().setMexEndPointInputEffect(e);

        e = new MessageErrorEffect();
        wb.getStsProfileBean().setMexEndPointMessageEffect(e);

        getMessagesBean().addMessageBean(mb);
        return false;
    }
}

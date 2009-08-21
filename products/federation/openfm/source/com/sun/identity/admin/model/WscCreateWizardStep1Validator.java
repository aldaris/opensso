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
 * $Id: WscCreateWizardStep1Validator.java,v 1.1 2009-08-21 21:07:35 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.effect.InputFieldErrorEffect;
import com.sun.identity.admin.effect.MessageErrorEffect;
import java.net.MalformedURLException;
import java.net.URL;
import javax.faces.application.FacesMessage;

public class WscCreateWizardStep1Validator 
        extends WscCreateWizardStepValidator
{
    public WscCreateWizardStep1Validator(WizardBean wb) {
        super(wb);
    }

    @Override
    public boolean validate() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        WssClientProfileBean wsc = wb.getWscProfileBean();

        if( !validProfileName() ) {
            return false;
        }
        
        if( !validEndPoint() ) {
            return false;
        }
        
        if( wsc.isUsingMexEndPoint() && !validMexEndPoint() ) {
            return false;
        }
        
        return true;
    }

    private boolean validProfileName() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        String profileName = wb.getWscProfileBean().getProfileName();
        String pattern = "[\\w ]{1,255}?";
        
        if( profileName != null && profileName.matches(pattern) ) {
            return true;
        }

        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidProfileNameSummary"));
        mb.setDetail(r.getString(this, "invalidProfileNameDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        Effect e;
        e = new InputFieldErrorEffect();
        wb.getWscProfileBean().setProfileNameInputEffect(e);

        e = new MessageErrorEffect();
        wb.getWscProfileBean().setProfileNameMessageEffect(e);

        getMessagesBean().addMessageBean(mb);
        return false;
    }

    private boolean validEndPoint() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        String endPoint = wb.getWscProfileBean().getEndPoint();
        String pattern = ".{1,1024}?";

        try {
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
        wb.getWscProfileBean().setEndPointInputEffect(e);

        e = new MessageErrorEffect();
        wb.getWscProfileBean().setEndPointMessageEffect(e);

        getMessagesBean().addMessageBean(mb);
        return false;
    }

    private boolean validMexEndPoint() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        String mexEndPoint = wb.getWscProfileBean().getMexEndPoint();
        String pattern = ".{1,1024}?";

        try {
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
        wb.getWscProfileBean().setMexEndPointInputEffect(e);

        e = new MessageErrorEffect();
        wb.getWscProfileBean().setMexEndPointMessageEffect(e);

        getMessagesBean().addMessageBean(mb);
        return false;
    }
}

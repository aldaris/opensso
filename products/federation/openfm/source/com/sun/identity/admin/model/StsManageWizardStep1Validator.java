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
 * $Id: StsManageWizardStep1Validator.java,v 1.1 2009-09-17 21:56:04 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import javax.faces.application.FacesMessage;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.effect.InputFieldErrorEffect;
import com.sun.identity.admin.effect.MessageErrorEffect;

public class StsManageWizardStep1Validator 
        extends StsManageWizardStepValidator
{
    public StsManageWizardStep1Validator(WizardBean wb) {
        super(wb);
    }

    @Override
    public boolean validate() {
        
        if( !validIssuer() ) {
            return false;
        }
        
        if( !validTokenLifetime() ) {
            return false;
        }
        
        if( !validKeyAlias() ) {
            return false;
        }
        
        if( !validTokenPluginClass() ) {
            return false;
        }
        
        return true;
    }

    private boolean validTokenPluginClass() {
        String tokenPluginClass = getStsManageWizardBean().getTokenPluginClass();
        boolean result = false;
        
        try {
            Class.forName(tokenPluginClass);
            result = true;
        } catch (ClassNotFoundException cnfe) {
            MessageBean mb = new MessageBean();
            Resources r = new Resources();
            mb.setSummary(r.getString(this, "invalidTokenPluginClassSummary"));
            mb.setDetail(r.getString(this, "invalidTokenPluginClassDetail"));
            mb.setSeverity(FacesMessage.SEVERITY_ERROR);
            
            Effect e;
            e = new InputFieldErrorEffect();
            getStsManageWizardBean().setTokenPluginClassInputEffect(e);
            
            e = new MessageErrorEffect();
            getStsManageWizardBean().setTokenPluginClassMessageEffect(e);
            
            getMessagesBean().addMessageBean(mb);
        }
        
        return result;
    }

    private boolean validKeyAlias() {
        if( getStsManageWizardBean().getKeyAlias() != null) {
            return true;
        }
        
        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidKeyAliasSummary"));
        mb.setDetail(r.getString(this, "invalidKeyAliasDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);
        
        getMessagesBean().addMessageBean(mb);
        return false;
    }

    private boolean validTokenLifetime() {
        if( getStsManageWizardBean().getTokenLifetime() > 0 ) {
            return true;
        }
        
        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidTokenLifetimeSummary"));
        mb.setDetail(r.getString(this, "invalidTokenLifetimeDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);
        
        Effect e;
        e = new InputFieldErrorEffect();
        getStsManageWizardBean().setTokenLifetimeInputEffect(e);
        
        e = new MessageErrorEffect();
        getStsManageWizardBean().setTokenLifetimeMessageEffect(e);
        
        getMessagesBean().addMessageBean(mb);
        
        return false;
    }

    private boolean validIssuer() {
        String issuer = getStsManageWizardBean().getIssuer();
        String pattern = "[\\w ]{1,255}?";
        
        if( issuer != null && issuer.matches(pattern) ) {
            return true;
        }
        
        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidIssuerSummary"));
        mb.setDetail(r.getString(this, "invalidIssuerDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);
        
        Effect e;
        e = new InputFieldErrorEffect();
        getStsManageWizardBean().setIssuerInputEffect(e);
        
        e = new MessageErrorEffect();
        getStsManageWizardBean().setIssuerMessageEffect(e);
        
        getMessagesBean().addMessageBean(mb);
        
        return false;
    }

}

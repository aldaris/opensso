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
 * $Id: WscCreateWizardStep3Validator.java,v 1.1 2009-08-21 21:07:35 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.effect.InputFieldErrorEffect;
import com.sun.identity.admin.effect.MessageErrorEffect;
import javax.faces.application.FacesMessage;

public class WscCreateWizardStep3Validator 
        extends WscCreateWizardStepValidator
{
    public WscCreateWizardStep3Validator(WizardBean wb) {
        super(wb);
    }

    @Override
    public boolean validate() {
        WscCreateWizardBean wb = getWscCreateWizardBean();
        WssClientProfileBean profileBean;

        if( wb.isUsingSts() ) {
            profileBean = wb.getStsProfileBean();
        } else {
            profileBean = wb.getWscProfileBean();
        }

        SecurityMechanism sm
                = SecurityMechanism.valueOf(profileBean.getSecurityMechanism());

        switch(sm) {
            case ANONYMOUS:
            case SAML2_HOK:
            case SAML2_SV:
            case SAML_HOK:
            case SAML_SV:
                // no additional fields to validate
                break;
            case USERNAME_TOKEN:
            case USERNAME_TOKEN_PLAIN:
                if( !validUserNameTokenSettings(profileBean) ) {
                    return false;
                }
                break;
            case KERBEROS_TOKEN:
                if( !validKerberosSettings(profileBean) ) {
                    return false;
                }
                break;
            case X509_TOKEN:
                if( !validX509TokenSettings(profileBean) ) {
                    return false;
                }
                break;
            default:
                noSecurityMechanism();
                return false;
        }

        return true;
    }

    private void noSecurityMechanism() {
        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidNoSecuritySettingsSummary"));
        mb.setDetail(r.getString(this, "invalidNoSecuritySettingsDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        getMessagesBean().addMessageBean(mb);
    }

    private boolean validUserNameTokenSettings(WssClientProfileBean pb) {
        String userName = pb.getUserNameTokenUserName();
        String password = pb.getUserNameTokenPassword();
        String pattern = "[\\w ]{0,50}?";

        if( (userName == null || userName.matches(pattern)) &&
            (password == null || password.matches(pattern)) )
        {
            return true;
        }

        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidUserNameTokenSettingsSummary"));
        mb.setDetail(r.getString(this, "invalidUserNameTokenSettingsDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        Effect e;
        e = new InputFieldErrorEffect();
        pb.setUserNameTokenUserNameInputEffect(e);
        pb.setUserNameTokenPasswordInputEffect(e);

        e = new MessageErrorEffect();
        pb.setUserNameTokenUserNameMessageEffect(e);
        pb.setUserNameTokenPasswordMessageEffect(e);

        getMessagesBean().addMessageBean(mb);
        return false;
    }

    private boolean validKerberosSettings(WssClientProfileBean pb) {
        String domain = pb.getKerberosDomain();
        String domainServer = pb.getKerberosDomainServer();
        String serverPrincipal = pb.getKerberosServicePrincipal();
        String ticketCache = pb.getKerberosTicketCache();
        String pattern = "[\\w \\@\\.\\/\\&\\:]{0,255}?";
        String summary = null;
        String detail = null;
        Resources r = new Resources();

        if(domain != null && !domain.matches(pattern)) {
            summary = r.getString(this, "invalidKerberosDomainSummary");
            detail = r.getString(this, "invalidKerberosDomainDetail");
            pb.setKerberosDomainInputEffect(new InputFieldErrorEffect());
            pb.setKerberosDomainMessageEffect(new MessageErrorEffect());

        } else if(domainServer != null && !domainServer.matches(pattern)) {
            summary = r.getString(this, "invalidKerberosDomainServerSummary");
            detail = r.getString(this, "invalidKerberosDomainServerDetail");
            pb.setKerberosDomainServerInputEffect(new InputFieldErrorEffect());
            pb.setKerberosDomainServerMessageEffect(new MessageErrorEffect());

        } else if(serverPrincipal != null && !serverPrincipal.matches(pattern)) {
            summary = r.getString(this, "invalidKerberosServicePrincipalSummary");
            detail = r.getString(this, "invalidKerberosServicePrincipalDetail");
            pb.setKerberosServicePrincipalInputEffect(new InputFieldErrorEffect());
            pb.setKerberosServicePrincipalMessageEffect(new MessageErrorEffect());

        } else if(ticketCache != null && !ticketCache.matches(pattern)) {
            summary = r.getString(this, "invalidKerberosServicePrincipalSummary");
            detail = r.getString(this, "invalidKerberosServicePrincipalDetail");
            pb.setKerberosTicketCacheInputEffect(new InputFieldErrorEffect());
            pb.setKerberosTicketCacheMessageEffect(new MessageErrorEffect());

        }

        if( summary != null ) {
            MessageBean mb = new MessageBean();
            mb.setSummary(summary);
            mb.setDetail(detail);
            mb.setSeverity(FacesMessage.SEVERITY_ERROR);
            getMessagesBean().addMessageBean(mb);
            return false;
        }

        return true;
    }

    private boolean validX509TokenSettings(WssClientProfileBean pb) {
        X509SigningRefType signingRef 
                = X509SigningRefType.valueOf(pb.getX509TokenSigningReferenceType());

        if( signingRef != null ) {
            return true;
        }

        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, "invalidX509TokenSettingsSummary"));
        mb.setDetail(r.getString(this, "invalidX509TokenSettingsDetail"));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        getMessagesBean().addMessageBean(mb);
        return false;
    }
}

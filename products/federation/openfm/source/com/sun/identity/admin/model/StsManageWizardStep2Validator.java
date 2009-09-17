/*
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
 * $Id: StsManageWizardStep2Validator.java,v 1.1 2009-09-17 21:56:04 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import javax.faces.application.FacesMessage;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.effect.InputFieldErrorEffect;
import com.sun.identity.admin.effect.MessageErrorEffect;

public class StsManageWizardStep2Validator 
        extends StsManageWizardStepValidator
{
    public StsManageWizardStep2Validator(WizardBean wb) {
        super(wb);
    }

    @Override
    public boolean validate() {
        
        if( !validSecurityTokenType() ) {
            return false;
        }
        
        if( !validAuthChain() ) {
            return false;
        }
        
        return true;
    }

    private boolean validAuthChain() {
        WssProviderProfileBean pb = getStsManageWizardBean().getStsProfileBean();
        
        if( pb.getAuthenticationChain() != null ) {
            return true;
        }
        
        showErrorMessage("invalidAuthChainSummary", "invalidAuthChainDetail");
        return false;
    }

    private boolean validSecurityTokenType() {
        WssProviderProfileBean stsProfile 
                                = getStsManageWizardBean().getStsProfileBean();
        Integer[] chosenTypes = stsProfile.getSecurityMechanisms();
        
        if( chosenTypes == null || chosenTypes.length == 0 ) {
            showErrorMessage("noSecurityTokenTypeSummary", "noSecurityTokenTypeDetail");
            return false;
        }
        
        for(Integer i : chosenTypes) {
            boolean valid = false;
            SecurityMechanism sm = SecurityMechanism.valueOf(i.intValue());
            
            switch(sm) {
                case ANONYMOUS:
                case SAML_HOK:
                case SAML_SV:
                case SAML2_HOK:
                case SAML2_SV:
                    // no additional validation needed
                    valid = true;
                    break;
                case USERNAME_TOKEN:
                case USERNAME_TOKEN_PLAIN:
                    valid = validUserNameTokenSettings();
                    break;
                case KERBEROS_TOKEN:
                    valid = validKerberosTokenSettings();
                    break;
                case X509_TOKEN:
                    valid = validX509TokenSettings();
                    break;
                default:
                    unsupportedSecurityTokenType();
                    break;
            }
            
            if( !valid ) {
                return false;
            }
        }
        
        return true;
    }
    
	private void showErrorMessage(String summaryKey, String detailKey) {
        MessageBean mb = new MessageBean();
        Resources r = new Resources();
        mb.setSummary(r.getString(this, summaryKey));
        mb.setDetail(r.getString(this, detailKey));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);
        
        getMessagesBean().addMessageBean(mb);
    }
    
    private void unsupportedSecurityTokenType() {
        showErrorMessage("unsupportedSecurityTokenTypeSummary",
                         "unsupportedSecurityTokenTypeDetail");
    }

    private boolean validUserNameTokenSettings() {
        WssProviderProfileBean pb 
                                = getStsManageWizardBean().getStsProfileBean();
        String userName = pb.getUserNameTokenUserName();
        String password = pb.getUserNameTokenPassword();
        String pattern = "[\\w ]{0,50}?";
        
        if( (userName == null || userName.matches(pattern)) &&
            (password == null || password.matches(pattern)) )
        {
            return true;
        }
        
        Effect e;
        e = new InputFieldErrorEffect();
        pb.setUserNameTokenUserNameInputEffect(e);
        pb.setUserNameTokenPasswordInputEffect(e);
        
        e = new MessageErrorEffect();
        pb.setUserNameTokenUserNameMessageEffect(e);
        pb.setUserNameTokenPasswordMessageEffect(e);
        
        showErrorMessage("invalidUserNameSummary",
                         "invalidUserNameDetail");
        
        return false;
    }

    private boolean validKerberosTokenSettings() {
        WssProviderProfileBean pb 
                                = getStsManageWizardBean().getStsProfileBean();
        String domain = pb.getKerberosDomain();
        String domainServer = pb.getKerberosDomainServer();
        String serverPrincipal = pb.getKerberosServicePrincipal();
        String keyTabFile = pb.getKerberosKeyTabFile();
        String pattern = "[\\w \\@\\.\\/\\&\\:]{0,255}?";
        String summaryKey = null;
        String detailKey = null;
        
        if(domain != null && !domain.matches(pattern)) {
            summaryKey = "invalidKerberosDomainSummary";
            detailKey = "invalidKerberosDomainDetail";
            pb.setKerberosDomainInputEffect(new InputFieldErrorEffect());
            pb.setKerberosDomainMessageEffect(new MessageErrorEffect());
            
        } else if(domainServer != null && !domainServer.matches(pattern)) {
            summaryKey = "invalidKerberosDomainServerSummary";
            detailKey = "invalidKerberosDomainServerDetail";
            pb.setKerberosDomainServerInputEffect(new InputFieldErrorEffect());
            pb.setKerberosDomainServerMessageEffect(new MessageErrorEffect());
            
        } else if(serverPrincipal != null && !serverPrincipal.matches(pattern)) {
            summaryKey = "invalidKerberosServicePrincipalSummary";
            detailKey = "invalidKerberosServicePrincipalDetail";
            pb.setKerberosServicePrincipalInputEffect(new InputFieldErrorEffect());
            pb.setKerberosServicePrincipalMessageEffect(new MessageErrorEffect());
            
        } else if(keyTabFile != null && !keyTabFile.matches(pattern)) {
            summaryKey = "invalidKerberosKeyTabFileSummary";
            detailKey = "invalidKerberosKeyTabFileDetail";
            pb.setKerberosKeyTabFileInputEffect(new InputFieldErrorEffect());
            pb.setKerberosKeyTabFileMessageEffect(new MessageErrorEffect());
            
        }
        
        if( summaryKey != null ) {
            showErrorMessage(summaryKey, detailKey);
            return false;
        }
        
        return true;
    }

    private boolean validX509TokenSettings() {
        WssProviderProfileBean pb = getStsManageWizardBean().getStsProfileBean();
        X509SigningRefType signingRef 
            = X509SigningRefType.valueOf(pb.getX509TokenSigningReferenceType());
        
        if( signingRef != null ) {
            return true;
        }
        
        showErrorMessage("invalidX509TokenSettingsSummary",
                         "invalidX509TokenSettingsDetail");
        return false;
    }
    
}

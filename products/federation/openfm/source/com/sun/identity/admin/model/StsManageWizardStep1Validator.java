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
 * $Id: StsManageWizardStep1Validator.java,v 1.2 2009-09-30 22:01:27 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
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

    private boolean validIssuer() {
        String issuer = getStsManageWizardBean().getIssuer();
        String pattern = "[\\w ]{1,255}?";
        
        if( issuer != null && issuer.matches(pattern) ) {
            return true;
        }
        
        Effect e;
        e = new InputFieldErrorEffect();
        getStsManageWizardBean().setIssuerInputEffect(e);
        
        e = new MessageErrorEffect();
        getStsManageWizardBean().setIssuerMessageEffect(e);

        showErrorMessage("invalidIssuerSummary", "invalidIssuerDetail");
        return false;
    }    
    
    private boolean validTokenLifetime() {
        if( getStsManageWizardBean().getTokenLifetime() > 0 ) {
            return true;
        }
        
        Effect e;
        e = new InputFieldErrorEffect();
        getStsManageWizardBean().setTokenLifetimeInputEffect(e);
        
        e = new MessageErrorEffect();
        getStsManageWizardBean().setTokenLifetimeMessageEffect(e);

        showErrorMessage("invalidTokenLifetimeSummary", 
                         "invalidTokenLifetimeDetail");
        return false;
    }
    
    private boolean validKeyAlias() {

        if( getStsManageWizardBean().getKeyAlias() != null) {
            return true;
        }
        
        showErrorMessage("invalidKeyAliasSummary", "invalidKeyAliasDetail");
        return false;
    }
    
    private boolean validTokenPluginClass() {
        String tokenPluginClass = getStsManageWizardBean().getTokenPluginClass();
        boolean result = false;
        
        try {
            Class.forName(tokenPluginClass);
            result = true;
        } catch (ClassNotFoundException cnfe) {
            Effect e;
            e = new InputFieldErrorEffect();
            getStsManageWizardBean().setTokenPluginClassInputEffect(e);
            
            e = new MessageErrorEffect();
            getStsManageWizardBean().setTokenPluginClassMessageEffect(e);
            
            showErrorMessage("invalidTokenPluginClassSummary", 
                             "invalidTokenPluginClassDetail");
        }
        
        return result;
    }

}

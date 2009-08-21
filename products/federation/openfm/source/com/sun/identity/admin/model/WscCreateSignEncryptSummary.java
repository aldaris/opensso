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
 * $Id: WscCreateSignEncryptSummary.java,v 1.1 2009-08-21 21:07:35 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import com.sun.identity.admin.ListFormatter;
import com.sun.identity.admin.Resources;
import java.util.ArrayList;

public class WscCreateSignEncryptSummary extends WscCreateWizardSummary {

    public WscCreateSignEncryptSummary(WscCreateWizardBean wizardBean) {
        super(wizardBean);
    }

    @Override
    public String getLabel() {
        Resources r = new Resources();
        String label = r.getString(this, "label");
        return label;
    }

    @Override
    public String getValue() {
        Resources r = new Resources();

        return r.getString(this, "value");
    
    }


    public String getFormattedStsMessageFlags() {
        WscCreateWizardBean wizardBean = getWscCreateWizardBean();
        return getFormattedMessageFlags(wizardBean.getStsProfileBean());
    }

    public String getFormattedWscMessageFlags() {
        WscCreateWizardBean wizardBean = getWscCreateWizardBean();
        return getFormattedMessageFlags(wizardBean.getWscProfileBean());
    }

    public String getFormattedStsEncryptionAlgorithm() {
        WscCreateWizardBean wizardBean = getWscCreateWizardBean();
        return getFormattedEncryptionAlgorithm(wizardBean.getStsProfileBean());
    }

    public String getFormattedWscEncryptionAlgorithm() {
        WscCreateWizardBean wizardBean = getWscCreateWizardBean();
        return getFormattedEncryptionAlgorithm(wizardBean.getWscProfileBean());
    }

    private String getFormattedEncryptionAlgorithm(WssClientProfileBean profile) {
        EncryptionAlgorithm ea
                = EncryptionAlgorithm.valueOf(profile.getEncryptionAlgorithm());

        return (ea == null) ? null : ea.toLocaleString();
    }


    private String getFormattedMessageFlags(WssClientProfileBean profile) {
        ArrayList a = new ArrayList();
        Resources r = new Resources();

        if( profile.isRequestSigned() ) {
            a.add(" " + r.getString(this, "requestSigned"));
        }
        if( profile.isRequestHeaderEncrypted() ) {
            a.add(" " + r.getString(this, "requestHeaderEncrypted"));
        }
        if( profile.isRequestEncrypted() ) {
            a.add(" " + r.getString(this, "requestEncrypted"));
        }
        if( profile.isResponseSignatureVerified() ) {
            a.add(" " + r.getString(this, "responseSignatureVerified"));
        }
        if( profile.isResponseDecrypted() ) {
            a.add(" " + r.getString(this, "responseDecrypted"));
        }

        ListFormatter lf = new ListFormatter(a);
        return lf.toString();
    }

    @Override
    public String getTemplate() {
        return "/admin/facelet/template/wss-summary-sign-encrypt.xhtml";
    }

    @Override
    public String getIcon() {
        return "../image/edit.png";
    }

    @Override
    public boolean isExpandable() {
        return true;
    }

    @Override
    public int getGotoStep() {
        return WscCreateWizardStep.WSC_SIGN_ENCRYPT.toInt();
    }

}

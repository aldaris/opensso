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
 * $Id: SamlV2RemoteSpCreateWizardBean.java,v 1.3 2009-06-20 08:41:58 asyhuang Exp $
 */
package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import java.io.Serializable;

public class SamlV2RemoteSpCreateWizardBean
        extends SamlV2RemoteCreateWizardBean
        implements Serializable {

    private String metaUrl;
    private RealmSamlV2RemoteSpCreateSummary realmSamlV2RemoteSpCreateSummary = new RealmSamlV2RemoteSpCreateSummary(this);
    private MetaUrlSamlV2RemoteSpCreateSummary metaUrlSamlV2RemoteSpCreateSummary = new MetaUrlSamlV2RemoteSpCreateSummary(this);
    private StdMetadataNameSamlV2RemoteSpCreateSummary stdMetadataNameSamlV2RemoteSpCreateSummary = new StdMetadataNameSamlV2RemoteSpCreateSummary(this);   
    private CotSamlV2RemoteSpCreateSummary cotSamlV2RemoteSpCreateSummary = new CotSamlV2RemoteSpCreateSummary(this);
    private Effect samlV2RemoteSpCreateEntityNameInputEffect;
    
    public SamlV2RemoteSpCreateWizardBean() {
        super();
    }

    @Override
    public void reset() {
        super.reset();
        metaUrl = null;
        realmSamlV2RemoteSpCreateSummary = new RealmSamlV2RemoteSpCreateSummary(this);
        metaUrlSamlV2RemoteSpCreateSummary = new MetaUrlSamlV2RemoteSpCreateSummary(this);
        stdMetadataNameSamlV2RemoteSpCreateSummary = new StdMetadataNameSamlV2RemoteSpCreateSummary(this);       
        cotSamlV2RemoteSpCreateSummary = new CotSamlV2RemoteSpCreateSummary(this);
    }

    public String getMetaUrl() {
        return metaUrl;
    }

    public void setMetaUrl(String metaUrl) {
        this.metaUrl = metaUrl;
    }

    public RealmSamlV2RemoteSpCreateSummary getRealmSamlV2RemoteSpCreateSummary() {
        return realmSamlV2RemoteSpCreateSummary;
    }

    public MetaUrlSamlV2RemoteSpCreateSummary getMetaUrlSamlV2RemoteSpCreateSummary() {
        return metaUrlSamlV2RemoteSpCreateSummary;
    }

    public CotSamlV2RemoteSpCreateSummary getCotSamlV2RemoteSpCreateSummary() {
        return cotSamlV2RemoteSpCreateSummary;
    }

    public StdMetadataNameSamlV2RemoteSpCreateSummary getStdMetadataNameSamlV2RemoteSpCreateSummary() {
        return stdMetadataNameSamlV2RemoteSpCreateSummary;
    }

    public Effect getSamlV2RemoteSpCreateEntityNameInputEffect() {
        return samlV2RemoteSpCreateEntityNameInputEffect;
    }

    public void setSamlV2RemoteSpCreateEntityNameInputEffect(Effect samlV2RemoteSpCreateEntityNameInputEffect) {
        this.samlV2RemoteSpCreateEntityNameInputEffect = samlV2RemoteSpCreateEntityNameInputEffect;
    }
}

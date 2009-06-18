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
 * $Id: SamlV2RemoteSpCreateWizardBean.java,v 1.2 2009-06-18 07:54:58 asyhuang Exp $
 */
package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import java.io.Serializable;

public class SamlV2RemoteSpCreateWizardBean
        extends SamlV2RemoteCreateWizardBean
        implements Serializable {

    private boolean retrieveFileFromSpServerVisible = true;
    private RealmSamlV2RemoteSpCreateSummary realmSamlV2RemoteSpCreateSummary = new RealmSamlV2RemoteSpCreateSummary(this);
    private EntityNameSamlV2RemoteSpCreateSummary entityNameSamlV2RemoteSpCreateSummary = new EntityNameSamlV2RemoteSpCreateSummary(this);
    private StdMetadataNameSamlV2RemoteSpCreateSummary stdMetadataNameSamlV2RemoteSpCreateSummary = new StdMetadataNameSamlV2RemoteSpCreateSummary(this);
    private ExtMetadataNameSamlV2RemoteSpCreateSummary extMetadataNameSamlV2RemoteSpCreateSummary = new ExtMetadataNameSamlV2RemoteSpCreateSummary(this);   
    private CotSamlV2RemoteSpCreateSummary cotSamlV2RemoteSpCreateSummary = new CotSamlV2RemoteSpCreateSummary(this);
    private Effect samlV2RemoteSpCreateEntityNameInputEffect;
    
    public SamlV2RemoteSpCreateWizardBean() {
        super();
    }

    @Override
    public void reset() {
        super.reset();
        realmSamlV2RemoteSpCreateSummary = new RealmSamlV2RemoteSpCreateSummary(this);
        entityNameSamlV2RemoteSpCreateSummary = new EntityNameSamlV2RemoteSpCreateSummary(this);
        stdMetadataNameSamlV2RemoteSpCreateSummary = new StdMetadataNameSamlV2RemoteSpCreateSummary(this);
        extMetadataNameSamlV2RemoteSpCreateSummary = new ExtMetadataNameSamlV2RemoteSpCreateSummary(this);
        cotSamlV2RemoteSpCreateSummary = new CotSamlV2RemoteSpCreateSummary(this);
    }

    public boolean getRetrieveFileFromSpServerVisible() {
        return retrieveFileFromSpServerVisible;
    }

    public void setRetrieveFileFromSpServerVisible(boolean retrieveFileFromSpServerVisible) {
        this.retrieveFileFromSpServerVisible = retrieveFileFromSpServerVisible;
    }

    public RealmSamlV2RemoteSpCreateSummary getRealmSamlV2RemoteSpCreateSummary() {
        return realmSamlV2RemoteSpCreateSummary;
    }

    public EntityNameSamlV2RemoteSpCreateSummary getEntityNameSamlV2RemoteSpCreateSummary() {
        return entityNameSamlV2RemoteSpCreateSummary;
    }

    public CotSamlV2RemoteSpCreateSummary getCotSamlV2RemoteSpCreateSummary() {
        return cotSamlV2RemoteSpCreateSummary;
    }

    public StdMetadataNameSamlV2RemoteSpCreateSummary getStdMetadataNameSamlV2RemoteSpCreateSummary() {
        return stdMetadataNameSamlV2RemoteSpCreateSummary;
    }

    public ExtMetadataNameSamlV2RemoteSpCreateSummary getExtMetadataNameSamlV2RemoteSpCreateSummary() {
        return extMetadataNameSamlV2RemoteSpCreateSummary;
    }

    public Effect getSamlV2RemoteSpCreateEntityNameInputEffect() {
        return samlV2RemoteSpCreateEntityNameInputEffect;
    }

    public void setSamlV2RemoteSpCreateEntityNameInputEffect(Effect samlV2RemoteSpCreateEntityNameInputEffect) {
        this.samlV2RemoteSpCreateEntityNameInputEffect = samlV2RemoteSpCreateEntityNameInputEffect;
    }
}

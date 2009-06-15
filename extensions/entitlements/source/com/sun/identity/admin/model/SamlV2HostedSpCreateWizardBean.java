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
 * $Id: SamlV2HostedSpCreateWizardBean.java,v 1.1 2009-06-15 18:43:46 asyhuang Exp $
 */
package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class SamlV2HostedSpCreateWizardBean
        extends SamlV2HostedCreateWizardBean
        implements Serializable {

    private String selectedRealm = "/";
    private boolean local = true;
    private boolean meta = false;
    private boolean cot = true;
    private String newEntityName;
    private String newCotName;
    private String selectedCot;
    private String selectedSigningKey;
    private List<SelectItem> availableRealmsList;
    private List<SelectItem> availableCotList;
    private List<SelectItem> availableSigningKeyList;
    private CotsBean cotsBean;
    private String stdMetaFile;
    private String stdMetaFilename;
    private int stdMetaFileProgress;
    private String extMetaFile;
    private String extMetaFilename;
    private int extMetaFileProgress;
    private boolean retrieveFileFromSpServerVisible = true;
    private RealmSamlV2HostedSpCreateSummary realmSamlV2CreateSummary = new RealmSamlV2HostedSpCreateSummary(this);
    private EntityNameSamlV2HostedSpCreateSummary entityNameSamlV2CreateSummary = new EntityNameSamlV2HostedSpCreateSummary(this);
    private StdMetadataNameSamlV2HostedSpCreateSummary stdMetadataNameSamlV2CreateSummary = new StdMetadataNameSamlV2HostedSpCreateSummary(this);
    private ExtMetadataNameSamlV2HostedSpCreateSummary extMetadataNameSamlV2CreateSummary = new ExtMetadataNameSamlV2HostedSpCreateSummary(this);
    private LocalRemoteSamlV2HostedSpCreateSummary localRemoteSamlV2CreateSummary = new LocalRemoteSamlV2HostedSpCreateSummary(this);
    private SigningKeySamlV2HostedSpCreateSummary signingKeySamlV2CreateSummary = new SigningKeySamlV2HostedSpCreateSummary(this);
    private CotSamlV2HostedSpCreateSummary cotSamlV2CreateSummary = new CotSamlV2HostedSpCreateSummary(this);

    public SamlV2HostedSpCreateWizardBean() {
        super();
    }

    @Override
    public void reset() {
        reset(true);
    }

    public void reset(boolean resetName) {
        super.reset();
        //advancedTabsetIndex = 0;
        selectedRealm = null;
        local = true;
        meta = false;
        cot = true;
        newEntityName = null;
        newCotName = null;
        selectedCot = null;
        selectedSigningKey = null;
        availableRealmsList = null;
        availableCotList = null;
        availableSigningKeyList = null;
        cotsBean = null;
        stdMetaFile = null;
        stdMetaFilename = null;
        extMetaFile = null;
        extMetaFilename = null;
        stdMetaFileProgress = 0;
        extMetaFileProgress = 0;

        realmSamlV2CreateSummary = new RealmSamlV2HostedSpCreateSummary(this);
        entityNameSamlV2CreateSummary = new EntityNameSamlV2HostedSpCreateSummary(this);
        stdMetadataNameSamlV2CreateSummary = new StdMetadataNameSamlV2HostedSpCreateSummary(this);
        extMetadataNameSamlV2CreateSummary = new ExtMetadataNameSamlV2HostedSpCreateSummary(this);
        localRemoteSamlV2CreateSummary = new LocalRemoteSamlV2HostedSpCreateSummary(this);
        signingKeySamlV2CreateSummary = new SigningKeySamlV2HostedSpCreateSummary(this);
        cotSamlV2CreateSummary = new CotSamlV2HostedSpCreateSummary(this);
    }

    public String getSelectedRealm() {
        return selectedRealm;
    }

    public void setSelectedRealm(String selectedRealm) {
        this.selectedRealm = selectedRealm;
        int idx = selectedRealm.indexOf("(");
        int end = selectedRealm.indexOf(")");
        String realm = selectedRealm.substring(idx + 1, end).trim();
        cotsBean = new CotsBean();
        cotsBean.setCotBeans(realm);
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isMeta() {
        return meta;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }

    public boolean isCot() {
        return cot;
    }

    public void setCot(boolean cot) {
        this.cot = cot;
    }

    public String getNewEntityName() {
        return newEntityName;
    }

    public void setNewEntityName(String entityName) {
        this.newEntityName = entityName;
    }

    public String getNewCotName() {
        return newCotName;
    }

    public void setNewCotName(String cotName) {
        this.newCotName = cotName;
    }

    public String getSelectedCot() {
        return selectedCot;
    }

    public void setSelectedCot(String selectedCot) {
        this.selectedCot = selectedCot;
    }

    public String getSigningKey() {
        return selectedSigningKey;
    }

    public void setSigningKey(String selectedSigningKey) {
        this.selectedSigningKey = selectedSigningKey;
    }

    public List<SelectItem> getAvailableRealmsList() {
        availableRealmsList = new ArrayList<SelectItem>();
        RealmsBean rlmbean = RealmsBean.getInstance();
        availableRealmsList = rlmbean.getRealmBeanItems();
        return availableRealmsList;
    }

    public List<SelectItem> getAvailableCotList() {
        availableCotList = new ArrayList<SelectItem>();
        availableCotList = cotsBean.getCotBeanItems();
        if (availableCotList.isEmpty()) {
            this.cot = false;
        }
        return availableCotList;
    }

    public String getStdMetaFile() {
        return stdMetaFile;
    }

    public void setStdMetaFile(String file) {
        this.stdMetaFile = file;
    }

    public String getStdMetaFilename() {
        return stdMetaFilename;
    }

    public void setStdMetaFilename(String name) {
        this.stdMetaFilename = name;
    }

    public String getExtMetaFile() {
        return extMetaFile;
    }

    public void setExtMetaFile(String file) {
        this.extMetaFile = file;
    }

    public String getExtMetaFilename() {
        return extMetaFilename;
    }

    public void setExtMetaFilename(String name) {
        this.extMetaFilename = name;
    }

    public int getStdMetaFileProgress() {
        return stdMetaFileProgress;
    }

    public void setStdMetaFileProgress(int fileProgress) {
        this.stdMetaFileProgress = fileProgress;
    }

    public int getExtMetaFileProgress() {
        return extMetaFileProgress;
    }

    public void setExtMetaFileProgress(int fileProgress) {
        this.extMetaFileProgress = fileProgress;
    }

    public boolean getRetrieveFileFromSpServerVisible() {
        return retrieveFileFromSpServerVisible;
    }

    public void setRetrieveFileFromSpServerVisible(boolean retrieveFileFromSpServerVisible) {
        this.retrieveFileFromSpServerVisible = retrieveFileFromSpServerVisible;
    }

    public RealmSamlV2HostedSpCreateSummary getRealmSamlV2CreateSummary() {
        return realmSamlV2CreateSummary;
    }

    public EntityNameSamlV2HostedSpCreateSummary getEntityNameSamlV2CreateSummary() {
        return entityNameSamlV2CreateSummary;
    }

    public LocalRemoteSamlV2HostedSpCreateSummary getLocalRemoteSamlV2CreateSummary() {
        return localRemoteSamlV2CreateSummary;
    }

    public SigningKeySamlV2HostedSpCreateSummary getSigningKeySamlV2CreateSummary() {
        return signingKeySamlV2CreateSummary;
    }

    public CotSamlV2HostedSpCreateSummary getCotSamlV2CreateSummary() {
        return cotSamlV2CreateSummary;
    }

    public StdMetadataNameSamlV2HostedSpCreateSummary getStdMetadataNameSamlV2CreateSummary() {
        return stdMetadataNameSamlV2CreateSummary;
    }

    public ExtMetadataNameSamlV2HostedSpCreateSummary getExtMetadataNameSamlV2CreateSummary() {
        return extMetadataNameSamlV2CreateSummary;
    }
}

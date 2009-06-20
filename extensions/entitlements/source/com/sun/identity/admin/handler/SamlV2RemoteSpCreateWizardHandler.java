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
 * $Id: SamlV2RemoteSpCreateWizardHandler.java,v 1.3 2009-06-20 08:41:58 asyhuang Exp $
 */
package com.sun.identity.admin.handler;

import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.dao.SamlV2RemoteSpCreateDao;
import com.sun.identity.admin.effect.InputFieldErrorEffect;
import com.sun.identity.admin.effect.MessageErrorEffect;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.SamlV2RemoteSpCreateWizardBean;
import com.sun.identity.admin.model.SamlV2RemoteSpCreateWizardStep;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

public class SamlV2RemoteSpCreateWizardHandler
        extends SamlV2RemoteCreateWizardHandler
        implements Serializable {

    private SamlV2RemoteSpCreateDao samlV2RemoteSpCreateDao;

    public void setSamlV2RemoteSpCreateDao(SamlV2RemoteSpCreateDao samlV2RemoteSpCreateDao) {
        this.samlV2RemoteSpCreateDao = samlV2RemoteSpCreateDao;
    }

    @Override
    public void finishListener(ActionEvent event) {

        if (!validateSteps()) {
            return;
        }

        String cot;
        boolean choseFromExisintCot = getSamlV2RemoteSpCreateWizardBean().isCot();
        if (choseFromExisintCot) {
            cot = getSamlV2RemoteSpCreateWizardBean().getSelectedCot();
        } else {
            cot = getSamlV2RemoteSpCreateWizardBean().getNewCotName();
        }

        String selectedRealmValue = getSamlV2RemoteSpCreateWizardBean().getSelectedRealm();
        int idx = selectedRealmValue.indexOf("(");
        int end = selectedRealmValue.indexOf(")");
        String realm = selectedRealmValue.substring(idx + 1, end).trim();

        //TODO: add attribute mapping to this ArrayList...
        List attrMapping = new ArrayList();
        
        if (getSamlV2RemoteSpCreateWizardBean().isMeta()) {
            String stdMetadataFile = getSamlV2RemoteSpCreateWizardBean().getStdMetaFile();
            samlV2RemoteSpCreateDao.importSamlv2RemoteSpFromFile(realm, cot, stdMetadataFile, attrMapping);
        } else {
            String stdMetadataFilename = getSamlV2RemoteSpCreateWizardBean().getMetaUrl();
            samlV2RemoteSpCreateDao.importSamlv2RemoteSpFromURL(realm, cot, stdMetadataFilename, attrMapping);
        }

        getSamlV2RemoteSpCreateWizardBean().reset();
        doFinishNext();
    }

    @Override
    public void cancelListener(ActionEvent event) {
        getSamlV2RemoteSpCreateWizardBean().reset();
        doCancelNext();
    }

    @Override
    public void previousListener(ActionEvent event) {
        int step = getStep(event);
        SamlV2RemoteSpCreateWizardStep pws = SamlV2RemoteSpCreateWizardStep.valueOf(step);

        switch (pws) {
            case REALM:
                break;
            case METADATA:
                if (!validateMetadata()) {
                    return;
                }
                break;
            case COT:
                if (!validateCot()) {
                    return;
                }
                break;
            case ATTRIBUTEMAPPING:
                break;
            case SUMMARY:
                break;
            default:
                assert false : "unhandled step: " + pws;
        }

        super.previousListener(event);
    }

    @Override
    public void nextListener(ActionEvent event) {
        int step = getStep(event);
        SamlV2RemoteSpCreateWizardStep pws = SamlV2RemoteSpCreateWizardStep.valueOf(step);

        switch (pws) {
            case REALM:
                break;
            case METADATA:
                if (!validateMetadata()) {
                    return;
                }
                break;
            case COT:
                if (!validateCot()) {
                    return;
                }
                break;
            case ATTRIBUTEMAPPING:
                break;
            case SUMMARY:
                break;
            default:
                assert false : "unhandled step: " + pws;
        }

        super.nextListener(event);
    }

    public boolean validateCot() {
        boolean usingExitingCot = getSamlV2RemoteSpCreateWizardBean().isCot();
        String cotname = getSamlV2RemoteSpCreateWizardBean().getNewCotName();

        if (!usingExitingCot) {
            if (cotname.length() == 0 || (cotname == null)) {
                MessageBean mb = new MessageBean();
                Resources r = new Resources();
                mb.setSummary(r.getString(this, "invalidCotSummary"));
                mb.setDetail(r.getString(this, "invalidCotDetail"));
                mb.setSeverity(FacesMessage.SEVERITY_ERROR);

                Effect e;

                e = new InputFieldErrorEffect();
                getSamlV2RemoteSpCreateWizardBean().setSamlV2RemoteSpCreateEntityNameInputEffect(e);

                e = new MessageErrorEffect();
                getSamlV2RemoteSpCreateWizardBean().setSamlV2RemoteSpCreateEntityNameInputEffect(e);

                getMessagesBean().addMessageBean(mb);
                getSamlV2RemoteSpCreateWizardBean().gotoStep(SamlV2RemoteSpCreateWizardStep.COT.toInt());

                return false;
            }
        }
        return true;
    }

    public boolean validateMetadata() {
        boolean usingMetaDataFile = getSamlV2RemoteSpCreateWizardBean().isMeta();        

        if (!usingMetaDataFile) {

            String url = getSamlV2RemoteSpCreateWizardBean().getMetaUrl();

            if (url.length() == 0 || (url == null)) {
                MessageBean mb = new MessageBean();
                Resources r = new Resources();
                mb.setSummary(r.getString(this, "invalidMetaUrlSummary"));
                mb.setDetail(r.getString(this, "invalidMetaUrlDetail"));
                mb.setSeverity(FacesMessage.SEVERITY_ERROR);

                Effect e;

                e = new InputFieldErrorEffect();
                getSamlV2RemoteSpCreateWizardBean().setSamlV2RemoteSpCreateEntityNameInputEffect(e);

                e = new MessageErrorEffect();
                getSamlV2RemoteSpCreateWizardBean().setSamlV2RemoteSpCreateEntityNameInputEffect(e);

                getMessagesBean().addMessageBean(mb);
                getSamlV2RemoteSpCreateWizardBean().gotoStep(SamlV2RemoteSpCreateWizardStep.METADATA.toInt());

                return false;
            }

        } else {

            String filename = getSamlV2RemoteSpCreateWizardBean().getStdMetaFilename();

            if (filename.length() == 0 || (filename == null)) {
                MessageBean mb = new MessageBean();
                Resources r = new Resources();
                mb.setSummary(r.getString(this, "invalidMetafileSummary"));
                mb.setDetail(r.getString(this, "invalidMetafileDetail"));
                mb.setSeverity(FacesMessage.SEVERITY_ERROR);

                Effect e;

                e = new InputFieldErrorEffect();
                getSamlV2RemoteSpCreateWizardBean().setSamlV2RemoteSpCreateEntityNameInputEffect(e);

                e = new MessageErrorEffect();
                getSamlV2RemoteSpCreateWizardBean().setSamlV2RemoteSpCreateEntityNameInputEffect(e);

                getMessagesBean().addMessageBean(mb);
                getSamlV2RemoteSpCreateWizardBean().gotoStep(SamlV2RemoteSpCreateWizardStep.METADATA.toInt());

                return false;
            }
        }

        return true;
    }

    private SamlV2RemoteSpCreateWizardBean getSamlV2RemoteSpCreateWizardBean() {
        return (SamlV2RemoteSpCreateWizardBean) getWizardBean();
    }

    private boolean validateSteps() {

        return true;
    }

    public void stdMetaUploadFile(ActionEvent event) throws IOException {
        InputFile inputFile = (InputFile) event.getSource();
        FileInfo fileInfo = inputFile.getFileInfo();
        if (fileInfo.getStatus() == FileInfo.SAVED) {
            // read the file into a string
            // reference our newly updated file for display purposes and
            // added it to filename string object in our bean
            File file = new File(fileInfo.getFile().getAbsolutePath());

            StringBuffer contents = new StringBuffer();
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new FileReader(file));
                String text = null;

                // repeat until all lines is read
                while ((text = reader.readLine()) != null) {
                    contents.append(text).append(System.getProperty(
                            "line.separator"));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            getSamlV2RemoteSpCreateWizardBean().setStdMetaFilename(fileInfo.getFileName());
            getSamlV2RemoteSpCreateWizardBean().setStdMetaFile(contents.toString());
        }
    }

    public void stdMetaFileUploadProgress(EventObject event) {
        InputFile ifile = (InputFile) event.getSource();
        getSamlV2RemoteSpCreateWizardBean().setStdMetaFileProgress(ifile.getFileInfo().getPercent());
    }   
}

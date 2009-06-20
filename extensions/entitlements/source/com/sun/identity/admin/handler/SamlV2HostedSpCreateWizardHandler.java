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
 * $Id: SamlV2HostedSpCreateWizardHandler.java,v 1.5 2009-06-20 08:41:58 asyhuang Exp $
 */
package com.sun.identity.admin.handler;

import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.dao.SamlV2HostedSpCreateDao;
import com.sun.identity.admin.effect.InputFieldErrorEffect;
import com.sun.identity.admin.effect.MessageErrorEffect;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.SamlV2HostedSpCreateWizardBean;
import com.sun.identity.admin.model.SamlV2HostedSpCreateWizardStep;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.EventObject;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

public class SamlV2HostedSpCreateWizardHandler
        extends SamlV2HostedCreateWizardHandler
        implements Serializable {

    private SamlV2HostedSpCreateDao samlV2HostedSpCreateDao;
    private MessagesBean messagesBean;

    public void setSamlV2HostedSpCreateDao(SamlV2HostedSpCreateDao samlV2HostedSpCreateDao) {
        this.samlV2HostedSpCreateDao = samlV2HostedSpCreateDao;
    }

    @Override
    public void cancelListener(ActionEvent event) {
        getSamlV2HostedSpCreateWizardBean().reset();
        doCancelNext();
    }

    @Override
    public void finishListener(ActionEvent event) {
        if (!validateSteps()) {
            return;
        }

        String cot;
        boolean choseFromExisintCot = getSamlV2HostedSpCreateWizardBean().isCot();
        if (choseFromExisintCot) {
            cot = getSamlV2HostedSpCreateWizardBean().getSelectedCot();
        } else {
            cot = getSamlV2HostedSpCreateWizardBean().getNewCotName();
        }

        boolean isMeta = getSamlV2HostedSpCreateWizardBean().isMeta();

        String selectedRealmValue = getSamlV2HostedSpCreateWizardBean().getSelectedRealm();
        int idx = selectedRealmValue.indexOf("(");
        int end = selectedRealmValue.indexOf(")");
        String realm = selectedRealmValue.substring(idx + 1, end).trim();
        String name = getSamlV2HostedSpCreateWizardBean().getNewEntityName();
        boolean defAttrMappings = getSamlV2HostedSpCreateWizardBean().getDefAttrMappings();
        
        if (!isMeta) {
            samlV2HostedSpCreateDao.createSamlv2HostedSp(realm, name, cot, defAttrMappings );
        } else {
            String stdMeta = getSamlV2HostedSpCreateWizardBean().getStdMetaFile();
            String extMeta = getSamlV2HostedSpCreateWizardBean().getExtMetaFile();
            samlV2HostedSpCreateDao.importSamlv2HostedSp(cot, stdMeta, extMeta, defAttrMappings );
        }

        getSamlV2HostedSpCreateWizardBean().reset();
        doFinishNext();
    }

    @Override
    public void previousListener(ActionEvent event) {
        int step = getStep(event);
        SamlV2HostedSpCreateWizardStep pws = SamlV2HostedSpCreateWizardStep.valueOf(step);

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
        SamlV2HostedSpCreateWizardStep pws = SamlV2HostedSpCreateWizardStep.valueOf(step);

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
        boolean usingExitingCot = getSamlV2HostedSpCreateWizardBean().isCot();
        String cotname = getSamlV2HostedSpCreateWizardBean().getNewCotName();

        if (!usingExitingCot) {
            if (cotname.length() == 0 || (cotname == null)) {
                MessageBean mb = new MessageBean();
                Resources r = new Resources();
                mb.setSummary(r.getString(this, "invalidCotSummary"));
                mb.setDetail(r.getString(this, "invalidCotDetail"));
                mb.setSeverity(FacesMessage.SEVERITY_ERROR);

                Effect e;

                e = new InputFieldErrorEffect();
                getSamlV2HostedSpCreateWizardBean().setSamlV2HostedSpCreateEntityNameInputEffect(e);

                e = new MessageErrorEffect();
                getSamlV2HostedSpCreateWizardBean().setSamlV2HostedSpCreateEntityNameInputEffect(e);

                getMessagesBean().addMessageBean(mb);
                getSamlV2HostedSpCreateWizardBean().gotoStep(SamlV2HostedSpCreateWizardStep.COT.toInt());

                return false;
            }
        }
        return true;
    }

    public boolean validateMetadata() {
        boolean usingMetaDataFile = getSamlV2HostedSpCreateWizardBean().isMeta();
        String newEntityName = getSamlV2HostedSpCreateWizardBean().getNewEntityName();

        if (!usingMetaDataFile) {
            if (newEntityName.length() == 0 || (newEntityName == null)) {
                MessageBean mb = new MessageBean();
                Resources r = new Resources();
                mb.setSummary(r.getString(this, "invalidEntityNameSummary"));
                mb.setDetail(r.getString(this, "invalidEntityNameDetail"));
                mb.setSeverity(FacesMessage.SEVERITY_ERROR);

                Effect e;

                e = new InputFieldErrorEffect();
                getSamlV2HostedSpCreateWizardBean().setSamlV2HostedSpCreateEntityNameInputEffect(e);

                e = new MessageErrorEffect();
                getSamlV2HostedSpCreateWizardBean().setSamlV2HostedSpCreateEntityNameInputEffect(e);

                getMessagesBean().addMessageBean(mb);
                getSamlV2HostedSpCreateWizardBean().gotoStep(SamlV2HostedSpCreateWizardStep.METADATA.toInt());

                return false;
            }
        }
        return true;
    }

    private SamlV2HostedSpCreateWizardBean getSamlV2HostedSpCreateWizardBean() {
        return (SamlV2HostedSpCreateWizardBean) getWizardBean();
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
            getSamlV2HostedSpCreateWizardBean().setStdMetaFilename(fileInfo.getFileName());
            getSamlV2HostedSpCreateWizardBean().setStdMetaFile(contents.toString());
        }
    }

    public void stdMetaFileUploadProgress(EventObject event) {
        InputFile ifile = (InputFile) event.getSource();
        getSamlV2HostedSpCreateWizardBean().setStdMetaFileProgress(ifile.getFileInfo().getPercent());
    }

    public void extMetaUploadFile(ActionEvent event) throws IOException {
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
            getSamlV2HostedSpCreateWizardBean().setExtMetaFilename(fileInfo.getFileName());
            getSamlV2HostedSpCreateWizardBean().setExtMetaFile(contents.toString());
        }
    }

    public void extMetaFileUploadProgress(EventObject event) {
        InputFile ifile = (InputFile) event.getSource();
        getSamlV2HostedSpCreateWizardBean().setExtMetaFileProgress(ifile.getFileInfo().getPercent());
    }

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }

    public MessagesBean getMessagesBean() {
        return messagesBean;
    }
}

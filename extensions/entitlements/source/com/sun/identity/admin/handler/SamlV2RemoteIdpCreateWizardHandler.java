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
 * $Id: SamlV2RemoteIdpCreateWizardHandler.java,v 1.1 2009-06-23 06:41:54 babysunil Exp $
 */

package com.sun.identity.admin.handler;

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.dao.SamlV2RemoteIdpCreateDao;
import com.sun.identity.admin.effect.InputFieldErrorEffect;
import com.sun.identity.admin.effect.MessageErrorEffect;
import com.sun.identity.admin.model.LinkBean;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.NextPopupBean;
import com.sun.identity.admin.model.SamlV2RemoteIdpCreateWizardBean;
import com.sun.identity.admin.model.SamlV2RemoteIdpCreateWizardStep;
import com.sun.identity.admin.model.WizardBean;
import com.sun.identity.admin.Resources;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

public class SamlV2RemoteIdpCreateWizardHandler
        extends SamlV2RemoteCreateWizardHandler {

    private SamlV2RemoteIdpCreateWizardBean samlV2RemoteIdpCreateWizardBean;
    private SamlV2RemoteIdpCreateDao samlV2RemoteIdpCreateDao;
    private MessagesBean messagesBean;

    public void setSamlV2RemoteIdpCreateDao(
            SamlV2RemoteIdpCreateDao samlV2RemoteIdpCreateDao) {
        this.samlV2RemoteIdpCreateDao = samlV2RemoteIdpCreateDao;
    }

    public boolean validateMetadata() {
        String newEntityName = getSamlV2RemoteIdpCreateWizardBean().
                getStdMetaFilename();
        if ((newEntityName == null) || (newEntityName.length() == 0)) {
            MessageBean mb = new MessageBean();
            Resources r = new Resources();
            mb.setSummary(r.getString(this, "invalidNameSummary"));
            mb.setDetail(r.getString(this, "invalidNameDetail"));
            mb.setSeverity(FacesMessage.SEVERITY_ERROR);

            Effect e;
            e = new InputFieldErrorEffect();
            getSamlV2RemoteIdpCreateWizardBean().
                    setSamlV2EntityNameInputEffect(e);

            e = new MessageErrorEffect();
            getSamlV2RemoteIdpCreateWizardBean().
                    setSamlV2EntityNameMessageEffect(e);

            getMessagesBean().addMessageBean(mb);
            getSamlV2RemoteIdpCreateWizardBean().gotoStep(
                    SamlV2RemoteIdpCreateWizardStep.METADATA.toInt());

            return false;
        }

        return true;
    }

    public boolean validateCot() {
        boolean usingExitingCot = getSamlV2RemoteIdpCreateWizardBean().isCot();
        String cotname = getSamlV2RemoteIdpCreateWizardBean().getNewCotName();

        if (!usingExitingCot) {
            if ((cotname == null) || (cotname.length() == 0)) {
                MessageBean mb = new MessageBean();
                Resources r = new Resources();
                mb.setSummary(r.getString(this, "invalidCotSummary"));
                mb.setDetail(r.getString(this, "invalidCotDetail"));
                mb.setSeverity(FacesMessage.SEVERITY_ERROR);

                Effect e;
                e = new InputFieldErrorEffect();
                getSamlV2RemoteIdpCreateWizardBean().
                        setSamlV2EntityNameInputEffect(e);

                e = new MessageErrorEffect();
                getSamlV2RemoteIdpCreateWizardBean().
                        setSamlV2EntityNameMessageEffect(e);

                getMessagesBean().addMessageBean(mb);
                getSamlV2RemoteIdpCreateWizardBean().gotoStep(
                        SamlV2RemoteIdpCreateWizardStep.COT.toInt());


                return false;
            }
        }
        return true;
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
            getSamlV2RemoteIdpCreateWizardBean().setStdMetaFilename(
                    fileInfo.getFileName());
            getSamlV2RemoteIdpCreateWizardBean().setStdMetaFile(
                    contents.toString());
            file.delete();
        }
    }

    public void stdMetaFileUploadProgress(EventObject event) {
        InputFile ifile = (InputFile) event.getSource();
        getSamlV2RemoteIdpCreateWizardBean().setStdMetaFileProgress(
                ifile.getFileInfo().getPercent());
    }
   
    public MessagesBean getMessagesBean() {
        return messagesBean;
    }

    public void setSamlV2RemoteIdpCreateWizardBean(
            SamlV2RemoteIdpCreateWizardBean samlV2RemoteIdpCreateWizardBean) {
        this.samlV2RemoteIdpCreateWizardBean = samlV2RemoteIdpCreateWizardBean;
    }

    private SamlV2RemoteIdpCreateWizardBean getSamlV2RemoteIdpCreateWizardBean() {
        return (SamlV2RemoteIdpCreateWizardBean) getWizardBean();
    }

    @Override
    public void setWizardBean(WizardBean wizardBean) {
        super.setWizardBean(wizardBean);
    }

    @Override
    public void previousListener(ActionEvent event) {
        int step = getStep(event);
        SamlV2RemoteIdpCreateWizardStep pws =
                SamlV2RemoteIdpCreateWizardStep.valueOf(step);

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
        SamlV2RemoteIdpCreateWizardStep pws =
                SamlV2RemoteIdpCreateWizardStep.valueOf(step);

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
            case SUMMARY:
                break;
            default:
                assert false : "unhandled step: " + pws;
        }

        super.nextListener(event);
    }

    @Override
    public void cancelListener(ActionEvent event) {
        getSamlV2RemoteIdpCreateWizardBean().reset();
        doCancelNext();
    }

    @Override
    public void doCancelNext() {
        NextPopupBean npb = NextPopupBean.getInstance();
        npb.setVisible(true);
        Resources r = new Resources();
        npb.setTitle(r.getString(this, "cancelTitle"));
        npb.setMessage(r.getString(this, "cancelMessage"));
        npb.setLinkBeans(getCancelLinkBeans());

    }

    private List<LinkBean> getCancelLinkBeans() {
        List<LinkBean> lbs = new ArrayList<LinkBean>();
        lbs.add(LinkBean.HOME);
        lbs.add(LinkBean.SAMLV2_HOSTED_IDP_CREATE);
        lbs.add(LinkBean.SAMLV2_REMOTE_SP_CREATE);
        lbs.add(LinkBean.SAMLV2_HOSTED_SP_CREATE);

        return lbs;
    }

    @Override
    public void finishListener(ActionEvent event) {
        if (!validateSteps()) {
            return;
        }

        String cot;
        boolean choseFromExisintCot =
                getSamlV2RemoteIdpCreateWizardBean().isCot();
        if (choseFromExisintCot) {
            cot = getSamlV2RemoteIdpCreateWizardBean().getSelectedCot();
        } else {
            cot = getSamlV2RemoteIdpCreateWizardBean().getNewCotName();
        }
        String selectedRealmValue =
                getSamlV2RemoteIdpCreateWizardBean().getSelectedRealm();
        int idx = selectedRealmValue.indexOf("(");
        int end = selectedRealmValue.indexOf(")");
        String realm = selectedRealmValue.substring(idx + 1, end).trim();
        if (getSamlV2RemoteIdpCreateWizardBean().isMeta()) {
            String stdMeta =
                    getSamlV2RemoteIdpCreateWizardBean().getStdMetaFile();
            samlV2RemoteIdpCreateDao.importSamlv2RemoteIdp(cot, realm, stdMeta);
        } else {
            String stdMetaFileName =
                    getSamlV2RemoteIdpCreateWizardBean().getMetaUrl();
            samlV2RemoteIdpCreateDao.importSamlv2RemoteIdp(
                    cot, realm, stdMetaFileName);
        }

        getSamlV2RemoteIdpCreateWizardBean().reset();
        getFinishAction();
    }

    public void getFinishAction() {
        getSamlV2RemoteIdpCreateWizardBean().reset();
        doFinishNext();
    }

    public void doFinishNext() {
        NextPopupBean npb = NextPopupBean.getInstance();
        npb.setVisible(true);
        Resources r = new Resources();
        npb.setTitle(r.getString(this, "finishTitle"));
        npb.setMessage(r.getString(this, "finishMessage"));
        npb.setLinkBeans(getFinishLinkBeans());

    }

    public void doNotFinishNext(String errormsg) {
        NextPopupBean npb = NextPopupBean.getInstance();
        npb.setVisible(true);
        Resources r = new Resources();
        npb.setTitle(errormsg);
        npb.setMessage(errormsg);
        npb.setLinkBeans(getFinishLinkBeans());

    }

    private List<LinkBean> getFinishLinkBeans() {
        List<LinkBean> lbs = new ArrayList<LinkBean>();
        lbs.add(LinkBean.HOME);
        lbs.add(LinkBean.SAMLV2_HOSTED_IDP_CREATE);
        lbs.add(LinkBean.SAMLV2_REMOTE_SP_CREATE);
        lbs.add(LinkBean.SAMLV2_HOSTED_SP_CREATE);

        return lbs;
    }

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }
}

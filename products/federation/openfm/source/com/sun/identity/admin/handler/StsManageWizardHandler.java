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
 * $Id: StsManageWizardHandler.java,v 1.1 2009-09-17 21:56:06 ggennaro Exp $
 */

package com.sun.identity.admin.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import com.sun.identity.admin.Resources;
import com.sun.identity.admin.model.LinkBean;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.NextPopupBean;
import com.sun.identity.admin.model.SamlAttributeMapEntry;
import com.sun.identity.admin.model.StsManageWizardBean;
import com.sun.identity.admin.model.StsManageWizardStep;
import com.sun.identity.admin.model.StsManageWizardStep1Validator;
import com.sun.identity.admin.model.StsManageWizardStep2Validator;

public class StsManageWizardHandler 
        extends WizardHandler 
        implements Serializable
{
    private MessagesBean messagesBean;
    
    @Override
    public void initWizardStepValidators() {
    	getWizardStepValidators()[StsManageWizardStep.TOKEN_ISSUANCE.toInt()] = new StsManageWizardStep1Validator(getWizardBean());
    	getWizardStepValidators()[StsManageWizardStep.SECURITY.toInt()] = new StsManageWizardStep2Validator(getWizardBean());
    }

    @Override
    public void cancelListener(ActionEvent event) {
        getWizardBean().reset();
        doCancelNext();
    }

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
        lbs.add(LinkBean.WSS);
        return lbs;
    }


    @Override
    public void finishListener(ActionEvent event) {
        if (!validateFinish(event)) {
            return;
        }

        if( save() ) {
            doFinishNext();
            getWizardBean().reset();
        }
    }

    public void doFinishNext() {
        NextPopupBean npb = NextPopupBean.getInstance();
        npb.setVisible(true);
        Resources r = new Resources();
        npb.setTitle(r.getString(this, "finishTitle"));
        npb.setMessage(r.getString(this, "finishMessage"));
        npb.setLinkBeans(getFinishLinkBeans());
    }

    private List<LinkBean> getFinishLinkBeans() {
        List<LinkBean> lbs = new ArrayList<LinkBean>();
        lbs.add(LinkBean.HOME);
        lbs.add(LinkBean.WSS);
        return lbs;
    }


    private boolean save() {
        return true;
    }

    private void showSaveErrorPopup(String summary, String detail) {
        MessageBean mb = new MessageBean(); 
        mb.setSummary(summary);
        mb.setDetail(detail);
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        getMessagesBean().addMessageBean(mb);
    }

    private void resetSamlAttributeInputFields() {
        StsManageWizardBean wizardBean = (StsManageWizardBean)getWizardBean();
        
        wizardBean.setShowingNewAttributeFields(false);
        wizardBean.setEditAttributeIndex(-1);
        wizardBean.setNewAssertionAttributeName(null);
        wizardBean.setNewLocalAttributeName(null);
    }
    
    public void addNewAttributeListener(ActionEvent event) {
        resetSamlAttributeInputFields();
        
        StsManageWizardBean wizardBean = (StsManageWizardBean)getWizardBean();
        wizardBean.setShowingNewAttributeFields(true);
    }
    
    public void saveNewAttributeListener(ActionEvent event) {
        StsManageWizardBean wizardBean = (StsManageWizardBean)getWizardBean();
        
        if( wizardBean.getNewLocalAttributeName().length() > 0 
                && wizardBean.getNewAssertionAttributeName().length() > 0 ) {
            
            SamlAttributeMapEntry entry = new SamlAttributeMapEntry();
            entry.setAssertionAttributeName(wizardBean.getNewAssertionAttributeName());
            entry.setLocalAttributeName(wizardBean.getNewLocalAttributeName());
            entry.setCustom(true);
            wizardBean.getAttributeMapping().add(entry);

            resetSamlAttributeInputFields();
    	}
    }

    public void cancelNewAttributeListener(ActionEvent event) {
    	resetSamlAttributeInputFields();
    }
    
    public void editAttributeListener(ActionEvent event) {
        resetSamlAttributeInputFields();
        
        StsManageWizardBean wizardBean = (StsManageWizardBean)getWizardBean();
        Integer index 
        = (Integer) event.getComponent().getAttributes().get("attributeIndex");

        if( index != null && wizardBean.getAttributeMapping() != null ) {
            wizardBean.setEditAttributeIndex(index.intValue());
            
            SamlAttributeMapEntry entry 
                    = wizardBean.getAttributeMapping().get(index.intValue());
            if( entry != null ) {
                if( entry.isCustom() ) {
                    wizardBean.setNewLocalAttributeName(entry.getLocalAttributeName());
                }
                wizardBean.setNewAssertionAttributeName(entry.getAssertionAttributeName());
            }
        }
    }
    
    public void saveEditAttributeListener(ActionEvent event) {
        StsManageWizardBean wizardBean = (StsManageWizardBean)getWizardBean();
        Integer index 
        = (Integer) event.getComponent().getAttributes().get("attributeIndex");
        
        if( index == null ) {
            return;
        }
        
        SamlAttributeMapEntry entry 
                       = wizardBean.getAttributeMapping().get(index.intValue());
        
        if( entry == null ) {
            return;
        }
        
        if( entry.isCustom() ) {
            
            if( wizardBean.getNewLocalAttributeName().length() > 0 
                    && wizardBean.getNewAssertionAttributeName().length() > 0 )
            {
                entry.setLocalAttributeName(wizardBean.getNewLocalAttributeName());
                entry.setAssertionAttributeName(wizardBean.getNewAssertionAttributeName());
            }
            
        } else {
            if( wizardBean.getNewAssertionAttributeName().length() > 0 ) {
                entry.setAssertionAttributeName(wizardBean.getNewAssertionAttributeName());    			
            }
        }
        
        resetSamlAttributeInputFields();
    }

    public void cancelEditAttributeListener(ActionEvent event) {
        resetSamlAttributeInputFields();
    }

    public void removeAttributeListener(ActionEvent event) {
        StsManageWizardBean wizardBean = (StsManageWizardBean)getWizardBean();
        Integer index 
        = (Integer) event.getComponent().getAttributes().get("attributeIndex");
        
        if( index != null && wizardBean.getAttributeMapping() != null ) {
            SamlAttributeMapEntry entry 
                                = wizardBean.getAttributeMapping().get(index);
            
            if( entry != null) {
                if( !entry.isCustom() ) {
                    entry.setAssertionAttributeName(null);
                } else {
                    wizardBean.getAttributeMapping().remove(index.intValue());
                }
            }
        }
        
        resetSamlAttributeInputFields();
    }


    // Getters / Setters -------------------------------------------------------

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }

    public MessagesBean getMessagesBean() {
        return messagesBean;
    }

}

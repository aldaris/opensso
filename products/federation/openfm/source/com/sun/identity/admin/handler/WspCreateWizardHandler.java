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
 * $Id $
 */

package com.sun.identity.admin.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.sun.identity.admin.Resources;
import com.sun.identity.admin.model.LinkBean;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.NextPopupBean;
import com.sun.identity.admin.model.SamlAttributeMapItem;
import com.sun.identity.admin.model.SecurityMechanismPanelBean;
import com.sun.identity.admin.model.StsManageWizardBean;
import com.sun.identity.admin.model.UserCredentialItem;
import com.sun.identity.admin.model.WspCreateWizardBean;
import com.sun.identity.admin.model.WspCreateWizardStep;

public class WspCreateWizardHandler 
        extends WizardHandler 
        implements Serializable
{
    private MessagesBean messagesBean;
    
    @Override
    public void initWizardStepValidators() {
//        getWizardStepValidators()[WscCreateWizardStep.WSC_PROFILE.toInt()] = new WscCreateWizardStep1Validator(getWizardBean());
//        getWizardStepValidators()[WscCreateWizardStep.WSC_USING_STS.toInt()] = new WscCreateWizardStep2Validator(getWizardBean());
//        getWizardStepValidators()[WscCreateWizardStep.WSC_SECURITY.toInt()] = new WscCreateWizardStep3Validator(getWizardBean());
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
        lbs.add(LinkBean.WSP_CREATE);
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
        lbs.add(LinkBean.WSP_CREATE);
        return lbs;
    }
    
    private boolean save() {
        return true;
    }
    
    public void usingMexEndPointListener(ValueChangeEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        
        if( wizardBean.isUsingMexEndPoint()
                && wizardBean.getEndPoint() != null 
                && wizardBean.getEndPoint().length() > 0 ) {
            
            wizardBean.setMexEndPoint(wizardBean.getEndPoint() + "/mex");
        } else {
            wizardBean.setMexEndPoint(null);
        }

        // reset wizard state to ensure user revisits steps in case of changes
        wizardBean.getWizardStepBeans()[WspCreateWizardStep.SUMMARY.toInt()].setEnabled(false);
    }
    
    
    // listeners for the security mechanism panels -----------------------------
    
    public void securityMechanismPanelChangeListener(ValueChangeEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        ArrayList<SecurityMechanismPanelBean> panelBeans
            = wizardBean.getSecurityMechanismPanels();
        Object attributeValue 
            = event.getComponent().getAttributes().get("panelBean");

        if( attributeValue instanceof SecurityMechanismPanelBean ) {
            SecurityMechanismPanelBean activePanelBean
                = (SecurityMechanismPanelBean) attributeValue;
            
            if( activePanelBean.isChecked() ) {
                activePanelBean.setExpanded(true);
            } else {
                activePanelBean.setExpanded(false);
            }
            
            for(SecurityMechanismPanelBean panelBean : panelBeans) {
                if( panelBean != activePanelBean 
                        && activePanelBean.isCollapsible() ) {

                        panelBean.setExpanded(false);
                }
            }
        }   
    }
    
    public void securityMechanismPanelActionListener(ActionEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        ArrayList<SecurityMechanismPanelBean> panelBeans
            = wizardBean.getSecurityMechanismPanels();
        Object attributeValue 
            = event.getComponent().getAttributes().get("panelBean");
        
        if( attributeValue instanceof SecurityMechanismPanelBean ) {
            SecurityMechanismPanelBean activePanelBean 
                = (SecurityMechanismPanelBean) attributeValue;
            
            // only one is actively shown
            for(SecurityMechanismPanelBean panelBean : panelBeans) {
                if( activePanelBean == panelBean 
                        && panelBean.isChecked() 
                        && !panelBean.isExpanded() ) {
                    panelBean.setExpanded(true);
                } else {
                    panelBean.setExpanded(false);
                }
            }
        }   
    }
    
    // listeners for the user credential items ---------------------------------
    
    private boolean validUserCredential(String username, String password) {
        String regExp = "[\\w ]{1,50}?";
        if( username.matches(regExp) && password.matches(regExp) ) {
            return true;
        } else {
            showErrorPopup("invalidCredentialSummary", 
                           "invalidCredentialDetail");
            return false;
        }
    }
    
    public void userCredentialShowAddListener(ActionEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        wizardBean.setShowingAddCredential(true);
        wizardBean.setNewUserName(null);
        wizardBean.setNewPassword(null);
    }
    
    public void userCredentialCancelAddListener(ActionEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        wizardBean.setShowingAddCredential(false);
        wizardBean.setNewUserName(null);
        wizardBean.setNewPassword(null);
    }
    
    public void userCredentialAddListener(ActionEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        String newUserName = wizardBean.getNewUserName();
        String newPassword = wizardBean.getNewPassword();
        
        if( validUserCredential(newUserName, newPassword) ) {
            UserCredentialItem uci = new UserCredentialItem();
            uci.setUserName(newUserName);
            uci.setPassword(newPassword);
            wizardBean.getUserCredentialItems().add(uci);
            
            wizardBean.setShowingAddCredential(false);
            wizardBean.setNewUserName(null);
            wizardBean.setNewPassword(null);
        }
    }
    
    public void userCredentialEditListener(ActionEvent event) {
        Object attributeValue 
            = event.getComponent().getAttributes().get("userCredentialItem");
        
        if( attributeValue instanceof UserCredentialItem ) {
            UserCredentialItem uci = (UserCredentialItem) attributeValue;
            uci.setEditing(true);
            uci.setNewUserName(uci.getUserName());
            uci.setNewPassword(uci.getPassword());
        }
    }
    
    public void userCredentialSaveListener(ActionEvent event) {
        Object attributeValue 
            = event.getComponent().getAttributes().get("userCredentialItem");

        if( attributeValue instanceof UserCredentialItem ) {
            UserCredentialItem item = (UserCredentialItem) attributeValue;

            if( validUserCredential(item.getNewUserName(), item.getNewPassword()) ) {
                item.setUserName(item.getNewUserName());
                item.setPassword(item.getNewPassword());
                item.setEditing(false);
            }
        }
    }
    
    public void userCredentialCancelSaveListener(ActionEvent event) {
        Object attributeValue 
            = event.getComponent().getAttributes().get("userCredentialItem");
        
        if( attributeValue instanceof UserCredentialItem ) {
            UserCredentialItem item = (UserCredentialItem) attributeValue;
            item.setEditing(false);
        }
    }
    
    public void userCredentialRemoveListener(ActionEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        Object attributeValue 
            = event.getComponent().getAttributes().get("userCredentialItem");
        
        if( attributeValue instanceof UserCredentialItem ) {
            UserCredentialItem itemToRemove
                = (UserCredentialItem) attributeValue;
            wizardBean.getUserCredentialItems().remove(itemToRemove);
        }
    }
    
    // listeners for the attribute map items -----------------------------------

    private boolean validAttributeMapItem(String assertionName, String localName) {
        String regExp = "[\\w ]{1,50}?";
        if( assertionName.matches(regExp) && localName.matches(regExp) ) {
            return true;
        } else {
            showErrorPopup("invalidAttributeMapSummary", 
                           "invalidAttributeMapDetail");
            return false;
        }
    }
    
    public void attrMapShowAddListener(ActionEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        wizardBean.setShowingAddAttribute(true);
        wizardBean.setNewLocalAttributeName(null);
        wizardBean.setNewAssertionAttributeName(null);
    }
    
    public void attrMapCancelAddListener(ActionEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        wizardBean.setShowingAddAttribute(false);
        wizardBean.setNewLocalAttributeName(null);
        wizardBean.setNewAssertionAttributeName(null);
    }
    
    public void attrMapAddListener(ActionEvent event) {
        WspCreateWizardBean wizardBean = (WspCreateWizardBean) getWizardBean();
        String assertionAttrName = wizardBean.getNewAssertionAttributeName();
        String localAttrName = wizardBean.getNewLocalAttributeName();

        
        if( validAttributeMapItem(assertionAttrName, localAttrName) ) {
            SamlAttributeMapItem item = new SamlAttributeMapItem();
            item.setAssertionAttributeName(assertionAttrName);
            item.setLocalAttributeName(localAttrName);
            item.setCustom(true);
            item.setEditing(false);
            wizardBean.getAttributeMapping().add(item);
            
            wizardBean.setShowingAddAttribute(false);
            wizardBean.setNewAssertionAttributeName(null);
            wizardBean.setNewLocalAttributeName(null);
        } else {
            showErrorPopup("invalidMapAttributeSummary", 
                           "invalidMapAttributeDetail");
        }
    }
    
    public void attrMapEditListener(ActionEvent event) {
        Object attributeValue 
            = event.getComponent().getAttributes().get("attributeMapItem");
    
        if( attributeValue instanceof SamlAttributeMapItem ) {
            SamlAttributeMapItem item = (SamlAttributeMapItem) attributeValue;
            item.setEditing(true);
            item.setNewAssertionAttributeName(item.getAssertionAttributeName());
            item.setNewLocalAttributeName(item.getLocalAttributeName());
        }
    }
    
    public void attrMapSaveListener(ActionEvent event) {
        Object attributeValue 
            = event.getComponent().getAttributes().get("attributeMapItem");
    
        if( attributeValue instanceof SamlAttributeMapItem ) {
            SamlAttributeMapItem item = (SamlAttributeMapItem) attributeValue;
            String assertionAttrName = item.getNewAssertionAttributeName();
            String localAttrName = item.getNewLocalAttributeName();

            if( validAttributeMapItem(assertionAttrName, localAttrName) ) {
                
                if( !item.isCustom() ) {
                    item.setLocalAttributeName(item.getNewLocalAttributeName());
                    item.setAssertionAttributeName(item.getNewAssertionAttributeName());
                } else {
                    item.setAssertionAttributeName(item.getNewAssertionAttributeName());
                }

                item.resetInterface();
                
            } else {
                showErrorPopup("invalidMapAttributeSummary", 
                               "invalidMapAttributeDetail");
            }
        }
    }
    
    public void attrMapCancelSaveListener(ActionEvent event) {
        Object attributeValue 
            = event.getComponent().getAttributes().get("attributeMapItem");

        if( attributeValue instanceof SamlAttributeMapItem ) {
            SamlAttributeMapItem item = (SamlAttributeMapItem) attributeValue;
            item.setEditing(false);
        }
    }
    
    public void attrMapRemoveListener(ActionEvent event) {
        StsManageWizardBean wizardBean = (StsManageWizardBean) getWizardBean();
        Object attributeValue 
            = event.getComponent().getAttributes().get("attributeMapItem");
    
        if( attributeValue instanceof SamlAttributeMapItem ) {
            SamlAttributeMapItem itemToRemove
                = (SamlAttributeMapItem) attributeValue;
            
            if( itemToRemove.isCustom() ) {
                wizardBean.getAttributeMapping().remove(itemToRemove);
            } else {
                itemToRemove.setAssertionAttributeName(null);
            }
        }
    }
  

    // -------------------------------------------------------------------------
    
    private void showErrorPopup(String summaryKey, String detailKey) {
        Resources r = new Resources();
        MessageBean mb = new MessageBean(); 
        mb.setSummary(r.getString(this, summaryKey));
        mb.setDetail(r.getString(this, detailKey));
        mb.setSeverity(FacesMessage.SEVERITY_ERROR);

        getMessagesBean().addMessageBean(mb);
    }
    
    // Getters / Setters -------------------------------------------------------

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }

    public MessagesBean getMessagesBean() {
        return messagesBean;
    }

}
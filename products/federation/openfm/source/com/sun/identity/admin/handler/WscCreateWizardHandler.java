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

import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.model.LinkBean;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.NextPopupBean;
import com.sun.identity.admin.model.SecurityTokenServiceType;
import com.sun.identity.admin.model.WscCreateWizardBean;
import com.sun.identity.admin.model.WscCreateWizardStep;
import com.sun.identity.admin.model.WscCreateWizardStep1Validator;
import com.sun.identity.admin.model.WscCreateWizardStep2Validator;
import com.sun.identity.admin.model.WscCreateWizardStep3Validator;
import com.sun.identity.admin.model.WssClientProfileBean;
import com.sun.identity.admin.model.WssProviderProfileBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

public class WscCreateWizardHandler 
        extends WizardHandler 
        implements Serializable
{
    private MessagesBean messagesBean;
    
    @Override
    public void initWizardStepValidators() {
        getWizardStepValidators()[WscCreateWizardStep.WSC_PROFILE.toInt()] = new WscCreateWizardStep1Validator(getWizardBean());
        getWizardStepValidators()[WscCreateWizardStep.WSC_USING_STS.toInt()] = new WscCreateWizardStep2Validator(getWizardBean());
        getWizardStepValidators()[WscCreateWizardStep.WSC_SECURITY.toInt()] = new WscCreateWizardStep3Validator(getWizardBean());
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
        lbs.add(LinkBean.WSC_CREATE);
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
        lbs.add(LinkBean.WSC_CREATE);
        return lbs;
    }


    public void wspEndPointListener(ValueChangeEvent event) {

        if (event.getComponent() instanceof SelectInputText) {

            SelectInputText sit = (SelectInputText) event.getComponent();
            String newEndPoint = (String) event.getNewValue();

            WscCreateWizardBean wizardBean = (WscCreateWizardBean) getWizardBean();
            ArrayList wspProfilePossibilities = new ArrayList();

            WssProviderProfileBean tmp;

            tmp = new WssProviderProfileBean();
            tmp.setEndPoint("http://example.com/path/to/service");
            tmp.setProfileName("Example Service");
            wspProfilePossibilities.add(tmp);

            tmp = new WssProviderProfileBean();
            tmp.setEndPoint("http://example.net/path/to/other/service");
            tmp.setProfileName("Other Example Service");
            wspProfilePossibilities.add(tmp);

            tmp = new WssProviderProfileBean();
            tmp.setEndPoint("http://something.com/service");
            tmp.setProfileName("Some Service");
            wspProfilePossibilities.add(tmp);

            tmp = new WssProviderProfileBean();
            tmp.setEndPoint("http://something.org/StockQuote");
            tmp.setProfileName("StockQuoteService");
            wspProfilePossibilities.add(tmp);

            tmp = new WssProviderProfileBean();
            tmp.setEndPoint("http://whatever.com/path/to/service");
            tmp.setProfileName("Yet Another Service");
            wspProfilePossibilities.add(tmp);

            ArrayList wspProfileSuggestions = new ArrayList();
            Iterator i = wspProfilePossibilities.iterator();
            while( i.hasNext() ) {
                WssProviderProfileBean wsp = (WssProviderProfileBean)i.next();
                if( wsp.getEndPoint().startsWith(newEndPoint) ) {
                    wspProfileSuggestions.add(new SelectItem(wsp, wsp.getEndPoint()));
                }
                if( wspProfileSuggestions.size() >=  sit.getRows() ) {
                    break;
                }
            }

            wizardBean.setWspProfileSuggestions(wspProfileSuggestions);
        }
        
    }

    public void stsTypeListener(ValueChangeEvent event) {

        Integer oldValue = (Integer)event.getOldValue();
        Integer newValue = (Integer)event.getNewValue();

        if( oldValue != newValue ) {
            WscCreateWizardBean wizardBean
                    = (WscCreateWizardBean) getWizardBean();
            SecurityTokenServiceType stsType
                    = SecurityTokenServiceType.valueOf(newValue);

            switch(stsType) {
                case OPENSSO:
                    wizardBean.setUsingSts(true);
                    wizardBean.setUsingOurSts(true);
                    break;
                case OTHER:
                    wizardBean.setUsingSts(true);
                    wizardBean.setUsingOurSts(false);
                    break;
                default:
                    wizardBean.setUsingSts(false);
                    wizardBean.setUsingOurSts(false);
                    break;
            }

            // reset wizard state to ensure user revisits steps in case of changes
            wizardBean.getWizardStepBeans()[WscCreateWizardStep.WSC_SIGN_ENCRYPT.toInt()].setEnabled(false);
            wizardBean.getWizardStepBeans()[WscCreateWizardStep.SUMMARY.toInt()].setEnabled(false);
        }
    }

    public void usingMexEndPointListener(ValueChangeEvent event) {

        WscCreateWizardBean wizardBean
                = (WscCreateWizardBean) getWizardBean();
        WssClientProfileBean wscProfileBean = wizardBean.getWscProfileBean();

        if( wscProfileBean.isUsingMexEndPoint()
                && wscProfileBean.getEndPoint() != null 
                && wscProfileBean.getEndPoint().length() > 0 )
        {
            wscProfileBean.setMexEndPoint(wscProfileBean.getEndPoint() + "/mex");
        } else {
            wscProfileBean.setMexEndPoint(null);
        }

        // reset wizard state to ensure user revisits steps in case of changes
        wizardBean.getWizardStepBeans()[WscCreateWizardStep.WSC_SIGN_ENCRYPT.toInt()].setEnabled(false);
        wizardBean.getWizardStepBeans()[WscCreateWizardStep.SUMMARY.toInt()].setEnabled(false);
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



    // Getters / Setters -------------------------------------------------------

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }

    public MessagesBean getMessagesBean() {
        return messagesBean;
    }

}

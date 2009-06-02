package com.sun.identity.admin.handler;

import com.sun.identity.admin.Resources;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.PhaseEventAction;
import com.sun.identity.admin.model.PolicyCreateWizardBean;
import com.sun.identity.admin.model.PolicyEditWizardBean;
import com.sun.identity.admin.model.PolicyManageBean;
import com.sun.identity.admin.model.QueuedActionBean;
import com.sun.identity.admin.model.RealmsBean;
import com.sun.identity.admin.model.ViewApplicationsBean;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

public class RealmsHandler implements Serializable {
    private RealmsBean realmsBean;
    private QueuedActionBean queuedActionBean;
    private MessagesBean messagesBean;

    public void realmChanged(ValueChangeEvent event) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(true);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{realmsHandler.handleReset}");
        pea.setParameters(new Class[]{});
        pea.setArguments(new Object[]{});

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public void realmSelectListener(ActionEvent event) {
        realmsBean.setRealmSelectPopupRealmBean(realmsBean.getRealmBean());
        realmsBean.setRealmSelectPopupVisible(true);
    }

    public void realmSelectPopupOkListener(ActionEvent event) {
        if (realmsBean.getRealmSelectPopupRealmBean() != null) {
            realmsBean.setRealmBean(realmsBean.getRealmSelectPopupRealmBean());
            handleReset();
        } else {
            MessageBean mb = new MessageBean();
            Resources r = new Resources();
            mb.setSummary(r.getString(this, "emptyRealmSummary"));
            mb.setDetail(r.getString(this, "emptyRealmDetail"));
            mb.setSeverity(FacesMessage.SEVERITY_ERROR);
            messagesBean.addMessageBean(mb);
        }


        realmsBean.resetRealmSelectPopup();
    }

    public void realmSelectPopupCancelListener(ActionEvent event) {
        realmsBean.resetRealmSelectPopup();
    }

    public void handleReset() {
        PolicyManageBean pmb = PolicyManageBean.getInstance();
        pmb.reset();
        PolicyCreateWizardBean pcwb = PolicyCreateWizardBean.getInstance();
        pcwb.reset();
        PolicyEditWizardBean pewb = PolicyEditWizardBean.getInstance();
        pewb.reset();
        ViewApplicationsBean vasb = ViewApplicationsBean.getInstance();
        vasb.reset();
    }

    public void setRealmsBean(RealmsBean realmsBean) {
        this.realmsBean = realmsBean;
    }

    public void setQueuedActionBean(QueuedActionBean queuedActionBean) {
        this.queuedActionBean = queuedActionBean;
    }

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }
}

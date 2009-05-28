package com.sun.identity.admin.handler;

import com.sun.identity.admin.Resources;
import com.sun.identity.admin.dao.PolicyDao;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.PhaseEventAction;
import com.sun.identity.admin.model.PolicyFilterHolder;
import com.sun.identity.admin.model.PolicyManageBean;
import com.sun.identity.admin.model.PolicyWizardBean;
import com.sun.identity.admin.model.PrivilegeBean;
import com.sun.identity.admin.model.QueuedActionBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

public class PolicyManageHandler implements Serializable {

    private PolicyManageBean policyManageBean;
    private QueuedActionBean queuedActionBean;
    private PolicyDao policyDao;
    private PolicyWizardBean policyEditWizardBean;
    private MessagesBean messagesBean;

    public PrivilegeBean getPrivilegeBean(ActionEvent event) {
        PrivilegeBean pb = (PrivilegeBean) event.getComponent().getAttributes().get("privilegeBean");
        assert (pb != null);

        return pb;
    }

    public PolicyFilterHolder getPolicyFilterHolder(ActionEvent event) {
        PolicyFilterHolder pfh = (PolicyFilterHolder) event.getComponent().getAttributes().get("policyFilterHolder");
        assert (pfh != null);

        return pfh;
    }

    public PolicyManageBean getPolicyManageBean() {
        return policyManageBean;
    }

    public void setPolicyManageBean(PolicyManageBean policyManageBean) {
        this.policyManageBean = policyManageBean;
    }

    public void sortTableListener(ActionEvent event) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(true);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{policyManageHandler.handleSort}");
        pea.setParameters(new Class[]{});
        pea.setArguments(new Object[]{});

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    private void addResetEvent() {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(true);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{policyManageHandler.handleReset}");
        pea.setParameters(new Class[]{});
        pea.setArguments(new Object[]{});

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public void handleSort() {
        policyManageBean.getPolicyManageTableBean().sort();
    }

    public void handleReset() {
        policyManageBean.reset();
    }

    public void viewOptionsListener(ActionEvent event) {
        policyManageBean.setViewOptionsPopupVisible(!policyManageBean.isViewOptionsPopupVisible());
    }

    public void actionPopupOkListener(ActionEvent event) {
        String action = policyManageBean.getAction();
        if (action.equals("remove")) {
            PhaseEventAction pea = new PhaseEventAction();
            pea.setDoBeforePhase(true);
            pea.setPhaseId(PhaseId.RENDER_RESPONSE);
            pea.setAction("#{policyManageHandler.handleRemoveAction}");
            pea.setParameters(new Class[]{});
            pea.setArguments(new Object[]{});

            queuedActionBean.getPhaseEventActions().add(pea);
        } else if (action.equals("export")) {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            try {
                String exportUrl = getExportUrl();
                ec.redirect(exportUrl);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        policyManageBean.setActionPopupVisible(false);
    }

    private String getExportUrl() {
        StringBuffer b = new StringBuffer();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        b.append(ec.getRequestContextPath());
        b.append("/admin/pxml?");
        for (PrivilegeBean pb: policyManageBean.getPrivilegeBeans()) {
            if (pb.isSelected()) {
                b.append("name=");
                b.append(pb.getName());
                b.append("&");
            }
        }

        String u = ec.encodeResourceURL(b.toString());
        return u;
    }

    public void selectAllListener(ActionEvent event) {
        policyManageBean.setSelectAll(!policyManageBean.isSelectAll());
        for (PrivilegeBean pb: policyManageBean.getPrivilegeBeans()) {
            pb.setSelected(policyManageBean.isSelectAll());
        }
    }

    public void actionPopupCancelListener(ActionEvent event) {
        policyManageBean.setActionPopupVisible(false);
    }

    public void addPolicyFilterListener(ActionEvent event) {
        getPolicyManageBean().newPolicyFilterHolder();
        addResetEvent();
    }

    public void policyFilterChangedListener(ValueChangeEvent event) {
        addResetEvent();
    }

    public void policyFilterChangedListener(ActionEvent event) {
        addResetEvent();
    }

    public void removePolicyFilterListener(ActionEvent event) {
        PolicyFilterHolder pfh = getPolicyFilterHolder(event);
        getPolicyManageBean().getPolicyFilterHolders().remove(pfh);
        addResetEvent();
    }

    public void editListener(ActionEvent event) {
        PrivilegeBean pb = getPrivilegeBean(event);
        policyEditWizardBean.reset();
        policyEditWizardBean.setPrivilegeBean(pb);
        policyEditWizardBean.setAllEnabled(true);
        policyEditWizardBean.gotoStep(4);
    }

    public void actionListener(ActionEvent event) {
        if (!policyManageBean.isActionPopupVisible()) {
            if (policyManageBean.getSizeSelected() == 0) {
                MessageBean mb = new MessageBean();
                Resources r = new Resources();
                mb.setSummary(r.getString(this, "actionNoneSelectedSummary"));
                mb.setDetail(r.getString(this, "actionNoneSelectedDetail"));
                mb.setSeverity(FacesMessage.SEVERITY_ERROR);
                messagesBean.addMessageBean(mb);
            } else {
                policyManageBean.setActionPopupVisible(true);
            }
        } else {
            policyManageBean.setActionPopupVisible(false);
        }
    }

    public void removeListener(ActionEvent event) {
        PrivilegeBean pb = getPrivilegeBean(event);
        assert (pb != null);

        addRemoveAction(pb);
    }

    public void handlePolicyRemove(PrivilegeBean pb) {
        policyManageBean.getPrivilegeBeans().remove(pb);
        policyDao.removePrivilege(pb.getName());
    }

    public void handleRemoveAction() {
        Set<PrivilegeBean> removed = new HashSet<PrivilegeBean>();
        for (PrivilegeBean pb : policyManageBean.getPrivilegeBeans()) {
            if (pb.isSelected()) {
                removed.add(pb);
                policyDao.removePrivilege(pb.getName());
            }
            pb.setSelected(false);
        }
        policyManageBean.getPrivilegeBeans().removeAll(removed);
    }

    private void addRemoveAction(PrivilegeBean pb) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(false);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{policyManageHandler.handlePolicyRemove}");
        pea.setParameters(new Class[]{PrivilegeBean.class});
        pea.setArguments(new Object[]{pb});

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public void setQueuedActionBean(QueuedActionBean queuedActionBean) {
        this.queuedActionBean = queuedActionBean;
    }

    public void setPolicyDao(PolicyDao policyDao) {
        this.policyDao = policyDao;
    }

    public void setPolicyEditWizardBean(PolicyWizardBean policyEditWizardBean) {
        this.policyEditWizardBean = policyEditWizardBean;
    }

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }
}


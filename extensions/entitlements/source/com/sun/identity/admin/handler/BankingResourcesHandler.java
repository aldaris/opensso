package com.sun.identity.admin.handler;

import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.admin.dao.SubjectDao;
import com.sun.identity.admin.model.BankingResource;
import com.sun.identity.admin.model.BankingResourcesBean;
import com.sun.identity.admin.model.IdRepoUserViewSubject;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.SubjectType;
import com.sun.identity.admin.model.ViewEntitlement;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;

public class BankingResourcesHandler implements Serializable {

    private BankingResourcesBean bankingResourcesBean;
    private SubjectDao subjectDao = null;
    private SubjectType subjectType;

    private BankingResource getBankingResource(FacesEvent event) {
        BankingResource br = (BankingResource) event.getComponent().getAttributes().get("bankingResource");
        assert (br != null);

        return br;
    }

    private ViewEntitlement getViewEntitlement(FacesEvent event) {
        ViewEntitlement ve = (ViewEntitlement) event.getComponent().getAttributes().get("viewEntitlement");
        assert (ve != null);

        return ve;
    }

    public void addListener(ActionEvent event) {
        List<ViewSubject> viewSubjects = subjectDao.getViewSubjects();
        bankingResourcesBean.setViewSubjects(viewSubjects);
        bankingResourcesBean.setAddPopupVisible(true);
    }

    public void removeListener(ActionEvent event) {
        BankingResource br = getBankingResource(event);
        ViewEntitlement ve = getViewEntitlement(event);

        ve.getResources().remove(br);
    }

    public void addPopupOkListener(ActionEvent event) {
        ViewEntitlement ve = getViewEntitlement(event);
        IdRepoUserViewSubject idus = (IdRepoUserViewSubject) bankingResourcesBean.getAddPopupViewSubject();

        if (idus == null) {
            MessageBean mb = new MessageBean();
            mb.setSummary("No match");
            mb.setDetail("No matching account found");
            mb.setSeverity(FacesMessage.SEVERITY_ERROR);
            ManagedBeanResolver mbr = new ManagedBeanResolver();
            MessagesBean msb = (MessagesBean) mbr.resolve("messagesBean");
            msb.addMessageBean(mb);
        } else {
            BankingResource br = new BankingResource();
            br.setName(idus.getEmployeeNumber());
            br.setOwner(idus);
            ve.getResources().add(br);

            bankingResourcesBean.reset();
        }
    }

    public void addPopupCancelListener(ActionEvent event) {
        bankingResourcesBean.reset();
    }

    public void setBankingResourcesBean(BankingResourcesBean bankingResourcesBean) {
        this.bankingResourcesBean = bankingResourcesBean;
    }

    public void addPopupAccountNumberChangedListener(ValueChangeEvent event) {
        if (event.getComponent() instanceof SelectInputText) {
            SelectInputText ac = (SelectInputText) event.getComponent();
            String newWord = (String) event.getNewValue();
            List<ViewSubject> viewSubjects = subjectDao.getViewSubjects(newWord);
            bankingResourcesBean.setViewSubjects(viewSubjects);

            if (ac.getSelectedItem() != null) {
                ViewSubject vs = (ViewSubject) ac.getSelectedItem().getValue();
                bankingResourcesBean.setAddPopupViewSubject(vs);
            } else {
                if (viewSubjects.size() == 1) {
                    bankingResourcesBean.setAddPopupViewSubject(viewSubjects.get(0));
                } else {
                    bankingResourcesBean.setAddPopupViewSubject(null);
                }
            }
        }
    }

    public void setSubjectDao(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }
}

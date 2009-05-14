package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.model.SelectItem;

public class BankingResourcesBean implements Serializable {

    private boolean addPopupVisible;
    private String addPopupAccountNumber;
    private List<ViewSubject> viewSubjects;
    private ViewSubject addPopupViewSubject;

    public BankingResourcesBean() {
        reset();
    }

    public void reset() {
        addPopupVisible = false;
        addPopupAccountNumber = null;
        viewSubjects = Collections.EMPTY_LIST;
        addPopupViewSubject = null;
    }

    public boolean isAddPopupVisible() {
        return addPopupVisible;
    }

    public void setAddPopupVisible(boolean addPopupVisible) {
        this.addPopupVisible = addPopupVisible;
    }

    public List<SelectItem> getViewSubjectItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (ViewSubject vs : viewSubjects) {
            IdRepoUserViewSubject idus = (IdRepoUserViewSubject) vs;
            SelectItem item;
            if (idus.getEmployeeNumber() != null && idus.getEmployeeNumber().length() > 0) {
                item = new SelectItem(idus, idus.getEmployeeNumber());
                items.add(item);
            }
        }
        return items;
    }

    public List<ViewSubject> getViewSubjects() {
        return viewSubjects;
    }

    public void setViewSubjects(List<ViewSubject> viewSubjects) {
        this.viewSubjects = viewSubjects;
    }

    public String getAddPopupAccountNumber() {
        return addPopupAccountNumber;
    }

    public void setAddPopupAccountNumber(String addPopupAccountNumber) {
        this.addPopupAccountNumber = addPopupAccountNumber;
    }

    public ViewSubject getAddPopupViewSubject() {
        return addPopupViewSubject;
    }

    public void setAddPopupViewSubject(ViewSubject addPopupViewSubject) {
        this.addPopupViewSubject = addPopupViewSubject;
    }
}

package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import com.sun.identity.admin.handler.ChooserSubjectHandler;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ChooserSubjectContainer
        extends BaseSubjectContainer
        implements Serializable {

    private List<ViewSubject> available = new ArrayList<ViewSubject>();
    private List<ViewSubject> selected = new ArrayList<ViewSubject>();
    private List<ViewSubject> filteredAvailable = new ArrayList<ViewSubject>();
    private List<ViewSubject> filteredSelected = new ArrayList<ViewSubject>();
    private String availableFilter;
    private String selectedFilter;
    private ChooserSubjectHandler chooserSubjectHandler = new ChooserSubjectHandler();

    public ChooserSubjectContainer() {
        chooserSubjectHandler = new ChooserSubjectHandler();
        chooserSubjectHandler.setChooserSubjectContainer(this);
    }

    @Override
    public void setSubjectDao(SubjectDao subjectDao) {
        super.setSubjectDao(subjectDao);
        available.addAll(subjectDao.getViewSubjects());
        filteredAvailable.addAll(available);
        filteredSelected = getViewSubjects();
    }

    public ChooserSubjectHandler getChooserSubjectHandler() {
        return chooserSubjectHandler;
    }

    public void setChooserSubjectHandler(ChooserSubjectHandler chooserSubjectHandler) {
        this.chooserSubjectHandler = chooserSubjectHandler;
    }

    public List<ViewSubject> getAvailable() {
        return available;
    }

    public void setAvailable(List<ViewSubject> available) {
        this.available = available;
    }

    public List<ViewSubject> getSelected() {
        return selected;
    }

    public void setSelected(List<ViewSubject> selected) {
        this.selected = selected;
    }

    public String getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public String getAvailableFilter() {
        return availableFilter;
    }

    public void setAvailableFilter(String availableFilter) {
        this.availableFilter = availableFilter;
    }

    public List<ViewSubject> getFilteredAvailable() {
        return filteredAvailable;
    }

    public void setFilteredAvailable(List<ViewSubject> filteredAvailable) {
        this.filteredAvailable = filteredAvailable;
    }

    public List<ViewSubject> getFilteredSelected() {
        return filteredSelected;
    }

    public void setFilteredSelected(List<ViewSubject> filteredSelected) {
        this.filteredSelected = filteredSelected;
    }
}

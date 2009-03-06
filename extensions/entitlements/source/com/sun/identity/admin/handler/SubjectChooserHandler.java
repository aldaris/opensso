package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.ChooserSubject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.event.ActionEvent;

public class SubjectChooserHandler implements Serializable {
    private List<ChooserSubject> availableChooserSubjects;
    private List<ChooserSubject> selectedChooserSubjects;

    public void addSelectedListener(ActionEvent event) {
        listMove(availableChooserSubjects, selectedChooserSubjects);
        setSelected(availableChooserSubjects, false);
        setSelected(selectedChooserSubjects, false);
    }

    public void addAllListener(ActionEvent event) {
        selectedChooserSubjects.addAll(availableChooserSubjects);
        availableChooserSubjects.clear();
        setSelected(selectedChooserSubjects, false);
    }

    public void removeSelectedListener(ActionEvent event) {
        listMove(selectedChooserSubjects, availableChooserSubjects);
        setSelected(availableChooserSubjects, false);
        setSelected(selectedChooserSubjects, false);
    }

    public void removeAllListener(ActionEvent event) {
        availableChooserSubjects.addAll(selectedChooserSubjects);
        selectedChooserSubjects.clear();
        setSelected(availableChooserSubjects, false);
    }

    private void listMove(List<ChooserSubject> src, List<ChooserSubject> dest) {
        List<ChooserSubject> moved = new ArrayList<ChooserSubject>();

        for (ChooserSubject cs: src) {
            if (cs.isSelected()) {
                moved.add(cs);
            }
        }

        src.removeAll(moved);
        dest.addAll(moved);
    }

    private void setSelected(List<ChooserSubject> src, boolean selected) {
        for (ChooserSubject cs: src) {
            cs.setSelected(selected);
        }
    }
}

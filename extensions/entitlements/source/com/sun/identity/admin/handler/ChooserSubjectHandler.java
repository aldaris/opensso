package com.sun.identity.admin.handler;

import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.dragdrop.DndEvent;
import java.io.Serializable;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.sun.identity.admin.model.ChooserSubject;
import com.sun.identity.admin.model.ChooserSubjectContainer;
import com.sun.identity.admin.model.ViewSubject;
import java.util.ArrayList;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

public class ChooserSubjectHandler implements Serializable {

    private ChooserSubjectContainer chooserSubjectContainer;

    public void addSelectedListener(ActionEvent event) {
        listMove(getChooserSubjectContainer().getAvailable(), getChooserSubjectContainer().getSelected());
        setSelected(getChooserSubjectContainer().getAvailable(), false);
        setSelected(getChooserSubjectContainer().getSelected(), false);

        filterBoth();
    }

    public void addAllListener(ActionEvent event) {
        getChooserSubjectContainer().getSelected().addAll(getChooserSubjectContainer().getAvailable());
        getChooserSubjectContainer().getAvailable().clear();
        setSelected(getChooserSubjectContainer().getSelected(), false);

        filterBoth();
    }

    public void removeSelectedListener(ActionEvent event) {
        listMove(getChooserSubjectContainer().getSelected(), getChooserSubjectContainer().getAvailable());
        setSelected(getChooserSubjectContainer().getAvailable(), false);
        setSelected(getChooserSubjectContainer().getSelected(), false);

        filterBoth();
    }

    public void removeAllListener(ActionEvent event) {
        getChooserSubjectContainer().getAvailable().addAll(getChooserSubjectContainer().getSelected());
        getChooserSubjectContainer().getSelected().clear();
        setSelected(getChooserSubjectContainer().getAvailable(), false);

        filterBoth();
    }

    private void filterBoth() {
        applyFilter(
                chooserSubjectContainer.getAvailable(),
                chooserSubjectContainer.getFilteredAvailable(),
                chooserSubjectContainer.getAvailableFilter());
        applyFilter(
                chooserSubjectContainer.getSelected(),
                chooserSubjectContainer.getFilteredSelected(),
                chooserSubjectContainer.getSelectedFilter());
    }

    private void listMove(List<ViewSubject> src, List<ViewSubject> dest) {
        List<ChooserSubject> moved = new ArrayList<ChooserSubject>();

        for (ViewSubject vs : src) {
            ChooserSubject cs = (ChooserSubject) vs;
            if (cs.isSelected()) {
                moved.add(cs);
            }
        }

        src.removeAll(moved);
        dest.addAll(moved);
    }

    private void setSelected(List<ViewSubject> src, boolean selected) {
        for (ViewSubject vs : src) {
            ChooserSubject cs = (ChooserSubject) vs;
            cs.setSelected(selected);
        }
    }

    public void dropListener(DropEvent dropEvent) {
        int type = dropEvent.getEventType();
        if (type == DndEvent.DROPPED) {
            List<ViewSubject> dest = (List<ViewSubject>) dropEvent.getTargetDropValue();
            List<ViewSubject> src = (List<ViewSubject>) dropEvent.getTargetDragValue();

            List<ViewSubject> dragged = new ArrayList<ViewSubject>();

            for (ViewSubject vs : src) {
                ChooserSubject cs = (ChooserSubject) vs;
                if (cs.isSelected()) {
                    dragged.add(cs);
                    dest.add(cs);
                    cs.setSelected(false);
                }
            }
            src.removeAll(dragged);
        }

        filterBoth();
    }

    public void selectionListener(RowSelectorEvent event) {
        // TODO? set draggable?
    }

    public void filterListener(ValueChangeEvent event) {
        String filter = (String) event.getNewValue();
        List<ViewSubject> master = (List<ViewSubject>) event.getComponent().getAttributes().get("master");
        List<ViewSubject> filtered = (List<ViewSubject>) event.getComponent().getAttributes().get("filtered");

        applyFilter(master, filtered, filter);
    }

    private void applyFilter(List<ViewSubject> master, List<ViewSubject> filtered, String filter) {
        filtered.clear();

        for (ViewSubject vs : master) {
            ChooserSubject cs = (ChooserSubject) vs;
            if (cs.getName().contains(filter)) {
                filtered.add(cs);
            }
        }
    }

    public ChooserSubjectContainer getChooserSubjectContainer() {
        return chooserSubjectContainer;
    }

    public void setChooserSubjectContainer(ChooserSubjectContainer chooserSubjectContainer) {
        this.chooserSubjectContainer = chooserSubjectContainer;
    }
}

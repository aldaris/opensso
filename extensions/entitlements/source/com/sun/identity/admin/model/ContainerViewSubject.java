package com.sun.identity.admin.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ContainerViewSubject extends ViewSubject implements ContainerTreeNode {
    private List<ViewSubject> viewSubjects = new ArrayList<ViewSubject>();

    public ContainerViewSubject() {
        super();
    }

    public List<ViewSubject> getViewSubjects() {
        return viewSubjects;
    }

    public List getTreeNodes() {
        return viewSubjects;
    }
    
    public void addViewSubject(ViewSubject vs) {
        viewSubjects.add(vs);
    }

    public int getViewSubjectsSize() {
        return viewSubjects.size();
    }

    @Override
    public String getToString() {
        return toString();
    }

    @Override
    public String getToFormattedString() {
        return getToFormattedString(0);
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(getTitle() + " (");

        if (getViewSubjects().size() > 0) {
            for (Iterator<ViewSubject> i = getViewSubjects().iterator(); i.hasNext();) {
                b.append(i.next().toString());
                if (i.hasNext()) {
                    b.append(",");
                }
            }
        }
        b.append(")");

        return b.toString();
    }

    @Override
    String getToFormattedString(int i) {
        StringBuffer b = new StringBuffer();
        String indent = getIndentString(i);

        b.append(indent);
        b.append(getTitle());
        b.append(" (\n");

        if (getViewSubjects().size() > 0) {
            for (Iterator<ViewSubject> iter = getViewSubjects().iterator(); iter.hasNext();) {
                b.append(iter.next().getToFormattedString(i+2));
                if (iter.hasNext()) {
                    b.append(",\n");
                }
            }
        }
        b.append("\n");
        b.append(indent);
        b.append(")");

        return b.toString();
    }

    @Override
    public String getTitle() {
        return getSubjectType().getTitle();
    }
}

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
    public String getTitle() {
        return getName() + " (" + getViewSubjectsSize() + ")";

    }

    protected abstract String getOperatorString();

    @Override
    public String getToString() {
        return toString();
    }

    @Override
    public String getToFormattedString() {
        return getToFormattedString(0);
    }

    String getToFormattedString(int i) {
        StringBuffer b = new StringBuffer();
        String indent = getIndentString(i);

        b.append(indent);
        b.append(getOperatorString());
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

}

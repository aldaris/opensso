package com.sun.identity.admin.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerViewCondition extends ViewCondition implements ContainerTreeNode {

    public ContainerViewCondition() {
        super();
    }

    private List<ViewCondition> viewConditions = new ArrayList<ViewCondition>();

    public void addViewCondition(ViewCondition vc) {
        viewConditions.add(vc);
    }

    public List<ViewCondition> getViewConditions() {
        return viewConditions;
    }

    public int getViewConditionsSize() {
        return viewConditions.size();
    }

    public List getTreeNodes() {
        return viewConditions;
    }

    @Override
    public String getTitle() {
        return getName() + " (" + getViewConditionsSize() + ")";

    }
}

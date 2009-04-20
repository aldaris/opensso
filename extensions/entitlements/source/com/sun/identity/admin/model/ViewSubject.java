package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Appear;
import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.entitlement.EntitlementSubject;

public abstract class ViewSubject implements MultiPanelBean, TreeNode {
    private boolean expanded = true;
    private Effect expandEffect;
    private Effect panelEffect;
    private boolean visible = false;
    private SubjectType subjectType;
    private String name;

    public ViewSubject() {
        panelEffect = new Appear();
        panelEffect.setSubmit(true);
        panelEffect.setTransitory(false);
    }

    public abstract EntitlementSubject getEntitlementSubject();

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Effect getExpandEffect() {
        return expandEffect;
    }

    public void setExpandEffect(Effect expandEffect) {
        this.expandEffect = expandEffect;
    }

    public Effect getPanelEffect() {
        return panelEffect;
    }

    public void setPanelEffect(Effect panelEffect) {
        this.panelEffect = panelEffect;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (!this.visible) {
            this.visible = visible;
        }
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public String getName() {
        return name != null ? name : subjectType.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return getName();
    }

    public String getToString() {
        return toString();
    }

    public int getSize() {
        Tree t = new Tree(this);
        return t.size();
    }
    
    @Override
    public String toString() {
        return getName();
    }
}

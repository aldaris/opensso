package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Appear;
import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.entitlement.EntitlementCondition;
import java.util.List;

public abstract class ViewCondition implements MultiPanelBean, TreeNode {

    private ConditionType conditionType;
    private boolean expanded = true;
    private String name;
    private Effect expandEffect;
    private Effect panelEffect;
    private boolean visible = false;
    private int titleWidth = 40;

    public ViewCondition() {
        panelEffect = new Appear();
        panelEffect.setSubmit(true);
        panelEffect.setTransitory(false);
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getName() {
        return (name == null) ? conditionType.getName() : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Effect getExpandEffect() {
        return expandEffect;
    }

    public void setExpandEffect(Effect expandEffect) {
        this.expandEffect = expandEffect;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (!this.visible) {
            this.visible = visible;
        }
    }

    public Effect getPanelEffect() {
        return panelEffect;
    }

    public void setPanelEffect(Effect panelEffect) {
        this.panelEffect = panelEffect;
    }

    public abstract EntitlementCondition getEntitlementCondition();

    public String getTitle() {
        return getName();
    }

    public String getToFormattedString() {
        return toString();
    }

    String getToFormattedString(int i) {
        return getIndentString(i) + toString();
    }

    public String getToString() {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getSize() {
        Tree t = new Tree(this);
        return t.size();
    }

    String getIndentString(int i) {
        String indent = "";
        for (int j = 0; j < i; j++) {
            indent += " ";
        }

        return indent;
    }

    public List<TreeNode> asList() {
        return new Tree(this).asList();
    }

    public List<TreeNode> getAsList() {
        return asList();
    }

    public int getTitleWidth() {
        return titleWidth;
    }
}

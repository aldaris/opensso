package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import java.io.Serializable;

public class BasicMultiPanelBean implements MultiPanelBean, Serializable {
    private Effect expandEffect;
    private Effect panelEffect;
    private boolean expanded = true;
    private boolean visible = true;

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

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

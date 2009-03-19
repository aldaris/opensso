package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;

public interface MultiPanelBean {
    public Effect getExpandEffect();
    public void setExpandEffect(Effect e);
    public Effect getPanelEffect();
    public void setPanelEffect(Effect e);
    public boolean isExpanded();
    public void setExpanded(boolean expanded);
    public boolean isVisible();
    public void setVisible(boolean visible);
}

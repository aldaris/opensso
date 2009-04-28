package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;

public interface MultiPanelBean {
    public Effect getPanelExpandEffect();
    public void setPanelExpandEffect(Effect e);
    public Effect getPanelEffect();
    public void setPanelEffect(Effect e);
    public boolean isPanelExpanded();
    public void setPanelExpanded(boolean expanded);
    public boolean isPanelVisible();
    public void setPanelVisible(boolean visible);
}

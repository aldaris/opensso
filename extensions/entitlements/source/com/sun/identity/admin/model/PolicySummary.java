package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.SlideDown;
import com.icesoft.faces.context.effects.SlideUp;
import com.sun.identity.admin.handler.MultiPanelHandler;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public abstract class PolicySummary
    implements Serializable, MultiPanelBean, MultiPanelHandler {

    private PolicyWizardBean policyWizardBean;
    private Effect panelExpandEffect;
    private Effect panelEffect;
    private boolean panelExpanded = false;
    private boolean panelVisible = true;

    public PolicySummary(PolicyWizardBean policyWizardBean) {
        this.policyWizardBean = policyWizardBean;
    }

    public abstract String getLabel();

    public abstract String getValue();

    public abstract String getTemplate();

    public abstract String getIcon();

    public abstract PolicyWizardStep getGotoStep();

    public int getAdvancedTabIndex() {
        return 0;
    }

    public void panelExpandListener(ActionEvent event) {
        Effect e;
        if (isPanelExpanded()) {
            e = new SlideUp();
        } else {
            e = new SlideDown();
        }
        e.setSubmit(true);
        e.setTransitory(false);
        setPanelExpandEffect(e);
    }

    public void panelRemoveListener(ActionEvent event) {
        // nothing
    }

    public Effect getPanelExpandEffect() {
        return panelExpandEffect;
    }

    public void setPanelExpandEffect(Effect panelExpandEffect) {
        this.panelExpandEffect = panelExpandEffect;
    }

    public Effect getPanelEffect() {
        return panelEffect;
    }

    public void setPanelEffect(Effect panelEffect) {
        this.panelEffect = panelEffect;
    }

    public boolean isPanelExpanded() {
        return panelExpanded;
    }

    public void setPanelExpanded(boolean panelExpanded) {
        this.panelExpanded = panelExpanded;
    }

    public boolean isPanelVisible() {
        return panelVisible;
    }

    public void setPanelVisible(boolean panelVisible) {
        this.panelVisible = panelVisible;
    }

    public PolicyWizardBean getPolicyWizardBean() {
        return policyWizardBean;
    }

    protected int getGotoStep(ActionEvent event) {
        Object o = event.getComponent().getAttributes().get("gotoStep");
        PolicyWizardStep pws = (PolicyWizardStep)o;
        int step = pws.toInt();

        return step;
    }

    public void editListener(ActionEvent event) {
        int gotoStep = getGotoStep(event);

        getPolicyWizardBean().gotoStep(gotoStep);
    }
}

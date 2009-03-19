package com.sun.identity.admin.handler;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Fade;
import com.icesoft.faces.context.effects.SlideDown;
import com.icesoft.faces.context.effects.SlideUp;
import com.sun.identity.admin.model.MultiPanelBean;
import com.sun.identity.admin.model.ViewCondition;
import java.io.Serializable;
import java.util.List;
import javax.faces.event.ActionEvent;

public class ViewConditionsHandler implements MultiPanelHandler, Serializable {
    private List<ViewCondition> viewConditions;

    public void expandListener(ActionEvent event) {
        MultiPanelBean mpb = (MultiPanelBean) event.getComponent().getAttributes().get("bean");
        assert(mpb != null);

        Effect e;
        if (mpb.isExpanded()) {
            e = new SlideUp();
        } else {
            e = new SlideDown();
        }
        e.setSubmit(true);
        e.setTransitory(false);
        mpb.setExpandEffect(e);
    }

    public void removeListener(ActionEvent event) {
        MultiPanelBean mpb = (MultiPanelBean) event.getComponent().getAttributes().get("bean");
        assert(mpb != null);
        //viewConditions.remove(mpb);

        Effect e = new Fade();
        e.setSubmit(true);
        e.setTransitory(false);
        mpb.setPanelEffect(e);
    }

    public void setViewConditions(List<ViewCondition> viewConditions) {
        this.viewConditions = viewConditions;
    }

}

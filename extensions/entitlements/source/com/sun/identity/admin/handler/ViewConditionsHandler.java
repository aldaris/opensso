package com.sun.identity.admin.handler;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Fade;
import com.icesoft.faces.context.effects.SlideDown;
import com.icesoft.faces.context.effects.SlideUp;
import com.sun.identity.admin.model.MultiPanelBean;
import com.sun.identity.admin.model.NotViewCondition;
import com.sun.identity.admin.model.ViewCondition;
import java.io.Serializable;
import java.util.List;
import javax.faces.event.ActionEvent;

public class ViewConditionsHandler implements MultiPanelHandler, Serializable {

    private List<ViewCondition> viewConditions;

    public void expandListener(ActionEvent event) {
        MultiPanelBean mpb = (MultiPanelBean) event.getComponent().getAttributes().get("bean");
        assert (mpb != null);

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
        assert (mpb != null);

        Effect e;

        e = new Fade();
        e.setSubmit(true);
        e.setTransitory(false);
        mpb.setPanelEffect(e);

        ViewCondition vc = (ViewCondition) mpb;
        ViewCondition next = getNextCondition(vc);
        ViewCondition previous = getPreviousCondition(vc);

        if (next != null) {
            e = new Fade();
            e.setSubmit(true);
            e.setTransitory(false);
            next.setPanelEffect(e);
        }

        if (next instanceof NotViewCondition) {
            next = getNextCondition(next);
            if (next != null) {
                e = new Fade();
                e.setSubmit(true);
                e.setTransitory(false);
                next.setPanelEffect(e);
            }
        }

        if (previous instanceof NotViewCondition) {
            e = new Fade();
            e.setSubmit(true);
            e.setTransitory(false);
            previous.setPanelEffect(e);
        }
    }

    public void setViewConditions(List<ViewCondition> viewConditions) {
        this.viewConditions = viewConditions;
    }

    private ViewCondition getNextCondition(ViewCondition viewCondition) {
        int i = viewConditions.indexOf(viewCondition);
        assert (i != -1);

        for (int j = i + 1; j < viewConditions.size(); j++) {
            if (viewConditions.get(j).isVisible()) {
                return viewConditions.get(j);
            }
        }

        return null;
    }

    private ViewCondition getPreviousCondition(ViewCondition viewCondition) {
        int i = viewConditions.indexOf(viewCondition);
        assert (i != -1);

        for (int j = i - 1; j >= 0; j--) {
            if (viewConditions.get(j).isVisible()) {
                return viewConditions.get(j);
            }
        }

        return null;
    }
}

package com.sun.identity.admin.handler;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.SlideDown;
import com.icesoft.faces.context.effects.SlideUp;
import com.sun.identity.admin.model.MultiPanelBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class BasicMultiPanelHandler implements Serializable {
    public void expandListener(ActionEvent event) {
        MultiPanelBean mpb = (MultiPanelBean) event.getComponent().getAttributes().get("bean");
        assert (mpb != null);

        Effect e;
        if (mpb.isPanelExpanded()) {
            e = new SlideUp();
        } else {
            e = new SlideDown();
        }
        e.setSubmit(true);
        e.setTransitory(false);
        mpb.setPanelExpandEffect(e);
    }

    public void removeListener(ActionEvent event) {
        // nothing
    }
}

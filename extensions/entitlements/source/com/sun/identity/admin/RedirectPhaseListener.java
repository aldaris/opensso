package com.sun.identity.admin;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

public class RedirectPhaseListener implements PhaseListener {

    public RedirectPhaseListener() {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void afterPhase(PhaseEvent phaseEvent) {
    }

    public void beforePhase(PhaseEvent phaseEvent) {
        FacesContext ctx = phaseEvent.getFacesContext();
        HttpServletRequest request =
                (HttpServletRequest) ctx.getExternalContext().getRequest();

        String viewId = request.getParameter("viewId");

        if (viewId != null) {
            UIViewRoot page = ctx.getApplication().getViewHandler().createView(ctx, viewId);
            ctx.setViewRoot(page);
            ctx.renderResponse();

        }
    }
}
package com.sun.identity.admin;

import com.sun.identity.admin.NavigationRules.NavigationCase;
import com.sun.identity.admin.NavigationRules.NavigationRule;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

public class ActionPhaseListener implements PhaseListener {

    public ActionPhaseListener() {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void beforePhase(PhaseEvent phaseEvent) {
    }

    public void afterPhase(PhaseEvent phaseEvent) {
        if (navigationRules == null) {
            navigationRules = new NavigationRules();
        }

        FacesContext ctx = phaseEvent.getFacesContext();
        HttpServletRequest request =
                (HttpServletRequest) ctx.getExternalContext().getRequest();

        String action = request.getParameter("jsf.action");

        if (action != null) {
            String currentViewId = ctx.getViewRoot().getViewId();
            NavigationRule nr = navigationRules.getNavigationRules().get(currentViewId);
            if (nr == null) {
                nr = navigationRules.getNavigationRules().get(null);
            }
            NavigationCase nc = nr.getNavigationCases().get(action);
            if (nc != null) {
                String newViewId = nc.getToViewId();
                UIViewRoot page = ctx.getApplication().getViewHandler().createView(ctx, newViewId);
                ctx.setViewRoot(page);
                ctx.renderResponse();
            }
        }
    }

    private NavigationRules navigationRules = null;
}
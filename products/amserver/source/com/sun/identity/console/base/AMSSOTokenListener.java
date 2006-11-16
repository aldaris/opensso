/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: AMSSOTokenListener.java,v 1.1 2006-11-16 04:31:07 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.common.RequestUtils;
import com.sun.identity.console.base.model.AMAdminConstants;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMSystemConfig;
import com.sun.identity.shared.Constants;
import java.io.IOException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

/**
 * This listener determines if user has authenticated or not.
 * If no, he/she will be redirected to login page.
 */
public class AMSSOTokenListener implements PhaseListener {
    /**
     * Redirects request to login page if user is not authenticated.
     *
     * @param event Phase Event.
     */
    public void afterPhase(PhaseEvent event) {
        FacesContext fc = event.getFacesContext();
        try {
            isUserAuthenticated(fc);
        } catch (SSOException e) {
            redirectToLoginPage(fc);
        }
    }

    public void beforePhase(PhaseEvent event) {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    private void redirectToLoginPage(FacesContext fc) {
        try {
            ExternalContext ec = fc.getExternalContext();
            ec.redirect(getLoginURL(fc));
        } catch (IOException e) {
            AMModelBase.debug.error(
                "AMSSOTokenListener.redirectToLoginPage", e);
        }
    }

    private void isUserAuthenticated(FacesContext fc)
        throws SSOException
    {
        HttpServletRequest req = (HttpServletRequest)
            fc.getCurrentInstance().getExternalContext().getRequest();
        SSOTokenManager manager = SSOTokenManager.getInstance();
        manager.validateToken(manager.createSSOToken(req));
    }

    private String getLoginURL(FacesContext fc) {
        StringBuffer redirectURL = new StringBuffer(2048);
        HttpServletRequest req = (HttpServletRequest)
            fc.getCurrentInstance().getExternalContext().getRequest();
        String host = req.getHeader("Host");

        String loginURL = SystemProperties.get(Constants.LOGIN_URL);
        if ((loginURL != null) && (loginURL.trim().length() > 0)) {
            redirectURL.append(loginURL);
        } else {
            String protocol = RequestUtils.getRedirectProtocol(
                req.getScheme(), host);
            redirectURL.append(protocol)
                .append("://")
                .append(host)
                .append(AMSystemConfig.serverDeploymentURI)
                .append(AMAdminConstants.URL_LOGIN);
        }
        return redirectURL.toString();
    }
}

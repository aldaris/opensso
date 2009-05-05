package com.sun.identity.admin;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class Token {
    private HttpServletRequest request;

    public Token() {
        request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    public SSOToken getSSOToken() {
        try {
            SSOTokenManager manager = SSOTokenManager.getInstance();
            SSOToken ssoToken = manager.createSSOToken(request);
            manager.validateToken(ssoToken);
            return ssoToken;
        } catch (SSOException ssoe) {
            throw new RuntimeException(ssoe);
        }
    }

    public SSOToken getAdminSSOToken() {
        return (SSOToken) AccessController.doPrivileged(AdminTokenAction.getInstance());
    }

}

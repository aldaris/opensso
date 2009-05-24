package com.sun.identity.admin.model;

import com.iplanet.sso.SSOException;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.Token;
import com.sun.identity.common.DNUtils;
import com.sun.identity.console.base.model.AMAdminConstants;
import com.sun.identity.console.base.model.AMSystemConfig;
import java.io.Serializable;
import javax.faces.context.FacesContext;

public class StatusBean implements Serializable {
    public String getHelpLink() {
        FacesContext fc = FacesContext.getCurrentInstance();
        String vid = fc.getViewRoot().getViewId();
        Resources r = new Resources();
        String hl = r.getString(vid + ".help");
        if (hl == null) {
            hl = r.getString("_unknown.help");
        }

        return hl;
    }

    public String getLogoutLink() {
        String ll = AMSystemConfig.serverDeploymentURI + AMAdminConstants.URL_LOGOUT;
        return ll;
    }

    public String getShortUserName() {
        Token t = new Token();
        String un;
        try {
            String name = t.getSSOToken().getPrincipal().getName();
            un = DNUtils.DNtoName(name);
        } catch (SSOException ssoe) {
            // TODO: localize
            un = "unknown";
        }

        return un;
    }

    public String getServerName() {
        String sn = AMSystemConfig.serverHost;
        return sn;
    }

    public String getVersion() {
        return AMSystemConfig.version;
    }
}

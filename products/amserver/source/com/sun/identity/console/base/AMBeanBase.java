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
 * $Id: AMBeanBase.java,v 1.1 2006-11-16 04:31:06 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
 
package com.sun.identity.console.base;

import com.sun.identity.console.base.model.AMAdminConstants;
import com.sun.identity.console.base.model.AMCommonNameGenerator;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMSystemConfig;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Base JSF Backing Bean for Web Console.
 */
public class AMBeanBase {
    protected AMModel model;
    protected HttpServletRequest req;
    protected FacesContext fc;
    protected Locale locale;

    /**
     * Constructs an instance.
     */
    public AMBeanBase() {
        fc = FacesContext.getCurrentInstance();
        req = (HttpServletRequest)fc.getExternalContext().getRequest();
        model = createModel();
        locale = fc.getViewRoot().getLocale();
    }

    protected AMModel createModel() {
        return new AMModelBase(req);
    }

    /**
     * Returns resource bundle name. 
     *
     * @return resource bundle name.
     */
    public String getBundle() {
        return AMAdminConstants.DEFAULT_RESOURCE_BUNDLE;
    }

    /**
     * Returns user name. 
     *
     * @return user bundle name.
     */
    public String getUserName() {
        return AMCommonNameGenerator.getInstance().generateCommonName(
            model, locale);
    }

    /**
     * Returns host name. 
     *
     * @return host bundle name.
     */
    public String getHostname() {
        String host = AMSystemConfig.serverHost;
        int idx = host.indexOf('.');
        if (idx != -1) {
            host = host.substring(0, idx);
        }
        return host;
    }

    /**
     * Returns logout URL. 
     *
     * @return logout URL. 
     */
    public String getLogoutURL() {
        StringBuffer url = new StringBuffer(30);
        url.append("../../..")
           .append(AMSystemConfig.serverDeploymentURI)
           .append(AMAdminConstants.URL_LOGOUT);
        return url.toString();
    }
}

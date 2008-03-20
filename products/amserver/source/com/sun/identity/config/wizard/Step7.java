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
 * $Id: Step7.java,v 1.4 2008-03-20 20:50:21 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.config.util.AjaxPage;
import com.sun.identity.setup.SetupConstants;
import net.sf.click.Page;

/**
 * This is the summary page for the values entered during the configuration
 * process. No actual work is done here except setting the page elements.
 */
public class Step7 extends AjaxPage {

    public void onInit() {
        
        // Config Store Properties
        String tmp =(String)getContext().getSessionAttribute("configDirectory");
        add("configDirectory", tmp);        
        tmp = getAttribute("configStoreHost", getHostName());
        add("configStoreHost", tmp);
        tmp = getAttribute("rootSuffix", Wizard.defaultRootSuffix);
        add("rootSuffix", tmp);
        tmp = getAttribute("configStorePort", getAvailablePort(50389));
        add("configStorePort", tmp);
        tmp = getAttribute("configStoreLoginId", Wizard.defaultUserName);
        add("configStoreLoginId", tmp);

        // User Config Store Properties
        tmp = (String)getContext().getSessionAttribute(SetupConstants.USER_STORE_HOST);
        add("userHostName", tmp);
        tmp = (String)getContext().getSessionAttribute(SetupConstants.USER_STORE_PORT);
        add("userHostPort", tmp);
        tmp = (String)getContext().getSessionAttribute(SetupConstants.USER_STORE_ROOT_SUFFIX);        
        add("userRootSuffix", tmp);
        
        // Load Balancer Properties
        add("loadBalancerHost", 
            (String)getContext().getSessionAttribute(SetupConstants.LB_SITE_NAME));
        add("loadBalancerPort", 
            (String)getContext().getSessionAttribute(SetupConstants.LB_PRIMARY_URL));

        super.onInit();
    }

    protected void add(String key, Object value) {
        if (value != null) {
            addModel(key, value);
        }
    }
}

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
 * $Id: Step6.java,v 1.4 2008-01-18 06:23:40 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.config.pojos.LDAPStore;
import com.sun.identity.config.util.AjaxPage;
import com.sun.identity.setup.AMSetupServlet;
import com.sun.identity.setup.SetupConstants;
import net.sf.click.control.ActionLink;

public class Step6 extends AjaxPage {

    public ActionLink validateInputLink = 
        new ActionLink("validateInput", this, "validateInput" );
    
    public Step6() {
    }
    
    public void onInit() {
        addModel("encryptionKey", AMSetupServlet.getRandomString());
        addModel("serverURL", getServerURL());
        addModel("cookieDomain", getCookieDomain());
        addModel("platformLocale", SetupConstants.DEFAULT_PLATFORM_LOCALE);
        addModel("configDirectory", getBaseDir());
    }   

    private String getServerURL() {        
        String hostname = (String)getContext().getRequest().getServerName();
        int portnum  = (int)getContext().getRequest().getServerPort();
        String protocol = (String)getContext().getRequest().getScheme();
        return protocol + "://" + hostname + ":" + portnum;
    }

    public boolean validateInput() {
        String key = toString("key");
        String value = toString("value");
 
        if (value == null) {        
            writeToResponse(getLocalizedString("missing.required.field"));
        } else { 
            getContext().setSessionAttribute(key, value);
            writeToResponse("OK");                               
        }
        setPath(null);
        return false;
    }
}

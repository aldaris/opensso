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
 * $Id: Step2.java,v 1.5 2008-01-24 20:26:40 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.ActionLink;

public class Step2 extends LDAPStoreWizardPage {

    public static final String LDAP_STORE_SESSION_KEY = "wizardCustomConfigStore";

    public ActionLink clearLink = new ActionLink("clear", this, "clear");
    public ActionLink validateConfigBaseDirLink = 
        new ActionLink("validateConfigBaseDir", this, "validateConfigBaseDir");
    
    public Step2() {
        setType("config");
        setTypeTitle( "Configuration" );
        setPageNum(2);
        setStoreSessionName(LDAP_STORE_SESSION_KEY);
    }
    
    public void onInit() {
        addModel("configBaseDir", getBaseDir());
        addModel("configStoreBaseDN", "dc=opensso,dc=java,dc=net");
        addModel("configStoreLoginId", "cn=Directory Manager");
        super.onInit();
    }   
    
    public boolean clear() {
        getContext().removeSessionAttribute(LDAP_STORE_SESSION_KEY);
        setPath( null );
        return false;
    }

    public boolean validateConfigBaseDir() {
        // verify base directory
        String path = toString("configBaseDir");
        if (path == null) {
            writeToResponse(getLocalizedString("missing.required.field"));            
        } else {                   
            getContext().setSessionAttribute("ConfigBaseDir", path);
            writeToResponse("true");
        }
        setPath(null);        
        return false;
    }
}

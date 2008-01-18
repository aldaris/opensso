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
 * $Id: Step3.java,v 1.4 2008-01-18 06:23:40 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import net.sf.click.control.ActionLink;

/**
 * Step 3 is for selecting the embedded or external configuration store 
 */
public class Step3 extends LDAPStoreWizardPage {

    public static final String LDAP_STORE_SESSION_KEY = "wizardCustomConfigStore";
    public ActionLink validateRootSuffixLink = 
        new ActionLink("validateRootSuffix", this, "validateRootSuffix");
    
    public Step3() {
        setType("config");
        setTypeTitle("Configuration");
        setPageNum(3);
        setStoreSessionName( LDAP_STORE_SESSION_KEY );
    }
    
    public void onInit() {
        addModel("rootSuffix", "dc=opensso,dc=java,dc=net");
        super.onInit();        
    }       

    public boolean validateRootSuffix() {
        String rootsuffix = toString("rootSuffix");
        
        if (rootsuffix == null) {
            writeToResponse(getLocalizedString("missing.required.field"));
        } else {
            writeToResponse("true");
        }
        setPath(null);        
        return false;    
    }
}

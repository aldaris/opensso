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
 * $Id: Step3.java,v 1.5 2008-02-21 22:35:44 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.setup.AMSetupServlet;
import net.sf.click.control.ActionLink;
import com.sun.identity.setup.SetupConstants;

/**
 * Step 3 is for selecting the embedded or external configuration store 
 */
public class Step3 extends LDAPStoreWizardPage {

    public static final String LDAP_STORE_SESSION_KEY = "wizardCustomConfigStore";
    public ActionLink validateRootSuffixLink = 
        new ActionLink("validateRootSuffix", this, "validateRootSuffix");
    public ActionLink setReplicationLink = 
        new ActionLink("setReplication", this, "setReplication");
    public ActionLink setConfigType = 
        new ActionLink("setConfigType", this, "setConfigType");
    
    public Step3() {
        setType("config");
        setTypeTitle("Configuration");
        setPageNum(3);
        setStoreSessionName( LDAP_STORE_SESSION_KEY );
    }
    
    public void onInit() {
        String val = (String)getContext().getSessionAttribute("rootSuffix");
        if (val == null) {
            val = "dc=opensso,dc=java,dc=net";
        }
        addModel("rootSuffix", val);

        val = (String)getContext().getSessionAttribute("configStorePort");
        if (val == null) {
            val = Integer.toString(
                AMSetupServlet.getUnusedPort(getHostName(), 50389, 1000));
        }
        addModel("configStorePort", val);

        val = (String)getContext().getSessionAttribute("localRepPort");
        if (val == null) {
            val = Integer.toString(
                AMSetupServlet.getUnusedPort(getHostName(), 58989, 1000));
        }
        addModel("localRepPort", val);

        val = (String)getContext().getSessionAttribute("existingPort");
        if (val == null) {
            val = Integer.toString(
                AMSetupServlet.getUnusedPort(getHostName(), 50389, 1000));
        }
        addModel("existingPort", val);

        val = (String)getContext().getSessionAttribute("existingRepPort");
        if (val == null) {
            val = Integer.toString(
                AMSetupServlet.getUnusedPort(getHostName(), 58990, 1000));
        }
        addModel("existingRepPort", val);

        // initialize the data store type being used
        val = (String)getContext().getSessionAttribute(
            SetupConstants.CONFIG_VAR_DATA_STORE);
        if (val == null) {
            val = SetupConstants.SMS_EMBED_DATASTORE;
        }
        addModel(SetupConstants.CONFIG_VAR_DATA_STORE, val);

        val = (String)getContext().getSessionAttribute("configStoreHost");
        if (val == null) {
            val = "localhost";
        }
        addModel("configStoreHost", val);

        val = (String)getContext().getSessionAttribute("configStorePassword");
        if (val == null) {
            val = Wizard.defaultPassword;
        }
        addModel("configStorePassword", val);

        val = (String)getContext().getSessionAttribute("configStoreLoginId");
        if (val == null) {
            val = Wizard.defaultUserName;
        }
        addModel("configStoreLoginId", val);

        super.onInit();        
    }       

    public boolean setConfigType() {
        String type = toString("type");
        if (type.equals("remote")) {
            type = SetupConstants.SMS_DS_DATASTORE;
        } else {
            type = SetupConstants.SMS_EMBED_DATASTORE;
        } 
        getContext().setSessionAttribute( 
            SetupConstants.CONFIG_VAR_DATA_STORE, type);
        return true;
    }

    public boolean setReplication() {
        String type = toString("multi");
        if (type.equals("enable")) {
            type = SetupConstants.DS_EMP_REPL_FLAG_VAL;
        } 

        getContext().setSessionAttribute(
            SetupConstants.DS_EMB_REPL_FLAG, type);
        return true;
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

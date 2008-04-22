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
 * $Id: Step4.java,v 1.8 2008-04-22 20:56:25 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;
import net.sf.click.control.ActionLink;
import com.sun.identity.config.util.AjaxPage;
import com.sun.identity.setup.SetupConstants;
import net.sf.click.Context;
/**
 * Step 4 is the input of the remote user data store properties.
 */
public class Step4 extends AjaxPage {
    public static final String LDAP_STORE_SESSION_KEY = "wizardCustomUserStore";
    public ActionLink clearDataLink = 
        new ActionLink("clearData", this, "clearData");
    public ActionLink copyDataLink = 
        new ActionLink("copyData", this, "copyData");

    public ActionLink setHostLink = 
        new ActionLink("setHost", this, "setHost");
    public ActionLink setPortLink = 
        new ActionLink("setPort", this, "setPort");
    public ActionLink setRootSuffixLink = 
        new ActionLink("setRootSuffix", this, "setRootSuffix");
    public ActionLink setLoginIDLink = 
        new ActionLink("setLoginID", this, "setLoginID");
    public ActionLink setPasswordLink = 
        new ActionLink("setPassword", this, "setPassword");
    public ActionLink setStoreTypeLink = 
        new ActionLink("setStoreType", this, "setStoreType");    

    private String responseString = "ok";
    
    public Step4() {
    }
    
    public void onInit() {
        super.onInit();
        copyDataEx();
    }

    public boolean copyData() {
        copyDataEx();
        setPath(null);
        return false;
    }
    public void copyDataEx() {
        Context ctx = getContext();
        
        if (ctx.getSessionAttribute(SetupConstants.USER_STORE_HOST) == null) {
            ctx.setSessionAttribute(SetupConstants.USER_STORE_HOST,
                getAttribute("configStoreHost", getHostName()));
        }
        if (ctx.getSessionAttribute(SetupConstants.USER_STORE_PORT) == null) {
            ctx.setSessionAttribute(SetupConstants.USER_STORE_PORT,
                getAttribute("configStorePort", getAvailablePort(50389)));
        }
        if (ctx.getSessionAttribute(SetupConstants.USER_STORE_LOGIN_ID) == null)
        {
            ctx.setSessionAttribute(SetupConstants.USER_STORE_LOGIN_ID,
                getAttribute("configStoreLoginId", Wizard.defaultUserName));
        }
        if (ctx.getSessionAttribute(SetupConstants.USER_STORE_ROOT_SUFFIX) 
            == null) {
            ctx.setSessionAttribute(SetupConstants.USER_STORE_ROOT_SUFFIX,
                getAttribute("rootSuffix", Wizard.defaultRootSuffix));
        }
        if (ctx.getSessionAttribute(SetupConstants.USER_STORE_TYPE) == null) {
            ctx.setSessionAttribute(SetupConstants.USER_STORE_TYPE,
                "LDAPv3ForAMDS");
        }
    }
    
    /**
     * Remove all User Config Data Store Data from the session.
     * Invoked when Embedded option is selected in the User Configuration
     * Store Page.
     */
    public boolean clearData() {
        getContext().removeSessionAttribute(SetupConstants.USER_STORE_HOST);
        getContext().removeSessionAttribute(SetupConstants.USER_STORE_LOGIN_ID);     
        getContext().removeSessionAttribute(SetupConstants.USER_STORE_LOGIN_PWD);       
        getContext().removeSessionAttribute(SetupConstants.USER_STORE_PORT);
        getContext().removeSessionAttribute(SetupConstants.USER_STORE_ROOT_SUFFIX);
        getContext().removeSessionAttribute(SetupConstants.USER_STORE_TYPE);
            
        setPath(null);
        return false;
    }
    
    public boolean setAll() {     
        setPath(null);
        return false;
    }
    
    public boolean setHost() {
            String host = toString("host");
        if ((host != null) && host.length() > 0) {
            getContext().setSessionAttribute(
                SetupConstants.USER_STORE_HOST, host);
        } else {
            responseString = "missing.host.name";            
        }
        writeToResponse(getLocalizedString(responseString));
        setPath(null);
        return false;
    }
        
    public boolean setPort() {
        String port = toString("port");
        
        if ((port != null) && port.length() > 0) {
            int intValue = Integer.parseInt(port);
            if ((intValue > 0) && (intValue < 65535)) {
                getContext().setSessionAttribute(
                    SetupConstants.USER_STORE_PORT, port);
            } else {
                responseString = "invalid.port.number";
            }
        } else {
            responseString = "missing.host.port";            
        }
        writeToResponse(getLocalizedString(responseString));
        setPath(null);
        return false;
    }
    
    public boolean setLoginID() {
        String dn = toString("dn");
        if ((dn != null) && dn.length() > 0) {
            getContext().setSessionAttribute(
                SetupConstants.USER_STORE_LOGIN_ID, dn);
        } else {
            responseString = "missing.login.id";            
        }
        writeToResponse(getLocalizedString(responseString));
        setPath(null);
        return false;
    }
    
    public boolean setPassword() {
        String pwd = toString("password");
        if ((pwd != null) && pwd.length() > 0) {
            getContext().setSessionAttribute(
                SetupConstants.USER_STORE_LOGIN_PWD, pwd);
        } else {
            responseString = "missing.password";            
        }
        writeToResponse(getLocalizedString(responseString));
        setPath(null);
        return false;
    }
    
    public boolean setRootSuffix() {
        String rootsuffix = toString("rootsuffix");
        if ((rootsuffix != null) && rootsuffix.length() > 0) {
            getContext().setSessionAttribute(
                SetupConstants.USER_STORE_ROOT_SUFFIX, rootsuffix);
        } else {
            responseString = "missing.root.suffix";            
        }
        writeToResponse(getLocalizedString(responseString));
        setPath(null);
        return false;
    }
    
    public boolean setStoreType() {
        String type = toString("type");
        if ((type != null) && type.length() > 0) {
            getContext().setSessionAttribute(
                SetupConstants.USER_STORE_TYPE, type);
        } 
        writeToResponse(responseString);
        setPath(null);
        return false;
    }    
}

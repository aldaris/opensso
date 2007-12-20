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
 * $Id: Options.java,v 1.3 2007-12-20 23:27:00 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config;

import com.sun.identity.config.util.TemplatedPage;
import net.sf.click.control.ActionLink;
import com.iplanet.am.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class Options extends TemplatedPage {

    public ActionLink createConfigLink = new ActionLink("upgradeLink", this, "upgrade" );
    public ActionLink testUrlLink = new ActionLink("coexistLink", this, "coexist" );
    public ActionLink pushConfigLink = new ActionLink("olderUpgradeLink", this, "olderUpgrade" );

    protected boolean passwordUpdateRequired = true;
    protected boolean upgrade = false;
    
    private java.util.Locale configLocale = null;
    
    protected String getTitle() {
        return "configOptions.title";
    }

    public void doInit() {
        passwordUpdateRequired = getConfigurator().isPasswordUpdateRequired();
        addModel("passwordUpdateRequired", Boolean.valueOf( passwordUpdateRequired ) );

        upgrade = !getConfigurator().isNewInstall();
        addModel( "upgrade", Boolean.valueOf( upgrade ) );
    }

    protected void initializeResourceBundle() {
        HttpServletRequest req = (HttpServletRequest)getContext().getRequest();
	HttpServletResponse res = (HttpServletResponse)getContext().getResponse();

        setLocale(req);
        try {
            req.setCharacterEncoding("UTF-8");
	    res.setContentType("text/html; charset=UTF-8");
        } catch (UnsupportedEncodingException uee) {
            //Do nothing.
        }
    }
    
    private void setLocale (HttpServletRequest request) {      
        if (request != null) {
            String superLocale = request.getParameter("locale");

            if (superLocale != null && superLocale.length() > 0) {
		configLocale = new java.util.Locale(superLocale);
            } else {
		String acceptLangHeader =
                          (String)request.getHeader("Accept-Language");
		if ((acceptLangHeader !=  null) &&
                     (acceptLangHeader.length() > 0)) {
                    String acclocale = 
                        Locale.getLocaleStringFromAcceptLangHeader(
                            acceptLangHeader);
		    configLocale = new java.util.Locale(acclocale);
		}
            }
            try {
                rb = ResourceBundle.getBundle(RB_NAME, configLocale);
            } catch (MissingResourceException mre) {
                
            }
       }
    }
    
    public boolean upgrade() {
        try {
            getConfigurator().upgrade();
            writeToResponse("true");
        } catch ( Exception e ) {
            writeToResponse( e.getMessage());
        }
        setPath(null);
        return false;
    }

    public boolean coexist() {
        try {
            getConfigurator().coexist();
            writeToResponse("true");
        } catch ( Exception e ) {
            writeToResponse(e.getMessage());
        }
        setPath(null);
        return false;
    }

    public boolean olderUpgrade() {
        try {
            getConfigurator().olderUpgrade();
            writeToResponse("true");
        } catch ( Exception e ) {
            writeToResponse(e.getMessage());
        }
        setPath(null);
        return false;
    }
}

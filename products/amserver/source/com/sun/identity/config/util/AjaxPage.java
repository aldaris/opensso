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
 * $Id: AjaxPage.java,v 1.10 2008-02-04 20:57:19 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.util;

import com.sun.identity.common.ISLocaleContext;
import com.sun.identity.config.Configurator;
import com.sun.identity.config.DummyConfigurator;
import com.sun.identity.setup.AMSetupServlet;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.locale.Locale;
import java.io.IOException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.click.Page;


public abstract class AjaxPage extends Page {

    public static final String RESPONSE_TEMPLATE = "{\"valid\":${valid}, \"body\":\"${body}\"}";
    public static final String OLD_RESPONSE_TEMPLATE = "{\"isValid\":${isValid}, \"errorMessage\":\"${errorMessage}\"}";
   
    private Configurator configurator = null;
    
    private boolean rendering = false;
    private String hostName;
    
    // localization properties
    protected ResourceBundle rb = null;
    protected static final String RB_NAME = "amConfigurator";
    
    public static Debug debug = Debug.getInstance("amConfigurator");
    
    public AjaxPage() {
    }

    public void onInit() {
        super.onInit();
        initializeResourceBundle();
        addModel("page", this);
    }

    public boolean isRendering() {
        return rendering;
    }

    protected Configurator getConfigurator() {
        if ( this.configurator == null ) {
            //TODO - retrieve Configuration instance from runtime environment
            //servlet context lookup?  JNDI?  Still awaiting word.  Use dummy for now:
            this.configurator = new DummyConfigurator( this );
        }
        return this.configurator;
    }

    protected String toString( String paramName ) {
        String value = getContext().getRequest().getParameter( paramName );
        value = ( value != null ? value.trim() : null );
        value = ("".equals(value) ? null : value );
        return value;
    }

    protected boolean toBoolean( String paramName ) {
        String value = toString( paramName );
        return ( value != null && value.toLowerCase().equals( "true" ) );
    }

    protected int toInt( String paramName ) {
        int intValue = 0;
        String value = toString( paramName );
        if ( value != null ) {
            try {
                intValue = Integer.parseInt( value );
            } catch ( NumberFormatException e ) {}
        }
        return intValue;
    }

    protected void writeValid() {
        writeValid( null );
    }
    protected void writeValid( String message ) {
        String out = ( message != null ? message : "" );
        writeJsonResponse(true, out);
    }
    protected void writeInvalid( String message ) {
        String out = ( message != null ? message : "" );
        writeJsonResponse( false, out );
    }

    protected void writeJsonResponse( boolean valid, String responseBody ) {
        String response = RESPONSE_TEMPLATE;
        response = response.replaceFirst("\\$\\{" + "valid" +  "\\}", String.valueOf(valid));
        response = response.replaceFirst("\\$\\{" + "body" +  "\\}", responseBody);
        writeToResponse(response);
    }

    protected void writeToResponse( String text ) {
        try {
            getContext().getResponse().getWriter().write( text );
            this.rendering = true;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    protected void writeToResponse(boolean isValid, String errorMessage) {
        String response = OLD_RESPONSE_TEMPLATE;
        response = response.replaceFirst("\\$\\{" + "isValid" +  "\\}", String.valueOf(isValid));
        response = response.replaceFirst("\\$\\{" + "errorMessage" +  "\\}", errorMessage);
        writeToResponse(response);
    }
    
    public void initializeResourceBundle() {
        HttpServletRequest req = 
            (HttpServletRequest)getContext().getRequest();
        HttpServletResponse res = 
            (HttpServletResponse)getContext().getResponse();

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
            java.util.Locale configLocale = null;
            if (superLocale != null && superLocale.length() > 0) {
                configLocale = new java.util.Locale(superLocale);
            } else {
                String acceptLangHeader =
                    (String)request.getHeader("Accept-Language");
                if ((acceptLangHeader !=  null) &&
                    (acceptLangHeader.length() > 0)) 
                {
                    String acclocale =
                        Locale.getLocaleStringFromAcceptLangHeader(
                            acceptLangHeader);
                    configLocale = new java.util.Locale(acclocale);
                }
            }
            try {
                rb = ResourceBundle.getBundle(RB_NAME, configLocale);
            } catch (MissingResourceException mre) {
                // do nothing
            }
       }
    }

    public String getLocalizedString(String i18nKey) {
        if (rb == null) {
            initializeResourceBundle();
        }
        
        String localizedValue = null;     
        try {
            localizedValue = Locale.getString(rb, i18nKey, debug);
        } catch (MissingResourceException mre) {
            // do nothing
        }
        return (localizedValue == null) ? i18nKey : localizedValue;
    }
    
    public String getHostName() { 
        if (hostName == null) {
            hostName = getContext().getRequest().getServerName();
        }
        return hostName;
    }
    
    public String getBaseDir() {
        String basedir = AMSetupServlet.getPresetConfigDir();
        if ((basedir == null) || (basedir.length() == 0)) {
            String tmp = System.getProperty("user.home");
            if (File.separatorChar == '\\') {
                tmp = tmp.replace('\\', '/');
            }
            basedir = tmp;
        } 

        if (basedir.endsWith("/")) {
            basedir = basedir + "opensso"; 
        } else {
            basedir = basedir + "/opensso";
        }

        return basedir;
    }
    
    public String getCookieDomain() {
        String cookieDomain = "";           
        String subDomain;
        String topLevelDomain;
        String hostname = getHostName();
        
        int idx1 = hostname.lastIndexOf(".");
        if ((idx1 != -1) && (idx1 != (hostname.length() -1))) {
            topLevelDomain = hostname.substring(idx1+1);
            int idx2 = hostname.lastIndexOf(".", idx1-1);
            if ((idx2 != -1) && (idx2 != (idx1 -1))) {
                subDomain = hostname.substring(idx2+1, idx1);
                try {
                    Integer.parseInt(topLevelDomain);  
                } catch (NumberFormatException e) {
                    try {
                        Integer.parseInt(subDomain);  
                    } catch (NumberFormatException e1) {
                        cookieDomain = "." + subDomain + "." + topLevelDomain;
                    }
                }
            }
        }
        
        return cookieDomain;
    }
}

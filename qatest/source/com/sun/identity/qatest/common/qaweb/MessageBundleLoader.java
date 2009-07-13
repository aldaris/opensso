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
 * $Id:
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.qaweb.common;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 * This class loads the property files in the resource bundle.
 *
 */
public class MessageBundleLoader {

    public static final String qawebproperties = "resources.qaweb";
    public static final String ServerProperties = "resources.server-icf";
    public static final String buildProperties = "resources.buildProp-icf";
    public static final String clientsamplesProperties =
            "resources.clientsamples-icf";
    public static final String logProperties = "resources.log-icf";
    public static final String saeProperties = "resources.sae-icf";
    public static final String authenticationProperties =
            "resources.authentication-icf";
    public static final String agentsproperties = "resources.agents-icf";
    public static final String cliproperties = "resources.cli-icf";

    // message bundle for component.
    private static ResourceBundle qawebMessages;
    private static ResourceBundle serverMessages;
    private static ResourceBundle buildMessages;
    private static ResourceBundle clientsamplesMessages;
    private static ResourceBundle logMessages;
    private static ResourceBundle saeMessages;
    private static ResourceBundle authenticationMessages;
    private static ResourceBundle agentsMessages;
    private static ResourceBundle cliMessages;

    /**
      * Initialization method.This method is called at the time of the start of
      * the application
      */
    private static void init() {
        Locale locale =
                FacesContext.getCurrentInstance().getViewRoot().getLocale();
        // assign a default locale if the faces context has none.
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        qawebMessages =  ResourceBundle.getBundle(qawebproperties, locale);
        serverMessages = ResourceBundle.getBundle(ServerProperties, locale);
        buildMessages = ResourceBundle.getBundle(buildProperties, locale);
        clientsamplesMessages = ResourceBundle.getBundle
                (clientsamplesProperties, locale);
        logMessages = ResourceBundle.getBundle(logProperties, locale);
        saeMessages = ResourceBundle.getBundle(saeProperties, locale);
        authenticationMessages = ResourceBundle.getBundle
                (authenticationProperties, locale);
        agentsMessages = ResourceBundle.getBundle(agentsproperties, locale);
        cliMessages = ResourceBundle.getBundle(cliproperties, locale);
    }

     /**
       * Returns value for the key present in qaweb.properties
       */
     public static String getQawebMessage(String key) {
         try {
            if (qawebMessages == null) {               
                init();
            }
            return qawebMessages.getString(key);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
     }

    /**
      * Returns value for the key present in server-icf.properties
      */
    public static String getServerMessage(String key) {
        try {
            if (serverMessages == null) {                
                init();
            }
            return serverMessages.getString(key);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
      * Returns value for the key present in buildProp-icf.properties
      */
    public static String getBuildMessage(String key) {
        try {
            if (buildMessages == null) {                
                init();
            }
            return buildMessages.getString(key);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
      * Returns value for the key present in Module specific property files
      */
    public static String getModuleMessage(String ModuleName, String key) {
        try {
            if (clientsamplesMessages == null || logMessages == null ||
                    saeMessages == null || authenticationMessages == null ||
                    agentsMessages == null || cliMessages == null) {
                init();
            }
            if (ModuleName.equals("clientsamples")) {
                return clientsamplesMessages.getString(key);

            } else if (ModuleName.equals("log")) {
                return logMessages.getString(key);

            } else if (ModuleName.equals("sae")) {
                return saeMessages.getString(key);

            } else if (ModuleName.equals("authentication")) {
                return authenticationMessages.getString(key);

            } else if (ModuleName.equals("agents")) {
                return agentsMessages.getString(key);

            } else if (ModuleName.equals("cli")) {
                return cliMessages.getString(key);
            }
            return null;
        } 
        catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}

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
 * $Id: SOAPBindingService.java,v 1.1 2006-10-30 23:15:22 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.liberty.ws.soapbinding; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import com.sun.identity.shared.locale.Locale;

import com.sun.identity.plugin.configuration.ConfigurationActionEvent;
import com.sun.identity.plugin.configuration.ConfigurationException;
import com.sun.identity.plugin.configuration.ConfigurationInstance;
import com.sun.identity.plugin.configuration.ConfigurationListener;
import com.sun.identity.plugin.configuration.ConfigurationManager;


/**
 * This class provides methods to retrieve SOAP configuration
 * from the data store. 
 */
public class SOAPBindingService implements ConfigurationListener {
    static final String SERVICE_NAME = "sunIdentityServerSOAPBinding";
    static final String REQUEST_HANDLER_LIST_ATTR = "RequestHandlerList";
    static final String WEB_SERVICE_AUTHENTICATOR_ATTR =
                            "WebServiceAuthenticator";
    static final String SUPPORTED_AUTHENTICATION_MECHANISMS_ATTR =
                            "SupportedAuthenticationMechanisms";

    static ConfigurationInstance ci = null;

    static HashMap supportedSOAPActionsMap = new HashMap();
    static HashMap handlers = new HashMap();
    static WebServiceAuthenticator wsAuthenticator = null;
    static Set supportedAuthMechs = null;

    static {
        try {
            ci = ConfigurationManager.getConfigurationInstance("SOAP_BINDING");
            ci.addListener(new SOAPBindingService());
            setValues();
        } catch (ConfigurationException ce) {
             Utils.debug.error("SOAPBindingService.static:", ce);
        }
    }

    /**
     * Default Constructor.
     */
    private SOAPBindingService() {
    }

    /**
     * Returns a web service authenticator.
     *
     * @return a WebServiceAuthenticator Object
     */
    static WebServiceAuthenticator getWebServiceAuthenticator() {
        return wsAuthenticator;
    }

    /**
     * Returns a set of supported authentication mechanisms.
     
     * @return a set of supported authentication mechanisms.
     */
    static Set getSupportedAuthenticationMechanisms() {
        return supportedAuthMechs;
    }

    /**
     * This method will be invoked when a component's 
     * configuration data has been changed. The parameters componentName,
     * realm and configName denotes the component name,
     * organization and configuration instance name that are changed 
     * respectively.
     *
     * @param e Configuration action event, like ADDED, DELETED, MODIFIED etc.
     */
    public void configChanged(ConfigurationActionEvent e) {
        if (Utils.debug.messageEnabled()) {
            Utils.debug.message("Utils.configChanged");
        }
  	setValues();
    }

    
    /**
     * This method reads values from service schema.
     */
    static private void setValues() {
        Map attrMap = null;
        try {
            attrMap = ci.getConfiguration(null, null);
        } catch (ConfigurationException ce) {
            Utils.debug.error("SOAPBindingService.setValues:", ce);
            return;
        }
        
        supportedSOAPActionsMap.clear();
        handlers.clear();
        Set values = (Set)attrMap.get(REQUEST_HANDLER_LIST_ATTR);
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            String value = (String)iter.next();
            StringTokenizer stz = new StringTokenizer(value, "|");
            String key = null;
            String class_ = null;
            String soapActions  = null;
            while(stz.hasMoreTokens()) {
                String token = stz.nextToken();
                if (token.startsWith("key=")) {
                    key = token.substring(4);
                } else if (token.startsWith("class=")) {
                    class_ = token.substring(6);
                } else if (token.startsWith("soapActions=")) {
                    soapActions = token.substring(12);
                }
            }
            if (key != null && class_ != null) {
                try {
                    handlers.put(key, Class.forName(class_).newInstance());
                    if (soapActions != null) {
                        StringTokenizer stz2 =new StringTokenizer(soapActions);
                        List list  = null;
                        while(stz2.hasMoreTokens()) {
                            if (list == null) {
                                list = new  ArrayList();
                            }
                            list.add(stz2.nextToken());
                        }
                        if (list != null) {
                            supportedSOAPActionsMap.put(key, list);
                        }
                    }
                } catch (Throwable t) {
                    Utils.debug.error("Utils.setValues class = " + class_, t);
                }
            } else {
                if (Utils.debug.warningEnabled()) {
                    Utils.debug.warning("Utils.setValues: Invalid syntax " +
                            "for Request Handler List: " +  value);
                }
            }
        }
        
        values = (Set)attrMap.get(WEB_SERVICE_AUTHENTICATOR_ATTR);
        if (values.isEmpty()) {
            wsAuthenticator = null;
        } else {
            String class_ = (String)values.iterator().next();
            try {
                wsAuthenticator =
                  (WebServiceAuthenticator)Class.forName(class_).newInstance();
            } catch (Exception ex) {
                if (Utils.debug.warningEnabled()) {
                    Utils.debug.warning("Utils.setValues: Unable to " +
                            "instantiate WebServiceAuthenticator", ex);
                }
                wsAuthenticator = null;
            }
        }
        
        supportedAuthMechs =
                (Set)attrMap.get(SUPPORTED_AUTHENTICATION_MECHANISMS_ATTR);
        
    }
}

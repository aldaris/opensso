/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: ConfigureGoogleApps.java,v 1.1 2009-02-24 18:40:41 babysunil Exp $
 *
 */

package com.sun.identity.workflow;

import com.iplanet.am.util.SystemProperties;
import com.sun.identity.cot.COTException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletContext;

/**
 *  * Creates Fedlet.
 *   */
public class ConfigureGoogleApps
    extends Task
{
    private static Map fedletBits = new HashMap();
    private static Map FedConfigTagSwap = new HashMap();
    private static List FedConfigTagSwapOrder = new ArrayList();
    private static Map jarExtracts = new HashMap();
    
    static {
        FedConfigTagSwap.put("@CONFIGURATION_PROVIDER_CLASS@", 
            "com.sun.identity.plugin.configuration.impl.FedletConfigurationImpl");
        FedConfigTagSwap.put("@DATASTORE_PROVIDER_CLASS@",
            "com.sun.identity.plugin.datastore.impl.FedletDataStoreProvider");
        FedConfigTagSwap.put("@LOG_PROVIDER_CLASS@",
            "com.sun.identity.plugin.log.impl.FedletLogger");
        FedConfigTagSwap.put("@SESSION_PROVIDER_CLASS@", 
            "com.sun.identity.plugin.session.impl.FedletSessionProvider");
        FedConfigTagSwap.put("@XML_SIGNATURE_PROVIDER@",
            "com.sun.identity.saml.xmlsig.AMSignatureProvider");
        FedConfigTagSwap.put("@XMLSIG_KEY_PROVIDER@", 
            "com.sun.identity.saml.xmlsig.JKSKeyProvider");
        FedConfigTagSwap.put("%BASE_DIR%%SERVER_URI%", "@FEDLET_HOME@");
        FedConfigTagSwap.put("%BASE_DIR%", "@FEDLET_HOME@"); 
        FedConfigTagSwap.put("com.sun.identity.common.serverMode=true",
            "com.sun.identity.common.serverMode=false");
        FedConfigTagSwap.put("@SERVER_PROTO@", "http");
        FedConfigTagSwap.put("@SERVER_HOST@", "example.identity.sun.com");
        FedConfigTagSwap.put("@SERVER_PORT@", "80");
        FedConfigTagSwap.put("/@SERVER_URI@", "/fedlet");
        
        FedConfigTagSwapOrder.add("@CONFIGURATION_PROVIDER_CLASS@"); 
        FedConfigTagSwapOrder.add("@DATASTORE_PROVIDER_CLASS@");
        FedConfigTagSwapOrder.add("@LOG_PROVIDER_CLASS@");
        FedConfigTagSwapOrder.add("@SESSION_PROVIDER_CLASS@"); 
        FedConfigTagSwapOrder.add("@XML_SIGNATURE_PROVIDER@");
        FedConfigTagSwapOrder.add("@XMLSIG_KEY_PROVIDER@");
        FedConfigTagSwapOrder.add("%BASE_DIR%%SERVER_URI%");
        FedConfigTagSwapOrder.add("%BASE_DIR%");
        FedConfigTagSwapOrder.add("com.sun.identity.common.serverMode=true");
        FedConfigTagSwapOrder.add("@SERVER_PROTO@");
        FedConfigTagSwapOrder.add("@SERVER_HOST@");
        FedConfigTagSwapOrder.add("@SERVER_PORT@");
        FedConfigTagSwapOrder.add("/@SERVER_URI@");
        
        ResourceBundle rb = ResourceBundle.getBundle("fedletBits");
        for (Enumeration e = rb.getKeys(); e.hasMoreElements(); ) {
            String k = (String)e.nextElement();
            fedletBits.put(k, rb.getObject(k));
        }
        
        rb = ResourceBundle.getBundle("fedletJarExtract");
        for (Enumeration e = rb.getKeys(); e.hasMoreElements(); ) {
            String jarName = (String)e.nextElement();
            String pkgNames = rb.getString(jarName);
            StringTokenizer st = new StringTokenizer(pkgNames, ",");
            Set set = new HashSet();
            while (st.hasMoreElements()) {
                set.add(st.nextToken().trim());
            }
            jarExtracts.put(jarName, set);
        }
        
    }
    
    public ConfigureGoogleApps() {
    }

    public String execute(Locale locale, Map params)
        throws WorkflowException {
        //validateParameters(params);
        String entityId = getString(params, ParameterKeys.P_ENTITY_ID);
        entityId = entityId.replaceAll("/", "%2F");
        Object[] param = {"test"};

        return MessageFormat.format(
            getMessage("GoogleApps.configured", locale), param);
    }
  
}


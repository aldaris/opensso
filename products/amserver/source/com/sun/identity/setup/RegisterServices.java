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
 * $Id: RegisterServices.java,v 1.3 2006-08-25 21:21:22 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.setup;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.shared.Constants;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Registers service during setup time.
 */
public class RegisterServices {
    
    private static final String DEFAULT_PLATFORM_LOCALE = "en_US";

    private static final String PROPERTY_FILENAME = "serviceNames";
    private static final String SERVICE_NAMES = "serviceNames";
    private static final List<String> serviceNames = new ArrayList<String>();

    static {
        ResourceBundle rb = ResourceBundle.getBundle(PROPERTY_FILENAME);
        String names = rb.getString(SERVICE_NAMES);
        StringTokenizer st = new StringTokenizer(names);
        while (st.hasMoreTokens()) {
            serviceNames.add(st.nextToken());
        }
    }

    /**
     * Registers services.
     *
     * @param adminToken Administrator Single Sign On token.
     * @throws IOException if file operation errors occur.
     * @throws SMSException if services cannot be registered.
     * @throws SSOException if single sign on token is not valid.
     */
    public void registers(SSOToken adminToken)
        throws IOException, SMSException, SSOException {
        System.setProperty(Constants.SYS_PROPERTY_INSTALL_TIME, "true");
        ServiceManager serviceManager = new ServiceManager(adminToken);

        for (String serviceFileName : serviceNames) {
            BufferedReader rawReader = null;
            InputStream serviceStream = null;
            
            try {
                rawReader = new BufferedReader(new InputStreamReader(
                     this.getClass().getClassLoader().getResourceAsStream(
                        serviceFileName)));
                StringBuffer buff = new StringBuffer();
                String line = null;
                
                while ((line = rawReader.readLine()) != null) {
                    buff.append(line);
                }
                
                rawReader.close();
                rawReader = null;
                String strXML = manipulateServiceXML(
                    serviceFileName, buff.toString());
                strXML = ServicesDefaultValues.tagSwap(strXML);
                serviceStream = (InputStream)new ByteArrayInputStream(
                    strXML.getBytes());
                serviceManager.registerServices(serviceStream);
                serviceStream.close();
                serviceStream = null;
            } finally {
                if (rawReader != null) {
                    rawReader.close();
                }
                if (serviceStream != null) {
                    serviceStream.close();
                }
                serviceManager.clearCache();
            }
            
        }
    }

    private String manipulateServiceXML(String serviceFileName, String strXML){
        if (serviceFileName.equals("idRepoService.xml")) {
            strXML = strXML.replaceAll(IDREPO_SUB_CONFIG_MARKER,
                IDREPO_SUB_CONFIG);
        }

        return strXML;
    }

    private static final String IDREPO_SUB_CONFIG_MARKER = "<SubConfiguration name=\"@IDREPO_DATABASE@\" id=\"@IDREPO_DATABASE@\" />";

    private static final String IDREPO_SUB_CONFIG = "<SubConfiguration name=\"files\" id=\"files\"><AttributeValuePair><Attribute name=\"sunIdRepoClass\" /><Value>com.sun.identity.idm.plugins.files.FilesRepo</Value></AttributeValuePair><AttributeValuePair><Attribute name=\"sunFilesIdRepoDirectory\" /><Value>@BASE_DIR@/@SERVER_URI@/idRepo</Value></AttributeValuePair></SubConfiguration>";
}

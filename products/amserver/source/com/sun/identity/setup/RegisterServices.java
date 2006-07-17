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
 * $Id: RegisterServices.java,v 1.1 2006-07-17 18:11:26 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.setup;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.common.Constants;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Registers service during setup time.
 */
public class RegisterServices {
    
    private static final String DEFAULT_PLATFORM_LOCALE = "en_US";

    private static final String[] SERVICES = {
        "amEntrySpecific.xml",
        "ums.xml",
        "amAuthConfig.xml",
        "amAuthHTTPBasic.xml",
        "amAdminConsole.xml",
        "idRepoService.xml",
        "amAuth.xml",
        "amAuthAD.xml",
        "amAuthAnonymous.xml",
        "amAuthCert.xml",
        "amAuthDataStore.xml",
        "amAuthJDBC.xml",
        "amAuthLDAP.xml",
        "amAuthMSISDN.xml",
        "amAuthMembership.xml",
        "amAuthNT.xml",
        "amAuthWindowsDesktopSSO.xml",
        "amClientData.xml",
        "amClientDetection.xml",
        "amDelegation.xml",
        "amG11NSettings.xml",
        "amLogging.xml",
        "amNaming.xml",
        "amPlatform.xml",
        "amPolicy.xml",
        "amPolicyConfig.xml",
        "amRealmService.xml",
        "amSession.xml",
        "amWebAgent.xml",
        "amUser.xml",
        "identityLocaleService.xml"
    };
    
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
        
        for (int i = 0; i < SERVICES.length; i++) {
            String serviceFileName = SERVICES[i];
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

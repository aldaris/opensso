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
 * $Id: SetupConfigurator.java,v 1.1 2005-11-01 00:28:36 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.util.Crypt;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.internal.AuthContext;
import com.sun.identity.authentication.internal.AuthPrincipal;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.ServiceManager;

public class SetupConfigurator {
    
    public static final String CONFIG_DEBUG_DIR =
        "com.iplanet.services.debug.directory";
    
    public static final String CONFIG_SMS_DIR =
        "com.sun.identity.sm.flatfile.root_dir";
    
    public static final String CONFIG_STATS_DIR =
        "com.iplanet.services.stats.directory";
    
    public static final String CONFIG_SERVERCONFIG_PATH =
        "com.iplanet.services.configpath";
    
    public static final String CONFIG_SUPER_USER =
        "com.sun.identity.authentication.super.user";
    
    public static final String CONFIG_SHARED_SECRET =
        "com.iplanet.am.service.secret";
    
    public static final String SERVERCONFIG_XML_FILENAME =
        "serverconfig.xml";
    
    public static final String[] SERVICES_TO_LOAD = new String [] {
        "amPlatform.xml", "amNaming.xml", "amSession.xml"
    };

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        log("Starting configuration setup");
        
        File ldapjdkJar = extractFile("ldapjdk.jar");
        if (ldapjdkJar == null) {
            throw new Exception("Failed to extract ldapjdk.jar");
        }
        
        File serverWar = extractFile("amserver.war");
        if (serverWar == null) {
            throw new Exception("Failed to extract amserver.war");
        }
        
        // Mark this file for automatic cleanup
        ldapjdkJar.deleteOnExit();
        
        // Bootstrap SystemProperties
        SystemProperties.initializeProperties(
                AdminTokenAction.AMADMIN_MODE, "true");
        
        // Create basic directory layout
        String debugDir = SystemProperties.get(CONFIG_DEBUG_DIR);
        String smsDir = SystemProperties.get(CONFIG_SMS_DIR);
        String statsDir = SystemProperties.get(CONFIG_STATS_DIR);
        String umsPath = SystemProperties.get(CONFIG_SERVERCONFIG_PATH);
        
        String umsDir = umsPath;
        if (umsPath.endsWith(SERVERCONFIG_XML_FILENAME)) {
            umsDir = umsPath.substring(0, SERVERCONFIG_XML_FILENAME.length()+1);
        }
        
        log("Creating debug directory: " + debugDir);
        createDir(debugDir);
        
        log("Creating sms directory: " + smsDir);
        createDir(smsDir);
        
        log("Creating stats directory: " + statsDir);
        createDir(statsDir);
        
        log("Creating serverconfig directory: " + umsDir);
        createDir(umsDir);
        
        // Create serverconfig.xml file on file system
        log("Transferring serverconfig.xml file to " + umsDir);
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                            ClassLoader.getSystemResourceAsStream(
                                    SERVERCONFIG_XML_FILENAME)));
            File configFile = new File(umsPath + File.separator 
                                    + SERVERCONFIG_XML_FILENAME);
            
            if (configFile.exists()) {
                log("WARNING: existing serverconfig.xml " +
                        "file will be overwritten");
            }
            
            writer = new PrintWriter(new FileOutputStream(configFile), true);
            
            String nextLine = null;
            while ((nextLine = reader.readLine()) != null) {
                writer.println(nextLine);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                    // No handling required
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    // No handling required
                }
            }
        }
        
        // Extracth ldapjdk file
        
        
        // Now register services
        SSOToken ssoToken = null;
        String superUser = SystemProperties.get(CONFIG_SUPER_USER);
        AuthPrincipal principal = new AuthPrincipal(superUser);
        
        String encPassword = SystemProperties.get(CONFIG_SHARED_SECRET);
        
        String passwordString = Crypt.decode(encPassword);
        
        char[] password = passwordString.toCharArray();
        AuthContext authContext = new AuthContext(principal, password);
        if (authContext.getLoginStatus() == AuthContext.AUTH_SUCCESS) {
            ssoToken = authContext.getSSOToken();
        }
        ServiceManager mgr = new ServiceManager(ssoToken);

        for (int i=0; i<SERVICES_TO_LOAD.length; i++) {
            String serviceFileName = SERVICES_TO_LOAD[i];
            log("Attempting to load: " + serviceFileName);
            InputStream serviceStream = null;
            try {
                serviceStream = 
                    ClassLoader.getSystemResourceAsStream(serviceFileName);
                
                if (serviceStream == null) {
                    throw new Exception("Faild to find " + serviceFileName);
                }
                                
                mgr.registerServices(serviceStream);
                
            } catch (Exception ex) {
                throw ex;
            } finally {
                if (serviceStream != null) {
                    try {
                        serviceStream.close();
                    } catch (Exception ex) {
                        // No handling requried
                    }
                }
            }
        }
        
        log("Configuration load complete.");
        log("You can now deploy amserver.war file created in this directory");
    }
    
    private static File extractFile(String name) throws Exception {
        File outfile = null;
        log("extracting file: " + name);
        InputStream instream = null;
        OutputStream outstream = null;
        try {
            instream = ClassLoader.getSystemResourceAsStream(name);
            if (instream == null) {
                throw new Exception("Failed to locate resource: " + name);
            }
            outfile = new File(name);
            if (outfile.exists()) {
                throw new Exception("File exists: " + outfile);
            }
            
            outstream = new FileOutputStream(outfile);
            byte[] buffer = new byte[1024];
            int bytesread = 0;
            int count = 0;
            while ((bytesread = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, bytesread);
                count++;
                System.out.print('.');
                if (count == 72) {
                    System.out.println();
                    count = 0;
                }
            }
            System.out.println();
            outstream.flush();
            outstream.close();
            instream.close();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (Exception ex) {
                    // No handling required
                }
            }
            if (outstream != null) {
                try {
                    outstream.close();
                } catch (Exception ex) {
                    // No handling required
                }
            }
        }
        
        return outfile;
    }
    
    
    private static void createDir(String path) throws Exception {
        File dir = new File(path);
        if (dir.exists()) {
            if(!dir.isDirectory()) {
                throw new Exception("Failed to create directory: " + dir
                        + ", file exists");
            }
            
            if (!dir.canWrite()) {
                throw new Exception(
                        "No write permissions for directory: " + dir);
            }
        } else {
            if (!dir.mkdirs()) {
                throw new Exception ("Failed to create directoyr: " + dir);
            } else {
                log ("Created directory: " + dir);
            }
        }
    }
    
    private static void log(String message) {
        System.out.println(message);
    }

}

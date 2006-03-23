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
 * $Id: SetupConfigurator.java,v 1.4 2006-03-23 19:10:08 veiming Exp $
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
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

import netscape.ldap.util.DN;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.util.Crypt;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.internal.AuthContext;
import com.sun.identity.authentication.internal.AuthPrincipal;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.ServiceManager;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;

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
    
    public static final String SERVER_HOST = "@SERVER_HOST@";
    public static final String SERVER_PORT = "@SERVER_PORT@";
    public static final String SERVER_PROTO = "@SERVER_PROTO@";
    public static final String CLIENT_HOST = "@CLIENT_HOST@";
    public static final String CLIENT_PORT = "@CLIENT_PORT@";
    public static final String CLIENT_PROTO = "@CLIENT_PROTO@";

    public static final String[] SERVICES_TO_LOAD = new String [] {
        "ums.xml", "amPlatform.xml", "amNaming.xml", "amSession.xml", 
        "amClientDetection.xml", "amAuthConfig.xml", 
        "amAuth.xml", "amAuthDataStore.xml",
        "idRepoService.xml"
    };

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 6) {
            log("Usage: java -jar amsetup.jar"+
                " <server_protocol> <server_host> <server_port>"+
                " <client_protocol> <client_host> <client_port>");
            System.exit(0);
        }
        String server_proto = args[0];
        String server_host = args[1];
        String server_port = args[2];
        String client_proto = args[3];
        String client_host = args[4];
        String client_port = args[5];
        
        log("Starting configuration setup");
        
        File ldapjdkJar = extractFile("ldapjdk.jar");
        if (ldapjdkJar == null) {
            throw new Exception("Failed to extract ldapjdk.jar");
        }
        
        File serverWar = extractFile("amserver.war");
        if (serverWar == null) {
            throw new Exception("Failed to extract amserver.war");
        }

        // Tag Swap host, port and proto in server AMConfig.properties
        String zipName = "amserver.war";
        String entryName = "WEB-INF/classes/AMConfig.properties";
        Map mapSwap = new HashMap();
        mapSwap.put(SERVER_PROTO, server_proto);
        mapSwap.put(SERVER_HOST, server_host);
        mapSwap.put(SERVER_PORT, server_port);
        mapSwap.put(CLIENT_PROTO, client_proto);
        mapSwap.put(CLIENT_HOST, client_host);
        mapSwap.put(CLIENT_PORT, client_port);
        tagSwap(mapSwap, entryName, zipName);       
        
        // Tag Swap host, port and proto in client AMConfig.properties
        zipName = "amdemoclient.war";
        entryName = "WEB-INF/classes/AMConfig.properties";
        tagSwap(mapSwap, entryName, zipName);

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
            InputStream servRawStream = null;
            try {
                servRawStream = 
                    ClassLoader.getSystemResourceAsStream(serviceFileName);
                byte [] buffer = new byte[1024];
                int bytesRead = 0;
                StringBuffer strBuff = new StringBuffer();
                while ((bytesRead = servRawStream.read(buffer)) != -1) {
                    strBuff.append(new String(buffer, 0, bytesRead));
                }
                
                searchReplace(strBuff,SERVER_HOST,server_host);
                searchReplace(strBuff,SERVER_PORT,server_port);
                searchReplace(strBuff,SERVER_PROTO,server_proto);
                if (serviceFileName.equals("amAuth.xml")) {
                    searchReplace(strBuff,CLIENT_HOST,client_host);
                    searchReplace(strBuff,CLIENT_PORT,client_port);
                    searchReplace(strBuff,CLIENT_PROTO,client_proto);
                }

                serviceStream = (InputStream)new java.io.ByteArrayInputStream
                    ((strBuff.toString()).getBytes());

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
                if (servRawStream != null) {
                    try {
                        servRawStream.close();
                    } catch (Exception ex) {
                        // No handling requried
                    }
                }
            }
        }
        
        // Set the organization alias & status
        try {
            mgr.clearCache();
            OrganizationConfigManager ocm = new OrganizationConfigManager(
                ssoToken, "/");
            HashSet values = new HashSet();
            values.add(server_host);
            // Get organization name
            String rootSuffix = SystemProperties.get("com.iplanet.am.rootsuffix");
            if (rootSuffix != null && DN.isDN(rootSuffix)) {
                DN rootDN = new DN(rootSuffix);
                String[] dns = rootDN.explodeDN(true);
                values.add(dns[0]);
            }
            HashMap attributes = new HashMap();
            attributes.put("sunOrganizationAliases", values);
            values = new HashSet();
            values.add("Active");
            attributes.put("sunOrganizationStatus", values);
            ocm.setAttributes("sunIdentityRepositoryService", attributes);
        } catch (SMSException e) {
            log("Unable to set organization aliases for login");
            log("Error: " + e.getMessage());
            throw (e);
        }

        log("Configuration load complete.");
        log("You can now deploy amserver.war file created in this directory");
    }

    /**
     * Swaps the tags of a resource in a <code>.war</code> file and 
     * re-generate the <code>.war</code> file.
     *
     * @param mapSwap Map of tag to value of tag.
     * @parma entryName Name of resource to be tag swapped.
     * @param jarFileName Name of <code>.war</code> file.
     */
    private static void tagSwap(
        Map mapSwap,
        String entryName,
        String jarFileName
    ) {
        JarOutputStream newZip = null;
        try {
            // Allocate a buffer for reading the entry data.
            byte [] buffer = new byte[1024];
            int bytesRead = 0;
            
            // Read the entry data and write it to the output file.
            log("Extract " + entryName + " From : " + jarFileName);

            String origFileName = jarFileName + ".tmp";
            File newFile = new File(jarFileName);
            newFile.renameTo(new File(origFileName));

            JarFile origJar = new JarFile(origFileName);
            newZip = new JarOutputStream(new FileOutputStream(jarFileName));
            JarEntry entry = new JarEntry(entryName);
            Enumeration enumeration1 = origJar.entries();
            while (enumeration1.hasMoreElements()) {
                JarEntry ent1 = (JarEntry)enumeration1.nextElement();
                InputStream stream1 = origJar.getInputStream(ent1);
                buffer = new byte[1024];
                bytesRead = 0;

                if (entry.getName().equals(ent1.getName())) {
                    newZip.putNextEntry(entry);
                    log("doing tag swap in : " + entryName);
                    StringBuffer sb = new StringBuffer();
                    while ((bytesRead = stream1.read(buffer)) != -1) {
                        sb.append(new String(buffer, 0, bytesRead));
                    }

                    for (Iterator iter = mapSwap.keySet().iterator();
                         iter.hasNext(); 
                    ) {
                        String key = (String)iter.next();
                        searchReplace(sb, key, (String)mapSwap.get(key)); 
                    }

                    newZip.write(sb.toString().getBytes(), 0, sb.length());
                } else {
                    newZip.putNextEntry(ent1);
                    // direct copy other files
                    while ((bytesRead = stream1.read(buffer)) != -1) {
                        newZip.write(buffer, 0, bytesRead);
                    }
                }
                stream1.close();
                System.gc();
            }
            newZip.close();
            newZip = null;
            System.gc();
            File oldFile = new File(origFileName);
            oldFile.delete();
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (newZip != null) {
                try {
                    newZip.close();
                } catch (IOException ioExp) {
                }
            }
        }
    }
    
    // Searches the 'fromString' in 'strBuff' and replaces the 'fromString'
    // with 'toString'.
    static void searchReplace(StringBuffer strBuff, String 
        fromString, String toString) {        
        int toStringLength = toString.length();
        int fromStringLength = fromString.length();
        int counter = 0;
        int found = 0;
        int prev_found = 0;
        while (counter + fromString.length() <= strBuff.length()) {
            prev_found = found;
            found = strBuff.indexOf(fromString, counter);
            if (found >= 0) {
                strBuff.replace(found, found + fromStringLength, toString);
                counter += (found - prev_found + toStringLength);
            } else {
                break;
            }
        } 
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

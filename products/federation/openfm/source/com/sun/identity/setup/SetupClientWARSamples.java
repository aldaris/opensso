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
 * $Id: SetupClientWARSamples.java,v 1.6 2008-05-28 19:54:39 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.setup;

import com.sun.identity.security.EncodeAction;
import com.iplanet.am.util.SystemProperties;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * This setup the FAM client WAR samples.
 */
public class SetupClientWARSamples {

    private static final String TAG_SERVER_PROTOCOL = "SERVER_PROTOCOL";
    private static final String TRUST_ALL_CERTS =
        "com.iplanet.am.jssproxy.trustAllServerCerts=true\n";

    ServletContext servletContext;

    /**
     * Constructor
     */
    public SetupClientWARSamples(ServletContext context) {
        servletContext = context; 
    }

    /**
     * Creates AMConfig.properties file
     * @param configFile Absolute path to the AMConfig.properties to be created.
     * @param templateFile Template file for AMConfig.properties
     * @param properties Properties to be swapped in the template
     */
    public void createAMConfigProperties(String configFile, 
        String templateFile, Properties properties) throws IOException {
        String content = getFileContent(templateFile);
        for (Iterator i = properties.keySet().iterator(); i.hasNext(); ) {
            String tag = (String)i.next();
            content = content.replaceAll("@" + tag + "@",
                (String)properties.get(tag));
        }

        String protocol = (String)properties.get(TAG_SERVER_PROTOCOL);
        if (protocol.equalsIgnoreCase("https")) {
            content += TRUST_ALL_CERTS;
        }

        // Setup default client keystore path.
        URL url = 
            servletContext.getResource("/WEB-INF/lib/openssoclientsdk.jar");
        if (url != null) {
            String keystoreLocation = (url.toString()).substring(5);
            int index = keystoreLocation.indexOf("WEB-INF");
            keystoreLocation = keystoreLocation.substring(0, index-1);
            content = 
                content.replaceAll("@" + "BASE_DIR" + "@", keystoreLocation);
        
        }
        
        BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
        out.write(content);
        out.close();
    }

    private String getFileContent(String fileName)
        throws IOException
    {
        InputStream in = servletContext.getResourceAsStream(fileName);
        if (in == null) {
            throw new IOException("Unable to open " + fileName);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer buff = new StringBuffer();
        String line = reader.readLine();

        while (line != null) {
            buff.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        return buff.toString();      
    }

    /**
     * Sets properties from AMConfig.properties
     * @param configFile path to the AMConfig.properties file
     * @throws ServletException when error occurs
     */
    public void setAMConfigProperties(String configFile) 
        throws ServletException {
        FileInputStream fileStr = null;
        try {
            fileStr = new FileInputStream(configFile);
            if (fileStr != null) {
                Properties props = new Properties();
                props.load(fileStr);
                SystemProperties.initializeProperties(props);
                ClientConfiguratorFilter.isConfigured = true;
            } else {
                throw new ServletException("Unable to open: " + configFile);
            }
        } catch (FileNotFoundException fexp) {
            fexp.printStackTrace();
            throw new ServletException(fexp.getMessage());
        } catch (IOException ioexp) {
            ioexp.printStackTrace();
            throw new ServletException(ioexp.getMessage());
        } finally {
            if (fileStr != null) {
                try {
                    fileStr.close();
                } catch (IOException ioe) {
                }
            } 
        }
    }
    
    /**
     * Create and copy the keystore file.
     *
     * @throws IOException if keystore file cannot be written.
     */
    private void createKeystoreFile() throws IOException
    {
        String location = 
        System.getProperty("user.home") + File.separator;
        InputStream in = servletContext.getResourceAsStream("/keystore.jks");
        byte[] b = new byte[2007];
        in.read(b);
        in.close();
        FileOutputStream fos = new FileOutputStream(location + "keystore.jks");
        fos.write(b);
        fos.flush();
        fos.close();
    }
}

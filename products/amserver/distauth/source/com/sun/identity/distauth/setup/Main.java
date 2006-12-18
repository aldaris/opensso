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
 * $Id: Main.java,v 1.1 2006-12-18 21:42:41 manish_rustagi Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.distauth.setup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This setup the DistAuth
 */
public class Main {
    private Map properties = new HashMap();
    private Map labels = new HashMap();

    private static final String TEMPLATE_AMCONFIG_PROPERTIES = 
        "resources/AMConfigTemplate.properties";
    private static final String FILE_AMCONFIG_PROPERTIES =
        "war/WEB-INF/classes/AMConfig.properties";
    private static final String CLASSES_AMCONFIG_PROPERTIES =
        "classes/AMConfig.properties";
    private static final String TAG_NAMING_URL = "NAMING_URL";
    private static final String TAG_SERVER_PROTOCOL = "SERVER_PROTOCOL";
    private static final String TAG_SERVER_HOST = "SERVER_HOST";
    private static final String TAG_SERVER_PORT = "SERVER_PORT";
    private static final String TAG_DEPLOY_URI = "DEPLOY_URI";
    //private static final String TAG_NOTIFICATION_URL = "NOTIFICATION_URL";
    //private static final String TAG_DISTAUTH_SERVER_PROTOCOL = "DISTAUTH_SERVER_PROTOCOL";
    //private static final String TAG_DISTAUTH_SERVER_HOST = "DISTAUTH_SERVER_HOST";
    //private static final String TAG_DISTAUTH_SERVER_PORT = "DISTAUTH_SERVER_PORT";
    //private static final String TAG_DISTAUTH_DEPLOY_URI = "DISTAUTH_DEPLOY_URI";

    private static final String TRUST_ALL_CERTS =
        "com.iplanet.am.jssproxy.trustAllServerCerts=true\n";

    private static List questions = new ArrayList();

    static {
        questions.add("DEBUG_DIR");
	questions.add("APPLICATION_USER");	
        questions.add("APPLICATION_PASSWD");
        questions.add("SERVER_PROTOCOL");
        questions.add("SERVER_HOST");
        questions.add("SERVER_PORT");
        questions.add("DEPLOY_URI");
        questions.add("NAMING_URL");
	questions.add("DISTAUTH_SERVER_PROTOCOL");
	questions.add("DISTAUTH_SERVER_HOST");
	questions.add("DISTAUTH_SERVER_PORT");
	questions.add("DISTAUTH_DEPLOY_URI");
	questions.add("NOTIFICATION_URL");
    }
    
    public Main()
        throws IOException, MissingResourceException
    {
        getDefaultValues();
        promptForAnswers();
        createPropertiesFile();
    }

    private void getDefaultValues() 
        throws MissingResourceException
    {
        ResourceBundle rb = ResourceBundle.getBundle("configDefault");
        for (Enumeration e = rb.getKeys(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            String value = (String)rb.getString(key);

            if (value.startsWith("[")) {
                labels.put(key, value.substring(1, value.length() -1));
            } else {
                properties.put(key, value);
            }
        }
    }

    private void promptForAnswers()
        throws IOException
    {
        for (Iterator i = questions.iterator(); i.hasNext(); ) {
            String q = (String)i.next();
            String value = "";
            while (value.length() == 0) {
                String defaultValue = null;
                if (q.equals(TAG_NAMING_URL)) {
                    defaultValue = properties.get(TAG_SERVER_PROTOCOL) + "://" +
                        properties.get(TAG_SERVER_HOST) + ":" + 
                        properties.get(TAG_SERVER_PORT) + "/" +
                        properties.get(TAG_DEPLOY_URI) + "/namingservice";
                }

                String label = (String)labels.get(q);

                if (defaultValue != null) {
                    label += " (hit enter to accept default value, " + 
                        defaultValue + ")";
                }

                System.out.print(label + ": ");
                value = (new BufferedReader(
                    new InputStreamReader(System.in))).readLine();
                value = value.trim();

                if ((value.length() == 0) && (defaultValue != null)) {
                    value = defaultValue;
                }
            }

            properties.put(q, value);
        }
    }

    private void createPropertiesFile()
        throws IOException
    {
        String content = getFileContent(TEMPLATE_AMCONFIG_PROPERTIES);
        for (Iterator i = properties.keySet().iterator(); i.hasNext(); ) {
            String tag = (String)i.next();
            content = content.replaceAll("@" + tag + "@", (String)properties.get(tag));
        }

        /*
        String protocol = properties.get(TAG_SERVER_PROTOCOL);
        if (protocol.equalsIgnoreCase("https")) {
            content += TRUST_ALL_CERTS;
        }
	*/

        BufferedWriter out = new BufferedWriter(new FileWriter(
            FILE_AMCONFIG_PROPERTIES));
        out.write(content);
        out.close();

        /*
        BufferedWriter fout = new BufferedWriter(new FileWriter(
            CLASSES_AMCONFIG_PROPERTIES));
        fout.write(content);
        fout.close();
        */
    }

    private String getFileContent(String fileName)
        throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuffer buff = new StringBuffer();
        String line = reader.readLine();

        while (line != null) {
            buff.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        return buff.toString();      
    }
    
    public static void main(String args[]) {
        try {
            Main main = new Main();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
    }
}

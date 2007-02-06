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
 * $Id: ClientConfigCreator.java,v 1.1 2007-02-06 19:55:35 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.setup;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This setup the OpenSSO samples.
 */
public class ClientConfigCreator {
    private Map properties = new HashMap();
    private Map labels = new HashMap();

    private static final String FILE_CLIENT_PROPERTIES = 
        "resources/AMConfig.properties";
    
    public ClientConfigCreator(String pdtDir, String testDir)
        throws Exception {
        getDefaultValues(pdtDir, testDir);
    }

    private void getDefaultValues(String pdtDir, String testDir)
        throws Exception {
        PropertyResourceBundle server = new PropertyResourceBundle(
            new FileInputStream(pdtDir + "/AMConfig.properties"));
        ResourceBundle client = ResourceBundle.getBundle("AMClient");
        for (Enumeration e = client.getKeys(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            String value = (String)client.getString(key);

            if (value.equals("@COPY_FROM_CONFIG@")) {
                value = server.getString(key);
            }
            value = value.replace("@BASE_DIR@", testDir);

            properties.put(key, value);
        }
    }

    private void create()
        throws Exception
    {
        StringBuffer buff = new StringBuffer();
        for (Iterator i = properties.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            buff.append(entry.getKey())
                .append("=")
                .append(entry.getValue())
                .append("\n");
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(
            FILE_CLIENT_PROPERTIES));
        out.write(buff.toString());
        out.close();
    }

    private static String getAnswer(String question)
        throws IOException
    {
        System.out.print(question + ": ");
        String value = (new BufferedReader(
            new InputStreamReader(System.in))).readLine();
        return value.trim();
    }

    public static void main(String args[]) {
        try {
            ClientConfigCreator creator = new ClientConfigCreator(
                args[0], args[1]);
            creator.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

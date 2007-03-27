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
 * $Id: RemoveDuplicateConfig.java,v 1.1 2007-03-27 06:03:34 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.setup;

import com.sun.identity.common.SystemConfigurationUtil;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

/**
 * Remove duplicate property from one configuration properties file
 * comparing it against another one (base properties file).
 */
public class RemoveDuplicateConfig {
    
    private RemoveDuplicateConfig() {
    }
    
    public static void main(String[] args) {
        try {
            Map map = getPropertyMap(args[0]);
            /*
             * in federation, we have com.sun.identity.common.serverMode
             * in amserver, we have com.iplanet.am.serverMode
             * they are referring to the same thing. Hence we need to
             * remove com.sun.identity.common.serverMode.
             */
            map.put(SystemConfigurationUtil.PROP_SERVER_MODE, "true"); 

            String result = removeDuplicates(args[1], map);
            writeToFile(result, args[2]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    private static String removeDuplicates(String filename, Map map)
        throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        StringBuffer commentBlock = new StringBuffer();
        StringBuffer buff = new StringBuffer();
        
        while (line != null) {
            line = line.trim();
            if (line.startsWith("#")) { // copyrights - keep
                buff.append(line).append("\n");
            } else if (line.length() == 0) {
                //spacer - discard
            } else if (line.startsWith("/*")) { //start comment - track
                // write out previous comments
                if (commentBlock.length() > 0) {
                    buff.append("\n").append(commentBlock.toString());
                    commentBlock = new StringBuffer();
                }
                commentBlock.append(line).append("\n");
            } else if (line.startsWith("*")) { //comment - track
                commentBlock.append(" ").append(line).append("\n");
            } else if (line.endsWith("*/")) { //end comment - track
                commentBlock.append(" ").append(line).append("\n");
            } else {
                int idx = line.indexOf('=');
                if (idx == -1) {
                    buff.append(line).append("\n");
                } else {
                    String key = line.substring(0, idx);
                    if (!map.containsKey(key)) {
                        if (commentBlock.length() > 0) {
                            buff.append("\n").append(commentBlock.toString());
                            commentBlock = new StringBuffer();
                        }
                        buff.append(line).append("\n");
                    } else if (commentBlock.length() > 0) {
                        commentBlock = new StringBuffer();
                    }
                }
            }
            line = reader.readLine();
        }
        return buff.toString();
    }
    
    private static Map getPropertyMap(String filename)
        throws Exception {
        Map map = new HashMap();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            PropertyResourceBundle bundle = new PropertyResourceBundle(fis);
            for (Enumeration e = bundle.getKeys(); e.hasMoreElements(); ) {
                String key = (String)e.nextElement();
                map.put(key, bundle.getString(key));
            }
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
        return map;
    }

    private static void writeToFile(String content, String filename)
        throws FileNotFoundException, IOException
    {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(filename);
            fout.write(content.getBytes());
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }
}


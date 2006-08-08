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
 * $Id: MergeProperties.java,v 1.1 2006-08-08 01:06:11 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.setup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Merge two properties file into one.
 */
public class MergeProperties {
    private MergeProperties() {
    }

    private MergeProperties(String origProp, String prependProp, String outfile)
        throws IOException
    {
        StringBuffer buff = new StringBuffer();
        Map<String, String> p1 = getProperties(origProp);
        Map<String, String> p2 = getProperties(prependProp);
        Set<String> p1Keys = p1.keySet();
        Set<String> p2Keys = p2.keySet();

        for (String key : p1Keys) {
            String val = p1.get(key);
            if (p2Keys.contains(key)) {
                val += " " + p2.get(key);
            }
            buff.append(key)
                .append("=")
                .append(val);
        }

        for (String key : p2Keys) {
            String val = p2.get(key);
            if (!p1Keys.contains(key)) {
                buff.append(key)
                    .append("=")
                    .append(val);
            }
        }

        writeToFile(outfile, buff.toString());
    }

    private Map<String, String> getProperties(String propertyName) {
        Map<String, String> results = new HashMap<String, String>();
        ResourceBundle res = ResourceBundle.getBundle(propertyName);
        for (Enumeration<String> e = res.getKeys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            results.put(key, res.getString(key));
        }
        return results;
    }

    private void writeToFile(String filename, String content)
        throws IOException
    {
        if (filename != null) {
            File fileHandle = new File(filename);
            FileWriter out = null;
            try {
                out = new FileWriter(filename);
                out.write(content);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    /**
     * Merges two properties files into one. The first argument is
     * the original properties file; the second is the one to prepend;
     * and the third is the output filename.
     * <p>
     * E.g.
     * <code>key1=x (from original properties file)</code> and
     * <code>key1=y (from the other properties file)</code> will result in
     * <code>key1=x y</code>.
     */
    public static void main(String[] args) {
        try {
            new MergeProperties(args[0], args[1], args[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

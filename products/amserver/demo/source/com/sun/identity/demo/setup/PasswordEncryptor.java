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
 * $Id: PasswordEncryptor.java,v 1.2 2006-04-26 18:41:58 bhavnab Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.setup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import com.iplanet.services.util.Crypt;

public class PasswordEncryptor {
    
    public static final String PROPERTY_CLEARTEXT_PASSWORD = "ADMIN_PASSWD";
    public static final String PROPERTY_ENC_KEY = "AM_ENC_KEY";
    public static final String PROPERTY_ENC_PASSOWRD = "ENCADMINPASSWD";
    public static final String SYSPROPERTY_ENC_KEY = "am.encryption.pwd";
    public static final String SYSPROPERTY_DEBUG_LEVEL =
        "com.iplanet.services.debug.level";
    public static final String SYSPROPERTY_DEBUG_DIR =
        "com.iplanet.services.debug.directory";

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Usage: " +
                    "java com.sun.identity.demo.setup.PasswordEncryptor " +
                    "<inputfile> <outputfile>");
        }
        
        File inputFile = new File(args[0]);
        if (!inputFile.exists() || !inputFile.canRead()) {
            throw new Exception("Invalid input file: " + args[0]);
        }
        
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(
                                new FileInputStream(inputFile));
            properties.load(inputStream);
        } catch (Exception ex) {
            throw new Exception("Failed to load input file: " + args[0], ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ex2) {
                    // No handling required
                }
            }            
        }
        
        String clearTextPassword = 
            properties.getProperty(PROPERTY_CLEARTEXT_PASSWORD);
        if (clearTextPassword == null || clearTextPassword.trim().length() == 0)
        {
            throw new Exception("Property not found: " 
                    + PROPERTY_CLEARTEXT_PASSWORD);
        }
        
        String encKey = properties.getProperty(PROPERTY_ENC_KEY);
        if (encKey == null || encKey.trim().length() == 0) {
            throw new Exception("Property not found: " + PROPERTY_ENC_KEY);
        }
        
        File outputFile = new File(args[1]);
        if (outputFile.exists()) {
            throw new Exception("Output file exists: " + args[1]);
        }
        
        System.setProperty(SYSPROPERTY_ENC_KEY, encKey);
        System.setProperty(SYSPROPERTY_DEBUG_LEVEL, "error");
        System.setProperty(SYSPROPERTY_DEBUG_DIR, 
                System.getProperty("java.io.tmpdir"));
        
        String encryptedPassword = Crypt.encrypt(clearTextPassword);
        
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(outputFile), true);
            writer.println(PROPERTY_ENC_PASSOWRD + "=" + encryptedPassword);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    // No handling required
                }
            }
        }
    }
}

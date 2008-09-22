/**
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
*
* Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
*
* The contents of this file are subject to the terms
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
* $Id: Patch.java,v 1.1 2008-09-22 20:49:27 kevinserwin Exp $
*/

package com.sun.identity.tools.patch;

import com.sun.identity.tools.manifest.Manifest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.Properties;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * The <code>Patch</code> class provides methods to parse the
 * commandline arguments, execute them accordingly for the 
 * command line tool - ssopatch.
 * This class generates manifest files for comparison
 * between different version of a given war file. 
 */

public class Patch implements PatchGeneratorConstants{
    String srcFilePath;
    String destFilePath;
    String src2FilePath;    
    Manifest man;
    Manifest man2;
    boolean createMode = false;    
    boolean compareMode = false;     
    Properties srcManifest;
    Properties src2Manifest;    
    char wildCard;
    Locale locale;
    ResourceBundle rbMessages;

    private static Locale getLocale(String strLocale) {
        StringTokenizer st = new StringTokenizer(strLocale, "_");
        String lang = (st.hasMoreTokens()) ? st.nextToken() : "";
        String country = (st.hasMoreTokens()) ? st.nextToken() : "";
        String variant = (st.hasMoreTokens()) ? st.nextToken() : "";
        return new Locale(lang, country, variant);
    }

    
    private void getProperties() {
        String propFilePath = System.getProperty(PROPERTIES_FILE);
        Properties sysProp = new Properties();
        if (propFilePath != null) {
            File propFile = new File(propFilePath);
            if (propFile.exists() && propFile.isFile()) {
                FileInputStream fin = null;
                try {
                    fin = new FileInputStream(propFile);
                    sysProp.load(fin);
                } catch (IOException ex){
                    System.out.println("Error occured when reading file "
                        + propFilePath);
                    System.exit(1);
                } finally {
                    try {
                        if (fin != null) {
                            fin.close();
                        }
                    } catch (IOException ex) {
                        // fin wasn't open
                    }
                }
            }
        }
        locale = getLocale(System.getProperty(OPTION_LOCALE,DEFAULT_LOCALE));
         
        if (locale == null) {
            locale = Locale.getDefault();
        }
        try {
            rbMessages = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);
        } catch (MissingResourceException e) {
            System.out.print("Resource file not found for locale: ");        
            System.out.print(System.getProperty(OPTION_LOCALE,DEFAULT_LOCALE));
            System.out.print("\n"); 
            System.exit(1);
        }
        
        srcFilePath = System.getProperty(SRC_FILE_PATH);
        destFilePath = System.getProperty(DEST_FILE_PATH);    
        src2FilePath = System.getProperty(SRC2_FILE_PATH);          
        
        wildCard = System.getProperty(WILDCARD_CHAR,
            sysProp.getProperty(WILDCARD_CHAR,DEFAULT_WILDCARD_CHAR)).charAt(0);
   
        if ((srcFilePath == null) || 
                ((destFilePath != null) && (src2FilePath != null))) {
            printUsage();
            System.exit(1);
        }              
        // if manifest file name passed in we are in create mode        
        if (destFilePath != null) {
            createMode = true;
        }  
        // if second source file name passed in we are in compare mode        
        if (src2FilePath != null) {
            compareMode = true;
        } else {
            src2FilePath = srcFilePath;
        }               
        man = new Manifest();        
        man.setDefaultProperties();
        
        man2 = new Manifest();        
        man2.setDefaultProperties();        
                        
    }
 
    
    private void compareManifest() {
        try {
            File srcFile = new File(srcFilePath);

            // Here we need to generate a manifest for the existing war file
            man.createManifest(srcFilePath, null, null, true, true);

            if (compareMode == false) {
                // we are not comparing two different war files
                // here we are comparing one warfile against its original
                // manifest to see what has changed
                JarFile src2War = new JarFile(srcFile);

                man2.setProperties(PatchGeneratorUtils.getManifest(src2War,
                        DEFAULT_MANIFEST_FILE, wildCard));
            } else {
                // we are going to compare two different war files
                // so generate the manifest for the second war file
                man2.createManifest(src2FilePath, null, null, true, true);
            }

            String idSrc = man.getProperty("identifier");
            String idSrc2 = man2.getProperty("identifier");
            int diffCount = 0;
            
            if ((idSrc != null) && (idSrc2 != null)) {
                System.out.print(rbMessages.getString("patch-compare"));
                System.out.print(srcFilePath);
                System.out.print(" (");
                System.out.print(idSrc);
                System.out.print(") ");
                System.out.print(rbMessages.getString("patch-against"));
                System.out.print(src2FilePath);
                System.out.print(" (");
                System.out.print(idSrc2);
                System.out.print(")\n");
            }
            // Now compare the src2 manifest against the source manifest
            // and report the differences
            for (Enumeration keysEnum = man2.getPropertyNames();
                    keysEnum.hasMoreElements();) {
                String key = (String) keysEnum.nextElement();
                String hashValue = man2.getProperty(key);
                String srcValue = man.getProperty(key);
                if (key.equals("identifier")) {
                    man.removeProperty(key);
                    continue;
                }
                if (srcValue == null) {
                    System.out.print(rbMessages.getString("patch-new"));
                    System.out.print(key);
                    System.out.print(")\n");
                    diffCount++;
                } else if (hashValue.equals(srcValue)) {
                    // remove equal keys from the list
                    man.removeProperty(key);
                } else {
                    // remove differences from the list
                    man.removeProperty(key);
                    if (!key.equals("META-INF/MANIFEST.MF")) {
                        System.out.print(rbMessages.getString("patch-modified"));
                        System.out.print(key);
                        System.out.print(")\n");
                        diffCount++;
                    }
                }
            }
            // At the end, the srcManifest will contain only those
            // items that were not found in the src2Manifest
            for (Enumeration keysEnum = man.getPropertyNames();
                    keysEnum.hasMoreElements();) {
                String key = (String) keysEnum.nextElement();
                String srcValue = man.getProperty(key);
                if ((key != null) && (!key.equals("META-INF/OpenSSO.manifest"))) {
                    System.out.print(rbMessages.getString("patch-missing"));
                    System.out.print(key);
                    System.out.print(")\n");
                    diffCount++;
                }
            }

            if (diffCount == 0) {
                System.out.println(rbMessages.getString("patch-identical"));
            } else {
                System.out.print(rbMessages.getString("patch-diff"));
                System.out.println(diffCount);
            }
        } catch (Exception ex) {
            System.out.println(rbMessages.getString("exception-read-error"));
            System.exit(1);
        }
    } 
   
    public void createManifest(String srcFilePath, String destFilePath) {
        man.createManifest(srcFilePath, destFilePath, null, true, true);
    }
    
    /* For manifest creation:
     *     -D"file.src.path=test/opensso.war" -D"file.dest.path=META-INF/OpenSSO_8_0.manifest"
     * 
     * For comparison to see what has changed
     *      -D"file.src.path=test/opensso.war"
     * 
     * For comparison of two versions of OpenSSO
     *     -D"file.src.path=test/opensso.war" -D"file.src2.path=test/new_opensso.war"
     */
    public static void main(String[] args) {
        Patch patch = new Patch();
        int diffCount = 0;

        patch.getProperties();
  
        // if the destination was passed in, then we are in create mode
        if (patch.createMode) {
            patch.createManifest(patch.srcFilePath, patch.destFilePath);
        } else {
            patch.compareManifest();
        }
        
    }
               
    
    /**
     * Prints  the usage for using the patch generation utility
     *
     */
    public void printUsage(){
        System.out.println(rbMessages.getString("usage"));
        System.out.println(rbMessages.getString("usage-arg"));
        System.out.println(rbMessages.getString("usage-src"));
        System.out.println(rbMessages.getString("usage-src-desc"));
        System.out.println(rbMessages.getString("usage-manifest"));
        System.out.println(rbMessages.getString("usage-manifest-desc"));        
        System.out.println(rbMessages.getString("usage-src2"));
        System.out.println(rbMessages.getString("usage-src2-desc"));      
    }
}

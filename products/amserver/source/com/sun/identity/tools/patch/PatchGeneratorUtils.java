/**
 * $Id: PatchGeneratorUtils.java,v 1.1 2008-09-04 16:44:17 kevinserwin Exp $
 * Copyright � 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to
 * technology embodied in the product that is described in this document.
 * In particular, and without limitation, these intellectual property rights
 * may include one or more of the U.S. patents listed at
 * http://www.sun.com/patents and one or more additional patents or pending
 * patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and applicable
 * provisions of the FAR and its supplements.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.Sun,
 * Sun Microsystems and  the Sun logo are trademarks or registered trademarks
 * of Sun Microsystems, Inc. in the U.S. and other countries.  
 *
 * Copyright � 2008 Sun Microsystems, Inc. Tous droits r�serv�s.
 * Sun Microsystems, Inc. d�tient les droits de propri�t� intellectuels relatifs
 * � la technologie incorpor�e dans le produit qui est d�crit dans ce document.
 * En particulier, et ce sans limitation, ces droits de propri�t�
 * intellectuelle peuvent inclure un ou plus des brevets am�ricains list�s
 * � l'adresse http://www.sun.com/patents et un ou les brevets suppl�mentaires
 * ou les applications de brevet en attente aux Etats - Unis et dans les
 * autres pays.
 *
 * L'utilisation est soumise aux termes du contrat de licence.
 *
 * Cette distribution peut comprendre des composants d�velopp�s par des
 * tierces parties.
 *
 * Sun,  Sun Microsystems et  le logo Sun sont des marques de fabrique ou des
 * marques d�pos�es de Sun Microsystems, Inc. aux Etats-Unis et dans
 * d'autres pays.
 */


package com.sun.identity.tools.patch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;


public class PatchGeneratorUtils {
    
    /**
     * Get a file object from the file system.
     *
     * @param path the path of the file
     * @return The file object the path indicate
     */
    
    public static File getFile(String path) throws FileNotFoundException{
        File file = new File(path);
        if (file.exists() && file.isFile()){
            return file;
        } else {
            throw new FileNotFoundException(path);
        }
    }
    
    /**
     * Load manifest information from a war file
     *
     * @param warFile the file object which contains the manifest information
     * @param manifestPattern the pattern of the name of the manifest file
     * @param wildCard a character which is used as a wild card in the pattern
     * @return a properties object contains the manifest information
     */
    
    public static Properties getManifest(JarFile warFile,
        String manifestPattern, char wildCard) throws Exception{
        Properties manifest = new Properties();
        for (Enumeration entries = warFile.entries();
            entries.hasMoreElements();) {
            JarEntry entry = (JarEntry)entries.nextElement();
            if ((!entry.isDirectory()) &&
                isMatch(entry.getName(), manifestPattern, wildCard)) {
                InputStream warIn = null;
                try {
                    warIn = warFile.getInputStream(entry); 
                    manifest.load(warIn);
                } finally {
                    try{
                        warIn.close();
                    } catch (Exception ignored) {}
                }
                break;
            }
        }
        return manifest;
    }
    
    /**
     * Load manifest information from a JarInputStream
     *
     * @param warFile the file stream which contains the manifest information
     * @param manifestPattern the pattern of the name of the manifest file
     * @param wildCard a character which is used as a wild card in the pattern
     * @return a properties object contains the manifest information
     */
    
    public static Properties getManifest(JarInputStream in,
        String manifestPattern, char wildCard) throws Exception {
        Properties manifest = new Properties();
        JarEntry entry = null;
        while((entry = in.getNextJarEntry()) != null) {
            if ((!entry.isDirectory()) &&
                isMatch(entry.getName(), manifestPattern, wildCard)) {
                try {
                    manifest.load(in);
                } finally {
                    try{
                        in.closeEntry();
                    } catch (Exception ignored) {}
                }
                break;
            }
        }
        return manifest;
    }
    
    /**
     * Get the input stream represented by entry name from a JarInputStream 
     *
     * @param jin the JarInputStream which contains the entry
     * @param entryName the pattern of the entry name
     * @return the input stream point to the jar entry indicated by entryName
     */
    
    public static JarInputStream getCorrectInputStream(JarInputStream jin,
        String entryName) throws Exception {
        if ((entryName == null) || (entryName.length() == 0)) {
            return jin;
        }
        JarEntry entry = null;
        while ((entry = jin.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {
                String tempName = entry.getName();
                if (entryName.equals(tempName)) {
                    return new JarInputStream(jin);
                } else {
                    if (entryName.startsWith(tempName)) {
                        return getCorrectInputStream(new JarInputStream(jin),
                            entryName.substring(tempName.length() + 1,
                            entryName.length()));
                    }
                }
            }            
        }
        return null;
    }
    
    /**
     * Check if the string matches the string pattern
     *
     * @param actualString the string is going to be checked
     * @param pattern the pattern of the string we are looking for
     * @param wildCard a character which is used as a wild card in the pattern
     * @return a boolean to indicate whether the string matches the pattern
     */
    
    public static boolean isMatch(String actualString, String pattern,
        char wildCard){
        String tempPattern = pattern.trim();
        int matchOffset = 0;
        boolean matched = true;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tempPattern.length(); i++) {
            if (tempPattern.charAt(i) != wildCard) {
                buffer.append(tempPattern.charAt(i));
            }
            if ((i == (tempPattern.length() - 1)) || (tempPattern.charAt(i)
                == wildCard)) {
                if (buffer.length() > 0) {
                    while(matchOffset < actualString.length()) {
                        int matchedIndex = actualString.indexOf(
                            buffer.toString(), matchOffset);
                        if (matchedIndex >= matchOffset) {
                            if (i != (tempPattern.length() - 1)) {
                                matchOffset = matchedIndex + buffer.length();
                            } else {
                                if (tempPattern.charAt(i) != wildCard) {
                                    if (actualString.substring(
                                        matchedIndex).length() !=
                                        buffer.length()) {
                                        matchOffset = matchedIndex + 1;
                                        continue;    
                                    }
                                }
                            }
                        } else {
                            matched = false;
                            break;
                        }
                        break;
                    }
                    buffer = new StringBuffer();
                }
            }
        }
        return matched;
    }
}

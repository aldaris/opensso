/**
 * $Id: FilesDigester.java,v 1.1 2008-08-08 22:36:23 kevinserwin Exp $
 * Copyright © 2008 Sun Microsystems, Inc.  All rights reserved.
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
 * Copyright © 2008 Sun Microsystems, Inc. Tous droits réservés.
 * Sun Microsystems, Inc. détient les droits de propriété intellectuels relatifs
 * à la technologie incorporée dans le produit qui est décrit dans ce document.
 * En particulier, et ce sans limitation, ces droits de propriété
 * intellectuelle peuvent inclure un ou plus des brevets américains listés
 * à l'adresse http://www.sun.com/patents et un ou les brevets supplémentaires
 * ou les applications de brevet en attente aux Etats - Unis et dans les
 * autres pays.
 *
 * L'utilisation est soumise aux termes du contrat de licence.
 *
 * Cette distribution peut comprendre des composants développés par des
 * tierces parties.
 *
 * Sun,  Sun Microsystems et  le logo Sun sont des marques de fabrique ou des
 * marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
 * d'autres pays.
 */

package com.sun.identity.tools.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.Properties;

public class FilesDigester implements ManifestConstants {
    
    protected LinkedList includePattern;
    protected LinkedList excludePattern;
    protected char wildCard;
    protected boolean recursive;
    
    /**
     * FileDigester constructor
     *
     * @param includePattern A list of patterns of file name should be included.
     * @param excludePattern A list of patterns of file name should be excluded.
     * @param wildCard The wildcard character which is used in the pattern.
     */
    
    public FilesDigester(LinkedList includePattern, LinkedList excludePattern,
        char wildCard, boolean recursive) {
        this.includePattern = includePattern;
        this.excludePattern = excludePattern;
        this.wildCard = wildCard;
        this.recursive = recursive;
    }
    
    /**
     * This function calculate the hash value of a war file.
     *
     * This function will calculate the manifest according to the decompressed
     * files contained in the war file.
     *
     * @param hashAlg The algorithm to be used for calculating the hash.
     * @param digestResult The Properties to store the results.
     * @param wfile The war file to be processed.
     * @param intoJar The flat to indicate whether to handle jar file by using
     *        its decompressed contents.
     */
    
    protected void digestWarFile(String hashAlg, Properties digestResult,
        JarFile wfile, boolean intoJar){
        Enumeration wEnum = wfile.entries();
        byte[] digestCode = null;
        String wename = null;
        InputStream in = null;
        try {            
            while (wEnum.hasMoreElements()) {
                JarEntry we = (JarEntry) wEnum.nextElement();
                if (!we.isDirectory()) {
                    wename=we.getName();
                    if (wename.endsWith(JAR_FILE_EXT) && (intoJar)) {
                        in=wfile.getInputStream(we);
                        digestCode = digestJarFile(hashAlg, in);
                        in.close();
                    } else{
                        in = wfile.getInputStream(we);
                        digestCode = Utils.getHash(hashAlg, in);
                        in.close();
                    }
                    appendResult(digestResult, wename, digestCode);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
                in = null;
            }
        }
    }
    
    /**
     * This function calculate the hash value of a jar file.
     *
     * This function handles the jar file as a concatenation of the decompressed
     * files it contains.
     *
     * @param hashAlg The algorithm to be used for calculating the hash.
     * @param in The InputStream of the jar file to be processed.
     */
    
    protected byte[] digestJarFile(String hashAlg, InputStream in){
        JarInputStream jin = null;
        try {
            jin=new JarInputStream(in);
            JarEntry je = null;
            MessageDigest md = MessageDigest.getInstance(hashAlg);
            while ((je=jin.getNextJarEntry()) != null) {
                if (!je.isDirectory()) {
                    md = Utils.hashing(md, jin);
                }
                jin.closeEntry();
            }
            jin.close();
            return md.digest();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } finally {
            if (jin != null) {
                try {
                    jin.close();
                } catch (IOException ignored) {
                }
                jin = null;
            }
        }
        return null;
    }
    
    /**
     * This function calculate the hash value of a file.
     *
     * @param hashAlg The algorithm to be used for calculating the hash.
     * @param file The file to be processed.
     * @param digestResult The Properties to store the results.
     * @param ignoredPath The parent's path to ignore when printing the 
     *        manifest entries.
     * @param intoJar The flag to indicate whether to specially handle 
     *        jar file.
     * @param intoWar The flag to indicate whether to specially handle 
     *        war file.
     */
    
    public void digest(String hashAlg, File file, Properties digestResult,
        String ignoredPath, boolean intoJar, boolean intoWar){
        if (file.exists()) {
            if (file.isDirectory()) {
                if (recursive) {
                    File[] tempFiles = null;
                    if (includePattern != null) {
                        tempFiles = file.listFiles(new GeneralFileFilter(
                            includePattern));
                    } else{
                        tempFiles = file.listFiles();
                    }
                    for (int i = 0; i < tempFiles.length; i++) {
                        if (tempFiles[i].isDirectory()) {
                            digest(hashAlg, tempFiles[i], digestResult,
                                ignoredPath, intoJar, intoWar);
                        } else{
                            if (excludePattern != null) {
                                if (!Utils.isMatch(tempFiles[i].getName(),
                                    excludePattern, wildCard)) {
                                    digest(hashAlg, tempFiles[i], digestResult,
                                        ignoredPath, intoJar, intoWar);
                                }
                            } else{
                                digest(hashAlg, tempFiles[i], digestResult, 
                                    ignoredPath, intoJar, intoWar);
                            }
                        }
                    }
                }
            } else{
                if (file.getName().endsWith(WAR_FILE_EXT) && (intoWar)) {
                    try {
                        digestWarFile(hashAlg, digestResult, new JarFile(file),
                            intoJar);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else{
                    byte[] digestedbyte = null;
                    if ((file.getName().endsWith(JAR_FILE_EXT)) && (intoJar)) {
                        FileInputStream fin = null;
                        try {
                            fin = new FileInputStream(file);
                            digestedbyte = digestJarFile(hashAlg, fin);
                            fin.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } finally {
                            if (fin != null) {
                                try {
                                    fin.close();
                                } catch (IOException ignored) {
                                }
                                fin = null;
                            }
                        }
                    } else{
                        FileInputStream fin = null;
                        try {
                            fin = new FileInputStream(file);
                            digestedbyte = Utils.getHash(hashAlg, fin);                        
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } finally {
                            if (fin != null) {
                                try {
                                    fin.close();
                                } catch (IOException ignored) {
                                }
                                fin = null;
                            }
                        }
                    }
                    String tempPath=file.getPath();
                    tempPath=tempPath.substring(tempPath.indexOf(ignoredPath) +
                        ignoredPath.length()).replaceAll("\\\\",
                        FILE_SEPARATOR);
                    if (tempPath.startsWith(FILE_SEPARATOR)) {
                        tempPath=tempPath.substring(1);
                    }
                    appendResult(digestResult, tempPath, digestedbyte);
                }    
            }
        }
    }
    
    /**
     * This function append the result to the StringBuffer.
     *
     * @param result The properties to store the results.
     * @param path The path of the entry.
     * @param digestedbyte The byte array which contains the digested result.
     */
    
    protected void appendResult(Properties result, String path,
        byte[] digestedbyte){
        result.setProperty(path, Utils.translateHashToString(digestedbyte));
    }
    
}

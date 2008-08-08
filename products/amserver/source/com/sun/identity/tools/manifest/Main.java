/**
 * $Id: Main.java,v 1.1 2008-08-08 22:36:23 kevinserwin Exp $
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

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Properties;

public class Main implements ManifestConstants{
    
    public static void main(String[] args) {
        String headerFilePath = System.getProperty(HEADER_FILE_PATH);
        String srcFilePath = System.getProperty(SRC_FILE_PATH);
        String destFilePath = System.getProperty(DEST_FILE_PATH);
        boolean recursive=Boolean.valueOf(System.getProperty(RECURSIVE,
            DEFAULT_RECURSIVE)).booleanValue();
        String includePatternString = System.getProperty(INCLUDE_PATTERN);
        String excludePatternString = System.getProperty(EXCLUDE_PATTERN);
        String manifestName = System.getProperty(MANIFEST_NAME,
            DEFAULT_MANIFEST_FILE_NAME);
        String wildCardString = System.getProperty(WILDCARD_CHAR);
        String digestAlg=System.getProperty(DIGEST_ALG, SHA1);
        boolean intoJar = Boolean.valueOf(System.getProperty(DIGEST_HANDLEJAR,
            DEFAULT_DIGEST_HANDLEJAR)).booleanValue();
        boolean intoWar = Boolean.valueOf(System.getProperty(DIGEST_HANDLEWAR,
            DEFAULT_DIGEST_HANDLEWAR)).booleanValue();
        boolean overwrite = Boolean.valueOf(System.getProperty(OVERWRITE,
            DEFAULT_OVERWRITE)).booleanValue();
        LinkedList includePattern = null;
        LinkedList excludePattern = null;
        if ((includePatternString != null) &&
            (includePatternString.length() > 0)) {
            includePattern = new LinkedList();
            int offset = 0;
            int index = includePatternString.indexOf(PATTERN_SEPARATOR, offset);
            do{
                if (index > offset) {
                    includePattern.add(includePatternString.substring(offset,
                        index).trim());
                } else{
                    if (offset < includePatternString.length()) {
                        String tempPattern = includePatternString.substring(
                            offset, includePatternString.length()).trim();
                        if (tempPattern.length() > 0) {
                            includePattern.add(tempPattern);
                        }
                    }
                    break;
                }
                offset = index + 1;
                index = includePatternString.indexOf(PATTERN_SEPARATOR, offset);
            } while (true);
        }
        if ((excludePatternString!=null) &&
            (excludePatternString.length() > 0)) {
            excludePattern = new LinkedList();
            int offset = 0;
            int index = excludePatternString.indexOf(PATTERN_SEPARATOR, offset);
            do{
                if (index > offset) {
                    excludePattern.add(excludePatternString.substring(offset,
                        index).trim());
                } else{
                    if (offset < excludePatternString.length()) {
                        String tempPattern = excludePatternString.substring(
                            offset, excludePatternString.length()).trim();
                        if (tempPattern.length() > 0) {
                            excludePattern.add(tempPattern);
                        }
                    }
                    break;
                }
                offset = index + 1;
                index = excludePatternString.indexOf(PATTERN_SEPARATOR, offset);
            } while (true);
        }
        
        
        char wildCard = DEFAULT_WILD_CARD;
        if (wildCardString != null) {
           wildCard = wildCardString.trim().charAt(0);
        }
        System.out.println(srcFilePath);

        File file = new File(srcFilePath);
        if (! file.exists()) {
            System.out.println("Source file not found!");
            System.exit(1);
        }
        FilesDigester digester = new FilesDigester(includePattern,
            excludePattern, wildCard, recursive);
        Properties digestResult = new Properties();
        digester.digest(digestAlg, file, digestResult, srcFilePath, intoJar,
            intoWar);
        if (destFilePath == null) {
            digestResult.list(System.out);
        } else{
            File destFile = new File(destFilePath);
            BufferedOutputStream fout = null;
            try {
                if (destFile.isDirectory()) {
                    fout = new BufferedOutputStream(new FileOutputStream(
                        new File(destFile, manifestName)));
                } else{
                    if (destFile.exists() && (!overwrite)) {
                        fout = new BufferedOutputStream(new FileOutputStream(
                            destFile, true));
                    } else{
                        File parentFile = destFile.getParentFile();
                        if ((parentFile != null) && (!parentFile.exists())) {
                            parentFile.mkdirs();
                        }
                        fout = new BufferedOutputStream(new FileOutputStream(
                            destFile));
                    }
                }
                if (headerFilePath != null) {
                    File headerFile = new File(headerFilePath);
                    if ((headerFile.exists()) && (headerFile.isFile())) {
                        BufferedReader fr = new BufferedReader(new
                            FileReader(headerFile));
                        String line;
                        while ((line = fr.readLine()) != null) {
                            fout.write(line.getBytes());
                            fout.write("\n".getBytes());
                        }
                        fr.close();
                    }
                }
                digestResult.store(fout, "");
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (fout != null) {
                    try {
                        fout.close();
                    } catch (IOException ignored) {
                    }
                    fout = null;
                }
            }
        }
    }
    
}

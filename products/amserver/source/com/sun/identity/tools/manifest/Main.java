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
* $Id: Main.java,v 1.2 2008-09-04 22:26:12 kevinserwin Exp $
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

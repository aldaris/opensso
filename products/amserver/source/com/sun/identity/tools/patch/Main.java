/**
 * $Id: Main.java,v 1.1 2008-09-04 16:44:17 kevinserwin Exp $
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.Properties;

/**
 * The <code>Main </code> class provides methods to parse the
 * commandline arguments, execute them accordingly for the 
 * command line tool - PatchGenerator.
 * This class generates a ZIP file containing the patch for a given 
 * release. The contents of the ZIP file are the delta between the 
 * different releases that are being compared.  
 *
 */


public class Main implements PatchGeneratorConstants{

    public static void main(String[] args) {
        String propFilePath = System.getProperty(PROPERTIES_FILE);
        Properties sysProp = new Properties();
        if (propFilePath != null) {
            File propFile = new File(propFilePath);
            if (propFile.exists() && propFile.isFile()) {
                FileInputStream fin = null;
                try {
                    fin = new FileInputStream(propFile);
                    sysProp.load(fin);
                } catch (Exception ex){
                    System.out.println("Error occurs when reading file "
                        + propFilePath);
                    System.exit(1);
                } finally {
                    try {
                        if (fin != null) {
                            fin.close();
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
        String origFilePath = System.getProperty(ORIGINAL_WAR_FILE,
            sysProp.getProperty(ORIGINAL_WAR_FILE));
        if (origFilePath == null) {
            printUsage(System.out);
            System.exit(1);
        }
        File origWarFile = null;
        try {
            origWarFile = PatchGeneratorUtils.getFile(origFilePath);
        } catch (Exception ex) {
            System.out.println("Error occurs when reading RTM zip file!");
            System.exit(1);
        }
        String origEntryPath = System.getProperty(ORIGINAL_WARFILE_ENTRY,
            sysProp.getProperty(ORIGINAL_WARFILE_ENTRY, DEFAULT_WARFILE_ENTRY));
        ArrayList origEntryList = new ArrayList();
        int offset = 0;
        int separatorIndex = 0;
        do {
            separatorIndex = origEntryPath.indexOf(ENTRY_SEPARATOR, offset);
            if (separatorIndex >= 0) {
                origEntryList.add(origEntryPath.substring(offset,
                    separatorIndex));
                offset = separatorIndex + 1;
            } else {
                origEntryList.add(origEntryPath.substring(offset,
                    origEntryPath.length()));
            }
        } while (separatorIndex >= 0);
        String latestFilePath = System.getProperty(LATEST_WAR_FILE,
            sysProp.getProperty(LATEST_WAR_FILE));
        if (latestFilePath == null) {
            printUsage(System.out);
            System.exit(1);
        }
        File latestWarFile = null;
        try {
            latestWarFile = PatchGeneratorUtils.getFile(latestFilePath);
        } catch (Exception ex) {
            System.out.println("Error occurs when reading latest zip file!");
            System.exit(1);
        }
        String latestEntryPath = System.getProperty(LATEST_WARFILE_ENTRY,
            sysProp.getProperty(LATEST_WARFILE_ENTRY, DEFAULT_WARFILE_ENTRY));
        ArrayList latestEntryList = new ArrayList();
        offset = 0;
        do {
            separatorIndex = latestEntryPath.indexOf(ENTRY_SEPARATOR, offset);
            if (separatorIndex >= 0) {
                latestEntryList.add(latestEntryPath.substring(offset,
                    separatorIndex));
                offset = separatorIndex + 1;
            } else {
                latestEntryList.add(latestEntryPath.substring(offset,
                    latestEntryPath.length()));
            }
        } while (separatorIndex >= 0);
        String sourcePatchFilePath = System.getProperty(SOURCE_PATCH_FILE,
            sysProp.getProperty(SOURCE_PATCH_FILE));
        File sourcePatchFile = null;
        if (sourcePatchFilePath != null) {
            try {
                sourcePatchFile = PatchGeneratorUtils.getFile(
                    sourcePatchFilePath);
            } catch (Exception ex) {
                System.out.println(
                    "Error occurs when reading previos patch file " +
                    sourcePatchFilePath);
                System.exit(1); 
            }
        }
        String destPatchFilePath = System.getProperty(DEST_PATCH_PATH,
            sysProp.getProperty(DEST_PATCH_PATH));
        String destPatchFileName = null;
        String manifestFileName = System.getProperty(MANIFEST_FILE_NAME,
            sysProp.getProperty(MANIFEST_FILE_NAME));
        if (manifestFileName == null) {
            String versionFileName = System.getProperty(VERSION_FILE,
                sysProp.getProperty(VERSION_FILE));
            if (versionFileName != null) {
                File versionFile = null;
                try {
                    versionFile = PatchGeneratorUtils.getFile(versionFileName);
                } catch (Exception ex) {
                    System.out.println("Error occurs when reading version file "
                        + versionFileName);
                }
                if (versionFile != null) {
                    Properties manifestFileNameProp = new Properties();
                    FileInputStream fin = null;
                    try {
                        fin = new FileInputStream(versionFile);
                        manifestFileNameProp.load(fin);
                        Enumeration keysEnum =
                            manifestFileNameProp.propertyNames();
                        manifestFileName = manifestFileNameProp.getProperty(
                            (String)keysEnum.nextElement());
                        manifestFileName = manifestFileName.substring(0,
                            manifestFileName.indexOf("("));
                        manifestFileName = manifestFileName.replaceAll("\"",
                            "");
                        manifestFileName =
                            manifestFileName.trim().replaceAll(" ", "");
                        destPatchFileName = "AccessManager_" + manifestFileName
                            + ".zip";
                        manifestFileName = "META-INF/AccessManager_" +
                            manifestFileName + ".manifest";
                    } catch (Exception ex) {
                        System.out.println("Error occurs when loading file " +
                            versionFileName);
                        System.exit(1);
                    } finally {
                        if (fin != null) {
                            try {
                                fin.close();
                            } catch (Exception ignored) {}
                        }
                    }
                } else {
                    manifestFileName = DEFAULT_MANIFEST_FILE_NAME;
                }
            } else {
                manifestFileName = DEFAULT_MANIFEST_FILE_NAME;
            }
        }
        String ignoredPath = null;
        int ignoredIndex = manifestFileName.lastIndexOf(ENTRY_FILE_SEPARATOR);
        if (ignoredIndex > 0) {
            ignoredPath = manifestFileName.substring(0, ignoredIndex) +
                ENTRY_FILE_SEPARATOR;
        }
        if (destPatchFileName == null) {
            destPatchFileName = System.getProperty(DEST_PATCH_FILE,
                sysProp.getProperty(DEST_PATCH_FILE));
            if (destPatchFileName == null) {
                printUsage(System.out);
                System.exit(1);
            }
        }
        String manifestPattern = System.getProperty(MANIFEST_PATTERN,
            sysProp.getProperty(MANIFEST_PATTERN, DEFAULT_MANIFEST_PATTERN));
        String identifierEntry = System.getProperty(IDENTIFIER_ENTRY,
            sysProp.getProperty(IDENTIFIER_ENTRY, DEFAULT_IDENTIFIER_ENTRY));
        char wildCard = System.getProperty(WILDCARD_CHAR,
            sysProp.getProperty(WILDCARD_CHAR, DEFAULT_WILDCARD_CHAR)).charAt(
            0);
        JarInputStream origWarIn = null;
        JarInputStream correctIn = null;
        Properties[] origManifest = new Properties[origEntryList.size()];
        JarInputStream latestWarIn = null;
        Properties[] latestManifest = new Properties[latestEntryList.size()];
        JarFile sourcePatch = null;
        Properties sourcePatchManifest = null;
        try {
            for (int i = 0; i < origEntryList.size(); i++) {
                try {
                    origWarIn = new JarInputStream(
                        new FileInputStream(origWarFile));
                    correctIn = PatchGeneratorUtils.getCorrectInputStream(
                        origWarIn, (String) origEntryList.get(i));
                    origManifest[i] = PatchGeneratorUtils.getManifest(correctIn,
                        manifestPattern, wildCard);
                } finally {
                    if (correctIn != null) {
                        try {
                            correctIn.close();
                        } catch (Exception ignored) {}
                    }
                    if (origWarIn != null) {
                        try {
                            origWarIn.close();
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error occurs when reading RTM war!");
            System.exit(1);
        }
        try {
            for (int i = 0; i < latestEntryList.size(); i++) {
                try {
                    latestWarIn = new JarInputStream(
                        new FileInputStream(latestWarFile));
                    correctIn = PatchGeneratorUtils.getCorrectInputStream(
                        latestWarIn, (String) latestEntryList.get(i));
                    latestManifest[i] = PatchGeneratorUtils.getManifest(
                        correctIn, manifestPattern, wildCard);
                } finally {
                    if (correctIn != null) {
                        try {
                            correctIn.close();
                        } catch (Exception ignored) {}
                    }
                    if (latestWarIn != null) {
                        try {
                            latestWarIn.close();
                        } catch (Exception ignored) {}
                    }
                }
            }
            if (sourcePatchFile != null){
                sourcePatch = new JarFile(sourcePatchFile);
                sourcePatchManifest = PatchGeneratorUtils.getManifest(
                    sourcePatch, manifestPattern, wildCard);
            }
        } catch (Exception ex) {
            System.out.println("Error occurs when reading latest war!");
            System.exit(1);
        }
        boolean allManifestFound = true;
        String[] latestIdentifierEntry = new String[latestEntryList.size()];
        for (int i = 0; i < latestEntryList.size(); i++) {
            if (latestManifest[i] != null) {
                latestIdentifierEntry[i] = latestManifest[i].getProperty(
                    identifierEntry);
                latestManifest[i].remove(identifierEntry);
            } else {
                allManifestFound = false;
                break;
            }
        }
        if (allManifestFound) {
            Properties[] addOrUpdateList = new Properties[
                latestEntryList.size()];
            Properties[] deleteList = new Properties[latestEntryList.size()];
            for (int i = 0; i < latestEntryList.size(); i++) {
                addOrUpdateList[i] = new Properties();
                deleteList[i] = new Properties();
            }
            if (sourcePatchManifest != null) {
                for (Enumeration keysEnum =
                    sourcePatchManifest.propertyNames();
                    keysEnum.hasMoreElements();) {
                    String key = (String) keysEnum.nextElement();
                    String hashValue = sourcePatchManifest.getProperty(key);
                    for (int i = 0; i < latestEntryList.size(); i++) {
                        String realKey = key.substring(0,
                            key.lastIndexOf(IDENTIFIER_SEPARATOR));
                        String latestHashValue =
                            latestManifest[i].getProperty(realKey);
                        if (key.endsWith(IDENTIFIER_SEPARATOR +
                            latestIdentifierEntry[i]) ||
                            key.endsWith(IDENTIFIER_SEPARATOR + ALL_SUFFIX)) {
                            if (latestHashValue != null) {
                                addOrUpdateList[i].setProperty(realKey,
                                    latestHashValue);
                            } else {
                                deleteList[i].setProperty(realKey, hashValue);
                            }
                        } else {
                            if (key.endsWith(IDENTIFIER_SEPARATOR +
                                DELETE_SUFFIX)) {
                                if (latestHashValue != null) {
                                    addOrUpdateList[i].setProperty(realKey,
                                        latestHashValue);
                                } else {
                                    HashSet versionSet = new HashSet();
                                    int versionOffset = 0;
                                    int versionIndex = 0;
                                    do {
                                        versionIndex = hashValue.indexOf(
                                            IDENTIFIER_SEPARATOR, versionOffset);
                                        if (versionIndex >= 0) {
                                            versionSet.add(hashValue.substring(
                                                versionOffset, versionIndex));
                                            versionOffset = versionIndex + 1;
                                        } else {
                                            versionSet.add(hashValue.substring(
                                                versionOffset,
                                                hashValue.length()));
                                        }
                                    } while (versionIndex >= 0);
                                    if (versionSet.contains(ALL_SUFFIX) ||
                                        versionSet.contains(
                                        latestIdentifierEntry[i])) {
                                        deleteList[i].setProperty(realKey,
                                            latestIdentifierEntry[i]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < latestEntryList.size(); i++) {
                for (int j = 0; j < origEntryList.size(); j++) {
                    String origVersion = origManifest[j].getProperty(
                        identifierEntry);
                    if ((origVersion != null) && origVersion.equals(
                        latestIdentifierEntry[i])) {
                        origManifest[j].remove(identifierEntry);
                        for (Enumeration keysEnum =
                            latestManifest[i].propertyNames();
                            keysEnum.hasMoreElements();) {
                            String latestKey = (String) keysEnum.nextElement();
                            if ((ignoredPath == null) || !latestKey.startsWith(
                                ignoredPath)) {
                                String latestHashValue =
                                    latestManifest[i].getProperty(latestKey);
                                String hashValue = origManifest[j].getProperty(
                                    latestKey);
                                if ((hashValue == null) ||
                                    (!hashValue.equalsIgnoreCase(
                                        latestHashValue))){
                                    addOrUpdateList[i].setProperty(latestKey,
                                        latestHashValue);
                                }
                            }
                            origManifest[j].remove(latestKey);
                        }
                        for (Enumeration keysEnum =
                            origManifest[j].propertyNames();
                            keysEnum.hasMoreElements();) {
                            String key = (String) keysEnum.nextElement();
                            deleteList[i].setProperty(key, 
                                origManifest[j].getProperty(key));
                        }
                        break;
                    }
                }
            }
            for (int i = 0; i < latestEntryList.size(); i++) {
                for (Enumeration keysEnum = addOrUpdateList[i].propertyNames();
                    keysEnum.hasMoreElements();) {
                    String key = (String)keysEnum.nextElement();
                    deleteList[i].remove(key);
                }
            }
            Properties versionAddOrUpdateList = new Properties();
            for (int i = 0; i < latestEntryList.size(); i++) {
                for (Enumeration keysEnum = addOrUpdateList[i].propertyNames();
                    keysEnum.hasMoreElements();) {
                    boolean allHave = ((i == 0) ? true : false );
                    String key = (String)keysEnum.nextElement();
                    String hashValue = addOrUpdateList[i].getProperty(key);
                    if (i == 0) {
                        for (int j = i + 1; j < latestEntryList.size(); j++) {
                            if (addOrUpdateList[j].getProperty(key) == null) {
                                allHave = false;
                                break;
                            }
                        }
                    }
                    if (allHave) {
                        versionAddOrUpdateList.setProperty(key +
                            IDENTIFIER_SEPARATOR + ALL_SUFFIX,
                            hashValue);
                        for (int k = 0; k < latestEntryList.size(); k++) {
                            addOrUpdateList[k].remove(key);
                        }
                    } else {
                        versionAddOrUpdateList.setProperty(key +
                            IDENTIFIER_SEPARATOR + latestIdentifierEntry[i],
                            hashValue);
                        addOrUpdateList[i].remove(key);
                    }
                }
                for (Enumeration keysEnum = deleteList[i].propertyNames();
                    keysEnum.hasMoreElements();) {
                    boolean allHave = ((i == 0) ? true : false);
                    String key = (String)keysEnum.nextElement();
                    String hashValue = deleteList[i].getProperty(key);
                    if (i == 0) {
                        for (int j = i + 1; j < latestEntryList.size(); j++) {
                            if (deleteList[j].getProperty(key) == null) {
                                allHave = false;
                                break;
                            }
                        }
                    }
                    if (allHave) {
                        versionAddOrUpdateList.setProperty(key +
                            IDENTIFIER_SEPARATOR + DELETE_SUFFIX, ALL_SUFFIX);
                        for (int k = 0; k < latestEntryList.size(); k++) {
                            deleteList[k].remove(key);
                        }
                    } else {
                        String hash = null;
                        if ((hash = versionAddOrUpdateList.getProperty(key +
                            IDENTIFIER_SEPARATOR + DELETE_SUFFIX)) != null) {
                            versionAddOrUpdateList.setProperty(key +
                                IDENTIFIER_SEPARATOR + DELETE_SUFFIX, hash + 
                                IDENTIFIER_SEPARATOR +
                                latestIdentifierEntry[i]);
                        } else {
                            versionAddOrUpdateList.setProperty(key +
                                IDENTIFIER_SEPARATOR + DELETE_SUFFIX,
                                latestIdentifierEntry[i]);
                        }
                        deleteList[i].remove(key);
                    }
                }
            }
            HashSet copiedFiles = new HashSet();
            JarOutputStream destPatchOut = null;
            try {
                File destPatchFile = null;
                if ((destPatchFilePath != null) && (destPatchFileName != null)) {
                    destPatchFile = new File(destPatchFilePath,
                        destPatchFileName);
                } else {
                    destPatchFile = new File(destPatchFileName);
                }
                File parentPath = destPatchFile.getParentFile();
                if ((parentPath != null) && (!parentPath.exists())) {
                    parentPath.mkdirs();
                }
                destPatchOut = new JarOutputStream(new FileOutputStream(
                    destPatchFile));
                byte[] buffer = new byte[BUFFER_SIZE];
                int byteRead = 0;
                boolean haveFound = false;
                for (Enumeration keysEnum =
                    versionAddOrUpdateList.propertyNames();
                    keysEnum.hasMoreElements();) {
                    String entryName = (String)keysEnum.nextElement();
                    if (!entryName.endsWith(IDENTIFIER_SEPARATOR +
                        DELETE_SUFFIX)) {
                        String identifier = entryName.substring(
                            entryName.lastIndexOf(IDENTIFIER_SEPARATOR) + 1,
                            entryName.length());
                        entryName = entryName.substring(0,
                            entryName.lastIndexOf(IDENTIFIER_SEPARATOR));
                        JarEntry entry = null;
                        haveFound = false;
                        if (!copiedFiles.contains(entryName)) {
                            for (int i = 0; i < latestEntryList.size(); i++) {
                                if (identifier.equals(ALL_SUFFIX) ||
                                    (latestIdentifierEntry[i].equals(
                                    identifier))) {
                                    try {
                                        latestWarIn = new JarInputStream(new
                                            FileInputStream(latestWarFile));
                                        correctIn = PatchGeneratorUtils
                                            .getCorrectInputStream(latestWarIn, 
                                            (String)latestEntryList.get(i));
                                        while ((entry =
                                            correctIn.getNextJarEntry())
                                            != null) {
                                            if (entry.getName().equals(
                                                entryName)) {
                                                destPatchOut.putNextEntry(new
                                                    JarEntry(entryName));
                                                while ((byteRead =
                                                    correctIn.read(buffer)) >
                                                    0) {
                                                    destPatchOut.write(buffer,
                                                        0, byteRead);
                                                }
                                                destPatchOut.closeEntry();
                                                haveFound = true;
                                                break;
                                            }
                                        }
                                    } finally {
                                        if (correctIn != null) {
                                            try{
                                                correctIn.close();
                                            } catch (Exception ignored) {}
                                        }
                                        if (latestWarIn != null) {
                                            try {
                                                latestWarIn.close();
                                            } catch (Exception ignored) {}
                                        }
                                    }
                                    copiedFiles.add(entryName);
                                }
                                if (haveFound) {
                                    break;
                                }
                            }
                        }
                    }
                }
                destPatchOut.putNextEntry(new JarEntry(manifestFileName));
                versionAddOrUpdateList.store(destPatchOut, "");
                destPatchOut.closeEntry();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error occurs when creating patch file " +
                    destPatchFilePath + "!");
                System.exit(1);
            } finally {
                try {
                    destPatchOut.close();
                } catch (Exception ignored) {}
            }
        } else {
            System.out.println("Manifest not found in latest war file!");
            System.exit(1);
        }
        System.exit(0);
    }
    
    /**
     * Prints  the usage for using the patch generation utility
     *
     */
    public static void printUsage(PrintStream out){
        System.out.println("Usage: java ARGUMENTS -jar PatchGenerator.jar");
        System.out.println("\nARGUMENTS");
        System.out.println("\t-D\"" + ORIGINAL_WAR_FILE + "=<RTM zip file>\"");
        System.out.println("\tPath of RTM zip file [Required].");
        System.out.println("\n\t-D\"" + LATEST_WAR_FILE +
            "=<latest zip file>\"");
        System.out.println("\tPath of latest zip file [Required].");
        System.out.println("\n\t-D\"" + ORIGINAL_WARFILE_ENTRY +
            "=<list of RTM war entry separated by '" + ENTRY_SEPARATOR +
            "'>\"");
        System.out.println("\tName of the war entries in the zip file " +
                "[Default: " + DEFAULT_WARFILE_ENTRY + "].");
        System.out.println("\n\t-D\"" + LATEST_WARFILE_ENTRY + 
            "=<list of latest war entry separated by '" + ENTRY_SEPARATOR + 
            "'>\"");
        System.out.println("\tName of the war entries in the zip file " +
                "[Default: " + DEFAULT_WARFILE_ENTRY + "].");
        System.out.println("\n\t-D\"" + SOURCE_PATCH_FILE +
            "=<previous patch file>\"");
        System.out.println("\tPath of the previous patch file (if any) " +
            "[Optional].");
        System.out.println("\n\t-D\"" + DEST_PATCH_FILE +
            "=<resulting patch file name>\"");
        System.out.println("\tName of the resulting file [Required if file." +
            "version is not defined].");
        System.out.println("\n\t-D\"" + DEST_PATCH_PATH + 
            "=<path of the resulting patch file>\"");
        System.out.println("\tPath of the resulting patch file [Optional].");
        System.out.println("\n\t-D\"" + IDENTIFIER_ENTRY +
            "=<name of the entry which indicate version>\"");
        System.out.println("\tName of the entry in manifest indicate identity " +
            "[Default: " + DEFAULT_IDENTIFIER_ENTRY + "].");
        System.out.println("\n\t-D\"" + MANIFEST_PATTERN +
            "=<pattern of manifest file>\"");
        System.out.println("\tPattern of the manifest file in the war file " +
            "[Default: " + DEFAULT_MANIFEST_PATTERN + "].");
        System.out.println("\n\t-D\"" + MANIFEST_FILE_NAME +
            "=<name of the manifest file in the resulting file>\"");
        System.out.println("\tName of the manifest file " +
            "[Required if " + VERSION_FILE + " is not defined].");
        System.out.println("\n\t-D\"" + WILDCARD_CHAR + 
            "=<char to be used as wildcard>\"");
        System.out.println("\tWild card character [Default: " +
            DEFAULT_WILDCARD_CHAR + "].");
        System.out.println("\n\t-D\"" + VERSION_FILE +
            "=<properties file indicate version>\"");
        System.out.println("\tProperties file indicate the version of patch " +
            "[Optional].");
        System.out.println("\n\t-D\"" + PROPERTIES_FILE +
            "=<propeties file has the above directive defined>\"");
        System.out.println("\tProperties file have above directives defined " +
            "[Optional].");
    }
}

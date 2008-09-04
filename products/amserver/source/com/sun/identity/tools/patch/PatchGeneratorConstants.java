/**
 * $Id: PatchGeneratorConstants.java,v 1.1 2008-09-04 16:44:17 kevinserwin Exp $
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

public interface PatchGeneratorConstants {

    public static final String ORIGINAL_WAR_FILE = "file.zip.rtm";
    public static final String LATEST_WAR_FILE = "file.zip.latest";
    public static final String SOURCE_PATCH_FILE = "file.patch.source";
    public static final String DEST_PATCH_FILE = "file.patch.dest";
    public static final String DEST_PATCH_PATH = "path.patch.dest";
    public static final String PROPERTIES_FILE = "file.properties";
    public static final String IDENTIFIER_ENTRY = "entry.identifier";
    public static final String DEFAULT_IDENTIFIER_ENTRY = "identifier";
    public static final String MANIFEST_PATTERN = "pattern.manifest";
    public static final String MANIFEST_FILE_NAME = "filename.manifest";
    public static final String DEFAULT_MANIFEST_FILE_NAME = ".manifest";
    public static final String WILDCARD_CHAR = "pattern.wildcard";
    public static final String DEFAULT_MANIFEST_PATTERN = "*.manifest";
    public static final String DEFAULT_WILDCARD_CHAR ="*";
    public static final String DELETE_SUFFIX = "delete";
    public static final String ALL_SUFFIX = "all";
    public static final String IDENTIFIER_SEPARATOR = ".";
    public static final String VERSION_FILE = "file.version";
    public static final String FILE_SEPARATOR =
        System.getProperty("file.separator");
    public static final String PATH_SEPARATOR =
        System.getProperty("path.separator");
    public static final String ENTRY_SEPARATOR = ";";
    public static final int BUFFER_SIZE = 8192;
    public static final String DEFAULT_WARFILE_ENTRY =
        "applications/jdk14/amserver.war" + ENTRY_SEPARATOR +
        "applications/jdk15/amserver.war" + ENTRY_SEPARATOR +
        "applications/amDistAuth.zip/amauthdistui.war";
    public static final String ENTRY_FILE_SEPARATOR = "/";
    public static final String ORIGINAL_WARFILE_ENTRY = "entry.war.rtm";
    public static final String LATEST_WARFILE_ENTRY = "entry.war.latest";
}

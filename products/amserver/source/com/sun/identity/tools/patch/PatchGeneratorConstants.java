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
* $Id: PatchGeneratorConstants.java,v 1.2 2008-09-04 22:26:12 kevinserwin Exp $
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

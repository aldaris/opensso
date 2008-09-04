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
* $Id: ManifestConstants.java,v 1.2 2008-09-04 22:26:12 kevinserwin Exp $
*/


package com.sun.identity.tools.manifest;

public interface ManifestConstants {
    
    int BUFFER_SIZE = 8192;
    String SHA1 = "SHA1";
    String DEFAULT_RECURSIVE = "true";
    String DEFAULT_MANIFEST_FILE_NAME = "MANIFEST.MF";
    String EQUAL = "=";
    String FILE_SEPARATOR = "/";
    char DEFAULT_WILD_CARD = '*';
    String PATTERN_SEPARATOR = ",";
    String HEADER_FILE_PATH = "file.header.path";
    String SRC_FILE_PATH = "file.src.path";
    String DEST_FILE_PATH = "file.dest.path";
    String RECURSIVE = "file.recursive";
    String INCLUDE_PATTERN = "file.include";
    String EXCLUDE_PATTERN = "file.exclude";
    String MANIFEST_NAME = "name.manifest";
    String WILDCARD_CHAR = "char.wildcard";
    String DIGEST_ALG = "digest.alg";
    String DIGEST_HANDLEJAR = "digest.handlejar";
    String DEFAULT_DIGEST_HANDLEJAR = "true";
    String DIGEST_HANDLEWAR = "digest.handlewar";
    String DEFAULT_DIGEST_HANDLEWAR = "true";
    String JAR_FILE_EXT = ".jar";
    String WAR_FILE_EXT = ".war";
    String OVERWRITE = "file.overwrite";
    String DEFAULT_OVERWRITE = "true";
    
}

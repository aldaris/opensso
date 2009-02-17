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
* $Id: PatchGeneratorConstants.java,v 1.5 2009-02-17 18:41:11 kevinserwin Exp $
*/

package com.sun.identity.tools.patch;

public interface PatchGeneratorConstants {
    String SRC_FILE_PATH = "file.src.path";
    String DEST_FILE_PATH = "file.dest.path";    
    String SRC2_FILE_PATH = "file.src2.path";    
    String RESOURCE_BUNDLE_NAME = "ssoPatch";
    String HEADER_FILE_PATH = "file.header.path";
    String STAGING_FILE_PATH = "file.staging.path";
    String OPTION_OVERWRITE = "option.overwrite";
    String OPTION_OVERRIDE = "option.override";

    String DEFAULT_OVERWRITE = "false";
    String DEFAULT_OVERRIDE = "false";

    
    public static final String MANIFEST_CREATE_FILE = "file.create.manifest";
    public static final String DEFAULT_MANIFEST_FILE = "META-INF/OpenSSO.manifest";
    public static final String PROPERTIES_FILE = "file.properties";
    public static final String IDENTIFIER_ENTRY = "entry.identifier";
    public static final String DEFAULT_IDENTIFIER_ENTRY = "identifier";
    public static final String MANIFEST_PATTERN = "pattern.manifest";
    public static final String MANIFEST_FILE_NAME = "filename.manifest";
    public static final String DEFAULT_MANIFEST_FILE_NAME = ".manifest";
    public static final String WILDCARD_CHAR = "pattern.wildcard";
    public static final String DEFAULT_MANIFEST_PATTERN = "*.manifest";
    public static final String DEFAULT_WILDCARD_CHAR ="*";
    public static final String OPTION_LOCALE = "option.locale";
    public static final String DEFAULT_LOCALE ="en_US";
}

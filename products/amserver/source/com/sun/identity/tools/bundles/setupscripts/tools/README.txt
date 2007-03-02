<!--
   The contents of this file are subject to the terms
   of the Common Development and Distribution License
   (the License). You may not use this file except in
   compliance with the License.

   You can obtain a copy of the License at
   https://opensso.dev.java.net/public/CDDLv1.0.html or
   opensso/legal/CDDLv1.0.txt
   See the License for the specific language governing
   permission and limitations under the License.

   When distributing Covered Code, include this CDDL
   Header Notice in each file and include the License file
   at opensso/legal/CDDLv1.0.txt.
   If applicable, add the following below the CDDL Header,
   with the fields enclosed by brackets [] replaced by
   your own identifying information:
   "Portions Copyrighted [year] [name of copyright owner]"

   $Id: README.txt,v 1.1 2007-03-02 18:58:02 ak138937 Exp $

   Copyright 2007 Sun Microsystems Inc. All Rights Reserved
-->
                                                                               
Sun Java System OpenSSO: amAdminTools.zip 
====================================================

This file contains information on installing and using amAdminTools.zip.
It is assumed that Access Manager Server is available.

Table of contents:
-----------------
1. Supported JDK versions
2. Installing amAdminTools.zip
3. What does this package contain

1. Supported JDK versions
-------------------------
Supported JDK versions are J2SE 1.4 or higher.

2. Installing amAdminTools.zip
------------------------------ 

Steps involved in installing amAdminTools.zip are:

Step 1: Unzip amAdminTools.zip to the desired directory.

Step 2: Go to the directory which has the amAdminTools.zip unzipped.
        Run the setup command as follows:
        "setup -p | --path <LOCATION_OF_CONFIGURED_AM_INSTANCE>"
        where <LOCATION_OF_CONFIGURED_AM_INSTANCE> is the location of
        AMConfig.properties

        Note: 
            a. Users under Linux or Unix environment may need to run
               "chmod +x setup" before running setup.
            b. If the setup is run without any options, then the user will be
               prompted as following:
               "Path to config files of Access Manager (example: /opt/SUNWam/
               config):"

Step 3: After step 2 is performed, the CLI's can be run under the following
        directory:
        <TOOLS_DIR>/<AM_INSTANCE_NAME>/bin
        where <TOOLS_DIR> is the directory which has amAdminTools.zip unzipped
        (ie: current directory), and <AM_INSTANCE_NAME> is the name of the 
        Access Manager and <AM_INSTANCE_NAME> is the name of the Access Manager
        deployed instance in the container.
        
3. What does this package contains
----------------------------------
<ZIP_ROOT>
|
|----README (this file)
|
|----lib
|      |
|      |----*.jar (binaries needed for the scripts)
|
|----locale
|      |
|      |----*.properties (properties file for binaries)
|      |
|      |----*.dtd (schema needed for xml parser)
|
|----setup (setup script for linux and unix)
|
|----setup.bat (setup script for windows)
|
|----template
|      |
|      |----unix/bin/*.template (template of scripts for unix and linux)
|      |
|      |----windows/bin/* (required windows specific libraries)

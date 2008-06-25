#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
#
# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the License). You may not use this file except in
# compliance with the License.
#
# You can obtain a copy of the License at
# https://opensso.dev.java.net/public/CDDLv1.0.html or
# opensso/legal/CDDLv1.0.txt
# See the License for the specific language governing
# permission and limitations under the License.
#
# When distributing Covered Code, include this CDDL
# Header Notice in each file and include the License file
# at opensso/legal/CDDLv1.0.txt.
# If applicable, add the following below the CDDL Header,
# with the fields enclosed by brackets [] replaced by
# your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# $Id: README.txt,v 1.3 2008-06-25 05:41:16 qcheng Exp $
#
amSessionTools.zip 
======================================================

This file contains information on installing and using amSessionTools.zip.
It is assumed that Access Manager Server is available.

Table of contents:
-----------------
1. Supported JDK versions
2. Installing amSessionTools.zip
3. What does this package contain

1. Supported JDK versions
-------------------------
Supported JDK versions are Java SE 5 or higher.

2. Installing amSessionTools.zip
-------------------------------- 

Steps involved in installing amSessionTools.zip are:

Step 1: Unzip amSessionTools.zip to the desired directory.

Step 2: Go to the directory which has the amSessionTools.zip unzipped.
        Run the setup command as follows:
        "setup -p | --path <DIRECTORY_OF_THE_SCRIPTS_TO_BE_PLACED>"
        where <DIRECTORY_OF_THE_SCRIPTS_TO_BE_PLACED> is the one level relative
        directory created under current directory

        Note:
            a. Users under Linux or Unix environment may need to run
               "chmod +x setup" before running setup.
            b. If the setup is run without any options, then the user will be
               prompted as following:
               "Name of the directory to install the scripts (example: sfoscripts):"

Step 3: After step 2 is performed, the CLI's can be run under the following
        directory:
        <SESSION_TOOLS_DIR>/<DIRECTORY_OF_THE_SCRIPTS_TO_BE_PLACED>/bin
        where <SESSION_TOOLS_DIR> is the directory which has amSessionTools.zip
        unzipped, and <DIRECTORY_OF_THE_SCRIPTS_TO_BE_PLACED> is the name of
        the directory user input.
        
3. What does this package contains
----------------------------------
<ZIP_ROOT>
|
|----README (this file)
|
|----ext
|      |
|      |----mq4_1-binary-Linux_X86-20070816.jar (jmq binaries for x86 linux)
|      |
|      |----mq4_1-binary-SunOS_X86-20070816.jar (jmq binaries for x86 Solaris)
|      |
|      |----mq4_1-binary-SunOS-20070816.jar (jmq binaries for sparc Solaris)
|      |
|      |----mq4_1-binary-WINNT-20070816.jar (jmq binaries for Windows)
|      |
|      |----je.jar (bdb binaries in java)
|
|----lib
|      |
|      |----SimpleSetupTools.jar (binaries for setup tools)
|      |
|      |----am_sessiondb.jar (binaries for session API)
|
|----locale/amSessionDB.properties (properties file for AM session API)
|
|----setup (setup script for linux and unix)
|
|----setup.bat (setup script for windows)
|
|----template
|      |
|      |----unix/bin/*.template (template of scripts for unix and linux)
|      |
|      |----unix/config/lib/*.template (template of scripts for unix and linux)
|      |
|      |----windows/bin/*.template (template of scripts for windows)
|      |
|      |----windows/config/lib/*.template (template of scripts for windows)

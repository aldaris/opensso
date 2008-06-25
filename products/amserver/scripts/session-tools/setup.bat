@echo off

   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
  
: Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
  
: The contents of this file are subject to the terms
: of the Common Development and Distribution License
: (the License). You may not use this file except in
: compliance with the License.
:
: You can obtain a copy of the License at
: https://opensso.dev.java.net/public/CDDLv1.0.html or
: opensso/legal/CDDLv1.0.txt
: See the License for the specific language governing
: permission and limitations under the License.
:
: When distributing Covered Code, include this CDDL
: Header Notice in each file and include the License file
: at opensso/legal/CDDLv1.0.txt.
: If applicable, add the following below the CDDL Header,
: with the fields enclosed by brackets [] replaced by
: your own identifying information:
: "Portions Copyrighted [year] [name of copyright owner]"
:
: $Id: setup.bat,v 1.6 2008-06-25 05:41:17 qcheng Exp $
:

SETLOCAL
IF "%1" == "-h" SET help_print=yes
IF "%1" == "--help" SET help_print=yes
IF "%1" == "-p" SET path_dest=%~2
IF "%1" == "--path" SET path_dest=%~2

"%JAVA_HOME%/bin/java.exe" -version:"1.4+" -D"help.print=%help_print%" -D"path.dest=%path_dest%" -jar "lib/am_session_setup.jar"
SETLOCAL

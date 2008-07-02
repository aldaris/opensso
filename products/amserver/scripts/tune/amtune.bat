@echo off
:
: DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
:
: Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
:
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
: $Id: amtune.bat,v 1.1 2008-07-02 18:28:55 kanduls Exp $

set TOOLS_HOME="@TOOLS_HOME@"
"@JAVA_HOME@/bin/java.exe" -cp "%TOOLS_HOME%/locale;%TOOLS_HOME%/lib/ldapjdk.jar;%TOOLS_HOME%/lib/amtune.jar;./;" com.sun.identity.tune.AMTune
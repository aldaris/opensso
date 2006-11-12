@echo off

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
: $Id: amadm.bat,v 1.1 2006-11-12 08:45:14 veiming Exp $
:
: Copyright 2006 Sun Microsystems Inc. All Rights Reserved

setlocal
:WHILE
if x%1==x goto WEND
set PARAMS=%PARAMS% %1
shift
goto WHILE
:WEND

java  -Xms64m -Xmx256m -cp classes;lib/ldapjdk.jar;lib/mail.jar;lib/j2ee.jar;lib/jaxb-api.jar;lib/jaxb-impl.jar;lib/jaxb-libs.jar;lib/xsdlib.jar;lib/xmlsec.jar -D"com.iplanet.am.serverMode=false" -D"definitionFiles=com.sun.identity.cli.AccessManager" -D"commandName=fmadm" -D"amconfig=AMConfig" -D"java.util.logging.manager=com.sun.identity.log.LogManager" -D"java.util.logging.config.class=com.sun.identity.log.s1is.LogConfigReader" com.sun.identity.cli.CommandManager %PARAMS%
endlocal

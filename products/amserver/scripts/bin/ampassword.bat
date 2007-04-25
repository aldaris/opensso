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
: $Id: ampassword.bat,v 1.3 2007-04-25 22:58:40 veiming Exp $
:
: Copyright 2006 Sun Microsystems Inc. All Rights Reserved

java -D"java.version.current=java.vm.version" -D"java.version.expected=1.4+" -D"version.file=@CONFIG_DIR@/AMConfig.properties" -D"xml.config=@CONFIG_DIR@/serverconfig.xml" -D"am.version.current=com.iplanet.am.version" -D"am.version.expected=7.5" -D"version.check=yes" -jar "@BASE_DIR@/lib/amadm_setup.jar" 
IF %ERRORLEVEL% EQU 1 GOTO END 

setlocal
:WHILE
if x%1==x goto WEND
set PARAMS=%PARAMS% %1
shift
goto WHILE
:WEND

set TOOLS_HOME=@TOOLS_HOME@

set TOOLS_CLASSPATH=@CONFIG_DIR@;%TOOLS_HOME%/lib/activation.jar;%TOOLS_HOME%/lib/db.jar;%TOOLS_HOME%/lib/j2ee.jar;%TOOLS_HOME%/lib/jaxb-api.jar;%TOOLS_HOME%/lib/jaxrpc-api.jar;%TOOLS_HOME%/lib/jaxrpc-impl.jar;%TOOLS_HOME%/lib/jaxrpc-spi.jar;%TOOLS_HOME%/lib/ldapjdk.jar;%TOOLS_HOME%/lib/mail.jar;%TOOLS_HOME%/lib/saaj-api.jar;%TOOLS_HOME%/lib/saaj-impl.jar;%TOOLS_HOME%/lib/opensso.jar;%TOOLS_HOME%/lib/opensso-sharedlib.jar;%TOOLS_HOME%/locale;%TOOLS_HOME%/config


java -Xms64m -Xmx256m -classpath %TOOLS_CLASSPATH% com.iplanet.services.ldap.ServerConfigMgr %PARAM%
endlocal
:END

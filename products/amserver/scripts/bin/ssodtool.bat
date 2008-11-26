:: DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
::
:: Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
::
:: The contents of this file are subject to the terms
:: of the Common Development and Distribution License
:: (the License). You may not use this file except in
:: compliance with the License.
::
:: You can obtain a copy of the License at
:: https://opensso.dev.java.net/public/CDDLv1.0.html or
:: opensso/legal/CDDLv1.0.txt
:: See the License for the specific language governing
:: permission and limitations under the License.
::
:: When distributing Covered Code, include this CDDL
:: Header Notice in each file and include the License file
:: at opensso/legal/CDDLv1.0.txt.
:: If applicable, add the following below the CDDL Header,
:: with the fields enclosed by brackets [] replaced by
:: your own identifying information:
:: "Portions Copyrighted [year] [name of copyright owner]"
::
:: $Id: ssodtool.bat,v 1.1 2008-11-22 02:51:48 ak138937 Exp $
::

if not "%JAVA_HOME%" == "" goto checkJavaHome
echo Please define JAVA_HOME environment variable before running this program
goto exit

:checkJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto invalidJavaHome
goto validJavaHome

:invalidJavaHome
echo The defined JAVA_HOME environment variable is not correct
goto exit

:validJavaHome
SETLOCAL
CALL %JAVA_HOME%\bin\java.exe -version 2>&1|more > java_version.txt
SET /P java_version=< java_version.txt
DEL java_version.txt
CALL :GET_VERSION_NUM %java_version:"1.= %
CALL :GET_MID_VERSION_NUM %java_version:.= %
IF "%java_version%" == "0" goto invalidJavaVersion
IF "%java_version%" == "1" goto invalidJavaVersion
IF "%java_version%" == "2" goto invalidJavaVersion
IF "%java_version%" == "3" goto invalidJavaVersion
IF "%java_version%" == "4" goto invalidJavaVersion
goto run

:invalidJavaVersion
echo This program is designed to work with 1.5 or newer JRE.
goto exit

:GET_VERSION_NUM
SET java_version=%3
goto exit

:GET_MID_VERSION_NUM
SET java_version=%1
goto exit

:run

SET DEBUG_FLAGS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8888,server=y,suspend=n"

%JAVA_HOME%/bin/java -cp .;config;lib/locale.jar;lib/opensso-sharedlib.jar;lib/amserver.jar;lib/ldapjdk.jar;lib/OpenDS.jar;lib/jaxb-impl.jar;lib/jaxb-api.jar;lib/xsdlib.jar;lib/toolbase.jar;lib/webservices-rt.jar com.sun.identity.diagnostic.base.core.DiagnosticToolMain %*
ENDLOCAL

:exit
exit /b 1
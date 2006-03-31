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
: $Id: amverifyarchive.bat,v 1.1 2006-03-31 05:07:02 veiming Exp $
:
: Copyright 2005 Sun Microsystems Inc. All Rights Reserved


:
: amverifyarchive.bat for Windows 2000
:

set OLD_PATH=%PATH%
set OLD_CLASSPATH=%CLASSPATH%
set AM_DIR=PLATDIR
set DP_HOME=%AM_DIR%
set JAVA_HOME=JDK_PATH\bin
set AM_PKG_PREFIX=com.sun.identity.log
set DPADMIN_JAR=%DP_HOME%\lib\am_services.jar;%DP_HOME%\lib\am_sdk.jar
set DP_CONFIG=%DP_HOME%\config
set DP_PROPERTIES=%DP_HOME%\locale
set DP_LIB=%DP_HOME%\lib
set SSO_CONFIG=%DP_CONFIG%\amadmin
set DP_CLASSPATH=%SSO_CONFIG%;%DP_CONFIG%;%DP_PROPERTIES%;%DP_LIB%\jaxp.jar;%DP_LIB%\crimson.jar;%DP_LIB%\servlet.jar;%DP_LIB%\jaas.jar;%DP_LIB%\mail.jar;%DP_LIB%\activation.jar;%DPADMIN_JAR%;%DP_LIB%;%DP_HOME%\lib\preference_servlet.jar;%DP_HOME%\lib\jss311.jar;%DP_HOME%\lib\acm.jar;%DP_HOME%\lib\am_logging.jar
set CLASSPATH=%DP_CLASSPATH%;%JAVA_HOME%
set NSPR_NATIVE_THREADS_ONLY=1
set PATH=%JAVA_HOME%;%DP_HOME%\lib;%PATH%
java -Xms64m -Xmx256m -D"max_conn_pool=10" -D"min_conn_pool=1" -D"java.protocol.handler.pkgs=com.iplanet.services.comm" -D"java.util.logging.config.class=com.sun.identity.log.s1is.LogConfigReader" -D"java.util.logging.manager=com.sun.identity.log.LogManager" -Xbootclasspath:"JDK_PATH\lib\tools.jar;JDK_PATH\jre\lib\rt.jar;%DP_HOME%\lib\jdk_logging.jar" %AM_PKG_PREFIX%.cli.ISArchiveVerify %*

set PATH=%OLD_PATH%
set CLASSPATH=%OLD_CLASSPATH%

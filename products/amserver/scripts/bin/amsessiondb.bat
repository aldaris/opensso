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
: $Id: amsessiondb.bat,v 1.2 2008-04-21 18:56:08 weisun2 Exp $
:
: Copyright 2006 Sun Microsystems Inc. All Rights Reserved

setlocal
set JAVA_HOME=JDK_PATH
set AM_HOME=BASEDIR\PRODUCT_DIR
set IMQ_JAR_PATH=%AM_HOME%\..\MessageQueue\lib
set JMS_JAR_PATH=%AM_HOME%\..\MessageQueue\lib
set BDB_JAR_PATH=%AM_HOME%\..\share\lib

set CLASSPATH=%IMQ_JAR_PATH%\imq.jar;%JMS_JAR_PATH%\jms.jar;%AM_HOME%\ext\je.jar;%AM_HOME%\locale;%AM_HOME%\lib\am_sessiondb.jar;.

set JAVA_OPTS=""

%JAVA_HOME%/bin/java.exe %JAVA_OPTS% -classpath "%CLASSPATH%" com.sun.identity.ha.jmqdb.client.FAMHaDB %*
endlocal

#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
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
# $Id: README.txt,v 1.2 2008-07-01 06:27:49 veiming Exp $
#

TODO: Where to get openssowssproviders.jar
      Have to setup jks on client end

Web Service Security Proxy (WSS Proxy)
--------------------------------------

%%1. Overview
%%2. Build WSS Proxy WAR 
     %%2.1 Library dependencies
     %%2.2 Server Configuration
     %%2.3 Build Web ARchive, wssproxy.war
%%3. Deploy WAR
%%4. Simple Test

________________________________________________________________________________
%%1. Overview

WSS Proxy is a Java EE compliance Web ARchive (WAR) that encrypts and decrypts
web service message. This is the typical setup.

                                   secured
 +-------------+    +-----------+  messages   +-----------+    +-------------+
 |             |    |           |             |           |    |             |
 |             |--->|           |------------>|           |--->|             |
 | Web Service |    | WSS Proxy |             | WSS Proxy |    | Web Service |
 |   Client    |    |           |<------------|           |<---|  Provider   |
 |             |    |           |             |           |    |             |
 +-------------+    +-----------+             +-----------+    +-------------+
                    encrypts  |                | decrypts
                              |                |
                              |                |
                              |                |
                           +-------------------------+
                           |                         |
                           |    OpenSSO Server       |
                           |                         |
                           +-------------------------+

Each WSS Proxy is associated with a web service profile in OpenSSO Server.
In order to encrypt and decrypt web service messages, the proxies need to 
read their profiles from OpenSSO Server. The profile provides information on
security mechanism and end points.

________________________________________________________________________________
%%2. Build WSS Proxy WAR 

%%2.1 Library dependencies
Followings are the dependencies
    webservices-api.jar
    webservices-extra-api.jar
    webservices-extra.jar
    webservices-rt.jar
    webservices-tools.jar
    xalan.jar
    xercesImpl.jar
    j2ee.jar
    openssowssproviders.jar
    openssoclientsdk.jar

The following jars can be obtained by downloading opensso-sun-extlib.zip from
http://download.java.net/general/opensso/extlib/latest/opensso-sun-extlib.zip

    webservices-api.jar
    webservices-extra-api.jar
    webservices-extra.jar
    webservices-rt.jar
    webservices-tools.jar
    xalan.jar
    xercesImpl.jar

Look at opensso/products/README for instruction on how to obtain
    j2ee.jar

download the latest openssoclientsdk.jar from
https://opensso.dev.java.net/public/use/index.html



%%2.2 Server Configuration
Enter server configuration information in resources/clientDefault.properties.

%%2.3 Build Web ARchive, wssproxy.war
type ant build

________________________________________________________________________________
%%3. Deploy WAR

Deploy the wssproxy.war in any Java EE compliant web container. Make sure that 
its server is up and running.

________________________________________________________________________________
%%4. Simple Test

Use NetBean to create a Hello World Web Service Provider. And deploy the WAR.
Let it is http://www.wspsample.com:8080/WSP

Login to OpenSSO server.
Create a WSSProxy for the Hello World Web Service Provider. Let it is be
wspProxy.
Select UserNameToken as one of the supported Security Mechanisms.
Enter http://www.wspsample.com:8080/WSP as the Web Service Security Proxy End
Point.

Create a WSSProxy for the Web Service Client.  Let it is be wscProxy.
Select UserNameToken as the Security Mechanism.
Enter http://www.wspproxy.com:8080/wssproxy/SecurityProxy/wspProxy as the
Web Service Security Proxy End Point.
   where
       http://www.wssproxy.com:8080/wspproxy/SecurityProxy is the WSSProxy
       servlet that you have setup in step %%3.
       wspProxy is name of the corresponding Hello World Web Service
       Provider's WSSProxy.

Use NetBean to create a Web Service Client. Create a web service client. Enter
http://www.wssproxy.com:8080/wscproxy/SecurityProxy/wscProxy to fetch WSDL.
In the index.jsp, generate code to call the Web Service Operation.
Deploy the WAR. let it is http://www.wscsample.com:8080/WSC

Point your browser to http://www.wscsample.com:8080/WSC and index.jsp will be
invoked and Hello world message will be printed accordingly.


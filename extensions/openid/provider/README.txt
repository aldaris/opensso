-----------------------------------------------------------------------------
OpenID provider 1.0 alpha2
README file (updated 2007-04-29)
------------------------------------------------------------------------------

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

$Id: README.txt,v 1.1 2007-04-30 01:28:25 pbryan Exp $

Copyright 2007 Sun Microsystems Inc. All Rights Reserved
Portions Copyrighted Paul C. Bryan
------------------------------------------------------------------------------


Introduction

The OpenID provider provides a complete OpenID Authentication 1.1 protocol
compliant identity provider implementation, complete with full support for
OpenID Simple Registration Extension 1.0


Features

This release includes the following enhancements over 1.0 alpha1:

 * Standalone web application as deployable WAR file
 * OpenID message object model; supports future consumer implementation
 * Trust management user interface (non-persistent trust decisions)
 * Simple Registration Extension user interface
 * On-the-fly l10n and i18n (English, French and German included)
 * Full decoupling from authentication infrastructure through getUserPrincipal
 * Integration with OpenSSO through servlet filter implementation
 * Configurable OpenID identity regular expression pattern
 * Configurable authentication provider principal mapping
 * No more dependencies on OpenSSO internal classes


Roadmap

This is the second release in a planned series of releases. Version 1.0
alpha3 targets to include the following enhancements:

 * Persistent trust decisions (via pluggable persistence SPI)
 * Persistent persona management and associated user interface
 * Integration with other authentication infrastructures
 * Response to errors through published openid.error mechanism
 * Further refinement in preparation for OpenID 2.0 ratification
 * Full supporting documentation
 * Comprehensive logging


Dependencies

To build the OpenID provider, the following dependencies must be met, by
including JAR files in the extlib directory. Versions supplied are those
that were tested with this release. Results of changes may vary:

 * Facelets 1.1.12
 * JavaServer Faces version 1.2_03-b05-FCS (included in Facelets release)
 * Jakarta Commons Codec 1.3
 * AM Client SDK (from OpenSSO "clientsdk" build target)
 * Java Enterprise Edition 5 SDK

This provider implementation has been tested on the following application
server environments:

 * Apache Tomcat 5.5.23
 * GlassFish v2 Build 44 25-April-07

This provider is coded using features present in Java 2 Standard Edition 5
and later. It was built, deployed and tested using the following JDK:

 * Java(TM) 2 Runtime Environment, Standard Edition (build 1.5.0_11-b03)


Installing dependencies

1. Download and extract the Facelets binary archive from
   https://facelets.dev.java.net/. Copy the following files from the extracted
   archive lib/ directory to the provider/extlib directory:

   * commons-beanutils.jar
   * commons-collections.jar
   * commons-digester.jar
   * commons-logging.jar
   * el-api.jar
   * el-ri.jar
   * jsf-api.jar
   * jsf-facelets.jar
   * jsf-impl.jar
   * jsp-api.jar

2. Download and extract the Jakarta Commons Codec binary archive from
   http://jakarta.apache.org/commons/codec/. Copy the commons-codec-1.3.jar
   file from the extracted archive to the provider/extlib directory:

3. Build the OpenSSO client SDK inside of the OpenSSO amserver product by
   invoking the "clientsdk" target in the associated Ant project. Once built,
   copy the amclientsdk.jar file from the built/dist directory to the
   provider/extlib directory.

4. Download the Java Enterprise Edition 5 SDK from
   http://java.sun.com/javaee/downloads/index.jsp. Copy the javaee.jar from the
   lib directory of the SDK to the provider/extlib directory (rename to
   j2ee.jar).


Configuration

The WAR file will contain the configuration properties files for both the
provider itself and the OpenSSO client SDK library. Prior to build, change
these files for the production WAR configuration:

1. Edit the resources/Provider.properties file to configure:

   * OpenID identity URL regular expression pattern
   * User principal regular expression pattern
   * Provider encryption key (to encrypt association handles)
   * Login URL for OpenSSO login page

2. Copy the AMClient.properties file from the OpenSSO client SDK resources
   directory (products/amserver/clientsdk/resources) to the
   provider/resources directory (renaming to AMConfig.properties). Configure:

   * Location of OpenSSO server (protocol, host, port)
   * Cookie name for OpenSSO session identifiers
   * Security credentials to access OpenSSO services
   * Encryption key to encrypt data to/from OpenSSO services
   * Naming URL in OpenSSO server

3. Create an agent identity in OpenSSO administration consile with security
   credentials that match those set in AMConfig.properties file.


Building the application

Build the provider.war file by invoking the "war" build target. It generates
a file, provider.war, which will be deployed on your application server.


Deploying the application

Deploy the provider.war file on your application server. The usual context
path is /provider, though others should work fine (e.g. /openid).


Linking to your identity provider

The main entry point to your identity provider is provided through the
/service servlet. It dispatches to internal actions and Facelets as required.

So, if you deployed the provider application in /provider on idp.example.com,
then the URL for the OpenID provider service would be:

 * http://idp.example.com/provider/service

This is what will be set in the <link /> tag in OpenID profile pages.
For example:

 <link rel="openid.server" href="http://idp.example.com/provider/service" />


Customization

The user interface of this application his highly templated, facilitating easy
customization. For a brief description of how files are used in this release,
see docs/files.txt.


Contact

Feel free to contact the author about this release. Contact information is as
follows:

 * Name: Paul C. Bryan
 * Email: pbryan@dev.java.net
 * IRC: pbryan@irc.freenode.net

------------------------------------------------------------------------------

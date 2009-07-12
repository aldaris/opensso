-----------------------------------------------------------------------------
OpenSSO Authentication Module for Information Cards version authnicrp-0.9
By Patrick Petit (Patrick.Petit@Sun.Com)
-----------------------------------------------------------------------------

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

$Id: README.txt,v 1.11 2009-07-12 22:11:58 ppetitsm Exp $

Copyright 2008 Sun Microsystems Inc. All Rights Reserved
Portions Copyrighted 2008 Patrick Petit Consulting

---------------------------------------------------------

Introduction
------------

The Information Card authentication module (Authnicrp) provides the ability for
a Relying Party (RP) to accept Information Cards as a means of authentication and
authorization to access secured Web resources. It has been successfully tested with
OpenSSO 8 build 1, running on Glassfish application server v2.1, using self-issued
cards and managed cards from DigitalMe and Azigo Identity Selectors.

As in the previous version, Authnicrp relies on the OpenInfocard project to handle
the Security Token issued by the Identity Selector or Security Token Service. But,
Authnicrp-0.9 presents a major improvement compared to the initial version, that
was committed more than year ago, which primary objective was the roll-out of a
quick proof of concept. In particular, the need a companion JavaDB database has
been removed. Information Card data is now part of the user profile that is stored
in the Identity Repository (IdRepo) of OpenSSO, which can either be Active Directory,
OpenDS, Sun Directory Server Enterprise Edition or MySQL through the JDBC IdRepo
plug-in. A RP can also now specify its Security Token requirements and security
policies dynamically though the Authnicrp's service configuration that is integrated
in the OpenSSO administration console. As such, different security policies for
secured Web resources can be expressed at the domain and sub-domain (realms) levels
in one instance of OpenSSO server. More fine-grained control levels are possible.

Thanks to the OpenSSO Agent 3.0, Information Card support can be added to any
Web application without programming. Information Card claims can be passed by
the agent to the application as HTTP request parameters, HTTP header parameters
or cookies. It also possible to obtain Information Card claims from the user's
session attributes.

Features
--------

The Information Card authentication module (Authnicrp) supports three different
authentication modes:

1 - Anonymous
2 - Required
3 - Dynamic

In Anonymous mode, Authnicrp authenticates the Information Card bearer at the
condition the Security Token returned by the Identity Selector complies with
the RP's security policy requirements. For instance, security policy
requirements may mandate certain required claims and that those required claims
be verified. Once the user is authenticated a session is created on behalf of the
user, which is assigned a configurable anonymous user ID. An anonymous user can
be assigned a set of default roles, which can further be used to evaluate the
user access rights to protected resources.

The Required mode, requires that a user account already exists in the Identity
Repository to authenticate the Information Card bearer successfully. The user is
asked to provide a user ID and password the first time the Information Card is
presented. Upon successful authentication of the user's credentials, the Information
Card is registered in the user's profile. Subsequent authentications will use the
Information Card instead of the user Id and password until the user changes his
password or remove the Information Card from his profile. As with the anonymous,
it is possible to define default roles when a user authenticates with his Information
Card, or specify specific roles based on the user's group membership.

The Dynamic mode is used for self-provisioning of new user accounts using the
Information Card claims. The Authnicrp configuration console provides a flexible
mechanism by which the administrator can define a claim to attribute mapping scheme,
by which a claim can be mapped to a user profile attribute.

Roadmap
-------

1 - The next release of Authnicrp (version 1.0) targets to fully support the Identity
Metasystem Interoperability Version 1.0, OASIS Committee Draft 01.

2 - Support claim to attribute types conversions

3 - Support role based authorization using claims


Build Instructions
------------------

In order to run the build.xml to compile the jar file, create a directory named
extlib under the authnicrp root containing the following JAR files.
You need to compile with JDK 1.6, and setup JAVA_HOME environment variable
accordingly.

xmldap-1.0.jar:
    Compile with JDK 6
    The Xmldap.org code can be retrieved from the 'openinfocard' project at
    http://code.google.com/p/openinfocard/
    To build:
        svn checkout http://openinfocard.googlecode.com/svn/trunk/ \
        openinfocard-read-only
        cd openinfocard-read-only/ant
        ant build_core
    Copy xmldap-1.0.jar from openinfocard-read-only/build/xmldap-1.0/ to extlib

opensso.jar:
opensso-sharelib.jar
    The above files can be retrieved from the <OPENSSO_INSTALL_DIR>/WEB-INF/lib

servlet.jar:
    The three names I have seen for servlet.jar, depends on the server
    (servlet.jar, servlet-api.jar, j2ee.jar). If you have a Servlet Container,
    it will be somewhere in the container's classpath, (Tomcat = common/lib 
    directory)
    Otherwise, look into downloading Tomcat, or look for J2EE 1.4 SDK and for an
    implementation from Sun Microsystems.


Modify the 'opensso-root.dir' property in build.xml to make it point to OpenSSO's
deployment directory.
For example: /opt/glassfish/domains/domain1/applications/j2ee-modules/opensso

Open the project in Netbeans 6.5.1 or latter, and click 'Clean & Build", or simply
invoke the default build target.

# ant

--------------------------------------------------------------------------------
To install the module in opensso you need to follow the instructions outlined 
below
--------------------------------------------------------------------------------

1 - Install OpenSSO (Download from 
    https://opensso.dev.java.net/public/use/index.html). 
    The module has been tested against OpenSSO v1 Builds 2, 3, 4 and 5

2 - Using the OpenSSO Admin console, navigate to Configuration>Core>Auth Module 
    and type com.identarian.infocard.opensso.rp.Infocard in the 'New Value' 
    field under 'Pluggable Authentication Module Classes', click 'Add', then 
    'Save'.

3 - Edit the source/amAuthInfocard.xml file to define the application server's
    key store password and alias which are respectively defined by the
    'iplanet-am-auth-infocard-keyStorePassword' attribute and
    'iplanet-am-auth-infocard-keyAlias' attribute.
    On Glassfish V2, which the module has been tested, the default alias is 's1as'
    and the default password is the application's server admin password (i.e. 
    'adminadmin').
    If you have installed Glassfish via NetBeans 6.0, then the key store 
    password is 'changeit'.
    On Glassfish you can verify the password and alias with keytool -list \
    -storepass changeit -keystore \
    <GLASSFISH_DIR>/domains/domain1/config/keystore.jks

4 - Install the module in OpenSSO. An 'install' ant target has been provided for
    your convenience. Its function is to copy all the necessary files in the
    deployed OpenSSO instance.
    Prior to invoking the 'install' ant target, you will need to modify the
    opensso-root.dir property in build.xml in order to define the location of the
    opensso instance. For example:
    /usr/local/glassfish/domains/domain1/applications/j2ee-modules/opensso/.

6-  Install the authentication module in opensso. Invoke the
    following commands in sequence:

    $ ssoadm create-svc -X amAuthInfocard.xml -u amadmin -f password

    $ ssoadm register-auth-module -a com.identarian.infocard.opensso.rp.Infocard \
      -u amadmin -f password
    
    Note: The file password (which mode must be read-only for the user
    (i.e. -r--------) constains amAdmin password

    Then you need to modify the iPlanetAMUserService service in order to enable
    the ic-ppid attribute whithin the console. Check the bundled amUser.xml to
    figure out how to modify the service.

    $ ssoadm delete-svc -u amadmin -f password -s iPlanetAMUserService

    And add it back in:

    $ ssoadm create-svc -u amAdmin -f password -X amUser.xml

7 - Modify the LDAP schema using the authnicrp.ldif file

8 - Restart the application server

9 - Point your browser at
    http(s)://my.domain.name:port/opensso/UI/Login?module=Infocard

10 - In order to play with the module, you'll have to install an Information
    Card Identity Selector application. The initial login page provides links 
    for where you can download an Identity Selector as a Firefox extension for 
    Linux, Windows and Mac OS X.
    Windows CardSpace is included with Windows Vista. On Windows XP SP2 and 
    above and Windows Server 2003 SP and above you must install Internet 
    Explorer 7 and .NET Framework 3.0 (both available via Windows Update).
    Most common Information Card extensions for Firefox are DigitalMe and
    xmldap.org and Azigo

Some reference articles:

About the sms.dtd Structure

* http://developers.sun.com/identity/reference/techart/authentication.html
* http://docs.sun.com/source/816-6774-10/prog_service.html#wp19647

For further explanations please email dev@opensso.dev.java.net
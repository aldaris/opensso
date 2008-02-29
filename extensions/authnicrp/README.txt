OpenSSO Authentication Module for Information Cards (RP).

--------------------------------------------------------------------------------
In order to run the build.xml to compile the jar file, create a directory named
lib under the authnicrp root containing the following JAR files
--------------------------------------------------------------------------------

xmldap-1.0.jar:
    The Xmldap.org code can be retrieved from the 'openinfocard' project at
    http://code.google.com/p/openinfocard/
    To build:
        svn checkout http://openinfocard.googlecode.com/svn/trunk/ \
        openinfocard-read-only
        cd openinfocard-read-only/ant
        ant
    Copy xmldap-1.0.jar from openinfocard-read-only/build/xmldap-1.0/ to 
    lib

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

--------------------------------------------------------------------------------
To install the module in opensso we need to follow the instructions outlined 
below
--------------------------------------------------------------------------------

1 - Install OpenSSO (Download from 
    https://opensso.dev.java.net/public/use/index.html). 
    The module has been tested against OpenSSO v1 Builds 2 and 3.

2 - Using the OpenSSO Admin console, navigate to Configuration>Core>Auth Module 
    and type com.identarian.infocard.opensso.rp.Infocard in the 'New Value' 
    field under 'Pluggable Authentication Module Classes', click 'Add', then 
    'Save'.

3 - Copy dist/Authnicrp.jar into <OPENSSO_INSTALL_DIR>/WEB-INF/lib
    <OPENSSO_INSTALL_DIR> on Glassfish will be something like 
    <GLASSFISH_DIR>/domains/domain1/applications/j2ee-modules/opensso/

4 - Copy lib/xmldap-1.0.jar <OPENSSO_INSTALL_DIR>/WEB-INF/lib

5 - Copy source/Infocard.xml into <OPENSSO_INSTALL_DIR>/config/auth/default

6 - Copy source/Infocard.jsp into <OPENSSO_INSTALL_DIR>/config/auth/default

7 - Copy source/Infocard.properties <OPENSSO_INSTALL_DIR>/WEB-INF/classes

8 - Copy images/infocard_71x50.png into <OPENSSO_INSTALL_DIR>/images

9 - Edit the <OPENSSO_INSTALL_DIR>/WEB-INF/classes/Infocard.properties file to 
    define the application server's key store password and alias. On Glassfish 
    V2 which the module has been tested, the default alias is 's1as' and the 
    default password is the application's server admin password (i.e. 
    'adminadmin').
    If you have installed Glassfish via NetBeans 6.0, then the key store 
    password is 'changeit'.
    On Glassfish you can verify the password and alias with keytool -list \
    -storepass changeit -keystore \
    <GLASSFISH_DIR>/domains/domain1/config/keystore.jks

10 - Start a JavaDB (Derby) server on localhost and default port '1527'.
    The module creates the database and tables at startup. The database is used
    to store relations between Information Cards presented by users and the 
    OpenSSO user accounts.

11 - Restart the server

12 - Point your browser at 
    http(s)://my.domain.name:port/opensso/UI/Login?module=Infocard

13 - In order to play with the module, you'll have to install an Information 
    Card Identity Selector application. The initial login page provides links 
    for where you can download an Identity Selector as a Firefox extension for 
    Linux, Windows and Mac OS X.
    Windows CardSpace is included with Windows Vista. On Windows XP SP2 and 
    above and Windows Server 2003 SP and above you must install Internet 
    Explorer 7 and .NET Framework 3.0 (both available via Windows Update).
    Most common Information Card extensions for Firefox are DigitalMe and
    xmldap.org.
    Go to http://www.bandit-project.org/index.php/Digital_Me#Download or
    http://xmldap.org/ to download the application and/or extension.

For further explanations please email dev@opensso.dev.java.net

OpenSSO Authentication Module for Information Cards (RP).

--------------------------------------------------------------------------------
In order to run the build.xml to compile the jar file the following files need
to be in the authnicrp/lib dir:
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
    authnicrp/lib

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
To install the module in opensso we need to follow the instructions outlined below
--------------------------------------------------------------------------------

1 - Install opensso 8.0. The module has been tested against build 2
2 - Using the opensso admin console, enter the class name of the module in the
    Config>Core>Auth Module
3 - Copy dist/InfocardLoginModule.jar into <OPENSSO_INSTALL_DIR>/WEB-INF/lib
4 - Copy libext/xmldap-1.0.jar <OPENSSO_INSTALL_DIR>/WEB-INF/lib
5 - Copy src/Infocard.xml into <OPENSSO_INSTALL_DIR>/config/auth/default
6 - Copy src/Infocard.jsp into <OPENSSO_INSTALL_DIR>/config/auth/default
7 - Copy src/Infocard.properties <OPENSSO_INSTALL_DIR>/WEB-INF/classes
8 - Copy images/infocard_71x50.png into <OPENSSO_INSTALL_DIR>/images
9 - Edit the <OPENSSO_INSTALL_DIR>/WEB-INF/classes/Infocard.properties file to define
    the application server's key store password and alias. On Glassfish V2 which the
    module has been tested, the default alias is 's1as' and the default password is
    the application's server admin password (i.e. 'adminadmin')
10 - Start a JavaDB (derby) server on localhost and default port '1527'.
    The module creates the database and tables at startup. The database is used
    to store relations between Information Cards presented by users and the opensso
    user accounts.
11 - Restart the server
12 - Point your browser at https://my.domaine.name:port/opensso/UI/Login?module=Infocard
13 - In order to play with the module, you'll have to install an Information Card Identity
    selector application. The initial login page provides links for where you can download
    an Identity Selector as a Firefox extension for Linux, Windows and Mac OS X.
    On Windows Vista, Information Card support (known as Cardspace or Infocard)
    comes out-of-the-box. Support for Windows XP is also possible. See Microsoft
    documentation for details.
    Most common Information Card extensions for Firefox are DigitalMe and
    Xmldap.org.
    Go to http://www.bandit-project.org/index.php/Digital_Me#Download or
    http://xmldap.org/ to download the application and / or extension.

For further explanations you can contact me at Patrick.Michel.Petit@gmail.com

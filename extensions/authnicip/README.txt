OpenSSO server for Information Cards (IDP). This server hosts OpenSSO STS 
(Security Token Service) and Information Card issuing servlet.

--------------------------------------------------------------------------------
In order to run the build.xml to compile the jar file, create a directory named
lib under the Authnicip root containing the following JAR files :
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
opensso-sharedlib.jar
openfedlib.jar
fam.jar
webservices-api.jar
webservices-rt.jar
    The above files can be retrieved from the <OPENSSO_INSTALL_DIR>/WEB-INF/lib

servlet.jar:
    The three names I have seen for servlet.jar, depends on the server
    (servlet.jar, servlet-api.jar, javaee.jar). If you have a Servlet Container,
    it will be somewhere in the container's classpath, (Tomcat = common/lib 
    directory)
    Otherwise, look into downloading Tomcat, or look for JAVA EE 1.5 SDK and for an
    implementation from Sun Microsystems.

--------------------------------------------------------------------------------
To install Security Token Service and Information Card issuing servlet
in opensso we need to follow the instructions outlined below :
--------------------------------------------------------------------------------

1 - Install OpenSSO (Download from 
    https://opensso.dev.java.net/public/use/index.html). 
    Take latest OpenSSO v1 Build.

2 - Copy contents of xml/web.xml into <OPENSSO_INSTALL_DIR>/WEB-INF/web.xml in
    <context-param> section. 
    Change "path to sun_logo_rgb.gif" to appropriate image path on the server.
    <OPENSSO_INSTALL_DIR> on Glassfish will be something like 
    <GLASSFISH_DIR>/domains/domain1/applications/j2ee-modules/opensso/

3 - Copy build/dist/Authnicip.jar into <OPENSSO_INSTALL_DIR>/WEB-INF/lib

4 - Copy lib/xmldap-1.0.jar into <OPENSSO_INSTALL_DIR>/WEB-INF/lib

5 - Copy your keystore.jks file into <OPENSSO_INSTALL_DIR>/WEB-INF/template/keystore
    Make sure your keypass and storepass is "secret" and certificate alias is "test".

6 - Copy all files under images/ into <OPENSSO_INSTALL_DIR>/images

7 - Edit the <OPENSSO_INSTALL_DIR>/WEB-INF/wsdl/famsts.wsdl file to define
    - Required binding type at "<sp:SymmetricBinding>"
    - Required Authentication token at "<sp:SignedSupportingTokens>"
    - Required Validation configuration at "<sc:ValidatorConfiguration>"
    Take help from wsdl/famsts.wsdl

8 - Configure OpenSSO web application with "https" protocol.

9 - Restart the OpenSSO server

10 - Access http(s)://my.domain.name:port/opensso/GetCard
    This will redirect to OpenSSO Authentication service. Authenticate using
    any existing OpenSSO user. Upon successful authentication, InfoCard for 
    authenticated OpenSSO user will be issued. 
    Save this card file with .crd extension. 

11 - You can use this saved OpenSSO InfoCard to login to any RP 
    (e.g. https://xmldap.org/) which accepts Infocard login.

For further explanations please email dev@opensso.dev.java.net

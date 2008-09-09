README
======

This README explains the installation and configuration of a custom 
authentication module for OpenSSO in a 
ClearTrust environment. The OpenSSO custom authentication enables the SSO
integration between RSA ClearTrust (CT) access server and Federation
Access Manager especially when the deployment contains CT for protecting
existing applications.  


Pre-requisites :
================

   1. ClearTrust server 
   2. ClearTrust SDK ct_runtime_api.jar
   3. opensso.war

Required SSO integration components:
===================================

   1. OpenSSO stable build
   2. A web container preferably Sun web container.
   3. Custom authentication module components which is in the config directory


OpenSSO Installation and Configuration:
=======================================

  0. Build ctauthmodule.jar

  1. Create a temporary directory /export/tmp and download opensso.war build2
     and unwar the opensso.war using jar -xvf opensso.war. 
     From now on, /export/tmp is called as a war staging area and is 
     represented with a marco $WAR_DIR 

  2. Copy ctauthmodule.jar to $WAR_DIR/WEB-INF/lib

  3. Copy config/CTAuth.properties to $WAR_DIR/WEB-INF/classes 

  4. Copy config/CTAuthModule.xml to $WAR_DIR/config/auth/default and
     also to the directory WAR_DIR/config/auth/default_en

  5. Re-war opensso.war using jar cvf opensso.war from $WAR_DIR

  6. Deploy opensso.war onto a web container. The deployment is self
     explanatory. Please check the web container documentation for war
     deployment.

  7. Access the deployed application using
      http://<host>:<port>/opensso

  8. Accessing deployed application redirects to opensso configurator.
     Choose custom configuration. By default OpenSSO uses embedded directory
     server for configuration, however, you could choose to use existing
     or a new directory server instance for configuration. 

     Note: The federation access manager can be configured to use various 
     user repository for validating the user existance, however, you 
     could also choose to ignore profile. 
  
  9. After successful configuration, the configuration redirects to a user
     login and verify your administrator credentials. 

Auth module configuration:
==========================

Now we have to load the ClearTrust authentication module service into 
Open SSO and configure for the SSO integration. The auth module service
is loaded from a OpenSSO command line utility called as "ssoadm". For OpenSSO,
the ssoadm utitily is exposed in both console mode and browser based
interfaces.  Here we will use use browser based ssoadm for OpenSSO configuration
changes.

  1. Login into OpenSSO using amadmin

  2. Now access the following URL
     http://<host>:<port>/opensso/ssoadm.jsp

  3. Choose create-service option. 

  4. Copy and paste the xml file from authmodule/smauthservice.xml and Submit
     This will load the auth module service into OpenSSO configuration.

  5. Now register the auth module into the authentication core framework. 

     http://<host>:<port>/opensso/ssoadm.jsp
     Choose register-auth-module option.
     Enter "com.sun.identity.authentication.cleartrust.CTAuthModule" as the
     auth module class name.

  6. Now verify that the auth module is registered to the default realm.
     http://<host>:<port>/opensso, click on default realm, and click on
     "authentication" tab, you should see "CTAuth" as the registered
     auth module.

  7. Click on CTAuth auth module
  
  8. CT Auth params are self explanatory and does not need to be changed.  

     Configure as appropriate and save the configuration.


Testing:
=======

The testing assumes that cleartrust SDK is already
installed and configured. Please check the Cleartrust documentation
for cleartrust SDK installation.

1. Copy ct_runtime_api.jar into $WAR_DIR/WEB-INF/lib directory

2. Restart the OpenSSO web container 

3. Now access the Cleartrust protected application and login with
   Cleartrust configured user to establish CTSESSION. The configuration
   of Cleartrust policy and authentication schemes are outside scope of this
   documentation and please check cleartrust documentation for more 
   information. 

4. After successful authentication at Cleartrust server, access the OpenSSO
   auth module url as follows:
  
   http://<host>:<port>/opensso/UI/Login?module=CTAuth

   This should provide a valid OpenSSO session.

   Note: Assumption here is that Cleartrust and OpenSSO are in the same 
         physical domain. 

   By default OpenSSO authentication framework looks for user profile existance
   in it's known data repositories. However, you could use ignoreProfile
   option if your integration does not require a user to be searched from
   cleartrust's user repository. Check the OpenSSO documentation for more info
   about ignoreProfile option.


SAML2 Integration:
=================

When OpenSSO is acting as an IDP,  the SAML2 metadata can be configured to
use a configured login url for authentication purposes to establish
OpenSSO session. The login url is configurable through SAML2 extended metadata
and the login URL has to be in this case as below:

      http://<host>:<port>/opensso/UI/Login?module=CTAuth


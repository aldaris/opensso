<!--
    Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved
    Use is subject to license terms.
-->

------------------------------------
J2EE Policy Agent Sample Application
------------------------------------

This document describes how to use the agent sample application in conjunction 
with the Application Server.

    * Overview
    * Compiling and Assembling the Application
    * Deploying the Sample Application
    * Running the Sample Application
    * Troubleshooting
    * Post Deployment Agent tasks


Overview
--------
The sample application is a collection of servlets, JSPs and EJB's that 
demonstrate the salient features of the J2EE policy Agent. These features 
include SSO, web-tier declarative security, programmatic security, URL policy 
evaluation and session/policy/profile attribute fetch.
The sample application is supported for 22 Policy Agent.


Compiling and Assembling the Application (Optional)
---------------------------------------------------

The application is already built and ready to be deployed, so you could skip the
current section. If you want to change something or get familiar with the build
details, then this section is useful.

This section contains instructions to build and assemble the sample application 
using a Command Line Interface (CLI).

To rebuild the entire application from scratch, follow these steps:

   1. Set your JAVA_HOME and CLASSPATH to JDK1.5 or above.
      Set WL_HOME to the weblogic home directory, e.g./usr/local/bea/wlserver_10.0. 

   2. Replace 'APPSERV_LIB_DIR' in build.xml with the directory where 
      weblogic.jar is located.  
      For Example: Replace APPSERV_LIB_DIR with 
      /usr/local/bea/wlserver_10.0/server/lib where /usr/local/bea/wlserver_10.0 is 
      the WL_HOME directory.

   3. Compile and assemble the application. 
      For example: execute the command <weblogic_install_home>/server/bin/ant 
      under <agent_install_root>/sampleapp/ to execute the default target build 
      and rebuild the EAR file. 
      The build target creates a built and dist directory with the EAR file.

      Note that you can also run 'ant rebuild' to clean the application project 
      area and run a new build.

Now you are ready to use the dist/agentsample.ear file for deployment.


Deploying the Sample Application
--------------------------------
To deploy the application, do the following:

Goto BEA WebLogic server console and deploy the application.


Post Deployment Agents Tasks
----------------------------

1. UUID to principal mapping in AMAgent.properties file:

    By default, the installation assume that the Access Manager product was
    under default Org/Realm "dc=iplanet,dc=com". If the Org/Realm for the
    deployment scenario is different from the default root suffix, the Universal
    Id (UUID) for the role/principal mappings should be changed accordingly. The
    Universal Id can be obtained by using the agentadmin --getUuid command.

    For WebLogic 10.0, an UUID has to be mapped to a value of type NMTOKEN, 
    and then the mapped value is used in weblogic-ejb-jar.xml and weblogic.xml 
    files as <principal-name> element value. The mapping is specified in 
    AMAgent.properties file by the property 

                com.sun.identity.agents.config.privileged.attribute.mapping[].

    Make sure the keys in the mapping are UUIDs corresponding to your Access
    Manager installation; There is no need to change the values. Now uncomment 
    out the mappings because by default they are commented out.

    The WebLogic server needs to be restarted.

    
2. On Access Manager admin console, create the following URL Policies:

    * Policy 1:
          o allow:
                + http://<hostname>:<port>/agentsample/jsp/*
                + http://<hostname>:<port>/agentsample/invokerservlet
                + http://<hostname>:<port>/agentsample/protectedservlet
                + http://<hostname>:<port>/agentsample/securityawareservlet
                + http://<hostname>:<port>/agentsample/unprotectedservlet
          o Subject: entire organization
    * Policy 2:
          o allow:
                + http://<hostname>:<port>/agentsample/urlpolicyservlet
          o Subject: LDAP Group: customer


3. Create the following users:

    * andy/andy
    * bob/bob
    * chris/chris
    * dave/dave
    * ellen/ellen
    * frank/frank
    * gina/gina


4. Assign Users to Roles

    * employee:
          o andy, bob, chris, dave, ellen, frank
    * manager:
          o andy, bob, chris
    * everyone:
          o andy, bob, chris, dave, ellen, frank, gina

5. LDAP Group Setup:

    * customer:
          o chris, ellen

6. Modify the following properties in the AMAgent.properties file:

    * Not enforced List:
          o com.sun.identity.agents.config.notenforced.uri[0] = /agentsample/public/*
          o com.sun.identity.agents.config.notenforced.uri[1] = /agentsample/images/*
          o com.sun.identity.agents.config.notenforced.uri[2] = /agentsample/styles/*
          o com.sun.identity.agents.config.notenforced.uri[3] = /agentsample/index.html
          o com.sun.identity.agents.config.notenforced.uri[4] = /agentsample
    * Access Denied URI:
          o com.sun.identity.agents.config.access.denied.uri = 
			/agentsample/authentication/accessdenied.html
    * Form List:
          o com.sun.identity.agents.config.login.form[0] = 
                        /agentsample/authentication/login.html



Running the Sample Application
----------------------------
You can run the application through the following URL:

http://<hostname>:<port>/agentsample

Traverse the various links to understand each agent feature.


Troubleshooting
----------------------------
If you encounter problems when running the application, review the log files to 
learn what exactly went wrong. J2EE Agent logs can be found at 
<agent_install_root>/<agent_instance>/logs/debug directory.

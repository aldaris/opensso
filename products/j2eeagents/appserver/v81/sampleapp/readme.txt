<!--
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

   $Id: readme.txt,v 1.5 2008-03-07 23:23:48 huacui Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
-->

------------------------------------
J2EE Policy Agent Sample Application
------------------------------------

This document describes how to use the agent sample application in conjunction with the 
Application Server.

    * Overview
    * Compiling and Assembling the Application
    * Deploying the Sample Application
    * Post Deployment Agent tasks
    * Running the Sample Application
    * Troubleshooting


Overview
--------
The sample application is a collection of servlets, JSPs and EJB's that demonstrate the salient 
features of the J2EE policy Agent. These features include SSO, web-tier declarative security, 
programmatic security, URL policy evaluation and session/policy/profile attribute fetch. 
The sample application is supported for Open Source Policy Agent and OpenSSO Server only.


Compiling and Assembling the Application
----------------------------------------
This section contains instructions to build and assemble the sample application using a Command 
Line Interface (CLI).

To build the entire application from scratch, follow these steps:

   1. Set your JAVA_HOME and CLASSPATH to JDK1.4 or above.
   2. Replace 'APPSERV_LIB_DIR' in build.xml with the directory where j2ee.jar is located.
      For example: replace APPSERV_LIB_DIR with /opt/SUNWappserver/appserver/lib   where 
      /opt/SUNWappserver/appserver is your <appserver_install_root>. 
   3. Compile and assemble the application. 
      For example: execute the command <appserver_install_root>/bin/asant 
      under <agent_install_root>/sampleapp/ to execute the default target build and rebuild the EAR file. 
      The build target creates a built and dist directory with the EAR file. 
      By default, the Application server specific deployment descriptors assume 
      that the OpenSSO Server product was installed under default Org/Realm 
      "dc=opensso,dc=java,dc=net". If the Org/Realm for the deployment scenario is 
      different from the default root suffix, the Universal Id(uuid) for the role/principal 
      mappings should be changed accordingly.  The Universal Id can be obtained 
      from the OpenSSO/FAM server console.
   4. Deploy the application. After you have re-created the sample application from scratch, you may 
      proceed directly to Deploying the Sample Application, or optionally perform step 3.
   5. Optionally you can run 'ant rebuild' to clean the application project area and run a 
      new build.

Now you are ready to use the dist/agentsample.ear file for deployment.


Deploying the Sample Application
--------------------------------
To deploy the application, do the following:

Execute the command asadmin deploy
<appserver_install_root/bin/asadmin deploy --user <username> --password <password> 
				<agent_install_root>/sampleapp/dist/agentsample.ear


Verifying Deployment
--------------------

As an optional step, you can use the Application Server Administration Tool to verify that the 
application has been registered. Otherwise, proceed directly to Running the Sample Application.

To verify the registration of the application:

   1. Execute the command asadmin list-components to look at applications deployed with a server instance. 
      For example:
      asadmin list-components --user <username> --password <password> 
      You will see agentsample listed.

   2. Alternately, use Admin Console and navigate to Applications > Web Applications. You will 
      see agentsample listed.


Post Deployment Agents Tasks
----------------------------
This agent sample application requires that the Access Manager server is
configured with the subjects and policies required by the sample application.

1. Create the following users:
   Here is the following list of users with username/password :

    * andy/andy
    * bob/bob
    * chris/chris
    * dave/dave
    * ellen/ellen
    * frank/frank
    * gina/gina


2. Assign Users to Groups
   Create new groups for employee, manager, everyone, and customer. Then assign 
   the users to the groups as follows:

    * employee:
          o andy, bob, chris, dave, ellen, frank
    * manager:
          o andy, bob, chris
    * everyone:
          o andy, bob, chris, dave, ellen, frank, gina
    * customer:
          o chris, ellen


3. Create the following URL Policies:

    * Policy 1:
          o allow:
                + http://<hostname>:<port>/agentsample/jsp/*
                + http://<hostname>:<port>/agentsample/invokerservlet
                + http://<hostname>:<port>/agentsample/protectedservlet
                + http://<hostname>:<port>/agentsample/securityawareservlet
                + http://<hostname>:<port>/agentsample/unprotectedservlet
          o Subject: entire organization which is all authenticated users.

    * Policy 2:
          o allow:
                + http://<hostname>:<port>/agentsample/urlpolicyservlet
          o Subject: Group: customer



4. Modify the following properties in the AMAgent.properties file:

    * Not enforced List:
          o com.sun.identity.agents.config.notenforced.uri[0] = /agentsample/public/*
          o com.sun.identity.agents.config.notenforced.uri[1] = /agentsample/images/*
          o com.sun.identity.agents.config.notenforced.uri[2] = /agentsample/styles/*
          o com.sun.identity.agents.config.notenforced.uri[3] = /agentsample/index.html
          o com.sun.identity.agents.config.notenforced.uri[4] = /agentsample
          o com.sun.identity.agents.config.notenforced.uri[5] = /agentsample/

    * Access Denied URI:
          o com.sun.identity.agents.config.access.denied.uri = /agentsample/authentication/accessdenied.html
    * Form List:
          o com.sun.identity.agents.config.login.form[0] = /agentsample/authentication/login.html



Running the Sample Application
----------------------------
You can run the application through the following URL:

http://<hostname>:<port>/agentsample

Traverse the various links to understand each agent feature.


Troubleshooting
----------------------------
If you encounter problems when running the application, review the log files to learn what exactly 
went wrong. Application server log files are located at 
<install_root>/domains/<domain_name>/logs/server.log and the J2EE Agent logs can be found 
at <agent_install_root>/agent_<instance_number>/logs/debug directory.



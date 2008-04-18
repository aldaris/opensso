---------------------------------------------
README file for executing QA test on OpenSSO
---------------------------------------------
#
# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the License). You may not use this file except in
# compliance with the License.
#
# You can obtain a copy of the License at
# https://OpenSSO.dev.java.net/public/CDDLv1.0.html or
# OpenSSO/legal/CDDLv1.0.txt
# See the License for the specific language governing
# permission and limitations under the License.
#
# When distributing Covered Code, include this CDDL
# Header Notice in each file and include the License file
# at OpenSSO/legal/CDDLv1.0.txt.
# If applicable, add the following below the CDDL Header,
# with the fields enclosed by brackets [] replaced by
# your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# $Id: README.txt,v 1.3 2008-04-18 19:28:47 nithyas Exp $
#
# Copyright 2007 Sun Microsystems Inc. All Rights Reserved
#
#
%% Contents:
    %% 1 Setup requirements
    %% 2 Execution
    %% 3 Post execution
    %% 4 Authentication module test execution
    %% 5 Issues

%% 1 Setup requirements

1.1 QA test frmework uses Apache Ant for driving the setup. You need to have 
Ant version 1.6.5 or higer setup on your system and available in your system
classpath.
1.2 Go to http://testng.org/doc/download.html and download version 5.1 of 
testng. Unzip the downloaded zip file. Copy testng-5.1-jdk15.jar to 
QATEST_HOME/lib 
1.3 Go to http://htmlunit.sourceforge.net/. Click on Download. Download 
Release 1.14. Unzip the downloaded zip file. Copy all the jars under lib
from unzipped file to QATEST_HOME/lib/htmlunit.
NOTE: If you are updating jars, make sure the old jars are removed.

%% 2 Execution

2.1 Check out the workspace. QATEST_HOME=<Checkout directory>

2.2 Framework acts as a client program to amserver. Hence it needs servlet.jar 
in its classpath. servlet.jar (j2ee 1.5 complaint) is bundled with the product 
and resides under the QATEST_HOME/lib directory. To use any other servlet.jar, 
just replace this jar (backup the original)

2.3 Copy amclientsdk.jar under QATEST_HOME/lib directory

2.4 Set 5 variables in QATEST_HOME/build.properties file:
2.4.1 OPENSSO_HOME - where OpenSSO product is installed
2.4.2 QATEST_HOME – home directory for qatest code base
2.4.3 EXECUTION_MODE - what tests we want to execute (server, server-smoke, 
clientsdk, the list can go on; these are group names used in Java 
implementation)
2.4.4 REPORT_DIR – In case the report need to be copied to a NFS location
2.4.5 TEST_MODULE - for testing an individual module, the test module name

2.5 Open AMClient.properties file under QATEST_HOME/resources and set the 
following properties: (This file contains all the properties that are common 
across test modules)
2.5.1 qatest.amadmin.user=<superuser name>
2.5.2 qatest.amadmin.password=<superuser password>
2.5.3 qatest.realm=<the realm to execute the tests for>
2.5.4 qatest.log.level=<the log level used by framework to log debug> 
2.5.5 com.iplanet.am.service.password=<superuser password>
2.5.5 com.iplanet.am.defaultOrg=<default root orqanization dn>
statements)
2.5.6 All the properties marked with value as @COPY_FROM_CONFIG@, have their 
values picked from the configuration AMConfig.properties

2.6 Properties required exclusively by any test modules are defined in their 
respective testing.xml files. These files sit under QATEST_HOME/xml/testng.

2.7 To execute all the testcases in the qatest framework,  go to QATEST_HOME 
and enter "ant all".

2.8 To execute all the testcases in the a module,  go to QATEST_HOME and enter 
"ant module".

9.The compiled classes are placed under 
QATEST_HOME/build/classes/com/sun/identity/qatest/<test module>

%% 3 Post Execution

3.1 The reports are placed under QATEST_HOME/docs/reports/<EXECUTION_MODE>/
${hostname}_${os.arch}_${osname}_${timestamp}

3.1 All the debug output goes into:
3.1.1 <execution mode>.output (this is for the global report scenario i.e. 
when all test modules are executed)
3.2.2 <test module>.output (this is for the module report scenario)
3.3.3 All the output files for a particular execution are under QATEST_HOME/
docs/reports/<EXECUTION_MODE>/${hostname}_${os.arch}_${osname}_${timestamp}

3.2 A file called test_env.txt is also created. This lists all the enviornment 
variables and parameters used for execution. This too sits under 
QATEST_HOME/docs/reports/<EXECUTION_MODE>/
${hostname}_${os.arch}_${osname}_${timestamp}

3.3 All the files under QATEST_HOME/docs/reports/<EXECUTION_MODE>/
${hostname}_${os.arch}_${osname}_${timestamp} and test_env.txt are copied 
under the REPORT_DIR, if REPORT_DIR is specified in 
QATEST_HOME/build.properties file.

3.4 A file called logs is generated undet QATEST_HOME. This contains all the 
output from the debug statements set in the source code.

%% 4 Authentication module test execution

4.1 Go to QATEST_HOME/xml/authentication and configure the attribute value for the give modules,
in their respective data files. For eg for LDAP auth module, the datafile name is LDAPTestData.
This contains attributes and values, which are required to create the module instance.

4.2 Go to QATEST_HOME/resources/authentication and configure the values defined in the
authenticationTest.properties file.

%% 5 Issues

5.1 Current authentication module testcase execution overwrites the reports generated using testng.
This is an issue in testng and we are working to resolve it. Hence the report shows execution
results for the last executed authentication module.

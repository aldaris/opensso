#!/bin/sh
#
#------------------------------------------------------------------------------
#README file for Federated Access Manager stand alone client sdk samples
#------------------------------------------------------------------------------
#The contents of this file are subject to the terms
#of the Common Development and Distribution License
#(the License). You may not use this file except in
#compliance with the License.
#
#You can obtain a copy of the License at
#https://opensso.dev.java.net/public/CDDLv1.0.html or
#opensso/legal/CDDLv1.0.txt
#See the License for the specific language governing
#permission and limitations under the License.
#
#When distributing Covered Code, include this CDDL
#Header Notice in each file and include the License file
#at opensso/legal/CDDLv1.0.txt.
#If applicable, add the following below the CDDL Header,
#with the fields enclosed by brackets [] replaced by
#your own identifying information:
#"Portions Copyrighted [year] [name of copyright owner]"
#
#$Id: run-xacml-client-sample.sh,v 1.6 2008-01-25 01:55:24 dillidorai Exp $
#Copyright 2007 Sun Microsystems Inc. All Rights Reserved
#------------------------------------------------------------------------------
#
#Runs the xacml client sample program
#
#constructs a xacml-context:Request 
#makes XACMLAuthzDecisionQuery to PDP,
#receives XACMLAuthzDecisionStatement
#prints out xacml-context:Response
#
#Requires one parameter: the name of the  resource file that defines
#property values used by the sample
#Default is xacmlClientSamples
#The corresponding file that would be read from classpath 
#is xacmlClientSample.properites 
#from classpath.
#A default template is included 
#at ../resources/xacmlClientSample.properties
#See the template for more information on the properties
#Please update it to match your deployment
#You have to create user and policy at PDP to get right policy decision
#see ../resources/xacmlClientSample.properties for more information
#
#Requires ../resources/AMConfig.properties 
#Must run "setup.sh" once to configure the client to find the FAM server, this
#is referred as PEP host below. Modify AMConfig.properties, set value of
#"com.sun.identity.agents.app.username" property to "amadmin", set value of 
#"com.iplanet.am.service.password" property to the amadmin password if it is 
#different from the password entered when running setup.sh command. 
#
#Setting up PDP FAM and PEP FAM
#
# At PDP host, that is the host that would run the FAM acting as PDP.
# We would call this PDP FAM. At PDP host, do the following:
#
# deploy fam.war  and configure it on a supported java ee container
# using FAM console, Configuration > SAMLv2 SOAP Binding,set soap handler, 
# key=/xacmlPdp|class=com.sun.identity.xacml.plugins.XACMLAuthzDecisionQueryHandler
#
# unzip famAdminTools.zip and setup FAM admin tools
# fam/bin/famadm create-cot -t xacml-pdp-cot -u amadmin -f <password_file>
# fam/bin/famadm create-metadata-templ -y xacmlPdpEntity -p /xacmlPdp -m xacmlPdp.xml -x xacmlPdp-x.xml -u amadmin -f <password_file>
# fam/bin/famadm import-entity -t xacml-pdp-cot -m xacmlPdp.xml -x xacmlPdp-x.xml -u amadmin -f <password_file>
#
#
# At PEP host, that is the host that would run the FAM acting as PEP metadata
# repository, do
#
# deploy fam.war  and configure it on a supported java ee container
#
# unzip famAdminTools.zip and setup FAM admin tools 
# fam/bin/famadm create-cot -t xacml-pep-cot -u amadmin -f <password_file>
# fam/bin/famadm create-metadata-templ -y xacmlPepEntity -e /xacmlPep -m xacmlPep.xml -x xacmlPep-x.xml -u amadmin -f <password_file>
# fam/bin/famadm import-entity -t xacml-pep-cot -m xacmlPep.xml -x xacmlPep-x.xml -u amadmin -f <password_file>
#
# copy xacmlPdp.xml from PDP host as  xacmlPdp-r.xml to PEP host, do
# fam/bin/famadm import-entity -t xacml-pep-cot -m xacmlPdp-r.xml -u amadmin -f <password_file>
#
#
# At PDP host, do the following:
# copy xacmlPep.xml from PEP host as xacmlPep-r.xml to PDP host
# fam/bin/famadm import-entity -t xacml-pdp-cot -m xacmlPep-r.xml -u amadmin -f <password_file>
#
# Then, run this script
java -classpath resources:lib/openssoclientsdk.jar:lib/j2ee.jar:lib/jaxb-libs.jar:lib/jaxb-impl.jar:lib/webservices-rt.jar:classes samples.xacml.XACMLClientSample xacmlClientSample


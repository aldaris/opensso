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
#$Id: run-xacml-client-sample.sh,v 1.1 2007-08-30 08:08:03 dillidorai Exp $
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
#
#Requires ../resources/AMConfig.properties 
#and ../resources/FederationConfig.properites
#they should be pointing as a client to the FAM that hosts
#PEP Metadata
#Default templates have been provided at ../resources.
#Please update them to match your deployment
#
#Setting up PDP FAM and PEP FAM
#
# deploy fam.war  and configure on PDP host on a java ee container
# using FAM console, Configuration > SAMLv2 SOAP Binding,set soap handler, 
# key=/xacmlPdp|class=com.sun.identity.xacml.plugins.XACMLAuthzDecisionQueryHandler
#
# unzip famAdminTools.zip and setup FAM admin tools on the PDP host
# fam/bin/famadm create-circle-of-trust -u amadmin -f <password_file> -t xacml-pdp-cot
# fam/bin/famadm create-metadata-template -u amadmin -f <password_file> -y xacmlPdpEntity -p /xacmlPdp -m xacmlPdp.xml -x xacmlPdp-x.xml
# fam/bin/famadm import-entity -u amadmin -f <password_file> -t xacml-pdp-cot -m xacmlPdp.xml -x xacmlPdp-x.xml
#
# deploy fam.war  and configure on the host that would act as metadata
# repository for PEP. We would call this PEP FAM.
#
# unzip famAdminTools.zip and setup FAM admin tools on the PDP host
# fam/bin/famadm create-circle-of-trust -u amadmin -f <password_file> -t xacml-pep-cot
# fam/bin/famadm create-metadata-template -u amadmin -f <password_file> -y xacmlPepEntity -e /xacmlPep -m xacmlPep.xml -x xacmlPep-x.xml
# fam/bin/famadm import-entity -u amadmin -f <password_file> -t xacml-pep-cot -m xacmlPep.xml -x xacmlPep-x.xml
#
# copy xacmlPdp.xml from PDP host as  xacmlPdp-r.xml
# At PDP host
# fam/bin/famadm import-entity -u amadmin -f <password_file> -t xacml-pep-cot -m xacmlPdp-r.xml
#
# get to xacmlPep.xml as xacmlPep-r.xml to PDP host
# At PDP host
# fam/bin/famadm import-entity -u amadmin -f <password_file> -t xacml-pdp-cot -m xacmlPep-r.xml
#
java -classpath ../resources:../lib/famclientsdk.jar:../lib/javaee.jar:../lib/jaxb-libs.jar:../lib/jaxb-impl.jar:../lib/webservices-rt.jar:../classes samples.xacml.XACMLClientSample xacmlClientSample


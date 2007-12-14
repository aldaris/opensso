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
#$Id: compile-samples.sh,v 1.6 2007-12-14 00:09:05 dillidorai Exp $
#Copyright 2007 Sun Microsystems Inc. All Rights Reserved
#------------------------------------------------------------------------------
#
javac -classpath resources:lib/openssoclientsdk.jar:lib/j2ee.jar:lib/jaxb-libs.jar:lib/jaxb-impl.jar:lib/webservices-rt.jar  -d classes source/samples/xacml/*.java source/com/sun/identity/samples/clientsdk/idrepo/*.java source/com/sun/identity/samples/clientsdk/logging/*.java source/com/sun/identity/samples/sso/*.java source/com/sun/identity/samples/authentication/*.java source/samples/policy/*.java

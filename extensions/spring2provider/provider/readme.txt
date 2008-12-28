------------------------------------------------------------------------------
README file for Spring Security 2.0 Provider
------------------------------------------------------------------------------
   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
  
Copyright (c) 2006 Sun Microsystems Inc. All Rights Reserved
  
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
"Portions Copyrighted 2008 Miguel Angel Alonso Negro <miguelangel.alonso@gmail.com>"

$Id: readme.txt,v 1.2 2008-12-28 21:40:27 malonso Exp $

------------------------------------------------------------------------------


Integration between OpenSSO agent and Spring Security 2.0
=========================================================

This component is the implementation of Spring Security using the OpenSSO platform.
The target is to configure the security module of a web application with Spring
Security but the implementation is according OpenSSO. So we can get:

 - SSO authentication, 
 - Authoritation based on policies defined in OpenSSO for the security domain

In the directory where this file is, you can find a maven 2 project. But the dependency 
of openssoclientsdk, at the moment, is not found in any maven  repository. Therefore, 
you must include in your local repository:

{dir maven repo}/com/sun/identity/openssoclientsdk/8.0/openssoclientsdk-8.0.jar.

Then, you can execute

    $ mvn install
    
And the provider will be located in 

{dir maven repo}/com/sun/identity/provider/springsecurity/0.1/springsecurity-0.1.jar

to be used in maven projects. (See more details in http://maven.apache.org)

The provider consists on the following classes:

    - com.sun.identity.provider.springsecurity.OpenSsoObjectDefinitionSource.- 
    It is in charge of getting the security policies defined for a resource and an
    userby web service of opensso.war application.
    
    - com.sun.identity.provider.springsecurity.OpenSsoAuthenticationProvider.- 
    Implementation of Spring AuthenticationProvider with OpenSSO struts

    - com.sun.identity.provider.springsecurity.OpenSsoProcessingFilter.-
    Implementation of filter which is responsible for processing authentications.

    - com.sun.identity.provider.springsecurity.OpenSsoProcessingFilterEntryPoint.-
    It is in charge of validating the cookie which says if the user is logged in and
    getting its credentials. This class is the basis of Single-Sign On implementation

    - com.sun.identity.provider.springsecurity.OpenSsoLogoutHandler.- It is in charge 
    of doing the logout in the application and in every application where the user
    logged via Single sign-on.

    - com.sun.identity.provider.springsecurity.OpenSsoVoter.- It is in charge of 
    translating the action decisions to votes. The votes are interpreted by the
    AccessDecisionManager (class of Spring Security) to know if the user has privileges
    for a resource.
    This is the weak spot because OpenSSO has a lot of ways to define policies and 
    it is necessary an implementation of AccessDecisionVoter for each way.
    
In the subversion URL:

https://opensso.dev.java.net/source/browse/opensso/extensions/spring2provider/example/

you can find an example web application. See readme.txt file for details.


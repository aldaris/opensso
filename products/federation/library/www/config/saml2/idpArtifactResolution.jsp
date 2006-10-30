<%--
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

   $Id: idpArtifactResolution.jsp,v 1.1 2006-10-30 23:17:20 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>





<%@page
import="com.sun.identity.saml2.profile.IDPArtifactResolution,
com.sun.identity.saml2.common.SAML2Utils"
%>

<%
    // check request, response
    if ((request == null) || (response == null)) {
	response.sendError(response.SC_BAD_REQUEST,
			SAML2Utils.bundle.getString("nullInput"));
	return;
    }

    /*
     * This call handles the artifact resolution request
     * from a service provider. It processes the artifact
     * resolution request sent by the service provider and
     * sends a proper SOAPMessage that contains an Assertion.
     */
    IDPArtifactResolution.doArtifactResolution(request, response);
    out.clear();
    out = pageContext.pushBody();
%>

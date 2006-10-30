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

   $Id: spMNISOAP.jsp,v 1.1 2006-10-30 23:17:23 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>




<%@ page import="com.sun.identity.shared.debug.Debug" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Constants" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Utils" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Exception" %>
<%@ page import="com.sun.identity.saml2.profile.DoManageNameID" %>
<%@ page import="java.util.HashMap" %>

<%--
    spMNIHttpReqProcess.jsp processes the ManageNameIDRequest from
    the Identity Provider with SOAP binding.
    Required parameters to this jsp are : NONE
--%>
<%
    try {
        HashMap paramsMap = new HashMap();
        paramsMap.put(SAML2Constants.ROLE, SAML2Constants.SP_ROLE);
        DoManageNameID.processSOAPRequest(request, response, paramsMap);
    } catch (SAML2Exception e) {
        SAML2Utils.debug.error("Error processing ManageNameIDRequest " , e);
        response.sendError(response.SC_BAD_REQUEST,
                SAML2Utils.bundle.getString("requestProcessingMNIError"));
    } catch (Exception e) {
        SAML2Utils.debug.error("Error processing Request ",e);
        response.sendError(response.SC_BAD_REQUEST,
                SAML2Utils.bundle.getString("requestProcessingMNIError"));
    }
    out.clear();
    out = pageContext.pushBody();
%>

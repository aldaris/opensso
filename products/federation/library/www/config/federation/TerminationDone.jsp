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

   $Id: TerminationDone.jsp,v 1.1 2006-10-30 23:17:18 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>





<%@ page language="java"
import="com.sun.liberty.LibertyManager"
%>
<html>
<head>
<title>Sun Java System Access Manager(Termination Status)</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../fed_css/styles.css" type="text/css">
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="9" marginwidth="9"
    topmargin="9" marginheight="9" >
<br>
<%@ include file="Header.jsp"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

   <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>
<%
	// Alias processing
	String providerAlias =
                request.getParameter(LibertyManager.getMetaAliasKey());
	if (providerAlias == null || providerAlias.length() < 1)
	{
		response.sendError(response.SC_INTERNAL_SERVER_ERROR,
			"Provider Alias not found");
		return;
	}
	String providerId = LibertyManager.getEntityID(providerAlias);
        String providerRole = LibertyManager.getProviderRole(providerAlias);
	String HOME_URI = "";
	if (providerId != null) {
	    HOME_URI = LibertyManager.getHomeURL(providerId, providerRole);
        }
	if (LibertyManager.isTerminationSuccess(request)) {
%>
	<p><b>The user has successfully terminated federation with the provider.<br></b></p>
<%
	}else if (LibertyManager.isTerminationCancelled(request)) {
%>
	<p><b>The user has cancelled federation termination.<br></b></p>
<%	} else {
%>
	<p><b>Unable to terminate federation with the provider.<br></b></p>
<%	}
%>
	<p>
        <% if (HOME_URI == null){ %>
            <a href="http://www.sun.com" >Continue</a>
        <%}else {%>
            <a href="<%=HOME_URI%>" >Continue</a>
        <% } %>
        </p>
    </td>
    </tr>
</table>
<%@ include file="Footer.jsp"%>

</body>
</html>

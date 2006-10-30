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

   $Id: NameRegistration.jsp,v 1.1 2006-10-30 23:17:17 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>





<%@page language="java"
import="com.sun.liberty.LibertyManager, java.util.Set, java.util.Iterator"
%>
<html>
<head>
<title>Sun Java System Access Manager(Registration)</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<!-- link to be set...
<!- <link rel="stylesheet" href="../../../fed_css/styles.css" type="text/css" -->
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="9" marginwidth="9"
      topmargin="9" marginheight="9" rightmargin="9">

<%@include file="Header.jsp"%>
<table width='100%' cellpadding='0' cellspacing='3'>
<%
    String providerAlias = request.getParameter(LibertyManager.getMetaAliasKey());
    String providerID = LibertyManager.getEntityID(providerAlias);
    String providerRole = LibertyManager.getProviderRole(providerAlias);
    String homeURL = LibertyManager.getHomeURL(providerID, providerRole);
    String preLoginURL =
        LibertyManager.getPreLoginServletURL(providerID, providerRole, request);
    String actionLocation = LibertyManager.getNameRegistrationURL(
             providerID, providerRole, request);
    String gotoUrl = HttpUtils.getRequestURL(request).toString()
		+ "?" + request.getQueryString();
    String userDN = LibertyManager.getUser(request);
    String nameRegistrationDonePageURL = LibertyManager.
            getNameRegistrationDonePageURL(providerID, providerRole, request);
        String dest = nameRegistrationDonePageURL + "&regStatus=cancel";

%>
<SCRIPT language="javascript">
    function doCancel() {
        location.href="<%=dest%>";
    }
</SCRIPT>
<%
    if (providerAlias == null || providerAlias.length() <= 0) {
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
            "Provider alias not found");
        return;
    } else if (providerID == null || providerID.length() <= 0) {
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
            "Provider not found");
        return;
    }
    if (userDN == null)	{
        // redirect for authentication
        char delimiter;
        delimiter = preLoginURL.indexOf('?') < 0 ? '?' : '&';
        response.sendRedirect(preLoginURL + delimiter + "goto=" +
                        java.net.URLEncoder.encode(gotoUrl));
        return;
    }
    try {
	Set providerList = LibertyManager.getRegisteredProviders(
            userDN, providerID, providerRole);
	if (providerList != null && !providerList.isEmpty()) {
%>
    <tr>
	<form name="selectprovider" method="POST" action="<%= actionLocation%>">
	<td class="loginText" align="center">
            <b> Please select a remote provider to register with: </b>
        </td>
    </tr>
    <tr>
        <td class="loginText" align="center">
            <SELECT name= <%=LibertyManager.getNameRegistrationProviderIDKey()%> size="1" >
<%
	Iterator iterProvider = providerList.iterator();
	while (iterProvider.hasNext()) {
            String providerId = (String) iterProvider.next();
%>
            <OPTION value="<%=providerId%>"><%=providerId%></OPTION>
<%
        }
%>
            </SELECT>
	</td>
     </tr>
     <tr>
	<td colspan="3">&nbsp;</td>
     </tr>
     <tr>
        <td><div align="center">
            <input name="doIt" type="submit" value="submit">
            <input name="button2" type="button" onClick='doCancel()' value="cancel">
            </div>
	</td>
    </tr>
<%  } else {
%>
    <B>User has no active registrations.</B>
    <p>
<%
     if (homeURL == null) {
%>
    <a href="http://www.sun.com" > Continue</a>
<%
    } else {
%>
	<a href="<%=homeURL%>" > Continue</a>

<%
    }
%>
</p>
<%
    }
    } catch(Exception ex){
	response.sendRedirect(preLoginURL + "?goto=" +
            java.net.URLEncoder.encode(gotoUrl));
	return;
    }
%>
</table>
<p>&nbsp</p>
<%@ include file="Footer.jsp"%>
</body>
</html>




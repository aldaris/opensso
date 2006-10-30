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

   $Id: Termination.jsp,v 1.1 2006-10-30 23:17:18 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>





<%@ page language="java"
import="com.sun.liberty.LibertyManager,
java.util.Set,java.util.Iterator"
%>
<%
	String providerAlias =
                    request.getParameter(LibertyManager.getMetaAliasKey());
        String hostedProviderId = LibertyManager.getEntityID(providerAlias);
        String providerRole = LibertyManager.getProviderRole(providerAlias);
        String termDonePageURL = LibertyManager.
            getTerminationDonePageURL(hostedProviderId, providerRole, request);
        String dest = termDonePageURL + "&termStatus=cancel";
%>
<html>
<SCRIPT language="javascript">
    function doCancel() {
        location.href="<%=dest%>";
    }
</SCRIPT>

<head>
<title>Sun Java System Access Manager(Federation Termination)
</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../fed_css/styles.css" type="text/css">
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="9" marginwidth="9"
    topmargin="9" marginheight="9" >
<br>
<%@ include file="Header.jsp"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

    <tr>
    <td colspan="2">&nbsp;</td>
    <td width="100%"> </td>
	<table border="0" cellspacing="3" cellpadding="0" align=center>
	</tr>
<%
	// Alias processing
	//String providerAlias =
    //                request.getParameter(LibertyManager.getMetaAliasKey());
	if (providerAlias == null || providerAlias.length() < 1)
	{
		response.sendError(response.SC_INTERNAL_SERVER_ERROR,
			"Provider Alias not found");
		return;
	}
	if (hostedProviderId == null || hostedProviderId.length() < 1)
	{
		response.sendError(response.SC_INTERNAL_SERVER_ERROR,
			"Provider not found");
		return;
	}
	String HOME_URL = LibertyManager.getHomeURL(
                hostedProviderId, providerRole);
	String preLoginURL = LibertyManager.getPreLoginServletURL(
               hostedProviderId, providerRole, request);
	String actionLocation = LibertyManager.getTerminationURL(
               hostedProviderId, providerRole, request);
	String gotoUrl = HttpUtils.getRequestURL(request).toString()
		+ "?" + request.getQueryString();
	String userDN = LibertyManager.getUser(request);
	if (userDN == null)
	{
        // redirect for authentication
        char delimiter;
        delimiter = preLoginURL.indexOf('?') < 0 ? '?' : '&';
        response.sendRedirect(preLoginURL + delimiter + "goto=" +
                        java.net.URLEncoder.encode(gotoUrl));
        return;
	}
	try{
	Set providerList = LibertyManager.getFederatedProviders(
            userDN, hostedProviderId, providerRole);
	if (providerList != null && !providerList.isEmpty())
	{
%>

        <tr>
	<form name="selectprovider" method="POST" action="<%= actionLocation%>">

	<td class="loginText" align="center"><b> Please select a remote provider to terminate federation with: </b></td>

	</tr>

	<tr>
	<td class="loginText" align="center">
	<SELECT name= <%=LibertyManager.getTerminationProviderIDKey()%> size="1" >
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
	</form>
	</tr>
	</table>
	<%	}
	else {
	%>
	<B>User has no active federations.</B>

	<p>
	<% if (HOME_URL == null){ %>
	<a href="http://www.sun.com" > Continue</a>
	<%}else {%>
	<a href="<%=HOME_URL%>" > Continue</a>
	<% } %>
	</p>

	<%
	}
	}catch(Exception ex){
		response.sendRedirect(preLoginURL + "?goto=" +
			 java.net.URLEncoder.encode(gotoUrl));
		return;
	}

	%>
</table>
<p>&nbsp</p>
Federation termination results in the mapping established between the user's account at service provider and Identity
provider to be disabled. Once account federation is terminated the Identity provider cannot issue authentication
assertions for this user to that service provider.
<%@ include file="Footer.jsp"%>

</body>
</html>

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

   $Id: Error.jsp,v 1.1 2006-10-30 23:17:15 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>





<%@ page language="java" import="java.io.*,java.util.*,
com.sun.liberty.LibertyManager"
%>

<%
	String ERROR_URL = LibertyManager.getFedErrorKey();
	String ERROR_REMARK = LibertyManager.getFedRemarkKey();
        String providerAlias =
                request.getParameter(LibertyManager.getMetaAliasKey());
        String providerId = LibertyManager.getEntityID(providerAlias);
        String providerRole = LibertyManager.getProviderRole(providerAlias);
	String HOME_URI = null;
	if (providerId != null) {
		HOME_URI = LibertyManager.getHomeURL(providerId, providerRole);
        }
%>
<HEAD>
<title>Sun Java System Access Manager(Error)</title>
</HEAD>
<BODY BGCOLOR="#ffffff" LINK="#666699" VLINK="#666699" ALINK="#333366">
<%@ include file="Header.jsp"%>
<FONT FACE="PrimaSans BT, Verdana, sans-serif" SIZE="3">
<TABLE BORDER="0" CELLPADDING="5" CELLSPACING="0" WIDTH="100%">
<TR><TD NOWRAP><BR><FONT FACE="PrimaSans BT, Verdana, sans-serif" SIZE="3" >
</TD></TR>
<% if (request.getParameter(ERROR_URL) != null) { %>
<tr>
 <td>
   	Error : <i><%=request.getParameter(ERROR_URL)%></i>
 </td>
</tr>
<tr>
 <td>
	Remark : <i><%=request.getParameter(ERROR_REMARK)%></i>
 </td>
</tr>
<% } else { %>
<B>Error occured during proccessing<B>.<br>Please contact your Service Provider.
<%}%>
</TABLE>
</FONT>
<P>
<% if (HOME_URI == null){ %>
	<A HREF="http://www.sun.com">Continue</A>
<%}else {%>
	<A HREF="<%=HOME_URI%>">Continue</A>
<% } %>
<P>
<%@ include file="Footer.jsp"%>
</BODY>
</HTML>





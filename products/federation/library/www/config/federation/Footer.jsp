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

   $Id: Footer.jsp,v 1.1 2006-10-30 23:17:16 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>



<%@ page
import="com.sun.identity.common.SystemConfigurationUtil"
%>

<%
    String uri = null;
    uri = SystemConfigurationUtil.getProperty(
        "com.iplanet.am.services.deploymentDescriptor");
    if ((uri == null) || (uri.length() == 0)) {
        uri = "../../..";
    }
%>

<html>
<head>
<title>Sun Java System Access Manager (Footer)</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="<%= uri %>/fed_css/styles.css" type="text/css">
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="9" marginwidth="9"
    topmargin="9" marginheight="9" >
<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

    <tr>
    <td colspan="3"><img src="<%= uri %>/fed_images/spacer.gif" width="1"
	height="57"></td>
    </tr>
    <tr>
    <HR>
    <td align="center" valign="top"><img
	src="<%= uri %>/fed_images/brandingLogo.gif"></td>
    <td><img src="<%= uri %>/fed_images/spacer.gif" width="9" height="1"></td>
    <td valign="top" class="footerText"></td>
</tr>

</table>
</body>
</html>

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

   $Id: Header.jsp,v 1.1 2006-10-30 23:17:16 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>



<%@ page
import="com.sun.identity.common.SystemConfigurationUtil"
%>

<%
    String deployuri = null;
    deployuri = SystemConfigurationUtil.getProperty(
        "com.iplanet.am.services.deploymentDescriptor");
    if ((deployuri == null) || (deployuri.length() == 0)) {
	deployuri = "../../..";
    }
%>

<html>
<head>
<title>Sun Java System Access Manager (Header)</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="<%= deployuri %>/fed_css/styles.css" type="text/css">
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="9" marginwidth="9"
    topmargin="9" marginheight="9" >
<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

    <tr>
        <td width="110"><img src="<%= deployuri %>/fed_images/logo_sun.gif" width="110" height="82"></td>
        <td><img src="<%= deployuri %>/fed_images/spacer.gif" width="9" height="1"></td>
        <td valign="bottom" bgcolor="#ACACAC" width="100%">
            <img src="<%= deployuri %>/fed_images/Identity_LogIn.gif">
        </td>
    </tr>

    <tr>
    <td colspan="3"><img src="<%= deployuri %>/fed_images/spacer.gif" width="1"
	height="39"></td>
    </tr>
</table>
</body>
</html>

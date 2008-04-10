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

   $Id: validateWait.jsp,v 1.1 2008-04-10 23:15:05 veiming Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page import="com.sun.identity.common.SystemConfigurationUtil" %>
<%@ page import="com.sun.identity.shared.Constants" %>

<%
    String deployuri = SystemConfigurationUtil.getProperty(
        Constants.AM_SERVICES_DEPLOYMENT_DESCRIPTOR);
    String message = request.getParameter("m");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<%= deployuri %>/com_sun_web_ui/css/css_ns6up.css" />
<meta name="Copyright" content="Copyright &copy; 2008 Sun Microsystems, Inc. All Rights Reserved. Use is subject to license terms." /><link rel="shortcut icon" href="<%= deployuri %>/com_sun_web_ui/images/favicon/favicon.ico" type="image/x-icon"></link>
<link rel="stylesheet" type="text/css" href="<%= deployuri %>/console/css/opensso.css" />


<script language="JavaScript">
</script>

<html>
</head>
<body class="DefBdy">

<div><img src="<%= deployuri %>/com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="120" width="1" /></div>
<center><img src="<%= deployuri %>/console/images/processing.gif" alt="" border="0" height="66" width="66" />
<p>&nbsp;</p>
<span class="ProgressText" id="message"><%= message %></span>
</center>



</body>
</html>

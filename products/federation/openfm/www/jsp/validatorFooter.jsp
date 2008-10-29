<%--
   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
  
   Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
  
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

   $Id: validatorFooter.jsp,v 1.4 2008-10-29 03:11:55 veiming Exp $

--%>

<%@ page import="com.sun.identity.common.SystemConfigurationUtil" %>
<%@ page import="com.sun.identity.shared.Constants" %>

<%
    String deployuri = SystemConfigurationUtil.getProperty(
        Constants.AM_SERVICES_DEPLOYMENT_DESCRIPTOR);
    String msg = request.getParameter("m");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" type="text/css" href="<%= deployuri %>/com_sun_web_ui/css/css_ns6up.css" />
<link rel="shortcut icon" href="<%= deployuri %>/com_sun_web_ui/images/favicon/favicon.ico" type="image/x-icon"></link>
<link rel="stylesheet" type="text/css" href="<%= deployuri %>/console/css/opensso.css" />
</head>
<body class="DefBdy">

<table border=0 cellpadding=5 cellspacing=0 width="100%">
<tr>
<td width="10"></td>
<td align="left">
<%
    if (msg != null) {
        out.println(msg);
    }
%>
</td>
<td align="right" nowrap="nowrap" valign="bottom">
<div class="TtlBtnDiv"> <input name="btnCancel" type="submit" class="Btn1" value="Cancel" onmouseover="javascript: this.className='Btn1Hov'" onmouseout="javascript: this.className='Btn1'" onblur="javascript: this.className='Btn1'" onfocus="javascript: this.className='Btn1Hov'" onClick="top.cancelOp();return false;"/>
</div>
</td>
<td width="10"></td>
</tr>
</table>
</body>
</head>
</html>

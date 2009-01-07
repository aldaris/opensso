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

   $Id: session.jsp,v 1.1 2009-01-07 18:51:12 vimal_67 Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>




<%@ page import="com.sun.identity.plugin.session.SessionManager" %>
<%@ page import="com.sun.identity.plugin.session.SessionProvider" %>
<%@ page import="com.sun.identity.saml.common.SAMLConstants" %>
<%@ page import="com.sun.identity.saml.common.SAMLUtils" %>


<head>
<title>Session</title>
</head>
<body bgcolor="#FFFFFF" text="#000000">
<%
    try {
        Object sessionObj = null;
        try {
            sessionObj = SessionManager.getProvider().getSession(request);
        } catch (Exception ex) {
        }
        if (sessionObj == null) {
            String authURL = request.getScheme() + "://" +
                request.getServerName() + ":" + request.getServerPort() +
                request.getContextPath() + "/UI/Login?goto=" +
                request.getRequestURL().toString();
            response.sendRedirect(authURL);
            return;
        }

        String sessionID =
            SessionManager.getProvider().getSessionID(sessionObj);

%>
sessionID = <%= sessionID %>
<br>

<%
        String userID =
            SessionManager.getProvider().getPrincipalName(sessionObj);

%>
userID = <%= userID %>
<br>

<%
        String[] values = SessionManager.getProvider().getProperty(
            sessionObj, SAMLConstants.USER_NAME);
        if ((values != null) && (values.length > 0)) {
%>
userName = <%= values[0] %>
<br>

<%
        }

        values = SessionManager.getProvider().getProperty(
            sessionObj, "mail");
        if ((values != null) && (values.length > 0)) {
            for(int i=0; i< values.length; i++) {
%>
mail = <%= values[i] %>
<br>

<%
            }
        } else {
%>
no mail attribute
<br>

<%
        }

        values = SessionManager.getProvider().getProperty(sessionObj,
            "telephoneNumber");
        if ((values != null) && (values.length > 0)) {
            for(int i=0; i< values.length; i++) {
%>
telephoneNumber = <%= values[i] %>
<br>

<%
            }
        } else {
%>
no telephoneNumber attribute
<br>

<%
        }
    } catch (Exception ex) {
	SAMLUtils.debug.error("Unable to retrieve session attributes " , ex);
%>
Got exception: <%= ex.getMessage() %>
<%
    }
%>
</body>
</html>
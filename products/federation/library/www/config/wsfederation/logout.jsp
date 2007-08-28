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

   $Id: logout.jsp,v 1.2 2007-08-28 00:37:57 qcheng Exp $

   Copyright 2007 Sun Microsystems Inc. All Rights Reserved
--%>

<%@page
    import="com.sun.identity.wsfederation.common.WSFederationConstants"
    import="java.util.Map"
    import="com.sun.identity.plugin.session.SessionManager"
    import="com.sun.identity.multiprotocol.MultiProtocolUtils"
    import="com.sun.identity.multiprotocol.SingleLogoutManager"
    import="com.sun.identity.wsfederation.common.WSFederationUtils"
%>
<%
    String displayName = 
        (String)request.getAttribute(WSFederationConstants.LOGOUT_DISPLAY_NAME);
    String wreply = 
        (String)request.getAttribute(WSFederationConstants.LOGOUT_WREPLY);
    Map<String, String> providerList = 
        (Map<String, String>)request.getAttribute(
        WSFederationConstants.LOGOUT_PROVIDER_LIST);
%>
<html xmlns="https://www.w3.org/1999/xhtml">
  <head>
    <title>Signing Out</title>
  </head>
  <body>
    <p>Signed out of <%=displayName%></p>
  <%
    if ( wreply!=null && wreply.length()>0 )
    {
  %>
    <p>Click <a href="<%=wreply%>">here to continue</p>
  <%
    }
  %>
  <%
    for ( String url : providerList.keySet() )
    {
  %>
    <p>Signing out from <%=providerList.get(url)%></p>
    <iframe width="500" src="<%=url%>"></iframe>
  <%
    }

    // handle multi-federation protocol case
    Object uSession = null;
    try {
        uSession = SessionManager.getProvider().getSession(request);
    } catch (Exception e) {
        // ignore
    }
    if ((uSession != null) && SessionManager.getProvider().isValid(uSession) &&
        MultiProtocolUtils.isMultipleProtocolSession(uSession, 
            SingleLogoutManager.WS_FED)) {
        WSFederationUtils.processMultiProtocolLogout(request, 
            response, uSession);
    }
  %>
  </body>
</html>

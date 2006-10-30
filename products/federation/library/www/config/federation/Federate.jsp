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

   $Id: Federate.jsp,v 1.1 2006-10-30 23:17:16 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>





<%@ page language="java" import="java.util.*,java.io.*,com.sun.liberty.LibertyManager"
 %>
<%
boolean bLECP = LibertyManager.isLECPProfile(request);
if(bLECP) {
    response.setContentType(LibertyManager.getLECPContentType());
    response.setHeader(
        LibertyManager.getLECPHeaderName(),
        request.getHeader(LibertyManager.getLECPHeaderName()));
    String responseData = LibertyManager.getAuthnRequestEnvelope(request);
    PrintWriter writer = response.getWriter();
    writer.write(responseData);
    writer.flush();
    writer.close();
}
%>

<html>
<%
    String metaAliasKey = LibertyManager.getMetaAliasKey();
    String metaAlias = request.getParameter(metaAliasKey);
%>
<SCRIPT language="javascript">
    function doSubmit() {
        document.form1.action.value = 'submit';
        document.form1.submit();
    }
    function doCancel() {
       location.href="FederationDone.jsp?metaAlias=<%=metaAlias%>&termStatus=cancel";
    }
</SCRIPT>
<head>
<title>Sun Java System Access Manager(Account Federation)</title>
</head>
<body bgcolor="#FFFFFF" text="#000000">
<%@ include file="Header.jsp"%>
<%
    Set providerSet = null;
    String LRURLKey = LibertyManager.getLRURLKey();
    String selectedProvider = LibertyManager.getSelectedProviderKey();
    String LRURL = request.getParameter(LRURLKey);
    String userDN = LibertyManager.getUser(request);
    String providerID = LibertyManager.getEntityID(metaAlias);
    String providerRole = LibertyManager.getProviderRole(metaAlias);
    String HOME_URL = LibertyManager.getHomeURL(providerID, providerRole);
	if (userDN == null)
	{
        String gotoUrl = HttpUtils.getRequestURL(request).toString()
		+ "?" + request.getQueryString();
        String preLoginURL = LibertyManager.getPreLoginServletURL(
                providerID, providerRole, request);
        char delimiter;
        delimiter = preLoginURL.indexOf('?') < 0 ? '?' : '&';
        response.sendRedirect(preLoginURL + delimiter + "goto=" +
                        java.net.URLEncoder.encode(gotoUrl));
        return;
        }

    String actionURL = LibertyManager.getFederationHandlerURL(request);
    if(providerID != null) {
        providerSet = LibertyManager.getProvidersToFederate(
            providerID, providerRole, userDN);
    } else {
       response.sendError(response.SC_INTERNAL_SERVER_ERROR,"Not able to get providerID");
    }
    if(LRURL == null || LRURL.length() <= 0) {
       LRURL = LibertyManager.getFederationDonePageURL(
               providerID, providerRole, request);
    }
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

    <tr>
    <td colspan="2">&nbsp;</td>
    <td width="100%">

    <table border="0" cellspacing="3" cellpadding="0" align=center>
    <% if(providerSet == null || providerSet.size() == 0) { %>
    <tr><td><br><b>There are no providers to federate with.</b> <br></td></tr>
    <% } else { %>
        <tr>
          <td colspan="3"> <div align="center"><font size="3" face="Arial, Helvetica, sans-serif"><b>
              Please select an Identity Provider to federate with:</b> </font></div></td>
        </tr>

        <form name="form1" method="post" action="<%= actionURL %>">
          <tr>
            <td colspan="3"> <div align="center">
                <input type="hidden" name="RelayState" value="<%= LRURL %>">
                <input type="hidden" name="metaAlias" value="<%= metaAlias%>">
                <input type="hidden" name="action" >
                <select name="<%= selectedProvider%>" size="1" >
                 <%  try {
            Iterator providerIter = providerSet.iterator();
            String idpID = new String();
            while(providerIter.hasNext()) {
                 idpID = (String)providerIter.next();
                %>
                  <option value="<%= idpID%>"> <%= idpID%> </option>
                  <%
            }//end of while
                } catch(Exception ex) {
                      response.sendError(response.SC_INTERNAL_SERVER_ERROR,"Error in handling request");
                }%>
        </select> <%
        }
     %>

              </div></td>
          </tr>
          <tr>
            <td colspan="3">&nbsp;</td>
          </tr>
        <%if(providerSet != null && providerSet.size() > 0) {%>
          <tr>
            <td width="175"><div align="right">
                <input name="button" type="button" onClick='doSubmit()' value="submit">
              </div></td>
            <td width="1">&nbsp;</td>
            <td width="170"><input name="button2" type="button" onClick='doCancel()' value="cancel"></td>
            </tr>
        <% } else  { %>
                <tr>
                <td colspan="3"><div align="center">
                <% if (HOME_URL == null){ %>
                    <a href="http://www.sun.com" >Continue</a>
                    <%} else {%>
                    <a href="<%=HOME_URL%>" >Continue</a>
                <% } %>
                </td>
                </tr>
            <% } %>
        </form>
      </table> </td>
    </tr>
</table>
<p>&nbsp;</p>
Account federation is the means to establish a mapping between a user's accounts
at the service provider and identity provider. A user whose account is so federated
can at all later times authenticate at identity provider and seamlessly be single
signed on to the service provider.
<%@ include file="Footer.jsp"%>
</body>

</html>

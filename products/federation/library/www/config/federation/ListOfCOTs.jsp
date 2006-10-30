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

   $Id: ListOfCOTs.jsp,v 1.1 2006-10-30 23:17:17 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>





<%@ page language="java" import="java.util.*,
com.sun.liberty.LibertyManager"
 %>

<html>
<SCRIPT language="javascript">
    function doSubmit() {
        document.form1.action.value = 'submit';
        document.form1.submit();
    }
    function doCancel() {
        document.form1.action.value = 'cancel';
        document.form1.submit();
    }
</SCRIPT>
<head>
<title>Sun Java System Access Manager(Authentication Domains)</title>
</head>
<body>
<%@ include file="Header.jsp"%>
<%

    Set cotSet = null;
    String metaAliasKey = LibertyManager.getMetaAliasKey();
    String LRURLKey = LibertyManager.getLRURLKey();
    String COTKey = LibertyManager.getCOTKey();
    String metaAlias = request.getParameter(metaAliasKey);
    String LRURL = request.getParameter(LRURLKey);
    String actionURL = LibertyManager.getConsentHandlerURL(request);
    String providerID = LibertyManager.getEntityID(metaAlias);
    String providerRole = LibertyManager.getProviderRole(metaAlias);
    if(providerID != null){
        cotSet = LibertyManager.getListOfCOTs(providerID, providerRole);
	}else {
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,"Not able to get Provider ID");
    }
    if(LRURL == null || LRURL.length() <= 0)
        LRURL = LibertyManager.getHomeURL(providerID, providerRole);
    if(cotSet == null) {
        response.sendRedirect(LRURL);
	return;
	}
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

    <tr>
    <td colspan="2">&nbsp;</td>
    <td width="100%"><table border="0" cellspacing="3" cellpadding="0" align=center>
        <form name="form1" method="post" action="<%= actionURL %>">
          <tr>
            <td colspan="3" ><div align="center"><font size="3" face="Arial, Helvetica, sans-serif">
            The Identity provider belongs to multiple authentication domains. </font></div></td>
            <td >&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" >&nbsp;</td>
            <td >&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" ><div align="center"><font size="3" face="Arial, Helvetica, sans-serif"><strong>Please select
                an authentication domain to set the <br>preferred Identity provider information:</strong></font></div></td>
            <td >&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" > <div align="center">
                <input type="hidden" name="LRURL" value="<%= LRURL %>">
                <input type="hidden" name="metaAlias" value="<%= metaAlias%>">
                <input type="hidden" name="action" >
                <select name="<%=COTKey%>" size="1" >
                  <%  try {
            Iterator cotIter = cotSet.iterator();
            String cotID = new String();
            while(cotIter.hasNext()) {
                 cotID = (String)cotIter.next();
                %>
                  <option value="<%= cotID%>"> <%= cotID%> </option>
                  <%
            }//end of while
                } catch(Exception ex) {
                   response.sendError(response.SC_INTERNAL_SERVER_ERROR,"Error in handling request");
                }
     %>
                </select>
              </div></td>
            <td >&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" >&nbsp;</td>
            <td >&nbsp;</td>
          </tr>
          <tr>
            <td width="194" > <div align="right">
                <input name="button" type="button" onClick='doSubmit()' value="submit">
              </div></td>
            <td width="1" >&nbsp;</td>
            <td width="179" ><input name="button2" type="button" onClick='doCancel()' value="cancel"></td>
            <td >&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" > <div align="center"> &nbsp;&nbsp; </div></td>
            <td width="1" >&nbsp; </td>
          </tr>
        </form>
      </table> </td>
    </tr>
</table>
An Identity Provider can belong to more than one authentication domain. In such cases the user will have to
select the authentication domain where he/she wants to publish this provider as the user's preferred Identity provider.
This information can later be used by service providers in this authentication domain to seamlessly single sign on the
user.
<%@ include file="Footer.jsp"%>
</body>
</html>


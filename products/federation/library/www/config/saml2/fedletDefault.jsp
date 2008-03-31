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

   $Id: fedletDefault.jsp,v 1.2 2008-03-31 19:54:11 qcheng Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>
<%@page
import="com.sun.identity.shared.encode.URLEncDec,
com.sun.identity.saml2.common.SAML2SDKUtils,
com.sun.identity.saml2.common.SAML2Constants,
com.sun.identity.saml2.profile.ResponseInfo,
com.sun.identity.saml2.protocol.Response,
com.sun.identity.saml2.assertion.Assertion,
com.sun.identity.saml2.assertion.AuthnStatement,
com.sun.identity.saml2.assertion.AttributeStatement,
com.sun.identity.saml2.assertion.Attribute,
com.sun.identity.saml2.assertion.NameID,
com.sun.identity.shared.encode.Base64,
com.sun.identity.saml2.protocol.ProtocolFactory,
java.util.Iterator,
java.util.List"
%>
<%
    String deployuri = request.getRequestURI();
    int slashLoc = deployuri.indexOf("/", 1);
    if (slashLoc != -1) {
        deployuri = deployuri.substring(0, slashLoc);
    } 
%>
<html>
<head>
    <title>Fedlet Default Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" type="text/css" href="<%= deployuri %>/com_sun_web_ui/css/css_ns6up.css" />
</head>

<body>
<div class="MstDiv"><table width="100%" border="0" cellpadding="0" cellspacing="0" class="MstTblTop" title="">
<tbody><tr>
<td nowrap="nowrap">&nbsp;</td>
<td nowrap="nowrap">&nbsp;</td>
</tr></tbody></table>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="MstTblBot" title="">
<tbody><tr>
<td class="MstTdTtl" width="99%">
<div class="MstDivTtl"><img name="ProdName" src="<%= deployuri %>/console/images/PrimaryProductName.png" alt="" /></div></td><td class="MstTdLogo" width="1%"><img name="RMRealm.mhCommon.BrandLogo" src="<%= deployuri %>/com_sun_web_ui/images/other/javalogo.gif" alt="Java(TM) Logo" border="0" height="55" width="31" /></td></tr></tbody></table>
<table class="MstTblEnd" border="0" cellpadding="0" cellspacing="0" width="100%"><tbody><tr><td><img name="RMRealm.mhCommon.EndorserLogo" src="<%= deployuri %>/com_sun_web_ui/images/masthead/masthead-sunname.gif" alt="Sun(TM) Microsystems, Inc." align="right" border="0" height="10" width="108" /></td></tr></tbody></table></div><div class="SkpMedGry1"><a name="SkipAnchor2089" id="SkipAnchor2089"></a></div>
<div class="SkpMedGry1"><a href="#SkipAnchor4928"><img src="<%= deployuri %>/com_sun_web_ui/images/other/dot.gif" alt="Jump Over Tab Navigation Area. Current Selection is: Access Control" border="0" height="1" width="1" /></a></div>

<%
    String respXML = (String) 
        request.getAttribute(SAML2Constants.SAML_RESPONSE);
    if (respXML != null && respXML.length() != 0) {
        Response samlResp = ProtocolFactory.getInstance().createResponse(
            new String(Base64.decode(respXML)));
        Assertion assertion = (Assertion) samlResp.getAssertion().get(0);
        out.println("<br><br><b>Single Sign-On successful.</b>");
        out.println("<br><br>");
        out.println("<table border=0>");
        NameID nameId = assertion.getSubject().getNameID();
        String value = nameId.getValue();
        String format = nameId.getFormat();
        if (format != null) {
            out.println("<tr>");
            out.println("<td valign=top><b>Name ID format: </b></td>");
            out.println("<td>" + format + "</td>");
            out.println("</tr>");
        }
        if (value != null) {
            out.println("<tr>");
            out.println("<td valign=top><b>Name ID value: </b></td>");
            out.println("<td>" + value + "</td>");
            out.println("</tr>");
        }
        AttributeStatement attrStat = null;
        List lst = assertion.getAttributeStatements();
        if ((lst != null) && !lst.isEmpty()) {
            attrStat = (AttributeStatement) 
                assertion.getAttributeStatements().get(0);    
        }
        if (attrStat != null) {
            out.println("<tr>");
            out.println("<td valign=top><b>Attributes: </b></td>");
            List attrList = attrStat.getAttribute();
            Iterator iter = attrList.iterator();
            out.println("<td>");
            while (iter.hasNext()) {
                Attribute attr = (Attribute) iter.next();
                out.println(attr.getName() + "=" 
                    + attr.getAttributeValueString().get(0) + "<br>");
            }
            out.println("</td>");
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("<br><br><b><a href=# onclick=toggleDisp('resinfo')>Click to view SAML2 Response XML</a></b><br>");
        out.println("<span style='display:none;' id=resinfo><textarea rows=40 cols=100>" + samlResp.toXMLString(true, true) + "</textarea></span>");

        out.println("<br><b><a href=# onclick=toggleDisp('assr')>Click to view Assertion XML</a></b>"); 
        out.println("<span style='display:none;' id=assr><br><textarea rows=40 cols=100>" + assertion.toXMLString(true, true) + "</textarea></span>");

        lst = assertion.getAuthnStatements();
        if (lst.size() > 0) {
            AuthnStatement authnStat = (AuthnStatement) 
                assertion.getAuthnStatements().get(0);    
            out.println("<br><br><b><a href=# onclick=toggleDisp('astat')>Click to view Authentication Statement XML</a></b><br>");
            out.println("<span style='display:none;' id=astat><textarea rows=6 cols=100>" + authnStat.toXMLString(true, true) + "</textarea></span>");
        }
  
        if (attrStat != null) {
            out.println("<br><b><a href=# onclick=toggleDisp('atstat')>Click to view Attribute Statement XML</b></a><br>");
            out.println("<span style='display:none;' id='atstat'><br><textarea rows=6 cols=100>" + attrStat.toXMLString(true, true) + "</textarea></span>");
        }
    } else {
	%>
	    <p><br><%= SAML2SDKUtils.bundle.getString("missingMessageParam") %>
	<%
    }
%>
<script>
function toggleDisp(id)
{
    var elem = document.getElementById(id);
    if (elem.style.display == 'none')
        elem.style.display = '';
    else
        elem.style.display = 'none';
}
</script>
</body>
</html>

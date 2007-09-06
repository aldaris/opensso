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

   $Id: configurationDone.jsp,v 1.3 2007-09-06 18:23:22 qcheng Exp $

   Copyright 2007 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page import="com.sun.identity.common.SystemConfigurationUtil" %>

<%
    String deployuri = SystemConfigurationUtil.getProperty(
        "com.iplanet.am.services.deploymentDescriptor");
    if ((deployuri == null) || (deployuri.length() == 0)) {
        deployuri = "../../..";
    }
    String status = request.getParameter("status");
%>

<html>
<head>
<title>Service Provider</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<%= deployuri %>/com_sun_web_ui/css/css_ns6up.css" />
</head>
<body class="DefBdy">
                                                                                
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
                                                                                

<table border="0" cellpadding="10" cellspacing="0" width="100%">
<tr><td>
<p>&nbsp;</p>
<%
    if ((status == null) || (status.length() == 0)) {
%>
    Invalid request, please contact your administrator for more details.
<%
    } else if (status.equalsIgnoreCase("success")) {
%>
    IDP configuration succeeded. Please click <a href="../demo/home.jsp">here</a>
    for sample home page.

    <br>
    <p>
    If this is for WS-Federation protocol, following manual steps is needed until OpenSSO issue 803 is fixed.<br>
<b>Note:</b> following are NOT needed for ID-FF and SAMLv2 protocol.<br>

    <br>
    <ol>
        <li>Following document to setup XML signing on the IDP instance.</li>
        <li>Export existing IDP metadata using famadm export-entity subcommand.</li>
        <li>Edit the standard metadata XML file, add the IDP signing Certificate to the metadata as <it>&lt;TokenSigningKeyInfo&gt;</it> element. e.g. 
<pre>
&lt;Federation FederationID="idp2" xmlns="http://schemas.xmlsoap.org/ws/2006/12/federation"&gt;
    &lt;TokenSigningKeyInfo&gt;
        &lt;ns1:SecurityTokenReference ns1:Usage="" xmlns:ns1="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"&gt;
            &lt;ns2:X509Data xmlns:ns2="http://www.w3.org/2000/09/xmldsig#"&gt;
                &lt;ns2:X509Certificate&gt;
MIICojCCAgugAwIBAgIBMjANBgkqhkiG9w0BAQQFADBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMK
Q2FsaWZvcm5pYTEUMBIGA1UEBxMLc2FudGEgY2xhcmExDDAKBgNVBAoTA3N1bjENMAsGA1UECxME
...
2oQ7tPCURs7sdllhk5COIhD1bfdPXtATcWos3y/CX4Go5QRuDRdBiSUT+ujqCeQY5/dvLgtKcsZ4
FJwG83VfWrBO0sg9HBNqsqDFTzTwIJoxgApHaxVuLWPPaCrg3iizi9B6cHSMLaYP+pj+
                &lt;/ns2:X509Certificate&gt;
            &lt;/ns2:X509Data&gt;
        &lt;/ns1:SecurityTokenReference&gt;
    &lt;/TokenSigningKeyInfo&gt;
    &lt;TokenIssuerName&gt;idp2&lt;/TokenIssuerName&gt;
    &lt;TokenIssuerEndpoint&gt;
        &lt;ns3:Address xmlns:ns3="http://www.w3.org/2005/08/addressing"&gt;http://moonriver.red.iplanet.com:58080/idp2/WSFederationServlet/metaAlias/wsfedidp&lt;/ns3:Address&gt;
    &lt;/TokenIssuerEndpoint&gt;
    &lt;TokenTypesOffered&gt;
        &lt;TokenType Uri="urn:oasis:names:tc:SAML:1.1"/&gt;
    &lt;/TokenTypesOffered&gt;
    &lt;UriNamedClaimTypesOffered&gt;
        &lt;ClaimType Uri="http://schemas.xmlsoap.org/claims/UPN"&gt;
            &lt;DisplayName&gt;User Principal Name&lt;/DisplayName&gt;
        &lt;/ClaimType&gt;
    &lt;/UriNamedClaimTypesOffered&gt;
&lt;/Federation&gt;
</pre> 
<br>
Edit IDP extended metadata, set the signing certification alias, e.g.
<pre>
        &lt;Attribute name="signingCertAlias"&gt;
            &lt;Value&gt;test&lt;/Value&gt;
        &lt;/Attribute&gt;

</pre>
        </li>
        <li>Delete the exisiting IDP metadata using famadm delete-entity subcommand, and reload the new XML files (both standard and extended) using famadm import-entity subcommand.</li>
        <li>Goto the SP machine, and export the remote IDP metadata using famadm command.</li>
        <li>Delete the existing IDP metadata on SP machine, and load the new IDP standard metadata with the signing Certificate using famadm</li>
    <ol>
<%
    } else {
%>
    IDP configuration failed, please contact IDP administrator for more details.
<%
    }
%>

<p>&nbsp;</p>
</td>
</tr>
</table>
</body>
</html>

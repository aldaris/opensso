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

   $Id: saeIDPApp.jsp,v 1.3 2008-02-29 00:26:01 exu Exp $

   Copyright 2007 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page import="com.sun.identity.sae.api.SecureAttrs"%>
<%@ page import="com.sun.identity.sae.api.Utils"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.sun.identity.common.SystemConfigurationUtil"%>
<%!
public void jspInit()
{
}
%>
<%
    String deployuri = SystemConfigurationUtil.getProperty(
        "com.iplanet.am.services.deploymentDescriptor");
    if ((deployuri == null) || (deployuri.length() == 0)) {
        deployuri = "../../..";
    }
%>
<html>
<head>
<title>Secure Attributes Exchange IDP APP SAMPLE</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<%= deployuri %>/com_sun_web_ui/css/css_ns6up.css" />
</head>
<body>
<%@ include file="header.jsp" %>
<br><b>Secure Attributes Exchange IDP APP SAMPLE</b><br>
<% 
    // Crypto type to be used with local FAM-IDP
    String cryptotype     = SecureAttrs.SAE_CRYPTO_TYPE_SYM;
    // Shared secret with local FAM-IDP
    String secret     = "secret";
    // Keystore path (for asym signing)
    String keystore = "/export/home/rajeev/mykeystore";
    // Keystore Password (for asym signing)
    String keypass = "22222222";
    // Private key Password (for asym signing)
    String privkeypass = "11111111";
    // identity of this application : this string should match a already 
    // registered application in one of the hosted IDP extended metadata.
    String idpAppName = request.getRequestURL().toString();

    // FAM-IDP hosted SAE url that will act like the gateway.
    String  saeServiceURL="http://www.idp.com:8080/idp/idpsaehandler/metaAlias/idp";

    // String representing authenticated user.
    String userid = "testuser";
    String authlevel = "0";
    // String representing profile attributes of authenticated user
    String mail   = "testuser@foo.com";
    String branch = "mainbranch" ;

    // SP-App to be invoked with profile attributes above.
    String spapp  = "http://www.sp.com:8080/spp/samples/saml2/sae/saeSPApp.jsp";
    if (request.getMethod().equals("GET"))
    {
%>
<br>
This sample represents an IDP-App wishing to securely invoke a remote SP-App and pass it some secure attributes (mail and branch).
<br>
<br>
IDP-App -sae---> IDP-FAM --samlv2---> SP-FAM --sae--> SP-App 
<br>
<br>
<b>Prerequisites :</b>
<br>
IDP=Identity Provider  SP=Service Provider
<br>
i) Trust key (shared secret for symmetric crypto  or publickey for asymmetric signing) & this application provisioned on IDP-FAM in one of the hosted extended metadata - you will enter the same appname and secret here.
<br>
ii) SP_App and corresponding shared secret or key-pair provisioned on SP-FAM and destination SP-App. You will enter SP-App here.
<br>
iii) "auto-federation" and corresponding attributes setup (branch and mail) on both SP-FAM and IDP-FAM ends.
<br>
iv) SP-App is already deployed and ready to accept requests.
<br>
<br>
<b>Please Fill up the following form :</b> (Note that it is assumed userid you are about to enter is already authenticated.)
<br><br>
    <form method="POST">
      <table>
        <tr>
          <td>Userid on local IDP : </td>
          <td><input  type="text" name="userid" value="<%=userid%>"></td>
        </tr>
        <tr>
          <td>Authenticated auth level : </td>
          <td><input  type="text" name="authlevel" value="<%=authlevel%>"></td>
        </tr>
        <tr>
          <td>mail attribute : </td>
          <td><input  type="text" name="mail" value="<%=mail%>"></td>
        </tr>
        <tr>
          <td>branch attribute : </td>
          <td><input  type="text" name="branch" value="<%=branch%>"></td>
        </tr>
        <tr>
          <td>SP App URL : </td>
          <td><input  type="text" name="spapp" size=80 value="<%=spapp%>"></td>
        </tr>
        <tr>
          <td>SAE URL on IDP end: </td>
          <td><input type="text" name="saeurl" size=80 value=<%=saeServiceURL%>></td>
        </tr>
        <tr>
          <td>This application's identity (should match Secret below) : </td>
          <td><input  type="text" name="idpappname" size=80 value="<%=idpAppName%>"></td>
        </tr>
        <tr>
          <td>Crypto Type (symmetric | asymmetric : </td>
          <td><input  type="text" name="cryptotype" value="<%=cryptotype%>"></td>
        </tr>
        <tr>
          <td>Shared Secret / Private Key alias : </td>
          <td><input  type="text" name="secret" value="<%=secret%>"></td>
        </tr>
        <tr>
          <td>Key store path (asymmetric only) : </td>
          <td><input  type="text" name="keystore" value="<%=keystore%>"></td>
        </tr>
        <tr>
          <td>Key store password (asymmetric only) : </td>
          <td><input  type="text" name="keypass" value="<%=keypass%>"></td>
        </tr>
        <tr>
          <td>Private Key password (asymmetric only) : </td>
          <td><input  type="text" name="privkeypass" value="<%=privkeypass%>"></td>
        </tr>
        <tr>
          <td><input  type="submit" value="Generate URL"></td>
          <td></td>
        </tr>
      </table>
    </form>

<%  } else  {// POST
        HashMap map = new HashMap();
        userid = request.getParameter("userid");    
        authlevel = request.getParameter("authlevel");
        mail = request.getParameter("mail");    
        branch = request.getParameter("branch");    
        spapp = request.getParameter("spapp");    
        saeServiceURL = request.getParameter("saeurl");    
        idpAppName = request.getParameter("idpappname");    
        cryptotype = request.getParameter("cryptotype");    
        secret = request.getParameter("secret");    
        keystore = request.getParameter("keystore");    
        keypass = request.getParameter("keypass");    
        privkeypass = request.getParameter("privkeypass");    

        if (SecureAttrs.SAE_CRYPTO_TYPE_ASYM.equals(cryptotype)) {
          Properties saeparams = new Properties();
          saeparams.setProperty(SecureAttrs.SAE_CONFIG_KEYSTORE_TYPE, "JKS");
          saeparams.put(SecureAttrs.SAE_CONFIG_PRIVATE_KEY_ALIAS, secret);
          saeparams.put(SecureAttrs.SAE_CONFIG_KEYSTORE_FILE, keystore);
          saeparams.put(SecureAttrs.SAE_CONFIG_KEYSTORE_PASS, keypass);
          saeparams.put(SecureAttrs.SAE_CONFIG_PRIVATE_KEY_PASS, privkeypass);
          SecureAttrs.init(saeparams);
        }

        SecureAttrs sa = SecureAttrs.getInstance(cryptotype);
        map.put("branch",branch); 
        map.put("mail",mail); 
        // Following code secures attributes
        map.put(SecureAttrs.SAE_PARAM_USERID, userid); 
        map.put(SecureAttrs.SAE_PARAM_AUTHLEVEL, authlevel);
        map.put(SecureAttrs.SAE_PARAM_SPAPPURL, spapp); 
        map.put(SecureAttrs.SAE_PARAM_IDPAPPURL, idpAppName);
        String encodedString = sa.getEncodedString(map, secret);

        out.println("<br>Setting up the following params:");
        out.println("<br>branch="+branch);
        out.println("<br>mail="+mail);
        out.println("<br>"+SecureAttrs.SAE_PARAM_USERID+"="+userid);
        out.println("<br>"+SecureAttrs.SAE_PARAM_AUTHLEVEL+"="+authlevel);
        out.println("<br>"+SecureAttrs.SAE_PARAM_SPAPPURL+"="+spapp);
        out.println("<br>"+SecureAttrs.SAE_PARAM_IDPAPPURL+"="+idpAppName);

        HashMap slomap = new HashMap();
        slomap.put(SecureAttrs.SAE_PARAM_CMD,SecureAttrs.SAE_CMD_LOGOUT); 
        slomap.put(SecureAttrs.SAE_PARAM_APPSLORETURNURL, request.getRequestURL().toString());; 
        slomap.put(SecureAttrs.SAE_PARAM_IDPAPPURL, idpAppName);
        String sloencodedString = sa.getEncodedString(slomap, secret);

        // We are ready to format the URLs to invoke the SP-App and Single logout
        String url = null;
        String slourl = null;
        String postForm = null;
        HashMap pmap = new HashMap();
        pmap.put(SecureAttrs.SAE_PARAM_DATA, encodedString);
        if (saeServiceURL.indexOf("?") > 0) {
            url = saeServiceURL+"&"+SecureAttrs.SAE_PARAM_DATA+"="+encodedString;
            slourl = saeServiceURL+"&"+SecureAttrs.SAE_PARAM_DATA+"="
                                  +sloencodedString;
        }
        else {
            url = saeServiceURL+"?"+SecureAttrs.SAE_PARAM_DATA+"="+encodedString;
            slourl = saeServiceURL+"?"+SecureAttrs.SAE_PARAM_DATA+"="
                                  +sloencodedString;
        }

        // This function is a simple wrapper to create a form - to
        // autosubmit the form via javascriopt chnage false to true.
        postForm = Utils.formFromMap(saeServiceURL, pmap, false);
        out.println(postForm);

        out.println("<br><br>Click here to invoke the remote SP App via http GET to local IDP : "+spapp+"  :  <a href="+url+">ssourl</a>");
        out.println("<br><br>Click here to invoke the remote SP App via http POST to IDP : "+spapp+"  :  <input type=\"button\" onclick=\"document.forms['saeform'].submit();\" value=POST>");
        out.println("<br><br>This URL will invoke global Logout : <a href="+slourl+">slourl</a>");
    }
%>
</body>
</html>

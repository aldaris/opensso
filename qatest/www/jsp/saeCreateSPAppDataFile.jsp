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

   $Id: saeCreateSPAppDataFile.jsp,v 1.3 2008-12-05 19:37:38 rmisra Exp $

   Copyright 2007 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page import="com.sun.identity.sae.api.SecureAttrs"%>
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
<title>Secure Attributes Exchange SP APP SAMPLE DATA FILE CONFIGURATION</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" type="text/css" href="<%= deployuri %>/com_sun_web_ui/css/css_ns6up.css" />
</head>
<body>
<%@ include file="header.jspf" %>
<br><b>Secure Attributes Exchange SP APP SAMPLE DATA FILE CONFIGURATION</b><br>
<% 
    // Crypto type to be used with local FAM-IDP
    String cryptotype     = SecureAttrs.SAE_CRYPTO_TYPE_SYM;
    // Shared secret with local FAM-IDP
    String secret     = "federatedaccessmanager";
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
    String  saeServiceURL="http://www.idp1.com:9080/idp1/idpsaehandler/metaAlias/idp";

    // String representing authenticated user.
    String userid = "testuser";
    // String representing profile attributes of authenticated user
    String mail   = "testuser@foo.com";
    String branch = "mainbranch" ;

    // SP-App to be invoked with profile attributes above.
    String spapp  = "http://www.sp1.com:9080/sp1/samples/saml2/sae/saeSPApp.jsp";

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
        String userDir = System.getProperty("user.dir");
        System.out.println(userDir);
        FileWriter fstream = new FileWriter(userDir + "/sae_sp_app_config_datafile.properties");
        BufferedWriter out1 = new BufferedWriter(fstream);
        HashMap map = new HashMap();
        userid = request.getParameter("userid");    
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

        out1.write("SP_SAMPLE_CRYPTO="  + cryptotype + "\n");
        out1.write("SP_SAMPLE_SECRET=" + secret + "\n");
        if (SecureAttrs.SAE_CRYPTO_TYPE_ASYM.equals(cryptotype)) {
            out1.write("SAE_CONFIG_PRIVATE_KEY_ALIAS=" + secret + "\n");
            out1.write("SAE_CONFIG_KEYSTORE_FILE=" + keystore + "\n");
            out1.write("SAE_CONFIG_KEYSTORE_PASS=" +keypass + "\n");
            out1.write("SAE_CONFIG_PRIVATE_KEY_PASS=" + privkeypass + "\n");
        }
        out1.flush();
        out1.close();
    }
%>
</body>
</html>

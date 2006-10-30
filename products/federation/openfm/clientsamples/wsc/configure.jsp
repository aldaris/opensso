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

   $Id: configure.jsp,v 1.1 2006-10-30 23:17:41 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>


<html>
<head>
<title>Configure Identity Provider</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="../com_sun_web_ui/css/css_ns6up.css" />


<%@ page import="
java.io.*,
java.util.HashSet,
java.util.Set"
%>

<%
    String bootstrapFile = System.getProperty("java.io.tmpdir") +
        File.separator + "ClientSampleWSC.properties";

    boolean configured = false;
    String errorMsg = null;

    String spProviderID = request.getParameter("spProviderID");
    String idpProt = request.getParameter("idpProt");
    String idpHost = request.getParameter("idpHost");
    String idpPort = request.getParameter("idpPort");
    String idpDeploymenturi = request.getParameter("idpDeploymenturi");
    String idpUserDN = request.getParameter("idpUserDN");
    String configDir = request.getParameter("configDir");
    String submit = request.getParameter("submit");
    String servletPath = request.getServletPath();

    if (submit != null) {
        File configDirF = new File(configDir);
        if (!configDirF.exists()) {
            errorMsg = "Configuration Directory '" + configDir + "' doesn't" +
                " exist.";
        } else {
            File fedConfig = new File(configDir +
                "/FederationConfig.properties");
            if (!fedConfig.exists()) {
                errorMsg = configDir + " is not a valid configuration " +
                    "directory.";
            } else {
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(new FileWriter(bootstrapFile));
                    pw.println("spProviderID=" + spProviderID);
                    pw.println("idpProt=" + idpProt);
                    pw.println("idpHost=" + idpHost);
                    pw.println("idpPort=" + idpPort);
                    pw.println("idpDeploymenturi=" + idpDeploymenturi);
                    pw.println("idpUserDN=" + idpUserDN);
                    pw.println("configDir=" + configDir);
                    configured = true;
                }catch (IOException ioex) {
                    errorMsg = "Unable to create WSC sample property " +
                        "file. " + ioex.getMessage();
                } finally {
                    if (pw != null) {
                        pw.close();
                    }
                }
            }
        }
    }
%>

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
<div class="MstDivTtl"><img name="ProdName" src="../console/images/PrimaryProductName.png" alt="" /></div></td><td class="MstTdLogo" width="1%"><img name="RMRealm.mhCommon.BrandLogo" src="../com_sun_web_ui/images/other/javalogo.gif" alt="Java(TM) Logo" border="0" height="55" width="31" /></td></tr></tbody></table>
<table class="MstTblEnd" border="0" cellpadding="0" cellspacing="0" width="100%"><tbody><tr><td><img name="RMRealm.mhCommon.EndorserLogo" src="../com_sun_web_ui/images/masthead/masthead-sunname.gif" alt="Sun(TM) Microsystems, Inc." align="right" border="0" height="10" width="108" /></td></tr></tbody></table></div><div class="SkpMedGry1"><a name="SkipAnchor2089" id="SkipAnchor2089"></a></div>
<div class="SkpMedGry1"><a href="#SkipAnchor4928"><img src="../com_sun_web_ui/images/other/dot.gif" alt="Jump Over Tab Navigation Area. Current Selection is: Access Control" border="0" height="1" width="1" /></a></div>
                                                                                
<table border="0" cellpadding="10" cellspacing="0" width="100%">
<tr><td>


<%
    if (!configured) {
%>

<h3>Configuring WSC Sample</h3>

<form action="configure.jsp" method="GET">
    Please provide the Service Provider Information.
    <p>&nbsp;</p>    

    <table border=0 cellpadding=5 cellspacing=0>

<%
    if (errorMsg != null) {
%>
    <tr>
    <td colspan="2" align="center">
    <b><font color="red"><%= errorMsg %></font>
    <br><br>
    </td>
    </tr>
<%
}
%>


    <tr>
    <td>SP Provider ID (eg. http://www.sp1.com):</td>
    <td><input name="spProviderID" type="text" size="40" value="<%= spProviderID == null ? "" : spProviderID %>" /></td>
    </tr>
    <tr>
    <td>IDP Protocol:</td>
    <td><input name="idpProt" type="text" size="6" value="<%= idpProt == null ? "" : idpProt %>" /></td>
    </tr>
    <tr>
    <td>IDP Host:</td>
    <td><input name="idpHost" type="text" size="30" value="<%= idpHost == null ? "" : idpHost %>" /></td>
    </tr>
    <tr>
    <td>IDP Port:</td>
    <td><input name="idpPort" type="text" size="6" value="<%= idpPort == null ? "" : idpPort %>" /></td>
    </tr>
    <tr>
    <td>IDP Deployment URI:</td>
    <td><input name="idpDeploymenturi" type="text" size="15" value="<%= idpDeploymenturi == null ? "" : idpDeploymenturi %>" /></td>
    </tr>
    <tr>
    <tr>
    <td>IDP User DN (IDP user whose personal profile resource 
       offering is to</td>
    <td><input name="idpUserDN" type="text" size="50" value="<%= idpUserDN == null ? "" : idpUserDN %>" /></td>
    </tr>
    <tr>
    <td>be created. For example id=idp,ou=user,dc=opensso,dc=java,dc=net):</t
    <td></td>
    </tr>

    <tr>
    <td>Configuration Directory:</td>
    <td><input name="configDir" type="text" size="15" value="<%= configDir == null ? "" : configDir %>" /></td>
    </tr>
    <tr>
    <td colspan="2" align="center">
    <input type="submit" name="submit" value="Configure" />
    <input type="reset" value="Reset" />
    </td>
    </tr>
    </table>
</form>

<%
} else {
%>
<p>&nbsp;</p>
WSC Sample is configured. Click <a href="index.html">here</a> to return
<%
}
%>
</td></tr></table>
</body>
</html>

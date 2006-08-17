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

   $Id: configurator.jsp,v 1.2 2006-08-17 17:09:45 veiming Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page import="com.sun.identity.setup.AMSetupServlet"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.MessageFormat"%>
<%@ page import="java.util.*"%>

<%
    ResourceBundle resBundle = ResourceBundle.getBundle("configuration");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Sun Java(TM) System Access Manager</title>
    <link rel="stylesheet" type="text/css" href="com_sun_web_ui/css/css_ns6up.css">
    <link rel="shortcut icon" href="com_sun_web_ui/images/favicon/favicon.ico" type="image/x-icon">

    <script language="Javascript">
        function validateForm(frm) {
            var elements = frm.elements;
            for (var i = 0; i < elements.length; i++) {
                var elt = elements[i];
                if (elt.name == "portnum") {
                   if (isNumericString(elt.value) != 1) {
                    <%
                        out.println("alert(\"" +
                            resBundle.getString("message-invalid-port") + 
                            "\");");
                    %>
                       return false;
                   }    
                } else if (elt.name == "protocol") {
                   if (((elt.value).toLowerCase()) != "http"  &&
                       ((elt.value).toLowerCase()) != "https" ) { 
                    <%
                        out.println("alert(\"" +
                            resBundle.getString("message-invalid-protocol") + 
                            "\");");
                    %>
                       return false;
                   } 
                } else if (elt.name == "deployuri") {
                   var idx = (elt.value).indexOf("/");
                   if (idx != 0) {
                    <%
                        out.println("alert(\"" +
                            resBundle.getString("message-invalid-deploymentURI")
                            + "\");");
                    %>
                       return false;
                   }
                } else if (elt.name == "adminPwd") {
                    if (elt.value.length < 8) {
                    <%
                        out.println("alert(\"" +
                            resBundle.getString("message-short-password") + 
                            "\");");
                    %>
                        return false;
                    }
                    if (elements['confirmAdminPwd'].value !=  elt.value) {
                    <%
                        out.println("alert(\"" +
                            resBundle.getString("message-mismatch-password") + 
                            "\");");
                    %>
                        return false;
                    }
                }
            }
            return true;
        }

        function isNumericString(inpStr)  {
            if (inpStr.length == 0) {
                return false;
            }

            var intStr = "1234567890";

            for (var x = 0; x < inpStr.length; x++)  {
                tmp = inpStr.substring (x, x+1);
                if (intStr.indexOf (tmp, 0)==-1) {
                    return (false);
                }
            }
            return (true);
        }

        function gotoLoginPage() {
            this.location.replace("./index.html");
        }

        function gotoConfigurator() {
            this.location.replace("configurator.jsp");
        }
    </script>
</head>
<body class="DefBdy">

    <div class="SkpMedGry1"><a href="#SkipAnchor3860"><img src="com_sun_web_ui/images/other/dot.gif" alt="Jump to End of Masthead" border="0" height="1" width="1"></a></div><div class="MstDiv">
    <table class="MstTblBot" title="" border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
        <td class="MstTdTtl" width="99%">
        <div class="MstDivTtl"><img name="AMConfig.configurator.ProdName" src="console/images/PrimaryProductName.png" alt="Sun Java System Access Manager" border="0"></div>
        </td>
        <td class="MstTdLogo" width="1%"><img name="AMConfig.configurator.BrandLogo" src="com_sun_web_ui/images/other/javalogo.gif" alt="Java(TM) Logo" border="0" height="55" width="31"></td>
        </tr>
    </table>
    <table class="MstTblEnd" border="0" cellpadding="0" cellspacing="0" width="100%"><tr><td><img name="RMRealm.mhCommon.EndorserLogo" src="com_sun_web_ui/images/masthead/masthead-sunname.gif" alt="Sun(TM) Microsystems, Inc." align="right" border="0" height="10" width="108" /></td></tr></table>
    </div>
    <table class="SkpMedGry1" border="0" cellpadding="5" cellspacing="0" width="100%"><tr><td><img src="com_sun_web_ui/images/other/dot.gif" alt="Jump to End of Masthead" border="0" height="1" width="1"></a></td></tr></table>
    <table border="0" cellpadding="10" cellspacing="0" width="100%"><tr><td></td></tr></table>


<%!
public void jspInit() {
    System.out.println("JSP INIT ...." + getServletContext());
}
%>

<table border=0 cellpadding=5>
<tr>
<td>

<% 
    boolean result = false;
    int portnum; 
    String hostname;
    String deployuri;
    String protocol;
    String basedir;

    if (request.getMethod().equals("POST")) {
        ServletContext sctx = getServletContext();
        hostname = request.getParameter("hostname");
        String portnumstr = request.getParameter("portnum");
        portnum = Integer.parseInt(portnumstr);
        protocol = request.getParameter("protocol");
        deployuri = request.getParameter("deployuri");
        basedir = request.getParameter("basedir");
        basedir = basedir.replace('\\', '/');

        if (AMSetupServlet.isConfigured()) {
            String redirectURL = protocol + "://" + hostname + ":" + portnum +
                deployuri;
            response.sendRedirect(redirectURL);
            return;
        } else {
            out.println("<br>" + 
                resBundle.getString("message-wait-configuring") + "<br>");
            out.flush();

            try {
                result = AMSetupServlet.processRequest(request, response);
            } catch (Exception e) {
                out.println("<b>" + e.getMessage() + "</b>");
                out.println("<script language=\"Javascript\">setTimeout('gotoConfigurator()', 3000);</script>\n");
                out.println("<p>" + 
                    resBundle.getString("message-redirect-to-configurator") + 
                    "</p>");
            }
        }

        if (result) {
            out.println("<p>" + resBundle.getString("message-configured")
                + "</p>");
            out.println("<script language=\"Javascript\">setTimeout('gotoLoginPage()', 5000);</script>\n");
            out.println("<p>" +
                resBundle.getString("message-redirect-to-loginpage") + "</p>");
        } else {
            out.println("<p>" +
                resBundle.getString("message-fail-configure") + "</p>");
            String debugDir = basedir + deployuri + "/debug";
            Object[] args = {debugDir};
            out.println("<p>" +
                MessageFormat.format(resBundle.getString("message-check-debug"), 
                    args) + "</p>");
        }
        out.flush();
        return;
    } else {
        hostname = request.getServerName(); 
        portnum  = request.getServerPort();
        protocol = request.getScheme();
        deployuri = request.getRequestURI();
        if (deployuri != null) {
            int idx = deployuri.indexOf("/configurator.jsp");
            if (idx > 0) {
                deployuri = deployuri.substring(0, idx);
            }
        }
        basedir = System.getProperty("user.home");

        if (File.separatorChar == '\\') {
            basedir = basedir.replace('\\', '/');
        }

        if (!basedir.endsWith("/")) {
            basedir += "/";
        }

        if (AMSetupServlet.isConfigured()) {
            String redirectURL = protocol + "://" + hostname + ":" + portnum +
                deployuri;
           response.sendRedirect(redirectURL);
           return;
        }
    }
%>
</td>
</tr>
</table>
   
<form name="configurator" method="post" action="configurator.jsp" onsubmit="return validateForm(this)">

    <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr valign="bottom">
            <td nowrap="nowrap" valign="bottom">
            <div class="TtlTxtDiv"><h1 class="TtlTxt">Configurator</h1></div>
            </td>
            <td align="right" nowrap="nowrap" valign="bottom">
            <div class="TtlBtnDiv"><input name="AMConfig.button1" class="Btn1" value=" Configure" onmouseover="javascript: if (this.disabled==0) this.className='Btn1Hov'" onmouseout="javascript: if (this.disabled==0) this.className='Btn1'" onblur="javascript: if (this.disabled==0) this.className='Btn1'" onfocus="javascript: if (this.disabled==0) this.className='Btn1Hov'" type="submit">&nbsp;<input name="AMConfig.button2" class="Btn2" value="Reset" onmouseover="javascript: if (this.disabled==0) this.className='Btn2Hov'" onmouseout="javascript: if (this.disabled==0) this.className='Btn2'" onblur="javascript: if (this.disabled==0) this.className='Btn2'" onfocus="javascript: if (this.disabled==0) this.className='Btn2Hov'" type="reset"> </div>
            </td>
        </tr>
    </table>
    <div class="LblRqdDiv" style="margin: 5px 10px 5px 0px;" align="right">
    <img src="com_sun_web_ui/images/other/required.gif" alt="Required Field" height="14" width="7">&nbsp;Indicates required field </div>

    <div class="ConFldSetDiv">
        <table title="" border="0" cellpadding="0" cellspacing="0">

            <!-- Host Name Textbox -->
            <tr>
            <td valign="top">
                <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="Required Field" title="Required Field" height="14" width="7"><span class="LblLev2Txt"><%= resBundle.getString("label-hostname") %>:</span></div>
            </td>
            <td valign="top">
                <div class="ConTblCl2Div"><input value="<%= hostname %>" name="hostname" id="hostname" size="75" class="TxtFld"></div>
            </td>
            </tr>

            <!-- Port Number Text Box -->
            <tr>
            <td valign="top">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="Required Field" title="Required Field" height="14" width="7"><span class="LblLev2Txt"><%= resBundle.getString("label-portnumber")%>:</span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input value="<%= portnum %>" name="portnum" id="portnum" size="35" class="TxtFld"></div>
            </td>
            </tr>

            <!-- Protocol Text Box -->
            <tr>
            <td valign="top">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="Required Field" title="Required Field" height="14" width="7"><span class="LblLev2Txt"><%= resBundle.getString("label-protocol")%></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input value="<%= protocol %>" name="protocol" id="protocol" size="35" class="TxtFld"></div>
            </td>
            </tr>
            
            <!-- Deploy URI Text Box -->
            <tr>
            <td valign="top">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="Required Field" title="Required Field" height="14" width="7"><span class="LblLev2Txt"><%= resBundle.getString("label-deploymentURI")%></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input value="<%= deployuri %>" name="deployuri" id="deployuri" size="50" class="TxtFld"><br /><div class="HlpFldTxt"><%= resBundle.getString("help-deploymentURI")%></div></div>
            </td>
            </tr>

            <!-- Admin Password Box -->
            <tr>
            <td valign="top">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="Required Field" title="Required Field" height="14" width="7"><span class="LblLev2Txt"><%= resBundle.getString("label-password")%></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input value="" type="password" name="adminPwd" id="adminPwd" size="15" class="TxtFld"><br /></div>
            </td>
            </tr>

            <!-- Admin Password Box (confirm) -->
            <tr>
            <td valign="top">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="Required Field" title="Required Field" height="14" width="7"><span class="LblLev2Txt"><%= resBundle.getString("label-password-again")%></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input value="" type="password" name="confirmAdminPwd" id="confirmAdminPwd" size="15" class="TxtFld"><br /></div>
            </td>
            </tr>

            <!-- Base Dir Text Box -->
            <tr>
            <td valign="top">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="Required Field" title="Required Field" height="14" width="7"><span class="LblLev2Txt"><%= resBundle.getString("label-database-dir")%></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input value="<%= basedir %>opensso" name="basedir" id="psLbl3" size="50" class="TxtFld"><br /><div class="HlpFldTxt"><%= resBundle.getString("help-database-dir")%></div></div>
            </td>
            </tr>

            <!-- Cookie Domain Text Box -->
            <tr>
            <td valign="top">
            <div class="ConTblCl1Div"><span class="LblLev2Txt"><%= resBundle.getString("label-cookie-domain")%></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input value="" name="cookieDomain" id="cookieDomain" size="50" class="TxtFld"></div>
            </td>
            </tr>

            <!-- Platform Locale Text Box -->
            <tr>
            <td valign="top">
            <div class="ConTblCl1Div"><span class="LblLev2Txt"><%= resBundle.getString("label-platform-locale") %></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input value="en_US" name="locale" id="locale" size="50" class="TxtFld"></div>
            </td>
            </tr>
        </table>
    </div>
    <div><img src="com_sun_web_ui/images/other/dot.gif?cfg=true" alt="" border="0" height="10" width="1"></div>
    </form>
</body>
</html>


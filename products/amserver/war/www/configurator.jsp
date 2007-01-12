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

   $Id: configurator.jsp,v 1.6 2007-01-12 21:29:44 veiming Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="com.sun.identity.setup.AMSetupServlet"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.servlet.ServletContext"%>

<%@taglib uri="/WEB-INF/configurator.tld" prefix="config" %>

<config:resBundle bundleName="amConfigurator"/>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Sun Java(TM) System Access Manager</title>
    <link rel="stylesheet" type="text/css" href="com_sun_web_ui/css/css_ns6up.css">
    <meta name="Copyright"
         content="Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved. Use is subject to license terms.">
    <link rel="shortcut icon" href="com_sun_web_ui/images/favicon/favicon.ico" type="image/x-icon">

    <script language="Javascript">
        function gotoLoginPage() {
            this.location.replace("./UI/Login");
        }
    </script>

</head>
<body class="DefBdy">

    <div class="SkpMedGry1"><a href="#SkipAnchor3860"><img src="com_sun_web_ui/images/other/dot.gif" alt="<config:message i18nKey="configurator.jumpmasthead"/>" border="0" height="1" width="1"></a></div><div class="MstDiv">
    <table class="MstTblBot" title="" border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
        <td class="MstTdTtl" width="99%">
        <div class="MstDivTtl"><img name="AMConfig.configurator.ProdName" src="console/images/PrimaryProductName.png" alt="Sun Java System Access Manager" border="0"></div>
        </td>
        <td class="MstTdLogo" width="1%"><img name="AMConfig.configurator.BrandLogo" src="com_sun_web_ui/images/other/javalogo.gif" alt="Java(TM) Logo" border="0" height="55" width="31"></td>
        </tr>
    </table>
    <table class="MstTblEnd" border="0" cellpadding="0" cellspacing="0" width="100%"><tr><td><img name="RMRealm.mhCommon.EndorserLogo" src="com_sun_web_ui/images/masthead/masthead-sunname.gif" alt="Sun(TM) Microsystems, Inc." align="right"
border="0" height="10" width="108" /></td></tr></table>
    </div>
    <table class="SkpMedGry1" border="0" cellpadding="5" cellspacing="0" width="100%"><tr><td><img src="com_sun_web_ui/images/other/dot.gif" alt="<config:message i18nKey="configurator.jumpmasthead"/>" border="0" height="1" width="1"></a></td></tr></table>
    <table border="0" cellpadding="10" cellspacing="0" width="100%"><tr><td></td></tr></table>


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
    String cookieDomain = "";
    String locale = "en_US";
    String adminPwd;
    String serverURL;

    if (request.getMethod().equals("POST")) {
        serverURL = request.getParameter("serverURL");
        deployuri = request.getParameter("deployuri");
        basedir = request.getParameter("BASE_DIR");
        basedir = basedir.replace('\\', '/');

        if (AMSetupServlet.isConfigured()) {
            String redirectURL = serverURL + deployuri;
            response.sendRedirect(redirectURL);
            return;
        } else {
            out.println("<br>");
            %><config:message i18nKey="configurator.configprogress"/><%
            out.println("...<br>");
            out.flush();

            try {
                result = AMSetupServlet.processRequest(request, response);
            } catch (Exception e) {
                out.println("<p>");
                out.println("<b>" + e.getMessage() + "</b>");
                out.println("<p>");
                %><config:message i18nKey="configurator.gotoConfiguratorJSP"/><%
                out.println("</p>");
            }
        }

        if (result) {
            out.println("<p>");
            %><config:message i18nKey="configurator.successstatus"/><%
            out.println("</p>");
            out.println("<script language=\"Javascript\">setTimeout('gotoLoginPage()', 5000);</script>\n");
            out.println("<p>");
            %><config:message i18nKey="configurator.redirect5"/><%
            out.println("</p>");
        } else {
            out.println("<p>");
            %><config:message i18nKey="configurator.failurestatus"/><%
            out.println("</p>");
            out.println("<p>");
            %><config:message i18nKey='configurator.log' patterntype='message'>
                <config:param index='0' arg='<%=basedir+deployuri+"/debug"%>'/>
              </config:message>
            <%
            out.println("</p>");
        }
        out.flush();
        return;
    } else {
        hostname = request.getServerName();
        portnum  = request.getServerPort();
        protocol = request.getScheme();
        serverURL = protocol + "://" + hostname + ":" + portnum;
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
        
        String subDomain;
        String topLevelDomain;
        int idx1 = hostname.lastIndexOf(".");
        if ((idx1 != -1) && (idx1 != (hostname.length() -1))) {
            topLevelDomain = hostname.substring(idx1+1);
            int idx2 = hostname.lastIndexOf(".", idx1-1);
            if ((idx2 != -1) && (idx2 != (idx1 -1))) {
                subDomain = hostname.substring(idx2+1, idx1);
                try {
                    Integer.parseInt(topLevelDomain);  
                } catch (NumberFormatException e) {
                    try {
                        Integer.parseInt(subDomain);  
                    } catch (NumberFormatException e1) {
                        cookieDomain = "." + subDomain + "." + topLevelDomain;
                    }
                }
            }
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

<form name="configurator" method="post" action="configurator.jsp" onsubmit="return true">
     <input type="hidden" name="deployuri" value="<%= deployuri %>">
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr valign="bottom">
            <td nowrap="nowrap" valign="bottom">
            <div class="TtlTxtDiv"><h1 class="TtlTxt"><config:message i18nKey="configurator.title"/></h1></div>
            </td>
            <td align="right" nowrap="nowrap" valign="bottom">
            <div class="TtlBtnDiv"><input name="AMConfig.button1" class="Btn1" value="<config:message i18nKey="configurator.button1label"/>" onmouseover="javascript: if (this.disabled==0) this.className='Btn1Hov'" onmouseout="javascript: if (this.disabled==0) this.className='Btn1'"
onblur="javascript: if (this.disabled==0) this.className='Btn1'" onfocus="javascript: if (this.disabled==0) this.className='Btn1Hov'" type="submit">&nbsp;<input name="AMConfig.button2" class="Btn2" value="<config:message i18nKey="configurator.button2reset"/>" onmouseover="javascript: if
(this.disabled==0) this.className='Btn2Hov'" onmouseout="javascript: if (this.disabled==0) this.className='Btn2'" onblur="javascript: if (this.disabled==0) this.className='Btn2'" onfocus="javascript: if (this.disabled==0) this.className='Btn2Hov'" type="reset"> </div>
            </td>
        </tr>
    </table>

   <div class="LblRqdDiv" style="margin: 5px 10px 5px 0px;" align="right">
    <img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7">&nbsp;<config:message i18nKey="configurator.indicaterequiredfield"/> </div>

<!-- ACCESS MANAGER SECTION -->
<table width="100%" border="0" cellpadding="0" cellspacing="0" title="">
    <tr>
        <td>
            <img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="10" />
        </td>
        <td class="ConLin" width="100%"><img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="1" />
        </td>
    </tr>
</table>

<div class="ConFldSetDiv">
<table title="" border="0" cellpadding="0" cellspacing="0">

    <!-- Main Label -->
    <tr>
        <td valign="top">
            <div class="ConFldSetLgdDiv"><config:message i18nKey="configurator.amsettings"/></div>
        </td>
    </tr>

    <!-- Server Settings -->
    <tr>
            <td colspan="2" valign="top">
            <div class="ConTblCl1Div"><span class="LblLev2Txt"><config:message i18nKey="configurator.serversettings"/></span></div>
            </td>
    </tr>

    <!-- Host Name Textbox -->
    <tr>
        <td valign="top">
            <div class="ConEmbTblCl1Div">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.serverurl"/></span></div>
        </td>
        <td valign="top">
                <div class="ConTblCl2Div"><input value="<%= serverURL%>"
                 name="SERVER_URL" id="psLbl1" size="50" class="TxtFld"></div>
</div>
        </td>
    </tr>

<!-- Cookie Domain Text Box -->
<tr>
<td valign="top">
<div class="ConEmbTblCl1Div">
<div class="ConTblCl1Div"><alt="<config:message i18nKey="configurator.optionalfield"/>" title="<config:message i18nKey="configurator.optionalfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.cookiedomain"/></span></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input value="<%= cookieDomain %>" name="COOKIE_DOMAIN" id="psLbl3" size="35" class="TxtFld"></div>
</td>
</tr>

<!-- Space between the section 
<tr><td colspan=2>&nbsp;</td></tr>
-->
         <!-- Administrator -->
         <tr>
            <td colspan="2" valign="top">
            <div class="ConTblCl1Div"><span class="LblLev2Txt"><config:message i18nKey="configurator.administrator"/></span></div>
            </td>
         </tr>

         <!-- Admin Password Text Box -->
         <tr> 
            <td valign="top">
            <div class="ConEmbTblCl1Div">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.name"/></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div">amAdmin</div>
</div>
            </td>
         </tr>

         <!-- Admin Password Text Box -->
         <tr>
            <td valign="top">
<div class="ConEmbTblCl1Div">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.password"/></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input type="password" value="" name="ADMIN_PWD" id="psLbl3" size="35" class="TxtFld"></div>
</div>
            </td>
         </tr>

         <!-- Confirm Admin Password Text Box -->
         <tr>
            <td valign="top">
<div class="ConEmbTblCl1Div">
            <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.confirmadminpasswd"/></span></div>
            </td>
            <td valign="top">
            <div class="ConTblCl2Div"><input type="password" value="" name="ADMIN_CONFIRM_PWD" id="psLbl3" size="35" class="TxtFld"></div>
</div>
            </td>
         </tr>

<!-- Space between the section  -->
<tr><td colspan=2>&nbsp;</td></tr>

         <!-- General Settings -->
         <tr>
            <td colspan="2" valign="top">
            <div class="ConTblCl1Div"><span class="LblLev2Txt"><config:message i18nKey="configurator.generalsettings"/></span></div>
            </td>
         </tr>
 
         <!-- Base Dir Text Box -->
         <tr>
             <td valign="top">
             <div class="ConEmbTblCl1Div">
             <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.configdirectory"/></span></div>
             </td>
             <td valign="top"> 
             <div class="ConTblCl2Div"><input value="<%= basedir %>" name="BASE_DIR" id="psLbl3" size="50" class="TxtFld"></div><div class="HlpFldTxt"><config:message i18nKey="configurator.amconfigdatadir"/></div>
             </td>
         </tr>
<!-- Space between the section -->
<!-- <tr><td colspan=2>&nbsp;</td></tr> -->

         <!-- Locale Text Box -->
         <tr>
             <td valign="top">
             <div class="ConEmbTblCl1Div">
             <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.platformlocale"/></span></div>
             </td>
             <td valign="top">
             <div class="ConTblCl2Div"><input value="<%= locale %>" name="PLATFORM_LOCALE" id="psLbl3" size="10" class="TxtFld"></div>
             </td>
         </tr>

         <!-- Encryption Key -->
         <tr> 
             <td valign="top">
             <div class="ConEmbTblCl1Div">
             <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.encryptionkey"/></span></div>
             </td>
             <td valign="top">
             <div class="ConTblCl2Div"><input value="<%= config.getServletContext().getAttribute("am.enc.pwd") %>" name="AM_ENC_KEY" id="psLbl3" size="50" class="TxtFld"></div>
             </td>
         </tr>


<tr><td colspan=2>&nbsp;</td></tr> 

</table>



<!-- CONFIGURATION STORE SECTION -->
<table width="100%" border="0" cellpadding="0" cellspacing="0" title=""><tr><td>
<img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="10" /></td><td class="ConLin" width="100%"><img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="1" /></td></tr></table>

<div class="ConFldSetDiv">
<div class="ConFldSetLgdDiv"><config:message i18nKey="configurator.configstoresettings"/></div>
<config:message i18nKey="configurator.configstoredescription"/>

<p>

<table>
    <tr>
        <td  valign="top">
            <span class="LblLev2Txt"><config:message i18nKey="configurator.type"/></span>
        </td>
        <td> 
            <table title="" border="0" cellpadding="0" cellspacing="0">

<!-- FLAT FILE CONFIGURATION SECTION -->
            <tr>
                <td colspan=2 valign="top">
                    <input type="radio" value="flatfile" name="DATA_STORE" id="psLbl2" size="35" checked="checked"><label for="psLbl2"><config:message i18nKey="configurator.filesystem"/></label></div>
                </td>
            </tr>

<!-- END FLAT FILE CONFIGURATION -->
            
<tr><td colspan=2>&nbsp;</td></tr>

<!-- DIRECTORY SERVER CONFIGURATION SECTION -->
<tr>
<td colspan=2 valign="top"> 
<input type="radio" value="dirServer" name="DATA_STORE" id="psLbl2" size="35"><label for="psLbl2"><config:message i18nKey="configurator.dirsvr"/></label>
</td>
</tr>

<tr>
<td colspan=2 valign="top"> 
<div class="ConEmbTblCl1Div"><span class="LblLev2Txt"><config:message i18nKey="configurator.serversettings"/></span></div>
</td>
</tr>

<!-- Directory Server Name -->
<tr>
<td valign="top">
<div class="ConEmbTblCl1Div"><div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.name"/></span></div></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input value="ds.opensso.java.net"  name="DIRECTORY_SERVER" id="psLbl1" size="50" class="TxtFld"></div>
</div>
</td>
</tr>

<!-- Directory Server Port -->
<tr>
<td valign="top">
<div class="ConEmbTblCl1Div"><div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.port"/></span></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input value="389"  name="DIRECTORY_PORT" id="psLbl1" size="5" class="TxtFld"></div>
</div>
</td>
</tr>

<!-- Root Suffix -->
<tr>
<td valign="top">
<div class="ConEmbTblCl1Div">
<div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.configdatasuffix"/></span></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input value="dc=opensso,dc=java,dc=net" name="ROOT_SUFFIX" id="psLbl1" size="50" class="TxtFld"></div>

</div>
</td>
</tr>

<tr><td colspan=2>&nbsp;</td></tr>

<!-- Root Suffix for Service Management-->
<tr>
<td valign="top">
<div class="ConEmbTblCl1Div">
<div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.configsmsdatasuffix"/></span></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input value="dc=opensso,dc=java,dc=net" name="SM_CONFIG_ROOT_SUFFIX" id="psLbl1" size="50" class="TxtFld"></div>

</div>
</td>
</tr>

<tr><td colspan=2>&nbsp;</td></tr>


<!-- Directory Server Admin Subsection -->
<tr>
<td colspan="2" valign="top">
<div class="ConEmbTblCl1Div"><span class="LblLev2Txt"><config:message i18nKey="configurator.dirsvradmin"/></span></div>
</td>
</tr>

<!-- Directory Manager Text Box -->
<tr>
<td valign="top">
<div class="ConEmbTblCl1Div">
<div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.dirsvradmindn"/></span></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input value="cn=Directory Manager" name="DS_DIRMGRDN" id="psLbl1" size="50" class="TxtFld"></div>
</div>
</td>
</tr>

<!-- Directory Server Admin Password Text Box -->
<tr>
<td valign="top">
<div class="ConEmbTblCl1Div"><div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.dirsvrpasswd"/></span></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input type="password" value="" name="DS_DIRMGRPASSWD" id="psLbl3" size="35" class="TxtFld"></div>
</div>
</td>
</tr>

<!-- Confirm Directory Server Admin Password Text Box -->
<tr>
<td valign="top">
<div class="ConEmbTblCl1Div">
<div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.confirmadminpasswd"/></span></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input type="password" value="" name="DS_CONFIRM_PWD" id="psLbl3" size="35" class="TxtFld"></div>
</div>
</td>
</tr>

<tr><td colspan=2>&nbsp;</td></tr>
<!-- </table> -->
<!-- END DIRECTORY SERVER -->

<tr>
<td valign="top">
<div class="ConEmbTblCl1Div">
<div class="ConTblCl1Div"><height="14" width="7">
<span class="LblLev2Txt"><config:message i18nKey="configurator.loaduserschema"/></span></div>
</td>
<td valign="top">
<div class="ConTblCl2Div"><input type="checkbox" value="sdkSchema" name="DS_UM_SCHEMA" id="psLbl3"></div><div class="HlpFldTxt"><config:message i18nKey="configurator.loadamsdkschema"/></div>

</td>
</tr>

</table>
<!-- END DIRECTORY SERVER -->

<table width="100%" border="0" cellpadding="0" cellspacing="0" title=""><tr><td><img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="10" /></td><td class="ConLin" width="100%"><img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="1" /></td></tr></table>

<!-- END DATA STORE CONFIGURATION -->
</form>
</body>
</html>

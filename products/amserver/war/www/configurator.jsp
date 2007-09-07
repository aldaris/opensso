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

   $Id: configurator.jsp,v 1.14 2007-09-07 20:29:56 jonnelson Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="com.sun.identity.setup.AMSetupServlet"%>
<%@ page import="com.sun.identity.setup.SetupConstants"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.servlet.ServletContext"%>

<%@taglib uri="/WEB-INF/configurator.tld" prefix="config" %>

<config:resBundle bundleName="amConfigurator"/>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Sun Java(TM) System Federated Access Manager</title>
    <link rel="stylesheet" type="text/css" href="com_sun_web_ui/css/css_ns6up.css">
    <meta name="Copyright"
         content="All Rights Reserved. Use is subject to license terms.">
    <link rel="shortcut icon" href="com_sun_web_ui/images/favicon/favicon.ico" type="image/x-icon">

    <script language="Javascript">
        function gotoLoginPage() {
            this.location.replace("./success.jsp");
        }

        function showDSDiv(um) {
            if (um) {
                document.forms['configurator'].elements['DS_UM_SCHEMA'].checked = false;
            }

            document.forms['configurator'].elements['DS_UM_SCHEMA'].disabled = um;
        }
    </script>
</head>

<body CLASS="DefBdy"
    LANG="en-US" 
    TEXT="#000000" 
    BACKGROUND="side-bg.gif" 
    DIR="LTR" 
    STYLE="background: url(side-bg.gif) no-repeat top left scroll">
<div style="margin:30px 30px 0px 300px">
        <!-- welcome message -->
    <div style="color: #50697d; font-size:18px">
        Welcome to the 
    </div>
    <div style="color: #f88017; font-size:24px">
        Sun Java(tm) System<br>
        Federated Access Manager<br>
        Configurator 
    </div>

<p>    
    <div style="color: #50697d; font-size:14px">
<%
    boolean result = false;
    int portnum;
    String hostname;
    String deployuri;
    String protocol;
    String basedir;
    String cookieDomain = "";
    int availableDSPort = 50389;
    String locale = "en_US";
    String adminPwd;
    String serverURL;
    String installType = "block";    
    boolean presetConfigDir = false;

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
        String type = request.getParameter("type");
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
        if (type == null) {
            String redirectURL = serverURL + deployuri + "/welcome.jsp";
            response.sendRedirect(redirectURL);
            return;
        } else {
            if (type.equals("simple")) {            
                installType = "none";
            } 
        }
        
        String configDir = null;
        try {
            ResourceBundle rb = ResourceBundle.getBundle(
                SetupConstants.BOOTSTRAP_PROPERTIES_FILE);
            configDir = rb.getString(SetupConstants.PRESET_CONFIG_DIR);
        } catch (MissingResourceException e) {
            //ignored because bootstrap properties file maybe absent.
        }

        if ((configDir == null) || (configDir.length() == 0)) {
            basedir = System.getProperty("user.home");
            if (File.separatorChar == '\\') {
                basedir = basedir.replace('\\', '/');
            }
        } else {
            presetConfigDir = true;
            basedir = configDir + "/" +
                SetupConstants.CONFIG_VAR_BOOTSTRAP_BASE_PREFIX +
            AMSetupServlet.getNormalizedRealPath(config.getServletContext());
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
        availableDSPort = 
            AMSetupServlet.getUnusedPort("localhost", 50389, 1000);
    }
%>


<form name="configurator" method="post" action="configurator.jsp" onsubmit="return true">
<input type="hidden" name="deployuri" value="<%= deployuri %>">
      
        <br>
        <div style="color: #50697d; font-size:18px">
    <% 
        if (installType.equals("none")) {
            %>Simple<%
        } else {
            %>Custom<%
        }
    %> 
    Configuration option selected...</div>
    
        <div id="config" style="display:<%= installType %>;">                

        <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr valign="bottom">
                <td align="right" nowrap="nowrap" valign="bottom">
                    <div class="TtlBtnDiv">
                        <input name="AMConfig.button1" class="Btn1" value="<config:message i18nKey="configurator.button1label"/>" onmouseover="javascript: if (this.disabled==0) this.className='Btn1Hov'" onmouseout="javascript: if (this.disabled==0) this.className='Btn1'"
                               onblur="javascript: if (this.disabled==0) this.className='Btn1'" onfocus="javascript: if (this.disabled==0) this.className='Btn1Hov'" type="submit">&nbsp;<input name="AMConfig.button2" class="Btn2" value="<config:message i18nKey="configurator.button2reset"/>" onmouseover="javascript: if
                                                                                                                                                                                         (this.disabled==0) this.className='Btn2Hov'" onmouseout="javascript: if (this.disabled==0) this.className='Btn2'" onblur="javascript: if (this.disabled==0) this.className='Btn2'" onfocus="javascript: if (this.disabled==0) this.className='Btn2Hov'" type="reset"> 
                                                                                                                                                                                     </div>
                </td>
            </tr>
        </table>
        <div class="LblRqdDiv" style="margin: 5px 10px 5px 0px;" align="right">
            <img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7">&nbsp;<config:message i18nKey="configurator.indicaterequiredfield"/> 
        </div>
   
        </div>

    <!-- ACCESS MANAGER SECTION -->
    <table title="" border="0" cellpadding="0" cellspacing="0" width="100%">
        <!-- spacer  -->
        <tr>
            <td colspan=2>&nbsp;</td>
        </tr>

        <!-- line separator -->
        <tr>
            <td colspan=2 class="ConLin" width="100%">
                <img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="1" />
            </td>
        </tr>

        <!-- spacer  -->
        <tr>
            <td colspan=2>&nbsp;</td>
        </tr>

        <!-- Main Label -->
        <tr>
            <td colspan=2 valign="top">
                <div class="ConFldSetLgdDiv"><config:message i18nKey="configurator.amsettings"/></div>
            </td>
        </tr>
        

        <!-- Administrator Settings -->
        <tr>
            <td colspan="2" valign="top">                         
                <div class="ConTblCl1Div">
                    <span class="LblLev2Txt"><config:message i18nKey="configurator.administrator"/></span>                         
                </div>
            </td>
        </tr>

        <!-- Admin Name -->
        <tr> 
            <td valign="top">
                <div class="ConEmbTblCl1Div">
                    <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.name"/></span></div>
                </div>
            </td>
            <td valign="top">
                <div class="ConTblCl2Div">amAdmin</div>
            </td>
        </tr>

        <!-- Admin Password  -->
        <tr>
            <td valign="top">                        
                <div class="ConEmbTblCl1Div">
                    <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.password"/></span></div>
                </div>
            </td>
            <td valign="top">
                <div class="ConTblCl2Div"><input type="password" value="" name="ADMIN_PWD" id="psLbl3" size="35" class="TxtFld"></div>
            </td>
        </tr>

        <!-- Confirm Admin Password -->
        <tr>
            <td valign="top">
                <div class="ConEmbTblCl1Div">
                    <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.confirmadminpasswd"/></span></div>
                </div>
            </td>
            <td valign="top">
                <div class="ConTblCl2Div"><input type="password" value="" name="ADMIN_CONFIRM_PWD" id="psLbl3" size="35" class="TxtFld"></div>
            </td>
        </tr>

        <!-- spacer  -->
        <tr>
            <td colspan=2>&nbsp;</td>
        </tr>
    </table>

    <div id="config" style="display:<%= installType %>;">
    <!-- Server Settings -->
    <table title="" border="0" cellpadding="0" cellspacing="0">                   
        <tr>
            <td colspan="2" valign="top">
                <div class="ConTblCl1Div"><span class="LblLev2Txt"><config:message i18nKey="configurator.serversettings"/></span></div>
            </td>
        </tr>

        <!-- Host Name Textbox -->
        <tr>
            <td valign="top">
                <div class="ConEmbTblCl1Div">
                    <div class="ConTblCl1Div">
                        <img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.serverurl"/></span>
                    </div>
                </div>
            </td>
            <td valign="top">
                <div class="ConTblCl2Div">
                    <input value="<%= serverURL%>" name="SERVER_URL" id="psLbl1" size="50" class="TxtFld">
                </div>                 
            </td>
        </tr>

        <!-- Cookie Domain Text Box -->
        <tr>
            <td valign="top">
                <div class="ConEmbTblCl1Div">
                    <div class="ConTblCl1Div"><alt="<config:message i18nKey="configurator.optionalfield"/>" title="<config:message i18nKey="configurator.optionalfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.cookiedomain"/></span></div>
                </div>
            </td>
            <td valign="top">
                <div class="ConTblCl2Div"><input value="<%= cookieDomain %>" name="COOKIE_DOMAIN" id="psLbl3" size="35" class="TxtFld"></div>
            </td>
        </tr>

        <!-- Locale Text Box -->
        <tr>
            <td valign="top">
                <div class="ConEmbTblCl1Div">
                    <div class="ConTblCl1Div">
                        <img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.platformlocale"/></span>
                    </div>
                </div>
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
                </div>
            </td>
            <td valign="top">
                <div class="ConTblCl2Div"><input value="<%= config.getServletContext().getAttribute("am.enc.pwd") %>" name="AM_ENC_KEY" id="psLbl3" size="50" class="TxtFld"></div>
            </td>
        </tr>

        <!-- Space between the section  -->
        <tr>
            <td colspan=2>&nbsp;</td>
        </tr>


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
                <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.configdirectory"/></span></div></div>
            </td>
            <td valign="top"> 
                <div class="ConTblCl2Div">
                    <% if (presetConfigDir) { %>
                    <%= basedir %>
                    <input type="hidden" value="<%= basedir %>" name="BASE_DIR">
                    <% } else { %>
                    <input value="<%= basedir %>" name="BASE_DIR" id="psLbl3" size="50" class="TxtFld">
                    <% } %>
                </div><div class="HlpFldTxt"><config:message i18nKey="configurator.amconfigdatadir"/></div>
            </td>
        </tr>

        <!-- Space between the section  -->
        <tr>
            <td colspan=2>&nbsp;</td>
        </tr>

    </table>

    <!-- CONFIGURATION STORE SECTION -->

        <table title="" border="0" cellpadding="0" cellspacing="0">
                <tr><td colspan=2>&nbsp;</td></tr>

                <!-- separator -->
                <tr>
                    <td colspan=2 class="ConLin" width="100%">
                        <img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="1" />
                    </td>
                </tr>                 

                <tr><td colspan=2>&nbsp;</td></tr>

            <!-- heading -->
            <tr>
                <td colspan=2>
                    <div class="ConFldSetLgdDiv">
                        <config:message i18nKey="configurator.configstoresettings"/>
                    </div>
                    <config:message i18nKey="configurator.configstoredescription"/>     
                </td>
            </tr>
            <tr><td colspan=2>&nbsp;</td></tr>                                                    

            <tr>
                <td colspan=2 valign="top"> 
                    <div class="ConTblCl1Div"><span class="LblLev2Txt"><config:message i18nKey="configurator.serversettings"/></span></div>
                </td>
            </tr>   

            <!-- data store type -->
            <tr>
                <td colspan=2>
                    <table>
                        <tr>
                            <td valign="top">
                                <div class="ConEmbTblCl1Div">
                                    <div class="ConTblCl1Div"><config:message i18nKey="configurator.type"/></div>
                                </div>
                            </td>
                            <td valign="top">  
                                <div class="ConTblCl2Div">
                                    <input type="radio" value="embedded" name="DATA_STORE" id="psLbl2" size="35" checked="checked" ">
                                    <label for="psLbl2"><config:message i18nKey="configurator.embedded"/></label>
                                    <br/>
                                    <input type="radio" value="dirServer" name="DATA_STORE" id="psLbl2" size="35" >
                                    <label for="psLbl2"><config:message i18nKey="configurator.remote"/></label>
                                    <br />
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>

            <!-- Root Suffix -->
            <tr>
                <td valign="top">
                    <div class="ConEmbTblCl1Div">
                        <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.configdatasuffix"/></span></div>
                    </div>
                </td>
                <td valign="top">
                    <div class="ConTblCl2Div"><input value="dc=opensso,dc=java,dc=net" name="ROOT_SUFFIX" id="psLbl1" size="50" class="TxtFld"></div>
                </td>
            </tr>

            <!-- Root Suffix for Service Management-->
            <tr>
                <td valign="top">
                    <div class="ConEmbTblCl1Div">
                        <div class="ConTblCl1Div">
                            <img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.configsmsdatasuffix"/></span>
                        </div>
                    </div>
                </td>
                <td valign="top">
                    <div class="ConTblCl2Div"><input value="dc=opensso,dc=java,dc=net" name="SM_CONFIG_ROOT_SUFFIX" id="psLbl1" size="50" class="TxtFld"></div>                                    
                </td>
            </tr>


            <tr><td colspan=2>&nbsp;</td></tr>  

            <!-- Directory Server Admin Subsection -->
            <tr>
                <td colspan="2" valign="top">
                    <div class="ConTblCl1Div"><span class="LblLev2Txt"><config:message i18nKey="configurator.dirsvradmin"/></span></div>
                </td>
            </tr>

            <!-- Server Name -->
            <tr>
                <td valign="top">
                    <div class="ConEmbTblCl1Div">
                        <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.name"/></span></div>
                    </div>
                </td>
                <td valign="top">
                    <div class="ConTblCl2Div"><input value="localhost"  name="DIRECTORY_SERVER" id="psLbl1" size="50" class="TxtFld"></div>
                </td>
            </tr>

            <!-- Server Port -->
            <tr>
                <td valign="top">
                    <div class="ConEmbTblCl1Div"><div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.port"/></span></div>
                    </div>
                </td>
                <td valign="top">
                    <div class="ConTblCl2Div"><input value="<%= availableDSPort %>"  name="DIRECTORY_PORT" id="psLbl1" size="5" class="TxtFld"></div>
                </td>
            </tr>

            <tr><td colspan=2>&nbsp;</td></tr>                   

            <!-- Directory Manager Text Box -->
            <tr>
                <td valign="top">
                    <div class="ConEmbTblCl1Div">
                        <div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.dirsvradmindn"/></span></div>
                    </div>
                </td>
                <td valign="top">
                    <div class="ConTblCl2Div"><input value="cn=Directory Manager" name="DS_DIRMGRDN" id="psLbl1" size="50" class="TxtFld"></div>
                </td>
            </tr>

            <!-- Directory Server Admin Password Text Box -->
            <tr>
                <td valign="top">
                    <div class="ConEmbTblCl1Div"><div class="ConTblCl1Div"><img src="com_sun_web_ui/images/other/required.gif" alt="<config:message i18nKey="configurator.requiredfield"/>" title="<config:message i18nKey="configurator.requiredfield"/>" height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.dirsvrpasswd"/></span></div>
                    </div>
                </td>
                <td valign="top">
                    <div class="ConTblCl2Div">
                        <input type="password" value="" name="DS_DIRMGRPASSWD" id="psLbl3" size="35" class="TxtFld">
                    </div>
                </td>
            </tr>

            <tr><td colspan=2>&nbsp;</td></tr>

            <!-- load schema -->
            <tr>
                <td valign="top">
                    <div class="ConEmbTblCl1Div">
                        <div class="ConTblCl1Div"><height="14" width="7"><span class="LblLev2Txt"><config:message i18nKey="configurator.loaduserschema"/></span></div>
                    </div>
                </td>
                <td valign="top">
                    <div class="ConTblCl2Div">
                        <input type="checkbox" value="sdkSchema" name="DS_UM_SCHEMA" id="psLbl3">
                    </div>
                    <div class="HlpFldTxt">
                        <config:message i18nKey="configurator.loadamsdkschema"/>
                    </div>
                </td>
            </tr>    

            <!-- separator -->


            <tr><td colspan=2>&nbsp;</td></tr>
        </table>    
    </div>

    <!-- lower buttons -->
    <table border="0" cellpadding="0" cellspacing="0" width="100%"> 
        <tr>
            <td class="ConLin" width="100%">
                <img src="com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="1" />
            </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr valign="bottom">
            <td align="right" nowrap="nowrap" valign="bottom">
                <div class="TtlBtnDiv">
                    <input name="AMConfig.button1" class="Btn1" value="<config:message i18nKey="configurator.button1label"/>" onmouseover="javascript: if (this.disabled==0) this.className='Btn1Hov'" onmouseout="javascript: if (this.disabled==0) this.className='Btn1'"
                           onblur="javascript: if (this.disabled==0) this.className='Btn1'" onfocus="javascript: if (this.disabled==0) this.className='Btn1Hov'" type="submit">&nbsp;<input name="AMConfig.button2" class="Btn2" value="<config:message i18nKey="configurator.button2reset"/>" onmouseover="javascript: if
                                                                                                                                                                                     (this.disabled==0) this.className='Btn2Hov'" onmouseout="javascript: if (this.disabled==0) this.className='Btn2'" onblur="javascript: if (this.disabled==0) this.className='Btn2'" onfocus="javascript: if (this.disabled==0) this.className='Btn2Hov'" type="reset"> 
                                                                                                                                                                                 </div>
            </td>
        </tr>
    </table>      
</div>
</form>
</body>
</html>

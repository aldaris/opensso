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
  
   $Id: amadm.jsp,v 1.3 2006-12-08 21:02:38 veiming Exp $
  
   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page import="com.sun.identity.cli.*" %>
<%@ page import="com.iplanet.sso.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Sun Java(TM) System Access Manager</title>
    <link rel="stylesheet" type="text/css" href="com_sun_web_ui/css/css_ns6up.css">
    <link rel="shortcut icon" href="com_sun_web_ui/images/favicon/favicon.ico" type="image/x-icon">

<script language="Javascript">
    var listboxes = new Array();
    
    function addOption(opt) {
        var frm = document.forms[0];
        var lblb = frm.elements[opt + 'lblb'];
        var lbValue = strTrim(lblb.value);

        if (lbValue != '') {
            var selectBox = frm.elements[opt];
            var optList = selectBox.options;
            optList[optList.length] = new Option(lbValue, lbValue);
            lblb.value = '';
        }
    }

    function strTrim(str){
        return str.replace(/^\s+/,'').replace(/\s+$/,'')
    }

    function removeSelFromList(opt) {
        var frm = document.forms[0];
        var list = frm.elements[opt];

        if (list != null) {
            var optList = list.options;
            var size = optList.length;

            for (var i = size-1; i >= 0; --i) {
                var opt = optList[i];
                if ((opt.selected) && (opt.value != "")) {
                    optList[i] = null;
                }
            }
        }
    }
    
    function selectListBoxes(frm) {
        for (var i = 0; i < listboxes.length; i++) {
            var list = frm.elements[listboxes[i]];
            for (var j = 0; j < list.options.length; j++) {
                list.options[j].selected = true;
            }
        }
    }

</script>

<%!
    CommandManager cmdMgr = null;
    BufferOutputWriter outputWriter = new BufferOutputWriter();

    private String autogenUI(String cmdName)
        throws Exception {
        ResourceBundle rb = cmdMgr.getResourceBundle();
        StringBuffer buff = new StringBuffer();

        buff.append(MessageFormat.format(rb.getString("web-interface-cmd-name"),
            cmdName))
            .append("<br />");
        SubCommand cmd = cmdMgr.getSubCommand(cmdName);
        if (cmd == null) {
            throw new Exception(rb.getString(
                "web-interface-cmd-name-not-found"));
        }
        buff.append(cmd.getDescription())
            .append("<br /><br />");
        buff.append("<form action=\"amadm.jsp?cmd=")
            .append(cmdName)
            .append("&submit=\" method=\"post\" ")
            .append("onSubmit=\"selectListBoxes(this)\">");
        buff.append("<table border=0>");

        for (Iterator i = cmd.getMandatoryOptions().iterator(); i.hasNext(); ) {
            genUI(cmd, (String)i.next(), true, buff);
        }
        for (Iterator i = cmd.getOptionalOptions().iterator(); i.hasNext(); ) {
            genUI(cmd, (String)i.next(), false, buff);
        }

        buff.append("<tr><td colspan=2 align=\"center\">")
            .append("<input type=\"submit\" value=\"submit\"/>&nbsp;")
            .append("<input type=\"reset\" value=\"reset\"/></td></tr>");
        buff.append("</table></form>");
        return buff.toString();
    }

    private void genUI(
        SubCommand cmd,
        String opt,
        boolean mandatory,
        StringBuffer buff
    ) {
        if (!cmd.isOptionAlias(opt) && !isAuthField(opt) &&
            !isIgnored(cmd, opt)
        ) {
            String label = opt;
            if (opt.equals("xmlfile")) {
                label = "xml";
            } else if (opt.equals("attributeschemafile")) {
                label = "attributeschemaxml";
            }
            buff.append("<tr><td valign=\"top\">")
                .append(label);
            if (mandatory) {
                buff.append("<font color=\"red\">*</font>");
            }
            buff.append("</td>");

            String shortOptionName = cmd.getShortOptionName(opt);
            if (!shortOptionName.equals(shortOptionName.toLowerCase())) {
                buff.append("<td><textarea cols=75 rows=30 name=\"")
                    .append(opt)
                    .append("\"></textarea>");
            } else if (cmd.isUnaryOption(opt)) {
                buff.append("<td><input type=\"checkbox\" name=\"")
                    .append(opt)
                    .append("\" value=\"\" />");
            } else if (cmd.isBinaryOption(opt)) {
                if (opt.indexOf("password") == -1) {
                    buff.append("<td><input type=\"text\" name=\"")
                        .append(opt)
                        .append("\" />");
                } else {
                    buff.append("<td><input type=\"password\" name=\"")
                        .append(opt)
                        .append("\" />");
                }
            } else {
                buff.append("<td><table border=0><tr><td>")
                    .append("<select name=\"")
                    .append(opt)
                    .append("\" size=\"10\" style=\"width:200\" ")
                    .append("width=\"200\" multiple=\"true\">")
                    .append("<td><input type=\"button\" value=\"Remove\"")
                    .append(" onClick=\"removeSelFromList('")
                    .append(opt)
                    .append("'); return false;\" />")
                    .append("</td></tr><tr><td colspan=2>")
                    .append("<input type=\"text\" name=\"")
                    .append(opt)
                    .append("lblb")
                    .append("\" size=\"30\"/>&nbsp;")
                    .append("<input type=\"button\" value=\"Add\"")
                    .append(" onClick=\"addOption('")
                    .append(opt)
                    .append("'); return false;\" />")
                    .append("</td></tr></table>");
                buff.append("\n<script language=\"javascript\">")
                    .append("listboxes[listboxes.length] ='")
                    .append(opt)
                    .append("';")
                    .append("</script>");
            }
            buff.append("<br />")
                .append(cmd.getOptionDescription(opt))
                .append("<br /><br />")
                .append("</td></tr>");
        }
    }

    private boolean isAuthField(String opt) {
        return opt.equals("adminid") || opt.equals("password");
    }

    private boolean isIgnored(SubCommand cmd, String opt) {
        return opt.equals("continue") || opt.equals("outfile") ||
            (opt.equals("datafile") &&
                cmd.isSupportedOption("attributevalues")) ||
            (opt.equals("datafile") &&
                cmd.isSupportedOption("choicevalues"));
    }
    
    private static String escapeTags(String html) {
        html = html.replace("&", "&amp;");
        return html.replace("<", "&lt;");
    }
%>

<%
try {
    Map env = new HashMap();
    env.put(CLIConstants.SYS_PROPERTY_OUTPUT_WRITER, outputWriter);
    env.put(CLIConstants.ARGUMENT_LOCALE, request.getLocale());
    env.put(CLIConstants.SYS_PROPERTY_DEFINITION_FILES,
        "com.sun.identity.cli.AccessManager");
    env.put(CLIConstants.SYS_PROPERTY_COMMAND_NAME, "amadm");
    env.put(CLIConstants.WEB_ENABLED_URL, "amadm.jsp");
    cmdMgr = new CommandManager(env);
} catch (Exception e) {
    out.println(e);
    return;
}
%>

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

<table cellpadding=5>
<tr>
<td>

<pre>
<%
    try {
        SSOTokenManager manager = SSOTokenManager.getInstance();
        SSOToken ssoToken = manager.createSSOToken(request);
        manager.validateToken(ssoToken);
       
        String cmdName = request.getParameter("cmd");
        if (cmdName != null) {
            out.println("<a href=\"amadm.jsp\">" +
                cmdMgr.getResourceBundle().getString(
                    "web-interface-goto-main-page") + "</a><br /><br />");
            String submit = request.getParameter("submit");
            if (submit == null) {
                out.println(autogenUI(cmdName));
            } else {
                List list = new ArrayList();
                Map map = request.getParameterMap();
                for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
                    String key = (String)i.next();
                    if (!key.equals("submit") && !key.equals("cmd") &&
                        !key.endsWith("lblb")
                    ) {
                        String[] values = (String[])map.get(key);
                        List temp = new ArrayList();
                        for (int j = 0; j < values.length; j++) {
                            String str = values[j];
                            str = str.trim();
                            if (str.length() > 0) {
                                temp.add(str);
                            }
                        }

                        if (!temp.isEmpty()) {
                            list.add("--" + key);
                            list.addAll(temp);
                        }
                    }
                }

                int sz = list.size();
                String[] args = new String[sz+1];
                args[0] = cmdName;
                for (int i = 0; i < sz; i++) {
                    args[i+1] = (String)list.get(i);
                }
                
                CLIRequest req = new CLIRequest(null, args, ssoToken);
                cmdMgr.addToRequestQueue(req);
            }
            cmdMgr.serviceRequestQueue();
            out.println(escapeTags(outputWriter.getBuffer()));
        } else {
            String[] arg = {"--help"};
            CLIRequest req = new CLIRequest(null, arg);
            cmdMgr.addToRequestQueue(req);
            cmdMgr.serviceRequestQueue();
            String strHelp = outputWriter.getBuffer();
            int idx = strHelp.indexOf("  <a ");
            if (idx != -1) {
                strHelp = strHelp.substring(idx);
            }
            out.println(strHelp);
        }

        outputWriter.clearBuffer();
    } catch (SSOException e) {
        response.sendRedirect("UI/Login?goto=../amadm.jsp");
    } catch (CLIException e) {
        out.println(e);
    }
%>

</pre>
</td></tr>
</table>
</body></html>

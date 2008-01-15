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

   $Id: CreateHostedIDP.jsp,v 1.1 2008-01-15 06:44:20 veiming Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page info="CreateHostedIDP" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.task.CreateHostedIDPViewBean"
    fireChildDisplayEvents="true" >

<cc:i18nbundle baseName="amConsole" id="amConsole"
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>

<cc:header name="hdrCommon" pageTitle="webconsole.title" bundleID="amConsole" copyrightYear="2008" fireDisplayEvents="true">

<link rel="stylesheet" type="text/css" href="../console/css/opensso.css" />

<script language="javascript" src="../console/js/am.js"></script>
<script language="javascript" src="../com_sun_web_ui/js/dynamic.js"></script>

<div id="main" style="position: absolute; margin: 0; border: none; padding: 0; width:auto; height:101%;">
<cc:form name="CreateHostedIDP" method="post">
<jato:hidden name="szCache" />
<script language="javascript">
    function confirmLogout() {
        return confirm("<cc:text name="txtLogout" defaultValue="masthead.logoutMessage" bundleID="amConsole"/>");
    }

    function openWindow(fieldName) {
        selectWin = window.open('../federation/FileChooser', fieldName,
            'height=650,width=650,top=' +
            ((screen.height-(screen.height/2))-(500/2)) +
            ',left=' +
            ((screen.width-650)/2) +
            ',scrollbars,resizable');
        selectWin.focus();
    }

    function metadataOptionSelect(radio) {
        var infodiv = document.getElementById("info");
        var metadiv = document.getElementById("meta");
        hasMetaData = radio.value;
        if (radio.value == 'yes') {
            infodiv.style.display = 'none';
            metadiv.style.display = 'block';
        } else {
            infodiv.style.display = 'block';
            metadiv.style.display = 'none';
        }
    }
</script>

<cc:primarymasthead name="mhCommon" bundleID="amConsole"  logoutOnClick="return confirmLogout();"/>
<table border="0" cellpadding="10" cellspacing="0" width="100%">
    <tr>
        <td>
        <cc:alertinline name="ialertCommon" bundleID="amConsole" />
        </td>
    </tr>
</table>

<%-- PAGE CONTENT --------------------------------------------------------- --%>
<cc:pagetitle name="pgtitle" bundleID="amConsole" pageTitleText="page.title.configure.hosted.idp" showPageTitleSeparator="true" viewMenuLabel="" pageTitleHelpMessage="" showPageButtonsTop="true" showPageButtonsBottom="false" />

<cc:propertysheet name="propertyAttributes" bundleID="amConsole" showJumpLinks="false"/>

</cc:form>
</cc:header>
</div>
<div id="dlg" class="dvs"></div>

<script language="javascript">
    var msgConfiguring = "<cc:text name="txtConfiguring" defaultValue="configure.provider.waiting" bundleID="amConsole" escape="false" />";
    var msgConfigured = "<cc:text name="txtConfigured" defaultValue="configure.provider.done" bundleID="amConsole" escape="false" />";
    var msgError = "<cc:text name="txtConfigured" defaultValue="configure.provider.error" bundleID="amConsole" escape="false" />";

    var hasMetaData = 'yes';
    var frm = document.forms['CreateHostedIDP'];
    var btn1 = frm.elements['CreateHostedIDP.button1'];
    btn1.onclick = submitPage;
    var ajaxObj = getXmlHttpRequestObject();
    var data = '';
    var userLocale = "<% viewBean.getUserLocale().toString(); %>";

    function submitPage() {
        fade();
        document.getElementById('dlg').innerHTML = '<center>' + 
        msgConfiguring + '</center>';
        var url = "../console/ajax/AjaxProxy.jsp";
        var params = 'locale=' + userLocale +
            '&class=com.sun.identity.workflow.CreateHostedIDP' + getData();
        ajaxPost(ajaxObj, url, params, configured);
        return false;
    }

    function getData() {
        if (hasMetaData == "yes") {
            return "&metadata=" +
            escape(frm.elements['CreateHostedIDP.tfMetadataFile'].value) +
            "&extendeddata=" +
            escape(frm.elements['CreateHostedIDP.tfExtendedFile'].value);
        } else {
            var realm = frm.elements['CreateHostedIDP.tfRealm'].value;
            var metaalias = frm.elements['CreateHostedIDP.tfMetaAlias'].value;

            if (metaalias.indexOf('/') == 0) {
                metaalias = metaalias.substring(1);
            }
            metaalias = (realm == "/") ? "/" + metaalias :
                realm + "/" + metaalias;

            return "&entityId=" +
            escape(frm.elements['CreateHostedIDP.tfEntityId'].value) +
            "&metaalias=" + escape(metaalias) +
            "&realm=" + escape(realm) +
            "&idpecert=" +
            escape(frm.elements['CreateHostedIDP.tfEncKey'].value) +
            "&idpscert=" +
            escape(frm.elements['CreateHostedIDP.tfSigningKey'].value) +
            "&cot=" +
            escape(frm.elements['CreateHostedIDP.tfCOT'].value);
        }
    }

    function createRemoteSP() {
        var cot = frm.elements['CreateHostedIDP.tfCOT'].value;
        document.location = 'CreateRemoteSP?cot=' + cot + '&' + data;
    }

    function configured() {
        if (ajaxObj.readyState == 4) {
            var result = ajaxObj.responseText;
            var status = result.substring(0, result.indexOf('|'));
            var result = result.substring(result.indexOf('|') +1);
            var msg = '';
            if (status == 0) {
                var idx = result.indexOf('|||');
                data = result.substring(idx +3);
                result = result.substring(0, idx);
                msg = '<center><p>' + result + '</p></center>';
                msg = msg + '<center>' +  msgConfigured + '</center>';
            } else {
                msg = '<center><p>' + result + '</p></center>';
		msg = msg + '<center>' +  msgError + '</center>';
            }
            document.getElementById('dlg').innerHTML = msg;
        }
    }
</script>

</jato:useViewBean>

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

   $Id: CreateRemoteSP.jsp,v 1.2 2008-01-17 06:36:28 veiming Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page info="CreateRemoteSP" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.task.CreateRemoteSPViewBean"
    fireChildDisplayEvents="true" >

<cc:i18nbundle baseName="amConsole" id="amConsole"
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>

<cc:header name="hdrCommon" pageTitle="webconsole.title" bundleID="amConsole" copyrightYear="2008" fireDisplayEvents="true">

<link rel="stylesheet" type="text/css" href="../console/css/opensso.css" />
<script language="javascript" src="../console/js/am.js"></script>
<script language="javascript" src="../com_sun_web_ui/js/dynamic.js"></script>

<div id="main" style="position: absolute; margin: 0; border: none; padding: 0; width:auto; height:101%;">
<cc:form name="CreateRemoteSP" method="post">
<jato:hidden name="szCache" />
<script language="javascript">
    function confirmLogout() {
        return confirm("<cc:text name="txtLogout" defaultValue="masthead.logoutMessage" bundleID="amConsole"/>");
    }
    function openWindow(fieldName) {
        selectWin = window.open('../federation/FileUploader', fieldName,
            'height=300,width=650,top=' +
            ((screen.height-(screen.height/2))-(500/2)) +
            ',left=' + ((screen.width-650)/2));
        selectWin.focus();
    }

    function metaOptionSelect(radio) {
        if (radio.value == 'url') {
            frm.elements['CreateRemoteSP.tfMetadataFileURL'].style.display = '';
            frm.elements['CreateRemoteSP.btnMetadata'].style.display = 'none';
            document.getElementById('metadatafilename').style.display = 'none';
        } else {
            frm.elements['CreateRemoteSP.tfMetadataFileURL'].style.display = 'none';
            frm.elements['CreateRemoteSP.btnMetadata'].style.display = '';
            document.getElementById('metadatafilename').style.display = '';
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
<cc:pagetitle name="pgtitle" bundleID="amConsole" pageTitleText="page.title.configure.remote.sp" showPageTitleSeparator="true" viewMenuLabel="" pageTitleHelpMessage="" showPageButtonsTop="true" showPageButtonsBottom="false" />

<cc:propertysheet name="propertyAttributes" bundleID="amConsole" showJumpLinks="false"/>

</cc:form>
</cc:header>
</div>
<div id="dlg" class="dvs"></div>

<script language="javascript">
    var msgConfiguring = "<cc:text name="txtConfiguring" defaultValue="configure.provider.waiting" bundleID="amConsole" escape="false" />";
    var msgConfigured = "<cc:text name="txtConfigured" defaultValue="configure.service.provider.done" bundleID="amConsole" escape="false" />";
    var msgError = "<cc:text name="txtConfigured" defaultValue="configure.provider.error" bundleID="amConsole" escape="false" />";

    var frm = document.forms['CreateRemoteSP'];
    var btn1 = frm.elements['CreateRemoteSP.button1'];
    btn1.onclick = submitPage;
    var ajaxObj = getXmlHttpRequestObject();
    var userLocale = "<% viewBean.getUserLocale().toString(); %>";

    function submitPage() {
	fade();
    	document.getElementById('dlg').innerHTML = '<center>' + 
            msgConfiguring + '</center>';
        var url = "../console/ajax/AjaxProxy.jsp";
        var params = 'locale=' + userLocale +
            '&class=com.sun.identity.workflow.CreateRemoteSP' + getData();
        ajaxPost(ajaxObj, url, params, configured);
	return false;
    }

    function getData() {
        var realm = frm.elements['CreateRemoteSP.tfRealm'].value;
        var metaRadio = getRadioVal('CreateRemoteSP.radioMeta');
        var meta = (metaRadio == 'url') ?
            frm.elements['CreateRemoteSP.tfMetadataFileURL'].value :
            frm.elements['CreateRemoteSP.tfMetadataFile'].value;

        return "&metadata=" + escape(meta) +
            "&realm=" + escape(realm) +
            "&cot=" +
            escape(frm.elements['CreateRemoteSP.tfcot'].value);
    }

    function configured() {
	if (ajaxObj.readyState == 4) {
            var result = ajaxObj.responseText;
            var status = result.substring(0, result.indexOf('|'));
            var result = result.substring(result.indexOf('|') +1);
            var msg = '<center><p>' + result + '</p></center>';
            if (status == 0) {
		msg = msg + '<center>' +  msgConfigured + '</center>';
            } else {
		msg = msg + '<center>' +  msgError + '</center>';
            }
    	    document.getElementById('dlg').innerHTML = msg;
        }
    }
    frm.elements['CreateRemoteSP.btnMetadata'].style.display = 'none';
</script>

</jato:useViewBean>

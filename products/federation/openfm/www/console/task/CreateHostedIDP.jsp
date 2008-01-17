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

   $Id: CreateHostedIDP.jsp,v 1.2 2008-01-17 06:36:28 veiming Exp $

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
        selectWin = window.open('../federation/FileUploader', fieldName,
            'height=300,width=650,top=' +
            ((screen.height-(screen.height/2))-(500/2)) +
            ',left=' + ((screen.width-650)/2));
        selectWin.focus();
    }

    function metadataOptionSelect(radio) {
        var infodiv = document.getElementById("info");
        var metadiv = document.getElementById("meta");
        hasMetaData = radio.value;
        if (radio.value == 'yes') {
            infodiv.style.display = 'none';
            metadiv.style.display = 'block';
            document.getElementById('cotsection').style.display = 'none';
            document.getElementById('cotq').style.display = 'none';
            document.getElementById('cottf').style.display = 'none';
            document.getElementById('cotchoice').style.display = 'none';
        } else {
            infodiv.style.display = 'block';
            metadiv.style.display = 'none';
            document.getElementById('cotsection').style.display = 'display';
            var realm = frm.elements['CreateHostedIDP.tfRealm'].value;
            getCircleOfTrust(realm);
        }
    }

    function metaOptionSelect(radio) {
        if (radio.value == 'url') {
            frm.elements['CreateHostedIDP.tfMetadataFileURL'].style.display = '';
            frm.elements['CreateHostedIDP.btnMetadata'].style.display = 'none';
            document.getElementById('metadatafilename').style.display = 'none';
        } else {
            frm.elements['CreateHostedIDP.tfMetadataFileURL'].style.display = 'none';
            frm.elements['CreateHostedIDP.btnMetadata'].style.display = '';
            document.getElementById('metadatafilename').style.display = '';
        }
    }

    function extendedOptionSelect(radio) {
        if (radio.value == 'url') {
            frm.elements['CreateHostedIDP.tfExtendedFileURL'].style.display = '';
            frm.elements['CreateHostedIDP.btnExtendedFile'].style.display = 'none';
            document.getElementById('extendedfilename').style.display = 'none';
        } else {
            frm.elements['CreateHostedIDP.tfExtendedFileURL'].style.display = 'none';
            frm.elements['CreateHostedIDP.btnExtendedFile'].style.display = '';
            document.getElementById('extendedfilename').style.display = '';
        }
    }

    function realmSelect(radio) {
    	getCircleOfTrust(radio.value);
    }

    function cotOptionSelect(radio) {
        var ans = radio.value;
        if (ans == 'yes') {
            document.getElementById('cotchoice').style.display = 'block';
            document.getElementById('cottf').style.display = 'none';
            frm.elements['CreateHostedIDP.tfCOT'].value = '';
        } else {
            document.getElementById('cotchoice').style.display = 'none';
            document.getElementById('cottf').style.display = 'block';
        }
    }

    function getExtendedData() {
        var extRadio = getRadioVal('CreateHostedIDP.radioExtendedData');
        var extended = (extRadio == 'url') ?
            frm.elements['CreateHostedIDP.tfExtendedFileURL'].value :
            frm.elements['CreateHostedIDP.tfExtendedFile'].value;
        extended = extended.replace(/^\s+/, "");
        extended = extended.replace(/\s+$/, "");
        return extended;
    }

    function getCircleOfTrustFromExt() {
        var extended = getExtendedData();
        if (extended.length == 0) {
            return;
        }

        fade();
        document.getElementById('dlg').innerHTML = '<center>' + 
            msgGetCOTs + '</center>';
        var url = "../console/ajax/AjaxProxy.jsp";
        var params = 'locale=' + userLocale +
            '&class=com.sun.identity.workflow.GetCircleOfTrusts' + 
            '&extendeddata=' + escape(extended);
        ajaxPost(ajaxObj, url, params, circleOfTrust);
    }

    function hideRealm() {
        var frm = document.forms['CreateHostedIDP'];
        var realmobj = frm.elements['CreateHostedIDP.tfRealm'];
        if (realmobj.options.length < 2) {
            document.getElementById('realmlbl').style.display = 'none';
            document.getElementById('realmfld').style.display = 'none';
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
    hideRealm();

    var msgConfiguring = "<cc:text name="txtConfiguring" defaultValue="configure.provider.waiting" bundleID="amConsole" escape="false" />";
    var msgConfigured = "<cc:text name="txtConfigured" defaultValue="configure.provider.done" bundleID="amConsole" escape="false" />";
    var msgError = "<cc:text name="txtConfigured" defaultValue="configure.provider.error" bundleID="amConsole" escape="false" />";
    var closeBtn = "<cc:text name="txtConfigured" defaultValue="ajax.close.button" bundleID="amConsole" escape="false" />";
    var msgGetCOTs = "<cc:text name="txtConfigured" defaultValue="configure.provider.get.cots" bundleID="amConsole" escape="false" />";

    var hasMetaData = 'no';
    var frm = document.forms['CreateHostedIDP'];
    var btn1 = frm.elements['CreateHostedIDP.button1'];
    btn1.onclick = submitPage;
    var ajaxObj = getXmlHttpRequestObject();
    var data = '';
    var userLocale = "<% viewBean.getUserLocale().toString(); %>";

    function submitPage() {
        if (document.getElementById('cotsection').style.display != 'block') {
            var extended = getExtendedData();
            if (extended.length > 0) {
                if (hasMetaData) {
                    getCircleOfTrustFromExt();
                } else {
                    var realm = frm.elements['CreateHostedIDP.tfRealm'].value;
                    getCircleOfTrust(realm);
                }
                return false;
            }
        }
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
        var cot;
        var cotRadio = getRadioVal('CreateHostedIDP.radioCOT');
        if (cotRadio == "yes") {
            cot = frm.elements['CreateHostedIDP.choiceCOT'].value;
        } else {
            cot = frm.elements['CreateHostedIDP.tfCOT'].value;
        }
        if (hasMetaData == "yes") {
            var metaRadio = getRadioVal('CreateHostedIDP.radioMeta');
            var meta = (metaRadio == 'url') ?
                frm.elements['CreateHostedIDP.tfMetadataFileURL'].value :
                frm.elements['CreateHostedIDP.tfMetadataFile'].value;
            var extRadio = getRadioVal('CreateHostedIDP.radioExtendedData');
            var extended = (extRadio == 'url') ?
                frm.elements['CreateHostedIDP.tfExtendedFileURL'].value :
                frm.elements['CreateHostedIDP.tfExtendedFile'].value;

            return "&metadata=" + escape(meta) +
                "&extendeddata=" + escape(extended) +
                "&cot=" + escape(cot);
        } else {
            var realm = frm.elements['CreateHostedIDP.tfRealm'].value;
            return "&entityId=" +
            escape(frm.elements['CreateHostedIDP.tfEntityId'].value) +
            "&realm=" + escape(realm) +
            "&idpecert=" +
            escape(frm.elements['CreateHostedIDP.tfEncKey'].value) +
            "&idpscert=" +
            escape(frm.elements['CreateHostedIDP.tfSigningKey'].value) +
            "&cot=" + escape(cot);
        }
    }

    function getCircleOfTrust(realm) {
        var url = "../console/ajax/AjaxProxy.jsp";
        var params = 'locale=' + userLocale +
            '&class=com.sun.identity.workflow.GetCircleOfTrusts' + 
            '&realm=' + escape(realm);
        ajaxPost(ajaxObj, url, params, circleOfTrust);
    }

    function circleOfTrust() {
        if (ajaxObj.readyState == 4) {
            var result = ajaxObj.responseText;
            var status = result.substring(0, result.indexOf('|'));
            var result = result.substring(result.indexOf('|') +1);
            var msg = '';
            if (status == 0) {
                document.getElementById('cotsection').style.display = 'block';
                result = result.replace(/^\s+/, '');
                result = result.replace(/\s+$/, '');
                if (result.length == 0) {
                    document.getElementById('cotq').style.display = 'none';
                    document.getElementById('cotchoice').style.display = 'none';
                    document.getElementById('cottf').style.display = 'block';
                    chooseRadio('CreateHostedIDP.radioCOT', 'no');
                } else {
                    var cots = result.split('|');
                    var choiceCOT = frm.elements['CreateHostedIDP.choiceCOT'];
                    for (var i = 0; i < cots.length; i++) {
                        choiceCOT.options[i] = new Option(cots[i], cots[i]);
                    }
                    document.getElementById('cotq').style.display = 'block';
                    document.getElementById('cotchoice').style.display = 'block';
                    document.getElementById('cottf').style.display = 'none';
                    chooseRadio('CreateHostedIDP.radioCOT', 'yes');
                }
                focusMain();
            } else {
                msg = '<center><p>' + result + '</p></center>';
	        msg = msg + '<center>' +  closeBtn + '</center>';
                document.getElementById('dlg').innerHTML = msg;
                document.getElementById('cotsection').style.display = 'none';
                ajaxObj = getXmlHttpRequestObject();
            }
        }
    }

    function chooseRadio(name, value) {
	var r = frm.elements[name];
        for (var i = 0; i < r.length; i++) {
	    if (r[i].value == value) {
                r[i].checked = true;
            }
        }
    }

    function getRadioVal(name) {
	var r = frm.elements[name];
        for (var i = 0; i < r.length; i++) {
            if (r[i].checked) {
                return r[i].value;
            }
        }
    }

    function createRemoteSP() {
        var cot = frm.elements['CreateHostedIDP.tfCOT'].value;
        document.location.replace('CreateRemoteSP?cot=' + cot + '&' + data);
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
                ajaxObj = getXmlHttpRequestObject();
            }
            document.getElementById('dlg').innerHTML = msg;
        }
    }

    frm.elements['CreateHostedIDP.tfMetadataFileURL'].style.display = 'none';
    frm.elements['CreateHostedIDP.tfExtendedFileURL'].style.display = 'none';
    getCircleOfTrust('/');
</script>

</jato:useViewBean>

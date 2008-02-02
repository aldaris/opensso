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

   $Id: CreateHostedSP.jsp,v 1.2 2008-02-02 03:32:16 veiming Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page info="CreateHostedSP" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.task.CreateHostedSPViewBean"
    fireChildDisplayEvents="true" >

<cc:i18nbundle baseName="amConsole" id="amConsole"
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>

<cc:header name="hdrCommon" pageTitle="webconsole.title" bundleID="amConsole" copyrightYear="2008" fireDisplayEvents="true">

<link rel="stylesheet" type="text/css" href="../console/css/opensso.css" />

<script language="javascript" src="../console/js/am.js"></script>
<script language="javascript" src="../com_sun_web_ui/js/dynamic.js"></script>

<div id="main" style="position: absolute; margin: 0; border: none; padding: 0; width:auto; height:101%;">
<cc:form name="CreateHostedSP" method="post">
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
            var realm = frm.elements['CreateHostedSP.tfRealm'].value;
            getCircleOfTrust(realm);
        }
    }

    function metaOptionSelect(radio) {
        if (radio.value == 'url') {
            frm.elements['CreateHostedSP.tfMetadataFileURL'].style.display = '';
            frm.elements['CreateHostedSP.btnMetadata'].style.display = 'none';
            document.getElementById('metadatafilename').style.display = 'none';
        } else {
            frm.elements['CreateHostedSP.tfMetadataFileURL'].style.display = 'none';
            frm.elements['CreateHostedSP.btnMetadata'].style.display = '';
            document.getElementById('metadatafilename').style.display = '';
        }
    }

    function extendedOptionSelect(radio) {
        if (radio.value == 'url') {
            frm.elements['CreateHostedSP.tfExtendedFileURL'].style.display = '';
            frm.elements['CreateHostedSP.btnExtendedFile'].style.display = 'none';
            document.getElementById('extendedfilename').style.display = 'none';
        } else {
            frm.elements['CreateHostedSP.tfExtendedFileURL'].style.display = 'none';
            frm.elements['CreateHostedSP.btnExtendedFile'].style.display = '';
            document.getElementById('extendedfilename').style.display = '';
        }
    }

    function cancelOp() {
        document.location.replace("../task/Home");
        return false;
    }

    function realmSelect(radio) {
    	getCircleOfTrust(radio.value);
    }

    function cotOptionSelect(radio) {
        var ans = radio.value;
        if (ans == 'yes') {
            document.getElementById('cotchoice').style.display = 'block';
            document.getElementById('cottf').style.display = 'none';
            frm.elements['CreateHostedSP.tfCOT'].value = '';
        } else {
            document.getElementById('cotchoice').style.display = 'none';
            document.getElementById('cottf').style.display = 'block';
        }
    }

    function getExtendedData() {
        var extRadio = getRadioVal(frm, 'CreateHostedSP.radioExtendedData');
        var extended = (extRadio == 'url') ?
            frm.elements['CreateHostedSP.tfExtendedFileURL'].value :
            frm.elements['CreateHostedSP.tfExtendedFile'].value;
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
        var frm = document.forms['CreateHostedSP'];
        var realmobj = frm.elements['CreateHostedSP.tfRealm'];
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
<cc:pagetitle name="pgtitle" bundleID="amConsole" pageTitleText="page.title.configure.hosted.sp" showPageTitleSeparator="true" viewMenuLabel="" pageTitleHelpMessage="" showPageButtonsTop="true" showPageButtonsBottom="false" />

<cc:propertysheet name="propertyAttributes" bundleID="amConsole" showJumpLinks="false"/>

</cc:form>
</cc:header>
</div>
<div id="dlg" class="dvs"></div>

<script language="javascript">
    hideRealm();

    var msgConfiguring = "<cc:text name="txtConfiguring" defaultValue="configure.provider.waiting" bundleID="amConsole" escape="false" />";

    var msgConfigured = '<p>&nbsp;</p><cc:text name="txtConfigured" defaultValue="configure.sp.done" bundleID="amConsole" /><p><div class="TtlBtnDiv"><input name="yesSp" type="submit" class="Btn1" value="<cc:text name="txtYesBtn" defaultValue="ajax.yes.button" bundleID="amConsole" />" onClick="createRemoteIDP();return false;" /> <input name="noSp" type="submit" class="Btn1" value="<cc:text name="txtCloseBtn" defaultValue="ajax.no.button" bundleID="amConsole" />" onClick="document.location.replace(\'../task/Home\');return false;" /></div></p>';

    var closeBtn = '<p>&nbsp;</p><p><div class="TtlBtnDiv"><input name="btnClose" type="submit" class="Btn1" value="<cc:text name="txtCloseBtn" defaultValue="ajax.close.button" bundleID="amConsole" />" onClick="focusMain();return false;" /></div></p>';

    var msgGetCOTs = "<cc:text name="txtConfigured" defaultValue="configure.provider.get.cots" bundleID="amConsole" escape="false" />";

    var hasMetaData = 'no';
    var frm = document.forms['CreateHostedSP'];
    var btn1 = frm.elements['CreateHostedSP.button1'];
    btn1.onclick = submitPage;
    var btn2 = frm.elements['CreateHostedSP.button2'];
    btn2.onclick = cancelOp;
    var ajaxObj = getXmlHttpRequestObject();
    var data = '';
    var userLocale = "<% viewBean.getUserLocale().toString(); %>";

    function submitPage() {
        fade();
        if (document.getElementById('cotsection').style.display != 'block') {
            var extended = getExtendedData();
            if (extended.length > 0) {
                if (hasMetaData) {
                    getCircleOfTrustFromExt();
                } else {
                    var realm = frm.elements['CreateHostedSP.tfRealm'].value;
                    getCircleOfTrust(realm);
                }
                focusMain();
                return false;
            }
        }
        document.getElementById('dlg').innerHTML = '<center>' + 
        msgConfiguring + '</center>';
        var url = "../console/ajax/AjaxProxy.jsp";
        var params = 'locale=' + userLocale +
            '&class=com.sun.identity.workflow.CreateHostedSP' + getData();
        ajaxPost(ajaxObj, url, params, configured);
        return false;
    }

    function getData() {
        var cot;
        var cotRadio = getRadioVal(frm, 'CreateHostedSP.radioCOT');
        if (cotRadio == "yes") {
            cot = frm.elements['CreateHostedSP.choiceCOT'].value;
        } else {
            cot = frm.elements['CreateHostedSP.tfCOT'].value;
        }
        if (hasMetaData == "yes") {
            var metaRadio = getRadioVal(frm, 'CreateHostedSP.radioMeta');
            var meta = (metaRadio == 'url') ?
                frm.elements['CreateHostedSP.tfMetadataFileURL'].value :
                frm.elements['CreateHostedSP.tfMetadataFile'].value;
            var extRadio = getRadioVal(frm, 'CreateHostedSP.radioExtendedData');
            var extended = (extRadio == 'url') ?
                frm.elements['CreateHostedSP.tfExtendedFileURL'].value :
                frm.elements['CreateHostedSP.tfExtendedFile'].value;

            return "&metadata=" + escape(meta) +
                "&extendeddata=" + escape(extended) +
                "&cot=" + escape(cot);
        } else {
            var realm = frm.elements['CreateHostedSP.tfRealm'].value;
            return "&entityId=" +
            escape(frm.elements['CreateHostedSP.tfEntityId'].value) +
            "&realm=" + escape(realm) +
            "&specert=" +
            escape(frm.elements['CreateHostedSP.tfEncKey'].value) +
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
                    chooseRadio(frm, 'CreateHostedSP.radioCOT', 'no');
                } else {
                    var cots = result.split('|');
                    var choiceCOT = frm.elements['CreateHostedSP.choiceCOT'];
                    for (var i = 0; i < cots.length; i++) {
                        choiceCOT.options[i] = new Option(cots[i], cots[i]);
                    }
                    document.getElementById('cotq').style.display = 'block';
                    document.getElementById('cotchoice').style.display = 'block';
                    document.getElementById('cottf').style.display = 'none';
                    chooseRadio(frm, 'CreateHostedSP.radioCOT', 'yes');
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

    function createRemoteIDP() {
        var cot;
        var cotRadio = getRadioVal(frm, 'CreateHostedSP.radioCOT');
        if (cotRadio == "yes") {
            cot = frm.elements['CreateHostedSP.choiceCOT'].value;
        } else {
            cot = frm.elements['CreateHostedSP.tfCOT'].value;
        }
        document.location.replace('CreateRemoteIDP?cot=' + cot + '&' + data);
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
		msg = msg + '<center>' +  closeBtn + '</center>';
                ajaxObj = getXmlHttpRequestObject();
            }
            document.getElementById('dlg').innerHTML = msg;
        }
    }

    frm.elements['CreateHostedSP.tfMetadataFileURL'].style.display = 'none';
    frm.elements['CreateHostedSP.tfExtendedFileURL'].style.display = 'none';
    getCircleOfTrust('/');
</script>

</jato:useViewBean>

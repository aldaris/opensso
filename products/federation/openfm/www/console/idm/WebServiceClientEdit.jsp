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

   $Id: WebServiceClientEdit.jsp,v 1.3 2007-08-28 22:20:59 veiming Exp $

   Copyright 2007 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page info="WebServiceClientEdit" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.idm.WebServiceClientEditViewBean"
    fireChildDisplayEvents="true" >

<cc:i18nbundle baseName="amConsole" id="amConsole"
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>

<cc:header name="hdrCommon" pageTitle="webconsole.title" bundleID="amConsole" copyrightYear="2004" fireDisplayEvents="true">

<script language="javascript" src="../console/js/am.js"></script>
<script language="javascript">
    function toggleKeyStoreComponent(radioComp) {
        frm = radioComp.form;
        disableCustomKeyStoreFields(frm, (radioComp.value == 'default'));
    }

    function disableCustomKeyStoreFields(frm, disable) {
        frm.elements['WebServiceClientEdit.keystorelocation'].disabled =
            disable;
        frm.elements['WebServiceClientEdit.keystorepassword'].disabled =
            disable;
        frm.elements['WebServiceClientEdit.keypassword'].disabled = disable;
        frm.elements['WebServiceClientEdit.certalias'].disabled = disable;
    }

    function disableSTSFields() {
        var frm = document.forms['WebServiceClientEdit'];
        var securityMech = frm.elements['WebServiceClientEdit.SecurityMech'];
        var disableSTS = true;
        for (var i = 0; (i < securityMech.length); i++) {
            if (securityMech[i].checked) {
                disableSTS =
                    (securityMech[i].value != "urn:sun:wss:sts:security");
                break;
            }
        }
        frm.elements['WebServiceClientEdit.securitytokenendpoint'].disabled = disableSTS;
        frm.elements['WebServiceClientEdit.securitytokenmetadataendpoint'].disabled = disableSTS;
    }
</script>

<cc:form name="WebServiceClientEdit" method="post">
<script language="javascript">
    function confirmLogout() {
        return confirm("<cc:text name="txtLogout" defaultValue="masthead.logoutMessage" bundleID="amConsole"/>");
    }
</script>
<cc:primarymasthead name="mhCommon" bundleID="amConsole"  logoutOnClick="return confirmLogout();"/>
<cc:breadcrumbs name="breadCrumb" bundleID="amConsole" />
<cc:tabs name="tabCommon" bundleID="amConsole" submitFormData="true" />

<table border="0" cellpadding="10" cellspacing="0" width="100%">
    <tr>
	<td>
	<cc:alertinline name="ialertCommon" bundleID="amConsole" />
	</td>
    </tr>
</table>

<%-- PAGE CONTENT --------------------------------------------------------- --%>
<cc:pagetitle name="pgtitleTwoBtns" bundleID="amConsole" pageTitleText="page.title.entities.create" showPageTitleSeparator="true" viewMenuLabel="" pageTitleHelpMessage="" showPageButtonsTop="true" showPageButtonsBottom="false" />

<cc:propertysheet name="propertyAttributes" bundleID="amConsole" showJumpLinks="false"/>

</cc:form>

<script language="javascript">
    var frm = document.forms['WebServiceClientEdit'];
    disableCustomKeyStoreFields(frm,
        frm.elements['WebServiceClientEdit.keystoreusage'][0].checked);
    disableSTSFields();
</script>
</cc:header>
</jato:useViewBean>

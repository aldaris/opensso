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

   $Id: WebServiceProviderEdit.jsp,v 1.2 2007-06-14 21:02:51 veiming Exp $

   Copyright 2007 Sun Microsystems Inc. All Rights Reserved
--%>

<%@ page info="WebServiceProviderEdit" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.idm.WebServiceProviderEditViewBean"
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
        frm.elements['WebServiceProviderEdit.keystorelocation'].disabled =
            disable;
        frm.elements['WebServiceProviderEdit.keystorepassword'].disabled =
            disable;
        frm.elements['WebServiceProviderEdit.keypassword'].disabled = disable;
        frm.elements['WebServiceProviderEdit.certalias'].disabled = disable;
    }
</script>

<cc:form name="WebServiceProviderEdit" method="post">
<jato:hidden name="szCache" />

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
<cc:spacer name="spacer" height="10" newline="true" />
                                                                                           
<cc:actiontable
    name="tblUserCredential"
    title="web.services.profile.username-token-tblheader"
    bundleID="amConsole"
    summary="web.services.profile.username-token-tblheader"
    empty="web.services.profile.username-token-no-entries"
    selectionType="multiple"
    showAdvancedSortingIcon="false"
    showLowerActions="false"
    showPaginationControls="true"
    showPaginationIcon="true"
    showSelectionIcons="true"
    selectionJavascript="toggleTblButtonState('WebServiceProviderEdit', 'WebServiceProviderEdit.tblUserCredential', 'tblHitCounter', 'WebServiceProviderEdit.tblButtonDelete', this)"
    showSelectionSortIcon="false"
    showSortingRow="false" />

</cc:form>

<script language="javascript">
    var frm = document.forms['WebServiceProviderEdit'];
    disableCustomKeyStoreFields(frm,
        frm.elements['WebServiceProviderEdit.keystoreusage'][0].checked);
</script>

</cc:header>
</jato:useViewBean>

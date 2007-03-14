<%--
    Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved
    Use is subject to license terms.
--%>


<%@ page info="EntityResourceOffering" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.idm.EntityResourceOfferingViewBean"
    fireChildDisplayEvents="true" >

<cc:i18nbundle baseName="amConsole" id="amConsole"
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>

<cc:header name="hdrCommon" pageTitle="webconsole.title" bundleID="amConsole" copyrightYear="2004" fireDisplayEvents="true">

<script language="javascript" src="../console/js/am.js"></script>
<cc:form name="EntityResourceOffering" method="post">

<script language="javascript">
    function confirmLogout() {
        return confirm("<cc:text name="txtLogout" defaultValue="masthead.logoutMessage" bundleID="amConsole"/>");
    }
</script>
<cc:primarymasthead name="mhCommon" bundleID="amConsole"  logoutOnClick="return confirmLogout();" 
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>
<cc:breadcrumbs name="breadCrumb" bundleID="amConsole" />
<table border="0" cellpadding="10" cellspacing="0" width="100%">
    <tr>
	<td>
	<cc:alertinline name="ialertCommon" bundleID="amConsole" />
	</td>
    </tr>
</table>

<%-- PAGE CONTENT --------------------------------------------------------- --%>
<cc:pagetitle name="pgtitle" bundleID="amConsole" pageTitleText="title.idm.resource.offering" showPageTitleSeparator="true" viewMenuLabel="" pageTitleHelpMessage="" showPageButtonsTop="true" showPageButtonsBottom="false" />

<cc:spacer name="spacer" height="10" newline="true" />
                                                                                
<cc:actiontable
    name="tblEntries"
    title="table.idm.resource.offering.title"
    bundleID="amConsole"
    summary="table.idm.resource.offering.title"
    empty="table.idm.resource.offering.empty.message"
    selectionType="multiple"
    showAdvancedSortingIcon="false"
    showLowerActions="false"
    showPaginationControls="false"
    showPaginationIcon="false"
    showSelectionIcons="true"
    selectionJavascript="toggleTblButtonState('EntityResourceOffering', 'EntityResourceOffering.tblEntries', 'tblButton', 'EntityResourceOffering.tblButtonDelete', this)"
    showSelectionSortIcon="false"
    showSortingRow="false" />


</cc:form>
</cc:header>
</jato:useViewBean>

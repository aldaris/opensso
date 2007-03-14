<%--
    Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved
    Use is subject to license terms.
--%>


<%@ page info="FSAuthDomains" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.federation.FSAuthDomainsViewBean"
    fireChildDisplayEvents="true" >

<cc:i18nbundle baseName="amConsole" id="amConsole"
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>


<cc:header name="hdrCommon" pageTitle="webconsole.title" bundleID="amConsole" copyrightYear="2004" fireDisplayEvents="true">

<script language="javascript" src="../console/js/am.js">
</script>

<cc:form name="FSAuthDomains" method="post" defaultCommandChild="/btnSearch">
<jato:hidden name="szCache" />

<%-- HEADER --%>
<script language="javascript">
    function confirmLogout() {
        return confirm("<cc:text name="txtLogout" defaultValue="masthead.logoutMessage" bundleID="amConsole"/>");
    }
</script>
<cc:primarymasthead name="mhCommon" bundleID="amConsole"  logoutOnClick="return confirmLogout();" 
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>
<cc:tabs name="tabCommon" bundleID="amConsole" />

<table border="0" cellpadding="10" cellspacing="0" width="100%">
    <tr>
	<td>
	<cc:alertinline name="ialertCommon" bundleID="amConsole" />
	</td>
    </tr>
</table>

<%-- PAGE CONTENT --%>

<cc:actiontable
    name="tblSearch"
    title="table.authDomains.title.name"
    bundleID="amConsole"
    summary="table.authDomains.summary"
    empty="table.authDomains.empty.message"
    selectionType="multiple"
    showAdvancedSortingIcon="false"
    showLowerActions="false"
    showPaginationControls="true"
    showPaginationIcon="true"
    showSelectionIcons="true"
    selectionJavascript="toggleTblButtonState('FSAuthDomains', 'FSAuthDomains.tblSearch', 'tblButton', 'FSAuthDomains.tblButtonDelete', this)"
    showSelectionSortIcon="false"
    showSortingRow="false" />


<%-- END CONTENT --%>
</cc:form>

</cc:header>
</jato:useViewBean>

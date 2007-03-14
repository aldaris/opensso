<%--
    Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved
    Use is subject to license terms.
--%>


<%@ page info="EntityResourceOfferingEdit" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.idm.EntityResourceOfferingEditViewBean"
    fireChildDisplayEvents="true" >

<cc:i18nbundle baseName="amConsole" id="amConsole"
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>

<cc:header name="hdrCommon" pageTitle="webconsole.title" bundleID="amConsole" copyrightYear="2004" fireDisplayEvents="true">

<script language="javascript" src="../console/js/am.js"></script>

<script language="javascript">
    function toggleTblButtonStateEx(obj) {
	toggleTblButtonState('EntityResourceOfferingEdit',
	    'EntityResourceOfferingEdit.SecurityMechID',
	    'securityMechIDCount',
	    'EntityResourceOfferingEdit.tblSecurityMechIDButtonDelete',
	    obj);
    }
</script>

<cc:form name="EntityResourceOfferingEdit" method="post" defaultCommandChild="/button1" >

<script language="javascript">
    function confirmLogout() {
        return confirm("<cc:text name="txtLogout" defaultValue="masthead.logoutMessage" bundleID="amConsole"/>");
    }
</script>
<cc:primarymasthead name="mhCommon" bundleID="amConsole"  logoutOnClick="return confirmLogout();" 
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>

<table border="0" cellpadding="10" cellspacing="0" width="100%">
    <tr>
	<td>
	<cc:alertinline name="ialertCommon" bundleID="amConsole" />
	</td>
    </tr>
</table>

<%-- PAGE CONTENT --------------------------------------------------------- --%>
<cc:pagetitle name="pgtitle" bundleID="amConsole" pageTitleText="discovery.service.bootstrapResOff.create.page.title" showPageTitleSeparator="true" viewMenuLabel="" pageTitleHelpMessage="" showPageButtonsTop="true" showPageButtonsBottom="false" />

<cc:propertysheet name="bootstrapRefPropertyAttributes" bundleID="amConsole" showJumpLinks="true" />

</cc:form>
</cc:header>
</jato:useViewBean>

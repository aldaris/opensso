<%--
/**
 * ident "@(#)ButtonFrame.jsp 1.5 04/08/24 SMI"
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */
--%>
<%@ page language="java" %>
<%@taglib uri="/WEB-INF/tld/com_iplanet_jato/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/tld/com_sun_web_ui/cc.tld" prefix="cc" %>

<jato:useViewBean className="com.sun.web.ui.servlet.version.ButtonFrameViewBean">

<!-- Header -->
<cc:header name="Header"
 pageTitle=""
 styleClass="VrsBtnBdy"
 baseName="com.sun.web.ui.resources.Resources"
 bundleID="testBundle"
 onLoad="document.buttonFrameForm.elements[0].focus();">

<cc:form name="buttonFrameForm" method="post">
<div class="VrsBtnAryDiv">
  <cc:button name="Close" defaultValue="help.close" type="Default" bundleID="testBundle"
   onClick="javascript: parent.close(); return false;" />
</div>
</cc:form>

</cc:header>

</jato:useViewBean>

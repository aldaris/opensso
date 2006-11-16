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
     
  $Id: version.jsp,v 1.1 2006-11-16 04:31:12 veiming Exp $
     
  Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://www.sun.com/web/ui" prefix="ui" %>

<f:view>
    <f:loadBundle var="common" basename="jsfConsole"/>

    <ui:page>
        <ui:head title="#{common['product.title']}" id="ProductVersion">
        <ui:link url="/theme/com/sun/web/ui/suntheme/images/favicon/favicon.ico"
            rel="shortcut icon" type="image/x-icon" />
        </ui:head>
        <ui:body>
            <ui:form id="form1">
            <ui:versionPage id="versionLink" styleClass="MstLnk"
                versionString="#{common['product.version']}"
                copyrightString="#{common['login.copyright']}"
                productImageDescription="#{common['productversion.imagedescription']}"
                productImageHeight="40" productImageWidth="262"
                productImageURL="../jsf/images/VersionProductName.png" />
            </ui:form>
        </ui:body>
    </ui:page>
</f:view>


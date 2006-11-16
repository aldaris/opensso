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
                                                                                
   $Id: index.jsp,v 1.1 2006-11-16 04:31:11 veiming Exp $
                                                                                
   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://www.sun.com/web/ui" prefix="ui" %>

<f:view>
<f:loadBundle var="common" basename="jsfConsole" />

<ui:html>
    <ui:head title="#{common['product.title']}">
    <ui:link url="/theme/com/sun/web/ui/suntheme/images/favicon/favicon.ico" rel="shortcut icon" type="image/x-icon" />
    </ui:head>

    <ui:page>
        <ui:body styleClass="DefBdy">
            <jsp:include page="masthead.jsp"/>
            <ui:form id="form1">
            <ui:contentPageTitle id="indexPageTitle" title="TestApp Home Page">
            <f:verbatim><br/></f:verbatim>
            </ui:contentPageTitle>                        
            </ui:form>
        </ui:body> 
    </ui:page>
</ui:html>
</f:view>


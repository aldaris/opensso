<!--
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
   
   $Id: masthead.jsp,v 1.2 2006-11-30 00:44:48 veiming Exp $
   
   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
-->

<jsp:root version="1.2"
    xmlns:f="http://java.sun.com/jsf/core"
    xmlns:h="http://java.sun.com/jsf/html"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:ui="http://www.sun.com/web/ui">
                                                                                
<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
<f:loadBundle var="common" basename="jsfConsole" />

<ui:masthead id="Masthead"
    productImageURL="../jsf/images/PrimaryProductName.png"
    productImageDescription="#{common['product.title']}"
    productImageWidth="300" productImageHeight="40"
    userInfo="#{basebean.userName}"
    serverInfo="#{basebean.hostname}">

    <!-- help button -->
    <f:facet name="helpLink">
        <ui:helpWindow id="helpwindow"  styleClass="MstLnk"
            windowTitle="#{common['helpwindow.title']}"
            mastheadImageDescription="#{common['helpwindow.mastheadDescription']}"
            mastheadImageUrl="./images/PrimaryProductName.png"
            linkText="#{common['helpwindow.pageTitle']}" helpFile="ps_aboutportalhelp.html"
            jspPathPrefix="console"
            toolTip="#{common['helpwindow.helpToolTip']}" />
    </f:facet>

    <!-- version button -->
    <f:facet name="versionLink">
        <ui:hyperlink url="../base/version.jsp" styleClass="MstLnk"
            id="version" text="#{common['version.link']}"
            onClick="javascript:var win = window.open('','versionWindow','height=500,width=650,top='+((screen.height-(screen.height/1.618))-(500/2))+',left='+((screen.width-650)/2)+',scrollbars,resizable');win.focus()"
            target="versionWindow" />
    </f:facet>

    <!-- logout out button -->
    <f:facet name="logoutLink">
        <ui:hyperlink target="_top" url="#{basebean.logoutURL}"
            action="#{common['logout.link']}" />
    </f:facet>
</ui:masthead>
<ui:form id="form">
    <ui:tabSet binding="#{tabbean.tabSet}" selected="realm" />
</ui:form>
</jsp:root>


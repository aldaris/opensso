<?xml version="1.0" encoding="UTF-8"?>
<!--
   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

   Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved

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

   $Id:
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ice="http://www.icesoft.com/icefaces/component" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
    <f:view>
        <f:loadBundle basename="resources.buildProp-icf" var="msgs"/>
        <html id="outputHtml1">
            <head id="outputHead1">
                <ice:outputStyle href="../resources/stylesheet.css" id="outputStyle1"/>
                <ice:outputStyle href="../xmlhttp/css/xp/xp.css" id="outputStyle2"/>
            </head>
            <body id="outputBody1" style="-rave-layout: grid">
                <ice:graphicImage id="graphicImage1" value="../images/logo.gif"/>
                <p><ice:outputText id="QATestFramwork" style=" color: silver; size: 5; font: Arial" value="OpenSSO QA Test Framework"/></p>
                <hr style=" color: silver"/>
                <ice:form id="buildInfoForm" >
                    <ice:outputConnectionStatus/>
                    <ice:outputText id="outputText5"  value="Select a Module to be Tested" style=" font-weight:bolder; font-size:large"/>
                    <ice:panelGrid columns="3" border="0" bgcolor="#FFFFCC" >
                        <ice:outputText value=""/>
                        <ice:outputText   value=""/>
                        <ice:outputText   value=""/>
                        <ice:outputText id="outputText1" value="Sanity"/>
                        <ice:selectManyCheckbox id="sanityCheckBox"  value="#{BuildInfoBean.selSanityMod}" immediate="true" valueChangeListener="#{BuildInfoBean.sanityModuleValueListener}" onclick="this.form.submit()" partialSubmit="true">
                            <f:selectItems id="selectManyCheckbox1selectItems" value="#{BuildInfoBean.sanityModLabel}" />
                        </ice:selectManyCheckbox>
                        <ice:outputText   id="error1"/>
                        <ice:outputText id="outputText2"  value="All"/>
                        <ice:selectManyCheckbox id="allCheckbox"  value="#{BuildInfoBean.selAllMod}" immediate="true" valueChangeListener="#{BuildInfoBean.allModuleValueListener}" onclick="this.form.submit()" partialSubmit="true">
                            <f:selectItems id="selectManyCheckbox2selectItems" value="#{BuildInfoBean.allModlabel}" />
                        </ice:selectManyCheckbox>
                        <ice:outputText   id="error"/>
                        <ice:outputText id="outputText3"  value="Single Server Modules"/>
                        <ice:selectManyCheckbox id="singleSvrCheckBox"  value="#{BuildInfoBean.selSingleSvrMod}" immediate="true" >
                            <f:selectItems id="selectManyCheckbox3selectItems" value="#{BuildInfoBean.singleSvrModLabel}"/>
                        </ice:selectManyCheckbox>
                        <ice:outputText  id="error2"/>
                        <ice:outputText id="outputText4"  value="Multi Server Modules"/>
                        <ice:selectManyCheckbox id="MultiSvrCheckBox"  value="#{BuildInfoBean.selMultiSvrMod}" immediate="true" >
                            <f:selectItems id="selectManyCheckbox4selectItems" value="#{BuildInfoBean.multiSvrModLabel}"/>
                        </ice:selectManyCheckbox>
                        <ice:outputText  id="error3"/>
                        <ice:outputLabel id="outputLabel1"  value="#{msgs['qatest.home']}"/>
                        <ice:inputText id="qatestHome"   required="true"  value="#{BuildInfoBean.qatestHome}"   validator="#{BuildInfoBean.qatestHomeValidator}" partialSubmit="true" />
                        <ice:message for="qatestHome" style="color: red" />
                        <ice:outputLabel id="outputLabel2" value="#{msgs['report.dir.label']}"/>
                        <ice:inputText id="reportDir"  required="true" value="#{BuildInfoBean.reportDir}" validator="#{BuildInfoBean.reportDirValidator}" partialSubmit="true" />
                        <ice:message for="reportDir" style="color: red"  />
                        <ice:outputLabel  value="#{msgs['executionmode.label']}"/>
                        <ice:selectOneMenu id="selectOneMenu1" value="#{BuildInfoBean.selExeMode}"  >
                            <f:selectItems id="selectOneMenu1selectItems" value="#{BuildInfoBean.execModeLabel}"/>
                        </ice:selectOneMenu>
                        <ice:outputText   id="error6"/>
                    </ice:panelGrid>
                    <ice:commandButton action="#{BuildInfoBean.buildButtonAction}" id="buildButton" disabled="false"
                                       style="height: 48px; width: 120px" value="NEXT"/>
                </ice:form>
            </body>
        </html>
    </f:view>
</jsp:root>


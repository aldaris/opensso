/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id:
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.qaweb.beans;

import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlMessage;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.sun.identity.qatest.qaweb.common.MessageBundleLoader;
import com.sun.identity.qatest.qaweb.common.QawebCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;
import javax.faces.validator.Validator;

/**
 *
 * This class is triggered on change of fqdn field in agents
 * property page displayed to the user
 */
public class agentsValueChangeListener implements ValueChangeListener {

    private static String fileSeperator = System.getProperty("file.separator");
    Boolean isFirstTime = true;
    String[] agentsGlobalData = {"/allow.html", "/qatest_index.html",
    "/allow1.html","/allow2.html", "/allow3.html", "/allow4.html",
    "/allow5.html", "/allow6.html", "/allow7.html", "/allow8.html", "/*.html",
    "/*.html", "/*.html", "/*.htmL", "/banner.gif", "/banner.txt",
    "/notvalid.html", "/allow17.html", "/allow18.html", "/allow19.html",
    "/allow20.html", "/allowha.html", "/notenf.html", "/AlloW.html",
    "/protectedservlet", "/unprotectedservlet", "/invokerservlet",
    "/securityawareservlet", "/jsp/*", "/urlpolicyservlet", "/allowhs.html",
    "/notenfhs.html", "/allowhs1.html", "/accessdenied.html",
    "/authentication/accessdenied.html"
    };

    /**
     * This method gets called whenever there is a change of value in a
     * UIComponent bound to this listener
     * @param event valueChangeEvent triggered by bound UI COmponent
     */
    public void processValueChange(ValueChangeEvent event)
            throws AbortProcessingException {
        PanelTabSet tabset = new PanelTabSet();
        tabset = (PanelTabSet) event.getComponent().getParent().
                getParent().getParent();
        HtmlPanelGrid moduleGrid = (HtmlPanelGrid) event.getComponent().
                getParent();
        HtmlInputText fqdn = (HtmlInputText) moduleGrid.findComponent("fqdn");
        if (event.getNewValue().equals("2.2J2EE") || event.getNewValue().
                equals("2.2WEB")) {
            HtmlSelectOneMenu confType = (HtmlSelectOneMenu) moduleGrid.
                    findComponent("agentsGlobal_30agentConfigurationType");
            confType.setDisabled(true);
        } else if (event.getNewValue().equals("3.0J2EE") || event.getNewValue().
                equals("3.0WEB") || event.getNewValue().equals("3.0WEBLOGIC")) {
            HtmlSelectOneMenu confType = (HtmlSelectOneMenu) moduleGrid.
                    findComponent("agentsGlobal_30agentConfigurationType");
            confType.setDisabled(false);
        }
        if (event.getNewValue().equals("2.2WEB") || event.getNewValue().
                equals("3.0WEB")) {
            HtmlSelectOneMenu headerFetchMode = (HtmlSelectOneMenu) moduleGrid.
                    findComponent("agentsGlobal_headerFetchMode");
            headerFetchMode.getChildren().clear();
            List valueList = new ArrayList();
            UISelectItems items = new UISelectItems();
            int NoOfMenuValues = Integer.parseInt(MessageBundleLoader.
                    getModuleMessage("agents", ("property" + 3 +
                    ".NoOfValues")));
            for (int m = 1; m <=
                    NoOfMenuValues - 1; m++) {
                SelectItem selectItem = new SelectItem(
                        MessageBundleLoader.getModuleMessage("agents",
                        ("property" + 3 + ".value" + m)),
                        MessageBundleLoader.getModuleMessage("agents",
                        ("property" + 3 + ".value" + m)));
                valueList.add(selectItem);
                items.setValue(valueList);
                headerFetchMode.getChildren().add(items);
            }
        } else if (event.getNewValue().equals("3.0J2EE") || event.getNewValue().
                equals("2.2J2EE") || event.getNewValue().equals("3.0WEBLOGIC")){
            HtmlSelectOneMenu headerFetchMode = (HtmlSelectOneMenu) moduleGrid.
                    findComponent("agentsGlobal_headerFetchMode");
            headerFetchMode.getChildren().clear();
            List valueList = new ArrayList();
            UISelectItems items = new UISelectItems();
            int NoOfMenuValues = Integer.parseInt(MessageBundleLoader.
                    getModuleMessage("agents", ("property" + 3 +
                    ".NoOfValues")));
            for (int m = 1; m <=
                    NoOfMenuValues; m++) {
                SelectItem selectItem = new SelectItem(
                        MessageBundleLoader.getModuleMessage("agents",
                        ("property" + 3 + ".value" + m)),
                        MessageBundleLoader.getModuleMessage("agents",
                        ("property" + 3 + ".value" + m)));
                valueList.add(selectItem);
                items.setValue(valueList);
                headerFetchMode.getChildren().add(items);
            }
        }
        if (event.getComponent().getId().equals("fqdn") && !fqdn.getValue().
                equals("")) {
            HtmlSelectOneMenu agentType = (HtmlSelectOneMenu) moduleGrid.
                    findComponent("agentsGlobal_agentType");
            Map moduleMap = new HashMap();
            Map map = FacesContext.getCurrentInstance().getExternalContext().
                    getSessionMap();
            BuildInfoBean bpbean = (BuildInfoBean) map.get("BuildInfoBean");
            HtmlInputText headerEval = (HtmlInputText) moduleGrid.findComponent
                    ("agentsGlobal_headerEvalScriptName");
            headerEval.setSize(100);
            if (agentType.getValue().equals("3.0J2EE") ||
                    agentType.getValue().equals("2.2J2EE") ||
                    agentType.getValue().equals("3.0WEBLOGIC")) {
                headerEval.setValue(fqdn.getValue() +
                        "/jsp/showHttpHeaders.jsp?*");
            } else {
                headerEval.setValue(fqdn.getValue() + "/cgi-bin/headers.cgi");
            }
            QawebCommon qawebCommon = new QawebCommon();
            String moduleFilename = bpbean.getQatestHome() + fileSeperator +
                    "resources" +
                    (String) (MessageBundleLoader.getModuleMessage("agents",
                    "path"));
            try {
                moduleMap = qawebCommon.getMapFromProperties(moduleFilename);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isFirstTime == true) {
                isFirstTime = false;
                int j = 0;
                for (int i = 28; i <= 62; i++) {
                    String propertyID = MessageBundleLoader.getModuleMessage
                            ("agents", ("property" + i + ".actualname"));
                    if (propertyID.contains(".")) {
                        propertyID = propertyID.replace(".", "_");
                    }
                    HtmlOutputLabel outputlabel = new HtmlOutputLabel();
                    outputlabel.setValue(MessageBundleLoader.getModuleMessage
                            ("agents", "property" + i + ".displayname"));
                    UIInput inputBox = new UIInput();
                    if (MessageBundleLoader.getModuleMessage("agents", 
                            "property" + i + ".type").equals("HtmlInputText")) {
                        inputBox = new HtmlInputText();
                        inputBox.setId(propertyID);
                        inputBox.setRequired(true);
                        ((HtmlInputText) inputBox).setSize(100);
                        if (agentType.getValue().equals("3.0J2EE") ||
                                agentType.getValue().equals("2.2J2EE") ||
                                agentType.getValue().equals("3.0WEBLOGIC")) {
                            if (i >= 52 && i <= 57) {
                                inputBox.setValue(fqdn.getValue().toString() +
                                        agentsGlobalData[j]);
                                j++;
                            } else if (i == 61) {
                                int lastIndex = fqdn.getValue().toString().
                                        lastIndexOf("/");
                                String val = fqdn.getValue().toString().
                                        substring(lastIndex);
                                inputBox.setValue(val +
                                        "/resources/accessdenied.html");
                            } else if (i == 62) {
                                int lastIndex = fqdn.getValue().toString().
                                        lastIndexOf("/");
                                String val = fqdn.getValue().toString().
                                        substring(lastIndex);
                                inputBox.setValue(val +
                                        "/authentication/accessdenied.html");
                            } else {
                                inputBox.setValue(fqdn.getValue().toString() +
                                        "/resources" + agentsGlobalData[j]);
                                j++;

                            }
                        } else if (agentType.getValue().equals("2.2WEB") ||
                                agentType.getValue().equals("3.0WEB")) {
                            inputBox.setValue(fqdn.getValue().toString() +
                                    agentsGlobalData[j]);
                            j++;
                            if (i == 61) {
                                int lastIndex = fqdn.getValue().toString().
                                        lastIndexOf("/");
                                String val = fqdn.getValue().toString();
                                inputBox.setValue(val + "/accessdenied.html");
                            }
                            if (i == 62) {
                                int lastIndex = fqdn.getValue().toString().
                                        lastIndexOf("/");
                                String val = fqdn.getValue().toString();
                                inputBox.setValue(val +
                                        "/authentication/accessdenied.html");
                            }
                        }
                        if (MessageBundleLoader.getModuleMessage("agents",
                                "property" + i +
                                ".validator").equals("true")) {
                            try {
                                Class moduleValidator = Class.forName
                                        ("com.sun.identity.qatest.qaweb.beans."
                                        + "agentsValidator");
                                Validator validator = (Validator)
                                        moduleValidator.newInstance();
                                inputBox.addValidator(validator);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        if (MessageBundleLoader.getModuleMessage("agents", 
                                "property" + i + ".valueChangeListener").
                                equals("true")) {
                            try {
                                Class moduleListener = Class.forName
                                        ("com.sun.identity.qatest.qaweb.beans."
                                        + "agentsValueChangeListener");
                                ValueChangeListener listener = 
                                        (ValueChangeListener) moduleListener.
                                        newInstance();
                                inputBox.addValueChangeListener(listener);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    HtmlMessage errorMessage = new HtmlMessage();
                    errorMessage.setFor(propertyID);
                    errorMessage.setStyle("color: red");
                    HtmlOutputLabel outputlabelDummy = new HtmlOutputLabel();
                    HtmlOutputText outputcomments = new HtmlOutputText();
                    outputcomments.setValue(MessageBundleLoader.
                            getModuleMessage("agents", "property" + i +
                            ".comment"));
                    outputcomments.setStyle("color: blue;");
                    HtmlOutputLabel outputlabeldummy = new HtmlOutputLabel();
                    moduleGrid.getChildren().add(outputlabel);
                    moduleGrid.getChildren().add(inputBox);
                    moduleGrid.getChildren().add(errorMessage);
                    moduleGrid.getChildren().add(outputlabelDummy);
                    moduleGrid.getChildren().add(outputcomments);
                    moduleGrid.getChildren().add(outputlabeldummy);
                }
            } else if (isFirstTime == false) {
                int j = 0;
                for (int i = 28; i <= 62; i++) {
                    String propertyID = MessageBundleLoader.getModuleMessage
                            ("agents", ("property" + i + ".actualname"));
                    if (propertyID.contains(".")) {
                        propertyID = propertyID.replace(".", "_");
                    }
                    HtmlInputText inputBox = (HtmlInputText) moduleGrid.
                            findComponent(propertyID);
                    if (agentType.getValue().equals("3.0J2EE") ||
                            agentType.getValue().equals("2.2J2EE") ||
                            agentType.getValue().equals("3.0WEBLOGIC")) {
                        if (i >= 52 && i <= 57) {
                            inputBox.setValue(fqdn.getValue().toString() +
                                    agentsGlobalData[j]);
                            j++;
                        } else if (i == 61) {
                            int lastIndex = fqdn.getValue().toString().
                                    lastIndexOf("/");
                            String val = fqdn.getValue().toString().
                                    substring(lastIndex);
                            inputBox.setValue(val +
                                    "/resources/accessdenied.html");
                        } else if (i == 62) {
                            int lastIndex = fqdn.getValue().toString().
                                    lastIndexOf("/");
                            String val = fqdn.getValue().toString().
                                    substring(lastIndex);
                            inputBox.setValue(val +
                                    "/authentication/accessdenied.html");
                        } else {
                            inputBox.setValue(fqdn.getValue().toString() +
                                    "/resources" + agentsGlobalData[j]);
                            j++;
                        }
                    } else if (agentType.getValue().equals("2.2WEB") ||
                            agentType.getValue().equals("3.0WEB")) {
                        inputBox.setValue(fqdn.getValue().toString() +
                                agentsGlobalData[j]);
                        j++;
                        if (i == 61) {
                            String val = fqdn.getValue().toString();
                            inputBox.setValue(val + "/accessdenied.html");
                        }
                        if (i == 62) {
                            String val = fqdn.getValue().toString();
                            inputBox.setValue(val +
                                    "/authentication/accessdenied.html");
                        }
                    }
                }
            }
        }
        FacesContext.getCurrentInstance().renderResponse();
    }
}

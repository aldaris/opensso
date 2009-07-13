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
import com.icesoft.faces.component.paneltabset.PanelTab;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.sun.identity.qatest.qaweb.common.MessageBundleLoader;
import com.sun.identity.qatest.qaweb.common.QawebCommon;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;
import javax.faces.validator.Validator;

/**
 * <p>This class is a session scope data bean which mainly deals with module
 * page of the web app.</p>
 * <p>An instance of this class will be created  automatically,
 * the first time , application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class ModuleBean extends AbstractSessionBean {

    PanelTabSet tabset = new PanelTabSet();
    private static String fileSeperator = System.getProperty("file.separator");
    boolean isAuthSameAsUM = true;
    private String host;
    private String bindDN;
    private String agentsName;
    private String bindPwd;
    private String baseDN;

    /**
     * <p>This method returns the list of agent names</p>
     * @return List of agent names
     */
    public String getAgentsName() {
        return agentsName;
    }

    /**
     * <p>This method sets the list of agent names</p>
     * @param agentsName List of agent names
     */
    public void setAgentsName(String agentsName) {
        this.agentsName = agentsName;
    }

    /**
     * <p>This method returns the Base DN</p>
     * @return BaseDN
     */
    public String getBaseDN() {
        return baseDN;
    }

    /**
     * <p>This method sets BaseDN</p>
     * @param BaseDN
     */
    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    /**
     * <p>This method returns bindDN</p>
     * @return bindDN
     */
    public String getBindDN() {
        return bindDN;
    }

    /**
     * <p>This method sets the bindDN</p>
     * @param bindDN
     */
    public void setBindDN(String bindDN) {
        this.bindDN = bindDN;
    }

    /**
     * <p>This method returns the bindPwd</p>
     * @return bindPwd
     */
    public String getBindPwd() {
        return bindPwd;
    }

    /**
     * <p>This method sets the bindPwd</p>
     * @param bindPwd
     */
    public void setBindPwd(String bindPwd) {
        this.bindPwd = bindPwd;
    }

    /**
     * <p>This method returns the host</p>
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * <p>This method sets host</p>
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * <p>This method determines if LDAP auth details are same as UM details</p>
     * @return Boolean
     */
    public boolean isIsAuthSameAsUM() {
        return isAuthSameAsUM;
    }

    /**
     * <p>This method sets true if LDAP auth details are same as UM details</p>
     * @param Boolean
     */
    public void setIsAuthSameAsUM(boolean isAuthSameAsUM) {
        this.isAuthSameAsUM = isAuthSameAsUM;
    }

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:
     * </strong> This method is automatically generated, so any user-specified
     * code inserted here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }

    /**
     * <p>Construct a new session data bean instance.</p>
     */
    public ModuleBean() {
    }

    /**
     * <p>This method is called when this bean is initially added to
     * session scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * session scope.</p>     
     */
    @Override
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        try {
            _init();
        } catch (Exception e) {
            log("ModuleBean Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e :
                new FacesException(e);
        }
    }

    /**
     * <p>This method is called when the session containing it is about to be
     * passivated.  Typically, this occurs in a distributed servlet container
     * when the session is about to be transferred to a different
     * container instance, after which the <code>activate()</code> method
     * will be called to indicate that the transfer is complete.</p>    
     */
    @Override
    public void passivate() {
    }

    /**
     * <p>This method is called when the session containing it was
     * reactivated.</p>    
     */
    @Override
    public void activate() {
    }

    /**
     * <p>This method is called when this bean is removed from
     * session scope.  Typically, this occurs as a result of
     * the session timing out or being terminated by the application.</p>     
     */
    @Override
    public void destroy() {
    }

    /**
     * <p>This method sets the Module Tabs</p>
     * @param paneltabset Panel Tab Set
     */
    public void setModules(PanelTabSet paneltabset) {
        this.tabset = paneltabset;
    }

    /**
     * <p>This method returns the Module Tabs</p>
     * @return paneltabset Panel Tab Set
     */
    public PanelTabSet getModules() {
        return tabset;
    }

    /**
     * <p>This method is used to remove module tabs from module.jsp</p>
     */
    public void removeModuleTab(String ModuleName) {
        ModuleBean mbean = (ModuleBean) getBean("ModuleBean");
        if (tabset == null) {
            // do nothing
        } else {
            PanelTab tab = (PanelTab) mbean.tabset.findComponent(ModuleName +
                    "Tab");
            mbean.tabset.getChildren().remove(tab);
        }
    }

    /**
     * <p>This method is used to add module tabs to module.jsp</p>
     */
    public void AddModuleTab(String ModuleName) {
        BuildInfoBean bpbean = (BuildInfoBean) getBean("BuildInfoBean");
        int NoofProperties = Integer.parseInt(MessageBundleLoader.
                getModuleMessage(ModuleName, "NoOfProperties"));
        PanelTab ModulePanelTab = new PanelTab();
        HtmlPanelGrid moduleGrid = new HtmlPanelGrid();
        HtmlPanelGrid errorGrid = new HtmlPanelGrid();
        Map moduleMap = new HashMap();
        Map umDataStoreMap = new HashMap();
        QawebCommon qawebCommon = new QawebCommon();
        String moduleFilename = bpbean.getQatestHome() + fileSeperator +
                "resources" +
                (String) (MessageBundleLoader.getModuleMessage(ModuleName,
                "path"));
        String umdataStoreFileName = bpbean.getQatestHome() +
                fileSeperator + "resources" + fileSeperator +
                "config" + fileSeperator +
                "UMGlobalDatastoreConfig.properties";
        try {
            moduleMap = qawebCommon.getMapFromProperties(moduleFilename);
            umDataStoreMap = qawebCommon.getMapFromProperties
                    (umdataStoreFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int j = 1; j <= NoofProperties; j++) {
            String propertyID = MessageBundleLoader.getModuleMessage(ModuleName,
                    ("property" + j + ".actualname"));
            if (propertyID.contains(".")) {
                propertyID = propertyID.replace(".", "_");
            }
            HtmlOutputLabel outputlabel = new HtmlOutputLabel();
            outputlabel.setValue(MessageBundleLoader.getModuleMessage
                    (ModuleName, "property" + j + ".displayname"));
            UIInput inputBox = new UIInput();

            if (MessageBundleLoader.getModuleMessage(ModuleName, "property" +
                    j + ".type").equals("HtmlInputText")) {
                inputBox = new HtmlInputText();
                inputBox.setId(propertyID);
                if (ModuleName.equals("agents") && j >= 10 && j <= 25) {
                } else {
                    inputBox.setRequired(true);
                }
                if (MessageBundleLoader.getModuleMessage(ModuleName, "property"
                        + j + ".validator").equals("true")) {
                    try {
                        Class moduleValidator = Class.forName
                                ("com.sun.identity.qatest.qaweb.beans." +
                                ModuleName + "Validator");
                        Validator validator = (Validator) moduleValidator.
                                newInstance();
                        inputBox.addValidator(validator);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (MessageBundleLoader.getModuleMessage(ModuleName, "property"
                        + j + ".valueChangeListener").equals("true")) {
                    try {
                        Class moduleListener = Class.forName
                                ("com.sun.identity.qatest.qaweb.beans." +
                                ModuleName + "ValueChangeListener");
                        ValueChangeListener listener = (ValueChangeListener)
                                moduleListener.newInstance();
                        inputBox.addValueChangeListener(listener);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                inputBox.setValue(moduleMap.get(MessageBundleLoader.
                        getModuleMessage(ModuleName, "property" + j +
                        ".actualname")));
                inputBox.setRequiredMessage(MessageBundleLoader.
                        getModuleMessage(ModuleName, "property" + j +
                        ".displayname") + " is a required field");
                ((HtmlInputText) inputBox).setPartialSubmit(true);

            } else if (MessageBundleLoader.getModuleMessage(ModuleName,
                    "property" + j +
                    ".type").equals("HtmlSelectOneMenu")) {
                inputBox = new HtmlSelectOneMenu();
                inputBox.setId(propertyID);
                ((HtmlSelectOneMenu) inputBox).setPartialSubmit(true);
                inputBox.setImmediate(true);
                if (MessageBundleLoader.getModuleMessage(ModuleName,
                        "property" + j +
                        ".validator").equals("true")) {
                    try {
                        Class moduleValidator = Class.forName
                                ("com.sun.identity.qatest.qaweb.beans." +
                                ModuleName + "Validator");
                        Validator validator = (Validator) moduleValidator.
                                newInstance();
                        inputBox.addValidator(validator);                       
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (MessageBundleLoader.getModuleMessage(ModuleName, "property"
                        + j + ".valueChangeListener").equals("true")) {
                    try {
                        Class moduleListener = Class.forName
                                ("com.sun.identity.qatest.qaweb.beans." +
                                ModuleName + "ValueChangeListener");
                        ValueChangeListener listener = (ValueChangeListener)
                                moduleListener.newInstance();
                        inputBox.addValueChangeListener(listener);                       
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                List valueList = new ArrayList();
                UISelectItems items = new UISelectItems();
                int NoOfMenuValues = Integer.parseInt(MessageBundleLoader.
                        getModuleMessage(ModuleName,
                        ("property" + j + ".NoOfValues")));
                for (int m = 1; m <=
                        NoOfMenuValues; m++) {
                    SelectItem selectItem = new SelectItem(
                            MessageBundleLoader.getModuleMessage(ModuleName,
                            ("property" + j + ".value" + m)),
                            MessageBundleLoader.getModuleMessage(ModuleName,
                            ("property" + j + ".value" + m)));
                    valueList.add(selectItem);
                    items.setValue(valueList);
                    inputBox.getChildren().add(items);
                }
            }
            HtmlMessage errorMessage = new HtmlMessage();
            errorMessage.setFor(propertyID);           
            errorMessage.setStyle("color: red");
            HtmlOutputLabel outputlabelDummy = new HtmlOutputLabel();
            HtmlOutputText outputcomments = new HtmlOutputText();           
            outputcomments.setValue(MessageBundleLoader.getModuleMessage
                    (ModuleName, "property" + j + ".comment"));
            HtmlOutputLabel outputlabeldummy = new HtmlOutputLabel();
            moduleGrid.getChildren().add(outputlabel);
            moduleGrid.getChildren().add(inputBox);
            moduleGrid.getChildren().add(errorMessage);
            moduleGrid.getChildren().add(outputlabelDummy);
            moduleGrid.getChildren().add(outputcomments);
            moduleGrid.getChildren().add(outputlabeldummy);
        }
        errorGrid.setBgcolor("#FFFFCC");
        moduleGrid.setId(ModuleName + "Grid");
        moduleGrid.setColumns(3);
        moduleGrid.setBgcolor("#FFFFCC");
        ModulePanelTab.setId(ModuleName + "Tab");
        ModulePanelTab.setLabel(ModuleName);
        ModulePanelTab.getChildren().add(errorGrid);
        ModulePanelTab.getChildren().add(moduleGrid);
        tabset.setStyle("width: 800px ;");
        tabset.getChildren().add(ModulePanelTab);
    }

    /**
     * <p>This method is used to navigate back to the last page</p>
     */
    public String goBackAction() {
        return "server";
    }

    /**
     * <p>This method is called on Submit Button action of module.jsp. It writes
     * the module details back to the original property files</p>
     */
    public String moduleButtonAction() {       
        BuildInfoBean bpbean = (BuildInfoBean) getBean("BuildInfoBean");
        ModuleBean mbean = (ModuleBean) getBean("ModuleBean");        
        int NumberOfTabs = bpbean.getModWithPropMap().size();
        PanelTabSet panelTabSet = (PanelTabSet) FacesContext.
                getCurrentInstance().getViewRoot().findComponent
                ("ModuleForm:modulePaneltabset");
        Iterator it = FacesContext.getCurrentInstance().getMessages();
        /* IF there are error messages in the queue, then remain in the same
        page */
        if (it.hasNext()) {
            return null;
        } else {
            /*Multi Server Validation. Check to see if the inactive Tabs have
            null values in the InputText COmponents */           
            boolean hasError = false;
            for (int r = 0; r < NumberOfTabs; r++) {
                PanelTab ModulePanelTab = (PanelTab) panelTabSet.
                        findComponent(bpbean.getModWithPropMap().get(r) +
                        "Tab");
                HtmlPanelGrid grid = (HtmlPanelGrid) panelTabSet.
                        findComponent(bpbean.getModWithPropMap().get(r) +
                        "Tab").findComponent(bpbean.getModWithPropMap().get(r)
                        + "Grid");
                String ModuleName = ModulePanelTab.getLabel();
                int NoofProperties = Integer.parseInt(MessageBundleLoader.
                        getModuleMessage(ModuleName, "NoOfProperties"));
                for (int s = 1; s <= NoofProperties; s++) {
                    String propertyID = MessageBundleLoader.getModuleMessage
                            (ModuleName, ("property" + s + ".actualname"));
                    if (propertyID.contains(".")) {
                        propertyID = propertyID.replace(".", "_");
                    }
                    UIInput inputBox = new UIInput();

                    if (MessageBundleLoader.getModuleMessage(ModuleName,
                            ("property" + s + ".type")).equals
                            ("HtmlInputText")) {
                        inputBox = new HtmlInputText();
                        inputBox = (HtmlInputText) grid.findComponent(
                                propertyID);
                        if (inputBox.getValue() == null) {
                            FacesMessage msg = new FacesMessage(
                                    MessageBundleLoader.getModuleMessage
                                    (ModuleName, "property" + s +
                                    ".displayname") + "is a Required field");
                            panelTabSet.setSelectedIndex(s);
                            FacesContext.getCurrentInstance().addMessage(
                                    inputBox.getClientId(FacesContext.
                                    getCurrentInstance()), msg);
                            hasError = true;
                        }
                    }

                }
            }
            if (hasError) {
                return null;
            /* If there are no errors, add module tabs to module.jsp and
             * write the Server Properties to the respective files
             */
            } else {               
                Map[] moduleInfoMap = new HashMap[NumberOfTabs];
                Map[] moduleCommentsMap = new HashMap[NumberOfTabs];
                QawebCommon[] getmoduleInfo = new QawebCommon[NumberOfTabs];
                QawebCommon[] writeModuleInfo = new QawebCommon[NumberOfTabs];
                for (int r = 0; r < NumberOfTabs; r++) {
                    moduleCommentsMap[r] = new HashMap();
                    PanelTab ModulePanelTab = new PanelTab();
                    ModulePanelTab = (PanelTab) panelTabSet.findComponent
                            (bpbean.getModWithPropMap().get(r) + "Tab");
                    HtmlPanelGrid grid = (HtmlPanelGrid) panelTabSet.
                            findComponent(bpbean.getModWithPropMap().get(r) +
                            "Tab").findComponent(bpbean.getModWithPropMap().
                            get(r) + "Grid");
                    String ModuleName = ModulePanelTab.getLabel();
                    int NoofProperties = Integer.parseInt(MessageBundleLoader.
                            getModuleMessage(ModuleName, "NoOfProperties"));
                    getmoduleInfo[r] = new QawebCommon();
                    String moduleFilename = bpbean.getQatestHome() + 
                            fileSeperator + "resources" +
                            (String) (MessageBundleLoader.getModuleMessage
                            (ModuleName, "path"));
                    try {
                        moduleInfoMap[r] = getmoduleInfo[r].
                                getMapFromProperties(moduleFilename);
                        if (ModuleName.equals("agents")) {
                            NoofProperties = 62;
                        }
                        for (int s = 1; s <= NoofProperties; s++) {
                            String propertyID = MessageBundleLoader.
                                    getModuleMessage(ModuleName,
                                    ("property" + s + ".actualname"));
                            if (propertyID.contains(".")) {
                                propertyID = propertyID.replace(".", "_");
                            }
                            UIInput inputBox = new UIInput();

                            if (MessageBundleLoader.getModuleMessage(ModuleName,
                                    ("property" + s + ".type")).equals
                                    ("HtmlInputText")) {
                                inputBox = new HtmlInputText();
                                inputBox = (HtmlInputText) grid.findComponent(
                                        propertyID);
                                if (ModuleName.equals("agents") && s == 26) {
                                } else {
                                    moduleInfoMap[r].put(MessageBundleLoader.
                                            getModuleMessage(ModuleName,
                                            "property" + s +
                                            ".actualname"),
                                            inputBox.getValue());
                                    moduleCommentsMap[r].put(MessageBundleLoader
                                            .getModuleMessage(ModuleName,
                                            "property" + s +
                                            ".actualname"), MessageBundleLoader.
                                            getModuleMessage(ModuleName,
                                            "property" + s +
                                            ".comment"));
                                }
                            } else if (MessageBundleLoader.getModuleMessage
                                    (ModuleName,"property" + s + ".type").
                                    equals("HtmlSelectOneMenu")) {
                                inputBox = new HtmlSelectOneMenu();
                                inputBox = (HtmlSelectOneMenu) grid.
                                        findComponent(propertyID);
                                if (ModuleName.equals("agents") && s == 1) {
                                    if (inputBox.getValue().toString().equals
                                            ("2.2WEB")) {
                                        agentsName = "2.2WebAgent";
                                    } else if (inputBox.getValue().toString().
                                            equals("2.2J2EE")) {
                                        agentsName = "2.2J2EEAgent";
                                    } else if (inputBox.getValue().toString().
                                            equals("3.0WEB")) {
                                        agentsName = "3.0WebAgent";
                                    } else if (inputBox.getValue().toString().
                                            equals("3.0J2EE")) {
                                        agentsName = "3.0J2EEAgent";
                                    } else if (inputBox.getValue().toString().
                                            equals("3.0WEBLOGIC")) {
                                        agentsName = "3.0J2EEAgent";
                                    }
                                }
                                moduleInfoMap[r].put(MessageBundleLoader.
                                        getModuleMessage(ModuleName,
                                        "property" + s +
                                        ".actualname"), inputBox.getValue());
                                moduleCommentsMap[r].put(MessageBundleLoader.
                                        getModuleMessage(ModuleName, "property"
                                        + s + ".actualname"),
                                        MessageBundleLoader.getModuleMessage
                                        (ModuleName, "property" + s +
                                        ".comment"));
                            }
                        }
                        writeModuleInfo[r] = new QawebCommon();
                        writeModuleInfo[r].createFileFromMap(moduleInfoMap[r],
                                moduleCommentsMap[r],
                                moduleFilename);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }              
                if (bpbean.getModWithPropMap().containsValue("agents")) {                    
                    try {
                        InetAddress ef = InetAddress.getLocalHost();
                        String clientHost = ef.getCanonicalHostName();
                        String ipaddress = ef.getHostAddress();
                        int ipLastIndex = ipaddress.lastIndexOf(".");
                        String num = ipaddress.substring(ipLastIndex + 1);
                        String partialIp = ipaddress.substring(0,
                                ipLastIndex + 1);
                        int ipNum = Integer.parseInt(num);
                        int PlusIpNum = ipNum + 5;
                        int MinuIpNum = ipNum - 5;
                        String plusIpAdress = partialIp + PlusIpNum;
                        String MinusIpAdress = partialIp + MinuIpNum;
                        int beginIndex = clientHost.indexOf(".");
                        clientHost = clientHost.substring(beginIndex + 1);
                        QawebCommon qawebCommon = new QawebCommon();
                        String moduleFilename = bpbean.getQatestHome() + 
                                fileSeperator + "resources" + fileSeperator +
                                "agents" + fileSeperator +
                                "AgentsTests.properties";
                        Map agentsInfo = qawebCommon.getMapFromProperties
                                (moduleFilename);
                        agentsInfo.put("AgentsTests2.policy0.condition0.att0",
                                "StartIp=" + ipaddress);
                        agentsInfo.put("AgentsTests2.policy0.condition0.att1",
                                "EndIp=" + ipaddress);
                        agentsInfo.put("AgentsTests2.policy2.condition0.att0",
                                "StartIp=" + MinusIpAdress);
                        agentsInfo.put("AgentsTests2.policy2.condition0.att1",
                                "EndIp=" + plusIpAdress);
                        agentsInfo.put("AgentsTests2.policy4.condition0.att0",
                                "StartIp=" + MinusIpAdress);
                        agentsInfo.put("AgentsTests2.policy4.condition0.att1",
                                "EndIp=" + plusIpAdress);
                        agentsInfo.put("AgentsTests2.policy6.condition0.att0",
                                "StartIp=" + MinusIpAdress);
                        agentsInfo.put("AgentsTests2.policy6.condition0.att1",
                                "EndIp=" + plusIpAdress);
                        agentsInfo.put("AgentsTests2.policy8.condition0.att0",
                                "StartIp=" + MinusIpAdress);
                        agentsInfo.put("AgentsTests2.policy8.condition0.att1",
                                "EndIp=" + plusIpAdress);
                        agentsInfo.put("AgentsTests1.policy0.condition0.att0",
                                "DnsName=*." + clientHost);
                        agentsInfo.put("AgentsTests2.policy4.condition2.att0",
                                "DnsName=" + clientHost);
                        agentsInfo.put("AgentsTests2.policy5.condition2.att0",
                                "DnsName=" + clientHost);
                        agentsInfo.put("AgentsTests2.policy8.condition2.att0",
                                "DnsName=" + clientHost);
                        Map comments = new HashMap();
                        qawebCommon.createFileFromMap(agentsInfo,
                                comments,
                                moduleFilename);
                    } catch (Exception e) {                        
                        e.printStackTrace();
                    }
                }
                return "summary";
            }
        }
    }
}

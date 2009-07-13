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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlMessages;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.menubar.MenuBar;
import com.icesoft.faces.component.menubar.MenuItem;
import com.icesoft.faces.component.paneltabset.PanelTab;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.sun.identity.qatest.qaweb.common.MessageBundleLoader;
import com.sun.identity.qatest.qaweb.common.QawebCommon;
import com.sun.identity.qatest.qaweb.common.Base64;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

/**
 * <p>This class is a session scope data bean which mainly deals with server
 * page of the web app.</p>
 * <p>An instance of this class will be created  automatically,
 * the first time , application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class ServerBean extends AbstractSessionBean implements ActionListener {

    PanelTabSet tabset = new PanelTabSet();
    private int noOfSvrTabs;
    List serverNames = new ArrayList();
    private static String fileSeperator = System.getProperty("file.separator");
    private Map oldModWithPropMap = new HashMap();
    private int oldModCounter = 0;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:
     * </strong> .This method is automatically generated, so any user-specified
     * code inserted here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }

    /**
     * <p>Construct a new session data bean instance.</p>
     */
    public ServerBean() {
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
            log("ServerBean Initialization Failure", e);
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
     * <p>This method returns the list of Server Names</p>
     * @return serverNames List of server Names
     */
    public List getServerNames() {
        return serverNames;
    }

    /**
     * <p>This method sets the list of server names</p>
     * @param serverNames List of Servers entered by the user
     */
    public void setServerNames(List serverNames) {
        this.serverNames = serverNames;
    }

    /**
     * <p>This method returns the number of server tabs</p>
     * @return noOfSvrTabs Number of server tabs
     */
    public int getNoOfSvrTabs() {
        return noOfSvrTabs;
    }

    /**
     * <p>This method sets the number of server tabs</p>
     * @param noOfSvrTabs Number of server tabs
     */
    public void setNoOfSvrTabs(int NoOfSvrTabs) {
        this.noOfSvrTabs = NoOfSvrTabs;
    }

    /**
     * <p>This method sets the Server Tabs</p>
     * @param paneltabset Panel Tab Set
     */
    public void setServerTabs(PanelTabSet paneltabset) {
        this.tabset = paneltabset;
    }

    /**
     * <p>This method returns the Server Tabs</p>
     * @return paneltabset Panel Tab Set
     */
    public PanelTabSet getServerTabs() {
        return tabset;
    }

    /**
     * Checks whether the string exists on the page
     */
    protected int getHtmlPageStringIndex(
            HtmlPage page,
            String searchStr)
            throws Exception {
        String strPage;
        try {
            strPage = page.asXml();
        } catch (java.lang.NullPointerException npe) {
            return 0;
        }
        int iIdx = strPage.indexOf(searchStr);
        return iIdx;
    }

    /**
     * <p>This method is called when the getServerDetails button is clicked
     * by the user.It is an ActionListener which listens to the event of button
     * click</p>
     * @param event This is Action event of button click
     */
    public void processAction(ActionEvent event) {        
        BuildInfoBean bpbean = (BuildInfoBean) getBean("BuildInfoBean");
        /*
         * Check the JSF phase in which the event was intiated.
         */
        PhaseId phaseId = event.getPhaseId();
        /*
         * Execute the logic only when INVOKE_PHASE is acheieved else queue the
         * events.
         */
        if (phaseId.equals(PhaseId.ANY_PHASE)) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else if (phaseId.equals(PhaseId.INVOKE_APPLICATION)) {
            PanelTabSet panelTabSet = new PanelTabSet();
            /*
             * Get the PanelTabeSet UI component. Event has been triggered by
             * htmlCommand Button which is residing in a grid which inturn is a
             * child of PanelTab . PanelTabset is the parent of Paneltab
             */
            panelTabSet = (PanelTabSet) event.getComponent().getParent().
                    getParent().getParent();
            int selectedTab = panelTabSet.getSelectedIndex();
            /*
             * Get the HtmlInputText UI component. Event has been triggered by
             * htmlCommand Button which is residing in a grid which inturn
             * contains the inputText. Server Name is the 1st UI InputText.
             */
            HtmlInputText selectedServerName = new HtmlInputText();
            selectedServerName = (HtmlInputText) event.getComponent().
                    getParent().findComponent(MessageBundleLoader.
                    getServerMessage("property" + 1 + ".actualname") +
                    selectedTab);
            HtmlInputText selectedServerURL = new HtmlInputText();
            selectedServerURL = (HtmlInputText) event.getComponent().
                    getParent().findComponent("com_iplanet_am_naming_url" +
                    selectedTab);
            HtmlInputText selectedAdminID = new HtmlInputText();
            selectedAdminID = (HtmlInputText) event.getComponent().
                    getParent().findComponent("amadmin_username" +
                    selectedTab);
            HtmlInputText selectedAdminPwd = new HtmlInputText();
            selectedAdminPwd = (HtmlInputText) event.getComponent().
                    getParent().findComponent("amadmin_password" +
                    selectedTab);
            Map serverMap = new HashMap();
            WebClient webclient = new WebClient();
            webclient.setTimeout(100000);
            try {
                int idx = selectedServerURL.getValue().toString().indexOf
                        ("/namingservice");
                String selectedServer = selectedServerURL.getValue().toString().
                        substring(0, idx);
                URL url = new URL(selectedServer);
                HtmlPage page = null;
                int pageIter = 0;
                boolean issueWithUrl = false;
                try {
                    page = (HtmlPage) webclient.getPage(url);
                } catch (com.gargoylesoftware.htmlunit.
                        FailingHttpStatusCodeException e) {
                    issueWithUrl = true;
                    FacesMessage msg = new FacesMessage("Server Configuration "
                            + "Information not Available");
                    FacesContext.getCurrentInstance().addMessage
                            (selectedServerURL.getClientId
                            (FacesContext.getCurrentInstance()), msg);
                }

                if (pageIter > 10) {
                    issueWithUrl = true;                   
                    FacesMessage msg = new FacesMessage("Server Configuration "
                            + "Information not Available");
                    FacesContext.getCurrentInstance().addMessage
                            (selectedServerURL.getClientId(FacesContext.
                            getCurrentInstance()), msg);
                }
                if (getHtmlPageStringIndex(page, "Not Found") != -1) {
                    issueWithUrl = true;                   
                    FacesMessage msg = new FacesMessage("Server Configuration "
                            + "Information not Available");
                    FacesContext.getCurrentInstance().addMessage
                            (selectedServerURL.getClientId(FacesContext.
                            getCurrentInstance()), msg);
                }

                if (issueWithUrl == false) {
                    if ((getHtmlPageStringIndex(page, "/config/options.htm") ==
                            -1)) {
                        HtmlForm form = page.getFormByName("Login");
                        HtmlHiddenInput txtUserName = (HtmlHiddenInput) form.
                                getInputByName("IDToken1");
                        txtUserName.setValueAttribute(selectedAdminID.
                                getValue().toString());
                        HtmlHiddenInput txtPwd = (HtmlHiddenInput) form.
                                getInputByName("IDToken2");
                        txtPwd.setValueAttribute(selectedAdminPwd.getValue().
                                toString());
                        page = (HtmlPage) form.submit();
                        URL showServerConfigURL = new URL(selectedServer +
                                "/showServerConfig.jsp?qaweb=set");
                        HtmlPage page1 = (HtmlPage) webclient.getPage
                                (showServerConfigURL);
                        String data = page1.asText().toString();
                        String correctData = data.substring(8);                       
                        StringTokenizer st = new StringTokenizer(correctData,
                                "|");
                        while (st.hasMoreTokens()) {                           
                            String tmp = st.nextToken();
                            String[] tmp1 = tmp.split("=", 2);
                            serverMap.put((String) tmp1[0].toString(),
                                    (String) tmp1[1].toString());
                        }                       
                        String ldapServer = (String) serverMap.get
                                ("sun-idrepo-ldapv3-config-ldap-server");
                        String[] templdapServer = ldapServer.split(":");
                        String ldapServerName = templdapServer[0];
                        String ldapPortNumber = templdapServer[1];                        
                        serverMap.put("sun-idrepo-ldapv3-config-ldap-server",
                                ldapServerName);
                        serverMap.put("sun-idrepo-ldapv3-config-ldap-port",
                                ldapPortNumber);
                        serverMap.put("amadmin_password",
                                selectedAdminPwd.getValue().toString());
                        serverMap.put("datastore-adminid", (String) 
                                serverMap.get
                                ("sun-idrepo-ldapv3-config-authid"));
                        serverMap.put("datastore-adminpw", (String)
                                serverMap.get
                                ("sun-idrepo-ldapv3-config-authpw"));
                        String encodedString = (String) serverMap.get
                                ("sun-idrepo-ldapv3-config-authpw");
                        serverMap.put("sun-idrepo-ldapv3-config-authpw",
                                new String(Base64.decode(encodedString)));
                        serverMap.put("datastore-adminpw", new String
                                (Base64.decode(encodedString)));
                        serverMap.put("datastore-root-suffix", (String) 
                                serverMap.get
                                ("sun-idrepo-ldapv3-config-organization_name"));
                        serverMap.put("datastore-type", "LDAPv3ForAMDS");
                        QawebCommon qawebCommon = new QawebCommon();                        
                        /*
                         * Get the starting and ending index of server 
                         * properties from resource bundle
                         */
                        int svrPropStartIndex = Integer.parseInt(
                                MessageBundleLoader.getServerMessage
                                ("serverPropStartIndex"));
                        int svrPropEndIndex = Integer.parseInt(
                                MessageBundleLoader.getServerMessage
                                ("serverPropEndIndex"));
                        for (int j = svrPropStartIndex + 1; j <=
                                svrPropEndIndex; j++) {
                            String propertyID = MessageBundleLoader.
                                    getServerMessage("property" + j +
                                    ".actualname");
                            if (propertyID.contains(".")) {
                                propertyID = propertyID.replace(".", "_");
                            }
                            UIInput inputBox = new UIInput();
                            if (MessageBundleLoader.getServerMessage
                                    ("property" + j +
                                    ".type").equals("HtmlInputText")) {
                                inputBox = new HtmlInputText();
                                inputBox = (HtmlInputText) panelTabSet.
                                        findComponent("server" + selectedTab).
                                        findComponent("serverGrid" +
                                        selectedTab).findComponent(propertyID +
                                        selectedTab);
                                inputBox.setValue((String) serverMap.get
                                        (MessageBundleLoader.getServerMessage
                                        ("property" + j + ".actualname")));
                            } else if (MessageBundleLoader.getServerMessage
                                    ("property" + j +
                                    ".type").equals("HtmlSelectOneMenu")) {
                                inputBox = new HtmlSelectOneMenu();
                                inputBox = (HtmlSelectOneMenu) panelTabSet.
                                        findComponent("server" + selectedTab).
                                        findComponent("serverGrid" + 
                                        selectedTab).findComponent(propertyID +
                                        selectedTab);
                                inputBox.setValue((String) serverMap.get(
                                        MessageBundleLoader.getServerMessage(
                                        "property" + j + ".actualname")));
                            }
                        }                        
                        int configPropStartIndex = Integer.parseInt(
                                MessageBundleLoader.getServerMessage
                                ("configPropStartIndex"));
                        int configPropEndIndex = Integer.parseInt(
                                MessageBundleLoader.getServerMessage
                                ("configPropEndIndex"));
                        for (int j = configPropStartIndex; j <=
                                configPropEndIndex; j++) {
                            String propertyID = MessageBundleLoader.
                                    getServerMessage("property" + j +
                                    ".actualname");
                            if (propertyID.contains(".")) {
                                propertyID = propertyID.replace(".", "_");
                            }
                            UIInput inputBox = new UIInput();
                            if (MessageBundleLoader.getServerMessage
                                    ("property" + j + ".type").equals
                                    ("HtmlInputText")) {
                                inputBox = new HtmlInputText();
                                inputBox = (HtmlInputText) panelTabSet.
                                        findComponent("server" + selectedTab).
                                        findComponent("configGrid" + 
                                        selectedTab).findComponent(propertyID +
                                        selectedTab);
                                inputBox.setValue((String) serverMap.get
                                        (MessageBundleLoader.getServerMessage
                                        ("property" + j + ".actualname")));
                            } else if (MessageBundleLoader.getServerMessage
                                    ("property" + j + ".type").equals
                                    ("HtmlSelectOneMenu")) {
                                inputBox = new HtmlSelectOneMenu();
                                inputBox = (HtmlSelectOneMenu) panelTabSet.
                                        findComponent("server" + selectedTab).
                                        findComponent("configGrid" + 
                                        selectedTab).findComponent(propertyID +
                                        selectedTab);
                                inputBox.setValue((String) serverMap.get(
                                        MessageBundleLoader.getServerMessage(
                                        "property" + j + ".actualname")));
                            }
                        }                        
                        int umPropStartIndex = Integer.parseInt(
                                MessageBundleLoader.getServerMessage
                                ("umPropStartIndex"));
                        int umPropEndIndex = Integer.parseInt(
                                MessageBundleLoader.getServerMessage
                                ("umPropEndIndex"));
                        try {
                            String umdataStoreFileName = bpbean.getQatestHome()
                                    + fileSeperator + "resources" +
                                    fileSeperator + "config" + fileSeperator +
                                    "UMGlobalDatastoreConfig.properties";
                            Map umDataStoreMap = qawebCommon.
                                    getMapFromProperties(umdataStoreFileName);
                            panelTabSet = (PanelTabSet) FacesContext.
                                    getCurrentInstance().getViewRoot().
                                    findComponent
                                    ("serverForm:serverPanelTabSet");
                            int umIndex = 1;
                            ServerBean sbean = (ServerBean) getBean
                                    ("ServerBean");
                            for (int j = umPropStartIndex; j <= umPropEndIndex;
                            j++) {
                                String propertyID = MessageBundleLoader.
                                        getServerMessage("property" + j +
                                        ".actualname");
                                if (propertyID.contains(".")) {
                                    propertyID = propertyID.replace(".", "_");
                                }
                                if (sbean.getNoOfSvrTabs() == 1) {
                                    umIndex = 1;
                                } else {
                                    umIndex = selectedTab;
                                }
                                if (MessageBundleLoader.getServerMessage
                                        ("property" + j + ".type").equals
                                        ("HtmlInputText")) {
                                    HtmlInputText inputBox = new
                                            HtmlInputText();
                                    inputBox = (HtmlInputText) panelTabSet.
                                            findComponent("server" +
                                            selectedTab).findComponent("umGrid"
                                            + selectedTab).findComponent
                                            (propertyID + selectedTab);
                                    inputBox.setValue((String) serverMap.get(
                                            MessageBundleLoader.
                                            getServerMessage("property" + j +
                                            ".actualname")));
                                }
                            }
                        } catch (FileNotFoundException fnfe) {
                            fnfe.printStackTrace();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        /*
                         * Get all the server information from Configurator
                         * Common.properties and Configurator-<servername>.
                         * properties into the map.
                         *
                         */
                        Map serverPropMap = new HashMap();
                        Map commonPropMap = new HashMap();
                        String configCommonFile = bpbean.getQatestHome() +
                                fileSeperator + "resources" + fileSeperator + 
                                "config" + fileSeperator + "default" +
                                fileSeperator + "ConfiguratorCommon.properties";
                        QawebCommon qawebCommon = new QawebCommon();
                        try {
                            commonPropMap = qawebCommon.getMapFromProperties
                                    (configCommonFile);
                        } catch (FileNotFoundException fnfe) {
                            fnfe.printStackTrace();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        File serverConfigFile = new File(bpbean.getQatestHome()+
                                fileSeperator + "resources" + fileSeperator +
                                "Configurator-" + selectedServerName.getValue()+
                                ".properties");
                        String serverConfigFileName = bpbean.getQatestHome() +
                                fileSeperator + "resources" + fileSeperator +
                                "Configurator-" + selectedServerName.getValue()+
                                ".properties";
                        /*
                         * If Configurator-<servername>.properties exists,
                         * set the values of InputText UI component in the
                         * server.jsp with the corresponding values present
                         * in the map.
                         */
                        if (serverConfigFile.exists()) {
                            try {
                                Map serverInfoMap = qawebCommon.
                                        getMapFromProperties(
                                        serverConfigFileName);
                                serverPropMap.putAll(serverInfoMap);
                                serverPropMap.putAll(commonPropMap);
                            } catch (FileNotFoundException fnfe) {
                                fnfe.printStackTrace();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            int svrPropStartIndex = Integer.parseInt(
                                    MessageBundleLoader.getServerMessage
                                    ("serverPropStartIndex"));
                            int svrPropEndIndex = Integer.parseInt(
                                    MessageBundleLoader.getServerMessage
                                    ("serverPropEndIndex"));
                            for (int j = svrPropStartIndex + 1; j <=
                                    svrPropEndIndex; j++) {
                                String propertyID = MessageBundleLoader.
                                        getServerMessage("property" + j +
                                        ".actualname");
                                if (propertyID.contains(".")) {
                                    propertyID = propertyID.replace(".", "_");
                                }
                                UIInput inputBox = new UIInput();
                                if (MessageBundleLoader.getServerMessage
                                        ("property" + j +
                                        ".type").equals("HtmlInputText")) {
                                    inputBox = new HtmlInputText();
                                    inputBox = (HtmlInputText) panelTabSet.
                                            findComponent("server" +
                                            selectedTab).
                                            findComponent("serverGrid" +
                                            selectedTab).
                                            findComponent(propertyID +
                                            selectedTab);
                                    inputBox.setValue((String) serverPropMap.get
                                            (MessageBundleLoader.
                                            getServerMessage("property" + j +
                                            ".actualname")));
                                } else if (MessageBundleLoader.getServerMessage
                                        ("property" + j + ".type").equals
                                        ("HtmlSelectOneMenu")) {
                                    inputBox = new HtmlSelectOneMenu();
                                    inputBox = (HtmlSelectOneMenu) panelTabSet.
                                            findComponent("server" +
                                            selectedTab).findComponent
                                            ("serverGrid" + selectedTab).
                                            findComponent(propertyID +
                                            selectedTab);
                                    inputBox.setValue((String) commonPropMap.
                                            get(MessageBundleLoader.
                                            getServerMessage("property" + j +
                                            ".actualname")));
                                }
                            }
                            int configPropStartIndex = Integer.parseInt(
                                    MessageBundleLoader.getServerMessage
                                    ("configPropStartIndex"));
                            int configPropEndIndex = Integer.parseInt(
                                    MessageBundleLoader.getServerMessage
                                    ("configPropEndIndex"));
                            for (int j = configPropStartIndex; j <=
                                    configPropEndIndex; j++) {
                                String propertyID = MessageBundleLoader.
                                        getServerMessage("property" + j +
                                        ".actualname");
                                if (propertyID.contains(".")) {
                                    propertyID = propertyID.replace(".", "_");
                                }
                                UIInput inputBox = new UIInput();
                                if (MessageBundleLoader.getServerMessage
                                        ("property" + j + ".type").equals
                                        ("HtmlInputText")) {
                                    inputBox = new HtmlInputText();
                                    inputBox = (HtmlInputText) panelTabSet.
                                            findComponent("server" +
                                            selectedTab).findComponent
                                            ("configGrid" + selectedTab).
                                            findComponent(propertyID +
                                            selectedTab);
                                    inputBox.setValue((String) serverPropMap.get
                                            (MessageBundleLoader.
                                            getServerMessage("property" + j +
                                            ".actualname")));
                                } else if (MessageBundleLoader.getServerMessage
                                        ("property" + j + ".type").equals
                                        ("HtmlSelectOneMenu")) {
                                    inputBox = new HtmlSelectOneMenu();
                                    inputBox = (HtmlSelectOneMenu) panelTabSet.
                                            findComponent("server" +
                                            selectedTab).
                                            findComponent("configGrid" +
                                            selectedTab).
                                            findComponent(propertyID +
                                            selectedTab);
                                    inputBox.setValue((String) commonPropMap.
                                            get(MessageBundleLoader.
                                            getServerMessage(
                                            "property" + j + ".actualname")));
                                }
                            }
                            int umPropStartIndex = Integer.parseInt(
                                    MessageBundleLoader.getServerMessage
                                    ("umPropStartIndex"));
                            int umPropEndIndex = Integer.parseInt(
                                    MessageBundleLoader.getServerMessage
                                    ("umPropEndIndex"));
                            try {
                                String umdataStoreFileName = bpbean.
                                        getQatestHome() + fileSeperator +
                                        "resources" + fileSeperator +
                                        "config" + fileSeperator +
                                        "UMGlobalDatastoreConfig.properties";
                                Map umDataStoreMap = qawebCommon.
                                        getMapFromProperties
                                        (umdataStoreFileName);
                                panelTabSet = (PanelTabSet) FacesContext.
                                        getCurrentInstance().getViewRoot().
                                        findComponent
                                        ("serverForm:serverPanelTabSet");
                                int umIndex = 1;
                                ServerBean sbean = (ServerBean) getBean
                                        ("ServerBean");
                                for (int j = umPropStartIndex; j <=
                                        umPropEndIndex; j++) {
                                    String propertyID = MessageBundleLoader.
                                            getServerMessage("property" + j +
                                            ".actualname");
                                    if (propertyID.contains(".")) {
                                        propertyID = propertyID.
                                                replace(".", "_");
                                    }
                                    if (sbean.getNoOfSvrTabs() == 1) {
                                        umIndex = 1;
                                    } else {
                                        umIndex = selectedTab;
                                    }
                                    if (MessageBundleLoader.getServerMessage
                                            ("property" + j + ".type").equals
                                            ("HtmlInputText")) {
                                        HtmlInputText inputBox = new
                                                HtmlInputText();
                                        inputBox = (HtmlInputText) panelTabSet.
                                                findComponent("server" +
                                                selectedTab).
                                                findComponent("umGrid" +
                                                selectedTab).
                                                findComponent(propertyID +
                                                selectedTab);
                                        inputBox.setValue((String)
                                                umDataStoreMap.get
                                                ("UMGlobalDatastoreConfig" +
                                                umIndex + "." +
                                                MessageBundleLoader.
                                                getServerMessage("property" + j
                                                + ".actualname") + "." + 0));
                                    }
                                }
                            } catch (FileNotFoundException fnfe) {
                                fnfe.printStackTrace();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    PanelTabSet serverTab = (PanelTabSet) FacesContext.
                            getCurrentInstance().getViewRoot().findComponent
                            ("serverForm:serverPanelTabSet");
                    HtmlInputText namingUrl = (HtmlInputText) serverTab.
                            findComponent("server" + selectedTab).
                            findComponent("serverGrid" + selectedTab).
                            findComponent("com_iplanet_am_naming_url" +
                            selectedTab);
                    FacesMessage msg = new FacesMessage("Server Configuration "
                            + "Information not Available");
                    FacesContext.getCurrentInstance().addMessage(namingUrl.
                            getClientId(FacesContext.getCurrentInstance()),
                            msg);

                }               
            } catch (Exception e) {
               e.printStackTrace();
            }
        }        
        FacesContext.getCurrentInstance().renderResponse();
    }

    /**
     * <p>This method is used to remove server tabs from server.jsp</p>
     * @param NoOfOldServers Number of ServerTabs from Last Run
     * @param NoOfTabstoBeRemoved Number of Server Tabs to be removed
     */
    public void removeTab(int NoOfOldServers, int NoOfTabstoBeRemoved) {
        ServerBean serverbean = (ServerBean) getBean("ServerBean");
        if (tabset == null) {
        } else {
            for (int i = 0; i <
                    NoOfTabstoBeRemoved; i++) {
                NoOfOldServers = NoOfOldServers - 1;
                PanelTab tab = (PanelTab) serverbean.tabset.findComponent
                        ("server" + NoOfOldServers);
                tabset.getChildren().remove(tab);
            }
        }
    }

    /**
     * <p>This method is used to add server tabs from server.jsp</p>
     * @param indexOfStartingTab Starting index of the new Server Tab
     * @param NoOfServerTabs Total number of server Tabs to be displayed.
     */
    public void addTab(int indexOfStartingTab, int NoOfServerTabs) {
        noOfSvrTabs = NoOfServerTabs;
        int svrPropStartIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("serverPropStartIndex"));
        int svrPropEndIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("serverPropEndIndex"));
        int configPropStartIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("configPropStartIndex"));
        int configPropEndIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("configPropEndIndex"));
        int umPropStartIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("umPropStartIndex"));
        int umPropEndIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("umPropEndIndex"));
        int menuEndIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("menuEndIndex"));
        /* Creating UI components for server.jsp */

        for (int i = indexOfStartingTab; i <
                NoOfServerTabs; i++) {
            PanelTab serverPanel = new PanelTab();
            HtmlPanelGrid menuGrid = new HtmlPanelGrid();
            HtmlPanelGrid serverGrid = new HtmlPanelGrid();
            HtmlPanelGrid configGrid = new HtmlPanelGrid();
            HtmlPanelGrid umGrid = new HtmlPanelGrid();
            HtmlMessages globalMessage = new HtmlMessages();
            globalMessage.setStyle("color : red;");
            MenuBar menubar = new MenuBar();
            for (int j = 1; j <= menuEndIndex; j++) {
                MenuItem menu = new MenuItem();
                menu.setId(MessageBundleLoader.getServerMessage("menu" + j +
                        ".id") + i);
                menu.setValue(MessageBundleLoader.getServerMessage("menu" + j
                        + ".value"));
                MenuChangeListener menuListener = new MenuChangeListener();
                menu.addActionListener(menuListener);
                menu.setImmediate(true);
                menubar.getChildren().add(menu);
            }
            menubar.setId("menuBar" + i);
            for (int j = svrPropStartIndex; j <=
                    svrPropEndIndex; j++) {
                String propertyID = MessageBundleLoader.getServerMessage
                        ("property" + j + ".actualname");
                if (propertyID.contains(".")) {
                    propertyID = propertyID.replace(".", "_");
                }
                HtmlOutputLabel outputlabel = new HtmlOutputLabel();
                outputlabel.setId("outputlabel" + i + j);
                outputlabel.setValue(MessageBundleLoader.getServerMessage
                        ("property" + j + ".displayname"));
                UIInput inputBox = new UIInput();
                if (MessageBundleLoader.getServerMessage("property" + j +
                        ".type").equals("HtmlInputText")) {
                    inputBox = new HtmlInputText();
                    inputBox.setId(propertyID + i);
                    inputBox.setRequired(true);
                    inputBox.setRequiredMessage(MessageBundleLoader.
                            getServerMessage("property" + j +
                            ".displayname") + " is a required field");
                    ((HtmlInputText) inputBox).setPartialSubmit(true);
                    if (MessageBundleLoader.getServerMessage("property" + j +
                            ".actualname").equals("ds_dirmgrpasswd")) {
                        LDAPValidationActionListener ldapListener = new
                                LDAPValidationActionListener();
                        inputBox.addValidator(ldapListener);
                        ((HtmlInputText) inputBox).setPartialSubmit(true);
                    }
                } else if (MessageBundleLoader.getServerMessage("property" + j +
                        ".type").equals("HtmlSelectOneMenu")) {
                    inputBox = new HtmlSelectOneMenu();
                    inputBox.setId(propertyID + i);
                    if (MessageBundleLoader.getServerMessage("property" + j +
                            ".valueChangeListener").equals("true")) {
                        ServerEventListener listener = new
                                ServerEventListener();
                        inputBox.addValueChangeListener(listener);
                        ((HtmlSelectOneMenu) inputBox).setPartialSubmit(true);
                        inputBox.setImmediate(true);
                    }
                    List valueList = new ArrayList();
                    UISelectItems items = new UISelectItems();
                    int NoOfMenuValues = Integer.parseInt(MessageBundleLoader.
                            getServerMessage("property" + j + ".NoOfValues"));
                    for (int m = 1; m <=
                            NoOfMenuValues; m++) {
                        SelectItem selectItem = new SelectItem(
                                MessageBundleLoader.getServerMessage("property"
                                + j + ".value" + m),
                                MessageBundleLoader.getServerMessage("property"
                                + j + ".value" + m));
                        valueList.add(selectItem);
                        items.setValue(valueList);
                        inputBox.getChildren().add(items);
                    }
                }
                UIComponent uicomp;
                if (MessageBundleLoader.getServerMessage("property" + j +
                        ".actualname").equals("amadmin_password")) {
                    HtmlCommandButton getServerCommandButton = new
                            HtmlCommandButton();
                    getServerCommandButton.setId("serverActionistenerButton"
                            + i);
                    getServerCommandButton.setValue("Get Server Information");
                    getServerCommandButton.addActionListener(new ServerBean());
                    getServerCommandButton.setPartialSubmit(true);
                    uicomp = getServerCommandButton;
                } else {
                    HtmlMessage errorMessage = new HtmlMessage();
                    errorMessage.setFor(propertyID + i);
                    errorMessage.setId("error" + i + j);
                    errorMessage.setStyle("color: red");
                    uicomp = errorMessage;
                }
                HtmlOutputLabel outputLblDummy = new HtmlOutputLabel();
                HtmlOutputText outputcomments = new HtmlOutputText();
                outputcomments.setId("outputcomments" + i + j);
                outputcomments.setValue(MessageBundleLoader.getServerMessage
                        ("property" + j + ".comment"));
                outputcomments.setStyle("font-size: x-small; color: grey");
                HtmlOutputLabel outputlblDummy = new HtmlOutputLabel();

                serverGrid.getChildren().add(outputlabel);
                serverGrid.getChildren().add(inputBox);
                serverGrid.getChildren().add(uicomp);
                serverGrid.getChildren().add(outputLblDummy);
                serverGrid.getChildren().add(outputcomments);
                serverGrid.getChildren().add(outputlblDummy);
            }
            for (int j = configPropStartIndex; j <=
                    configPropEndIndex; j++) {
                String propertyID = MessageBundleLoader.getServerMessage
                        ("property" + j + ".actualname");
                if (propertyID.contains(".")) {
                    propertyID = propertyID.replace(".", "_");
                }
                HtmlOutputLabel outputlabel = new HtmlOutputLabel();
                outputlabel.setId("outputlabel" + i + j);
                outputlabel.setValue(MessageBundleLoader.getServerMessage
                        ("property" + j + ".displayname"));
                UIInput inputBox = new UIInput();
                if (MessageBundleLoader.getServerMessage("property" + j +
                        ".type").equals("HtmlInputText")) {
                    inputBox = new HtmlInputText();
                    inputBox.setId(propertyID + i);
                    inputBox.setRequired(true);
                    inputBox.setRequiredMessage(MessageBundleLoader.
                            getServerMessage("property" + j +
                            ".displayname") + " is a required field");
                    ((HtmlInputText) inputBox).setPartialSubmit(true);
                    if (MessageBundleLoader.getServerMessage("property" + j +
                            ".actualname").equals("ds_dirmgrpasswd")) {
                        LDAPValidationActionListener ldapListener = new
                                LDAPValidationActionListener();
                        inputBox.addValidator(ldapListener);
                        ((HtmlInputText) inputBox).setPartialSubmit(true);
                    }
                } else if (MessageBundleLoader.getServerMessage("property" + j +
                        ".type").equals("HtmlSelectOneMenu")) {
                    inputBox = new HtmlSelectOneMenu();
                    inputBox.setId(propertyID + i);
                    if (MessageBundleLoader.getServerMessage("property" + j +
                            ".valueChangeListener").equals("true")) {
                        ServerEventListener listener = new
                                ServerEventListener();
                        inputBox.addValueChangeListener(listener);
                        ((HtmlSelectOneMenu) inputBox).setPartialSubmit(true);
                        inputBox.setImmediate(true);
                    }
                    List valueList = new ArrayList();
                    UISelectItems items = new UISelectItems();
                    int NoOfMenuValues = Integer.parseInt(MessageBundleLoader.
                            getServerMessage("property" + j + ".NoOfValues"));
                    for (int m = 1; m <=
                            NoOfMenuValues; m++) {
                        SelectItem selectItem = new SelectItem(
                                MessageBundleLoader.getServerMessage("property"
                                + j + ".value" + m),
                                MessageBundleLoader.getServerMessage("property"
                                + j + ".value" + m));
                        valueList.add(selectItem);
                        items.setValue(valueList);
                        inputBox.getChildren().add(items);
                    }
                }
                UIComponent uicomp;
                if (MessageBundleLoader.getServerMessage("property" + j +
                        ".actualname").equals("amadmin_password")) {
                    HtmlCommandButton getServerCommandButton = new
                            HtmlCommandButton();
                    getServerCommandButton.setId("serverActionistenerButton"
                            + i);
                    getServerCommandButton.setValue("Get Server Information");
                    getServerCommandButton.addActionListener(new ServerBean());
                    getServerCommandButton.setPartialSubmit(true);
                    uicomp = getServerCommandButton;
                } else {
                    HtmlMessage errorMessage = new HtmlMessage();
                    errorMessage.setFor(propertyID + i);
                    errorMessage.setId("error" + i + j);
                    errorMessage.setStyle("color: red");
                    uicomp = errorMessage;
                }
                HtmlOutputLabel outputLblDummy = new HtmlOutputLabel();
                HtmlOutputText outputcomments = new HtmlOutputText();
                outputcomments.setId("outputcomments" + i + j);
                outputcomments.setValue(MessageBundleLoader.getServerMessage
                        ("property" + j + ".comment"));
                HtmlOutputLabel outputlblDummy = new HtmlOutputLabel();
                configGrid.getChildren().add(outputlabel);
                configGrid.getChildren().add(inputBox);
                configGrid.getChildren().add(uicomp);
                configGrid.getChildren().add(outputLblDummy);
                configGrid.getChildren().add(outputcomments);
                configGrid.getChildren().add(outputlblDummy);

            }
            for (int j = umPropStartIndex; j <=
                    umPropEndIndex; j++) {
                String propertyID = MessageBundleLoader.getServerMessage
                        ("property" + j + ".actualname");
                if (propertyID.contains(".")) {
                    propertyID = propertyID.replace(".", "_");
                }
                HtmlOutputLabel outputlabel = new HtmlOutputLabel();
                outputlabel.setId("outputlabel" + i + j);
                outputlabel.setValue(MessageBundleLoader.getServerMessage(
                        "property" + j + ".displayname"));
                UIInput inputBox = new UIInput();
                if (MessageBundleLoader.getServerMessage("property" + j +
                        ".type").equals("HtmlInputText")) {
                    inputBox = new HtmlInputText();
                    inputBox.setId(propertyID + i);
                    inputBox.setRequired(true);
                    inputBox.setRequiredMessage(MessageBundleLoader.
                            getServerMessage("property" + j +
                            ".displayname") + " is a required field");
                    ((HtmlInputText) inputBox).setPartialSubmit(true);
                    if (MessageBundleLoader.getServerMessage("property" + j +
                            ".actualname").equals("datastore-root-suffix")) {
                        LDAPValidationActionListener ldapListener = new
                                LDAPValidationActionListener();
                        inputBox.addValidator(ldapListener);
                        ((HtmlInputText) inputBox).setPartialSubmit(true);
                    }
                } else if (MessageBundleLoader.getServerMessage("property" + j +
                        ".type").equals("HtmlSelectOneMenu")) {
                    inputBox = new HtmlSelectOneMenu();
                    inputBox.setId(propertyID + i);
                    List valueList = new ArrayList();
                    UISelectItems items = new UISelectItems();
                    int NoOfMenuValues = Integer.parseInt(MessageBundleLoader.
                            getServerMessage("property" + j + ".NoOfValues"));
                    for (int m = 1; m <=
                            NoOfMenuValues; m++) {
                        SelectItem selectItem = new SelectItem(
                                MessageBundleLoader.getServerMessage(
                                "property" + j + ".value" + m),
                                MessageBundleLoader.getServerMessage(
                                "property" + j + ".value" + m));
                        valueList.add(selectItem);
                        items.setValue(valueList);
                        inputBox.getChildren().add(items);
                    }
                }
                HtmlMessage errorMessage = new HtmlMessage();
                errorMessage.setFor(propertyID + i);
                errorMessage.setId("error" + i + j);
                errorMessage.setStyle("color: red");
                HtmlOutputLabel outputlabel_dummy1 = new HtmlOutputLabel();
                HtmlOutputText outputcomments = new HtmlOutputText();
                outputcomments.setId("outputcomments" + i + j);
                outputcomments.setValue(MessageBundleLoader.getServerMessage(
                        "property" + j + ".comment"));
                HtmlOutputLabel outputlabel_dummy2 = new HtmlOutputLabel();
                umGrid.getChildren().add(outputlabel);
                umGrid.getChildren().add(inputBox);
                umGrid.getChildren().add(errorMessage);
                umGrid.getChildren().add(outputlabel_dummy1);
                umGrid.getChildren().add(outputcomments);
                umGrid.getChildren().add(outputlabel_dummy2);
            }
            menuGrid.setId("menuGrid" + i);
            menuGrid.getChildren().add(menubar);
            menuGrid.getChildren().add(globalMessage);
            serverGrid.setId("serverGrid" + i);
            serverGrid.setBgcolor("#FFFFCC");
            serverGrid.setColumns(3);
            umGrid.setId("umGrid" + i);
            umGrid.setColumns(3);
            umGrid.setBgcolor("#FFD5FF");
            configGrid.setId("configGrid" + i);
            configGrid.setColumns(3);
            configGrid.setBgcolor("#C6FFFF");
            int serverNumber = i + 1;
            serverPanel.setId("server" + i);
            serverPanel.setLabel("server" + serverNumber);
            serverGrid.setVisible(true);
            configGrid.setVisible(false);
            umGrid.setVisible(false);
            serverPanel.getChildren().add(menuGrid);
            serverPanel.getChildren().add(serverGrid);
            tabset.setStyle("width: 800px;");
            serverPanel.getChildren().add(configGrid);
            serverPanel.getChildren().add(umGrid);
            tabset.getChildren().add(serverPanel);
        }
    }

    /**
     * <p>This method is used to navigate back to the last page</p>
     */
    public String goBackAction() {
        return "home";
    }

    /**
     * <p>This method is called on Submit Button action of server.jsp. It writes
     * the server properties to the respective files </p>
     */
    public String ServerButtonAction() {
        PanelTabSet panelTabSet = (PanelTabSet) FacesContext.
                getCurrentInstance().getViewRoot().
                findComponent("serverForm:serverPanelTabSet");
        int svrPropStartIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("serverPropStartIndex"));
        int svrPropEndIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("serverPropEndIndex"));
        int configPropStartIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("configPropStartIndex"));
        int configPropEndIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("configPropEndIndex"));
        int umPropStartIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("umPropStartIndex"));
        int umPropEndIndex = Integer.parseInt(
                MessageBundleLoader.getServerMessage("umPropEndIndex"));
        Iterator it = FacesContext.getCurrentInstance().getMessages();
        /* IF there are error messages in the queue, then remain in the same
        page */
        if (it.hasNext()) {
            return null;
        } else {
            /*Multi Server Validation. Check to see if the inactive Tabs have
            null values in the InputText COmponents */
            boolean hasError = false;
            for (int s = 0; s < noOfSvrTabs; s++) {
                HtmlPanelGrid serverGrid = (HtmlPanelGrid) panelTabSet.
                        findComponent("server" + s).
                        findComponent("serverGrid" + s);
                HtmlPanelGrid configGrid = (HtmlPanelGrid) panelTabSet.
                        findComponent("server" + s).
                        findComponent("configGrid" + s);
                HtmlPanelGrid umGrid = (HtmlPanelGrid) panelTabSet.
                        findComponent("server" + s).
                        findComponent("umGrid" + s);
                for (int q = svrPropStartIndex; q <= svrPropEndIndex; q++) {
                    UIInput inputBox = new UIInput();
                    String propertyID = MessageBundleLoader.getServerMessage
                            ("property" + q + ".actualname");
                    if (propertyID.contains(".")) {
                        propertyID = propertyID.replace(".", "_");
                    }
                    if (MessageBundleLoader.getServerMessage("property" + q +
                            ".type").equals("HtmlInputText")) {
                        inputBox = new HtmlInputText();
                        inputBox = (HtmlInputText) serverGrid.findComponent(
                                propertyID + s);
                        if (inputBox.getValue() == null) {
                            FacesMessage msg = new FacesMessage
                                    (MessageBundleLoader.getServerMessage
                                    ("property" + q + ".displayname") +
                                    "is a Required field");
                            panelTabSet.setSelectedIndex(s);
                            FacesContext.getCurrentInstance().addMessage
                                    (inputBox.getClientId(FacesContext.
                                    getCurrentInstance()), msg);
                            hasError = true;
                        }
                    }
                }

                for (int q = configPropStartIndex; q <= configPropEndIndex;
                q++) {
                    UIInput inputBox = new UIInput();
                    String propertyID = MessageBundleLoader.getServerMessage
                            ("property" + q + ".actualname");
                    if (propertyID.contains(".")) {
                        propertyID = propertyID.replace(".", "_");
                    }
                    if (MessageBundleLoader.getServerMessage("property" + q +
                            ".type").equals("HtmlInputText")) {
                        inputBox = new HtmlInputText();
                        inputBox = (HtmlInputText) configGrid.findComponent(
                                propertyID + s);
                        if (inputBox.getValue() == null) {
                            FacesMessage msg = new FacesMessage
                                    ("Value Required");
                            panelTabSet.setSelectedIndex(s);
                            FacesContext.getCurrentInstance().addMessage
                                    (inputBox.getClientId(FacesContext.
                                    getCurrentInstance()), msg);
                            hasError = true;
                        }
                    }
                }
                for (int q = umPropStartIndex; q <=
                        umPropEndIndex; q++) {
                    String propertyID = MessageBundleLoader.getServerMessage
                            ("property" + q + ".actualname");
                    if (propertyID.contains(".")) {
                        propertyID = propertyID.replace(".", "_");
                    }
                    if (MessageBundleLoader.getServerMessage("property" + q +
                            ".type").equals("HtmlInputText")) {
                        HtmlInputText inputBox = new HtmlInputText();
                        inputBox =
                                (HtmlInputText) umGrid.findComponent
                                (propertyID + s);
                        if (inputBox.getValue() == null) {
                            FacesMessage msg = new FacesMessage
                                    ("Value Required");
                            panelTabSet.setSelectedIndex(s);
                            FacesContext.getCurrentInstance().
                                    addMessage(inputBox.getClientId
                                    (FacesContext.getCurrentInstance()), msg);
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
                BuildInfoBean bpbean = (BuildInfoBean) getBean("BuildInfoBean");
                ModuleBean mbean = (ModuleBean) getBean("ModuleBean");

                serverNames.clear();

                for (int s = 0; s < noOfSvrTabs; s++) {
                    HtmlInputText serverName = new HtmlInputText();
                    HtmlPanelGrid serverGrid = (HtmlPanelGrid) panelTabSet.
                            findComponent("server" + s).
                            findComponent("serverGrid" + s);
                    serverName = (HtmlInputText) serverGrid.findComponent
                            (MessageBundleLoader.getServerMessage("property" +
                            1 + ".actualname") + s);
                    serverNames.add(serverName.getValue());
                }
                for (int s = 0; s <
                        noOfSvrTabs; s++) {
                    try {
                        HtmlInputText serverName = new HtmlInputText();
                        HtmlPanelGrid serverGrid = (HtmlPanelGrid) panelTabSet.
                                findComponent("server" + s).
                                findComponent("serverGrid" + s);
                        HtmlPanelGrid umGrid = (HtmlPanelGrid) panelTabSet.
                                findComponent("server" + s).
                                findComponent("umGrid" + s);
                        HtmlPanelGrid configGrid = (HtmlPanelGrid) panelTabSet.
                                findComponent("server" + s).
                                findComponent("configGrid" + s);
                        serverName = (HtmlInputText) serverGrid.findComponent
                                (MessageBundleLoader.getServerMessage
                                ("property" + 1 + ".actualname") + s);
                        QawebCommon qawebCommon = new QawebCommon();
                        Map[] serverMap = new Map[noOfSvrTabs];
                        serverMap[s] = new HashMap();
                        Map[] commentsMap = new Map[noOfSvrTabs];
                        commentsMap[s] = new HashMap();
                        String serverConfigFileName = bpbean.getQatestHome() +
                                fileSeperator + "resources" + fileSeperator +
                                "Configurator-" + serverName.getValue() +
                                ".properties";
                        File serverConfigFile = new File(bpbean.getQatestHome()
                                + fileSeperator + "resources" +
                                fileSeperator + "Configurator-" +
                                serverName.getValue() + ".properties");
                        if (serverConfigFile.exists()) {
                            serverMap[s] = qawebCommon.getMapFromProperties
                                    (serverConfigFileName);
                        }
                        for (int q = svrPropStartIndex + 1; q <=
                                svrPropEndIndex; q++) {
                            UIInput inputBox = new UIInput();
                            String propertyID = MessageBundleLoader.
                                    getServerMessage("property" + q +
                                    ".actualname");
                            if (propertyID.contains(".")) {
                                propertyID = propertyID.replace(".", "_");
                            }
                            if (MessageBundleLoader.getServerMessage
                                    ("property" + q + ".type").equals
                                    ("HtmlInputText")) {
                                inputBox = new HtmlInputText();
                                inputBox = (HtmlInputText) serverGrid.
                                        findComponent(propertyID + s);
                                serverMap[s].put(MessageBundleLoader.
                                        getServerMessage(
                                        "property" + q + ".actualname"),
                                        inputBox.getValue());
                                commentsMap[s].put(MessageBundleLoader.
                                        getServerMessage(
                                        "property" + q + ".actualname"),
                                        MessageBundleLoader.getServerMessage(
                                        "property" + q + ".comment"));
                            } else if (MessageBundleLoader.getServerMessage(
                                    "property" + q + ".type").equals(
                                    "HtmlSelectOneMenu")) {
                                inputBox = new HtmlSelectOneMenu();
                                inputBox = (HtmlSelectOneMenu) serverGrid.
                                        findComponent(propertyID + s);
                                serverMap[s].put(MessageBundleLoader.
                                        getServerMessage(
                                        "property" + q + ".actualname"),
                                        inputBox.getValue());
                                commentsMap[s].put(MessageBundleLoader.
                                        getServerMessage(
                                        "property" + q + ".actualname"),
                                        MessageBundleLoader.getServerMessage(
                                        "property" + q + ".comment"));
                            }
                        }
                        for (int q = configPropStartIndex; q <=
                                configPropEndIndex; q++) {
                            UIInput inputBox = new UIInput();
                            String propertyID = MessageBundleLoader.
                                    getServerMessage("property" + q +
                                    ".actualname");
                            if (propertyID.contains(".")) {
                                propertyID = propertyID.replace(".", "_");
                            }
                            if (MessageBundleLoader.getServerMessage
                                    ("property" + q + ".type").equals
                                    ("HtmlInputText")) {
                                inputBox = new HtmlInputText();
                                inputBox = (HtmlInputText) configGrid.
                                        findComponent(propertyID + s);
                                serverMap[s].put(MessageBundleLoader.
                                        getServerMessage(
                                        "property" + q + ".actualname"),
                                        inputBox.getValue());
                                commentsMap[s].put(MessageBundleLoader.
                                        getServerMessage(
                                        "property" + q + ".actualname"),
                                        MessageBundleLoader.getServerMessage(
                                        "property" + q + ".comment"));
                            } else if (MessageBundleLoader.getServerMessage(
                                    "property" + q + ".type").equals(
                                    "HtmlSelectOneMenu")) {
                                inputBox = new HtmlSelectOneMenu();
                                inputBox = (HtmlSelectOneMenu) configGrid.
                                        findComponent(propertyID + s);
                                serverMap[s].put(MessageBundleLoader.
                                        getServerMessage(
                                        "property" + q + ".actualname"),
                                        inputBox.getValue());
                                commentsMap[s].put(MessageBundleLoader.
                                        getServerMessage(
                                        "property" + q + ".actualname"),
                                        MessageBundleLoader.getServerMessage(
                                        "property" + q + ".comment"));
                            }
                        }
                        if (s == 0) {
                            if (noOfSvrTabs > 2) {
                                if (noOfSvrTabs == 4) {
                                    serverMap[s].put("wsfed_sp",
                                            serverNames.get(3));
                                }
                                serverMap[s].put("multiprotocol_enabled",
                                        "true");
                                serverMap[s].put("idff_sp", serverNames.get(2));
                            } else {
                                serverMap[s].put("multiprotocol_enabled",
                                        "false");
                            }
                        }
                        qawebCommon.createFileFromMap(serverMap[s],
                                commentsMap[s],
                                serverConfigFileName);
                        String filename_ds = bpbean.getQatestHome() +
                                fileSeperator + "resources" + fileSeperator +
                                "config" + fileSeperator +
                                "UMGlobalDatastoreConfig.properties";
                        QawebCommon getdsmap = new QawebCommon();
                        Map[] dataStoreMap = new Map[noOfSvrTabs];
                        dataStoreMap[s] = new HashMap();
                        dataStoreMap[s] = getdsmap.getMapFromProperties
                                (filename_ds);
                        Map[] dataStoreCommentsMap = new Map[noOfSvrTabs];
                        dataStoreCommentsMap[s] = new HashMap();
                        int umIndex = 1;
                        for (int q = umPropStartIndex; q <=
                                umPropEndIndex; q++) {
                            if (noOfSvrTabs != 1) {
                                umIndex = s;
                            }
                            String propertyID = MessageBundleLoader.
                                    getServerMessage("property" + q +
                                    ".actualname");
                            if (propertyID.contains(".")) {
                                propertyID = propertyID.replace(".", "_");
                            }
                            if (MessageBundleLoader.getServerMessage
                                    ("property" + q + ".type").equals
                                    ("HtmlInputText")) {
                                HtmlInputText inputBox = new HtmlInputText();
                                inputBox = (HtmlInputText) umGrid.findComponent
                                        (propertyID + s);
                                dataStoreMap[s].put("UMGlobalDatastoreConfig" +
                                        umIndex + "." + MessageBundleLoader.
                                        getServerMessage("property" + q +
                                        ".actualname") + "." + 0,
                                        inputBox.getValue());
                                dataStoreCommentsMap[s].put
                                        ("UMGlobalDatastoreConfig" + umIndex +
                                        "." + MessageBundleLoader.
                                        getServerMessage("property" + q +
                                        ".actualname") + "." + 0,
                                        MessageBundleLoader.getServerMessage(
                                        "property" + q + ".comment"));
                            }
                        }
                        qawebCommon.createFileFromMap(dataStoreMap[s],
                                dataStoreCommentsMap[s], filename_ds);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                boolean summary = true;
                if (bpbean.getModWithPropMap().size() > 0 &&
                        oldModWithPropMap.isEmpty()) {
                    summary = false;                    
                    for (int i = 0; i < bpbean.getModWithPropMap().size();
                            i++) {
                        mbean.AddModuleTab((String) bpbean.getModWithPropMap().
                                get(i));
                        oldModWithPropMap.put(oldModCounter,
                                bpbean.getModWithPropMap().
                                get(i));
                        oldModCounter++;
                    }
                } else if (bpbean.getModWithPropMap().size() > 0 &&
                        !oldModWithPropMap.isEmpty()) {
                    summary = false;
                    for (int i = 0; i < bpbean.getModWithPropMap().size();
                    i++) {
                        if (!oldModWithPropMap.containsValue(bpbean.
                                getModWithPropMap().get(i))) {
                            mbean.AddModuleTab((String) bpbean.
                                    getModWithPropMap().get(i));
                        }
                    }
                    for (int i = 0; i < oldModWithPropMap.size(); i++) {                      
                        if (!bpbean.getModWithPropMap().containsValue
                                (oldModWithPropMap.get(i))) {
                            mbean.removeModuleTab(oldModWithPropMap.get(i).
                                    toString());
                        }
                    }
                    oldModWithPropMap.clear();
                    oldModWithPropMap.putAll(bpbean.getModWithPropMap());
                    if (bpbean.getModWithPropMap().containsValue
                            ("authentication") && mbean.isAuthSameAsUM ==
                            true) {
                        FacesContext.getCurrentInstance().getViewRoot().getId();
                        HtmlPanelGrid umGrid = (HtmlPanelGrid) panelTabSet.
                                findComponent("server" + 0).
                                findComponent("umGrid" + 0);
                        HtmlInputText server = (HtmlInputText) umGrid.
                                findComponent
                                ("sun-idrepo-ldapv3-config-ldap-server" + 0);
                        HtmlInputText port = (HtmlInputText) umGrid.
                                findComponent
                                ("sun-idrepo-ldapv3-config-ldap-port" + 0);
                        HtmlInputText baseDN = (HtmlInputText) umGrid.
                                findComponent("datastore-root-suffix" + 0);
                        HtmlInputText bindDN = (HtmlInputText) umGrid.
                                findComponent("datastore-adminid" + 0);
                        HtmlInputText bindPwd = (HtmlInputText) umGrid.
                                findComponent("datastore-adminpw" + 0);
                        mbean.setHost(server.getValue().toString() + ":" +
                                port.getValue());
                        mbean.setBaseDN(baseDN.getValue().toString());
                        mbean.setBindDN(bindDN.getValue().toString());
                        mbean.setBindPwd(bindPwd.getValue().toString());
                    }
                }
                if (summary == true) {
                    return "summary";
                } else {
                    return "module";
                }
            }
        }
    }
}

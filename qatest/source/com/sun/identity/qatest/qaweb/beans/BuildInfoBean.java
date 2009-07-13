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

import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.component.ext.HtmlSelectManyCheckbox;
import com.sun.identity.qatest.qaweb.common.MessageBundleLoader;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 * <p>This class is a session scope data bean which mainly deals with home page
 * of the web app.
 * <p>An instance of this class will be created  automatically,
 * the first time , application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p> 
 */
public class BuildInfoBean extends AbstractSessionBean {

    private List<String> selSingleSvrMod;
    private List<String> selMultiSvrMod;
    private List<String> selSanityMod;
    private List<String> selAllMod;
    private List<String> selectedModules = new ArrayList<String>();
    private String qatestHome;
    private String reportDir;
    private String selExeMode;
    private Map modWithPropMap = new HashMap();
    private int noOfModWithProp = 0;
    private boolean sanity = false;
    private boolean all = false;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:
     * </strong>
     * This method is automatically generated, so any user-specified code 
     * inserted here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }   

    /**
     * <p>Construct a new BuildInfoBean instance.</p>
     */
    public BuildInfoBean() {
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
            log("ModuleInfoBean Initialization Failure", e);
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
     * <p>This method returns the list of selected modules</p>
     * @return List of Selected Modules
     */
    public List<String> getSelectedModules() {
        return selectedModules;
    }

    /**
     * <p>This method sets the list of selected modules</p>
     * @param selectedModules List of Selected Modules
     */
    public void setSelectedModules(List<String> selectedModules) {
        this.selectedModules = selectedModules;
    }

    /**
     * <p>This method returns the Map of modules which need
     * extra properties to be set in order to run using QA Automation</p>
     * @return Map of Modules with extra property requirements
     */
    public Map getModWithPropMap() {
        return modWithPropMap;
    }

    /**
     * <p>This method sets the Map of modules which need
     * extra properties to be set in order to run using QA Automation</p>
     * @param modWithPropMap Map of Modules with extra property requirements
     */
    public void setModWithPropMap(Map modWithPropMap) {
        this.modWithPropMap = modWithPropMap;
    }

    /**
     * <p>This method returns the "All" module in a list</p>
     * @return selected "All" Module in a list
     */
    public List<String> getSelAllMod() {
        return selAllMod;
    }

    /**
     * <p>This method sets the "All" module in a list</p>
     * @param selAllMod selected "All" Module
     */
    public void setSelAllMod(List<String> selAllMod) {
        this.selAllMod = selAllMod;
    }

    /**
     * <p>This method returns the "Sanity" module in a list</p>
     * @return Sanity Module in a List
     */
    public List<String> getSelSanityMod() {
        return selSanityMod;
    }

    /**
     * <p>This method sets the "Sanity" module in a list</p>
     * @param selSanityMod Sanity Module selected
     */
    public void setSelSanityMod(List<String> selSanityMod) {
        this.selSanityMod = selSanityMod;
    }

    /**
     * <p>This method  returns the list of Execution Modes</p>
     * @return List of selected Execution Modes
     */
    public String getSelExeMode() {
        return selExeMode;
    }

    /**
     * <p>This method sets the list of Execution Modes</p>
     * @param selExeMode List of selected Execution Modes
     */
    public void setSelExeMode(String selExeMode) {
        this.selExeMode = selExeMode;
    }

    /**
     * <p>This method returns QatestHome</p>
     * @return qatest Home directory
     */
    public String getQatestHome() {
        return qatestHome;
    }

    /**
     * <p>This method sets QatestHome</p>
     * @param qatestHome qatest home directory
     */
    public void setQatestHome(String qatestHome) {
        this.qatestHome = qatestHome;
    }

    /**
     * <p>This method returns ReportDirectory</p>
     * @return report directory
     */
    public String getReportDir() {
        return reportDir;
    }

    /**
     * <p>This method sets report Directory</p>
     * @param reportDir report directory
     */
    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    /**
     * <p>This method sets the list of Single Server Modules</p>
     * @param selSingleSvrMod List of selected SIngle Server Modules
     */
    public void setSelSingleSvrMod(List<String> selSingleSvrMod) {
        this.selSingleSvrMod = selSingleSvrMod;
    }

    /**
     * <p>This method returns the list of Single Server Modules</p>
     * @return List of selected SIngle Server Modules
     */
    public List<String> getSelSingleSvrMod() {
        return selSingleSvrMod;
    }

    /**
     * <p>This method sets the list of Multi Server Modules</p>
     * @param selMultiSvrMod List of selected Multi Server Modules
     */
    public void setSelMultiSvrMod(List<String> selMultiSvrMod) {
        this.selMultiSvrMod = selMultiSvrMod;
    }

    /**
     * <p>This method returns the list of Single Server Modules</p>
     * @return List of selected Multi Server Modules
     */
    public List<String> getSelMultiSvrMod() {
        return selMultiSvrMod;
    }

    /**
     * <p>This method returns array of Single Server Modules Labels for the
     * home.jsp</p>
     */
    public SelectItem[] getSingleSvrModLabel() {
        int NoOfSingleServerModules = Integer.parseInt(
                MessageBundleLoader.getBuildMessage("NoOfSingleServerModules"));
        SelectItem[] singleSvrModLabel = new SelectItem
                [NoOfSingleServerModules];
        for (int i = 0; i < NoOfSingleServerModules; i++) {
            singleSvrModLabel[i] = new SelectItem(MessageBundleLoader.
                    getBuildMessage("singleservermodule" + i + ".name"),
                    MessageBundleLoader.getBuildMessage
                    ("singleservermodule" + i + ".name"));
        }
        return singleSvrModLabel;
    }

    /**
     * <p>This method returns the array of Multi Server Modules Labels for the
     * home.jsp</p>
     */
    public SelectItem[] getMultiSvrModLabel() {
        int NoOfMultiServerModules = Integer.parseInt(
                MessageBundleLoader.getBuildMessage("NoOfMultiServerModules"));
        SelectItem[] multiSvrModLabel = new SelectItem[NoOfMultiServerModules];
        for (int i = 0; i < NoOfMultiServerModules; i++) {
            multiSvrModLabel[i] = new SelectItem(MessageBundleLoader.
                    getBuildMessage("multiservermodule" + i + ".name"),
                    MessageBundleLoader.getBuildMessage
                    ("multiservermodule" + i + ".name"));
        }
        return multiSvrModLabel;
    }

    /**
     * <p>This method returns the Sanity Modules Label for the
     * home.jsp</p>
     */
    public SelectItem[] getSanityModLabel() {
        SelectItem[] sanityModLabel = new SelectItem[]{
            new SelectItem(MessageBundleLoader.getBuildMessage(
            "sanity"), MessageBundleLoader.getBuildMessage(
            "sanity"))
        };
        return sanityModLabel;
    }

    /**
     * <p>This method returns the "All" Modules Label for the
     * home.jsp</p>
     */
    public SelectItem[] getAllModlabel() {
        SelectItem[] allModlabel = new SelectItem[]{
            new SelectItem(MessageBundleLoader.getBuildMessage(
            "all"), MessageBundleLoader.getBuildMessage(
            "all"))
        };
        return allModlabel;
    }

    /**
     * <p>This method returns the array of Execution Mode Labels for the
     * home.jsp</p>
     */
    public SelectItem[] getExecModeLabel() {
        int NoOfExecModes = Integer.parseInt(
                MessageBundleLoader.getBuildMessage("NoOfExecModes"));
         SelectItem[] execModeLabel = new SelectItem[NoOfExecModes];
        for (int i = 0; i < NoOfExecModes; i++) {
            execModeLabel[i] = new SelectItem(MessageBundleLoader.
                    getBuildMessage(
                    "executionmode" + i ), MessageBundleLoader.getBuildMessage(
                    "executionmode" + i ));
        }
        return execModeLabel;
    }

    /**
     * <p>This method checks if sanity module checkbox is checked. If checked,
     * then the single server, multi server and "all" module are disabled. If
     * unchecked then the other modules are enabled</p>
     */
    public void sanityModuleValueListener(ValueChangeEvent event) {
        List<String> sanityModule = (List<String>) event.getNewValue();        
        if (event.getComponent().getId().equals("sanityCheckBox")) {            
            if (sanityModule.size() > 0 && sanityModule.get(0).equals
                    ("sanity")) {
                HtmlForm form = (HtmlForm) event.getComponent().getParent().
                        getParent();
                HtmlSelectManyCheckbox allCheckbox = (HtmlSelectManyCheckbox)
                        form.findComponent("allCheckbox");
                HtmlSelectManyCheckbox singleSvrCheckbox = 
                        (HtmlSelectManyCheckbox) form.
                        findComponent("singleSvrCheckBox");
                HtmlSelectManyCheckbox multiSvrCheckbox = 
                        (HtmlSelectManyCheckbox) form.findComponent
                        ("MultiSvrCheckBox");
                allCheckbox.setDisabled(true);
                singleSvrCheckbox.setDisabled(true);
                multiSvrCheckbox.setDisabled(true);
                sanity = true;
            } else if (sanity == true) {                
                HtmlForm form = (HtmlForm) event.getComponent().getParent().
                        getParent();
                HtmlSelectManyCheckbox allCheckbox = (HtmlSelectManyCheckbox)
                        form.findComponent("allCheckbox");
                HtmlSelectManyCheckbox singleSvrCheckbox = 
                        (HtmlSelectManyCheckbox) form.
                        findComponent("singleSvrCheckBox");
                HtmlSelectManyCheckbox multiSvrCheckbox = 
                        (HtmlSelectManyCheckbox) form.
                        findComponent("MultiSvrCheckBox");
                allCheckbox.setDisabled(false);
                singleSvrCheckbox.setDisabled(false);
                multiSvrCheckbox.setDisabled(false);
                sanity = false;
            }
            FacesContext.getCurrentInstance().renderResponse();
        }
    }

    /**
     * <p>This method checks if "All" module checkbox is checked. If checked,
     * then the single server, multi server and sanity module are disabled. If
     * unchecked then the other modules are enabled</p>
     */
    public void allModuleValueListener(ValueChangeEvent event) {
        List<String> allModule = (List<String>) event.getNewValue();        
        if (event.getComponent().getId().equals("allCheckbox")) {
            if (allModule.size() > 0 && allModule.get(0).equals("all")) {                
                HtmlForm form = (HtmlForm) event.getComponent().getParent().
                        getParent();
                HtmlSelectManyCheckbox sanityCheckbox = (HtmlSelectManyCheckbox)
                        form.findComponent("sanityCheckBox");
                HtmlSelectManyCheckbox singleSvrCheckbox = 
                        (HtmlSelectManyCheckbox) form.
                        findComponent("singleSvrCheckBox");
                HtmlSelectManyCheckbox multiSvrCheckbox = 
                        (HtmlSelectManyCheckbox) form.
                        findComponent("MultiSvrCheckBox");
                sanityCheckbox.setDisabled(true);
                singleSvrCheckbox.setDisabled(true);
                multiSvrCheckbox.setDisabled(true);
                all = true;
            } else if (all == true) {                
                HtmlForm form = (HtmlForm) event.getComponent().
                        getParent().getParent();
                HtmlSelectManyCheckbox sanityCheckbox = (HtmlSelectManyCheckbox)
                        form.findComponent("sanityCheckBox");
                HtmlSelectManyCheckbox singleSvrCheckbox =
                        (HtmlSelectManyCheckbox) form.findComponent
                        ("singleSvrCheckBox");
                HtmlSelectManyCheckbox multiSvrCheckbox = 
                        (HtmlSelectManyCheckbox) form.findComponent
                        ("MultiSvrCheckBox");
                sanityCheckbox.setDisabled(false);
                singleSvrCheckbox.setDisabled(false);
                multiSvrCheckbox.setDisabled(false);
                all = false;
            }
            FacesContext.getCurrentInstance().renderResponse();
        }
    }
   

    /**
     * <p>This method validates if QAtest Home directory is correct and is 
     * writable.If not , then the user is displayed the error messages.</p>
     */
    public void qatestHomeValidator(FacesContext context, UIComponent validate,
            Object value) {
        File f = new File(value + "/build.properties");
        if (!f.exists()) {
            FacesMessage msg = new FacesMessage("Directory doesnot exist");
            context.addMessage(validate.getClientId(context), msg);           
            FacesContext.getCurrentInstance().renderResponse();
        } else if (!f.canWrite()) {
            FacesMessage msg = new FacesMessage
                    ("Directory doesnot have write permissions");
            context.addMessage(validate.getClientId(context), msg);            
            FacesContext.getCurrentInstance().renderResponse();
        }
    }

    /**
     * <p>This method validates if Report directory exists and is
     * writable.If not , then the user is displayed the error messages.</p>
     */
    public void reportDirValidator(FacesContext context, UIComponent validate,
            Object value) {
        File f1 = new File((String) value);
        if (!f1.exists()) {            
            FacesMessage msg = new FacesMessage("Directory doesnot exist");
            context.addMessage(validate.getClientId(context), msg);
            FacesContext.getCurrentInstance().renderResponse();
        } else if (!f1.canWrite()) {            
            FacesMessage msg = new FacesMessage
                    ("Directory doesnot have write permissions");
            context.addMessage(validate.getClientId(context), msg);
            FacesContext.getCurrentInstance().renderResponse();
        }
    }

    /**
     * <p>This method is called when the action button for home.jsp is clicked.
     * </p>
     */
    public String buildButtonAction() {
        /*
         * clears the list of selected modules,Map of modules with properties
         * and number of modules with properties . This is required so that
         * there are no duplicate information from the previous runs.
         */
        selectedModules.clear();
        modWithPropMap.clear();
        noOfModWithProp = 0;
        /*
         * If not the first run, then get the number of server tabs created for
         * previous run. If first run , then the no of server tabs is zero
         */
        ServerBean serverbean = (ServerBean) getBean("ServerBean");       
        int oldNoOfServers = serverbean.getNoOfSvrTabs();
        /*
         * If Sanity is selected . Add sanity to the list of selected modules.
         * Correct the number of server tabs if not the first run.Sanity
         * requires only one server tab to be displayed to the user
         */
        if (!getSelSanityMod().isEmpty()) {
            selectedModules.addAll(getSelSanityMod());
            serverbean.setNoOfSvrTabs(1);
             if (oldNoOfServers > 1) {
                if (oldNoOfServers == 2) {
                    serverbean.removeTab(2, 1);
                } else if (oldNoOfServers == 3) {
                    serverbean.removeTab(3, 2);
                } else if (oldNoOfServers == 4) {
                    serverbean.removeTab(4, 3);
                }
            } else {
                serverbean.addTab(0, 1);
            }
        /*
         * If All is selected . Add All to the list of selected modules.
         * Correct the number of server tabs if not the first run.
         */
        } else if (!getSelAllMod().isEmpty()) {
            selectedModules.addAll(getSelAllMod());
            /*
             * Iterate and get all multi server modules from resource bundle and
             * check which servers have showproperties true. This determines
             * which modules require extra properties to be displayed to the
             * user
             */
            int NoOfMultiServerModules = Integer.parseInt(MessageBundleLoader.
                    getBuildMessage("NoOfMultiServerModules"));
            for (int i = 0; i < NoOfMultiServerModules; i++) {
                if (MessageBundleLoader.getBuildMessage("multiservermodule" + i
                        + ".showproperties").equals("true")) {
                    modWithPropMap.put(noOfModWithProp, (Object) 
                            MessageBundleLoader.getBuildMessage
                            ("multiservermodule" + i + ".name"));
                    noOfModWithProp++;
                }
            }
            /*
             * Iterate and get all single server modules from resource bundle
             * and check which servers have showproperties true. This determines
             * which modules require extra properties to be displayed to the
             * user
             */
            int NoOfSingleServerModules = Integer.parseInt(MessageBundleLoader.
                    getBuildMessage("NoOfSingleServerModules"));
            for (int i = 0; i < NoOfSingleServerModules; i++) {
                if (MessageBundleLoader.getBuildMessage("singleservermodule" +
                        i + ".showproperties").equals("true")) {
                    modWithPropMap.put(noOfModWithProp, (Object)
                            MessageBundleLoader.getBuildMessage
                            ("singleservermodule" + i + ".name"));
                    noOfModWithProp++;
                }
            }
            /*
             * Since 4 is the highest number of server tabs. Therefore, "All"
             * module would require 4 server tabs to be displayed to the user
             */
            serverbean.setNoOfSvrTabs(4);
                if (oldNoOfServers == 1) {
                    serverbean.addTab(1, 4);
                } else if (oldNoOfServers == 2) {
                    serverbean.addTab(2, 4);
                } else if (oldNoOfServers == 3) {
                    serverbean.addTab(3, 4);
                } else if (oldNoOfServers == 0) {
                    serverbean.addTab(0, 4);
                }            
        }
        /*
         * If Multi Server checkboxes are not emplt. Add selected multiserver
         * modules to the list of selected modules.
         * Correct the number of server tabs if not the first run depending on
         * modules selected
         */
        if (!getSelMultiSvrMod().isEmpty()) {
            selectedModules.addAll(getSelMultiSvrMod());
            int NoOfMultiServerModules = Integer.parseInt
                    (MessageBundleLoader.getBuildMessage
                    ("NoOfMultiServerModules"));
            for (int j = 0; j < selMultiSvrMod.size(); j++) {
                for (int i = 0; i < NoOfMultiServerModules; i++) {
                    if ((MessageBundleLoader.getBuildMessage
                            ("multiservermodule" + i + ".name")).
                            equals(selMultiSvrMod.get(j)) &&
                            (MessageBundleLoader.getBuildMessage(
                            "multiservermodule" + i + ".showproperties").
                            equals("true"))) {
                        modWithPropMap.put(noOfModWithProp, (Object)
                                selMultiSvrMod.get(j));
                        noOfModWithProp++;
                    }
                }
            }
            if (selectedModules.contains("multiprotocol")) {
                serverbean.setNoOfSvrTabs(4);
                    if (oldNoOfServers == 1) {
                        serverbean.addTab(1, 4);
                    } else if (oldNoOfServers == 2) {
                        serverbean.addTab(2, 4);
                    } else if (oldNoOfServers == 3) {
                        serverbean.addTab(3, 4);
                    } else if (oldNoOfServers == 0) {
                        serverbean.addTab(0, 4);
                    }                
            } else if (selectedModules.contains("samlv2idpproxy")) {
                serverbean.setNoOfSvrTabs(3);
                    if (oldNoOfServers == 1) {
                        serverbean.addTab(1, 3);
                    } else if (oldNoOfServers == 2) {
                        serverbean.addTab(2, 3);
                    } else if (oldNoOfServers == 4) {
                        serverbean.removeTab(4, 1);
                    } else if (oldNoOfServers == 0) {
                        serverbean.addTab(0, 3);
                    }
                }else{                
                serverbean.setNoOfSvrTabs(2);
                     if (oldNoOfServers == 1) {
                        serverbean.addTab(1, 2); }
                         if (oldNoOfServers == 0) {
                        serverbean.addTab(0, 2);}
                         if (oldNoOfServers == 3) {
                        serverbean.removeTab(3, 1);}
                         if (oldNoOfServers == 4) {
                        serverbean.removeTab(4, 2);}
                }
                /*
                 * If Single server modules are also selected by the user.Add
                 * appropriate modules to the map of modules which require extra
                 * properties to be displayed to the user
                 */
                if (!getSelSingleSvrMod().isEmpty()) {
                    selectedModules.addAll(getSelSingleSvrMod());
                    int NoOfSingleServerModules = Integer.parseInt
                            (MessageBundleLoader.getBuildMessage
                            ("NoOfSingleServerModules"));
                    for (int j = 0; j < selSingleSvrMod.size(); j++) {
                        for (int i = 0; i < NoOfSingleServerModules; i++) {
                            if ((MessageBundleLoader.getBuildMessage
                                    ("singleservermodule" + i + ".name")).
                                    equals(selSingleSvrMod.get(j)) &&
                                    (MessageBundleLoader.getBuildMessage
                                    ("singleservermodule" + i +
                                    ".showproperties").equals("true"))) {
                                modWithPropMap.put(noOfModWithProp, (Object)
                                        selSingleSvrMod.get(j));
                                noOfModWithProp++;
                            }
                        }
                    }
                }
            }
             /*
              * If ONLY Single server modules are also selected by the user and
              * no multi server modules are selected. Add appropriate modules
              * to the Map of modules with extra properties. Adjust the number
              * of server tabs to 1
              */
            if (getSelMultiSvrMod().isEmpty() && !getSelSingleSvrMod().
            isEmpty()) {
                selectedModules.addAll(getSelSingleSvrMod());
                int NoOfSingleServerModules = Integer.parseInt
                        (MessageBundleLoader.getBuildMessage
                        ("NoOfSingleServerModules"));
                for (int j = 0; j < selSingleSvrMod.size(); j++) {
                    for (int i = 0; i < NoOfSingleServerModules; i++) {
                        if ((MessageBundleLoader.getBuildMessage
                                ("singleservermodule" + i + ".name")).
                                equals(selSingleSvrMod.get(j)) &&
                                (MessageBundleLoader.getBuildMessage
                                ("singleservermodule" + i + ".showproperties").
                                equals("true"))) {
                            modWithPropMap.put(noOfModWithProp, (Object)
                                    selSingleSvrMod.get(j));
                            noOfModWithProp++;
                        }
                    }
                }
                serverbean.setNoOfSvrTabs(1);
                    if (oldNoOfServers == 2) {
                        serverbean.removeTab(2, 1);
                    } else if (oldNoOfServers == 3) {
                        serverbean.removeTab(3, 2);
                    } else if (oldNoOfServers == 4) {
                        serverbean.removeTab(4, 3);
                    } else if(oldNoOfServers == 0) {
                    serverbean.addTab(0, 1);
                }
            }            
            return "success";
        }
    }

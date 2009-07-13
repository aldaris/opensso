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

import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.sun.identity.qatest.qaweb.common.MessageBundleLoader;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import java.util.HashMap;
import java.util.Map;
import javax.faces.FacesException;

/**
 * <p>This class is a session scope data bean which mainly deals with summary
 * page of the web app.</p>
 * <p>An instance of this class will be created  automatically,
 * the first time , application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class SummaryBean extends AbstractSessionBean {

    HtmlPanelGrid summaryGrid = new HtmlPanelGrid();
    boolean showAntResults;
    
    /**
     * <p>Automatically managed component initialization.<strong>WARNING:
     * </strong>.This method is automatically generated, so any user-specified
     * code inserted here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }   

    /**
     * <p>Construct a new session data bean instance.</p>
     */
    public SummaryBean() {
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
            log("SummaryBean Initialization Failure", e);
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
     * <p>This method returns if ShowANtResults is true or false</p>
     * @return showAntResults
     */
    public boolean isShowAntResults() {
        return showAntResults;
    }

    /**
     * <p>This method sets ShowANtResults to true or false</p>
     * @param showAntResults
     */
    public void setShowAntResults(boolean showAntResults) {
        this.showAntResults = showAntResults;
    }

     /**
     * <p>This method is used to navigate back to the last page</p>
     */
    public String goBackAction() {
        BuildInfoBean bpbean = (BuildInfoBean) getBean("BuildInfoBean");
        if (bpbean.getModWithPropMap().size() > 0) {
            return "module";
        } else {
            return "server";
        }
    }

     /**
     * <p>This method returns HmlPanelGrid to be displayed in summary.jsp</p>
      * @return summaryGrid HtmlPanelGrid
     */
    public HtmlPanelGrid getSummaryGrid() {
        BuildInfoBean bpbean = (BuildInfoBean) getBean("BuildInfoBean");
        ServerBean sbean = (ServerBean) getBean("ServerBean");
        Map moduleTimeMap = new HashMap();
        int NoOfSingleServerModules = Integer.parseInt
                (MessageBundleLoader.getBuildMessage
                ("NoOfSingleServerModules"));
        for (int i = 0; i < bpbean.getSelectedModules().size(); i++) {
            for (int j = 0; j < NoOfSingleServerModules; j++) {
                if ((MessageBundleLoader.getBuildMessage
                        ("singleservermodule" + j + ".name")).equals
                        (bpbean.getSelectedModules().get(i))) {
                    moduleTimeMap.put(MessageBundleLoader.getBuildMessage
                            ("singleservermodule" + j + ".name"),
                            MessageBundleLoader.getBuildMessage
                            ("singleservermodule" + j + ".execTime"));
                }
            }
        }
        int NoOfMultiServerModules = Integer.parseInt(MessageBundleLoader.
                getBuildMessage("NoOfMultiServerModules"));
        for (int i = 0; i < bpbean.getSelectedModules().size(); i++) {
            for (int j = 0; j < NoOfMultiServerModules; j++) {
                if ((MessageBundleLoader.getBuildMessage
                        ("multiservermodule" + j + ".name")).equals
                        (bpbean.getSelectedModules().get(i))) {
                    moduleTimeMap.put(MessageBundleLoader.
                            getBuildMessage("multiservermodule" + j + ".name"),
                            MessageBundleLoader.getBuildMessage
                            ("multiservermodule" + j + ".execTime"));
                }
            }
        }       
        summaryGrid.setBgcolor("#FFFFCC");        
        summaryGrid.setColumns(2);
        summaryGrid.setCellpadding("5");
        summaryGrid.setCellspacing("5");
        HtmlOutputText qatestHomeLbl = new HtmlOutputText();
        qatestHomeLbl.setValue("Qatest Home");
        HtmlOutputText reportDirLbl = new HtmlOutputText();
        reportDirLbl.setValue("Report Directory");
        HtmlOutputText execModeLbl = new HtmlOutputText();
        execModeLbl.setValue("Execution Mode");
        HtmlOutputText testModulesLbl = new HtmlOutputText();
        testModulesLbl.setValue("Test Modules");
        HtmlOutputText serverNamesLbl = new HtmlOutputText();
        serverNamesLbl.setValue("Servers");
        HtmlOutputText qatestHome = new HtmlOutputText();
        qatestHome.setValue(bpbean.getQatestHome());
        HtmlOutputText reportDir = new HtmlOutputText();
        reportDir.setValue(bpbean.getReportDir());
        HtmlOutputText execMode = new HtmlOutputText();
        execMode.setValue(bpbean.getSelExeMode());
        HtmlOutputText testModules = new HtmlOutputText();
        testModules.setValue(bpbean.getSelectedModules());
        HtmlOutputText serverNames = new HtmlOutputText();
        serverNames.setValue(sbean.getServerNames());       
        summaryGrid.getChildren().add(qatestHomeLbl);
        summaryGrid.getChildren().add(qatestHome);
        summaryGrid.getChildren().add(reportDirLbl);
        summaryGrid.getChildren().add(reportDir);
        summaryGrid.getChildren().add(execModeLbl);
        summaryGrid.getChildren().add(execMode);
        summaryGrid.getChildren().add(testModulesLbl);
        summaryGrid.getChildren().add(testModules);
        summaryGrid.getChildren().add(serverNamesLbl);
        summaryGrid.getChildren().add(serverNames);
        for (int i = 0; i < bpbean.getSelectedModules().size(); i++) {
            HtmlOutputText moduleName = new HtmlOutputText();
            moduleName.setValue(bpbean.getSelectedModules().get(i));
            HtmlOutputText timeTaken = new HtmlOutputText();
            timeTaken.setValue(moduleTimeMap.get(bpbean.getSelectedModules().
                    get(i)));
            summaryGrid.getChildren().add(moduleName);
            summaryGrid.getChildren().add(timeTaken);
        }        
        HtmlOutputText showResultsLbl = new HtmlOutputText();
        showResultsLbl.setValue("Show Ant Results");
        HtmlSelectBooleanCheckbox showResults = new HtmlSelectBooleanCheckbox();
        showResults.setId("checkBoxAnt");
        showResults.setLabel("Show Ant Results");
        showResults.setTitle("Show Ant Results");
        showResults.setValue(showAntResults);
        summaryGrid.getChildren().add(showResultsLbl);
        summaryGrid.getChildren().add(showResults);        
        return summaryGrid;
    }

    /**
     * <p>This method sets the HmlPanelGrid to be displayed in summary.jsp</p>
      * @param summaryGrid HtmlPanelGrid
     */
    public void setSummaryGrid(HtmlPanelGrid summaryGrid) {
        this.summaryGrid = summaryGrid;
    }
}

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

import com.icesoft.faces.async.render.IntervalRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.sun.identity.qatest.qaweb.common.MessageBundleLoader;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>This class is a session scope data bean which mainly deals with results
 * page of the web app.</p>
 * <p>An instance of this class will be created  automatically,
 * the first time , application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class TriggerAntBean extends AbstractSessionBean implements Renderable {

    String bufferedData = "";
    Process antProcess;
    ProcessBuilder antProcessBuilder;
    List<String> antResults = new ArrayList();
    BufferedReader stdInput;
    private static String fileSeperator = System.getProperty("file.separator");
    /**
     * Time interval, in milliseconds, between renders.
     */
    private final int renderInterval = 1;
    /**
     * The state associated with the current user that can be used for
     * server-initiated render calls.
     */
    private PersistentFacesState state;
    /**
     * A named render group that can be shared by all TimeZoneBeans for
     * server-initiated render calls.  Setting the interval determines the
     * frequency of the render call.
     */
    private IntervalRenderer clock;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:
     * </strong>.This method is automatically generated, so any user-specified
     * code inserted here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }

    /**
     *  Constructor
     * */
    public TriggerAntBean() {
        state = PersistentFacesState.getInstance();
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
            log("TriggerAntBean Initialization Failure", e);
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
     * Used to create, setup, and start an IntervalRenderer from the passed
     * renderManager This is used in conjunction with faces-config.xml to allow
     * the same single render manager to be set in all TimeZoneBeans
     *
     * @param renderManager RenderManager to get the IntervalRenderer from
     */
    public void setRenderManager(RenderManager renderManager) {
        clock = renderManager.getIntervalRenderer("clock");
        clock.setInterval(renderInterval);
        clock.add(this);
        if (bufferedData == null) {
            clock.requestStop();
        }
        clock.requestRender();
    }

    /**
     * Gets RenderManager
     *
     * @return RenderManager null
     */
    public RenderManager getRenderManager() {
        return null;
    }

    /**
     * Gets the current instance of PersistentFacesState
     *
     * @return PersistentFacesState state
     */
    public PersistentFacesState getState() {
        return state;
    }

    /**
     * Callback to inform us that there was an Exception while rendering.
     * Continue from a transientRenderingException but not
     * from a FatalRenderingException
     *
     * @param renderingException render exception passed in frome framework.
     */
    public void renderingException(RenderingException renderingException) {
        renderingException.printStackTrace();
    }

    /**
     * <p>This method is called on Submit Button action of summary.jsp.
     * It triggers ant to run the qatest framework </p>
     */
    public String triggerAntAction() {
        BuildInfoBean bpbean = (BuildInfoBean) getBean("BuildInfoBean");
        ServerBean sbean = (ServerBean) getBean("ServerBean");
        ModuleBean mbean = (ModuleBean) getBean("ModuleBean");
        /* Write the build.properties File */
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(bpbean.getQatestHome() +
                    fileSeperator + "build.properties");
            NodeList buildPropNode = doc.getElementsByTagName("property");
            Node node1 = buildPropNode.item(0);
            NamedNodeMap attrs1 = node1.getAttributes();
            attrs1.item(0).setNodeValue("EXECUTION_MODE");
            attrs1.item(1).setNodeValue(bpbean.getSelExeMode());
            Node node2 = buildPropNode.item(1);
            NamedNodeMap attrs2 = node2.getAttributes();
            attrs2.item(0).setNodeValue("TEST_MODULE");
            if (!bpbean.getSelSanityMod().isEmpty()) {
                attrs2.item(1).setNodeValue("sanity");
            } else if (!bpbean.getSelAllMod().isEmpty()) {
                attrs2.item(1).setNodeValue("all");
            } else {
                Object[] tempselectedModules = bpbean.getSelectedModules().
                        toArray();
                StringBuffer selectedModules = new StringBuffer();
                for (int m = 0; m <
                        tempselectedModules.length; m++) {
                    if (m == tempselectedModules.length - 1) {
                        if (tempselectedModules[m].equals("agents")) {
                            tempselectedModules[m] = mbean.getAgentsName();
                        }
                        selectedModules.append((String) tempselectedModules[m]);
                    } else {
                        if (tempselectedModules[m].equals("agents")) {
                            tempselectedModules[m] = mbean.getAgentsName();
                        }
                        selectedModules.append((String) tempselectedModules[m]);
                        selectedModules.append(",");
                    }
                }
                attrs2.item(1).setNodeValue(selectedModules.toString());
            }
            XMLSerializer serializer = new XMLSerializer();
            serializer.setOutputCharStream(
                    new java.io.FileWriter(bpbean.getQatestHome() +
                    fileSeperator + "build.properties"));
            serializer.serialize(doc);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        HtmlForm hform = (HtmlForm) FacesContext.getCurrentInstance().
                getViewRoot().findComponent("summaryForm");
        HtmlSelectBooleanCheckbox checkbox = (HtmlSelectBooleanCheckbox) hform.
                findComponent("summaryGrid").findComponent("checkBoxAnt");
        try {
            if (sbean.serverNames.size() > 1) {
                antProcessBuilder = new ProcessBuilder(MessageBundleLoader.
                        getQawebMessage("ant_home"), "-lib",
                        "./lib/ant-contrib-1.0b3.jar",
                        "-DSERVER_NAME1=" + sbean.serverNames.get(0),
                        "-DSERVER_NAME2=" + sbean.serverNames.get(1), "run");
            } else {
                antProcessBuilder = new ProcessBuilder(MessageBundleLoader.
                        getQawebMessage("ant_home"), "-lib",
                        "./lib/ant-contrib-1.0b3.jar", "-DSERVER_NAME1=" +
                        sbean.serverNames.get(0), "run");
            }
            antProcessBuilder.directory(new File(bpbean.getQatestHome()));
            antProcessBuilder.environment().put("JAVA_HOME",
                    MessageBundleLoader.getQawebMessage("java_home"));
            antProcess = antProcessBuilder.start();
            stdInput = new BufferedReader(
                    new InputStreamReader(antProcess.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkbox.getValue().equals(true)) {
            return "success";
        } else {
            return "results";
        }

    }

     /**
     * <p>This method adds data from the buffered stream into a List
     * @return antResults
     */
    public List<String> getAntResults() {
        try {
            if (bufferedData == null) {
                clock.requestStop();
            }
            if ((bufferedData = stdInput.readLine()) != null) {
                antResults.add(bufferedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return antResults;
    }
}

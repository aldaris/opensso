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
 * $Id: IDFFCommon.java,v 1.2 2007-06-04 22:23:16 mrudulahg Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestConstants;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

/**
 * This class contains helper methods for IDFF tests
 */
public class IDFFCommon extends TestCommon {
    
    /** Creates a new instance of IDFFCommon */
    public IDFFCommon() {
        super("IDFFCommon");
    }
    
    /** Creates a new instance of IDFFCommon */
    public IDFFCommon(String componentName) {
        super(componentName);
    }
    
   /**
     * This method creates xml sp init federation
     * It assumes that sp session is present. 
     * The flow is as follows
     * 1. Go to Federate.jsp on sp side. Click on select provider button. 
     * 2. It redirects to idp login. Enter idp user id & password.
     * 3. After successful idp login, success federation msg is displayed.
     * @param xmlFileName is the file to be created.
     * @param Map m contains all the data for xml generation
     */
    public static void getxmlSPIDFFFederate(String xmlFileName, Map m)
    throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String spProto = (String)m.get(TestConstants.KEY_SP_PROTOCOL);
        String spPort = (String)m.get(TestConstants.KEY_SP_PORT);
        String spHost = (String)m.get(TestConstants.KEY_SP_HOST);
        String spDeploymentURI = (String)m.get(
                TestConstants.KEY_SP_DEPLOYMENT_URI);
        String spMetaalias = (String)m.get(TestConstants.KEY_SP_METAALIAS);
        String idpEntityName = (String)m.get(TestConstants.KEY_IDP_ENTITY_NAME);
        String spUser = (String)m.get(TestConstants.KEY_SP_USER);
        String spUserpw = (String)m.get(TestConstants.KEY_SP_USER_PASSWORD);
        String idpUser = (String)m.get(TestConstants.KEY_IDP_USER);
        String idpUserpw = (String)m.get(TestConstants.KEY_IDP_USER_PASSWORD);
        String strResult = (String)m.get(TestConstants.KEY_SSO_INIT_RESULT);
        
        out.write("<url href=\"" + spProto +"://" + spHost + ":"
                + spPort + spDeploymentURI
                + "/config/federation/default/Federate.jsp?metaAlias=/"
                + spMetaalias + "&amp;idpEntityID=" + idpEntityName );
        out.write("\">");
        out.write(System.getProperty("line.separator"));
        out.write("<form name=\"form1\" buttonName=\"button\" />");
        out.write(System.getProperty("line.separator"));
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write(System.getProperty("line.separator"));
        out.write("<input name=\"IDToken1\" value=\"" + idpUser + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("<input name=\"IDToken2\" value=\""
                + idpUserpw + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("<result text=\"" + strResult + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("</form>");
        out.write(System.getProperty("line.separator"));
        out.write("</url>");
        out.write(System.getProperty("line.separator"));
        out.close();
    }
    
    /**
     * This method creates xml sp init sso
     * First login to idp & then run this xml file. 
     * Since user is already federated, SSO will be successful. 
     * @param xmlFileName is the file to be created.
     * @param Map m contains all the data for xml generation
     */
    public static void getxmlSPIDFFSSO(String xmlFileName, Map m)
    throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String spProto = (String)m.get(TestConstants.KEY_SP_PROTOCOL);
        String spPort = (String)m.get(TestConstants.KEY_SP_PORT);
        String spHost = (String)m.get(TestConstants.KEY_SP_HOST);
        String spDeploymentURI = (String)m.get(
                TestConstants.KEY_SP_DEPLOYMENT_URI);
        String spMetaalias = (String)m.get(TestConstants.KEY_SP_METAALIAS);
        String idpEntityName = (String)m.get(TestConstants.KEY_IDP_ENTITY_NAME);
        String spUser = (String)m.get(TestConstants.KEY_SP_USER);
        String spUserpw = (String)m.get(TestConstants.KEY_SP_USER_PASSWORD);
        String idpUser = (String)m.get(TestConstants.KEY_IDP_USER);
        String idpUserpw = (String)m.get(TestConstants.KEY_IDP_USER_PASSWORD);
        String strResult = (String)m.get(TestConstants.KEY_SSO_RESULT);
        
        out.write("<url href=\"" + spProto +"://" + spHost + ":"
                + spPort + spDeploymentURI
                + "/config/federation/default/Federate.jsp?metaAlias=/"
                + spMetaalias + "&amp;idpEntityID=" + idpEntityName );
        out.write("\">");
        out.write(System.getProperty("line.separator"));
        out.write("<form>");
        out.write(System.getProperty("line.separator"));
        out.write("<result text=\"" + strResult + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("</form>");
        out.write(System.getProperty("line.separator"));
        out.write("</url>");
        out.write(System.getProperty("line.separator"));
        out.close();
    }
    
    /**
     * This method creates xml sp init Logout
     * @param xmlFileName is the file to be created.
     * @param Map m contains all the data for xml generation
     */
    public static void getxmlSPIDFFLogout(String xmlFileName, Map m)
    throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String spProto = (String)m.get(TestConstants.KEY_SP_PROTOCOL);
        String spPort = (String)m.get(TestConstants.KEY_SP_PORT);
        String spHost = (String)m.get(TestConstants.KEY_SP_HOST);
        String spDeploymentURI = (String)m.get(
                TestConstants.KEY_SP_DEPLOYMENT_URI);
        String spMetaalias = (String)m.get(TestConstants.KEY_SP_METAALIAS);
        String idpEntityName = (String)m.get(TestConstants.KEY_IDP_ENTITY_NAME);
        String spUser = (String)m.get(TestConstants.KEY_SP_USER);
        String spUserpw = (String)m.get(TestConstants.KEY_SP_USER_PASSWORD);
        String idpUser = (String)m.get(TestConstants.KEY_IDP_USER);
        String idpUserpw = (String)m.get(TestConstants.KEY_IDP_USER_PASSWORD);
        String strResult = (String)m.get(TestConstants.KEY_SP_SLO_RESULT);
        
        out.write("<url href=\"" + spProto +"://" + spHost + ":"
                + spPort + spDeploymentURI
                + "/liberty-logout?metaAlias=/" + spMetaalias);
        out.write("\">");
        out.write(System.getProperty("line.separator"));
        out.write("<form>");
        out.write("<result text=\"" + strResult + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("</form>");
        out.write(System.getProperty("line.separator"));
        out.write("</url>");
        out.write(System.getProperty("line.separator"));
        out.close();
    }
    
    /**
     * This method creates xml sp init federation termination
     * @param xmlFileName is the file to be created.
     * @param Map m contains all the data for xml generation
     */
    public static void getxmlSPIDFFTerminate(String xmlFileName, Map m)
    throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String spProto = (String)m.get(TestConstants.KEY_SP_PROTOCOL);
        String spPort = (String)m.get(TestConstants.KEY_SP_PORT);
        String spHost = (String)m.get(TestConstants.KEY_SP_HOST);
        String spDeploymentURI = (String)m.get(
                TestConstants.KEY_SP_DEPLOYMENT_URI);
        String spMetaalias = (String)m.get(TestConstants.KEY_SP_METAALIAS);
        String idpEntityName = (String)m.get(TestConstants.KEY_IDP_ENTITY_NAME);
        String spUser = (String)m.get(TestConstants.KEY_SP_USER);
        String spUserpw = (String)m.get(TestConstants.KEY_SP_USER_PASSWORD);
        String idpUser = (String)m.get(TestConstants.KEY_IDP_USER);
        String idpUserpw = (String)m.get(TestConstants.KEY_IDP_USER_PASSWORD);
        String strResult = (String)m.get(TestConstants.KEY_TERMINATE_RESULT);
        
        out.write("<url href=\"" + spProto +"://" + spHost + ":"
                + spPort + spDeploymentURI
                + "/config/federation/default/Termination.jsp?metaAlias=/"
                + spMetaalias );
        out.write("\">");
        out.write(System.getProperty("line.separator"));
        out.write("<form name=\"selectprovider\" buttonName=\"doIt\">");
        out.write("<result text=\"" + strResult + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("</form>");
        out.write(System.getProperty("line.separator"));
        out.write("</url>");
        out.write(System.getProperty("line.separator"));
        out.close();
    }
    
    /**
     * This method creates xml sp init Logout
     * @param xmlFileName is the file to be created.
     * @param Map m contains all the data for xml generation
     */
    public static void getxmlIDPIDFFLogout(String xmlFileName, Map m)
    throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idpProto = (String)m.get(TestConstants.KEY_IDP_PROTOCOL);
        String idpPort = (String)m.get(TestConstants.KEY_IDP_PORT);
        String idpHost = (String)m.get(TestConstants.KEY_IDP_HOST);
        String idpDeploymentURI = (String)m.get(
                TestConstants.KEY_IDP_DEPLOYMENT_URI);
        String idpMetaalias = (String)m.get(TestConstants.KEY_IDP_METAALIAS);
        String strResult = (String)m.get(TestConstants.KEY_IDP_SLO_RESULT);
        
        out.write("<url href=\"" + idpProto +"://" + idpHost + ":"
                + idpPort + idpDeploymentURI
                + "/liberty-logout?metaAlias=/" + idpMetaalias);
        out.write("\">");
        out.write(System.getProperty("line.separator"));
        out.write("<form>");
        out.write("<result text=\"" + strResult + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("</form>");
        out.write(System.getProperty("line.separator"));
        out.write("</url>");
        out.write(System.getProperty("line.separator"));
        out.close();
    }
    
    /**
     * This method creates xml idp init federation termination
     * @param xmlFileName is the file to be created.
     * @param Map m contains all the data for xml generation
     */
    public static void getxmlIDPIDFFTerminate(String xmlFileName, Map m)
    throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idpProto = (String)m.get(TestConstants.KEY_IDP_PROTOCOL);
        String idpPort = (String)m.get(TestConstants.KEY_IDP_PORT);
        String idpHost = (String)m.get(TestConstants.KEY_IDP_HOST);
        String idpDeploymentURI = (String)m.get(
                TestConstants.KEY_IDP_DEPLOYMENT_URI);
        String idpMetaalias = (String)m.get(TestConstants.KEY_IDP_METAALIAS);
        String strResult = (String)m.get(TestConstants.KEY_TERMINATE_RESULT);
        
        out.write("<url href=\"" + idpProto +"://" + idpHost + ":"
                + idpPort + idpDeploymentURI
                + "/config/federation/default/Termination.jsp?metaAlias=/"
                + idpMetaalias );
        out.write("\">");
        out.write(System.getProperty("line.separator"));
        out.write("<form name=\"selectprovider\" buttonName=\"doIt\">");
        out.write("<result text=\"" + strResult + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("</form>");
        out.write(System.getProperty("line.separator"));
        out.write("</url>");
        out.write(System.getProperty("line.separator"));
        out.close();
    }
    
    /**
     * This method creates xml sp init Name Registration
     * @param xmlFileName is the file to be created.
     * @param Map m contains all the data for xml generation
     */
    public static void getxmlSPIDFFNameReg(String xmlFileName, Map m)
    throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String spProto = (String)m.get(TestConstants.KEY_SP_PROTOCOL);
        String spPort = (String)m.get(TestConstants.KEY_SP_PORT);
        String spHost = (String)m.get(TestConstants.KEY_SP_HOST);
        String spDeploymentURI = (String)m.get(
                TestConstants.KEY_SP_DEPLOYMENT_URI);
        String spMetaalias = (String)m.get(TestConstants.KEY_SP_METAALIAS);
        String strResult = (String)m.get(TestConstants.KEY_NAME_REG_RESULT);
        
        out.write("<url href=\"" + spProto +"://" + spHost + ":"
                + spPort + spDeploymentURI
                + "/config/federation/default/NameRegistration.jsp?metaAlias=/"
                + spMetaalias );
        out.write("\">");
        out.write(System.getProperty("line.separator"));
        out.write("<form name=\"selectprovider\" buttonName=\"doIt\">");
        out.write("<result text=\"" + strResult + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("</form>");
        out.write(System.getProperty("line.separator"));
        out.write("</url>");
        out.write(System.getProperty("line.separator"));
        out.close();
    }

    /**
     * This method creates xml sp init Name Registration
     * @param xmlFileName is the file to be created.
     * @param Map m contains all the data for xml generation
     */
    public static void getxmlIDPIDFFNameReg(String xmlFileName, Map m)
    throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idpProto = (String)m.get(TestConstants.KEY_IDP_PROTOCOL);
        String idpPort = (String)m.get(TestConstants.KEY_IDP_PORT);
        String idpHost = (String)m.get(TestConstants.KEY_IDP_HOST);
        String idpDeploymentURI = (String)m.get(
                TestConstants.KEY_IDP_DEPLOYMENT_URI);
        String idpMetaalias = (String)m.get(TestConstants.KEY_IDP_METAALIAS);
        String strResult = (String)m.get(TestConstants.KEY_NAME_REG_RESULT);
        
        out.write("<url href=\"" + idpProto +"://" + idpHost + ":"
                + idpPort + idpDeploymentURI
                + "/config/federation/default/NameRegistration.jsp?metaAlias=/"
                + idpMetaalias );
        out.write("\">");
        out.write(System.getProperty("line.separator"));
        out.write("<form name=\"selectprovider\" buttonName=\"doIt\">");
        out.write("<result text=\"" + strResult + "\" />");
        out.write(System.getProperty("line.separator"));
        out.write("</form>");
        out.write(System.getProperty("line.separator"));
        out.write("</url>");
        out.write(System.getProperty("line.separator"));
        out.close();
    }
}

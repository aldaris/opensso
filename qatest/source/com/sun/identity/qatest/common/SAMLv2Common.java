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
 * $Id: SAMLv2Common.java,v 1.3 2007-05-08 16:53:08 mrudulahg Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class contains helper methods for samlv2 tests
 */
public class SAMLv2Common extends TestCommon {

    public static String newline = System.getProperty("line.separator");
    public static String fileseparator = System.getProperty("file.separator");

    /** Creates a new instance of SAMLv2Common */
    public SAMLv2Common() {
        super("SAMLv2Common");
    }
    
   /**
    * This method creates spssoinit xml 
    * It handles two redirects. The flow is as follows
    * 1. Go to spSSOInit.jsp on sp side.
    * 2. It redirects to idp login. Enter idp user id & password. 
    * 3. After successful idp login, it is redirected to sp login page. 
    * Enter sp user id & password. 
    * 4. After successful sp login, "Single sign-on succeeded" msg is displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    * @param bindingType can be artifact or post
    * @param idpLoginOnly can be used in autofedertion case, where only idplogin 
    * is req
    */
    public static void getxmlSPInitSSO(String xmlFileName, Map m, 
            String bindingType, boolean idpLoginOnly)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String sp_alias = (String)m.get("sp_alias");
        String idp_entity_name = (String)m.get("idp_entity_name");
        String sp_user = (String)m.get("sp_user");
        String sp_userpw = (String)m.get("sp_userpw");
        String idp_user = (String)m.get("idp_user");
        String idp_userpw = (String)m.get("idp_userpw");
        String strResult = (String)m.get("ssoinitresult");

        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri 
                + "/saml2/jsp/spSSOInit.jsp?metaAlias=/" + sp_alias 
                + "&amp;idpEntityID=" + idp_entity_name );
        if (bindingType == "post"){
            out.write("&amp;binding=HTTP-POST");
        }
        if (m.get("urlparams")!= null){
            out.write("&amp;" + m.get("urlparams"));
        }
        out.write("\">");
        out.write(newline);
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + idp_user + "\" />");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\""
                + idp_userpw + "\" />");
        out.write(newline);
        if(!idpLoginOnly){
            out.write("</form>");
            out.write(newline);
            out.write("<form name=\"Login\" buttonName=\"\" >");
            out.write(newline);
            out.write("<input name=\"IDToken1\" value=\"" + sp_user + "\" />");
            out.write(newline);
            out.write("<input name=\"IDToken2\" value=\"" 
                    + sp_userpw + "\" />");
            out.write(newline);
        }
        out.write("<result text=\"" + strResult + "\" />");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

   /**
    * This method creates spssoinit xml 
    * It assumes that user has logged in to the idp. 
    * 1. Go to idpSSOInit.jsp on idp side.
    * 2. It redirects to sp login. 
    * Enter sp user id & password. 
    * 4. After successful sp login, "Single sign-on succeeded" msg is displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    * @param bindingType can be artifact or post
    * @param idpLoginOnly can be used in autofedertion case, where only idplogin 
    * is req
    */
    public static void getxmlIDPInitSSO(String xmlFileName, Map m, 
            String bindingType, boolean idpLoginOnly)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idp_proto = (String)m.get("idp_proto");
        String idp_port = (String)m.get("idp_port");
        String idp_host = (String)m.get("idp_host");
        String idp_deployment_uri = (String)m.get("idp_deployment_uri");
        String idp_alias = (String)m.get("idp_alias");
        String sp_entity_name = (String)m.get("sp_entity_name");
        String sp_user = (String)m.get("sp_user");
        String sp_userpw = (String)m.get("sp_userpw");
        String idp_user = (String)m.get("idp_user");
        String idp_userpw = (String)m.get("idp_userpw");
        String strResult = (String)m.get("ssoinitresult");
        out.write("<url href=\"" + idp_proto +"://" + idp_host + ":" 
                + idp_port + idp_deployment_uri 
                + "/saml2/jsp/idpSSOInit.jsp?metaAlias=/" + idp_alias 
                + "&amp;spEntityID=" + sp_entity_name);
        if (bindingType == "post"){
            out.write("&amp;binding=HTTP-POST");
        }
        if (m.get("urlparams")!= null){
            out.write("&amp;" + m.get("urlparams"));
        }
        out.write("\">");
        out.write(newline);
        if(!idpLoginOnly){
            out.write("<form name=\"Login\" buttonName=\"\" >");
            out.write(newline);
            out.write("<input name=\"IDToken1\" value=\"" + sp_user + "\" />");
            out.write(newline);
            out.write("<input name=\"IDToken2\" value=\""+ sp_userpw + "\" />");
            out.write(newline);
        } else {
            out.write("<form>");
            out.write(newline);
        }
        out.write("<result text=\"" + strResult + "\" />");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }


   /**
    * This method creates spsso xml 
    * This xml is for sp initiated sso for already existing federation. 
    * The flow is as follows
    * 1. Go to spSSOInit.jsp on sp side.
    * 2. It redirects to idp login. Enter idp user id & password. 
    * 3. After successful idp login, "Single sign-on succeeded" msg is displayed 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    * @param bindingType can be artifact or post
    */
    public static void getxmlSPSSO(String xmlFileName, Map m, String bindingType)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String sp_alias = (String)m.get("sp_alias");
        String idp_entity_name = (String)m.get("idp_entity_name");
        String idp_user = (String)m.get("idp_user");
        String idp_userpw = (String)m.get("idp_userpw");
        String strResult = (String)m.get("ssoresult");

        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri 
                + "/saml2/jsp/spSSOInit.jsp?metaAlias=/" + sp_alias 
                + "&amp;idpEntityID=" + idp_entity_name );
        if (bindingType == "artifact"){
            out.write("\">");
        }else{
            out.write("&amp;binding=HTTP-POST\">");
        }
        out.write(newline);
        out.write("<form name=\"Login\" buttonName=\"\">");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + idp_user + "\"/>");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\""+ idp_userpw + "\"/>");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
        }
    
   /**
    * This method creates spslo xml 
    * This xml is for sp initiated slo. The flow is as follows
    * 1. Go to spSingleLogoutInit.jsp on sp side.
    * 2. After successful logout on sp & idp "SP initiated single logout 
    * succeeded." msg is displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    * @param bindingType can be http or soap
    */
    public static void getxmlSPSLO(String xmlFileName, Map m, 
            String bindingType)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String sp_alias = (String)m.get("sp_alias");
        String idp_entity_name = (String)m.get("idp_entity_name");
        String strResult = (String)m.get("spsloresult");

        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri 
                + "/saml2/jsp/spSingleLogoutInit.jsp?metaAlias=/" 
                + sp_alias + "&amp;idpEntityID=" + idp_entity_name);
        if (bindingType == "soap"){
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
        }
        if (m.get("urlparams")!= null){
            out.write("&amp;" + m.get("urlparams"));
        }
        out.write("\">");
        out.write(newline);
        out.write("<form>");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

   /**
    * This method creates spslo xml 
    * This xml is for idp initiated slo. The flow is as follows
    * 1. Go to idpSingleLogoutInit.jsp on sp side.
    * 2. After successful logout on sp & idp "IDP initiated single logout 
    * succeeded." msg is displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    * @param bindingType can be http (http-redirect) or soap
    */
    public static void getxmlIDPSLO(String xmlFileName, Map m, 
            String bindingType)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idp_proto = (String)m.get("idp_proto");
        String idp_port = (String)m.get("idp_port");
        String idp_host = (String)m.get("idp_host");
        String idp_deployment_uri = (String)m.get("idp_deployment_uri");
        String idp_alias = (String)m.get("idp_alias");
        String sp_entity_name = (String)m.get("sp_entity_name");
        String strResult = (String)m.get("idpsloresult");

        out.write("<url href=\"" + idp_proto +"://" + idp_host + ":" 
                + idp_port + idp_deployment_uri 
                + "/saml2/jsp/idpSingleLogoutInit.jsp?metaAlias=/" 
                + idp_alias + "&amp;spEntityID=" + sp_entity_name);
        if (bindingType == "soap"){
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
        }
        if (m.get("urlparams")!= null){
            out.write("&amp;" + m.get("urlparams"));
        }
        out.write("\">");
        out.write(newline);
        out.write("<form>");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

   /**
    * This method creates spMNIRequestInit xml 
    * This xml is for sp initiated termination. This assumes there is no sso 
    * done in the current browser session. The flow is as follows
    * 1. Go to spMNIRequestInit.jsp on sp side.
    * 2. It redirects to sp login. Enter sp user id & password. 
    * 3. After successful sp login, "ManageNameID Request succeeded." msg is 
    * displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    * @param bindingType can be http (http-redirect) or soap
    */
    public static void getxmlSPTerminate(String xmlFileName, Map m, 
            String bindingType)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String sp_alias = (String)m.get("sp_alias");
        String idp_entity_name = (String)m.get("idp_entity_name");
        String sp_user = (String)m.get("sp_user");
        String sp_userpw = (String)m.get("sp_userpw");
        String strResult = (String)m.get("terminateresult");

        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri 
                + "/saml2/jsp/spMNIRequestInit.jsp?metaAlias=/" + sp_alias 
                + "&amp;idpEntityID=" + idp_entity_name 
                + "&amp;requestType=Terminate");
        if (bindingType == "soap"){
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
        }
        if (m.get("urlparams")!= null){
            out.write("&amp;" + m.get("urlparams"));
        }
        out.write("\">");
        out.write(newline);
        out.write("<form name=\"Login\" buttonName=\"\">");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + sp_user + "\"/>");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\"" + sp_userpw + "\"/>");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }
    
   /**
    * This method creates spMNIRequestInit xml 
    * This xml is for sp initiated termination. This assumes there is no sso 
    * done in the current browser session. The flow is as follows
    * 1. Go to spMNIRequestInit.jsp on sp side.
    * 2. It redirects to sp login. Enter sp user id & password. 
    * 3. After successful sp login, "ManageNameID Request succeeded." msg is 
    * displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    * @param bindingType can be http (http-redirect) or soap
    */
    public static void getxmlIDPTerminate(String xmlFileName, Map m, 
            String bindingType)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idp_proto = (String)m.get("idp_proto");
        String idp_port = (String)m.get("idp_port");
        String idp_host = (String)m.get("idp_host");
        String idp_deployment_uri = (String)m.get("idp_deployment_uri");
        String idp_alias = (String)m.get("idp_alias");
        String sp_entity_name = (String)m.get("sp_entity_name");
        String idp_user = (String)m.get("idp_user");
        String idp_userpw = (String)m.get("idp_userpw");
        String strResult = (String)m.get("terminateresult");

        out.write("<url href=\"" + idp_proto +"://" + idp_host + ":" 
                + idp_port + idp_deployment_uri 
                + "/saml2/jsp/idpMNIRequestInit.jsp?metaAlias=/" + idp_alias 
                + "&amp;spEntityID=" + sp_entity_name 
                + "&amp;requestType=Terminate");
        if (bindingType == "soap"){
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
        }
        if (m.get("urlparams")!= null){
            out.write("&amp;" + m.get("urlparams"));
        }
        out.write("\">");
        out.write(newline);
        out.write("<form name=\"Login\" buttonName=\"\">");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + idp_user + "\"/>");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\"" + idp_userpw 
                + "\"/>");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

    
    /**
    * This method creates spMNIRequestInit xml 
    * This xml is for sp initiated termination. This assumes there is no sso 
    * done in the current browser session. The flow is as follows
    * 1. Go to spMNIRequestInit.jsp on sp side.
    * 2. It redirects to sp login. Enter sp user id & password. 
    * 3. After successful sp login, "ManageNameID Request succeeded." msg is 
    * displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    * @param bindingType can be http (http-redirect) or soap
    */
    public static void getxmlSPTerminate2(String xmlFileName, Map m, 
            String bindingType)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String sp_alias = (String)m.get("sp_alias");
        String idp_entity_name = (String)m.get("idp_entity_name");
        String strResult = (String)m.get("terminateresult");

        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri 
                + "/saml2/jsp/spMNIRequestInit.jsp?metaAlias=/" + sp_alias 
                + "&amp;idpEntityID=" + idp_entity_name
                + "&amp;requestType=Terminate" );
        if (bindingType == "soap"){
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
        }
        if (m.get("urlparams")!= null){
            out.write("&amp;" + m.get("urlparams"));
        }
        out.write("\">");
        out.write(newline);
        out.write("<form>");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.close();
    }
    
        
    /**
    * This method creates spconfigurator xml 
    * This xml is for configuring samlv2 sp. The flow is as follows
    *  1. go to samples/saml2/sp/configure.jsp & enter idp details. 
    *  2. Configuration is successful. 
    * displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    */
    public static void getxmlSPConfigurator(String xmlFileName, Map m)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String idp_proto = (String)m.get("idp_proto");
        String idp_port = (String)m.get("idp_port");
        String idp_host = (String)m.get("idp_host");
        String idp_deployment_uri = (String)m.get("idp_deployment_uri");
        String sp_alias = (String)m.get("sp_alias");
        String idp_entity_name = (String)m.get("idp_entity_name");
        String strResult = (String)m.get("spconfiguratorresult");

        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri 
                + "/samples/saml2/sp/configure.jsp\" >");
        out.write(newline);
        out.write("<form name=\"_none_\" buttonName=\"\">");
        out.write(newline);
        out.write("<input name=\"proto\" value=\"" + idp_proto + "\"/>");
        out.write(newline);
        out.write("<input name=\"host\" value=\"" + idp_host + "\"/>");
        out.write(newline);
        out.write("<input name=\"port\" value=\"" + idp_port + "\"/>");
        out.write(newline);
        out.write("<input name=\"deploymenturi\" value=\"" 
                + idp_deployment_uri + "\"/>");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.close();
    }
   
    /**
    * This method creates spconfigurator xml 
    * This xml is for configuring samlv2 sp. The flow is as follows
    *  1. go to samples/saml2/sp/configure.jsp & enter idp details. 
    *  2. Configuration is successful. 
    * displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    */
    public static void getxmlIDPConfigurator(String xmlFileName, Map m)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String idp_proto = (String)m.get("idp_proto");
        String idp_port = (String)m.get("idp_port");
        String idp_host = (String)m.get("idp_host");
        String idp_deployment_uri = (String)m.get("idp_deployment_uri");
        String sp_alias = (String)m.get("sp_alias");
        String idp_entity_name = (String)m.get("idp_entity_name");
        String strResult = (String)m.get("idconfiguratorresult");

        out.write("<url href=\"" + idp_proto +"://" + idp_host + ":" 
                + idp_port + idp_deployment_uri 
                + "/samples/saml2/idp/configure.jsp\" >");
        out.write(newline);
        out.write("<form name=\"_none_\" buttonName=\"\">");
        out.write(newline);
        out.write("<input name=\"proto\" value=\"" + sp_proto + "\"/>");
        out.write(newline);
        out.write("<input name=\"host\" value=\"" + sp_host + "\"/>");
        out.write(newline);
        out.write("<input name=\"port\" value=\"" + sp_port + "\"/>");
        out.write(newline);
        out.write("<input name=\"deploymenturi\" value=\"" 
                + sp_deployment_uri + "\"/>");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.close();
    }
    /**
    * This method creates splogin xml 
    * Enter sp user id & password. 
    * After successful sp login, "Authentication successful" msg is displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    */
    public static void getxmlSPLogin(String xmlFileName, Map m)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String sp_user = (String)m.get("sp_user");
        String sp_userpw = (String)m.get("sp_userpw");
        String strResult = (String)m.get("loginresult");
        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri + "\">");
        out.write(newline);
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + sp_user + "\" />");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\""
                + sp_userpw + "\" />");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\" />");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

    /**
    * This method creates idplogin xml 
    * Enter idp user id & password. 
    * After successful sp login, "Authentication successful" msg is displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    */
    public static void getxmlIDPLogin(String xmlFileName, Map m)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idp_proto = (String)m.get("idp_proto");
        String idp_port = (String)m.get("idp_port");
        String idp_host = (String)m.get("idp_host");
        String idp_deployment_uri = (String)m.get("idp_deployment_uri");
        String idp_user = (String)m.get("idp_user");
        String idp_userpw = (String)m.get("idp_userpw");
        String strResult = (String)m.get("loginresult");
        out.write("<url href=\"" + idp_proto +"://" + idp_host + ":" 
                + idp_port + idp_deployment_uri + "\">");
        out.write(newline);
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + idp_user + "\" />");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\""
                + idp_userpw + "\" />");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\" />");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

    /**
    * This method creates spconsolelogin xml 
    * Enter sp admin user id & password. 
    * After successful sp login, "Authentication successful" msg is displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    */
    public static void getxmlSPConsoleLogin(String xmlFileName, Map m)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("sp_proto");
        String sp_port = (String)m.get("sp_port");
        String sp_host = (String)m.get("sp_host");
        String sp_deployment_uri = (String)m.get("sp_deployment_uri");
        String sp_admin = (String)m.get("sp_admin");
        String sp_adminpw = (String)m.get("sp_adminpw");
        String strResult = (String)m.get("loginresult");
        //Added goto parameter as javascript in console is 
        //resulting into an exception. 
        //This should be removed once that problem is fixed. 
        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri + 
                "/UI/Login?goto=http://www.google.com\">");
        out.write(newline);
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + sp_admin + "\" />");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\""
                + sp_adminpw + "\" />");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\" />");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

    /**
    * This method creates idpconsolelogin xml 
    * Enter idp admin user id & password. 
    * After successful sp login, "Authentication successful" msg is displayed. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    */
    public static void getxmlIDPConsoleLogin(String xmlFileName, Map m)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idp_proto = (String)m.get("idp_proto");
        String idp_port = (String)m.get("idp_port");
        String idp_host = (String)m.get("idp_host");
        String idp_deployment_uri = (String)m.get("idp_deployment_uri");
        String idp_admin = (String)m.get("idp_admin");
        String idp_adminpw = (String)m.get("idp_adminpw");
        String strResult = (String)m.get("loginresult");
        out.write("<url href=\"" + idp_proto +"://" + idp_host + ":" 
                + idp_port + idp_deployment_uri + 
                "/UI/Login\">");
        out.write(newline);
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write(newline);
        out.write("<input name=\"IDToken1\" value=\"" + idp_admin + "\" />");
        out.write(newline);
        out.write("<input name=\"IDToken2\" value=\""
                + idp_adminpw + "\" />");
        out.write(newline);
        out.write("<result text=\"" + strResult + "\" />");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

    /**
    * This method creates spconsolelogout xml 
    * Logs the user out 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    */
    public static void getxmlSPLogout(String xmlFileName, Map m)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String sp_proto = (String)m.get("idp_proto");
        String sp_port = (String)m.get("idp_port");
        String sp_host = (String)m.get("idp_host");
        String sp_deployment_uri = (String)m.get("idp_deployment_uri");
        String strResult = (String)m.get("loginresult");
        out.write("<url href=\"" + sp_proto +"://" + sp_host + ":" 
                + sp_port + sp_deployment_uri + "/UI/Logout\">");
        out.write(newline);
        out.write("<form>");
        out.write(newline);
        out.write("<result text=\"Access Manager\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

    /**
    * This method creates idplogout xml 
    * Logs the user out. 
    * @param xmlFileName is the file to be created. 
    * @param Map m contains all the data for xml generation
    */
    public static void getxmlIDPLogout(String xmlFileName, Map m)
        throws Exception {
        FileWriter fstream = new FileWriter(xmlFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        String idp_proto = (String)m.get("idp_proto");
        String idp_port = (String)m.get("idp_port");
        String idp_host = (String)m.get("idp_host");
        String idp_deployment_uri = (String)m.get("idp_deployment_uri");
        String strResult = (String)m.get("loginresult");
        out.write("<url href=\"" + idp_proto +"://" + idp_host + ":" 
                + idp_port + idp_deployment_uri + "\">");
        out.write(newline);
        out.write("<form>");
        out.write(newline);
        out.write("<result text=\"Access Manager\"/>");
        out.write(newline);
        out.write("</form>");
        out.write(newline);
        out.write("</url>");
        out.write(newline);
        out.close();
    }

   /**
    * This method loads the resource bundle & puts all the values in map
    * @param String rbName Resource bundle name 
    * @param Map m will be populated with resource bundle data
    */ 
    public static void getEntriesFromResourceBundle(String rbName, Map map) {
        ResourceBundle rb = ResourceBundle.getBundle(rbName);
        for (Enumeration e = rb.getKeys(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            map.put(key, rb.getString(key));
        }
    } 
    
   /**
    * This method grep Metadata from the htmlpage & returns as the string. 
    * @param HtmlPage page which contains metadata 
    */ 
    public static String getMetadataFromPage(HtmlPage page) {
        String metadata = "";
        try{
            String metaPage = page.getWebResponse().getContentAsString();
            metadata = metaPage.substring(metaPage.
                    indexOf("EntityDescriptor") - 4,
                    metaPage.lastIndexOf("EntityDescriptor") + 17);
            metadata = metadata.replaceAll("&lt;", "<");
            metadata = metadata.replaceAll("&gt;", ">");
        } catch (Exception e){
            
        }
        return metadata;
    } 

   /**
    * This method grep ExtendedMetadata from the htmlpage & returns as the string
    * @param HtmlPage page which contains extended metadata 
    */ 
    public static String getExtMetadataFromPage(HtmlPage page) {
        String metadata = "";
        try{
            String metaPage = page.getWebResponse().getContentAsString();
            metadata = metaPage.substring(metaPage.
                    indexOf("EntityConfig") - 4,
                    metaPage.lastIndexOf("EntityConfig") + 13);
            metadata = metadata.replaceAll("&lt;", "<");
            metadata = metadata.replaceAll("&gt;", ">");        
        } catch (Exception e){
            
        }
        return metadata;
    } 

   /**
    * This method creates the hosted SP metadata template & loads it. 
    * It returns the uploaded standard & extended metadata. 
    * Null is returned in case of failure.
    * @param WebClient object after admin login is successful. 
    * @param Map consisting of SP data
    * @param boolean signed metadata should contain signature true or false
    */ 
    public static String[] configureSP(WebClient webClient, Map m, 
            boolean signed) {
       String[] arrMetadata= {"", ""};
       try {
            String spurl = m.get("sp_proto") + "://" +
                    m.get("sp_host") + ":" + m.get("sp_port")
                    + m.get("sp_deployment_uri");
            
            //get sp & idp extended metadata
            FederationManager spfm = new FederationManager(spurl);
            HtmlPage spmetaPage;
            if(signed){
                spmetaPage = spfm.createMetadataTemplate(webClient,
                        (String)m.get("sp_entity_name"), true, true, 
                        (String)m.get("sp_alias"), null, null, null,
                        (String)m.get("sp_certalias"), null, null, null,
                        (String)m.get("sp_certalias"), null, null, null, 
                        "saml2");
            } else {
                 spmetaPage = spfm.createMetadataTemplate(webClient,
                        (String)m.get("sp_entity_name"), true, true, 
                        (String)m.get("sp_alias"), null, null, null,
                        null, null, null, null, null, null, null, null, 
                         "saml2");
            }
            
            String spPage = spmetaPage.getWebResponse().getContentAsString();
            arrMetadata[0] = spPage.substring(
                    spPage.indexOf("EntityDescriptor") - 4, 
                    spPage.lastIndexOf("EntityDescriptor") + 17);
            arrMetadata[1] = spPage.substring(
                    spPage.indexOf("EntityConfig") - 4, 
                    spPage.lastIndexOf("EntityConfig") + 13);
            if((arrMetadata[0].equals(null))||(arrMetadata[1].equals(null))){
                assert(false);
            } else {   
                arrMetadata[0] = arrMetadata[0].replaceAll("&lt;", "<");
                arrMetadata[0] = arrMetadata[0].replaceAll("&gt;", ">");
                arrMetadata[1] = arrMetadata[1].replaceAll("&lt;", "<");
                arrMetadata[1] = arrMetadata[1].replaceAll("&gt;", ">");
                HtmlPage importMeta = spfm.importEntity(webClient, 
                        (String)m.get("sp_realm"), arrMetadata[0], 
                        arrMetadata[1], (String)m.get("sp_cot"), "saml2");
                if(!importMeta.getWebResponse().getContentAsString().
                        contains("Import file, web.")) {
                    assert(false);
                    arrMetadata[0] = null;
                    arrMetadata[1] = null;
                }       
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrMetadata;
       
    } 
    
   /**
    * This method creates the hosted IDP metadata template & loads it. 
    * @param WebClient object after admin login is successful. 
    * @param Map consisting of IDP data
    * @param boolean signed metadata should contain signature true or false
    */ 
    public static String[] configureIDP(WebClient webClient, Map m, 
            boolean signed) {
        String[] arrMetadata={"",""};
        try {
            String idpurl = m.get("idp_proto") + "://" +
                    m.get("idp_host") + ":" + m.get("idp_port")
                    + m.get("idp_deployment_uri");
            
            //get sp & idp extended metadata
            FederationManager idpfm = new FederationManager(idpurl);
            HtmlPage idpmetaPage;
            if(signed){
                idpmetaPage = idpfm.createMetadataTemplate(webClient,
                        (String)m.get("idp_entity_name"), true, true, 
                        null, (String)m.get("idp_alias"), null, null,
                        null, (String)m.get("idp_certalias"), null, null,
                        null, (String)m.get("idp_certalias"), null, null, 
                        "saml2");
            } else {
                idpmetaPage = idpfm.createMetadataTemplate(webClient,
                        (String)m.get("idp_entity_name"), true, true, 
                        null, (String)m.get("idp_alias"), null, null,
                        null, null, null, null, null, null, null, null, 
                        "saml2");
            }
            String idpPage = idpmetaPage.getWebResponse().getContentAsString();
            arrMetadata[0] = idpPage.substring(
                    idpPage.indexOf("EntityDescriptor") - 4, 
                    idpPage.lastIndexOf("EntityDescriptor") + 17);
            arrMetadata[1] = idpPage.substring(
                    idpPage.indexOf("EntityConfig") - 4, 
                    idpPage.lastIndexOf("EntityConfig") + 13);
            if((arrMetadata[0].equals(null))||(arrMetadata[1].equals(null))){
                assert(false);
            } else {   
                arrMetadata[0] = arrMetadata[0].replaceAll("&lt;", "<");
                arrMetadata[0] = arrMetadata[0].replaceAll("&gt;", ">");
                arrMetadata[1] = arrMetadata[1].replaceAll("&lt;", "<");
                arrMetadata[1] = arrMetadata[1].replaceAll("&gt;", ">");
                HtmlPage importMeta = idpfm.importEntity(webClient, 
                        (String)m.get("idp_realm"), arrMetadata[0], 
                        arrMetadata[1], (String)m.get("idp_cot"), "saml2");
                if(!importMeta.getWebResponse().getContentAsString().
                        contains("Import file, web.")) {
                    assert(false);
                    arrMetadata[0] = null;
                    arrMetadata[1] = null;
                }       
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return arrMetadata;
    } 
}

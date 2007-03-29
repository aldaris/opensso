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
 * $Id: SAMLv2Common.java,v 1.1 2007-03-29 21:41:14 mrudulahg Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class contains helper methods for samlv2 tests
 */
public class SAMLv2Common extends TestCommon {

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
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
    * bindingType can be artifact or post
    */
    public static void getxmlSPInitSSO(String xmlFileName, Map m, String bindingType)
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
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + idp_user + "\" />");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\""
                + idp_userpw + "\" />");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + sp_user + "\" />");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\"" 
                + sp_userpw + "\" />");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\" />");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }

    /**
    * This method creates spssoinit xml 
    * It assumes that user has logged in to the idp. 
    * 1. Go to idpSSOInit.jsp on idp side.
    * 2. It redirects to sp login. 
    * Enter sp user id & password. 
    * 4. After successful sp login, "Single sign-on succeeded" msg is displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
    * bindingType can be artifact or post
    */
    public static void getxmlIDPInitSSO(String xmlFileName, Map m, String bindingType)
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
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + sp_user + "\" />");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\""+ sp_userpw + "\" />");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\" />");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }


    /*
    * This method creates spsso xml 
    * This xml is for sp initiated sso for already existing federation. 
    * The flow is as follows
    * 1. Go to spSSOInit.jsp on sp side.
    * 2. It redirects to idp login. Enter idp user id & password. 
    * 3. After successful idp login, "Single sign-on succeeded" msg is displayed 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
    * bindingType can be artifact or post
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
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\">");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + idp_user + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\""+ idp_userpw + "\"/>");
        out.write("\r\n");
        out.write("<result text=" + strResult + "/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
        }
    
    /**
    * This method creates spslo xml 
    * This xml is for sp initiated slo. The flow is as follows
    * 1. Go to spSingleLogoutInit.jsp on sp side.
    * 2. After successful logout on sp & idp "SP initiated single logout 
    * succeeded." msg is displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
    * bindingType can be artifact or post
    */
    public static void getxmlSPSLO(String xmlFileName, Map m, String bindingType)
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
        if (bindingType == "http"){
            out.write("\">");
        }else{
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP\">");
        }
        out.write("\r\n");
        out.write("<form>");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }

    /**
    * This method creates spslo xml 
    * This xml is for idp initiated slo. The flow is as follows
    * 1. Go to idpSingleLogoutInit.jsp on sp side.
    * 2. After successful logout on sp & idp "IDP initiated single logout 
    * succeeded." msg is displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
    * bindingType can be http (http-redirect) or soap
    */
    public static void getxmlIDPSLO(String xmlFileName, Map m, String bindingType)
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
        if (bindingType == "http"){
            out.write("\">");
        }else{
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP\">");
        }
        out.write("\r\n");
        out.write("<form>");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
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
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
    * bindingType can be http (http-redirect) or soap
    */
    public static void getxmlSPTerminate(String xmlFileName, Map m, String bindingType)
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
        if (bindingType == "http"){
            out.write("\">");
        }else{
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP\">");
        }
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\">");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + sp_user + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\"" + sp_userpw + "\"/>");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
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
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
    * bindingType can be http (http-redirect) or soap
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
        if (bindingType == "http"){
            out.write("\">");
        }else{
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP\">");
        }
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\">");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + idp_user + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\"" + idp_userpw 
                + "\"/>");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
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
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
    * bindingType can be http (http-redirect) or soap
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
        if (bindingType == "http"){
            out.write("\">");
        }else{
            out.write("&amp;" +
                    "binding=urn:oasis:names:tc:SAML:2.0:bindings:SOAP\">");
        }
        out.write("\r\n");
        out.write("<form>");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.close();
    }
    
        
    /**
    * This method creates spconfigurator xml 
    * This xml is for configuring samlv2 sp. The flow is as follows
    *  1. go to samples/saml2/sp/configure.jsp & enter idp details. 
    *  2. Configuration is successful. 
    * displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
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
        out.write("\r\n");
        out.write("<form name=\"_none_\" buttonName=\"\">");
        out.write("\r\n");
        out.write("<input name=\"proto\" value=\"" + idp_proto + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"host\" value=\"" + idp_host + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"port\" value=\"" + idp_port + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"deploymenturi\" value=\"" 
                + idp_deployment_uri + "\"/>");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.close();
    }
   
    /**
    * This method creates spconfigurator xml 
    * This xml is for configuring samlv2 sp. The flow is as follows
    *  1. go to samples/saml2/sp/configure.jsp & enter idp details. 
    *  2. Configuration is successful. 
    * displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
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
        out.write("\r\n");
        out.write("<form name=\"_none_\" buttonName=\"\">");
        out.write("\r\n");
        out.write("<input name=\"proto\" value=\"" + sp_proto + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"host\" value=\"" + sp_host + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"port\" value=\"" + sp_port + "\"/>");
        out.write("\r\n");
        out.write("<input name=\"deploymenturi\" value=\"" 
                + sp_deployment_uri + "\"/>");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.close();
    }
    /**
    * This method creates splogin xml 
    * Enter sp user id & password. 
    * After successful sp login, "Authentication successful" msg is displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
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
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + sp_user + "\" />");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\""
                + sp_userpw + "\" />");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\" />");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }

    /**
    * This method creates idplogin xml 
    * Enter idp user id & password. 
    * After successful sp login, "Authentication successful" msg is displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
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
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + idp_user + "\" />");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\""
                + idp_userpw + "\" />");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\" />");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }

    /**
    * This method creates spconsolelogin xml 
    * Enter sp admin user id & password. 
    * After successful sp login, "Authentication successful" msg is displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
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
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + sp_admin + "\" />");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\""
                + sp_adminpw + "\" />");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\" />");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }

    /**
    * This method creates idpconsolelogin xml 
    * Enter idp admin user id & password. 
    * After successful sp login, "Authentication successful" msg is displayed. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
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
        //Added goto parameter as javascript in console is 
        //resulting into an exception. 
        //This should be removed once that problem is fixed. 
        out.write("<url href=\"" + idp_proto +"://" + idp_host + ":" 
                + idp_port + idp_deployment_uri + 
                "/UI/Login?goto=http://www.google.com\">");
        out.write("\r\n");
        out.write("<form name=\"Login\" buttonName=\"\" >");
        out.write("\r\n");
        out.write("<input name=\"IDToken1\" value=\"" + idp_admin + "\" />");
        out.write("\r\n");
        out.write("<input name=\"IDToken2\" value=\""
                + idp_adminpw + "\" />");
        out.write("\r\n");
        out.write("<result text=\"" + strResult + "\" />");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }


    /**
    * This method creates spconsolelogout xml 
    * Logs the user out 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
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
        out.write("\r\n");
        out.write("<form>");
        out.write("\r\n");
        out.write("<result text=\"Access Manager\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }

    /**
    * This method creates idplogout xml 
    * Logs the user out. 
    * xmlFileName is the file to be created. 
    * Map m contains all the data for xml generation
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
        out.write("\r\n");
        out.write("<form>");
        out.write("\r\n");
        out.write("<result text=\"Access Manager\"/>");
        out.write("\r\n");
        out.write("</form>");
        out.write("\r\n");
        out.write("</url>");
        out.write("\r\n");
        out.close();
    }

   /**
   * This method loads the resource bundle & puts all the values in map
   */ 
    public static void getEntriesFromResourceBundle(String rbName, Map map) {
        ResourceBundle rb = ResourceBundle.getBundle(rbName);
        for (Enumeration e = rb.getKeys(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            map.put(key, rb.getString(key));
        }
    } 
}

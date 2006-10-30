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
 * $Id: WSPRedirectHandlerServlet.java,v 1.1 2006-10-30 23:15:11 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.liberty.ws.interaction;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.liberty.ws.common.LogUtil;
import com.sun.identity.liberty.ws.interaction.jaxb.InquiryElement;
import com.sun.identity.liberty.ws.interaction.jaxb.InteractionResponseElement;
import com.sun.identity.liberty.ws.interaction.jaxb.ParameterType;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class that works in conjection with InteractionManager to facilitate
 * WSP- resource owner interactions
 */
public class WSPRedirectHandlerServlet extends HttpServlet {

    private static Debug debug = Debug.getInstance("libInteraction");
    private static ResourceBundle i18n = 
        ResourceBundle.getBundle("libInteraction");
    private static String PARAMETER_PREFIX = "isparam_";

    private static String TRUE_LABEL = "trueLabel";
    private static String FALSE_LABEL = "falseLabel";
    private static String HELP_LABEL = "helpLabel";
    private static String HINT_LABEL = "hintLabel";
    private static String LINK_LABEL = "linkLabel";
    private static String MORE_LINK_LABEL = "moreLinkLabel";

    private static String DEFAULT_TRUE_LABEL = "true";
    private static String DEFAULT_FALSE_LABEL = "false";
    private static String DEFAULT_HELP_LABEL = "help";
    private static String DEFAULT_HINT_LABEL = "hint";
    private static String DEFAULT_LINK_LABEL = "link";
    private static String DEFAULT_MORE_LINK_LABEL = "moreLink";

    private DOMSource htmlStyleSource = null;
    private DOMSource wmlStyleSource = null;

    private static String WML_CLIENT = "WML";

    public void init(ServletConfig servletConfig) 
            throws ServletException {
        super.init(servletConfig);

        String htmlStyleSheetLocation 
                = InteractionConfig.getInstance()
                .getHTMLStyleSheetLocation();
        String wmlStyleSheetLocation 
                = InteractionConfig.getInstance()
                .getWMLStyleSheetLocation();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(htmlStyleSheetLocation));
            htmlStyleSource = new DOMSource(doc);
            db = dbf.newDocumentBuilder();
            doc = db.parse(new File(wmlStyleSheetLocation));
            wmlStyleSource = new DOMSource(doc);
        } catch (ParserConfigurationException pce) {
            debug.error("WSPRedirectHandlerServlet.init()", pce);
            throw new ServletException(pce);
        } catch (SAXException se) {
            debug.error("WSPRedirectHandlerServlet.init()", se);
            throw new ServletException(se);
        } catch (IOException ioe) {
            debug.error("WSPRedirectHandlerServlet.init()", ioe);
            throw new ServletException(ioe);
        }
        if (debug.messageEnabled()) {
            debug.message("WSPRedirectHandlerServlet.init():initialized");
        }
    }

    public void doGet(HttpServletRequest httpRequest, 
            HttpServletResponse httpResponse) 
            throws IOException { 
        handleRequest(httpRequest, httpResponse);
        //testXSL(httpRequest, httpResponse);
    }

    public void doPost(HttpServletRequest httpRequest, 
            HttpServletResponse httpResponse) 
            throws IOException { 
        handleRequest(httpRequest, httpResponse);
    }

    private void handleRequest(HttpServletRequest httpRequest, 
            HttpServletResponse httpResponse) 
            throws IOException { 
        String requestURL = getRequestURL(httpRequest);
        String messageID = httpRequest.getParameter(
                InteractionManager.TRANS_ID);
        String returnToURL = httpRequest.getParameter(
                InteractionManager.RETURN_TO_URL);
        if (debug.messageEnabled()) {
            debug.message(
                    "WSPRedirectHandlerServlet.handleRequest():entering "
                    + "with requestURL=" + requestURL
                    + ":messageID=" + messageID
                    + ":returnToURL=" + returnToURL);
        }
        if (messageID != null) {
            if (returnToURL != null) {
                if (debug.messageEnabled()) {
                    debug.message(
                            "WSPRedirecthandlerServlet.handleRequest():"
                            + " entering with returnToRL=" + returnToURL
                            + " :a new request");
                }

                //returnToURL should not have ResendMessage parameter
                if (!(returnToURL.indexOf(InteractionManager.RESEND_MESSAGE 
                            + "=") == -1)) {
                    debug.error(
                            "WSPRedirecthandlerServlet.handleRequest():"
                            + "invalid Request - illegal parameter:"
                            + InteractionManager.RESEND_MESSAGE
                            + ":returnToURL=" + returnToURL); 
                    showErrorPage(httpRequest, httpResponse, 
                            "Invalid Request - illegal parameter:"
                            + InteractionManager.RESEND_MESSAGE
                            + ":returnToURL=" + returnToURL); 
                    return;
                    
                }

                //returnToURL should be https
                if (InteractionConfig.getInstance().wspEnforcesHttpsCheck()
                            && (returnToURL.indexOf("https") != 0) ) {
                    debug.error(
                            "WSPRedirecthandlerServlet.handleRequest():"
                            + "Invalid Request " 
                            + InteractionManager.RETURN_TO_URL
                            + " not https"
                            + ":returnToURL=" + returnToURL);
                    showErrorPage(httpRequest, httpResponse, 
                            "Invalid Request " 
                            + InteractionManager.RETURN_TO_URL
                            + " not https"
                            + ":returnToURL=" + returnToURL);
                    return;
                }

                //returnToURL should point to requestHost
                if (InteractionConfig.getInstance()
                            .wspEnforcesReturnToHostEqualsRequestHost()
                            && !checkReturnToHost(messageID, returnToURL)) {
                    debug.error(
                            "WSPRedirecthandlerServlet.handleRequest():"
                            + "Invalid Request ReturnToHost differs from " 
                            + " RequestHost"
                            + ":returnToURL=" + returnToURL
                            + ":requestHost=" 
                            + InteractionManager.getInstance()
                            .getRequestHost(messageID));
                    showErrorPage(httpRequest, httpResponse, 
                            "Invalid Request ReturnToHost differs from " 
                            + " RequestHost"
                            + ":returnToURL=" + returnToURL
                            + ":requestHost=" 
                            + InteractionManager.getInstance()
                            .getRequestHost(messageID));
                    return;
                }

                //save returnToURL against messageID in InteractionManager
                InteractionManager.getInstance().setReturnToURL(messageID,
                        returnToURL);
                sendInteractionRequestPage(messageID, 
                        httpRequest, httpResponse);
            } else {

                if (debug.messageEnabled()) {
                    debug.message(
                            "WSPRedirecthandlerServlet.handleRequest():"
                            + " entering without retunrnToRL:"
                            + " response for query");
                }
                
                //get returnToURL against messageID in InteractionManager
                returnToURL = InteractionManager.getInstance()
                        .getReturnToURL(messageID);
                if (returnToURL == null) {
                    debug.error(
                            "WSPRedirecthandlerServlet.handleRequest():"
                            + " returnToURL not found to redirect");
                    showErrorPage(httpRequest, httpResponse, 
                             " returnToURL not found in cache");
                } else {
                    sendInteractionResponsePage(messageID, 
                            httpRequest, httpResponse, returnToURL);
                }
            }
        } else {
            debug.error(
                    "WSPRedirecthandlerServlet.handleRequest():"
                    + "request without messageID"
                    + ":requestURL=" + requestURL);

            //show some error page
            showErrorPage(httpRequest, httpResponse, 
                    "Invalid Request - missing messageID"
                            + ":requestURL=" + requestURL);
        }
        if (debug.messageEnabled()) {
            debug.message(
                    "WSPRedirecthandlerServlet.handleRequest():returning");
        }
    }


    /*
     * Constructs an HTML/WML page from InteractionQuery using 
     * an XSL stylesheet and sends the page to browser.
     */
    private void sendInteractionRequestPage(String messageID,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) 
            throws IOException {
        InquiryElement inquiryElement 
                = InteractionManager.getInstance().
                getInquiryElement(messageID);

        // generate html page, with action url pointing back to this servlet
        // set query parameters transID and responseID
        String action = httpRequest.getRequestURL().toString();
        action = action +"?" + InteractionManager.TRANS_ID + "=" + messageID; 
        if (debug.messageEnabled()) {
            debug.message(
                    "WSPRedirectHandlerServlet.sendInteractionRequestPage():"
                    + "action=" + action);
        }
        DOMSource styleSource = null;
        boolean wmlClient = isWMLClient(httpRequest);
        PrintWriter out = null;
        if (!wmlClient) { //not a wmlClient, assume html client
            if (debug.messageEnabled()) {
                debug.message( "WSPRedirectHandlerServlet."
                       + "sendInteractionRequestPage():"
                        + "clientType=html");
            }
            styleSource = htmlStyleSource;
            httpResponse.setContentType("text/html");
            out = httpResponse.getWriter();
        } else { //a wml client
            if (debug.messageEnabled()) {
                debug.message( "WSPRedirectHandlerServlet."
                       + "sendInteractionRequestPage():"
                        + "clientType=wml");
            }
            styleSource = wmlStyleSource;
            httpResponse.setContentType("text/vnd.wap.wml");
            out = httpResponse.getWriter();
            out.println("<?xml version=\"1.0\"?>");  
            out.println(
               "<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\"");  
            out.println("    \"http://www.wapforum.org/DTD/wml_1.1.xml\">");  
        }

        try {
            //style inquiryElement and send it to browser
            JAXBContext jaxbContext =
                    JAXBContext.newInstance(
                    "com.sun.identity.liberty.ws.interaction.jaxb");
            Marshaller marshaller = jaxbContext.createMarshaller();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            marshaller.marshal(inquiryElement, doc);

            doc.getDocumentElement().setAttribute("action", action);

            String trueLabel = DEFAULT_TRUE_LABEL;
            String falseLabel = DEFAULT_FALSE_LABEL;
            String helpLabel = DEFAULT_HELP_LABEL;
            String hintLabel = DEFAULT_HINT_LABEL;
            String linkLabel = DEFAULT_LINK_LABEL;
            String moreLinkLabel = DEFAULT_MORE_LINK_LABEL;
            String language = InteractionManager.getInstance().
                    getLanguage(messageID);
            if (language == null) {
                trueLabel = i18n.getString(TRUE_LABEL);
                falseLabel = i18n.getString(FALSE_LABEL);
                helpLabel = i18n.getString(HELP_LABEL);
                hintLabel = i18n.getString(HINT_LABEL);
                linkLabel = i18n.getString(LINK_LABEL);
                moreLinkLabel = i18n.getString(MORE_LINK_LABEL);
            } else {
                Locale locale = new Locale(language);
                ResourceBundle bundle = 
                    ResourceBundle.getBundle("libInteraction", locale);
                trueLabel = bundle.getString(TRUE_LABEL);
                falseLabel = bundle.getString(FALSE_LABEL);
                helpLabel = bundle.getString(HELP_LABEL);
                hintLabel = bundle.getString(HINT_LABEL);
                linkLabel = bundle.getString(LINK_LABEL);
                moreLinkLabel = bundle.getString(MORE_LINK_LABEL);
            }
            Element documentElement = doc.getDocumentElement();
            documentElement.setAttribute(TRUE_LABEL, trueLabel);
            documentElement.setAttribute(FALSE_LABEL, falseLabel);
            documentElement.setAttribute(HELP_LABEL, helpLabel);
            documentElement.setAttribute(HINT_LABEL, hintLabel);
            documentElement.setAttribute(LINK_LABEL, linkLabel);
            documentElement.setAttribute(MORE_LINK_LABEL, moreLinkLabel);

            TransformerFactory transformerFactory 
                    = TransformerFactory.newInstance();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(out);
            Transformer transformer 
                    = transformerFactory.newTransformer(styleSource);
            transformer.transform(domSource, streamResult);
            if (LogUtil.isLogEnabled()) {
                String[] objs = new String[1];
                objs[0] = messageID;
                LogUtil.access(Level.INFO,
                               LogUtil.IS_PRESENTED_QUERY_TO_USER_AGENT,objs);
            }
        } catch (JAXBException je) {
            debug.error(
                    "WSPRedirectHandlerServlet.sendInteractionRequestPage():"
                    + "catching JAXBException =", je);
            showErrorPage(httpRequest, httpResponse, 
                    "Error creating JAXBObject:"
                    + je.getMessage());
        } catch (ParserConfigurationException pce) {
            debug.error(
                    "WSPRedirectHandlerServlet.sendInteractionRequestPage():"
                    + "catching ParserConfigurationException =", pce);
            showErrorPage(httpRequest, httpResponse, 
                    "Error creating interaction request page:"
                    + pce.getMessage());
        } catch (TransformerException tce) {
            debug.error(
                    "WSPRedirectHandlerServlet.sendInteractionRequestPage():"
                    + "catching TransformerException =", tce);
            showErrorPage(httpRequest, httpResponse, 
                    "Error creating interaction request page:"
                    + tce.getMessage());
        }
    }

    private void sendInteractionResponsePage(String messageID,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse, String returnToURL) 
            throws IOException {
        if (debug.messageEnabled()) {
            debug.message(
                    "WSPRedirectHandlerServlet.sendInteractionResponsePage():"
                            +"entering");
        }
        try {

            //read and save query parameters;
            InteractionResponseElement interactionResponseElement 
                    = JAXBObjectFactory.getObjectFactory()
                    .createInteractionResponseElement();
            List list = interactionResponseElement.getParameter();
            Enumeration parameterNames = httpRequest.getParameterNames();
            while ( parameterNames.hasMoreElements()) {
                String parameterName 
                        = (String)parameterNames.nextElement();
                /*
                ParameterType parameterType  
                        = JAXBObjectFactory.getObjectFactory()
                        .createParameterType();
                */
                String parameterValue 
                        = httpRequest.getParameter(parameterName);
                if (debug.messageEnabled()) {
                    debug.message("WSPRedirectHandlerServlet"
                            + ".sendInteractionResponsePage():"
                            + "parameterName=" + parameterName
                            + "parameterValue=" + parameterValue);

                }
                int index = parameterName.indexOf(PARAMETER_PREFIX);
                if (index != -1) {
                    ParameterType parameterType  
                            = JAXBObjectFactory.getObjectFactory()
                            .createParameterType();
                    parameterName = parameterName.substring(index
                             + PARAMETER_PREFIX.length());
                    parameterType.setName(parameterName);
                    parameterType.setValue(parameterValue);
                    list.add(parameterType);
                }
            }
            if (LogUtil.isLogEnabled()) {
                String[] objs = new String[1];
                objs[0] = messageID;
                LogUtil.access(Level.INFO,
                               LogUtil.IS_COLLECTED_RESPONSE_FROM_USER_AGENT,
                               objs);
            }


            //store InteractionResponse in interaction manager;
            InteractionManager.getInstance().setInteractionResponseElement(
                    messageID, interactionResponseElement);
            if (returnToURL.indexOf("?") != -1) {
                returnToURL = returnToURL + "&" 
                        + InteractionManager.RESEND_MESSAGE + "=" 
                        + InteractionManager.getInstance()
                        .getRequestMessageID(messageID);
            } else {
                returnToURL = returnToURL + "?" 
                        + InteractionManager.RESEND_MESSAGE + "=" + messageID;
            }
            if (debug.messageEnabled()) {
                debug.message(
                        "WSPRedirectHandlerServlet."
                        + " sendInteractionResponsePage():"
                        + "redirecting user agent to returnToURL="
                        + returnToURL);
            }
            httpResponse.sendRedirect(returnToURL);
            if (LogUtil.isLogEnabled()) {
                String[] objs = new String[1];
                objs[0] = messageID;
                LogUtil.access(Level.INFO,LogUtil.IS_REDIRECTED_USER_AGENT_BACK,
                               objs);
            }
        } catch (JAXBException je) {
            debug.error(
                    "WSPRedirectHandlerServlet.sendInteractionResponsePage():"
                    + "catching JAXBException =", je);
            showErrorPage(httpRequest, httpResponse, 
                    "Error createing JAXBObject:"
                    + je.getMessage());
        } catch (Exception e) {
            debug.error(
                    "WSPRedirectHandlerServlet.sendInteractionResponsePage():"
                    + "catching Exception =", e);
        }

    }


    private String getRequestURL(HttpServletRequest httpRequest) {
        return  httpRequest.getRequestURL().append("?")
                .append(httpRequest.getQueryString()).toString();
    }

    private void showErrorPage(HttpServletRequest httpRequest, 
            HttpServletResponse httpResponse, String message) 
            throws IOException { 
        boolean wmlClient = isWMLClient(httpRequest);
        if (!wmlClient) { //not a wml client
            httpResponse.setContentType("text/plain");
            PrintWriter out = httpResponse.getWriter();
            out.println("<html>");
            out.println("<head><title>WSPRedirectHandler</title></head>");
            out.println("<body>");
            out.println("WSPRedirectHandler - Interaction Error");
            out.println(message);
            out.println("</body>");
            out.println("</html>");
        } else { //a wml client
            httpResponse.setContentType("text/vnd.wap.wml");
            PrintWriter out = httpResponse.getWriter();
            out.println("<wml>");
            out.println("<card>");
            out.println("<p>");
            out.println("WSPRediretHandler - encountered error");
            out.println("</p>");
            out.println("</card>");
            out.println("</wml>");
        }
    }

    private boolean checkReturnToHost(String messageID, String returnToURL) {
        boolean answer = false;
        String requestHost = InteractionManager.getInstance()
                .getRequestHost(messageID);
        URL url = null;
        if (requestHost != null) {
            try {
                url = new URL(returnToURL);
                String returnToHost = url.getHost();
                if (requestHost.equals(returnToHost)) {
                    answer = true;
                }
            } catch (MalformedURLException mfe) {
                    debug.error(
                            "WSPRedirecthandlerServlet.handleRequest():"
                            + "malformed "
                            + InteractionManager.RETURN_TO_URL
                            + "=" + returnToURL);
            }
        }

        String returnToHost = null;
        if (url != null) {
            returnToHost = url.getHost();
            answer = requestHost.equalsIgnoreCase(returnToHost);
        }

        //requestHost does not include domain under jdk1.3
        if ( (answer == false) 
                 && (returnToHost.indexOf(requestHost + ".") == 0)) { 
            answer =true;
        }

        if (debug.messageEnabled()) {
            debug.message(
                    "WSPRedirectHandlerServlet.checkReturnToHost():"
                    + " returning: "
                    + ":requestHost=" + requestHost
                    + ":returnToHost=" + returnToHost
                    + ":returnValue=" + answer);
        }

        return answer;
    }

    private boolean isWMLClient(HttpServletRequest httpRequest) {
        // TODO: need to find a way to detect client
        return false;
    }

}

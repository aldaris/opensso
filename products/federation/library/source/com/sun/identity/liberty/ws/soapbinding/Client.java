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
 * $Id: Client.java,v 1.2 2006-12-23 05:09:59 hengming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.liberty.ws.soapbinding; 

import com.sun.identity.shared.configuration.SystemPropertiesManager;

import com.sun.identity.liberty.ws.common.wsse.WSSEConstants;
import com.sun.identity.liberty.ws.security.SecurityUtils;
import com.sun.identity.saml.xmlsig.JKSKeyProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;

import java.security.cert.X509Certificate;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The <code>Client</code> class provides web service clients with a method to
 * send requests using SOAP connection to web service servers.
 * 
 * @supported.all.api
 */
public class Client {

    private static TransformerFactory tfactory =
                       TransformerFactory.newInstance();
    private static KeyManager[] kms = null;
    private static TrustManager[] tms = null;
    private static X509KeyManager defaultX509km = null;
    private static String defaultCertAlias = null;

    static {
        defaultCertAlias = SystemPropertiesManager.get(
            "com.sun.identity.liberty.ws.soap.certalias");
    }

    private Client() {}

    /**
     * Sends a request to a SOAP endpoint and returns the response. The server
     * only contains one servlet for different web services. So the SOAP
     * endpoint URL has format 'servlet_URL/key'
     *
     * @param req the request
     * @param connectTo the SOAP endpoint URL
     * @return a response from the SOAP endpoint
     * @throws SOAPBindingException if an error occurs while sending the
     *                                 message
     * @throws SOAPFaultException if the response is a SOAP Fault
     */
    public static Message sendRequest(Message req,String connectTo)
                           throws SOAPBindingException, SOAPFaultException {
        return sendRequest(req, connectTo, null, null);
    }

    /**
     * Sends a request to a SOAP endpoint and returns the response. The server
     * only contains one servlet for different web services. So the SOAP
     * endpoint URL has format 'servlet_URL/key'.
     *
     * @param req the request message.
     * @param connectTo the SOAP endpoint URL
     * @param certAlias the cert alias of a client certificate being used in
     *                  SSL
     * @return a response from the SOAP endpoint
     * @throws SOAPBindingException if an error occurs while sending the
     *                                 message
     * @throws SOAPFaultException if the response is a SOAP Fault
     */
    public static Message sendRequest(Message req,String connectTo,
           String certAlias) throws SOAPBindingException, SOAPFaultException {
        return sendRequest(req, connectTo, certAlias, null);
    }

    /**
     * Sends a request to a SOAP endpoint and returns the response. The server
     * only contains one servlet for different web services. So the SOAP
     * endpoint URL has format 'servlet_URL/key'.
     *
     * @param req the request message.
     * @param connectTo the SOAP endpoint URL
     * @param certAlias the cert alias of a client certificate
     * @param soapAction the SOAPAction header
     * @return a response from the SOAP endpoint
     * @throws SOAPFaultException if a SOAP Fault occurs
     * @throws SOAPBindingException if a error occurs while processing,
     *                                 sending or receiving Message
     */
    public static Message sendRequest(Message req,String connectTo,
                           String certAlias,String soapAction) 
                           throws SOAPBindingException, SOAPFaultException {
        URLConnection con = null;

        try {
            con = getConnection(connectTo, certAlias);
        } catch (Exception e) {
            Utils.debug.error("Client:sendRequest", e); 
            throw new SOAPBindingException(e.getMessage());
        }      

	if(soapAction == null || soapAction.length() == 0) {
	    soapAction = "";
	}

	con.setRequestProperty(SOAPBindingConstants.SOAP_ACTION_HEADER,
			       soapAction);


        Document doc = null;
        int securityProfileType = req.getSecurityProfileType();
        if (securityProfileType == Message.ANONYMOUS ||
            securityProfileType == Message.BEARER_TOKEN) {
            doc  = req.toDocument(true);
        } else {

            Element sigElem = SecurityUtils.signMessage(req);
            if (sigElem == null) {
                String msg = Utils.bundle.getString("cannotSignRequest");
                Utils.debug.error("Client.sendRequest: " + msg);
                throw new SOAPBindingException(msg);
            }
            doc = sigElem.getOwnerDocument();
        }
        if (Utils.debug.messageEnabled()) {
            Utils.debug.message("Client.sendRequest: signed request\n" + req);
        }

        OutputStream os = null;
        try {
            os = con.getOutputStream(); 
	    Transformer transformer = tfactory.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
	    transformer.transform(new DOMSource(doc.getDocumentElement()),
		new StreamResult(os));
        } catch (Exception e) {
            Utils.debug.error("Client:sendRequest", e); 
            throw new SOAPBindingException(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    Utils.debug.error("Client:sendRequest", e); 
                }
            }
        }

        Message resp = null;
        InputStream is = null;
        try {
            is = con.getInputStream();
            resp = new Message(is);
            if (resp.getSOAPFault() != null) {
                throw new SOAPFaultException(resp);
            }
            Utils.enforceProcessingRules(resp,
                             req.getCorrelationHeader().getMessageID(), false);
        } catch (IOException e) {
            Utils.debug.error("Client:sendRequest", e); 
            throw new SOAPBindingException(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    Utils.debug.error("Client:sendRequest", e); 
                }
            }
        }

        resp.setProtocol(con.getURL().getProtocol());
        if (resp.getSecurityProfileType() != Message.ANONYMOUS &&
            !SecurityUtils.verifyMessage(resp)) {

            String msg = Utils.bundle.getString("cannotVerifySignature");
            Utils.debug.error("Client.sendRequest: " + msg);
            throw new SOAPBindingException(msg);
        }
        return resp;
    }

    /**
     * Gets URLConnection associated with the endpoint. If it is a SSL
     * connection, the certAlias will be used to get the client certificate.
     *
     * @param endpoint the url of the SOAP receiver
     * @param certAlias the cert alias of a client certificate
     * @return a URLConnection object
     * @throws Exception if an error occurs while connecting to server
     */
    private static URLConnection getConnection(String endpoint,String certAlias) 
                                               throws Exception {
	URLConnection con = new URL(endpoint).openConnection();

        if (Utils.debug.messageEnabled()) {
            Utils.debug.message("Client.getConnection: con class = " +
                                con.getClass());
        }

        if (con instanceof HttpsURLConnection) {
            if (kms == null) {
                initializeJSSE();
            }
            if (certAlias != null) {
                kms[0] = new WSX509KeyManager(defaultX509km, certAlias);
            } else {
                kms[0] = new WSX509KeyManager(defaultX509km, defaultCertAlias);
            }

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kms, tms, null);
            HttpsURLConnection scon = (HttpsURLConnection) con;
            scon.setSSLSocketFactory(ctx.getSocketFactory());
        } else { 
            if (Utils.debug.warningEnabled()) {
                Utils.debug.warning("Client.getConnection: not instance of " +
                    "HttpsURLConnection, client cert not selected.");  
            }
        }

	con.setDoInput(true);
	con.setDoOutput(true);
	con.setRequestProperty("content-type", "text/xml");

        return con;
    }
    
    /**
     * Initializes JSSE enviroment.
     *
     * @throws Exception if an error occurs while initializing JSSE
     */
    private static void initializeJSSE() throws Exception {
        // put SunJSSE at fisrt place, so that JSSE will work
        java.security.Security.removeProvider("SunJSSE");
        java.security.Security.insertProviderAt(
                new com.sun.net.ssl.internal.ssl.Provider(), 1);
        
        JKSKeyProvider jkskp = new JKSKeyProvider();
        KeyStore ks = jkskp.getKeyStore();
        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
        kf.init(ks, jkskp.getPrivateKeyPass().toCharArray());
        
        kms = kf.getKeyManagers();
        defaultX509km = (X509KeyManager)kms[0];
        
        TrustManagerFactory tf = TrustManagerFactory.getInstance("SunX509");
        tf.init(ks);
        tms = tf.getTrustManagers();
    }
}

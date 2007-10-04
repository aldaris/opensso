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
 * $Id: LEP.java,v 1.1 2007-10-04 16:55:29 hengming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.soap.*;

import org.w3c.dom.*;

import com.sun.identity.federation.message.*;
import com.sun.identity.federation.services.*;

import com.sun.identity.liberty.ws.paos.*;
import com.sun.identity.liberty.ws.soapbinding.*;
import com.sun.identity.saml.common.SAMLConstants;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.ecp.ECPFactory;
import com.sun.identity.saml2.ecp.ECPRelayState;
import com.sun.identity.saml2.ecp.ECPRequest;
import com.sun.identity.saml2.ecp.ECPResponse;
import com.sun.identity.saml2.protocol.AuthnRequest;
import com.sun.identity.saml2.protocol.IDPEntry;
import com.sun.identity.saml2.protocol.IDPList;
import com.sun.identity.saml2.protocol.ProtocolFactory;
import com.sun.identity.saml2.protocol.Response;
import com.sun.identity.shared.xml.XMLUtils;

public class LEP extends Thread {
    private static Hashtable cookieCache = null;
    private static Hashtable ecpCache = null;
    private static String IDPS_FILE = "config/idps.properties";
    private static String HTTP_HEADERS_FILE = "config/httpHeaders.properties";
    private static Properties idpsConfig = new Properties();
    private static Properties httpHeadersConfig = new Properties();
    private Socket clientSocket = null;
    private String PROVIDER_URL = "/amserver/ssosoap";
    private String VND_PAOS = "application/vnd.paos+xml";

    public LEP (Socket sock) {
        clientSocket = sock;
        if(cookieCache == null) {
            cookieCache = new Hashtable();
        }
        if(ecpCache == null) {
            ecpCache = new Hashtable();
        }
        try {
            idpsConfig.load(new FileInputStream(IDPS_FILE));
            httpHeadersConfig.load(new FileInputStream(HTTP_HEADERS_FILE));
        } catch(IOException ie) {
            ie.printStackTrace();
            System.out.println("Cannot read configuration file:" +
                ie.getMessage());        
        }
    }

    public void run () {
        HttpURLConnection conn = null;
        BufferedOutputStream outAgent = null;
        BufferedOutputStream outServer = null;
        BufferedInputStream inAgent = null;
        BufferedInputStream inServer = null;
        try {
            HttpRequestHdr request = new HttpRequestHdr();
            inAgent = new BufferedInputStream(clientSocket.getInputStream());
            request.parse(inAgent);
            URL url = new URL(request.getURL());
            System.out.println("METHOD:" + request.getMethod() + " URL:" + url);
            
            int b1 = 0;
            outAgent = new BufferedOutputStream(clientSocket.getOutputStream());

            request.addHeader("Accept", ECPUtils.PAOS_MIME_TYPE_VAL);
            request.addHeader(ECPUtils.PAOS_HEADER_TYPE,
                ECPUtils.PAOS_HEADER_VAL);
            
            conn = openConnection(url);
            conn.setFollowRedirects(false);
            conn.setInstanceFollowRedirects(false);
            Hashtable h = request.getHeaders();
            for(Enumeration e = h.keys(); e.hasMoreElements() ;) {
                String k = (String) e.nextElement();
                Set vals = (Set) h.get(k);
                if ((vals != null) && (!vals.isEmpty())) {
                    for(Iterator iter = vals.iterator(); iter.hasNext();) {
                        String val = (String)iter.next();
                        System.out.println("Request Header = " + k + ": " +
                            val);
                        conn.addRequestProperty(k, val);
                    }
                }
            }

            if(request.getMethod().equals("POST")) {
                conn.setDoOutput(true);
                conn.connect();
                outServer = new BufferedOutputStream(conn.getOutputStream());
                writeContent(outServer, inAgent, request.getContentLength());
            } else {
                conn.setDoOutput(false);
                conn.connect();
            }

            inServer = new BufferedInputStream(conn.getInputStream());
            sendResponse(inServer, outAgent, conn);
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (outAgent != null) {
                try {
                    outAgent.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (outServer != null) {
                try {
                    outServer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (inAgent != null) {
                try {
                    inAgent.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (inServer != null) {
                try {
                    inServer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    private String findIDPEndpoint(String spEntityID, IDPList idpList)
        throws Exception {

        String idpEndpoint = null;
        idpEndpoint = (String)idpsConfig.get(spEntityID);
        if (idpEndpoint != null) {
            System.out.print("Got idp endpoint from idps.properties = " +
                idpEndpoint);
            return idpEndpoint;
        }

        if (idpList != null) {
            List idps = idpList.getIDPEntries();
            if ((idps != null) && (!idps.isEmpty())) {
                for(Iterator iter = idps.iterator(); iter.hasNext();) {
                    IDPEntry idpEntry = (IDPEntry)iter.next();
                    idpEndpoint = idpEntry.getLoc();
                    if (idpEndpoint != null) {
                        System.out.print("Got idp endpoint from ecp Request = "
                            + idpEndpoint);
                        return idpEndpoint;
                    }
                }
            }
        }

        throw new Exception("Unable to determine idp endpoint.");
    }

    /**
     * Forward the <AuthnRequest> SOAP message received from the SP to the
     * IDP.
     */
    private SOAPMessage sendAuthnRequestToIDP (String idpEndpoint,
        OutputStream out, SOAPMessage msg) throws Exception {

        SOAPMessage response = null;
        // remove SOAP headers received from SP before forwarding.
        SOAPHeader header = msg.getSOAPHeader();
        header.detachNode();

        System.out.println("Sending the following MSG to provider:" + idpEndpoint);
        msg.writeTo(System.out);
        response = postSOAPMsgToProvider(idpEndpoint, msg, out);
        if(response != null) {
            System.out.println("Response from IDP:" );
            response.writeTo(System.out);
        } else {
            System.out.println("RESPONSE FROM IDP IS NON SOAP");
        }
            
        return response;
    }

    /**
     * This method posts a SOAP Authn message to the IDP using HttpURLConnection
     * and processes either a SOAP response or a non-SOAP response in case
     * the IDP requires a login.
     */
    public SOAPMessage postSOAPMsgToProvider(String provider, SOAPMessage msg, 
        OutputStream out) throws IOException, SOAPException {
        SOAPMessage response = null;

        URL url = null;
        try {
            url = new URL(provider);
        } catch (MalformedURLException me) {
            throw new IOException(me.getMessage());
        }

        HttpURLConnection conn = openConnection(url);
        conn.setFollowRedirects(false);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("Content-type", "text/xml");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.writeTo(baos);
        StringBuffer postBody  = new StringBuffer(baos.toString());

        conn.setDoOutput(true);
        conn.connect();
        PrintWriter writer = new PrintWriter(conn.getOutputStream());
        writer.print(postBody);
        writer.close();
        InputStream is = conn.getInputStream();
        String contentType = conn.getContentType();
        if(contentType.startsWith("text/xml"))  {
            // SOAP message response
            MessageFactory mf = MessageFactory.newInstance();
            MimeHeaders mimeHdrs = new MimeHeaders();
            mimeHdrs.addHeader("Content-type", "text/xml");
            response = mf.createMessage(mimeHdrs, is);
        } else {
            // Non-SOAP message response
            System.out.println("CONTENT TYPE:" + contentType);
            forwardResponse(is, conn, out);
        }
        return response;
    }

    
    private void sendResponse(InputStream in, OutputStream out, 
        HttpURLConnection conn) throws Exception {

        String contentType = conn.getContentType();
        System.out.println("Response content type = " + contentType);
        if (contentType == null) {
            forwardResponse(in, conn, out);
        } else if(contentType.startsWith("text/xml")) {
            try {
                String acURL = null;
                Message message = new Message(in);
                List soapHeaders = message.getOtherSOAPHeaders();
                if (soapHeaders != null) {
                    for(Iterator iter=soapHeaders.iterator();iter.hasNext();) {
                        Element e = (Element)iter.next();
                        String tagName = e.getLocalName();
                        String namespace = e.getNamespaceURI();
                        System.out.println("TAG NAME:" + tagName);
                        System.out.println("NAMESPACE:" + namespace);
                        if (ECPUtils.ECPNAMESPACE.equals(namespace) && 
                            tagName.equals("Response")) {


                            ECPResponse ecpResp =
                                ECPFactory.getInstance().createECPResponse(e);
                            acURL = ecpResp.getAssertionConsumerServiceURL();
                            break;
                        }
                    }
                }

                List soapBodies = message.getBodies();
                if ((soapBodies == null) || (soapBodies.isEmpty())) {
                    throw new Exception("SAML Response not found.");
                }
                Element elem = (Element)soapBodies.get(0);

                Response samlResp = 
                    ProtocolFactory.getInstance().createResponse(elem);
                
                String inRespTo = samlResp.getInResponseTo();
                CacheEntry ce = (CacheEntry)ecpCache.remove(inRespTo);
                if (ce == null) {
                    throw new Exception("SAML Response ID doesn't match any " +
                        "AuthnRequest ID.");
                }


                SOAPMessage resp = postToSP(acURL, soapBodies,
                    ce.getPAOSRequest(), ce.getECPRelayState(), out);
                if(resp != null) {
                    System.out.println("GOT SOAP RESPONSE BACK");
                }
            } catch(Exception se) {
                se.printStackTrace();
                throw new Exception(se.getMessage());
            }

        } else if(contentType.startsWith(VND_PAOS)) {
            try {
                String authnMsgId = null;
                String msgId = null;
                String acURL = null;
                AuthnRequest ar = null;
                PAOSRequest pr = null;
                ECPRelayState ers = null;

                SOAPMessage m = readSOAPMessageFromStream(in);
                SOAPHeader hdrs = m.getSOAPHeader();

                SOAPBody body = m.getSOAPBody();
                Iterator it = body.getChildElements();
                while(it.hasNext()) {
                    SOAPElement se = (SOAPElement) it.next();
                    Name n = se.getElementName();
                    String localName = n.getLocalName();
                    System.out.println("LOCAL NAME:" + localName);
                    if(localName.equals("AuthnRequest")) {

                        ar = ProtocolFactory.getInstance()
                            .createAuthnRequest(se);
                        authnMsgId = ar.getID();
                        System.out.println("AUTHN MESSAGE ID:" + authnMsgId);
                    }
                }

                if (ar == null) {
                    throw new Exception("AuthnRequest not found.");
                }

                Iterator li = hdrs.examineAllHeaderElements();

                IDPList idpList = null;
                String spEntityID = null;
                while(li.hasNext()) {
                    SOAPHeaderElement e = (SOAPHeaderElement) li.next();
                    String tagName = e.getLocalName();
                    String namespace = e.getNamespaceURI();
                    if(namespace.equals(ECPUtils.PAOSNAMESPACE) && 
                            tagName.equals("Request")) {
                        pr = new PAOSRequest(e);
                        msgId = pr.getMessageID();
                        System.out.println("PAOSRequest:" +
                            pr.toXMLString(true, true));

                    } else if(namespace.equals(ECPUtils.ECPNAMESPACE) && 
                                tagName.equals("Request")) {
                        ECPRequest ecpReq =
                            ECPFactory.getInstance().createECPRequest(e);
                        idpList = ecpReq.getIDPList();
                        spEntityID = ecpReq.getIssuer().getValue();
                        System.out.println("ECPRequest:" +
                            ecpReq.toXMLString(true, true));
                    } else if(namespace.equals(ECPUtils.ECPNAMESPACE) && 
                            tagName.equals("RelayState")) {
                        ers = ECPFactory.getInstance().createECPRelayState(e);
                        System.out.println("ECPRelayState:" +
                            ers.toXMLString(true, true));
                    }
                }

                System.out.println("SP Provider:" + spEntityID);
                System.out.println("IDP Providers from IDPList:" + idpList);
                String idpEndpoint = findIDPEndpoint(spEntityID, idpList);
                SOAPMessage response = sendAuthnRequestToIDP(idpEndpoint, out,
                    m);
                if(response == null) {
                    CacheEntry ce = new CacheEntry(pr, ers);
                    ecpCache.put(authnMsgId, ce);
                    System.out.println("Not a SOAP response. Add " +
                        "AuthnRequest ID " + authnMsgId + " to cache.");
                    return;
                } else {
                    System.out.println("SOAP RESPONSE:");
                    response.writeTo(System.out);
                }

                Message message = new Message(response);
                List soapHeaders = message.getOtherSOAPHeaders();
                if (soapHeaders != null) {
                    for(Iterator iter=soapHeaders.iterator();iter.hasNext();) {
                        Element e = (Element) iter.next();
                        String tagName = e.getLocalName();
                        String namespace = e.getNamespaceURI();
                        if (namespace.equals(ECPUtils.ECPNAMESPACE) && 
                            tagName.equals("Response")) {

                            ECPResponse ecpResp =
                                ECPFactory.getInstance().createECPResponse(e);
                            acURL = ecpResp.getAssertionConsumerServiceURL();
                        }
                    }
                }
                List soapBodies = message.getBodies();
                SOAPMessage respMsg = postToSP(acURL, soapBodies, pr, ers,
                    out);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            // Plain HTML response. Send back to browser.
            forwardResponse(in, conn, out);
        }
    }

    /**
     * Post a SOAP message to the service provider.
     */
    private SOAPMessage postToSP(String acURL, List soapBodies,
        PAOSRequest pr, ECPRelayState ers, OutputStream out) throws Exception {
        // Send PAOS response header in message to SP.
        SOAPMessage respMsg = null;
        if (acURL != null) {
            System.out.println ("AssertionConsumerURL: " + acURL);
            PAOSResponse paosResp = new PAOSResponse(pr.getMessageID(),
                Boolean.TRUE, ECPUtils.ACTOR);

            String header = paosResp.toXMLString(true, true) + 
                ((ers == null) ? "" : ers.toXMLString(true, true));
            
            StringBuffer bodySB = new StringBuffer();
            for(Iterator iter = soapBodies.iterator(); iter.hasNext();) {
                Element childElem = (Element)iter.next();
                bodySB.append(XMLUtils.print(childElem));
            }

            System.out.println("Sending following msg to SP");
            SOAPMessage msg = createSOAPMessage(header, bodySB.toString());
            msg.writeTo(System.out);
            respMsg = postSOAPMsgToProvider(acURL, msg, out);
        } else {
            System.out.println("No SP info found");
        }
        if(respMsg != null) {
            System.out.println("RESP FROM SP");
            respMsg.writeTo(System.out);
        }
        return respMsg;
    }

    /**
     * Read the contents of the SOAP message from the specified input stream.
     */
    private SOAPMessage readSOAPMessageFromStream(InputStream is) 
        throws SOAPException, IOException {
        MessageFactory mf = MessageFactory.newInstance();
        MimeHeaders mimeHdrs = new MimeHeaders();
        mimeHdrs.addHeader("Content-type", "text/xml");
        SOAPMessage msg = mf.createMessage(mimeHdrs, is);
        return msg;
    }

    /**
     * Read the contents of the response from the URL connection
     * and return to browser.
     */
    private void forwardResponse(InputStream in, HttpURLConnection conn,
        OutputStream out) throws IOException {

        Map headerFields = conn.getHeaderFields();
        Set headerKeys = headerFields.keySet();
        Iterator itr = headerKeys.iterator();
        StringBuffer headersSB = new StringBuffer();
        headersSB.append("HTTP/1.1 ").
            append(conn.getResponseCode()).
            append(" ").
            append(conn.getResponseMessage()).
            append("\r\n");

        while(itr.hasNext()) {
            String hdr = (String) itr.next();
            if(hdr != null && !hdr.equalsIgnoreCase("Transfer-encoding")) {
                List vals = (List)headerFields.get(hdr);
                for(int i=0; i<vals.size(); i++) {
                    String val = (String)vals.get(i);
                    headersSB.append(hdr + ": " + val).append("\r\n");
                    System.out.println("Header = " + hdr + ": " + val);
                }
            }
        }
        headersSB.append("\r\n");
        out.write(headersSB.toString().getBytes());

        writeContent(out, in, conn.getContentLength());
    }

    /**
     * Add the cookie to the cache to used later by the ECP.
     */
    private void addCookieToCache(String cookieHeader) {
        if(cookieHeader != null) {
            int nameIdx = cookieHeader.indexOf("=");
            if(nameIdx != -1){
                String cookieKey = cookieHeader.substring(0, nameIdx);
                String cookieVal = cookieHeader.substring(nameIdx+1, 
                                    cookieHeader.length());
                int valIdx = cookieVal.indexOf(";");
                if(valIdx != -1) {
                    cookieVal = cookieVal.substring(0, valIdx);
                }
                System.out.println("COOKIE:" +cookieKey + " VAL=" +cookieVal);
                cookieCache.put(cookieKey, cookieVal);
            }
        } 
    }

    private HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Add addtional HTTP headers, for example, X-MSISDN header
        for(Enumeration e = httpHeadersConfig.keys(); e.hasMoreElements() ;) {
            String key = (String) e.nextElement();
            String val = (String) httpHeadersConfig.get(key);
            System.out.println("Setting HTTP request header:" + key + ":" +
                val);
            conn.setRequestProperty(key, val);
        }
        return conn;
    }

    private static int writeContent(OutputStream out, InputStream in,
        int contentLength) throws IOException {

        byte content[] = new byte[2048];

        if (contentLength != -1) {
            int read = 0, totalRead = 0;
            int left;
            while (totalRead < contentLength) {
                left = contentLength - totalRead;
                read = in.read(content, 0,
                    left < content.length ? left : content.length);
                if (read == -1) {
                    // We need to close connection !!
                    break;
                } else {
                    if (read > 0) {
                        totalRead += read;
                        out.write(content, 0, read);
                        out.flush();
                    }
                }
            }

            return totalRead;
        } else {
            int numbytes;
            int totalRead = 0;

            while (true) {
                numbytes = in.read(content);
                if (numbytes == -1) {
                    break;
                }

                totalRead += numbytes;

                out.write(content, 0, numbytes);
                out.flush();
            }//while loop
            return totalRead;
        }//if/else
    }

    /**
     * Creates <code>SOAPMessage</code> with the input XML String
     * as message header and body.
     * @param header XML string to be put into <code>SOAPMessage</code> header.
     * @param body XML string to be put into <code>SOAPMessage</code> body.
     * @return newly created <code>SOAPMessage</code>.
     * @exception SOAPException if it cannot create the
     *     <code>SOAPMessage</code>.
     */
    private static SOAPMessage createSOAPMessage(String header, String body)
        throws Exception {

        SOAPMessage msg = null;

        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", "text/xml");
            
        StringBuffer sb = new StringBuffer(500);
        sb.append("<").append(SAMLConstants.SOAP_ENV_PREFIX)
          .append(":Envelope").append(SAMLConstants.SPACE)
          .append("xmlns:").append(SAMLConstants.SOAP_ENV_PREFIX)
          .append("=\"").append(SAMLConstants.SOAP_URI).append("\">");
        if (header != null) {
            sb.append("<")
              .append(SAMLConstants.SOAP_ENV_PREFIX).append(":Header>")
              .append(header)
              .append(SAMLConstants.START_END_ELEMENT)
              .append(SAMLConstants.SOAP_ENV_PREFIX)
              .append(":Header>");
        }
        if (body != null) {
            sb.append("<")
              .append(SAMLConstants.SOAP_ENV_PREFIX).append(":Body>")
              .append(body)
              .append(SAMLConstants.START_END_ELEMENT)
              .append(SAMLConstants.SOAP_ENV_PREFIX)
              .append(":Body>");
        }
        sb.append(SAMLConstants.START_END_ELEMENT)
          .append(SAMLConstants.SOAP_ENV_PREFIX)
          .append(":Envelope>").append(SAMLConstants.NL);
            
        return MessageFactory.newInstance().createMessage(mimeHeaders,
            new ByteArrayInputStream(sb.toString().getBytes(
            SAML2Constants.DEFAULT_ENCODING)));
    }
    
    class CacheEntry {
        private PAOSRequest pr = null;
        private ECPRelayState ers = null;

        CacheEntry(PAOSRequest pr, ECPRelayState ers) {
            this.pr = pr;
            this.ers = ers;
        }

        PAOSRequest getPAOSRequest() {
            return pr;
        }

        ECPRelayState getECPRelayState() {
            return ers;
        }
    }
}


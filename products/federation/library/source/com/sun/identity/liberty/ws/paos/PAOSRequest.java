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
 * $Id: PAOSRequest.java,v 1.1 2006-10-30 23:15:14 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.liberty.ws.paos; 

import com.sun.identity.shared.debug.Debug;

import com.sun.identity.liberty.ws.idpp.common.IDPPUtils;
import com.sun.identity.liberty.ws.idpp.jaxb.QueryElement;
import com.sun.identity.liberty.ws.soapbinding.Message;
import com.sun.identity.liberty.ws.soapbinding.ProviderHeader;
import com.sun.identity.liberty.ws.soapbinding.SOAPBindingException;
import com.sun.identity.liberty.ws.soapbinding.Utils;

import com.sun.identity.liberty.ws.paos.jaxb.ObjectFactory;
import com.sun.identity.liberty.ws.paos.jaxb.RequestElement;
import com.sun.identity.saml.common.SAMLUtils;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;


/**
 * The <code>PAOSRequest</code> class is used by a web application on
 * HTTP server side to construct a <code>PAOS</code> request message and send
 * it via an HTTP response to the user agent side.
 *
 * This class can be used in conjunction with either the Personal Profile
 * Client API or other service APIs. These two different cases would use
 * different constructors, and different get methods for original queries.
 *
 * @supported.all.api
 */
public class PAOSRequest {

    static final String ACTOR="http://schemas.xmlsoap.org/soap/actor/next";
    static final String PAOS_CONTENT_TYPE="application/vnd.paos+xml";
    static final String RESOURCE_ID="urn:liberty:isf:implied-resource";
    static Hashtable reqTable = new Hashtable();
    static Debug debug = Debug.getInstance("amPAOS");

    private String origURL = null;
    private String[] qItems = null;
    private List qBodies = null;
    private String messageID = null;
    private String msgStr = null;
    
    /**
     * This constructor composes the request message based on the input
     * parameters. It is to be used in conjunction with service APIs other
     * than the Personal Profile Client API.
     *
     * @param originalURL the URL for the above mentioned servlet.
     * @param service the URI of the service from which this <code>PAOS</code>
     *        request is requesting data from, it should be already published in
     *        the HTTP request header <code>"PAOS:"/code>.
     * @param responseConsumerURL the URL where the <code>PAOS</code> response
     *        should be sent and consumed, this URL should be used by the
     *        <code>PAOS</code> server side.
     * @param soapBodies a list of <code>org.w3c.dom.Element</code> objects
     *                   representing the queries for data, they should be
     *                   filled in by the underlying service API (other than
     *                   the Personal Profile Client API).
     * @throws PAOSException if there is any problem composing the request
     *                       message
     */
    public PAOSRequest(String originalURL,String service,
                       String responseConsumerURL,List soapBodies)
	               throws PAOSException {
	try {
	    compose(originalURL,service,responseConsumerURL,soapBodies);
	} catch (javax.xml.bind.JAXBException jaxbe) {
	    throw new PAOSException("Unable to compose PAOS request message. " +
				    "More details: "+jaxbe.getMessage());
	}
    }

    /**
     * This constructor composes the request message based on the input
     * parameters. It is to be used in conjunction with the Personal Profile
     * Client API.
     *
     * @param originalURL the URL for the above mentioned servlet.
     * @param service the URI of the service from which this <code>PAOS</code>
     *        request is requesting data from, it should be already published in
     *        the HTTP request header <code>"PAOS:"</code>.
     * @param responseConsumerURL the URL where the <code>PAOS</code> response
     *        should be sent and consumed, this URL should be used by the
     *        <code>PAOS</code> server side.
     * @param queryItems an array of query expressions understood by the 
     *                   Personal Profile Client API, the API would use them to 
     *                   construct soap body
     * @throws PAOSException if there is any problem composing the request
     *                       message
     */
    public PAOSRequest(String originalURL,String service,
            String responseConsumerURL,String[] queryItems)
            throws PAOSException {
        qItems = queryItems;
        
        int n = queryItems.length;
        
        ArrayList queryExpressions = new ArrayList(n);
        
        for (int i=0; i<n; i++) {
            queryExpressions.add(queryItems[i]);
        }
        qBodies = new ArrayList(1);
        QueryElement qe = null;
        try {
            qe = IDPPUtils.createQueryElement(queryExpressions,
                    RESOURCE_ID,
                    false);
        } catch (Exception e) {
            debug.error(
                    "PAOSRequest.PAOSRequest: Unable to create QueryElement.",
                    e);
            throw new PAOSException(
                    "Unable to compose PAOS request message. More details: "+
                    e.getMessage());
        }
        try {
            qBodies.add(Utils.convertJAXBToElement(qe));
            compose(originalURL,service,responseConsumerURL,null);
        } catch (javax.xml.bind.JAXBException jaxbe) {
            throw new PAOSException(
                    "Unable to compose PAOS request message. More details: "+
                    jaxbe.getMessage());
        }
    }

    private void compose(String originalURL,String service,
			 String responseConsumerURL,List soapBodies)
	                 throws javax.xml.bind.JAXBException {
	origURL = originalURL;
	if (soapBodies != null) {
	    qBodies = soapBodies;
	}
	com.sun.identity.liberty.ws.paos.jaxb.ObjectFactory of = new 
	  com.sun.identity.liberty.ws.paos.jaxb.ObjectFactory();
	RequestElement req = null;

	try {
	    req = of.createRequestElement();
	} catch (javax.xml.bind.JAXBException jaxbe) {
	    debug.error(
		"PAOSRequest:compose: Unable to create RequestElement.",
		jaxbe);
	    throw jaxbe;
	}

	req.setService(service);
	req.setMustUnderstand(true);
	req.setResponseConsumerURL(responseConsumerURL);
	req.setActor(ACTOR);

        Message msg = null;
        try {
            String providerID = "tmp";
	    msg = new Message(new ProviderHeader(providerID));
        } catch (SOAPBindingException sbe) {
            debug.message("Error creating Provider Header: " , sbe);
        }

	List headerList = new ArrayList(1);
	headerList.add(req);
        headerList = Utils.convertJAXBToElement(headerList);
	msg.setOtherSOAPHeaders(headerList, null); 
	msg.setSOAPBodies(qBodies);
	
	msgStr = msg.toString();
	messageID = msg.getCorrelationHeader().getMessageID();
    }

    /**
     * Sends the <code>PAOS</code> request message via an HTTP response to the
     * User Agent side.
     *
     * @param res the HTTP response through which the <code>PAOS</code> request
     *        message is sent to the User Agent side.
     * @param closeOutputStream whether to close the output steam in the first
     *        parameter. or to leave it open for the caller to close it.
     * @exception java.io.IOException if there are IO problems obtaining the
     *            output stream from the first parameter
     */
    public void send(HttpServletResponse res, boolean closeOutputStream)
	             throws java.io.IOException {
	
	res.setContentType(PAOS_CONTENT_TYPE);
	res.setContentLength(1+msgStr.length());
	
	OutputStream os = null;
	try {
	    os = res.getOutputStream();
	} catch (java.io.IOException ioe) {
	    debug.error(
		"PAOSRequest:send: Unable to get output stream from "
                + "HttpServletResponse.", ioe);
	    throw ioe;
	}
	
	PrintWriter out = new PrintWriter(os);
	out.println(msgStr);
	
	if (closeOutputStream) { 
	    out.close();
	}
	reqTable.put((Object)messageID, (Object)this);
    }
    
    /**
     * Returns the message ID value used inside <code>PAOS</code> request.
     * It is useful for checking the <code>refMessageID</code> in
     * <code>PAOS</code> response against the original message ID.
     *
     * @return the message ID value used inside <code>PAOS</code> request.
     */
    public String getMessageID() {
        return messageID;
    }
    
    /**
     * Returns an array of the original query expression strings.
     * This method is relevant only in the case of Personal Profile service.
     * It is useful for correlating the query responses to the original queries.
     *
     * @return an array of query expression strings.
     */
    public String[] getQueryItems() {
        return qItems;
    }
    
    /**
     * Returns a list of the original query <code>org.w3c.dom.Element</code>
     * objects. This method is more relevant in the non-Personal Profile case.
     * It is useful for correlating the query responses to the original queries.
     *
     * @return a list of <code>org.w3c.dom.Element</code> objects representing
     *         the original queries.
     */
    public List getQueryObjects() {
        return qBodies;
    }
    
    /**
     * Returns the original URL from which the <code>PAOS</code> request is
     * made. The URL may be useful in processing the corresponding
     * <code>PAOS</code> response.
     *
     * @return the URL string representing the web application making the
     *         <code>PAOS</code> request
     */
    public String getOriginalURL() {
        return origURL;
    }

    /**
     * Returns the content string which resides inside an HTTP response,
     * and represents the <code>PAOS</code> request in the form of a SOAP
     * message.
     *
     * @return the string representing the <code>PAOS</code> request in the
     *         form of a SOAP message
     */
    public String toString() {
        return msgStr;
    }
}

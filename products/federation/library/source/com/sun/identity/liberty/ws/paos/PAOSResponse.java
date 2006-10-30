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
 * $Id: PAOSResponse.java,v 1.1 2006-10-30 23:15:14 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.liberty.ws.paos;

import com.sun.identity.liberty.ws.dst.DSTException;
import com.sun.identity.liberty.ws.dst.DSTData;
import com.sun.identity.liberty.ws.dst.DSTQueryResponse;

import com.sun.identity.liberty.ws.soapbinding.Message;
import com.sun.identity.liberty.ws.soapbinding.SOAPFault;
import com.sun.identity.liberty.ws.soapbinding.SOAPBindingException;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.bind.JAXBException;

import org.w3c.dom.Element;

/**
 * The <code>PAOSResponse</code> class is used by a web application on
 * HTTP server side to receive and parse a <code>PAOS</code> response via an
 * HTTP request from the user agent side.
 *
 * From this class, the original <code>PAOSRequest</code> object could obtained
 * to correlate with this response.
 *
 * This class can be used in conjunction with either the Personal Profile
 * Client API or other service APIs. These two different cases would use
 * different get methods for query responses.
 *
 * @supported.all.api
 */
public class PAOSResponse {
    
    private String refToMessageID = null;
    private List bodies = null;
    private PAOSRequest paosReq = null;
    private String msgStr = null;
    
    /**
     * This constructor parses the HTTP request to get <code>PAOS</code>
     * response.
     *
     * @param req the HTTP request where the <code>PAOS</code> response is
     *        residing.
     * @exception PAOSException if there are errors during parsing
     *            <code>PAOS</code> response. The errors could include the case
     *            where an <code>PAOS</code> message response is not conformant
     *            to <code>PAOS</code> protocols like
     *            <code>refToMessageID</code> attribute missing, etc.
     * @exception IOException if there are IO problems obtaining the input
     *                        stream from the HTTP request
     */
    public PAOSResponse(HttpServletRequest req)
    throws PAOSException, java.io.IOException {
        
        String contentType = req.getContentType();
        
        if (!contentType.equals(PAOSRequest.PAOS_CONTENT_TYPE)) {
            PAOSRequest.debug.error(
                    "PAOSResponse:PAOSResponse: Wrong content type: " +
                    contentType);
            throw new PAOSException("Wrong content type in HTTP request: " +
                    contentType);
        }
        
        InputStream is = null;
        
        try {
            is = req.getInputStream();
        } catch (java.io.IOException ioe) {
            PAOSRequest.debug.error(
                    "Unable to get input stream from HttpServletRequest", ioe);
            throw ioe;
        }
        
        Message msg = null;
        
        try {
            msg = new Message(is);
        } catch (SOAPBindingException sbe) {
            PAOSRequest.debug.error(
                    "PAOSResponse:PAOSResponse: SOAP binding exception in "+
                    "message construction from input stream.", sbe);
            throw new PAOSException(
                    "Unable to parse the input stream content.");
        }
        
        SOAPFault sf = msg.getSOAPFault();
        if (sf != null) {
            String faultString = sf.getFaultString();
            PAOSRequest.debug.error(
                    "PAOSResponse:PAOSResponse: SOAP Fault in response: " +
                    faultString);
            throw new PAOSException("Got fault in response: " + faultString);
        }
        
        refToMessageID = msg.getCorrelationHeader().getRefToMessageID();
        if (refToMessageID == null) {
            PAOSRequest.debug.error(
                    "PAOSResponse:PAOSResponse: No refToMessageID.");
            throw new PAOSException(
                    "Unrecognized PAOS response message.");
        }
        if (! PAOSRequest.reqTable.containsKey(refToMessageID)) {
            PAOSRequest.debug.error(
                    "PAOSResponse:PAOSResponse: refToMessageID="+
                    refToMessageID+"is not recognized.");
            throw new PAOSException(
                    "Unrecognized PAOS response message.");
        } else {
            if (PAOSRequest.debug.messageEnabled()) {
                PAOSRequest.debug.message(
                        "PAOSResponse:PAOSResponse: refToMessageID=" +
                        refToMessageID + " is recognized.");
            }
        }
        paosReq = (PAOSRequest)(PAOSRequest.reqTable.remove(refToMessageID));
        
        bodies = msg.getBodies();
        msgStr = msg.toString();
        
        // looks like inside Message class, XMLUtils.toDOMDocument(inputStream)
        // already closed the input stream, so don't close it again here
    }
    
    /**
     * Returns a list of <code>DSTData</code> objects representing query
     * responses. This method is relevant only in the case of the Personal
     * Profile service.
     *
     * @return a list of <code>DSTData</code> objects representing query
     *         responses.
     * @throws PAOSException if there are errors during parsing Personal
     *                       Profile Query response.
     */
    public List getPPResponse() throws PAOSException {
        if (bodies == null || bodies.size() == 0) {
            return null;
        }
        if (PAOSRequest.debug.messageEnabled()) {
            PAOSRequest.debug.message(
                    "PAOSResponse:getPPResponse: SOAP bodies size = " +
                    bodies.size());
        }
        org.w3c.dom.Element qre = (org.w3c.dom.Element)(bodies.get(0));
        DSTQueryResponse dstQr = null;
        try {
            dstQr = new DSTQueryResponse(qre);
        } catch (DSTException de) {
            PAOSRequest.debug.error(
                    "PAOSResponse:getPPResponse: Error while parsing "+
                    "query response.", de);
            throw new PAOSException("Error while parsing query response: " +
                    de.getMessage());
        }
        return dstQr.getData();
    }
    
    /**
     * Return a String representation of the query response from the Personal
     * Profile service. This is just a convenient method on top of the method
     * <code>getPPResponse()</code>.
     *
     * @return a String representing the query response from the Personal
     *         Profile service.
     * @exception PAOSException if there are errors during parsing Personal
     *                          Profile Query response.
     */
    public String getPPResponseStr() throws PAOSException {
        
        String retStr = "";
        DSTData dstData = null;
        
        List dstDataList = getPPResponse();
        
        Iterator iter = dstDataList.iterator();
        while (iter.hasNext()) {
            dstData = (DSTData)iter.next();
            retStr += dstData.toString(true,true) + "\n";
        }
        return retStr;
    }
    
    
    /**
     * Returns a list of <code>org.w3c.dom.Element</code> objects
     * representing query responses. This method is more relevant
     * in the case of non-Personal Profile service (but can be used
     * for in the case of Personal Profile service).
     *
     * @return a list of <code>org.w3c.dom.Element</code> objects
     *         representing query responses.
     */
    public List getResponse() {
        return bodies;
    }
    
    /**
     * Returns the original <code>PAOSRequest</code> object which corresponds
     * to this <code>PAOSResponse</code> object. This is useful for correlation
     * between <code>PAOS</code> requests and responses.
     *
     * @return the original <code>PAOS</code> request object.
     */
    public PAOSRequest getOriginalPAOSRequest() {
        return paosReq;
    }
    
    /**
     * Returns the content string which resides inside an HTTP request,
     * and represents the <code>PAOS</code> response in the form of a SOAP
     * message.
     *
     * @return the string representing the <code>PAOS</code> response in the
     *         form of a SOAP message
     */
    public String toString() {
        return msgStr;
    }
}






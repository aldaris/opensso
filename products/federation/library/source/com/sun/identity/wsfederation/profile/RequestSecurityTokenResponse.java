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
 * $Id: RequestSecurityTokenResponse.java,v 1.1 2007-06-21 23:01:39 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.profile;

import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.saml.assertion.Assertion;
import com.sun.identity.saml.common.SAMLConstants;
import com.sun.identity.saml.common.SAMLException;
import com.sun.identity.saml.common.SAMLUtils;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wsfederation.common.WSFederationConstants;

import com.sun.identity.wsfederation.common.WSFederationRequesterException;
import com.sun.identity.wsfederation.common.WSFederationException;
import com.sun.identity.wsfederation.common.WSFederationUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
public class RequestSecurityTokenResponse {
    private static Debug debug = WSFederationUtils.debug;
    
    protected boolean	signed		= false;
    protected boolean	valid		= true;
    protected String	recipient	= null;
    protected boolean validationDone    = true;
    
    protected String	xmlString	= null;
    protected String appliesTo = null; 
    protected String issuer = null; 
    protected List<Assertion> assertions = Collections.EMPTY_LIST;
    
    /** Creates a new instance of RequestSecurityTokenResponse */
    public RequestSecurityTokenResponse() {
    }
    
    /**
     * Returns RequestSecurityTokenResponse object based on the XML document 
     * received from server. This method is used primarily at the client side. 
     * The schema of the XML document is describe above.
     *
     * @param xml The RequestSecurityTokenResponse XML document String.
     * @return RequestSecurityTokenResponse object based on the XML document 
     * received from server.
     * @exception WSFederationException if XML parsing failed
     */
    public static RequestSecurityTokenResponse parseXML(String xml) 
        throws WSFederationException {
	// parse the xml string
	Document doc = XMLUtils.toDOMDocument(xml, debug);
	Element root = doc.getDocumentElement();

	return new RequestSecurityTokenResponse(root);
    }
    
    public static RequestSecurityTokenResponse parseXML(InputStream is) 
        throws WSFederationException {
	Document doc = XMLUtils.toDOMDocument(is, debug);
	Element root = doc.getDocumentElement();

	return new RequestSecurityTokenResponse(root);
    }
    
    /**
     * Constructor.
     *
     * @param root <code>RequestSecurityTokenResponse</code> element
     * @throws WSFederationException if error occurs.
     */
    public RequestSecurityTokenResponse(Element root) 
        throws WSFederationException {
        String classMethod = "RequestSecurityTokenResponse:" + 
            "RequestSecurityTokenResponse(Element)";
	// Make sure this is a Response
	if (root == null) {
            if ( debug.messageEnabled() ) {
                debug.message(classMethod + "null input.");
            }
	    throw new WSFederationRequesterException(
		SAMLUtils.bundle.getString("nullInput"));
	}
	String tag = null;
	if (((tag = root.getLocalName()) == null) ||
	    (!tag.equals("RequestSecurityTokenResponse"))) {
            if ( debug.messageEnabled() ) {
                debug.message(classMethod + "wrong input.");
            }
	    throw new WSFederationRequesterException(
		SAMLUtils.bundle.getString("wrongInput"));
	}

        if ( debug.messageEnabled() ) {
            debug.message(classMethod + "found RequestSecurityTokenResponse.");
        }
        
        NodeList list = root.getChildNodes();
	int length = list.getLength();
	for (int i = 0; i < length; i++) {
	    Node child = list.item(i);
            String name = child.getLocalName();
            
            if ( debug.messageEnabled() ) {
                debug.message(classMethod + "examining:"+name);
            }
            
            if ( name.equals("AppliesTo"))
            {
                NodeList nodes = 
                    ((Element)child).getElementsByTagNameNS(
                    WSFederationConstants.WS_ADDRESSING_URI, "Address");
                // ASSUME exactly one address 
                String appliesTo = nodes.item(0).getTextContent();
                
                if ( debug.messageEnabled() ) {
                    debug.message(classMethod + "found AppliesTo:" + appliesTo);
                }
            }
            else if ( name.equals("RequestedSecurityToken"))
            {
                if ( debug.messageEnabled() ) {
                    debug.message(classMethod + "found RequestedSecurityToken");
                }
                
                NodeList nodes = ((Element)child).getElementsByTagNameNS(
                    SAMLConstants.assertionSAMLNameSpaceURI,"Assertion");
                
                // ASSUME exactly one RequestedSecurityToken
                Element ae = (Element)nodes.item(0);
                Assertion a = null;
                try {
                    a = new Assertion(ae);
                }
                catch (SAMLException se)
                {
                    if ( debug.messageEnabled() ) {
                        debug.message("Caught SAMLException, " + 
                            "rethrowing",se);
                    }
                    throw new WSFederationException(se.getMessage());
                }
                issuer = a.getIssuer();
                
                if ( debug.messageEnabled() ) {
                    debug.message(classMethod + "found Assertion with issuer:" +
                        issuer);
                }
                
                if (assertions == Collections.EMPTY_LIST) {
                    assertions = new ArrayList<Assertion>();
                }
                assertions.add(a);
                
                List signs = XMLUtils.getElementsByTagNameNS1(ae,
                    SAMLConstants.XMLSIG_NAMESPACE_URI,
                    SAMLConstants.XMLSIG_ELEMENT_NAME);
                int signsSize = signs.size();
                if (signsSize == 1) {
                    xmlString = XMLUtils.print(ae);
                    signed = true;
                    if ( debug.messageEnabled() ) {
                        debug.message(classMethod + "found signature");
                    }
                } else if (signsSize != 0) {
                    if ( debug.messageEnabled() ) {
                        debug.message(classMethod + 
                            "included more than one Signature element.");
                    }
                    throw new WSFederationException(
                        SAMLUtils.bundle.getString("moreElement"));
                }
            }
        }
        
        if ( assertions.size() != 1 )
        {
            if ( debug.messageEnabled() ) {
                debug.message(classMethod + "missing element <Assertion>.");
            }
	    throw new WSFederationRequesterException(
		SAMLUtils.bundle.getString("missingAssertion"));
        }
    }
    
    /**
     * Gets the recipient of the Response.
     *
     * @return The Recipient.
     */
    public String getRecipient() {
	return recipient;
    }

    /**
     * Set the Recipient attribute of the Response.
     *
     * @param recipient A String representing the Recipient attribute of the
     *	      Response.
     * @return true if the operation is successful;
     */
    public boolean setRecipient(String recipient) {
	if (signed) {
	    return false;
	}
	if ((recipient == null) || (recipient.length() == 0)) {
	     return false;
	}
	this.recipient = recipient;
	return true;
    }

    /**
     * Return whether the object is signed or not.
     * @return true if the object is signed; false otherwise.
     */
    public boolean isSigned() {
	return signed;
    }
    
    /** 
     * This method returns the set of Assertions that is the content of
     * the response.
     * @return The set of Assertions that is the content of the response.
     *		It could be Collections.EMPTY_LIST when there is no Assertion
     *		in the response.
     */
    public List<Assertion> getAssertion() {
	return assertions;
    }

}

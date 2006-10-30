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
 * $Id: RequestAbstractImpl.java,v 1.1 2006-10-30 23:16:50 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.protocol.impl;


import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.shared.DateUtils;
import com.sun.identity.saml.xmlsig.XMLSignatureException;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2SDKUtils;
import com.sun.identity.saml2.assertion.Issuer;
import com.sun.identity.saml2.protocol.RequestAbstract;
import com.sun.identity.saml2.protocol.Extensions;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.Signature;
import com.sun.identity.saml2.xmlsig.SigManager;
import java.text.ParseException;
import java.util.Date;
import org.w3c.dom.Element;


/**
 * This abstract class defines methods for setting and retrieving attributes and
 * elements associated with a SAML request message used in SAML protocols. This
 * class is the base class for all SAML Requests.
 */


public abstract class RequestAbstractImpl implements RequestAbstract {
    
    protected Issuer nameID = null;
    protected Extensions extensions = null;
    protected String requestId = null; 
    protected String version = null;
    protected Date issueInstant = null;
    protected String destinationURI = null;
    protected String consent = null;
    protected boolean isSigned = false;
    protected Boolean isSignatureValid = null;
    protected PublicKey publicKey = null;
    protected boolean isMutable = false;
    protected String  signatureString = null;
    protected String  signedXMLString = null; 
 
    /**
     * Sets the <code>Issuer</code> object.
     *
     * @param nameID the new <code>Issuer</code> object.
     * @throws SAML2Exception if the object is immutable.
     * @see #getIssuer
     */
    public void setIssuer(Issuer nameID) throws SAML2Exception {
         if (!isMutable) {
            throw new SAML2Exception(
                    SAML2SDKUtils.bundle.getString("objectImmutable"));
        }
        this.nameID = nameID ;
    }
    
    /**
     * Returns the <code>Issuer</code> Object.
     *
     * @return the <code>Issuer</code> object.
     * @see #setIssuer(Issuer)
     */
    public Issuer getIssuer() {
        return nameID;
    }
    
    /**
     * Returns the <code>Signature</code> Object as a string.
     *
     * @return the <code>Signature</code> object as a string.
     */
    public String getSignature() {
        return signatureString;
    }
   
     /**
     * Signs the Request.
     *
     * @param privateKey Signing key
     * @param cert Certificate which contain the public key correlated to
     *             the signing key; It if is not null, then the signature
     *             will include the certificate; Otherwise, the signature
     *             will not include any certificate.
     * @throws SAML2Exception if it could not sign the Request.
     */
     public void sign(PrivateKey privateKey, X509Certificate cert)
        throws SAML2Exception  {

        Element signatureEle = SigManager.getSigInstance().sign(
            toXMLString(true, true),
            getID(),
            privateKey,
            cert
        );
        signatureString = XMLUtils.print(signatureEle); 
        signedXMLString = XMLUtils.print(signatureEle.getOwnerDocument().
                                         getDocumentElement());
        isSigned =true;
        makeImmutable();
    }   
        
    /**
     * Sets the <code>Extensions</code> Object.
     *
     * @param extensions the <code>Extensions</code> object.
     * @throws SAML2Exception if the object is immutable.
     * @see #getExtensions
     */
    public void setExtensions(Extensions extensions) throws SAML2Exception {
         if (!isMutable) {
            throw new SAML2Exception(
                    SAML2SDKUtils.bundle.getString("objectImmutable"));
        }
        this.extensions = extensions;
    }
 
    /**
     * Returns the <code>Extensions</code> Object.
     *
     * @return the <code>Extensions</code> object.
     * @see #setExtensions(Extensions)
     */
    public Extensions getExtensions() {
        return extensions;
    }
    
    /**
     * Sets the value of the <code>ID</code> attribute.
     *
     * @param id the new value of <code>ID</code> attribute.
     * @throws SAML2Exception if the object is immutable.
     * @see #getID
     */
    public void setID(String id) throws SAML2Exception {
         if (!isMutable) {
            throw new SAML2Exception(
                    SAML2SDKUtils.bundle.getString("objectImmutable"));
        }

        this.requestId = id;
    }
    
    /**
     * Returns the value of the <code>ID</code> attribute.
     *
     * @return the value of <code>ID</code> attribute.
     * @see #setID(String)
     */
    public String getID () {
        return requestId;
    }
    
    /**
     * Sets the value of the <code>Version</code> attribute.
     *
     * @param version the value of <code>Version</code> attribute.
     * @throws SAML2Exception if the object is immutable.
     * @see #getVersion
     */
    public void setVersion(String version) throws SAML2Exception {
         if (!isMutable) {
            throw new SAML2Exception(
                    SAML2SDKUtils.bundle.getString("objectImmutable"));
        }
        this.version = version;
    }

    /** 
     * Returns the value of the <code>Version</code> attribute.
     *
     * @return value of <code>Version</code> attribute.
     * @see #setVersion(String)
     */

    public String getVersion() {
        return version;
    }
    
    /**
     * Sets the value of <code>IssueInstant</code> attribute.
     *
     * @param dateTime new value of the <code>IssueInstant</code> attribute.
     * @throws SAML2Exception if the object is immutable.
     * @see #getIssueInstant
     */
    public void setIssueInstant(Date dateTime) throws SAML2Exception {
         if (!isMutable) {
            throw new SAML2Exception(
                    SAML2SDKUtils.bundle.getString("objectImmutable"));
        }

        issueInstant = dateTime;
    }
    
    /**
     * Returns the value of <code>IssueInstant</code> attribute.
     *
     * @return value of the <code>IssueInstant</code> attribute.
     * @see #setIssueInstant(Date)
     */
    public Date getIssueInstant() {
        return issueInstant;
    }
    
    /**
     * Sets the value of the <code>Destination</code> attribute.
     *
     * @param destinationURI new value of <code>Destination</code> attribute.
     * @throws SAML2Exception if the object is immutable.
     * @see #getDestination
     */
    public void  setDestination(String destinationURI) throws SAML2Exception {
         if (!isMutable) {
            throw new SAML2Exception(
                    SAML2SDKUtils.bundle.getString("objectImmutable"));
        }

        this.destinationURI = destinationURI;
    }
    
    /**
     * Returns the value of the <code>Destination</code> attribute.
     *
     * @return  the value of <code>Destination</code> attribute.
     * @see #setDestination(String)
     */
    public String getDestination() {
        return destinationURI;
    }
    
    /** 
     * Sets the value of the Consent property.
     *
     * @param consent ,  value of Consent property.
     * @see #getConsent
     */
    public void setConsent(String consent) throws SAML2Exception {
         if (!isMutable) {
            throw new SAML2Exception(
                    SAML2SDKUtils.bundle.getString("objectImmutable"));
        }

        this.consent = consent;
    }
    
    /**
     * Sets the value of the <code>Consent</code> attribute.
     *
     * @return the value of <code>Consent</code> attribute.
     * @throws SAML2Exception if the object is immutable.
     * @see #setConsent(String)
     */
    public String getConsent() {
        return consent;
    }
    
    /**
     * Returns true if message is signed.
     *
     * @return true if message is signed.
     */
    public boolean isSigned() {
        return isSigned;
    }
    
    /**
     * Return whether the signature is valid or not.
     *
     * @param senderCert Certificate containing the public key
     *             which may be used for  signature verification;
     *             This certificate may also may be used to check
     *             against the certificate included in the signature
     * @return true if the signature is valid; false otherwise.
     * @throws SAML2Exception if the signature could not be verified
     */
    public boolean isSignatureValid(X509Certificate senderCert)
        throws SAML2Exception {
	if (isSignatureValid == null) {
            isSignatureValid = new Boolean(
                SigManager.getSigInstance().verify(
		    signedXMLString, getID(), senderCert
                )
            );
        }
        return isSignatureValid.booleanValue();
    }

    /**
     * Returns a String representation of this Object.
     *
     * @return a String representation of this Object.
     * @throws SAML2Exception if it could not create String object
     */
    abstract public String toXMLString() throws SAML2Exception;
    
    /**
     * Returns a String representation of this Object.
     *
     * @param includeNSPrefix determines whether or not the namespace
     *         qualifier is prepended to the Element when converted
     * @param declareNS determines whether or not the namespace is declared
     *         within the Element.
     * @throws SAML2Exception if it could not create String object.
     * @return a String representation of this Object.
     */
    abstract public String toXMLString(boolean includeNSPrefix,
                                       boolean declareNS) throws SAML2Exception;

    protected String getAttributes() 
    throws SAML2Exception {
        StringBuffer xml = new StringBuffer();

        xml.append("ID=\"");
        xml.append(requestId);
        xml.append("\" ");

        xml.append("Version=\"");
        xml.append(version);
        xml.append("\" ");

        xml.append("IssueInstant=\"");
        xml.append(DateUtils.toUTCDateFormat(issueInstant));
        xml.append("\" ");

        if ((destinationURI != null) && (destinationURI.length() > 0)) {
            xml.append("Destination=\"");
            xml.append(destinationURI);
            xml.append("\" ");
        }

        if ((consent != null) && (consent.length() > 0)) {
            xml.append("Consent=\"");
            xml.append(consent);
            xml.append("\" ");
        }

        return xml.toString();
    }

    protected String getElements(boolean includeNSPrefix, boolean declareNS) 
    throws SAML2Exception {
        StringBuffer xml = new StringBuffer();
        if (nameID != null) {
            xml.append(nameID.toXMLString(includeNSPrefix,declareNS));
        }

        if (signatureString != null && !signatureString.equals(""))  {
            xml.append(signatureString);
        }

        if (extensions != null) {
            xml.append(extensions.toXMLString(includeNSPrefix,declareNS));
        }

        return xml.toString();
    }


    /**
     * Makes this object immutable.
     */
    public void makeImmutable() {
	if (isMutable) {
	    if ((nameID != null) && (nameID.isMutable())) {
	    	nameID.makeImmutable();
	    }

	    if ((extensions != null) && (extensions.isMutable())) {
		extensions.makeImmutable();
	    }
	    isMutable=false;
	}
    }
	

    /**
     * Returns true if object is mutable.
     *
     * @return true if object is mutable.
     */
    public boolean isMutable() {
	return isMutable;
    }


   /* Validates the requestID in the SAML Request. */
    protected void validateID(String requestID) throws SAML2Exception {
	if ((requestId == null) || (requestId.length() == 0 )) {
	    SAML2SDKUtils.debug.message("ID is missing in the SAMLRequest");
            throw new SAML2Exception(
		    SAML2SDKUtils.bundle.getString("missingIDAttr"));
	}
    }


   /* Validates the version in the SAML Request. */
    protected void validateVersion(String version) throws SAML2Exception {
	if ((version == null) || (version.length() == 0) ) {
            throw new SAML2Exception(SAML2SDKUtils.bundle.getString(
                                                "missingVersion"));
	} else if (!version.equals(SAML2Constants.VERSION_2_0)) {
            throw new SAML2Exception(SAML2SDKUtils.bundle.getString(
                                        "incorrectVersion"));
	}
    }

   /* Validates the IssueInstant attribute in the SAML Request. */
    protected void validateIssueInstant(String issueInstantStr)
                 	throws SAML2Exception {
	if ((issueInstantStr == null || issueInstantStr.length() == 0)) {
            throw new SAML2Exception(
		SAML2SDKUtils.bundle.getString("missingIssueInstant"));
	} else {
            try {
                issueInstant = DateUtils.stringToDate(issueInstantStr);
            } catch (ParseException e) {
		SAML2SDKUtils.debug.message("Error parsing IssueInstant", e);
		throw new SAML2Exception(
			SAML2SDKUtils.bundle.getString("incorrectIssueInstant"));
            }
	}
    }

   /* Validates the required elements in the SAML Request. */
    protected void validateData() throws SAML2Exception {
	validateID(requestId);
	validateVersion(version);
	if (issueInstant == null) {
	    throw new SAML2Exception(
		SAML2SDKUtils.bundle.getString("incorrectIssueInstant"));
	}
	validateIssueInstant(DateUtils.dateToString(issueInstant));
    }
}

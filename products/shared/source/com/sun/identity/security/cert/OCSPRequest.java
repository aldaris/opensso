/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: OCSPRequest.java,v 1.2 2008-06-25 05:52:58 qcheng Exp $
 *
 */

package com.sun.identity.security.cert;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import sun.misc.HexDumpEncoder;
import sun.security.x509.*;
import sun.security.util.*;

/**
 * This class can be used to generate an OCSP request and send it over 
 * an outputstream. Currently we do not support signing requests
 * The OCSP Request is specified in RFC 2560 and 
 * the ASN.1 definition is as follows:
 * <pre>
 *
 * OCSPRequest     ::=     SEQUENCE {
 *	tbsRequest                  TBSRequest,
 *     	optionalSignature   [0]     EXPLICIT Signature OPTIONAL }
 *
 *   TBSRequest      ::=     SEQUENCE {
 *     	version             [0]     EXPLICIT Version DEFAULT v1,
 *     	requestorName       [1]     EXPLICIT GeneralName OPTIONAL,
 *     	requestList                 SEQUENCE OF Request,
 *     	requestExtensions   [2]     EXPLICIT Extensions OPTIONAL }
 *
 *  Signature       ::=     SEQUENCE {
 *    	signatureAlgorithm      AlgorithmIdentifier,
 *    	signature               BIT STRING,
 *    	certs               [0] EXPLICIT SEQUENCE OF Certificate OPTIONAL
 *   }
 *
 *  Version         ::=             INTEGER  {  v1(0) }
 *
 *  Request         ::=     SEQUENCE {
 * 	reqCert                     CertID,
 *     	singleRequestExtensions     [0] EXPLICIT Extensions OPTIONAL }
 *
 *  CertID 	    ::= SEQUENCE {
 *	 hashAlgorithm 	AlgorithmIdentifier,
 *       issuerNameHash OCTET STRING, -- Hash of Issuer's DN
 *       issuerKeyHash 	OCTET STRING, -- Hash of Issuers public key
 *       serialNumber 	CertificateSerialNumber
 * } 
 *
 * </pre>
 *
 * @version 	1.2 12/19/03
 * @author	Ram Marti
 */

class OCSPRequest {

    private static final boolean dump = false;

    // Serial number of the certificates to be checked for revocation 
    private SerialNumber serialNumber; 	
 
    // Issuer's certificate (for computing certId hash values)
    private X509CertImpl issuerCert;

    // CertId of the certificate to be checked
    private CertId certId = null;

    /*
     * Constructs an OCSPRequest. This constructor is used
     * to construct an unsigned OCSP Request for a single user cert.
     */
    // used by OCSPChecker
    OCSPRequest(X509CertImpl userCert, X509CertImpl issuerCert)
	throws CertPathValidatorException {

	if (issuerCert == null) {
	    throw new CertPathValidatorException("Null IssuerCertificate");
	}
	this.issuerCert = issuerCert;
	/*
	serialNumber = userCert.getSerialNumberObject();
	*/
	serialNumber = new SerialNumber(userCert.getSerialNumber());
    }

    // used by OCSPChecker
    byte[] encodeBytes() throws IOException {

	// encode tbsRequest 
	DerOutputStream tmp = new DerOutputStream();
	DerOutputStream derSingleReqList  = new DerOutputStream();
	SingleRequest singleRequest = null;

	try {
	    singleRequest = new SingleRequest(serialNumber);
	} catch (Exception e) {
	    throw new IOException("Error encoding OCSP request");
	}

	certId = singleRequest.getCertId();
	singleRequest.encode(derSingleReqList);
	tmp.write(DerValue.tag_Sequence, derSingleReqList);
	// No extensions supported
	DerOutputStream tbsRequest = new DerOutputStream();
	tbsRequest.write(DerValue.tag_Sequence, tmp);

	// OCSPRequest without the signature 
	DerOutputStream ocspRequest = new DerOutputStream();
	ocspRequest.write(DerValue.tag_Sequence, tbsRequest);

	byte[] bytes = ocspRequest.toByteArray();

        if (dump) {
	    HexDumpEncoder hexEnc = new HexDumpEncoder();
	    System.out.println ("OCSPRequest bytes are... ");
	    System.out.println(hexEnc.encode(bytes));
        }

	return(bytes);
    }

    // used by OCSPChecker
    CertId getCertId() {
	return certId;
    }
    
    private class SingleRequest {
	private CertId certId;	

	// No extensions are set

	private SingleRequest(SerialNumber serialNo) throws Exception {
	    certId = new CertId(issuerCert, serialNo);
	}
	
	private void encode(DerOutputStream out) throws IOException {
	    DerOutputStream tmp = new DerOutputStream();
	    certId.encode(tmp);
	    out.write(DerValue.tag_Sequence, tmp);
	}

	private CertId getCertId() {
	    return certId;
	}
    }
}

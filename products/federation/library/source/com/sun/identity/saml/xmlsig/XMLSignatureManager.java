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
 * $Id: XMLSignatureManager.java,v 1.4 2007-07-11 06:17:00 mrudul_uchil Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.saml.xmlsig;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import com.sun.identity.saml.common.*;
import com.sun.identity.common.SystemConfigurationUtil;

/**
 * The class <code>XMLSignatureManager</code> provides methods 
 * to sign and verify XML signature.
 * <p>
 */

public class XMLSignatureManager {
    // Singleton instance of XMLSignatureManager
    protected static XMLSignatureManager instance = null;
    private SignatureProvider sp = null; 
    
    /**
     * Constructor
     */
    protected XMLSignatureManager() {
        try {
           String sprovider = SystemConfigurationUtil.getProperty(
                SAMLConstants.SIGNATURE_PROVIDER_IMPL_CLASS,
                SAMLConstants.AM_SIGNATURE_PROVIDER);
           sp= (SignatureProvider) Class.forName(sprovider).newInstance();
        } catch (Exception e) {
            SAMLUtilsCommon.debug.error("XMLSignatureManager: " +
                "constructor error"); 
        }
    }
    
    /** 
     *Constructor 
     */
    protected XMLSignatureManager(KeyProvider keyProvider, 
                                  SignatureProvider sigProvider) {
        sigProvider.initialize(keyProvider); 
        sp = sigProvider;
    }

    /**
     * Gets the singleton instance of <code>XMLSignatureManager</code> with
     * default <code>KeyProvider</code> and <code>SignatureProvider</code>.
     * @return <code>XMLSignatureManager</code>
     */ 
    public static XMLSignatureManager getInstance() {
	if (instance == null) {
	    synchronized (XMLSignatureManager.class) {
		if (instance == null) {
		    if (SAMLUtilsCommon.debug.messageEnabled() ) {
			SAMLUtilsCommon.debug.message(
                            "Constructing a new instance"
		            + " of XMLSignatureManager");
		    }
		    instance = new XMLSignatureManager();
		}
	    }
	}
	return (instance);
    }

    /**
     * Get an instance of <code>XMLSignatureManager</code> with specified 
     * <code>KeyProvider</code> and <code>SignatureProvider</code>.
     * @param keyProvider <code>KeyProvider</code>
     * @param sigProvider <code>SignatureProvider</code>.
     * @return <code>XMLSignatureManager</code>.
     */
    public static XMLSignatureManager getInstance(KeyProvider keyProvider, 
                                                 SignatureProvider sigProvider){
        return new XMLSignatureManager(keyProvider, sigProvider);
    }

    /**
     * Sign the XML document using enveloped signatures.
     * @param doc XML dom object
     * @param certAlias Signer's certificate alias name
     * @return signature dom object
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signXML(org.w3c.dom.Document doc, 
                                       java.lang.String certAlias)
        throws XMLSignatureException {
        return sp.signXML(doc, certAlias);
    }

    /**
     * Sign the XML document using enveloped signatures.
     * @param doc XML dom object
     * @param certAlias Signer's certificate alias name
     * @param algorithm signature algorithm 
     * @return signature dom object
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signXML(org.w3c.dom.Document doc, 
                                       java.lang.String certAlias,
                                       java.lang.String algorithm)
        throws XMLSignatureException {
        return sp.signXML(doc, certAlias, algorithm);
    }
    
    /**
     * Sign the XML string using enveloped signatures.
     * @param XML XML string to be signed
     * @param certAlias Signer's certificate alias name
     * @return XML signature string
     * @throws XMLSignatureException if the XML string could not be signed
     */
    public java.lang.String signXML(java.lang.String XML,
                                java.lang.String certAlias)
        throws XMLSignatureException {
        return sp.signXML(XML, certAlias);
    }

    /**
     * Sign the XML string using enveloped signatures.
     * @param XML XML string to be signed
     * @param certAlias Signer's certificate alias name
     * @param algorithm signature algorithm
     * @return XML signature string
     * @throws XMLSignatureException if the XML string could not be signed
     */
    public java.lang.String signXML(java.lang.String XML,
                                    java.lang.String certAlias, 
                                    java.lang.String algorithm)
        throws XMLSignatureException {
        return sp.signXML(XML, certAlias, algorithm);
    }

     /**                                                                    
     * Sign part of the XML document referred by the supplied id attribute using
       enveloped signatures and use exclusive XML canonicalization.
     * @param doc XML dom object                                               
     * @param certAlias Signer's certificate alias name                        
     * @param algorithm XML signature algorithm                                
     * @param id attribute value of the node to be signed                   
     * @return signature dom object                                            
     * @throws XMLSignatureException if the document could not be signed       
     */                                                                        
    public org.w3c.dom.Element signXML(org.w3c.dom.Document doc,               
                                       java.lang.String certAlias,             
                                       java.lang.String algorithm,             
                                       java.lang.String id)                    
        throws XMLSignatureException {                                         
            return sp.signXML(doc, certAlias, algorithm, id);                  
    }                                                                          

    /**
     * Sign part of the XML document referred by the supplied id attribute
     * using enveloped signatures and use exclusive XML canonicalization.
     * @param doc XML dom object
     * @param certAlias Signer's certificate alias name
     * @param algorithm XML signature algorithm
     * @param id id attribute value of the node to be signed
     * @param xpath expression should uniquely identify a node before which
     * @return signature dom object
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signXML(org.w3c.dom.Document doc,
                                       java.lang.String certAlias,
                                       java.lang.String algorithm,
                                       java.lang.String id,
                                       java.lang.String xpath)
        throws XMLSignatureException {
        return sp.signXML(doc, certAlias, algorithm, id, xpath);
    }

     /**
     * Sign part of the XML document referred by the supplied id attribute
     * using enveloped signatures and use exclusive XML canonicalization.
     * @param doc XML dom object
     * @param certAlias Signer's certificate alias name
     * @param algorithm XML signature algorithm
     * @param idAttrName attribute name for the id attribute of the node to be
     *        signed.
     * @param id id attribute value of the node to be signed
     * @param includeCert if true, include the signing certificate in 
     *        <code>KeyInfo</code>. if false, does not include the signing
     *        certificate.
     * @return signature dom object
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signXML(org.w3c.dom.Document doc,
                                       java.lang.String certAlias,
                                       java.lang.String algorithm,
                                       java.lang.String idAttrName,
                                       java.lang.String id,
                                       boolean includeCert)
        throws XMLSignatureException {
        return sp.signXML(doc, certAlias, algorithm, idAttrName,  id, includeCert);
    }


     /**
     * Sign part of the XML document referred by the supplied id attribute
     * using enveloped signatures and use exclusive XML canonicalization.
     * @param xmlString a string representing XML dom object
     * @param certAlias Signer's certificate alias name
     * @param algorithm XML signature algorithm
     * @param idAttrName attribute name for the id attribute of the node to be
     *        signed.
     * @param id id attribute value of the node to be signed
     * @param includeCert if true, include the signing certificate in 
     *        <code>KeyInfo</code>.
     *                    if false, does not include the signing certificate.
     * @return a string of signature dom object
     * @throws XMLSignatureException if the document could not be signed
     */
    public java.lang.String signXML(java.lang.String xmlString,
                                       java.lang.String certAlias,
                                       java.lang.String algorithm,
                                       java.lang.String idAttrName,
                                       java.lang.String id,
                                       boolean includeCert)
        throws XMLSignatureException {
    
    	return sp.signXML(xmlString, certAlias, algorithm, idAttrName,  id, includeCert);
    }
        
     /**
     * Sign part of the XML document referred by the supplied id attribute 
     * using enveloped signatures and use exclusive XML canonicalization.
     * @param doc XML dom object
     * @param certAlias Signer's certificate alias name
     * @param algorithm XML signature algorithm
     * @param idAttrName attribute name for the id attribute of the node to be
     *        signed.
     * @param id id attribute value of the node to be signed
     * @param includeCert if true, include the signing certificate in 
     *        <code>KeyInfo</code>.
     *                    if false, does not include the signing certificate. 
     * @param xpath expression should uniquely identify a node before which
     * @return signature dom object
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signXML(org.w3c.dom.Document doc,
                                       java.lang.String certAlias,
                                       java.lang.String algorithm,
                                       java.lang.String idAttrName,
                                       java.lang.String id,
                                       boolean includeCert,
				       java.lang.String xpath) 
        throws XMLSignatureException {
            return sp.signXML(doc, certAlias, algorithm, 
                              idAttrName, id, includeCert, xpath);
    }
                                                                               
   /**                                                                    
     * Sign the XML string using enveloped signatures.                         
     * @param xmlString XML string to be signed                                
     * @param certAlias Signer's certificate alias name                        
     * @param algorithm XML Signature algorithm                                
     * @param id id attribute value of the node to be signed                   
     * @return XML signature string                                            
     * @throws XMLSignatureException if the XML string could not be signed     
     */                                                                        
    public java.lang.String signXML(java.lang.String xmlString,                
                                    java.lang.String certAlias,                
                                    java.lang.String algorithm,                
                                    java.lang.String id)                       
        throws XMLSignatureException {                                         
            return sp.signXML(xmlString, certAlias, algorithm, id);            
    }                                                                          
                                                                               
    /**
     *
     * Sign part of the XML document referred by the supplied a list
     * of id attributes of nodes
     * @param doc XML dom object
     * @param certAlias Signer's certificate alias name
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @return signature dom object
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signXML(org.w3c.dom.Document doc,               
                                       java.lang.String certAlias,             
                                       java.lang.String algorithm,             
                                       java.util.List ids)                    
        throws XMLSignatureException {                                         
            return sp.signXML(doc, certAlias, algorithm, ids);                  
    }                                                                          

    /**
     * Sign part of the XML document referred by the supplied a list
     * of id attributes of nodes
     * @param xmlString XML dom object's string format 
     * @param certAlias Signer's certificate alias name
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @return XML signature string
     * @throws XMLSignatureException if the document could not be signed
     */
    public java.lang.String signXML(java.lang.String xmlString,                
                                    java.lang.String certAlias,                
                                    java.lang.String algorithm,                
                                    java.util.List ids)                       
        throws XMLSignatureException {                                         
        return sp.signXML(xmlString, certAlias, algorithm, ids);            
    }                                                                          
    
    /**
     * Sign part of the XML document referred by the supplied a list
     * of id attributes of nodes
     * @param doc XML dom object
     * @param certAlias Signer's certificate alias name
     * @param algorithm XML signature algorithm
     * @param transformAlag XML signature transform algorithm 
     *        Those transfer constants are defined as
     *        <code>SAMLConstants.TRANSFORM_XXX</code>.
     * @param ids list of id attribute values of nodes to be signed
     * @return XML signature element 
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signXML(org.w3c.dom.Document doc,              
                                    java.lang.String certAlias,                
                                    java.lang.String algorithm,
                                    java.lang.String transformAlag, 
                                    java.util.List ids)                       
        throws XMLSignatureException {                                         
        return sp.signXML(doc, certAlias, algorithm, transformAlag, ids);            
    }     
    
    /**
     * Sign part of the XML document referred by the supplied a list
     * of id attributes of nodes
     * @param doc XML dom object
     * @param cert signer's Certificate
     * @param assertionID assertion ID for the SAML Security Token
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @return SAML Security Token  signature
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signWithWSSSAMLTokenProfile(
				   org.w3c.dom.Document doc,
				   java.security.cert.Certificate cert,
				   java.lang.String assertionID,
                                   java.lang.String algorithm,
                                   java.util.List ids)
        throws XMLSignatureException {

        return sp.signWithWSSSAMLTokenProfile(doc, cert, assertionID,
						algorithm, ids);

    }

    /**
     * Sign part of the XML document referred by the supplied a list
     * of id attributes of nodes
     * @param doc XML dom object
     * @param cert signer's Certificate
     * @param assertionID assertion ID for the SAML Security Token
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @param the web services framework that should be used.
     *     For WSF1.1, the version must be "1.1" and for WSF1.0,
     *     it must be "1.0"
     * @return SAML Security Token  signature
     * @exception XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signWithWSSSAMLTokenProfile(
        org.w3c.dom.Document doc, java.security.cert.Certificate cert,
        String assertionID, String algorithm, java.util.List ids,
        String wsfVersion) throws XMLSignatureException {

        return sp.signWithWSSSAMLTokenProfile(doc, cert, assertionID,
            algorithm, ids, wsfVersion);
    }
    
    /**
     * Sign part of the XML document referred by the supplied a list
     * of id attributes of nodes
     * @param doc XML dom object
     * @param cert signer's Certificate
     * @param assertionID assertion ID for the SAML Security Token
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @return SAML Security Token  signature
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signWithSAMLToken(
				   org.w3c.dom.Document doc,
				   java.security.cert.Certificate cert,
				   java.lang.String assertionID,
                                   java.lang.String algorithm,
                                   java.util.List ids)
        throws XMLSignatureException {

        return sp.signWithSAMLToken(doc, cert, assertionID, algorithm, ids);

    }

    /**
     * Sign part of the XML document wth binary security token using 
     * referred by the supplied a list of id attributes of nodes.
     * @param doc the XML <code>DOM</code> document.
     * @param cert Signer's certificate
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @return X509 Security Token  signature
     * @exception XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signWithBinarySecurityToken(
                 org.w3c.dom.Document doc,
                 java.security.cert.Certificate cert,
                 java.lang.String algorithm,
                 java.util.List ids)
        throws XMLSignatureException {
           return sp.signWithBinarySecurityToken(doc, cert, algorithm, ids);
    }

    /**
     * Sign part of the XML document wth UserName security token using 
     * referred by the supplied a list of id attributes of nodes.
     * @param doc the XML <code>DOM</code> document.
     * @param cert Signer's certificate
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @return X509 Security Token  signature
     * @exception XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signWithUserNameToken(
                 org.w3c.dom.Document doc,
                 java.security.cert.Certificate cert,
                 java.lang.String algorithm,
                 java.util.List ids)
        throws XMLSignatureException {
           return sp.signWithUserNameToken(doc, cert, algorithm, ids);
    }
    
    /**
     *
     * Sign part of the XML document referred by the supplied a list
     * of id attributes of nodes
     * @param doc XML dom object
     * @param cert Signer's certificate
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @return X509 Security Token  signature
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signWithWSSX509TokenProfile(
        org.w3c.dom.Document doc, java.security.cert.Certificate cert,
        String algorithm, java.util.List ids) throws XMLSignatureException {
        return sp.signWithWSSX509TokenProfile(doc, cert, algorithm, ids);
    }

    /**
     *
     * Sign part of the XML document referred by the supplied a list
     * of id attributes of nodes
     * @param doc XML dom object
     * @param cert Signer's certificate
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @param the web services framework that should be used.
     *     For WSF1.1, it should be "1.1" and for WSF1.0,
     *     it should be "1.0"
     * @return X509 Security Token  signature
     * @exception XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signWithWSSX509TokenProfile(
        org.w3c.dom.Document doc, java.security.cert.Certificate cert,
        String algorithm, java.util.List ids, String wsfVersion)
        throws XMLSignatureException {

         return sp.signWithWSSX509TokenProfile(doc, cert, algorithm, ids,
             wsfVersion);
    }
    
    /**                                                                        
     * Verify all the signatures of the XML document                           
     * @param document XML dom document whose signature to be verified              
     * @return true if the XML signature is verified, false otherwise          
     * @throws XMLSignatureException if problem occurs during verification     
     */                                                                        
    public boolean verifyXMLSignature(org.w3c.dom.Document document)           
        throws XMLSignatureException {                                         
        return sp.verifyXMLSignature(document);                                
    }                                                                          
    /**                                                                        
     * Verify all the signatures of the XML document                           
     * @param document XML dom document whose signature to be verified              
     * @param certAlias alias for Signer's certificate, this is used to search 
     *        signer's public certificate if it is not presented in
     *        <code>ds:KeyInfo</code>.
     * @return true if the XML signature is verified, false otherwise          
     * @throws XMLSignatureException if problem occurs during verification     
     */                                                                        
    public boolean verifyXMLSignature(org.w3c.dom.Document document,           
                                       java.lang.String certAlias)             
        throws XMLSignatureException {                                         

        return sp.verifyXMLSignature(document, certAlias);                     
    }

    /**
     * Verify the signature of the XML document
     * @param document XML dom document whose signature to be verified
     * @param cert Signer's certificate, this is used to search signer's
     *        public certificate if it is not presented in
     *        <code>ds:KeyInfo</code>.
     * @return true if the XML signature is verified, false otherwise
     * @throws XMLSignatureException if problem occurs during verification
     */
    public boolean verifyXMLSignature(org.w3c.dom.Document document,
                                      java.security.cert.Certificate cert)
        throws XMLSignatureException {

	return sp.verifyXMLSignature(document, cert);
    }

    /**
     * Verify the signature of the XML document
     * @param element XML dom document whose signature to be verified
     * @return true if the XML signature is verified, false otherwise
     * @throws XMLSignatureException if problem occurs during verification
     */
    public boolean verifyXMLSignature(org.w3c.dom.Element element)
        throws XMLSignatureException {
        return sp.verifyXMLSignature(element);
    }

    /**
     * Verify the signature of the XML document
     * @param element XML dom document whose signature to be verified
     * @param certAlias alias for Signer's certificate, this is used to search
     *        signer's public certificate if it is not presented in
     *        <code>ds:KeyInfo</code>
     * @return true if the XML signature is verified, false otherwise
     * @throws XMLSignatureException if problem occurs during verification
     */
    public boolean verifyXMLSignature(org.w3c.dom.Element element, 
                                      java.lang.String certAlias)
        throws XMLSignatureException {
        return sp.verifyXMLSignature(element, certAlias);
    }

    /**
     * Verify the signature of the XML document
     * @param element XML dom document whose signature to be verified
     * @param idAttrName Attribute name for the id attribute 
     * @param certAlias alias for Signer's certificate, this is used to search
     *        signer's public certificate if it is not presented in 
     *        <code>ds:KeyInfo</code>.
     * @return true if the XML signature is verified, false otherwise
     * @throws XMLSignatureException if problem occurs during verification
     */
    public boolean verifyXMLSignature(org.w3c.dom.Element element,
                                      java.lang.String idAttrName, 
                                      java.lang.String certAlias)
        throws XMLSignatureException {
        return sp.verifyXMLSignature(element, idAttrName, certAlias);
    }
 
    /**
     * Verify the signature of the XML string
     * @param XML XML string whose signature to be verified
     * @return true if the XML signature is verified, false otherwise
     * @throws XMLSignatureException if problem occurs during verification
     */
    public boolean verifyXMLSignature(java.lang.String XML)
        throws XMLSignatureException {
        return sp.verifyXMLSignature(XML);
    }

    /**
     * Verify the signature of the XML string
     * @param XML XML string whose signature to be verified
     * @param certAlias alias for Signer's certificate, this is used to search 
     *        signer's public certificate if it is not presented in
     *        <code>ds:KeyInfo</code>/
     * @return true if the XML signature is verified, false otherwise
     * @throws XMLSignatureException if problem occurs during verification
     */
    public boolean verifyXMLSignature(java.lang.String XML, 
                                      java.lang.String certAlias)
        throws XMLSignatureException {
        return sp.verifyXMLSignature(XML, certAlias);
    }

    /**
     * Verify the signature of the XML string
     * @param xmlString XML string whose signature to be verified
     * @param idAttrName Attribute name for the id attribute
     * @param certAlias <code>certAlias</code> for Signer's certificate,
     *        this is used to search signer's public certificate if it is not
     *        presented in <code>ds:KeyInfo</code>.
     * @return true if the XML signature is verified, false otherwise.
     * @throws XMLSignatureException if problem occurs during verification.
     */
    public boolean verifyXMLSignature(java.lang.String xmlString,
                                      java.lang.String idAttrName,
                                      java.lang.String certAlias)
        throws XMLSignatureException {
        return sp.verifyXMLSignature(xmlString, idAttrName, certAlias);
    }
    
    /**
     * Verify all the signatures of the XML document
     * @param wsfVersion the web services version that should be used.
     * @param certAlias alias for Signer's certificate, this is used to search
     *     signer's public certificate if it is not presented in
     *     <code>ds:KeyInfo</code>.
     * @param document XML dom document whose signature to be verified
     * @return true if the XML signature is verified, false otherwise
     * @exception XMLSignatureException if problem occurs during verification.
     */
    public boolean verifyXMLSignature(String wsfVersion, String certAlias,
        org.w3c.dom.Document document) throws XMLSignatureException {
        return sp.verifyXMLSignature(wsfVersion, certAlias, document);
    }
    
    /**
     * Verify all the signatures of the XML document for the
     * web services security.
     * @param document XML dom document whose signature to be verified
     *
     * @param certAlias alias for Signer's certificate, this is used to search
     *        signer's public certificate if it is not presented in
     *        <code>ds:KeyInfo</code>.
     * @return true if the XML signature is verified, false otherwise
     * @throws XMLSignatureException if problem occurs during verification
     */
    public boolean verifyWSSSignature(org.w3c.dom.Document document,
                                       java.lang.String certAlias)
        throws XMLSignatureException {

        return sp.verifyWSSSignature(document, certAlias);
    }
    
    /**
     * Get <code>KeyProvider</code>
     * @return <code>KeyProvider</code>
     */
    public KeyProvider getKeyProvider() { 
        return sp.getKeyProvider();
    }
}

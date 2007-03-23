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
 * $Id: WSSSignatureProvider.java,v 1.1 2007-03-23 00:02:07 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.security;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.signature.XMLSignature;
import com.sun.identity.saml.xmlsig.AMSignatureProvider;
import com.sun.identity.saml.xmlsig.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xml.internal.security.utils.IdResolver;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.transforms.Transform;

import com.sun.identity.saml.common.SAMLConstants;
import com.sun.identity.saml.common.SAMLUtils;
import com.sun.identity.shared.xml.XMLUtils;


/**
 *
 * @author Administrator
 */
public class WSSSignatureProvider extends AMSignatureProvider {
    
    /** Creates a new instance of WSSSignatureProvider */
    public WSSSignatureProvider() {
        super();
        try {
            Transform.register(STRTransform.STR_TRANSFORM_URI,
                               STRTransform.class.getName());
        } catch (Exception e) {
            if(WSSUtils.debug.messageEnabled()) {
               WSSUtils.debug.message("WSSSignatureProvider.constructor: STR"+
                    " Transform is already registered");
            }
        }
    }
    
    /**
     * Sign part of the xml document referered by the supplied a list
     * of id attributes of nodes
     * @param doc XML dom object
     * @param cert Signer's certificate
     * @param assertionID assertion ID
     * @param algorithm XML signature algorithm
     * @param ids list of id attribute values of nodes to be signed
     * @return SAML Security Token  signature
     * @throws XMLSignatureException if the document could not be signed
     */
    public org.w3c.dom.Element signWithSAMLToken(
                                   org.w3c.dom.Document doc,
                                   java.security.cert.Certificate cert,
                                   String assertionID,
                                   java.lang.String algorithm,
                                   java.util.List ids)
        throws XMLSignatureException {
        
        if (doc == null) {
            WSSUtils.debug.error("WSSSignatureProvider.signWithSAMLToken: " +
                     "doc is null.");
            throw new XMLSignatureException(
                      WSSUtils.bundle.getString("nullInput"));
        }
        if (cert == null) {
            WSSUtils.debug.error("WSSSignatureProvider.signWithSAMLToken: " +
                                        "Certificate is null");
            throw new XMLSignatureException(
                      WSSUtils.bundle.getString("nullInput"));
        }
        if (assertionID == null) {
            WSSUtils.debug.error("WSSSignatureProvider.signWithSAMLToken: " +
                                        "Certificate is null");
            throw new XMLSignatureException(
                      WSSUtils.bundle.getString("nullInput"));
        }
        Element root = (Element) doc.getDocumentElement().
                getElementsByTagNameNS(WSSConstants.WSSE_NS,
                         WSSConstants.WSSE_SECURITY_LNAME).item(0);

        org.w3c.dom.Element timeStamp = (Element) doc.getDocumentElement().
                getElementsByTagNameNS(WSSConstants.WSU_NS,
                         WSSConstants.TIME_STAMP).item(0);
        XMLSignature signature = null;
        try {
            Constants.setSignatureSpecNSprefix("ds");

            String certAlias = keystore.getCertificateAlias(cert);            
            PrivateKey privateKey =
                                (PrivateKey) keystore.getPrivateKey(certAlias);
            if (privateKey == null) {
                WSSUtils.debug.error("WSSSignatureProvider.signWithSAMLToken:"
                           + "Private Key is null for " + certAlias);
                throw new XMLSignatureException(
                          WSSUtils.bundle.getString("nullprivatekey"));
            }
            
            if (algorithm == null || algorithm.length() == 0) {
                algorithm = getPublicKey((X509Certificate)cert).getAlgorithm();                                
                algorithm = getAlgorithmURI(algorithm);
            }
            
            if (!isValidAlgorithm(algorithm)) {
                throw new XMLSignatureException(
                           WSSUtils.bundle.getString("invalidalgorithm"));
            }
            Element wsucontext = com.sun.org.apache.xml.internal.security.utils.
                    XMLUtils.createDSctx(doc, "wsu", WSSConstants.WSU_NS);

            NodeList wsuNodes = (NodeList)XPathAPI.selectNodeList(doc,
                    "//*[@wsu:Id]", wsucontext);

            if (wsuNodes != null && wsuNodes.getLength() != 0) {
                for (int i = 0; i < wsuNodes.getLength(); i++) {
                     Element elem = (Element) wsuNodes.item(i);
                     String id = elem.getAttributeNS(WSSConstants.WSU_NS, "Id");
                     if (id != null && id.length() != 0) {
                         IdResolver.registerElementById(elem, id);
                     }
                }
            }

            signature = new XMLSignature(doc, null, algorithm,
                  Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            Element sigEl = signature.getElement();
            doc.importNode(sigEl, true);
            sigEl.setPrefix("ds");
            root.insertBefore(sigEl, timeStamp);
            Node textNode = doc.createTextNode("\n");
            root.insertBefore(textNode, sigEl);

            Element transformParams = doc.createElementNS(WSSConstants.WSSE_NS,
                    WSSConstants.WSSE_TAG + ":" +
                    WSSConstants.TRANSFORMATION_PARAMETERS);
            transformParams.setAttributeNS(SAMLConstants.NS_XMLNS,
                    WSSConstants.TAG_XML_WSSE, WSSConstants.WSSE_NS);
            Element canonElem =  doc.createElementNS(
                    SAMLConstants.XMLSIG_NAMESPACE_URI,
                    "ds:CanonicalizationMethod");
            canonElem.setAttributeNS(null, "Algorithm",
                    Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            transformParams.appendChild(canonElem);
                        Element securityTokenRef =
                doc.createElementNS(WSSConstants.WSSE_NS,
                        SAMLConstants.TAG_SECURITYTOKENREFERENCE);
            securityTokenRef.setPrefix(WSSConstants.WSSE_TAG);
            securityTokenRef.setAttributeNS(
                    WSSConstants.NS_XML,
                    WSSConstants.TAG_XML_WSU,
                    WSSConstants.WSU_NS);
            String secRefId = SAMLUtils.generateID();
            securityTokenRef.setAttributeNS(WSSConstants.WSU_NS,
                    WSSConstants.WSU_ID, secRefId);
            signature.addKeyInfo((X509Certificate)cert);
            KeyInfo keyInfo = signature.getKeyInfo();
            keyInfo.addUnknownElement(securityTokenRef);
            IdResolver.registerElementById(securityTokenRef, secRefId);

            int size = ids.size();
            for (int i = 0; i < size; ++i) {
                Transforms transforms = new Transforms(doc);
                transforms.addTransform(
                                Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
                String id = (String) ids.get(i);
                if (WSSUtils.debug.messageEnabled()) {
                    WSSUtils.debug.message("id = " +id);
                }
                signature.addDocument("#"+id, transforms,
                        Constants.ALGO_ID_DIGEST_SHA1);
            }

            Transforms strtransform = new Transforms(doc);
            strtransform.addTransform(STRTransform.STR_TRANSFORM_URI,
                       transformParams);
            signature.addDocument("#"+secRefId, strtransform,
                        Constants.ALGO_ID_DIGEST_SHA1);

            Element keyIdentifier = doc.createElementNS(
                        WSSConstants.WSSE_NS,
                        WSSConstants.TAG_KEYIDENTIFIER);
            keyIdentifier.setPrefix(WSSConstants.WSSE_TAG);
            keyIdentifier.setAttribute(WSSConstants.WSU_ID,
                        SAMLUtils.generateID());
            Text value = doc.createTextNode(assertionID);
            keyIdentifier.appendChild(value);
            keyIdentifier.setAttributeNS(null, SAMLConstants.TAG_VALUETYPE,
                        WSSConstants.SAML_VALUETYPE);
            securityTokenRef.appendChild(keyIdentifier);
            signature.sign(privateKey);
        } catch (Exception e) {
            WSSUtils.debug.error("WSSSignatureProvider.signWithSAMLToken" +
                      " Exception: ", e);
            throw new XMLSignatureException(e.getMessage());
        }

        if (WSSUtils.debug.messageEnabled()) {
            WSSUtils.debug.message("WSSSignatureProvider.signWithSAMLToken" +
                 "Signed document"+ XMLUtils.print(doc.getDocumentElement()));
        }

        return signature.getElement();                                
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
        if (doc == null) {
            WSSUtils.debug.error("WSSSignatureProvider.signWithBinarySecurity" +
            "Token:: XML doc is null.");
            throw new XMLSignatureException(
                      WSSUtils.bundle.getString("nullInput"));
        }

        if (WSSUtils.debug.messageEnabled()) {
            WSSUtils.debug.message("WSSSignatureProvider.signWithBinary" +
                "SecurityToken: Document to be signed : " +
                XMLUtils.print(doc.getDocumentElement()));
        }

        org.w3c.dom.Element root = (Element) doc.getDocumentElement().
                getElementsByTagNameNS(WSSConstants.WSSE_NS,
                                        SAMLConstants.TAG_SECURITY).item(0);

        XMLSignature signature = null;
        try {
            Constants.setSignatureSpecNSprefix(SAMLConstants.PREFIX_DS);
            String certAlias = keystore.getCertificateAlias(cert);
            PrivateKey privateKey =
                                (PrivateKey) keystore.getPrivateKey(certAlias);
            if (privateKey == null) {
                WSSUtils.debug.error("WSSSignatureProvider.signWithWSSToken:" +
                   " private key is null");
                throw new XMLSignatureException(
                          WSSUtils.bundle.getString("nullprivatekey"));
            }

            if (algorithm == null || algorithm.length() == 0) {
                algorithm = getPublicKey((X509Certificate)cert).getAlgorithm();
                algorithm = getAlgorithmURI(algorithm);
            }
                        if (!isValidAlgorithm(algorithm)) {
                throw new XMLSignatureException(
                           WSSUtils.bundle.getString("invalidalgorithm"));
            }

            Element wsucontext = com.sun.org.apache.xml.internal.security.utils.
                    XMLUtils.createDSctx(doc, "wsu", WSSConstants.WSU_NS);

            NodeList wsuNodes = (NodeList)XPathAPI.selectNodeList(doc,
                    "//*[@wsu:Id]", wsucontext);

            if (wsuNodes != null && wsuNodes.getLength() != 0) {
                for (int i = 0; i < wsuNodes.getLength(); i++) {
                     Element elem = (Element) wsuNodes.item(i);
                     String id = elem.getAttributeNS(WSSConstants.WSU_NS, "Id");
                     if (id != null && id.length() != 0) {
                         IdResolver.registerElementById(elem, id);
                     }
                }
            }

            signature = new XMLSignature(doc, "", algorithm,
                  Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

            Element sigEl = signature.getElement();
            root.appendChild(sigEl);

            Element transformParams = doc.createElementNS(WSSConstants.WSSE_NS,
                    WSSConstants.WSSE_TAG + ":" +
                    WSSConstants.TRANSFORMATION_PARAMETERS);
            transformParams.setAttributeNS(SAMLConstants.NS_XMLNS,
                    WSSConstants.TAG_XML_WSSE, WSSConstants.WSSE_NS);
            Element canonElem =  doc.createElementNS(
                    SAMLConstants.XMLSIG_NAMESPACE_URI,
                    "ds:CanonicalizationMethod");
            canonElem.setAttributeNS(null, "Algorithm",
                    Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            transformParams.appendChild(canonElem);

            Element securityTokenRef = doc.createElementNS(WSSConstants.WSSE_NS,
                    "wsse:" + SAMLConstants.TAG_SECURITYTOKENREFERENCE);
            securityTokenRef.setAttributeNS(SAMLConstants.NS_XMLNS,
                    WSSConstants.TAG_XML_WSSE, WSSConstants.WSSE_NS);
            securityTokenRef.setAttributeNS(SAMLConstants.NS_XMLNS,
                    WSSConstants.TAG_XML_WSU, WSSConstants.WSU_NS);
            String secRefId = SAMLUtils.generateID();

            securityTokenRef.setAttributeNS(WSSConstants.WSU_NS,
                  WSSConstants.WSU_ID, secRefId);
                        KeyInfo keyInfo = signature.getKeyInfo();
            keyInfo.addUnknownElement(securityTokenRef);
            IdResolver.registerElementById(securityTokenRef, secRefId);
            int size = ids.size();
            for (int i = 0; i < size; ++i) {
                Transforms transforms = new Transforms(doc);
                transforms.addTransform(
                                Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
                String id = (String) ids.get(i);                
                signature.addDocument("#"+id, transforms,
                        Constants.ALGO_ID_DIGEST_SHA1);
            }
            Transforms strtransforms = new Transforms(doc);
            strtransforms.addTransform(
                               STRTransform.STR_TRANSFORM_URI, transformParams);
            signature.addDocument("#"+secRefId, strtransforms,
                        Constants.ALGO_ID_DIGEST_SHA1);

            Element bsf = (Element)root.getElementsByTagNameNS(
                        WSSConstants.WSSE_NS,
                        SAMLConstants.BINARYSECURITYTOKEN).item(0);

            String certId = bsf.getAttributeNS(WSSConstants.WSU_NS,
                            SAMLConstants.TAG_ID);

            Element reference = doc.createElementNS(WSSConstants.WSSE_NS,
                        SAMLConstants.TAG_REFERENCE);
            securityTokenRef.appendChild(reference);
            reference.setAttributeNS(null, SAMLConstants.TAG_URI, "#"+certId);

            signature.sign(privateKey);

        } catch (Exception e) {
           WSSUtils.debug.error("WSSSignatureProvider.signWithBinaryToken" +
                    " Exception: ", e);
           throw new XMLSignatureException(e.getMessage());
        }

        return (signature.getElement());        
    }
    
        /**
     * Verify all the signatures of the WSS xml document
     * @param doc XML dom document whose signature to be verified
     * @param certAlias certAlias alias for Signer's certificate, this is used
                        to search signer's public certificate if it is not
                        presented in ds:KeyInfo
     * @return true if the xml signature is verified, false otherwise
     * @throws XMLSignatureException if problem occurs during verification
     */
    public boolean verifyWSSSignature(Document doc, String certAlias)
                   throws XMLSignatureException {

        if (doc == null) {
            WSSUtils.debug.error("WSSSignatureProvider.verifyWSSSignature: " +
                    "document is null.");
            throw new XMLSignatureException(
                      WSSUtils.bundle.getString("nullInput"));
        }

        try {
            Element wsucontext = com.sun.org.apache.xml.internal.security.utils.
                    XMLUtils.createDSctx(doc, "wsu", WSSConstants.WSU_NS);

            NodeList wsuNodes = (NodeList)XPathAPI.selectNodeList(doc,
                    "//*[@wsu:Id]", wsucontext);

            if(wsuNodes != null && wsuNodes.getLength() != 0) {
               for(int i=0; i < wsuNodes.getLength(); i++) {
                   Element elem = (Element) wsuNodes.item(i);
                   String id = elem.getAttributeNS(WSSConstants.WSU_NS, "Id");
                   if (id != null && id.length() != 0) {
                       IdResolver.registerElementById(elem, id);
                   }
               }
            }
            NodeList aList = (NodeList)XPathAPI.selectNodeList(
                     doc, "//*[@" + "AssertionID" + "]");
            if (aList != null && aList.getLength() != 0) {
                int len = aList.getLength();
                for (int i = 0; i < len; i++) {
                     Element elem = (Element) aList.item(i);
                     String id = elem.getAttribute("AssertionID");
                     if (id != null && id.length() != 0) {
                         IdResolver.registerElementById(elem, id);
                     }
                }
            }

            Element nscontext = com.sun.org.apache.xml.internal.security.utils.
                  XMLUtils.createDSctx (doc,"ds",Constants.SignatureSpecNS);
            NodeList sigElements = XPathAPI.selectNodeList (doc,
                "//ds:Signature", nscontext);
            if (WSSUtils.debug.messageEnabled()) {
                WSSUtils.debug.message("WSSSignatureProvider.verifyWSSSignature"
                      + ": sigElements " + "size = " + sigElements.getLength());
            }
            X509Certificate newcert= keystore.getX509Certificate (certAlias);
            PublicKey key = keystore.getPublicKey (certAlias);
            Element sigElement = null;
            //loop
            for(int i = 0; i < sigElements.getLength(); i++) {
                sigElement = (Element)sigElements.item(i);
                if (WSSUtils.debug.messageEnabled ()) {
                    WSSUtils.debug.message("Sig(" + i + ") = " +
                        XMLUtils.print(sigElement));
                }
                XMLSignature signature = new XMLSignature (sigElement, "");
                signature.addResourceResolver (
                    new com.sun.identity.saml.xmlsig.OfflineResolver ());
                KeyInfo ki = signature.getKeyInfo ();
                PublicKey pk = this.getX509PublicKey(doc, ki);
                if (pk!=null) {


                                    if (signature.checkSignatureValue (pk)) {
                        if (WSSUtils.debug.messageEnabled ()) {
                            WSSUtils.debug.message ("verifyWSSSignature:" +
                                " Signature " + i + " verified");
                        }
                    } else {
                        if(WSSUtils.debug.messageEnabled()) {
                           WSSUtils.debug.message("verifyWSSSignature:" +
                           " Signature Verfication failed");
                        }
                        return false;
                    }
                } else {
                    if (certAlias == null || certAlias.equals ("")) {
                        if(WSSUtils.debug.messageEnabled()) {
                           WSSUtils.debug.message("verifyWSSSignature:" +
                           "Certificate Alias is null");
                        }
                        return false;
                    }
                    if (WSSUtils.debug.messageEnabled ()) {
                        WSSUtils.debug.message ("Could not find a KeyInfo, " +
                        "try to use certAlias");
                    }
                    if (newcert != null) {
                        if (signature.checkSignatureValue (newcert)) {
                            if (WSSUtils.debug.messageEnabled ()) {
                                WSSUtils.debug.message ("verifyWSSSignature:" +
                                        " Signature " + i + " verified");
                            }
                        } else {
                            return false;
                        }
                    } else {
                        if (key != null) {
                            if (signature.checkSignatureValue (key)) {
                                if (WSSUtils.debug.messageEnabled ()) {
                                    WSSUtils.debug.message (
                                    "verifyWSSSignature: Signature " + i +
                                    " verified");
                                }
                            } else {
                                return false;
                            }

                        } else {
                            WSSUtils.debug.error ("Could not find public key"
                                + " based on certAlias to verify signature");
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            WSSUtils.debug.error("verifyWSSSignature Exception: ", ex);
            throw new XMLSignatureException (ex.getMessage ());
        }
    }
}




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
 * $Id: WSSUtils.java,v 1.2 2007-05-17 18:49:19 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.security;





import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Iterator;
import java.math.BigInteger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.xml.namespace.QName;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.
       DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.
       RSAKeyValue;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.Init;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.shared.locale.Locale;
import com.sun.identity.saml.xmlsig.XMLSignatureException;
import com.sun.identity.saml.xmlsig.XMLSignatureManager;
import com.sun.identity.saml.xmlsig.JKSKeyProvider;
import com.sun.identity.saml.assertion.Assertion;
import com.sun.identity.saml.assertion.Subject;
import com.sun.identity.saml.assertion.SubjectConfirmation;
import com.sun.identity.saml.assertion.Statement;
import com.sun.identity.saml.assertion.AuthenticationStatement;
import com.sun.identity.saml.common.SAMLConstants;
import com.sun.identity.saml.common.SAMLUtils;
import com.iplanet.am.util.SystemProperties;
import com.sun.identity.shared.encode.Base64;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.security.SecureRandom;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.MimeHeaders;

/**
 * This class provides util methods for the web services security. 
 */
public class WSSUtils {

     public static ResourceBundle bundle = null;
     public static Debug debug = Debug.getInstance("fmWebServicesSecurity");
     private static XMLSignatureManager xmlSigManager = null;
     
     static {
            bundle = Locale.getInstallResourceBundle("fmWSSecurity");
     }

     /**
      * Returns the certificate present in the security token.
      * @param securityToken the security token.
      * @return the certificate.
      */
     public static X509Certificate getCertificate(SecurityToken securityToken)
             throws SecurityException {

         String tokenType = securityToken.getTokenType();

         if(tokenType.equals(SecurityToken.WSS_SAML_TOKEN)) {
            Element keyInfo = null;
            AssertionToken assertionToken = (AssertionToken)securityToken;
            if(!assertionToken.isSenderVouches()) {
               Assertion assertion = assertionToken.getAssertion();
               keyInfo = getKeyInfo(assertion); 
               return getCertificate(keyInfo);
            }

         } else if(tokenType.equals(SecurityToken.WSS_X509_TOKEN)) {
            BinarySecurityToken binaryToken = 
                      (BinarySecurityToken)securityToken;
            String certValue = binaryToken.getTokenValue();
            StringBuffer xml = new StringBuffer(100);
            xml.append(WSSConstants.BEGIN_CERT);
            xml.append(certValue);
            xml.append(WSSConstants.END_CERT);
            byte[] bytevalue = xml.toString().getBytes();
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream bais = new ByteArrayInputStream(bytevalue);
                return (X509Certificate)cf.generateCertificate(bais);
            } catch (Exception ex) {
                debug.error("WSSUtils.getCertificate:: Unable to retrieve " +
                " certificate from the binary token", ex);
                throw new SecurityException(
                      bundle.getString("cannotRetrieveCert"));
            }
           
         }
         return null;
     }

     private static Element getKeyInfo(Assertion assertion) {
        try {
            AuthenticationStatement authStatement = null;
            Subject subject = null;
            Set statements = assertion.getStatement();
            if (statements == null) {
                debug.error(
                "Assertion does not contain any Statement.");
            }
            if (!(statements.isEmpty())) {
                Iterator iterator =  statements.iterator();
                while (iterator.hasNext()) {
                    Statement statement =(Statement)iterator.next();
                    if (statement.getStatementType()==
                                Statement.AUTHENTICATION_STATEMENT) {
                        authStatement = (AuthenticationStatement) statement;
                        subject = authStatement.getSubject();
                        break;
                    }
                }
            }

            SubjectConfirmation subConfirm = subject.getSubjectConfirmation();
            return subConfirm.getKeyInfo();
        } catch (Exception e) {
            debug.error("getCertificate Exception: ", e);
        }
        return null;
     }

     public static X509Certificate getCertificate(Element keyinfo) {

        X509Certificate cert = null;

        if (debug.messageEnabled()) {
            debug.message("KeyInfo = " + XMLUtils.print(keyinfo));
        }

        Element x509 = (Element) keyinfo.getElementsByTagNameNS(
                                Constants.SignatureSpecNS,
                                SAMLConstants.TAG_X509CERTIFICATE).item(0);

        if (x509 == null) { 
            //noo cert found. try DSA/RSA key
            try {
                PublicKey pk = getPublicKey(keyinfo);
                cert = (X509Certificate) AMTokenProvider.getKeyProvider().
                          getCertificate(pk);
            } catch (Exception e) {
                debug.error("getCertificate Exception: ", e);
            }

        } else {
            String certString = x509.getChildNodes().item(0).getNodeValue();
            cert = getCertificate(certString, null);
        }

        return cert;
    }

    private static PublicKey getPublicKey(Element reference)
    throws XMLSignatureException {

        PublicKey pubKey = null;
        Document doc = reference.getOwnerDocument();
        Element dsaKey = (Element) reference.getElementsByTagNameNS(
                                        Constants.SignatureSpecNS,
                                        SAMLConstants.TAG_DSAKEYVALUE).item(0);
        if (dsaKey != null) { // It's DSAKey
            NodeList nodes = dsaKey.getChildNodes();
            int nodeCount = nodes.getLength();
            if (nodeCount > 0) {
                BigInteger p=null, q=null, g=null, y=null;
                for (int i = 0; i < nodeCount; i++) {
                    Node currentNode = nodes.item(i);
                    if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                        String tagName = currentNode.getLocalName();
                        Node sub = currentNode.getChildNodes().item(0);
                        String value = sub.getNodeValue();
                        value = SAMLUtils.removeNewLineChars(value);
                        BigInteger v = new BigInteger(Base64.decode(value));
                        if (tagName.equals("P")) {
                            p = v;
                        } else if (tagName.equals("Q")) {
                            q = v;
                        } else if (tagName.equals("G")) {
                            g = v;
                        } else if (tagName.equals("Y")) {
                            y = v;
                        } else {
                            throw new XMLSignatureException("Invalid reference");
                        }
                    }
                }
                DSAKeyValue dsaKeyValue = new DSAKeyValue(doc, p, q, g, y);
                try {
                    pubKey = dsaKeyValue.getPublicKey();
                } catch (XMLSecurityException xse) {
                    debug.error("Could not get Public Key from" +
                                        " DSA key value.");
                    throw new XMLSignatureException(
                        bundle.getString("errorObtainPK"));
                }
            }
        } else {
            Element rsaKey =
                (Element) reference.getElementsByTagNameNS(
                                Constants.SignatureSpecNS,
                                SAMLConstants.TAG_RSAKEYVALUE).item(0);
            if (rsaKey != null) { // It's RSAKey
                NodeList nodes = rsaKey.getChildNodes();
                int nodeCount = nodes.getLength();
                BigInteger m=null, e=null;
                if (nodeCount > 0) {
                    for (int i = 0; i < nodeCount; i++) {
                        Node currentNode = nodes.item(i);
                        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                            String tagName = currentNode.getLocalName();
                            Node sub = currentNode.getChildNodes().item(0);
                            String value = sub.getNodeValue();
                            value = SAMLUtils.removeNewLineChars(value);
                            BigInteger v =new BigInteger(Base64.decode(value));
                            if (tagName.equals("Exponent")) {
                                e = v;
                            }
                            else if (tagName.equals("Modulus")){
                                m = v;
                            } else {
                                throw new XMLSignatureException
                                        ("Invalid reference");
                            }
                        }
                    }
                }
                RSAKeyValue rsaKeyValue =
                    new RSAKeyValue(doc,m, e);
                try {
                    pubKey = rsaKeyValue.getPublicKey();
                } catch (XMLSecurityException ex) {
                    debug.error("Could not get Public Key from" +
                                        " RSA key value.");
                    throw new XMLSignatureException(
                                bundle.getString("errorObtainPK"));
                }
            }
        }
        return pubKey;
    }

    /**
     * Get the X509Certificate embedded in SAML Assertion
     * @param assertion SAML assertion
     * @return a X509Certificate
     */
    private static X509Certificate getCertificate(String certString,
                                           String format)
    {
        X509Certificate cert = null;

        try {
            if (debug.messageEnabled()) {
                debug.message("getCertificate(Assertion) : " +
                        certString);
            }
            StringBuffer xml = new StringBuffer(100);
            xml.append(SAMLConstants.BEGIN_CERT);
            xml.append(certString);
            xml.append(SAMLConstants.END_CERT);

            byte[] barr = null;
            barr = (xml.toString()).getBytes();

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream bais = new ByteArrayInputStream(barr);

            if ((format !=null) &&
                format.equals(SAMLConstants.TAG_PKCS7)) { // PKCS7 format
                Collection c = cf.generateCertificates(bais);
                Iterator i = c.iterator();
                while (i.hasNext()) {
                    cert = (java.security.cert.X509Certificate) i.next();
                }
            } else { //X509:v3 format
                while (bais.available() > 0) {
                    cert = (java.security.cert.X509Certificate)
                                cf.generateCertificate(bais);
                }
            }
        } catch (Exception e) {
            debug.error("getCertificate Exception: ", e);
        }
        return cert;
    }

    public static SOAPMessage toSOAPMessage(Document document) {
        try {
            MessageFactory msgFactory = MessageFactory.newInstance();
            MimeHeaders mimeHeaders = new MimeHeaders();
            mimeHeaders.addHeader("Content-Type", "text/xml");

            String xmlStr = print(document);
            return msgFactory.createMessage(mimeHeaders,
                   new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

   public static String print(Node node) {
       try {
           TransformerFactory tFactory =
                TransformerFactory.newInstance();
           Transformer transformer = tFactory.newTransformer();
           transformer.setOutputProperty("omit-xml-declaration", "yes");
           DOMSource source = new DOMSource(node);
           ByteArrayOutputStream os = new ByteArrayOutputStream(2000);
           StreamResult result = new StreamResult(os);
           transformer.transform(source, result);
           return os.toString();
       } catch (Exception ex) {
           ex.printStackTrace();
           return null;
       }
    }

    public static Element prependChildElement(
    Element parent,
    Element child,
    boolean addWhitespace,
    Document doc) {

        Node firstChild = parent.getFirstChild();
        if (firstChild == null) {
            parent.appendChild(child);
        } else {
            parent.insertBefore(child, firstChild);
        }

        if (addWhitespace) {
            Node whitespaceText = doc.createTextNode("\n");
            parent.insertBefore(whitespaceText, child);
        }
        return child;
    }

    public static Node getDirectChild(Node fNode, 
                 String localName,  String namespace) {
        for (Node currentChild = fNode.getFirstChild(); currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            if (localName.equals(currentChild.getLocalName())
                    && namespace.equals(currentChild.getNamespaceURI())) {
                return currentChild;
            }
        }
        return null;
    }
    
    public static XMLSignatureManager getXMLSignatureManager() {

        if (xmlSigManager == null) {
	    synchronized (XMLSignatureManager.class) {
		if (xmlSigManager == null) {		    
	            xmlSigManager = XMLSignatureManager.getInstance(
                          new JKSKeyProvider(),
                          new WSSSignatureProvider());	    
		}
	    }
	}
        return xmlSigManager;        	
    }
    
    /**
     * Returns corresponding Authentication method URI to be set in Assertion.
     * @param authModuleName name of the authentication module used to
     *          authenticate the user.
     * @return String corresponding Authentication Method URI to be set in
     *          Assertion.
     */
    public static String getAuthMethodURI(String authModuleName) {
        if (authModuleName == null) {
            return null;
        }

        if (authModuleName.equalsIgnoreCase(SAMLConstants.AUTH_METHOD_CERT)) {
            return SAMLConstants.AUTH_METHOD_CERT_URI;
        }
        if (authModuleName.equalsIgnoreCase(SAMLConstants.AUTH_METHOD_KERBEROS))
        {
            return SAMLConstants.AUTH_METHOD_KERBEROS_URI;
        }
        if (SAMLConstants.passwordAuthMethods.contains(
            authModuleName.toLowerCase()))
        {
            return SAMLConstants.AUTH_METHOD_PASSWORD_URI;
        }
        if (SAMLConstants.tokenAuthMethods.contains(
            authModuleName.toLowerCase()))
        {
            return SAMLConstants.AUTH_METHOD_HARDWARE_TOKEN_URI;
        } else {
            StringBuffer sb = new StringBuffer(100);
            sb.append(SAMLConstants.AUTH_METHOD_URI_PREFIX).
                        append(authModuleName);
            return sb.toString();
        }
    }

}

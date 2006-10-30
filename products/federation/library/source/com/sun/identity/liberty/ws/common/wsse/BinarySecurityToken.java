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
 * $Id: BinarySecurityToken.java,v 1.1 2006-10-30 23:14:47 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.liberty.ws.common.wsse;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.saml.common.SAMLUtils;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.HashMap;
import com.sun.identity.shared.locale.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import javax.xml.namespace.QName;

/**
 * The class <code>BinarySecurityToken</code> provides interface to parse and
 * create X.509 Security Token depicted by Web Service Security : X.509 
 * Certificate Token Profile and Liberty ID-WSF Security Mechanisms 
 * specifications.
 *  <p>The following schema fragment specifies the expected content within the BinarySecurityToken object.
 * <p>
 * <pre>
 * &lt;element name="BinarySecurityToken" type="wsse:BinarySecurityTokenType/>
 * &lt;complexType name="BinarySecurityTokenType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://schemas.xmlsoap.org/ws/2003/06/secext>EncodedString">
 *       &lt;attribute name="ValueType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * &lt;xsd:complexType name="EncodedString">
 *   &lt;xsd:simpleContent>
 *     &lt;xsd:extension base="wsse:AttributedString">
 *       &lt;xsd:attribute name="EncodingType" type="xsd:QName"/>
 *     &lt;/xsd:extension>
 *   &lt;/xsd:simpleContent>
 * &lt;/xsd:complexType>
 * &lt;xsd:complexType name="AttributedString">
 *   &lt;xsd:simpleContent>
 *     &lt;xsd:extension base="xsd:string">
 *       &lt;xsd:attribute ref="wsu:Id"/>
 *         &lt;xsd:anyAttribute namespace="##other" processContents="lax"/>
 *     &lt;/xsd:extension>
 *   &lt;/xsd:simpleContent>
 * &lt;/xsd:complexType>
 * </pre>
 * 
 * @supported.api
 */
public class BinarySecurityToken {

    private String value = null;
    private QName valueType = null;
    private QName encodingType = null;
    private String id = null;
    private String xmlString = null;
    private static ResourceBundle bundle = Locale.getInstallResourceBundle(
                                            "libBinarySecurityToken");
    private static Debug debug = Debug.getInstance("libBinarySecurityToken");

    // constants for WSS namespace & tag
    /**
     * iPlanet-PUBLIC-STATIC
     */ 
    private static final String WSSE_NS = 
        "http://schemas.xmlsoap.org/ws/2003/06/secext";
    private static final String WSU_NS = 
        "http://schemas.xmlsoap.org/ws/2003/06/utility";
    /**
     * iPlanet-PUBLIC-STATIC
     */ 
    private static final String WSSE = "wsse";
    private static final String WSU = "wsu";
    private static final String BINARY_SECURITY_TOKEN = "BinarySecurityToken";
    private static final String ENCODING_TYPE = "EncodingType";
    private static final String VALUE_TYPE = "ValueType";
    private static final String ID = "Id";
    private static final String XML_NS = "xmlns";

    /**
     * Default constructor
     */
    private BinarySecurityToken () {}

    /**
     * Constructor.
     * @param value the value must be the specified <code>valueType</code>
     *        and encoded in the specified <code>encodingType</code>.
     *
     * @param valueType type of the value
     * @param encodingType encoding type of the value
     * @exception Exception if any input parameter is null
     * @supported.api
     */
    public BinarySecurityToken (String value,
                                QName valueType,
                                QName encodingType) 
    throws Exception
    {
        if (value == null || valueType == null || encodingType == null) {
            debug.error("BinarySecurityToken: null input param");
            throw new Exception(bundle.getString("nullInputParameter")) ;
        }
        this.value = value;
        this.valueType = valueType;
        this.encodingType = encodingType;
        this.id = SAMLUtils.generateID();
    }

    /**
     * Constructor.
     * @param token Binary Security Token Element
     * @exception Exception if token Element is not a valid binary 
     *     security token 
     * @supported.api
     */
    public BinarySecurityToken(Element token) 
        throws Exception {
        if (token == null) {
            debug.error("BinarySecurityToken: null input token");
            throw new Exception(bundle.getString("nullInputParameter")) ;
        }

        // check element name            
        String elementName = token.getLocalName();
        if (elementName == null)  {
            debug.error("BinarySecurityToken: local name missing");
            throw new Exception(bundle.getString("nullInput")) ;
        }
        if (!(elementName.equals(BINARY_SECURITY_TOKEN)))  {
            debug.error("BinarySecurityToken: invalid root element");
            throw new Exception(bundle.getString("invalidElement") + 
                ":" + elementName) ;   
        }

        // check attributes
        NamedNodeMap nm = token.getAttributes();
        if (nm == null) {
            debug.error("BinarySecurityToken: missing attr in element");
            throw new Exception(bundle.getString("missingAttribute"));
        }
        int len = nm.getLength();
        for (int i = 0; i < len; i++) {
            Attr attr = (Attr) nm.item(i);
            String localName = attr.getLocalName();
            if (localName == null) {
                // exception?? Ignore for now
                if (debug.messageEnabled()) {
                    debug.message("BST.Elemement, invalid attr " + localName);
                } 
                continue;
            }
            // check Id/EncodingType/ValueType attribute
            if (localName.equals(ID)) {
                this.id = attr.getValue();
            } else if (localName.equals(ENCODING_TYPE)) {
                // no namespace match done here
                encodingType = (QName) 
                    encodingMap.get(trimPrefix(attr.getValue()));
            } else if (localName.equals(VALUE_TYPE)) {
                // no namespace match done here
                valueType = (QName) valueMap.get(trimPrefix(attr.getValue()));
            }
        }
 
        if (id == null || id.length() == 0) {
            debug.error("BinarySecurityToken: ID missing");
            throw new Exception(bundle.getString("missingAttribute") +
                    " : " + ID);
        }

        if (encodingType == null) {
            debug.error("BinarySecurityToken: encoding type missing");
            throw new Exception(bundle.getString("missingAttribute") +
                " : " + ENCODING_TYPE);
        }

        if (valueType == null) {
            debug.error("BinarySecurityToken: valueType missing");
            throw new Exception(bundle.getString("missingAttribute") +
            " : " + VALUE_TYPE);
        }

        // get X509 certificate value
        try {
            this.value = token.getFirstChild().getNodeValue().trim();
        } catch (Exception e) {
            debug.error("BinarySecurityToken: unable to get value", e);
            this.value = null;
        }

        if (value == null) {
            debug.error("BinarySecurityToken: value missing");
            throw new Exception(bundle.getString("missingValue"));
        }
 
        // save the original string for toString()
        xmlString = XMLUtils.print(token);

    }

    /**
     * trim prefix and get the value, e.g, for wsse:X509v3 will return X509v3 
     */
    private String trimPrefix(String val) {
        int pos = val.indexOf(":");
        if (pos == -1) {
            return val;
        } else if (pos == val.length()) {
            return "";
        } else {
            return val.substring(pos+1);
        } 
    }

    /**
     * Gets encoding type for the token.
     *
     * @return encoding type for the token. 
     * @supported.api
     */
    public QName getEncodingType() {
        return encodingType;
    }

    /**
     * Gets value type for the token.
     *
     * @return value type for the token. 
     * @supported.api
     */
    public QName getValueType() {
        return valueType;
    }

    /**
     * Gets id attribute for the tokens.
     *
     * @return id attribute for the token.
     * @supported.api
     */
    public String getId() {
        return id;
    }

    /**
     * Gets value of the token.
     *
     * @return value of the token.
     * @supported.api
     */
    public String getTokenValue() { 
        return value;
    }

    /**
     * Returns a String representation of the token.
     * @return A string containing the valid XML for this element
     * @supported.api
     */
    public String toString() {
        if (xmlString == null) {
            xmlString = "<" + WSSE + ":" + BINARY_SECURITY_TOKEN + " " +
                XML_NS + ":" + WSSE + "=\"" + WSSE_NS + "\" " + 
                XML_NS + ":" + WSU + "=\"" + WSU_NS + "\" " + WSU + ":" +
                ID + "=\"" + id + "\" " + VALUE_TYPE + "=\"" + 
                WSSE + ":" + valueType.getLocalPart() + "\" " +
                ENCODING_TYPE + "=\"" + WSSE + ":" + 
                encodingType.getLocalPart() + "\">\n" + value.toString() + 
                "\n" + "</" + WSSE + ":" + BINARY_SECURITY_TOKEN + ">\n"; 
        }
        return xmlString;
    }

    /**
     * The <code>X509V3</code> value type indicates that
     * the value name given corresponds to a X509 Certificate.
     * @supported.api
     */
    public static final QName X509V3 = new QName(WSSE_NS, "X509v3", WSSE);

    /**
     * The <code>KERBEROSV5TGT</code> value type indicates that
     * the value name given corresponds to a Kerberos V5 TGT.
     */
    public static final QName KERBEROSV5TGT = 
        new QName(WSSE_NS, "Kerberosv5TGT", WSSE);

    /**
     * The <code>KERBEROSV5ST</code> value type indicates
     * that the value name given corresponds to a Kerberos V5 service ticket.
     */
    public static final QName KERBEROSV5ST = 
        new QName(WSSE_NS, "Kerberosv5ST", WSSE);

    /**
     * The <code>PKCS7</code> value type indicates
     * that the value name given corresponds to a
     * PKCS7 object.
     * @supported.api
     */
    public static final QName PKCS7 = new QName(WSSE_NS, "PKCS7", WSSE);

    /**
     * The <code>PKIPATH</code> value type indicates
     * that the value name given corresponds to a
     * PKI Path object.
     * @supported.api
     */
    public static final QName PKIPath = new QName(WSSE_NS, "PKIPath", WSSE);

    // map from string to ValueType object
    static Map valueMap = new HashMap(); 
    static {
        valueMap.put(X509V3.getLocalPart(), X509V3);
        valueMap.put(PKIPath.getLocalPart(), PKIPath);
        valueMap.put(PKCS7.getLocalPart(), PKCS7);
        valueMap.put(KERBEROSV5ST.getLocalPart(), KERBEROSV5ST);
        valueMap.put(KERBEROSV5TGT.getLocalPart(), KERBEROSV5TGT);
    }

    /** 
     * The <code>BASE64BINARY</code> encoding type indicates that the encoding
     * name given corresponds to base64 encoding of a binary value.
     * @supported.api
     */
    public static final QName BASE64BINARY = 
        new QName(WSSE_NS, "Base64Binary", WSSE);
        
    /**
     * The <code>HEXBINARY</code> encoding type indicates that
     * the encoding name given corresponds to Hex encoding of
     * a binary value.
     * @supported.api
     */
    public static final QName HEXBINARY =
        new QName(WSSE_NS, "HexBinary", WSSE);
        
    // map from string to EncodingType object 
    static Map encodingMap = new HashMap(); 
    static {
        encodingMap.put(HEXBINARY.getLocalPart(), HEXBINARY);
        encodingMap.put(BASE64BINARY.getLocalPart(), BASE64BINARY);
    } 
}

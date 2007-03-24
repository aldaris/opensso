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
 * $Id: StatusCodeImpl.java,v 1.1 2007-03-24 01:25:57 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.context.impl;

import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.StatusCode;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>StatusCode</code> element is a container of 
 * one or more <code>StatusCode</code>s issuded by authorization authority.
 * @supported.all.api
 * <p/>
 * <pre>
 *
 * Schema:
 *  &lt;xs:element name="StatusCode" type="xacml-context:StatusCodeType"/>
 *  &lt;xs:complexType name="StatusCodeType">
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="xacml-context:StatusCode" minOccurs="0"/>
 *      &lt;xs:sequence>
 *      &lt;xs:attribute name="Value" type="xs:anyURI" use="required"/>
 *  &lt;xs:complexType>
 * </pre>
 */
public class StatusCodeImpl implements StatusCode {


    String value = null;
    String minorCodeValue = null;
    private boolean mutable = true;

    /** 
     * Constructs a <code>StatusCode</code> object
     */
    public StatusCodeImpl() throws XACML2Exception {
    }

    /** 
     * Constructs a <code>StatusCode</code> object from an XML string
     *
     * @param xml string representing a <code>StatusCode</code> object
     * @throws SAMLException if the XML string could not be processed
     */
    public StatusCodeImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                "StatusCodeImpl.processElement(): invalid XML input");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "errorObtainingElement"));
        }
    }

    /** 
     * Constructs a <code>StatusCode</code> object from an XML DOM element
     *
     * @param element XML DOM element representing a <code>StatusCode</code> 
     * object
     *
     * @throws SAMLException if the DOM element could not be processed
     */
    public StatusCodeImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }

    /**
     * Returns the <code>value</code> of this object
     *
     * @return the <code>value</code> of this object
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the <code>value</code> of this object
     *
     * @exception XACML2Exception if the object is immutable
     */
    public void setValue(String value) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }

        if (value == null) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid")); //i18n
        }

        if (!XACML2SDKUtils.isValidStatusCode(value)) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("invalid_value")); //i18n
        }
        this.value = value;
    }

    /**
     * Returns the <code>minorCodeValue</code> of this object
     *
     * @return the <code>minorCodeValue</code> of this object
     */
    public String getMinorCodeValue() {
        return minorCodeValue;
    }

    /**
     * Sets the <code>minorCodeValue</code> of this object
     *
     * @exception XACML2Exception if the object is immutable
     */
    public void setMinorCodeValue(String minorCodeValue) 
            throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }

        if (value == null) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid")); //i18n
        }

        if (!XACML2SDKUtils.isValidMinorStatusCode(value)) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("invalid_value")); //i18n
        }
        this.minorCodeValue = minorCodeValue;
    }

   /**
    * Returns a string representation
    *
    * @return a string representation
    * @exception XACML2Exception if conversion fails for any reason
    */
    public String toXMLString() throws XACML2Exception {
        return toXMLString(true, false);
    }

   /**
    * Returns a string representation
    * @param includeNSPrefix Determines whether or not the namespace qualifier
    *        is prepended to the Element when converted
    * @param declareNS Determines whether or not the namespace is declared
    *        within the Element.
    * @return a string representation
    * @exception XACML2Exception if conversion fails for any reason
     */
    public String toXMLString(boolean includeNSPrefix, boolean declareNS)
            throws XACML2Exception {
        StringBuffer sb = new StringBuffer(2000);
        String nsPrefix = "";
        String nsDeclaration = "";
        if (includeNSPrefix) {
            nsPrefix = XACML2Constants.CONTEXT_PREFIX;
        }
        if (declareNS) {
            nsDeclaration = XACML2Constants.CONTEXT_DECLARE_STR;
        }
        sb.append("<").append(nsPrefix)
                .append(XACML2Constants.STATUS_CODE_ELEMENT)
                .append(" ")
                .append(nsDeclaration);
        if (value != null) {
            sb.append(XACML2Constants.VALUE_ATTRIBUTE)
            .append("=")
            .append(XACML2SDKUtils.quote(value));
        }
        sb.append(">");
        if (minorCodeValue != null) {
            sb.append("<").append(nsPrefix)
                    .append(XACML2Constants.STATUS_CODE_ELEMENT)
                    .append(" ")
                    .append(nsDeclaration)
                    .append(XACML2Constants.VALUE_ATTRIBUTE)
                    .append("=")
                    .append(XACML2SDKUtils.quote(minorCodeValue))
                    .append(">");
                    sb.append("</").append(nsPrefix)
                            .append(XACML2Constants.STATUS_CODE_ELEMENT)
                            .append(">");
        }
        sb.append("</").append(nsPrefix).append(XACML2Constants.STATUS_CODE_ELEMENT)
                .append(">");
        return sb.toString();
    }

   /**
    * Checks if the object is mutable
    *
    * @return <code>true</code> if the object is mutable,
    *         <code>false</code> otherwise
    */
    public boolean isMutable() {
        return mutable;
    }
    
   /**
    * Makes the object immutable
    */
    public void makeImmutable() {
        mutable = false;
    }

    private void processElement(Element element) throws XACML2Exception {
        if (element == null) {
            XACML2SDKUtils.debug.error(
                "StatusMessageImpl.processElement(): invalid root element");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_element"));
        }
        String elemName = element.getLocalName();
        if (elemName == null) {
            XACML2SDKUtils.debug.error(
                "StatusMessageImpl.processElement(): local name missing");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_local_name"));
        }

        if (!elemName.equals(XACML2Constants.STATUS_CODE_ELEMENT)) {
            XACML2SDKUtils.debug.error(
                    "StatusMessageImpl.processElement(): invalid local name " 
                    + elemName);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                    "invalid_local_name"));
        }
        String attrValue = element.getAttribute(XACML2Constants.VALUE_ATTRIBUTE);
        if ((attrValue == null) || (attrValue.length() == 0)) {
            XACML2SDKUtils.debug.error(
                "StatusCodeImpl.processElement(): statuscode missing");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_status_code")); //i18n
        } 
        if (!XACML2SDKUtils.isValidStatusMessage(attrValue.trim())) {
            throw new XACML2Exception(
                    XACML2SDKUtils.bundle.getString("invalid_value")); //i18n
        } else {
            this.value = attrValue;
        }
        //process child StatusCode element
        NodeList nodes = element.getChildNodes();
        int numOfNodes = nodes.getLength();
        List childElements = new ArrayList(); 
        int i = 0;
        while (i < numOfNodes) { 
            Node child = (Node) nodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                childElements.add(child);
            }
           i++;
        }
        int childCount = childElements.size();
        if (childCount > 1) {
            XACML2SDKUtils.debug.error(
                "ResultImpl.processElement(): invalid child element count: " 
                        + childCount);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_child_count")); //FIXME: add i18n key
        }
        if (childCount == 1) {
            Element childElement = (Element)childElements.get(0);
            elemName = childElement.getLocalName();
            if (elemName == null) {
                XACML2SDKUtils.debug.error(
                    "StatusMessageImpl.processElement(): local name missing");
                throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                    "missing_local_name"));
            }

            if (!elemName.equals(XACML2Constants.STATUS_CODE_ELEMENT)) {
                XACML2SDKUtils.debug.error(
                        "StatusMessageImpl.processElement(): invalid local name " 
                        + elemName);
                throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                        "invalid_local_name"));
            }
            attrValue = childElement.getAttribute(XACML2Constants.VALUE_ATTRIBUTE);
            if ((attrValue == null) || (attrValue.length() == 0)) {
                XACML2SDKUtils.debug.error(
                    "StatusCodeImpl.processElement(): minor statuscode missing");
                throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                    "missing_minor_status_code")); //i18n
            } 
            if (!XACML2SDKUtils.isValidStatusMessage(attrValue.trim())) {
                throw new XACML2Exception(
                        XACML2SDKUtils.bundle.getString("invalid_value")); //i18n
            } else {
                this.minorCodeValue = attrValue;
            }
        } else {
        }
    }

}

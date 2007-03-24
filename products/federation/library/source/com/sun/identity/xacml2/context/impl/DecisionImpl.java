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
 * $Id: DecisionImpl.java,v 1.1 2007-03-24 01:25:54 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.context.impl;

import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.Decision;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The <code>Decision</code> element is a container of 
 * one or more <code>Decision</code>s issued by policy decision point
 * @supported.all.api
 * <p/>
 * Schema:
 * <pre>
 * <xs:simpleType name="DecisionType">
 *     <xs:restriction base="xs:string">
 *         <xs:enumeration value="Permit"/>
 *         <xs:enumeration value="Deny"/>
 *         <xs:enumeration value="Indeterminate"/>
 *         <xs:enumeration value="NotApplicable"/>
 *     </xs:restriction>
 * </xs:simpleType>
 * </pre>
 * 
 */
public class DecisionImpl implements Decision {

    private String value = null;
    private boolean mutable = true;

    /** 
     * Constructs a <code>Decision</code> object
     */
    public DecisionImpl() throws XACML2Exception {
    }

    /** 
     * Constructs a <code>Decision</code> object from an XML string
     *
     * @param xml string representing a <code>Decision</code> object
     * @throws SAMLException if the XML string could not be processed
     */
    public DecisionImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                "DecisionImpl.processElement(): invalid XML input");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "errorObtainingElement"));
        }
    }

    /** 
     * Constructs a <code>Decision</code> object from an XML DOM element
     *
     * @param element XML DOM element representing a <code>Decision</code> 
     * object
     *
     * @throws SAMLException if the DOM element could not be processed
     */
    public DecisionImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }

    /**
     * Returns the <code>value</code>s of this object
     *
     * @return the <code>value</code>s of this object
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the <code>value</code>s of this object
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

        if (!XACML2SDKUtils.isValidDecision(value)) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("invalid_value")); //i18n
        }
        this.value = value; 
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
        if (declareNS) {
            nsDeclaration = XACML2Constants.CONTEXT_DECLARE_STR;
        }
        if (includeNSPrefix) {
            nsPrefix = XACML2Constants.CONTEXT_PREFIX;
        }
        sb.append("<").append(nsPrefix).append(XACML2Constants.DECISION_ELEMENT)
                .append(nsDeclaration).append(">");
        if (value != null) {
            sb.append(value);
        }
        sb.append("</").append(nsPrefix).append(XACML2Constants.DECISION_ELEMENT)
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
                "DecisionImpl.processElement(): invalid root element");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_element"));
        }
        String elemName = element.getLocalName();
        if (elemName == null) {
            XACML2SDKUtils.debug.error(
                "DecisionImpl.processElement(): local name missing");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_local_name"));
        }

        if (!elemName.equals(XACML2Constants.DECISION_ELEMENT)) {
            XACML2SDKUtils.debug.error(
                    "DecisionImpl.processElement(): invalid local name " 
                    + elemName);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                    "invalid_local_name"));
        }
        String elementValue = element.getTextContent();
        if (elementValue == null) {
            throw new XACML2Exception(
                    XACML2SDKUtils.bundle.getString("null_not_valid")); //i18n
        }
        if (!XACML2SDKUtils.isValidDecision(elementValue.trim())) {
            throw new XACML2Exception(
                    XACML2SDKUtils.bundle.getString("invalid_value")); //i18n
        } else {
            this.value = elementValue;
        }
    }
    
}

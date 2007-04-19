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
 * $Id: StatusDetailImpl.java,v 1.2 2007-04-19 19:14:29 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.context.impl;

import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.StatusDetail;

import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The <code>StatusCode</code> element is a container of 
 * one or more <code>Status</code>s issuded by authorization authority.
 * @supported.all.api
 * <p/>
 *  Schema:
 * <pre>
 *  &lt;xs:element name="StatusDetail" type="xacml-context:StatusDetailType"/>
 *  &lt;xs:complexType name="StatusDetailType">
 *      &lt;xs:sequence>
 *      &lt;xs:any namespace="##any" processContents="lax" 
 *          minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:sequence>
 *  &lt;xs:complexType>
 * >/pre>
 */
public class StatusDetailImpl implements StatusDetail {


    private Element element;
    private boolean mutable = true;

    /** 
     * Constructs a <code>StatusDetail</code> object
     */
    public StatusDetailImpl() throws XACML2Exception {
        String xmlString = "<xacml-context:StatusDetail xmlns:xacml-context="
                + "\"urn:oasis:names:tc:xacml:2.0:context:schema:cd:04\"/>";
        element = new StatusDetailImpl(xmlString).getElement();
    }

    /** 
     * Constructs a <code>StatusDetail</code> object from an XML string
     *
     * @param xml string representing a <code>StatusDetail</code> object
     * @throws SAMLException if the XML string could not be processed
     */
    public StatusDetailImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                "StatusDetailImpl.processElement(): invalid XML input");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "errorObtainingElement"));
        }
    }

    /** 
     * Constructs a <code>StatusDetail</code> object from an XML DOM element
     *
     * @param element XML DOM element representing a <code>StatusDetail</code> 
     * object
     *
     * @throws SAMLException if the DOM element could not be processed
     */
    public StatusDetailImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }

        if (element == null) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid")); //i18n
        }

        String elemName = element.getLocalName();
        if (elemName == null) {
            XACML2SDKUtils.debug.error(
                "StatusMessageImpl.processElement(): local name missing");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_local_name"));
        }
        this.element = element;
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
        String xmlString = null;
        String nsPrefix = "";
        String nsDeclaration = "";
        if (includeNSPrefix) {
            nsPrefix = XACML2Constants.CONTEXT_PREFIX;
        }
        if (declareNS) {
            nsDeclaration = XACML2Constants.CONTEXT_DECLARE_STR;
        }
        if (element != null) {
            if (includeNSPrefix && (element.getPrefix() == null)) {
                element.setPrefix(nsPrefix);
            }
            if(declareNS) {
                StringTokenizer st = new StringTokenizer(nsDeclaration, "=");
                String nsName = st.nextToken();
                String nsUri = st.nextToken();
                if (element.getAttribute(nsName) == null) {
                    element.setAttribute(nsName, nsUri);
                }
            }
            xmlString = XMLUtils.print(element) + "\n";
        } else {
            StringBuffer sb = new StringBuffer(2000);
            sb.append("<").append(nsPrefix)
                    .append(XACML2Constants.STATUS_DETAIL_ELEMENT)
                    .append(" ")
                    .append(nsDeclaration)
                    .append(">")
                    .append("</")
                    .append(nsPrefix)
                    .append(XACML2Constants.STATUS_DETAIL_ELEMENT)
                    .append(">\n");
            xmlString = sb.toString();
        }
        return xmlString;
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

        if (!elemName.equals(XACML2Constants.STATUS_DETAIL_ELEMENT)) {
            XACML2SDKUtils.debug.error(
                    "StatusMessageImpl.processElement(): invalid local name " 
                    + elemName);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                    "invalid_local_name"));
        }
        this.element = element;
    }

}

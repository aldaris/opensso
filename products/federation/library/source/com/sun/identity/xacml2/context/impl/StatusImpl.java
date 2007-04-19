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
 * $Id: StatusImpl.java,v 1.2 2007-04-19 19:14:29 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.context.impl;

import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;

import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Status;
import com.sun.identity.xacml2.context.StatusCode;
import com.sun.identity.xacml2.context.StatusMessage;
import com.sun.identity.xacml2.context.StatusDetail;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>Status</code> element is a container of 
 * one or more <code>Status</code>s issuded by authorization authority.
 * @supported.all.api
 * <p/>
 * <pre>
 *
 * Schema:
 * &lt;xs:complexType name="StatusType">
 *     &lt;xs:sequence>
 *         &lt;xs:element ref="xacml-context:StatusCode"/>
 *         &lt;xs:element ref="xacml-context:StatusMessage" minOccurs="0"/>
 *         &lt;xs:element ref="xacml-context:StatusDetail" minOccurs="0"/>
 *     &lt;xs:sequence>
 * &lt;xs:complexType>
 */
public class StatusImpl implements Status {

    private StatusCode statusCode;
    private StatusMessage statusMessage;
    private StatusDetail statusDetail;
    private boolean mutable = true;

    /** 
     * Constructs a <code>Status</code> object
     */
    public StatusImpl() throws XACML2Exception {
    }

    /** 
     * Constructs a <code>Status</code> object from an XML string
     *
     * @param xml string representing a <code>Status</code> object
     * @throws SAMLException if the XML string could not be processed
     */
    public StatusImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                "StatusImpl.processElement(): invalid XML input");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "errorObtainingElement"));
        }
    }

    /** 
     * Constructs a <code>Status</code> object from an XML DOM element
     *
     * @param element XML DOM element representing a <code>Status</code> 
     * object
     *
     * @throws SAMLException if the DOM element could not be processed
     */
    public StatusImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }


    /**
     * Returns the <code>StatusCode</code> of this object
     *
     * @return the <code>StatusCode</code> of this object
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the <code>StatusCode</code> of this object
     *
     * @exception XACML2Exception if the object is immutable
     */
    public void setStatusCode(StatusCode statusCode) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }

        if (statusCode == null) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid")); //i18n
        }
        this.statusCode = statusCode;
    }

    /**
     * Returns the <code>StatusMessage</code> of this object
     *
     * @return the <code>StatusMessage</code> of this object
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the <code>StatusMessage</code> of this object
     *
     * @exception XACML2Exception if the object is immutable
     */
    public void setStatusMessage(StatusMessage statusMessage) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }
        this.statusMessage = statusMessage;
    }

    /**
     * Returns the <code>StatusDetail</code> of this object
     *
     * @return the <code>StatusDetail</code> of this object
     */
    public StatusDetail getStatusDetail() {
        return statusDetail;
    }

    /**
     * Sets the <code>StatusDetail</code> of this object
     *
     * @exception XACML2Exception if the object is immutable
     */
    public void setStatusDetail(StatusDetail statusDetail) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }
        this.statusDetail = statusDetail;
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
        sb.append("<").append(nsPrefix).append(XACML2Constants.STATUS_ELEMENT).
                append(nsDeclaration).append(">\n");
        if (statusCode != null) {
            sb.append(statusCode.toXMLString(includeNSPrefix, false)); 
        }
        if (statusMessage != null) {
            sb.append(statusMessage.toXMLString(includeNSPrefix, false)); 
        }
        if (statusDetail != null) {
            sb.append(statusDetail.toXMLString(includeNSPrefix, false));
        }
        sb.append("</").append(nsPrefix).append(XACML2Constants.STATUS_ELEMENT)
                .append(">\n");
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
        if (mutable) {
            if (statusCode != null) {
                statusCode.makeImmutable();
            }
            if (statusMessage != null) {
                statusMessage.makeImmutable();
            }
            if (statusDetail != null) {
                statusDetail.makeImmutable();
            }
            mutable = false;
        }
    }

    private void processElement(Element element) throws XACML2Exception {
        if (element == null) {
            XACML2SDKUtils.debug.error(
                "StatusImpl.processElement(): invalid root element");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_element"));
        }
        String elemName = element.getLocalName();
        if (elemName == null) {
            XACML2SDKUtils.debug.error(
                "StatusImpl.processElement(): local name missing");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_local_name"));
        }

        if (!elemName.equals(XACML2Constants.STATUS_ELEMENT)) {
            XACML2SDKUtils.debug.error(
                "StatusImpl.processElement(): invalid local name " + elemName);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_local_name"));
        }

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
        if (childCount < 1) {
            XACML2SDKUtils.debug.error(
                "StatusImpl.processElement(): invalid child element count: " 
                        + childCount);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_child_count")); //FIXME: add i18n key
        } else if (childCount > 3) {
            XACML2SDKUtils.debug.error(
                "StatusImpl.processElement(): invalid child element count: " 
                        + childCount);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_child_count")); //FIXME: add i18n key
        }
        Element firstChild = (Element)childElements.get(0);
        String firstChildName = firstChild.getLocalName();
        if (firstChildName.equals(XACML2Constants.STATUS_CODE_ELEMENT)) {
            statusCode =  ContextFactory.getInstance()
                    .createStatusCode(firstChild);
        } else {
            XACML2SDKUtils.debug.error(
                "StatusImpl.processElement(): invalid first child element: " 
                        + firstChildName);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_first_child")); //FIXME: add i18n key
        }
        //process statusMessage element
        if (childCount > 1) {
            Element secondChild = (Element)childElements.get(1);
            String secondChildName = secondChild.getLocalName();
            if (secondChildName.equals(
                        XACML2Constants.STATUS_MESSAGE_ELEMENT)) {
                statusMessage =  ContextFactory.getInstance()
                        .createStatusMessage(secondChild);

            } else if (secondChildName.equals(
                    XACML2Constants.STATUS_DETAIL_ELEMENT)) {
                if (childCount == 2) {
                    statusDetail =  ContextFactory.getInstance()
                            .createStatusDetail(secondChild);
                } else {
                    XACML2SDKUtils.debug.error(
                        "StatusImpl.processElement(): "
                                + "invalid second child element: " 
                                + secondChildName);
                    throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                        "invalid_second_child")); //FIXME: add i18n key
                }
            }
            if (childCount > 2) {
                Element thirdChild = (Element)childElements.get(2);
                String thirdChildName = thirdChild.getLocalName();
                if (thirdChildName.equals(
                            XACML2Constants.STATUS_DETAIL_ELEMENT)) {
                    statusDetail =  ContextFactory.getInstance()
                            .createStatusDetail(thirdChild);
                } else {
                    XACML2SDKUtils.debug.error(
                        "StatusImpl.processElement(): invalid third child element: " 
                                + thirdChildName);
                    throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                        "invalid_third_child")); //FIXME: add i18n key
                }
            }
        }
    }

}

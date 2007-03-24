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
 * $Id: ResultImpl.java,v 1.1 2007-03-24 01:25:56 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.context.impl;

import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Result;
import com.sun.identity.xacml2.context.Decision;
import com.sun.identity.xacml2.context.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>Result</code> element contains decision, status and obligations
 * per resource id
 *
 * <p/>
 * schema
 * <pre>
 *  &lt;xs:complexType name="ResultType">
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="xacml-context:Decision"/>
 *          &lt;xs:element ref="xacml-context:Status" minOccurs="0"/>
 *          &lt;xs:element ref="xacml:Obligations" minOccurs="0"/>
 *      &lt;xs:sequence>
 *      &lt;xs:attribute name="ResourceId" type="xs:string" use="optional"/>
 *  &lt;xs:complexType>
 *
 * </pre>
 */
public class ResultImpl implements Result {

    private String resourceId = null; //optional
    private Decision decision = null; //required
    private Status status = null; //optional

    private boolean mutable = true;

    /** 
     * Constructs a <code>Result</code> object
     */
    public ResultImpl() throws XACML2Exception {
    }

    /** 
     * Constructs a <code>Result</code> object from an XML string
     *
     * @param xml string representing a <code>Result</code> object
     * @throws SAMLException if the XML string could not be processed
     */
    public ResultImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                "ResultImpl.processElement(): invalid XML input");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "errorObtainingElement"));
        }
    }

    /** 
     * Constructs a <code>Result</code> object from an XML DOM element
     *
     * @param element XML DOM element representing a <code>Result</code> 
     * object
     *
     * @throws SAMLException if the DOM element could not be processed
     */
    public ResultImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }

    /**
     * Returns <code>resourceId</code> of this object
     * @return  <code>resourceId</code> of this object
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets <code>resourceId</code> of this object
     * @param  <code>resourceId</code> of this object
     * @exception XACML2Exception if the object is immutable
     */
    public void setResourceId(String resourceId) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }
        this.resourceId = resourceId;
    }

    /**
     * Returns the <code>Decision</code> of this object
     *
     * @return the <code>Decision</code> of this object
     */
    public Decision getDecision() {
        return decision;
    }

    /**
     * Sets the <code>Decision</code> of this object
     *
     * @exception XACML2Exception if the object is immutable
     * 
     */
    public void setDecision(Decision decision) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }
        if (decision == null) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid")); //add i18n
        }
        this.decision = decision;
    }

    /**
     * Returns the <code>Status</code> of this object
     *
     * @return the <code>Status</code> of this object
     */
    public Status getStatus() {
        return status; 
    }

    /**
     * Sets the <code>Status</code> of this object
     *
     * @exception XACML2Exception if the object is immutable
     */
    public void setStatus(Status status) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }
        this.status = status;
    }


   /**
    * Returns a string representation of this object
    *
    * @return a string representation of this object
    * @exception XACML2Exception if conversion fails for any reason
    */
    public String toXMLString() throws XACML2Exception {
        return this.toXMLString(true, false);
    }

   /**
    * Returns a string representation of this object
    * @param includeNSPrefix Determines whether or not the namespace qualifier
    *        is prepended to the Element when converted
    * @param declareNS Determines whether or not the namespace is declared
    *        within the Element.
    * @return a string representation of this object
    * @exception XACML2Exception if conversion fails for any reason
     */
    public String toXMLString(boolean includeNSPrefix, boolean declareNS)
            throws XACML2Exception {
        StringBuffer sb = new StringBuffer(2000);
        String nsDeclaration = "";
        String nsPrefix = "";
        if (declareNS) {
            nsDeclaration = XACML2Constants.CONTEXT_DECLARE_STR;
        }
        if (includeNSPrefix) {
            nsPrefix = XACML2Constants.CONTEXT_PREFIX;
        }
        sb.append("<").append(nsPrefix).append(XACML2Constants.RESULT_ELEMENT)
                .append(nsDeclaration);
        sb.append(" ");
        if (resourceId != null) {
            sb.append(XACML2Constants.RESOURCE_ID_ATTRIBUTE)
                .append("=")
                .append(XACML2SDKUtils.quote(resourceId));
        }
        sb.append(">\n");
        if (decision != null) {
            sb.append(decision.toXMLString(includeNSPrefix, declareNS));
        }
        if (status != null) {
            sb.append(status.toXMLString(includeNSPrefix, declareNS));
        }
        sb.append("</").append(nsPrefix).append(XACML2Constants.RESULT_ELEMENT)
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
        if (mutable) {
            if (decision != null) {
                decision.makeImmutable();
            }
            if (status != null) {
                status.makeImmutable();
            }
            mutable = false;
        }
    }

    private void processElement(Element element) throws XACML2Exception {
        if (element == null) {
            XACML2SDKUtils.debug.error(
                "ResultImpl.processElement(): invalid root element");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_element"));
        }
        String elemName = element.getLocalName();
        if (elemName == null) {
            XACML2SDKUtils.debug.error(
                "ResultImpl.processElement(): local name missing");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_local_name"));
        }

        if (!elemName.equals(XACML2Constants.RESULT_ELEMENT)) {
            XACML2SDKUtils.debug.error(
                "ResultImpl.processElement(): invalid local name " + elemName);
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
                "ResultImpl.processElement(): invalid child element count: " 
                        + childCount);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_child_count")); //FIXME: add i18n key
        } else if (childCount > 3) {
            XACML2SDKUtils.debug.error(
                "ResultImpl.processElement(): invalid child element count: " 
                        + childCount);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_child_count")); //FIXME: add i18n key
        }
        Element firstChild = (Element)childElements.get(0);
        String firstChildName = firstChild.getLocalName();
        if (firstChildName.equals(XACML2Constants.DECISION_ELEMENT)) {
            decision =  ContextFactory.getInstance()
                    .createDecision(firstChild);
        } else {
            XACML2SDKUtils.debug.error(
                "ResultImpl.processElement(): invalid first child element: " 
                        + firstChildName);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_first_child")); //FIXME: add i18n key
        }
        //process decision element
        if (childCount > 1) {
            Element secondChild = (Element)childElements.get(1);
            String secondChildName = secondChild.getLocalName();
            if (secondChildName.equals(XACML2Constants.STATUS_ELEMENT)) {
                status =  ContextFactory.getInstance()
                        .createStatus(secondChild);

            } else {
                XACML2SDKUtils.debug.error(
                    "ResultImpl.processElement(): invalid second child element: " 
                            + secondChildName);
                throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                    "invalid_second_child")); //FIXME: add i18n key
            }
            if (childCount > 2) {
                Element thirdChild = (Element)childElements.get(2);
                String thirdChildName = thirdChild.getLocalName();
                XACML2SDKUtils.debug.error(
                    "ResultImpl.processElement(): invalid third child element: " 
                            + thirdChildName);
                throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                    "invalid_third_child")); //FIXME: add i18n key
            }
        }
    }

}

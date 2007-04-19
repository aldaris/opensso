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
 * $Id: ResponseImpl.java,v 1.2 2007-04-19 19:14:29 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.context.impl;

import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Response;
import com.sun.identity.xacml2.context.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>Response</code> element is a container of 
 * one or more <code>Result</code>s issuded by authorization authority.
 *
 *
 * <p/>
 * schema:
 * <pre>
 *      &lt;xs:complexType name="ResponseType">
 *          &lt;xs:sequence>
 *              &lt;xs:element ref="xacml-context:Result" 
 *                      maxOccurs="unbounded"/>
 *          &lt;xs:sequence>
 *      &lt;xs:complexType>
 * </pre>
 */
public class ResponseImpl implements Response {


    private List results = new ArrayList(); //Result+ 
    private boolean mutable = true;

    /** 
     * Constructs a <code>Response</code> object
     */
    public ResponseImpl() {
    }

    /** 
     * Constructs a <code>Response</code> object from an XML string
     *
     * @param xml string representing a <code>Response</code> object
     * @throws SAMLException if the XML string could not be processed
     */
    public ResponseImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                "ResponseImpl.processElement(): invalid XML input");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "errorObtainingElement"));
        }
    }

    /** 
     * Constructs a <code>Response</code> object from an XML DOM element
     *
     * @param element XML DOM element representing a <code>Response</code> 
     * object
     *
     * @throws SAMLException if the DOM element could not be processed
     */
    public ResponseImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }

    /**
     * Returns the <code>Result</code>s of this object
     *
     * @return the <code>Result</code>s of this object
     */
    public List getResults() {
        return results; 
    }

    /**
     * Sets the <code>Result</code>s of this object
     *
     * @param results the <code>Result</code>s of this object
     *
     * @exception XACML2Exception if the object is immutable
     */
    public void setResults(List values) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }
        if (values != null) {
            Iterator iter = values.iterator();
            results = new ArrayList();
            while (iter.hasNext()) {
                Result value = (Result) iter.next();
                results.add(value);
            }
        } else {
            results = null;
        }

    }

    /**
     * Adds a <code>Result</code> to this object
     *
     * @param result the <code>Result</code> to add
     *
     * @exception XACML2Exception if the object is immutable
     */
    public void addResult(Result result) throws XACML2Exception {
        if (!mutable) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("objectImmutable"));
        }
        if (results == null) {
            results = new ArrayList();
        }
        results.add(result);
    }

   /**
    * Returns a string representation of this object
    *
    * @return a string representation of this object
    * @exception XACML2Exception if conversion fails for any reason
    */
    public String toXMLString() throws XACML2Exception {
        //top level element, declare namespace
        return this.toXMLString(true, true);
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
        String nsPrefix = "";
        String nsDeclaration = "";
        if (declareNS) {
            nsDeclaration = XACML2Constants.CONTEXT_DECLARE_STR;
        }
        if (includeNSPrefix) {
            nsPrefix = XACML2Constants.CONTEXT_PREFIX;
        }
        sb.append("<").append(nsPrefix).append(XACML2Constants.RESPONSE_ELEMENT).
            append(nsDeclaration).append(">\n");
        int length = 0;
        if (results != null) {
            length = results.size();
            for (int i = 0; i < length; i++) {
                Result result = (Result)results.get(i);
                sb.append(result.toXMLString(includeNSPrefix, false));
            }
        }
        sb.append("</").append(nsPrefix)
                .append(XACML2Constants.RESPONSE_ELEMENT).append(">\n");
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
            if (results != null) {
                int length = results.size();
                for (int i = 0; i < length; i++) {
                    Result result = (Result)results.get(i);
                    result.makeImmutable();
                }
                results = Collections.unmodifiableList(results);
            }
            mutable = false;
        }
    }

    /** 
     * Initializes a <code>Response</code> object from an XML DOM element
     *
     * @param element XML DOM element representing a <code>Response</code> 
     * object
     *
     * @throws SAMLException if the DOM element could not be processed
     */
    private void processElement(Element element) throws XACML2Exception {
        if (element == null) {
            XACML2SDKUtils.debug.error(
                "ResponseImpl.processElement(): invalid root element");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_element"));
        }
        String elemName = element.getLocalName();
        if (elemName == null) {
            XACML2SDKUtils.debug.error(
                "ResponseImpl.processElement(): local name missing");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_local_name"));
        }

        if (!elemName.equals(XACML2Constants.RESPONSE_ELEMENT)) {
            XACML2SDKUtils.debug.error(
                "ResponseImpl.processElement(): invalid local name " + elemName);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_local_name"));
        }

        // starts processing subelements
        NodeList nodes = element.getChildNodes();
        int numOfNodes = nodes.getLength();
        int nextElem = 0;

        while (nextElem < numOfNodes) { 
            Node child = (Node) nodes.item(nextElem);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childName = child.getLocalName();
                if (childName != null) {
                    if (childName.equals(XACML2Constants.RESULT_ELEMENT)) {
                        results.add(
                            ContextFactory.getInstance().createResult(
                                    (Element)child));
                            //XMLUtils.getElementValue((Element)child));
                    } else {
                        XACML2SDKUtils.debug.error(
                            "ResponseImpl.processElement(): "
                            + " invalid child element: " + elemName);
                        throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                            "invalid_child_name")); //FIXME: add i18n key
                    }
                }
            }
            nextElem++;
        }
    }

}

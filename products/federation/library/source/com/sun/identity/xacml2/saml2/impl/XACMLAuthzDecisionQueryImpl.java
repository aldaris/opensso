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
 * $Id: XACMLAuthzDecisionQueryImpl.java,v 1.1 2007-03-15 06:19:11 bhavnab Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.saml2.impl;

import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.Request;
import com.sun.identity.xacml2.saml2.XACMLAuthzDecisionQuery;
import com.sun.identity.xacml2.context.ContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>XACMLAuthzDecisionQueryImpl</code> is an impelmentation
 * of <code>XACMLAuthzDecisionQuery</code> interface which is a SAML Query
 * that extends Protocol schema. It allows a PEP to submit an XACML Request
 * Context in a  SAML Request along with other information. This element is
 * an alternative to SAML-defined <code><samlp:AuthzDecisionQuery></code>
 * that allows a PEP to use the full capabilities of an XACML PDP.
 * <p>
 * <pre>
 *&lt;xs:element name="XACMLAuthzDecisionQuery"
 *         type="XACMLAuthzDecisionQueryType"/>
 *&lt;xs:complexType name="XACMLAuthzDecisionQueryType">
 *  &lt;xs:complexContent>
 *    &lt;xs:extension base="samlp:RequestAbstractType">
 *      &lt;xs:sequence>
 *        &lt;xs:element ref="xacml-context:Request"/>
 *      &lt;xs:sequence>
 *      &lt;xs:attribute name="InputContextOnly"
 *                    type="boolean"
 *                    use="optional"
 *                    default="false"/>
 *      &lt;xs:attribute name="ReturnContext"
 *                    type="boolean"
 *                    use="optional"
 *                    default="false"/>
 *    &lt;xs:extension>
 *  &lt;xs:complexContent>
 *&lt;xs:complexType>
 * </pre>
 *@supported.all.api
 */
public class XACMLAuthzDecisionQueryImpl implements XACMLAuthzDecisionQuery {
    
    private boolean inputContextOnly = false;
    private boolean returnContext = false;
    private boolean isMutable = true;
    private Request request;
    private XACML2Constants xc;
    /**
     * Default constructor
     */
    public XACMLAuthzDecisionQueryImpl() {
    }
    
    /**
     * This constructor is used to build <code>XACMLAuthzDecisionQuery</code>
     * object from a XML string.
     *
     * @param xml A <code>java.lang.String</code> representing
     *        an <code>XACMLAuthzDecisionQuery</code> object
     * @exception XACML2Exception if it could not process the XML string
     */
    public XACMLAuthzDecisionQueryImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                    "XACMLAuthzDecisionQueryImpl.processElement(): invalid XML "
                     +"input");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                    "errorObtainingElement"));
        }
    }
    
    /**
     * This constructor is used to build <code>XACMLAuthzDecisionQuery</code> 
     * object from a block of existing XML that has already been built into a
     * DOM.
     *
     * @param element A <code>org.w3c.dom.Element</code> representing
     *        DOM tree for <code>XACMLAuthzDecisionQuery</code> object
     * @exception XACML22Exception if it could not process the Element
     */
    public XACMLAuthzDecisionQueryImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }
    
    private void processElement(Element element) throws XACML2Exception {
        String value = null;
        if (element == null) {
            XACML2SDKUtils.debug.error(
                    "XACMLAuthzDecisionQueryImpl.processElement(): invalid root "
                    +"element");
            throw new XACML2Exception( XACML2SDKUtils.bundle.getString(
                    "invalid_element"));
        }
        
        // First check that we're really parsing an XACMLAuthzDecisionQuery
        if (! element.getLocalName().equals(xc.XACMLAUTHZDECISIONQUERY)) {
            XACML2SDKUtils.debug.error(
                    "XACMLAuthzDecisionQueryImpl.processElement(): invalid root "
                    +"element");
            throw new XACML2Exception( XACML2SDKUtils.bundle.getString(
                    "missing_local_name"));
        }
        
        // now we get the request
        NodeList nodes = element.getChildNodes();
        ContextFactory factory = ContextFactory.getInstance();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if ((node.getNodeType() == Node.ELEMENT_NODE) ||
                    (node.getNodeType() == Node.ATTRIBUTE_NODE)) {
                if (node.getLocalName().equals(xc.REQUEST)) {
                    request = factory.getInstance().createRequest((Element)node);
                }
            }
        }
        // make sure we got a request
        if (request == null) {
            throw new XACML2Exception(
                    XACML2SDKUtils.bundle.getString("null_not_valid"));
        }
        
        NamedNodeMap attrs = element.getAttributes();
        try {
            returnContext = Boolean.valueOf(attrs.getNamedItem(xc.RETURNCONTEXT).
                    getNodeValue()).booleanValue();
        } catch (Exception e) {
            throw new XACML2Exception("XACMLAuthzDecisionQueryImpl."
                    +"processElement():"
                    + "Error parsing optional attribute "+ xc.RETURNCONTEXT+":"
                    +e.getMessage());
        }
        try {
            inputContextOnly = Boolean.valueOf(attrs.getNamedItem(
                    xc.INPUTCONTEXTONLY).getNodeValue()).booleanValue();
        } catch (Exception e) {
            throw new XACML2Exception("XACMLAuthzDecisionQueryImpl."
                    +"processElement():"
                    + "Error parsing optional attribute "+ xc.INPUTCONTEXTONLY 
                    +":"+e.getMessage());
        }
        
    }
    
    
    /**
     * Returns the XML attribute boolean value which governs the
     * source of information that the PDP is allowed to use in
     * making an authorization decision. If this attribute is "true"
     * then it indiactes that the authorization decision has been made
     * solely on the basis of information contained in the <code>
     * XACMLAuthzDecisionQuery</code>; no external attributes have been
     * used. If this value is "false" then the decision may have been made
     * on the basis of external attributes not conatined in the <code>
     * XACMLAuthzDecisionQuery</code>.
     * @return <code>boolean</code> indicating the value
     * of this attribute.
     */
    public boolean getInputContextOnly() {
        return inputContextOnly;
    }
    
    
    /**
     * Sets the XML attribute boolean value which governs the
     * source of information that the PDP is allowed to use in
     * making an authorization decision. If this attribute is "true"
     * then it indicates to the PDP  that the authorization decision has to be 
     * made solely on the basis of information contained in the <code>
     * XACMLAuthzDecisionQuery</code>; no external attributes may be
     * used. If this value is "false" then the decision can be  made
     * on the basis of external attributes not conatined in the <code>
     * XACMlAuthzDecisionQuery</code>.
     * @param inputContextOnly <code>boolean</code> indicating the value
     * of this attribute.
     *
     * @exception XACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     */
    public void setInputContextOnly(boolean inputContextOnly) throws
            XACML2Exception 
    {
        this.inputContextOnly = inputContextOnly;
    }
    
    
    /**
     * Returns the XML attribute boolean value which provides means
     * to PEP to request that an <code>xacml-context>Request</code>
     * element be included in the <code>XACMlAuthzdecisionStatement</code>
     * resulting from the request. It also governs the contents of that
     * <code.Request</code> element. If this attribite is "true" then the
     * PDP SHALL include the <code>xacml-context:Request</code> element in the
     * <code>XACMLAuthzDecisionStatement</code> element in the 
     * <code>XACMLResponse</code>.
     * The <code>xacml-context:Request</code> SHALL include all the attributes 
     * supplied by the PEP in the <code>AuthzDecisionQuery</code> which were 
     * used in making the authz decision. Other addtional attributes which may 
     * have been used by the PDP may be included.
     * If this attribute is "false" then the PDP SHALL NOT include the
     * <code>xacml-context:Request</code> element in the 
     * <code>XACMLAuthzDecisionStatement<code>.
     *
     * @return <code>boolean</code> indicating the value
     * of this attribute.
     */
    public boolean getReturnContext() {
        return returnContext;
    }
    
    /**
     * Sets the boolean value for this XML attribute
     *
     * @param returnContext <code>boolean</code> indicating the value
     * of this attribute.
     *
     * @exception XACML2ExceptioXACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     *
     * @see #getReturnContext()
     */
    public void setReturnContext(boolean returnContext) throws XACML2Exception {
        this.returnContext = returnContext;
    }
    
    /**
     * Returns the <code>xacml-context:Request</code> element of this object
     *
     * @return the <code>xacml-context:Request</code> elements of this object
     */
    public Request getRequest() {
        return request;
    }
    
    /**
     * Sets the <code>xacml-context:Request</code> element of this object
     *
     * @param request the <code>xacml-context:Request</code> element of this
     * object.
     *
     * @exception XACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     */
    public void setRequest(Request request) throws XACML2Exception {
        if (request == null) {
            throw new XACML2Exception(
                    XACML2SDKUtils.bundle.getString("null_not_valid")); //TODO i18n string
        }
        this.request = request;
    }
    
    /**
     * Returns a <code>String</code> representation of this object
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
        StringBuffer NS = new StringBuffer(100);
        String appendNS = "";
        if (declareNS) {
            NS.append(xc.SAMLP_DECLARE_STR).append(xc.SPACE);
            NS.append(xc.CONTEXT_DECLARE_STR);
            NS.append(xc.SPACE).append(xc.NS_XML).append(xc.SPACE);
        }
        if (includeNSPrefix) {
            appendNS = xc.SAMLP_PREFIX;
        }
        sb.append("<").append(appendNS).append(xc.XACMLAUTHZDECISIONQUERY).
                append(xc.SPACE).append(NS);
        sb.append(xc.SPACE).append(xc.INPUTCONTEXTONLY).append("=").append("\"");
        sb.append(Boolean.toString(inputContextOnly));
        sb.append("\"").append(xc.SPACE);
        sb.append(xc.RETURNCONTEXT).append("=").append("\"");
        sb.append(Boolean.toString(returnContext));
        sb.append("\"").append(xc.SPACE).append(">").append("\n");
        sb.append(request.toXMLString(true,false)).append("\n");
        sb.append("</").append(appendNS).append(xc.XACMLAUTHZDECISIONQUERY);
        sb.append(">\n");
        return  sb.toString();
    }
    
    /**
     * Returns a string representation of this object
     *
     * @return a string representation of this object
     * @exception XACML2Exception if conversion fails for any reason
     */
    public String toXMLString() throws XACML2Exception {
        return toXMLString(true, false);
    }
    
    /**
     * Makes the object immutable
     */
    public void makeImmutable() {
        //TODO
    }
    
    /**
     * Checks if the object is mutable
     *
     * @return <code>true</code> if the object is mutable,
     *         <code>false</code> otherwise
     */
    public boolean isMutable() {
        return isMutable;
    }
    
}

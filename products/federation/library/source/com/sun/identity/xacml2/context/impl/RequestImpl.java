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
 * $Id: RequestImpl.java,v 1.1 2007-03-15 06:19:09 bhavnab Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.context.impl;

import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.context.Attribute;
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Resource;
import com.sun.identity.xacml2.context.Request;
import com.sun.identity.xacml2.context.Subject;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>Request</code> element is the top-level element in the XACML
 * context schema. Its an abstraction layer used by the policy language.
 * It contains <code>Subject</code>, <code>Resource</code>, <code>Action
 * </code> and <code>Environment<code> elements.
 * <p>
 * <pre>
 * &lt;xs:complexType name="RequestType">
 *   &lt;xs:sequence>
 *     &lt;xs:element ref="xacml-context:Subject" maxOccurs="unbounded"/>
 *     &lt;xs:element ref="xacml-context:Resource" maxOccurs="unbounded"/>
 *     &lt;xs:element ref="xacml-context:Action"/>
 *     &lt;xs:element ref="xacml-context:Environment"/>
 *   &lt;xs:sequence>
 * &lt;xs:complexType>
 * </pre>
 *@supported.all.api
 */
public class RequestImpl implements Request {
    private List subjects = new ArrayList();
    private List resources = new ArrayList();
    private List actions = new ArrayList();
    private List env = new ArrayList();
    private Subject subject;
    private Resource resource;
    private boolean isMutable = true;
    private static XACML2Constants xc;

    private  static Set supportedSubjectCategory = new HashSet();
    static {
        supportedSubjectCategory.add(xc.SUBJECT_CATEGORY_DEFAULT);
        supportedSubjectCategory.add(xc.SUBJECT_CATEGORY_INTERMEDIARY);
    };
   /** 
    * Default constructor
    */
    public RequestImpl() {
    }

    /**
     * This constructor is used to build <code>Request</code> object from a
     * XML string.
     *
     * @param xml A <code>java.lang.String</code> representing
     *        a <code>Request</code> object
     * @exception XACML2Exception if it could not process the XML string
     */
    public RequestImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                "RequestImpl.processElement(): invalid XML input");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "errorObtainingElement"));
        }
    }

    /**
     * This constructor is used to build <code>Request</code> object from a
     * block of existing XML that has already been built into a DOM.
     *
     * @param element A <code>org.w3c.dom.Element</code> representing
     *        DOM tree for <code>Request</code> object
     * @exception XACML22Exception if it could not process the Element
     */
    public  RequestImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }

    private void processElement(Element element) throws XACML2Exception {
        if (element == null) {
            XACML2SDKUtils.debug.error(
                "RequestImpl.processElement(): invalid root element");
            throw new XACML2Exception( XACML2SDKUtils.bundle.getString(
                "invalid_element"));
        }
        String elemName = element.getLocalName(); 
        if (elemName == null) {
             XACML2SDKUtils.debug.error(
                "RequestImpl.processElement(): local name missing");
            throw new XACML2Exception( XACML2SDKUtils.bundle.getString(
                "missing_local_name"));
        }

        if (!elemName.equals(xc.REQUEST)) {
            XACML2SDKUtils.debug.error(
                "RequestImpl.processElement(): invalid local name " +
                 elemName);
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "invalid_local_name"));
        }

        // starts processing subelements
        NodeList nodes = element.getChildNodes();
        int numOfNodes = nodes.getLength();
        if (numOfNodes < 1) {
            XACML2SDKUtils.debug.error(
                "RequestImpl.processElement(): request has no subelements");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_subelements"));
        }
   
        ContextFactory factory = ContextFactory.getInstance();
        List children = new ArrayList();
        int i = 0;
        Node child;
        while ( i < numOfNodes) {
            child = (Node)nodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                children.add(child);
            }
            i++;
        }
        if (children.isEmpty()) {
            XACML2SDKUtils.debug.error("RequestImpl.processElement():"
                + " request has no subelements");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "missing_subelements"));
        }
        child = (Node)children.get(0);
        // The first subelement should be <Subject>
        String childName = child.getLocalName();
        if ((childName == null) || (!childName.equals(xc.SUBJECT))) {
            XACML2SDKUtils.debug.error("RequestImpl.processElement():"+
                " the first element is not <Subject>");
        throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
            "missing_subelement_subject"));
        }
        subject = factory.getInstance().createSubject((Element)child);
        if (!supportedSubjectCategory.contains(
            subject.getSubjectCategory().toString())) 
        {
            XACML2SDKUtils.debug.error("RequestImpl.processElement():subject "
                +"category in subject not supported");
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "unsupported_subject_category")); //TODO add i18n string
        }
        subjects.add(subject);
        boolean resourceFound = false;
        boolean actionFound = false;
        boolean envFound = false;
        for ( int j = 1; j < children.size(); j++) {
            child = (Node)children.get(j);
            // so far <Resource> is not encountered
            // Go through next sub elements for <Subject> and <Resource>
            // The next subelement may be <Resource> or <Subject>
            childName = child.getLocalName();
            if ((childName != null) &&
                (childName.equals(xc.RESOURCE) || childName.equals(xc.SUBJECT)))
            {
                    if (resourceFound) {
                        if (childName.equals(xc.SUBJECT)) {
                            // all <Subject> should be before <Resource>
                            XACML2SDKUtils.debug.error("RequestImpl."
                                +"processElement(): <Subject> should be "
                                + "before <Resource>");
                            throw new XACML2Exception(
                                XACML2SDKUtils.bundle.getString(
                                    "element_out_of_place")); //TODO i18n string
                        } else { // found another resource
                            resource = factory.getInstance().createResource((
                                Element)child);
                            resources.add(resource);
                        }
                    } else if (childName.equals(xc.SUBJECT)) {
                            subject = factory.getInstance().createSubject(
                                (Element)child);
                            subjects.add(subject);
                        } else { // childname is resource
                            resourceFound = true;
                            resource = factory.getInstance().createResource((
                                Element)child);
                            resources.add(resource);
                        }
            } else if ((childName != null) && (childName.equals(xc.ACTION))) {
                if (!resourceFound) {
                    XACML2SDKUtils.debug.error("RequestImpl."
                        +"processElement(): <Resource> should be "
                        + "before <Action>");
                    throw new XACML2Exception(
                        XACML2SDKUtils.bundle.getString(
                            "element_out_of_place")); //TODO i18n string
                } else {
                    actionFound = true;
                    //get children which are the <Attribute> elements
                    NodeList attrNodes = child.getChildNodes();
                    int numOfAttrNodes = attrNodes.getLength();
                    for (int k=0; k < numOfAttrNodes; k++) {
                        Node attrChild = (Node)attrNodes.item(k);
                        if (attrChild.getNodeType() == Node.ELEMENT_NODE) {
                            String attrChildName = attrChild.getLocalName();
                            if (attrChildName.equals(xc.ATTRIBUTE)) {
                                Attribute attribute = 
                                    factory.getInstance().createAttribute(
                                    (Element)attrChild);
                                actions.add(attribute);
                            } else {
                                XACML2SDKUtils.debug.error("RequestImpl."
                                    +"processElement(): Invalid element :"
                                    +attrChildName);
                                throw new XACML2Exception( 
                                    XACML2SDKUtils.bundle.getString(
                                        "invalid_element"));
                            }
                        }
                    }
                }
            } else if ((childName != null) && (childName.equals(xc.ENVIRONMENT)))
            {
                if (!resourceFound || !actionFound){
                    XACML2SDKUtils.debug.error("RequestImpl."
                        +"processElement(): <Resource> and "
                        +"Action should be before <Environment>");
                        throw new XACML2Exception(
                            XACML2SDKUtils.bundle.getString(
                                "element_out_of_place")); //TODO i18n string
                } else {
                    envFound = true;
                    //get children which are the <Attribute> elements
                    NodeList envNodes = child.getChildNodes();
                    int numOfAttrNodes = envNodes.getLength();
                    for (int k=0; k < numOfAttrNodes; k++) {
                        Node envChild = (Node)envNodes.item(k);
                        if (envChild.getNodeType() == Node.ELEMENT_NODE) {
                            String envChildName = envChild.getLocalName();
                            if (envChildName.equals(xc.ATTRIBUTE)) {
                                Attribute attribute = factory.getInstance().
                                    createAttribute((Element)envChild);
                                env.add(attribute);
                            } else {
                                XACML2SDKUtils.debug.error("RequestImpl."
                                    +"processElement(): Invalid element :"
                                    +envChildName);
                                throw new XACML2Exception(
                                    XACML2SDKUtils.bundle.getString(
                                        "invalid_element"));
                            }
                        }
                    }
                 }
            }
        }
        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message("resourceFound:"+resourceFound);
            XACML2SDKUtils.debug.message("actionFound:"+actionFound);
            XACML2SDKUtils.debug.message("envFound:"+envFound);
        }
        if (!resourceFound || !actionFound || !envFound) {
            XACML2SDKUtils.debug.error("RequestImpl.processElement(): Some"
                +"of required elements are missing");
            throw new XACML2Exception(
            XACML2SDKUtils.bundle.getString("missing_subelements"));
        }
    }
        

    /**
     * Returns the one to many <code>Subject</code> elements of this object
     *
     * @return the <code>Subject</code> elements of this object
     */
    public List getSubjects() {
        return subjects;
    }

    /**
     * Sets the one to many <code>Subject</code> elements of this object
     *
     * @param subjects the one to many <code>Subject</code> elements of this 
     * object
     *
     * @exception XACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     */
    public void setSubjects(List subjects) throws XACML2Exception {
        if (!isMutable) {
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "objectImmutable"));
        }
        if (subjects == null || subjects.isEmpty()) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid"));
        }
        this.subjects.addAll(subjects);
    }
    

    /**
     * Returns the one to many <code>Resource</code> elements of this object
     *
     * @return the <code>Resource</code> elements of this object
     */
    public List getResources() {
        return resources;
    }

    /**
     * Sets the one to many <code>Resource</code> elements of this object
     *
     * @param resources the one to many <code>Resource</code> elements of this 
     * object
     *
     * @exception XACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     */
    public void setResources(List resources) throws XACML2Exception {
        if (!isMutable) {
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "objectImmutable"));
        } 
        if (resources == null || resources.isEmpty()) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid"));
        }
        this.resources.addAll(resources);
    }

    /**
     * Returns the List containing one to many <code>Attribute</code> elements
     * associated with this <code>Action</code> element.
     *
     * @return the List of <code>Attribute</code> elements associated with 
     * the <code>Action</code>.
     */
    public List getActions() {
        return actions;
    }

    /**
     * Sets the one to many <code>Attribute</code> elements associated with
     * the <code>Action</code>
     *
     * @param actions List of the one to many <code>Attributes</code> elements 
     * of the <code>Action</code>.
     *
     * @exception XACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     */
    public void setActions(List actions) throws XACML2Exception {
        if (!isMutable) {
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "objectImmutable"));
        } 
        if (actions == null || actions.isEmpty()) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid")); 
        }
        this.actions.addAll(actions);
    }

    /**
     * Returns the List containing one to many <code>Attribute</code> elements 
     * associated with the  <code>Environment</code> element.
     *
     * @return the List of <code>Attribute</code> elements associated with 
     * the <code>Action</code>.
     */
    public List getEnvironment() {
        return env;
    }

    /**
     * Sets the one to many <code>Attribute</code> elements associated with
     * the <code>Environment</code>
     *
     * @param env List of the one to many <code>Attributes</code> elements 
     * of the <code>Environment</code>.
     *
     * @exception XACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     */
    public void setEnvironment(List env) throws XACML2Exception {
        if (!isMutable) {
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "objectImmutable"));
        } 
        if (env == null || env.isEmpty()) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid")); 
        }
        this.env.addAll(env);
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
            NS.append(xc.CONTEXT_DECLARE_STR).append(xc.SPACE);
            NS.append(xc.NS_XML).append(xc.SPACE).append(
                    xc.CONTEXT_SCHEMA_LOCATION);
        }
        if (includeNSPrefix) {
            appendNS = XACML2Constants.CONTEXT_PREFIX;
        }
        sb.append("<").append(appendNS).append(xc.REQUEST).append(NS);
        sb.append(xc.END_TAG);
        int length = 0;
        if (subjects != null && !subjects.isEmpty()) {
            sb.append("\n");
            length = subjects.size();
            for (int i = 0; i < length; i++) {
                Subject sub = (Subject)subjects.get(i);
                sb.append(subject.toXMLString(includeNSPrefix, false));
            }
        }
        if (resources != null && !resources.isEmpty()) {
            sb.append("\n");
            length = resources.size();
            for (int i = 0; i < length; i++) {
                Resource resource = (Resource)resources.get(i);
                sb.append(resource.toXMLString(includeNSPrefix, false));
            }
        }
        if (actions != null && !actions.isEmpty()) {
            sb.append("\n");
            sb.append("<").append(appendNS).append(xc.ACTION).append(">\n");
            length = actions.size();
            for (int i = 0; i < length; i++) {
                Attribute action = (Attribute)actions.get(i);
                sb.append(action.toXMLString(includeNSPrefix, false));
            }
            sb.append("</").append(appendNS).append(xc.ACTION).append(">\n");
        }
        if (env != null && !env.isEmpty()) {
            sb.append("\n");
            sb.append("<").append(appendNS).append(xc.ENVIRONMENT).
                    append(">\n");
            length = env.size();
            for (int i = 0; i < length; i++) {
                Attribute environment = (Attribute)env.get(i);
                sb.append(environment.toXMLString(includeNSPrefix, false));
            }
            sb.append("</").append(appendNS).append(xc.ENVIRONMENT).
                    append(">\n");
        }
        sb.append("</").append(appendNS).append(xc.REQUEST).
        append(">\n");
        return sb.toString();
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
    * Makes the object immutable
    */
    public void makeImmutable() {}

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

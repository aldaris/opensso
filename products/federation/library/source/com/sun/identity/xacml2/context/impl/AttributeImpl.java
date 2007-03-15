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
 * $Id: AttributeImpl.java,v 1.1 2007-03-15 06:19:08 bhavnab Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.context.impl;

import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.Attribute;

import java.util.List;
import java.util.ArrayList;
import java.net.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>Attribute</code> element specifies information about the
 * action/subject/resource requested in the <code>Request</code> context by 
 * listing a sequence of <code>Attribute</code> elements associated with 
 * the action.
 * <p>
 * <pre>
 * &lt;xs:element name="Attribute" type="xacml-context:AttributeType"/>
 * &lt;xs:complexType name="AttributeType">
 *    &lt;xs:sequence>
 *       &lt;xs:element ref="xacml-context:AttributeValue" 
 *        maxOccurs="unbounded"/>
 *    &lt;xs:sequence>
 *    &lt;xs:attribute name="AttributeId" type="xs:anyURI" use="required"/>
 *    &lt;xs:attribute name="DataType" type="xs:anyURI" use="required"/>
 *    &lt;xs:attribute name="Issuer" type="xs:string" use="optional"/>
 * &lt;xs:complexType>
 * </pre>
 *@supported.all.api
 */
public class AttributeImpl implements Attribute {


    URI id = null;
    URI type = null;
    String issuer = null;
    private List values ;
    private boolean isMutable = true;
    private static XACML2Constants xc;

   /** 
    * Default constructor
    */
    public AttributeImpl() {
    }

    /**
     * This constructor is used to build <code>Attribute</code> object from a
     * XML string.
     *
     * @param xml A <code>java.lang.String</code> representing
     *        an <code>Attribute</code> object
     * @exception XACML2Exception if it could not process the XML string
     */
    public AttributeImpl(String xml) throws XACML2Exception {
        Document document = XMLUtils.toDOMDocument(xml, XACML2SDKUtils.debug);
        if (document != null) {
            Element rootElement = document.getDocumentElement();
            processElement(rootElement);
            makeImmutable();
        } else {
            XACML2SDKUtils.debug.error(
                "AttributeImpl.processElement(): invalid XML input");
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
    public AttributeImpl(Element element) throws XACML2Exception {
        processElement(element);
        makeImmutable();
    }

    private void processElement(Element element) throws XACML2Exception {
        String value = null;
        if (element == null) {
            XACML2SDKUtils.debug.error(
                "AttributeImpl.processElement(): invalid root element");
            throw new XACML2Exception( XACML2SDKUtils.bundle.getString(
                "invalid_element"));
        }

      // First check that we're really parsing an Attribute
      if (! element.getLocalName().equals(xc.ATTRIBUTE)) {
            XACML2SDKUtils.debug.error(
                "AttributeImpl.processElement(): invalid root element");
            throw new XACML2Exception( XACML2SDKUtils.bundle.getString(
                "invalid_element"));
      }
      NamedNodeMap attrs = element.getAttributes();

      try {
          id = new URI(attrs.getNamedItem(xc.ATTRIBUTE_ID).getNodeValue());
      } catch (Exception e) {
          throw new XACML2Exception("AttributeImpl.processElement():"
              + "Error parsing required attribute "+ xc.ATTRIBUTE_ID +"in "
              + "AttributeType"); // TODO add i18n
      }
      if (id == null) {
          throw new XACML2Exception("AttributeImpl.processElement():Attribute "
              +"must contain AttributeId ");
          // TODO add 118n debug
      }
      try {
          type = new URI(attrs.getNamedItem(xc.DATATYPE).getNodeValue());
      } catch (Exception e) {
          throw new XACML2Exception("AttributeImpl.processElement():"
              + "Error parsing required attribute "+ xc.DATATYPE +"in "
              + "DataType:"+e.getMessage()); // TODO add i18n
      }
      if (type == null) {
          throw new XACML2Exception("AttributeImpl.processElement():Attribute "
              +"must contain DataType ");
          // TODO add 118n debug
      }
      try {
          Node issuerNode = attrs.getNamedItem(xc.ISSUER);
          if (issuerNode != null)
              issuer = issuerNode.getNodeValue();
  
      } catch (Exception e) {
          throw new XACML2Exception("AttributeImpl.processElement():"
              + "Error parsing optional attribute "+ xc.ISSUER +"in "
              + "AttributeType:"+e.getMessage()); // TODO add i18n
      }
 
      // now we get the attribute value
      NodeList nodes = element.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++) {
          Node node = nodes.item(i);
          if ((node.getNodeType() == Node.ELEMENT_NODE) ||
              (node.getNodeType() == Node.ATTRIBUTE_NODE)) {
              if (node.getLocalName().equals(xc.ATTRIBUTE_VALUE)) {
                  if (values == null) {
                      values = new ArrayList();
                  }
                  values.add(node);
              }
          }
      }

      // make sure we got a value
      if (values.isEmpty()) {
          throw new XACML2Exception("AttributeImpl.processElement():Attribute "
              +"must contain a value"); // TODO add i18n
      }
    }

    /**
     * Returns the issuer of the <code>Attribute</code>.
     * @return <code>String</code> representing the issuer. It MAY be an 
     * x500Name that binds to a public key or some other identification 
     * exchanged out-of-band by participating entities.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the issuer of the <code>Attribute</code>.
     * @param issuer <code>String</code> representing the issuer. 
     * It MAY be an x500Name that binds to a public key or some other 
     * identification  exchanged out-of-band by participating entities. 
     * This is optional so return value could be null or an empty 
     * <code>String</code>.
     * @exception XACML2Exception if the object is immutable
     */
    public void setIssuer(String issuer) throws XACML2Exception {
        if (!isMutable) {
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "objectImmutable"));
        }
        this.issuer=issuer;
    }

    /**
     * Returns the AttributeId of the <code>Attribute</code>
     * which the attribute identifier.
     * @return the <code>URI</code> representing the data type.
     */
    public URI getAttributeID() {
        return id;
    }

    /**
     * Sets the attribiteId of the <code>>Attribute</code>
     * @param attributeID <code>URI</code> representing the attribite id.
     * @exception XACML2Exception if the object is immutable
     */
    public void setAttributeID(URI attributeID) throws XACML2Exception {
        if (!isMutable) {
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "objectImmutable"));
        }
        if (attributeID == null) {
                throw new XACML2Exception(
                    XACML2SDKUtils.bundle.getString("null_not_valid")); 
        }
        id = attributeID;
    }
      
    /**
     * Returns the datatype of the contents of the <code>AttributeValue</code>
     * elements. This will be either a primitive datatype defined by XACML 2.0 
     * specification or a type ( primitive or structured) defined in a  
     * namespace declared in the <xacml-context> element.
     * @return the <code>URI</code> representing the data type.
     */
    public URI getDataType() {
        return type;
    }

    /**
     * Sets the data type of the contents of the <code>AttributeValue</code>
     * elements.
     * @param dataType <code>URI</code> representing the data type.
     * @exception XACML2Exception if the object is immutable
     */
    public void setDataType(URI dataType) throws XACML2Exception {
        if (!isMutable) {
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "objectImmutable"));
        }
        if (dataType == null) {
                throw new XACML2Exception(
                    XACML2SDKUtils.bundle.getString("null_not_valid"));
        }
        type = dataType;
    }
      
    /**
     * Returns one to many values in the <code>AttributeValue</code> elements 
     * of this object
     *
     * @return the List containing <code>Element</code>s representing the 
     * <code>AttributeValue</code> of this object
     */
    public List getAttributeValues() {
        return values;
    }

    /**
     * Sets the <code>AttributeValue</code> elements of this object
     *
     * @param values a <code>List</code> containing Element representing 
     * <code>AttributeValue</code> of this object.
     *
     * @exception XACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     */
    public void setAttributeValues(List values) throws XACML2Exception {
        if (!isMutable) {
            throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                "objectImmutable"));
        }
        if (this.values == null) {
            this.values = new ArrayList();
        }
        if (values == null || values.isEmpty()) {
            throw new XACML2Exception(
                XACML2SDKUtils.bundle.getString("null_not_valid")); 

        }
        for (int i=0; i < values.size(); i++) {
            Element value = (Element)values.get(i);
            String elemName = value.getLocalName();
            if (elemName == null) {
                XACML2SDKUtils.debug.error(
                    "StatusMessageImpl.processElement(): local name missing");
                throw new XACML2Exception(XACML2SDKUtils.bundle.getString(
                        "missing_local_name"));
            }
            this.values.add(value);
        }
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
            throws XACML2Exception 
    {
        StringBuffer sb = new StringBuffer(2000);
        StringBuffer NS = new StringBuffer(100);
        String appendNS = "";
        if (declareNS) {
            NS.append(xc.CONTEXT_DECLARE_STR).append(xc.SPACE);
            NS.append(xc.NS_XML).append(xc.SPACE).append(
                    xc.CONTEXT_SCHEMA_LOCATION);
        }
        if (includeNSPrefix) {
            appendNS = xc.CONTEXT_PREFIX;
        }
        sb.append("<").append(appendNS).append(xc.ATTRIBUTE).append(NS);
        sb.append(xc.SPACE);
        if (type != null) {
            sb.append(xc.DATATYPE).append("=").append("\"").
                    append(type.toString());
            sb.append("\"").append(xc.SPACE);
        }
        if (id != null) {
            sb.append(xc.ATTRIBUTE_ID).append("=").append("\"").
                    append(id.toString());
            sb.append("\"").append(xc.SPACE);
        }
        if (issuer != null) {
            sb.append(xc.ISSUER).append("=").append("\"").append(issuer).
                    append("\"");
        }
        sb.append(xc.END_TAG);
        int length = 0;
        String xmlString = null;
        if (values != null && !values.isEmpty()) {
            for (int i=0; i < values.size(); i++) {
                Element value = (Element)values.get(i);
                sb.append("\n");
                // ignore trailing ":"
                if (includeNSPrefix && (value.getPrefix() == null)) {
                    value.setPrefix(appendNS.substring(0, appendNS.length()-1));
                }
                if(declareNS) {
                    int index = NS.indexOf("=");
                    String namespaceName = NS.substring(0, index);
                    String namespaceURI = NS.substring(index+1);
                    System.out.println("namespace:"+namespaceName);
                    System.out.println("namespaceURI:"+namespaceURI);
                    if (value.getNamespaceURI() == null) {
                        value.setAttribute(namespaceName, namespaceURI);
                        // does not seem to work to append namespace TODO
                    }
                }
                sb.append(XMLUtils.print(value));
             }
        } else { // values are empty put empty tags
             sb.append("<").append(appendNS).append(xc.ATTRIBUTE_VALUE);
             sb.append(NS).append(">").append("\n"); 
             sb.append("</").append(appendNS).append(xc.ATTRIBUTE_VALUE);
             sb.append(">").append("\n");
        }
        sb.append("</").append(appendNS).append(xc.ATTRIBUTE);
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
    public void makeImmutable() {//TODO
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

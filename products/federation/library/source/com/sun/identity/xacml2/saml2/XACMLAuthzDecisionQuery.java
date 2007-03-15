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
 * $Id: XACMLAuthzDecisionQuery.java,v 1.1 2007-03-15 06:19:10 bhavnab Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.saml2;

import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.context.Request;


/**
 * The <code>XACMLAuthzDecisionQuery</code> element is a SAML Query that extends
 * Protocol schema. It allows a PEP to submit an XACML Request Context in a  SAML
 * Request along with other information. This element is an alternative to 
 * SAML-defined <code><samlp:AuthzDecisionQuery></code> that alloaws a PEP to
 * use the full capabilities of an XACML PDP.
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
public interface XACMLAuthzDecisionQuery {

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
    public boolean getInputContextOnly();

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
            XACML2Exception;

    /**
     * Returns the XML attribute boolean value which provides means
     * to PEP to request that an <code>xacml-context>Request</code>
     * element be included in the <code>XACMlAuthzdecisionStatement</code>
     * resulting from the request. It also governs the contents of that
     * <code.Request</code> element. If this attribite is "true" then the
     * PDP SHALL include the <code>xacml-context:Request</code> element in the
     * <code>XACMLAuthzDecisionStatement</code> element in the 
     * <code>XACMLResponse</code>.  The <code>xacml-context:Request</code> SHALL 
     * include all the attributes supplied by the PEP in the
     * <code>AuthzDecisionQuery</code> which were used in making 
     * the authz decision. Other addtional attributes which may have been used 
     * by the PDP may be included.
     * If this attribute is "false" then the PDP SHALL NOT include the 
     * <code>xacml-context:Request</code> element in the
     * <code>XACMLAuthzDecisionStatement<code>.
     * 
     * @return <code>boolean</code> indicating the value
     * of this attribute.
     */
    public boolean getReturnContext();

    /**
     * Sets the boolean value for this XML attribute
     * @see getReturnContext()
     *
     * @param returnContext <code>boolean</code> indicating the value
     * of this attribute.
     *
     * @exception XACML2ExceptioXACML2Exception if the object is immutable
     * An object is considered <code>immutable</code> if <code>
     * makeImmutable()</code> has been invoked on it. It can
     * be determined by calling <code>isMutable</code> on the object.
     */
    public void setReturnContext(boolean returnContext) throws XACML2Exception;

    /**
     * Returns the <code>xacml-context:Request</code> element of this object
     *
     * @return the <code>xacml-context:Request</code> elements of this object
     */
    public Request getRequest();

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
    public void setRequest(Request request) throws XACML2Exception;

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
            throws XACML2Exception;

   /**
    * Returns a string representation of this object
    *
    * @return a string representation of this object
    * @exception XACML2Exception if conversion fails for any reason
    */
    public String toXMLString() throws XACML2Exception;

   /**
    * Makes the object immutable
    */
    public void makeImmutable();

   /**
    * Checks if the object is mutable
    *
    * @return <code>true</code> if the object is mutable,
    *         <code>false</code> otherwise
    */
    public boolean isMutable();
    
}

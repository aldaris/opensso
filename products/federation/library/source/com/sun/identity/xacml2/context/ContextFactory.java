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
 * $Id: ContextFactory.java,v 1.2 2007-03-24 01:25:45 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.xacml2.context;

import org.w3c.dom.Element;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.context.impl.AttributeImpl;
import com.sun.identity.xacml2.context.impl.RequestImpl;
import com.sun.identity.xacml2.context.impl.ResourceImpl;
import com.sun.identity.xacml2.context.impl.SubjectImpl;
import com.sun.identity.xacml2.saml2.impl.XACMLAuthzDecisionQueryImpl;
import com.sun.identity.xacml2.saml2.XACMLAuthzDecisionQuery;
import com.sun.identity.xacml2.context.impl.DecisionImpl;
import com.sun.identity.xacml2.context.impl.StatusCodeImpl;
import com.sun.identity.xacml2.context.impl.StatusDetailImpl;
import com.sun.identity.xacml2.context.impl.StatusImpl;
import com.sun.identity.xacml2.context.impl.StatusMessageImpl;
import com.sun.identity.xacml2.context.impl.ResponseImpl;
import com.sun.identity.xacml2.context.impl.ResultImpl;

/**
 * This is the factory class to obtain instances of the objects defined
 * in xacml2 context schema.
 * There are three ways to obtain an instance of a object type:
 * with no parameters, with a DOM tree element, or with an XML String.
 *
 * @supported.all.api
 */
public class ContextFactory {

    private static ContextFactory instance = new ContextFactory();
    private XACML2Constants xc;

    /**
     * Sole Constructor.
     */
    private ContextFactory() {
    }

    /**
     * Returns the instance of <code>ContextSchemaFactory</code>.
     * 
     * @return <code>ContextSchemaFactory</code>.
     * 
     */
    public static ContextFactory getInstance() {
        return instance;
    }

    /**
     * Returns a new instance of <code>Request</code>.
     *
     * @return a new instance of <code>Request</code>
     * 
     */
    public Request createRequest() {
        Object obj = XACML2SDKUtils.getObjectInstance(xc.REQUEST);
        if (obj == null) {
            return new RequestImpl();
        } else {
            return (Request) obj;
        }
    }

    /**
     * Returns a new instance of <code>Request</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>Request</code>
     * @return a new instance of <code>Request</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public Request createRequest(Element elem)
        throws XACML2Exception {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.REQUEST, elem);
        if (obj == null) {
            return new RequestImpl(elem);
        } else {
            return (Request) obj;
        }
    }

    /**
     * Returns a new instance of <code>Request</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>Request</code>
     * @return a new instance of <code>Resource</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public Request createRequest(String xml)
        throws XACML2Exception {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.REQUEST, xml);
        if (obj == null) {
            return new RequestImpl(xml);
        } else {
            return (Request) obj;
        }
    }

    
    /**
     * Returns a new instance of <code>Resource</code>.
     *
     * @return a new instance of <code>Resource</code>
     * 
     */
    public Resource createResource() {
        Object obj = XACML2SDKUtils.getObjectInstance(xc.RESOURCE);
        if (obj == null) {
            return new ResourceImpl();
        } else {
            return (Resource) obj;
        }
    }

    /**
     * Returns a new instance of <code>Resource</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>Resource</code>
     * @return a new instance of <code>Resource</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public Resource createResource(Element elem)
        throws XACML2Exception {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.RESOURCE, elem);
        if (obj == null) {
            return new ResourceImpl(elem);
        } else {
            return (Resource) obj;
        }
    }

    /**
     * Returns a new instance of <code>Resource</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>Resource</code>
     * @return a new instance of <code>Resource</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public Resource createResource(String xml)
        throws XACML2Exception {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.RESOURCE, xml);
        if (obj == null) {
            return new ResourceImpl(xml);
        } else {
            return (Resource) obj;
        }
    }

    /**
     * Returns a new instance of <code>Subject</code>.
     *
     * @return a new instance of <code>Subject</code>
     * 
     */
    public Subject createSubject() {
        Object obj = XACML2SDKUtils.getObjectInstance(xc.SUBJECT);
        if (obj == null) {
            return new SubjectImpl();
        } else {
            return (Subject) obj;
        }
    }

    /**
     * Returns a new instance of <code>Subject</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>Subject</code>
     * @return a new instance of <code>Subject</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public Subject createSubject(Element elem)
        throws XACML2Exception {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.SUBJECT, elem);
        if (obj == null) {
            return new SubjectImpl(elem);
        } else {
            return (Subject) obj;
        }
    }

    /**
     * Returns a new instance of <code>Subject</code>.
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>Subject</code>
     * @return a new instance of <code>Subject</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public Subject createSubject(String xml)
        throws XACML2Exception {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.SUBJECT, xml);
        if (obj == null) {
            return new SubjectImpl(xml);
        } else {
            return (Subject) obj;
        }
    }

    /**
     * Returns a new instance of <code>Attribute</code>.
     * Caller may need to call setters of the class to populate the object.
     *
     * @return a new instance of <code>Attribute</code>.
     * 
     */
    public Attribute createAttribute() {
        Object obj = XACML2SDKUtils.getObjectInstance(xc.ATTRIBUTE);
        if (obj == null) {
            return new AttributeImpl();
        } else {
            return (Attribute) obj;
        }
    }

    /**
     * Returns a new instance of <code>Attribute</code>. The return object
     * is immutable.
     *
     * @param elem an <code>Element</code> representation of
     *                <code>Attribute</code>.
     * @return a new instance of <code>Attribute</code>.
     * @throws XACML2Exception if error occurs while processing the
     *                <code>Element</code>.
     * 
     */
    public Attribute createAttribute(Element elem)
                throws XACML2Exception
    {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.ATTRIBUTE, elem);
        if (obj == null) {
            return new AttributeImpl(elem);
        } else {
            return (Attribute) obj;
        }
    }

    /**
     * Returns a new instance of <code>Attribute</code>. The return object
     * is immutable.
     *
     * @param xml an XML String representing <code>Attribute</code>.
     * @return a new instance of <code>Attribute</code>.
     * @throws XACML2Exception if error occurs while processing the XML string.
     * 
     */
    public Attribute createAttribute(String xml)
                throws XACML2Exception
    {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.ATTRIBUTE, xml);
        if (obj == null) {
            return new AttributeImpl(xml);
        } else {
            return (Attribute) obj;
        }
    }

    /**
     * Returns a new instance of <code>XACMLAuthzDecisionQuery</code>.
     * Caller may need to call setters of the class to populate the object.
     *
     * @return a new instance of <code>XACMLAuthzDecisionQuery</code>.
     * 
     */
    public XACMLAuthzDecisionQuery createXACMLAuthzDecisionQuery() {
        Object obj = XACML2SDKUtils.getObjectInstance(
                xc.XACMLAUTHZDECISIONQUERY);
        if (obj == null) {
            return new XACMLAuthzDecisionQueryImpl();
        } else {
            return (XACMLAuthzDecisionQuery) obj;
        }
    }

    /**
     * Returns a new instance of <code>XACMLAuthzDecisionQuery</code>. 
     * The return object is immutable.
     *
     * @param elem an <code>Element</code> representation of
     *                <code>XACMLAuthzDecisionQuery</code>.
     * @return a new instance of <code>XACMLAuthzDecisionQuery</code>.
     * @throws XACML2Exception if error occurs while processing the
     *                <code>Element</code>.
     * 
     */
    public XACMLAuthzDecisionQuery createXACMLAuthzDecisionQuery(Element elem)
                throws XACML2Exception
    {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.XACMLAUTHZDECISIONQUERY, elem);
        if (obj == null) {
            return new XACMLAuthzDecisionQueryImpl(elem);
        } else {
            return (XACMLAuthzDecisionQuery) obj;
        }
    }

    /**
     * Returns a new instance of <code>XACMLAuthzDecisionQuery</code>. 
     * The return object is immutable.
     *
     * @param xml an XML String representing 
     * <code>XACMLAuthzDecisionQuery</code>.
     * @return a new instance of <code>XACMLAuthzDecisionQuery</code>.
     * @throws XACML2Exception if error occurs while processing the XML string.
     * 
     */
    public XACMLAuthzDecisionQuery createXACMLAuthzDecisionQuery(String xml)
                throws XACML2Exception
    {
        Object obj = XACML2SDKUtils.getObjectInstance(
            xc.XACMLAUTHZDECISIONQUERY, xml);
        if (obj == null) {
            return new XACMLAuthzDecisionQueryImpl(xml);
        } else {
            return (XACMLAuthzDecisionQuery) obj;
        }
    }

    /**
     * Returns a new instance of <code>Response</code>.
     *
     * @return a new instance of <code>Response</code>
     * 
     */
    public Response createResponse() throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.RESPONSE_ELEMENT);
        if (object == null) {
            return new ResponseImpl();
        } else {
            return (Response)object;
        }
    }

    /**
     * Returns a new instance of <code>Response</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>Response</code>
     * @return a new instance of <code>Response</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public Response createResponse(Element elem)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.RESPONSE_ELEMENT, elem);
        if (object == null) {
            return new ResponseImpl(elem);
        } else {
            return (Response)object;
        }
    }

    /**
     * Returns a new instance of <code>Response</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>Response</code>
     * @return a new instance of <code>Response</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public Response createResponse(String xml)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.RESPONSE_ELEMENT, xml);
        if (object == null) {
            return new ResponseImpl(xml);
        } else {
            return (Response)object;
        }
    }

    /**
     * Returns a new instance of <code>Result</code>.
     *
     * @return a new instance of <code>Result</code>
     * 
     */
    public Result createResult() throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.RESULT_ELEMENT);
        if (object == null) {
            return new ResultImpl();
        } else {
            return (Result)object;
        }
    }

    /**
     * Returns a new instance of <code>Result</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>Result</code>
     * @return a new instance of <code>Result</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public Result createResult(Element elem)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.RESULT_ELEMENT, elem);
        if (object == null) {
            return new ResultImpl(elem);
        } else {
            return (Result)object;
        }
    }

    /**
     * Returns a new instance of <code>Result</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>Result</code>
     * @return a new instance of <code>Result</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public Result createResult(String xml)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.RESULT_ELEMENT, xml);
        if (object == null) {
            return new ResultImpl(xml);
        } else {
            return (Result)object;
        }
    }

    /**
     * Returns a new instance of <code>Decision</code>.
     *
     * @return a new instance of <code>Decision</code>
     * 
     */
    public Decision createDecision() throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.DECISION_ELEMENT);
        if (object == null) {
            return new DecisionImpl();
        } else {
            return (Decision)object;
        }
    }

    /**
     * Returns a new instance of <code>Decision</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>Decision</code>
     * @return a new instance of <code>Decision</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public Decision createDecision(Element elem)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.DECISION_ELEMENT, elem);
        if (object == null) {
            return new DecisionImpl(elem);
        } else {
            return (Decision)object;
        }
    }

    /**
     * Returns a new instance of <code>Decision</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>Decision</code>
     * @return a new instance of <code>Decision</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public Decision createDecision(String xml)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.DECISION_ELEMENT, xml);
        if (object == null) {
            return new DecisionImpl(xml);
        } else {
            return (Decision)object;
        }
    }

    /**
     * Returns a new instance of <code>Status</code>.
     *
     * @return a new instance of <code>Status</code>
     * 
     */
    public Status createStatus() throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_ELEMENT);
        if (object == null) {
            return new StatusImpl();
        } else {
            return (Status)object;
        }
    }

    /**
     * Returns a new instance of <code>Status</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>Status</code>
     * @return a new instance of <code>Status</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public Status createStatus(Element elem)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_ELEMENT, elem);
        if (object == null) {
            return new StatusImpl(elem);
        } else {
            return (Status)object;
        }
    }

    /**
     * Returns a new instance of <code>Status</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>Status</code>
     * @return a new instance of <code>Status</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public Status createStatus(String xml)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_ELEMENT, xml);
        if (object == null) {
            return new StatusImpl(xml);
        } else {
            return (Status)object;
        }
    }

    /**
     * Returns a new instance of <code>StatusCode</code>.
     *
     * @return a new instance of <code>StatusCode</code>
     * 
     */
    public StatusCode createStatusCode() throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_CODE_ELEMENT);
        if (object == null) {
            return new StatusCodeImpl();
        } else {
            return (StatusCode)object;
        }
    }

    /**
     * Returns a new instance of <code>StatusCode</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>StatusCode</code>
     * @return a new instance of <code>StatusCode</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public StatusCode createStatusCode(Element elem)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_CODE_ELEMENT, elem);
        if (object == null) {
            return new StatusCodeImpl(elem);
        } else {
            return (StatusCode)object;
        }
    }

    /**
     * Returns a new instance of <code>StatusCode</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>StatusCode</code>
     * @return a new instance of <code>StatusCode</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public StatusCode createStatusCode(String xml)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_CODE_ELEMENT, xml);
        if (object == null) {
            return new StatusCodeImpl(xml);
        } else {
            return (StatusCode)object;
        }
    }

    /**
     * Returns a new instance of <code>StatusMessage</code>.
     *
     * @return a new instance of <code>StatusMessage</code>
     * 
     */
    public StatusMessage createStatusMessage() throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_MESSAGE_ELEMENT);
        if (object == null) {
            return new StatusMessageImpl();
        } else {
            return (StatusMessage)object;
        }
    }

    /**
     * Returns a new instance of <code>StatusMessage</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>StatusMessage</code>
     * @return a new instance of <code>StatusMessage</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public StatusMessage createStatusMessage(Element elem)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_MESSAGE_ELEMENT, elem);
        if (object == null) {
            return new StatusMessageImpl(elem);
        } else {
            return (StatusMessage)object;
        }
    }

    /**
     * Returns a new instance of <code>StatusMessage</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>StatusMessage</code>
     * @return a new instance of <code>StatusMessage</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public StatusMessage createStatusMessage(String xml)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_MESSAGE_ELEMENT, xml);
        if (object == null) {
            return new StatusMessageImpl(xml);
        } else {
            return (StatusMessage)object;
        }
    }

    /**
     * Returns a new instance of <code>StatusDetail</code>.
     *
     * @return a new instance of <code>StatusDetail</code>
     * 
     */
    public StatusDetail createStatusDetail() throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_DETAIL_ELEMENT);
        if (object == null) {
            return new StatusDetailImpl();
        } else {
            return (StatusDetail)object;
        }
    }
    /**
     * Returns a new instance of <code>StatusDetail</code>.
     * The return object is immutable.
     *
     * @param elem a DOM Element representation of <code>StatusDetail</code>
     * @return a new instance of <code>StatusDetail</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    DOM Element 
     * 
     */
    public StatusDetail createStatusDetail(Element elem)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_DETAIL_ELEMENT, elem);
        if (object == null) {
            return new StatusDetailImpl(elem);
        } else {
            return (StatusDetail)object;
        }
    }

    /**
     * Returns a new instance of <code>StatusDetail</code>
     * The return object is immutable.
     *
     * @param xml a XML string representation of <code>StatusDetail</code>
     * @return a new instance of <code>StatusDetail</code>
     * @throws XACML2Exception if error occurs while processing the 
     *    XML string
     * 
     */
    public StatusDetail createStatusDetail(String xml)throws XACML2Exception {
        Object object = XACML2SDKUtils.getObjectInstance(
                XACML2Constants.STATUS_DETAIL_ELEMENT, xml);
        if (object == null) {
            return new StatusDetailImpl(xml);
        } else {
            return (StatusDetail)object;
        }
    }

}

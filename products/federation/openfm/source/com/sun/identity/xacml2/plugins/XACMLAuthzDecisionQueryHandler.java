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
 * $Id: XACMLAuthzDecisionQueryHandler.java,v 1.2 2007-08-07 23:33:52 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.xacml2.plugins;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;

//import com.sun.identity.policy.PolicyEvaluator;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.ResourceResult;

import com.sun.identity.policy.client.PolicyEvaluator;
import com.sun.identity.policy.client.PolicyEvaluatorFactory;

import com.sun.identity.saml2.common.SAML2Exception;
//import com.sun.identity.saml2.protocol.Response;
import com.sun.identity.saml2.protocol.RequestAbstract;
import com.sun.identity.saml2.soapbinding.RequestHandler;
import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.context.Request;
import com.sun.identity.xacml2.context.Resource;
import com.sun.identity.xacml2.saml2.XACMLAuthzDecisionQuery;
import com.sun.identity.xacml2.saml2.XACMLAuthzDecisionStatement;

import javax.xml.soap.SOAPMessage;

//the following imports to support createTestResponse
import com.sun.identity.saml2.assertion.AssertionFactory;
import com.sun.identity.saml2.assertion.Assertion;
import com.sun.identity.saml2.assertion.Issuer;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.protocol.ProtocolFactory;

import com.sun.identity.shared.test.UnitTestBase;

import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.Attribute;
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Decision;
import com.sun.identity.xacml2.context.Request;
import com.sun.identity.xacml2.context.Response;
import com.sun.identity.xacml2.context.Result;
import com.sun.identity.xacml2.context.Status;
import com.sun.identity.xacml2.context.StatusCode;
import com.sun.identity.xacml2.context.StatusMessage;
import com.sun.identity.xacml2.context.StatusDetail;

import com.sun.identity.xacml2.spi.ActionMapper;
import com.sun.identity.xacml2.spi.EnvironmentMapper;
import com.sun.identity.xacml2.spi.ResourceMapper;
import com.sun.identity.xacml2.spi.ResultMapper;
import com.sun.identity.xacml2.spi.SubjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;

import org.testng.annotations.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is an extension point for all XACML related exceptions.
 * This class also handles message localization in XACML exceptions.
 *
 * @supported.all.api
 */
public class XACMLAuthzDecisionQueryHandler implements RequestHandler {

    private SubjectMapper subjectMapper;
    private ResourceMapper resourceMapper;
    private ActionMapper actionMapper;
    private EnvironmentMapper environmentMapper;
    private ResultMapper resultMapper;
    
    
    /**
     * This class is an implementation of SAML2 query RequestHandler to handle
     * XACMLAuthzDecisionQuery
     * 
     */
    public XACMLAuthzDecisionQueryHandler() {
        subjectMapper = new FMSubjectMapper();
        resourceMapper = new FMResourceMapper();
        actionMapper = new FMActionMapper();
        environmentMapper = new FMEnvironmentMapper();
        resultMapper = new FMResultMapper();
    }
    
    /**
     * Processes an XACMLAuthzDecisionQuery and retruns a SAML2 Response
     * @param pdpEntityId EntityID of PDP
     * @param pepEntityId EntityID of PEP
     * @param request SAML2 Request, an XAMLAuthzDecisionQuery
     * @param soapMessage SOAPMessage that carried the SAML2 Request
     * @return SAML2 Response with an XAMLAuthzDecisionStatement
     * @exception SAML2Exception if the query can not be handled
     */
    public com.sun.identity.saml2.protocol.Response handleQuery(
            String pdpEntityId, String pepEntityId, 
            RequestAbstract samlpRequest, SOAPMessage  soapMessage) 
            throws SAML2Exception {

        //TODO: add real processing logic, logging, i18n

        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message(
                    "XACMLAuthzDecisionQueryHandler.handleQuery(), entering"
                    + ":pdpEntityId=" + pdpEntityId
                    + ":pepEntityId=" + pepEntityId
                    + ":samlpRequest=\n" + samlpRequest.toXMLString(
                        true, true)
                    + ":soapMessage=\n" + soapMessage);
        }

        Request xacmlRequest 
                = ((XACMLAuthzDecisionQuery)samlpRequest).getRequest();
        boolean returnContext 
                = ((XACMLAuthzDecisionQuery)samlpRequest).getReturnContext();


        //get native sso token
        SSOToken ssoToken =
                (SSOToken)subjectMapper.mapToNativeSubject(
                xacmlRequest.getSubjects()); 

        //get native service name, resource name 
        List resources = xacmlRequest.getResources();
        Resource resource = null;
        if (!resources.isEmpty()) {
            //We deal with only one resource for now
            resource = (Resource)resources.get(0);
        }

        String resourceName = null;
        String serviceName = null;

        if (resource != null) {
            String[] resourceService = resourceMapper.mapToNativeResource(
                    resource);
            if (resourceService != null) {
                if (resourceService.length > 0) {
                    resourceName = resourceService[0];
                }
                if (resourceService.length > 1) {
                    serviceName = resourceService[1];
                }
            }
        }

        //get native action name
        String actionName = null;
        if (serviceName != null) {
            actionName = actionMapper.mapToNativeAction(
                    xacmlRequest.getAction(), serviceName);
        }

        //get environment map
        Map environment = environmentMapper.mapToNativeEnvironment(
                xacmlRequest.getEnvironment(), 
                xacmlRequest.getSubjects());


        //get native policy deicison using native policy evaluator
        boolean booleanDecision = false;
        boolean evaluationFailed = false;
        try {
            //PolicyEvaluator pe = new PolicyEvaluator(serviceName);
            PolicyEvaluator pe = PolicyEvaluatorFactory.getInstance()
                    .getPolicyEvaluator(serviceName);
            booleanDecision = pe.isAllowed(ssoToken, resourceName,
                    actionName, environment);
        } catch (SSOException ssoe) {
            evaluationFailed = true;
        } catch (PolicyException pe) {
            evaluationFailed = true;
        }

        //decision: Indeterminate, Deny, Permit, NotApplicable
        //status code: missing_attribute, syntax_error, processing_error, ok

        Decision decision = ContextFactory.getInstance().createDecision();
        Status status = ContextFactory.getInstance().createStatus();
        StatusCode code = ContextFactory.getInstance().createStatusCode();
        StatusMessage message 
                = ContextFactory.getInstance().createStatusMessage();
        StatusDetail detail 
                = ContextFactory.getInstance().createStatusDetail();
        detail.getElement().insertBefore(detail.getElement().cloneNode(true), 
                null);
        if (evaluationFailed) {
            decision.setValue(XACML2Constants.INDETERMINATE);
            code.setValue(XACML2Constants.STATUS_CODE_PROCESSING_ERROR);
            //code.setValue(XACML2Constants.STATUS_CODE_MISSING_ATTRIBUTE);
            message.setValue("processing_eorror"); //TODO: i18n
        } else if (booleanDecision) {
            decision.setValue(XACML2Constants.PERMIT);
            code.setValue(XACML2Constants.STATUS_CODE_OK);
            message.setValue("ok"); //TODO: i18n
        } else {
            decision.setValue(XACML2Constants.DENY);
            code.setValue(XACML2Constants.STATUS_CODE_OK);
            message.setValue("ok"); //TODO: i18n
        }

        Result result = ContextFactory.getInstance().createResult();
        String resourceId = resourceName; //TODO: find resourceId from Resource
        result.setResourceId(resourceId);
        result.setDecision(decision);

        status.setStatusCode(code);
        status.setStatusMessage(message);
        status.setStatusDetail(detail);
        result.setStatus(status);

        Response response = ContextFactory.getInstance().createResponse();
        response.addResult(result);

        XACMLAuthzDecisionStatement statement = ContextFactory.getInstance()
                .createXACMLAuthzDecisionStatement();
        statement.setResponse(response);
        if (returnContext) {
            statement.setRequest(xacmlRequest);
        }

        com.sun.identity.saml2.protocol.Response samlpResponse
                = createSamlpResponse(statement, 
                status.getStatusCode().getValue());

        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message(
                    "XACMLAuthzDecisionQueryHandler.handleQuery(), returning"
                    + ":samlResponse=\n" 
                    + samlpResponse.toXMLString(true, true));
        }

        return samlpResponse;
    }


    private com.sun.identity.saml2.protocol.Response createSamlpResponse(
            XACMLAuthzDecisionStatement statement, String statusCodeValue) 
            throws XACML2Exception, SAML2Exception {

        com.sun.identity.saml2.protocol.Response samlpResponse
                = ProtocolFactory.getInstance().createResponse();
        samlpResponse.setID("response-id:1");
        samlpResponse.setVersion("2.0");
        samlpResponse.setIssueInstant(new Date());

        com.sun.identity.saml2.protocol.StatusCode samlStatusCode
                = ProtocolFactory.getInstance().createStatusCode();
        samlStatusCode.setValue(statusCodeValue);
        com.sun.identity.saml2.protocol.Status samlStatus
                = ProtocolFactory.getInstance().createStatus();
        samlStatus.setStatusCode(samlStatusCode);
        samlpResponse.setStatus(samlStatus );

        Assertion assertion = AssertionFactory.getInstance().createAssertion();
        assertion.setVersion("2.0");
        assertion.setID("response-id:1");
        assertion.setIssueInstant(new Date());
        Issuer issuer = AssertionFactory.getInstance().createIssuer();
        issuer.setValue("issuer-1");
        assertion.setIssuer(issuer);
        List statements = new ArrayList();
        statements.add(
                statement.toXMLString(true, true)); //add decisionstatement
        assertion.setStatements(statements);
        List assertions = new ArrayList();
        assertions.add(assertion);
        samlpResponse.setAssertion(assertions);
        return samlpResponse;
    }

}


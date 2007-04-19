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
 * $Id: XACMLAuthzDecisionQueryHandler.java,v 1.1 2007-04-19 19:18:47 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.xacml2.plugins;
import com.sun.identity.saml2.common.SAML2Exception;
//import com.sun.identity.saml2.protocol.Response;
import com.sun.identity.saml2.protocol.RequestAbstract;
import com.sun.identity.saml2.soapbinding.RequestHandler;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.context.Request;
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
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Decision;
import com.sun.identity.xacml2.context.Request;
import com.sun.identity.xacml2.context.Response;
import com.sun.identity.xacml2.context.Result;
import com.sun.identity.xacml2.context.Status;
import com.sun.identity.xacml2.context.StatusCode;
import com.sun.identity.xacml2.context.StatusMessage;
import com.sun.identity.xacml2.context.StatusDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    
    
    /**
     * This class is an implementation of SAML2 query RequestHandler to handle
     * XACMLAuthzDecisionQuery
     * 
     */
    public XACMLAuthzDecisionQueryHandler() {
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
        com.sun.identity.saml2.protocol.Response samlpResponse 
                = createSampleSaml2Response(samlpRequest);
        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message(
                    "XACMLAuthzDecisionQueryHandler.handleQuery(), returning"
                    + ":samlResponse=\n" 
                    + samlpResponse.toXMLString(true, true));
        }
        return samlpResponse;
    }


    private com.sun.identity.saml2.protocol.Response createSampleSaml2Response(
            RequestAbstract samlpRequest) 
            throws XACML2Exception, SAML2Exception {

        Response xacmlResponse = createSampleXacml2Response();
        Request xacmlRequest 
                = ((XACMLAuthzDecisionQuery)samlpRequest).getRequest();

        XACMLAuthzDecisionStatement statement 
                = createSampleXacmlAuthzDecisionStatement(
                xacmlResponse, xacmlRequest);
        
        com.sun.identity.saml2.protocol.Response samlpResponse
                = ProtocolFactory.getInstance().createResponse();
        samlpResponse.setID("response-id:1");
        samlpResponse.setVersion("2.0");
        samlpResponse.setIssueInstant(new Date());
        com.sun.identity.saml2.protocol.StatusCode samlStatusCode
                = ProtocolFactory.getInstance().createStatusCode();
        samlStatusCode.setValue("stausCode");
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

    private Response createSampleXacml2Response() 
            throws XACML2Exception {
        StatusCode code = ContextFactory.getInstance().createStatusCode();
        code.setValue("10");
        code.setMinorCodeValue("5");
        StatusMessage message 
                = ContextFactory.getInstance().createStatusMessage();
        message.setValue("success");
        StatusDetail detail 
                = ContextFactory.getInstance().createStatusDetail();
        detail.getElement().insertBefore(detail.getElement().cloneNode(true)
                , null);
        Status status = ContextFactory.getInstance().createStatus();
        status.setStatusCode(code);
        status.setStatusMessage(message);
        status.setStatusDetail(detail);
        Decision decision = ContextFactory.getInstance().createDecision();
        decision.setValue("Permit");
        Result result = ContextFactory.getInstance().createResult();
        result.setResourceId("http://insat.red.iplanet.com/banner.html");
        result.setDecision(decision);
        result.setStatus(status);
        Response response = ContextFactory.getInstance().createResponse();
        response.addResult(result);
        return response;
        
    }

    private XACMLAuthzDecisionStatement createSampleXacmlAuthzDecisionStatement(
            Response xacmlResponse, Request xacmlRequest) 
            throws XACML2Exception {
        XACMLAuthzDecisionStatement statement = ContextFactory.getInstance()
                .createXACMLAuthzDecisionStatement();
        statement.setResponse(xacmlResponse);
        statement.setRequest(xacmlRequest);
        return statement;
        
    }

}


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
 * $Id: XACML2RequestProcessor.java,v 1.2 2007-08-07 23:33:50 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.client;

import com.sun.identity.saml2.assertion.AssertionFactory;
import com.sun.identity.saml2.assertion.Assertion;
import com.sun.identity.saml2.assertion.Issuer;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.protocol.ProtocolFactory;
import com.sun.identity.saml2.soapbinding.QueryClient;
import com.sun.identity.saml2.soapbinding.RequestHandler;

import com.sun.identity.xacml2.context.Request;
import com.sun.identity.xacml2.context.Response;
import com.sun.identity.xacml2.common.XACML2Exception;

import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2SDKUtils;
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Request;
import com.sun.identity.xacml2.context.Resource;
import com.sun.identity.xacml2.context.Subject;
import com.sun.identity.xacml2.context.Action;
import com.sun.identity.xacml2.context.Environment;
import com.sun.identity.xacml2.saml2.XACMLAuthzDecisionQuery;
import com.sun.identity.xacml2.saml2.XACMLAuthzDecisionStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class provides the public API to process XACML context Request. 
 * This class accepts XACML context Request to get authorization decision,
 * posts the request to PDP using SAML2 profile, gets SAML Response back, 
 * extacts XACML context Response from the XACMLAuthzDecisionStatement 
 * returned in SAML Response and returns the XACML context Response.
 * XACML context Response includes the xacml context Result with 
 * the XACML context authorization Decision
 *
 */
public class XACML2RequestProcessor {
    
    private XACML2RequestProcessor() {
    }
    
    /**
     * Returns an instance of <code>XACML2RequestProcessor</code>
     * @exception if can not return an instance of 
     *             <code>XACML2RequestProcessor</code>
     */
    public static XACML2RequestProcessor getInstance() throws XACML2Exception {
        return new XACML2RequestProcessor();
    }

    /**
     * Processes an XACML context Request and returns an XACML context 
     * Response. 
     *
     * @param xacmlRequest XACML context Request. This describes the
     *        Resource(s), Subject(s), Action, Environment of the request
     *        and corresponds to XACML context schema element Request.
     *        One would contruct this Request object using XACML client SDK.
     *
     * @param pdpEntityId EntityID of PDP
     * @param pepEntityId EntityID of PEP
     * @return XACML context Response. This corresponds to 
     *               XACML context schema element Response
     * @exception XACML2Exception if request could not be processed 
     */
    public Response processRequest(Request xacmlRequest, 
            String pdpEntityId, String pepEntityId) 
            throws XACML2Exception, SAML2Exception {

        //TODO: clean up logic, add logging, i18n

        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message(
                    "XACMLRequestProcessor.processRequest(), entering"
                    + ":pdpEntityId=" + pdpEntityId
                    + ":pepEntityId=" + pepEntityId
                    + ":xacmlRequest=\n" 
                    + xacmlRequest.toXMLString(true, true));
        }
        XACMLAuthzDecisionQuery samlpQuery 
            = createXACMLAuthzDecisionQuery(xacmlRequest);

        //set InputContextOnly
        samlpQuery.setInputContextOnly(true);

        //set ReturnContext
        samlpQuery.setReturnContext(true);

        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message(
                    "XACMLRequestProcessor.processRequest(),"
                    + "samlpQuery=\n" + samlpQuery.toXMLString(true, true));
        }

        //TODO: fix, switch to using SAML2 client processor
        /*
            samplResponse = QueryClient.processXACMLQuery(requestAbstract, 
                    hostedEntityId, remoteEntityId)
        */

        /*
        XACMLAuthzDecisionQueryHandler xacmlQueryHandler
            = new XACMLAuthzDecisionQueryHandler();
        */

        //TODO:, use saml2 QueryClient after it is available
        RequestHandler xacmlQueryHandler = null;
        try {
            xacmlQueryHandler = (RequestHandler)(Class.forName(
                    "com.sun.identity.xacml2.plugins.XACMLAuthzDecisionQueryHandler")
                    .newInstance());
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException ie) {
            ie.printStackTrace();
        }

        //TODO:, use saml2 QueryClient after it is available
        com.sun.identity.saml2.protocol.Response samlpResponse 
                = xacmlQueryHandler.handleQuery(pdpEntityId, pepEntityId, 
                samlpQuery, null);
        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message(
                    "XACMLRequestProcessor.processRequest(),"
                    + ":samlpResponse=\n" 
                    + samlpResponse.toXMLString(true, true));
        }

        //TODO: add bounds check, null check
        List assertions = samlpResponse.getAssertion();
        Assertion assertion = (Assertion)(assertions.get(0));
        List statements = assertion.getStatements();
        String statementString = (String)(statements.get(0));
        XACMLAuthzDecisionStatement statement = ContextFactory.getInstance()
                .createXACMLAuthzDecisionStatement(statementString);
        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message(
                    "XACMLRequestProcessor.processRequest(),"
                    + ":xacmlAuthzDecisionStatement=\n" 
                    + statement.toXMLString(true, true));
        }

        Response xacmlResponse = statement.getResponse();
        if (XACML2SDKUtils.debug.messageEnabled()) {
            XACML2SDKUtils.debug.message(
                    "XACMLRequestProcessor.processRequest(), returning"
                    + ":xacmlResponse=\n" 
                    + xacmlResponse.toXMLString(true, true));
        }
                 
        return xacmlResponse;
    }

    //TODO: clean up and fix
    private XACMLAuthzDecisionQuery createXACMLAuthzDecisionQuery(
            Request xacmlRequest) 
            throws XACML2Exception, SAML2Exception {
        XACMLAuthzDecisionQuery query 
                = ContextFactory.getInstance().createXACMLAuthzDecisionQuery();
        query.setID("query-1");
        query.setVersion("2.0");
        query.setIssueInstant(new Date());
        query.setDestination("destination-uri");
        query.setConsent("consent-uri");

        Issuer issuer = AssertionFactory.getInstance().createIssuer();
        issuer.setValue("issuer-1");
        issuer.setNameQualifier("name-qualifier");
        //issuer.setSPProvidedID("sp-provided-id");
        issuer.setSPNameQualifier("sp-name-qualifier");
        issuer.setSPNameQualifier("sp-name-qualifier");
        issuer.setFormat("format");
        query.setIssuer(issuer);

        query.setRequest(xacmlRequest);

        return query;
    }

}


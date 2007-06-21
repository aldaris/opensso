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
 * $Id: RPSigninResponse.java,v 1.1 2007-06-21 23:01:33 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.servlet;

import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionProvider;
import com.sun.identity.saml.assertion.Assertion;
import com.sun.identity.saml.assertion.Statement;
import com.sun.identity.saml.assertion.Subject;
import com.sun.identity.saml.assertion.SubjectStatement;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wsfederation.common.WSFederationException;
import com.sun.identity.wsfederation.common.WSFederationUtils;
import com.sun.identity.wsfederation.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.wsfederation.logging.LogUtil;
import com.sun.identity.wsfederation.meta.WSFederationMetaException;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.meta.WSFederationMetaUtils;
import com.sun.identity.wsfederation.plugins.PartnerAccountMapper;
import com.sun.identity.wsfederation.profile.RequestSecurityTokenResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pat
 */
public class RPSigninResponse extends WSFederationAction {
    private static Debug debug = WSFederationUtils.debug;
    
    private String wresult;
    private String wctx;
    
    /** Creates a new instance of RPSigninResponse 
     * @param request HTTP Servlet request
     * @param response HTTP Servlet response
     * @param wresult wresult parameter from request
     * @param wctx wctx parameter from request
     */
    public RPSigninResponse(HttpServletRequest request,
        HttpServletResponse response, String wresult, String wctx) {
        super(request,response);
        this.wresult = wresult;
        this.wctx = wctx;
    }
    
    public void process() throws IOException {
        String classMethod = "RPSigninResponse.process: ";
        
        if ((wresult == null) || (wresult.length() == 0)) {
            String[] data = {request.getQueryString()};
            LogUtil.error(Level.INFO,
                    LogUtil.MISSING_WRESULT,
                    data,
                    null);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Validate context
        if ((wctx == null) || (wctx.length() == 0)) {
            String[] data = {request.getQueryString()};
            LogUtil.error(Level.INFO,
                    LogUtil.MISSING_WCTX,
                    data,
                    null);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String target = WSFederationUtils.removeReplyURL(wctx);
        
        RequestSecurityTokenResponse rstr = null;
        try {
            rstr = RequestSecurityTokenResponse.parseXML(wresult);
        } catch (WSFederationException se) {
            String[] data = {wresult};
            LogUtil.error(Level.INFO,
                    LogUtil.INVALID_WRESULT,
                    data,
                    null);
            response.sendError(response.SC_FORBIDDEN);
            return;
        }
        
        if ( debug.messageEnabled() ) {
            debug.message(classMethod +"Received RSTR: "
                    + rstr.toString());
        }
        
        Map attributeMap = null;
        String realm = null;
        
        String requestURL = request.getRequestURL().toString();
        // get entity id and orgName
        String metaAlias = 
            WSFederationMetaUtils.getMetaAliasByUri(requestURL);
        realm = WSFederationMetaUtils.getRealmByMetaAlias(metaAlias);
        String hostEntityId = null;
        try {
            hostEntityId = 
                WSFederationMetaManager.getEntityByMetaAlias(metaAlias);
        } catch (WSFederationException wsfex) {
            String[] data = {wsfex.getLocalizedMessage(), metaAlias, realm};
            LogUtil.error(Level.INFO,
                    LogUtil.CONFIG_ERROR_GET_ENTITY_CONFIG,
                    data,
                    null);
            response.sendError(response.SC_FORBIDDEN);
            return;
        }
        
        if (realm == null || realm.length() == 0) {
            realm = "/";
        }
        
        // check Assertion and get back a Map of relevant data including,
        // Subject, SOAPEntry for the partner and the List of Assertions.
        if ( debug.messageEnabled() ) {
            debug.message(classMethod +" - verifying assertion");
        }
        try {
            attributeMap = 
                verifySAML11Assertion(rstr, realm, hostEntityId, target);
        } catch (WSFederationException wsfex) {
            // verifySAML11Assertion will log the error
            response.sendError(response.SC_FORBIDDEN);
            return;
        }
        
        if (attributeMap == null) {
            // verifySAML11Assertion will log the error
            response.sendError(response.SC_FORBIDDEN);
            return;
        }
        
        // Quick hack for now... Need to do proper mapping
        String mapperClassName = 
            "com.sun.identity.wsfederation.plugins.DefaultADFSPartnerAccountMapper";
        Class mapperClass = null;
        try {
            mapperClass = 
                Class.forName(mapperClassName);
        } catch (ClassNotFoundException cnfe) {
            String[] data = {cnfe.getLocalizedMessage(), mapperClassName};
            LogUtil.error(Level.INFO,
                    LogUtil.CANT_FIND_SP_ACCOUNT_MAPPER,
                    data,
                    null);
            response.sendError(response.SC_FORBIDDEN);
            return;
        }
        PartnerAccountMapper mapper = null;
        try {
            mapper = (PartnerAccountMapper)mapperClass.newInstance();
        }
        catch ( InstantiationException ie )
        {
            String[] data = {ie.getLocalizedMessage(), mapperClassName};
            LogUtil.error(Level.INFO,
                    LogUtil.CANT_CREATE_SP_ACCOUNT_MAPPER,
                    data,
                    null);
            response.sendError(response.SC_FORBIDDEN);
            return;
        }
        catch ( IllegalAccessException iae )
        {
            String[] data = {iae.getLocalizedMessage(), mapperClassName};
            LogUtil.error(Level.INFO,
                    LogUtil.CANT_CREATE_SP_ACCOUNT_MAPPER,
                    data,
                    null);
            response.sendError(response.SC_FORBIDDEN);
            return;
        }
        Map accountMap = mapper.getUser(
            (List)attributeMap.get(SAML2Constants.ASSERTIONS), null, null);
        String userName = (String) accountMap.get(PartnerAccountMapper.NAME);
        String authLevel = 
            attributeMap.get(SAML2Constants.AUTH_LEVEL).toString();
        
        // Set up Attributes for session creation
        Map sessionInfoMap = new HashMap();
        sessionInfoMap.put(SessionProvider.REALM, realm);
        sessionInfoMap.put(SessionProvider.PRINCIPAL_NAME, userName);        
        sessionInfoMap.put(SessionProvider.AUTH_LEVEL, authLevel);
        
        Object session = null;
        
        try {
            SessionProvider sessionProvider = SessionManager.getProvider();
            session = sessionProvider.createSession(sessionInfoMap,
                    request, response, null);
            // Much work to do on mapping!
            // SPACSUtils.setAttrMapInSession(sessionProvider, attributeMap, 
            //    session);
        } catch (SessionException se) {
            String[] data = {se.getLocalizedMessage(),realm, userName, authLevel};
            LogUtil.error(Level.INFO,
                    LogUtil.CANT_CREATE_SESSION,
                    data,
                    null);
            response.sendError(response.SC_FORBIDDEN);
            return;
        }
        
        String[] data = {wctx, LogUtil.isErrorLoggable(Level.FINER)? wresult : 
                rstr.getAssertion().get(0).getAssertionID(), 
                realm,
                userName,
                authLevel,
                target};
        LogUtil.access(Level.INFO,
                LogUtil.SSO_SUCCESSFUL,
                data,
                session);
        
        response.sendRedirect(target);
    }
    
    // check Assertion and get back a Map of relevant data including,
    // Subject, SOAPEntry for the partner and the List of Assertions.
    private Map verifySAML11Assertion(RequestSecurityTokenResponse response, 
        String realm, String hostEntityId, String target) 
        throws WSFederationMetaException {
        String classMethod = "RPSigninResponse.verifySAML11Assertion";
        
        Subject assertionSubject = null;
        Assertion assertion = null;
        
        SPSSOConfigElement spConfig = null;
        spConfig = WSFederationMetaManager.getSPSSOConfig(realm, hostEntityId);
        
        int timeskew = SAML2Constants.ASSERTION_TIME_SKEW_DEFAULT;
        String timeskewStr = WSFederationMetaUtils.getAttribute(spConfig,
                SAML2Constants.ASSERTION_TIME_SKEW);
        if (timeskewStr != null && timeskewStr.trim().length() > 0) {
            timeskew = Integer.parseInt(timeskewStr);
            if (timeskew < 0) {
                timeskew = SAML2Constants.ASSERTION_TIME_SKEW_DEFAULT;
            }
        }
        if (debug.messageEnabled()) {
            debug.message(classMethod + "timeskew = " + timeskew);
        }

        List assertions = response.getAssertion();
        Iterator iter = assertions.iterator();
        
        // We only check first assertion in the response
        if (iter.hasNext()) {
            assertion = (Assertion) iter.next();
            
            // check issuer of the assertions
            String issuer = assertion.getIssuer();
            if (! WSFederationMetaManager.isTrustedProvider(
                            realm, hostEntityId, issuer)) {
                String[] data = 
                    {LogUtil.isErrorLoggable(Level.FINER)? response.toString() : 
                    response.getAssertion().get(0).getAssertionID(), 
                    realm, hostEntityId, target};
                LogUtil.error(Level.INFO,
                        LogUtil.UNTRUSTED_ISSUER,
                        data,
                        null);
                return null;
            }
            
            if (!WSFederationUtils.isSignatureValid(assertion, realm, issuer)) {
                // isSignatureValid will log the error
                return null;
            }
            
            // must be valid (timewise)
            if (!WSFederationUtils.isTimeValid(assertion, timeskew)) {
                // isTimeValid will log the error
                return null;
            }
            
            // TODO: IssuerInstant of the assertion is within a few minutes
            // This is a MAY in spec. Which number to use for the few minutes?
            
            // TODO: check AudienceRestrictionCondition
            
            //for each assertion, loop to check each statement
            Iterator stmtIter = assertion.getStatement().iterator();
            while (stmtIter.hasNext()) {
                Statement statement = (Statement) stmtIter.next();
                int stmtType = statement.getStatementType();
                if ((stmtType == Statement.AUTHENTICATION_STATEMENT) ||
                        (stmtType == Statement.ATTRIBUTE_STATEMENT) ||
                        (stmtType == 
                        Statement.AUTHORIZATION_DECISION_STATEMENT)) {
                    Subject subject = 
                        ((SubjectStatement)statement).getSubject();
                    
                    if (stmtType == Statement.AUTHENTICATION_STATEMENT) {
                        //TODO: if it has SubjectLocality,its IP must == sender
                        // browser IP. This is a MAY item in the spec.
                        if (assertionSubject == null) {
                            assertionSubject = subject;
                        }
                    }
                }
            }
        }
        
        // must have at least one SSO assertion
        if ( assertionSubject == null ) {
            String[] data = 
                {LogUtil.isErrorLoggable(Level.FINER)? response.toString() : 
                response.getAssertion().get(0).getAssertionID()};
            LogUtil.error(Level.INFO,
                    LogUtil.MISSING_SUBJECT,
                    data,
                    null);
            return null;
        }

        /* TODO - need to think about authn context mapping
        String mapperClass = WSFederationMetaUtils.getAttribute(spConfig, 
            SAML2Constants.SP_AUTHCONTEXT_MAPPER);
        SPAuthnContextMapper mapper = getSPAuthnContextMapper(orgName,
                hostEntityId,mapperClass);
        RequestedAuthnContext reqContext = null;
        int authLevel = mapper.getAuthLevel(reqContext,
                authnStmt.getAuthnContext(),
                orgName,
                hostEntityId,
                idpEntityId);
        */
        int authLevel = 0;

        Map attrMap = new HashMap();
        
        attrMap.put(SAML2Constants.SUBJECT, assertionSubject);
        attrMap.put(SAML2Constants.POST_ASSERTION, assertion);
        attrMap.put(SAML2Constants.ASSERTIONS, assertions);

        if (authLevel >= 0) {
            attrMap.put(SAML2Constants.AUTH_LEVEL, new Integer(authLevel));
        }
        
        Date sessionNotOnOrAfter = assertion.getConditions().getNotOnorAfter();
        // SessionNotOnOrAfter
        if (sessionNotOnOrAfter != null) {
            long maxSessionTime = (sessionNotOnOrAfter.getTime() -
                    System.currentTimeMillis()) / 60000;
            if (maxSessionTime > 0) {
                attrMap.put(SAML2Constants.MAX_SESSION_TIME,
                        new Long(maxSessionTime));
            }
        }
        
        if ( debug.messageEnabled() ) {
            debug.message(classMethod +" Attribute Map : " + attrMap);
        }
        
        return attrMap;
    }
}

<%--
   The contents of this file are subject to the terms
   of the Common Development and Distribution License
   (the License). You may not use this file except in
   compliance with the License.

   You can obtain a copy of the License at
   https://opensso.dev.java.net/public/CDDLv1.0.html or
   opensso/legal/CDDLv1.0.txt
   See the License for the specific language governing
   permission and limitations under the License.

   When distributing Covered Code, include this CDDL
   Header Notice in each file and include the License file
   at opensso/legal/CDDLv1.0.txt.
   If applicable, add the following below the CDDL Header,
   with the fields enclosed by brackets [] replaced by
   your own identifying information:
   "Portions Copyrighted [year] [name of copyright owner]"

   $Id: spAssertionConsumer.jsp,v 1.1 2006-10-30 23:17:22 qcheng Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>





<%@page
import="com.sun.identity.shared.encode.URLEncDec,
com.sun.identity.federation.common.FSUtils,
com.sun.identity.saml.common.SAMLUtils,
com.sun.identity.saml2.common.SAML2Constants,
com.sun.identity.saml2.common.SAML2Exception,
com.sun.identity.saml2.common.SAML2Utils,
com.sun.identity.saml2.meta.SAML2MetaException,
com.sun.identity.saml2.meta.SAML2MetaManager,
com.sun.identity.saml2.meta.SAML2MetaUtils,
com.sun.identity.saml2.profile.ResponseInfo,
com.sun.identity.saml2.profile.SPACSUtils,
com.sun.identity.saml2.protocol.Response,
com.sun.identity.plugin.session.SessionManager,
com.sun.identity.plugin.session.SessionProvider,
com.sun.identity.plugin.session.SessionException
"
%>

<html>
<head>
    <title>SP Assertion Consumer Service</title>
</head>

<%!
    private String getLocalLoginUrl(
                                String orgName,
                                String hostEntityId,
                                SAML2MetaManager metaManager,
                                ResponseInfo respInfo,
                                String requestURL,
                                String relayState)
    {
        String localLoginUrl = SPACSUtils.prepareForLocalLogin(
                orgName, hostEntityId, metaManager, respInfo, requestURL);
        if (localLoginUrl.indexOf("?") == -1) {
            localLoginUrl += "?goto=";
        } else {
            localLoginUrl += "&goto=";
        }
        String gotoURL = requestURL + "?resID="
                        + URLEncDec.encode(respInfo.getResponse().getID());
        if (relayState != null && relayState.length() != 0) {
                gotoURL += "&RelayState=" + URLEncDec.encode(relayState);
        }
        localLoginUrl += URLEncDec.encode(gotoURL);
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("spAssertionConsumer.jsp: local login "
                        + "url=" + localLoginUrl);
        }
        return localLoginUrl;
    }
%>

<body>
<%
    // check request, response, content length
    if ((request == null) || (response == null)) {
        response.sendError(response.SC_BAD_REQUEST,
                        SAML2Utils.bundle.getString("nullInput"));
        return;
    }
    // to avoid dos attack
    // or use SAML2Utils?
    try {
        SAMLUtils.checkHTTPContentLength(request);
    } catch (ServletException se) {
        response.sendError(response.SC_BAD_REQUEST, se.getMessage());
        return;
    }

    String requestURL = request.getRequestURL().toString();
    // get entity id and orgName
    String metaAlias = SAML2MetaUtils.getMetaAliasByUri(requestURL);
    SAML2MetaManager metaManager = SAML2Utils.getSAML2MetaManager();
    if (metaManager == null) {
        // logging?
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        SAML2Utils.bundle.getString("errorMetaManager"));
        return;
    }
    String hostEntityId = null;
    try {
        hostEntityId = metaManager.getEntityByMetaAlias(metaAlias);
    } catch (SAML2MetaException sme) {
        // logging?
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        SAML2Utils.bundle.getString("metaDataError"));
        return;
    }
    if (hostEntityId == null) {
        // logging?
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        SAML2Utils.bundle.getString("metaDataError"));
        return;
    }
    String orgName = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
    if (orgName == null || orgName.length() == 0) {
        orgName = "/";
    }
    String relayState = request.getParameter(SAML2Constants.RELAY_STATE);
    // federate flag
    String federate = request.getParameter(SAML2Constants.FEDERATE);
    SessionProvider sessionProvider = null;
    try {
        sessionProvider = SessionManager.getProvider();
    } catch (SessionException se) {
        response.sendError(
            response.SC_INTERNAL_SERVER_ERROR,
            se.getMessage());
        return;
    }
    ResponseInfo respInfo = SPACSUtils.getResponse(
        request, response, orgName, hostEntityId, metaManager);
    Object token = null;
    try {
        token = sessionProvider.getSession(request);
    } catch (SessionException se) {
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                "spAssertionConsumer.jsp: Token is null." +
                se.getMessage());
        }
        token = null;
    }
    if (federate != null && federate.trim().equals("true") &&
        token == null) {
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("spAssertionConsumer.jsp: federate "
                + "is true, and token is null. do local login first.");
        }
        FSUtils.forwardRequest(request, response, getLocalLoginUrl(
            orgName, hostEntityId, metaManager, respInfo,
            requestURL, relayState));
        return;
    }
    Object newSession = null;
    try {
        newSession = SPACSUtils.processResponse(
            request, response, metaAlias, token, respInfo,
            orgName, hostEntityId, metaManager);
    } catch (SAML2Exception se) {
        if (se.getMessage().equals("NoUserMapping")) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("spAssertionConsumer.jsp:need "
                    + " local login!!");
            }
            // logging?
            FSUtils.forwardRequest(request, response, getLocalLoginUrl(
                orgName, hostEntityId, metaManager, respInfo,
                requestURL, relayState));
            return;
        }
        SAML2Utils.debug.error("spAssertionConsumer.jsp: SSO failed.", se);
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
            SAML2Utils.bundle.getString("SSOFailed"));
        return;
    }
    if (newSession == null) {
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("Session is null.");
            SAML2Utils.debug.message("spAssertionConsumer.jsp:Login has "
                + "failed!!");
        }
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
            SAML2Utils.bundle.getString("SSOFailed"));
        return;
    }
    SAML2Utils.debug.message("SSO SUCCESS");

    // redirect to relay state
    String finalUrl = SPACSUtils.getRelayState(
        relayState, orgName, hostEntityId, metaManager);
    String realFinalUrl = finalUrl;
    if (finalUrl != null && finalUrl.length() != 0) {
        try {
            realFinalUrl =
                sessionProvider.rewriteURL(newSession, finalUrl);
        } catch (SessionException se) {
            SAML2Utils.debug.message(
                 "spAssertionConsumer.jsp: URL rewriting failed.", se);
                 realFinalUrl = finalUrl;
        }
    }
    String redirectUrl = SPACSUtils.getIntermediateURL(
        orgName, hostEntityId, metaManager);
    String realRedirectUrl = null;
    if (redirectUrl != null && redirectUrl.length() != 0) {
        if (realFinalUrl != null && realFinalUrl.length() != 0) {
            if (redirectUrl.indexOf("?") != -1) {
                redirectUrl += "&goto=";
            } else {
                redirectUrl += "?goto=";
            }
            redirectUrl += URLEncDec.encode(realFinalUrl);
            try {
                realRedirectUrl = sessionProvider.rewriteURL(
                    newSession, redirectUrl);
            } catch (SessionException se) {
                SAML2Utils.debug.message(
                    "spAssertionConsumer.jsp: URL rewriting failed.", se);
                realRedirectUrl = redirectUrl;
            }
        } else {
            realRedirectUrl = redirectUrl;
        }
    } else {
        realRedirectUrl = finalUrl;
    }
    if (realRedirectUrl == null || redirectUrl.length() == 0) {
        %>
        <jsp:forward page="/saml2/jsp/default.jsp?message=ssoSuccess" />
        <%
    } else {
        // log it
        response.sendRedirect(redirectUrl);
    }
%>
</body>
</html>

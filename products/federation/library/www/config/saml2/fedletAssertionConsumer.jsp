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

   $Id: fedletAssertionConsumer.jsp,v 1.1 2008-03-26 04:31:40 qcheng Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>





<%@page
import="com.sun.identity.shared.encode.URLEncDec,
com.sun.identity.shared.encode.Base64,
com.sun.identity.federation.common.FSUtils,
com.sun.identity.saml.common.SAMLUtils,
com.sun.identity.saml2.common.SAML2Constants,
com.sun.identity.saml2.common.SAML2Exception,
com.sun.identity.saml2.common.SAML2SDKUtils,
com.sun.identity.saml2.meta.SAML2MetaException,
com.sun.identity.saml2.meta.SAML2MetaManager,
com.sun.identity.saml2.meta.SAML2MetaUtils,
com.sun.identity.saml2.profile.ResponseInfo,
com.sun.identity.saml2.profile.SPACSUtils,
com.sun.identity.saml2.profile.IDPProxyUtil,
com.sun.identity.saml2.protocol.Response,
com.sun.identity.plugin.session.SessionManager,
com.sun.identity.plugin.session.SessionProvider,
com.sun.identity.plugin.session.SessionException,
java.util.List
"
%>

<html>
<head>
    <title>Fedlet Assertion Consumer Service</title>
</head>

<body>
<%
    // check request, response, content length
    if ((request == null) || (response == null)) {
        response.sendError(response.SC_BAD_REQUEST,
                        SAML2SDKUtils.bundle.getString("nullInput"));
        return;
    }
/*
    // to avoid dos attack
    try {
        SAMLUtils.checkHTTPContentLength(request);
    } catch (ServletException se) {
        response.sendError(response.SC_BAD_REQUEST, se.getMessage());
        return;
    }
*/

    String requestURL = request.getRequestURL().toString();
    SAML2MetaManager metaManager = new SAML2MetaManager(); 
    if (metaManager == null) {
        // logging?
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        SAML2SDKUtils.bundle.getString("errorMetaManager"));
        return;
    }
    String metaAlias = SAML2MetaUtils.getMetaAliasByUri(requestURL);
    if ((metaAlias ==  null) || (metaAlias.length() == 0)) {
        // pick the first available one
        List spMetaAliases =
            metaManager.getAllHostedServiceProviderMetaAliases("/");
        if ((spMetaAliases != null) && !spMetaAliases.isEmpty()) {
            // get first one
            metaAlias = (String) spMetaAliases.get(0);
        }
        if ((metaAlias ==  null) || (metaAlias.length() == 0)) {
            response.sendError(response.SC_BAD_REQUEST,
                   SAML2SDKUtils.bundle.getString("nullSPEntityID"));
            return;
        }
    }
    String hostEntityId = null;
    try {
        hostEntityId = metaManager.getEntityByMetaAlias(metaAlias);
    } catch (SAML2MetaException sme) {
        // logging?
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        SAML2SDKUtils.bundle.getString("metaDataError"));
        return;
    }
    if (hostEntityId == null) {
        // logging?
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                        SAML2SDKUtils.bundle.getString("metaDataError"));
        return;
    }
    // organization is always root org
    String orgName = "/"; 
    String relayState = request.getParameter(SAML2Constants.RELAY_STATE);
    SessionProvider sessionProvider = null;
    ResponseInfo respInfo = null; 
    try {
        sessionProvider = SessionManager.getProvider();
    } catch (SessionException se) {
        response.sendError(
            response.SC_INTERNAL_SERVER_ERROR,
            se.getMessage());
        return;
    }
    respInfo = SPACSUtils.getResponse(
        request, response, orgName, hostEntityId, metaManager);

    Object newSession = null;
    try {
        newSession = SPACSUtils.processResponse(
            request, response, metaAlias, null, respInfo,
            orgName, hostEntityId, metaManager);
    } catch (SAML2Exception se) {
        SAML2SDKUtils.debug.error("fedletAssertionConsumer.jsp: SSO failed.", 
            se);
        if (se.isRedirectionDone()) {
            // response had been redirected already.
            return;
        }
        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
            SAML2SDKUtils.bundle.getString("SSOFailed"));
        return;
    }
    SAML2SDKUtils.debug.message("SSO SUCCESS");
    String[] redirected = sessionProvider.getProperty(newSession,
        SAML2Constants.RESPONSE_REDIRECTED);
    if ((redirected != null) && (redirected.length != 0) &&
        redirected[0].equals("true")) {
        SAML2SDKUtils.debug.message("Redirection already done in SPAdapter.");
        // response redirected already in SPAdapter
        return;
    }
    // redirect to relay state
    String finalUrl = SPACSUtils.getRelayState(
        relayState, orgName, hostEntityId, metaManager);
    String realFinalUrl = finalUrl;
    if (finalUrl != null && finalUrl.length() != 0) {
        try {
            realFinalUrl =
                sessionProvider.rewriteURL(newSession, finalUrl);
        } catch (SessionException se) {
            SAML2SDKUtils.debug.message(
                 "fedletAssertionConsumer.jsp: URL rewriting failed.", se);
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
                SAML2SDKUtils.debug.message(
                    "fedletAssertionConsumer.jsp: URL rewriting failed.", se);
                realRedirectUrl = redirectUrl;
            }
        } else {
            realRedirectUrl = redirectUrl;
        }
    } else {
        realRedirectUrl = finalUrl;
    }
    if (realRedirectUrl == null || (realRedirectUrl.trim().length() == 0)) {
            request.setAttribute(SAML2Constants.SAML_RESPONSE, Base64.encode(
                respInfo.getResponse().toXMLString(true,true).getBytes()));
        %>
          <jsp:forward page="/saml2/jsp/fedletDefault.jsp?message=ssoSuccess" />
        <%
    } else {
        if (realRedirectUrl.toLowerCase().startsWith("http") || 
            realRedirectUrl.toLowerCase().startsWith("https")) {
            // redirection case
            response.sendRedirect(realRedirectUrl);
        } else {
            // forward case
            request.setAttribute(SAML2Constants.SAML_RESPONSE, Base64.encode(
                respInfo.getResponse().toXMLString(true,true).getBytes()));
        %>
          <jsp:forward page="<%= realRedirectUrl %>" />
        <%
        }
    }
%>
</body>
</html>

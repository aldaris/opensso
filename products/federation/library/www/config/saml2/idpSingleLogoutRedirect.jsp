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

   $Id: idpSingleLogoutRedirect.jsp,v 1.5 2007-10-17 18:47:17 weisun2 Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>




<%@ page import="com.sun.identity.shared.debug.Debug" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Utils" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Constants" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Exception" %>
<%@ page import="com.sun.identity.saml2.profile.IDPCache" %>
<%@ page import="com.sun.identity.saml2.profile.IDPSingleLogout" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sun.identity.saml2.protocol.LogoutResponse" %>
<%@ page import="com.sun.identity.saml2.protocol.ProtocolFactory" %>
<%@ page import="com.sun.identity.saml2.profile.LogoutUtil" %>
<%--
    idpSingleLogoutRedirect.jsp

    - receives the LogoutRequest and sends the LogoutResponse to
    Service Provider from the Identity Provider.
    OR
    - receives the LogoutResponse from the Service Provider.

    Required parameters to this jsp are :
    - RelayState - the target URL on successful Single Logout
    - SAMLRequest - the LogoutRequest
    OR
    - SAMLResponse - the LogoutResponse

    Check the SAML2 Documentation for supported parameters.
--%>
<html>

<head>
    <title>SAMLv2 Single Logout Redirect binding at IDP</title>
</head>
<body bgcolor="#FFFFFF" text="#000000">

<%
    // Retrieves the LogoutRequest or LogoutResponse
    //Retrieves :
    //- RelayState - the target URL on successful Single Logout
    //- SAMLRequest - the LogoutRequest
    //OR
    //- SAMLResponse - the LogoutResponse

    String relayState = request.getParameter(SAML2Constants.RELAY_STATE);
    if (relayState != null) {
        String tmpRs = (String) IDPCache.relayStateCache.remove(relayState);
        if (tmpRs != null) {
            relayState = tmpRs;
        }
    }

    String samlResponse = request.getParameter(SAML2Constants.SAML_RESPONSE);
    if (samlResponse != null) {
        boolean doRelayState = true;
        try {
        /**
         * Gets and processes the Single <code>LogoutResponse</code> from SP,
         * destroys the local session, checks response's issuer
         * and inResponseTo.
         *
         * @param request the HttpServletRequest.
         * @param response the HttpServletResponse.
         * @param samlResponse <code>LogoutResponse</code> in the
         *          XML string format.
         * @param relayState the target URL on successful
         * <code>LogoutResponse</code>.
         * @throws SAML2Exception if error processing
         *          <code>LogoutResponse</code>.
         */
            doRelayState = IDPSingleLogout.processLogoutResponse(
                request, response,samlResponse, relayState);
            //IDP Proxy          
            String decodedStr =
                SAML2Utils.decodeFromRedirect(samlResponse);
            if (decodedStr == null) {
                throw new SAML2Exception(
                    SAML2Utils.bundle.getString(
                    "nullDecodedStrFromSamlResponse"));
            }
            LogoutResponse logoutRes =
                ProtocolFactory.getInstance().createLogoutResponse(
                decodedStr);
            String requestId = logoutRes.getInResponseTo();
            Map logoutResponseMap = (Map) 
                IDPCache.logoutResponseCache.get(requestId);
            if (logoutResponseMap != null &&
               (!logoutResponseMap.isEmpty())) { 
                LogoutResponse logoutResp = (LogoutResponse)
                    logoutResponseMap.get("LogoutResponse");     
                String location = (String) logoutResponseMap.get(
                    "Location");
                String spEntity = (String) logoutResponseMap.get(
                    "spEntityID"); 
                String idpEntity = (String) logoutResponseMap.get(
                    "idpEntityID"); 
                if (logoutResp != null && location != null && 
                    spEntity != null && idpEntity !=null) { 
                    LogoutUtil.sendSLOResponse(response, logoutResp,
                        location, relayState,
                        "/", spEntity, SAML2Constants.SP_ROLE, idpEntity);
                    return;
                }
            } 
        } catch (SAML2Exception sse) {
            SAML2Utils.debug.error("Error processing LogoutResponse :",
                sse);
            response.sendError(response.SC_BAD_REQUEST,
                SAML2Utils.bundle.getString(
                "LogoutResponseProcessingError"));
            return;
        } catch (Exception e) {
            SAML2Utils.debug.error("Error processing LogoutResponse ",e);
            response.sendError(response.SC_BAD_REQUEST,
                SAML2Utils.bundle.getString(
                "LogoutResponseProcessingError"));
            return;
        }

        if (!doRelayState) {
            if (relayState != null) {
                if (relayState.indexOf("?") != -1) {
                    response.sendRedirect(relayState 
                        + "&logoutStatus=logoutSuccess");
                } else {
                    response.sendRedirect(relayState 
                        + "?logoutStatus=logoutSuccess");
                }
            } else {
                %>
                <jsp:forward page="/saml2/jsp/default.jsp?message=idpSloSuccess" />
                <%
            }    
        }
    } else {
        String samlRequest = request.getParameter(SAML2Constants.SAML_REQUEST);
        if (samlRequest != null) {
            try {
            /**
             * Gets and processes the Single <code>LogoutRequest</code> from SP.
             *
             * @param request the HttpServletRequest.
             * @param response the HttpServletResponse.
             * @param samlRequest <code>LogoutRequest</code> in the
             *          XML string format.
             * @param relayState the target URL on successful
             * <code>LogoutRequest</code>.
             * @throws SAML2Exception if error processing
             *          <code>LogoutRequest</code>.
             */
            IDPSingleLogout.processLogoutRequest(request,response,
                samlRequest,relayState);
            } catch (SAML2Exception sse) {
                SAML2Utils.debug.error("Error processing LogoutRequest :", sse);
                response.sendError(response.SC_BAD_REQUEST,
                     SAML2Utils.bundle.getString("LogoutRequestProcessingError"));
                return;
            } catch (Exception e) {
                SAML2Utils.debug.error("Error processing LogoutRequest ",e);
                response.sendError(response.SC_BAD_REQUEST,
                     SAML2Utils.bundle.getString("LogoutRequestProcessingError"));
                return;
            }
        }
    }
%>

</body>
</html>

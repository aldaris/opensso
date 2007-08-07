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

   $Id: spSingleLogoutRedirect.jsp,v 1.4 2007-08-07 23:38:25 weisun2 Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>




<%@ page import="com.sun.identity.shared.debug.Debug" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Utils" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Constants" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Exception" %>
<%@ page import="com.sun.identity.saml2.profile.CacheObject" %>
<%@ page import="com.sun.identity.saml2.profile.SPCache" %>
<%@ page import="com.sun.identity.saml2.profile.SPSingleLogout" %>
<%@ page import="com.sun.identity.saml2.profile.IDPCache" %>
<%@ page import="com.sun.identity.saml2.profile.IDPSingleLogout" %>
<%@ page import="com.sun.identity.saml2.protocol.LogoutRequest" %>
<%@ page import="com.sun.identity.saml2.assertion.Issuer" %>
<%@ page import="com.sun.identity.saml2.profile.IDPProxyUtil" %>
<%@ page import="com.sun.identity.saml2.protocol.ProtocolFactory" %>
<%@ page import="java.util.Map" %>

<%--
    spSingleLogoutRedirect.jsp

    - receives the LogoutRequest and sends the LogoutResponse to
    Identity Provider from the Service Provider.
    OR
    - receives the LogoutResponse from the Identity Provider.

    Required parameters to this jsp are :
    - RelayState - the target URL on successful Single Logout
    - SAMLRequest - the LogoutRequest
    OR
    - SAMLResponse - the LogoutResponse

    Check the SAML2 Documentation for supported parameters.
--%>
<html>

<head>
    <title>SAMLv2 Single Logout Redirect binding at SP</title>
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
        CacheObject tmpRs= 
            (CacheObject) SPCache.relayStateHash.remove(relayState);
        if ((tmpRs != null)) {
            relayState = (String) tmpRs.getObject();
        }
    }
    
    String samlResponse = request.getParameter(SAML2Constants.SAML_RESPONSE);
    if (samlResponse != null) {
        try {
        /**
         * Gets and processes the Single <code>LogoutResponse</code> from IDP,
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
          Map infoMap = 
              SPSingleLogout.processLogoutResponse(request,response,
              samlResponse, relayState);
          String inRes = (String) infoMap.get("inResponseTo"); 
          String origLogoutRequest = (String) 
              IDPCache.proxySPLogoutReqCache.get(inRes); 
          if (origLogoutRequest != null && !origLogoutRequest.equals("")) {
              IDPCache.proxySPLogoutReqCache.remove(inRes);
              String decodedStr =
              SAML2Utils.decodeFromRedirect(origLogoutRequest);
              LogoutRequest logoutReq =
                  ProtocolFactory.getInstance().createLogoutRequest(
                  decodedStr);
              IDPProxyUtil.sendProxyLogoutResponse(response,logoutReq.getID(),
                  infoMap, logoutReq.getIssuer().getValue()); 
              return;        
          }          
        } catch (SAML2Exception sse) {
            SAML2Utils.debug.error("Error processing LogoutResponse :", sse);
            response.sendError(response.SC_BAD_REQUEST,
                 SAML2Utils.bundle.getString("LogoutResponseProcessingError"));
            return;
        } catch (Exception e) {
            SAML2Utils.debug.error("Error processing LogoutResponse ",e);
            response.sendError(response.SC_BAD_REQUEST,
                 SAML2Utils.bundle.getString("LogoutResponseProcessingError"));
            return;
        }

        if (relayState == null) {
            %>
            <jsp:forward page="/saml2/jsp/default.jsp?message=spSloSuccess" />
            <%
        }
    } else {
        String samlRequest = request.getParameter(SAML2Constants.SAML_REQUEST);
        if (samlRequest != null) {
            try {
            /**
             * Gets and processes the Single <code>LogoutRequest</code> from IDP.
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
            SPSingleLogout.processLogoutRequest(request,response,
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

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

   $Id: spMNIRequestInit.jsp,v 1.3 2007-09-11 22:02:16 weisun2 Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>




<%@ page import="com.sun.identity.shared.debug.Debug" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Constants" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Utils" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Exception" %>
<%@ page import="com.sun.identity.saml2.meta.SAML2MetaManager" %>
<%@ page import="com.sun.identity.saml2.meta.SAML2MetaUtils" %>
<%@ page import="com.sun.identity.saml2.profile.DoManageNameID" %>
<%@ page import="java.util.HashMap" %>

<%--
    idpMNIRequestInit.jsp initiates the ManageNameIDRequest at
    the Identity Provider.
    Required parameters to this jsp are :
    - metaAlias - identifier for Service Provider
    - idpEntityID - identifier for Identity Provider
    - requestType - the request type of ManageNameIDRequest (Terminate / NewID)

    Somce of the other optional parameters are :
    - relayState - the target URL on successful complete of the Request

    Check the SAML2 Documentation for supported parameters.

--%>
<html>

<head>
<title>SAMLv2 SP ManageNameIDRequest Initiation JSP</title>
</head>
<body bgcolor="#FFFFFF" text="#000000">

<%
    // Retreive the Request Query Parameters
    // metaAlias, idpEntiyID and RequestType are the required query parameters
    // metaAlias - Hosted Entity Id
    // idpEntityID - Identity Provider Identifier
    // requestType - the request type of ManageNameIDRequest (Terminate / NewID)
    // Query parameters supported will be documented.
    try {
        String metaAlias = request.getParameter("metaAlias");
        if ((metaAlias ==  null) || (metaAlias.length() == 0)) {
            response.sendError(response.SC_BAD_REQUEST,
                           SAML2Utils.bundle.getString("nullIDPEntityID"));
            return;
        }

        String idpEntityID = request.getParameter("idpEntityID");

        if ((idpEntityID == null) || (idpEntityID.length() == 0)) {
            response.sendError(response.SC_BAD_REQUEST,
                           SAML2Utils.bundle.getString("nullIDPEntityID"));
            return;
        }

        String requestType = request.getParameter("requestType");

        if ((requestType == null) || (requestType.length() == 0)) {
            response.sendError(response.SC_BAD_REQUEST,
                           SAML2Utils.bundle.getString("nullRequestType"));
            return;
        }

        String RelayState = request.getParameter(SAML2Constants.RELAY_STATE);
        String binding = DoManageNameID.getMNIBindingInfo(request, metaAlias,
                                        SAML2Constants.SP_ROLE, idpEntityID);
        
        if ((RelayState == null) || (RelayState.equals(""))) {
            SAML2MetaManager metaManager= new SAML2MetaManager();
            String hostEntity = metaManager.getEntityByMetaAlias(metaAlias);
            String realm = SAML2MetaUtils.getRealmByMetaAlias(metaAlias);
            RelayState = SAML2Utils.getAttributeValueFromSSOConfig(
                realm, hostEntity, SAML2Constants.SP_ROLE,
                SAML2Constants.DEFAULT_RELAY_STATE);
        } 

        HashMap paramsMap = new HashMap();
        paramsMap.put("metaAlias", metaAlias);
        paramsMap.put("idpEntityID", idpEntityID);
        paramsMap.put("requestType", requestType);
        paramsMap.put(SAML2Constants.ROLE, SAML2Constants.SP_ROLE);
        paramsMap.put(SAML2Constants.BINDING, binding);

        if (RelayState != null) {
            paramsMap.put(SAML2Constants.RELAY_STATE, RelayState);
        }

        Object sess = SAML2Utils.checkSession(request,response,
                                          metaAlias, paramsMap);
        if (sess == null) {
            return;
        }

        DoManageNameID.initiateManageNameIDRequest(request,response,
                                          metaAlias, idpEntityID, paramsMap);

        if (binding.equalsIgnoreCase(SAML2Constants.SOAP)) {
            if (RelayState != null) {
                response.sendRedirect(RelayState);
            } else {
                %>
                <jsp:forward page="/saml2/jsp/default.jsp?message=mniSuccess" />
                <%
            }
        }
    } catch (SAML2Exception e) {
        SAML2Utils.debug.error("Error sending ManageNameIDRequest " , e);
        response.sendError(response.SC_BAD_REQUEST,
                SAML2Utils.bundle.getString("requestProcessingMNIError"));
    }
%>

</body>
</html>

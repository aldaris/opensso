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

   $Id: validate.jsp,v 1.1 2008-02-04 20:13:23 exu Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>

<%--
TOFIX: localize all the messages.
--%>

<%-- imports --%>
<%@ page import="com.sun.identity.common.SystemConfigurationUtil" %>
<%@ page import="com.sun.identity.saml2.common.AccountUtils" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Constants" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Utils" %>
<%@ page import="com.sun.identity.saml2.meta.SAML2MetaManager" %>
<%@ page import="java.util.*, java.net.URLEncoder" %>
<%@ page import="com.iplanet.sso.SSOTokenManager,
            com.iplanet.sso.SSOException,
            com.iplanet.sso.SSOToken"
%>

<%-- functions --%>
<%!
    private static final String UNIVERSAL_IDENTIFIER =
        "sun.am.UniversalIdentifier";
%>

<%
    String deployuri = SystemConfigurationUtil.getProperty(
        "com.iplanet.am.services.deploymentDescriptor");
    if ((deployuri == null) || (deployuri.length() == 0)) {
        deployuri = "../..";
    }

    boolean iAmIdp = false;
    boolean iAmSp = false;

    String myMetaAlias = null;
    String myEntityID = null;
    String myTitle = null;
    String partnerEntityID = null;
    String partnerTitle = null;

    String thisUrl = request.getRequestURL().toString();
    String relayState = thisUrl;
    String queryString = request.getQueryString();
    if (queryString != null) {
        relayState = thisUrl + "?" + queryString;
    }
    
    String appBase = thisUrl.substring(0, thisUrl.lastIndexOf("/saml2") + 1);

    SSOToken ssoToken = null;
    boolean userLoggedIn = false;
    String userName = "";
    String userLabel = "";

    String idpMetaAlias = request.getParameter("idpMetaAlias");
    String spMetaAlias = request.getParameter("spMetaAlias");
    String idpEntity = request.getParameter("idpEntity");
    String spEntity = request.getParameter("spEntity");
    String ssoProfile = request.getParameter("ssoProfile");
    if ((ssoProfile == null) || (ssoProfile.length() == 0)) {
        ssoProfile = SAML2Constants.HTTP_ARTIFACT;
    }
    String sloProfile = request.getParameter("sloProfile");
    if ((sloProfile == null) || (sloProfile.length() == 0)) {
        sloProfile = SAML2Constants.HTTP_REDIRECT;
    }

    SAML2MetaManager mm = SAML2Utils.getSAML2MetaManager();
    if ((idpMetaAlias != null) && (idpMetaAlias.length() != 0)) {
        iAmIdp = true;
        myMetaAlias = idpMetaAlias;
        myEntityID = mm.getEntityByMetaAlias(myMetaAlias);
        if ((spEntity == null) || (spEntity.length() == 0)) {
            response.sendError(response.SC_BAD_REQUEST,
                    "This is an IDP, "
                    + "No SAML2 Trusted Partner SP Service Registered Here, "
                    + " Verify SAML2 Metadata Configuration");
            return;
        } else {
            partnerEntityID = spEntity;
        }
        myTitle =  "IDP: " + myEntityID;
        partnerTitle = "SP: " + partnerEntityID;
    } else if ((spMetaAlias != null) && (spMetaAlias.length() != 0)) {
        iAmSp = true;
        myMetaAlias = spMetaAlias;
        myEntityID = mm.getEntityByMetaAlias(myMetaAlias);
        if ((idpEntity == null) || (idpEntity.length() == 0)) {
            response.sendError(response.SC_BAD_REQUEST,
                    "This is an SP, "
                    + "No SAML2 Trusted IDP Service Registered Here, "
                    + " Verify SAML2 Metadata Configuration");
            return;
        } else {
            partnerEntityID = idpEntity;
        }
        myTitle =  "SP: " + myEntityID;
        partnerTitle = "IDP: " + partnerEntityID;
    }

    if(!iAmIdp && !iAmSp) {
        response.sendError(response.SC_BAD_REQUEST,
                "No SAML2 Service Hosted Here, "
                + " Verify SAML2 Metadata Configuration");
        return;
    }

    try {
        SSOTokenManager tokenManager = SSOTokenManager.getInstance();
        ssoToken = tokenManager.createSSOToken(request);
        if ((ssoToken != null) && tokenManager.isValidToken(ssoToken)) {
            userLoggedIn = true;
            userName = ssoToken.getProperty(UNIVERSAL_IDENTIFIER);
            userLabel = userName;
            int j = userName.indexOf("=");
            int k = userName.indexOf(",");
            if ((j > 0) && (k > j)) {
                userLabel = userName.substring(j+1,k).trim();
            }
            userLabel = userLabel.substring(0,1)
                    + ((userLabel.length() > 0)
                    ? userLabel.substring(1, userLabel.length())
                    : "");
        }
    } catch (SSOException e) {
        //response.sendError(response.SC_INTERNAL_SERVER_ERROR);
    }


%>

<html>
<head>
<title>SAMLv2 Setup Verification</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<%= deployuri %>/com_sun_web_ui/css/css_ns6up.css" />
</head>
<body>


<p>&nbsp;</p>                                                                                
    <h3><center><%= myTitle%></center></h3>
    <p>
    <h4>User <%= userLoggedIn ? userLabel + " is logged in." : "is logged out."%></h4>
    <hr/>
    <table cellpadding="2" cellspacing="2" border="0" width="100%">
    <tr>
    <td valign="top" align="left">
    <% if(iAmIdp) { %>  
    Following are the tests that can be performed on the Identity Provider:
    <% } else { %>  
    Following are the tests that can be performed on the Service Provider:
    <% } %>
    </td>
    </tr>
    <tr>
    <td valign="top" align="left">  </td>
    </tr>
    <tr>
    <!-- Login/Logout prompt -->
    <td valign="top" align="left">
      <ul>
        <li>
        <% if(!userLoggedIn) { %>   <!-- user not logged in -->
                    <% if(iAmIdp) { %>      <!-- not logged in, i am idp -->
                            <a href="<%= appBase %>idpssoinit?metaAlias=<%= myMetaAlias %>&spEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=<%= ssoProfile %>&RelayState=<%= URLEncoder.encode(relayState) %>">
                                Single Sign-On to <%= partnerTitle%></a>
                    <% } else { %>          <!-- not logged in, i am sp -->
                            <a href="<%= appBase %>spssoinit?metaAlias=<%= myMetaAlias %>&idpEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=<%= ssoProfile %>&RelayState=<%= URLEncoder.encode(relayState) %>">
                            Single Sign-On through <%=  partnerTitle%></a>
                    <% } %>
       <%  } else { %>             <!-- user logged in -->
            
                    <% if(iAmIdp) { %>      <!-- logged in, i am idp -->
                            <a href="<%= appBase %>IDPSloInit?<%= SAML2Constants.BINDING %>=<%= sloProfile %>&RelayState=<%= URLEncoder.encode(relayState) %>">
                               Single Logout</a>
                    <% } else { %>          <!-- logged in, i am sp -->
                            <a href="<%= appBase %>SPSloInit?idpEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=<%= sloProfile %>&RelayState=<%= URLEncoder.encode(relayState) %>">
                               Single Logout</a>
                    <% } %>
                <%  } %>
            </li>


        </td>
      </tr>
    </table>

</body>
</html>

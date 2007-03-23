<!--
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

   $Id: home.jsp,v 1.2 2007-03-23 22:24:24 bina Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
-->

<%@ include file="init.jspf" %>

<html>
<head><title>Book Flight With Great Air</title>
<body>
    <h3><center><%= myTitle%> 
            appreciates your business<%= userLoggedIn ? ", " + userLabel : ""%></center></h3>
    <hr/>
    <table cellpadding="2" cellspacing="2" border="0" width="100%">

        <tr>

            <!-- Login/Logout prompt -->
            <td valign="top" align="left">
                <% if(!userLoggedIn) { %>   <!-- user not logged in -->
                    <% if(iAmIdp) { %>      <!-- not logged in, i am idp -->
                            <a href="<%= localLoginUrl %>?goto=<%= thisUrl %>">
                                Login</a>
                    <% } else { %>          <!-- not logged in, i am sp -->
                            <a href="<%= appBase %>spssoinit?metaAlias=<%= myMetaAlias %>&idpEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=HTTP-Artifact&RelayState=<%= thisUrl %>">
                            Login, secure service provided by <%=  idpTitle%></a>
                    <% } %>
                <%  } else { %>             <!-- user logged in -->
                    <% if(iAmIdp) { %>      <!-- logged in, i am idp -->
                            <a href="<%= appBase %>IDPSloInit?<%= SAML2Constants.BINDING %>=<%= SAML2Constants.HTTP_REDIRECT %>&RelayState=<%= thisUrl %>">
                               Logout</a>
                    <% } else { %>          <!-- logged in, i am sp -->
                            <a href="<%= appBase %>SPSloInit?idpEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=<%= SAML2Constants.HTTP_REDIRECT %>&RelayState=<%= thisUrl %>">
                                Logout</a>
                    <% } %>
                <%  } %>
            </td>

            <!-- Federate/Defederate prompt only if user is logged in -->
            <td valign="top" align="right">
                <% if(userLoggedIn) { %>             <!-- user logged in -->
                    <% if(federatedWithPartner) { %> <!-- federated -->
                        <% if(iAmIdp) { %>           <!-- federated, i am idp -->
                                <a href="<%= appBase %>IDPMniInit?metaAlias=<%= myMetaAlias %>&spEntityID=<%= partnerEntityID %>&requestType=Terminate&RelayState=<%= thisUrl %>">
                                    Terminate Federation with <%= partnerTitle %></a>
                        <% } else { %>               <!-- federated, i am sp -->
                                <a href="<%= appBase %>SPMniInit?metaAlias=<%= myMetaAlias %>&idpEntityID=<%= partnerEntityID %>&requestType=Terminate&RelayState=<%= thisUrl %>">
                                    Terminate Federation with <%= partnerTitle %></a>
                        <% } %>
                <%  } else if(iAmIdp) { %>           <!-- not federated, i am idp -->
                                <a href="<%= appBase %>idpssoinit?metaAlias=<%= myMetaAlias %>&spEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=<%= SAML2Constants.HTTP_ARTIFACT %>&RelayState=<%= thisUrl %>">
                                    Federate with <%= partnerTitle %></a>
                        <% } else { %>               <!-- not federated, i am sp -->
                                <a href="<%= appBase %>spssoinit?metaAlias=<%= myMetaAlias %>&idpEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=HTTP-Artifact&RelayState=<%= thisUrl %>">
                                    Federate with <%= partnerTitle %></a>
                        <% } %>
                <%  } %>

            </td>

        </tr>

        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>


        <!-- links to hosted pages and pages hosted by partner -->
        <% if (userLoggedIn) { %>   <!-- user logged in -->
            <% if (iAmIdp) { %>     <!-- logged in, i am idp -->
                <tr>
                    <td align="center" colspan="2">
                        <a href="reserveFlight.jsp">
                            Reserve Flight with us, <%= myTitle %>
                        </a>
                    </td>
                </tr>
                <tr align="right">
                    <td align="center" colspan="2">
                        <a href="<%= appBase %>idpssoinit?metaAlias=<%= myMetaAlias %>&spEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=<%= SAML2Constants.HTTP_ARTIFACT %>&RelayState=<%= reserveCarWithPartnerUrl %>">
                            Reserve Car with our assosciate, <%= partnerTitle %>
                        </a>
                    </td>
                </tr>
            <% } else {%>           <!-- logged in, i am sp -->
                <tr>
                    <td align="center" colspan="2">
                        <a href="<%= reserveCarUrl %>">
                            Reserve Car with us, <%= myTitle %>
                        </a>
                    </td>
                </tr>
            <% } %>
        <% } else { %>              <!-- user not logged in -->
            <% if (iAmIdp) { %>     <!-- not logged in, i am idp -->
                <tr>
                    <td align="center" colspan="2">
                        <a href="<%= localLoginUrl %>?goto=<%= reserveFlightUrl %>">
                            Reserve Flight with us, <%= myTitle %>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="2">
                        <a href="<%= appBase %>idpssoinit?metaAlias=<%= myMetaAlias %>&spEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=<%= SAML2Constants.HTTP_ARTIFACT %>&RelayState=<%= reserveCarWithPartnerUrl %>">
                            Reserve Car with our assosciate, <%= partnerTitle %>
                        </a>
                    </td>
                </tr>
            <% } else {%>           <!-- not logged in, i am sp -->
                <tr>
                    <td align="center" colspan="2">
                        <a href="reserveCar.jsp">
                            <a href="<%= appBase %>spssoinit?metaAlias=<%= myMetaAlias %>&idpEntityID=<%= partnerEntityID %>&<%= SAML2Constants.BINDING %>=HTTP-Artifact&RelayState=<%= reserveCarUrl %>">
                                Reserve Car with us, <%= myTitle %>
                            </a>
                    </td>
                </tr>
            <% } %>
        <% } %>

    </table>

    <hr/>

    <!-- show link to partner sample home -->
    <table>
        <% if (partnerSampleHomeUrl != null) { %> 
            <tr>
                <td align="left">
                </td>
                <td width="100%" align="right">
                    <a href="<%= partnerSampleHomeUrl %>">
                            <%= partnerTitle %> Sample Home</a> 
                </td>
            </tr>
        <% } %>
    </table>

</body>
</html>

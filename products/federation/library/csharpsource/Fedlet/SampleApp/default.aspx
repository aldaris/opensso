<%--
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 * 
 * The contents of this file are subject to the terms
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
 * $Id: default.aspx,v 1.3 2009-05-21 23:46:55 ggennaro Exp $
 */
--%>
<%@ Page Language="C#" MasterPageFile="~/site.master"%>
<%@ Import Namespace="System.Xml" %>
<%@ Import Namespace="Sun.Identity.Saml2" %>
<%@ Import Namespace="Sun.Identity.Saml2.Exceptions" %>

<asp:Content ID="Content1" ContentPlaceHolderID="content" runat="server">

    <h1>Sample Application with OpenSSO and ASP.NET</h1>
    <p>
    This sample application demonstrates a simple Fedlet with OpenSSO and
    a .NET application. Please be sure to follow the instructions of the
    README file to ensure your sample application will function correctly.
    </p>

    <h2>Based on the README file, you should have...</h2>
    <ol class="instructions">
        <li>A Circle of Trust within your OpenSSO deployment.</li>
        <li>A hosted Identity Service Provider within your OpenSSO deployment.</li>
        <li>
            This sample application configured with metadata edited appropriately
            and placed into this application's <span class="resource">App_Data/</span>
            folder.
            <ol class="summary">
                <li>
                    The HTTP-POST service location should have been edited appropriately
                    within your OpenSSO deployment for this Service Provider.<br />
                    For example:
                    <span class="resource">http://sp.example.com/SampleApp/fedletapplication.aspx</span>
                </li>
                <li>
                    Optionally added attribute mappings to be passed within the assertion
                    to this sample application.
                </li>
            </ol>
        </li>
        <li>
            Placed the <span class="resource">Fedlet.dll</span> within this 
            application's <span class="resource">Bin/</span> folder.
        </li>
    </ol>

    <h2>To try it out...</h2>

    <%
        string idpLinks = "";
        string errorMessage = null;
        bool hasMultipleIdps = false;
        string preferredIdpEntityId = null;
        
        try
        {
            ServiceProviderUtility serviceProviderUtility;

            serviceProviderUtility = (ServiceProviderUtility)Cache["spu"];
            if (serviceProviderUtility == null)
            {
                serviceProviderUtility = new ServiceProviderUtility(Context);
                Cache["spu"] = serviceProviderUtility;
            }

            ServiceProvider sp = serviceProviderUtility.ServiceProvider;
            hasMultipleIdps = (serviceProviderUtility.IdentityProviders.Count > 1);
            preferredIdpEntityId = IdentityProviderDiscoveryUtils.GetPreferredIdentityProvider(Request);

            Hashtable identityProviders = serviceProviderUtility.IdentityProviders;
            foreach (string key in identityProviders.Keys)
            {
                IdentityProvider idp = (IdentityProvider) identityProviders[key];
                string ssoDeployment = null;
                string ssoMetaAlias = null;
                string pattern = "(.+?/opensso).+?/metaAlias(.+?)$";
                Match m = null;

                foreach (XmlNode node in idp.SingleSignOnServiceLocations)
                {
                    string binding = node.Attributes["Binding"].Value;
                    string location = node.Attributes["Location"].Value;
                    if ( binding != null && location != null)
                    {
                        m = Regex.Match(location, pattern);
                        if (m.Success && m.Groups.Count == 3)
                        {
                            ssoDeployment = m.Groups[1].Value;
                            ssoMetaAlias = m.Groups[2].Value;
                        }
                        break;
                    }
                }

                if (ssoMetaAlias == null)
                {
                    throw new ServiceProviderUtilityException("IDP initiated SSO is currently only supported with an OpenSSO deployment.");
                }
                
                string idpAuthUrl = string.Format("{0}/idpssoinit?NameIDFormat=urn:oasis:names:tc:SAML:2.0:nameid-format:transient&metaAlias={1}&spEntityID={2}&binding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST",
                                                  ssoDeployment, ssoMetaAlias, sp.EntityId);
                idpLinks += string.Format("<li><a href=\"{0}\">IDP initiated SSO with {1}</a>{2}</li>\n", 
                                          Server.HtmlEncode(idpAuthUrl), 
                                          Server.HtmlEncode(idp.EntityId),
                                          (preferredIdpEntityId == idp.EntityId ? " (preferred)" : string.Empty) );
            }
        }
        catch (ServiceProviderUtilityException spue)
        {
            errorMessage = spue.Message;
        }
    %>

    <% if( errorMessage == null ) { %>
    
        <p>
        Perform the IDP initiated Single Sign On to take you to the OpenSSO login form. 
        Upon successfull login, you will be taken to the HTTP-POST destination configured 
        for your Fedlet for this sample application.  
        </p>
        
        <ul>
        <%=idpLinks%>
        </ul>
        <%
           if( hasMultipleIdps ) 
           {
               string currentPage = Request.Url.AbsoluteUri;
               if( currentPage.IndexOf("?") > 0 ) 
               {
                   currentPage = currentPage.Substring(0, currentPage.IndexOf("?"));
               }
        %>
            <p>
            Since you have multiple identity providers specified, you can optionally
            <a href="discoveridp.aspx?RelayState=<%=currentPage %>">use the IDP Discovery Service</a> to determine 
            your preferred IDP if you have specified the reader service within your 
            circle-of-trust file.
            </p>
        <% } %>

        <p>
        The above demonstrates how a .NET developer could issue a redirect
        to non-authenticated users from their .NET application to the OpenSSO 
        login page for authentication.
        </p>
    
    <% } else { %>
    
        <p>
        Please be sure to follow the instructions within the README file as well as review the 
        information above.  
        </p>
        <p>
        The following error was encountered:<br />
        <%=errorMessage %>
        </p>
    
    <% } %>


</asp:Content>

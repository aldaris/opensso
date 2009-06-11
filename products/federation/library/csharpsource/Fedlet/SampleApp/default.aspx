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
 * $Id: default.aspx,v 1.4 2009-06-11 18:38:00 ggennaro Exp $
 */
--%>
<%@ Page Language="C#" MasterPageFile="~/site.master"%>
<%@ Import Namespace="System.Xml" %>
<%@ Import Namespace="Sun.Identity.Saml2" %>
<%@ Import Namespace="Sun.Identity.Saml2.Exceptions" %>
<asp:Content ID="Content1" ContentPlaceHolderID="content" runat="server">
<%
    string fedletUrl = Request.Url.AbsoluteUri;
    fedletUrl = fedletUrl.Substring(0, fedletUrl.LastIndexOf("/")) + "/fedletapplication.aspx";
%>

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
                    <span class="resource"><%=Server.HtmlEncode(fedletUrl)%></span>
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
        StringBuilder idpListItems = new StringBuilder();
        string idpListItemFormat = "<li>IDP initiated SSO with <span class=\"resource\">{0}</span> using <a href=\"{1}\">{2}</a> or <a href=\"{3}\">{4}</a></li>";
        string idpUrlFormat = "{0}/idpssoinit?NameIDFormat=urn:oasis:names:tc:SAML:2.0:nameid-format:transient&metaAlias={1}&spEntityID={2}&binding={3}";

        StringBuilder spListItems = new StringBuilder();
        string spListItemFormat = "<li>SP initiated SSO with <span class=\"resource\">{0}</span> using {1}</li>";
        string spLinkFormat = "<a href=\"{0}\">{1}</a>";
        string spUrlFormat = "spinitiatedsso.aspx?idpEntityId={0}&binding={1}";

        string errorMessage = null;
        bool hasMultipleIdps = false;
        bool spSupportsPost = false;
        bool spSupportsArtifact = false;
        
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
            spSupportsArtifact = (!String.IsNullOrEmpty(sp.GetAssertionConsumerServiceLocation(Saml2Constants.HttpArtifactProtocolBinding)));
            spSupportsPost = (!String.IsNullOrEmpty(sp.GetAssertionConsumerServiceLocation(Saml2Constants.HttpPostProtocolBinding)));

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

                if (ssoMetaAlias != null)
                {
                    string postUrl = string.Format(idpUrlFormat, ssoDeployment, ssoMetaAlias, sp.EntityId, Saml2Constants.HttpPostProtocolBinding);
                    string artifactUrl = string.Format(idpUrlFormat, ssoDeployment, ssoMetaAlias, sp.EntityId, Saml2Constants.HttpArtifactProtocolBinding);
                    idpListItems.Append(string.Format(idpListItemFormat,
                                                      Server.HtmlEncode(idp.EntityId),
                                                      Server.HtmlEncode(postUrl),
                                                      "HTTP Post",
                                                      Server.HtmlEncode(artifactUrl),
                                                      "HTTP Artifact" ));
                }

                if ( spSupportsPost || spSupportsArtifact )
                {
                    StringBuilder spLinks = new StringBuilder();
                    string urlValue = null;
                    
                    if (spSupportsPost)
                    {
                        urlValue = String.Format(spUrlFormat, idp.EntityId, Saml2Constants.HttpPostProtocolBinding);
                        spLinks.Append(String.Format(spLinkFormat, Server.HtmlEncode(urlValue), "HTTP Post"));
                    }
                    if (spSupportsArtifact)
                    {
                        if (spLinks.Length != 0)
                        {
                            spLinks.Append(" or ");
                        }

                        urlValue = String.Format(spUrlFormat, idp.EntityId, Saml2Constants.HttpArtifactProtocolBinding);
                        spLinks.Append(String.Format(spLinkFormat, Server.HtmlEncode(urlValue), "HTTP Artifact"));
                    }

                    spListItems.Append(String.Format(spListItemFormat, Server.HtmlEncode(idp.EntityId), spLinks.ToString()));
                }
            }

            if (idpListItems.Length == 0)
            {
                idpListItems.Append("<li>IDP initiated SSO is currently only supported with an OpenSSO deployment.</li>");
            }
            if (spListItems.Length == 0)
            {
                spListItems.Append("<li>SP initiated SSO requires either HTTP-POST or HTTP-Artifact assertion consumer service locations to be configured.</li>");
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
        Upon successfull login, you will be taken to the location configured for your Fedlet
        for this sample application.  
        </p>
        
        <ul>
        <%=idpListItems.ToString()%>
        </ul>
        
        <p>
        Alternatively, you can perform SP initiated Single Sign On with the link(s) provided
        below.
        </p>
        
        <ul>
        <%=spListItems.ToString()%>
        </ul>
        
        <% if( hasMultipleIdps ) { %>
            <p>
            Since you have multiple identity providers specified, you can optionally
            <a href="spinitiatedsso.aspx">use the IDP Discovery Service</a> 
            to perform Single Sign On with your preferred IDP if you have specified 
            the reader service within your circle-of-trust file.
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

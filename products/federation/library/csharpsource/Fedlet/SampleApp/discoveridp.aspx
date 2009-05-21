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
 * $Id: discoveridp.aspx,v 1.2 2009-05-21 23:46:55 ggennaro Exp $
 */
--%>
<%@ Page Language="C#" %>
<%@ Import Namespace="System.IO" %>
<%@ Import Namespace="System.Xml" %>
<%@ Import Namespace="Sun.Identity.Saml2" %>
<%@ Import Namespace="Sun.Identity.Saml2.Exceptions" %>
<%
    ServiceProviderUtility serviceProviderUtility = (ServiceProviderUtility)Cache["spu"];
    if (serviceProviderUtility == null)
    {
        serviceProviderUtility = new ServiceProviderUtility(Context);
        Cache["spu"] = serviceProviderUtility;
    }

    // Determine if the IDP has already been discovered...
    string idpEntityId = IdentityProviderDiscoveryUtils.GetPreferredIdentityProvider(Request);

    if (idpEntityId == null)
    {
        // Discover the IDP by redirecting to the reader service.
        IdentityProviderDiscoveryUtils.StoreRequestParameters(Context);

        Uri readerServiceUrl = IdentityProviderDiscoveryUtils.GetReaderServiceUrl(serviceProviderUtility, Context);

        if (readerServiceUrl != null)
        {
            IdentityProviderDiscoveryUtils.RedirectToReaderService(readerServiceUrl, Context);
            return;
        }
    }

    // Retrieve all previously stored parameters and reset the discovery
    // process if we've exhausted all reader services...
    NameValueCollection parameters = IdentityProviderDiscoveryUtils.RetrieveRequestParameters(Context);
    IdentityProviderDiscoveryUtils.ResetDiscovery(Context);

    // Check for required parameters...
    if (String.IsNullOrEmpty(parameters["RelayState"]))
    {
        Response.StatusCode = 400;
        Response.StatusDescription = "RelayState not provided.";
        Response.End();
        return;
    }

    // Redirect back to the RelayState...
    StringBuilder redirectUrl = new StringBuilder();
    redirectUrl.Append(parameters["RelayState"]);

    // ...with all original parameters...    
    bool foundFirst = false;
    foreach (string name in parameters.Keys)
    {
        if (foundFirst)
        {
            redirectUrl.Append("&");
        }
        else
        {
            foundFirst = true;
            redirectUrl.Append("?");
        }

        redirectUrl.Append(name);
        redirectUrl.Append("=");
        redirectUrl.Append(parameters[name]);
    }

    // ...and pass on the value provided by the reader...
    if (idpEntityId != null)
    {
        if (parameters != null)
        {
            redirectUrl.Append("&");
        }
        else
        {
            redirectUrl.Append("?");
        }

        redirectUrl.Append(IdentityProviderDiscoveryUtils.CommonDomainCookieName);
        redirectUrl.Append("=");
        redirectUrl.Append(Server.HtmlEncode(Request.QueryString[IdentityProviderDiscoveryUtils.CommonDomainCookieName]));
    }
        
    Response.Redirect(redirectUrl.ToString(), true);
%>
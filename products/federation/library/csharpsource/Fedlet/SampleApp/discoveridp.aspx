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
 * $Id: discoveridp.aspx,v 1.1 2009-05-19 16:01:05 ggennaro Exp $
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

    string commonDomainDiscoverySessionAttribute = "_cotList";
    
    StringBuilder homePageUrl = new StringBuilder();
    homePageUrl.Append("default.aspx");
    
    if (Request.QueryString[Saml2Constants.CommonDomainCookieName] != null)
    {
        // Value obtained, clear the session attribute and redirect with results to the homepage.
        Session[commonDomainDiscoverySessionAttribute] = null;
        
        homePageUrl.Append("?");
        homePageUrl.Append(Saml2Constants.CommonDomainCookieName);
        homePageUrl.Append("=");
        homePageUrl.Append(Request.QueryString[Saml2Constants.CommonDomainCookieName]);

        Response.Redirect(homePageUrl.ToString(), true);
    }
    else
    {
        // Value not obtained yet, redirect to reader service.
        ArrayList cotList = (ArrayList)Session[commonDomainDiscoverySessionAttribute];

        if (cotList == null)
        {
            cotList = new ArrayList();
            foreach (string cotName in serviceProviderUtility.CircleOfTrusts.Keys)
            {
                CircleOfTrust cot = (CircleOfTrust)serviceProviderUtility.CircleOfTrusts[cotName];

                if (cot.ReaderServiceUrl != null)
                {
                    cotList.Add(cotName);
                }
            }
        }

        IEnumerator enumerator = cotList.GetEnumerator();
        if (enumerator.MoveNext())
        {
            Response.AppendHeader("test", cotList.Count.ToString());
            string cotName = (string)enumerator.Current;
            cotList.Remove(cotName);
            Session[commonDomainDiscoverySessionAttribute] = cotList;

            CircleOfTrust cot = (CircleOfTrust)serviceProviderUtility.CircleOfTrusts[cotName];
            string readerSvc = cot.ReaderServiceUrl.AbsoluteUri;

            // redirect to the service and terminate the calling response.
            StringBuilder readerSvcUrl = new StringBuilder();
            readerSvcUrl.Append(readerSvc);
            readerSvcUrl.Append("?");
            readerSvcUrl.Append("RelayState=");
            readerSvcUrl.Append(Request.Url.AbsoluteUri);

            Response.Redirect(readerSvcUrl.ToString(), true);
            return;
        }

        // Exhausted list, reset the session attribute head home.
        Session[commonDomainDiscoverySessionAttribute] = null;
        homePageUrl.Append("?idpNotDiscovered");
        Response.Redirect(homePageUrl.ToString(), true);        
    }
%>
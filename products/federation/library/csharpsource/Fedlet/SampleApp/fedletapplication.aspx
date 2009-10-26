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
 * $Id: fedletapplication.aspx,v 1.4 2009-10-26 18:55:36 ggennaro Exp $
 */
--%>
<%@ Page Language="C#" MasterPageFile="~/site.master" %>
<%@ Import Namespace="System.IO" %>
<%@ Import Namespace="System.Xml" %>
<%@ Import Namespace="Sun.Identity.Saml2" %>
<%@ Import Namespace="Sun.Identity.Saml2.Exceptions" %>

<asp:Content ID="Content1" ContentPlaceHolderID="content" runat="server">

    <%
        string errorMessage = null;
        string errorTrace = null;
        AuthnResponse authnResponse = null;
        try
        {
            ServiceProviderUtility serviceProviderUtility;

            serviceProviderUtility = (ServiceProviderUtility)Cache["spu"];
            if (serviceProviderUtility == null)
            {
                serviceProviderUtility = new ServiceProviderUtility(Context);
                Cache["spu"] = serviceProviderUtility;
            }

            authnResponse = serviceProviderUtility.GetAuthnResponse(Context);
        }
        catch (Saml2Exception se)
        {
            errorMessage = se.Message;
            errorTrace = se.StackTrace;
            if (se.InnerException != null)
                errorTrace += "<br/>" + se.InnerException.StackTrace;
        }
        catch (ServiceProviderUtilityException spue)
        {
            errorMessage = spue.Message;
            errorTrace = spue.StackTrace;
            if (spue.InnerException != null)
                errorTrace += "<br/>" + spue.InnerException.StackTrace;
        }
    %>

    <h1>Sample Application with OpenSSO and ASP.NET</h1>
    <p>
    Once succesfully authenticated by your OpenSSO deployment, your browser was redirected
    to this location with a SAML response. This response can be consumed as follows:
    </p>

    <div class="code">
    AuthnResponse authnResponse = null;
    try
    {
        ServiceProviderUtility serviceProviderUtility = new ServiceProviderUtility(Context);
        authnResponse = serviceProviderUtility.GetAuthnResponse(Context);
    }
    catch (Saml2Exception se)
    {
        // invalid AuthnResponse received
    }
    catch (ServiceProviderUtilityException spue)
    {
        // issues with deployment (reading metadata)
    }
    </div>
    
    <% if (errorMessage != null) { %>
        <p>
        However, an error occured:
        </p>
<div class="code">
<%=Server.HtmlEncode(errorMessage) %><br />
<%=Server.HtmlEncode(errorTrace) %>
</div>

    <% } else { %>
        <p>
        Once the <span class="resource">AuthnResponse</span> object has been retrieved, you could
        easily access attributes from the response as demonstrated below:
        </p>
        
        <table class="output">
        <tr>
            <th>Method</th>
            <th>Returns</th>
            <th>Output</th>
        </tr>
        <tr>
            <td>authnResponse.XmlDom</td>
            <td>System.Xml.XPath.IXPathNavigable</td>
            <td>
                <form action="javascript:void();" method="get">
                <textarea rows="5" cols="80"><%
                    StringWriter stringWriter = new StringWriter();
                    XmlTextWriter xmlWriter = new XmlTextWriter(stringWriter);
                    XmlDocument xml = (XmlDocument)authnResponse.XmlDom;
                    xml.WriteTo(xmlWriter);
                    Response.Write(Server.HtmlEncode(stringWriter.ToString()));
                %></textarea>
                </form>
            </td>
        </tr>
        <tr>
            <td>authnResponse.SubjectNameId</td>
            <td>System.String</td>
            <td><%=Server.HtmlEncode(authnResponse.SubjectNameId)%></td>
        </tr>
        <tr>
            <td>authnResponse.Attributes</td>
            <td>System.Collections.Hashtable</td>
            <td>
                <table class="samlAttributes">
                <tr>
                  <th>key</th>
                  <th>value(s)</th>
                </tr>
                <%
                    foreach (string key in authnResponse.Attributes.Keys)
                    {
                        ArrayList values = (ArrayList)authnResponse.Attributes[key];

                        Response.Write("<tr>\n");
                        Response.Write("<td>" + Server.HtmlEncode(key) + "</td>\n");
                        Response.Write("<td>\n");
                        foreach (string value in values)
                        {
                            Response.Write(Server.HtmlEncode(value) + "<br/>\n");
                        }
                        Response.Write("</td>\n");
                        Response.Write("</tr>\n");
                    }
                %>
                </table>
            </td>
        </tr>
        </table>
    <% } %>

    <p>
    Return to the <a href="default.aspx">homepage</a> to try other examples available in this sample application.
    </p>


</asp:Content>

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

   $Id: exportmetadata.jsp,v 1.1 2008-01-17 01:10:35 qcheng Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>

<%-- NOTE : remove this JSP from the OpenSSO WAR if you don't want to 
     publically expose your hosted metadata, but some of the SAML2 metadata
     workflows will not work anymore --%> 

<%@ page import="com.sun.identity.saml2.common.SAML2Constants" %>
<%@ page import="com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement" %>
<%@ page import="com.sun.identity.saml2.meta.SAML2MetaManager" %>
<%@ page import="com.sun.identity.saml2.meta.SAML2MetaUtils" %>
<%@ page import="java.util.List" %>

<%
    // This JSP is used to export standard entity metadata, 
    // there are three supported query parameters:
    //    * role     -- role of the entity: sp, idp or any
    //    * realm    -- realm of the entity 
    //    * entityid -- ID of the entity to be exported
    // If none of the query parameter is specified, it will try to export
    // the first hosted SP metadata under root realm. If there is no hosted 
    // SP under the root realm, the first hosted IDP under root realm will
    // be exported. If there is no hosted SP or IDP, an error message will
    // be displayed.
    String metaXML = null;
    String errorMsg = null;
    try {
        String role = request.getParameter("role");
        if ((role == null) || (role.length() == 0)) {
            // default role is any if not specified
            role = "any";
        } 
        String realm = request.getParameter("realm");
        if ((realm == null) || (realm.length() == 0)) {
            // default to root realm
            realm = "/";
        }
        String entityID = request.getParameter("entityid");
        SAML2MetaManager manager = new SAML2MetaManager();
        if ((entityID == null) || (entityID.length() == 0)) {
            // find first available one
            List providers;
            if ("sp".equals(role)) {
                providers = manager. 
                    getAllHostedServiceProviderEntities(realm);
            } else if ("idp".equals(role)) {
                providers = manager.
                    getAllHostedIdentityProviderEntities(realm);
            } else {
                // will return any role
                // try SP first
                providers = manager.
                    getAllHostedServiceProviderEntities(realm);
                if ((providers == null) || providers.isEmpty()) {
                    providers = manager.
                        getAllHostedIdentityProviderEntities(realm);
                }
            }
            if ((providers != null) && !providers.isEmpty()) {
                entityID = (String) providers.iterator().next();
            }
        }

        if ((entityID == null) || (entityID.length() == 0)) {
            errorMsg = "No matching entity metadata found.";
        } else {
            EntityDescriptorElement desp = manager.getEntityDescriptor(
                realm, entityID);
            if (desp == null) {
                errorMsg = "No metadata for entity \"" + entityID +
                    "\" under realm \"" + realm + "\" found.";
            } else {
                metaXML = SAML2MetaUtils.convertJAXBToString(desp);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        errorMsg = e.getMessage();
    }
    if (errorMsg != null) {
%>
ERROR : <%= errorMsg %>
<%
    } else {
        response.setContentType("text/xml");
        response.setHeader("Pragma", "no-cache");
        response.getWriter().print(metaXML);
    }
%>

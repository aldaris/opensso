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
 * $Id: Saml2Constants.cs,v 1.2 2009-05-19 16:01:03 ggennaro Exp $
 */

namespace Sun.Identity.Saml2
{
    /// <summary>
    /// Constants used in the SAMLv2 context.
    /// </summary>
    public static class Saml2Constants
    {
        /// <summary>
        /// Constant for the HTTP-POST form parameter for SAML responses.
        /// </summary>
        public const string HttpPostParameter = "SAMLResponse";

        /// <summary>
        /// Constant for status codes used in SAML responses.
        /// </summary>
        public const string Success = "urn:oasis:names:tc:SAML:2.0:status:Success";

        /// <summary>
        /// Constant for the name of the common domain cookie.
        /// </summary>
        public const string CommonDomainCookieName = "_saml_idp";

        /// <summary>
        /// Constant for defining the length of IDs used in SAMLv2
        /// assertions, requests, and responses.
        /// </summary>
        public const int IdLength = 20;
    }
}

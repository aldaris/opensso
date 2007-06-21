/* The contents of this file are subject to the terms
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
 * $Id: WSFederationConstants.java,v 1.1 2007-06-21 23:01:34 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.common;

/**
 *
 * @author ap102904
 */
public final class WSFederationConstants {
    public static final String WSIGNIN10 = "wsignin1.0";
    
    public static final String POST_RST_RESPONSE_PARAM = "wresponse";
    public static final String URN_OASIS_NAMES_TC_SAML_11 = 
        "urn:oasis:names:tc:SAML:1.1";
    public static final String CLAIMS_UPN_URI = 
        "http://schemas.xmlsoap.org/claims/UPN";
    public static final String WS_ADDRESSING_URI = 
        "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    public static final String CLAIMS_UPN_DISPLAY_NAME = "UPN";
    
    // SP Entity Config Attributes
    public static final String ACCOUNT_REALM_SELECTION = 
        "AccountRealmSelection";
    public static final String ACCOUNT_REALM_COOKIE_NAME = 
        "AccountRealmCookieName";
    public static final String HOME_REALM_DISCOVERY_SERVICE = 
        "HomeRealmDiscoveryService";
    
    // IdP Entity Config Attributes
    public static final String DISPLAY_NAME = "DisplayName";
    
    public static final String ACCOUNT_REALM_COOKIE_NAME_DEFAULT = 
        "amWSFederationAccountRealm";
    
    public static final String USERAGENT = "user-agent";
    public static final String COOKIE = "cookie";

    public static final String ACCOUNT_REALM_SELECTION_DEFAULT = COOKIE;
}

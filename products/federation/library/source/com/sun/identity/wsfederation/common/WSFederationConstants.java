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
 * $Id: WSFederationConstants.java,v 1.2 2007-08-01 21:04:45 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.common;

/**
 * <code>WSFederationConstants</code> defines various constants for the 
 * WS-Federation implementation
 */
public final class WSFederationConstants {
    /**
     * WS-Federation data store provider name.
     */ 
    public static final String WSFEDERATION = "wsfederation";
    /**
     * WS-Federation 'sign-in' action.
     */ 
    public static final String WSIGNIN10 = "wsignin1.0";
    /**
     * WS-Federation 'sign-out' action.
     */ 
    public static final String WSIGNOUT10 = "wsignout1.0";
    /**
     * WS-Federation 'sign-out cleanup' action. This is handled identically
     * to <code>WSIGNOUT10</code>, following the WS-Federation 1.1 
     * specification.
     */ 
    public static final String WSIGNOUTCLEANUP10 = "wsignoutcleanup1.0";
    /**
     * XML tag name for <code>&lt;RequestedSecurityToken%gt;</code>.
     */ 
    public static final String RST_TAG_NAME = "RequestedSecurityToken";
    /**
     * XML tag name for <code>&lt;RequestSecurityTokenResponse%gt;</code>.
     */ 
    public static final String RSTR_TAG_NAME = "RequestSecurityTokenResponse";
    /**
     * XML tag name for <code>&lt;AppliesTo%gt;</code>.
     */ 
    public static final String APPLIESTO_TAG_NAME = "AppliesTo";
    /**
     * XML tag name for <code>&lt;Address%gt;</code>.
     */ 
    public static final String ADDRESS_TAG_NAME = "Address";
    /**
     * SAML 1.1 URN.
     */ 
    public static final String URN_OASIS_NAMES_TC_SAML_11 = 
        "urn:oasis:names:tc:SAML:1.1";
    /**
     * User Principal Name (UPN) claim URI.
     */ 
    public static final String CLAIMS_UPN_URI = 
        "http://schemas.xmlsoap.org/claims/UPN";
    /**
     * Email address claim URI.
     */ 
    public static final String CLAIMS_EMAIL_ADDRESS_URI = 
        "http://schemas.xmlsoap.org/claims/EmailAddress";
    /**
     * Common name claim URI.
     */ 
    public static final String CLAIMS_COMMON_NAME_URI = 
        "http://schemas.xmlsoap.org/claims/CommonName";
    /**
     * Group claim URI.
     */ 
    public static final String CLAIMS_GROUP_URI = 
        "http://schemas.xmlsoap.org/claims/Group";
    /**
     * WS-Addressing URI.
     */ 
    public static final String WS_ADDRESSING_URI = 
        "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    /**
     * Display name for UPN claim.
     */ 
    public static final String CLAIMS_UPN_DISPLAY_NAME = 
        "User Principal Name";
    /**
     * Display name for Email Address claim.
     */ 
    public static final String CLAIMS_EMAIL_ADDRESS_DISPLAY_NAME = 
        "Email Address";
    /**
     * Display name for Common Name claim.
     */ 
    public static final String CLAIMS_COMMON_NAME_DISPLAY_NAME = 
        "Common Name";
    /**
     * Display name for Group claim.
     */ 
    public static final String CLAIMS_GROUP_DISPLAY_NAME = 
        "Group";
    
    /**
     * Configuration attribute for account realm selection mechanism.
     */ 
    public static final String ACCOUNT_REALM_SELECTION = 
        "AccountRealmSelection";
    /**
     * Configuration attribute for account realm cookie name.
     */ 
    public static final String ACCOUNT_REALM_COOKIE_NAME = 
        "AccountRealmCookieName";
    /**
     * Configuration attribute for home realm discovery URL.
     */ 
    public static final String HOME_REALM_DISCOVERY_SERVICE = 
        "HomeRealmDiscoveryService";

    /**
     * Configuration attribute for provider display name.
     */ 
    public static final String DISPLAY_NAME = 
        "displayName";
    /**
     * Configuration attribute for default UPN domain.
     */ 
    public static final String UPN_DOMAIN = 
        "upnDomain";
    /**
     * Default value for account realm cookie name
     */ 
    public static final String ACCOUNT_REALM_COOKIE_NAME_DEFAULT = 
        "amWSFederationAccountRealm";
    /**
     * User agent HTTP header name
     */ 
    public static final String USERAGENT = "user-agent";
    /**
     * Cookie HTTP header name
     */ 
    public static final String COOKIE = "cookie";
    /**
     * Default mechanism for carrying account realm
     */ 
    public static final String ACCOUNT_REALM_SELECTION_DEFAULT = COOKIE;
    
    /**
     * NameID info attribute.
     */ 
    public static final String NAMEID_INFO = "sun-fm-wsfed-nameid-info";

    /**
     * NameID info key attribute.
     */
    public static final String NAMEID_INFO_KEY = "sun-fm-wsfed-nameid-infokey";
    
    /**
     * Session property name for list of service provider to which this identity
     * provider has sent a token
     */
    public static final String SESSION_SP_LIST = "sun-fm-wsfed-sp-list";
    
    /**
     * Session property name for identity provider from which this service
     * provider has received a token
     */
    public static final String SESSION_IDP = "sun-fm-wsfed-idp";
    
    /**
     * Attribute name for communicating form action URL from servlet to JSP
     */
    public static final String POST_ACTION = 
        "com.sun.identity.wsfederation.post.action";
    /**
     * Attribute name for communicating WS-Federation wa parameter from servlet 
     * to JSP
     */
    public static final String POST_WA =
        "com.sun.identity.wsfederation.post.wa";
    /**
     * Attribute name for communicating WS-Federation wctx parameter from 
     * servlet to JSP
     */
    public static final String POST_WCTX =
        "com.sun.identity.wsfederation.post.wctx";
    /**
     * Attribute name for communicating WS-Federation wresult parameter from 
     * servlet to JSP
     */
    public static final String POST_WRESULT =
        "com.sun.identity.wsfederation.post.wresult";
    
    /**
     * Attribute name for communicating local provider display name from 
     * servlet to JSP
     */
    public static final String LOGOUT_DISPLAY_NAME =
        "com.sun.identity.wsfederation.logout.displayname";
    /**
     * Attribute name for communicating WS-Federation wreply parameter from 
     * servlet to JSP
     */
    public static final String LOGOUT_WREPLY =
        "com.sun.identity.wsfederation.logout.wreply";
    /**
     * Attribute name for communicating list of providers from 
     * servlet to JSP
     */
    public static final String LOGOUT_PROVIDER_LIST =
        "com.sun.identity.wsfederation.logout.providerlist";
    /**
     * Debug log name.
     */
    public static String AM_WSFEDERATION = "amWSFederation";
    /**
     * Resource bundle name.
     */
    public static final String BUNDLE_NAME = "libWSFederation";
    /**
     * Constant used to identify meta alias in URL.
     */
    public static final String NAME_META_ALIAS_IN_URI = "metaAlias";
    /**
     * Entity ID to use if WS-Federation omits it.
     */
    public static final String DEFAULT_FEDERATION_ID = 
        "sunFMWSFederationDefaultFederationID";
    /**
     * WS-Federation HTTP parameter for 'action'.
     */
    public static final String WA = "wa";
    /**
     * WS-Federation HTTP parameter for 'result'.
     */
    public static final String WRESULT = "wresult";
    /**
     * WS-Federation HTTP parameter for 'home realm'.
     */
    public static final String WHR = "whr";
    /**
     * WS-Federation HTTP parameter for 'requesting realm'.
     */
    public static final String WTREALM = "wtrealm";
    /**
     * WS-Federation HTTP parameter for 'destination url'.
     */
    public static final String WREPLY = "wreply";
    /**
     * WS-Federation HTTP parameter for 'current time'.
     */
    public static final String WCT = "wct";
    /**
     * WS-Federation HTTP parameter for 'context value'.
     */
    public static final String WCTX = "wctx";
}

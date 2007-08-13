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
 * $Id: IDFFEntityProviderModel.java,v 1.2 2007-08-13 19:09:48 asyhuang Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.liberty.ws.meta.jaxb.IDPDescriptorType;
import com.sun.identity.liberty.ws.meta.jaxb.SPDescriptorType;
import java.util.Set;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

public interface IDFFEntityProviderModel
    extends AMModel {
    
    public static final String ATTR_PROVIDER_ALIAS = "tfAlias";
    public static final String ATTR_PROVIDER_TYPE = "tfProviderType";
    
    public static final String ATTR_SERVER_NAME_IDENTIFIER_MAPPING =
        "elistServerNameIdentifierMapping";
    
    // standard meta data
    public static final String ATTR_XMLNS = "xmlns";
    public static final String ATTR_PROVIDER_ID = "providerID";
    public static final String ATTR_PROTOCOL_SUPPORT_ENUMERATION =
        "txtProtocolSupportEnum";
    public static final String ATTR_SOAP_END_POINT =
        "tfSOAPEndpointURL";
    public static final String ATTR_SINGLE_SIGN_ON_SERVICE_URL =
        "tfSingleSignOnServiceURL";
    public static final String ATTR_SINGLE_LOGOUT_SERVICE_URL =
        "tfSingleLogoutServiceURL";
    public static final String ATTR_SINGLE_LOGOUT_SERVICE_RETURN_URL =
        "tfSingleLogoutReturnURL";
    public static final String ATTR_FEDERATION_TERMINATION_SERVICES_URL =
        "tfFederationTerminationServiceURL";
    public static final String ATTR_FEDERATION_TERMINATION_SERVICE_RETURN_URL =
        "tfFederationTerminationReturnURL";
    public static final String ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_URL =
        "tfNameRegistrationServiceURL";
    public static final
        String ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_RETURN_URL =
        "tfNameRegistrationReturnURL";
    
    // communication profiles
    public static final String
        ATTR_FEDERATION_TERMINATION_NOTIFICATION_PROTOCOL_PROFILE =
        "singleChoiceFederationTerminationProfile";
    public static final String ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE =
        "singleChoiceSingleLogoutProfile";
    public static final
        String ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE =
        "singleChoiceNameRegistrationProfile";
    public static final String ATTR_SINGLE_SIGN_ON_PROTOCOL_PROFILE =
        "singleChoiceFederationProfile";
    
    //KeyDescriptor property.
    public static final String ATTR_SIGNING_KEY_ALIAS =
        "tfSigningKeyAlias";
    public static final String ATTR_ENCRYPTION_KEY_ALIAS =
        "tfEncryptionKeyAlias";
    public static final String ATTR_EMCRYPTION_KEY_SIZE =
        "tfEncryptionKeySize";
    public static final String ATTR_ENCRYPTION_METHOD =
        "singleChoiceEncryptionMethod";
    public static final String ATTR_ENABLE_NAME_IDENTIFIER_ENCRYPTION =
        "cbEnableNameIdentifierEncryption";
    
    // SP standard meta Assertion Consumer Service URL property.
    public static final String ATTR_ASSERTION_CUSTOMER_SERVICE_URIID =
        "tfAssertionConsumerServiceURLID";
    public static final String ATTR_ASSERTION_CUSTOMER_SERVICE_URL =
        "tfAssertionConsumerServiceURL";
    public static final String ATTR_ASSERTION_CUSTOMER_SERVICE_URL_AS_DEFAULT =
        "cbAssertionConsumerServiceURLasDefault";
    public static final String ATTR_AUTHN_REQUESTS_SIGNED =
        "cbAuthnRequestsSigned";
    
    // BOTH idp AND SP extended metadata
    public static final String ATTR_DO_FEDERATION_PAGE_URL =
        "doFederatePageURL";
    public static final String ATTR_ATTRIBUTE_MAPPER_CLASS =
        "attributeMapperClass";
    public static final String ATTR_ENABLE_AUTO_FEDERATION =
        "enableAutoFederation";
    public static final String ATTR_REGISTERATION_DONE_URL =
        "registrationDoneURL";
    public static final String ATTR_COT_LIST =
        "cotlist";
    public static final String ATTR_RESPONSD_WITH =
        "responsdWith";
    public static final String ATTR_ENABLE_NAME_ID_ENCRYPTION =
        "enableNameIDEncryption";
    public static final String ATTR_SSO_FAILURE_REDIRECT_URL =
        "ssoFailureRedirectURL";
    public static final String ATTR_LIST_OF_COTS_PAGE_URL =
        "listOfCOTsPageURL";
    public static final String ATTR_DEFAULT_AUTHN_CONTEXT =
        "defaultAuthnContext";
    public static final String ATTR_SIGNING_CERT_ALIAS =
        "signingCertAlias";
    public static final String ATTR_REALM_NAME =
        "realmName";
    public static final String ATTR_USER_PROVIDER_CLASS =
        "userProviderClass";
    public static final String ATTR_NAME_ID_IMPLEMENETATION_CLASS =
        "nameIDImplementationClass";
    public static final String ATTR_FEDERATION_DONE_URL =
        "federationDoneURL";
    public static final String ATTR_AUTH_TYPE =
        "authType";
    public static final String ATTR_ENCRYPTION_CERT_ALIAS =
        "encryptionCertAlias";
    public static final String ATTR_TERMINATION_DONE_URL =
        "terminationDoneURL";
    public static final String ATTR_AUTO_FEDERATION_ATTRIBUTE =
        "autoFederationAttribute";
    public static final String ATTR_ERROR_PAGE_URL =
        "errorPageURL";
    public static final String ATTR_PROVIDER_STATUS =
        "providerStatus";
    public static final String ATTR_PROVIDER_DESCRIPTION =
        "providerDescription";
    public static final String ATTR_LOGOUT_DONE_URL =
        "logoutDoneURL";
    public static final String ATTR_PROVIDER_HOME_PAGE_URL =
        "providerHomePageURL";
    
    // IDP extend meta attribute ONLY IDP
    
    // idp
    public static final String ATTR_ASSERTION_LIMIT =
        "assertionLimit";
    public static final String ATTR_ATTRIBUTE_PLUG_IN =
        "attributePlugin";
    public static final String ATTR_IDP_ATTRIBUTE_MAP =
        "idpAttributeMap";
    public static final String ATTR_ASSERTION_ISSUER =
        "assertionIssuer";
    public static final String ATTR_CLEANUP_INTERVAL =
        "cleanupInterval";
    public static final String ATTR_IDP_AUTHN_CONTEXT_MAPPING =
        "idpAuthnContextMapping";
    public static final String ATTR_GERNERATE_BOOT_STRAPPING =
        "generateBootstrapping";
    public static final String ATTR_ARTIFACT_TIMEOUT =
        "artifactTimeout";
    public static final String ATTR_ASSERTION_INTERVAL =
        "assertionInterval";
    
    
    // SP extend meta attribute.. ONLY SP
    
    public static final String ATTR_IS_PASSIVE =
        "isPassive";
    public static final String ATTR_SP_ATTRIBUTE_MAP =
        "spAttributeMap";
    public static final String ATTR_SP_AUTHN_CONTEXT_MAPPING =
        "spAuthnContextMapping";
    public static final String ATTR_IDP_PROXY_LIST =
        "idpProxyList";
    public static final String ATTR_ENABLE_IDP_PROXY =
        "enableIDPProxy";
    public static final String ATTR_NAME_ID_POLICY =
        "nameIDPolicy";
    public static final String ATTR_FEDERATION_SP_ADAPTER_ENV =
        "federationSPAdapterEnv";
    public static final String ATTR_ENABLE_AFFILIATION =
        "enableAffiliation";
    public static final String ATTR_FORCE_AUTHN =
        "forceAuthn";
    public static final String ATTR_IDP_PROXY_COUNT =
        "idpProxyCount";
    public static final String ATTR_FEDERATION_SP_ADAPTER =
        "federationSPAdapter";
    public static final String ATTR_USE_INTRODUCTION_FOR_IDP_PROXY =
        "useIntroductionForIDPProxy";
    public static final String ATTR_SUPPORTED_SSO_PROFILE =
        "supportedSSOProfile";
    
    
    /**
     * Returns the type of a provider such as hosted or remote.
     *
     * @param name of Entity Descriptor.
     * @param role Provider's role.
     * @return the type of a provider such as hosted or remote.
     */
    public String getProviderType(String name, String role);
    
    public IDPDescriptorType getIdentityProvider(String name);
    public SPDescriptorType getServiceProvider(String name);
    
    
    public Map getEntityIDPDescriptor(String entityName);
    public Map getEntitySPDescriptor(String entityName);
    
    /**
     * Returns attributes values of provider.
     *
     * @param entityName Name of Entity Descriptor.
     * @param role Role of provider.
     * @param location Location of provider such as Hosted or Remote.
     * @return attributes values of provider.
     */
    public Map getEntityConfig(
        String entityName,
        String role,
        String location);
    
    /**
     * updateEntityDescriptor
     * Modifies a provider's standard metadata.
     *
     * @param entityName Name of Entity Descriptor.
     * @param role Role of provider. (SP or IDP)
     * @param attrValues Map of attribute name to set of values.
     * @throws AMConsoleException if provider cannot be modified.
     */
    public void updateEntityDescriptor(
        String entityName,
        String role,
        Map attrValues
        ) throws AMConsoleException ;
    
    /**
     * updateEntityConfig
     * Modifies a provider's extended metadata.
     *
     * @param entityName Name of Entity Descriptor.
     * @param role Role of provider. (SP or IDP)
     * @param attrValues Map of attribute name to set of values.
     * @throws AMConsoleException if provider cannot be modified.
     */
    public void updateEntityConfig(
        String entityID,
        String role,
        Map attrValues)
        throws AMConsoleException, JAXBException;
    
    public void createEntityConfig(
        String entityName,
        String role,
        String location
        ) throws AMConsoleException, JAXBException;
}

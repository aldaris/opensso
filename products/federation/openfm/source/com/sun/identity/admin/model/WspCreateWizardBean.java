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
 * $Id: WspCreateWizardBean.java,v 1.4 2009-10-06 18:28:03 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.wss.security.PasswordCredential;

public class WspCreateWizardBean
        extends WssWizardBean
        implements Serializable
{
    private String profileName;
    private Effect profileNameInputEffect;
    private Effect profileNameMessageEffect;
    private String endPoint;
    private Effect endPointInputEffect;
    private Effect endPointMessageEffect;
    private boolean usingMexEndPoint;
    private String mexEndPoint;
    private Effect mexEndPointInputEffect;
    private Effect mexEndPointMessageEffect;
    private WspCreateProfileNameSummary profileNameSummary;
    
    private ArrayList<SecurityMechanismPanelBean> securityMechanismPanels;
    private String authenticationChain;
    private String tokenConversionType;
    private ArrayList<UserCredentialItem> userCredentialItems;
    private String newUserName;
    private String newPassword;
    private boolean showingAddCredential;
    private String kerberosDomain;
    private Effect kerberosDomainInputEffect;
    private Effect kerberosDomainMessageEffect;
    private String kerberosDomainServer;
    private Effect kerberosDomainServerInputEffect;
    private Effect kerberosDomainServerMessageEffect;
    private String kerberosServicePrincipal;
    private Effect kerberosServicePrincipalInputEffect;
    private Effect kerberosServicePrincipalMessageEffect;
    private String kerberosKeyTabFile;
    private Effect kerberosKeyTabFileInputEffect;
    private Effect kerberosKeyTabFileMessageEffect;
    private String x509SigningReferenceType;
    private WspCreateServiceSecuritySummary serviceSecuritySummary;
    
    private boolean requestSignatureVerified;
    private boolean requestHeaderDecrypted;
    private boolean requestDecrypted;
    private boolean responseSigned;
    private boolean responseEncrypted;
    private String encryptionAlgorithm;
    private String privateKeyAlias;
    private String publicKeyAlias;
    private WspCreateSignEncryptSummary signEncryptSummary;

    private String nameIdMapper;
    private Effect nameIdMapperInputEffect;
    private Effect nameIdMapperMessageEffect;
    private String attributeNamespace;
    private Effect attributeNamespaceInputEffect;
    private Effect attributeNamespaceMessageEffect;
    private boolean includeMemberships;
    private ArrayList<SamlAttributeMapItem> attributeMapping;
    private boolean showingAddAttribute;
    private String newLocalAttributeName;
    private String newAssertionAttributeName;
    private WspCreateSamlSummary samlSummary;
    
    private RealmSummary realmSummary;
    
    public WspCreateWizardBean() {
        super();
        initialize();
    }

    @Override
    public void reset() {
        super.reset();
        initialize();
    }

    private void initialize() {
        
        this.setProfileName(null);
        this.setEndPoint(null);
        this.setUsingMexEndPoint(false);
        this.setMexEndPoint(null);
        
        this.setAuthenticationChain(null);
        this.setTokenConversionType(null);
        initSecurityMechPanels();
        this.setKerberosDomain(null);
        this.setKerberosDomainServer(null);
        this.setKerberosKeyTabFile(null);
        this.setKerberosServicePrincipal(null);
        initX509SigningRefType();
        initUserNameCredentials();
        
        this.setRequestSignatureVerified(false);
        this.setRequestHeaderDecrypted(false);
        this.setRequestDecrypted(false);
        this.setResponseSigned(false);
        this.setResponseEncrypted(false);
        
        this.setNameIdMapper(null);
        this.setAttributeNamespace(null);
        this.setIncludeMemberships(false);
        initAttributeMapping();
        
        this.setProfileNameSummary(new WspCreateProfileNameSummary(this));
        this.setServiceSecuritySummary(new WspCreateServiceSecuritySummary(this));
        this.setSignEncryptSummary(new WspCreateSignEncryptSummary(this));
        this.setSamlSummary(new WspCreateSamlSummary(this));
        this.setRealmSummary(new RealmSummary());
    }
    
    private void initSecurityMechPanels() {
        // to-do:  change this
        ArrayList<String> configValues = new ArrayList<String>();
        ArrayList<SecurityMechanismPanelBean> 
            panelBeans = new ArrayList<SecurityMechanismPanelBean>();
        SecurityMechanismPanelBean panelBean;

        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.ANONYMOUS);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.SAML_HOK);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.SAML_SV);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.SAML2_HOK);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.SAML2_SV);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.USERNAME_TOKEN);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBean.setSettingsTemplate("wss-usernames.xhtml");
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.USERNAME_TOKEN_PLAIN);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBean.setSettingsTemplate("wss-usernames.xhtml");
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.KERBEROS_TOKEN);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBean.setSettingsTemplate("wss-kerberos-provider.xhtml");
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.X509_TOKEN);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBean.setSettingsTemplate("wss-x509.xhtml");
        panelBeans.add(panelBean);
        
        this.setSecurityMechanismPanels(panelBeans);
    }
    
    private void initX509SigningRefType() {
        // to-do:  change this
        String configValue = null;
        
        if( configValue != null ) {
            X509SigningRefType x = X509SigningRefType.valueOfConfig(configValue);
            
            if( x != null ) {
                this.setX509SigningReferenceType(x.toString());
            }
        }
    }
    
    private void initUserNameCredentials() {
        // to-do: change this
        ArrayList<PasswordCredential> configValues 
            = new ArrayList<PasswordCredential>();
        ArrayList<UserCredentialItem> newList
            = new ArrayList<UserCredentialItem>();
        
        for(PasswordCredential p : configValues) {
            UserCredentialItem i = new UserCredentialItem();
            i.setUserName(p.getUserName());
            i.setPassword(p.getPassword());
            newList.add(i);
        }
        
        this.setUserCredentialItems(newList);
        this.setNewUserName(null);
        this.setNewPassword(null);
        this.setShowingAddCredential(false);
    }
    
    private void initAttributeMapping() {
        // to-do: change this
        ArrayList<String> mapPairs = new ArrayList<String>();
        Hashtable<String, String> stsConfigValues
            = new Hashtable<String, String>();
        
        if( mapPairs != null ) {
            for(String s : mapPairs) {
                if( s != null && s.contains("=") ) {
                    String assertionAttrName = s.substring(0, s.indexOf("="));
                    String localAttrName = s.substring(s.indexOf("=") + 1);
                    
                    if( assertionAttrName.length() > 0
                            && localAttrName.length() > 0 ) {
                        stsConfigValues.put(localAttrName, assertionAttrName);
                    }
                }
            }
        }
        
        ArrayList<String> defaultValues = new ArrayList<String>();
        defaultValues.add("cn");
        defaultValues.add("employeenumber");
        defaultValues.add("givenname");
        defaultValues.add("mail");
        defaultValues.add("manager");
        defaultValues.add("postaladdress");
        defaultValues.add("sn");
        defaultValues.add("telephonenumber");
        defaultValues.add("uid");
        
        ArrayList<SamlAttributeMapItem> attributeMap
            = new ArrayList<SamlAttributeMapItem>();
        
        for(String s : defaultValues) {
            SamlAttributeMapItem item = new SamlAttributeMapItem();
            item.setCustom(false);
            item.setLocalAttributeName(s);
            
            if( stsConfigValues.containsKey(s) ) {
                item.setAssertionAttributeName(stsConfigValues.get(s));
                stsConfigValues.remove(s);
            } else {
                item.setAssertionAttributeName(null);
            }
            attributeMap.add(item);
        }

        for(String s : stsConfigValues.keySet()) {
            SamlAttributeMapItem item = new SamlAttributeMapItem();
            item.setCustom(true);
            item.setLocalAttributeName(s);
            item.setAssertionAttributeName(stsConfigValues.get(s));
            attributeMap.add(item);
        }
        
        this.setAttributeMapping(attributeMap);
        this.setShowingAddAttribute(false);
        this.setNewLocalAttributeName(null);
        this.setNewAssertionAttributeName(null);
    }
    
    
    // Getters / Setters -------------------------------------------------------

    public void setRealmSummary(RealmSummary realmSummary) {
        this.realmSummary = realmSummary;
    }

    public RealmSummary getRealmSummary() {
        return realmSummary;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Effect getProfileNameInputEffect() {
        return profileNameInputEffect;
    }

    public void setProfileNameInputEffect(Effect profileNameInputEffect) {
        this.profileNameInputEffect = profileNameInputEffect;
    }

    public Effect getProfileNameMessageEffect() {
        return profileNameMessageEffect;
    }

    public void setProfileNameMessageEffect(Effect profileNameMessageEffect) {
        this.profileNameMessageEffect = profileNameMessageEffect;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Effect getEndPointInputEffect() {
        return endPointInputEffect;
    }

    public void setEndPointInputEffect(Effect endPointInputEffect) {
        this.endPointInputEffect = endPointInputEffect;
    }

    public Effect getEndPointMessageEffect() {
        return endPointMessageEffect;
    }

    public void setEndPointMessageEffect(Effect endPointMessageEffect) {
        this.endPointMessageEffect = endPointMessageEffect;
    }

    public boolean isUsingMexEndPoint() {
        return usingMexEndPoint;
    }

    public void setUsingMexEndPoint(boolean usingMexEndPoint) {
        this.usingMexEndPoint = usingMexEndPoint;
    }

    public String getMexEndPoint() {
        return mexEndPoint;
    }

    public void setMexEndPoint(String mexEndPoint) {
        this.mexEndPoint = mexEndPoint;
    }

    public Effect getMexEndPointInputEffect() {
        return mexEndPointInputEffect;
    }

    public void setMexEndPointInputEffect(Effect mexEndPointInputEffect) {
        this.mexEndPointInputEffect = mexEndPointInputEffect;
    }

    public Effect getMexEndPointMessageEffect() {
        return mexEndPointMessageEffect;
    }

    public void setMexEndPointMessageEffect(Effect mexEndPointMessageEffect) {
        this.mexEndPointMessageEffect = mexEndPointMessageEffect;
    }

    public WspCreateProfileNameSummary getProfileNameSummary() {
        return profileNameSummary;
    }

    public void setProfileNameSummary(WspCreateProfileNameSummary profileNameSummary) {
        this.profileNameSummary = profileNameSummary;
    }

    public void setSecurityMechanismPanels(ArrayList<SecurityMechanismPanelBean> securityMechanismPanels) {
        this.securityMechanismPanels = securityMechanismPanels;
    }

    public ArrayList<SecurityMechanismPanelBean> getSecurityMechanismPanels() {
        return securityMechanismPanels;
    }

    public void setAuthenticationChain(String authenticationChain) {
        this.authenticationChain = authenticationChain;
    }

    public String getAuthenticationChain() {
        return authenticationChain;
    }

    public void setServiceSecuritySummary(WspCreateServiceSecuritySummary serviceSecuritySummary) {
        this.serviceSecuritySummary = serviceSecuritySummary;
    }

    public WspCreateServiceSecuritySummary getServiceSecuritySummary() {
        return serviceSecuritySummary;
    }

    public ArrayList<UserCredentialItem> getUserCredentialItems() {
        return userCredentialItems;
    }

    public void setUserCredentialItems(
            ArrayList<UserCredentialItem> userCredentialItems) {
        this.userCredentialItems = userCredentialItems;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean isShowingAddCredential() {
        return showingAddCredential;
    }

    public void setShowingAddCredential(boolean showingAddCredential) {
        this.showingAddCredential = showingAddCredential;
    }

    public String getKerberosDomain() {
        return kerberosDomain;
    }

    public void setKerberosDomain(String kerberosDomain) {
        this.kerberosDomain = kerberosDomain;
    }

    public Effect getKerberosDomainInputEffect() {
        return kerberosDomainInputEffect;
    }

    public void setKerberosDomainInputEffect(Effect kerberosDomainInputEffect) {
        this.kerberosDomainInputEffect = kerberosDomainInputEffect;
    }

    public Effect getKerberosDomainMessageEffect() {
        return kerberosDomainMessageEffect;
    }

    public void setKerberosDomainMessageEffect(Effect kerberosDomainMessageEffect) {
        this.kerberosDomainMessageEffect = kerberosDomainMessageEffect;
    }

    public String getKerberosDomainServer() {
        return kerberosDomainServer;
    }

    public void setKerberosDomainServer(String kerberosDomainServer) {
        this.kerberosDomainServer = kerberosDomainServer;
    }

    public Effect getKerberosDomainServerInputEffect() {
        return kerberosDomainServerInputEffect;
    }

    public void setKerberosDomainServerInputEffect(
            Effect kerberosDomainServerInputEffect) {
        this.kerberosDomainServerInputEffect = kerberosDomainServerInputEffect;
    }

    public Effect getKerberosDomainServerMessageEffect() {
        return kerberosDomainServerMessageEffect;
    }

    public void setKerberosDomainServerMessageEffect(
            Effect kerberosDomainServerMessageEffect) {
        this.kerberosDomainServerMessageEffect = kerberosDomainServerMessageEffect;
    }

    public String getKerberosServicePrincipal() {
        return kerberosServicePrincipal;
    }

    public void setKerberosServicePrincipal(String kerberosServicePrincipal) {
        this.kerberosServicePrincipal = kerberosServicePrincipal;
    }

    public Effect getKerberosServicePrincipalInputEffect() {
        return kerberosServicePrincipalInputEffect;
    }

    public void setKerberosServicePrincipalInputEffect(
            Effect kerberosServicePrincipalInputEffect) {
        this.kerberosServicePrincipalInputEffect = kerberosServicePrincipalInputEffect;
    }

    public Effect getKerberosServicePrincipalMessageEffect() {
        return kerberosServicePrincipalMessageEffect;
    }

    public void setKerberosServicePrincipalMessageEffect(
            Effect kerberosServicePrincipalMessageEffect) {
        this.kerberosServicePrincipalMessageEffect = kerberosServicePrincipalMessageEffect;
    }

    public String getKerberosKeyTabFile() {
        return kerberosKeyTabFile;
    }

    public void setKerberosKeyTabFile(String kerberosKeyTabFile) {
        this.kerberosKeyTabFile = kerberosKeyTabFile;
    }

    public Effect getKerberosKeyTabFileInputEffect() {
        return kerberosKeyTabFileInputEffect;
    }

    public void setKerberosKeyTabFileInputEffect(
            Effect kerberosKeyTabFileInputEffect) {
        this.kerberosKeyTabFileInputEffect = kerberosKeyTabFileInputEffect;
    }

    public Effect getKerberosKeyTabFileMessageEffect() {
        return kerberosKeyTabFileMessageEffect;
    }

    public void setKerberosKeyTabFileMessageEffect(
            Effect kerberosKeyTabFileMessageEffect) {
        this.kerberosKeyTabFileMessageEffect = kerberosKeyTabFileMessageEffect;
    }

    public String getX509SigningReferenceType() {
        return x509SigningReferenceType;
    }

    public void setX509SigningReferenceType(String signingReferenceType) {
        x509SigningReferenceType = signingReferenceType;
    }

    public void setTokenConversionType(String tokenConversionType) {
        this.tokenConversionType = tokenConversionType;
    }

    public String getTokenConversionType() {
        return tokenConversionType;
    }

    public boolean isRequestSignatureVerified() {
        return requestSignatureVerified;
    }

    public void setRequestSignatureVerified(boolean requestSignatureVerified) {
        this.requestSignatureVerified = requestSignatureVerified;
    }

    public boolean isRequestHeaderDecrypted() {
        return requestHeaderDecrypted;
    }

    public void setRequestHeaderDecrypted(boolean requestHeaderDecrypted) {
        this.requestHeaderDecrypted = requestHeaderDecrypted;
    }

    public boolean isRequestDecrypted() {
        return requestDecrypted;
    }

    public void setRequestDecrypted(boolean requestDecrypted) {
        this.requestDecrypted = requestDecrypted;
    }

    public boolean isResponseSigned() {
        return responseSigned;
    }

    public void setResponseSigned(boolean responseSigned) {
        this.responseSigned = responseSigned;
    }

    public boolean isResponseEncrypted() {
        return responseEncrypted;
    }

    public void setResponseEncrypted(boolean responseEncrypted) {
        this.responseEncrypted = responseEncrypted;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }

    public void setPrivateKeyAlias(String privateKeyAlias) {
        this.privateKeyAlias = privateKeyAlias;
    }

    public String getPublicKeyAlias() {
        return publicKeyAlias;
    }

    public void setPublicKeyAlias(String publicKeyAlias) {
        this.publicKeyAlias = publicKeyAlias;
    }

    public WspCreateSignEncryptSummary getSignEncryptSummary() {
        return signEncryptSummary;
    }

    public void setSignEncryptSummary(WspCreateSignEncryptSummary signEncryptSummary) {
        this.signEncryptSummary = signEncryptSummary;
    }

    public String getNameIdMapper() {
        return nameIdMapper;
    }

    public void setNameIdMapper(String nameIdMapper) {
        this.nameIdMapper = nameIdMapper;
    }

    public Effect getNameIdMapperInputEffect() {
        return nameIdMapperInputEffect;
    }

    public void setNameIdMapperInputEffect(Effect nameIdMapperInputEffect) {
        this.nameIdMapperInputEffect = nameIdMapperInputEffect;
    }

    public Effect getNameIdMapperMessageEffect() {
        return nameIdMapperMessageEffect;
    }

    public void setNameIdMapperMessageEffect(Effect nameIdMapperMessageEffect) {
        this.nameIdMapperMessageEffect = nameIdMapperMessageEffect;
    }

    public String getAttributeNamespace() {
        return attributeNamespace;
    }

    public void setAttributeNamespace(String attributeNamespace) {
        this.attributeNamespace = attributeNamespace;
    }

    public Effect getAttributeNamespaceInputEffect() {
        return attributeNamespaceInputEffect;
    }

    public void setAttributeNamespaceInputEffect(
            Effect attributeNamespaceInputEffect) {
        this.attributeNamespaceInputEffect = attributeNamespaceInputEffect;
    }

    public Effect getAttributeNamespaceMessageEffect() {
        return attributeNamespaceMessageEffect;
    }

    public void setAttributeNamespaceMessageEffect(
            Effect attributeNamespaceMessageEffect) {
        this.attributeNamespaceMessageEffect = attributeNamespaceMessageEffect;
    }

    public boolean isIncludeMemberships() {
        return includeMemberships;
    }

    public void setIncludeMemberships(boolean includeMemberships) {
        this.includeMemberships = includeMemberships;
    }

    public ArrayList<SamlAttributeMapItem> getAttributeMapping() {
        return attributeMapping;
    }

    public void setAttributeMapping(ArrayList<SamlAttributeMapItem> attributeMapping) {
        this.attributeMapping = attributeMapping;
    }

    public boolean isShowingAddAttribute() {
        return showingAddAttribute;
    }

    public void setShowingAddAttribute(boolean showingAddAttribute) {
        this.showingAddAttribute = showingAddAttribute;
    }

    public String getNewLocalAttributeName() {
        return newLocalAttributeName;
    }

    public void setNewLocalAttributeName(String newLocalAttributeName) {
        this.newLocalAttributeName = newLocalAttributeName;
    }

    public String getNewAssertionAttributeName() {
        return newAssertionAttributeName;
    }

    public void setNewAssertionAttributeName(String newAssertionAttributeName) {
        this.newAssertionAttributeName = newAssertionAttributeName;
    }

    public WspCreateSamlSummary getSamlSummary() {
        return samlSummary;
    }

    public void setSamlSummary(WspCreateSamlSummary samlSummary) {
        this.samlSummary = samlSummary;
    }
}

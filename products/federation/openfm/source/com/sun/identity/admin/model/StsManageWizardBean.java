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
 * $Id: StsManageWizardBean.java,v 1.5 2009-10-16 19:38:47 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.dao.StsConfigurationDao;
import com.sun.identity.wss.security.PasswordCredential;

public class StsManageWizardBean
        extends WssWizardBean
        implements Serializable
{
    private String issuer;
    private Effect issuerMessageEffect;
    private Effect issuerInputEffect;
    private int tokenLifetime;
    private Effect tokenLifetimeMessageEffect;
    private Effect tokenLifetimeInputEffect;
    private String keyAlias;
    private String tokenPluginClass;
    private Effect tokenPluginClassMessageEffect;
    private Effect tokenPluginClassInputEffect;
    private StsManageTokenIssuanceSummary tokenIssuanceSummary;

    private String authenticationChain;
    private ArrayList<SecurityMechanismPanelBean> securityMechanismPanels;
    private UserCredentialsTableBean userCredentialsTable;
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
    private StsManageServiceSecuritySummary serviceSecuritySummary;
    
    private boolean requestSignatureVerified;
    private boolean requestHeaderDecrypted;
    private boolean requestDecrypted;
    private boolean responseSigned;
    private boolean responseEncrypted;
    private String encryptionAlgorithm;
    private String privateKeyAlias;
    private String publicKeyAlias;
    private StsManageSignEncryptSummary signEncryptSummary;
    
    private String nameIdMapper;
    private Effect nameIdMapperInputEffect;
    private Effect nameIdMapperMessageEffect;
    private String attributeNamespace;
    private Effect attributeNamespaceInputEffect;
    private Effect attributeNamespaceMessageEffect;
    private boolean includeMemberships;
    private SamlAttributesTableBean samlAttributesTable;
    private StsManageSamlSummary samlSummary;

    private EditableSelectOneBean trustedIssuers;
    private EditableSelectOneBean trustedAddresses;
    private StsManageTokenValidationSummary tokenValidationSummary;

    public StsManageWizardBean() {
        super();
        initialize();
    }

    @Override
    public void reset() {
        super.reset();
        initialize();
        setAllEnabled(true);
        this.gotoStep(StsManageWizardStep.SUMMARY.toInt());
    }

    
    private void initialize() {
        StsConfigurationBean stsConfig = StsConfigurationDao.retrieveConfig();

        this.setIssuer(stsConfig.getIssuer());
        this.setTokenLifetime(stsConfig.getTokenLifetime() / 1000);
        this.setKeyAlias(stsConfig.getKeyAlias());
        this.setTokenPluginClass(stsConfig.getTokenPluginClassName());
        
        this.setAuthenticationChain(stsConfig.getAuthenticationChain());
        initSecurityMechPanels(stsConfig);
        this.setKerberosDomain(stsConfig.getKerberosDomain());
        this.setKerberosDomainServer(stsConfig.getKerberosDomainServer());
        this.setKerberosKeyTabFile(stsConfig.getKerberosKeyTabFile());
        this.setKerberosServicePrincipal(stsConfig.getKerberosServicePrincipal());
        initX509SigningRefType(stsConfig);
        initUserNameCredentialsTable(stsConfig);
        
        this.setRequestSignatureVerified(stsConfig.isRequestSigned());
        this.setRequestDecrypted(stsConfig.isRequestEncrypted());
        this.setRequestHeaderDecrypted(stsConfig.isRequestHeaderEncrypted());
        this.setResponseEncrypted(stsConfig.isResponseEncrypted());
        this.setResponseSigned(stsConfig.isResponseSigned());
        initEncryptionAlgorithm(stsConfig);
        this.setPrivateKeyAlias(stsConfig.getPrivateKeyAlias());
        this.setPublicKeyAlias(stsConfig.getPublicKeyAlias());
        
        this.setNameIdMapper(stsConfig.getNameIdMapper());
        this.setIncludeMemberships(stsConfig.isIncludeMemberships());
        this.setAttributeNamespace(stsConfig.getAttributeNamespace());
        initSamlAttributesTable(stsConfig);

        initTrustedIssuers(stsConfig);
        initTrustedIpAddresses(stsConfig);
        
        // summaries
        this.setTokenIssuanceSummary(new StsManageTokenIssuanceSummary(this));
        this.setServiceSecuritySummary(new StsManageServiceSecuritySummary(this));
        this.setSignEncryptSummary(new StsManageSignEncryptSummary(this));
        this.setSamlSummary(new StsManageSamlSummary(this));
        this.setTokenValidationSummary(new StsManageTokenValidationSummary(this));
    }

    private void initSecurityMechPanels(StsConfigurationBean stsConfig) {
        ArrayList<String> configValues = stsConfig.getSecurityMechanisms();
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
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.USERNAME_TOKEN_PLAIN);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.KERBEROS_TOKEN);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBeans.add(panelBean);
        
        panelBean = new SecurityMechanismPanelBean();
        panelBean.setSecurityMechanism(SecurityMechanism.X509_TOKEN);
        panelBean.setChecked(configValues.contains(panelBean.getConfigValue()));
        panelBeans.add(panelBean);
        
        this.setSecurityMechanismPanels(panelBeans);
    }

    private void initX509SigningRefType(StsConfigurationBean stsConfig) {
        String configValue = stsConfig.getX509SigningReferenceType();
        
        if( configValue != null ) {
            X509SigningRefType x = X509SigningRefType.valueOfConfig(configValue);
            
            if( x != null ) {
                this.setX509SigningReferenceType(x.toString());
            }
        }
    }
    
    private void initUserNameCredentialsTable(StsConfigurationBean stsConfig) {
        ArrayList<PasswordCredential> configValues
            = stsConfig.getUserNameTokenCredentials();
        ArrayList<UserCredentialItem> newList
            = new ArrayList<UserCredentialItem>();
        
        for(PasswordCredential p : configValues) {
            UserCredentialItem i = new UserCredentialItem();
            i.setUserName(p.getUserName());
            i.setPassword(p.getPassword());
            newList.add(i);
        }
        
        UserCredentialsTableBean uctb = new UserCredentialsTableBean();
        uctb.setUserCredentialItems(newList);
        this.setUserCredentialsTable(uctb);
    }
    
    private void initEncryptionAlgorithm(StsConfigurationBean stsConfig) {
        String configAlg = stsConfig.getEncryptionAlgorithm();
        int configStrength = stsConfig.getEncryptionStrength();
        EncryptionAlgorithm encryptionAlgorithm = null;

        // default is AES - 128
        if(configAlg.equals(StsConfigurationDao.ENCRYPTION_ALGORITHM_DESEDE)) {
            
            switch(configStrength) {
                case 168:
                    encryptionAlgorithm = EncryptionAlgorithm.TRIPLEDES_168;
                    break;
                case 112:
                    encryptionAlgorithm = EncryptionAlgorithm.TRIPLEDES_112;
                    break;
                default:
                    encryptionAlgorithm = EncryptionAlgorithm.TRIPLEDES_0;
                    break;
            }
            
        } else {

            switch(configStrength) {
                case 256:
                    encryptionAlgorithm = EncryptionAlgorithm.AES_256;
                    break;
                case 192:
                    encryptionAlgorithm = EncryptionAlgorithm.AES_192;
                    break;
                default:
                    encryptionAlgorithm = EncryptionAlgorithm.AES_128;
                    break;
            }

        }
        
        this.setEncryptionAlgorithm(encryptionAlgorithm.toString());
    }
    
    private void initSamlAttributesTable(StsConfigurationBean stsConfig) {
        ArrayList<String> mapPairs = stsConfig.getSamlAttributeMapping();
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
        
        ArrayList<SamlAttributeMapItem> attributeMapItems
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
            attributeMapItems.add(item);
        }

        for(String s : stsConfigValues.keySet()) {
            SamlAttributeMapItem item = new SamlAttributeMapItem();
            item.setCustom(true);
            item.setLocalAttributeName(s);
            item.setAssertionAttributeName(stsConfigValues.get(s));
            attributeMapItems.add(item);
        }
        
        SamlAttributesTableBean samlAttributesTable 
            = new SamlAttributesTableBean();
        samlAttributesTable.setAttributeMapItems(attributeMapItems);
        
        this.setSamlAttributesTable(samlAttributesTable);
    }
    
    private void initTrustedIssuers(StsConfigurationBean stsConfig) {
        EditableSelectOneBean esmb = new EditableSelectOneBean();
        esmb.setItems(stsConfig.getTrustedIssuers());
        this.setTrustedIssuers(esmb);
    }
    
    private void initTrustedIpAddresses(StsConfigurationBean stsConfig) {
        EditableSelectOneBean esmb = new EditableSelectOneBean();
        Resources r = new Resources();

        esmb = new EditableSelectOneBean();
        esmb.setItems(stsConfig.getTrustedIpAddresses());
        esmb.setValidPattern("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
        esmb.setInvalidMessageDetail(r.getString(this, "trustedAddresses.invalidMessageDetail"));
        this.setTrustedAddresses(esmb);
    }
    
    // Getters / Setters -------------------------------------------------------

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Effect getIssuerMessageEffect() {
        return issuerMessageEffect;
    }

    public void setIssuerMessageEffect(Effect issuerMessageEffect) {
        this.issuerMessageEffect = issuerMessageEffect;
    }

    public Effect getIssuerInputEffect() {
        return issuerInputEffect;
    }

    public void setIssuerInputEffect(Effect issuerInputEffect) {
        this.issuerInputEffect = issuerInputEffect;
    }

    public int getTokenLifetime() {
        return tokenLifetime;
    }

    public void setTokenLifetime(int tokenLifetime) {
        this.tokenLifetime = tokenLifetime;
    }

    public Effect getTokenLifetimeMessageEffect() {
        return tokenLifetimeMessageEffect;
    }

    public void setTokenLifetimeMessageEffect(Effect tokenLifetimeMessageEffect) {
        this.tokenLifetimeMessageEffect = tokenLifetimeMessageEffect;
    }

    public Effect getTokenLifetimeInputEffect() {
        return tokenLifetimeInputEffect;
    }

    public void setTokenLifetimeInputEffect(Effect tokenLifetimeInputEffect) {
        this.tokenLifetimeInputEffect = tokenLifetimeInputEffect;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getTokenPluginClass() {
        return tokenPluginClass;
    }

    public void setTokenPluginClass(String tokenPluginClass) {
        this.tokenPluginClass = tokenPluginClass;
    }

    public Effect getTokenPluginClassMessageEffect() {
        return tokenPluginClassMessageEffect;
    }

    public void setTokenPluginClassMessageEffect(
            Effect tokenPluginClassMessageEffect) {
        this.tokenPluginClassMessageEffect = tokenPluginClassMessageEffect;
    }

    public Effect getTokenPluginClassInputEffect() {
        return tokenPluginClassInputEffect;
    }

    public void setTokenPluginClassInputEffect(Effect tokenPluginClassInputEffect) {
        this.tokenPluginClassInputEffect = tokenPluginClassInputEffect;
    }

    public StsManageTokenIssuanceSummary getTokenIssuanceSummary() {
        return tokenIssuanceSummary;
    }

    public void setTokenIssuanceSummary(
            StsManageTokenIssuanceSummary tokenIssuanceSummary) {
        this.tokenIssuanceSummary = tokenIssuanceSummary;
    }

    public String getAuthenticationChain() {
        return authenticationChain;
    }

    public void setAuthenticationChain(String authenticationChain) {
        this.authenticationChain = authenticationChain;
    }

    public ArrayList<SecurityMechanismPanelBean> getSecurityMechanismPanels() {
        return securityMechanismPanels;
    }

    public void setSecurityMechanismPanels(
            ArrayList<SecurityMechanismPanelBean> securityMechanismPanels) {
        this.securityMechanismPanels = securityMechanismPanels;
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

    public void setServiceSecuritySummary(StsManageServiceSecuritySummary serviceSecuritySummary) {
        this.serviceSecuritySummary = serviceSecuritySummary;
    }

    public StsManageServiceSecuritySummary getServiceSecuritySummary() {
        return serviceSecuritySummary;
    }

    public void setRequestSignatureVerified(boolean requestSignatureVerified) {
        this.requestSignatureVerified = requestSignatureVerified;
    }

    public boolean isRequestSignatureVerified() {
        return requestSignatureVerified;
    }

    public void setRequestHeaderDecrypted(boolean requestHeaderDecrypted) {
        this.requestHeaderDecrypted = requestHeaderDecrypted;
    }

    public boolean isRequestHeaderDecrypted() {
        return requestHeaderDecrypted;
    }

    public void setRequestDecrypted(boolean requestDecrypted) {
        this.requestDecrypted = requestDecrypted;
    }

    public boolean isRequestDecrypted() {
        return requestDecrypted;
    }

    public void setResponseSigned(boolean responseSigned) {
        this.responseSigned = responseSigned;
    }

    public boolean isResponseSigned() {
        return responseSigned;
    }

    public void setResponseEncrypted(boolean responseEncrypted) {
        this.responseEncrypted = responseEncrypted;
    }

    public boolean isResponseEncrypted() {
        return responseEncrypted;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setPrivateKeyAlias(String privateKeyAlias) {
        this.privateKeyAlias = privateKeyAlias;
    }

    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }

    public void setPublicKeyAlias(String publicKeyAlias) {
        this.publicKeyAlias = publicKeyAlias;
    }

    public String getPublicKeyAlias() {
        return publicKeyAlias;
    }

    public void setSignEncryptSummary(StsManageSignEncryptSummary signEncryptSummary) {
        this.signEncryptSummary = signEncryptSummary;
    }

    public StsManageSignEncryptSummary getSignEncryptSummary() {
        return signEncryptSummary;
    }

    public void setNameIdMapper(String nameIdMapper) {
        this.nameIdMapper = nameIdMapper;
    }

    public String getNameIdMapper() {
        return nameIdMapper;
    }

    public void setIncludeMemberships(boolean includeMemberships) {
        this.includeMemberships = includeMemberships;
    }

    public boolean isIncludeMemberships() {
        return includeMemberships;
    }

    public void setAttributeNamespace(String attributeNamespace) {
        this.attributeNamespace = attributeNamespace;
    }

    public String getAttributeNamespace() {
        return attributeNamespace;
    }

    public void setSamlSummary(StsManageSamlSummary samlSummary) {
        this.samlSummary = samlSummary;
    }

    public StsManageSamlSummary getSamlSummary() {
        return samlSummary;
    }

    public void setTrustedIssuers(EditableSelectOneBean trustedIssuers) {
        this.trustedIssuers = trustedIssuers;
    }

    public EditableSelectOneBean getTrustedIssuers() {
        return trustedIssuers;
    }

    public void setTrustedAddresses(EditableSelectOneBean trustedAddresses) {
        this.trustedAddresses = trustedAddresses;
    }

    public EditableSelectOneBean getTrustedAddresses() {
        return trustedAddresses;
    }

    public void setTokenValidationSummary(StsManageTokenValidationSummary tokenValidationSummary) {
        this.tokenValidationSummary = tokenValidationSummary;
    }

    public StsManageTokenValidationSummary getTokenValidationSummary() {
        return tokenValidationSummary;
    }

    public void setNameIdMapperInputEffect(Effect nameIdMapperInputEffect) {
        this.nameIdMapperInputEffect = nameIdMapperInputEffect;
    }

    public Effect getNameIdMapperInputEffect() {
        return nameIdMapperInputEffect;
    }

    public void setNameIdMapperMessageEffect(Effect nameIdMapperMessageEffect) {
        this.nameIdMapperMessageEffect = nameIdMapperMessageEffect;
    }

    public Effect getNameIdMapperMessageEffect() {
        return nameIdMapperMessageEffect;
    }

    public void setAttributeNamespaceInputEffect(
            Effect attributeNamespaceInputEffect) {
        this.attributeNamespaceInputEffect = attributeNamespaceInputEffect;
    }

    public Effect getAttributeNamespaceInputEffect() {
        return attributeNamespaceInputEffect;
    }

    public void setAttributeNamespaceMessageEffect(
            Effect attributeNamespaceMessageEffect) {
        this.attributeNamespaceMessageEffect = attributeNamespaceMessageEffect;
    }

    public Effect getAttributeNamespaceMessageEffect() {
        return attributeNamespaceMessageEffect;
    }

    public void setSamlAttributesTable(SamlAttributesTableBean samlAttributesTable) {
        this.samlAttributesTable = samlAttributesTable;
    }

    public SamlAttributesTableBean getSamlAttributesTable() {
        return samlAttributesTable;
    }

    public void setUserCredentialsTable(UserCredentialsTableBean userCredentialsTable) {
        this.userCredentialsTable = userCredentialsTable;
    }

    public UserCredentialsTableBean getUserCredentialsTable() {
        return userCredentialsTable;
    }

}

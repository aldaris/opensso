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
 * $Id: StsManageWizardBean.java,v 1.1 2009-09-17 21:56:04 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.faces.model.SelectItem;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.dao.SigningKeysDao;
import com.sun.identity.admin.dao.StsConfigurationDao;

public class StsManageWizardBean
        extends WizardBean
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

    private WssProviderProfileBean stsProfileBean;

    private ArrayList<SamlAttributeMapEntry> attributeMapping;
    private boolean showingNewAttributeFields;
    private String newLocalAttributeName;
    private String newAssertionAttributeName;
    private int editAttributeIndex;
    private String nameIdMapper;
    private boolean includeMemberships;
    private String attributeNamespace;

    private EditableSelectOneBean trustedIssuers;
    private EditableSelectOneBean trustedAddresses;

    private StsManageTokenIssuanceSummary tokenIssuanceSummary;
    private StsManageServiceSecuritySummary serviceSecuritySummary;
    private StsManageSignEncryptSummary signEncryptSummary;
    private StsManageSamlSummary samlSummary;
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
        Resources r = new Resources();
        StsConfigurationBean stsConfig 
                                = StsConfigurationDao.retrieveConfiguration();

        this.setIssuer(stsConfig.getIssuer());
        this.setTokenLifetime(stsConfig.getTokenLifetime() / 1000);
        this.setKeyAlias(stsConfig.getKeyAlias());
        this.setTokenPluginClass(stsConfig.getTokenPluginClassName());
        this.setNameIdMapper(stsConfig.getNameIdMapper());
        this.setIncludeMemberships(stsConfig.isIncludeMemberships());
        this.setAttributeNamespace(stsConfig.getAttributeNamespace());

        this.setStsProfileBean(new WssProviderProfileBean());
        this.getStsProfileBean().setSecurityMechanisms(getMechanismsFromStsConfig(stsConfig)); 
        this.getStsProfileBean().setAuthenticationChain(stsConfig.getAuthenticationChain());
        this.getStsProfileBean().setRequestSignatureVerified(stsConfig.isRequestSigned());
        this.getStsProfileBean().setRequestHeaderDecrypted(stsConfig.isRequestHeaderEncrypted());
        this.getStsProfileBean().setRequestDecrypted(stsConfig.isRequestEncrypted());
        this.getStsProfileBean().setResponseSigned(stsConfig.isResponseSigned());
        this.getStsProfileBean().setResponseEncrypted(stsConfig.isResponseEncrypted());
        this.getStsProfileBean().setEncryptionAlgorithm(getEncryptionFromStsConfig(stsConfig));
        this.getStsProfileBean().setPrivateKeyAlias(stsConfig.getPrivateKeyAlias());
        this.getStsProfileBean().setPublicKeyAlias(stsConfig.getPublicKeyAlias());
        // to-do: talk about multi username password entry
        this.getStsProfileBean().setKerberosDomain(stsConfig.getKerberosDomain());
        this.getStsProfileBean().setKerberosDomainServer(stsConfig.getKerberosDomainServer());
        this.getStsProfileBean().setKerberosServicePrincipal(stsConfig.getKerberosServicePrincipal());
        this.getStsProfileBean().setKerberosKeyTabFile(stsConfig.getKerberosKeyTabFile());
        this.getStsProfileBean().setX509TokenSigningReferenceType(getSigningRefTypeFromStsConfig(stsConfig));

        EditableSelectOneBean esmb;
        esmb = new EditableSelectOneBean();
        esmb.setItems(stsConfig.getTrustedIssuers());
        this.setTrustedIssuers(esmb);

        esmb = new EditableSelectOneBean();
        esmb.setItems(stsConfig.getTrustedIpAddresses());
        esmb.setValidPattern("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
        esmb.setInvalidMessageDetail(r.getString(this, "trustedAddresses.invalidMessageDetail"));
        this.setTrustedAddresses(esmb);
        
        // attribute map interface
        this.setAttributeMapping(getDefaultAttributeMap(stsConfig));
        this.setShowingNewAttributeFields(false);
        this.setNewLocalAttributeName(null);
        this.setNewAssertionAttributeName(null);
        this.setEditAttributeIndex(-1);
        
        // summaries
        this.setTokenIssuanceSummary(new StsManageTokenIssuanceSummary(this));
        this.setServiceSecuritySummary(new StsManageServiceSecuritySummary(this));
        this.setSignEncryptSummary(new StsManageSignEncryptSummary(this));
        this.setSamlSummary(new StsManageSamlSummary(this));
        this.setTokenValidationSummary(new StsManageTokenValidationSummary(this));
    }

    private int getEncryptionFromStsConfig(StsConfigurationBean stsConfig) {
        String algorithm = stsConfig.getEncryptionAlgorithm();
        int strength = stsConfig.getEncryptionStrength();
        EncryptionAlgorithm ea = null;
        
        if( algorithm.equals(StsConfigurationDao.ENCRYPTION_ALGORITHM_DESEDE)) {
            switch(strength) {
                case 168:
                    ea = EncryptionAlgorithm.TRIPLEDES_168;
                    break;
                case 112:
                    ea = EncryptionAlgorithm.TRIPLEDES_112;
                    break;
                default:
                    ea = EncryptionAlgorithm.TRIPLEDES_0;
                    break;
            }
        } else {
            switch(strength) {
                case 128:
                    ea = EncryptionAlgorithm.AES_128;
                    break;
                case 192:
                    ea = EncryptionAlgorithm.AES_192;
                    break;
                default:
                    ea = EncryptionAlgorithm.AES_256;
                    break;
            }
        }
        
        return ea == null ? -1 : ea.toInt();
    }
    
    private Integer[] getMechanismsFromStsConfig(StsConfigurationBean stsConfig) {
        Integer[] values = null;
        ArrayList<String> securityMechanisms = stsConfig.getSecurityMechanisms();
        
        if( securityMechanisms != null ) {
            ArrayList<Integer> smIds = new ArrayList<Integer>();
            for(String s : securityMechanisms) {
                SecurityMechanism sm = SecurityMechanism.valueOfConfig(s);
                if( sm != null) {
                    smIds.add(new Integer(sm.toInt()));
                }
            }
            
            if( smIds.size() > 0 ) {
                values = new Integer[smIds.size()];
                for(int i=0; i < smIds.size(); i++) {
                    values[i] = smIds.get(i);
                }
            }
        }
        
        return values;
    }

    private int getSigningRefTypeFromStsConfig(StsConfigurationBean stsConfig) {
        int value = -1;
        String type = stsConfig.getX509SigningReferenceType();

        if( type != null ) {
            X509SigningRefType x = X509SigningRefType.valueOfConfig(type);
            
            if( x != null ) {
                value = x.toInt();
            }
        }
        return value;
    }

    private ArrayList<SamlAttributeMapEntry> getDefaultAttributeMap(StsConfigurationBean stsConfig) {
        ArrayList<SamlAttributeMapEntry> attributeMap 
                                    = new ArrayList<SamlAttributeMapEntry>();
        
        // obtain the sts configuration map...
        ArrayList<String> pairs = stsConfig.getSamlAttributeMapping();
        Hashtable<String, String> stsConfigEntries 
                                    = new Hashtable<String, String>();
        
        if( pairs != null ) {
            for(String s : pairs) {
                if( s != null ) {
                    String assertionAttrName = s.substring(0, s.indexOf("="));
                    String localAttrName = s.substring(s.indexOf("=") + 1);
                    
                    if( assertionAttrName.length() > 0 
                            && localAttrName.length() > 0 ) {
                        stsConfigEntries.put(localAttrName, assertionAttrName);
                    }
                }
            }
        }

        // put in the defaults...
        ArrayList<String> defaultEntries = new ArrayList<String>();
        defaultEntries.add("cn");
        defaultEntries.add("employeenumber");
        defaultEntries.add("givenname");
        defaultEntries.add("mail");
        defaultEntries.add("manager");
        defaultEntries.add("postaladdress");
        defaultEntries.add("sn");
        defaultEntries.add("telephonenumber");
        defaultEntries.add("uid");

        // set the attribute map for the ui...
        for(String s : defaultEntries) {
            SamlAttributeMapEntry entry = new SamlAttributeMapEntry();
            entry.setCustom(false);
            entry.setLocalAttributeName(s);
            
            if( stsConfigEntries.containsKey(s) ) {
                entry.setAssertionAttributeName(stsConfigEntries.get(s));
                stsConfigEntries.remove(s);
            } else {
                entry.setAssertionAttributeName(null);
            }
            attributeMap.add(entry);
        }
        
        // add any remaining entries...
        for(String s : stsConfigEntries.keySet()) {
            SamlAttributeMapEntry entry = new SamlAttributeMapEntry();
            entry.setCustom(true);
            entry.setLocalAttributeName(s);
            entry.setAssertionAttributeName(stsConfigEntries.get(s));
            attributeMap.add(entry);
        }
        
        return attributeMap;
    }

    // Lists -------------------------------------------------------------------

    public List<SelectItem> getKeyAliasList() {
        List<SelectItem> items = new ArrayList<SelectItem>();

        SigningKeysDao signingKeysDao = new SigningKeysDao();
        List<SigningKeyBean> signingKeys = signingKeysDao.getSigningKeyBeans();
        for( SigningKeyBean bean : signingKeys ) {
            items.add(new SelectItem(bean.getTitle()));
        }

        return items;
    }


    // Getters / Setters -------------------------------------------------------

    public WssProviderProfileBean getStsProfileBean() {
        return stsProfileBean;
    }

    public void setStsProfileBean(WssProviderProfileBean stsProfileBean) {
        this.stsProfileBean = stsProfileBean;
    }

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

    public ArrayList<SamlAttributeMapEntry> getAttributeMapping() {
        return attributeMapping;
    }

    public void setAttributeMapping(
            ArrayList<SamlAttributeMapEntry> attributeMapping) {
        this.attributeMapping = attributeMapping;
    }

    public boolean isShowingNewAttributeFields() {
        return showingNewAttributeFields;
    }

    public void setShowingNewAttributeFields(boolean showingNewAttributeFields) {
        this.showingNewAttributeFields = showingNewAttributeFields;
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

    public int getEditAttributeIndex() {
        return editAttributeIndex;
    }

    public void setEditAttributeIndex(int editAttributeIndex) {
        this.editAttributeIndex = editAttributeIndex;
    }

    public String getNameIdMapper() {
        return nameIdMapper;
    }

    public void setNameIdMapper(String nameIdMapper) {
        this.nameIdMapper = nameIdMapper;
    }

    public boolean isIncludeMemberships() {
        return includeMemberships;
    }

    public void setIncludeMemberships(boolean includeMemberships) {
        this.includeMemberships = includeMemberships;
    }

    public String getAttributeNamespace() {
        return attributeNamespace;
    }

    public void setAttributeNamespace(String attributeNamespace) {
        this.attributeNamespace = attributeNamespace;
    }

    public EditableSelectOneBean getTrustedIssuers() {
        return trustedIssuers;
    }

    public void setTrustedIssuers(EditableSelectOneBean trustedIssuers) {
        this.trustedIssuers = trustedIssuers;
    }

    public EditableSelectOneBean getTrustedAddresses() {
        return trustedAddresses;
    }

    public void setTrustedAddresses(EditableSelectOneBean trustedAddresses) {
        this.trustedAddresses = trustedAddresses;
    }

    public StsManageTokenIssuanceSummary getTokenIssuanceSummary() {
        return tokenIssuanceSummary;
    }

    public void setTokenIssuanceSummary(
            StsManageTokenIssuanceSummary tokenIssuanceSummary) {
        this.tokenIssuanceSummary = tokenIssuanceSummary;
    }

    public StsManageServiceSecuritySummary getServiceSecuritySummary() {
        return serviceSecuritySummary;
    }

    public void setServiceSecuritySummary(
            StsManageServiceSecuritySummary serviceSecuritySummary) {
        this.serviceSecuritySummary = serviceSecuritySummary;
    }

    public StsManageSignEncryptSummary getSignEncryptSummary() {
        return signEncryptSummary;
    }

    public void setSignEncryptSummary(StsManageSignEncryptSummary signEncryptSummary) {
        this.signEncryptSummary = signEncryptSummary;
    }

    public StsManageSamlSummary getSamlSummary() {
        return samlSummary;
    }

    public void setSamlSummary(StsManageSamlSummary samlSummary) {
        this.samlSummary = samlSummary;
    }

    public StsManageTokenValidationSummary getTokenValidationSummary() {
        return tokenValidationSummary;
    }

    public void setTokenValidationSummary(
            StsManageTokenValidationSummary tokenValidationSummary) {
        this.tokenValidationSummary = tokenValidationSummary;
    }

}

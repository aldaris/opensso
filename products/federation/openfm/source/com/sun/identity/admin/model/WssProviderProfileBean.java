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
 * $Id: WssProviderProfileBean.java,v 1.2 2009-09-17 21:56:04 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.dao.SigningKeysDao;
import com.sun.identity.authentication.service.ConfiguredAuthServices;

public class WssProviderProfileBean implements Serializable {

    private String profileName;
    private String endPoint;

    private Integer[] securityMechanisms;
    private String authenticationChain;

    private boolean requestSignatureVerified;
    private boolean requestHeaderDecrypted;
    private boolean requestDecrypted;
    private boolean responseSigned;
    private boolean responseEncrypted;
    private int encryptionAlgorithm;
    private String privateKeyAlias;
    private String publicKeyAlias;

    private String userNameTokenUserName;
    private Effect userNameTokenUserNameInputEffect;
    private Effect userNameTokenUserNameMessageEffect;
    private String userNameTokenPassword;
    private Effect userNameTokenPasswordInputEffect;
    private Effect userNameTokenPasswordMessageEffect;
    private String kerberosDomain;
    private Effect kerberosDomainInputEffect;
    private Effect kerberosDomainMessageEffect;
    private String kerberosDomainServer;
    private Effect kerberosDomainServerInputEffect;
    private Effect kerberosDomainServerMessageEffect;
    private String kerberosServicePrincipal;
    private Effect kerberosServicePrincipalInputEffect;
    private Effect kerberosServicePrincipalMessageEffect;

    private int x509TokenSigningReferenceType;

    private String kerberosKeyTabFile;
    private Effect kerberosKeyTabFileInputEffect;
    private Effect kerberosKeyTabFileMessageEffect;
    
    // -------------------------------------------------------------------------
    
    public boolean getNeedsTokenConfig() {
        return getNeedsUserNameConfig() 
            || getNeedsKerberosConfig() 
            || getNeedsX509Config();
    }
    
    public boolean getNeedsUserNameConfig() {
        
        if( this.getSecurityMechanisms() != null ) {
            for(Integer i : this.getSecurityMechanisms() ) {
                
                SecurityMechanism sm = SecurityMechanism.valueOf(i.intValue());
                
                if( sm == SecurityMechanism.USERNAME_TOKEN 
                        || sm == SecurityMechanism.USERNAME_TOKEN_PLAIN ) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean getNeedsKerberosConfig() {
        
        if( this.getSecurityMechanisms() != null ) {
            for(Integer i : this.getSecurityMechanisms() ) {
                
                SecurityMechanism sm = SecurityMechanism.valueOf(i.intValue());
                
                if( sm == SecurityMechanism.KERBEROS_TOKEN ) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean getNeedsX509Config() {
        
        if( this.getSecurityMechanisms() != null ) {
            for(Integer i : this.getSecurityMechanisms() ) {
                
                SecurityMechanism sm = SecurityMechanism.valueOf(i.intValue());
                
                if( sm == SecurityMechanism.X509_TOKEN ) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // Lists -------------------------------------------------------------------

    @SuppressWarnings("unchecked")
	public List<SelectItem> getAuthenticationChainList() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        
        ConfiguredAuthServices authServices = new ConfiguredAuthServices();
        Set<String> authChains = authServices.getChoiceValues().keySet();
        Iterator<String> i = authChains.iterator();
        
        while( i.hasNext() ) {
            String authChain = i.next();
            items.add(new SelectItem(authChain));
        }
        
        return items;
    }

    public List<SelectItem> getSecurityMechanismList() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        
        items.add(new SelectItem(SecurityMechanism.ANONYMOUS.toInt(),
                                 SecurityMechanism.ANONYMOUS.toLocaleString()));
        items.add(new SelectItem(SecurityMechanism.SAML_HOK.toInt(),
                                 SecurityMechanism.SAML_HOK.toLocaleString()));
        items.add(new SelectItem(SecurityMechanism.SAML_SV.toInt(),
                                 SecurityMechanism.SAML_SV.toLocaleString()));
        items.add(new SelectItem(SecurityMechanism.SAML2_HOK.toInt(),
                                 SecurityMechanism.SAML2_HOK.toLocaleString()));
        items.add(new SelectItem(SecurityMechanism.SAML2_SV.toInt(),
                                 SecurityMechanism.SAML2_SV.toLocaleString()));
        items.add(new SelectItem(SecurityMechanism.USERNAME_TOKEN.toInt(),
                                 SecurityMechanism.USERNAME_TOKEN.toLocaleString()));
        items.add(new SelectItem(SecurityMechanism.USERNAME_TOKEN_PLAIN.toInt(),
                                 SecurityMechanism.USERNAME_TOKEN_PLAIN.toLocaleString()));
        items.add(new SelectItem(SecurityMechanism.KERBEROS_TOKEN.toInt(),
                                 SecurityMechanism.KERBEROS_TOKEN.toLocaleString()));
        items.add(new SelectItem(SecurityMechanism.X509_TOKEN.toInt(),
                                 SecurityMechanism.X509_TOKEN.toLocaleString()));
        
        return items;
    }

    public List<SelectItem> getEncryptionAlgorithmList() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        
        items.add(new SelectItem(EncryptionAlgorithm.AES_128.toInt(),
                                 EncryptionAlgorithm.AES_128.toLocaleString()));
        items.add(new SelectItem(EncryptionAlgorithm.AES_192.toInt(),
                                 EncryptionAlgorithm.AES_192.toLocaleString()));
        items.add(new SelectItem(EncryptionAlgorithm.AES_256.toInt(),
                                 EncryptionAlgorithm.AES_256.toLocaleString()));
        items.add(new SelectItem(EncryptionAlgorithm.TRIPLEDES_0.toInt(),
                                 EncryptionAlgorithm.TRIPLEDES_0.toLocaleString()));
        items.add(new SelectItem(EncryptionAlgorithm.TRIPLEDES_112.toInt(),
                                 EncryptionAlgorithm.TRIPLEDES_112.toLocaleString()));
        items.add(new SelectItem(EncryptionAlgorithm.TRIPLEDES_168.toInt(),
                                 EncryptionAlgorithm.TRIPLEDES_168.toLocaleString()));
        
        return items;
    }

    public List<SelectItem> getPublicKeyAliasList() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        
        SigningKeysDao signingKeysDao = new SigningKeysDao();
        List<SigningKeyBean> signingKeys = signingKeysDao.getSigningKeyBeans();
        for( SigningKeyBean bean : signingKeys ) {
            items.add(new SelectItem(bean.getTitle()));
        }
        
        return items;
    }

    public List<SelectItem> getPrivateKeyAliasList() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        
        SigningKeysDao signingKeysDao = new SigningKeysDao();
        List<SigningKeyBean> signingKeys = signingKeysDao.getSigningKeyBeans();
        for( SigningKeyBean bean : signingKeys ) {
            items.add(new SelectItem(bean.getTitle()));
        }
        
        return items;
    }

    public List<SelectItem> getX509SigningReferenceTypeList() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        
        items.add(new SelectItem(X509SigningRefType.DIRECT.toInt(),
                                 X509SigningRefType.DIRECT.toLocaleString()));
        items.add(new SelectItem(X509SigningRefType.KEY_IDENTIFIER.toInt(),
                                 X509SigningRefType.KEY_IDENTIFIER.toLocaleString()));
        items.add(new SelectItem(X509SigningRefType.ISSUER_SERIAL.toInt(),
                                 X509SigningRefType.ISSUER_SERIAL.toLocaleString()));
        
        return items;
    }
    
    // Getters / Setters -------------------------------------------------------

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Integer[] getSecurityMechanisms() {
        return securityMechanisms;
    }

    public void setSecurityMechanisms(Integer[] securityMechanisms) {
        this.securityMechanisms = securityMechanisms;
    }

    public String getAuthenticationChain() {
        return authenticationChain;
    }

    public void setAuthenticationChain(String authenticationChain) {
        this.authenticationChain = authenticationChain;
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

    public int getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(int encryptionAlgorithm) {
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

    public String getUserNameTokenUserName() {
        return userNameTokenUserName;
    }

    public void setUserNameTokenUserName(String userNameTokenUserName) {
        this.userNameTokenUserName = userNameTokenUserName;
    }

    public Effect getUserNameTokenUserNameInputEffect() {
        return userNameTokenUserNameInputEffect;
    }

    public void setUserNameTokenUserNameInputEffect(
            Effect userNameTokenUserNameInputEffect) {
        this.userNameTokenUserNameInputEffect = userNameTokenUserNameInputEffect;
    }

    public Effect getUserNameTokenUserNameMessageEffect() {
        return userNameTokenUserNameMessageEffect;
    }

    public void setUserNameTokenUserNameMessageEffect(
            Effect userNameTokenUserNameMessageEffect) {
        this.userNameTokenUserNameMessageEffect = userNameTokenUserNameMessageEffect;
    }

    public String getUserNameTokenPassword() {
        return userNameTokenPassword;
    }

    public void setUserNameTokenPassword(String userNameTokenPassword) {
        this.userNameTokenPassword = userNameTokenPassword;
    }

    public Effect getUserNameTokenPasswordInputEffect() {
        return userNameTokenPasswordInputEffect;
    }

    public void setUserNameTokenPasswordInputEffect(
            Effect userNameTokenPasswordInputEffect) {
        this.userNameTokenPasswordInputEffect = userNameTokenPasswordInputEffect;
    }

    public Effect getUserNameTokenPasswordMessageEffect() {
        return userNameTokenPasswordMessageEffect;
    }

    public void setUserNameTokenPasswordMessageEffect(
            Effect userNameTokenPasswordMessageEffect) {
        this.userNameTokenPasswordMessageEffect = userNameTokenPasswordMessageEffect;
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

    public int getX509TokenSigningReferenceType() {
        return x509TokenSigningReferenceType;
    }

    public void setX509TokenSigningReferenceType(int tokenSigningReferenceType) {
        x509TokenSigningReferenceType = tokenSigningReferenceType;
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

}

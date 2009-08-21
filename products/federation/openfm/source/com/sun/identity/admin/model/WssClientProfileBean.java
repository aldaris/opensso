/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * $Id: WssClientProfileBean.java,v 1.1 2009-08-21 21:07:35 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.dao.SigningKeysDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class WssClientProfileBean implements Serializable {

    private String profileName;
    private Effect profileNameMessageEffect;
    private Effect profileNameInputEffect;

    private String endPoint;
    private Effect endPointMessageEffect;
    private Effect endPointInputEffect;

    private boolean usingMexEndPoint;

    private String mexEndPoint;
    private Effect mexEndPointMessageEffect;
    private Effect mexEndPointInputEffect;

    private int securityMechanism;
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
    private String kerberosTicketCache;
    private Effect kerberosTicketCacheInputEffect;
    private Effect kerberosTicketCacheMessageEffect;
    private int x509TokenSigningReferenceType;

    private boolean requestSigned;
    private boolean requestHeaderEncrypted;
    private boolean requestEncrypted;
    private boolean responseSignatureVerified;
    private boolean responseDecrypted;
    private int encryptionAlgorithm;
    private String privateKeyAlias;
    private String publicKeyAlias;


    // Lists -------------------------------------------------------------------

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

    // Stack Panel Selectors ---------------------------------------------------

    public String getSecurityMechanismPanel() {
        SecurityMechanism sm = SecurityMechanism.valueOf(this.getSecurityMechanism());
        String panelId;

        switch(sm) {
            case USERNAME_TOKEN:
            case USERNAME_TOKEN_PLAIN:
                panelId = "userNameTokenSettingsPanel";
                break;
            case KERBEROS_TOKEN:
                panelId = "kerberosSettingsPanel";
                break;
            case X509_TOKEN:
                panelId = "x509TokenSettingsPanel";
                break;
            default:
                panelId = "noSettingsPanel";
                break;
        }

        return panelId;
    }

    // Getters / Setters -------------------------------------------------------

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Effect getProfileNameMessageEffect() {
        return profileNameMessageEffect;
    }

    public void setProfileNameMessageEffect(Effect profileNameMessageEffect) {
        this.profileNameMessageEffect = profileNameMessageEffect;
    }

    public Effect getProfileNameInputEffect() {
        return profileNameInputEffect;
    }

    public void setProfileNameInputEffect(Effect profileNameInputEffect) {
        this.profileNameInputEffect = profileNameInputEffect;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Effect getEndPointMessageEffect() {
        return endPointMessageEffect;
    }

    public void setEndPointMessageEffect(Effect endPointMessageEffect) {
        this.endPointMessageEffect = endPointMessageEffect;
    }

    public Effect getEndPointInputEffect() {
        return endPointInputEffect;
    }

    public void setEndPointInputEffect(Effect endPointInputEffect) {
        this.endPointInputEffect = endPointInputEffect;
    }

    public String getMexEndPoint() {
        return mexEndPoint;
    }

    public void setMexEndPoint(String mexEndPoint) {
        this.mexEndPoint = mexEndPoint;
    }

    public Effect getMexEndPointMessageEffect() {
        return mexEndPointMessageEffect;
    }

    public void setMexEndPointMessageEffect(Effect mexEndPointMessageEffect) {
        this.mexEndPointMessageEffect = mexEndPointMessageEffect;
    }

    public Effect getMexEndPointInputEffect() {
        return mexEndPointInputEffect;
    }

    public void setMexEndPointInputEffect(Effect mexEndPointInputEffect) {
        this.mexEndPointInputEffect = mexEndPointInputEffect;
    }

    public int getSecurityMechanism() {
        return securityMechanism;
    }

    public void setSecurityMechanism(int securityMechanism) {
        this.securityMechanism = securityMechanism;
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

    public void setUserNameTokenUserNameInputEffect(Effect userNameTokenUserNameInputEffect) {
        this.userNameTokenUserNameInputEffect = userNameTokenUserNameInputEffect;
    }

    public Effect getUserNameTokenUserNameMessageEffect() {
        return userNameTokenUserNameMessageEffect;
    }

    public void setUserNameTokenUserNameMessageEffect(Effect userNameTokenUserNameMessageEffect) {
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

    public void setUserNameTokenPasswordInputEffect(Effect userNameTokenPasswordInputEffect) {
        this.userNameTokenPasswordInputEffect = userNameTokenPasswordInputEffect;
    }

    public Effect getUserNameTokenPasswordMessageEffect() {
        return userNameTokenPasswordMessageEffect;
    }

    public void setUserNameTokenPasswordMessageEffect(Effect userNameTokenPasswordMessageEffect) {
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

    public void setKerberosDomainServerInputEffect(Effect kerberosDomainServerInputEffect) {
        this.kerberosDomainServerInputEffect = kerberosDomainServerInputEffect;
    }

    public Effect getKerberosDomainServerMessageEffect() {
        return kerberosDomainServerMessageEffect;
    }

    public void setKerberosDomainServerMessageEffect(Effect kerberosDomainServerMessageEffect) {
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

    public void setKerberosServicePrincipalInputEffect(Effect kerberosServicePrincipalInputEffect) {
        this.kerberosServicePrincipalInputEffect = kerberosServicePrincipalInputEffect;
    }

    public Effect getKerberosServicePrincipalMessageEffect() {
        return kerberosServicePrincipalMessageEffect;
    }

    public void setKerberosServicePrincipalMessageEffect(Effect kerberosServicePrincipalMessageEffect) {
        this.kerberosServicePrincipalMessageEffect = kerberosServicePrincipalMessageEffect;
    }

    public String getKerberosTicketCache() {
        return kerberosTicketCache;
    }

    public void setKerberosTicketCache(String kerberosTicketCache) {
        this.kerberosTicketCache = kerberosTicketCache;
    }

    public Effect getKerberosTicketCacheInputEffect() {
        return kerberosTicketCacheInputEffect;
    }

    public void setKerberosTicketCacheInputEffect(Effect kerberosTicketCacheInputEffect) {
        this.kerberosTicketCacheInputEffect = kerberosTicketCacheInputEffect;
    }

    public Effect getKerberosTicketCacheMessageEffect() {
        return kerberosTicketCacheMessageEffect;
    }

    public void setKerberosTicketCacheMessageEffect(Effect kerberosTicketCacheMessageEffect) {
        this.kerberosTicketCacheMessageEffect = kerberosTicketCacheMessageEffect;
    }

    public int getX509TokenSigningReferenceType() {
        return x509TokenSigningReferenceType;
    }

    public void setX509TokenSigningReferenceType(int x509TokenSigningReferenceType) {
        this.x509TokenSigningReferenceType = x509TokenSigningReferenceType;
    }

    public boolean isRequestSigned() {
        return requestSigned;
    }

    public void setRequestSigned(boolean requestSigned) {
        this.requestSigned = requestSigned;
    }

    public boolean isRequestHeaderEncrypted() {
        return requestHeaderEncrypted;
    }

    public void setRequestHeaderEncrypted(boolean requestHeaderEncrypted) {
        this.requestHeaderEncrypted = requestHeaderEncrypted;
    }

    public boolean isRequestEncrypted() {
        return requestEncrypted;
    }

    public void setRequestEncrypted(boolean requestEncrypted) {
        this.requestEncrypted = requestEncrypted;
    }

    public boolean isResponseSignatureVerified() {
        return responseSignatureVerified;
    }

    public void setResponseSignatureVerified(boolean responseSignatureVerified) {
        this.responseSignatureVerified = responseSignatureVerified;
    }

    public boolean isResponseDecrypted() {
        return responseDecrypted;
    }

    public void setResponseDecrypted(boolean responseDecrypted) {
        this.responseDecrypted = responseDecrypted;
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

    public boolean isUsingMexEndPoint() {
        return usingMexEndPoint;
    }

    public void setUsingMexEndPoint(boolean usingMexEndPoint) {
        this.usingMexEndPoint = usingMexEndPoint;
    }


}

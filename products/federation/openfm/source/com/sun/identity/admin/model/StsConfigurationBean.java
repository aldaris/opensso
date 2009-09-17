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
 * $Id: StsConfigurationBean.java,v 1.1 2009-09-17 21:56:04 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.sun.identity.wss.security.PasswordCredential;

public class StsConfigurationBean implements Serializable {
    
    private String issuer;
    private int tokenLifetime;
    private String keyAlias;
    private String tokenPluginClassName;
    private ArrayList<String> securityMechanisms;
    private ArrayList<PasswordCredential> userNameTokenCredentials;
    private String kerberosDomain;
    private String kerberosDomainServer;
    private String kerberosServicePrincipal;
    private String kerberosKeyTabFile;
    private String x509SigningReferenceType;
    private String authenticationChain;
    private boolean responseSigned;
    private boolean responseEncrypted;
    private boolean requestSigned;
    private boolean requestEncrypted;
    private boolean requestHeaderEncrypted;
    private String encryptionAlgorithm;
    private int encryptionStrength;
    private String privateKeyAlias;
    private String publicKeyAlias;
    private String nameIdMapper;
    private boolean includeMemberships;
    private String attributeNamespace;
    private ArrayList<String> trustedIssuers;
    private ArrayList<String> trustedIpAddresses;
    private ArrayList<String> samlAttributeMapping;
    
    
    public String getIssuer() {
        return issuer;
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    public int getTokenLifetime() {
        return tokenLifetime;
    }
    public void setTokenLifetime(int tokenLifetime) {
        this.tokenLifetime = tokenLifetime;
    }
    public String getKeyAlias() {
        return keyAlias;
    }
    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }
    public String getTokenPluginClassName() {
        return tokenPluginClassName;
    }
    public void setTokenPluginClassName(String tokenPluginClassName) {
        this.tokenPluginClassName = tokenPluginClassName;
    }
    public ArrayList<String> getSecurityMechanisms() {
        return securityMechanisms;
    }
    public void setSecurityMechanisms(ArrayList<String> securityMechanisms) {
        this.securityMechanisms = securityMechanisms;
    }
    public ArrayList<PasswordCredential> getUserNameTokenCredentials() {
        return userNameTokenCredentials;
    }
    public void setUserNameTokenCredentials(
            ArrayList<PasswordCredential> userNameTokenCredentials) {
        this.userNameTokenCredentials = userNameTokenCredentials;
    }
    public String getKerberosDomain() {
        return kerberosDomain;
    }
    public void setKerberosDomain(String kerberosDomain) {
        this.kerberosDomain = kerberosDomain;
    }
    public String getKerberosDomainServer() {
        return kerberosDomainServer;
    }
    public void setKerberosDomainServer(String kerberosDomainServer) {
        this.kerberosDomainServer = kerberosDomainServer;
    }
    public String getKerberosServicePrincipal() {
        return kerberosServicePrincipal;
    }
    public void setKerberosServicePrincipal(String kerberosServicePrincipal) {
        this.kerberosServicePrincipal = kerberosServicePrincipal;
    }
    public String getKerberosKeyTabFile() {
        return kerberosKeyTabFile;
    }
    public void setKerberosKeyTabFile(String kerberosKeyTabFile) {
        this.kerberosKeyTabFile = kerberosKeyTabFile;
    }
    public String getX509SigningReferenceType() {
        return x509SigningReferenceType;
    }
    public void setX509SigningReferenceType(String signingReferenceType) {
        x509SigningReferenceType = signingReferenceType;
    }
    public String getAuthenticationChain() {
        return authenticationChain;
    }
    public void setAuthenticationChain(String authenticationChain) {
        this.authenticationChain = authenticationChain;
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
    public boolean isRequestSigned() {
        return requestSigned;
    }
    public void setRequestSigned(boolean requestSigned) {
        this.requestSigned = requestSigned;
    }
    public boolean isRequestEncrypted() {
        return requestEncrypted;
    }
    public void setRequestEncrypted(boolean requestEncrypted) {
        this.requestEncrypted = requestEncrypted;
    }
    public boolean isRequestHeaderEncrypted() {
        return requestHeaderEncrypted;
    }
    public void setRequestHeaderEncrypted(boolean requestHeaderEncrypted) {
        this.requestHeaderEncrypted = requestHeaderEncrypted;
    }
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }
    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }
    public int getEncryptionStrength() {
        return encryptionStrength;
    }
    public void setEncryptionStrength(int encryptionStrength) {
        this.encryptionStrength = encryptionStrength;
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
    public ArrayList<String> getTrustedIssuers() {
        return trustedIssuers;
    }
    public void setTrustedIssuers(ArrayList<String> trustedIssuers) {
        this.trustedIssuers = trustedIssuers;
    }
    public ArrayList<String> getTrustedIpAddresses() {
        return trustedIpAddresses;
    }
    public void setTrustedIpAddresses(ArrayList<String> trustedIpAddresses) {
        this.trustedIpAddresses = trustedIpAddresses;
    }
    public ArrayList<String> getSamlAttributeMapping() {
        return samlAttributeMapping;
    }
    public void setSamlAttributeMapping(ArrayList<String> samlAttributeMapping) {
        this.samlAttributeMapping = samlAttributeMapping;
    }
    
}

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
 * $Id: WscProfileBean.java,v 1.2 2009-10-16 19:39:19 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import java.io.Serializable;

import com.icesoft.faces.context.effects.Effect;

public class WscProfileBean extends WssProfileBean implements Serializable {

    private String securityMechanism;
    private String stsClientProfileName;
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
    private String x509SigningRefType;
    
    
    // getters / setters -------------------------------------------------------
    
    public void setSecurityMechanism(String securityMechanism) {
        this.securityMechanism = securityMechanism;
    }

    public String getSecurityMechanism() {
        return securityMechanism;
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

    public String getKerberosTicketCache() {
        return kerberosTicketCache;
    }

    public void setKerberosTicketCache(String kerberosTicketCache) {
        this.kerberosTicketCache = kerberosTicketCache;
    }

    public Effect getKerberosTicketCacheInputEffect() {
        return kerberosTicketCacheInputEffect;
    }

    public void setKerberosTicketCacheInputEffect(
            Effect kerberosTicketCacheInputEffect) {
        this.kerberosTicketCacheInputEffect = kerberosTicketCacheInputEffect;
    }

    public Effect getKerberosTicketCacheMessageEffect() {
        return kerberosTicketCacheMessageEffect;
    }

    public void setKerberosTicketCacheMessageEffect(
            Effect kerberosTicketCacheMessageEffect) {
        this.kerberosTicketCacheMessageEffect = kerberosTicketCacheMessageEffect;
    }

    public void setStsClientProfileName(String stsClientProfileName) {
        this.stsClientProfileName = stsClientProfileName;
    }

    public String getStsClientProfileName() {
        return stsClientProfileName;
    }

    public void setX509SigningRefType(String x509SigningRefType) {
        this.x509SigningRefType = x509SigningRefType;
    }

    public String getX509SigningRefType() {
        return x509SigningRefType;
    }


}

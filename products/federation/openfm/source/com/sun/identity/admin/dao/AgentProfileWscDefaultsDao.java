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
 * $Id: AgentProfileWscDefaultsDao.java,v 1.1 2009-10-08 16:16:20 ggennaro Exp $
 */

package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOException;
import com.sun.identity.sm.SMSException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentProfileWscDefaultsDao
        extends AgentProfileDefaultsDao
{
    private static final String typeName = "WSCAgent";

    protected static String getDefault(String attributeName) {
        try {
            return getDefault(typeName, attributeName);
        } catch (SMSException ex) {
            Logger.getLogger(AgentProfileWscDefaultsDao.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SSOException ex) {
            Logger.getLogger(AgentProfileWscDefaultsDao.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static boolean isRequestSign() {
        return Boolean.valueOf(getDefault("isRequestSign"));
    }
    
    public static boolean isRequestHeaderEncrypt() {
        return Boolean.valueOf(getDefault("isRequestHeaderEncrypt"));
    }

    public static boolean isRequestEncrypt() {
        return Boolean.valueOf(getDefault("isRequestEncrypt"));
    }

    public static boolean isResponseSign() {
        return Boolean.valueOf(getDefault("isResponseSign"));
    }

    public static boolean isResponseEncrypt() {
        return Boolean.valueOf(getDefault("isResponseEncrypt"));
    }

    /*
    userpassword
    sunIdentityServerDeviceStatus
    SecurityMech
    STS
    Discovery
    forceUserAuthn
    keepSecurityHeaders
    useDefaultStore
    KeyStoreFile
    KeyStorePassword
    KeyPassword
    privateKeyAlias
    publicKeyAlias
    WSPEndpoint
    WSPProxyEndpoint
    serviceType
    UserCredential
    KerberosDomainServer
    KerberosDomain
    KerberosServicePrincipal
    KerberosTicketCacheDir
    isPassThroughSecurityToken
    SigningRefType
    EncryptionAlgorithm
    EncryptionStrength
    SAMLAttributeMapping
    NameIDMapper
    AttributeNamespace
    includeMemberships
    */
}

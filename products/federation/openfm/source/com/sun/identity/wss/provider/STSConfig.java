/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: STSConfig.java,v 1.4 2008-07-02 16:57:22 mallas Exp $
 *
 */
package com.sun.identity.wss.provider;

/**
 * This is an abstract that represents the configuration about a 
 * STS trusted authority.
 * 
 *
 * <p> This class can be extended to define the trust authority config
 * such as discovery configuration, ws-trust etc.
 * 
 * @supported.all.api
 */

public abstract class STSConfig extends TrustAuthorityConfig {
    
    protected String mexEndpoint = null;
    protected String stsConfigName = null;
    protected String kdcDomain = null;
    protected String kdcServer = null;
    protected String ticketCacheDir = null;
    protected String servicePrincipal = null;
    
    /** Creates a new instance of STSConfig */
    public STSConfig() {
    }
    
    /**
     * Returns STS Mex endpoint
     * @return STS Mex endpoint
     */
    public String getMexEndpoint() {
        return mexEndpoint;
    }         
    
    /**
     * Sets STS Mex endpoint
     * @param mexEndpoint STS Mex endpoint
     *
     */
    public void setMexEndpoint(String mexEndpoint) {
        this.mexEndpoint = mexEndpoint;
    }

    /**
     * Returns STS configuration name
     * @return STS configuration name
     */
    public String getSTSConfigName() {
        return stsConfigName;
    }         
    
    /**
     * Sets STS configuration name
     * @param stsConfigName STS configuration name
     *
     */
    public void setSTSConfigName(String stsConfigName) {
        this.stsConfigName = stsConfigName;
    }
    
    /**
     * Returns Kerberos Domain Controller Domain
     * @return Kerberos Domain Controller Domain
     */
     
    public String getKDCDomain() {
        return kdcDomain;
    }
    
    /**
     * Sets Kerberos Domain Controller Domain
     * @param domain Kerberos Domain Controller Domain
     */
    public void setKDCDomain(String domain) {
        this.kdcDomain = domain;
    }
    
    /**
     * Returns Kerberos Domain Controller Server.
     * @return Kerberos Domain Controller Server.
     */
    public String getKDCServer() {
        return kdcServer;
    }
    
    /**
     * Sets Kerberos Domain Controller Server
     * @param kdcServer Kerberos Domain Controller Server
     */
    public void setKDCServer(String kdcServer) {
        this.kdcServer = kdcServer;
    }
    
    /**
     * This method is used by the web services client to get the kerberos
     * ticket cache directory.
     * @return kerberos ticket cache dir
     */
    public String getKerberosTicketCacheDir() {
        return ticketCacheDir;
    }
    
    /**
     * Sets kerberos ticket cache dir.
     * @param cacheDir kerberos ticket cache dir
     */
    public void setKerberosTicketCacheDir(String cacheDir) {
        this.ticketCacheDir = cacheDir;
    }
    
    /**
     * Returns kerberos service principal
     * @return the kerberos service principal
     */
    public String getKerberosServicePrincipal() {
        return servicePrincipal;
    }
    
    /**
     * Sets kerberos service principal.
     * @param principal the kerberos service principal.
     */
    public void setKerberosServicePrincipal(String principal) {
        this.servicePrincipal = principal;
    }
}

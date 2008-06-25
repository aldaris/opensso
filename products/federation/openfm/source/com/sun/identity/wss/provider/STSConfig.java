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
 * $Id: STSConfig.java,v 1.3 2008-06-25 05:50:05 qcheng Exp $
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
}

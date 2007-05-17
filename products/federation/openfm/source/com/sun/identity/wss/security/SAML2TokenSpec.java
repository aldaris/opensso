/* The contents of this file are subject to the terms
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
 * $Id: SAML2TokenSpec.java,v 1.1 2007-05-17 18:49:17 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.security;

import com.sun.identity.saml2.assertion.NameID;

/**
 * This class implements the interface <code>SecurityTokenSpec</code to
 * create <code>SAML2</code> Assertions.
 */
public class SAML2TokenSpec implements SecurityTokenSpec {
    
    private SecurityMechanism securityMechanism = null;
    private String certAlias = null;
    private NameID nameIdentifier = null;
    
   /**
    * Creates a new instance of SAML2TokenSpec
    */
    public SAML2TokenSpec(NameID nameIdentifier, 
                SecurityMechanism securityMechanism, 
                String certAlias) {

        this.nameIdentifier = nameIdentifier;
        this.securityMechanism = securityMechanism;
        this.certAlias = certAlias;
    }
    
    /**
     * Returns the authenticated subject name identifier.
     *
     * @return the name identifier of the authenticated subject.
     */
    public NameID getSenderIdentity() {
        return nameIdentifier;
    } 

    /**
     * Returns the security mechanism
     * @return the security mechanism
     */
    public SecurityMechanism getSecurityMechanism() {
        return securityMechanism;
    }

    /**
     * Returns the certficate alias of the subject.
     *
     * @return the certificate alias of the subject.
     */
    public String getSubjectCertAlias() {
        return certAlias;
    }    
    
}

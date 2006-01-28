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
 * $Id: CRLValidator.java,v 1.1 2006-01-28 09:28:36 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security.cert;

import java.io.IOException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import com.iplanet.am.util.Debug;
import com.iplanet.security.x509.X500Name;
import com.sun.identity.security.SecurityDebug;

/**
 * This interface is for <code>CRLValidator</code> that is representing
 * configued <code>X509CRLValidator</code> 
 */
public abstract class CRLValidator {
    private AMLDAPCertStoreParameters ldapParams = null;

    private static Debug debug = SecurityDebug.debug;
    
    /**
     * Validate certificate against configured crl
     * @param cert cert to be validated 
     * @param chkCRLAttr ldap attribute name to get crl from ldap crlstore
     * @return true if certificate is not in crl
     */
    abstract public boolean validateCertificate(X509Certificate cert, 
                                                String chkCRLAttr);
    
    /**
     * Get certificate revocation list from cofigured ldap store
     * @param cert cert to be validated 
     * @param chkCRLAttr ldap attribute name to get crl from ldap crlstore
     * @return crl if ldap store configured with crl
     */
    public X509CRL getCRL(X509Certificate cert, String chkCRLAttr) {
        X509CRL crl = null;
            /*
         * Get the CN of the input certificate
         */
        String attrValue = null;
        
        try {
            X500Name dn = AMCRLStore.getIssuerDN(cert);
            // Retrieve attribute value of amAuthCert_chkAttrCertInLDAP
            if (dn != null) {
                attrValue = dn.getAttributeValue(chkCRLAttr);
            }
        } catch (Exception ex) {
            debug.error("attrValue to search crl : " + attrValue, ex); 
            return null;
        }

        if ((attrValue == null) || (ldapParams == null))
            return null;
        
        if (debug.messageEnabled()) {
            debug.message("CRLValidator - " +
                              "attrValue to search crl : " + attrValue); 
        }

        /*
         * Lookup the certificate in the LDAP certificate
         * directory and compare the values.
         */ 
        String searchFilter = AMCRLStore.setSearchFilter(chkCRLAttr, attrValue);
        ldapParams.setSearchFilter(searchFilter);
        try {
            AMCRLStore store = new AMCRLStore(ldapParams);
            crl = store.getCRL(cert);
        } catch (IOException e) {
            debug.error("X509Certificate: verifyCertificate." + e.toString());
        }
                
        return crl; 
    }

    /**
     * Get certificate revocation list from cofigured ldap store
     * @param params ldap parameters to ldap crl store 
     */
    public void setLdapParams(AMLDAPCertStoreParameters params) {
        ldapParams = params;
    }
}

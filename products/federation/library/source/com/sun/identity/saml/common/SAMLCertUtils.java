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
 * $Id: SAMLCertUtils.java,v 1.1 2006-10-30 23:15:41 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.saml.common;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

import com.sun.identity.saml.xmlsig.JKSKeyProvider;
 
/**
 * This class finds any matching cert in the jks store. 
 */
public class SAMLCertUtils { 

    private static Map certdbCerts =
        Collections.synchronizedMap(new HashMap());
    static {
        if (SAMLUtils.debug.messageEnabled()) {
            SAMLUtils.debug.message(
                "Start loading certs from jks key store");
        }
        JKSKeyProvider kp = new JKSKeyProvider();
        if (kp != null) {
            KeyStore ks = kp.getKeyStore();
            if (ks != null) {
                try {
                    Enumeration e = ks.aliases();
                    while (e.hasMoreElements()) {
                        String alias = (String) e.nextElement ();
                        X509Certificate cert =
                            (X509Certificate)ks.getCertificate(alias);
                        CertEntry certEntry = new CertEntry(
                            alias,
                            cert.getIssuerDN(), 
                            cert.getSerialNumber()
                        );                 
                        certdbCerts.put(alias,certEntry);
                    }
                } catch (KeyStoreException kes) {
                    SAMLUtils.debug.error(
                        "Key store has problem.", kes);
                }
            }
        }
    }
    
    /** This class contains the mapping between a 
    * java.security.cert.X509Certificate's nickname and its related Data.
    */
    public static class CertEntry {
        private String nickName = null; 
        private java.security.Principal issuerDN = null;
        private BigInteger serialNumber;

        /**
         * Constructor.
         * @param nickName nick name of the certificate.
         * @param issuerDN Principal name of the certificate.
         * @param serialNumber serial number of the certificate.
         */
        public CertEntry(String nickName, 
            java.security.Principal issuerDN, BigInteger serialNumber) 
        {
            this.nickName = nickName;
            this.issuerDN = issuerDN;
            this.serialNumber = serialNumber;
        }

        /**
         * Returns nick name of the certificate.
         * @return String which is nick name of the certificate.
         */
        public String getNickName() {
            return nickName;
        }

        /**
         * Returns issuer DN of the certificate.
         * @return Principal which is issuer DN of the certificate.
         */
        public java.security.Principal getIssuerDN() {
            return issuerDN;
        }

        /**
         * Returns serial number of the certificate.
         * @return BigInteger which is serial number of the certificate.
         */
        public BigInteger getSerialNumber() {
            return serialNumber;
        }
    }
    
    /**
     * Finds matching certicate from internal certificate database.
     * @param inCert <code>X509Certificate</code> to be matched.
     * @return CertEntry which is the matching certificate entry.
     */
    public static CertEntry getMatchingCertEntry(
        java.security.cert.X509Certificate inCert) 
    {
        String inCertName = inCert.getIssuerDN().getName().trim();
        BigInteger inCertSerialNumber = inCert.getSerialNumber();

        Iterator it = certdbCerts.entrySet().iterator();
        if (SAMLUtils.debug.messageEnabled()) {
            SAMLUtils.debug.message("Found "+certdbCerts.entrySet().size()
                                    +" jks certs");
        }
        while (it.hasNext()) {
            CertEntry certEntry = (CertEntry)
                ((Map.Entry)it.next()).getValue();
            if (inCertName.equals(certEntry.getIssuerDN().getName()) && 
                (inCertSerialNumber.equals(certEntry.getSerialNumber())))
            {
                if (SAMLUtils.debug.messageEnabled()) {
                    SAMLUtils.debug.message("Matching cert found.");
                }
                return certEntry;
            }
        }
        // if reached here, then no matching entry found
        if (SAMLUtils.debug.messageEnabled()) {
            SAMLUtils.debug.message("Matching cert not found.");
        }
        return null;
    }
 }

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
 * $Id: CertId.java,v 1.1 2007-10-22 15:06:31 beomsuk Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security.cert;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.x509.*;
import sun.security.util.*;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.security.SecurityDebug;

/**
 * This class corresponds to the CertId field in OCSP Request 
 * and the OCSP Response. The ASN.1 definition for CertID is defined
 * in RFC 2560 as:
 * <pre>
 *
 * CertID          ::=     SEQUENCE {
 *      hashAlgorithm       AlgorithmIdentifier,
 *      issuerNameHash      OCTET STRING, -- Hash of Issuer's DN
 *      issuerKeyHash       OCTET STRING, -- Hash of Issuers public key
 *      serialNumber        CertificateSerialNumber 
 *        }
 * 
 * </pre>
 *
 * @version         1.2 12/19/03
 * @author        Ram Marti
 */

public class CertId {

    private static final Debug debug = SecurityDebug.debug;
    private AlgorithmId hashAlgId;
    private byte[] issuerNameHash;
    private byte[] issuerKeyHash;
    private SerialNumber certSerialNumber;
    private int myhash = -1; // hashcode for this CertId

    /**
     * Creates a CertId. The hash algorithm used is SHA-1. 
     */
    public CertId(X509CertImpl issuerCert, SerialNumber serialNumber) 
        throws Exception {

        // compute issuerNameHash
        MessageDigest md = MessageDigest.getInstance("SHA1");
        hashAlgId = AlgorithmId.get("SHA1");
        md.update(issuerCert.getIssuerX500Principal().getEncoded());
        issuerNameHash = md.digest();

        // compute issuerKeyHash (remove the tag and length)
        byte[] pubKey = issuerCert.getPublicKey().getEncoded();
        DerValue val = new DerValue(pubKey);
        DerValue[] seq = new DerValue[2];        
        seq[0] = val.data.getDerValue(); // AlgorithmID
        seq[1] = val.data.getDerValue(); // Key
        byte[] keyBytes = seq[1].getBitString();
        md.update(keyBytes);
        issuerKeyHash = md.digest();
        certSerialNumber = serialNumber;

        if (debug.messageEnabled()) {
            HexDumpEncoder encoder = new HexDumpEncoder();
            debug.message("CertId.CertId: Issuer Certificate is " + issuerCert+
                          "\nissuerNameHash is " +
                          encoder.encode(issuerNameHash) +
                          "\nissuerKeyHash is " +encoder.encode(issuerKeyHash));
        }
    }

    /**
     * Creates a CertId from its ASN.1 DER encoding.
     */
    public CertId(DerInputStream derIn) throws IOException {

        hashAlgId = AlgorithmId.parse(derIn.getDerValue());
        issuerNameHash = derIn.getOctetString();
        issuerKeyHash = derIn.getOctetString();
        certSerialNumber = new SerialNumber(derIn);
    }
        
    /**
     * Return the hash algorithm identifier.
     */
    public AlgorithmId getHashAlgorithm() {
        return hashAlgId;
    }

    /**
     * Return the hash value for the issuer name.
     */
    public byte[] getIssuerNameHash() {
        return issuerNameHash;
    }

    /**
     * Return the hash value for the issuer key.
     */
    public byte[] getIssuerKeyHash() {
        return issuerKeyHash;
    }

    /**
     * Return the serial number.
     */
    public BigInteger getSerialNumber() {
        return certSerialNumber.getNumber();
    }

    /**
     * Encode the CertId using ASN.1 DER.
     * The hash algorithm used is SHA-1.
     */
    public void encode(DerOutputStream out) throws IOException {

        DerOutputStream tmp = new DerOutputStream();
        hashAlgId.encode(tmp);
        tmp.putOctetString(issuerNameHash);
        tmp.putOctetString(issuerKeyHash);
        certSerialNumber.encode(tmp);
        out.write(DerValue.tag_Sequence, tmp);

        if (debug.messageEnabled()) {
            HexDumpEncoder encoder = new HexDumpEncoder();
            debug.message("CertId.encode: Encoded certId is " +
                          encoder.encode(out.toByteArray()));
        }
    }

   /**
     * Returns a hashcode value for this CertId.
     *
     * @return the hashcode value.
     */
    public int hashCode() {
        if (myhash == -1) {
            myhash = hashAlgId.hashCode();
            for (int i = 0; i < issuerNameHash.length; i++) {
                myhash += issuerNameHash[i] * i;
            }
            for (int i = 0; i < issuerKeyHash.length; i++) {
                myhash += issuerKeyHash[i] * i;
            }
            myhash += certSerialNumber.getNumber().hashCode();
        }
        return myhash;
    }

    /**
     * Compares this CertId for equality with the specified
     * object. Two CertId objects are considered equal if their hash algorithms,
     * their issuer name and issuer key hash values and their serial numbers 
     * are equal.
     *
     * @param other the object to test for equality with this object.
     * @return true if the objects are considered equal, false otherwise.
     */
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (other == null || (!(other instanceof CertId))) {
            return false;
        }

        CertId that = (CertId) other;
        if (hashAlgId.equals(that.getHashAlgorithm()) &&
            Arrays.equals(issuerNameHash, that.getIssuerNameHash()) &&
            Arrays.equals(issuerKeyHash, that.getIssuerKeyHash()) &&
            certSerialNumber.getNumber().equals(that.getSerialNumber())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Create a string representation of the CertId.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CertId \n");
        sb.append("Algorithm: " + hashAlgId.toString() +"\n");
        sb.append("issuerNameHash \n");
        HexDumpEncoder encoder = new HexDumpEncoder();
        sb.append(encoder.encode(issuerNameHash));
        sb.append("\nissuerKeyHash: \n");
        sb.append(encoder.encode(issuerKeyHash));
        sb.append("\n" +  certSerialNumber.toString());
        return sb.toString();
    }
}

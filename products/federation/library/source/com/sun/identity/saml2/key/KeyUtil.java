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
 * $Id: KeyUtil.java,v 1.1 2006-10-30 23:16:19 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.key;

import java.util.Map;
import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;

import com.sun.identity.common.SystemConfigurationUtil;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.saml2.jaxb.metadata.SSODescriptorType;
import com.sun.identity.saml2.jaxb.metadata.KeyDescriptorType;
import com.sun.identity.saml2.jaxb.xmlsig.*;
import com.sun.identity.saml2.jaxb.xmlenc.*;

import com.sun.identity.saml.common.SAMLConstants;
import com.sun.identity.saml.common.SAMLUtils;
import com.sun.identity.saml.xmlsig.KeyProvider;

/**
 * The <code>KeyUtil</code> provides methods to obtain
 * the hosting entity's signing key and decryption key, and
 * to obtain a partner entity's signature verification key
 * and encryption related information
 */
public class KeyUtil {
    
    private static KeyProvider kp = null;

    // key is EntityID|Role
    // value is EncInfo
    protected static Hashtable encHash = new Hashtable();

    // key is EntityID|Role
    // value is X509Certificate
    protected static Hashtable sigHash = new Hashtable();
    
    static {
        try {
            kp = (KeyProvider)Class.forName(SystemConfigurationUtil.getProperty(
                SAMLConstants.KEY_PROVIDER_IMPL_CLASS)).newInstance();
        } catch (ClassNotFoundException cnfe) {
            SAML2Utils.debug.error(
                "KeyUtil static block:" +
                " Couldn't find the class.",
                cnfe);
            kp = null;
        } catch (InstantiationException ie) {
            SAML2Utils.debug.error(
                "KeyUtil static block:" +
                " Couldn't instantiate the key provider instance.",
                ie);
            kp = null;
        } catch (IllegalAccessException iae) {
            SAML2Utils.debug.error(
                "KeyUtil static block:" +
                " Couldn't access the default constructor.",
                iae);
            kp = null;
        }            
    }

    private KeyUtil() {
    }

    /**
     * Returns the instance of <code>KeyProvider</code>.
     * @return <code>KeyProvider</code>
     */
    public static KeyProvider getKeyProviderInstance() {
        return kp;
    }

    /**
     * Returns the host entity's signing certificate alias.
     * @param baseConfig <code>BaseConfigType</code> for the host entity
     * @return <code>String</code> for host entity's signing
     * certificate alias
     */    
    public static String getSigningCertAlias(BaseConfigType baseConfig) {

        Map map = SAML2MetaUtils.getAttributes(baseConfig);
        List list = (List)map.get(SAML2Constants.SIGNING_CERT_ALIAS);
        if (list != null && !list.isEmpty()) {
            String alias = (String)list.get(0);
            if (alias != null && alias.length() != 0 && kp != null) {
                return alias;
            }
        }
        return null;
    }

    /**
     * Returns the host entity's decryption key.
     * @param baseConfig <code>BaseConfigType</code> for the host entity
     * @return <code>PrivateKey</code> for decrypting a message received
     * by the host entity
     */    
    public static PrivateKey getDecryptionKey(BaseConfigType baseConfig) {

        Map map = SAML2MetaUtils.getAttributes(baseConfig);
        List list = (List)map.get(SAML2Constants.ENCRYPTION_CERT_ALIAS);
        PrivateKey decryptionKey = null;
        if (list != null && !list.isEmpty()) {
            String alias = (String)list.get(0);
            if (alias != null && alias.length() != 0 && kp != null) {
                decryptionKey = kp.getPrivateKey(alias);
            }
        }
        return decryptionKey;
    }

    /**
     * Returns the partner entity's signature verification certificate.
     * @param ssod <code>SSODescriptor</code> for the partner entity
     * @param entityID partner entity's ID
     * @param isIDP whether partner entity's role is IDP or SP 
     * @return <code>X509Certificate</code> for verifying the partner
     * entity's signature
     */    
    public static X509Certificate getVerificationCert(
        SSODescriptorType ssod,
        String entityID,
        boolean isIDP
    ) {
        
        String classMethod = "KeyUtil.getVerificationCert: ";
        String role = (isIDP) ? "idp":"sp";        
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                classMethod +
                "Entering... \nEntityID=" +
                entityID + "\nRole="+role
            );
        }
        // first try to get it from cache
        String index = entityID.trim()+"|"+ role;
        X509Certificate cert = (X509Certificate)sigHash.get(index);
        if (cert != null) {
            return cert;
        }
        // else get it from meta
        if (ssod == null) {
            SAML2Utils.debug.error(
                classMethod+
                "Null SSODescriptorType input for entityID=" +
                entityID + " in "+role+" role."
            );
            return null;
        }
        KeyDescriptorType kd =
            getKeyDescriptor(ssod, "signing");
        if (kd == null) {
            SAML2Utils.debug.error(
                classMethod+
                "No signing KeyDescriptor for entityID=" +
                entityID + " in "+role+" role."
            );
            return null;
        }
        cert = getCert(kd);
        if (cert == null) {
            SAML2Utils.debug.error(
                classMethod +
                "No signing cert for entityID=" +
                entityID + " in "+role+" role."
            );
            return null;
        }
        sigHash.put(index, cert);
        return cert;
    }
    
    /**
     * Returns the encryption information which will be used in
     * encrypting messages intended for the partner entity.
     * @param ssod <code>SSODescriptor</code> for the partner entity
     * @param entityID partner entity's ID
     * @param isIDP whether partner entity's role is IDP or SP 
     * @return <code>EncInfo</code> which includes partner entity's
     * public key for wrapping the secret key, data encryption algorithm,
     * and data encryption strength 
     */        
    public static EncInfo getEncInfo(
        SSODescriptorType ssod,
        String entityID,
        boolean isIDP
    ) {

        String classMethod = "KeyUtil.getEncInfo: ";
        String role = (isIDP) ? "idp":"sp";                
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message(
                classMethod +
                "Entering... \nEntityID=" +
                entityID + "\nRole="+role
            );
        }
        // first try to get it from cache
        String index = entityID.trim()+"|"+role;
        EncInfo encInfo = (EncInfo)encHash.get(index);
        if (encInfo != null) {
            return encInfo;
        }
        // else get it from meta
        if (ssod == null) {
            SAML2Utils.debug.error(
                classMethod+
                "Null SSODescriptorType input for entityID=" +
                entityID + " in "+role+" role."
            );
            return null;
        }
        KeyDescriptorType kd =
            getKeyDescriptor(ssod, "encryption");
        if (kd == null) {
            SAML2Utils.debug.error(
                classMethod+
                "No encryption KeyDescriptor for entityID=" +
                entityID + " in "+role+" role."
            );
            return null;
        }
        java.security.cert.X509Certificate cert = getCert(kd);
        if (cert == null) {
            SAML2Utils.debug.error(
                classMethod +
                "No encryption cert for entityID=" +
                entityID + " in "+role+" role."
            );
            return null;
        }
        List emList = kd.getEncryptionMethod();
        EncryptionMethodType em = null;
        String algorithm = null;
        int keySize = 0;
        if (emList != null && !emList.isEmpty()) {            
            em = (EncryptionMethodType)emList.get(0);
            if (em != null) {
                algorithm = em.getAlgorithm();
                List cList = em.getContent();
                if (cList != null) {
                    keySize =
                        ((EncryptionMethodType.KeySize)(cList.get(0))).
                        getValue().intValue();
                }
            }
        }
        if (algorithm == null || algorithm.length() == 0) {
            algorithm = XMLCipher.AES_128;
            keySize = 128;
        }
        PublicKey pk = cert.getPublicKey();
        if (pk != null) {
            encInfo = new EncInfo(pk, algorithm, keySize);
        }
        if (encInfo != null) {
            encHash.put(index, encInfo);
        }
        return encInfo;
    }


    /**
     * Returns <code>KeyDescriptorType</code> from 
     * <code>SSODescriptorType</code>.
     * @param ssod <code>SSODescriptorType</code> which contains
     *                <code>KeyDescriptor</code>s.
     * @param usage type of the <code>KeyDescriptorType</code> to be retrieved.
     *                Its value is "encryption" or "signing".
     * @return KeyDescriptorType in <code>SSODescriptorType</code> that matched
     *                the usage type.
     */
    public static KeyDescriptorType getKeyDescriptor(
        SSODescriptorType ssod,
        String usage
    ) {
        
        List list = ssod.getKeyDescriptor();
        Iterator iter = list.iterator();
        KeyDescriptorType kd = null;
        String use = null;
        KeyDescriptorType noUsageKD = null;
        while (iter.hasNext()) {
            kd = (KeyDescriptorType)iter.next();
            use = kd.getUse();
            if ((use == null) || (use.trim().length() == 0)) {
		if (noUsageKD == null) {
                    noUsageKD = kd;
                    continue;
                }
            }
            if (use.trim().toLowerCase().equals(usage)) {
                break;
            } else {
                kd = null;
            }
        }
        if (kd != null) {
            return kd;
        } else {
            return noUsageKD;
        }
    }

    /**
     * Returns certificate stored in <code>KeyDescriptorType</code>.
     * @param kd <code>KeyDescriptorType</code> which contains certificate info
     * @return X509Certificate contained in <code>KeyDescriptorType</code>; or
     *                <code>null</code> if no certificate is included.
     */
    public static java.security.cert.X509Certificate getCert(
        KeyDescriptorType kd
    ) {

        String classMethod = "KeyUtil.getCert: ";
        KeyInfoType ki = kd.getKeyInfo();
        if (ki == null) {
            SAML2Utils.debug.error(classMethod +
                                   "No KeyInfo.");
            
            return null;
        }
        X509DataElement data = (X509DataElement) ki.getContent().get(0);
        byte[] bt = 
            ((com.sun.identity.saml2.jaxb.xmlsig.X509DataType.X509Certificate)
             data.getX509IssuerSerialOrX509SKIOrX509SubjectName().get(0)).
            getValue();
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (java.security.cert.CertificateException ce) {
            SAML2Utils.debug.error(
                classMethod +
                "Unable to get CertificateFactory "+
                "for X.509 type", ce);
            return null;
        }                
        ByteArrayInputStream bais = new ByteArrayInputStream(bt);
        java.security.cert.X509Certificate retCert = null;
        try {
            while (bais.available() > 0) {
                retCert = (java.security.cert.X509Certificate) 
                    cf.generateCertificate(bais);
            }
        } catch (java.security.cert.CertificateException ce) {
            SAML2Utils.debug.error(
                classMethod +
                "Unable to generate certificate from byte "+
                "array input stream.", ce);
            return null;
        }
        return retCert;
    }        
} 

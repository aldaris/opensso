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
 * $Id: AMTokenProvider.java,v 1.1 2007-03-23 00:01:55 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.wss.security;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOTokenManager;
import com.iplanet.am.util.SystemProperties;
import com.sun.identity.shared.debug.Debug;

import com.sun.identity.saml.xmlsig.XMLSignatureManager;
import com.sun.identity.saml.xmlsig.KeyProvider;
import com.sun.identity.saml.xmlsig.JKSKeyProvider;
import com.sun.identity.shared.Constants;

import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.
           XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

/**
 * This class implements the <code>TokenProvider</code> interface to
 * generate the WS-Security tokens.
 *
 * <p>This token provider is the default implementation which uses
 * the default <code>XMLSignatureManager</code> and 
 * the <code>KeyProvider</code> for token generation.
 */
public class AMTokenProvider implements TokenProvider {

     private SSOToken ssoToken = null;
     private SecurityTokenSpec tokenSpec = null;
     private static XMLSignatureManager sigManager = null;
     private static KeyProvider keyProvider = null; 
     private static Debug debug = WSSUtils.debug;
     private static ResourceBundle bundle = WSSUtils.bundle;

     /**
      * Default constructor
      *
      * @param token Single sign-on token
      *
      * @exception SSOException if the single sign on token is invalid.
      */
     public AMTokenProvider(SSOToken token) throws SSOException {

         SSOTokenManager.getInstance().validateToken(token);
         this.ssoToken = token;         
         sigManager = WSSUtils.getXMLSignatureManager();
         keyProvider = sigManager.getKeyProvider();
     }

    /**
     * Initialize the <code>AMTokenProvider</code> to generate the necessary
     * security token. 
     *
     * @param tokenSpec the token specification required to generate the
     *        security token. 
     */
    public void init(SecurityTokenSpec tokenSpec) {
        this.tokenSpec = tokenSpec;
    }

    /**
     * Returns the <code>SecurityToken</code> for the WS-Security.
     *
     * @return the security token for the initialzed security token.
     *
     * @exception SecurityException if unable to generate the security token
     *             for the initialized token specification.
     */
    public  SecurityToken getSecurityToken() throws SecurityException {

        if(tokenSpec == null) {
           throw new SecurityException(
                 WSSUtils.bundle.getString("tokenSpecNotSpecified"));
        }
        if(tokenSpec instanceof AssertionTokenSpec) {
           AssertionToken assertionToken = new AssertionToken(
                    (AssertionTokenSpec)tokenSpec, ssoToken);

           // Currently it reads the same SAML Authority until the STS
           // service is ready.
           String trustAlias = SystemProperties.get(
              Constants.SAML_XMLSIG_CERT_ALIAS);

           assertionToken.sign(trustAlias);
           return assertionToken; 

        } else if(tokenSpec instanceof X509TokenSpec) {
           return  new BinarySecurityToken(
                  (X509TokenSpec)tokenSpec);

        } else if(tokenSpec instanceof UserNameTokenSpec) {
           return new UserNameToken((UserNameTokenSpec)tokenSpec);

        } else {
           debug.error("AMTokenProvider.getSecurityToken:: unsupported token" +
           " specification");
           throw new SecurityException(
               bundle.getString("unsupportedTokenSpec"));
        }

    }

    /**
     * Returns the instance of key provider to issue the tokens. 
     */
    static KeyProvider getKeyProvider() {
        return keyProvider;
    }

    /**
     * Returns the instance of XMLSignatureManager to sign/verify the tokens.
     */
    static XMLSignatureManager getSignatureManager() {
        return sigManager;
    }

    /**
     * Returns the <code>X509Certificate</code> for a given certificate
     * alias.
     */
    static X509Certificate getX509Certificate(String alias) {
         return keyProvider.getX509Certificate(alias);
    }

    /**
     * Returns list of <code>X509Certificate</code>s for the string
     * array of certificate aliases.
     */
    static List getX509Certificates(String[] alias) {
        List list = new ArrayList();
        for(int i= 0; i < alias.length; i++) {
            String aliasName = alias[i];
            X509Certificate cert = getX509Certificate(aliasName);
            if(cert != null) {
               list.add(alias);
            }
        }
        return list;
    }

    /**
     * Returns the <code>X509Certificate</code> for the given input stream.
     */
    static X509Certificate loadCertificate(InputStream in) 
                    throws SecurityException{
        X509Certificate cert = null;
        try {
            cert = 
              (X509Certificate) getCertificateFactory().generateCertificate(in);
        } catch(CertificateException ce) {
            debug.error("AMTokenProvider.loadCertificate:: failed to load" +
            " certificate", ce);
        }
        return cert;
    }

    /**
     * Returns the certificate factory.
     */
    static CertificateFactory getCertificateFactory() throws SecurityException {
        try {
            return CertificateFactory.getInstance("X.509");
        } catch (CertificateException ce) {
            throw new SecurityException(ce.getMessage());
        }
    }

    /**
     * Returns the <code>X509Certificate</code> for the given 
     * <code>X509Data</code>.
     */
    static X509Certificate getX509Certificate(X509Data x509Data) 
             throws SecurityException {

        String issuerName = null;
        BigInteger serialNumber = null;
        try {
            XMLX509IssuerSerial issuerSerial = x509Data.itemIssuerSerial(0);
            issuerName = issuerSerial.getIssuerName();
            serialNumber = issuerSerial.getSerialNumber();
        } catch (XMLSecurityException xe) {
            throw new SecurityException(xe.getMessage());
        }

        X509Certificate x509cert = null;
        Certificate cert = null;

        try {
            JKSKeyProvider jksProvider = (JKSKeyProvider)keyProvider;
            KeyStore keystore = jksProvider.getKeyStore();
            for (Enumeration e = keystore.aliases(); e.hasMoreElements();) {
                String alias = (String) e.nextElement();
                Certificate[] certs = keystore.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    cert = keystore.getCertificate(alias);
                    if (cert == null) {
                        continue;
                    }
                } else {
                    cert = certs[0];
                }
                if (!(cert instanceof X509Certificate)) {
                    continue;
                }
                x509cert = (X509Certificate) cert;
                if (x509cert.getSerialNumber().compareTo(serialNumber) == 0) {
                    String certDN = x509cert.getIssuerDN().getName();
                    if (certDN.equals(issuerName)) {
                        return x509cert;
                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new SecurityException(
                bundle.getString("keystoreException"));
        }
        return null;
        
    }
    
}

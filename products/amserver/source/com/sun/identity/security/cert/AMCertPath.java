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
 * $Id: AMCertPath.java,v 1.2 2006-05-31 21:50:10 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security.cert;

import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.iplanet.am.util.Debug;
import com.sun.identity.security.SecurityDebug;

/**
 * Class AMCertPath is special cased Certpath validation class.
 * It does cert path validation together with CRL check and ocsp checking 
 * if they are properly configured.
 */

public class AMCertPath {

    // enable OCSP or not
    private static String OCSP_ENABLE= null;

    // If OCSP_RESPONDER_URL is not set, provider should use AIA in ee cert
    private static String OCSP_RESPONDER_URL= null;

    // RESPONDER_CERT_SUBJECT_NAME,
    // OCSP_RESPONDER_CERT_ISSUER_NAME and OCSP_RESPONDER_CERT_SERIAL_NUMBER
    // can be set as system property. Responder's certificate has to have
    // these value.
    private static String OCSP_RESPONDER_CERT_SUBJECT_NAME = null;
    private static String OCSP_RESPONDER_CERT_ISSUER_NAME = null;
    private static String OCSP_RESPONDER_CERT_SERIAL_NUMBER = null;
    private static CertificateFactory cf = null;
    private static CertPathValidator cpv = null;
    private CertStore store = null;
    public static Debug debug = SecurityDebug.debug;
    
    static {
            /**
             * Get jvm security option setting for OCSP checking.
             */
            OCSP_ENABLE = Security.getProperty("ocsp.enable");
            OCSP_RESPONDER_URL = Security.getProperty("ocsp.responderURL");
            OCSP_RESPONDER_CERT_SUBJECT_NAME = 
                    Security.getProperty("ocsp.responderCertSubjectName");
            OCSP_RESPONDER_CERT_ISSUER_NAME = 
                    Security.getProperty("ocsp.responderCertIssuerName");
            OCSP_RESPONDER_CERT_SERIAL_NUMBER = 
                    Security.getProperty("ocsp.responderCertSerialNumber");
            
            try {
                cf= CertificateFactory.getInstance("X509");
            cpv= CertPathValidator.getInstance("PKIX");
            } catch (Exception e) {
                    debug.error("" + e.toString());
            }
    }

    /**
     * Class constructor
     * param Vector crls
     */
    public AMCertPath(Vector crls) 
         throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        if (debug.messageEnabled()) {
            X509CRL crl = (X509CRL) crls.elementAt(0);
            if (crl != null) {
                debug.message("" + crl.toString());
            }
        }
                
        if ((crls != null) && (crls.size() > 0)) {
            CollectionCertStoreParameters collection = 
                                new CollectionCertStoreParameters(crls);
            store = CertStore.getInstance("Collection", collection);
        }
    }

    private void printOCSPConfig() {
        if (debug.messageEnabled()) {
            debug.message("OCSP_ENABLE :" + OCSP_ENABLE);
            debug.message("OCSP_RESPONDER_URL:" + OCSP_RESPONDER_URL);
            debug.message("OCSP_RESPONDER_CERT_SUBJECT_NAME :" 
                                      + OCSP_RESPONDER_CERT_SUBJECT_NAME);
            debug.message("OCSP_RESPONDER_CERT_ISSUER_NAME :" 
                                      + OCSP_RESPONDER_CERT_ISSUER_NAME);
            debug.message("OCSP_RESPONDER_CERT_SERIAL_NUMBER :" 
                                    + OCSP_RESPONDER_CERT_SERIAL_NUMBER);
        }
    }
        
    /**
     * It does cert path validation together with CRL check and ocsp checking 
     * if they are properly configured.
     * @param X509Certificate[] certs
     **/
    public boolean velify(X509Certificate[] certs) {
        try {
                   printOCSPConfig();

            List certList = Arrays.asList(certs);
            CertPath cp= (CertPath) cf.generateCertPath(certList);

            // init PKIXParameters
            Class trustMgrClass = Class.forName(
                  "com.sun.identity.security.keystore.AMX509TrustManager");
            Object trustMgr = (Object) trustMgrClass.newInstance();
            Method method = trustMgrClass.getMethod(
                "getKeyStore", (Class)null);
            KeyStore keystore = (KeyStore) method.invoke(
                trustMgr, (Object[])null);
            PKIXParameters pkixparams= new PKIXParameters(keystore);
            if (store != null) {
                    pkixparams.addCertStore(store);
            }
            
            // validate
            CertPathValidatorResult cpvResult= cpv.validate(cp, pkixparams);

            if (debug.messageEnabled()) {
                debug.message("VALIDATE_RESULT: PASS" + cpvResult.toString());
            }
        }
        catch (Exception e) {
                debug.message("VALIDATE_RESULT: FAILED");
                debug.message("" + e.toString());
            return false;
        }

        return true;
    }
}

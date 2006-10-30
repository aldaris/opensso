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
 * $Id: IDFFMetaSecurityUtils.java,v 1.1 2006-10-30 23:14:17 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.federation.meta;

import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.encode.Base64;

import com.sun.identity.saml.xmlsig.JKSKeyProvider;
import com.sun.identity.saml.xmlsig.KeyProvider;

import com.sun.identity.federation.key.KeyUtil;

/**
 * The <code>IDFFMetaSecurityUtils</code> class provides metadata security 
 * related utility functions.
 */
public final class IDFFMetaSecurityUtils {

    public static final String NS_XMLSIG = "http://www.w3.org/2000/09/xmldsig#";    public static final String NS_XMLENC = "http://www.w3.org/2001/04/xmlenc#";

    private static Debug debug = IDFFMetaUtils.debug;
    private static KeyProvider keyProvider = null;
    private static KeyStore keyStore = null;
    private static boolean keyProviderInitialized = false;

    private IDFFMetaSecurityUtils() {

    }

    private static synchronized void initializeKeyStore() {
        if (keyProviderInitialized) {
            return;
        }

        com.sun.org.apache.xml.internal.security.Init.init();

        keyProvider = KeyUtil.getKeyProviderInstance();
        if (keyProvider instanceof JKSKeyProvider) {
            keyStore = ((JKSKeyProvider)keyProvider).getKeyStore();
        }

        keyProviderInitialized = true;
    }

    /**
     * Returns BASE64 encoded X509 Certificate string corresponding to the 
     * certificate alias.
     * @param certAlias Alias of the Certificate to be retrieved.
     * @return BASE64 encoded X509 Certificate string, return null if null
     *    or empty certificate alias is specified.
     * @throws IDFFMetaException if unable to retrieve the certificate from the
     *     internal key store.
     */
    public static String buildX509Certificate(String certAlias)
        throws IDFFMetaException
    {
        if ((certAlias == null) || (certAlias.trim().length() == 0)) {
            return null;
        }

        if (!keyProviderInitialized) {
            initializeKeyStore();
        }

        X509Certificate cert = keyProvider.getX509Certificate(certAlias);

        if (cert != null) {
            try {
                return Base64.encode(cert.getEncoded(), 76);
            } catch (Exception ex) {
                if (debug.messageEnabled()) {
                    debug.message(
                          "IDFFMetaSecurityUtils.buildX509Certificate:", ex);
                }
            }
        }

        Object[] objs = { certAlias };
        throw new IDFFMetaException("invalid_cert_alias", objs);
    }
}

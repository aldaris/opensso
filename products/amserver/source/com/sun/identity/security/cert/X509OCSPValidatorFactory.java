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
 * $Id: X509OCSPValidatorFactory.java,v 1.2 2006-08-25 21:21:18 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security.cert;

import com.sun.identity.shared.debug.Debug;
import com.iplanet.am.util.SystemProperties;
import com.sun.identity.shared.Constants;
import com.sun.identity.security.SecurityDebug;

/**
 * This is the factory class is to obtain object instances of 
 * <code>OCSPValidator</code>
 */
public class X509OCSPValidatorFactory {
    private static X509OCSPValidatorFactory instance = null; 
    private static String provider = null;
    private static Class validatorClass = null;
    private static Debug debug = SecurityDebug.debug;
    
    static {
        /* 
         * Retrieve configured X.509 Security provider package
         * Default security provider pkg is com.sun.identity.security.x509.impl
         */ 
        provider = SystemProperties.get(Constants.SECURITY_PROVIDER_PKG, 
                                        Constants.SUN_SECURITY_PROVIDER_PKG);
        instance = new X509OCSPValidatorFactory();
    }

    /* Constructor for X509OCSPValidatorFactory */
    private X509OCSPValidatorFactory() {
    }
            
    private static void loadValidatorClass() {
        String className = provider+".X509OCSPValidatorImpl";
        try {
            validatorClass = Class.forName(className, 
                                     false, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            debug.error("X509OCSPValidatorFactory : class load failed.", e);
        }
    }
    
    /**
     * Returns an instance of the <code>X509CRLValidatorFactory</code> Object.
     *
     * @return an instance of the <code>X509CRLValidatorFactory</code> object.
     */
    public static X509OCSPValidatorFactory getInstance() {
        return instance;
    }
        
    /**
     * Returns an instance of the <code>OCSPValidator</code> Object.
     *
     * @return an instance of the <code>OCSPValidator</code> object.
     */
    public OCSPValidator createOCSPValidator() {
        if (validatorClass == null) {
            loadValidatorClass();
        }
        
        if (validatorClass != null) {
            try {
                return (OCSPValidator) validatorClass.newInstance();
            } catch (Exception e) {
                debug.error("X509OCSPValidatorFactory : " +
                            "class instantiation failed.", e);
            }
        }
                        
        return null;
    }
}

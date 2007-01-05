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
 * $Id: X509CRLExtensionFactory.java,v 1.3 2007-01-05 23:43:15 beomsuk Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.security.x509;

import com.iplanet.am.util.SystemProperties;
import com.sun.identity.security.SecurityDebug;
import com.sun.identity.shared.Constants;
import com.sun.identity.shared.debug.Debug;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * This is the factory class is to obtain object instances of 
 * <code>CRLDistributionPoint</code>,
 * IssuingDistributionPoint, CRLDistributionPointsExtension, 
 * IssuingDistributionPointExtension.
 */
public class X509CRLExtensionFactory {
    private static X509CRLExtensionFactory instance = null;
    private static String provider = null;
    private static Class cdpClass = null;
    private static Class idpClass = null;
    private static Class cdpExtensionClass = null;
    private static Class idpExtensionClass = null;
    private static Class booleanClass = java.lang.Boolean.class;
    private static Class objectClass = java.lang.Object.class;
    private static Debug debug = SecurityDebug.debug;
    static final String CDP_CLASS_NAME = "CRLDistributionPointImpl";
    static final String IDP_CLASS_NAME = "IssuingDistributionPointImpl";
    static final String CDP_EXTENSION_CLASS_NAME = 
    	"CRLDistributionPointsExtensionImpl";
    static final String IDP_EXTENSION_CLASS_NAME = 
    	"IssuingDistributionPointExtensionImpl";
    
    static {
        /* 
         * Retrieve configured X.509 Security provider package
         * Default security provider pkg is com.sun.identity.security.x509.impl
         */ 
        provider = SystemProperties.get(Constants.SECURITY_PROVIDER_PKG, 
                       Constants.SUN_SECURITY_PROVIDER_PKG).trim();
        instance = new X509CRLExtensionFactory();
    }
    
    /* Constructor for X509CRLExtensionFactory */
    private X509CRLExtensionFactory() {
    }
    
    private static void loadCRLExtensionClass() {
        try {
            cdpClass = Class.forName(provider + "." + CDP_CLASS_NAME);
            idpClass = Class.forName(provider + "." + IDP_CLASS_NAME);
            cdpExtensionClass = Class.forName(provider + "." + 
                CDP_EXTENSION_CLASS_NAME);
            idpExtensionClass = Class.forName(provider + "." + 
                IDP_EXTENSION_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            debug.error("X509CRLExtensionFactory.loadCRLExtensionClass : " + 
                "class load failed.", e);
        }
    }

    /**
     * Returns an instance of the <code>X509CRLExtensionFactory</code> Object.
     *
     * @return an instance of the <code>X509CRLExtensionFactory</code> object.
     */
    public static X509CRLExtensionFactory getInstance() {
        return instance;
    }
        
    /**
     * Returns an instance of the <code>CRLDistributionPoint</code> Object.
     *
     * @return an instance of the <code>CRLDistributionPoint</code> object.
     */
    public CRLDistributionPoint createCRLDistributionPoint() {
        if (cdpClass == null) {
            loadCRLExtensionClass();
        }
        
        if (cdpClass != null) {
            try {
                return (CRLDistributionPoint)cdpClass.newInstance();
            } catch (Exception e) {
                debug.error(
                    "X509CRLExtensionFactory.createCRLDistributionPoint : " +
                    "class instantiation failed.", e);
            }
        }
         
        return null;
    }
            
    /**
     * Returns an instance of the <code>CRLDistributionPointsExtension</code> 
     * Object.
     * 
     * @param critical specify extension is critical
     * @param value DER enconded extension value
     * @return an instance of the <code>CRLDistributionPointsExtension</code> 
     *        object.
     * @throws IOException if DER encoded value is incorrect
     */
    public CRLDistributionPointsExtension createCRLDistributionPointsExtension
       (Boolean critical, Object value)        throws IOException {
        if (cdpExtensionClass == null) {
            loadCRLExtensionClass();
        }
        
        if (cdpExtensionClass != null) {
            try {
                Class [] paramTypes = {booleanClass, objectClass}; 
                Object [] parameters = {critical, value};
                
                Constructor constructor = 
                    cdpExtensionClass.getConstructor(paramTypes); 
                return (CRLDistributionPointsExtension)
                    constructor.newInstance(parameters);
            } catch (Exception e) {
                debug.error("X509CRLExtensionFactory." + 
                    "createCRLDistributionPointsExtension : " +
                    "class instantiation failed.", e);
            }
        }
         
        return null;
    }

    /**
     * Returns an instance of the <code>CRLDistributionPointsExtension</code> 
     * Object.
     * 
     * @param cdp specify extension value of <code>CRLDistributionPoint</code>
     * @return an instance of the <code>CRLDistributionPointsExtension</code> 
     *        object.
     */
    public CRLDistributionPointsExtension 
        createCRLDistributionPointsExtension(CRLDistributionPoint cdp) {
        if (cdpExtensionClass == null) {
            loadCRLExtensionClass();
        }
        
        if (cdpExtensionClass != null) {
            try {
                Class [] paramTypes = {cdpClass}; 
                Object [] parameters = {cdp};
                
                Constructor constructor = 
                    cdpExtensionClass.getConstructor(paramTypes); 
                return (CRLDistributionPointsExtension)
                    constructor.newInstance(parameters);
            } catch (Exception e) {
                debug.error("X509CRLExtensionFactory." + 
                    "createCRLDistributionPointsExtension : " +
                    "class instantiation failed.", e);
            }
        }
           
        return null;
    }

    /**
     * Returns an instance of the <code>IssuingDistributionPoint</code> Object.
     *
     * @return an instance of the <code>IssuingDistributionPoint</code> object.
     */
    public IssuingDistributionPoint createIssuingDistributionPoint() {
        if (idpClass == null) {
            loadCRLExtensionClass();
        }
                        
        if (idpClass != null) {
            try {
                return (IssuingDistributionPoint) idpClass.newInstance();
            } catch (Exception e) {
                debug.error("X509CRLExtensionFactory." + 
                    "createIssuingDistributionPoint: " +
                    "class instantiation failed.", e);
            }
        }
            
        return null;
    }
    
    /**
     * Returns an instance of the <code>IssuingDistributionPointExtension</code>
     * Object.
     * 
     * @param critical specify extension is critical
     * @param value DER enconded extension value
     * @return an instance of the <code>IssuingDistributionPointExtension</code>
     *        object.
     * @throws IOException if DER encoded value is incorrect
     */
    public IssuingDistributionPointExtension 
        createIssuingDistributionPointExtension(Boolean critical, Object value) 
        throws IOException {
        if (idpExtensionClass == null) {
            loadCRLExtensionClass();
        }
        
        if (idpExtensionClass != null) {
            try {
                Class [] paramTypes = {booleanClass, objectClass}; 
                Object [] parameters = {critical, value};
                
                Constructor constructor = 
                    idpExtensionClass.getConstructor(paramTypes); 
                return (IssuingDistributionPointExtension)
                    constructor.newInstance(parameters);
            } catch (Exception e) {
                debug.error("X509CRLExtensionFactory." + 
                    "createIssuingDistributionPointExtension : " +
                    "class instantiation failed.", e);
            }
        }

        return null;
    }
    
    /**
     * Returns an instance of the <code>IssuingDistributionPointExtension</code>
     * Object.
     * 
     * @param idp specify extension value of 
     *        <code>IssuingDistributionPoint</code>
     * @return an instance of the <code>IssuingDistributionPointExtension</code>
     *        object.
     */
    public IssuingDistributionPointExtension 
        createIssuingDistributionPointExtension(IssuingDistributionPoint idp) {
        if (idpExtensionClass == null) {
            loadCRLExtensionClass();
        }
                        
        if (idpExtensionClass != null) {
            try {
                Class [] paramTypes = {idpClass}; 
                Object [] parameters = {idp};
                
                Constructor constructor = 
                    idpExtensionClass.getConstructor(paramTypes); 
                return (IssuingDistributionPointExtension)
                    constructor.newInstance(parameters);
            }  catch (Exception e) {
                debug.error("X509CRLExtensionFactory." + 
                    "createIssuingDistributionPointExtension : " +
                    "class instantiation failed.", e);
            }
        }
        
        return null;
    }
}

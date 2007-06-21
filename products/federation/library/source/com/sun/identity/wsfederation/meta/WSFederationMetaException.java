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
 * $Id: WSFederationMetaException.java,v 1.1 2007-06-21 23:01:32 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.wsfederation.meta;

import com.sun.identity.wsfederation.common.WSFederationException;


/**
 * This class is an extension point for all SAML2 Meta related exceptions.
 * This class also handles message localization in SAML2 Meta exceptions.
 */
public class WSFederationMetaException extends WSFederationException {
    
    /**
     * Constructs a new <code>WSFederationMetaException</code> without a nested
     * <code>Throwable</code> using default resource bundle.
     * 
     * @param errorCode Key to resource bundle. You can use
     * <pre>
     * ResourceBundle rb = ResourceBunde.getBundle (rbName,locale);
     * String localizedStr = rb.getString(errorCode);
     * </pre>
     * @param args arguments to message. If it is not present pass them
     * as null
     */
    public WSFederationMetaException(String errorCode, Object[] args) {
        super(WSFederationMetaUtils.RESOURCE_BUNDLE_NAME, errorCode, args);
    }

    /**
     * Constructs a new <code>WSFederationMetaException</code> without a nested
     * <code>Throwable</code>.
     * 
     * @param rbName Resource Bundle Name to be used for getting
     * localized error message.
     * @param errorCode Key to resource bundle. You can use
     * <pre>
     * ResourceBundle rb = ResourceBunde.getBundle (rbName,locale);
     * String localizedStr = rb.getString(errorCode);
     * </pre>
     * @param args arguments to message. If it is not present pass them
     * as null
     */
    public WSFederationMetaException(String rbName, String errorCode, 
        Object[] args) {
        super(rbName, errorCode, args);
    }
    
    /**
     * Constructs a new <code>WSFederationMetaException</code> with the given
     * message.
     * 
     * @param message message for this exception. This message can be later
     * retrieved by <code>getMessage()</code> method.
     */
    public WSFederationMetaException(String message) {
        super(message);
    }
    
    /**
     * Constructs an <code>WSFederationMetaException</code> with given
     * <code>Throwable</code>.
     * 
     * @param t Exception nested in the new exception.
     */
    public WSFederationMetaException(Throwable t) {
        super(t);
    }
    
}


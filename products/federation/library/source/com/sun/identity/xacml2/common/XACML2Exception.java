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
 * $Id: XACML2Exception.java,v 1.1 2007-03-15 06:19:05 bhavnab Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.xacml2.common;
import com.sun.identity.saml2.common.SAML2Exception;


/**
 * This class is an extension point for all XACML related exceptions.
 * This class also handles message localization in XACML exceptions.
 *
 * @supported.all.api
 */
public class XACML2Exception extends SAML2Exception {
    
    
    /**
     * Constructs a new <code>XACML2Exception</code> with the given
     * message.
     *
     * @param message message for this exception. This message can be later
     * retrieved by <code>getMessage()</code> method.
     * 
     */
    public XACML2Exception(String message) {
        super(message);
    }
    
    /**
     * Constructs an <code>XACML2Exception</code> with given
     * <code>Throwable</code>.
     *
     * @param t Exception nested in the new exception.
     * 
     */
    public XACML2Exception(Throwable t) {
        super(t);
    }
    
}


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
 * $Id: AuthException.java,v 1.1 2006-01-28 09:16:30 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.authentication.service;

import com.sun.identity.common.L10NMessageImpl;

/**
 * This class is the super-class for all auth related <B>checked</B> exceptions.
 * An AuthException is thrown when anything goes wrong for authentication
 */
public class AuthException extends L10NMessageImpl {
    private static String bundleName = AuthD.BUNDLE_NAME;

    /**
     * Constructor.
     *
     * @param errorCode Key of the error message in resource bundle.
     * @param args Arguments to the message.
     */
    public AuthException(String errorCode, Object[] args) {
        super(bundleName, errorCode, args);
    }

    /**
     * Constructor.
     *
     * @param message English message for this exception.
     */
    public AuthException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param t Root cause of this exception.
     */
    public AuthException(Throwable t) {
        super(t);
    }
}

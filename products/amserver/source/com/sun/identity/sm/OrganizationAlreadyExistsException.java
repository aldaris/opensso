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
 * $Id: OrganizationAlreadyExistsException.java,v 1.1 2005-11-01 00:31:25 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

/* iPlanet-PUBLIC-CLASS */

/**
 * The <code>OrganizationAlreadyExistsException</code> is thrown if the
 * organization already exists.
 * 
 * @see java.lang.Exception
 * @see java.lang.Throwable
 */

public class OrganizationAlreadyExistsException extends SMSException {

    /**
     * Constructs an <code>OrganizationAlreadyExistsException</code> with no
     * specified detail message.
     */
    public OrganizationAlreadyExistsException() {
        super();
    }

    /**
     * Constructs an <code>OrganizationAlreadyExistsException</code> with the
     * specified detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public OrganizationAlreadyExistsException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>OrganizationAlreadyExistsException</code> with the
     * specified error code. It can be used to pass localized error message.
     * 
     * @param rbName
     *            Resource Bundle name where localized error message is located.
     * @param errCode
     *            error code or message id to be used for
     *            <code>ResourceBundle.getString()</code> to locate error
     *            message.
     * @param args
     *            any arguments to be used for error message formatting
     *            <code>getMessage()</code> will construct error message using
     *            English resource bundle.
     */
    public OrganizationAlreadyExistsException(String rbName, String errCode,
            Object[] args) {
        super(rbName, errCode, args);
    }
}

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
 * $Id: Validator.java,v 1.3 2006-08-25 21:20:40 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common.validation;

import java.util.Set;

/**
 * Validator interface defines method for performing validation.
 *
 * @deprecated As of OpenSSO version 8.0
 *             {@link com.sun.identity.shared.validation.Validator}
 */
public interface Validator {

    /**
     * Performs validation on a string.
     *
     * @param strData String to be validated.
     * @throws ValidationException if <code>strData</code> is in incorrect
     *         format.
     */
    public void validate(String strData)
        throws ValidationException;

    /**
     * Performs validation on a set of string.
     *
     * @param setData Set of string to be validated.
     * @throws ValidationException if one or more strings are in incorrect
     *         format.
     */
    public void validate(Set setData)
        throws ValidationException;
}

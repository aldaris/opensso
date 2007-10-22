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
 * $Id: AMPasswordUtil.java,v 1.1 2007-10-22 14:58:24 beomsuk Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.util;

import com.iplanet.services.util.Crypt;

/**
 * This class which contains utilities to encrypt and decrypt attribute value of
 * password type.
 * @supported.all.api
 */
public class AMPasswordUtil {

    /**
     * Encrypts the password.
     * 
     * @param password
     *            The password string.
     * @return The encrypted password.
     */
    public static String encrypt(String password) {
        return (Crypt.encode(password));
    }

    /**
     * Decrypts the encrypted password. If the string cannot be decrypted the
     * original string passed in will be returned.
     * 
     * @param encrypted
     *            encrypted string.
     * @return The decrypted password.
     */
    public static String decrypt(String encrypted) {
        String tmp;
        try {
            tmp = (Crypt.decode(encrypted));
        } catch (Exception ex) {
            tmp = encrypted;
        }
        if (tmp == null) {
            tmp = encrypted;
        }
        return tmp;
    }
}

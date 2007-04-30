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
 * $Id: Crypt.java,v 1.1 2007-04-30 01:28:29 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * A helper class that provides encryption and decryption methods.
 *
 * @author pbryan
 */
public class Crypt
{
    /** Symmetric algorithm to encrypt and decrypt association handles. */
    private static final String ALGORITHM = "AES";

    /** Secret key in properties file to encrypt and decrypt with. */
    private static final SecretKey SECRET_KEY =
     Config.getSecretKey(Config.ENCRYPTION_KEY, ALGORITHM);

    /**
     * TODO: Description.
     *
     * @param value TODO.
     * @return TODO.
     */
    public static String decrypt(String value)
    {
        if (value == null) {
            return null;
        }

        Cipher cipher;
        
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        }

        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        catch (NoSuchPaddingException nspe) {
            throw new IllegalStateException(nspe);
        }

        try {
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
        }

        catch (InvalidKeyException ike) {
            throw new IllegalStateException(ike);
        }

        try {
            return new String(cipher.doFinal(Codec.decodeBytes(value)), "UTF-8");
        }

        catch (IllegalBlockSizeException ibse) {
            throw new IllegalStateException(ibse);
        }

        // handle JVM that doesn't have UTF-8 encoding support
        catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException(uee);
        }

        // handle failure to decode UTF-8 input (e.g., malformed encoding)
        catch (Exception e) {
            return null;
        }
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     * @return TODO.
     */
    public static String encrypt(String value)
    {
        if (value == null) {
            return null;
        }

        Cipher cipher;
        
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        }

        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        catch (NoSuchPaddingException nspe) {
            throw new IllegalStateException(nspe);
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
        }

        catch (InvalidKeyException ike) {
            throw new IllegalStateException(ike);
        }

        try {
            return Codec.encodeBytes(cipher.doFinal(value.getBytes("UTF-8")));
        }

        catch (BadPaddingException bpe) {
            throw new IllegalStateException(bpe);
        }

        catch (IllegalBlockSizeException ibse) {
            throw new IllegalStateException(ibse);
        }

        // JVM that doesn't have UTF-8 encoding support
        catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException(uee);
        }
    }
}

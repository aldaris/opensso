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
 * $Id: AssocHandle.java,v 1.1 2007-04-30 01:28:28 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class AssocHandle implements Serializable
{
    /** Handle to return for invalid decodes. */
    private static final AssocHandle INVALID = new AssocHandle();

    /** TODO: Description. */
    public enum Type { ASSOCIATED, STATELESS };

    /** Number of seconds to allow an association to be valid. */
    private static final int ASSOC_SECONDS = 15 * 60;

    /** Number of bits to be used for shared secret. */
    private static final int HMAC_SIZE = 160;

    /** TODO: Description. */
    private SecretKey secret = null;

    /** Specifies date at which point association handle expires. */
    private Long expiry = null;

    /** TODO: Description. */
    private Type type = null;

    /** String version of association handle. */
    String value = null;

    /** Indicates if handle is valid. */
    private boolean valid = false;

    /**
     * TODO: Description.
     */
    private AssocHandle() {
    }

    /**
     * Generates an HMAC shared secret key, conformant with the OpenID
     * specification.
     *
     * @return the HMAC shared secret key.
     */
    private static SecretKey generateSecret()
    {
        KeyGenerator generator;

        try {
            generator = KeyGenerator.getInstance(Constants.HMAC_ALGORITHM);
        }

        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        generator.init(HMAC_SIZE);

        return generator.generateKey();
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    private String generateValue()
    {
        return Crypt.encrypt(type + " " +
         Codec.encodeLong(expiry) + " " + Codec.encodeSecretKey(secret));
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     * @return TODO.
     */
    public static AssocHandle decode(String value)
    {
        if (value == null) {
            return null;
        }

        String cleartext = Crypt.decrypt(value);

        if (cleartext == null) {
            return INVALID;
        }

        String[] split = cleartext.split(" ");

        if (split.length != 3) {
            return INVALID;
        }

        AssocHandle handle = new AssocHandle();

        try {
            handle.type = Type.valueOf(split[0]);
        }

        catch (IllegalArgumentException iae) {
            return INVALID;
        }

        try {
            handle.expiry = Codec.decodeLong(split[1]);
        }

        catch (DecodeException de) {
            return INVALID;
        }

        try {
            handle.secret = Codec.decodeSecretKey(split[2], Constants.HMAC_ALGORITHM);
        }

        catch (DecodeException de) {
            return INVALID;
        }

        handle.valid = true;
        return handle;
    }

    /**
     * TODO: Description.
     *
     * @param type TODO.
     */
    public static AssocHandle generate(Type type)
    {
        AssocHandle handle = new AssocHandle();

        handle.type = type;
        handle.secret = generateSecret();
        handle.expiry = new Long(System.currentTimeMillis() + (ASSOC_SECONDS * 1000));
        handle.value = handle.generateValue();
        handle.valid = true;

        return handle;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public String encode() {
        return value;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public SecretKey getSecret() {
        return (valid ? secret : null);
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public Type getType() {
        return (valid ? type : null);
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public boolean isValid() {
        return (valid && getExpiresIn().longValue() > 0L);
    }

    /**
     * Returns the number of seconds the association handle expires in.
     *
     * @return TODO.
     */
    public Long getExpiresIn() {
        return (valid ? (expiry.longValue() - System.currentTimeMillis()) / 1000 : null);
    }
}

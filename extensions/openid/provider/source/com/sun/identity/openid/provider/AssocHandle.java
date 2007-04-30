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
 * $Id: AssocHandle.java,v 1.2 2007-04-30 05:36:12 pbryan Exp $
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
 * Represents the association handle in OpenID messages.
 *
 * In this OpenID implementation, the association handle encapsulates the
 * shared secret key for authenticating OpenID identities. This allows
 * multiple OpenID providers to be clustered without sharing anything except
 * the actual encryption key (which is a configuration option).
 *
 * @author pbryan
 */
public class AssocHandle implements Serializable
{
    /** Handle to return for invalid decodes. */
    private static final AssocHandle INVALID = new AssocHandle();

    /** Determines if handle created in associate request or because of invalid supplied handle. */
    public enum Type { ASSOCIATED, STATELESS };

    /** Number of seconds to allow an association to be valid. */
// TODO: consider making this a configuration option
    private static final int ASSOC_SECONDS = 15 * 60;

    /** Number of bits to be used for shared secret. */
    private static final int HMAC_SIZE = 160;

    /** The secret key being encapsulated by the association handle. */
    private SecretKey secret = null;

    /** Specifies date at which point association handle expires. */
    private Long expiry = null;

    /** Establishes how the handle can be used by the consumer in requests. */
    private Type type = null;

    /** String version of association handle. */
    String value = null;

    /** Indicates if handle is valid. */
    private boolean valid = false;

    /**
     * Constructs an empty association handle.
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
     * Generates the association handle as it will be expressed in a response:
     * encrypted and Base64-encoded.
     *
     * @return encrypted, Base64-encoded association handle.
     */
    private String generateValue() {
        return Crypt.encrypt(type + " " +
         Codec.encodeLong(expiry) + " " + Codec.encodeSecretKey(secret));
    }

    /**
     * Decodes an association handle from its encoded string value. If the
     * parsed value yields an invalid handle, then a handle with invalid state
     * is returned.
     *
     * @param value string value to decode association handle from.
     * @return decoded association handle, or invalid handle if decode fails.
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
     * Generates a new association handle, complete with new secret key and
     * expiry time.
     *
     * @param type indicates if handle should be associated or stateless.
     * @return new association handle object.
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
     * Returns the association handle as it will be expressed in a response:
     * encrypted and Base64-encoded.
     *
     * @return string value of assoication handle.
     */
    public String encode() {
        return value;
    }

    /**
     * Returns the secret key for the association handle. If the handle is not
     * valid (for example, failed to parse from request), null is returned.
     *
     * @return secret key if handle is valid; otherwise null.
     */
    public SecretKey getSecret() {
        return (valid ? secret : null);
    }

    /**
     * Returns the type of association handle. If the handle is not
     * valid (for example, failed to parse from request), null is returned.
     *
     * There are two types of handle: ASSOCIATED (created from an OpenID
     * associate request by a consumer, which allows the consumer to validate
     * signatures from the identity provider itself) and STATLESS (created
     * when a checkid_* request is made by the consumer with an invalid (or
     * no) association handle.
     *
     * @return the type of handle if handle is valid; otherwise null.
     */
    public Type getType() {
        return (valid ? type : null);
    }

    /**
     * Returns true if association handle was parsed or generated correctly
     * and has not yet expired.
     *
     * @return true if handle valid and not expired.
     */
    public boolean isValid() {
        return (valid && getExpiresIn().longValue() > 0L);
    }

    /**
     * Returns the number of seconds the association handle expires in.
     *
     * @return handle expiry, in number of seconds.
     */
    public Long getExpiresIn() {
        return (valid ? (expiry.longValue() - System.currentTimeMillis()) / 1000 : null);
    }
}

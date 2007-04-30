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
 * $Id: AssociateResult.java,v 1.2 2007-04-30 05:36:13 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.math.BigInteger;
import java.util.Map;
import javax.crypto.SecretKey;

/**
 * Represents the response to an OpenID associate request.
 *
 * @author pbryan
 */
public class AssociateResult extends Message
{
    /** The association handle to be provided in future transactions. */
    private AssocHandle assocHandle;

    /** Algorithm to be used to sign subsequent messages. */
    private AssocType assocType;

    /** The encrypted shared secret, if using DH-SHA1. */
    private byte[] encMacKey;

    /** The number of seconds the association handle is good for. */
    private Long expiresIn;

    /** The plaintext shared secret, if not using DH-SHA1. */
    private SecretKey macKey;

    /** Diffie-Hellman public key from server used in shared key negotiation. */
    private BigInteger serverPublic;

    /** Method used to encode the association's MAC key in transit. */
    private SessionType sessionType;

    /**
     * Constructs an empty associate result.
     */
    public AssociateResult() {
        super();
    }

    /**
     * Returns the association handle to be provided in future transactions.
     *
     * @return association handle.
     */
    public AssocHandle getAssocHandle() {
        return assocHandle;
    }

    /**
     * Returns the algorithm to be used to sign subsequent messages.
     *
     * @return HMAC_SHA1.
     */
    public AssocType getAssocType() {
        return assocType;
    }

    /**
     * Returns the encrypted shared secret, if using DH-SHA1.
     *
     * @return encrypted shared secret.
     */
    public byte[] getEncMacKey() {
        return encMacKey;
    }

    /**
     * Returns the number of seconds the association handle is good for.
     *
     * @return number of seconds until handle expiry.
     */
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * Returns the plaintext shared secret, if not using DH-SHA1.
     *
     * @return plaintext shared secret.
     */
    public SecretKey getMacKey() {
        return macKey;
    }

    /**
     * Returns the Diffie-Hellman public key from server used in shared key
     * negotiation.
     *
     * @return server public key.
     */
    public BigInteger getServerPublic() {
        return serverPublic;
    }

    /**
     * Returns method used to encode the association's MAC key in transit.
     *
     * @return DH_SHA1 or CLEAR.
     */
    public SessionType getSessionType() {
        return sessionType;
    }

    /**
     * Sets the association handle to be provided in future transactions.
     *
     * @param value association handle.
     */
    public void setAssocHandle(AssocHandle value) {
        assocHandle = value;
    }

    /**
     * Sets the algorithm to be used to sign subsequent messages.
     *
     * @param value HMAC_SHA1.
     */
    public void setAssocType(AssocType value) {
        assocType = value;
    }

    /**
     * Sets the encrypted shared secret, if using DH-SHA1.
     *
     * @param value encrypted shared secret.
     */
    public void setEncMacKey(byte[] value) {
        encMacKey = value;
    }

    /**
     * Sets the number of seconds the association handle is good for.
     *
     * @param value number of seconds until handle expiry.
     */
    public void setExpiresIn(Long value) {
        expiresIn = value;
    }

    /**
     * Sets the plaintext shared secret, if not using DH-SHA1.
     *
     * @param value plaintext shared secret.
     */
    public void setMacKey(SecretKey value) {
        macKey = value;
    }

    /**
     * Sets the Diffie-Hellman public key from server used in shared key
     * negotiation.
     *
     * @param value server public key.
     */
    public void setServerPublic(BigInteger value) {
        serverPublic = value;
    }

    /**
     * Sets method used to encode the association's MAC key in transit.
     *
     * @param value DH_SHA1 or CLEAR.
     */
    public void setSessionType(SessionType value) {
        sessionType = value;
    }

    /**
     * Encodes the associate response into a map of key-value pairs.
     *
     * @return map containing the associate result.
     */
    public Map<String,String> encode()
    {
        Map<String,String> map = super.encode();

        if (assocType != null) {
            map.put("assoc_type", assocType.encode());
        }

        if (assocHandle != null) {
            map.put("assoc_handle", assocHandle.encode());
        }

        if (expiresIn != null) {
            map.put("expires_in", Codec.encodeLong(expiresIn));
        }

        if (sessionType != null) {
            map.put("session_type", sessionType.encode());
        }

        if (serverPublic != null) {
            map.put("dh_server_public", Codec.encodeBigInteger(serverPublic));
        }

        if (encMacKey != null) {
            map.put("enc_mac_key", Codec.encodeBytes(encMacKey));
        }

        if (macKey != null) {
            map.put("mac_key", Codec.encodeSecretKey(macKey));
        }

        return map;
    }
}

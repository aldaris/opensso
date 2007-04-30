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
 * $Id: AssociateResult.java,v 1.1 2007-04-30 01:28:28 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.math.BigInteger;
import java.util.Map;
import javax.crypto.SecretKey;

/**
 * TODO: Description.
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
     * TODO: Description.
     */
    public AssociateResult() {
        super();
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public AssocHandle getAssocHandle() {
        return assocHandle;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public AssocType getAssocType() {
        return assocType;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public byte[] getEncMacKey() {
        return encMacKey;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public SecretKey getMacKey() {
        return macKey;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public BigInteger getServerPublic() {
        return serverPublic;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public SessionType getSessionType() {
        return sessionType;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setAssocHandle(AssocHandle value) {
        assocHandle = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setAssocType(AssocType value) {
        assocType = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setEncMacKey(byte[] value) {
        encMacKey = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setExpiresIn(Long value) {
        expiresIn = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setMacKey(SecretKey value) {
        macKey = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setServerPublic(BigInteger value) {
        serverPublic = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setSessionType(SessionType value) {
        sessionType = value;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
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

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
 * $Id: AssociateQuery.java,v 1.1 2007-04-30 01:28:28 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.math.BigInteger;
import java.util.Map;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class AssociateQuery extends Message
{
    /** Default Diffie-Hellman prime modulus if not specified by consumer. */
    private static BigInteger DEFAULT_MODULUS = new BigInteger("155172898181" +
     "4736974712322577637155399157248019669154044797077953140576293785419175" +
     "8065122742369818899372781615264663143856159582568818888995127215884267" +
     "5419950341258706556549803580104870537681476726513255747040765857479291" +
     "2915723345106432450947150072296210941943497839259847603755949858482533" +
     "59305585439638443");

    /** Default Diffie-Hellman base generator if not specified by consumer. */
    private static BigInteger DEFAULT_GENERATOR = BigInteger.valueOf(2L);

    /** Algorithm to be used to sign subsequent messages. */
    private AssocType assocType;

    /** Method used to encode the association's MAC key in transit. */
    private SessionType sessionType;

    /** Diffie-Hellman prime modulus used in key negotiation. */
    private BigInteger modulus;

    /** Diffie-Hellman base generator used in key negotiation. */
    private BigInteger generator;

    /** Diffie-Hellman public key from consumer used in key negotiation. */
    private BigInteger consumerPublic;

    /**
     * TODO: Description.
     */
    public AssociateQuery() {
        super();
    }

    /**
     * TODO: Description.
     *
     * @param map TODO.
     * @throws DecodeException TODO.
     */
    protected void decode(Map<String,String> map)
    throws DecodeException
    {
        super.decode(map);

        if (!getMode().equals(Mode.ASSOCIATE)) {
            throw new DecodeException("mode must be associate");
        }

        assocType = AssocType.decode(map.get("assoc_type"));

        if (assocType == null) {
            assocType = AssocType.HMAC_SHA1;
        }

        sessionType = SessionType.decode(map.get("session_type"));

        if (sessionType == null) {
            throw new DecodeException("session_type is required");
        }

        modulus = Codec.decodeBigInteger(map.get("dh_modulus"));

        if (modulus == null) {
            modulus = DEFAULT_MODULUS;
        }

        generator = Codec.decodeBigInteger(map.get("dh_gen"));

        if (generator == null) {
            generator = DEFAULT_GENERATOR;
        }

        consumerPublic = Codec.decodeBigInteger(map.get("dh_consumer_public"));

        if (sessionType == SessionType.DH_SHA1 && consumerPublic == null) {
            throw new DecodeException("dh_consumer_public is required");
        }
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
    public BigInteger getConsumerPublic() {
        return consumerPublic;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public BigInteger getGenerator() {
        return generator;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public BigInteger getModulus() {
        return modulus;
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
    public void setAssocType(AssocType value) {
        assocType = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setConsumerPublic(BigInteger value) {
        consumerPublic = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setGenerator(BigInteger value) {
        generator = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setModulus(BigInteger value) {
        modulus = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setSessionType(SessionType value) {
        sessionType = value;
    }
}

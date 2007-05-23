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
 * $Id: AssociateAction.java,v 1.3 2007-05-23 00:04:32 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An action that is performed in response to an OpenID associate request.
 *
 * @author pbryan
 */
public class AssociateAction extends Action
{
    /** OpenID message representing the parameters of an associate query. */
    AssociateQuery query = new AssociateQuery();

    /** OpenID message representing body of response to associate query. */
    AssociateResult result = new AssociateResult();

    /**
     * Constructs a new instance of the action.
     *
     * @param request the request dispatched from the servlet.
     * @param response the response to provide to the dispatching servlet.
     */
    public AssociateAction(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    /**
     * Returns the SHA-1 hash function result against the input bytes.
     *
     * @param input bytes to hash.
     * @return SHA-1 hash for input bytes.
     */
    private static byte[] sha1(byte[] input)
    {
        try {
            return MessageDigest.getInstance("SHA1").digest(input);
        }
        
        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }
    }

    /**
     * Performs an exlusive OR operation on corresponding bytes between two
     * byte arrays.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return the byte arrays, XORed
     */
    private static byte[] xor(byte[] a, byte[] b)
    {
        assert (a.length == b.length);

        byte[] bytes = new byte[a.length];

        for (int n = 0; n < a.length; n++) {
            bytes[n] = (byte)(a[n] ^ b[n]);
        }

        return bytes;
    }

    /**
     * Generates the server's Diffie-Hellman key pair based on the supplied
     * modulus and generator.
     *
     * @param modulus Diffie-Hellman prime modulus (p).
     * @param generator Diffie-Hellman base generator (g).
     * @return the generated Diffie-Hellman keypair.
     */
    private static KeyPair generateKeyPair(BigInteger modulus, BigInteger generator)
    {
        KeyPairGenerator keyPairGenerator;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
        }

        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        DHParameterSpec dhParameterSpec = new DHParameterSpec(modulus, generator);
            
        try {
            keyPairGenerator.initialize(dhParameterSpec);
        }

        catch (InvalidAlgorithmParameterException iape) {
            throw new IllegalStateException(iape);
        }

        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Performs Diffie-Hellman shared key exchange to securely transmit the
     * shared secret to the consumer.
     *
     * @param secret the shared MAC key used to verify checkid_* requests.
     */
    private void dhSHA1(SecretKey secret)
    {
        // diffie-hellman-specific parameters from query
        BigInteger modulus = query.getModulus();
        BigInteger generator = query.getGenerator();
        BigInteger consumerPublic = query.getConsumerPublic();

        // generate diffie-hellman keypair for prime modulus and base generator
        KeyPair keyPair = generateKeyPair(modulus, generator);

        // calculate diffie hellman secret to xor with mac key
        byte[] xorKey = sha1(consumerPublic.modPow(
         ((DHPrivateKey)keyPair.getPrivate()).getX(), modulus).toByteArray());

        // provide server public key in response to allow client to decrypt secret
        result.setServerPublic(((DHPublicKey)keyPair.getPublic()).getY());

        // xor shared secret with calculated xor key
        result.setEncMacKey(xor(xorKey, secret.getEncoded()));
    }

    /**
     * Processes the OpenID associate request.
     *
     * @throws BadRequestException if the associate request is malformed.
     * @throws IOException if an input/output error occurs.
     */
    public void perform() throws BadRequestException, IOException
    {
        // populate associate query from HTTP request
        query.populate(request);

        // generate new associated handle for request
        AssocHandle handle = AssocHandle.generate(AssocHandle.Type.ASSOCIATED);

        // set common result parameters
        result.setAssocType(query.getAssocType());
        result.setAssocHandle(handle);
        result.setExpiresIn(handle.getExpiresIn());
        result.setSessionType(query.getSessionType());

        // get mac key from handle, to return in result
        SecretKey secret = handle.getSecret();

        SessionType sessionType = query.getSessionType();

        if (sessionType == SessionType.DH_SHA1) {
            dhSHA1(secret);
        }

        else if (sessionType == null || sessionType == SessionType.CLEAR) {
            result.setMacKey(secret);
        }

        else {
            throw new BadRequestException("unsupported session type");
        }

        // compose result as a string set of key-value pairs
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(Maps.toResponseString(result.encode()));
    }
}

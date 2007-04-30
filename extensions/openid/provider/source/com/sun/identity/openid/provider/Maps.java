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
 * $Id: Maps.java,v 1.2 2007-04-30 05:52:18 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

/**
 * Provides functions to convert OpenID messages to and from maps of key-value
 * pairs.
 *
 * @author pbryan
 */
public class Maps
{
    /** Prefix for HTTP request parameters. */
    private static final String PREFIX = "openid.";

    /**
     * Returns the hashed message authentication code for the specified map
     * keys, Base64 encoded.
     *
     * @param map map of key-value pairs to compute HMAC for.
     * @param algorithm algorithm to use to compute HMAC.
     * @param secret secret key to use to compute HMAC
     * @param keys specifies which entries, in what order to compute HMAC for.
     * @return the HMAC in Base64 encoding.
     */
    private static String hmac(Map<String,String> map,
    String algorithm, SecretKey secret, List<String> keys)
    {
        Mac mac;

        try {
            mac = Mac.getInstance(algorithm);
        }

        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae);
        }

        try {
            mac.init(secret);
        }

        catch (InvalidKeyException ike) {
            throw new IllegalStateException(ike);
        }

        // linked hash map to ensure order of iteration through entries
        LinkedHashMap<String,String> sign = new LinkedHashMap<String,String>();

        for (String name : keys) {
            sign.put(name, map.get(name));
        }

        return Codec.encodeBytes(mac.doFinal(toBytes(sign)));
    }

    /**
     * Provides a map of key-value pairs for all parameters in the specified
     * request that begin with the OpenID parmeter prefix.
     *
     * @param request the HTTP request to retrieve parameters from.
     * @return the request parameters as a map of key-value pairs.
     */
    public static Map<String,String> fromRequest(HttpServletRequest request)
    {
        int prefixLength = PREFIX.length();

        HashMap<String,String> map = new HashMap<String,String>();

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();)
        {
            String name = (String)e.nextElement();

            // ignore parameters not pertinent to OpenID requests
            if (!name.startsWith(PREFIX)) {
                continue;
            }

            map.put(name.substring(prefixLength), request.getParameter(name));
        }

        return map;
    }

    /**
     * Provides the map in the OpenID key-value form encoding, in which each
     * entry begins with a key, followed by a colon, the value associated with
     * the key and a newline terminator.
     *
     * @param map key-value pairs to encode into OpenID key-value form encoding.
     * @return key-value form encoding, expressed as UTF-8 byte encoding.
     */
    public static byte[] toBytes(Map<String,String> map)
    {
        try {
            return toResponseString(map).getBytes("UTF-8");
        }

        // a java virtual machine without UTF-8 encoding shouldn't occur
        catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException(uee);
        }
    }

    /**
     * Provides the map in the OpenID key-value form encoding, in which each
     * entry begins with a key, followed by a colon, the value associated with
     * the key and a newline terminator.
     *
     * @param map key-value pairs to encode into OpenID key-value form encoding.
     * @return key-value form encoding as a string.
     */
    public static String toResponseString(Map<String,String> map)
    {
        StringBuffer buf = new StringBuffer();

        for (String name : map.keySet())
        {
            String value = map.get(name);

            // only include the key-value pair if there is a value
            if (value != null) {
                buf.append(name).append(':').append(value).append('\n');
            }
        }

        return buf.toString();
    }

    /**
     * Appends the specified map key-value pairs to the specified URL as a
     * query string of parameters.
     *
     * @param url the URL, expressed as a string, to append query string to.
     * @param map the map of key-value pairs to append to URL.
     * @return the supplied URL with appended query string parameters.
     */
    public static String toQueryString(String url, Map<String,String> map)
    {
        if (url == null) {
            return null;
        }

        if (map == null) {
            return url;
        }

        StringBuffer buf = new StringBuffer(url);

        // compute next delimiter depending on if URL has query string already
        char delim = (url.indexOf('?') == -1 ? '?' : '&');

        for (String name : map.keySet())
        {
            String value = map.get(name);

            // only include the key-value pair if there is a value
            if (value == null) {
                continue;
            }

            buf.append(delim).append(PREFIX).append(name).append('=');

            try {
                buf.append(URLEncoder.encode(value, "UTF-8"));
            }

            // a java virtual machine without UTF-8 encoding shouldn't occur
            catch (UnsupportedEncodingException uee) {
                throw new IllegalStateException(uee);
            }

            delim = '&';
        }

        return buf.toString();
    }

    /**
     * Appends the specified map key-value pairs to the specified URL as a
     * query string of parameters.
     *
     * @param url the URL to append query string to.
     * @param map the map of key-value pairs to append to URL.
     * @return the supplied URL with appended query string parameters.
     */
    public static String toQueryString(URL url, Map<String,String> map)
    {
        if (url == null) {
            return null;
        }

        return toQueryString(Codec.encodeURL(url), map);
    }

    /**
     * Signs the map of key-value pairs, and adds the associated "signed" and
     * "sig" entries to the map.
     *
     * @param map the map of key-value pairs to sign.
     * @param algorithm the HMAC algorithm used to generate signature.
     * @param secret the shared secret key to compute HMAC with.
     */
    public static void sign(Map<String,String> map,
    String algorithm, SecretKey secret)
    {
        List<String> keys = new ArrayList<String>(map.keySet());
        map.put("sig", hmac(map, Constants.HMAC_ALGORITHM, secret, keys));
        map.put("signed", Codec.encodeList(keys));
    }

    /**
     * Verifies the map of key-value pairs against its "signed" and "sig"
     * entries using.
     *
     * @param map the map of key-value pairs to verify.
     * @param algorithm the HMAC algorithm used to verify signature.
     * @param secret the shared secret key to compute HMAC with.
     * @return true if the signature was verified successfully.
     */
    public static Boolean verify(Map<String,String> map,
    String algorithm, SecretKey secret)
    {
        String _signed = map.get("signed");
        String sig = map.get("sig");

        // verification fails if requisite signature entries are not present
        if (_signed == null || sig == null) {
            return false;
        }

        List<String> keys = Codec.decodeList(_signed);

        return hmac(map, Constants.HMAC_ALGORITHM, secret, keys).equals(sig);
    }
}

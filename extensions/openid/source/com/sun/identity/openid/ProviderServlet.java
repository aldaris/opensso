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
 * $Id: ProviderServlet.java,v 1.1 2007-03-10 23:00:09 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan.
 */

/*
 * TODO LIST (to turn this from POC to potentially product-grade):
 *
 * - Make ASSOC_SECONDS a configuration option.
 * - Stop hard-coding login URI, OpenID URI and goto parameter.
 * - Better (less manual) way to perform DH-SHA1-XOR encryption? Can use more of JCA?
 * - Provide user interface to allow user to choose to trust trust_root or not.
 * - Validate trust_root against return_to.
 * - Provide configuration to allow user association in multiple OpenSSO realms.
 * - Currently using the AM crypt utility class; use something better/stronger?
 * - Move to federation project instead of amserver, which means:
 *   - no iplanet crypt or base64 utility classes
 *   - stop using auth module shared secret as key to encrypt association handles
 *   - use SystemConfigurationUtil for servlet URL discovery
 * - proper diagnostic logging
 * - automatic profile page generation with server/delegate?
 * - canonical code formatting a'la opensso coding standards
 */

package com.sun.identity.openid;

// TODO: these are temporary until code base moved to federation product space
import com.iplanet.services.util.Base64;
import com.iplanet.services.util.Crypt;
import com.iplanet.am.util.SystemProperties;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOTokenManager;
import com.iplanet.sso.SSOToken;
import com.sun.identity.common.Constants;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  TODO: Description.
 *
 *  @author pbryan
 */
public class ProviderServlet extends HttpServlet
{
    /** Number of seconds to allow an association to be valid. */
    private static final int ASSOC_SECONDS = 15 * 60;

    /** JCE algorithm used to produce OpenID-compatible HMAC. */
    private static final String HMAC_ALGORITHM = "HmacSHA1";

    /** Number of bits to be used for shared secret. Must be same as SHA-1 digest length. */
    private static final int HMAC_LENGTH_BITS = 160;
    
    /** Number of bytes to be used for shared secret. Computed from HMAC_LENGTH_BITS. */
    private static final int HMAC_LENGTH_BYTES = HMAC_LENGTH_BITS / 8;

    /** Required encoding format of HMAC key. Must be RAW. */
    private static final String HMAC_FORMAT = "RAW";

    // request methods used in openid
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    // association types
    private static final String ASSOC_HMAC_SHA1 = "HMAC-SHA1";

    // session types
    private static final String SESSION_DH_SHA1 = "DH-SHA1";

    /** Default Diffie-Hellman prime modulus if not specified by consumer in association mode. */
    private static BigInteger DH_DEFAULT_P = new BigInteger("1551728981814736974712322577637155" +
     "3991572480196691540447970779531405" + "7629378541917580651227423698188993" +
     "7278161526466314385615958256881888" + "8995127215884267541995034125870655" +
     "6549803580104870537681476726513255" + "7470407658574792912915723345106432" +
     "4509471500722962109419434978392598" + "4760375594985848253359305585439638443");

    /** Default Diffie-Hellman base generator if not specified by consumer in association mode. */
    private static BigInteger DH_DEFAULT_G = BigInteger.valueOf(2L);

    /** Indicates handle type is not important when retreiving secret. */
    private static final int HANDLE_DONTCARE = 0;

    /** Indicates handle must be stateless when secret is retrieved. */
    private static final int HANDLE_STATELESS = 1;

    /** Indicates handle must be associated with consumer when secret is retrieved. */
    private static final int HANDLE_ASSOCIATED = 2; 

// FIXME: hard-coded values that should be dynamically discovered or configurable
    private static final String HARD_CODED_LOGIN_URI = "/UI/Login";
    private static final String HARD_CODED_PARAM_GOTO = "goto";
    private static final String HARD_CODED_OPENID_URI = "/openid";

// FIXME: multiple configurable openid realms should be supported
    private static final String HARD_CODED_OPENID_REALM = "http://openid.example.com/";

    /** Date format used in OpenID specification. */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

// TODO: this should be a configurable option
    /** Some clients don't comply with the OpenID spec. Setting to false relaxes enforcement. */
    private static boolean STRICT_OPENID = false;

    /**
     *    Generates an HMAC shared secret key, conformant with the OpenID specification.
     *
     *    @return the HMAC shared secret key.
     *    @throws ServletException TODO.
     */
    private static SecretKey generateSecret()
    throws ServletException
    {
        try
        {
            KeyGenerator generator = KeyGenerator.getInstance(HMAC_ALGORITHM);
            generator.init(HMAC_LENGTH_BITS);
            SecretKey key = generator.generateKey();
            assert key.getFormat().equals(HMAC_FORMAT);
            return key;
        }

        catch (NoSuchAlgorithmException nsae) {
            throw new ServletException(nsae);
        }
    }

    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param response TODO.
     *    @throws ServletException TODO.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }

    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param response TODO.
     *    @throws ServletException TODO.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

        // dispatch to mode handler
        try
        {
            String mode = getRequiredParameter(request, "openid.mode");

            if (mode.equals("associate")) {
                associate(request, response);
            }

            else if (mode.equals("checkid_immediate")) {
                checkid_immediate(request, response);
            }

            else if (mode.equals("checkid_setup")) {
                checkid_setup(request, response);
            }

            else if (mode.equals("check_authentication")) {
                check_authentication(request, response);
            }

            else {
                throw new BadRequestException("unknown mode");
            }
        }

        // handle case where a bad request was made to the openid identity provider service        
        catch (BadRequestException bre) {
            response.sendError(response.SC_BAD_REQUEST, bre.getMessage());
        }
    }

    /**
     *    Returns true if the user has a current session with OpenSSO and the user name
     *    matches that being requested by the consumer.
     *
     *    @param principal TODO.
     *    @param identity TODO.
     *    @return TODO.
     */
    private static boolean identityMatches(Principal principal, String identity)
    {
// TODO: should support configurable multiple openid identity provider realms
        if (!identity.startsWith(HARD_CODED_OPENID_REALM)) {
            return false;
        }

        String identityUID = identity.substring(HARD_CODED_OPENID_REALM.length());

        if (identityUID.length() == 0) {
            return false;
        }

        if (principal == null) {
            return false;
        }

        String userDN = principal.getName();

        if (userDN == null) {
            return false;
        }

// TODO: should support configurable multiple opensso realms
        String principalUID = userDN.substring(userDN.indexOf('=') + 1, userDN.indexOf(','));

        if (principalUID == null || !principalUID.equals(identityUID)) {
            return false;
        }
        
        return true;
    }

    /**
     *    TODO: Description.
     *
     *    @return TODO.
     */
// TODO: this is currently used for am login url as well as openid provider servlet.
// this may not necessarily always be true?
    private static StringBuffer getAMServerURL()
    {
        StringBuffer url = new StringBuffer();

// TODO: use federation code base instead of internal iplanet class
        url.append(SystemProperties.get(Constants.AM_SERVER_PROTOCOL)).append("://");
        url.append(SystemProperties.get(Constants.AM_SERVER_HOST)).append(":");
        url.append(SystemProperties.get(Constants.AM_SERVER_PORT));
        url.append(SystemProperties.get(Constants.AM_SERVICES_DEPLOYMENT_DESCRIPTOR));

        return url;
    }

    /**
     *    Returns the authentication service login URL with goto parameter containing original
     *    target of request.
     *
     *    @param request TODO.
     *    @param response TODO.
     *    @throws IOException TODO.
     */
    private static void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
    throws IOException
    {
        // generate goto url to return to upon successful authentication
        StringBuffer gotoURL = new StringBuffer(request.getRequestURL().toString());
        String query = request.getQueryString();

        if (query != null && query.length() > 0) {
            gotoURL.append('?').append(query);
        }

        // generate complete url to redirect to
        StringBuffer url = getAMServerURL();
        url.append(HARD_CODED_LOGIN_URI).append('?');
        url.append(HARD_CODED_PARAM_GOTO).append("=");
        url.append(URLEncoder.encode(gotoURL.toString()));
        response.sendRedirect(url.toString());
    }
        
    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param method TODO.
     *    @throws BadRequestException TODO.
     */
    private static void requireMethod(HttpServletRequest request, String method)
    throws BadRequestException
    {
        if (STRICT_OPENID && !request.getMethod().equals(method)) {
            throw new BadRequestException("mode requires " + method + " method");
        }
    }
    
    /**
     *    TODO: Description.
     *
     *    @param type TODO.
     *    @param expiry TODO.
     *    @param secret TODO.
     *    @return TODO.
     *    @throws ServletException TODO.
     */
    private static String generateHandle(int type, Date expiry, SecretKey secret)
    throws ServletException
    {
// TODO: include an hmac inside encrypted handle to provide further entropy and validation of handle itself?
        return Crypt.encrypt(type + " " + encode(expiry) + " " + encode(secret));
    }

    /**
     *    TODO: Description.
     *
     *    @param value TODO.
     *    @return TODO.
     */
    private static Integer parseInteger(String value)
    {
        try {
            return Integer.decode(value);
        }

        catch (NumberFormatException nfe) {
            return null;
        }
    }

    /**
     *    Decodes a date from a passed string, returning the date itself, or null if date could not
     *    be parsed.
     *
     *    @param value string value to decode into date.
     *    @return date value parsed from string.
     */
    private static Date decodeDate(String value)
    {
        try {
            return DATE_FORMAT.parse(value);
        }

        catch (ParseException pe) {
            return null;
        }
    }

    /**
     *    TODO: Description.
     *
     *    @param value TODO.
     *    @return TODO.
     */
    private static byte[] decodeBytes(String value)
    {
        if (value == null) {
            return null;
        }

        return Base64.decode(value);
    }

    /**
     *    TODO: Description.
     *
     *    @param value TODO.
     *    @return TODO.
     */
    private static BigInteger decodeBigInteger(String value)
    {
        byte[] bytes = decodeBytes(value);

        if (bytes == null) {
            return null;
        }

        return new BigInteger(bytes);
    }

    /**
     *    TODO: Description.
     *
     *    @param value TODO.
     *    @return TODO.
     */
    private static SecretKey decodeSecretKey(String value)
    {
        byte[] bytes = decodeBytes(value);

        // if nothing decoded or incorrect length, key not valid
        if (bytes == null || bytes.length != HMAC_LENGTH_BYTES) {
            return null;
        }

        SecretKey key = new SecretKeySpec(bytes, HMAC_ALGORITHM);
        assert key.getFormat().equals(HMAC_FORMAT);

        return key;
    }

    /**
     *    TODO: Description.
     *
     *    @param handle TODO.
     *    @param type TODO.
     *    @return TODO.
     */
    private static SecretKey getSecretFromHandle(String handle, int type)
    {
        if (handle == null || handle.length() == 0) {
            return null;
        }

        String cleartext = Crypt.decrypt(handle);

        // no valid key from handle if could not be decrypted
        if (cleartext == null) {
            return null;
        }

        String[] split = cleartext.split(" ");

        // handle is not valid unless all components are present
        if (split.length != 3) {
            return null;
        }

        // passed handle must parse and match expected type to succeed validation
        Integer handleType = parseInteger(split[0]);
        if (handleType == null || (type != HANDLE_DONTCARE && type != handleType.intValue())) {
            return null;
        }

        // handle is not valid if date incorrectly formatted or not in the future
        Date expiry = decodeDate(split[1]);
        if (expiry == null || !expiry.after(new Date())) {
            return null;
        }

        return decodeSecretKey(split[2]);
    }

    /**
     *    TODO: Description.
     *
     *    @response TODO.
     *    @map TODO.
     *    @throws IOException TODO.
     *    @throws ServletException TODO.
     */
    private static void mapResponse(HttpServletResponse response, LinkedHashMap map)
    throws IOException, ServletException
    {
        response.setContentType("text/plain");
        response.getOutputStream().write(mapBytes(map));
    }

    /**
     *    Returns a set of key-value pairs as an array of bytes, newline-delimited,
     *    in UTF-8 encoding, as specified by OpenID.
     *
     *    @param map TODO.
     *    @return TODO.
     *    @throws ServletException TODO.
     */
    private static byte[] mapBytes(LinkedHashMap map)
    throws ServletException
    {
        StringBuffer buf = new StringBuffer();

        for (Iterator i = map.keySet().iterator(); i.hasNext();)
        {
            String key = (String)i.next();
            Object value = map.get(key);

            // only include the key-value pair if there is a value
            if (value != null) {
                buf.append(key).append(':').append(encode(value)).append('\n');
            }
        }

        try {
            return buf.toString().getBytes("UTF-8");
        }

        catch (UnsupportedEncodingException uee) {
            throw new ServletException(uee);
        }
    }

    /**
     *    TODO: Description.
     *
     *    @param map TODO.
     *    @return TODO.
     */
    private static String mapKeys(LinkedHashMap map)
    {
        StringBuffer buf = new StringBuffer();

        String delim = "";
        
        for (Iterator i = map.keySet().iterator(); i.hasNext();)
        {
            String key = (String)i.next();

            if (map.get(key) != null) {
                buf.append(delim).append(key);
                delim = ",";
            }
        }
        
        return buf.toString();
    }

    /**
     *    Converts a data type into the string format required by the OpenID specification.
     *
     *    @param o the object to convert into the canonical string format.
     *    @return the canonical string format of the object being converted.
     *    @throws ServletException TODO.
     */
    private static String encode(Object o)
    throws ServletException
    {
        if (o instanceof String) {
            return (String)o;
        }

        if (o instanceof byte[]) {
            return Base64.encode((byte[])o);
        }

        if (o instanceof Date) {
            return DATE_FORMAT.format((Date)o);
        }

        if (o instanceof BigInteger) {
            return encode(((BigInteger)o).toByteArray());
        }

        if (o instanceof SecretKey) {
            return encode(((SecretKey)o).getEncoded());
        }

        if (o instanceof Boolean) {
            return ((Boolean)o).toString();
        }

        throw new ServletException("unsupported value type");
    }

    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @return TODO.
     */
    private Principal getPrincipal(HttpServletRequest request)
    {
        try
        {
            SSOTokenManager manager = SSOTokenManager.getInstance();
            SSOToken token = manager.createSSOToken(request);

            if (!manager.isValidToken(token)) {
                return null;
            }
        
            return token.getPrincipal();
        }
        
// TODO: should this be servlet exception (assertion) or will it ever legitimately throw ssoexception?
        catch (SSOException ssoe) {
            return null;
        }
    }

    /**
     *    TODO: Description.
     *
     *    @param TODO.
     *    @return TODO.
     *    @throws ServletException TODO.
     */
    private static byte[] sha1(byte[] input)
    throws ServletException
    {
        try {
            return MessageDigest.getInstance("SHA1").digest(input);
        }
        
        catch (NoSuchAlgorithmException nsae) {
            throw new ServletException(nsae);
        }
    }

    /**
     *    Performs an exlusive OR operation on corresponding bytes between two byte arrays.
     *
     *    @param a the first byte array
     *    @param b the second byte array
     *    @return the byte arrays, XORed
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
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param parameter TODO.
     *    @return TODO.
     */
    private static String getRequiredParameter(HttpServletRequest request, String parameter)
    throws BadRequestException
    {
        String value = request.getParameter(parameter);

        if (value == null) {
            throw new BadRequestException(parameter + " parameter is required");
        }

        return value;
    }

    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param parameter TODO.
     *    @param defaultValue TODO.
     *    @return TODO.
     */
    private static String getDefaultParameter(HttpServletRequest request, String parameter, String defaultValue)
    {
        String value = request.getParameter(parameter);
        return (value == null ? defaultValue : value);
    }

    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param parameter TODO.
     *    @return TODO.
     */
    private static String getOptionalParameter(HttpServletRequest request, String parameter)
    {
        return request.getParameter(parameter);
    }

    /**
     *    Calculates and returns the expiry of a new association handle, assuming
     *    is issued at time of the call.
     *
     *    @return the association expiry type, based on the number of seconds configured.
     */
    private static Date calculateExpiry()
    {
        GregorianCalendar cal = new GregorianCalendar();
// TODO: should be configurable, not hard-coded
        cal.add(cal.SECOND, ASSOC_SECONDS);
        return cal.getTime();
    }

    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param response TODO.
     *    @throws BadRequestException TODO.
     *    @throws IOException TODO.
     *    @throws ServletException TODO.
     */
    public void associate(HttpServletRequest request, HttpServletResponse response)
    throws BadRequestException, IOException, ServletException
    {
        // associate is only sent via post method
        requireMethod(request, METHOD_POST);

        // associate request parameters
        String assoc_type = getDefaultParameter(request, "openid.assoc_type", ASSOC_HMAC_SHA1);
        String session_type = getRequiredParameter(request, "openid.session_type");
        String dh_modulus = getOptionalParameter(request, "openid.dh_modulus");
        String dh_gen = getOptionalParameter(request, "openid.dh_gen");
        String dh_consumer_public = getOptionalParameter(request, "openid.dh_consumer_public");

        // in this spec, there is one and only one association type
        if (!assoc_type.equals(ASSOC_HMAC_SHA1)) {
            throw new BadRequestException("unsupported openid.assoc_type");
        }

        // validate session type (must be blank or dh-sha1)
        if (session_type.length() > 0 && !session_type.equals(SESSION_DH_SHA1)) {
            throw new BadRequestException("unsupported openid.session_type");
        }

        // determine if dh-sha1 is being used to encrypt the session
        boolean dhRequested = session_type.equals(SESSION_DH_SHA1);

        // determine the time at which the association handle expires
        Date expiry = calculateExpiry();

        // generate a new shared secret (the hmac key)
        SecretKey secret = generateSecret();

        LinkedHashMap map = new LinkedHashMap();

        map.put("assoc_type", assoc_type);
        map.put("assoc_handle", generateHandle(HANDLE_ASSOCIATED, expiry, secret));
        map.put("expires_in", String.valueOf(ASSOC_SECONDS));
        map.put("session_type", session_type);

        // using dh-sha1 to encrypt the session
        if (dhRequested)
        {
            BigInteger consumerPublicKey = decodeBigInteger(dh_consumer_public);

            if (consumerPublicKey == null) {
                throw new BadRequestException("must specify openid.dh_consumer_public for " +
                 SESSION_DH_SHA1 + " session type");
            }

            // get diffie-hellman prime modulus from parameter (or default if not specified)
            BigInteger p = (dh_modulus != null ? decodeBigInteger(dh_modulus) : DH_DEFAULT_P);

            // get diffie-hellman generator from parameter (or default if not specified)
            BigInteger g = (dh_gen != null ? decodeBigInteger(dh_gen) : DH_DEFAULT_G);

            // securely generate a diffie-hellman keypair
            KeyPairGenerator generator;

            try {
                generator = KeyPairGenerator.getInstance("DH");
            }

            catch (NoSuchAlgorithmException nsae) {
                throw new ServletException(nsae);
            }

            DHParameterSpec params = new DHParameterSpec(p, g);
            
            try {
                generator.initialize(params);
            }

            catch (InvalidAlgorithmParameterException iape) {
                throw new ServletException(iape);
            }

            KeyPair keyPair = generator.generateKeyPair();

            // manually calculate dh secret and digest to create key to xor with openid shared secret
// TODO: could I have achieved this using the jca instead of manually? i can't see how... :(
            byte[] xorKey = sha1(consumerPublicKey.modPow(((DHPrivateKey)keyPair.getPrivate()).getX(), p).toByteArray());

            map.put("dh_server_public", ((DHPublicKey)keyPair.getPublic()).getY());
            map.put("enc_mac_key", xor(xorKey, secret.getEncoded()));
        }

        // not using dh-sha1 to encrypt the hmac shared secret
        else {
            map.put("mac_key", encode(secret));
        }

        mapResponse(response, map);
    }

    /**
     *    Returns the hashed message authentication code for a set of key-value pairs.
     *
     *    @param secret TODO.
     *    @param map TODO.
     *    @return TODO.
     *    @throws ServletException TODO.
     */
    public static byte[] mapHMAC(SecretKey secret, LinkedHashMap map)
    throws ServletException
    {
        Mac mac;

        try {
            mac = Mac.getInstance(HMAC_ALGORITHM);
        }

        catch (NoSuchAlgorithmException nsae) {
            throw new ServletException(nsae);
        }

        try {
            mac.init(secret);
        }

        catch (InvalidKeyException ike) {
            throw new ServletException(ike); 
        }
 
        return mac.doFinal(mapBytes(map));
    }

    /**
     *    Ask an identity provider if an end user owns the claimed identifier,
     *    getting back an immediate "yes" or "can't say" answer.
     *
     *    @param request TODO.
     *    @param response TODO.
     *    @throws BadRequestException TODO.
     *    @throws IOException TODO.
     *    @throws ServletException TODO.
     */
    public void checkid_immediate(HttpServletRequest request, HttpServletResponse response)
    throws BadRequestException, IOException, ServletException
    {
        // checkid_immediate is only sent via get method
        requireMethod(request, METHOD_GET);

        // checkid_immediate request parameters
        String identity = getRequiredParameter(request, "openid.identity");
        String assoc_handle = getOptionalParameter(request, "openid.assoc_handle");
        String return_to = getRequiredParameter(request, "openid.return_to");
        String trust_root = getRequiredParameter(request, "openid.trust_root");

// TODO: support for trust_root

        // if user does not have a valid session or doesn't match, checkid_setup is required to login
        if (!identityMatches(getPrincipal(request), identity))
        {
            // create parameters to pass to setup url
            LinkedHashMap setup = new LinkedHashMap();
            setup.put("openid.mode", "checkid_setup");
            setup.put("openid.identity", identity);
            setup.put("openid.assoc_handle", assoc_handle);
            setup.put("openid.return_to", return_to);
            setup.put("openid.trust_root", trust_root);

            String setupURL = getAMServerURL().append(HARD_CODED_OPENID_URI).toString();

            // generate response for failed assertion
            LinkedHashMap map = new LinkedHashMap();
            map.put("openid.mode", "id_res");
            map.put("openid.user_setup_url", mapURL(setupURL, setup));

            // redirect back to consumer with user_setup_url response
            response.sendRedirect(mapURL(return_to, map));
            return;
        }

        positiveAssertion(request, response);
    }
    
    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param response TODO.
     *    @throws BadRequestException TODO.
     *    @throws IOException TODO.
     *    @throws ServletException
     */
    private static void positiveAssertion(HttpServletRequest request, HttpServletResponse response)
    throws BadRequestException, IOException, ServletException
    {
        String identity = getRequiredParameter(request, "openid.identity");
        String assoc_handle = getOptionalParameter(request, "openid.assoc_handle");
        String return_to = getRequiredParameter(request, "openid.return_to");

        // won't be sent in response unless it gets populated with a value
        String invalidate_handle = null;

        // extract the shared secret from the association handle, if possible
        SecretKey secret = getSecretFromHandle(assoc_handle, HANDLE_DONTCARE);

        // handle was not provided, was not valid or has expired
        if (secret == null)
        {
            // indicate that the handle provided was not accepted by the provider
            invalidate_handle = assoc_handle;

            // generate a new shared secret and store in new association handle, to be returned
            secret = generateSecret();
            assoc_handle = generateHandle(HANDLE_STATELESS, calculateExpiry(), secret);
        }

        // generate the set of key-value pairs to sign with the hmac
        LinkedHashMap sig = new LinkedHashMap();
        sig.put("identity", identity);
        sig.put("return_to", return_to);

        // generate the response parameters
        LinkedHashMap map = new LinkedHashMap();
        map.put("openid.mode", "id_res");
        map.put("openid.identity", identity);
        map.put("openid.assoc_handle", assoc_handle);
        map.put("openid.return_to", return_to);
        map.put("openid.signed", mapKeys(sig));
        map.put("openid.sig", mapHMAC(secret, sig));
        map.put("openid.invalidate_handle", invalidate_handle);

        // respond by redirecting back to consumer through user agent
        response.sendRedirect(mapURL(return_to, map));
    }

    /**
     *    Create a URL to redirect to based on the supplied URL base and key-value
     *    pairs as parameters.
     *
     *    @param url TODO.
     *    @param map TODO.
     *    @return TODO.
     *    @throws ServletException TODO.
     */
    private static String mapURL(String baseURL, LinkedHashMap map)
    throws ServletException
    {
        StringBuffer buf = new StringBuffer(baseURL);

        // preserve query string when appending any response parameters
        char nextDelim = (baseURL.indexOf('?') != -1 ? '&' : '?');

        for (Iterator i = map.keySet().iterator(); i.hasNext();)
        {
            String key = (String)i.next();
            Object value = map.get(key);

            // only include the key-value pair if there is a value
            if (value != null)
            {
                buf.append(nextDelim).append(key).append('=');
                
                try {
                    buf.append(URLEncoder.encode(encode(value), "UTF-8"));
                }

                catch (UnsupportedEncodingException uee) {
                    throw new ServletException(uee);
                }

                nextDelim = '&';
            }
        }
        
        return buf.toString();
    }
    
    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param response TODO.
     *    @throws BadRequestException TODO.
     *    @throws IOException TODO.
     *    @throws ServletException TODO.
     */
    public void checkid_setup(HttpServletRequest request, HttpServletResponse response)
    throws BadRequestException, IOException, ServletException
    {
        // checkid_setup is only sent via get
        requireMethod(request, METHOD_GET);

        // checkid_setup request parameters
        String identity = getRequiredParameter(request, "openid.identity");
        String assoc_handle = getOptionalParameter(request, "openid.assoc_handle");
        String return_to = getRequiredParameter(request, "openid.return_to");
        String trust_root = getRequiredParameter(request, "openid.trust_root");

// TODO: support for trust_root

        Principal principal = getPrincipal(request);
        
        // no principal means no session; user must authenticate first
        if (principal == null)
        {
            redirectToLogin(request, response);
            return;
        }

        // non-match of current user results in failed assertion (cancellation)
        if (!identityMatches(principal, identity))
        {
            LinkedHashMap map = new LinkedHashMap();
            map.put("openid.mode", "cancel");
            response.sendRedirect(mapURL(return_to, map));
            return;
        }

        positiveAssertion(request, response);
    }

    /**
     *    TODO: Description.
     *
     *    @param request TODO.
     *    @param response TODO.
     *    @throws BadRequestException TODO.
     *    @throws IOException TODO.
     *    @throws ServletException TODO.
     */
    public void check_authentication(HttpServletRequest request, HttpServletResponse response)
    throws BadRequestException, IOException, ServletException
    {
        // check_authentication is only sent via post method
        requireMethod(request, METHOD_POST);

        // start off assuming that the signature is invalid
        Boolean valid = Boolean.FALSE;

        String assoc_handle = getRequiredParameter(request, "openid.assoc_handle");
        String sig = getRequiredParameter(request, "openid.sig");
        String signed = getRequiredParameter(request, "openid.signed");
        String invalidate_handle = getOptionalParameter(request, "openid.invalidate_handle");

        // only validates signatures with stateless association handles
        SecretKey secret = getSecretFromHandle(assoc_handle, HANDLE_STATELESS);

        // continue processing signature only if association handle is valid
        if (secret != null)
        {        
            LinkedHashMap values = new LinkedHashMap();

            String[] split = signed.split(",");

            for (int n = 0; n < split.length; n++) {
                values.put(split[n], request.getParameter("openid." + split[n]));
            }

            // generate signature and encode in same format received from client
            String hmac = encode(mapHMAC(secret, values));

            // compare calculated hmac to that provided by client
            if (hmac.equals(sig)) {
                valid = Boolean.TRUE;
            }
        }

        // response key-values
        LinkedHashMap map = new LinkedHashMap();

        map.put("openid.mode", "id_res");
        map.put("is_valid", valid);

        // process request to invalidate handle
        if (invalidate_handle != null && getSecretFromHandle(invalidate_handle, HANDLE_ASSOCIATED) == null) {
            map.put("invalidate_handle", invalidate_handle);
        }

        mapResponse(response, map);
    }
}

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
 * $Id: CheckidQuery.java,v 1.1 2007-04-30 01:28:29 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class CheckidQuery extends Message
{
    /** The association handle from the association request. */
    private AssocHandle assocHandle = null;

    /** Claimed identifier. */
    private URL identity = null;

    /** Simple registration extension fields consumer is requesting. */
    private List<String> optional = null;

    /** URL consumer provides to give end user place to read privacy policy. */
    private URL policyURL = null;

    /** Simple registration extension fields the consumer requires. */
    private List<String> required = null;

    /** URL where provider should return the user-agent back to. */
    private URL returnTo = null;

    /** The URL which the end user shall actually see to approve. */
    private URL trustRoot = null;

    /**
     * TODO: Description.
     */
    protected CheckidQuery() {
        super();
    }

    /**
     * TODO: Description.
     *
     * @param path TODO.
     * @return TODO.
     */
    private static String path(URL url)
    {
        String path = url.getPath();

        if (path == null || path.length() == 0) {
            return "/";
        }

        // for normalization puposes, assume path is always a directory
        if (!path.endsWith("/")) {
            return path + "/";
        }

        return path;
    }

    /**
     * TODO: Description.
     *
     * @param url TODO.
     * @return TODO.
     */
    private static int port(URL url)
    {
        int port = url.getPort();

        if (port != -1) {
            return port;
        }

        return url.getDefaultPort();
    }

    /**
     * TODO: Description.
     *
     * @param url TODO.
     * @return TODO.
     */
    private static String protocol(URL url) {
        return url.getProtocol();
    }

    /**
     * Returns true if the passed URL contains a wildcard domain that is
     * overly general. Overly general trust roots can be dangerous when used
     * for identifying a particular consumer.
     *
     * @param url TODO.
     * @return TODO.
     */
    private static boolean isOverlyGeneral(URL url)
    {
// TODO: persistent trust: prevent overly simplistic trust roots (e.g. http://*.com)
        return false;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    private boolean hostsMatch()
    {
        String trustHost = trustRoot.getHost();

        if (trustHost == null || trustHost.length() == 0) {
            return false;
        }

        String returnHost = returnTo.getHost();

        if (returnHost == null || returnHost.length() == 0) {
            return false;
        }

        // handle easy (non-wildcard) case first
        if (!trustHost.startsWith("*.")) {
            return trustHost.equalsIgnoreCase(returnHost);
        }

        // handle harder (wildcard) case
        return ("." + returnHost.toLowerCase()).endsWith(
         trustHost.substring(1).toLowerCase());
    }

    /**
     * TODO: Description.
     *
     * @throws DecodeException TODO.
     */
    private boolean isValidTrustRoot()
    {
        if (isOverlyGeneral(trustRoot)) {
            return false;
        }

        // the trust root must not contain a URI fragment
        if (trustRoot.getRef() != null) {
            return false;
        }

        // protcols must be identical
        if (!protocol(trustRoot).equals(protocol(returnTo))) {
            return false;
        }

        // hosts must match identically or by wildcard
        if (!hostsMatch()) {
            return false;
        }

        // ports must match explicitly or based-on protocol
        if (port(trustRoot) != port(returnTo)) {
            return false;
        }

        // trust root path must be base of return to path
        if (!path(returnTo).startsWith(path(trustRoot))) {
            return false;
        }

        // all hurdles overcome
        return true;
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
    public URL getIdentity() {
        return identity;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public List<String> getOptional() {
        return optional;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public URL getPolicyURL() {
        return policyURL;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public List<String> getRequired() {
        return required;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public URL getReturnTo() {
        return returnTo;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public URL getTrustRoot() {
        return trustRoot;
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
    public void setIdentity(URL value) {
        identity = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setOptional(List<String> value) {
        optional = value;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public void setPolicyURL(URL value) {
        policyURL = value;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public void setRequired(List<String> value) {
        required = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setReturnTo(URL value) {
        returnTo = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setTrustRoot(URL value) {
        trustRoot = value;
    }

    /**
     * TODO: Description.
     *
     * @throws BadRequestException if validation fails.
     */
    protected void decode(Map<String,String> map)
    throws DecodeException
    {
        // validate parameters common to all OpenID messages
        super.decode(map);

        identity = Codec.decodeURL(map.get("identity"));

        if (identity == null) {
            throw new DecodeException("identity is required");
        }

        assocHandle = AssocHandle.decode(map.get("assoc_handle"));

        returnTo = Codec.decodeURL(map.get("return_to"));

        if (returnTo == null) {
            throw new DecodeException("return_to is required");
        }

        trustRoot = Codec.decodeURL(map.get("trust_root"));

        if (trustRoot == null) {
            trustRoot = returnTo;
        }

        // ensure trust root and return to URLs are consistent
        if (!isValidTrustRoot()) {
            throw new DecodeException("invalid trust_root");
        }

        required = Codec.decodeList(map.get("sreg.required"));

        optional = Codec.decodeList(map.get("sreg.optional"));

        policyURL = Codec.decodeURL(map.get("policy_url"));
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public Map<String,String> encode()
    {
        Map<String,String> map = super.encode();

        if (identity != null) {
            map.put("identity", Codec.encodeURL(identity));
        }

        if (assocHandle != null) {
            map.put("assoc_handle", assocHandle.encode());
        }

        if (returnTo != null) {
            map.put("return_to", Codec.encodeURL(returnTo));
        }

        if (trustRoot != null) {
            map.put("trust_root", Codec.encodeURL(trustRoot));
        }

        if (required != null) {
            map.put("sreg.required", Codec.encodeList(required));
        }

        if (optional != null) {
            map.put("sreg.optional", Codec.encodeList(optional));
        }

        if (policyURL != null) {
            map.put("sreg.policy_url", Codec.encodeURL(policyURL));
        }

        return map;
    }
}

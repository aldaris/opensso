/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the terms
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
 * $Id: AmTomcatRealm.java,v 1.2 2008-11-28 12:35:32 saueree Exp $
 */


package com.sun.identity.agents.tomcat.v6;

import com.sun.identity.agents.arch.IModuleAccess;
import com.sun.identity.agents.arch.ModuleAccess;
import com.sun.identity.agents.realm.AmRealmAuthenticationResult;
import com.sun.identity.agents.realm.AmRealmManager;
import com.sun.identity.agents.realm.IAmRealm;

import org.apache.catalina.realm.RealmBase;

import java.lang.UnsupportedOperationException;

import java.security.Principal;
import java.security.cert.X509Certificate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * <b>AmTomcatRealm</b> is the facade for an underlying security realm used to
 * authenticate Identity server users to Tomcat. Realms can be attached at any
 * Container level, but will typically only be attached to a Context, or higher
 * level, Container.
 *
 */
public class AmTomcatRealm extends RealmBase {
    private static IAmRealm _amRealm = null;
    private static IModuleAccess _moduleAccess = null;

    static {
        try {
            
            _amRealm = AmRealmManager.getAmRealmInstance();
            _moduleAccess = AmRealmManager.getModuleAccess();

            if ((_moduleAccess != null)
                    && _moduleAccess.isLogMessageEnabled()) {
                _moduleAccess.logMessage(
                    "AmTomcatRealm: Realm Initialized");
            }
        } catch (Exception ex) {
            if ((_moduleAccess != null)
                    && _moduleAccess.isLogWarningEnabled()) {
                _moduleAccess.logError(
                    "AmTomcatRealm: Realm Instantiation Error: " + ex);
            }
        }
    }

    /** Descriptive information about this Realm implementation */
    private final String _info = 
            "AmTomcatRealm - Realm implementation for Tomcat ";

    /**
     * The <code> AmTomcatRealm </code> returns the AmTomcatUser associated with
     * the specified username and credentials; otherwise returns
     * <code>null</code>.
     *
     * @param username
     *            Username of the Principal to look up
     * @param credentials
     *            Password or other credentials to use in authenticating this
     *            username
     */
    public Principal authenticate(
        String username,
        String credentials) {
        AmTomcatUser tomcatUser = null;

        try {
           /* _amRealm = AmRealmManager.getAmRealmInstance();
            _moduleAccess = AmRealmManager.getModuleAccess();
            */
            AmRealmAuthenticationResult result = _amRealm.authenticate(
                    username,
                    credentials);

            if ((result == null) || (!result.isValid())) {
                if ((_moduleAccess != null)
                        && _moduleAccess.isLogMessageEnabled()) {
                    _moduleAccess.logMessage(
                        "AmTomcatRealm: Authentication FAILED for "
                        + username);
                }
            } else {
                tomcatUser = new AmTomcatUser(username);

                if ((_moduleAccess != null)
                        && _moduleAccess.isLogMessageEnabled()) {
                    _moduleAccess.logMessage(
                        "AmTomcatRealm: Authentication SUCCESSFUL for "
                        + username);
                }

                if ((_moduleAccess != null)
                        && _moduleAccess.isLogMessageEnabled()) {
                    Set roles = result.getAttributes();

                    if ((roles != null) && (roles.size() > 0)) {
                        Iterator it = roles.iterator();
                        StringBuffer bufRoles = new StringBuffer();

                        while (it.hasNext()) {
                            String role = (String) it.next();
                            bufRoles.append(role);
                            bufRoles.append(" ");
                        }

                        _moduleAccess.logMessage(
                            "AmTomcatRealm: User " + username
                            + " has roles: " + bufRoles.toString());
                    }
                }
            }
        } catch (Exception ex) {
            if (_moduleAccess != null) {
                _moduleAccess.logError(
                    "AmTomcatRealm: encountered exception "
                    + ex.getMessage() + " while authenticating user "
                    + username,
                    ex);
            }
        }

        return tomcatUser;
    }

    /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * @param username
     *            Username of the Principal to look up
     * @param credentials
     *            Password or other credentials to use in authenticating this
     *            username
     */
    public Principal authenticate(
        String username,
        byte[] credentials) {
        String password = new String(credentials);

        return authenticate(
            username,
            password);
    }

    /**
     * Return the Principal associated with the specified username, which
     * matches the digest calculated using the given parameters using the method
     * described in RFC 2069; otherwise return <code>null</code>.
     *
     * @param username
     *            Username of the Principal to look up
     * @param digest
     *            Digest which has been submitted by the client
     * @param nonce
     *            Unique (or supposedly unique) token which has been used for
     *            this request
     * @param realm
     *            Realm name
     * @param md5a2
     *            Second MD5 digest used to calculate the digest : MD5(Method +
     *            ":" + uri)
     */
    public Principal authenticate(
        String username,
        String digest,
        String nonce,
        String nc,
        String cnonce,
        String qop,
        String realm,
        String md5a2) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the Principal associated with the specified chain of X509 client
     * certificates. If there is none, return <code>null</code>.
     *
     * @param certs
     *            Array of client certificates, with the first one in the array
     *            being the certificate of the client itself.
     */
    public Principal authenticate(X509Certificate[] certs) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return <code>true</code> if the specified user belongs to the specified
     * security role; otherwise return <code>false</code>.
     *
     * @param principal
     *            Principal for whom the role is to be checked
     * @param role
     *            Security role to be checked
     */
    public boolean hasRole(
        Principal tomcatUser,
        String role) {
        String username = null;
        boolean hasRole = false;
        Set setRoles = null;

        try {
           /* _amRealm = AmRealmManager.getAmRealmInstance();
            _moduleAccess = AmRealmManager.getModuleAccess();
            */
            if (tomcatUser != null) {
                username = tomcatUser.getName();
            }

            if ((role != null) && (username != null)) {
                setRoles = _amRealm.getMemberships(username);

                if ((setRoles != null) && (setRoles.size() > 0)) {
                    hasRole = setRoles.contains(role);

                    if ((_moduleAccess != null)
                            && _moduleAccess.isLogMessageEnabled()
                            && hasRole) {
                        _moduleAccess.logMessage(
                            "AmTomcatRealm: " + username
                            + " has secuity role " + role);
                    } else {
                        if ((_moduleAccess != null)
                                && _moduleAccess.isLogMessageEnabled()) {
                            Iterator it = setRoles.iterator();
                            StringBuffer roleList = new StringBuffer();

                            while (it.hasNext()) {
                                roleList.append((String) it.next());
                                roleList.append(" ");
                            }

                            _moduleAccess.logMessage(
                                "AmTomcatRealm: " + username
                                + " has roles : " + roleList.toString());
                        }
                    }
                }
            }

            if (!hasRole && (_moduleAccess != null)
                    && _moduleAccess.isLogMessageEnabled()) {
                _moduleAccess.logMessage(
                    "AmTomcatRealm: " + username + " does not have role "
                    + role);
            }
        } catch (Exception ex) {
            if (_moduleAccess != null) {
                _moduleAccess.logError(
                    "AmTomcatRealm: encountered exception "
                    + ex.getMessage() + " while fetching roles for user "
                    + username,
                    ex);
            }
        }

        return hasRole;
    }

    /**
     * Return the Principal associated with the given user name.
     */
    protected Principal getPrincipal(String username) {
        return new AmTomcatUser(username);
    }

    /**
     * Return descriptive information about this Realm implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getName() {
        return _info;
    }

    /**
     * Return the password associated with the given principal's user name.
     */
    protected String getPassword(String username) {
        return (null);
    }

    private IAmRealm getAMRealm() {
        return _amRealm;
    }

    private void setAMRealm(IAmRealm amRealm) {
        _amRealm = amRealm;
    }

    private IModuleAccess getModuleAccess() {
        return _moduleAccess;
    }

    private void setModuleAccess(IModuleAccess moduleAccess) {
        _moduleAccess = moduleAccess;
    }
}

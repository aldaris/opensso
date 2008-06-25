/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: CachedSubEntries.java,v 1.8 2008-06-25 05:44:03 qcheng Exp $
 *
 */

package com.sun.identity.sm;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.am.util.Cache;
import com.sun.identity.common.CaseInsensitiveHashMap;
import com.sun.identity.shared.debug.Debug;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import netscape.ldap.util.DN;

public class CachedSubEntries {
    // Cache of SMSEntries to obtain sub entries
    protected static HashMap smsEntries = new CaseInsensitiveHashMap(100);

    // Instance variables
    protected Map ssoTokenToSubEntries = new Cache(100);

    protected CachedSMSEntry cEntry;

    protected SMSEntry entry;

    protected String notificationID;

    // Debug & I18n variables
    private static Debug debug = SMSEntry.debug;

    // Private constructor, can be instantiated only via getInstance
    private CachedSubEntries(SSOToken t, String dn) throws SMSException {
        try {
            cEntry = CachedSMSEntry.getInstance(t, dn, null);
            entry = cEntry.smsEntry;
            update();
            Class c = this.getClass();
            notificationID = SMSEventListenerManager.notifyChangesToSubNodes(
                t, dn, c.getDeclaredMethod("update", (Class[])null), this,null);
        } catch (NoSuchMethodException e) {
            // this should not happen
            debug.error("CachedSubEntries: unable to register "
                    + "for notifications: ", e);
        } catch (SSOException ssoe) {
            // invalid ssoToken
            debug.warning("CachedSubEntries::init Invalid SSOToken", ssoe);
            throw (new SMSException(SMSEntry.bundle
                    .getString("sms-INVALID_SSO_TOKEN"),
                    "sms-INVALID_SSO_TOKEN"));
        }
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries::init: " + dn);
        }
    }

    protected Set getSubEntries(SSOToken t) throws SMSException, SSOException {
        String tokenID = t.getTokenID().toString();
        Set subEntries = (Set) ssoTokenToSubEntries.get(tokenID);
        if (!SMSEntry.cacheSMSEntries || subEntries == null) {
            // Since caching is disabled, do a lookup
            subEntries = getSubEntries(t, "*");
        }
        // Add to cache
        if (SMSEntry.cacheSMSEntries) {
            ssoTokenToSubEntries.put(tokenID, subEntries);
            Set answer = new TreeSet();
            answer.addAll(subEntries);
            subEntries = answer;
        }
        return (subEntries);
    }

    public Set getSubEntries(SSOToken token, String pattern)
            throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries: reading sub-entries DN: " + 
               cEntry.dn2Str + " pattern: " + pattern);
        }
        return (entry.subEntries(token, pattern, 0, true, true));
    }

    protected Set getSchemaSubEntries(String pattern, String serviceidPattern)
            throws SMSException, SSOException {
        // Get a valid SSOToken to perform the search
        SSOToken token = cEntry.getValidSSOToken();
        if (token == null) {
            // Need to remove this entry from the cache
            synchronized (smsEntries) {
                smsEntries.remove(cEntry.dnRFCStr);
            }
            SMSEventListenerManager.removeNotification(notificationID);
            throw (new SMSException(SMSEntry.bundle
                    .getString("sms-INVALID_SSO_TOKEN"),
                    "sms-INVALID_SSO_TOKEN"));
        }
        return (getSchemaSubEntries(token, pattern, serviceidPattern));
    }

    public Set getSchemaSubEntries(SSOToken token, String pattern,
            String serviceidPattern) throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries: reading sub-entries DN: " + 
                cEntry.dn2Str + " pattern: " + serviceidPattern);
        }
        return (entry.schemaSubEntries(token, pattern, serviceidPattern, 0,
                true, true));
    }

    protected void add(String entry) {
        // Clear the cache, will be updated in the next lookup
        ssoTokenToSubEntries = new Cache(100);
    }

    protected void remove(String entry) {
        // Clear the cache, will be updated in the next lookup
        ssoTokenToSubEntries = new Cache(100);
    }

    protected boolean isEmpty(SSOToken t) throws SMSException, SSOException {
        return (getSubEntries(t).isEmpty());
    }

    protected boolean contains(SSOToken t, String entry) throws SMSException,
            SSOException {
        return (getSubEntries(t).contains(entry));
    }

    protected SMSEntry getSMSEntry() {
        return (entry);
    }

    protected void update() {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries::update called for dn: " 
                + cEntry.dn2Str);
        }
        // Clear the cache, will be updated in the next lookup
        ssoTokenToSubEntries = new Cache(100);
    }

    protected void finalize() throws Throwable {
        SMSEventListenerManager.removeNotification(notificationID);
    }

    public static CachedSubEntries getInstance(SSOToken token, String dn)
            throws SMSException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries::getInstance DN: " + dn);
        }
        String entry = (new DN(dn)).toRFCString();
        CachedSubEntries answer = null;
        synchronized (smsEntries) {
            answer = (CachedSubEntries) smsEntries.get(entry);
        }
        if (answer == null) {
            answer = new CachedSubEntries(token, dn);
            CachedSubEntries tmp;
            synchronized (smsEntries) {
                if ((tmp = (CachedSubEntries) smsEntries.get(entry)) == null) {
                    // Not present in cached, add the created one to cache
                     smsEntries.put(entry, answer);
                } else {
                    answer = tmp;
                }
            }
        } else {
            // We need to add the SSOToken to the CachedSMSEntry
            // to keep track of valid SSOTokens
            answer.cEntry.addPrincipal(token);
        }
        return (answer);
    }

    public Set searchSubOrgNames(SSOToken token, String pattern,
            boolean recursive) throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries: reading subOrgNames DN: " + 
                cEntry.dn2Str + " pattern: " + pattern);
        }
        if (token == null) {
            // Need to remove this entry from the cache
            synchronized (smsEntries) {
                smsEntries.remove(cEntry.dnRFCStr);
            }
            SMSEventListenerManager.removeNotification(notificationID);
            throw (new SMSException(SMSEntry.bundle
                    .getString("sms-INVALID_SSO_TOKEN"),
                    "sms-INVALID_SSO_TOKEN"));
        }
        boolean sortResults = true;
        boolean sortOrder = true;
        if (recursive) {
            sortResults = false;
            sortOrder = false;
        }
        return (entry.searchSubOrgNames(token, pattern, 0, sortResults,
                sortOrder, recursive));
    }

    public Set searchOrgNames(SSOToken token, String serviceName,
            String attrName, Set values) throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries: reading orgNames DN: " + 
                cEntry.dn2Str + " attrName: " + attrName);
        }

        return (entry.searchOrganizationNames(token, 0, true, true,
                serviceName, attrName, values));
    }

    static void clearCache() {
        synchronized (smsEntries) {
            smsEntries.clear();
        }
    }
}

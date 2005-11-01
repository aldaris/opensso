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
 * $Id: CachedSubEntries.java,v 1.1 2005-11-01 00:31:21 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import netscape.ldap.util.DN;

import com.iplanet.am.util.Cache;
import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.common.CaseInsensitiveHashMap;

class CachedSubEntries {
    // Cache of SMSEntries to obtain sub entries
    protected static HashMap smsEntries = new CaseInsensitiveHashMap(100);

    // Instance variables
    protected Map ssoTokenToSubEntries = new Cache(100);

    protected CachedSMSEntry cEntry;

    protected SMSEntry entry;

    protected DN dn;

    protected String notificationID;

    // Debug & I18n variables
    private static Debug debug = SMSEntry.debug;

    // Private constructor, can be instantiated only via getInstance
    private CachedSubEntries(SSOToken t, String dn) throws SMSException {
        try {
            cEntry = CachedSMSEntry.getInstance(t, dn, null);
            entry = cEntry.smsEntry;
            this.dn = cEntry.dn;
            update();
            Class c = this.getClass();
            notificationID = SMSEventListenerManager.notifyChangesToSubNodes(t,
                    dn, c.getDeclaredMethod("update", null), this, null);
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

    protected Set getSubEntries(SSOToken token, String pattern)
            throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries: reading sub-entries DN: " + dn
                    + " pattern: " + pattern);
        }
        return (entry.subEntries(token, pattern, 0, true, true));
    }

    protected Set getSchemaSubEntries(String pattern, String serviceidPattern)
            throws SMSException, SSOException {
        // Get a valid SSOToken to perform the search
        SSOToken token = cEntry.getValidSSOToken();
        if (token == null) {
            // Need to remove this entry from the cache
            smsEntries.remove(dn.toRFCString());
            SMSEventListenerManager.removeNotification(notificationID);
            throw (new SMSException(SMSEntry.bundle
                    .getString("sms-INVALID_SSO_TOKEN"),
                    "sms-INVALID_SSO_TOKEN"));
        }
        return (getSchemaSubEntries(token, pattern, serviceidPattern));
    }

    protected Set getSchemaSubEntries(SSOToken token, String pattern,
            String serviceidPattern) throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries: reading sub-entries DN: " + dn
                    + " pattern: " + serviceidPattern);
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
            debug.message("CachedSubEntries::update called for dn: " + dn);
        }
        // Clear the cache, will be updated in the next lookup
        ssoTokenToSubEntries = new Cache(100);
    }

    protected void finalize() throws Throwable {
        SMSEventListenerManager.removeNotification(notificationID);
    }

    static CachedSubEntries getInstance(SSOToken token, String dn)
            throws SMSException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries::getInstance DN: " + dn);
        }
        String entry = (new DN(dn)).toRFCString();
        CachedSubEntries answer = null;
        answer = (CachedSubEntries) smsEntries.get(entry);
        if (answer == null) {
            synchronized (smsEntries) {
                if ((answer = (CachedSubEntries) smsEntries.get(entry)) == null)
                {
                    // Not present in cached, create a new instance
                    answer = new CachedSubEntries(token, dn);
                    smsEntries.put(entry, answer);
                }
            }
        } else {
            // We need to add the SSOToken to the CachedSMSEntry
            // to keep track of valid SSOTokens
            answer.cEntry.addPrincipal(token);
        }
        return (answer);
    }

    protected Set searchSubOrgNames(SSOToken token, String pattern,
            boolean recursive) throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries: reading subOrgNames DN: " + dn
                    + " pattern: " + pattern);
        }
        if (token == null) {
            // Need to remove this entry from the cache
            smsEntries.remove(dn.toRFCString());
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

    protected Set searchOrgNames(SSOToken token, String serviceName,
            String attrName, Set values) throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug.message("CachedSubEntries: reading orgNames DN: " + dn
                    + " attrName: " + attrName);
        }

        return (entry.searchOrganizationNames(token, 0, true, true,
                serviceName, attrName, values));
    }
}

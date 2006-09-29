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
 * $Id: AmRealmMemebershipCache.java,v 1.1 2006-09-29 00:04:50 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.realm;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOTokenEvent;
import com.iplanet.sso.SSOTokenListener;
import com.sun.identity.agents.arch.AgentBase;
import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Manager;
import com.sun.identity.agents.common.SSOValidationResult;


/**
 * The agent realm cache that stores the user membership information
 */
public class AmRealmMemebershipCache extends AgentBase 
implements IAmRealmMembershipCache {
    
    public AmRealmMemebershipCache(Manager manager) throws AgentException {
        super(manager);
    }
    
    public void initialize() throws AgentException {
        // No initialization required
    }
    
    public Set getMembershipFromCache(String userName) {
        Set result = null;
        synchronized(LOCK) {
            result = (Set) getMembershipCache().get(userName);
        }
        
        if (result == null) {
            result = Collections.EMPTY_SET;
            if (isLogWarningEnabled()) {
                logWarning("AmRealmMembershipCache: No memberships found for: "
                        + userName + ", may be expired!");
            }
        }
        
        if (isLogMessageEnabled()) {
            logMessage("AmRealmMemebershipCache: Lookup for " + userName 
                    + " found memeberships: " + result);
        }
        
        return result;
    }
    
    public void addMembershipCacheEntry(String userName, Set membership, 
            SSOValidationResult ssoValidationResult) throws AgentException
    {
        try {
            ssoValidationResult.getSSOToken().addSSOTokenListener(
                new AmRealmMemebershipCacheListener(userName));
            synchronized(LOCK) {
                getMembershipCache().put(userName, membership);

                if(isLogMessageEnabled()) {
                    logMessage("AmRealmMembershipCache: cache size = "
                               + getMembershipCache().size());
                }
            }
        } catch (Exception ex) {
            throw new AgentException("Failed to add cache entry", ex);
        }
    }
    
    private void removeMemebershipCacheEntry(String userName) {
        synchronized(LOCK) {
            getMembershipCache().remove(userName);
            if(isLogMessageEnabled()) {
                logMessage("AmRealmMembershipCache: removed expired memebership"
                        + " cache entry for user: " + userName
                        + ", cache size = " + getMembershipCache().size());
            }
        }    
    }
    
    class AmRealmMemebershipCacheListener implements SSOTokenListener {
        
        AmRealmMemebershipCacheListener(String userName) {
            setUserName(userName);
        }
        
        public void ssoTokenChanged(SSOTokenEvent ssoTokenEvent) {

            removeMemebershipCacheEntry(getUserName());

            if(isLogMessageEnabled()) {
                try {
                    int    type    = ssoTokenEvent.getType();
                    String typeStr = null;

                    switch(type) {

                    case SSOTokenEvent.SSO_TOKEN_DESTROY :
                        typeStr = "SSO_TOKEN_DESTROY";
                        break;

                    case SSOTokenEvent.SSO_TOKEN_IDLE_TIMEOUT :
                        typeStr = "SSO_TOKEN_IDLE_TIMEOUT";
                        break;

                    case SSOTokenEvent.SSO_TOKEN_MAX_TIMEOUT :
                        typeStr = "SSO_TOKEN_MAX_TIMEOUT";
                        break;

                    default :
                        typeStr = "UNKNOWN TYPE EVENT = " + type;
                        break;
                    }

                    logMessage("AmRealmMemebershipCacheListener: User " 
                            + getUserName()
                               + " Token Event: " + typeStr);
                } catch(SSOException ssoEx) {
                    if(isLogWarningEnabled()) {
                        logWarning(
                            "AmRealmMemebershipCacheListener: Exception caught",
                            ssoEx);
                    }
                }
            }
        }
        
        private void setUserName(String userName) {
            _userName = userName;
        }
        
        private String getUserName() {
            return _userName;
        }
        
        private String _userName;
    }
    
    /////////////////////////////////////////////////////////////////////
    // Static Methods and Fields
    /////////////////////////////////////////////////////////////////////
    
    private static Hashtable getMembershipCache() {
        return _membershipCache;
    }
    
    private static Hashtable _membershipCache = new Hashtable();
    private static final String LOCK = "amRealm.AmRealmMemebershipCache_LOCK";

}

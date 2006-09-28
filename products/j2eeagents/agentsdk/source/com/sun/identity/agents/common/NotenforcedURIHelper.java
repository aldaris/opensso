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
 * $Id: NotenforcedURIHelper.java,v 1.1 2006-09-28 23:25:36 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.common;

import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Module;
import com.sun.identity.agents.arch.SurrogateBase;
import com.sun.identity.agents.util.AgentCache;


/**
 * The class manages the not enforced URIs
 */

public class NotenforcedURIHelper extends SurrogateBase 
        implements INotenforcedURIHelper
{
    public NotenforcedURIHelper(Module module)
    {
        super(module);
    }
    
    public void initialize(String accessDeniedURI, boolean isInverted, 
            boolean cacheEnabled, int maxSize,
            String[] notenforcedURIEntries) throws AgentException
    {
        setAccessDeniedURI(accessDeniedURI);
        setCacheEnabledFlag(cacheEnabled);
        setInvertState(isInverted);

        if(cacheEnabled) {
            setNotenforcedURICache(new AgentCache("NotenforcedURI", maxSize));
            setEnforcedURICache(new AgentCache("EnforcedURI", maxSize));
         } else { //cache is disabled
             if(isLogMessageEnabled()) {
               logMessage("NotenforcedURIHelper: Caching is Disabled");
             }
        }
        CommonFactory cf = new CommonFactory(getModule());
        setMatcher(cf.newPatternMatcher(notenforcedURIEntries));

        if (notenforcedURIEntries.length == 0 && accessDeniedURI == null &&
                !isInverted())
        {
            setActiveState(false);
        } else {
            setActiveState(true);
        }
        
        if (isLogMessageEnabled()) {
            logMessage("NotenforcedURIHelper: accessDeniedURI: " 
                    + accessDeniedURI);
            logMessage("NotenforcedURIHelper: isInverted: " + isInverted());
            logMessage("NotenforcedURIHelper: cacheEnabled: " + cacheEnabled);
            logMessage("NotenforcedURIHelper: cache-size: " + maxSize);
            for (int i=0; i<notenforcedURIEntries.length; i++ ) {
                logMessage("NotenforcedURIHelper: next URI: " + 
                        notenforcedURIEntries[i]);
            }
        }
    }

    public boolean isActive() {
        return _isActive;
    }
    
    public boolean isNotEnforced (String requestURI) {
        boolean result = false;
        String  enforcedCacheEntry  = getEnforcedCacheEntry(requestURI);
        String  notEnforcedCacheEntry = getNotEnforcedCacheEntry(requestURI);

        if(enforcedCacheEntry != null) {
            if(isLogMessageEnabled()) {
                logMessage("NotenforcedURIHelper.isNotEnforced("
                           + requestURI + ") found in enforced cache");
            }
        } else if(notEnforcedCacheEntry != null) {
            result = true;
            if(isLogMessageEnabled()) {
                logMessage("NotenforcedURIHelper.isNotEnforced("
                           + requestURI
                           + ") found in not-enforced cache");
            }
        } else {    //there is no match in cache
            String accessDeniedURI = getAccessDeniedURI();
            if((accessDeniedURI != null) && accessDeniedURI.equals(requestURI))
            {
                result = true;
                if(isLogMessageEnabled()) {
                    logMessage("NotenforcedURIHelper.isNotEnforced("
                               + requestURI
                               + ") is access denied URI: Not Enforced");
                }
            } else {
                if(getMatcher().match(requestURI)) {
                    if(isInverted()) {
                        addToEnforcedURICache(requestURI, requestURI);
                    } else {
                        result = true;
                        addToNotenforcedURICache(requestURI, requestURI);
                    }
                } else {
                    if(isInverted()) {
                        result = true;
                        addToNotenforcedURICache(requestURI, requestURI);
                    } else {
                        addToEnforcedURICache(requestURI, requestURI);
                    }
                }
            }
        }

        if(isLogMessageEnabled()) {
            logMessage("NotenforcedURIHelper.isNotEnforced(" + requestURI
                       + ") => " + result);
        }

        return result;
    }    

    private String getCacheEntry(String key, AgentCache cache) {
       return  ((String) cache.get(key));
    }

    private void addCacheEntry(String key, String value, AgentCache cache) {
        cache.put(key, value);
    }

    private void addToNotenforcedURICache(String key, String value) {
        if(isCacheEnabled()) {
            addCacheEntry(key, value, getNotenforcedURICache());
        }
    }

    private void addToEnforcedURICache(String key, String value) {
        if(isCacheEnabled()) {
            addCacheEntry(key, value, getEnforcedURICache());
        }
    }

    private String getEnforcedCacheEntry(String key) {
        String result = null;
        if(isCacheEnabled()) {
            result = getCacheEntry(key, getEnforcedURICache());
        }
        return result;
    }

    private String getNotEnforcedCacheEntry(String key) {
        String result = null;
        if(isCacheEnabled()) {
            result = getCacheEntry(key, getNotenforcedURICache());
        }
        return result;
    }

    private void setAccessDeniedURI(String accessDeniedURI) {
        _accessDeniedURI = accessDeniedURI;
    }

    private String getAccessDeniedURI() {
        return _accessDeniedURI;
    }

    private void setNotenforcedURICache(AgentCache cache) {
        _notenforcedURICache = cache;
    }

    private void setEnforcedURICache(AgentCache cache) {
        _enforcedURICache = cache;
    }

    private AgentCache getNotenforcedURICache() {
        return _notenforcedURICache;
    }

    private AgentCache getEnforcedURICache() {
        return _enforcedURICache;
    }

    private void setCacheEnabledFlag(boolean cacheEnabled) {
        _cacheEnabled = cacheEnabled;
    }

    private boolean isCacheEnabled() {
        return _cacheEnabled;
    }

    private boolean isInverted() {
        return _isInverted;
    }

    private void setInvertState(boolean isInverted) {
        _isInverted = isInverted;
    }

    private IPatternMatcher getMatcher() {
            return _matcher;
    }

    private void setMatcher(IPatternMatcher matcher) {
        _matcher = matcher;
    }
    
    private void setActiveState(boolean state) {
        _isActive = state;
    }

    private boolean _isInverted;
    private boolean _cacheEnabled;
    private int _maxCacheSize;
    private boolean _isActive;
    private String _accessDeniedURI;
    private AgentCache _notenforcedURICache;
    private AgentCache _enforcedURICache;
    private IPatternMatcher _matcher;
}

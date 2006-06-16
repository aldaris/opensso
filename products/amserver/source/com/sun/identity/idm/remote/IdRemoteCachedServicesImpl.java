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
 * $Id: IdRemoteCachedServicesImpl.java,v 1.1 2006-06-16 19:36:48 rarcot Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm.remote;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;

import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdCachedServices;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdServices;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.idm.common.IdCacheBlock;

import com.iplanet.am.sdk.AMEvent;
import com.iplanet.am.sdk.AMHashMap;
import com.iplanet.am.sdk.common.MiscUtils;
import com.iplanet.am.util.Cache;
import com.iplanet.am.util.SystemProperties;

/*
 * Class which provides caching on top of available IdRepoLDAPServices.
 */
public class IdRemoteCachedServicesImpl extends IdRemoteServicesImpl implements
        IdCachedServices {

    private static int maxSize = 10000;

    private static IdServices instance = null;

    static final String CACHE_MAX_SIZE_KEY = "com.iplanet.am.sdk.cache.maxSize";

    // Class Private
    private Cache idRepoCache;

    // TODO: Add Statistics!
    // private CacheStats cacheStats;

    static {
        initializeParams();
    }

    /**
     * Method to check if caching is enabled or disabled and configure the size
     * of the cache accordingly.
     */
    private static void initializeParams() {
        // Check if the caching property is set in System runtime.
        String cacheSize = SystemProperties.get(CACHE_MAX_SIZE_KEY, "10000");
        try {
            maxSize = Integer.parseInt(cacheSize);
            if (maxSize < 1) {
                maxSize = 10000; // Default
            }
            if (getDebug().messageEnabled()) {
                getDebug().message(
                        "IdRemoteCachedServicesImpl."
                                + "intializeParams() Caching size set to: "
                                + maxSize);
            }
        } catch (NumberFormatException ne) {
            maxSize = 10000;
            getDebug().warning("IdRemoteCachedServicesImpl.initializeParams() "
                    + "- invalid value for cache size specified. Setting "
                    + "to default value: " + maxSize);
        }
    }

    private IdRemoteCachedServicesImpl() {
        super();
        initializeCache();
        // TODO: ADD Cache usage statistics
        // cacheStats = CacheStats.createInstance(getClass().getName(),
        // getDebug());
    }

    private void initializeCache() {
        idRepoCache = new Cache(maxSize);
    }

    /**
     * Method to get the current cache size
     * 
     * @return the size of the SDK LRU cache
     */
    public int getSize() {
        return idRepoCache.size();
    }

    protected static synchronized IdServices getInstance() {
        if (instance == null) {
            getDebug().message("IdRemoteCachedServicesImpl.getInstance(): "
                    + "Creating new Instance of IdRemoteCachedServicesImpl()");
            instance = new IdRemoteCachedServicesImpl();
        }
        return instance;
    }

    /**
     * Method to get the maximum size of the Cache. To be called by all other
     * LRU Caches that are created in AM SDK
     * 
     * @return the maximum cache size for a LRU cache
     */
    protected static int getMaxSize() {
        return maxSize;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n<<<<<<< BEGIN SDK CACHE CONTENTS >>>>>>>>");
        if (!idRepoCache.isEmpty()) { // Should never be null
            Enumeration cacheKeys = idRepoCache.keys();
            while (cacheKeys.hasMoreElements()) {
                String key = (String) cacheKeys.nextElement();
                IdCacheBlock cb = (IdCacheBlock) idRepoCache.get(key);
                sb.append("\nSDK Cache Block: ").append(key);
                sb.append(cb.toString());
            }
        } else {
            sb.append("<empty>");
        }
        sb.append("\n<<<<<<< END SDK CACHE CONTENTS >>>>>>>>");
        return sb.toString();
    }

    // *************************************************************************
    // Update/Dirty methods of this class.
    // *************************************************************************
    private void removeCachedAttributes(String affectDNs, Set attrNames) {
        Enumeration cacheKeys = idRepoCache.keys();
        while (cacheKeys.hasMoreElements()) {
            String key = (String) cacheKeys.nextElement();
            int l1 = key.length();
            int l2 = affectDNs.length();
            if (key.regionMatches(true, (l1 - l2), affectDNs, 0, l2)) {
                // key ends with 'affectDN' string
                IdCacheBlock cb = (IdCacheBlock) idRepoCache.get(key);
                if (cb != null && !cb.hasExpiredAndUpdated() && cb.isExists()) {
                    cb.removeAttributes(attrNames);
                }
            }
        }
    }

    private void clearCachedEntries(String affectDNs) {

        Enumeration cacheKeys = idRepoCache.keys();
        while (cacheKeys.hasMoreElements()) {
            String key = (String) cacheKeys.nextElement();
            int l1 = key.length();
            int l2 = affectDNs.length();
            if (key.regionMatches(true, (l1 - l2), affectDNs, 0, l2)) {
                // key ends with 'affectDN' string
                IdCacheBlock cb = (IdCacheBlock) idRepoCache.get(key);
                if (cb != null) {
                    cb.clear();
                }
            }
        }
    }

    /**
     * This method is used to clear the entire SDK cache in the event that
     * EventService notifies that all entries have been modified (or should be
     * marked dirty).
     * 
     * @param dn
     */
    public synchronized void clearCache() {
        idRepoCache.clear();
        initializeCache();
    }

    /**
     * This method will be called by <code>AMIdRepoListener</code>. This
     * method will update the cache by removing all the entires which are
     * affected as a result of an event notification caused because of
     * changes/deletions/renaming of entries with and without aci's.
     * 
     * <p>
     * NOTE: The event could have been caused either by changes to an aci entry
     * or a costemplate or a cosdefinition or changes to a normal entry
     * 
     * @param dn
     *            name of entity being modified
     * @param eventType
     *            type of modification
     * @param cosType
     *            true if it is cos related. false otherwise
     * @param aciChange
     *            true if it is aci related. false otherwise
     * @param attrNames
     *            Set of attribute Names which should be removed from the
     *            CacheEntry in the case of COS change
     */
    public void dirtyCache(String dn, int eventType, boolean cosType,
            boolean aciChange, Set attrNames) {
        IdCacheBlock cb;
        String originalDN = dn;
        dn = MiscUtils.formatToRFC(dn);
        switch (eventType) {
        case AMEvent.OBJECT_ADDED:
            cb = getFromCache(dn);
            if (cb != null) { // Mark an invalid entry as valid now
                cb.setExists(true);
            }
            if (cosType) { // A cos type event remove all affected attributes
                removeCachedAttributes(dn, attrNames);
            }
            break;
        case AMEvent.OBJECT_REMOVED:
            cb = (IdCacheBlock) idRepoCache.remove(dn);
            if (cb != null) {
                cb.clear(); // Clear anyway & help the GC process
            }
            if (cosType) {
                removeCachedAttributes(dn, attrNames);
            }
            break;
        case AMEvent.OBJECT_RENAMED:
            // Better to remove the renamed entry, or else it will be just
            // hanging in the cache, until LRU kicks in.
            cb = (IdCacheBlock) idRepoCache.remove(dn);
            if (cb != null) {
                cb.clear(); // Clear anyway & help the GC process
            }
            if (cosType) {
                removeCachedAttributes(dn, attrNames);
            }
            break;
        case AMEvent.OBJECT_CHANGED:
            cb = getFromCache(dn);
            if (cb != null) {
                cb.clear(); // Just clear the entry. Don't remove.
            }
            if (cosType) {
                removeCachedAttributes(dn, attrNames);
            } else if (aciChange) { // Clear all affected entries
                clearCachedEntries(dn);
            }
            break;
        }
        if (getDebug().messageEnabled()) {
            getDebug().message("IdRemoteCachedServicesImpl.dirtyCache(): Cache "
                    + "dirtied because of Event Notification. Parameters - "
                    + "eventType: " + eventType + ", cosType: "
                    + cosType + ", aciChange: " + aciChange
                    + ", fullDN: " + originalDN + "; rfcDN ="
                    + dn);
        }   
    }

    /**
     * Method that updates the cache entries locally. This method does a write
     * through cache
     */
    private void updateCache(SSOToken token, String dn, Map stringAttributes,
            Map byteAttributes) throws SSOException {
        String key = MiscUtils.formatToRFC(dn);
        IdCacheBlock cb = (IdCacheBlock) idRepoCache.get(key);
        if (cb != null && !cb.hasExpiredAndUpdated() && cb.isExists()) {
            String pDN = MiscUtils.getPrincipalDN(token);
            cb.replaceAttributes(pDN, stringAttributes, byteAttributes);
        }
    }

    private void dirtyCache(String dn) {
        String key = MiscUtils.formatToRFC(dn);
        IdCacheBlock cb = getFromCache(key);
        if (cb != null) {
            cb.clear();
        }
    }

    public Map getAttributes(SSOToken token, IdType type, String name,
            Set attrNames, String amOrgName, String amsdkDN,
            boolean isStringValues) throws IdRepoException, SSOException {
        AMIdentity id = new AMIdentity(token, name, type, amOrgName, amsdkDN);
        String dn = IdUtils.getUniversalId(id).toLowerCase();

        AMIdentity tokenId = IdUtils.getIdentity(token);
        String principalDN = IdUtils.getUniversalId(tokenId).toLowerCase();

        IdCacheBlock cb = (IdCacheBlock) idRepoCache.get(dn);
        AMHashMap attributes;
        if (attrNames == null || attrNames.isEmpty()) {
            return getAttributes(token, type, name, amOrgName, amsdkDN);
        }

        if (getDebug().messageEnabled()) {
            getDebug().message("In IdRemoteCachedServicesImpl."
                    + "getAttributes(SSOToken type, name, attrNames, amOrgName,"
                    + " amsdkDN) (" + principalDN + ", " + dn
                    + ", " + attrNames + " ," + amOrgName
                    + " , " + amsdkDN + " method.");
        }

        if (cb == null) { // Entry not present in cache
            if (getDebug().messageEnabled()) {
                getDebug().message("IdRemoteCachedServicesImpl."
                        + "getAttributes(): NO entry found in Cachefor key = "
                        + dn + ". Getting all these attributes from DS: "
                        + attrNames);
            }   

            // If the attributes returned here have an empty set as value, then
            // such attributes do not have a value or invalid attributes.
            // Internally keep track of these attributes.
            attributes = (AMHashMap) super.getAttributes(token, type, name,
                    attrNames, amOrgName, amsdkDN, isStringValues);

            // These attributes are either not present or not found in DS.
            // Try to check if they need to be fetched by external
            // plugins
            Set missAttrNames = attributes.getMissingAndEmptyKeys(attrNames);
            cb = new IdCacheBlock(dn, true);
            cb.putAttributes(principalDN, attributes, missAttrNames, false,
                    !isStringValues);
            idRepoCache.put(dn, cb);
            return attributes;
        } else { // Entry present in cache
            attributes = (AMHashMap) cb.getAttributes(principalDN, attrNames,
                    !isStringValues);

            // Find the missing attributes that need to be obtained from DS
            // Only find the missing keys as the ones with empty sets are not
            // found in DS
            Set missAttrNames = attributes.getMissingKeys(attrNames);
            if (!missAttrNames.isEmpty()) {
                if (getDebug().messageEnabled()) {
                    getDebug().message("IdRemoteCachedServicesImpl."
                            + "getAttributes(): Trying to gett these missing "
                            + "attributes from DS: "
                            + missAttrNames);
                }
                AMHashMap dsAttributes = (AMHashMap) super.getAttributes(token,
                        type, name, attrNames, amOrgName, amsdkDN,
                        isStringValues);

                attributes.putAll(dsAttributes);
                // Add these attributes, may be found in DS or just mark them as
                // invalid (Attribute level Negative caching)
                Set newMissAttrNames = dsAttributes
                        .getMissingAndEmptyKeys(missAttrNames);
                cb.putAttributes(principalDN, dsAttributes, newMissAttrNames,
                        false, !isStringValues);
            } else { // All attributes found in cache
                if (getDebug().messageEnabled()) {
                    getDebug().message("IdRemoteCachedServicesImpl." + 
                            "getAttributes(): found all attributes in Cache.");
                }   
            }
        }
        return attributes;
    }

    public Map getAttributes(SSOToken token, IdType type, String name,
            String amOrgName, String amsdkDN) throws IdRepoException,
            SSOException {
        AMIdentity id = new AMIdentity(token, name, type, amOrgName, amsdkDN);
        String dn = IdUtils.getUniversalId(id).toLowerCase();

        AMIdentity tokenId = IdUtils.getIdentity(token);
        String principalDN = IdUtils.getUniversalId(tokenId).toLowerCase();

        IdCacheBlock cb = (IdCacheBlock) idRepoCache.get(dn);
        AMHashMap attributes;
        if (cb != null) {
            // validateEntry(token, cb);
            if (cb.hasCompleteSet(principalDN)) {
                // cacheStats.updateHitCount();
                if (getDebug().messageEnabled()) {
                    getDebug().message("IdRemoteCachedServicesImpl."
                            + "getAttributes(): found all attributes in "
                            + "Cache.");
                }
                attributes = (AMHashMap) cb.getAttributes(principalDN, false);
            } else { // Get the whole set from DS and store it;
                // ignore incomplete set
                if (getDebug().messageEnabled()) {
                    getDebug().message("IdRemoteCachedServicesImpl."
                            + "getAttributes(): complete attribute set NOT "
                            + "found in cache. Getting from DS.");
                }

                attributes = (AMHashMap) super.getAttributes(token, type, name,
                        amOrgName, amsdkDN);
                cb.putAttributes(principalDN, attributes, null, true, false);
            }
        } else { // Attributes not cached
            // Get all the attributes from DS and store them
            attributes = (AMHashMap) super.getAttributes(token, type, name,
                    amOrgName, amsdkDN);
            // attributes = new AMHashMap((HashMap) hmattrs);
            cb = new IdCacheBlock(dn, true);
            cb.putAttributes(principalDN, attributes, null, true, false);
            idRepoCache.put(dn, cb);
            if (getDebug().messageEnabled()) {
                getDebug().message("IdRemoteCachedServicesImpl."
                        + "getAttributes(): attributes NOT found in cache. "
                        + "Fetched from DS.");
            }
        }

        return attributes;
    }

    public void setAttributes(SSOToken token, IdType type, String name,
            Map attributes, boolean isAdd, String amOrgName, String amsdkDN,
            boolean isString) throws IdRepoException, SSOException {

        super.setAttributes(token, type, name, attributes, isAdd, amOrgName,
                amsdkDN, isString);
        AMIdentity id = new AMIdentity(token, name, type, amOrgName, amsdkDN);
        String dn = IdUtils.getUniversalId(id).toLowerCase();

        if (type.equals(IdType.USER)) {
            // Update cache locally for modified delted user attributes
            if (isString) {
                updateCache(token, dn, attributes, null);
            } else {
                updateCache(token, dn, null, attributes);
            }
        } else {
            dirtyCache(dn);
        }
    }

    // FIXME: See how we can make it more efficient
    private IdCacheBlock getFromCache(String dn) {
        IdCacheBlock cb = (IdCacheBlock) idRepoCache.get(dn);
        if ((cb == null)) {
            int ind = dn.indexOf("amsdkdn=");
            if (ind > -1) {
                String tmp = dn.substring(0, ind);
                // TODO: Should return entries which might have amsdkDN but
                // notifications have not told us about it (like
                // notifications from plugins other than AMSDKRepo
                cb = (IdCacheBlock) idRepoCache.get(tmp);
            }
        }
        return cb;
    }
}

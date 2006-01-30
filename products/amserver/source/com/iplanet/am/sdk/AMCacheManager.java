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
 * $Id: AMCacheManager.java,v 1.3 2006-01-30 20:58:42 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import netscape.ldap.util.DN;

import com.iplanet.am.util.Cache;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;

class AMCacheManager extends AMDirectoryManager {
    // Other Caching Parameters (static)
    private static boolean cachingEnabled = true; // Enabled by default

    private static int maxSize = 10000;

    private static AMDirectoryManager instance = null;

    // Class Private
    protected static Cache sdkCache;

    private CacheStats cacheStats;

    protected static Cache idrepoCache;

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
            if (debug.messageEnabled()) {
                debug.message("AMCachingManager.intializeParams() "
                        + "Caching size set to: " + maxSize);
            }
        } catch (NumberFormatException ne) {
            maxSize = 10000;
            debug.warning("AMCachingManager.initializeParams() "
                    + "- invalid value for cache size specified. Setting "
                    + "to default value: " + maxSize);
        }
    }

    private AMCacheManager() {
        super();
        initializeCache();
        cacheStats = CacheStats.getInstance();
    }

    private void initializeCache() {
        sdkCache = new Cache(maxSize);
        idrepoCache = new Cache(maxSize);
    }

    /**
     * Method to get the current cache size
     * 
     * @return the size of the SDK LRU cache
     */
    protected int getCachesize() {
        return sdkCache.size();
    }

    protected static synchronized AMDirectoryManager getInstance() {
        if (instance == null) {
            debug.message("AMCacheManager.getInstance(): Creating a new "
                    + "Instance of AMCacheManager()");
            instance = new AMCacheManager();
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

    /**
     * Method to check if Caching is enabled or disabled. To be called by all
     * other LRU Caches that are created in AM SDK
     * 
     * @return the maximum cache size for a LRU cache
     */
    protected static boolean isCachingEnabled() {
        return cachingEnabled;
    }

    /**
     * Prints the contents of the cache. For debug purpose only
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n<<<<<<< BEGIN SDK CACHE CONTENTS >>>>>>>>");
        if (!sdkCache.isEmpty()) { // Should never be null
            Enumeration cacheKeys = sdkCache.keys();
            while (cacheKeys.hasMoreElements()) {
                String key = (String) cacheKeys.nextElement();
                CacheBlock cb = (CacheBlock) sdkCache.get(key);
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
    private void removeCachedAttributes(Cache cache, String affectDNs,
            Set attrNames) {
        Enumeration cacheKeys = cache.keys();
        while (cacheKeys.hasMoreElements()) {
            String key = (String) cacheKeys.nextElement();
            int l1 = key.length();
            int l2 = affectDNs.length();
            if (key.regionMatches(true, (l1 - l2), affectDNs, 0, l2)) {
                // key ends with 'affectDN' string
                CacheBlock cb = (CacheBlock) cache.get(key);
                if (cb != null && cb.isExists()) {
                    cb.removeAttributes(attrNames);
                }
            }
        }
    }

    private void clearCachedEntries(Cache cache, String affectDNs) {
        Enumeration cacheKeys = cache.keys();
        while (cacheKeys.hasMoreElements()) {
            String key = (String) cacheKeys.nextElement();
            int l1 = key.length();
            int l2 = affectDNs.length();
            if (key.regionMatches(true, (l1 - l2), affectDNs, 0, l2)) {
                // key ends with 'affectDN' string
                CacheBlock cb = (CacheBlock) cache.get(key);
                if (cb != null) {
                    cb.clear();
                }
            }
        }
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
    protected void dirtyCache(Cache thisCache, String dn, int eventType,
            boolean cosType, boolean aciChange, Set attrNames) {
        CacheBlock cb;
        String origdn = dn;
        dn = AMCommonUtils.formatToRFC(dn);
        switch (eventType) {
        case AMEvent.OBJECT_ADDED:
            cb = getFromCache(thisCache, dn);
            if (cb != null) { // Mark an invalid entry as valid now
                cb.setExists(true);
            }
            if (cosType) { // A cos type event remove all affected attributes
                removeCachedAttributes(thisCache, dn, attrNames);
            }
            break;
        case AMEvent.OBJECT_REMOVED:
            cb = (CacheBlock) thisCache.remove(dn);
            if (cb != null) {
                cb.clear(); // Clear anyway & help the GC process
            }
            if (cosType) {
                removeCachedAttributes(thisCache, dn, attrNames);
            }
            break;
        case AMEvent.OBJECT_RENAMED:
            // Better to remove the renamed entry, or else it will be just
            // hanging in the cache, until LRU kicks in.
            cb = (CacheBlock) thisCache.remove(dn);
            if (cb != null) {
                cb.clear(); // Clear anyway & help the GC process
            }
            if (cosType) {
                removeCachedAttributes(thisCache, dn, attrNames);
            }
            break;
        case AMEvent.OBJECT_CHANGED:
            cb = getFromCache(thisCache, dn);
            if (cb != null) {
                cb.clear(); // Just clear the entry. Don't remove.
            }
            if (cosType) {
                removeCachedAttributes(thisCache, dn, attrNames);
            } else if (aciChange) { // Clear all affected entries
                clearCachedEntries(thisCache, dn);
            }
            break;
        }
        if (debug.messageEnabled()) {
            debug.message("AMCacheManager.dirtyCache(): Cache dirtied "
                    + "because of Event Notification. Parameters - eventType: "
                    + eventType + ", cosType: " + cosType + ", aciChange: "
                    + aciChange + ", fullDN: " + origdn + "; rfcDN =" + dn);
        }
        dn = origdn;
    }

    /**
     * This method is used to clear the entire SDK cache in the event that
     * EventService notifies that all entries have been modified (or should be
     * marked dirty).
     * 
     * @param dn
     */
    public synchronized void clearCache() {
        sdkCache.clear();
        idrepoCache.clear();
        initializeCache();
    }

    private synchronized void removeFromCache(Cache thisCache, String dn) {
        String key = AMCommonUtils.formatToRFC(dn);
        thisCache.remove(key);
    }

    private void dirtyCache(String dn) {
        String key = AMCommonUtils.formatToRFC(dn);
        CacheBlock cb = (CacheBlock) sdkCache.get(key);
        if (cb != null) {
            cb.clear();
        }
    }

    private void dirtyCache(Cache thisCache, Set entries) {
        Iterator itr = entries.iterator();
        while (itr.hasNext()) {
            String entryDN = (String) itr.next();
            String key = AMCommonUtils.formatToRFC(entryDN);
            CacheBlock cb = getFromCache(thisCache, key);
            if (cb != null) {
                cb.clear();
            }
        }
    }

    /**
     * Method that updates the cache entries locally. This method does a write
     * through cache
     */
    private void updateCache(SSOToken token, Cache thisCache, String dn,
            Map stringAttributes, Map byteAttributes) throws SSOException {
        String key = AMCommonUtils.formatToRFC(dn);
        CacheBlock cb = (CacheBlock) thisCache.get(key);
        if (cb != null && cb.isExists()) {
            String pDN = AMCommonUtils.getPrincipalDN(token);
            cb.replaceAttributes(pDN, stringAttributes, byteAttributes);
        }
    }

    // ***********************************************************************
    // Overriden Methods of AMDirectoryManager class below
    // ***********************************************************************

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMDirectoryManager#createEntry(
     *      com.iplanet.sso.SSOToken,
     *      java.lang.String, int, java.lang.String, java.util.Map)
     */
    public void createEntry(SSOToken token, String entryName, int objectType,
            String parentDN, Map attributes) throws AMEntryExistsException,
            AMException {

        super.createEntry(token, entryName, objectType, parentDN, attributes);
        String cacheDN = AMNamingAttrManager.getNamingAttr(objectType) + "="
                + entryName + "," + parentDN;
        removeFromCache(sdkCache, cacheDN);
    }

    /**
     * Method to be called to validate the entry before any of the get/put/
     * remove methods are called.
     * 
     * @throws AMException
     *             if the entry does not exist in the DS
     */
    private void validateEntry(SSOToken token, CacheBlock cb)
            throws AMException {
        if (!cb.isExists()) { // Entry does not exist in DS, invalid entry
            String params[] = { cb.getEntryDN() };
            boolean isPresent = super.doesEntryExists(token, params[0]);
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager : validateEntry :DN " + params[0]
                        + " got from DS & exists: " + isPresent);
            }
            if (isPresent) {
                // Intialize the CacheBlock based on isPresent
                // else throw '461' exception/error message.
                // This is for certain containers created dynamically.
                // eg. ou=agents,ou=container,ou=agents.
                String dn = AMCommonUtils.formatToRFC(params[0]);
                cb = new CacheBlock(params[0], isPresent);
                sdkCache.put(dn, cb);
            } else {
                String locale = AMCommonUtils.getUserLocale(token);
                throw new AMException(AMSDKBundle.getString("461", params,
                        locale), "461", params);
            }
        }
    }

    public boolean doesEntryExists(SSOToken token, String entryDN) {
        String dn = AMCommonUtils.formatToRFC(entryDN);
        CacheBlock cb = (CacheBlock) sdkCache.get(dn);
        if (cb != null) {
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.doesEntryExist(): entryDN: "
                        + entryDN + " found in cache & exists: "
                        + cb.isExists());
            }
            return cb.isExists();
        } else {
            boolean isPresent = super.doesEntryExists(token, dn);
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.doesEntryExist(): entryDN: "
                        + entryDN + " got from DS & exists: " + isPresent);
            }
            // Intialize the CacheBock based on isPresent
            cb = new CacheBlock(entryDN, isPresent);
            sdkCache.put(dn, cb);
            return isPresent;
        }
    }

    private void setOrganizationDNs(String organizationDN, Set childDNSet) {
        Iterator itr = childDNSet.iterator();
        while (itr.hasNext()) {
            String cDN = (String) itr.next();
            CacheBlock cb = (CacheBlock) sdkCache.get(cDN);
            if (cb == null) {
                cb = new CacheBlock(cDN, organizationDN, true);
                sdkCache.put(cDN, cb);
            } else {
                cb.setOrganizationDN(organizationDN);
            }
        }
        if (debug.messageEnabled() && !childDNSet.isEmpty()) {
            debug.message("AMCacheManager.setOrganizationDNs(): Set org DNs "
                    + "as: " + organizationDN + " for children: " + childDNSet);
        }
    }

    public void updateUserAttribute(SSOToken token, Set members,
            String staticGroupDN, boolean toAdd) throws AMException {
        super.updateUserAttribute(token, members, staticGroupDN, toAdd);
        // Note here we are updating the cache only after all user attributes
        // are set. Even if the operation fails for a particular user then, we
        // will not be updating the cache. It should be okay as event
        // notification would clean up.
        dirtyCache(sdkCache, members); // TODO: Just remove the modified
                                        // attribute
    }

    /**
     * Gets the Organization DN for the specified entryDN. If the entry itself
     * is an org, then same DN is returned.
     * <p>
     * <b>NOTE:</b> This method will involve serveral directory searches, hence
     * be cautious of Performance hit.
     * 
     * <p>
     * This method does not call its base classes method unlike the rest of the
     * overriden methods to obtain the organization DN, as it requires special
     * processing requirements.
     * 
     * @param token
     *            a valid SSOToken
     * @param entryDN
     *            the entry whose parent Organization is to be obtained
     * @return the DN String of the parent Organization
     * @throws AMException
     *             if an error occured while obtaining the parent Organization
     */
    public String getOrganizationDN(SSOToken token, String entryDN)
            throws AMException {
        if (entryDN.equals("") || !DN.isDN(entryDN)) {
            debug.error("AMCacheManager.getOrganizationDN() Invalid DN: "
                    + entryDN);
            throw new AMException(token, "157");
        }

        DN dnObject = new DN(entryDN);
        String organizationDN = "";
        Set childDNSet = new HashSet();
        boolean errorCondition = false;
        boolean found = false;
        while (!errorCondition && !found) {
            boolean lookupDirectory = true;
            String childDN = dnObject.toRFCString().toLowerCase();
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.getOrganizationDN() - "
                        + "looping Organization DN for entry: " + childDN);
            }

            CacheBlock cb = (CacheBlock) sdkCache.get(childDN);
            if (cb != null) {
                organizationDN = cb.getOrganizationDN();
                if (organizationDN != null) {
                    if (debug.messageEnabled()) {
                        debug.message("AMCacheManager."
                                + "getOrganizationDN(): found OrganizationDN: "
                                + organizationDN + " for: " + childDN);
                    }
                    found = true;
                    setOrganizationDNs(organizationDN, childDNSet);
                    continue;
                } else if (cb.getObjectType() == AMObject.ORGANIZATION
                        || cb.getObjectType() == AMObject.ORGANIZATIONAL_UNIT) {
                    // Object type is organization
                    organizationDN = childDN;
                    found = true;
                    childDNSet.add(childDN);
                    setOrganizationDNs(organizationDN, childDNSet);
                    continue;
                } else if (cb.getObjectType() != 
                    AMObject.UNDETERMINED_OBJECT_TYPE) {
                    // Don't lookup directory if the object type is unknown
                    lookupDirectory = false;
                }
            }
            childDNSet.add(childDN);
            if (lookupDirectory) {
                organizationDN = dMgr
                        .verifyAndGetOrgDN(token, entryDN, childDN);
            }
            if (organizationDN != null && organizationDN.length() > 0) {
                found = true;
                setOrganizationDNs(organizationDN, childDNSet);
            } else if (dnObject.countRDNs() == 1) { // Reached topmost level
                errorCondition = true;
                debug.error("AMCacheManager.getOrgnizationDN(): "
                        + "Reached root suffix. Unable to get parent Org");
            } else { // Climb tree on level up
                dnObject = dnObject.getParent();
            }
        }
        return organizationDN;
    }

    /**
     * Gets the type of the object given its DN.
     * 
     * @param SSOToken
     *            token a valid SSOToken
     * @param dn
     *            DN of the object whose type is to be known.
     * 
     * @throws AMException
     *             if the data store is unavailable or if the object type is
     *             unknown
     * @throws SSOException
     *             if ssoToken is invalid or expired.
     */
    public int getObjectType(SSOToken token, String dn) throws AMException,
            SSOException {
        int objectType = AMObject.UNDETERMINED_OBJECT_TYPE;
        String entryDN = AMCommonUtils.formatToRFC(dn);
        CacheBlock cb = (CacheBlock) sdkCache.get(entryDN);
        if (cb != null) {
            validateEntry(token, cb);
            objectType = cb.getObjectType();
            if (objectType != AMObject.UNDETERMINED_OBJECT_TYPE) {
                return objectType;
            }
        }

        // The method below will throw an AMException if the entry does not
        // exist in the directory. If it exists, then create a cache entry for
        // this DN
        if (cb == null) {
            objectType = super.getObjectType(token, entryDN);
            cb = new CacheBlock(entryDN, true);
            sdkCache.put(entryDN, cb);
        } else {
            objectType = super.getObjectType(token, entryDN, cb.getAttributes(
                    AMCommonUtils.getPrincipalDN(token), false));
        }
        cb.setObjectType(objectType);
        if (objectType == AMObject.ORGANIZATION
                || objectType == AMObject.ORGANIZATIONAL_UNIT) {
            cb.setOrganizationDN(entryDN);
        }
        return objectType;
    }

    /**
     * Returns attributes from an external data store.
     * 
     * @param token
     *            Single sign on token of user
     * @param entryDN
     *            DN of the entry user is trying to read
     * @param attrNames
     *            Set of attributes to be read
     * @param profileType
     *            Integer determining the type of profile being read
     * @return A Map of attribute-value pairs
     * @throws AMException
     *             if an error occurs when trying to read external datastore
     */
    public Map getExternalAttributes(SSOToken token, String entryDN,
            Set attrNames, int profileType) throws AMException {

        String eDN;
        if (profileType == AMObject.USER) {
            eDN = (new DN(entryDN)).getParent().toString();
        } else {
            eDN = entryDN;
        }
        String orgDN = getOrganizationDN(token, eDN);
        if (AMCommonUtils.isExternalGetAttributesEnabled(orgDN)) {
            return super.getExternalAttributes(token, entryDN, attrNames,
                    profileType);
        } else {
            return null;
        }
    }

    public Map getAttributes(SSOToken token, String entryDN, int profileType)
            throws AMException, SSOException {
        boolean ignoreCompliance = true;
        boolean byteValues = false;
        return getAttributes(token, entryDN, ignoreCompliance, byteValues,
                profileType);
    }

    public Map getAttributes(SSOToken token, String entryDN, Set attrNames,
            int profileType) throws AMException, SSOException {
        boolean ignoreCompliance = true;
        boolean byteValues = false;
        return getAttributes(token, entryDN, attrNames, ignoreCompliance,
                byteValues, profileType);
    }

    public Map getAttributesByteValues(SSOToken token, String entryDN,
            int profileType) throws AMException, SSOException {
        // fetch byte values
        boolean byteValues = true;
        boolean ignoreCompliance = true;
        return getAttributes(token, entryDN, ignoreCompliance, byteValues,
                profileType);
    }

    public Map getAttributesByteValues(SSOToken token, String entryDN,
            Set attrNames, int profileType) throws AMException, SSOException {
        // fetch byte values
        boolean byteValues = true;
        boolean ignoreCompliance = true;
        return getAttributes(token, entryDN, attrNames, ignoreCompliance,
                byteValues, profileType);
    }

    /**
     * Gets all attributes corresponding to the entryDN. This method obtains the
     * DC Tree node attributes and also performs compliance related verification
     * checks in compliance mode. Note: In compliance mode you can skip the
     * compliance checks by setting ignoreCompliance to "false".
     * 
     * @param token
     *            a valid SSOToken
     * @param entryDN
     *            the DN of the entry whose attributes need to retrieved
     * @param ignoreCompliance
     *            a boolean value specificying if compliance related entries
     *            need to ignored or not. Ignored if true.
     * @param fetchByteValues
     *            if false StringValues are fetched, if true byte values are
     *            fetched.
     * @return a Map containing attribute names as keys and Set of values
     *         corresponding to each key.
     * @throws AMException
     *             if an error is encountered in fetching the attributes
     */
    public Map getAttributes(SSOToken token, String entryDN,
            boolean ignoreCompliance, boolean byteValues, int profileType)
            throws AMException, SSOException {
        // Attributes are being requested; increment cache stats request counter
        cacheStats.incrementRequestCount();

        String principalDN = AMCommonUtils.getPrincipalDN(token);
        String dn = AMCommonUtils.formatToRFC(entryDN);

        if (debug.messageEnabled()) {
            debug.message("In AMCacheManager.getAttributes(SSOToken"
                    + "entryDN, ignoreCompliance) " + "(" + principalDN + ", "
                    + entryDN + ", " + ignoreCompliance + " method.");
        }

        CacheBlock cb = (CacheBlock) sdkCache.get(dn);
        AMHashMap attributes;
        if (cb != null) {
            validateEntry(token, cb);
            if (cb.hasCompleteSet(principalDN)) {
                cacheStats.updateHitCount();
                if (debug.messageEnabled()) {
                    debug.message("AMCacheManager.getAttributes(): found"
                            + "all attributes in Cache.");
                }
                attributes = (AMHashMap) cb.getAttributes(principalDN,
                        byteValues);
            } else { // Get the whole set from DS and store it;
                // ignore incomplete set
                if (debug.messageEnabled()) {
                    debug
                            .message("AMCacheManager.getAttributes():  "
                                    + "complete attribute set NOT found in "
                                    + "cache. Getting from DS.");
                }
                attributes = (AMHashMap) super.getAttributes(token, entryDN,
                        ignoreCompliance, byteValues, profileType);
                cb.putAttributes(principalDN, attributes, null, true,
                        byteValues);
            }
        } else { // Attributes not cached
            // Get all the attributes from DS and store them
            attributes = (AMHashMap) super.getAttributes(token, entryDN,
                    ignoreCompliance, byteValues, profileType);
            cb = new CacheBlock(entryDN, true);
            cb.putAttributes(principalDN, attributes, null, true, byteValues);
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.getAttributes(): attributes "
                        + "NOT found in cache. Fetched from DS.");
            }
            sdkCache.put(dn, cb);
        }

        // Get all external DS attributes by calling plugin modules.
        // Note these attributes should not be cached.
        Map extAttributes = getExternalAttributes(token, entryDN, null,
                profileType);
        if (extAttributes != null && !extAttributes.isEmpty()) {
            // Note the attributes stored in the cache are already copied to a
            // new map. Hence modifying this attributes is okay.
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.getAttributes(): External "
                        + "attributes present. Adding them with original list");
            }
            attributes.putAll(extAttributes);
        }
        return attributes;
    }

    private AMHashMap getPluginAttrsAndUpdateCache(SSOToken token,
            String principalDN, String entryDN, CacheBlock cb,
            AMHashMap attributes, Set missAttrNames, boolean byteValues,
            int profileType) throws AMException {
        // Get all external attributes by calling plugin modules.
        // Note these attributes should not be cached.
        Map extAttributes = getExternalAttributes(token, entryDN,
                missAttrNames, profileType);

        if (extAttributes != null && !extAttributes.isEmpty()) {
            // Remove these external attributes from Cache
            Set extAttrNames = extAttributes.keySet();
            cb.removeAttributes(extAttrNames);

            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.getPluginAttrsAndUpdateCache(): "
                                + "External attributes present. Adding them "
                                + "with original list. External Attributes: " 
                                + extAttrNames);
            }

            // Note the attributes stored in the cache are already copied
            // to a new map. Hence modifying this attributes is okay.
            attributes.putAll(extAttributes);
        }
        return attributes;
    }

    /**
     * Gets the specific attributes corresponding to the entryDN. This method
     * obtains the DC Tree node attributes and also performs compliance related
     * verification checks in compliance mode. Note: In compliance mode you can
     * skip the compliance checks by setting ignoreCompliance to "false".
     * 
     * @param token
     *            a valid SSOToken
     * @param entryDN
     *            the DN of the entry whose attributes need to retrieved
     * @param attrNames
     *            a Set of names of the attributes that need to be retrieved.
     *            The attrNames should not be null
     * @param ignoreCompliance
     *            a boolean value specificying if compliance related entries
     *            need to ignored or not. Ignored if true.
     * @return a Map containing attribute names as keys and Set of values
     *         corresponding to each key.
     * @throws AMException
     *             if an error is encountered in fetching the attributes
     */
    public Map getAttributes(SSOToken token, String entryDN, Set attrNames,
            boolean ignoreCompliance, boolean byteValues, int profileType)
            throws AMException, SSOException {
        if (attrNames == null || attrNames.isEmpty()) {
            return getAttributes(token, entryDN, ignoreCompliance, byteValues,
                    profileType);
        }

        // Attributes are being requested; increment cache stats request counter
        cacheStats.incrementRequestCount();

        // Load the whole attrset in the cache, if in DCTree mode
        // Not good for performance, but fix later TODO (Deepa)
        if (AMDCTree.isRequired()) { // TODO: This needs to be fixed!
            getAttributes(token, entryDN, ignoreCompliance, byteValues,
                    profileType);
        }

        String principalDN = AMCommonUtils.getPrincipalDN(token);
        if (debug.messageEnabled()) {
            debug.message("In AMCacheManager.getAttributes(SSOToken"
                    + "entryDN, attrNames, ignoreCompliance, byteValues) "
                    + "(" + principalDN + ", " + entryDN + ", " + attrNames
                    + ", " + ignoreCompliance + ", " + byteValues + " method.");
        }

        String dn = AMCommonUtils.formatToRFC(entryDN);
        CacheBlock cb = (CacheBlock) sdkCache.get(dn);
        if (cb == null) { // Entry not present in cache
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.getAttributes():  NO entry "
                        + "found in Cache. Getting all these attributes from "
                        + "DS: "+ attrNames);
            }

            // If the attributes returned here have an empty set as value, then
            // such attributes do not have a value or invalid attributes.
            // Internally keep track of these attributes.
            AMHashMap attributes = (AMHashMap) super.getAttributes(token,
                    entryDN, attrNames, ignoreCompliance, byteValues,
                    profileType);

            // These attributes are either not present or not found in DS.
            // Try to check if they need to be fetched by external
            // plugins
            Set missAttrNames = attributes.getMissingAndEmptyKeys(attrNames);
            cb = new CacheBlock(dn, true);
            cb.putAttributes(principalDN, attributes, missAttrNames, false,
                    byteValues);
            sdkCache.put(dn, cb);

            if (!missAttrNames.isEmpty()) {
                attributes = getPluginAttrsAndUpdateCache(token, principalDN,
                        entryDN, cb, attributes, missAttrNames, byteValues,
                        profileType);
            }
            return attributes;
        } else { // Entry present in cache
            validateEntry(token, cb); // Entry may be an invalid entry
            AMHashMap attributes = (AMHashMap) cb.getAttributes(principalDN,
                    attrNames, byteValues);

            // Find the missing attributes that need to be obtained from DS
            // Only find the missing keys as the ones with empty sets are not
            // found in DS
            Set missAttrNames = attributes.getMissingKeys(attrNames);
            if (!missAttrNames.isEmpty()) {
                boolean isComplete = cb.hasCompleteSet(principalDN);
                AMHashMap dsAttributes;
                if (!isComplete) {
                    if (debug.messageEnabled()) {
                        debug.message("AMCacheManager.getAttributes(): "
                                + "Trying to get these missing attributes "
                                + "from DS: " + missAttrNames);
                    }
                    dsAttributes = (AMHashMap) super.getAttributes(token,
                            entryDN, missAttrNames, ignoreCompliance,
                            byteValues, profileType);

                    if (!dsAttributes.isEmpty()) {
                        attributes.putAll(dsAttributes);
                        // Add these attributes, may be found in DS or mark
                        // as invalid (Attribute level Negative caching)
                        Set newMissAttrNames = dsAttributes
                                .getMissingAndEmptyKeys(missAttrNames);

                        // Update dsAttributes with reset of the attributes
                        // in cache
                        dsAttributes.putAll(cb.getAttributes(principalDN,
                                byteValues));

                        // Update the cache
                        cb.putAttributes(principalDN, dsAttributes,
                                newMissAttrNames, isComplete, byteValues);
                        missAttrNames = newMissAttrNames;
                    }
                } else {
                    // Update the cache with invalid attributes
                    cb.putAttributes(principalDN, cb.getAttributes(principalDN,
                            byteValues), missAttrNames, isComplete, byteValues);
                }

                if (!missAttrNames.isEmpty()) {
                    attributes = getPluginAttrsAndUpdateCache(token,
                            principalDN, entryDN, cb, attributes,
                            missAttrNames, byteValues, profileType);
                }
            } else { // All attributes found in cache
                if (debug.messageEnabled()) {
                    debug.message("AMCacheManager.getAttributes():  found "
                            + "all attributes in Cache.");
                }
                cacheStats.updateHitCount();
            }
            // Remove all the empty values from the return attributes
            return attributes;
        }
    }

    /**
     * Renames an entry. Currently used for only user renaming.
     * 
     * @param token
     *            the sso token
     * @param profileType
     *            the type of entry
     * @param entryDN
     *            the entry DN
     * @param newName
     *            the new name (i.e., if RDN is cn=John, the value passed should
     *            be "John"
     * @param deleteOldName
     *            if true the old name is deleted otherwise it is retained.
     * @return new <code>DN</code> of the renamed entry
     * @throws AMException
     *             if the operation was not successful
     */
    public String renameEntry(SSOToken token, int objectType, String entryDN,
            String newName, boolean deleteOldName) throws AMException {
        String newDN = super.renameEntry(token, objectType, entryDN, newName,
                deleteOldName);
        // Just rename the dn in the cache. Don't remove the entry. So when the
        // event notification happens, it won't find the entry as it is already
        // renamed. Chances are this cache rename operation may happen before
        // the notification thread trys clean up.
        // NOTE: We should have the code to remove the entry for rename
        // operation as the operation could have been performed by some other
        // process such as amadmin.
        String oldDN = AMCommonUtils.formatToRFC(entryDN);
        CacheBlock cb = (CacheBlock) sdkCache.remove(oldDN);
        newDN = AMCommonUtils.formatToRFC(newDN);
        sdkCache.put(newDN, cb);
        return newDN;
    }

    /**
     * Method Set the attributes of an entry.
     * 
     * @param token
     *            SSOToken
     * @param entryDN
     *            DN of the profile whose template is to be set
     * @param objectType
     *            profile type
     * @param attributes
     *            a AMHashMap of attributes to be set
     */
    public void setAttributes(SSOToken token, String entryDN, int objectType,
            Map stringAttributes, Map byteAttributes, boolean isAdd)
            throws AMException, SSOException {
        super.setAttributes(token, entryDN, objectType, stringAttributes,
                byteAttributes, isAdd);
        // Cache clean ups
        if (objectType == AMObject.USER) {
            // Update cache locally for modified delted user attributes
            updateCache(token, sdkCache, entryDN, stringAttributes,
                    byteAttributes);
        } else if (objectType != AMObject.USER) {
            // Remove the entry from cache
            dirtyCache(entryDN);
        }
    }

    /**
     * Remove an entry from the directory.
     * 
     * @param token
     *            SSOToken
     * @param entryDN
     *            dn of the profile to be removed
     * @param objectType
     *            profile type
     * @param recursive
     *            if true, remove all sub entries & the object
     * @param softDelete
     *            Used to let pre/post callback plugins know that this delete is
     *            either a soft delete (marked for deletion) or a purge/hard
     *            delete itself, otherwise, remove the object only
     */
    public void removeEntry(SSOToken token, String entryDN, int objectType,
            boolean recursive, boolean softDelete) throws AMException,
            SSOException {
        super.removeEntry(token, entryDN, objectType, recursive, softDelete);
        // write through the cache in case of successful delete (only this
        // entry)
        removeFromCache(sdkCache, entryDN);
    }

    /**
     * Create an AMTemplate (COSTemplate)
     * 
     * @param token
     *            token
     * @param entryDN
     *            DN of the profile whose template is to be set
     * @param serviceName
     *            Service Name
     * @param attrSet
     *            attributes to be set
     * @param priority
     *            template priority
     * @param type
     *            Template type, AMTemplate.DYNAMIC_TEMPLATE
     * @return String DN of the newly created template
     */
    public String createAMTemplate(SSOToken token, String entryDN,
            int objectType, String serviceName, Map attributes, int priority)
            throws AMException {
        String templateDN = super.createAMTemplate(token, entryDN, objectType,
                serviceName, attributes, priority);
        // Mark the entry as exists in cache
        String dn = AMCommonUtils.formatToRFC(templateDN);
        CacheBlock cb = (CacheBlock) sdkCache.get(dn);
        if (cb != null) {
            cb.setExists(true);
        }
        return templateDN;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMDirectoryManager#getAttributes(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Set,
     *      java.lang.String, java.lang.String)
     */
    public Map getAttributes(SSOToken token, IdType type, String name,
            Set attrNames, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        AMIdentity id = new AMIdentity(token, name, type, amOrgName, amsdkDN);
        String dn = IdUtils.getUniversalId(id).toLowerCase();
        AMIdentity tokenId = IdUtils.getIdentity(token);
        String principalDN = IdUtils.getUniversalId(tokenId).toLowerCase();
        CacheBlock cb = (CacheBlock) idrepoCache.get(dn);
        AMHashMap attributes;
        if (attrNames == null || attrNames.isEmpty()) {
            return getAttributes(token, type, name, amOrgName, amsdkDN);
        }

        if (debug.messageEnabled()) {
            debug.message("In AMCacheManager.getAttributes(SSOToken"
                    + "type, name, attrNames, amOrgName, amsdkDN) " + "("
                    + principalDN + ", " + dn + ", " + attrNames + " ,"
                    + amOrgName + " , " + amsdkDN + " method.");
        }

        if (cb == null) { // Entry not present in cache
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.getAttributes():  NO entry "
                        + "found in Cachefor key = " + dn
                        + ". Getting all these attributes from DS: "
                        + attrNames);
            }

            // If the attributes returned here have an empty set as value, then
            // such attributes do not have a value or invalid attributes.
            // Internally keep track of these attributes.
            attributes = (AMHashMap) super.getAttributes(token, type, name,
                    attrNames, amOrgName, amsdkDN, true);
            // attributes = new AMHashMap(hmattributes);

            // These attributes are either not present or not found in DS.
            // Try to check if they need to be fetched by external
            // plugins
            Set missAttrNames = attributes.getMissingAndEmptyKeys(attrNames);
            cb = new CacheBlock(dn, true);
            cb.putAttributes(principalDN, attributes, missAttrNames, false,
                    false);
            idrepoCache.put(dn, cb);
            return attributes;
        } else { // Entry present in cache
            // validateEntry(token, cb); // Entry may be an invalid entry
            attributes = (AMHashMap) cb.getAttributes(principalDN, attrNames,
                    false);

            // Find the missing attributes that need to be obtained from DS
            // Only find the missing keys as the ones with empty sets are not
            // found in DS
            Set missAttrNames = attributes.getMissingKeys(attrNames);
            if (!missAttrNames.isEmpty()) {
                if (debug.messageEnabled()) {
                    debug.message("AMCacheManager.getAttributes(): Trying to "
                            + "get these missing attributes from DS: "
                            + missAttrNames);
                }
                AMHashMap dsAttributes = (AMHashMap) super.getAttributes(token,
                        type, name, attrNames, amOrgName, amsdkDN, true);
                // AMHashMap dsAttributes = new AMHashMap(dsAttrs);
                attributes.putAll(dsAttributes);
                // Add these attributes, may be found in DS or just mark them as
                // invalid (Attribute level Negative caching)
                Set newMissAttrNames = dsAttributes
                        .getMissingAndEmptyKeys(missAttrNames);
                cb.putAttributes(principalDN, dsAttributes, newMissAttrNames,
                        false, false);
            } else { // All attributes found in cache
                if (debug.messageEnabled()) {
                    debug.message("AMCacheManager.getAttributes():  found "
                            + "all attributes in Cache.");
                }
            }
        }
        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMDirectoryManager#getAttributes(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Map getAttributes(SSOToken token, IdType type, String name,
            String amOrgName, String amsdkDN) throws IdRepoException,
            SSOException {
        AMIdentity id = new AMIdentity(token, name, type, amOrgName, amsdkDN);
        String dn = IdUtils.getUniversalId(id).toLowerCase();
        AMIdentity tokenId = IdUtils.getIdentity(token);
        String principalDN = IdUtils.getUniversalId(tokenId).toLowerCase();
        CacheBlock cb = (CacheBlock) idrepoCache.get(dn);
        AMHashMap attributes;
        if (cb != null) {
            // validateEntry(token, cb);
            if (cb.hasCompleteSet(principalDN)) {
                // cacheStats.updateHitCount();
                if (debug.messageEnabled()) {
                    debug.message("AMCacheManager.getAttributes(): found"
                            + "all attributes in Cache.");
                }
                attributes = (AMHashMap) cb.getAttributes(principalDN, false);
            } else { // Get the whole set from DS and store it;
                // ignore incomplete set
                if (debug.messageEnabled()) {
                    debug.message("AMCacheManager.getAttributes():  "
                            + "complete attribute set NOT found in cache. "
                            + "Getting from DS.");
                }
                attributes = (AMHashMap) super.getAttributes(token, type, name,
                        amOrgName, amsdkDN);
                // attributes = new AMHashMap((HashMap) hmattributes);
                cb.putAttributes(principalDN, attributes, null, true, false);
            }
        } else { // Attributes not cached
            // Get all the attributes from DS and store them
            attributes = (AMHashMap) super.getAttributes(token, type, name,
                    amOrgName, amsdkDN);
            // attributes = new AMHashMap((HashMap) hmattrs);
            cb = new CacheBlock(dn, true);
            cb.putAttributes(principalDN, attributes, null, true, false);
            idrepoCache.put(dn, cb);
            if (debug.messageEnabled()) {
                debug.message("AMCacheManager.getAttributes(): attributes "
                        + "NOT found in cache. Fetched from DS.");
            }
        }

        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMDirectoryManager#setAttributes(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Map,
     *      boolean, java.lang.String, java.lang.String)
     */
    public void setAttributes(SSOToken token, IdType type, String name,
            Map attributes, boolean isAdd, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {

        super.setAttributes(token, type, name, attributes, isAdd, amOrgName,
                amsdkDN, true);
        AMIdentity id = new AMIdentity(token, name, type, amOrgName, amsdkDN);
        String dn = IdUtils.getUniversalId(id).toLowerCase();
        if (type.equals(IdType.USER)) {
            // Update cache locally for modified delted user attributes
            updateCache(token, idrepoCache, dn, attributes, null);
        } else {
            Set s = new HashSet();
            s.add(dn);
            // Remove the entry from cache
            dirtyCache(idrepoCache, s);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMDirectoryManager#setGroupFilter(
     *      com.iplanet.sso.SSOToken,
     *      java.lang.String, java.lang.String)
     */
    public void setGroupFilter(SSOToken token, String entryDN, String filter)
            throws AMException, SSOException {
        // TODO Auto-generated method stub
        super.setGroupFilter(token, entryDN, filter);
    }

    private CacheBlock getFromCache(Cache cache, String dn) {
        CacheBlock cb = (CacheBlock) cache.get(dn);
        if ((cb == null)) {
            int ind = dn.indexOf("amsdkdn=");
            if (ind > -1) {
                String tmp = dn.substring(0, ind);
                // TODO: Should return entries which might have amsdkDN but
                // notifications have not told us about it (like
                // notifications from plugins other than AMSDKRepo
                cb = (CacheBlock) cache.get(tmp);
            }
        }
        return cb;
    }
}

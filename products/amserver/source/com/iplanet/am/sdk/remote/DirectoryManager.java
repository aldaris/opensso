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
 * $Id: DirectoryManager.java,v 1.1 2005-11-01 00:29:32 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk.remote;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.iplanet.am.sdk.AMEntryExistsException;
import com.iplanet.am.sdk.AMEventManagerException;
import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMHashMap;
import com.iplanet.am.sdk.AMObjectListener;
import com.iplanet.am.sdk.AMSDKBundle;
import com.iplanet.am.sdk.AMSearchResults;
import com.iplanet.am.sdk.AMTemplate;
import com.iplanet.am.sdk.DirectoryManagerInterface;
import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.ums.SearchControl;
import com.iplanet.ums.SortKey;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdOperation;
import com.sun.identity.idm.IdRepo;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.jaxrpc.SOAPClient;
import com.sun.identity.sm.SchemaType;

public class DirectoryManager implements DirectoryManagerInterface {
    private static String AM_SDK_DEBUG_FILE = "amProfile_Client";

    protected static String SDK_SERVICE = "DirectoryManagerIF";

    protected static String IDREPO_SERVICE = "IdRepoServiceIF";

    protected static Debug debug = Debug.getInstance(AM_SDK_DEBUG_FILE);

    protected SOAPClient client;

    protected EventListener eventListener;

    protected static DirectoryManager instance;

    protected static final String AMSR_COUNT = "__count";

    protected static final String AMSR_RESULTS = "__results";

    protected static final String AMSR_CODE = "__errorCode";

    protected static final String AMSR_ATTRS = "__attrs";

    public DirectoryManager() {
        if (client == null) {
            client = new SOAPClient(SDK_SERVICE);
        }
    }

    public static synchronized DirectoryManager getInstance() {
        if (instance == null) {
            debug.message("DirectoryManager.getInstance(): Creating a new "
                    + "Instance of DirectoryManager()");
            instance = new DirectoryManager();
        }
        return instance;
    }

    /**
     * Checks if the entry exists in the directory.
     * 
     * @param token
     *            a valid SSOToken
     * @param entryDN
     *            The DN of the entry that needs to be checked
     * @return true if the entryDN exists in the directory, false otherwise
     */
    public boolean doesEntryExists(SSOToken token, String entryDN) {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN };
            Boolean res = ((Boolean) client.send(client.encodeMessage(
                    "doesEntryExists", objs), null));
            return res.booleanValue();
        } catch (RemoteException rex) {
            return false;
        } catch (Exception ex) {
            return false;
        }

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
        try {
            Object[] objs = { token.getTokenID().toString(), dn };
            Integer res = ((Integer) client.send(client.encodeMessage(
                    "getObjectType", objs), null));
            return res.intValue();
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getObjectType: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getObjectType: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getObjectType: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.getObjectType: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.remote.DirectoryManagerIF#
     *      getObjectType(java.lang.String, java.lang.String, Map)
     */
    public int getObjectType(SSOToken token, String dn, Map cachedAttributes)
            throws AMException, SSOException {
        return (getObjectType(token, dn));
    }

    /**
     * Gets the attributes for this entryDN from the corresponding DC Tree node.
     * The attributes are fetched only for Organization entries in DC tree mode.
     * 
     * @param token
     *            a valid SSOToken
     * @param po
     *            the PersistentObject
     * @return an AttrSet of values or null if not found
     * @throws AMException
     *             if error encountered in fetching the DC node attributes.
     */
    public Map getDCTreeAttributes(SSOToken token, String entryDN,
            Set attrNames, boolean byteValues, int objectType)
            throws AMException, SSOException {
        // Object []
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    attrNames, new Boolean(byteValues),
                    new Integer(objectType) };
            return ((Map) client.send(client.encodeMessage(
                    "getDCTreeAttributes", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error(
                    "DirectoryManager.getDCTreeAttributes: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.getDCTreeAttributes: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getDCTreeAttributes: caught " +
                    "SSOException=",ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.getDCTreeAttributes: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public Map getAttributes(SSOToken token, String entryDN, int profileType)
            throws AMException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(profileType) };
            Map map = (Map) client.send(client.encodeMessage("getAttributes1",
                    objs), null);
            AMHashMap res = new AMHashMap();
            res.copy(map);
            return res;

        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getAttributes: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributes: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getAttributes: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.getAttributes: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public Map getAttributes(SSOToken token, String entryDN, Set attrNames,
            int profileType) throws AMException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    attrNames, new Integer(profileType) };
            Map map = (Map) client.send(client.encodeMessage("getAttributes2",
                    objs), null);
            AMHashMap res = new AMHashMap();
            res.copy(map);
            return res;
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getAttributes: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributes: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getAttributes: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.getAttributes: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public Map getAttributesByteValues(SSOToken token, String entryDN,
            int profileType) throws AMException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(profileType) };
            return ((Map) client.send(client.encodeMessage(
                    "getAttributesByteValues1", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getAttributesByteValues: caught " +
                    "exception=",amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributesByteValues: caught " +
                    "exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getAttributesByteValues: caught " +
                    "SSOException=", ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error("DirectoryManager.getAttributesByteValues: caught " +
                    "exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public Map getAttributesByteValues(SSOToken token, String entryDN,
            Set attrNames, int profileType) throws AMException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    attrNames, new Integer(profileType) };
            return ((Map) client.send(client.encodeMessage(
                    "getAttributesByteValues2", objs), null));

        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getAttributesByteValues: caught " +
                    "exception=", amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributesByteValues: caught " +
                    "exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getAttributesByteValues: caught " +
                    "SSOException=", ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error("DirectoryManager.getAttributesByteValues: caught " +
                    "exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

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
     * @return a Map containing attribute names as keys and Set of values
     *         corresponding to each key.
     * @throws AMException
     *             if an error is encountered in fetching the attributes
     */
    public Map getAttributes(SSOToken token, String entryDN,
            boolean ignoreCompliance, boolean byteValues, int profileType)
            throws AMException, SSOException {

        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Boolean(ignoreCompliance), new Boolean(byteValues),
                    new Integer(profileType) };
            Map map = (Map) client.send(client.encodeMessage("getAttributes3",
                    objs), null);
            AMHashMap res = new AMHashMap();
            res.copy(map);
            return res;
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getAttributes: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributes: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getAttributes: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.getAttributes: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

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
     *            The attrNames should not be null.
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
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    attrNames, new Boolean(ignoreCompliance),
                    new Boolean(byteValues), new Integer(profileType) };
            Map map = (Map) client.send(client.encodeMessage("getAttributes4",
                    objs), null);
            AMHashMap res = new AMHashMap();
            res.copy(map);
            return res;
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getAttributes: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributes: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getAttributes: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.getAttributes: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public String getOrgSearchFilter(String entryDN) {
        try {
            Object[] objs = { entryDN };
            return ((String) client.send(client.encodeMessage(
                    "getOrgSearchFilter", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error(
                    "DirectoryManager.getOrgSearchFilter: caught exception=",
                    amrex);
            return ("");
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.getOrgSearchFilter: caught exception=",
                    rex);
            return ("");
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.getOrgSearchFilter: caught exception=",
                    ex);
            return ("");
        }

    }

    /**
     * Gets the Organization DN for the specified entryDN. If the entry itself
     * is an org, then same DN is returned.
     * <p>
     * <b>NOTE:</b> This method will involve serveral directory searches, hence
     * be cautious of Performance hit
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
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN };
            return ((String) client.send(client.encodeMessage(
                    "getOrganizationDN", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error(
                    "DirectoryManager.getOrganizationDN: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.getOrganizationDN: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error(
                    "DirectoryManager.getOrganizationDN: caught SSOException=",
                    ssoe);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getOrganizationDN: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Gets the Organization DN for the specified entryDN. If the entry itself
     * is an org, then same DN is returned.
     * 
     * @param token
     *            a valid SSOToken
     * @param entryDN
     *            the entry whose parent Organization is to be obtained
     * @param childDN
     *            the immediate entry whose parent Organization is to be
     *            obtained
     * @return the DN String of the parent Organization
     * @throws AMException
     *             if an error occured while obtaining the parent Organization
     */
    public String verifyAndGetOrgDN(SSOToken token, String entryDN,
            String childDN) throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN, childDN };
            return ((String) client.send(client.encodeMessage(
                    "verifyAndGetOrgDN", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error(
                    "DirectoryManager.verifyAndGetOrgDN: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.verifyAndGetOrgDN: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error(
                    "DirectoryManager.verifyAndGetOrgDN: caught SSOException=",
                    ssoe);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.verifyAndGetOrgDN: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

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
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    attrNames, new Integer(profileType) };
            return ((Map) client.send(client.encodeMessage(
                    "getExternalAttributes", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getExternalAttributes: caught " +
                    "exception=", amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getExternalAttributes: caught " +
                    "exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getExternalAttributes: caught " +
                    "exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Adds or remove static group DN to or from member attribute
     * 'iplanet-am-static-group-dn'
     * 
     * @param token
     *            SSOToken
     * @param members
     *            set of user DN's
     * @param staticGroupDN
     *            DN of the static group
     * @param toAdd
     *            true to add, false to remove
     * @throws AMException
     *             if there is an internal problem with AM Store.
     */
    public void updateUserAttribute(SSOToken token, Set members,
            String staticGroupDN, boolean toAdd) throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), members,
                    staticGroupDN, new Boolean(toAdd) };
            client
                    .send(client.encodeMessage("updateUserAttribute", objs),
                            null);
        } catch (AMRemoteException amrex) {
            debug.error(
                    "DirectoryManager.updateUserAttribute: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.updateUserAttribute: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.updateUserAttribute: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Create an entry in the Directory
     * 
     * @param token
     *            SSOToken
     * @param entryName
     *            name of the entry (naming value), e.g. "sun.com", "manager"
     * @param objectType
     *            Profile Type, ORGANIZATION, AMObject.ROLE, AMObject.USER, etc.
     * @param parentDN
     *            the parent DN
     * @param attrSet
     *            the initial attribute set for creation
     */
    public void createEntry(SSOToken token, String entryName, int objectType,
            String parentDN, Map attributes) throws AMEntryExistsException,
            AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryName,
                    new Integer(objectType), parentDN, attributes };
            client.send(client.encodeMessage("createEntry", objs), null);

        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.createEntry: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.createEntry: caught exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.createEntry: caught exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
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
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(objectType), new Boolean(recursive),
                    new Boolean(softDelete) };
            client.send(client.encodeMessage("removeEntry", objs), null);
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.removeEntry: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.removeEntry: caught exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.removeEntry: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error("DirectoryManager.removeEntry: caught exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Remove group admin role
     * 
     * @param token
     *            SSOToken of the caller
     * @param dn
     *            group DN
     * @param recursive
     *            true to delete all admin roles for all sub groups or sub
     *            people container
     */
    public void removeAdminRole(SSOToken token, String dn, boolean recursive)
            throws SSOException, AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), dn,
                    new Boolean(recursive) };
            client.send(client.encodeMessage("removeAdminRole", objs), null);
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.removeAdminRole: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.removeAdminRole: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error(
                    "DirectoryManager.removeAdminRole: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error("DirectoryManager.removeAdminRole: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Searches the Directory
     * 
     * @param token
     *            SSOToken
     * @param entryDN
     *            DN of the entry to start the search with
     * @param searchFilter
     *            search filter
     * @param searchScope
     *            search scope, BASE, ONELEVEL or SUBTREE
     * @return Set set of matching DNs
     */
    public Set search(SSOToken token, String entryDN, String searchFilter,
            int searchScope) throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    searchFilter, new Integer(searchScope) };
            return ((Set) client.send(client.encodeMessage("search1", objs),
                    null));
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.search: caught exception=", amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.search: caught exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.search: caught exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /**
     * Search the Directory
     * 
     * @param token
     *            SSOToken
     * @param entryDN
     *            DN of the entry to start the search with
     * @param searchFilter
     *            search filter
     * @param SearchControl
     *            search control defining the VLV indexes and search scope
     * @return Set set of matching DNs
     */
    public AMSearchResults search(SSOToken token, String entryDN,
            String searchFilter, SearchControl searchControl,
            String attrNames[]) throws AMException {
        try {
            SortKey[] keys = searchControl.getSortKeys();
            LinkedList sortKeys = new LinkedList();
            for (int i = 0; (keys != null) && (i < keys.length); i++) {
                if (keys[i].reverse) {
                    sortKeys.add("true:" + keys[i].attributeName);
                } else {
                    // Using "fals" instead of "false" so that it
                    // has 4 characters as "true", hence easy to
                    // reconstruct SortKey
                    sortKeys.add("fals:" + keys[i].attributeName);
                }
            }

            int[] vlvRange = searchControl.getVLVRange();
            if (vlvRange == null) {
                vlvRange = new int[3];
            }
            Object[] objs = {
                    token.getTokenID().toString(),
                    entryDN,
                    searchFilter,
                    sortKeys,
                    new Integer(vlvRange[0]),
                    new Integer(vlvRange[1]),
                    new Integer(vlvRange[2]),
                    searchControl.getVLVJumpTo(),
                    new Integer(searchControl.getTimeOut()),
                    new Integer(searchControl.getMaxResults()),
                    new Integer(searchControl.getSearchScope()),
                    new Boolean(
                            searchControl.isGetAllReturnAttributesEnabled()),
                    attrNames };
            Map results = (Map) client.send("search2", objs, null);
            String cString = (String) results.remove(AMSR_COUNT);
            Set dns = (Set) results.remove(AMSR_RESULTS);
            String eString = (String) results.remove(AMSR_CODE);
            int count = 0, errorCode = 0;
            try {
                count = Integer.parseInt(cString);
                errorCode = Integer.parseInt(eString);
            } catch (NumberFormatException nfe) {
                debug.error("DirectoryManager.search: caught number "
                        + "format error", nfe);
            }
            return (new AMSearchResults(count, dns, errorCode, results));
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.search: caught exception=", amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.search: caught exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.search: caught exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /**
     * Get members for roles, dynamic group or static group
     * 
     * @param token
     *            SSOToken
     * @param entryDN
     *            DN of the role or group
     * @param objectType
     *            objectType of the target object, AMObject.ROLE or
     *            AMObject.GROUP
     * @return Set Member DNs
     */
    public Set getMembers(SSOToken token, String entryDN, int objectType)
            throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(objectType) };
            return ((Set) client.send(client.encodeMessage("getMembers", objs),
                    null));
        } catch (AMRemoteException amrex) {
            debug
                    .error("DirectoryManager.getMembers: caught exception=",
                            amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getMembers: caught exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getMembers: caught exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Renames an entry. Currently used for only user renaming
     * 
     * @param token
     *            the sso token
     * @param objectType
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
        try {
            Object[] objs = { token.getTokenID().toString(),
                    new Integer(objectType), entryDN, newName,
                    new Boolean(deleteOldName) };
            return ((String) client.send(client.encodeMessage("renameEntry",
                    objs), null));

        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.renameEntry: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.renameEntry: caught exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.renameEntry: caught exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

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
     * @param attrSet
     *            attributes to be set
     */
    public void setAttributes(SSOToken token, String entryDN, int objectType,
            Map stringAttributes, Map byteAttributes, boolean isAdd)
            throws AMException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(objectType), stringAttributes, byteAttributes,
                    new Boolean(isAdd) };
            client.send(client.encodeMessage("setAttributes", objs), null);
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.setAttributes: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.setAttributes: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.setAttributes: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.setAttributes: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Returns an array containing the dynamic group's scope, base dn, and
     * filter.
     */
    public String[] getGroupFilterAndScope(SSOToken token, String entryDN,
            int profileType) throws SSOException, AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(profileType) };
            LinkedList list = (LinkedList) client.send(client.encodeMessage(
                    "getGroupFilterAndScope", objs), null);
            String[] array = new String[list.size()];
            list.toArray(array);
            return array;
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getGroupFilterAndScope: caught " +
                    "exception=", amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getGroupFilterAndScope: caught " +
                    "exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getGroupFilterAndScope: caught " +
                    "SSOException=", ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error("DirectoryManager.getGroupFilterAndScope: caught " +
                    "exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Sets the filter for a dynamic group in the datastore.
     * 
     * @param token
     * @param entryDN
     * @param filter
     * @throws AMException
     * @throws SSOException
     */
    public void setGroupFilter(SSOToken token, String entryDN, String filter)
            throws AMException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN, filter };
            client.send(client.encodeMessage("setGroupFilter", objs), null);
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.setGroupFilter: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.setGroupFilter: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error(
                    "DirectoryManager.setGroupFilter: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error("DirectoryManager.setGroupFilter: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Modify member ship for role or static group
     * 
     * @param token
     *            SSOToken
     * @param members
     *            Set of member DN to be operated
     * @param target
     *            DN of the target object to add the member
     * @param type
     *            type of the target object, AMObject.ROLE or AMObject.GROUP
     * @param operation
     *            type of operation, ADD_MEMBER or REMOVE_MEMBER
     * @param updateUserEntry
     *            If true then call the updatUserAttribute when modifying group
     *            membership
     */
    public void modifyMemberShip(SSOToken token, Set members, String target,
            int type, int operation) throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), members, target,
                    new Integer(type), new Integer(operation) };
            client.send(client.encodeMessage("modifyMemberShip", objs), null);

        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.modifyMemberShip: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.modifyMemberShip: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.modifyMemberShip: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Get registered services for an organization
     * 
     * @param token
     *            SSOToken
     * @param entryDN
     *            DN of the org
     * @return Set set of service names
     */
    public Set getRegisteredServiceNames(SSOToken token, String entryDN)
            throws AMException {
        try {
            // String tokenid = (token != null) ? token.getTokenID().toString()
            // : null;
            Object[] objs = { null, entryDN };
            return ((Set) client.send(client.encodeMessage(
                    "getRegisteredServiceNames", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getRegisteredServiceNames: caught " +
                    "exception=", amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getRegisteredServiceNames: caught " +
                    "exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getRegisteredServiceNames: caught " +
                    "exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Register a service for an org or org unit policy to a profile
     * 
     * @param token
     *            token
     * @param orgDN
     *            DN of the org
     * @param serviceName
     *            Service Name
     */
    public void registerService(SSOToken token, String orgDN, 
            String serviceName) throws AMException, SSOException {
        try {
            Object[] objs = 
                { token.getTokenID().toString(), orgDN, serviceName };
            client.send(client.encodeMessage("registerService", objs), null);
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.registerService: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.registerService: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error(
                    "DirectoryManager.registerService: caught SSOException=",
                    ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error("DirectoryManager.registerService: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Un register service for a AMro profile.
     * 
     * @param token
     *            SSOToken
     * @param entryDN
     *            DN of the profile whose service is to be removed
     * @param objectType
     *            profile type
     * @param serviceName
     *            Service Name
     * @param template
     *            AMTemplate
     * @param type
     *            Template type
     */
    public void unRegisterService(SSOToken token, String entryDN,
            int objectType, String serviceName, AMTemplate template, int type)
            throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(objectType), serviceName, new Integer(type) };
            client.send(client.encodeMessage("unRegisterService", objs), null);
        } catch (AMRemoteException amrex) {
            debug.error(
                    "DirectoryManager.unRegisterService: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.unRegisterService: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.unRegisterService: caught exception=",
                            ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Get the AMTemplate DN (COSTemplateDN)
     * 
     * @param token
     *            SSOToken
     * @param entryDN
     *            DN of the profile whose template is to be set
     * @param serviceName
     *            Service Name
     * @param type
     *            the template type, AMTemplate.DYNAMIC_TEMPLATE
     * @return String DN of the AMTemplate
     */
    public String getAMTemplateDN(SSOToken token, String entryDN,
            int objectType, String serviceName, int type) throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(objectType), serviceName, new Integer(type) };
            return ((String) client.send(client.encodeMessage(
                    "getAMTemplateDN", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getAMTemplateDN: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAMTemplateDN: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getAMTemplateDN: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

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
     * @return String DN of the newly created template
     */
    public String createAMTemplate(SSOToken token, String entryDN,
            int objectType, String serviceName, Map attributes, int priority)
            throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), entryDN,
                    new Integer(objectType), serviceName, attributes,
                    new Integer(priority) };
            return ((String) client.send(client.encodeMessage(
                    "createAMTemplate", objs), null));

        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.createAMTemplate: caught exception=",
                    amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.createAMTemplate: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.createAMTemplate: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /**
     * Gets the naming attribute after reading it from the corresponding
     * creation template. If not found, a default value will be used
     */
    public String getNamingAttr(int objectType, String orgDN) {
        try {
            Object[] objs = { new Integer(objectType), orgDN };
            return ((String) client.send(client.encodeMessage("getNamingAttr",
                    objs), null));
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getNamingAttr: caught exception=",
                    rex);
            return null;
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.getNamingAttr: caught exception=",
                            ex);
            return null;
        }

    }

    /**
     * Get the name of the creation template to use for specified object type.
     */
    public String getCreationTemplateName(int objectType) {
        try {
            Object[] objs = { new Integer(objectType) };
            return ((String) client.send(client.encodeMessage(
                    "getCreationTemplateName", objs), null));
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getCreationTemplateName: caught " +
                    "exception=", rex);
            return null;
        } catch (Exception ex) {
            debug.error("DirectoryManager.getCreationTemplateName: caught " +
                    "exception=", ex);
            return null;
        }

    }

    public String getObjectClassFromDS(int objectType) {
        try {
            Object[] objs = { new Integer(objectType) };
            return ((String) client.send(client.encodeMessage(
                    "getObjectClassFromDS", objs), null));
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.getObjectClassFromDS: caught exception=",
                    rex);
            return null;
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.getObjectClassFromDS: caught exception=",
                    ex);
            return null;
        }

    }

    /**
     * Returns the set of attributes (both optional and required) needed for an
     * objectclass based on the LDAP schema
     * 
     * @param objectclass
     * @return
     */
    public Set getAttributesForSchema(String objectclass) {
        try {
            Object[] objs = { objectclass };
            return ((Set) client.send(client.encodeMessage(
                    "getAttributesForSchema", objs), null));
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributesForSchema: caught " +
                    "exception=", rex);
            return Collections.EMPTY_SET;
        } catch (Exception ex) {
            debug.error("DirectoryManager.getAttributesForSchema: caught " +
                    "exception=", ex);
            return Collections.EMPTY_SET;
        }

    }

    public String getSearchFilterFromTemplate(int objectType, String orgDN,
            String searchTemplateName) {
        try {
            Object[] objs = { new Integer(objectType), orgDN };
            return ((String) client.send(client.encodeMessage(
                    "getSearchFilterFromTemplate", objs), null));
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getSearchFilterFromTemplate: " +
                    "caught exception=", rex);
            return null;
        } catch (Exception ex) {
            debug.error("DirectoryManager.getSearchFilterFromTemplate: " +
                    "caught exception=", ex);
            return null;
        }

    }

    public Set getTopLevelContainers(SSOToken token) throws AMException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString() };
            return ((Set) client.send(client.encodeMessage(
                    "getTopLevelContainers", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getTopLevelContainers: caught " +
                    "exception=", amrex);
            throw convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getTopLevelContainers: caught " +
                    "exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (SSOException ssoe) {
            debug.error("DirectoryManager.getTopLevelContainers: caught " +
                    "SSOException=", ssoe);
            throw ssoe;
        } catch (Exception ex) {
            debug.error("DirectoryManager.getTopLevelContainers: caught " +
                    "exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    protected static AMException convertException(AMRemoteException amrx) {
        return new AMException(amrx.getMessage(), amrx.getErrorCode(), amrx
                .getMessageArgs());
    }

    public void addListener(SSOToken token, AMObjectListener listener,
            Map configMap) throws AMEventManagerException {
        if (eventListener == null) {
            eventListener = new EventListener();
        }
        eventListener.addListener(token, listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#create(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Map,
     *      java.lang.String)
     */
    public AMIdentity create(SSOToken token, IdType type, String name,
            Map attrMap, String amOrgName) throws IdRepoException, SSOException 
    {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, attrMap, amOrgName };
            String univid = (String) client.send(client.encodeMessage(
                    "create_idrepo", objs), null);
            return IdUtils.getIdentity(token, univid);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.create_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.create_idrepo: caught exception=",
                            ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#delete(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void delete(SSOToken token, IdType type, String name,
            String orgName, String amsdkDN) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, orgName, amsdkDN };
            client.send(client.encodeMessage("delete_idrepo", objs), null);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.create_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug
                    .error("DirectoryManager.create_idrepo: caught exception=",
                            ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#getAttributes(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Set,
     *      java.lang.String, java.lang.String)
     */
    public Map getAttributes(SSOToken token, IdType type, String name,
            Set attrNames, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, attrNames, amOrgName, amsdkDN };
            Map res = ((Map) client.send(client.encodeMessage(
                    "getAttributes1_idrepo", objs), null));
            if (res != null) {
                Map res2 = new AMHashMap();
                Iterator it = res.keySet().iterator();
                while (it.hasNext()) {
                    Object attr = it.next();
                    res2.put(attr, res.get(attr));
                }
                res = res2;
            }
            return res;
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributes1_idrepo: caught " +
                    "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getAttributes1_idrepo: caught " +
                    "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#getAttributes(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Map getAttributes(SSOToken token, IdType type, String name,
            String amOrgName, String amsdkDN) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, amOrgName, amsdkDN };
            Map res = ((Map) client.send(client.encodeMessage(
                    "getAttributes2_idrepo", objs), null));
            if (res != null) {
                Map res2 = new AMHashMap();
                Iterator it = res.keySet().iterator();
                while (it.hasNext()) {
                    Object attr = it.next();
                    res2.put(attr, res.get(attr));
                }
                res = res2;
            }
            return res;
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAttributes2_idrepo: caught " +
                    "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getAttributes2_idrepo: caught " +
                    "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#removeAttributes(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Set,
     *      java.lang.String, java.lang.String)
     */
    public void removeAttributes(SSOToken token, IdType type, String name,
            Set attrNames, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, attrNames, amOrgName, amsdkDN };
            client.send(client.encodeMessage("removeAttributes_idrepo", objs),
                    null);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.removeAttributes_idrepo: caught " +
                    "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.removeAttributes_idrepo: caught " +
                    "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#search(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String,
     *      com.sun.identity.idm.IdSearchControl, java.lang.String)
     */
    public IdSearchResults search(SSOToken token, IdType type, String pattern,
            IdSearchControl ctrl, String amOrgName) throws IdRepoException,
            SSOException {
        IdSearchOpModifier modifier = ctrl.getSearchModifier();
        Map avMap = ctrl.getSearchModifierMap();
        int filterOp;
        if (modifier.equals(IdSearchOpModifier.AND)) {
            filterOp = IdRepo.AND_MOD;
        } else {
            filterOp = IdRepo.OR_MOD;
        }
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    pattern, new Integer(ctrl.getTimeOut()),
                    new Integer(ctrl.getMaxResults()),
                    ctrl.getReturnAttributes(),
                    new Boolean(ctrl.isGetAllReturnAttributesEnabled()),
                    new Integer(filterOp), avMap,
                    new Boolean(ctrl.isRecursive()), amOrgName };
            Map idresults = ((Map) client.send(client.encodeMessage(
                    "search2_idrepo", objs), null));
            return mapToIdSearchResults(token, type, amOrgName, idresults);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.search2_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.search2_idrepo: caught exception=",
                    ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#search(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Map,
     *      boolean, int, int, java.util.Set, java.lang.String)
     */
    public IdSearchResults search(SSOToken token, IdType type, String pattern,
            Map avPairs, boolean recursive, int maxResults, int maxTime,
            Set returnAttrs, String amOrgName) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    pattern, avPairs, new Boolean(recursive),
                    new Integer(maxResults), new Integer(maxTime), returnAttrs,
                    amOrgName };
            Map idresults = ((Map) client.send(client.encodeMessage(
                    "search1_idrepo", objs), null));
            return mapToIdSearchResults(token, type, amOrgName, idresults);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.search1_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.search1_idrepo: caught exception=",
                    ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#setAttributes(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Map,
     *      boolean, java.lang.String, java.lang.String)
     */
    public void setAttributes(SSOToken token, IdType type, String name,
            Map attributes, boolean isAdd, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, attributes, new Boolean(isAdd), amOrgName, amsdkDN };
            client.send(client.encodeMessage("setAttributes_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.setAttributes_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.setAttributes_idrepo: caught exception=",
                    ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#assignService(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String,
     *      com.sun.identity.sm.SchemaType, java.util.Map, java.lang.String,
     *      java.lang.String)
     */
    public void assignService(SSOToken token, IdType type, String name,
            String serviceName, SchemaType stype, Map attrMap,
            String amOrgName, String amsdkDN) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, serviceName, stype.getType(), attrMap, amOrgName,
                    amsdkDN };
            client.send(client.encodeMessage("assignService_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.assignService_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.assignService_idrepo: caught exception=",
                    ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#getAssignedServices(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Map,
     *      java.lang.String, java.lang.String)
     */
    public Set getAssignedServices(SSOToken token, IdType type, String name,
            Map mapOfServiceNamesAndOCs, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, mapOfServiceNamesAndOCs, amOrgName, amsdkDN };
            return ((Set) client.send(client.encodeMessage(
                    "getAssignedServices_idrepo", objs), null));

        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getAssignedServices_idrepo: caught " +
                    "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getAssignedServices_idrepo: caught " +
                    "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#getServiceAttributes(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String,
     *      java.util.Set, java.lang.String, java.lang.String)
     */
    public Map getServiceAttributes(SSOToken token, IdType type, String name,
            String serviceName, Set attrNames, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, serviceName, attrNames, amOrgName, amsdkDN };
            return ((Map) client.send(client.encodeMessage(
                    "getServiceAttributes_idrepo", objs), null));

        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getServiceAttributes_idrepo: caught " 
                    + "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getServiceAttributes_idrepo: caught " 
                    + "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#unassignService(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String,
     *      java.util.Map, java.lang.String, java.lang.String)
     */
    public void unassignService(SSOToken token, IdType type, String name,
            String serviceName, Map attrMap, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, serviceName, attrMap, amOrgName, amsdkDN };
            client.send(client.encodeMessage("unassignService_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            debug.error("DirectoryManager.unassignService_idrepo: caught " +
                    "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.unassignService_idrepo: caught " +
                    "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#modifyService(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String,
     *      com.sun.identity.sm.SchemaType, java.util.Map, java.lang.String,
     *      java.lang.String)
     */
    public void modifyService(SSOToken token, IdType type, String name,
            String serviceName, SchemaType stype, Map attrMap,
            String amOrgName, String amsdkDN) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, serviceName, stype.getType(), attrMap, amOrgName,
                    amsdkDN };
            client.send(client.encodeMessage("modifyService_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.modifyService_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.modifyService_idrepo: caught exception=",
                    ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#getMembers(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String,
     *      com.sun.identity.idm.IdType)
     */
    public Set getMembers(SSOToken token, IdType type, String name,
            String amOrgName, IdType membersType, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, amOrgName, membersType.getName(), amsdkDN };
            Set res = (Set) client.send(client.encodeMessage(
                    "getMembers_idrepo", objs), null);
            Set idres = new HashSet();
            if (res != null) {
                Iterator it = res.iterator();
                while (it.hasNext()) {
                    String univid = (String) it.next();
                    AMIdentity id = IdUtils.getIdentity(token, univid);
                    idres.add(id);
                }
            }
            return idres;
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.getMembers_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getMembers_idrepo: caught exception=",
                            ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#getMemberships(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String,
     *      com.sun.identity.idm.IdType, java.lang.String)
     */
    public Set getMemberships(SSOToken token, IdType type, String name,
            IdType membershipType, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, membershipType.getName(), amOrgName, amsdkDN };
            Set res = (Set) client.send(client.encodeMessage(
                    "getMemberships_idrepo", objs), null);
            Set idres = new HashSet();
            if (res != null) {
                Iterator it = res.iterator();
                while (it.hasNext()) {
                    String univid = (String) it.next();
                    AMIdentity id = IdUtils.getIdentity(token, univid);
                    idres.add(id);
                }
            }
            return idres;

        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getMemberships_idrepo: caught " +
                    "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getMemberships_idrepo: caught " +
                    "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#modifyMemberShip(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.util.Set,
     *      com.sun.identity.idm.IdType, int, java.lang.String)
     */
    public void modifyMemberShip(SSOToken token, IdType type, String name,
            Set members, IdType membersType, int operation, String amOrgName)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, members, membersType.getName(),
                    new Integer(operation), amOrgName };
            client.send(client.encodeMessage("modifyMemberShip_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            debug.error("DirectoryManager.modifyMemberShip_idrepo: caught " +
                    "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.modifyMemberShip_idrepo: caught " +
                    "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#getSupportedOperations(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String)
     */
    public Set getSupportedOperations(SSOToken token, IdType type,
            String amOrgName) throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    amOrgName };
            Set ops = (Set) client.send(client.encodeMessage(
                    "getSupportedOperations_idrepo", objs), null);
            Set resOps = new HashSet();
            if (ops != null) {
                Iterator it = ops.iterator();
                while (it.hasNext()) {
                    String op = (String) it.next();
                    IdOperation idop = new IdOperation(op);
                    resOps.add(idop);
                }
            }
            return resOps;
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getSupportedOperations_idrepo: " +
                    "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getSupportedOperations_idrepo: " +
                    "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#getSupportedTypes(
     *      com.iplanet.sso.SSOToken,
     *      java.lang.String)
     */
    public Set getSupportedTypes(SSOToken token, String amOrgName)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), amOrgName };
            Set types = (Set) client.send(client.encodeMessage(
                    "getSupportedTypes_idrepo", objs), null);
            Set resTypes = new HashSet();
            if (types != null) {
                Iterator it = types.iterator();
                while (it.hasNext()) {
                    String currType = (String) it.next();
                    IdType thisType = IdUtils.getType(currType);
                    resTypes.add(thisType);
                }
            }
            return resTypes;
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getSupportedTypes_idrepo: caught " +
                    "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getSupportedTypes_idrepo: caught " +
                    "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.DirectoryManagerInterface#isExists(
     *      com.iplanet.sso.SSOToken,
     *      com.sun.identity.idm.IdType, java.lang.String, java.lang.String)
     */
    public boolean isExists(SSOToken token, IdType type, String name,
            String amOrgName) throws SSOException, IdRepoException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isActive(SSOToken token, IdType type, String name,
            String amOrgName, String amsdkDN) throws SSOException,
            IdRepoException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, amOrgName, amsdkDN };
            Boolean res = ((Boolean) client.send(client.encodeMessage(
                    "isActive_idrepo", objs), null));
            return res.booleanValue();
        } catch (RemoteException rex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    private IdSearchResults mapToIdSearchResults(SSOToken token, IdType type,
            String orgName, Map m) throws IdRepoException {
        IdSearchResults results = new IdSearchResults(type, orgName);
        Set idSet = (Set) m.get(AMSR_RESULTS);
        Map attrMaps = (Map) m.get(AMSR_ATTRS);
        Integer err = (Integer) m.get(AMSR_CODE);
        if (idSet != null) {
            Iterator it = idSet.iterator();
            while (it.hasNext()) {
                String idStr = (String) it.next();
                AMIdentity id = IdUtils.getIdentity(token, idStr);
                Map attrMap = (Map) attrMaps.get(idStr);
                results.addResult(id, attrMap);
            }
        }
        if (err != null) {
            results.setErrorCode(err.intValue());
        }
        return results;
    }
}

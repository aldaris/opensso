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
 * $Id: InfocardUtils.java,v 1.1 2009-07-08 08:59:28 ppetitsm Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2008 Patrick Petit Consulting
 */

package com.identarian.infocard.opensso.rp;

import com.identarian.infocard.opensso.exception.InfocardException;
import com.identarian.infocard.opensso.rp.InfocardData;
import com.iplanet.sso.SSOException;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.security.DecodeAction;
import com.sun.identity.security.EncodeAction;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.encode.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Patrick
 *
 * TODO: Find a way to invoke removeInfocard() when a PPID multi-value is removed.
 *       Current implementation may cause memory leak on a long run
 */
public class InfocardUtils {

    protected static final String INFOCARD_OBJECT_CLASS = "infocard";
    protected static final String PPID_ATTRIBUTE = "ic-ppid";
    protected static final String INFOCARD_ATTRIBUTE = "ic-data";
    protected static final Set<String> INFOCARD_ATTRIBUTES = new HashSet<String>() {

        {
            add(PPID_ATTRIBUTE);
            add(INFOCARD_ATTRIBUTE);
        }
    };
    private static Debug debug = Debug.getInstance(Infocard.amAuthInfocard);

    protected static InfocardData readInfocardData(AMIdentity userIdentity,
            String ppid) throws InfocardException {

        InfocardData icData = null;
        Map<String, Set> attributeMap = (Map<String,Set>)Collections.EMPTY_MAP;
        Set<String> attributeValue = (Set<String>)Collections.EMPTY_SET;

        try {
            attributeMap = userIdentity.getAttributes(INFOCARD_ATTRIBUTES);
            attributeValue = (Set<String>) attributeMap.get(PPID_ATTRIBUTE);
            if ((attributeValue != null) && (!attributeValue.isEmpty())) {
                Iterator itr = attributeValue.iterator();
                String value = null;
                while (itr.hasNext()) {
                    value = ((String) itr.next()).trim();
                    if (value.equals(ppid)) {
                        icData = readInfocardDataAttribute(attributeMap);
                        if (icData == null) {
                            // Looks like an internal error.
                            // Handle silently by removing all
                            // orphan Information Card attributes
                            removeInfocards(userIdentity);
                            throw new InfocardException(
                                    "Internal error: ic-data attribute missing from idRepo.");
                        }
                        break;
                    }
                }
            }
        } catch (IdRepoException e1) {
            throw new InfocardException("Failed to read Information Card attributes", e1);
        } catch (SSOException e2) {
            throw new InfocardException("Failed to read Information Card attributes", e2);
        }
        return icData;
    }

    protected static void addInfocard(AMIdentity userIdentity, String ppid,
            String digest, String password) throws InfocardException {

        Map<String, Set> attributeMap = (Map<String,Set>)Collections.EMPTY_MAP;
        Set<String> ppidAttrValue = (Set<String>)Collections.EMPTY_SET;
        Set<String> icDataAttrValue = null;

        try {
            attributeMap = userIdentity.getAttributes(INFOCARD_ATTRIBUTES);
            ppidAttrValue = (Set<String>) attributeMap.get(PPID_ATTRIBUTE);
            if ((ppidAttrValue != null) && (!ppidAttrValue.isEmpty())) {
                if (!ppidAttrValue.contains(ppid)) {
                    // This PPID is not registered
                    ppidAttrValue.add(ppid);
                    InfocardData icData = readInfocardDataAttribute(attributeMap);
                    if (icData != null) {
                        icData.put(ppid, digest);
                        icDataAttrValue = new HashSet<String>();
                        icDataAttrValue.add(getEncodedInfocardData(icData));
                    } else {
                        // Something's broken. Handle this by removing all
                        // IC attributes
                        removeInfocards(userIdentity);
                        throw new InfocardException(
                                "Internal error: Information Card ic-data attribute missing from idRepo.");
                    }
                } else {
                    if (debug.errorEnabled()) {
                        debug.error("Attempting to create an Information Card record duplicate");
                    }
                }
            } else {
                // No Information Card registered for that user yet
                ppidAttrValue = new HashSet<String>();
                ppidAttrValue.add(ppid);
                InfocardData icData = new InfocardData(password);
                icData.put(ppid, digest);
                icDataAttrValue = new HashSet<String>();
                icDataAttrValue.add(getEncodedInfocardData(icData));
                setInfocardObjectClass(userIdentity);
            }
            attributeMap = new HashMap<String, Set>();
            attributeMap.put(PPID_ATTRIBUTE, ppidAttrValue);
            attributeMap.put(INFOCARD_ATTRIBUTE, icDataAttrValue);
            userIdentity.setAttributes(attributeMap);
            userIdentity.store();

        } catch (IdRepoException e1) {
            throw new InfocardException("Failed to add Information Card attributes", e1);
        } catch (SSOException e2) {
            throw new InfocardException("Failed to add Information Card attributes", e2);
        }
    }

    protected static void removeInfocard(AMIdentity userIdentity, String ppid)
            throws InfocardException {

        Map<String, Set> attributeMap = Collections.EMPTY_MAP;
        Set<String> ppidAttrValue = Collections.EMPTY_SET;
        Set<String> icDataAttrValue = null;

        try {
            attributeMap = userIdentity.getAttributes(INFOCARD_ATTRIBUTES);
            ppidAttrValue = (Set<String>) attributeMap.get(PPID_ATTRIBUTE);
            if ((ppidAttrValue != null) && !ppidAttrValue.isEmpty() && ppidAttrValue.contains(ppid)) {
                // This IC is not registered
                ppidAttrValue.remove(ppid);
                InfocardData icData = readInfocardDataAttribute(attributeMap);
                if (icData != null) {
                    icData.remove(ppid);
                    icDataAttrValue = new HashSet<String>();
                    icDataAttrValue.add(getEncodedInfocardData(icData));
                    attributeMap = new HashMap<String, Set>();
                    attributeMap.put(PPID_ATTRIBUTE, ppidAttrValue);
                    attributeMap.put(INFOCARD_ATTRIBUTE, icDataAttrValue);
                    userIdentity.setAttributes(attributeMap);
                    userIdentity.store();
                    attributeMap.put(INFOCARD_ATTRIBUTE, ppidAttrValue);
                } else {
                    // Something's broken. Handle by removing all
                    // IC attributes for that user
                    removeInfocards(userIdentity);
                    debug.error("Internal error: Information Card ic-data attribute missing from idRepo");
                    throw new InfocardException(
                            "Internal error: Information Card ic-data attribute missing from idRepo");
                }
            }
        } catch (IdRepoException e1) {
            throw new InfocardException("Failed to read Information Card attributes", e1);
        } catch (SSOException e2) {
            throw new InfocardException("Failed to read Information Card attributes", e2);
        }
    }

    protected static AMIdentity searchUserIdentity(AMIdentityRepository idRepo,
            String ppid, String userId) throws InfocardException {

        AMIdentity userIdentity = null;

        IdType idtype = IdType.USER;
        IdSearchControl isc = new IdSearchControl();
        isc.setAllReturnAttributes(true);
        isc.setTimeOut(0);
        Map<String,Set> avMap = new HashMap<String,Set>();
        Set<String> set = new HashSet<String>();
        set.add(INFOCARD_OBJECT_CLASS);
        avMap.put("objectclass", set);
        set = new HashSet();
        set.add(ppid);
        avMap.put(PPID_ATTRIBUTE, set);
        isc.setSearchModifiers(IdSearchOpModifier.OR, avMap);
        try {
            IdSearchResults results = idRepo.searchIdentities(idtype, userId, isc);
            Set idSet = results.getSearchResults();
            userIdentity = getFirstMatchingEntry(idSet, ppid);
        } catch (IdRepoException e1) {
            throw new InfocardException("Failed to search identity", e1);
        } catch (SSOException e2) {
            throw new InfocardException("Failed to search identity", e2);
        }

        return userIdentity;
    }

    protected static void removeInfocards(AMIdentity userIdentity)
            throws InfocardException {

        try {
            userIdentity.removeAttributes(INFOCARD_ATTRIBUTES);
        } catch (IdRepoException e1) {
            throw new InfocardException("Failed to remove Information Card attributes", e1);
        } catch (SSOException e2) {
            throw new InfocardException("Failed to remove Information Card attributes", e2);
        }
    }

    protected static String generateDynamicUserId(String gn, String sn) {

        StringBuffer fname = new StringBuffer();
        if (gn != null && gn.length() != 0)
            fname.append(gn);
        if (sn != null && sn.length() > 0)
            fname.append(sn);
        UUID uid = UUID.nameUUIDFromBytes(fname.toString().getBytes());
        return String.valueOf(uid);
    }

    private static InfocardData readInfocardDataAttribute(Map<String, Set> attributeMap)
    throws InfocardException {

        InfocardData icData = null;
        Set<String> attributeValue = (Set<String>)Collections.EMPTY_SET;
        attributeValue = (Set<String>) attributeMap.get(INFOCARD_ATTRIBUTE);
        if ((attributeValue != null) && (!attributeValue.isEmpty())) {
            Iterator itr = attributeValue.iterator();
            String value = null;
            // ic-data is a SINGLE_VALUE attribute
            value =
                    ((String) itr.next()).trim();
            if (value != null && value.length() != 0) {
                icData = getDecodedInfocardData(value);
            }
        }
        return icData;
    }

    private static AMIdentity getFirstMatchingEntry(Set idSet, String ppid)
            throws InfocardException {

        Object[] objs = idSet.toArray();
        AMIdentity userIdentity = null;
        int setsize = idSet.size();

        if (setsize > 0) {
            if (debug.messageEnabled()) {
                debug.message("getSearchResultsFirstEntry: Search returns " + setsize + " entries");
            }

            int i;
            String userID;
            AMIdentity curIdentity = null;
            for (i = 0; i < setsize; i++) {
                curIdentity = (AMIdentity) objs[i];
                userID = curIdentity.getName();
                if (debug.messageEnabled()) {
                    debug.message("\tFound " +
                            userID + " with universal ID = " + curIdentity.getUniversalId());
                }
                // Get admin users out this
                if (userID.equalsIgnoreCase("amadmin") ||
                        userID.equalsIgnoreCase("amldapuser") ||
                        userID.equalsIgnoreCase("dsameuser") ||
                        userID.equalsIgnoreCase("amService-URLAccessAgent")) {
                    // don't mess with admin users
                    return null;
                }

                Set attributeValue = Collections.EMPTY_SET;
                try {
                    attributeValue = curIdentity.getAttribute(PPID_ATTRIBUTE);
                    if ((attributeValue != null) && (!attributeValue.isEmpty())) {
                        Iterator itr = attributeValue.iterator();
                        while (itr.hasNext()) {
                            String value = ((String) itr.next()).trim();
                            if (value.equals(ppid)) {
                                userIdentity = curIdentity;
                                break;
                            }
                        }
                    }
                } catch (IdRepoException e1) {
                    throw new InfocardException("Failed to remove Information Card attributes", e1);
                } catch (SSOException e2) {
                    throw new InfocardException("Failed to remove Information Card attributes", e2);
                }

                if (userIdentity != null) {
                    break;
                }
            }
        }
        return userIdentity;
    }

    private static void setInfocardObjectClass(AMIdentity userIdentity)
            throws InfocardException {

        try {
            Set attrValueSetObjectClass = userIdentity.getAttribute("objectClass");
            if ((attrValueSetObjectClass != null) &&
                    (!attrValueSetObjectClass.contains(INFOCARD_OBJECT_CLASS))) {
                attrValueSetObjectClass.add(INFOCARD_OBJECT_CLASS);
                Map<String, Set> map = new HashMap<String, Set>(2);
                map.put("ObjectClass", attrValueSetObjectClass);
                userIdentity.setAttributes(map);
            }
        } catch (IdRepoException e1) {
            throw new InfocardException("Failed to set 'infocard' Object Class", e1);
        } catch (SSOException e2) {
            throw new InfocardException("Failed to set 'infocard' Object Class", e2);
        }

    }

    private static String getEncodedInfocardData(InfocardData icData)
            throws InfocardException {

        byte[] sSerialized = null;

        String encodedString = null;
        ByteArrayOutputStream byteOut;

        ObjectOutputStream objOutStream;

        try {
            byteOut = new ByteArrayOutputStream();
            objOutStream =
                    new ObjectOutputStream(byteOut);

            //convert object to byte using streams
            objOutStream.writeObject(icData);
            sSerialized =
                    byteOut.toByteArray();

            // base 64 encoding & encrypt
            encodedString =
                    (String) AccessController.doPrivileged(
                    new EncodeAction(Base64.encode(sSerialized).trim()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InfocardException("Fatal internal error", e);
        }
        return encodedString;
    }

    private static InfocardData getDecodedInfocardData(String icDataStr)
            throws InfocardException {

        InfocardData icData = null;

        // decrypt and then decode
        String decStr = (String) AccessController.doPrivileged(
                new DecodeAction(icDataStr));
        byte[] sSerialized = Base64.decode(decStr);

        if (sSerialized == null) {
            return null;
        }

        byte byteDecrypted[];
        ByteArrayInputStream byteIn;

        ObjectInputStream objInStream = null;
        try {
            byteDecrypted = sSerialized;
            //convert byte to object using streams
            byteIn =
                    new ByteArrayInputStream(byteDecrypted);
            objInStream =
                    new ObjectInputStream(byteIn);
            icData =
                    (InfocardData) objInStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InfocardException("Fatal internal error", e);
        }
        return icData;
    }
}

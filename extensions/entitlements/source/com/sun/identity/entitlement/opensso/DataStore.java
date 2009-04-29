/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: DataStore.java,v 1.6 2009-04-29 13:22:47 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.ResourceSaveIndexes;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import com.sun.identity.entitlement.SubjectAttributesManager;
import com.sun.identity.entitlement.util.NetworkMonitor;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.BufferedIterator;
import com.sun.identity.shared.encode.Base64;
import com.sun.identity.shared.ldap.LDAPDN;
import com.sun.identity.sm.AttributeSchema;
import com.sun.identity.sm.SMSDataEntry;
import com.sun.identity.sm.SMSEntry;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceSchemaManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class DataStore {
    private static final String SERVICE_NAME = "PolicyIndex";
    private static final String INDEX_COUNT = "indexCount";
    private static final String START_DN_TEMPLATE =
         "ou=default,ou=GlobalConfig,ou=1.0,ou=" + SERVICE_NAME +
         ",ou=services,{0}";
    private static final String SUBJECT_INDEX_KEY = "subjectindex";
    private static final String HOST_INDEX_KEY = "hostindex";
    private static final String PATH_INDEX_KEY = "pathindex";
    private static final String PATH_PARENT_INDEX_KEY = "pathparentindex";
    private static final String SERIALIZABLE_INDEX_KEY = "serializable";
    private static final String SUBJECT_FILTER_TEMPLATE =
        "(" + SMSEntry.ATTR_XML_KEYVAL + "=" + SUBJECT_INDEX_KEY + "={0})";
    private static final String HOST_FILTER_TEMPLATE =
        "(" + SMSEntry.ATTR_XML_KEYVAL + "=" + HOST_INDEX_KEY + "={0})";
    private static final String PATH_FILTER_TEMPLATE =
        "(" + SMSEntry.ATTR_XML_KEYVAL + "=" + PATH_INDEX_KEY + "={0})";
    private static final String PATH_PARENT_FILTER_TEMPLATE =
        "(" + SMSEntry.ATTR_XML_KEYVAL + "=" + PATH_PARENT_INDEX_KEY + "={0})";
    private static String PRIVILEGE_DN;
    private static String BASE_DN;

    private static final NetworkMonitor DB_MONITOR =
        NetworkMonitor.getInstance("dbLookup");

    static {
        Object[] p = {SMSEntry.getRootSuffix()};
        BASE_DN = MessageFormat.format(START_DN_TEMPLATE, p);
        PRIVILEGE_DN = "ou={0}," + BASE_DN;
    }

    public static String getDistinguishedName(String name) {
        Object[] arg = {name};
        return MessageFormat.format(PRIVILEGE_DN, arg);
    }

    private void updateIndexCount(SSOToken adminToken, int num) {
        try {
            ServiceSchemaManager mgr = new ServiceSchemaManager(
                SERVICE_NAME, adminToken);
            AttributeSchema as = mgr.getGlobalSchema().getAttributeSchema(
                INDEX_COUNT);
            Set set = as.getDefaultValues();
            int count = 0;
            if ((set != null) && !set.isEmpty()) {
                String strCount = (String)set.iterator().next();
                count = Integer.parseInt(strCount);
                count += num;
                set.clear();
                set.add(Integer.toString(count));
            } else {
                set = new HashSet();
                set.add(Integer.toString(num));
            }
            as.setDefaultValues(set);
        } catch (NumberFormatException ex) {
            //TOFIX
        } catch (SMSException ex) {
            //TOFIX
        } catch (SSOException ex) {
            //TOFIX
        }
    }

    private int getIndexCount() {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        int count = 0;
        try {
            ServiceSchemaManager mgr = new ServiceSchemaManager(
                SERVICE_NAME, adminToken);
            AttributeSchema as = mgr.getGlobalSchema().getAttributeSchema(
                INDEX_COUNT);
            Set set = as.getDefaultValues();
            if ((set != null) && !set.isEmpty()) {
                String strCount = (String)set.iterator().next();
                count = Integer.parseInt(strCount);
            }
        } catch (NumberFormatException ex) {
            //TOFIX
        } catch (SMSException ex) {
            //TOFIX
        } catch (SSOException ex) {
            //TOFIX
        }
        return count;
    }

    public String add(Privilege p)
        throws EntitlementException {
        ResourceSaveIndexes indexes =
            p.getEntitlement().getResourceSaveIndexes();
        Set<String> subjectIndexes =
            SubjectAttributesManager.getSubjectSearchIndexes(p);

        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        String dn = getDistinguishedName(p.getName());
        try {
            SMSEntry s = new SMSEntry(adminToken, dn);
            Map<String, Set<String>> map = new HashMap<String, Set<String>>();

            Set<String> searchable = new HashSet<String>();
            map.put(SMSEntry.ATTR_XML_KEYVAL, searchable);

            for (String i : indexes.getHostIndexes()) {
                searchable.add(HOST_INDEX_KEY + "=" + i);
            }
            for (String i : indexes.getPathIndexes()) {
                searchable.add(PATH_INDEX_KEY + "=" + i);
            }
            for (String i : indexes.getParentPath()) {
                searchable.add(PATH_PARENT_INDEX_KEY + "=" + i);
            }
            for (String i : subjectIndexes) {
                searchable.add(SUBJECT_INDEX_KEY + "=" + i);
            }

            Set<String> set = new HashSet<String>(2);
            map.put(SMSEntry.ATTR_KEYVAL, set);
            set.add(SERIALIZABLE_INDEX_KEY + "=" + serializeObject(p));

            Set<String> setObjectClass = new HashSet<String>(4);
            map.put(SMSEntry.ATTR_OBJECTCLASS, setObjectClass);
            setObjectClass.add(SMSEntry.OC_TOP);
            setObjectClass.add(SMSEntry.OC_SERVICE_COMP);

            Set<String> info = new HashSet<String>(8);

            String createdBy = p.getCreatedBy();
            if (createdBy != null) {
                info.add(Privilege.CREATED_BY_ATTRIBUTE + "=" + createdBy);
            }

            String lastModifiedBy = p.getLastModifiedBy();
            if (lastModifiedBy != null) {
                info.add(Privilege.LAST_MODIFIED_BY_ATTRIBUTE + "=" +
                    lastModifiedBy);
            }

            long creationDate = p.getCreationDate();
            if (creationDate > 0) {
                String data = Long.toString(creationDate) + "=" +
                    Privilege.CREATION_DATE_ATTRIBUTE;
                info.add(data);
                info.add("|" + data);
            }

            long lastModifiedDate = p.getLastModifiedDate();
            if (lastModifiedDate > 0) {
                String data = Long.toString(lastModifiedDate) + "=" +
                    Privilege.LAST_MODIFIED_DATE_ATTRIBUTE;
                info.add(data);
                info.add("|" + data);
            }
            map.put("ou", info);

            s.setAttributes(map);
            s.save();
            updateIndexCount(adminToken, 1);
        } catch (SSOException e) {
            //TOFIX
        } catch (SMSException e) {
            //TOFIX
        }
        return dn;
    }

    public void delete(String name)
        throws EntitlementException {
        String dn = getDistinguishedName(name);

        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        if (SMSEntry.checkIfEntryExists(dn, adminToken)) {
            try {
                SMSEntry s = new SMSEntry(adminToken, dn);
                s.delete();
                updateIndexCount(adminToken, -1);
            } catch (SMSException e) {
                Object[] arg = {dn};
                throw new EntitlementException(51, arg, e);
            } catch (SSOException e) {
                throw new EntitlementException(10, null, e);
            }
        }
    }

    public Set<String> search(
        String filter,
        int numOfEntries,
        boolean sortResults,
        boolean ascendingOrder
    ) {
        Set<String> results = new HashSet<String>();

        try {
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            Set<String> dns = SMSEntry.search(adminToken, BASE_DN, filter);
            for (String dn : dns) {
                String rdns[] = LDAPDN.explodeDN(dn, true);
                if ((rdns != null) && rdns.length > 0) {
                    results.add(rdns[0]);
                }
            }
        } catch (SMSException ex) {
            //TOFIX
        }
        return results;
    }

    public Set<Privilege> search(
        BufferedIterator iterator,
        ResourceSearchIndexes indexes,
        Set<String> subjectIndexes,
        boolean bSubTree,
        Set<String> excludeDNs
    ) throws EntitlementException {
        Set<Privilege> results = new HashSet<Privilege>();
        String filter = getFilter(indexes, subjectIndexes, bSubTree);

        if (filter != null) {
            long start = DB_MONITOR.start();
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            try {
                Iterator i = SMSEntry.search(
                    adminToken, BASE_DN, filter, excludeDNs);
                while (i.hasNext()) {
                    SMSDataEntry e = (SMSDataEntry)i.next();
                    Privilege privilege = (Privilege)deserializeObject(
                        e.getAttributeValue(SERIALIZABLE_INDEX_KEY));
                    iterator.add(privilege);
                    results.add(privilege);
                }
                iterator.isDone();
            } catch (SMSException e) {
                Object[] arg = {BASE_DN};
                throw new EntitlementException(52, arg, e);
            }
            DB_MONITOR.end(start);
        }
        return results;
    }

    private String getFilter(
        ResourceSearchIndexes indexes,
        Set<String> subjectIndexes,
        boolean bSubTree
    ) {
        StringBuffer filter = new StringBuffer();

        if ((subjectIndexes != null) && !subjectIndexes.isEmpty()) {
            filter.append("(|");
            for (String i : subjectIndexes) {
                Object[] o = {i};
                filter.append(MessageFormat.format(SUBJECT_FILTER_TEMPLATE, o));
            }
            filter.append(")");
        }

        Set<String> hostIndexes = indexes.getHostIndexes();
        if ((hostIndexes != null) && !hostIndexes.isEmpty()) {
            filter.append("(|");
            for (String h : indexes.getHostIndexes()) {
                Object[] o = {h};
                filter.append(MessageFormat.format(HOST_FILTER_TEMPLATE, o));
            }
            filter.append(")");
        }

        if (bSubTree) {
            Set<String> parentPathIndexes = indexes.getParentPathIndexes();
            filter.append("(|");
            if ((parentPathIndexes != null) && !parentPathIndexes.isEmpty()) {
                for (String p : parentPathIndexes) {
                    Object[] o = {p};
                    filter.append(MessageFormat.format(
                        PATH_PARENT_FILTER_TEMPLATE, o));
                }
            }
            filter.append(")");
        } else {
            Set<String> pathIndexes = indexes.getPathIndexes();
            if ((pathIndexes != null) && !pathIndexes.isEmpty()) {
                filter.append("(|");
                for (String p : pathIndexes) {
                    Object[] o = {p};
                    filter.append(MessageFormat.format(
                        PATH_FILTER_TEMPLATE, o));
                }
                filter.append(")");
            }
        }

        String result = filter.toString();
        return (result.length() > 0) ? "(&" + result + ")" : null;
    }

    private String serializeObject(Serializable object)
        throws EntitlementException {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(out);
            oos.writeObject(object);
            oos.close();
            return Base64.encode(out.toByteArray());
        } catch (IOException e) {
            throw new EntitlementException(200, null, e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private Object deserializeObject(String strSerialized)
        throws EntitlementException {
        ObjectInputStream ois = null;
        try {
            InputStream in = new ByteArrayInputStream(
                Base64.decode(strSerialized));
            ois = new ObjectInputStream(in);
            return ois.readObject();
        } catch (ClassNotFoundException ex) {
            throw new EntitlementException(201, null, ex);
        } catch (IOException ex) {
            throw new EntitlementException(201, null, ex);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                // ignore
            }
        }
    }
}

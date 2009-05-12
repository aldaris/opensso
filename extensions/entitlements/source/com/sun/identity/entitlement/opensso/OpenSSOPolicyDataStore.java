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
 * $Id: OpenSSOPolicyDataStore.java,v 1.2 2009-05-12 19:58:40 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.PolicyDataStore;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeIndexStore;
import com.sun.identity.policy.Policy;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.PolicyManager;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.sm.DNMapper;
import com.sun.identity.sm.SMSEntry;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import java.io.ByteArrayInputStream;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 */
public class OpenSSOPolicyDataStore extends PolicyDataStore {
    private static final String REALM_DN_TEMPLATE =
         "ou=" + PolicyManager.NAMED_POLICY +
         ",ou=default,ou=OrganizationConfig,ou=1.0,ou=" +
         PolicyManager.POLICY_SERVICE_NAME + ",ou=services,{0}";

    public void addPolicy(String realm, Object policy)
        throws EntitlementException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());

        //TOFIX
        if (policy instanceof Policy) {
            Policy policyObj = (Policy) policy;
            String name = policyObj.getName();
            String dn = getPolicyDistinguishedName(realm, name);

            try {
                createParentNode(adminToken, realm);

                SMSEntry s = new SMSEntry(adminToken, dn);
                Map<String, Set<String>> map = new HashMap<String, Set<String>>();

                Set<String> setObjectClass = new HashSet<String>(4);
                map.put(SMSEntry.ATTR_OBJECTCLASS, setObjectClass);
                setObjectClass.add(SMSEntry.OC_TOP);
                setObjectClass.add(SMSEntry.OC_SERVICE_COMP);

                Set<String> setValue = new HashSet<String>(2);
                map.put(SMSEntry.ATTR_KEYVAL, setValue);
                setValue.add(policyObj.toXML());
                s.setAttributes(map);
                s.save();

                PrivilegeIndexStore pis = PrivilegeIndexStore.getInstance(
                    realm);
                Set<Privilege> privileges =
                    PrivilegeUtils.policyToPrivileges(policyObj);
                for (Privilege p : privileges) {
                    p.canonicalizeResources();
                }
                pis.add(privileges);
            } catch (SSOException e) {
                Object[] params = {name};
                throw new EntitlementException(202, params, e);
            } catch (SMSException e) {
                Object[] params = {name};
                throw new EntitlementException(202, params, e);
            } catch (PolicyException e) {
                Object[] params = {name};
                throw new EntitlementException(202, params, e);
            }
        }
    }



    private void createParentNode(SSOToken adminToken, String realm)
        throws SSOException, SMSException {

        ServiceConfig orgConf = getOrgConfig(adminToken, realm);
        Set<String> subConfigNames = orgConf.getSubConfigNames();
        if (!subConfigNames.contains(PolicyManager.NAMED_POLICY)) {
            orgConf.addSubConfig(PolicyManager.NAMED_POLICY,
                PolicyManager.NAMED_POLICY, 0, null);
        }
    }

    private ServiceConfig getOrgConfig(SSOToken adminToken, String realm)
        throws SMSException, SSOException {
        ServiceConfigManager mgr = new ServiceConfigManager(
            PolicyManager.POLICY_SERVICE_NAME, adminToken);
        ServiceConfig orgConf = mgr.getOrganizationConfig(realm, null);
        if (orgConf == null) {
            mgr.createOrganizationConfig(realm, null);
        }
        return orgConf;
    }

    public Object getPolicy(String realm, String name)
        throws EntitlementException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        String dn = getPolicyDistinguishedName(realm, name);
        if (!SMSEntry.checkIfEntryExists(dn, adminToken)) {
            Object[] params = {name};
            throw new EntitlementException(203, params);
        }
        try {
            SMSEntry s = new SMSEntry(adminToken, dn);
            Map<String, Set<String>> map = s.getAttributes();
            Set<String> xml = map.get(SMSEntry.ATTR_KEYVAL);
            String strXML = xml.iterator().next();
            return createPolicy(adminToken, realm, strXML);
        } catch (SSOException ex) {
            Object[] params = {name};
            throw new EntitlementException(204, params, ex);
        } catch (SMSException ex) {
            Object[] params = {name};
            throw new EntitlementException(204, params, ex);
        } catch (Exception ex) {
            Object[] params = {name};
            throw new EntitlementException(204, params, ex);
        }
    }

    private Policy createPolicy(SSOToken adminToken, String realm, String xml)
        throws Exception, 
        SSOException, PolicyException {
        PolicyManager pm = new PolicyManager(adminToken, realm);
        Document doc = XMLUtils.getXMLDocument(
            new ByteArrayInputStream(xml.getBytes("UTF8")));
        Node rootNode = XMLUtils.getRootNode(doc, 
            PolicyManager.POLICY_ROOT_NODE);
        return new Policy(pm, rootNode);
    }

    public void removePolicy(String realm, String name)
        throws EntitlementException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        String dn = getPolicyDistinguishedName(realm, name);
        if (!SMSEntry.checkIfEntryExists(dn, adminToken)) {
            Object[] params = {name};
            throw new EntitlementException(203, params);
        }
        try {
            SMSEntry s = new SMSEntry(adminToken, dn);
            s.delete();
            PrivilegeIndexStore pis = PrivilegeIndexStore.getInstance(
                realm);
            pis.delete(name);
        } catch (SSOException ex) {
            Object[] params = {name};
            throw new EntitlementException(205, params, ex);
        } catch (SMSException ex) {
            Object[] params = {name};
            throw new EntitlementException(205, params, ex);
        }
    }

    private static String getPolicyDistinguishedName(
        String realm,
        String name) {
        return "ou=" + name + "," + getStoreBaseDN(realm);
    }

    private static String getStoreBaseDN(String realm) {
        Object[] args = {DNMapper.orgNameToDN(realm)};
        return MessageFormat.format(REALM_DN_TEMPLATE, args);
    }

    public void modifyPolicy(String realm, Object policy)
        throws EntitlementException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());

        //TOFIX
        if (policy instanceof Policy) {
            Policy policyObj = (Policy) policy;
            String name = policyObj.getName();
            String dn = getPolicyDistinguishedName(realm, name);

            try {
                SMSEntry s = new SMSEntry(adminToken, dn);
                Map<String, Set<String>> map = new
                    HashMap<String, Set<String>>();

                Set<String> setObjectClass = new HashSet<String>(4);
                map.put(SMSEntry.ATTR_OBJECTCLASS, setObjectClass);
                setObjectClass.add(SMSEntry.OC_TOP);
                setObjectClass.add(SMSEntry.OC_SERVICE_COMP);

                Set<String> setValue = new HashSet<String>(2);
                map.put(SMSEntry.ATTR_KEYVAL, setValue);
                setValue.add(policyObj.toXML());
                s.setAttributes(map);
                s.save();

                PrivilegeIndexStore pis = PrivilegeIndexStore.getInstance(
                    realm);
                pis.delete(name);
                Set<Privilege> privileges =
                    PrivilegeUtils.policyToPrivileges(policyObj);
                for (Privilege p : privileges) {
                    p.canonicalizeResources();
                }
                pis.add(privileges);
            } catch (SSOException e) {
                Object[] params = {name};
                throw new EntitlementException(206, params, e);
            } catch (SMSException e) {
                Object[] params = {name};
                throw new EntitlementException(206, params, e);
            } catch (PolicyException e) {
                Object[] params = {name};
                throw new EntitlementException(206, params, e);
            }
        }
    }
}

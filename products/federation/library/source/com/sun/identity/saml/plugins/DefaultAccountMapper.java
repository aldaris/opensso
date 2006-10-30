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
 * $Id: DefaultAccountMapper.java,v 1.1 2006-10-30 23:15:45 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml.plugins;

import com.sun.identity.saml.assertion.NameIdentifier;
import com.sun.identity.saml.assertion.Subject;
import com.sun.identity.saml.assertion.SubjectConfirmation;

import com.sun.identity.saml.common.SAMLConstants;
import com.sun.identity.saml.common.SAMLUtils;
import com.sun.identity.common.SystemConfigurationUtil;
import java.util.Map;
import java.util.HashMap;

import netscape.ldap.util.DN; 

/**
 * The class <code>DefaultAccountMapper</code> provide a default
 * implementation of the <code>AccountMapper</code> interface. 
 * <p>
 * The implementation assume two sites have exactly the same DIT structure.
 */

public class DefaultAccountMapper implements AccountMapper {

    /**
     * Default Constructor.
     */
    public DefaultAccountMapper() {}

    /**
     * Returns a Map of users matching the <code>Subject</code>
     * in the assertion.
     *
     * @param subject the <code>Subject</code> in the assertion.
     * @param sourceID the Source Identifier.
     * @see com.sun.identity.saml.plugins.AccountMapper#getUser
     */
    public Map getUser(com.sun.identity.saml.assertion.Subject subject,
                       String sourceID) {
        Map map = new HashMap();
        // No need to check SSO in SubjectConfirmation here
        // since AssertionManager will handle it without calling account mapper
        NameIdentifier nameIdentifier = subject.getNameIdentifier();
        if (nameIdentifier != null) {
            String name = nameIdentifier.getName();
            String org = nameIdentifier.getNameQualifier();
            String rootSuffix = SystemConfigurationUtil.getProperty(
                SAMLConstants.DEFAULT_ORG);   
            if (name != null && (name.length() != 0)) {
                String temp = name; 
                if (org != null && (org.length() != 0)) {
                    DN dn1 = new DN(name); 
                    DN dn2 = new DN(org); 
                    if (dn1.isDescendantOf(dn2)) {
                        int  num = dn1.countRDNs() - dn2.countRDNs();
                        String[] rdns = dn1.explodeDN(false); 
                        StringBuffer sb = new StringBuffer(50);
                        for (int i = 0; i < num; i++) { 
                            sb.append(rdns[i]).append(",");
                        }
                        sb.append(rootSuffix);
                        map.put(NAME, sb.toString()); 
                    }
                } else {
                    SAMLUtils.debug.error("DefaultAccountMapper: Org is null.");           
                } 
            } else {
                SAMLUtils.debug.error("DefaultAccountMapper: Name is null");
            }
            map.put(ORG, rootSuffix); 
            return map;
        }

        // user could not be mapped by the default account mapper      
        return map;
    } 
}

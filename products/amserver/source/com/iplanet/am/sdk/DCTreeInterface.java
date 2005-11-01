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
 * $Id: DCTreeInterface.java,v 1.1 2005-11-01 00:29:24 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import com.iplanet.sso.SSOToken;

/**
 * This interface defines the methods to be implemented for DC Tree related
 * operations. Two implementations are avaialble, <code> ldap
 * </code> and
 * <code> remote </code>
 */
public interface DCTreeInterface {
    /**
     * Return the Organization Distinguished Name for a given domain. For
     * example, if the domain is <code> sun.com </code>, then the value of the
     * attribute <code> inetBaseDomainDN </code> in the DC tree node
     * representing <code> sun.com </code> is returned.
     * 
     * @param token
     *            User's single sign on token
     * @param domainName
     *            Name of the domain
     * @return <code> DN </code> of the entry.
     * @throws AMException
     *             if unable to access the datastore.
     */
    public String getOrganizationDN(SSOToken token, String domainName)
            throws AMException;
}

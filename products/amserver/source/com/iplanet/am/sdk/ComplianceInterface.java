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
 * $Id: ComplianceInterface.java,v 1.1 2005-11-01 00:29:24 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;

/**
 * Defines the interface for checking if compliance mode is enabled and other
 * related operations. There are two supported implementations:
 * <code> ldap </code> and <code> remote </code>
 */
public interface ComplianceInterface {
    /**
     * Returns true if any of the organizations upto the base DN are marked
     * deleted.
     * 
     * @param token
     *            User's single sign on token
     * @param dn
     *            <code>DN </code> of entry
     * @param profileType
     *            Integer representing the object type
     * @return True or false
     * @throws AMException
     *             if there is an error accessing datastore
     */
    public boolean isAncestorOrgDeleted(SSOToken token, String dn,
            int profileType) throws AMException;

    /**
     * Verified if the object in question is marked for deletion, in which case
     * it is deleted. Otherwise, it just marks the object for deletion.
     * 
     * @param token
     *            User's single sign on token
     * @param profileDN
     *            <code> DN </code> of entry
     * @throws AMException
     *             if there is an error accessing datastore
     */
    public void verifyAndDeleteObject(SSOToken token, String profileDN)
            throws AMException;

    /**
     * Returns the search filter to be used for searching deleted objects. For
     * example, for searching deleted users, the search filter is
     * <code> (&(objectclass=inetOrgPerson)(inetUserStatus=deleted)) </code>.
     * The search filters are configured in the Admin Console service
     * 
     * @param objectType
     *            Integer representing the object type
     * @return Search filter
     * @throws AMException
     *             if there is an error accessing the datastore
     * @throws SSOException
     *             if the user's single sign on token is invalid
     */
    public String getDeletedObjectFilter(int objectType) throws AMException,
            SSOException;
}

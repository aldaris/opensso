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
 * $Id: IRole.java,v 1.1 2005-11-01 00:30:37 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.ums;

/**
 * iPlanet-PUBLIC-CLASS This is a common iterface from which different roles can
 * be implemented.
 */
public interface IRole {
    /**
     * Evaluates whether an object is member of this IRole.
     * 
     * @param po
     *            persistent object that is being checked for membership
     * @param useDSComputedAttr
     *            if set to <code>true</code> the attribute computed by
     *            directory server, (if the directory server does compute the
     *            attribute), would be made use of to evalaulte membership.
     *            Otherwise, logic implemented by the role would be used to
     *            compute the membership
     * @return <code>true</code> if the object is member of the role
     *         implementing this interface, <code>false</code> otherwise.
     * @throws UMSException
     *             if an exception occurs while determining if this role has the
     *             member.
     */
    public boolean hasMember(PersistentObject po) throws UMSException;

    /**
     * Gets the GUID of this object
     * 
     * @return the GUID of this object
     * 
     */
    public Guid getGuid();
}

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
 * $Id: AMModel.java,v 1.1 2006-11-16 04:31:08 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base.model;

/**
 * Base model class for Web Console.
 */
public interface AMModel {
    /**
     * Returns universal ID of user.
     *
     * @return Universal ID of user.
     */
    String getUniversalID();

    /**
     * Returns the starting realm for the administrator. It is the realm where
     * he has logged in to.
     *
     * @return starting realm.
     */
    String getStartRealm();
}

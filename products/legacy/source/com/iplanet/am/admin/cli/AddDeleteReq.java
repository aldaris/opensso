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
 * $Id: AddDeleteReq.java,v 1.1 2007-06-06 05:55:42 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.admin.cli;

import java.util.HashSet;
import java.util.Set;

abstract class AddDeleteReq extends AdminReq {
    protected Set DNSet = new HashSet(); 

    /**
     * Constructs a new empty AddDeleteReq.
     */
    AddDeleteReq() {
        super();
    }

    /**
     * Constructs a new AddDeleteReq.
     *
     * @param targetDN.
     */        
    AddDeleteReq(String targetDN) {
        super(targetDN);
    }

    /**
     * adds the dn to Set DNSet which holds all the DN's whose information
     * should be added or deleted.
     *
     * @param DN.
     */
    void addDNSet(String DN) {
         DNSet.add(DN);
    }
}

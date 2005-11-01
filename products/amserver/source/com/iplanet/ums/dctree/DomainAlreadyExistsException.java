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
 * $Id: DomainAlreadyExistsException.java,v 1.1 2005-11-01 00:30:45 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.ums.dctree;

import com.iplanet.ums.UMSException;

/**
 * Exception occurs when adding a domain that already exists in the dctree
 */
public class DomainAlreadyExistsException extends UMSException {

    /**
     * iPlanet-PUBLIC-CONSTRUCTOR Default constructor iPlanet-PUBLIC-CONSTRUCTOR
     */
    public DomainAlreadyExistsException() {
        super();
    }

    /**
     * iPlanet-PUBLIC-CONSTRUCTOR Constructor with a message string
     * 
     * @param msg
     *            Message string for the exception iPlanet-PUBLIC-CONSTRUCTOR
     */
    public DomainAlreadyExistsException(String msg) {
        super(msg);
    }

    /**
     * iPlanet-PUBLIC-CONSTRUCTOR Constructor with message string and an
     * embedded exception
     * 
     * @param msg
     *            Message string
     * @param t
     *            The embedded exception iPlanet-PUBLIC-CONSTRUCTOR
     */
    public DomainAlreadyExistsException(String msg, Throwable t) {
        super(msg, t);
    }

}

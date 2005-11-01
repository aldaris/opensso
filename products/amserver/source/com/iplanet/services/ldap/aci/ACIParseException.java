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
 * $Id: ACIParseException.java,v 1.1 2005-11-01 00:30:19 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.services.ldap.aci;

/**
 * Exception thrown to indicate problems while parsing value of aci attribute
 * read from directory to ACI object
 */
public class ACIParseException extends ACIException {

    /**
     * No arguments constructor. iPlanet-PUBLIC-CONSTRUCTOR
     */
    public ACIParseException() {
    }

    /**
     * @param message
     *            Error message. iPlanet-PUBLIC-CONSTRUCTOR
     */
    public ACIParseException(String message) {
        super(message);
    }
}

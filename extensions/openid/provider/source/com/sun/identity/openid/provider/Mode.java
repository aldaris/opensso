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
 * $Id: Mode.java,v 1.1 2007-04-30 01:28:31 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A type-safe enumeration of message modes.
 *
 * @author pbryan
 */
public class Mode implements Serializable
{
    /** TODO: Description. */
    public static final Mode ASSOCIATE = new Mode("associate");

    /** TODO: Description. */
    public static final Mode CANCEL = new Mode("cancel");

    /** TODO: Description. */
    public static final Mode CHECKID_IMMEDIATE = new Mode("checkid_immediate");

    /** TODO: Description. */
    public static final Mode CHECKID_SETUP = new Mode("checkid_setup");

    /** TODO: Description. */
    public static final Mode CHECK_AUTHENTICATION = new Mode("check_authentication");

    /** TODO: Description. */
    public static final Mode ID_RES = new Mode("id_res");

    /** TODO: Description. */
    private static final Mode[] VALUES_ARRAY = { ASSOCIATE, CANCEL,
     CHECKID_IMMEDIATE, CHECKID_SETUP, CHECK_AUTHENTICATION, ID_RES };

    /** TODO: Description. */
    private static final List VALUES =
     Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /** The encoded value represented by this enumeration value. */
    private final String value;

    /**
     * TODO: Description.
     *
     * @param encoded TODO.
     */
    private Mode(String value) {
        this.value = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     * @return TODO.
     * @throws DecodeException TODO.
     */
    public static Mode decode(String value) throws DecodeException
    {
        if (value == null) {
            return null;
        }

        for (Mode item : VALUES_ARRAY) {
            if (value.equals(item.value)) {
                return item;
            }
        }

        throw new DecodeException("invalid mode");
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public String encode() {
        return value; 
    }
}

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
 * $Id: SessionType.java,v 1.2 2007-04-30 05:36:14 pbryan Exp $
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
 * A type-safe enumeration of session types.
 *
 * @author pbryan
 */
public class SessionType implements Serializable
{
    /** Cleartext association session: MAC sent in plain-text. */
    public static final SessionType CLEAR = new SessionType("");

    /** DH-SHA1 association session. */
    public static final SessionType DH_SHA1 = new SessionType("DH-SHA1");

    /** Valid values for enumeration. */
    private static final SessionType[] VALUES_ARRAY = { CLEAR, DH_SHA1 };

    /** The encoded value represented by this enumeration value. */
    private final String value;

    /**
     * Constructs a new session type with the associated value.
     *
     * @param value the value used to encode and decode enumeration value.
     */
    private SessionType(String value) {
        this.value = value;
    }

    /**
     * Returns the enumeration value constant with the specified string value.
     *
     * @param value the string value to decode into an enumeration value.
     * @return the associated enumeration value constant.
     * @throws DecodeException if the enumeration type has no such constant.
     */
    public static SessionType decode(String value) throws DecodeException
    {
        if (value == null) {
            return null;
        }

        for (SessionType item : VALUES_ARRAY)
        {
            if (value.equals(item.value)) {
                return item;
            }
        }

        throw new DecodeException("invalid session type");
    }

    /**
     * Returns the value of the enumeration constant, exactly as declared in
     * its constructor.
     *
     * @return string value of enumeration constant.
     */
    public String encode() {
        return value; 
    }
}

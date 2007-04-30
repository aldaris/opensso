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
 * $Id: AuthenticationQuery.java,v 1.1 2007-04-30 01:28:28 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class AuthenticationQuery extends Message
{
    /** Indicates whether supplied signature is valid. */
    private Boolean valid = Boolean.FALSE;

    /** Association handle from checkid_setup or checkid_immediate. */
    private AssocHandle assocHandle = null;

    /** Association handle returned by checkid_*. */
    private AssocHandle invalidateHandle = null;

    /**
     * TODO: Description.
     */
    public AuthenticationQuery() {
        super();
    }

    /**
     * TODO: Description.
     *
     * @param map TODO.
     * @throws DecodeException TODO.
     */
    protected void decode(Map<String,String> map) throws DecodeException
    {
        super.decode(map);

        if (!getMode().equals(Mode.CHECK_AUTHENTICATION)) {
            throw new DecodeException("mode must be check_authentication");
        }

        assocHandle = AssocHandle.decode(map.get("assoc_handle"));

        if (assocHandle == null) {
            throw new DecodeException("assoc_handle is required");
        }

        invalidateHandle = AssocHandle.decode(map.get("invalidate_handle"));

        // only use secret key if supplied association handle is still valid
        SecretKey secret = null;

        // extract secret from handle if it's valid and of the correct type
        if (assocHandle.isValid() &&
        assocHandle.getType() == AssocHandle.Type.STATELESS) {
            secret = assocHandle.getSecret();
        }

        // if valid secret key was found, verify signature
        if (secret != null)
        {
            // copy the map so we can change its mode to verify signature
            HashMap<String,String> m = new HashMap<String,String>();
            m.putAll(map);

            // change the mode to id_res to verify signature
            m.put("mode", "id_res");

            // verify signature in map
            valid = Maps.verify(m, Constants.HMAC_ALGORITHM, secret);
        }
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public AssocHandle getAssocHandle() {
        return assocHandle;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public AssocHandle getInvalidateHandle() {
        return invalidateHandle;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public Boolean isValid() {
        return valid;
    }
}

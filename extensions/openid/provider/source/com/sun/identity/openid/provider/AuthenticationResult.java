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
 * $Id: AuthenticationResult.java,v 1.1 2007-04-30 01:28:28 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.util.Map;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class AuthenticationResult extends CheckidResult
{
    /** Indicates whether signature is valid. */
    private Boolean valid = null;

    /** If present, consumer should uncache returned association handle. */
    private AssocHandle invalidateHandle = null;

    /**
     * TODO: Description.
     */
    public AuthenticationResult()
    {
        super();

        // check_authentication response mode is always id_res
        setMode(Mode.ID_RES);
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

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setInvalidateHandle(AssocHandle value) {
        invalidateHandle = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setValid(Boolean value) {
        valid = value;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public Map<String,String> encode()
    {
        Map<String,String> map = super.encode();

        if (valid != null) {
            map.put("is_valid", Codec.encodeBoolean(valid));
        }

        if (invalidateHandle != null) {
            map.put("invalidate_handle", invalidateHandle.encode());
        }

        return map;
    }
}

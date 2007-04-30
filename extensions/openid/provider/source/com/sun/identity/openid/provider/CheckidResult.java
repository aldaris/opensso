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
 * $Id: CheckidResult.java,v 1.1 2007-04-30 01:28:29 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class CheckidResult extends Message
{
    /** TODO: Description. */
    private AssocHandle assocHandle = null;

    /** TODO: Description. */
    private URL identity = null;

    /** TODO: Description. */
    private AssocHandle invalidateHandle = null;

    /** TODO: Description. */
    private URL returnTo = null;

    /** TODO: Description. */
    private HashMap<String,String> sreg = new HashMap<String,String>();

    /** TODO: Description. */
    private URL trustRoot = null;

    /**
     * TODO: Description.
     */
    protected CheckidResult() {
        super();
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
    public URL getIdentity() {
        return identity;
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
    public URL getReturnTo() {
        return returnTo;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public HashMap<String,String> getSreg() {
        return sreg;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public URL getTrustRoot() {
        return trustRoot;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setAssocHandle(AssocHandle value) {
        assocHandle = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setIdentity(URL value) {
        identity = value;
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
    public void setReturnTo(URL value) {
        returnTo = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setTrustRoot(URL value) {
        trustRoot = value;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public Map<String,String> encode()
    {
        Map<String,String> map = super.encode();

        if (identity != null) {
            map.put("identity", Codec.encodeURL(identity));
        }

        if (assocHandle != null) {
            map.put("assoc_handle", assocHandle.encode());
        }

        if (returnTo != null) {
            map.put("return_to", Codec.encodeURL(returnTo));
        }

        if (invalidateHandle != null) {
            map.put("invalidate_handle", invalidateHandle.encode());
        }

        for (String name : sreg.keySet())
        {
            String value = sreg.get(name);

            if (value != null) {
                map.put("sreg." + name, sreg.get(name));
            }
        }
        
        return map;
    }
}

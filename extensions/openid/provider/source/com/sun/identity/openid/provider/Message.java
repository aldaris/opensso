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
 * $Id: Message.java,v 1.1 2007-04-30 01:28:31 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class Message implements Serializable
{
    /** TODO: Description. */
    private Mode mode = null;

    /**
     * TODO: Description.
     */
    protected Message() {
    }

    /**
     * TODO: Description.
     *
     * @param map TODO.
     * @throws DecodeException TODO.
     */
    protected void decode(Map<String,String> map)
    throws DecodeException
    {
        mode = Mode.decode(map.get("mode"));

        if (mode == null) {
            throw new DecodeException("mode is required");
        }
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    protected Map<String,String> encode()
    {
        HashMap<String,String> map = new HashMap<String,String>();

        if (mode != null) {
            map.put("mode", mode.encode());
        }

        return map;
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @throws BadRequestException TODO.
     */
    public void populate(HttpServletRequest request)
    throws BadRequestException
    {
        Map<String,String> map = Maps.fromRequest(request);

        try {
            decode(map);
        }

        catch (DecodeException de) {
            throw new BadRequestException(de.getMessage());
        }
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setMode(Mode value) {
        mode = value;
    }
}

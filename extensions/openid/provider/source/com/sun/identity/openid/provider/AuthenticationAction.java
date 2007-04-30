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
 * $Id: AuthenticationAction.java,v 1.1 2007-04-30 01:28:28 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class AuthenticationAction extends Action
{
    /** TODO: Description. */
    AuthenticationQuery query = new AuthenticationQuery();

    /** TODO: Description. */
    AuthenticationResult result = new AuthenticationResult();

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @param response TODO.
     */
    public AuthenticationAction(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    /**
     * TODO: Description.
     *
     * @throws BadRequestException TODO.
     * @throws IOException TODO.
     */
    public void perform() throws BadRequestException, IOException
    {
        // populate check_authentication query from HTTP request
        query.populate(request);

        // validity was established when query was decoded from request
        result.setValid(query.isValid());

        AssocHandle invalidateHandle = query.getInvalidateHandle();

        // determine if invalidation candidate is indeed invalid association
        if (invalidateHandle != null && (!invalidateHandle.isValid() ||
        invalidateHandle.getType() != AssocHandle.Type.ASSOCIATED)) {
            result.setInvalidateHandle(invalidateHandle);
        }

        // compose result as a string set of key-value pairs
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(Maps.toResponseString(result.encode()));
    }
}

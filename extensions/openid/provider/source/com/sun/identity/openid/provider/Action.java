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
 * $Id: Action.java,v 1.2 2007-04-30 05:36:12 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An implementation that is subclassed to implement functionality that is
 * dispatched from a servlet.
 *
 * @author pbryan
 */
public abstract class Action
{
    /** The request dispatched from the servlet. */
    protected final HttpServletRequest request;

    /** The response to provide to the dispatching servlet. */
    protected final HttpServletResponse response;

    /**
     * Creates a new instance of the action.
     *
     * @param request the request dispatched from the servlet.
     * @param response the response to provide to the dispatching servlet.
     */
    public Action(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Performs the dispatched action.
     *
     * @throws BadRequestException if the request was malformed.
     * @throws IOException if an input/output error occurs.
     */
    public abstract void perform() throws BadRequestException, IOException;
}

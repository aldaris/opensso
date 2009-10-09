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
 * $Id: Filter.java,v 1.2 2009-10-09 07:38:37 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.handler;

import com.sun.identity.proxy.http.Exchange;
import java.io.IOException;

/**
 * The base class for all message exchange filters. A filter is a specialized
 * type of handler, which knows what next handler to pass the message exchange
 * to after it has successfully performed its function. Filters are typically
 * added into a {@link Chain}.
 * <p>
 * A particular filter instance can have one and only one next handler. This
 * means that it cannot be added to more than one filter chain.
 *
 * @author Paul C. Bryan
 */

public abstract class Filter implements Handler
{
    /** The next hander to pass the exchange to once this filter has successfully processed it. */
    public Handler next;

    /**
     * Called to request the filter handle the request. Once handled
     * successfully, the filter should call the next handler's
     * <tt>handle(<em>exchange</em>)</tt> method. The filter is allowed to
     * <em>not</em> pass the exchange on to the next handler.
     * <p>
     * As with a handler, if an existing response object exists in the exchange
     * and the filter intends to replace it with another response object, it must
     * first check to see if the existing response object has an entity, and if
     * it does, must call its <tt>close()</tt> method in order to signal that the
     * processing of the response from a remote server is complete.
     *
     * @param exchange the message exchange to handle.
     * @throws HandlerException if an exception occurs that prevents handling the exchange.
     * @throws IOException if an I/O exception occurs.
     */
    @Override
    public abstract void handle(Exchange exchange) throws HandlerException, IOException;
}


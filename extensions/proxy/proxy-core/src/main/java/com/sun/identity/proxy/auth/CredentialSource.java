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
 * $Id: CredentialSource.java,v 1.1 2009-10-06 01:05:16 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.auth;

import com.sun.identity.proxy.handler.HandlerException;
import com.sun.identity.proxy.http.Exchange;
import com.sun.identity.proxy.http.Request;
import java.io.IOException;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public interface CredentialSource {

	/**
	 * Returns the credentials that are appropriate for the current request.
	 * Typically, credentials are associated with a particular principal, or
	 * with attributes supplied in the request and/or session.
	 *
	 * @param request the incoming request to establish credentials for.
	 * @return the matching credentials, or null if none could be found.
	 */
	public Credentials credentials(Request request);

    /**
     * Called when the supplied credentials are not valid. For example, this
     * method gives the credential source object the opportunity to redirect
     * the user agent to a service to manage credientials.
     *
     * @param exchange TODO.
     * @throws IOException TODO.
     * @throws HandlerException TODO.
     */
	public void invalid(Exchange exchange) throws HandlerException, IOException;
}


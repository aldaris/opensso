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
 * $Id: HardCodedPasswordSource.java,v 1.1 2009-10-06 01:05:16 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.auth;

import com.sun.identity.proxy.handler.HandlerException;
import com.sun.identity.proxy.http.Request;
import com.sun.identity.proxy.http.Exchange;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class HardCodedPasswordSource implements PasswordSource {

    /** TODO: Description. */
    private final PasswordCredentials credentials = new PasswordCredentials();

    /**
     * TODO: Description.
     *
     * @param username TODO.
     * @param password TODO.
     */
    public HardCodedPasswordSource(String username, String password) {
        credentials.username = username;
        credentials.password = password;
    }

	/**
	 * Returns the hard-coded credentials.
	 *
	 * @param request the incoming request to establish credentials for.
	 * @return the matching credentials, or null if none could be found.
	 */
	public PasswordCredentials credentials(Request request) {
	    return credentials;
	}

    /**
     * Called when the supplied credentials are not valid.
     *
     * @param exchange TODO.
     * @throws IOException TODO.
     * @throws HandlerException TODO.
     */
	public void invalid(Exchange exchange) throws HandlerException, IOException {
	    throw new HandlerException("Failed to authenticate with hard-coded credentials.");
	}
}

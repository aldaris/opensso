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
 * $Id: ResponseInfo.java,v 1.2 2007-10-04 04:34:50 hengming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.profile;

import com.sun.identity.saml2.protocol.Response;

/**
 * This class stores information about the response made to
 * the Service Provider.
 */
public class ResponseInfo extends CacheObject {
    private Response resp = null;
    private boolean isPOSTBinding = false;
    private String relayState = null;

    /**
     * Constructor creates the ResponseInfo.
     * @param response the Response
     * @param isPOSTBinding whether the Response is from POST binding.
     * @param relayState relayState retrieved from ECP RelayState.
     */
    public ResponseInfo(Response response, boolean isPOSTBinding,
        String relayState) {
        this.resp = response;
        this.isPOSTBinding = isPOSTBinding;
        this.relayState = relayState;
        time = System.currentTimeMillis();
    }

    /**
     * Returns the <code>Response</code> object.
     *
     * @return the <code>Response</code> object.
     */
    public Response getResponse() {
        return resp;
    }

    /**
     * Returns the binding.
     *
     * @return the binding.
     */
    public boolean getIsPOSTBinding() {
        return isPOSTBinding;
    }

    /**
     * Returns the relayState.
     *
     * @return the relayState.
     */
    public String getRelayState() {
        return relayState;
    }
}

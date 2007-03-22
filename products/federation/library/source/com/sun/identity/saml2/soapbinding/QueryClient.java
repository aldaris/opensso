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
 * $Id: QueryClient.java,v 1.1 2007-03-22 23:59:05 bina Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.saml2.soapbinding; 

import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.protocol.RequestAbstract;
import com.sun.identity.saml2.protocol.Response;

/**
 * The <code>QueryClient</code> class provides Query Requester clients with 
 * a method to send requests using SOAP connection to SOAP endpoint.
 *
 * NOTE: this is the initial checkin.
 * TODO: the actual implementation.
 */

public class QueryClient {

    private QueryClient() {}

    /**
     * Sends a SOAP Message to a SOAP endpoint and returns the SAMLv2 response. 
     * Prior to sending the request query, attributes required for completeness
     * of the SAMLv2 Request will be set (eg. Issuer) if not already set. 
     * Message will be signed if signing is enabled.
     * SAMLv2 Query Request will be enclosed in the SOAP Body to create a SOAP
     * message to send to the server.
     *
     * @param request the SAMLv2 <code>RequestAbstract</code> object.
     * @param hostedEntityID entity identifier of the hosted query requester.
     * @param remoteEntityID entity identifier of the remote server. 
     * @return SAMLv2 <code>Response</code> received from the 
     *         Query Responder.
     * @throws SAML2Exception if there is an error processing the query.
     */
    public static Response processXACMLQuery(RequestAbstract request,
                                              String hostedEntityID,
                                              String remoteEntityID) 
                                              throws SAML2Exception {
        return null;
    }
}

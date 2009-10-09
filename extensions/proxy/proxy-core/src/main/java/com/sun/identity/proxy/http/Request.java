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
 * $Id: Request.java,v 1.3 2009-10-09 07:38:37 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.http;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * An HTTP request message.
 *
 * @author Paul C. Bryan
 */
public class Request extends Message
{
    /** The method to be performed on the resource. */
    public String method = null;

    /** The resource identified in the request. */
    public String uri = null;

    /** The user principal that the container associated with the request. */
    public Principal principal = null;

    /** A local context object associated with the request client. */
    public Session session = null;

    /** Allows information to be attached to the request for downstream handlers. */
    public final Map<String, Object> attributes = new HashMap<String, Object>();
}


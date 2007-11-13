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
 * $Id: ClientUserToken.java,v 1.1 2007-11-13 19:46:25 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.sts;

import com.sun.xml.ws.security.Token;

/**
 *
 * This class extends XWSS Security Token and enables the STS Clients to
 * use any custom tokens that can be used in WS-Trust protocol element
 * <code>OnBehalfOf</code>. This element is used by the <code>FAM</code>
 * STS Service to allow any custom changes to the issued tokens by the STS
 */
public interface ClientUserToken extends Token {
    
    public void init(Object obj) throws FAMSTSException;

    public String toString();
}

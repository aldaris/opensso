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
 * $Id: ClientUserToken.java,v 1.2 2008-03-08 03:03:19 mallas Exp $
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
    
    /**
     * Initializes client user token
     * @param obj credential object to initialize the user token     
     * @throws com.sun.identity.wss.sts.FAMSTSException
     */
    public void init(Object obj) throws FAMSTSException;

    /**
     * Returns the principal name that the client user token carries
     * @return the principal name that the client user token carries
     * @throws com.sun.identity.wss.sts.FAMSTSException
     */     
    public String getPrincipalName() throws FAMSTSException;

    /**
     * Returns the <code>java.lang.String</code> representation of
     * this client user token.
     * @return the string format for this client user token.
     */
    public String toString();

}

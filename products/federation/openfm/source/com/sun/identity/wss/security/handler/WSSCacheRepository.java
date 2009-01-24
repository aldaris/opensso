/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: WSSCacheRepository.java,v 1.1 2009-01-24 01:31:26 mallas Exp $
 */

package com.sun.identity.wss.security.handler;

import java.util.Set;

/**
 * This inteface <code> WSSCacheRepository </code> stores the Web services
 * security related cache information. This cache is primarily used to 
 * prevent wss security related replay attacks.
 * 
 * This interface helps the ws-security deployers to store the state of
 * the cache persistently.
 */
public interface WSSCacheRepository {
       
    
    /**
     * Retrieves the stored user name token nonce cache.
     * @param timestamp timestamp is the key for the nonce cache.
     * @return the set of previous stored nonces for a given timestamp.
     */
    public Set retrieveUserTokenNonce(String timestamp);
    
    /**
     * Saves the user name token nonces for a given timestamp.
     * @param timestamp the timestamp is the index for the cache.
     * @param nonces the set of nonces that must be stored.
     */ 
    public void saveUserTokenNonce(String timestamp, Set nonces);

}

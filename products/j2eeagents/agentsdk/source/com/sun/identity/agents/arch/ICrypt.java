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
 * $Id: ICrypt.java,v 1.1 2006-09-28 23:22:01 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.arch;

/**
 * Public interface to be implemented by the AM70 and AM63 Crypt classes
 */

public interface ICrypt {
        
        
    /**
     * This function will take the clear text data as the method param
     * to encrypt the data
     *
     * @param data clear text data
     * @return String encrypted data
     */
    public String encrypt(String data);
        
    /**
     * This function will take the encrypted data as the method param
     * to decrypt the data
     *
     * @param data encrypted data
     * @return String clear text data
     */
    public String decrypt(String data);

}

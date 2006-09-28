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
 * $Id: EncryptionKeyGenerator.java,v 1.1 2006-09-28 07:37:57 rarcot Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.install.tools.util;

import java.security.SecureRandom;

public class EncryptionKeyGenerator {

    public static String generateRandomString() {

        String randomStr = null;
        try {
            
            byte[] bytes = new byte[24];
            SecureRandom random = SecureRandom.getInstance(STR_SHA_ALGO,
                    STR_PROVIDER);
            random.nextBytes(bytes);
            randomStr = Base64.encode(bytes).trim();

        } catch (Exception ex) {
            Debug.log("EncryptionKeyGenerator.generateRandomNumber() : threw "
                    + "exception : ", ex);
        }
        return randomStr;
    }

    public static String STR_SHA_ALGO = "SHA1PRNG";

    public static String STR_PROVIDER = "SUN";
}

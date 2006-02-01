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
 * $Id: AuthenticationLogger.java,v 1.1 2006-02-01 08:55:20 mrudul_uchil Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.demo.server;

import java.util.Hashtable;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.service.AuthenticationLogHelper;

public class AuthenticationLogger implements AuthenticationLogHelper {

    public AuthenticationLogger() throws Exception {
    }

    /** Dummy implementation
     */
    public void logMessage(
        String messageId,
        String data[],
        Hashtable properties,
        SSOToken ssoToken) {
    }

    /** Dummy Implementation
     */
    public void logError(
        String messageId,
        String data[],
        Hashtable properties,
        SSOToken ssoToken) {
    }
}

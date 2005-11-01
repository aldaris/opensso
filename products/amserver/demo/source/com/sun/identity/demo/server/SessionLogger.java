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
 * $Id: SessionLogger.java,v 1.1 2005-11-01 00:28:35 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.server;

import java.util.logging.Level;

import com.iplanet.dpro.session.service.InternalSession;
import com.iplanet.dpro.session.service.SessionLogHelper;
import com.iplanet.sso.SSOToken;

public class SessionLogger implements SessionLogHelper {

    public void logMessage(InternalSession sess, String id,
            SSOToken sessionServiceToken) {
        // No implementation for demo

    }

    public void logSystemMessage(String id, Level level,
            SSOToken sessionServiceToken) {
        // No implementation for demo

    }

}

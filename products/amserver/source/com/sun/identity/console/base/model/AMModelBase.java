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
 * $Id: AMModelBase.java,v 1.1 2006-11-16 04:31:09 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base.model;

import javax.servlet.http.HttpServletRequest;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.shared.Constants;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.sm.DNMapper;

/**
 * Base model implementation classes.
 */
public class AMModelBase implements AMModel {
    /**
     * Debugger instance.
     */
    public static Debug debug = Debug.getInstance(
        AMAdminConstants.CONSOLE_DEBUG_FILENAME);
    
    private SSOToken ssoToken;
    private String userUID;

    /**
     * Constructs an instance.
     *
     * @param req HTTP Servlet Request.
     */
    public AMModelBase(HttpServletRequest req) {
        initialize(req);
    }

    private void initialize(HttpServletRequest req) {
        try {
            ssoToken = AMAuthUtils.getSSOToken(req);
            userUID = getUniversalID();
        } catch (SSOException e) {
            debug.warning("AMModelBase.initialize", e);
        }
     }

    /**
     * Returns universal ID of user.
     *
     * @return Universal ID of user.
     */
    public String getUniversalID() {
        String univId = null;
        try {
            univId = ssoToken.getProperty(Constants.UNIVERSAL_IDENTIFIER);
        } catch (SSOException e) {
            debug.warning("AMModelBase.getUniversalID", e);
        }
        return univId;
    }

    /**
     * Returns the starting realm for the administrator. It is the realm where
     * he has logged in to.
     *
     * @return starting realm.
     */
    public String getStartRealm() {
        String startDN = "/";
        try {
            startDN = DNMapper.orgNameToRealmName(
                ssoToken.getProperty(Constants.ORGANIZATION));
        } catch (SSOException e) {
            debug.warning("AMModelBase.getStartRealm", e);
        }
        return startDN;
    }
}

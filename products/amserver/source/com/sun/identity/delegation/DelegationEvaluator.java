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
 * $Id: DelegationEvaluator.java,v 1.2 2006-01-19 21:56:50 huacui Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.delegation;

import java.util.Map;
import java.util.Set;

import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.delegation.interfaces.DelegationInterface;
import com.sun.identity.sm.DNMapper;

/**
 * The <code>DelegationEvaluator</code> class provides interfaces to evaluate
 * access permissions for an administrator.
 */

public class DelegationEvaluator {

    static final Debug debug = DelegationManager.debug;

    static String privilegedUserName;

    private DelegationInterface pluginInstance = null;

    /**
     * Constructor of <code>DelegationEvaluator</code> to get access control
     * permissions for users.
     * 
     * @throws DelegationException
     *             for any abnormal condition
     */

    static {
        try {
            privilegedUserName = DelegationManager.getAdminToken()
                    .getPrincipal().getName();
        } catch (Exception e) {
            debug.error("DelegationEvaluator:", e);
        }
    }

    public DelegationEvaluator() throws DelegationException {
        if (debug.messageEnabled()) {
            debug.message("Instantiated a DelegationEvaluator.");
        }
    }

    /**
     * Returns a boolean value indicating if a user has the specified
     * permission.
     * 
     * @param token
     *            sso token of the user evaluating permission
     * @param permission
     *            delegation permission to be evaluated
     * @param envParameters
     *            run-time environment parameters
     * 
     * @return the result of the evaluation as a boolean value
     * 
     * @throws SSOException
     *             single-sign-on token invalid or expired
     * @throws DelegationException
     *             for any other abnormal condition
     */

    public boolean isAllowed(SSOToken token, DelegationPermission permission,
            Map envParameters) throws SSOException, DelegationException {

        boolean result = false;

        if ((permission != null) && (token != null)) {
            String userName = token.getPrincipal().getName();
            if (userName.equalsIgnoreCase(privilegedUserName)) {
                result = true;
            } else {
                if (pluginInstance == null) {
                    pluginInstance = DelegationManager.getDelegationPlugin();
                    if (pluginInstance == null) {
                        throw new DelegationException(ResBundleUtils.rbName,
                            "no_plugin_specified", null, null);
                    }
                }
                result = pluginInstance.isAllowed(token, permission,
                        envParameters);
            }
        }
        if (debug.messageEnabled()) {
            debug.message("isAllowed() returns " + result + " for user: "
                    + token.getPrincipal().getName() + " for permission "
                    + permission);
        }
        return result;
    }

    /**
     * Returns a set of permissions that a user has.
     * 
     * @param token
     *            sso token of the user requesting permissions
     * @param orgName
     *            The name of the realm in which a user's delegation permissions
     *            are evaluated.
     * 
     * @return a set of permissions that a user has
     * 
     * @throws SSOException
     *             single-sign-on token invalid or expired
     * @throws DelegationException
     *             for any other abnormal condition
     */

    public Set getPermissions(SSOToken token, String orgName)
            throws SSOException, DelegationException {
        if (pluginInstance != null) {
            String name = DNMapper.orgNameToDN(orgName);
            return pluginInstance.getPermissions(token, name);
        } else {
            throw new DelegationException(ResBundleUtils.rbName,
                    "no_plugin_specified", null, null);
        }
    }
}

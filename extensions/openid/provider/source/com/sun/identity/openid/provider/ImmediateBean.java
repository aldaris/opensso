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
 * $Id: ImmediateBean.java,v 1.1 2007-04-30 01:28:30 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.event.PhaseEvent;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class ImmediateBean extends CheckidBean
{
    /** TODO: Description. */
    private ImmediateResult result = new ImmediateResult();

    /** TODO: Description. */
    private ImmediateQuery query = new ImmediateQuery();

    /**
     * TODO: Description.
     */
    public ImmediateBean() {
        super();
    }

    /**
     * Reponds with a redirect to the relying party with a positive assertion
     * that the user authenticated successfully.
     */
    private void grant() {
        super.grant(query, result);
    }

    /**
     * Responds with a redirect to the checkid_setup URL to allow user to
     * intervene.
     *
     * @throws BadRequestException TODO.
     */
    private void setup() throws BadRequestException
    {
        // set mode in query so redirect will result in checkid_setup
// TODO: maybe use clone instead of modifying query in place?
        query.setMode(Mode.CHECKID_SETUP);

        // generate URL that should be used to perform checkid_setup
        try {
            result.setUserSetupURL(Codec.decodeURL(
             Maps.toQueryString(getServiceURL(), query.encode())));
        }

        // somehow, user_setup_url we just built doesn't pass validation
        catch (DecodeException de) {
            throw new IllegalStateException(de);
        }

        // send result with user_setup_url query string argument
        sendRedirect(Maps.toQueryString(query.getReturnTo(), result.encode()));
    }

    /**
     * TODO: Description.
     *
     * @param event TODO.
     * @throws BadRequestException TODO.
     */
    public void beforeRenderResponse(PhaseEvent event)
    throws BadRequestException
    {
        // populate checkid_immediate query from HTTP request
        query.populate(request);

        // suppress simple registration extension if disabled in configuration
        if (!Config.getBoolean(Config.SIMPLE_REGISTRATION)) {
            disableRegistration(query);
        }

        // not logged in or OpenID identity does not match principal
        if (!identityMatches(getPrincipal(), query.getIdentity())) {
            setup();
            return;
        }

        // simple registration (currently) requires user intervention
        if (query.getRequired() != null || query.getOptional() != null) {
            setup();
            return;
        }

// TODO: persistent trust: call grant method here accordingly

        // user hasn't set persistent trust for consumer; user intervention required
        setup();
    }
}

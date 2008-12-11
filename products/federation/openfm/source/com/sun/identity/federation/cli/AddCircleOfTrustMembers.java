/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: AddCircleOfTrustMembers.java,v 1.4 2008-12-11 22:36:28 veiming Exp $
 *
 */

package com.sun.identity.federation.cli;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.cot.COTException;
import com.sun.identity.cot.CircleOfTrustManager;
import com.sun.identity.cot.COTUtils;
import com.sun.identity.shared.locale.L10NMessage;
import java.text.MessageFormat;

/**
 * Add member to a Circle of Trust.
 */
public class AddCircleOfTrustMembers extends AuthenticatedCommand {
    private static Debug debug = COTUtils.debug;
    
    private String realm;
    private String cot;
    private String spec;
    private String entityID;
    
    /**
     * Adds member to a circle of trust.
     *
     * @param rc Request Context.
     * @throws CLIException if unable to process this request.
     */
    public void handleRequest(RequestContext rc) throws CLIException {
        super.handleRequest(rc);
        ldapLogin();
        realm = getStringOptionValue(FedCLIConstants.ARGUMENT_REALM, "/");
        cot = getStringOptionValue(FedCLIConstants.ARGUMENT_COT);
        spec=FederationManager.getIDFFSubCommandSpecification(rc);
        
        entityID = getStringOptionValue(FedCLIConstants.ARGUMENT_ENTITY_ID);
        
        try {
            CircleOfTrustManager cotManager= new CircleOfTrustManager();
            cotManager.addCircleOfTrustMember(realm, cot, spec,entityID);
            
            Object[] objs = {spec, cot ,entityID, realm};
            getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString("add-circle-of-trust-member-succeeded"),
                    objs));
        } catch (COTException e) {
            debug.warning("AddCircleOfTrustMembers.handleRequest", e);
            if (e instanceof L10NMessage) {
                throw new CLIException(
                    ((L10NMessage)e).getL10NMessage(
                        getCommandManager().getLocale()),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED);

            } else {
                throw new CLIException(e.getMessage(),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
        }
    }
}

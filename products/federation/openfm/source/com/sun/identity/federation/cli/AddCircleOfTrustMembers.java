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
 * $Id: AddCircleOfTrustMembers.java,v 1.1 2006-10-30 23:17:59 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
import java.text.MessageFormat;

/**
 * Add member to a Circle of Trust.
 */
public class AddCircleOfTrustMembers extends AuthenticatedCommand {
    private static Debug debug = COTUtils.debug;
    private static final String ARGUMENT_REALM = "realm";
    private static final String ARGUMENT_COT = "cot";
    private static final String ARGUMENT_ENTITY_ID = "entityid";
    
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
        realm = getStringOptionValue(ARGUMENT_REALM, "/");
        cot = getStringOptionValue(ARGUMENT_COT);
        spec=FederationManager.getIDFFSubCommandSpecification(rc);
        
        entityID = getStringOptionValue(ARGUMENT_ENTITY_ID);
        
        try {
            CircleOfTrustManager cotManager= new CircleOfTrustManager();
            cotManager.addCircleOfTrustMember(realm, cot, spec,entityID);
            
            Object[] objs = {spec, cot ,entityID, realm};
            getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString("add-circle-of-trust-member-succeeded"),
                    objs));
        } catch (COTException e) {
            debug.warning("AddCircleOfTrustMembers.handleRequest", e);
            throw new CLIException(e.getMessage(),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
}
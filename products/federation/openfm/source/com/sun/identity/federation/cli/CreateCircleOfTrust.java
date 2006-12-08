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
 * $Id: CreateCircleOfTrust.java,v 1.2 2006-12-08 21:02:39 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.federation.cli;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.cot.CircleOfTrustDescriptor;
import com.sun.identity.cot.COTConstants;
import com.sun.identity.cot.COTUtils;
import com.sun.identity.cot.COTException;
import com.sun.identity.cot.CircleOfTrustManager;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Create Circle of Trust.
 */
public class CreateCircleOfTrust extends AuthenticatedCommand {
    private static Debug debug = COTUtils.debug;
    static final String ARGUMENT_REALM = "realm";
    static final String ARGUMENT_COT = "cot";
    static final String ARGUMENT_TRUSTED_PROVIDERS = "trustedproviders";
    static final String ARGUMENT_PREFIX = "prefix";
    
    private String realm;
    private String cot;
    private String spec;
    private List trustedProviders;
    private String prefix;
    
    /**
     * Creates a circle of trust.
     *
     * @param rc Request Context.
     * @throws CLIException if unable to process this request.
     */
    public void handleRequest(RequestContext rc) throws CLIException {
        super.handleRequest(rc);
        ldapLogin();
        realm = getStringOptionValue(ARGUMENT_REALM, "/");
        cot = getStringOptionValue(ARGUMENT_COT);
        prefix = getStringOptionValue(ARGUMENT_PREFIX);
        spec = FederationManager.getIDFFSubCommandSpecification(rc);
        
        trustedProviders = (List)rc.getOption(ARGUMENT_TRUSTED_PROVIDERS);
        
        Set providers = new HashSet();
        if (trustedProviders != null) {
            providers.addAll(trustedProviders);
        }
        String readerURL = "/saml2reader";
        String writerURL = "/saml2writer";
        
        if (spec.equalsIgnoreCase(FederationManager.IDFF_SPECIFICATION)) {
            readerURL = "/idffreader";
            writerURL = "/idffwriter";
        }
        
        try {
            CircleOfTrustDescriptor descriptor =
                    ((prefix == null) || (prefix.trim().length() == 0)) ?
                        new CircleOfTrustDescriptor(cot, spec,
                    COTConstants.ACTIVE,"",
                    null, null, providers) :
                        new CircleOfTrustDescriptor(cot, spec,
                    COTConstants.ACTIVE,"",
                    prefix + readerURL,
                    prefix + writerURL,
                    providers);
            CircleOfTrustManager cotManager= new CircleOfTrustManager();
            cotManager.createCircleOfTrust(realm,descriptor);
            
            Object[] objs = {cot, realm};
            getOutputWriter().printlnMessage(MessageFormat.format(
                    getResourceString("create-circle-of-trust-succeeded"),
                    objs));
        } catch (COTException e) {
            debug.warning("CreateCircleOfTrust.handleRequest", e);
            throw new CLIException(e.getMessage(),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
}

/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: FedletAdapter.java,v 1.1 2009-06-09 20:28:31 exu Exp $
 *
 */

package com.sun.identity.saml2.plugins;

import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.protocol.LogoutRequest;
import com.sun.identity.saml2.protocol.LogoutResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.List;

/**
 * The <code>FedletAdapter</code> abstract class provides methods
 * that could be extended to perform user specific logics during SAMLv2 
 * protocol processing on the Service Provider side. The implementation class
 * could be configured on a per service provider basis in the extended
 * metadata configuration.   
 * <p>
 * A singleton instance of this <code>FedletAdapter</code>
 * class will be used per Service Provider during runtime, so make sure 
 * implementation of the methods are thread safe. 
 * @supported.all.api
 */

public abstract class FedletAdapter {

    /**
     * Constants for hosted entity id parameter
     */
    public static final String HOSTED_ENTITY_ID = "HOSTED_ENTITY_ID";

    /**
     * Initializes the fedlet adapter, this method will only be executed
     * once after creation of the adapter instance.
     * @param initParams  initial set of parameters configured in the fedlet
     *          for this adapter. One of the parameters named
     *          <code>HOSTED_ENTITY_ID</code> refers to the ID of this
     *          fedlet entity.
     */
    public abstract void initialize(Map initParams);

    /**
     * Invokes after Fedlet receives SLO request from IDP. It does the work
     * of logout the user.
     * @param request servlet request
     * @param response servlet response
     * @param hostedEntityID entity ID for the fedlet
     * @param idpEntityID entity id for the IDP to which the request will 
     * 		be sent. This will be null in ECP case.
     * @param siList List of SessionIndex whose session to be logged out
     * @param nameIDValue nameID value whose session to be logged out
     * @exception SAML2Exception if user want to fail the process.
     */
    public boolean doFedletSLO (
        HttpServletRequest request, 
        HttpServletResponse response, 
        LogoutRequest logoutReq,
        String hostedEntityID, 
        String idpEntityID,
        List siList,
        String nameIDValue)
    throws SAML2Exception {
        return true;
    }
} 

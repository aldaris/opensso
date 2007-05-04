/**
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
 * $Id: SingleLogoutManager.java,v 1.1 2007-05-04 22:55:17 qcheng Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.multiprotocol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * The <code>SingleLogoutManager</code> class provides methods to perform
 * single logout cross multiple federation protocols. This method
 * <code>doIDPSingleLogout</code> need to be invoked by identity providers
 * after finishing processing its protocol specific single logout logics and 
 * before destroying the local session(s).
 * 
 */
public class SingleLogoutManager {
    /**
     * Status code for logout success
     */
    public static final int LOGOUT_SUCCEEDED_STATUS = 0;

    /**
     * Status code for logout failure 
     */
    public static final int LOGOUT_FAILED_STATUS = 1;

    /**
     * Status code for partial logout success
     */
    public static final int LOGOUT_PARTIAL_STATUS = 2;

    /**
     * Status code for logout request being redirected 
     */
    public static final int LOGOUT_REDIRECTED_STATUS = 3;

    /** single logout manager instance */
    private static SingleLogoutManager manager = new SingleLogoutManager();
    
    /** Creates a new instance of SingleLogoutManager */
    private SingleLogoutManager() {
    }
    
    /**
     * Returns SingleLogoutManager singleton instance.
     * @return manager instance.
     */
    public static SingleLogoutManager getInstance() {
        return manager;
    }
    
    /**
     * Performs single logout cross multiple federation protocols. This method
     * will invoke single logout processing for all the federation protocols. 
     *
     * Normally, there are three types of single logout to be supported:
     * - logout single session (specified by userSession parameter)
     * - logout a list of session (specified by userSession parameter)
     * - logout all sessions for a specific user (specified by userID parameter)
     *
     * As a single instance of the implementation class will be used internally
     * in the SingleLogoutManager class, implementation of the method shall 
     * not maintain any states.
     *
     * @param userSession Set of user session objects (java.lang.Object) to be 
     *     logout.
     * @param userID Universal identifier of the user to be logout.
     * @param request HTTP servlet request object of the request.
     * @param response HTTP servlet response object of the request.
     * @param isSOAPInitiated True means original single logout request is 
     *     initiated using SOAP binding, false means the original single logout 
     *     request is initiated using HTTP binding.
     * @param isIDPInitiated True means this is identity provider initiated
     *     single logout, false means this is service provider initiated single
     *     logout.
     * @param protocol The protocol of the original single logout. 
     *     Possible values for this parameter:
     *          SAML2  - single logout initiated using SAMLv2 protocol
     *          IDFF   - single logout initiated using ID-FF protocol
     *          WS-FED - single logout initiated using WS-Federation protocol
     * @param realm Realm of the hosted entity.
     * @param idpEntityID <code>EntityID</code> of the hosted identity provider
     *      in the original Single Logout request.
     * @param spEntityID <code>EntityID</code> of the remote service provider
     *      in the original Single Logout request.
     * @param relayState A state information to be relayed back in response.
     * @param singleLogoutRequestXML Original single logout request in XML 
     *      string.
     * @param currentStatus Current logout status, this is the single logout 
     *      status for the federation protocol just processed.
     *      Possible values:
     *         <code>LOGOUT_SUCCEEDED_STATUS</code> - single logout succeeded.
     *         <code>LOGOUT_FAILED_STATUS</code>    - single logout failed.
     *         <code>LOGOUT_PARTIAL_STATUS</code>   - single logout partially 
     *                                                succeeded.
     * @return accumulative status of single logout for all protocols 
     *      processed so far, or status indicating the logout request has been
     *      redirected for processing. Possible values:
     *         <code>LOGOUT_SUCCEEDED_STATUS</code> - single logout succeeded.
     *         <code>LOGOUT_FAILED_STATUS</code>    - single logout failed.
     *         <code>LOGOUT_PARTIAL_STATUS</code>   - single logout partially 
     *                                                succeeded.
     *         <code>LOGOUT_REDIRECTED_STATUS</code> - single logout request 
     *                                                redirected.
     * @exception Exception if error occurs when processing the protocol.
     */
    public int doIDPSingleLogout(
        Set userSession,
        String userID,
        HttpServletRequest request,
        HttpServletResponse response,
        boolean isSOAPInitiated,
        boolean isIDPInitiated,
        String protocol,
        String realm,
        String idpEntityID,
        String SPEntityID,
        String relayState,
        String singleLogoutRequestXML,
        int currentStatus
    ) throws Exception {
        return LOGOUT_SUCCEEDED_STATUS;
    }
}

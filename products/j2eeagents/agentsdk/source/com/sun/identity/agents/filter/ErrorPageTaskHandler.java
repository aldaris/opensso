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
 * $Id: ErrorPageTaskHandler.java,v 1.2 2008-06-25 05:51:44 qcheng Exp $
 *
 */

package com.sun.identity.agents.filter;

import java.util.HashSet;

import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Manager;

/**
 * <p>
 * This task handler provides the necessary functionality to process incoming
 * requests for J2EE form based login error pages that the agent is configured
 * to handle.
 * </p>
 */
public class ErrorPageTaskHandler extends AmFilterTaskHandler 
implements IErrorPageTaskHandler {

    /**
     * The constructor that takes a <code>Manager</code> intance in order
     * to gain access to the infrastructure services such as configuration
     * and log access.
     *
     * @param manager the <code>Manager</code> for the <code>filter</code>
     * subsystem.
     * @throws AgentException if this task handler could not be initialized
     */
    public ErrorPageTaskHandler(Manager manager) throws AgentException {
        super(manager);
    }
    
    public void initialize(ISSOContext context, AmFilterMode mode) 
    throws AgentException {
        super.initialize(context, mode);
        initErrorPageList();
    }

    /**
     * Checks to see if the incoming request is that for a J2EE FBL error
     * page and suggests any action needed to handle such requests 
     * appropriately.
     *
     * @param ctx the <code>AmFilterRequestContext</code> that carries 
     * information about the incoming request and response objects.
     *
     * @return <code>null</code> if no action is necessary, or
     * <code>AmFilterResult</code> object indicating the necessary action in
     * order to handle form based logins.
     * @throws AgentException if the request cannot be processed successfully
     */
    public AmFilterResult process(AmFilterRequestContext ctx)
        throws AgentException
    {
        AmFilterResult result = null;
        if(isErrorPageRequest(ctx)) {
            if(isLogMessageEnabled()) {
                logMessage("ErrorPageTaskHandler: Identified request URI: "
                        + ctx.getHttpServletRequest().getRequestURI()
                        + " as an Error Page request");
            }

            result = ctx.getBlockAccessResult();
        }

        return result;
    }

    /**
     * Method isErrorPageRequest
     *
     * @return
     */
    private boolean isErrorPageRequest(AmFilterRequestContext ctx) {
        return getErrorPageList().contains(ctx.getHttpServletRequest().
                getRequestURI());
    }

    /**
     * Returns a boolean value indicating if this task handler is enabled 
     * or not.
     * @return true if this task handler is enabled, false otherwise
     */
    public boolean isActive() {
        return isModeJ2EEPolicyActive() && (getErrorPageList().size() > 0);
    }

    /**
     * Returns a String that can be used to identify this task handler
     * @return the name of this task handler
     */
    public String getHandlerName() {
        return AM_FILTER_ERROR_PAGE_TASK_HANDLER_NAME;
    }

    /**
     * Method initErrorPageList
     *
     */
    private void initErrorPageList() {
        String[] errorList =
                        getConfigurationStrings(CONFIG_FORM_ERROR_LIST);

        HashSet list = new HashSet();
        if ((errorList != null) && (errorList.length > 0)) {
            for (int i=0; i<errorList.length; i++) {
                list.add(errorList[i]);
            }
        }

        setErrorPageList(list);
    }

    /**
     * Method setErrorPageList
     *
     * @param list
     */
    private void setErrorPageList(HashSet list) {
        _errorPageList = list;
        if (isLogMessageEnabled()) {
            logMessage("ErrorPageTaskHandler: error page list is: " + list);
        }
    }

    /**
     * Method getErrorPageList
     *
     * @return
     */
    private HashSet getErrorPageList() {
        return _errorPageList;
    }

    private HashSet _errorPageList;
}

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
 * $Id: NotenforcedListTaskHandler.java,v 1.2 2008-04-16 00:45:46 leiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.filter;

import javax.servlet.http.HttpServletRequest;

import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Manager;
import com.sun.identity.agents.common.CommonFactory;
import com.sun.identity.agents.common.INotenforcedURIHelper;

/**
 *  FIXME: Rename this class to remove the word LIST from all places.
 * <p>
 * This task handler provides the necessary functionality to evaluate incoming
 * requests against the Notenforced List as set in the Agent Configuration.
 * </p>
 */
public class NotenforcedListTaskHandler extends AmFilterTaskHandler
implements INotenforcedListTaskHandler {

    /**
     * The constructor that takes a <code>Manager</code> intance in order
     * to gain access to the infrastructure services such as configuration
     * and log access.
     *
     * @param manager the <code>Manager</code> for the <code>filter</code>
     * subsystem.
     * @param accessDeniedURI the access denied URI that is being used in the
     * system. This entry is always not-enforced by the Agent.
     * @throws AgentException if this task handler could not be initialized.
     */
    public NotenforcedListTaskHandler(Manager manager)
    {
        super(manager);
    }
    
    public void initialize(ISSOContext context, AmFilterMode mode) 
    throws AgentException {
        super.initialize(context, mode);
        String accessDeniedURI = getConfigurationString(
                CONFIG_ACCESS_DENIED_URI);
        boolean cacheEnabled = getConfigurationBoolean(
                CONFIG_NOTENFORCED_LIST_CACHE_FLAG, 
                DEFAULT_NOTENFORCED_LIST_CACHE_FLAG);
        
        boolean isInverted = getConfigurationBoolean(
                CONFIG_INVERT_NOTENFORCED_LIST_FLAG,
                DEFAULT_INVERT_NOTENFORCED_LIST_FLAG);
        
        int cacheSize = getConfigurationInt(
                CONFIG_NOTENFORCED_LIST_CACHE_SIZE,
                DEFAULT_NOTENFORCED_LIST_CACHE_SIZE)/2;
        
        String[] notenforcedURIs = getConfigurationStrings(
                CONFIG_NOTENFORCED_LIST);
        
        CommonFactory cf = new CommonFactory(getModule());
        setNotEnforcedListURIHelper(cf.newNotenforcedURIHelper(
                accessDeniedURI, isInverted, cacheEnabled, 
                cacheSize, notenforcedURIs));
    }

    /**
     * Checks to see if the incoming request is to be notenforced and
     * suggests any action needed to handle such requests appropriately.
     *
     * @param ctx the <code>AmFilterRequestContext</code> that carries 
     * information about the incoming request and response objects.
     *
     * @return <code>null</code> if no action is necessary, or
     * <code>AmFilterResult</code> object indicating the necessary action in
     * order to handle notifications.
     * @throws AgentException if the processing of this request results in an
     * unexpected error condition
     */
    public AmFilterResult process(AmFilterRequestContext ctx)
        throws AgentException
    {
        AmFilterResult result = null;
        HttpServletRequest request = ctx.getHttpServletRequest();
        //requestURI includes complete URL requested
        String requestURI = ctx.getPolicyDestinationURL();
        if(isNotEnforcedURI(requestURI)) {
            if(isLogMessageEnabled()) {
                logMessage("NotenforcedListTaskHandler: The request URI "
                           + requestURI + " was found in Not Enforced List");
            }
            result = ctx.getContinueResult();
            result.markAsNotEnforced();
        }

        return result;
    }

    /**
     * Returns a boolean value indicating if this task handler is enabled 
     * or not.
     * @return true if the task handler is enabled, false otherwise
     */
    public boolean isActive() {
        return isModeSSOOnlyActive() 
                                && getNotEnforcedListURIHelper().isActive();
    }

    /**
     * Returns a String that can be used to identify this task handler
     * @return the name of this task handler
     */
    public String getHandlerName() {
        return AM_FILTER_NOT_ENFORCED_LIST_TASK_HANDLER_NAME;
    }

    private boolean isNotEnforcedURI(String uri) {
        return getNotEnforcedListURIHelper().isNotEnforced(uri);
    }

    private void setNotEnforcedListURIHelper(INotenforcedURIHelper helper) {
        _notEnforcedListURIHelper = helper;
    }

    private INotenforcedURIHelper getNotEnforcedListURIHelper() {
        return _notEnforcedListURIHelper;
    }

    private INotenforcedURIHelper _notEnforcedListURIHelper;
}

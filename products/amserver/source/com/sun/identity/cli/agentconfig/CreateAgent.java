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
 * $Id: CreateAgent.java,v 1.1 2007-10-26 17:15:17 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.agentconfig;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.sun.identity.cli.AttributeValues;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIConstants;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.CLIUtil;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.IArgument;
import com.sun.identity.cli.LogWriter;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdOperation;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * This command creates agent.
 */
public class CreateAgent extends AuthenticatedCommand {
    /**
     * Services a Commandline Request.
     *
     * @param rc Request Context.
     * @throw CLIException if the request cannot serviced.
     */
    public void handleRequest(RequestContext rc) 
        throws CLIException {
        super.handleRequest(rc);
        ldapLogin();

        SSOToken adminSSOToken = getAdminSSOToken();
        String realm = "/"; // only root realm for now

        String agentName = getStringOptionValue(IArgument.AGENT_NAME);
        String agentType = getStringOptionValue(IArgument.AGENT_TYPE);
        String datafile = getStringOptionValue(IArgument.DATA_FILE);
        List attrValues = rc.getOption(IArgument.ATTRIBUTE_VALUES);
        Map attributeValues = Collections.EMPTY_MAP;
        
        if ((datafile != null) || (attrValues != null)) {
            attributeValues = AttributeValues.parse(getCommandManager(),
                datafile, attrValues);        
        }
        
        if ((attributeValues == null) || !attributeValues.isEmpty()) {
            attributeValues = new HashMap();
        }
        
        String[] params = {realm, agentType, agentName};
        writeLog(LogWriter.LOG_ACCESS, Level.INFO, "ATTEMPT_CREATE_AGENT",
            params);
        try {
            AMIdentityRepository amir = new AMIdentityRepository(
                adminSSOToken, realm);
            Set set = amir.getAllowedIdOperations(IdType.AGENTONLY);
            if (!set.contains(IdOperation.CREATE)) {
                String[] args = {realm};
                throw new CLIException(MessageFormat.format(
                    getResourceString("does-not-support-agent-creation"),
                    (Object[])args),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }

            Set setAgentType = new HashSet(2);
            setAgentType.add(agentType);
            attributeValues.put(CLIConstants.ATTR_NAME_AGENT_TYPE, 
                setAgentType);

            amir.createIdentity(IdType.AGENTONLY, agentName, attributeValues);
            getOutputWriter().printlnMessage(MessageFormat.format(
                getResourceString("create-agent-succeeded"),
                (Object[])params));
            writeLog(LogWriter.LOG_ACCESS, Level.INFO, "SUCCEED_CREATE_AGENT",
                params);
        } catch (IdRepoException e) {
            String[] args = {realm, agentType, agentName, e.getMessage()};
            debugError("CreateAgent.handleRequest", e);
            writeLog(LogWriter.LOG_ERROR, Level.INFO, "FAILED_CREATE_AGENT",
                args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SSOException e) {
            String[] args = {realm, agentType, agentName, e.getMessage()};
            debugError("CreateAgent.handleRequest", e);
            writeLog(LogWriter.LOG_ERROR, Level.INFO, "FAILED_CREATE_AGENT",
                args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
}

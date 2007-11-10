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
 * $Id: ListAgentGroupMembers.java,v 1.1 2007-11-10 06:14:02 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.agentconfig;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.IArgument;
import com.sun.identity.cli.IOutput;
import com.sun.identity.cli.LogWriter;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

/**
 * This command gets the members of an agent group.
 */
public class ListAgentGroupMembers extends AuthenticatedCommand {
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
        IOutput outputWriter = getOutputWriter();
        String realm = "/";
        String agentGroupName = getStringOptionValue(
            IArgument.AGENT_GROUP_NAME);
        String[] params = {realm, agentGroupName};

        try {
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "ATTEMPT_SHOW_AGENT_GROUP_MEMBERS", params);
            AMIdentity amid = new AMIdentity(
                adminSSOToken, agentGroupName, IdType.AGENTGROUP, realm, null); 
            Set members = amid.getMembers(IdType.AGENT);

            if ((members != null) && !members.isEmpty()) {
                outputWriter.printlnMessage(
                    getResourceString("list-agent-group-members-succeeded"));
                outputWriter.printlnMessage("");
                String msg = getResourceString(
                    "format-list-agent-group-members-results");
                String[] arg = {"", ""};

                for (Iterator i = members.iterator(); i.hasNext(); ) {
                    AMIdentity a = (AMIdentity)i.next();
                    arg[0] = a.getName();
                    arg[1] = a.getUniversalId();
                    outputWriter.printlnMessage(
                        MessageFormat.format(msg, (Object[])arg));
                }
            } else {
                outputWriter.printlnMessage(
                    getResourceString("list-agent-group-members-no-members"));
            }
            writeLog(LogWriter.LOG_ACCESS, Level.INFO, 
                "SUCCEED_SHOW_AGENT_GROUP_MEMBERS", params);
        } catch (IdRepoException e) {
            String[] args = {realm, agentGroupName, e.getMessage()};
            debugError("ListAgentGroupMembers.handleRequest", e);
            writeLog(LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_SHOW_AGENT_GROUP_MEMBERS", args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SSOException e) {
            String[] args = {realm, agentGroupName, e.getMessage()};
            debugError("ListAgentGroupMembers.handleRequest", e);
            writeLog(LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_SHOW_AGENT_GROUP_MEMBERS", args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
}

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
 * $Id: LoadSchema.java,v 1.1 2006-05-31 21:50:01 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.schema;


import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.Debugger;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.LogWriter;
import com.sun.identity.cli.IArgument;
import com.sun.identity.cli.IOutput;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Adds service schema.
 */
public class LoadSchema extends AuthenticatedCommand {

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

        boolean continueFlag = isOptionSet(IArgument.CONTINUE);
        IOutput outputWriter = getOutputWriter();        
        List xmlFiles = (List)rc.getOption(IArgument.XML_FILE);
        ServiceManager ssm = null;
        
        try {
            ssm = new ServiceManager(adminSSOToken);
        } catch (SMSException e) {
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SSOException e) {
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
        
        for (Iterator i = xmlFiles.iterator(); i.hasNext(); ) {
            String file = (String)i.next();
            String[] param = {file};

            try {
                writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                    "ATTEMPT_LOAD_SCHEMA", param);
                loadSchema(ssm, file);
                outputWriter.printlnMessage(
                    getResourceString("schema-added"));
                writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                    "SUCCEED_LOAD_SCHEMA", param);
            } catch (CLIException e) {
                if (continueFlag) {
                    outputWriter.printlnError(
                        getResourceString("schema-failed") + e.getMessage());
                    if (isVerbose()) {
                        outputWriter.printlnError(Debugger.getStackTrace(e));
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    private void loadSchema(ServiceManager ssm, String fileName)
        throws CLIException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            ssm.registerServices(fis);
        } catch (IOException e) {
            String[] args = {fileName, e.getMessage()};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO, "FAILED_LOAD_SCHEMA",
                args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SSOException e) {
            String[] args = {fileName, e.getMessage()};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO, "FAILED_LOAD_SCHEMA",
                args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SMSException e) {
            String[] args = {fileName, e.getMessage()};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO, "FAILED_LOAD_SCHEMA",
                args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ie) {
                    //igore if file input stream cannot be closed.
                }
            }
        }
    }
}

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
 * $Id: PrintAllSubCommands.java,v 1.2 2006-12-08 21:02:19 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli;


import java.util.Iterator;
import java.util.List;

/**
 * Prints all subcommands.
 */
public class PrintAllSubCommands extends CLICommandBase {
    /**
     * Services a Commandline Request.
     *
     * @param rc Request Context.
     * @throw CLIException if the request cannot serviced.
     */
    public void handleRequest(RequestContext rc) 
        throws CLIException {
        super.handleRequest(rc);
        UsageFormatter uf = UsageFormatter.getInstance();
        CommandManager mgr = rc.getCommandManager();
        List definitions = mgr.getDefinitionObjects();

        for (Iterator i = definitions.iterator(); i.hasNext(); ) {
            IDefinition def = (IDefinition)i.next();
            List subcmds = def.getSubCommands();
            for (Iterator it = subcmds.iterator(); it.hasNext(); ) {
                uf.format(mgr, (SubCommand)it.next());
            }
        }
    }
}

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
 * $Id: CLIDefinitionBase.java,v 1.3 2006-12-08 21:02:18 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli;

import com.sun.identity.cli.stubs.ICLIStub;
import com.sun.identity.cli.stubs.SubCommandStub;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This is the base class for CLI definition class.
 */
public abstract class CLIDefinitionBase implements IDefinition {
    private String version;
    private List subCommands = new ArrayList();
    private String definitionClass;
    protected ResourceBundle rb;

    /**
     * Constructs an instance of this class.
     *
     * @param definitionClass Definition class name.
     */
    public CLIDefinitionBase(String definitionClass)
        throws CLIException {
        this.definitionClass = definitionClass;
        ICLIStub defObject = getDefinitionObject();
        rb = ResourceBundle.getBundle(defObject.getResourceBundleName());
        version = defObject.getVersion();
        getCommands(defObject);
    }
    
    private ICLIStub getDefinitionObject()
        throws CLIException {
        try {
            Class clazz = Class.forName(definitionClass);
            return (ICLIStub)clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new CLIException(e, ExitCodes.MISSING_DEFINITION_CLASS);
        } catch (IllegalAccessException e) {
            throw new CLIException(e, ExitCodes.INSTANTIATION_DEFINITION_CLASS);
        } catch (InstantiationException e) {
            throw new CLIException(e, ExitCodes.INSTANTIATION_DEFINITION_CLASS);
        }

    }

    private void getCommands(ICLIStub defObject) 
        throws CLIException 
    {
        List subCommandStubs = defObject.getSubCommandStubs();
        for (Iterator i = subCommandStubs.iterator(); i.hasNext(); ) {
            SubCommandStub stub = (SubCommandStub)i.next();
            String subcmdName = stub.name;
            subcmdName = subcmdName.replace('_', '-');
            subCommands.add(new SubCommand(
                this, rb, subcmdName, stub.mandatoryOptions, 
                stub.optionalOptions, stub.aliasOptions, stub.implClassName));
        }
    }

    /**
     * Returns version.
     *
     * @return version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns a list of sub commands.
     *
     * @return a list of sub commands.
     */
    public List getSubCommands() {
        return subCommands;
    }

    /**
     * Returns sub command object.
     *
     * @param name Name of sub command.
     * @return sub command object.
     */
    public SubCommand getSubCommand(String name) {
        SubCommand result = null;
        for (Iterator i = subCommands.iterator();
            i.hasNext() && (result == null);
        ) {
            SubCommand cmd = (SubCommand)i.next();
            if (cmd.getName().equals(name)) {
                result = cmd;
            }
        }
        return result;
    }
}

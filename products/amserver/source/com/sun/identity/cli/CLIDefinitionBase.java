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
 * $Id: CLIDefinitionBase.java,v 1.8 2008-10-09 04:28:56 veiming Exp $
 *
 */

package com.sun.identity.cli;

import com.sun.identity.cli.stubs.ICLIStub;
import com.sun.identity.cli.stubs.SubCommandStub;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is the base class for CLI definition class.
 */
public abstract class CLIDefinitionBase implements IDefinition {
    private List subCommands = new ArrayList();
    private String definitionClass;
    private String logName;
    private ICLIStub defObject;
    protected ResourceBundle rb;

    /**
     * Constructs an instance of this class.
     *
     * @param definitionClass Definition class name.
     */
    public CLIDefinitionBase(String definitionClass)
        throws CLIException {
        this.definitionClass = definitionClass;
        defObject = getDefinitionObject();
        logName = defObject.getLogName();
    }
    
    /**
     * Initializes the definition class.
     * 
     * @param locale Locale of the request.
     * @throws CLIException if command definition cannot initialized.
     */    
    public void init(Locale locale) throws CLIException {
        String rbName = defObject.getResourceBundleName();
        rb = ResourceBundle.getBundle(rbName, locale);
        getCommands();
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

    private void getCommands() 
        throws CLIException 
    {
        List subCommandStubs = defObject.getSubCommandStubs();
        for (Iterator i = subCommandStubs.iterator(); i.hasNext(); ) {
            SubCommandStub stub = (SubCommandStub)i.next();
            String subcmdName = stub.name;
            subcmdName = subcmdName.replace('_', '-');
            subCommands.add(new SubCommand(
                this, rb, subcmdName, stub.mandatoryOptions, 
                stub.optionalOptions, stub.aliasOptions, stub.implClassName,
                stub.webSupport));
        }
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
     * Returns log name.
     *
     * @return log name.
     */
    public String getLogName() {
        return logName;
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

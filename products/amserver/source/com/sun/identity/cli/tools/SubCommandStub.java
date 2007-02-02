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
 * $Id: SubCommandStub.java,v 1.2 2007-02-02 18:05:35 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.tools;

import java.util.Iterator;
import java.util.List;

/**
 * This is a helper class to generate sub command code for 
 * CLI Definition Stub Class (to support JDK 1.4)
 */
public class SubCommandStub {
    public String name;
    public String implClassName;
    public List<String> mandatoryOptions;
    public List<String> optionalOptions;
    public List<String> aliasOptions;

    /**
     * Constructs an instance of <code>SubCommandStub</code>.
     *
     * @param name Name of the Sub Command.
     * @param impl Implementation class of the Sub Command.
     * @param m List of mandatory options (pipe separated format).
     * @param o List of optional options (pipe separated format).
     * @param a List of options aliases (pipe separated format).
     */
    public SubCommandStub(
        String name,
        String impl,
        List<String> m, 
        List<String> o,
        List<String> a
    ) {
        this.name = name;
        this.implClassName = impl;
        mandatoryOptions = m;
        optionalOptions = o;
        aliasOptions = a;
    }

    /**
     * Returns the Java source code to create a sub command; and register
     * the it with the parent class.
     *
     * @return Java source code to create a sub command; and register
     *         the it with the parent class.
     */
    public String generateJavaStatement() {
        StringBuffer buff = new StringBuffer();
        buff.append("\n{\nSubCommandStub cmd = new SubCommandStub(\"")
            .append(name)
            .append("\", \"")
            .append(implClassName)
            .append("\");\n");
        
        for (Iterator i = mandatoryOptions.iterator(); i.hasNext(); ) {
            buff.append("cmd.mandatoryOptions.add(\"")
                .append((String)i.next())
                .append("\");\n");
        }
        for (Iterator i = optionalOptions.iterator(); i.hasNext(); ) {
            buff.append("cmd.optionalOptions.add(\"")
                .append((String)i.next())
                .append("\");\n");
        }
        for (Iterator i = aliasOptions.iterator(); i.hasNext(); ) {
            buff.append("cmd.aliasOptions.add(\"")
                .append((String)i.next())
                .append("\");\n");
        }
        buff.append("addSubCommand(cmd);\n");
        buff.append("}");
        return buff.toString();
    }
}

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
 * $Id: CLIException.java,v 1.1 2006-05-31 21:49:42 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli;


/**
 * Commandline Interface Exception.
 */
public class CLIException extends Exception {
    private Throwable root;
    private int exitCode = 0;
    private String subcommandName;

    /**
     * Constructs a CLI Exception.
     *
     * @param message Exception message.
     * @param exitCode Exit code.
     * @param subcommandName Sub Command Name.
     */
    public CLIException(String message, int exitCode, String subcommandName) {
        super(message);
        this.exitCode = exitCode;
        this.subcommandName = subcommandName;
    }

    /**
     * Constructs a CLI Exception.
     *
     * @param message Exception message.
     * @param exitCode Exit code.
     */
    public CLIException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    /**
     * Constructs a CLI Exception.
     *
     * @param e Throwable object.
     * @param exitCode Exit code.
     */
    public CLIException(Throwable e, int exitCode) {
        super(e.getMessage());
        root = e;
        this.exitCode = exitCode;
    }

    /**
     * Constructs a CLI Exception.
     *
     * @param e Throwable object.
     * @param exitCode Exit code.
     * @param subcommandName Sub Command Name.
     */
    public CLIException(Throwable e, int exitCode, String subcommandName) {
        super(e.getMessage());
        root = e;
        this.exitCode = exitCode;
        this.subcommandName = subcommandName;
    }

    /**
     * Returns exit code.
     *
     * @return exit code.
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Returns sub command name.
     *
     * @return sub command name.
     */
    public String getSubcommandName() {
        return subcommandName;
    }
}

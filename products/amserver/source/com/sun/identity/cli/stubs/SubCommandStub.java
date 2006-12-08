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
 * $Id: SubCommandStub.java,v 1.1 2006-12-08 21:02:31 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.stubs;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains information about a sub command.
 * This is added for JDK 1.4 where annotation support is missing.
 */
public class SubCommandStub {
    /**
     * Name of Sub Command.
     */
    public String name;

    /**
     * Implementation Class Name of Sub Command.
     */
    public String implClassName;

    /**
     * List of mandatory options of Sub Command.
     */
    public List mandatoryOptions = new ArrayList();

    /**
     * List of optional options of Sub Command.
     */
    public List optionalOptions = new ArrayList();

    /**
     * List of option aliases of Sub Command.
     */
    public List aliasOptions = new ArrayList();

    /**
     * Constructs an instance of <code>SubCommandStub</code>.
     *
     * @param name Name of Sub Command.
     * @param impl Implementation Class Name of Sub Command.
     */
    public SubCommandStub(String name, String impl) {
        this.name = name;
        this.implClassName = impl;
    }
}

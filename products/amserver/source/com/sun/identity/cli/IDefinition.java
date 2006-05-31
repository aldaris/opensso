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
 * $Id: IDefinition.java,v 1.1 2006-05-31 21:49:44 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli;


import java.util.List;

/**
 * This interface defines methods for a CLI definition class.
 */
public interface IDefinition {
    /**
     * Returns product name.
     *
     * @return product name.
     */
    String getProductName();

    /**
     * Returns version.
     *
     * @return version.
     */
    String getVersion();

    /**
     * Returns a list of sub commands.
     *
     * @return a list of sub commands.
     */
    List getSubCommands();

    /**
     * Returns sub command object.
     *
     * @param name Name of sub command.
     * @return sub command object.
     */
    SubCommand getSubCommand(String name);
}

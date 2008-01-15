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
 * $Id: ITask.java,v 1.1 2008-01-15 06:44:20 veiming Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.workflow;

import java.util.Locale;
import java.util.Map;

/**
 * Workflow task interface.
 */
public interface ITask {

    /**
     * Executes the task.
     *
     * @param locale Locale of the request. Need for providing locale message.
     * @param mapParams Map of parameters.
     */
    String execute(Locale locale, Map mapParams)
        throws WorkflowException;
}

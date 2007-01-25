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
 * $Id: IURLFailoverHelper.java,v 1.2 2007-01-25 20:42:32 madan_ranganath Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.common;

import com.sun.identity.agents.arch.AgentException;

/**
 * The interface for URLFailoverHelper
 */
public interface IURLFailoverHelper {
    public abstract void initiailze(
            boolean probeEnabled, 
            boolean isPrioritized, 
            long timeout,
            String[] urlList) throws AgentException;

    public abstract String getAvailableURL() throws AgentException;
}

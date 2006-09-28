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
 * $Id: IApplicationLogoutHandler.java,v 1.1 2006-09-28 23:31:41 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.filter;


/**
 * The interface for defining agent application logout handler constants
 */
public interface IApplicationLogoutHandler extends IAmFilterTaskHandler {

    public static final String AM_APP_LOGOUT_HANDLER_NAME = 
        "Application Logout Handler";

    public static final String ARG_NEW_SESSION_PARAMETER = 
        "arg=newsession";
}

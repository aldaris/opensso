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
 * $Id: AmFilterResultStatus.java,v 1.1 2006-09-28 23:30:01 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.filter;

import com.sun.identity.agents.util.ConstrainedSelection;

/**
 * The status of a <code>AmFilterResult</code>
 */
public class AmFilterResultStatus extends ConstrainedSelection {
    
    public static final int INT_STATUS_CONTINUE = 0;
    public static final int INT_STATUS_REDIRECT = 1;
    public static final int INT_STATUS_FORBIDDEN = 2;
    public static final int INT_STATUS_SERVE_DATA = 3;
    
    public static final String STR_STATUS_CONTINUE = "CONTINUE";
    public static final String STR_STATUS_REDIRECT = "REDIRECT";
    public static final String STR_STATUS_FORBIDDEN = "FORBIDDEN";
    public static final String STR_STATUS_SERVE_DATA = "SERVE DATA";
    
    public static final AmFilterResultStatus STATUS_CONTINUE =
        new AmFilterResultStatus(STR_STATUS_CONTINUE, INT_STATUS_CONTINUE);
    
    public static final AmFilterResultStatus STATUS_REDIRECT =
        new AmFilterResultStatus(STR_STATUS_REDIRECT, INT_STATUS_REDIRECT);
    
    public static final AmFilterResultStatus STATUS_FORBIDDEN =
        new AmFilterResultStatus(STR_STATUS_FORBIDDEN, INT_STATUS_FORBIDDEN);
    
    public static final AmFilterResultStatus STATUS_SERVE_DATA =
        new AmFilterResultStatus(STR_STATUS_SERVE_DATA, INT_STATUS_SERVE_DATA);
    
    private static final AmFilterResultStatus[] values =
        new AmFilterResultStatus[] { STATUS_CONTINUE, 
            STATUS_REDIRECT, STATUS_FORBIDDEN, STATUS_SERVE_DATA };
    
    
    public static AmFilterResultStatus getStatus(String status) {
        return (AmFilterResultStatus) ConstrainedSelection.get(status, values);
    }
    
    public static AmFilterResultStatus getStatus(int status) {
        return (AmFilterResultStatus) ConstrainedSelection.get(status, values);
    }
    
    private AmFilterResultStatus(String name, int value) {
        super(name, value);
    }

}

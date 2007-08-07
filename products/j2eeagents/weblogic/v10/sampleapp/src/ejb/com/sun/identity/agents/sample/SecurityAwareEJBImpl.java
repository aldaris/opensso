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
 * $Id: SecurityAwareEJBImpl.java,v 1.1 2007-08-07 01:47:43 sean_brydon Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.sample;

import java.security.Principal;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.ejb.CreateException;

public class SecurityAwareEJBImpl implements SessionBean {

    public String getDetails() {
        Principal principal = context.getCallerPrincipal();
        String user = (principal != null)?principal.toString():"Anonymous";
        boolean isManager = context.isCallerInRole("MANAGER_ROLE");
        boolean isEmployee = context.isCallerInRole("EMPLOYEE_ROLE");

        StringBuffer buff = new StringBuffer();
        buff.append("The User \"").append(user).append("\" is ");
        if (!isManager) {
            if (!isEmployee) {
                buff.append(" neither ");
            } else {
                buff.append(" not ");
            }
        }
        buff.append("a manager");
        if (isEmployee) {
            if (!isManager) {
                buff.append(", but is an employee.");
            } else {
                buff.append(" and also an employee.");
            }
        } else {
            buff.append(" nor an employee.");
        }

        return buff.toString();
    }

    public void ejbActivate() { }

    public void ejbPassivate() { }

    public void ejbRemove() { }

    public void ejbCreate() throws CreateException { }

    public void setSessionContext(SessionContext context) {
        this.context = context;
    }

    private SessionContext context;
}


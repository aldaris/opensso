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
 * $Id: AMConsoleException.java,v 1.1 2007-02-07 20:19:41 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* - NEED NOT LOG - */

/**
 * This exception is thrown to signal to the view bean
 * that incorrect behavior is encountered in processing a request.
 */
public class AMConsoleException extends Exception {
    private List errList = null;

    /**
     * Creates a Console Exception object.
     *
     * @param msg exception message.
     */
    public AMConsoleException(String msg) {
        super(msg);
        errList = new ArrayList(1);
        errList.add(msg);
    }

    /**
     * Creates a Console Exception object.
     *
     * @param errors list of error messages.
     */
    public AMConsoleException(List errors) {
        super(errors.toArray().toString());
        errList = errors;
    }

    /**
     * Creates a Console Exception object.
     *
     * @param t <code>Throwable</code> instance.
     */
    public AMConsoleException(Throwable t) {
        super(t.getMessage());
        errList = new ArrayList(1);
        errList.add(t.getMessage());
    }

    /**
     * Gets error list.
     *
     * @return error list.
     */
    public List getErrors() {
        return (errList == null) ? Collections.EMPTY_LIST : errList;
    }
}

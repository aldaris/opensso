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
 * $Id: SendRequestException.java,v 1.1 2005-11-01 00:30:13 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.services.comm.client;

/**
 * A <code>SendRequestException</code> object is thrown if the the request can
 * not be sent to the destination URL of the service.
 */

public class SendRequestException extends Exception {

    /*
     * CONSTRUCTORS
     */

    /**
     * Constructs an instance of the <code>SendRequestException</code> class.
     * 
     * @param msg
     *            The message provided by the object which is throwing the
     *            exception
     */
    public SendRequestException(String msg) {
        super(msg);
        fillInStackTrace();
    }

    /**
     * Constructs an instance of the <code>SendRequestException</code> class.
     * 
     * @param t
     *            The Throwable object provided by the object which is throwing
     *            the exception
     */
    public SendRequestException(Throwable t) {
        super(t.getMessage());
        fillInStackTrace();
    }
}

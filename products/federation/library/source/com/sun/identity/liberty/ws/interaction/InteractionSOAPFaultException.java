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
 * $Id: InteractionSOAPFaultException.java,v 1.1 2006-10-30 23:15:10 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.liberty.ws.interaction;
import com.sun.identity.liberty.ws.soapbinding.SOAPFaultException;

/**
 * Class for exception thrown by <code>InteractionManager</code> 
 * in case of any SOAP fault while handling interaction for the
 * <code>WSP</code>.
 *
 * @supported.all.api 
 */
public class InteractionSOAPFaultException 
        extends InteractionException {

    private SOAPFaultException soapFaultException;

    /**
     * Constructor
     * @param soapFaultException <code>SOAPFaultException</code> that contains
     *        the SOAP fault
     *
     * @supported.api
     */
    public InteractionSOAPFaultException(
           Throwable soapFaultException) {
        super(soapFaultException);
        this.soapFaultException = (SOAPFaultException) soapFaultException;
    }

    /**
     * Gets the <code>SOAPFaultException</code> that contains SOAP fault
     * @return <code>SOAPFaultException</code> that contains SOAP fault
     *
     * @supported.api
     */
    public SOAPFaultException getSOAPFaultException() {
        return soapFaultException;
    }

}


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
 * $Id: IdentityServices.java,v 1.1 2007-08-30 00:26:03 arviranga Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idsvcs;

import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Base interface for all security providers.
 */
public interface IdentityServices extends Remote {

    /**
     * Attempt to authenticate using simple user/password credentials.
     * @param username Subject's user name.
     * @param password Subject's password
     * @param uri Subject's additional context such as module, organ, etc
     * @return Subject's token if authenticated.
     * @throws UserNotFound if user not found.
     * @throws InvalidPassword if password is invalid.
     * @throws NeedMoreCredentials if additional credentials are needed for
     * authentication.
     * @throws InvalidCredentials if credentials are invalid.
     * @throws GeneralFailure on other errors.
     */
    public Token authenticate(String username, String password, String uri)
        throws UserNotFound, InvalidPassword, NeedMoreCredentials,
        InvalidCredentials, GeneralFailure, RemoteException;

    /**
     * Attempt to authorize the subject for the optional action on the
     * requested URI.
     * @param uri URI for which authorization is required
     * @param action Optional action for which subject is being authorized
     * @param subject Token identifying subject to be authorized
     * @return boolean <code>true</code> if allowed; else <code>false</code>
     * @throws NeedMoreCredentials when more credentials are required for
     * authorization.
     * @throws TokenExpired when subject's token has expired.
     * @throws GeneralFailure on other errors.
     */
    public boolean authorize(String uri, String action, Token subject)
        throws NeedMoreCredentials, TokenExpired, GeneralFailure,
        RemoteException;

    /**
     * Retrieve user details (roles, attributes) for the subject.
     * @param attributeNames Optional list of attributes to be returned
     * @param subject Token for subject.
     * @return User details for the subject.
     * @throws TokenExpired when Token has expired.
     * @throws GeneralFailure on other errors.
     */
    public UserDetails attributes(List attributeNames, Token subject)
        throws TokenExpired, GeneralFailure, RemoteException;

    /**
     * Logs a message on behalf of the authenticated app.
     *
     * @param app         Token corresponding to the authenticated application.
     * @param subject     Optional token identifying the subject for which the
     * log record pertains.
     * @param logName     Identifier for the log file, e.g. "MyApp.access"
     * @param message     String containing the message to be logged
     * @throws AccessDenied   if app token is not specified
     * @throws GeneralFailure on error
     */
    public void log(Token app, Token subject, String logName, String message)
        throws AccessDenied, TokenExpired, GeneralFailure, RemoteException;

}

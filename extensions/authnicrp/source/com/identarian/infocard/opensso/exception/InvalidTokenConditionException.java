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
 * $Id: InvalidTokenConditionException.java,v 1.1 2009-07-08 08:59:26 ppetitsm Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2008 Patrick Petit Consulting
 */

package com.identarian.infocard.opensso.exception;


public class InvalidTokenConditionException extends Exception{
    public InvalidTokenConditionException() {
    }

    public InvalidTokenConditionException(String string) {
        super(string);
    }

    public InvalidTokenConditionException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public InvalidTokenConditionException(Throwable throwable) {
        super(throwable);
    }
}
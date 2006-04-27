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
 * $Id: ITokenProvider.java,v 1.2 2006-04-27 07:53:38 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.log.spi;

/** 
 * Interface to define the implementation of a token generator.
 */
public interface ITokenProvider {
    /**
     * Return <code>Object</code> with given user name and password
     * @param name User name
     * @param password User password
     * @return <code>Object</code> with given user name and password
     */
    public Object createToken(String name, String password);
}


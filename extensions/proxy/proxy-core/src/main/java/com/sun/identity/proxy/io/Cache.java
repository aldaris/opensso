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
 * $Id: Cache.java,v 1.1 2009-10-06 01:05:19 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.IOException;

public interface Cache {

    /**
     * TODO: Description.
     *
     * @throws IOException TODO.
     */
    public void close() throws IOException;

    /**
     * TODO: Description.
     *
     * @return TODO.
     * @throws IOException TODO.
     */
    public int position() throws IOException;
    
    /**
     * TODO: Description.
     *
     * @param b TODO.
     * @param off TODO.
     * @param len TODO.
     * @return TODO.
     * @throws IOException TODO.
     */
    public int read(byte[] b, int off, int len) throws IOException;

    /**
     * TODO: Description.
     *
     * @throws IOException TODO.
     */
    public void rewind() throws IOException;

    /**
     * TODO: Description.
     *
     * @return TODO.
     * @throws IOException TODO.
     */
    public int size() throws IOException;

    /**
     * TODO: Description.
     *
     * @param n TODO.
     * @return TODO.
     * @throws IOException TODO.
     */
    public int skip(int n) throws IOException;

    /**
     * TODO: Description.
     *
     * @param b TODO.
     * @param off TODO.
     * @param len TODO.
     * @throws IOException TODO.
     */
    public void write(byte[] b, int off, int len) throws IOException;
}

